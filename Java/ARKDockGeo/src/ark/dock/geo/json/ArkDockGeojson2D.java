package ark.dock.geo.json;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Writer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Map;

import org.json.simple.JSONValue;

import ark.dock.io.json.ArkDockJsonConsts;
import dust.gen.DustGenException;

public interface ArkDockGeojson2D extends ArkDockGeojsonConsts, ArkDockJsonConsts {
	enum FormatParam {
		FakeZCoord
	}

	@SuppressWarnings("rawtypes")
	abstract class Formatter implements JsonFormatter {
		BitSet params = new BitSet(FormatParam.values().length);

		public Formatter(EnumSet<FormatParam> params) {
			for (FormatParam p : FormatParam.values()) {
				this.params.set(p.ordinal(), params.contains(p));
			}
		}

		void pointToJson(double x, double y, Writer target) throws Exception {
			target.write("[ ");
			target.write(JSONValue.toJSONString(x));
			target.write(", ");
			target.write(JSONValue.toJSONString(y));

			if ( params.get(FormatParam.FakeZCoord.ordinal()) ) {
				target.write(", 0.0");
			}

			target.write(" ]");
		}
		
		void pathToJson(Path2D.Double path, Writer target) throws Exception {
			double d[] = new double[6];
			boolean hasContent = false;

			for (PathIterator pi = path.getPathIterator(null); !pi.isDone(); pi.next()) {
				if ( hasContent ) {
					target.write(", ");
				} else {
					target.write("[ ");
					hasContent = true;
				}
				pi.currentSegment(d);
				pointToJson(d[0], d[1], target);
			}

			if ( hasContent ) {
				target.write(" ]");
			}
		}
		
		void loadPoint(Object data, Point2D.Double pt) {
			ArrayList al = (ArrayList) data;
			pt.x = (Double) al.get(0);
			pt.y = (Double) al.get(1);
		}
		
		protected Object jsonToPath(Object data) {
			Path2D.Double path = null;
			for ( Object pd : (ArrayList) data ) {
				Point2D.Double pt = new Point2D.Double();
				loadPoint(pd, pt);
				if ( null == path ) {
					path = new Path2D.Double();
					path.moveTo(pt.x, pt.y);
				} else {
					path.lineTo(pt.x, pt.y);
				}
			}
			return path;
		}
	}

	class PointFormatter extends Formatter {
		public PointFormatter(EnumSet<FormatParam> params) {
			super(params);
		}

		@Override
		public void toJson(Object data, Writer target) throws Exception {
			Point2D.Double pt = (Point2D.Double) data;
			pointToJson(pt.x, pt.y, target);
		}
		
		@Override
		public Object fromParsedData(Object data) {
			Point2D.Double pt = new Point2D.Double();
			loadPoint(data, pt);
			return pt;
		}

		@Override
		public Class<?> getDataClass() {
			return Point2D.Double.class;
		}
	}

	class PathFormatter extends Formatter {
		public PathFormatter(EnumSet<FormatParam> params) {
			super(params);
		}

		@Override
		public void toJson(Object data, Writer target) throws Exception {
			pathToJson((Path2D.Double) data, target);
		}
		
		@Override
		public Object fromParsedData(Object data) {
			return jsonToPath(data);
		}

		@Override
		public Class<?> getDataClass() {
			return Path2D.Double.class;
		}
	}

	@SuppressWarnings("rawtypes")
	class PolygonFormatter extends Formatter {
		public PolygonFormatter(EnumSet<FormatParam> params) {
			super(params);
		}

		@Override
		public void toJson(Object data, Writer target) throws Exception {
			GeojsonPolygon poly = (GeojsonPolygon) data;

			target.write("[ ");
			pathToJson((Path2D.Double) poly.getExterior(), target);
			
			ArrayList al = poly.getHoles();

			if ( null != al ) {
				for (Object h : al) {
					target.write(", ");
					pathToJson((Path2D.Double) h, target);
				}
			}

			target.write(" ]");
		}
		
		@Override
		public Object fromParsedData(Object data) {
			GeojsonPolygon poly = new GeojsonPolygon();
			
			for ( Object pd : (ArrayList) data ) {
				poly.addChild(jsonToPath(pd));
			}
			return poly;
		}

		@Override
		public Class<?> getDataClass() {
			return GeojsonPolygon.class;
		}
	}

	class BBoxFormatter implements JsonFormatter {
		@Override
		public Class<?> getDataClass() {
			return Rectangle2D.Double.class;
		}

		@Override
		public void toJson(Object data, Writer target) throws Exception {
			Rectangle2D.Double rect = (Rectangle2D.Double) data;
			target.append("{ \"x\": ").append(JSONValue.toJSONString(rect.x));
			target.append(", \"y\": ").append(JSONValue.toJSONString(rect.y));
			target.append(", \"h\": ").append(JSONValue.toJSONString(rect.height));
			target.append(", \"w\": ").append(JSONValue.toJSONString(rect.width)).append(" }");
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object fromParsedData(Object data) {
			Map<String, Object> src = (Map<String, Object>) data;
			Rectangle2D.Double rect = new Rectangle2D.Double((double) src.get("x"), (double) src.get("y"),
					(double) src.get("w"), (double) src.get("h"));
			return rect;
		}
	}

	public class GeojsonBuilder2DDouble extends GeojsonBuilder {
		boolean useArea;
		// static Map<Class, GeojsonType> classToType = new HashMap<>();
		int coordIdx;
		double[] temp = new double[6];

		public GeojsonBuilder2DDouble() {
			this(false);
		}
		
		public GeojsonBuilder2DDouble(boolean useArea) {
			this.useArea = useArea;
		}

		@Override
		public Object newGeojsonObj(GeojsonType gjt) {
			switch ( gjt ) {
			case Point:
				coordIdx = 0;
				return new Point2D.Double();
			case LineString:
				return new Path2D.Double();
			case Polygon:
				return useArea ? new Area() : new GeojsonPolygon<Path2D.Double>();
			default:
				return super.newGeojsonObj(gjt);
			}
		}

		@Override
		public GeojsonType getObjType(Object geoObj) {
			if ( geoObj instanceof Point2D.Double ) {
				return GeojsonType.Point;
			} else if ( geoObj instanceof Path2D.Double ) {
				return GeojsonType.LineString;
			} else if ( geoObj instanceof Area ) {
				return GeojsonType.Polygon;
			}

			return super.getObjType(geoObj);
		}

		@Override
		public boolean addChild(Object data) {
			Point2D.Double pt;
			if ( currObj instanceof Point2D.Double ) {
				pt = (Point2D.Double) currObj;
				switch ( coordIdx++ ) {
				case 0:
					pt.x = (double) data;
					break;
				case 1:
					pt.y = (double) data;
					break;
				default:
					if ( 0.0 != (double) data ) {
						DustGenException.throwException(null, "2DPoint losing coordinate", coordIdx, data);
					}
				}
			} else if ( currObj instanceof Path2D.Double ) {
				Path2D.Double path = (Path2D.Double) currObj;
				pt = (Point2D.Double) data;
				if ( null == path.getCurrentPoint() ) {
					path.moveTo(pt.x, pt.y);
				} else {
					path.lineTo(pt.x, pt.y);
				}
			} else if ( currObj instanceof Area ) {
				Area area = (Area) currObj;
				Shape shp = (Shape) data;
				if ( area.isEmpty() ) {
					area.add(new Area(shp));
				} else {
					area.subtract(new Area(shp));
				}
			} else {
				return super.addChild(data);
			}

			return true;
		}
	}
}
