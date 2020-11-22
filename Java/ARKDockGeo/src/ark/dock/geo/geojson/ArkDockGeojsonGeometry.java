package ark.dock.geo.geojson;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import ark.dock.ArkDockUnit;
import ark.dock.io.json.ArkDockJsonConsts;
import dust.gen.DustGenException;

public interface ArkDockGeojsonGeometry extends ArkDockGeojsonConsts, ArkDockJsonConsts.JsonWritable {
	public GeojsonGeometryType getType();

	static ArkDockGeojsonGeometry forName(String name) {
		return forType(GeojsonGeometryType.valueOf(name));
	}

	static Point forCoordArr(ArkDockUnit u, DustEntity e, DustEntity m) {
		ArkDockGeojsonGeometry.Point pt = null;
		
		for (int ci = 0; ci < 3; ++ci) {
			if ( null == pt ) {
				pt = new ArkDockGeojsonGeometry.Point();
			}
			pt.addCoord(ci, u.getMember(e, m, 0.0, ci));
		}
		return pt;
	}

	static Polygon forRect(Rectangle2D rect) {
		LineString ls = new LineString();
		
		double minX = rect.getMinX();
		double minY = rect.getMinY();
		double maxX = rect.getMaxX();
		double maxY = rect.getMaxY();

		ls.moveTo(minX, minY);
		ls.lineTo(maxX, minY);
		ls.lineTo(maxX, maxY);
		ls.lineTo(minX, maxY);
		ls.lineTo(minX, minY);
		
		Polygon ret = new Polygon();
		ret.addInfo(ls);
		
		return ret;
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

		@Override
		public String toString() {
			return "GeojsonPoint [" + x + ", " + y + ", " + z + "]";
		}

		@Override
		public void toJson(ArkDockAgent<JsonContext> target) throws Exception {
			JsonContext ctx = target.getActionCtx();

			ctx.block = JsonBlock.Array;
			target.agentAction(DustAgentAction.BEGIN);

			ctx.param = x;
			target.agentAction(DustAgentAction.PROCESS);
			ctx.param = y;
			target.agentAction(DustAgentAction.PROCESS);
			ctx.param = z;
			target.agentAction(DustAgentAction.PROCESS);

			ctx.block = JsonBlock.Array;
			target.agentAction(DustAgentAction.END);
		}
	}

	interface GeoColl<DataType extends ArkDockGeojsonGeometry> extends ArkDockGeojsonGeometry {
		public void addInfo(DataType o);
	}

	public static abstract class GeoCollArr<DataType extends ArkDockGeojsonGeometry> extends ArrayList<DataType>
			implements GeoColl<DataType> {
		private static final long serialVersionUID = 1L;

		public void addInfo(DataType o) {
			add(o);
		}

		@Override
		public void toJson(ArkDockAgent<JsonContext> target) throws Exception {
			JsonContext ctx = target.getActionCtx();

			ctx.block = JsonBlock.Array;
			target.agentAction(DustAgentAction.BEGIN);

			for (DataType m : this) {
				m.toJson(target);
			}

			ctx.block = JsonBlock.Array;
			target.agentAction(DustAgentAction.END);
		}
	}

	public static class MultiPoint extends GeoCollArr<ArkDockGeojsonGeometry.Point> {
		private static final long serialVersionUID = 1L;

		@Override
		public GeojsonGeometryType getType() {
			return GeojsonGeometryType.MultiPoint;
		}
	}

	public static class LineString extends Path2D.Double implements GeoColl<ArkDockGeojsonGeometry.Point> {
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
		
		@Override
		public void toJson(ArkDockAgent<JsonContext> target) throws Exception {
			JsonContext ctx = target.getActionCtx();

			ctx.block = JsonBlock.Array;
			target.agentAction(DustAgentAction.BEGIN);
			
			int zIdx = 0;			
			double d[] = new double[6];

			for (PathIterator pi = getPathIterator(null); !pi.isDone(); pi.next()) {
				pi.currentSegment(d);
				d[2] = (null == z) ? 0.0 : z.get(zIdx++);
				
				target.agentAction(DustAgentAction.BEGIN);
				for ( int i = 0; i < 3; ++i ) {
					ctx.param = d[i];
					target.agentAction(DustAgentAction.PROCESS);
				}
				target.agentAction(DustAgentAction.END);
			}
			
			target.agentAction(DustAgentAction.END);
		}
	}

	public static class MultiLineString extends GeoCollArr<ArkDockGeojsonGeometry.LineString> {
		private static final long serialVersionUID = 1L;

		@Override
		public GeojsonGeometryType getType() {
			return GeojsonGeometryType.MultiLineString;
		}
	}

	public static class Polygon implements GeoColl<ArkDockGeojsonGeometry.LineString> {
		LineString main;
		ArrayList<LineString> exclusions;

		@Override
		public GeojsonGeometryType getType() {
			return GeojsonGeometryType.Polygon;
		}

		public LineString getMain() {
			return main;
		}

		public Iterable<LineString> getExclusions() {
			return exclusions;
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

		@Override
		public void toJson(ArkDockAgent<JsonContext> target) throws Exception {
			JsonContext ctx = target.getActionCtx();

			ctx.block = JsonBlock.Array;
			target.agentAction(DustAgentAction.BEGIN);

			if ( null != main ) {
				main.toJson(target);
			}

			if ( null != exclusions ) {
				for (LineString m : exclusions) {
					m.toJson(target);
				}
			}
			
			ctx.block = JsonBlock.Array;
			target.agentAction(DustAgentAction.END);
		}
	}

	public static class MultiPolygon extends GeoCollArr<ArkDockGeojsonGeometry.Polygon> {
		private static final long serialVersionUID = 1L;

		@Override
		public GeojsonGeometryType getType() {
			return GeojsonGeometryType.MultiPolygon;
		}
	}

	public static class GeometryCollection extends GeoCollArr<ArkDockGeojsonGeometry> {
		private static final long serialVersionUID = 1L;

		@Override
		public GeojsonGeometryType getType() {
			return GeojsonGeometryType.GeometryCollection;
		}

		@Override
		public void toJson(ArkDockAgent<JsonContext> target) throws Exception {
			// TODO Auto-generated method stub
			super.toJson(target);
		}
	}
}