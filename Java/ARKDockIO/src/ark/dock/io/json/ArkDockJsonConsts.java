package ark.dock.io.json;

import java.io.Writer;

import ark.dock.io.ArkDockIOConsts;
import dust.gen.DustGenUtils;

public interface ArkDockJsonConsts extends ArkDockIOConsts {

    enum JsonBlock {
        Entry, Object, Array
    }
    
	interface JsonWritable {
		void toJson(ArkDockAgent<JsonContext> target) throws Exception;
    }
    
	interface JsonFormatter {
		Class<?> getDataClass();
		void toJson(Object data, Writer target) throws Exception;
		Object fromParsedData(Object data);
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
}
