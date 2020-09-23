package ark.dock.geo.json;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Writer;
import java.util.BitSet;
import java.util.EnumSet;

import org.json.simple.JSONValue;

public interface ArkDockGeojson2D extends ArkDockGeojsonConsts {
	enum FormatParam {
		FakeZCoord
	}
		
	abstract class Formatter implements JsonFormatter {
		BitSet params = new BitSet(FormatParam.values().length);
		
		public Formatter(EnumSet<FormatParam> params) {
			for ( FormatParam p : FormatParam.values() ) {
				this.params.set(p.ordinal(), params.contains(p));
			}
		}
		
		void pointToJson(double x, double y, Writer target) throws Exception  {
			target.write("[ ");
			target.write(JSONValue.toJSONString(x));
			target.write(", ");
			target.write(JSONValue.toJSONString(y));
			
			if ( params.get(FormatParam.FakeZCoord.ordinal())) {
				target.write(", 0.0");
			}
			
			target.write(" ]");
		}
		
		void pathToJson(Path2D.Double path, Writer target) throws Exception  {
			double d[] = new double[6];
			boolean hasContent = false;
			
			for ( PathIterator pi = path.getPathIterator(null); !pi.isDone(); pi.next() ) {
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
		public Class<?> getDataClass() {
			return Path2D.Double.class;
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
		
		
	}

    public class GeojsonBuilder2DDouble extends GeojsonBuilder {
        // static Map<Class, GeojsonType> classToType = new HashMap<>();
        int coordIdx;

        @Override
        public Object newGeojsonObj(GeojsonType gjt) {
            switch (gjt) {
            case Point:
                coordIdx = 0;
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
        public GeojsonType getObjType(Object geoObj) {
            if (geoObj instanceof Point2D.Double) {
                return GeojsonType.Point;
            } else if (geoObj instanceof Path2D.Double) {
                return GeojsonType.LineString;
            }

            return super.getObjType(geoObj);
        }

        @Override
        public boolean addChild(Object data) {
            Point2D.Double pt;
            if (currObj instanceof Point2D.Double) {
                pt = (Point2D.Double) currObj;
                switch (coordIdx++) {
                case 0:
                    pt.x = (double) data;
                    break;
                case 1:
                    pt.y = (double) data;
                    break;
                default:
                    if ( 0.0 != (double) data ) {
                        DustException.throwException(null, "2DPoint losing coordinate", coordIdx, data);
                    }
                }
            } else if (currObj instanceof Path2D.Double) {
                Path2D.Double path = (Path2D.Double) currObj;
                pt = (Point2D.Double) data;
                if (null == path.getCurrentPoint()) {
                    path.moveTo(pt.x, pt.y);
                } else {
                    path.lineTo(pt.x, pt.y);
                }
            } else {
                return super.addChild(data);
            }

            return true;
        }
    }
}
