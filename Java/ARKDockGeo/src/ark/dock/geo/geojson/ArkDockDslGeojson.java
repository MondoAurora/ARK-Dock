package ark.dock.geo.geojson;

import ark.dock.ArkDock;
import ark.dock.ArkDockConsts;
import ark.dock.ArkDockDslBuilder;

// source https://tools.ietf.org/html/rfc7946

public interface ArkDockDslGeojson extends ArkDockConsts {
	
	public class DslGeojson {
		public final DustEntity unit;

		public final DustEntity typObject;
		public final DustEntity memObBbox;
		
		public final DustEntity typGeometry;
		public final DustEntity memGeometryData;
		
		public final DustEntity tagGeometry;
		public final DustEntity tagGeometryPoint;
		public final DustEntity tagGeometryMultiPoint;
		public final DustEntity tagGeometryLineString;
		public final DustEntity tagGeometryMultiLineString;
		public final DustEntity tagGeometryPolygon;
		public final DustEntity tagGeometryMultiPolygon;
		public final DustEntity tagGeometryCollection;
		

		public DslGeojson() {
			ArkDockDslBuilder meta = ArkDock.getDslBuilder("Geojson");
			
			unit = meta.getUnit();

			typObject = meta.getType("Object");
			memObBbox = meta.defineMember(typObject, "Bbox", DustValType.RAW, DustCollType.ONE);
			
			typGeometry = meta.getType("Geometry");
			memGeometryData = meta.defineMember(typGeometry, "Data", DustValType.RAW, DustCollType.ONE);
			
			tagGeometry = meta.defineTag("Geometry", null);
			tagGeometryPoint = meta.defineTag("Point", tagGeometry);
			tagGeometryMultiPoint = meta.defineTag("MultiPoint", tagGeometry);
			tagGeometryLineString = meta.defineTag("LineString", tagGeometry);
			tagGeometryMultiLineString = meta.defineTag("MultiLineString", tagGeometry);
			tagGeometryPolygon = meta.defineTag("Polygon", tagGeometry);
			tagGeometryMultiPolygon = meta.defineTag("MultiPolygon", tagGeometry);
			tagGeometryCollection = meta.defineTag("Collection", tagGeometry);
		}		
	}	
}
