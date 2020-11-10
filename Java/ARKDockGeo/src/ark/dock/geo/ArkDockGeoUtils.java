package ark.dock.geo;

import java.awt.geom.Rectangle2D;

import ark.dock.ArkDock;
import ark.dock.ArkDockConsts;
import ark.dock.ArkDockDslTools.DslGeometry;
import ark.dock.ArkDockMind;
import ark.dock.ArkDockUnit;

public class ArkDockGeoUtils implements ArkDockConsts {
	
	DslGeometry dslGeo;
	ArkDockUnit uMain;
	
	public ArkDockGeoUtils() {
		ArkDockMind mind = ArkDock.getMind();
		uMain = mind.getMainUnit();
		dslGeo = mind.getDsl(DslGeometry.class);
	}
	
	public Rectangle2D extendBBox(DustEntity e, Rectangle2D bb) {
		Rectangle2D bbox = uMain.getMember(e, dslGeo.memGeomBBox, null, null);

		if ( null == bbox ) {
			bbox = new Rectangle2D.Double();
			bbox.setRect(bb);
			uMain.setMember(e, dslGeo.memGeomBBox, bbox, null);
		} else {
			bbox.add(bb);
		}

		return bbox;
	}

}
