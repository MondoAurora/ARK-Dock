package ark.dock.json;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import ark.dock.ArkDockConsts.ArkDockAgentBase;

public class ArkDockJsonWriterAgent extends ArkDockAgentBase<ArkDockJsonConsts.JsonContext> implements ArkDockJsonConsts {
	enum JsonHeader {
		ArkJsonInfo, VersionInfo
	}

	Writer target;

	Map<Class<?>, JsonFormatter> formatters;
	boolean pretty = true;
	boolean closeOnRelease;
	StringBuilder sbIndent = new StringBuilder();

	boolean contEntity;
	boolean contMember;
	String valueClose;
	

	public ArkDockJsonWriterAgent(Writer target, boolean closeOnRelease) {
		this.target = target;
		this.closeOnRelease = closeOnRelease;
		setActionCtx(new JsonContext());
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
//		JsonContext ctx = getActionCtx();
//		String closeLine = "";
//
//		switch ( action ) {
//		case INIT:
//			contEntity = contMember = false;
//			valueClose = null;
//			break;
//		case BEGIN:
//			sbIndent.append("  ");
//			switch ( ctx.block ) {
//			case Array:
//				target.write("[");
//				break;
//			case Entry:
//				endLine(closeLine);
//				JSONValue.writeJSONString(ctx.param.toString(), target);
//				break;
//			case Object:
//				target.write("{");
//				break;
//			default:
//				break;
//			}
//			break;
//		case END:
//			sbIndent.delete(0, 2);
//			if ( null != valueClose ) {
//				target.write(valueClose);
//				valueClose = null;
//			}
//
//			switch ( ctx.block ) {
//			case Entity:
//				endLine("").write("}");
//				break;
//			case Member:
//				break;
//			}
//			break;
//		case PROCESS:
//			Object val = ctx.value;
//			if ( null != val ) {
//				if ( null == valueClose ) {
//					DustCollType ct = (null == ctx.collType) ? DustCollType.ONE : ctx.collType;
//					switch ( ct ) {
//					case ARR:
//					case SET:
//						target.write("[ ");
//						valueClose = " ]";
//						break;
//					case MAP:
//						target.write("{ ");
//						valueClose = " }";
//						break;
//					default:
//						valueClose = "";
//						break;
//					}
//				} else {
//					target.write(", ");
//				}
//
//				if ( ctx.collType == DustCollType.MAP ) {
//					JSONValue.writeJSONString(ctx.key.toString(), target);
//					target.write(": ");
//				}
//
//				if ( null != formatters ) {
//					JsonFormatter fmt = formatters.get(val.getClass());
//					if ( null != fmt ) {
//						fmt.toJson(val, target);
//						return DustResultType.ACCEPT_READ;
//					}
//				}
//
//				JSONValue.writeJSONString(val, target);
//			}
//			break;
//		case RELEASE:
//			target.flush();
//			target.close();
//
//			break;
//		}

		return DustResultType.ACCEPT_READ;
	}

}
