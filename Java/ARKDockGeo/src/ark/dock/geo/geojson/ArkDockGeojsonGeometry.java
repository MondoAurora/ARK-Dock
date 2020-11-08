package ark.dock.geo.geojson;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import dust.gen.DustGenException;

public interface ArkDockGeojsonGeometry<DataType> extends ArkDockGeojsonConsts {
	public GeojsonGeometryType getType();

	public void addInfo(DataType o);

	static <DataType> ArkDockGeojsonGeometry<DataType> forName(String name) {
		return forType(GeojsonGeometryType.valueOf(name));
	}

	@SuppressWarnings("unchecked")
	static <DataType> ArkDockGeojsonGeometry<DataType> forType(GeojsonGeometryType type) {
		switch ( type ) {
		case Point:
			return (ArkDockGeojsonGeometry<DataType>) new Point();
		case MultiPoint:
			return (ArkDockGeojsonGeometry<DataType>) new MultiPoint();
		case LineString:
			return (ArkDockGeojsonGeometry<DataType>) new LineString();
		case MultiLineString:
			return (ArkDockGeojsonGeometry<DataType>) new MultiLineString();
		case Polygon:
			return (ArkDockGeojsonGeometry<DataType>) new Polygon();
		case MultiPolygon:
			return (ArkDockGeojsonGeometry<DataType>) new MultiPolygon();
		case GeometryCollection:
			return (ArkDockGeojsonGeometry<DataType>) new GeometryCollection();
		}

		return DustGenException.throwException(null, "Should never get here");
	}

	public static class Point extends Point2D.Double implements ArkDockGeojsonGeometry<Double> {
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
		
		@Override
		public void addInfo(java.lang.Double o) {
			DustGenException.throwException(null, "addInfo not supported in Point");
		}

		public void addCoord(int idx, java.lang.Double o) {
			switch ( idx ) {
			case 0:
				x = o;
				break;
			case 1:
				y = o;
				break;
			case 2:
				z = o;
				break;
			default:
				DustGenException.throwException(null, "Invalid info index in GeojsonPoint", idx);
			}
		}
	}

	public static class MultiPoint extends ArrayList<ArkDockGeojsonGeometry.Point>
			implements ArkDockGeojsonGeometry<ArkDockGeojsonGeometry.Point> {
		private static final long serialVersionUID = 1L;

		@Override
		public GeojsonGeometryType getType() {
			return GeojsonGeometryType.MultiPoint;
		}

		@Override
		public void addInfo(Point o) {
			add(o);
		}
	}

	public static class LineString extends Path2D.Double
			implements ArkDockGeojsonGeometry<ArkDockGeojsonGeometry.Point> {
		private static final long serialVersionUID = 1L;
		ArrayList<java.lang.Double> z;

		@Override
		public GeojsonGeometryType getType() {
			return GeojsonGeometryType.LineString;
		}

		@Override
		public void addInfo(Point o) {
			if ( null == getCurrentPoint() ) {
				moveTo(o.x, o.y);
				z = new ArrayList<>();
			} else {
				lineTo(o.x, o.y);
			}
			z.add(o.z);
		}
	}

	public static class MultiLineString extends ArrayList<ArkDockGeojsonGeometry.LineString>
			implements ArkDockGeojsonGeometry<ArkDockGeojsonGeometry.LineString> {
		private static final long serialVersionUID = 1L;

		@Override
		public GeojsonGeometryType getType() {
			return GeojsonGeometryType.MultiLineString;
		}

		@Override
		public void addInfo(LineString o) {
			add(o);
		}
	}

	public static class Polygon implements ArkDockGeojsonGeometry<ArkDockGeojsonGeometry.LineString> {
		LineString main;
		ArrayList<LineString> exclusions;

		@Override
		public GeojsonGeometryType getType() {
			return GeojsonGeometryType.Polygon;
		}

		public LineString getMain() {
			return main;
		}

		@Override
		public void addInfo(LineString o) {
			if ( null == main ) {
				main = o;
			} else {
				if ( null == exclusions ) {
					exclusions = new ArrayList<>();
				}
				exclusions.add(o);
			}
		}

		public Iterable<LineString> getExclusions() {
			return exclusions;
		}
	}

	public static class MultiPolygon extends ArrayList<ArkDockGeojsonGeometry.Polygon>
			implements ArkDockGeojsonGeometry<ArkDockGeojsonGeometry.Polygon> {
		private static final long serialVersionUID = 1L;

		@Override
		public GeojsonGeometryType getType() {
			return GeojsonGeometryType.MultiPolygon;
		}

		@Override
		public void addInfo(Polygon o) {
			add(o);
		}
	}

	public static class GeometryCollection extends ArrayList<ArkDockGeojsonGeometry<?>>
			implements ArkDockGeojsonGeometry<ArkDockGeojsonGeometry<?>> {
		private static final long serialVersionUID = 1L;

		@Override
		public GeojsonGeometryType getType() {
			return GeojsonGeometryType.GeometryCollection;
		}

		@Override
		public void addInfo(ArkDockGeojsonGeometry<?> o) {
			add(o);
		}
	}
}