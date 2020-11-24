package ark.dock.geo.geojson;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import ark.dock.ArkDock;
import ark.dock.ArkDockConsts;
import ark.dock.ArkDockUnit;
import ark.dock.io.json.ArkDockJsonConsts;
import dust.gen.DustGenTranslator;
import dust.gen.DustGenUtils;

public interface ArkDockGeojsonConsts extends ArkDockConsts, ArkDockJsonConsts, ArkDockDslGeojson {
	enum GeojsonKey {
		type, id, features, geometry, geometries, coordinates, properties, bbox, coordSys;
	}

	enum GeojsonGeometryType {
		Point, MultiPoint(true, Point), LineString(false, Point), MultiLineString(true, LineString),
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
		private static final Map<String, Object> EMPTY_PROPS = Collections.unmodifiableMap(new HashMap<>());

		public GeojsonObjectType typeObj;
		public Map<String, Object> data = new TreeMap<>();

		public ArkDockGeojsonGeometry geometry;
		
		public GeojsonContext() {
			this(EMPTY_PROPS);
		}

		public GeojsonContext(Map<String, Object> props) {
			data.put(GeojsonKey.properties.name(), props);
		}

		@SuppressWarnings("unchecked")
		public <RetType> RetType getData(Object key) {
			return (RetType) data.get(key.toString());
		}

		@Override
		public String toString() {
			return DustGenUtils.sbAppend(null, ",", true, typeObj, geometry, data).toString();
		}
	}

	class GeometryTranslator extends DustGenTranslator<GeojsonGeometryType, DustEntity> {
		DslGeojson dsl;
		
		public GeometryTranslator() {
			dsl = ArkDock.getDsl(DslGeojson.class);
			
			add(GeojsonGeometryType.Point, dsl.tagGeometryPoint);
			add(GeojsonGeometryType.MultiPoint, dsl.tagGeometryMultiPoint);
			add(GeojsonGeometryType.LineString, dsl.tagGeometryLineString);
			add(GeojsonGeometryType.MultiLineString, dsl.tagGeometryMultiLineString);
			add(GeojsonGeometryType.Polygon, dsl.tagGeometryPolygon);
			add(GeojsonGeometryType.MultiPolygon, dsl.tagGeometryMultiPolygon);
			add(GeojsonGeometryType.GeometryCollection, dsl.tagGeometryCollection);
		}
		
		public void updateEntity(ArkDockUnit unit, DustEntity entity, ArkDockGeojsonGeometry geo) {
			unit.setMember(entity, dsl.memGeometryData, geo, null);
			unit.setTag(entity, getRight(geo.getType()), DustDialogCmd.ADD);
		}
	}
}
