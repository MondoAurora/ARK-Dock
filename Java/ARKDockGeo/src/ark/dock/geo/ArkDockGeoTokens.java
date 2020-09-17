package ark.dock.geo;

import ark.dock.ArkDockConsts;
import ark.dock.ArkDockModelMeta;

public class ArkDockGeoTokens implements ArkDockConsts {

    public static final String MEMBER_GEOM_POINT = "Point";
    public static final String MEMBER_GEOM_POLYGONS = "Polygons";
    public static final String MEMBER_GEOM_BBOX = "BBox";
    public static final String MEMBER_GEOM_BBOXMEMBERS = "BBoxMembers";

    public final DustEntity eTypeGeom;
    
    public final DustEntity eGeomPoint;
    public final DustEntity eGeomPolygons;
    public final DustEntity eGeomBBox;
    public final DustEntity eGeomBBoxMembers;

    public ArkDockGeoTokens(ArkDockModelMeta meta) {
        eTypeGeom = meta.getType(meta.eUnitArk, TYPE_GEOM);

        eGeomPoint = meta.getMember(eTypeGeom, MEMBER_GEOM_POINT);
        eGeomPolygons = meta.getMember(eTypeGeom, MEMBER_GEOM_POLYGONS);
        eGeomBBox = meta.getMember(eTypeGeom, MEMBER_GEOM_BBOX);
        eGeomBBoxMembers = meta.getMember(eTypeGeom, MEMBER_GEOM_BBOXMEMBERS);
    }
}
