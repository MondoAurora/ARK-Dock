package ark.dock.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.ParseException;

import ark.dock.ArkDockUtils;

public interface ArkDockJsonUtils extends ArkDockJsonConsts {
    
    @SuppressWarnings("rawtypes")
    public class JsonValueCollector extends JsonContentHandlerBase {
        String key;
        
        Collection collector;
        boolean readingKey;
        
        public JsonValueCollector(String key, Collection collector) {
            super();
            this.key = key;
            this.collector = collector;
        }
        
        @Override
        public boolean startObjectEntry(String key) throws ParseException, IOException {
            readingKey = this.key.equals(key);
            return true;
        }
        @SuppressWarnings("unchecked")
        @Override
        public boolean primitive(Object arg0) throws ParseException, IOException {
            if ( readingKey ) {
                collector.add(arg0);
                readingKey = false;
            }
            return true;
        }
        
        public Collection getCollector() {
            return collector;
        }
    }
    
    public class JsonArrayCollector extends JsonContentHandlerBase {
        ArrayList<Object> arr;
        
        public JsonArrayCollector(ArrayList<Object> arr) {
            super();
            this.arr = arr;
        }
        
        public JsonArrayCollector() {
            this(new ArrayList<>());
        }
        
        @Override
        public boolean primitive(Object arg0) throws ParseException, IOException {
            arr.add(arg0);
            return true;
        }
        
        public ArrayList<Object> getArr() {
            return arr;
        }
    }
    
    public class JsonFlatCollector extends JsonContentHandlerBase {
        String key;
        
        Map<String, Object> content;
        
        public JsonFlatCollector(Map<String, Object> content) {
            super();
            this.content = content;
        }
        
        public JsonFlatCollector() {
            this(new TreeMap<>());
        }
        
        @Override
        public boolean startObjectEntry(String key) throws ParseException, IOException {
            this.key = key;
            return true;
        }
        
        @Override
        public boolean primitive(Object arg0) throws ParseException, IOException {
            if ( !ArkDockUtils.isEmpty(key) ) {
                content.put(key, arg0);
                key = null;
            }
            return true;
        }
        
        public Map<String, Object> getContent() {
            return content;
        }
    }
    
    public class JsonContentHandlerRelay extends JsonContentHandlerBase {
        Stack<ContentHandler> stack = new Stack<>();
        
        protected void relayStart(ContentHandler h) {
            stack.push(h);
        }
        
        protected ContentHandler relayEnd() {
            return stack.pop();
        }
        @Override
        public boolean startObjectEntry(String arg0) throws ParseException, IOException {
            return stack.peek().startObjectEntry(arg0);
        }

        @Override
        public boolean endObjectEntry() throws ParseException, IOException {
            return stack.peek().endObjectEntry();
        }

        @Override
        public boolean primitive(Object arg0) throws ParseException, IOException {
            return stack.peek().primitive(arg0);
        }

        @Override
        public boolean startArray() throws ParseException, IOException {
            return stack.peek().startArray();
        }
        
        @Override
        public boolean endArray() throws ParseException, IOException {
            return stack.peek().endArray();
        }

        @Override
        public boolean startObject() throws ParseException, IOException {
            return stack.peek().startObject();
        }

        @Override
        public boolean endObject() throws ParseException, IOException {
            return stack.peek().endObject();
        }

    }
}
