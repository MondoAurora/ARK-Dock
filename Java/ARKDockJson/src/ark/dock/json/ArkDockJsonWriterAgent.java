package ark.dock.json;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import ark.dock.ArkDockConsts.ArkDockAgentBase;

public class ArkDockJsonWriterAgent extends ArkDockAgentBase<ArkDockJsonConsts.JsonContext>
		implements ArkDockJsonConsts {

	Writer target;

	Map<Class<?>, JsonFormatter> formatters;
	boolean pretty = true;
	boolean closeOnRelease;
	StringBuilder sbIndent = new StringBuilder();

	boolean cont;

	public ArkDockJsonWriterAgent(Writer target, boolean closeOnRelease) {
		this.target = target;
		this.closeOnRelease = closeOnRelease;
	}

	public void addFormatter(JsonFormatter fmt) {
		if ( null == formatters ) {
			formatters = new HashMap<>();
		}

		formatters.put(fmt.getDataClass(), fmt);
	}

	public void setPretty(boolean pretty) {
		this.pretty = pretty;
	}

	private Writer endLine(String close) throws Exception {
		target.append(close);
		if ( pretty ) {
			target.append("\n");
			target.append(sbIndent);
		} else {
			target.append(" ");
		}
		return target;
	}

	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {
		JsonContext ctx = getActionCtx();

		switch ( action ) {
		case INIT:
			cont = false;
			break;
		case BEGIN:
			if ( cont ) {
				endLine(",");
				cont = false;
			}
			switch ( ctx.block ) {
			case Array:
				sbIndent.append("  ");
//				target.write("[ ");
				endLine("[");
				break;
			case Object:
				sbIndent.append("  ");
//				target.write("{ ");
				endLine("{");
				break;
			case Entry:
//				endLine("");
				JSONValue.writeJSONString(ctx.param.toString(), target);
				target.write(" : ");
				break;
			default:
				break;
			}
			break;
		case END:
			switch ( ctx.block ) {
			case Array:
				sbIndent.delete(0, 2);
				endLine("").write("]");
				break;
			case Object:
				sbIndent.delete(0, 2);
				endLine("").write("}");
				break;
			default:
				break;
			}
			cont = true;
			break;
		case PROCESS:
			if ( cont ) {
				target.write(",");
			}
			cont = true;

			Object val = ctx.param;
			if ( null != val ) {
				if ( val instanceof Enum ) {
					val = ((Enum<?>) val).name();
				} else {
					if ( null != formatters ) {
						JsonFormatter fmt = formatters.get(val.getClass());
						if ( null != fmt ) {
							fmt.toJson(val, target);
							return DustResultType.ACCEPT_READ;
						}
					}
				}
			}
			JSONValue.writeJSONString(val, target);
			break;
		case RELEASE:
			target.flush();

			if ( closeOnRelease ) {
				target.close();
			}

			break;
		}

		return DustResultType.ACCEPT_READ;
	}

	@SuppressWarnings("rawtypes")
	public static void sendMultiEntry(ArkDockJsonWriterAgent jsonAgent, Map src, Object... keys) throws Exception {
		for (Object k : keys) {
			Object v = src.get(k);
			if ( null != v ) {
				sendSimpleEntry(jsonAgent, k, v);
			}
		}
	}

	public static void sendSimpleEntry(ArkDockJsonWriterAgent jsonAgent, Object name, Object value) throws Exception {
		JsonContext ctx = jsonAgent.getActionCtx();

		ctx.block = JsonBlock.Entry;
		ctx.param = name;
		jsonAgent.agentAction(DustAgentAction.BEGIN);

		ctx.param = value;
		jsonAgent.agentAction(DustAgentAction.PROCESS);
		jsonAgent.agentAction(DustAgentAction.END);
	}

}
