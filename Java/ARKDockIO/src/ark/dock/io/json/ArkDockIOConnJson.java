package ark.dock.io.json;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.json.simple.JSONValue;
import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ark.dock.ArkDockConsts.ArkDockAgent;
import ark.dock.ArkDockConsts.ArkDockAgentDefault;
import ark.dock.io.ArkDockIOConsts.ArkDockIOSource;
import ark.dock.io.ArkDockIOUtils;
import ark.dock.io.json.ArkDockJsonConsts.JsonBlock;
import ark.dock.io.json.ArkDockJsonConsts.JsonContext;
import ark.dock.io.json.ArkDockJsonConsts.JsonWritable;
import dust.gen.DustGenConsts.DustAgentAction;
import dust.gen.DustGenConsts.DustResultType;
import dust.gen.DustGenException;

public class ArkDockIOConnJson extends ArkDockIOUtils.ArkDockIOConnector<JsonContext> {

	public static class JsonContentDispatcher implements ContentHandler {
		JsonContext ctx;
		ArkDockAgent<JsonContext> processor;

		public JsonContentDispatcher(ArkDockAgent<JsonContext> processor) {
			this.processor = processor;
			this.ctx = processor.getActionCtx();
		}

		boolean processJsonEvent(DustAgentAction action, JsonBlock block, Object param) {
			ctx.block = block;
			ctx.param = param;

			try {
				DustResultType ret = processor.agentAction(action);

				switch ( ret ) {
				case NOTIMPLEMENTED:
					return DustGenException.throwException(null);
				case REJECT:
					return false;
				default:
					return true;
				}
			} catch (Exception e) {
				return DustGenException.throwException(e);
			}
		}

		@Override
		public void startJSON() throws ParseException, IOException {
			processJsonEvent(DustAgentAction.INIT, null, null);
		}

		@Override
		public void endJSON() throws ParseException, IOException {
			processJsonEvent(DustAgentAction.RELEASE, null, null);
		}

		@Override
		public boolean startObjectEntry(String arg0) throws ParseException, IOException {
			return processJsonEvent(DustAgentAction.BEGIN, JsonBlock.Entry, arg0);
		}

		@Override
		public boolean endObjectEntry() throws ParseException, IOException {
			return processJsonEvent(DustAgentAction.END, JsonBlock.Entry, null);
		}

		@Override
		public boolean primitive(Object arg0) throws ParseException, IOException {
			return processJsonEvent(DustAgentAction.PROCESS, null, arg0);
		}

		@Override
		public boolean startObject() throws ParseException, IOException {
			return processJsonEvent(DustAgentAction.BEGIN, JsonBlock.Object, null);
		}

		@Override
		public boolean endObject() throws ParseException, IOException {
			return processJsonEvent(DustAgentAction.END, JsonBlock.Object, null);
		}

		@Override
		public boolean startArray() throws ParseException, IOException {
			return processJsonEvent(DustAgentAction.BEGIN, JsonBlock.Array, null);
		}

		@Override
		public boolean endArray() throws ParseException, IOException {
			return processJsonEvent(DustAgentAction.END, JsonBlock.Array, null);
		}
	}

	public class JsonContentWriter extends ArkDockAgentDefault<JsonContext> {
		Writer target;
		
		StringBuilder sbIndent;
		boolean cont;

		public JsonContentWriter(Writer target) {
			this.target = target;
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
				sbIndent = new StringBuilder();
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
					endLine("[");
					break;
				case Object:
					sbIndent.append("  ");
					endLine("{");
					break;
				case Entry:
					JSONValue.writeJSONString(ctx.param.toString(), target);
					target.write(" : ");
					cont = false;
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

				Object val = ctx.param;
				if ( null != val ) {
					if ( val instanceof Enum ) {
						val = ((Enum<?>) val).name();
					}
				}

				if ( val instanceof JsonWritable ) {
					((JsonWritable) val).toJson(this);
				} else {
					JSONValue.writeJSONString(val, target);
				}
				
				cont = true;
				break;
			case RELEASE:
				target.flush();
				cont = false;

				break;
			}

			return DustResultType.ACCEPT_READ;
		}
	}
	
	
	
	boolean pretty = true;

	@Override
	public boolean isText() {
		return true;
	}

	@Override
	public JsonContext createContext() {
		return new JsonContext();
	}

	public void setPretty(boolean pretty) {
		this.pretty = pretty;
	}

	@Override
	public DustResultType read(Reader source, ArkDockAgent<JsonContext> processor) throws Exception {
		JSONParser p = new JSONParser();
		JsonContentDispatcher h = new JsonContentDispatcher(processor);
		p.parse(source, h);
		return DustResultType.ACCEPT;
	}

	@Override
	public DustResultType write(ArkDockIOSource<JsonContext> source, Writer target) throws Exception {
		JsonContentWriter cw = new JsonContentWriter(target);
		JsonContext ctx = new JsonContext();
		cw.setActionCtx(ctx);
		
		return source.write(cw);
	}

}