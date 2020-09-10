package ark.dock.geo.json;

import java.util.ArrayList;
import java.util.Collection;

import org.json.simple.parser.ContentHandler;

public interface ArkDockGeojsonConsts {
    enum GeojsonTypes {
        Point, MultiPoint(true, Point), 
        LineString, MultiLineString(true, LineString), 
        Polygon(false, LineString), MultiPolygon(true, Polygon), GeometryCollection(true), 
        Feature, FeatureCollection(true, Feature), 
        NULL;

        public final boolean container;
        public final GeojsonTypes childType;

        private GeojsonTypes(boolean coll, GeojsonTypes childType) {
            this.container = coll;
            this.childType = childType;
        }
        private GeojsonTypes(GeojsonTypes childType) {
            this(false, childType);
        }
        private GeojsonTypes(boolean coll) {
            this(coll, null);
        }
        private GeojsonTypes() {
            this(false, null);
        }
    };

    enum GeojsonKeys {
        type, features, geometry, geometries, coordinates, properties, bbox, NULL
    }

    abstract class GeojsonBuilder {
        protected ContentHandler extHandler;
        protected GeojsonTypes currType;
        protected Object currObj;
        
        public void select(GeojsonTypes gjt, Object geoObj) {
            currType = gjt;
            currObj = geoObj;
        }
        
        public Object newBBox(Collection<?> points) {
            return null;
        }

        public boolean setBBox(Object bb) {
            if (currObj instanceof GeojsonObjectContainer) {
                ((GeojsonObjectContainer) currObj).setBBox(bb);
                return true;
            }
            return false;
        }

        public boolean addChild(Object data) {
            if (currObj instanceof GeojsonObjectContainer) {
                ((GeojsonObjectContainer) currObj).addChild(data);
                return true;
            }
            return false;
        }

        public Object newGeojsonObj(GeojsonTypes gjt) {
            return gjt.container ? new GeojsonObjectArray(gjt) : null;
        }
        
        public ContentHandler getExtHandler() {
            return extHandler;
        }
    }

    public interface GeojsonObjectContainer {
        boolean addChild(Object data);
        void setBBox(Object bb);
        Object getBbox();
    }

    public class GeojsonObjectArray extends ArrayList<Object> implements GeojsonObjectContainer {
        private static final long serialVersionUID = 1L;

        private final GeojsonTypes type;
        protected Object bbox;

        GeojsonObjectArray(GeojsonTypes t) {
            this.type = t;
        }

        public GeojsonTypes getType() {
            return type;
        }

        public void setBBox(Object bb) {
            this.bbox = bb;
        }

        public Object getBbox() {
            return bbox;
        }
        
        @Override
        public boolean addChild(Object data) {
            add(data);
            return true;
        }
    }
    
    public class GeojsonPolygon<NativeLineRing> implements GeojsonObjectContainer {
        protected Object bbox;
        protected NativeLineRing exterior;
        protected ArrayList<NativeLineRing> holes;

        public GeojsonTypes getType() {
            return GeojsonTypes.Polygon;
        }

        public void setBBox(Object bb) {
            this.bbox = bb;
        }

        public Object getBbox() {
            return bbox;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public boolean addChild(Object data) {
            NativeLineRing lr = (NativeLineRing) data;
            if ( null == exterior ) {
                exterior = lr;
            } else {
                getHoles().add(lr);
            }
            return true;
        }
        
        public NativeLineRing getExterior() {
            return exterior;
        }
        
        public ArrayList<NativeLineRing> getHoles() {
            if ( null == holes ) {
                holes = new ArrayList<>();
            }
            return holes;
        }
    }
}
