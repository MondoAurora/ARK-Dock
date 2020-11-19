package ark.dock.geo;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import dust.gen.DustGenConsts.DustEntity;

public interface ArkDockGeoConsts {
	class GeoBBoxCollector {
		final Map<DustEntity, Rectangle2D> areaBBoxes = new HashMap<>();

		public void reset() {
			areaBBoxes.clear();
		}

		public void set(DustEntity e, Rectangle2D rect) {
			if ( null != e ) {
				if ( null == rect ) {
					areaBBoxes.remove(e);
				} else {
					Rectangle2D val = new Rectangle2D.Double();
					val.setRect(rect);
					areaBBoxes.put(e, val);
				}
			}
		}

		public void add(DustEntity e, Rectangle2D rect) {
			if ( null != e ) {
				if ( null == rect ) {
					areaBBoxes.remove(e);
				} else {
					Rectangle2D orig = areaBBoxes.get(e);
					if ( null == orig ) {
						orig = new Rectangle2D.Double();
						orig.setRect(rect);
						areaBBoxes.put(e, orig);
					} else {
						orig.add(rect);
					}
				}
			}
		}

		public Rectangle2D get(DustEntity e) {
			return areaBBoxes.get(e);
		}

		public Iterable<Map.Entry<DustEntity, Rectangle2D>> entrySet() {
			return areaBBoxes.entrySet();
		}
	}
}
