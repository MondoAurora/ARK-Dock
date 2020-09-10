package ark.dock.json;

import java.io.IOException;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.ParseException;

public interface ArkDockJsonConsts {
    public class JsonContentHandlerBase implements ContentHandler {
        @Override
        public boolean endArray() throws ParseException, IOException {
            return true;
        }

        @Override
        public void endJSON() throws ParseException, IOException {
        }

        @Override
        public boolean endObject() throws ParseException, IOException {
            return true;
        }

        @Override
        public boolean endObjectEntry() throws ParseException, IOException {
            return true;
        }

        @Override
        public boolean primitive(Object arg0) throws ParseException, IOException {
            return true;
        }

        @Override
        public boolean startArray() throws ParseException, IOException {
            return true;
        }

        @Override
        public void startJSON() throws ParseException, IOException {
        }

        @Override
        public boolean startObject() throws ParseException, IOException {
            return true;
        }

        @Override
        public boolean startObjectEntry(String arg0) throws ParseException, IOException {
            return true;
        }
        
    }
}
