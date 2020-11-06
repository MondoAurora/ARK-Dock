package ark.dock.io.json;

import java.io.IOException;
import java.io.Writer;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.ParseException;

import ark.dock.io.ArkDockIOConsts;
import dust.gen.DustGenException;
import dust.gen.DustGenUtils;

public interface ArkDockJsonConsts extends ArkDockIOConsts {

    enum JsonBlock {
        Entry, Object, Array
    }
    
	interface JsonFormatter {
		Class<?> getDataClass();
		void toJson(Object data, Writer target) throws Exception;
		Object fromParsedData(Object data);
	}

	enum SerializeHeader {
		ArkJsonInfo, VersionInfo
	}


    public class JsonContext {
        public JsonBlock block;
        public Object param;
        
		@Override
		public String toString() {
			StringBuilder sb = DustGenUtils.sbAppend(null, " ", true, block, param);
			return sb.toString();
		}
    }

    public class JsonContentDispatcher implements ContentHandler {
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

                switch (ret) {
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
}
