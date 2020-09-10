package ark.dock.geo.json;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ark.dock.ArkDockUtils;
import ark.dock.json.ArkDockJsonUtils;

public class ArkDockGeojsonParser implements ArkDockJsonUtils, ArkDockGeojsonConsts {

    class MainHandler extends JsonContentHandlerBase {
        JsonArrayCollector hndPoint = new JsonArrayCollector();
        JsonFlatCollector hndProps = new JsonFlatCollector();

        Map<GeojsonTypes, Object> currObjs = new HashMap<>();
        ArrayList<GeojsonTypes> procStack = new ArrayList<>();
        int depth;
        // int cutDepth;
        int count;
        int resCount;
        int primCount;
        int coordIdx;

        double[] coords = new double[6];

        String key;
        GeojsonKeys currKey = GeojsonKeys.NULL;

        GeojsonBuilder builder;

        public MainHandler(GeojsonBuilder builder) {
            this.builder = builder;
        }

        void step(boolean down) {
            if (down) {
                ++depth;
                if (procStack.size() < depth) {
                    procStack.add(null);
                }

            } else {
                --depth;
            }
        }

        @Override
        public void startJSON() throws ParseException, IOException {
            depth = 0;
        }

        @Override
        public void endJSON() throws ParseException, IOException {
            ArkDockUtils.log("objCount", count, "maxDepth", procStack.size(), "currDepth", depth, "keyRes", resCount, "prim", primCount);
        }
        @Override
        public boolean startObjectEntry(String arg0) throws ParseException, IOException {
            this.key = arg0;
            ++resCount;
            currKey = ArkDockUtils.fromString(key, GeojsonKeys.NULL);
            coordIdx = -1;

            return true;
        }

        @Override
        public boolean primitive(Object arg0) throws ParseException, IOException {
            ++primCount;
            if (0 <= coordIdx) {
                coords[coordIdx++] = (Double) arg0;
            } else {
                switch (currKey) {
                case type:
                    GeojsonTypes type = ArkDockUtils.fromString((String) arg0, GeojsonTypes.NULL);

                    if (GeojsonTypes.NULL != type) {
                        procStack.set(depth-1, type);
                        currObjs.put(type,  builder.newGeojsonObj(type));
                    }
                    break;
                default:
                    break;
                }
            }

            return true;
        }

        @Override
        public boolean endObjectEntry() throws ParseException, IOException {
            return true;
        }

        @Override
        public boolean startObject() throws ParseException, IOException {
            ++count;
            step(true);
            return true;
        }

        @Override
        public boolean endObject() throws ParseException, IOException {
            step(false);
            return true;
        }

        @Override
        public boolean startArray() throws ParseException, IOException {
            step(true);
            coordIdx = 0;
            return true;
        }

        @Override
        public boolean endArray() throws ParseException, IOException {
            coordIdx = -1;
            step(false);
            return true;
        }
    }

    public void parse(Reader r, GeojsonBuilder factory) throws IOException, ParseException {
        JSONParser p = new JSONParser();
        MainHandler h = new MainHandler(factory);

        p.parse(r, h);
    }
}
