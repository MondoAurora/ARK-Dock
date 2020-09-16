package ark.dock.geo.test;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import ark.dock.geo.json.ArkDockGeojson2D.GeojsonBuilder2DDouble;
import ark.dock.geo.json.ArkDockGeojsonConsts.GeojsonKey;
import ark.dock.geo.json.ArkDockGeojsonConsts.GeojsonObjectSource;
import dust.gen.DustGenLog;
import ark.dock.geo.json.ArkDockGeojsonParser;

public class ArkDockGeojsonTest {
    public static void importGeojson(File f, GeojsonObjectSource obSrc) throws Exception {
        GeojsonBuilder2DDouble bd = new GeojsonBuilder2DDouble();
        
        ArkDockGeojsonParser.parse(f, bd, obSrc);
    }
    
    public static void main(String[] args) throws Exception {
        JSONObject i = (JSONObject) JSONValue.parseWithException(new FileReader("testImport.json"));
        JSONArray a = (JSONArray) i.get("files");
        
        Map<GeojsonKey, Object> target = new HashMap<>();
        
        Map<String, Map<String, Object>> result = new HashMap<>();
        
        for ( Object f : a ) {            
            File dr = new File((String) f);
            String name = dr.getName();
            
            GeojsonObjectSource obSrc = new GeojsonObjectSource() {
                int count;
                @Override
                public Map<GeojsonKey, Object> getObToFill() {
                    ++count;
                    target.clear();
                    return target;
                }
                
                @Override
                public String toString() {
                    return name + " contains " + count + " features.";
                }
            };
            
            importGeojson(dr, obSrc);       
            DustGenLog.log(obSrc);
        }
        
        for (Map.Entry<String, Map<String, Object>> e : result.entrySet()) {
            DustGenLog.log(e.getKey(), e.getValue().size());
        }
    }
}
