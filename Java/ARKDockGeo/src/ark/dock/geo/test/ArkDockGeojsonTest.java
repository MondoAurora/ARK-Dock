package ark.dock.geo.test;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import ark.dock.geo.json.ArkDockGeojson2D.GeojsonBuilder2DDouble;
import dust.gen.DustGenLog;
import ark.dock.geo.json.ArkDockGeojsonParser;

public class ArkDockGeojsonTest {
    public static void importGeojson(File f, Map<String, Object> data) throws Exception {
        GeojsonBuilder2DDouble bd = new GeojsonBuilder2DDouble();
        
        ArkDockGeojsonParser.parse(f, bd, data);
        
//        JSONParser p = new JSONParser();        
//        Set<String> locs = new TreeSet<>();
//        
//        p.parse(new FileReader(fLoc), new JsonValueCollector("LOC_CD", locs));
//        
//        ArkDockUtils.log("count", locs.size());
    }
    
    public static void main(String[] args) throws Exception {
        JSONObject i = (JSONObject) JSONValue.parseWithException(new FileReader("testImport.json"));
        JSONArray a = (JSONArray) i.get("files");
        
        Map<String, Map<String, Object>> result = new HashMap<>();
        
        for ( Object f : a ) {            
            File dr = new File((String) f);
            String name = dr.getName();
            Map<String, Object> data = new HashMap<>();            
            importGeojson(dr, data);            
            result.put(name, data);
        }
        
        for (Map.Entry<String, Map<String, Object>> e : result.entrySet()) {
            DustGenLog.log(e.getKey(), e.getValue().size());
        }
    }
}
