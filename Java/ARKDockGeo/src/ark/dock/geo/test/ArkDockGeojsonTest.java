package ark.dock.geo.test;

import java.io.File;
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import ark.dock.ArkDockUtils;
import ark.dock.geo.json.ArkDockGeojson2D.GeojsonBuilder2DDouble;
import ark.dock.geo.json.ArkDockGeojsonParser;

public class ArkDockGeojsonTest {
    public static void importGeojson(File fLoc) throws Exception {
        long l = System.currentTimeMillis();
        
        FileReader fr = new FileReader(fLoc);       
        GeojsonBuilder2DDouble bd= new GeojsonBuilder2DDouble();
        ArkDockGeojsonParser gp = new ArkDockGeojsonParser();
        
        gp.parse(fr, bd);
        
//        JSONParser p = new JSONParser();        
//        Set<String> locs = new TreeSet<>();
//        
//        p.parse(new FileReader(fLoc), new JsonValueCollector("LOC_CD", locs));
//        
//        ArkDockUtils.log("count", locs.size());
        
        ArkDockUtils.log("Time", System.currentTimeMillis() - l);
    }
    
    public static void main(String[] args) throws Exception {
        JSONObject i = (JSONObject) JSONValue.parseWithException(new FileReader("testImport.json"));
        JSONArray a = (JSONArray) i.get("files");
        
        for ( Object f : a ) {            
            File dr = new File((String) f);
            importGeojson(dr);            
        }
    }
}
