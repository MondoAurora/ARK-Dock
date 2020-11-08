package ark.dock.geo.geojson;

import java.util.Map;

import ark.dock.ArkDockConsts;
import dust.gen.DustGenUtils;

public interface ArkDockGeojsonConsts extends ArkDockConsts {
    enum GeojsonKey {
        type, id, features, geometry, geometries, coordinates, properties, bbox;
    }

    enum GeojsonGeometryType {
        Point, MultiPoint(true, Point), 
        LineString(false, Point), MultiLineString(true, LineString), 
        Polygon(false, LineString), MultiPolygon(true, Polygon), 
        
        GeometryCollection(true, null, GeojsonKey.geometries);
        
        public final boolean container;
        public final GeojsonGeometryType childType;
        public final GeojsonKey childKey;

        private GeojsonGeometryType(boolean container, GeojsonGeometryType childType, GeojsonKey childKey) {
            this.container = container;
            this.childType = childType;
            this.childKey = childKey;
        }
        private GeojsonGeometryType(boolean container, GeojsonGeometryType childType) {
            this(container, childType, GeojsonKey.coordinates);
        }
        private GeojsonGeometryType() {
            this(false, null, null);
        }
    }

    enum GeojsonObjectType {
        Feature, FeatureCollection
    }
    
    class GeojsonContext {
    	GeojsonObjectType typeObj;
    	Map<String, Object> data;

    	ArkDockGeojsonGeometry<?> geometry;
    	
    	public GeojsonObjectType getTypeObj() {
			return typeObj;
		}
    	
    	public ArkDockGeojsonGeometry<?> getGeometry() {
			return geometry;
		}
    	
    	public Iterable<String> getDataKeys() {
			return data.keySet();
		}
    	
    	public Object getData(String key) {
			return data.get(key);
		}
    	
    	@Override
    	public String toString() {
    		return DustGenUtils.sbAppend(null, ",", true, typeObj, geometry, data).toString();
    	}
    }
    
}
