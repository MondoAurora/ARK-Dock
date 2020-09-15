package ark.dock.geo.json;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public interface ArkDockGeojson2D extends ArkDockGeojsonConsts {

    public class GeojsonBuilder2DDouble extends GeojsonBuilder {
        // static Map<Class, GeojsonType> classToType = new HashMap<>();

        // Path2D.Double pendingPath;
        int coordIdx;

        @Override
        public Object newGeojsonObj(GeojsonType gjt) {
            switch (gjt) {
            case Point:
                coordIdx = 0;
                return new Point2D.Double();
            case Polygon:
                return new GeojsonPolygon<Path2D.Double>();
            case LineString:
                return new Path2D.Double();
            default:
                return super.newGeojsonObj(gjt);
            }
        }

        @Override
        public GeojsonType getObjType(Object geoObj) {
            if (geoObj instanceof Point2D.Double) {
                return GeojsonType.Point;
            } else if (geoObj instanceof Path2D.Double) {
               return GeojsonType.LineString;
            }
            
            return super.getObjType(geoObj);
        }

        // void optAddPoint(Point2D.Double pt) {
        // if (null != pendingPath) {
        // if (null == pendingPath.getCurrentPoint()) {
        // pendingPath.moveTo(pt.x, pt.y);
        // } else {
        // pendingPath.lineTo(pt.x, pt.y);
        // }
        // }
        // }

        @Override
        public boolean addChild(Object data) {
            Point2D.Double pt;
            if (currObj instanceof Point2D.Double) {
                pt = (Point2D.Double) currObj;
                switch (coordIdx++) {
                case 0:
                    pt.x = (double) data;
                    break;
                case 1:
                    pt.y = (double) data;
                    break;
                }
            } else if (currObj instanceof Path2D.Double) {
                Path2D.Double path = (Path2D.Double) currObj;
                pt = (Point2D.Double) data;
                if (null == path.getCurrentPoint()) {
                    path.moveTo(pt.x, pt.y);
                } else {
                    path.lineTo(pt.x, pt.y);
                }
            } else {
                return super.addChild(data);
            }

            return true;

            // // double[] coords;
            // Point2D.Double pt;
            //
            // switch (currType) {
            // case Point:
            // pt = (Point2D.Double) currObj;
            //// ArrayList<Double> pts = (ArrayList<Double>) data;
            //// ((Point2D.Double)currObj).setLocation(pts.get(0), pts.get(1));
            // switch (coordIdx ++) {
            // case 0:
            // pt.x = (double) data;
            // break;
            // case 1:
            // pt.y = (double) data;
            //// optAddPoint(pt);
            // break;
            // }
            // return true;
            // case LineString:
            //// pendingPath = (Path2D.Double) currObj;
            // pt = (Point2D.Double) data;
            // Path2D.Double path = (Path2D.Double) currObj;
            // if (null == path.getCurrentPoint()) {
            // path.moveTo(pt.x, pt.y);
            // } else {
            // path.lineTo(pt.x, pt.y);
            // }
            // return true;
            // default:
            // return super.addChild(data);
            // }
        }
    }
}
