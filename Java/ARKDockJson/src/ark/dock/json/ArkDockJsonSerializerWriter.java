package ark.dock.json;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import ark.dock.ArkDockModelSerializer.SerializeAgent;
import dust.gen.DustGenConsts.DustEntityContext;
import dust.gen.DustGenUtils;

public class ArkDockJsonSerializerWriter extends SerializeAgent<DustEntityContext> implements ArkDockJsonConsts {
	enum JsonHeader {
		ArkJsonInfo, VersionInfo
	}

	Writer target;
	Map<JsonHeader, Object> header;

	Map<Class<?>, JsonFormatter> formatters;

	boolean contEntity;
	boolean contMember;
	String valueClose;

	public ArkDockJsonSerializerWriter(Writer target, Map<JsonHeader, Object> header) {
		this.target = target;
		this.header = header;
		setActionCtx(new DustEntityContext());
	}

	public ArkDockJsonSerializerWriter(File file, Map<JsonHeader, Object> header) throws Exception {
		this(new PrintWriter(DustGenUtils.ensureParents(file), "UTF-8"), header);
	}

	public ArkDockJsonSerializerWriter(String fileName, Map<JsonHeader, Object> header) throws Exception {
		this(new File(fileName), header);
	}

	public void addFormatter(JsonFormatter fmt) {
		if ( null == formatters ) {
			formatters = new HashMap<>();
		}

		formatters.put(fmt.getDataClass(), fmt);
	}


	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {
		DustEntityContext ctx = getActionCtx();
		StringBuilder line = null;
		String closeLine = "";

		switch ( action ) {
		case INIT:
			target.write("[\n  ");
			JSONValue.writeJSONString(header, target);
			target.write(",\n  {");
			contEntity = contMember = false;
			valueClose = null;
			break;
		case BEGIN:
			switch ( ctx.block ) {
			case Entity:
				if ( contEntity ) {
					closeLine = ",";
				} else {
					contEntity = true;
				}
				line = DustGenUtils.sbAppend(null, "", true, closeLine, "\n   ", JSONValue.toJSONString(ctx.entityId.toString()),
						" : {");
				target.write(line.toString());
				contMember = false;

				break;
			case Member:
				if ( contMember ) {
					closeLine = ",";
				} else {
					contMember = true;
				}
				line = DustGenUtils.sbAppend(null, "", true, closeLine, "\n     \"",
						JSONValue.escape(ctx.member.toString()), "\" : ");
				target.write(line.toString());
				valueClose = null;
				break;
			}
			break;
		case END:
			if ( null != valueClose ) {
				target.write(valueClose);
				valueClose = null;
			}

			switch ( ctx.block ) {
			case Entity:
				target.write("\n  }");
				break;
			case Member:
				break;
			}
			break;
		case PROCESS:
			Object val = ctx.value;
			if ( null != val ) {
				if ( null == valueClose ) {
					DustCollType ct = (null == ctx.collType) ? DustCollType.ONE : ctx.collType;
					switch ( ct ) {
					case ARR:
					case SET:
						target.write("[ ");
						valueClose = " ]";
						break;
					case MAP:
						target.write("{ ");
						valueClose = " }";
						break;
					default:
						valueClose = "";
						break;
					}
				} else {
					target.write(", ");
				}

				if ( ctx.collType == DustCollType.MAP ) {
					JSONValue.writeJSONString(ctx.mKey.toString(), target);
					target.write(": ");
				}

				if ( null != formatters ) {
					JsonFormatter fmt = formatters.get(val.getClass());
					if ( null != fmt ) {
						fmt.toJson(val, target);
						return DustResultType.ACCEPT_READ;
					}
				}

				JSONValue.writeJSONString(val, target);
			}
			break;
		case RELEASE:
			target.write("\n  }\n]");
			
			target.flush();
			target.close();

			break;
		}

		return DustResultType.ACCEPT_READ;
	}

}
