package ark.dock.geo.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ark.dock.ArkDockUtils;
import ark.dock.ArkDockVisitor;
import ark.dock.json.ArkDockJsonConsts;
import ark.dock.json.ArkDockJsonReaderAgent;
import dust.gen.DustGenCounter;
import dust.gen.DustGenDevUtils;
import dust.gen.DustGenException;
import dust.gen.DustGenLog;

public class ArkDockGeojsonParser implements ArkDockJsonConsts, ArkDockGeojsonConsts, DustGenDevUtils {

    static class RootAgent implements DustAgent {

        class CoordinatesAgent implements DustAgent {
            private GeojsonType gjt;
            private Object root;
            
            public void init(GeojsonType gjt) {
                this.gjt = gjt;
                this.root = null;
            }

            @Override
            public DustResultType agentAction(DustAgentAction action) throws Exception {
                switch (action) {
                case BEGIN:
                    if (ctx.block == JsonBlock.Array) {
                        Object gjtOb = builder.newGeojsonObj(gjt);
                        if ( null == root ) {
                            root = gjtOb;
                        }
                        builder.select(gjtOb);
                        jsonVisitor.setProcCtx(gjtOb);
                        typeCounter.add(gjt);

                        gjt = gjt.childType;

                        return DustResultType.ACCEPT_READ;
                    }
                    break;
                case END:
                    switch (ctx.block) {
                    case Array: {
                        Object oc = jsonVisitor.getProcCtx();
                        Object parent = jsonVisitor.getProcCtxNeighbor(true);

                        builder.select(parent);
                        builder.addChild(oc);

                        gjt = builder.getObjType(oc);

                        return DustResultType.ACCEPT_READ;
                    }
                    case Entry:
                        return DustResultType.ACCEPT_READ;
                    default:
                        break;
                    }
                    break;
                case PROCESS:
                    builder.addChild(ctx.param);
                    return DustResultType.ACCEPT_READ;
                default:
                    break;
                }

                return DustGenException.throwException(null, "Invalid GeoJSON parse state");
            }
            
            public Object getRoot() {
                return root;
            }
        }

        GeojsonObjectSource obSrc;
        Map<GeojsonKey, Object> currObj;

        JsonContext ctx = new JsonContext();

        GeojsonBuilder builder;

        ArkDockVisitor<JsonContext> jsonVisitor;
        CoordinatesAgent coordAgent;
        ArkDockJsonReaderAgent propertiesAgent;

        String keyStr;
        GeojsonKey keyGeoJSON = GeojsonKey.NULL;
        GeojsonType currType;
        
        DustGenCounter typeCounter = new DustGenCounter(true);

        public RootAgent(GeojsonBuilder builder, GeojsonObjectSource obSrc) {
            this.builder = builder;
            this.obSrc = obSrc;
            jsonVisitor = new ArkDockVisitor<JsonContext>(ctx, this);
            coordAgent = new CoordinatesAgent();
            propertiesAgent = new ArkDockJsonReaderAgent(jsonVisitor);
        }

        public void parse(Reader r, GeojsonBuilder builder) throws IOException, ParseException {
            DevTimer parseTimer = new DevTimer("Parse");

            JSONParser p = new JSONParser();
            JsonContentVisitor h = new JsonContentVisitor(jsonVisitor);
            p.parse(r, h);
            
            parseTimer.log();
            DustGenLog.log("Content:", this);
        }

        @Override
        public DustResultType agentAction(DustAgentAction action) throws Exception {
            switch (action) {
            case BEGIN:
                switch (ctx.block) {
                case Entry:
                    keyStr = (String) ctx.param;
                    keyGeoJSON = ArkDockUtils.fromString(keyStr, GeojsonKey.NULL);
                    
                    switch (keyGeoJSON) {
                    case coordinates:
                        coordAgent.init(currType);
                        jsonVisitor.setRelay(coordAgent, true);
                        break;
                    case properties:
                        propertiesAgent.agentAction(DustAgentAction.INIT);
                        jsonVisitor.setRelay(propertiesAgent, true);
                        break;
                    default:
                        break;
                    }

                    break;
                default:
                    break;
                }
                break;
            case END:
                switch (ctx.block) {
                case Entry:
                    Object content = null;

                    switch (keyGeoJSON) {
                    case coordinates:
                        content = coordAgent.getRoot();
                        break;
                    case properties:
                        content = propertiesAgent.getRoot();
                        break;
                    default:
                        break;
                    }
                    
                    if ( null != content ) {
                        if ( null == currObj ) {
                            currObj = obSrc.getObToFill();
                        }
                        currObj.put(keyGeoJSON, content);
                        currObj.put(GeojsonKey.type, currType);
                    }

                    break;
                default:
                    break;
                }
                break;
            case INIT:
                break;
            case PROCESS:
                if (GeojsonKey.type == keyGeoJSON) {
                    currType = ArkDockUtils.fromString((String) ctx.param, GeojsonType.NULL);

                    if (GeojsonType.Feature == currType) {
                        currObj = obSrc.getObToFill();
                    }
                }
                break;
            case RELEASE:
                break;
            default:
                break;
            }

            return DustResultType.ACCEPT;
        }

        @Override
        public String toString() {
            return typeCounter.toString();
        }
    };

    public static void parse(File f, GeojsonBuilder builder, GeojsonObjectSource obSrc) throws Exception {
        DustGenLog.log("Parsing GeoJSON file", f.getName(), "size", f.length());
        Reader fr = new InputStreamReader(new FileInputStream(f), "UTF-8");
        parse(fr, builder, obSrc);
    }

    public static void parse(Reader r, GeojsonBuilder builder, GeojsonObjectSource obSrc) throws IOException, ParseException {
        new RootAgent(builder, obSrc).parse(r, builder);
    }
}
