package ark.dock.geo.json;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public interface ArkDockGeojson2D extends ArkDockGeojsonConsts {

    public class GeojsonBuilder2DDouble extends GeojsonBuilder {
        Path2D.Double pendingPath;

        @Override
        public Object newGeojsonObj(GeojsonType gjt) {
            switch (gjt) {
            case Point:
                return new Point2D.Double();
            case Polygon:
                return new GeojsonPolygon<Path2D.Double>();
            case LineString:
                return new Path2D.Double();
            default:
                return super.newGeojsonObj(gjt);
            }
        }

        void optAddPoint(Point2D.Double pt) {
            if (null != pendingPath) {
                if (null == pendingPath.getCurrentPoint()) {
                    pendingPath.moveTo(pt.x, pt.y);
                } else {
                    pendingPath.lineTo(pt.x, pt.y);
                }
            }
        }

        @Override
        public boolean addChild(Object data, int idx) {
            // double[] coords;
            Point2D.Double pt;

            switch (currType) {
            case Point:
                // coords = (double[]) data;
                // ((Point2D.Double)currObj).setLocation(coords[0], coords[1]);
                pt = (Point2D.Double) currObj;
                switch (idx) {
                case 0:
                    pt.x = (double) data;
                    break;
                case 1:
                    pt.y = (double) data;
                    optAddPoint(pt);
                    break;
                }
                return true;
            case LineString:
                pendingPath = (Path2D.Double) currObj;
//                pt = (Point2D.Double) data;
//                Path2D.Double path = (Path2D.Double) currObj;
//                if (null == path.getCurrentPoint()) {
//                    path.moveTo(pt.x, pt.y);
//                } else {
//                    path.lineTo(pt.x, pt.y);
//                }
                return true;
            default:
                return super.addChild(data, idx);
            }
        }
    }
}
