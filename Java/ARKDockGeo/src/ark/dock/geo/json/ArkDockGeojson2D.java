package ark.dock.geo.json;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public interface ArkDockGeojson2D extends ArkDockGeojsonConsts {

    public class GeojsonBuilder2DDouble extends GeojsonBuilder {
        // static Map<Class, GeojsonType> classToType = new HashMap<>();
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
        }
    }
}
