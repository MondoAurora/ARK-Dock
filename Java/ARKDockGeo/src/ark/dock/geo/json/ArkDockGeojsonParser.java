package ark.dock.geo.json;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ark.dock.ArkDockUtils;
import ark.dock.json.ArkDockJsonUtils;
import ark.dock.json.ArkDockJsonValueAgent;
import dust.gen.DustGenAgentSmart;
import dust.gen.DustGenCounter;
import dust.gen.DustGenDevUtils;
import dust.gen.DustGenLog;

public class ArkDockGeojsonParser implements ArkDockJsonUtils, ArkDockGeojsonConsts, DustGenDevUtils {

    static class RootAgent implements DustGenAgent {

        class CoordinatesAgent implements DustGenAgent {
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
                    if (ctx.getBlock() == JsonBlock.Array) {
                        Object gjtOb = builder.newGeojsonObj(gjt);
                        if ( null == root ) {
                            root = gjtOb;
                        }
                        builder.select(gjtOb);
                        relayAgent.setCtxOb(gjtOb);
                        typeCounter.add(gjt);

                        gjt = gjt.childType;

                        return DustResultType.ACCEPT_READ;
                    }
                    break;
                case END:
                    switch (ctx.getBlock()) {
                    case Array: {
                        Object oc = relayAgent.getCtxOb();
                        Object parent = relayAgent.getCtxNeighbor(true);

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
                    builder.addChild(ctx.getParam());
                    return DustResultType.ACCEPT_READ;
                default:
                    break;
                }

                return DustException.throwException(null, "Invalid GeoJSON parse state");
            }
            
            public Object getRoot() {
                return root;
            }
        }

        GeojsonObjectSource obSrc;
        Map<GeojsonKey, Object> currObj;

        JsonContext ctx = new JsonContext();

        GeojsonBuilder builder;

        DustGenAgentSmart relayAgent;
        CoordinatesAgent coordAgent;
        ArkDockJsonValueAgent propertiesAgent;

        String keyStr;
        GeojsonKey keyGeoJSON = GeojsonKey.NULL;
        GeojsonType currType;
        
        DustGenCounter typeCounter = new DustGenCounter(true);

        public RootAgent(GeojsonBuilder builder, GeojsonObjectSource obSrc) {
            this.builder = builder;
            this.obSrc = obSrc;
            relayAgent = new DustGenAgentSmart(this);
            coordAgent = new CoordinatesAgent();
            propertiesAgent = new ArkDockJsonValueAgent(relayAgent, ctx);
        }

        public void parse(Reader r, GeojsonBuilder builder) throws IOException, ParseException {
            DevTimer parseTimer = new DevTimer("Parse");

            JSONParser p = new JSONParser();
            JsonContentHandlerAgent h = new JsonContentHandlerAgent(relayAgent, ctx);
            p.parse(r, h);
            
            parseTimer.log();
            DustGenLog.log("Content:", this);
        }

        @Override
        public DustResultType agentAction(DustAgentAction action) throws Exception {
            switch (action) {
            case BEGIN:
                switch (ctx.getBlock()) {
                case Entry:
                    keyStr = (String) ctx.getParam();
                    keyGeoJSON = ArkDockUtils.fromString(keyStr, GeojsonKey.NULL);
                    
                    switch (keyGeoJSON) {
                    case coordinates:
                        coordAgent.init(currType);
                        relayAgent.setRelay(coordAgent, true);
                        break;
                    case properties:
                        propertiesAgent.agentAction(DustAgentAction.INIT);
                        relayAgent.setRelay(propertiesAgent, true);
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
                switch (ctx.getBlock()) {
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
                    currType = ArkDockUtils.fromString((String) ctx.getParam(), GeojsonType.NULL);

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
        FileReader fr = new FileReader(f);
        parse(fr, builder, obSrc);
    }

    public static void parse(Reader r, GeojsonBuilder builder, GeojsonObjectSource obSrc) throws IOException, ParseException {
        new RootAgent(builder, obSrc).parse(r, builder);
    }
}
