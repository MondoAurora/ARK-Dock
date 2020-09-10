package ark.dock.geo.json;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public interface ArkDockGeojson2D extends ArkDockGeojsonConsts {

    public class GeojsonBuilder2DDouble extends GeojsonBuilder {
        @Override
        public Object newGeojsonObj(GeojsonTypes gjt) {
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

        @Override
        public boolean addChild(Object data) {
            switch (currType) {
            case Point: 
                ((Point2D.Double)currObj).setLocation(((double[])data)[0], ((double[])data)[1]);
                return true;
            default:
                return super.addChild(data);
            }
        }
    }
}
