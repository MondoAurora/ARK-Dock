package ark.dock.geo.json;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ark.dock.ArkDockUtils;
import ark.dock.json.ArkDockJsonUtils;

public class ArkDockGeojsonParser2 implements ArkDockJsonUtils, ArkDockGeojsonConsts {

    static class MainHandler extends JsonContentHandlerBase {
        JsonArrayCollector hndPoint = new JsonArrayCollector();
        JsonFlatCollector hndProps = new JsonFlatCollector();

        Map<GeojsonType, Object> currObjs = new HashMap<>();
        ArrayList<GeojsonType> procStack = new ArrayList<>();
        int depth;
        // int cutDepth;
        int count;
        int resCount;
        int primCount;
        int coordIdx;

        double[] coords = new double[6];

        String key;
        GeojsonKey currKey = GeojsonKey.NULL;

        GeojsonBuilder builder;

        public MainHandler(GeojsonBuilder builder) {
            this.builder = builder;
        }

        void step(boolean down) {
            if (down) {
                ++depth;
                if (procStack.size() <= depth) {
                    procStack.add(null);
                }

            } else {
                --depth;
            }
        }

        @Override
        public void startJSON() throws ParseException, IOException {
            depth = -1;
        }

        @Override
        public void endJSON() throws ParseException, IOException {
            ArkDockUtils.log("objCount", count, "maxDepth", procStack.size(), "currDepth", depth, "keyRes", resCount, "prim", primCount);
        }
        @Override
        public boolean startObjectEntry(String arg0) throws ParseException, IOException {
            this.key = arg0;
            ++resCount;
            currKey = ArkDockUtils.fromString(key, GeojsonKey.NULL);
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
                    GeojsonType type = ArkDockUtils.fromString((String) arg0, GeojsonType.NULL);
                    switch (type) {
                    case NULL:
                        break;
                    default:
                        if ((-1 == depth) || (type == GeojsonType.GeometryCollection)) {
                            step(true);
                            currObjs.put(type, builder.newGeojsonObj(type));
                        }
                        procStack.set(depth, type);
                        break;
                    }
                    break;
                case features:
                case geometries:
                case geometry:
                    step(true);
                    break;
                default:
                    break;
                }
                currKey = GeojsonKey.NULL;
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
            // step(true);
            return true;
        }

        @Override
        public boolean endObject() throws ParseException, IOException {
            // step(false);
            return true;
        }

        @Override
        public boolean startArray() throws ParseException, IOException {
            GeojsonType type = procStack.get(depth);

            Object geo = currObjs.get(type);
            if (null == geo) {
                geo = builder.newGeojsonObj(type);
                currObjs.put(type, geo);
            }
            builder.select(type, geo);

            step(true);

            type = type.childType;
            procStack.set(depth, type);

            if (null == type) {
                coordIdx = 0;
            }

            return true;
        }

        @Override
        public boolean endArray() throws ParseException, IOException {
            if (-1 != coordIdx) {
                coordIdx = -1;
                builder.addChild(coords, 0);
            }
            step(false);
            GeojsonType type = procStack.get(depth);

            if (0 < depth) {
                if (1 == depth) {
                    ArkDockUtils.log("hopp");
                }
                GeojsonType pType = procStack.get(depth - 1);
                builder.select(pType, currObjs.get(pType));
                builder.addChild(currObjs.remove(type), 0);
            }
            return true;
        }
    }

    @SuppressWarnings("rawtypes")
    static class GeoFactory implements ContainerFactory {

        GeojsonBuilder builder;

        Map<Object, List> currArrays = new HashMap<>();
        GeojsonType arrType = GeojsonType.NULL;

        int objCount, itemCount;

        class GeoMap extends TreeMap<String, Object> {
            private static final long serialVersionUID = 1L;

            GeojsonType type = GeojsonType.NULL;

            @Override
            public Object put(String key, Object value) {
                if ("type".equals(key)) {
                    this.type = ArkDockUtils.fromString((String) value, GeojsonType.NULL);
                }

                if (type.isArrKey(key)) {
                    value = optExtractObFrom(value, true);
                } else {
                    arrType = this.type;
                }

                return super.put(key, value);
            }
        }

        class GeoList extends AbstractList {
            final GeojsonType type;
            Object ob = null;
            int idx = 0;

            public GeoList(GeojsonType type) {
                this.type = type;
            }

            public Object load() {
                if (null == ob) {
                    ob = builder.newGeojsonObj(type);
                }
                return ob;
            }

            @Override
            public void clear() {
                this.ob = null;
                idx = 0;
            }

            @Override
            public Object get(int arg0) {
                return null;
            }

            @Override
            public int size() {
                return idx;
            }

            @Override
            public boolean add(Object data) {
                data = optExtractObFrom(data, false);

                if (null != data) {
                    load();
                    builder.select(type, ob);
                    builder.addChild(data, idx++);
                }

//                arrType = type;

                ++itemCount;
                return true;
            }
        }

        public GeoFactory(GeojsonBuilder builder) {
            this.builder = builder;
        }

        Object optExtractObFrom(Object data, boolean createIfMissing) {
            if (data instanceof GeoList) {
                GeoList src = (GeoList) data;
                data = src.ob;

                if (createIfMissing && (null == data)) {
                    data = src.load();
                }
            }

            return data;
        }

        @Override
        public Map createObjectContainer() {
            ++objCount;
            return new GeoMap();
        }

        @Override
        public List creatArrayContainer() {
            List ret = currArrays.get(arrType);

            if (null == ret) {
                ret = new GeoList(arrType);
                currArrays.put(arrType, ret);
            } else {
                ret.clear();
            }

            
            arrType = arrType.childType;

            return ret;
        }

        public void log() {
            ArkDockUtils.log("Objects", objCount, "Items", itemCount);
        }
    }

    public static void parse(File f, GeojsonBuilder builder) throws Exception {
        ArkDockUtils.log("Parsing GeoJSON file", f.getName());
        FileReader fr = new FileReader(f);
        parse(fr, builder);
    }

    public static void parse(Reader r, GeojsonBuilder builder) throws IOException, ParseException {
        long l = System.currentTimeMillis();

        JSONParser p = new JSONParser();

        // MainHandler h = new MainHandler(factory);
        // p.parse(r, h);

        GeoFactory f = new GeoFactory(builder);
        p.parse(r, f);
        f.log();

        long elapse = System.currentTimeMillis() - l;

        ArkDockUtils.log("Time", elapse);
    }
}
