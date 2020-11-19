package ark.dock.geo;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import ark.dock.ArkDockUtils;
import dust.gen.DustGenLog;

public class ArkDockGeoEnv {
    Map<String, Path2D.Double> idToPoly = new HashMap<>();
    
    public Set<String> readMultiPoly(Object source) {
        Set<String> ret = new TreeSet<>();
        
        ArrayList<?> polygons = (ArrayList<?>) ArkDockUtils.resolvePath(source, "geometry", "coordinates");
        
        for ( Object p : polygons ) {
            for ( Object p2 : (ArrayList<?>) p ) {
                ret.add(registerPolygon((ArrayList<?>) p2));
            }
        }
        
        return ret;
    }

    public String registerPolygon(ArrayList<?> path) {
        double x, y;

        Path2D.Double pp = null;
        for (Object pt : path) {
            ArrayList<?> point = (ArrayList<?>) pt;
            x = (Double) point.get(0);
            y = (Double) point.get(1);
            if (null == pp) {
                pp = new Path2D.Double();
                pp.moveTo(x, y);
            } else {
                pp.lineTo(x, y);
            }
        }
        return registerPolygon(pp);
    }
    
    public String registerPolygon(Path2D.Double poly) {
        double dd[] = new double[6];
        ArrayList<Double> pts = new ArrayList<>();
        String id = null;
        int cnt;
        
        for ( PathIterator pi = poly.getPathIterator(null); !pi.isDone(); pi.next() ) {
            pi.currentSegment(dd);
            pts.add(dd[0]);
            pts.add(dd[1]);
        }
        cnt = pts.size();
        
        for (Map.Entry<String, Path2D.Double> ip : idToPoly.entrySet()) {
            int idx = 0;
            id = ip.getKey();
            
            for ( PathIterator pi = ip.getValue().getPathIterator(null); !pi.isDone(); pi.next() ) {
                pi.currentSegment(dd);
                if((idx >= cnt) || (dd[0] != pts.get(idx++)) || (dd[1] != pts.get(idx++))) {
                    id = null;
                }
            }
            
            if ( null != id ) {
                break;
            }
        }


        if (null == id) {
            id = "ArkDockGeoPoly_" + idToPoly.size();
            idToPoly.put(id, poly);
        }

        return id;
    }

    public String findByPoint(double x, double y) {
        for (Map.Entry<String, Path2D.Double> pi : idToPoly.entrySet()) {
            if (pi.getValue().contains(x, y)) {
                return pi.getKey();
            }
        }

        return null;
    }

    public PathIterator getPolyIterator(String id, AffineTransform at) {
        Path2D.Double poly = idToPoly.get(id);
        return (null == poly) ? null : poly.getPathIterator(at);
    }
    

    public void testPoly(FileReader fr) throws Exception {
        JSONObject data = (JSONObject) JSONValue.parse(fr);
        JSONObject item;

        JSONArray features = (JSONArray) data.get("features");

        int idx = 0;

        DustGenLog.log("Import Locations", features.size(), "records from", data.get("name"));

        for (Object o : features) {
            item = (JSONObject) o;
            Set<String> polyRefs = readMultiPoly(item);

            DustGenLog.log(polyRefs);

            if (0 == (++idx % 500)) {
                DustGenLog.log("\n*************\n row", idx, "\n*************\n");
            }
        }
    }

}
