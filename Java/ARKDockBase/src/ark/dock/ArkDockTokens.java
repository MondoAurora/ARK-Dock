package ark.dock;

public interface ArkDockTokens extends ArkDockConsts {
	
    String UNIT_ARK = "Ark";
    
    String TYPE_UNIT = "Unit";
    String TYPE_TYPE = "Type";
    String TYPE_MEMBER = "Member";
    
    String TYPE_GEOM = "Geom";

    String MEMBER_GEOM_POINT = "Point";
    String MEMBER_GEOM_POLYGONS = "Polygons";
    String MEMBER_GEOM_BBOX = "BBox";
    String MEMBER_GEOM_BBOXMEMBERS = "BBoxMembers";

    
    public class Meta implements ArkDockConsts {

    	public final DustEntity eUnitArk;

    	public final DustEntity eTypeUnit;
    	public final DustEntity eTypeType;
    	public final DustEntity eTypeMember;

        public Meta(ArkDockModelMeta meta) {
    		eUnitArk = meta.getEntity(ArkDockUtils.buildGlobalId(UNIT_ARK, TYPE_UNIT, UNIT_ARK), true);
    		eTypeUnit = meta.getEntity(ArkDockUtils.buildGlobalId(UNIT_ARK, TYPE_TYPE, TYPE_UNIT), true);
    		eTypeType = meta.getEntity(ArkDockUtils.buildGlobalId(UNIT_ARK, TYPE_TYPE, TYPE_TYPE), true);
    		eTypeMember = meta.getEntity(ArkDockUtils.buildGlobalId(UNIT_ARK, TYPE_TYPE, TYPE_MEMBER), true);
        }
    }
    
    public class Geometry implements ArkDockConsts {
        public final DustEntity eTypeGeom;
        
        public final DustEntity eGeomPoint;
        public final DustEntity eGeomPolygons;
        public final DustEntity eGeomBBox;
        public final DustEntity eGeomBBoxMembers;

        public Geometry(ArkDockModelMeta meta) {
            eTypeGeom = meta.getType(meta.mt.eUnitArk, TYPE_GEOM);

            eGeomPoint = meta.getMember(eTypeGeom, MEMBER_GEOM_POINT);
            eGeomPolygons = meta.getMember(eTypeGeom, MEMBER_GEOM_POLYGONS);
            eGeomBBox = meta.getMember(eTypeGeom, MEMBER_GEOM_BBOX);
            eGeomBBoxMembers = meta.getMember(eTypeGeom, MEMBER_GEOM_BBOXMEMBERS);
        }
    }

}
