package ark.dock.geo.geojson;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import dust.gen.DustGenException;

public interface ArkDockGeojsonGeometry extends ArkDockGeojsonConsts {
	public GeojsonGeometryType getType();
	
	static ArkDockGeojsonGeometry forName(String name) {
		return forType(GeojsonGeometryType.valueOf(name));
	}
	
	static ArkDockGeojsonGeometry forType(GeojsonGeometryType type) {
		switch ( type ) {
		case Point:
			return new Point();
		case MultiPoint:
			return new MultiPoint();
		case LineString:
			return new LineString();
		case MultiLineString:
			return new MultiLineString();
		case Polygon:
			return new Polygon();		
		case MultiPolygon:
			return new MultiPolygon();
		case GeometryCollection:
			return new GeometryCollection();
		}
		
		return DustGenException.throwException(null, "Should never get here");
	}
	
	public static class Point extends Point2D.Double implements ArkDockGeojsonGeometry {
		private static final long serialVersionUID = 1L;
		double z;
		
		@Override
		public GeojsonGeometryType getType() {
			return GeojsonGeometryType.Point;
		}
		
		public double getZ() {
			return z;
		}
		
		public void setZ(double z) {
			this.z = z;
		}
	}
	
	public static class MultiPoint extends ArrayList<ArkDockGeojsonGeometry.Point> implements ArkDockGeojsonGeometry {
		private static final long serialVersionUID = 1L;
		
		@Override
		public GeojsonGeometryType getType() {
			return GeojsonGeometryType.MultiPoint;
		}
	}
	
	public static class LineString extends Path2D.Double implements ArkDockGeojsonGeometry {
		private static final long serialVersionUID = 1L;
		
		@Override
		public GeojsonGeometryType getType() {
			return GeojsonGeometryType.LineString;
		}
	}
	
	public static class MultiLineString extends ArrayList<ArkDockGeojsonGeometry.LineString> implements ArkDockGeojsonGeometry {
		private static final long serialVersionUID = 1L;
		
		@Override
		public GeojsonGeometryType getType() {
			return GeojsonGeometryType.MultiLineString;
		}
	}
	
	public static class Polygon extends LineString implements ArkDockGeojsonGeometry {
		private static final long serialVersionUID = 1L;
		
		ArrayList<LineString> exclusions;
		
		@Override
		public GeojsonGeometryType getType() {
			return GeojsonGeometryType.Polygon;
		}
		
		public void addExclusion(LineString path) {
			if ( null == exclusions ) {
				exclusions = new ArrayList<>();
			}
			exclusions.add(path);
		}
		
		public Iterable<LineString> getExclusions() {
			return exclusions;
		}
	}
	
	public static class MultiPolygon extends ArrayList<ArkDockGeojsonGeometry.Polygon> implements ArkDockGeojsonGeometry {
		private static final long serialVersionUID = 1L;
		
		@Override
		public GeojsonGeometryType getType() {
			return GeojsonGeometryType.MultiPolygon;
		}
	}
	
	public static class GeometryCollection extends ArrayList<ArkDockGeojsonGeometry> implements ArkDockGeojsonGeometry {
		private static final long serialVersionUID = 1L;
		
		@Override
		public GeojsonGeometryType getType() {
			return GeojsonGeometryType.GeometryCollection;
		}
	}
}