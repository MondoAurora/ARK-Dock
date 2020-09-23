package ark.dock;

public interface ArkDockTokens extends ArkDockConsts {
	
    String UNIT_ARK = "Ark";
    
    String TYPE_UNIT = "Unit";
    String TYPE_TYPE = "Type";
    String TYPE_MEMBER = "Member";
    String TYPE_ENTITY = "Entity";

    String MEMBER_ENTITY_ID = "Id";
    String MEMBER_ENTITY_GLOBALID = "GlobalId";
    String MEMBER_ENTITY_PRIMARYTYPE = "PrimaryType";
    
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

    	public final DustEntity eTypeEntity;
    	public final DustEntity eEntityId;
    	public final DustEntity eEntityGlobalId;
    	public final DustEntity eEntityPrimType;
    	
    	
        public Meta(ArkDockModelMeta meta) {
    		eUnitArk = meta.getBootEntity(ArkDockUtils.buildGlobalId(UNIT_ARK, TYPE_UNIT, UNIT_ARK));
    		eTypeUnit = meta.getBootEntity(ArkDockUtils.buildGlobalId(UNIT_ARK, TYPE_TYPE, TYPE_UNIT));
    		eTypeType = meta.getBootEntity(ArkDockUtils.buildGlobalId(UNIT_ARK, TYPE_TYPE, TYPE_TYPE));
    		eTypeMember = meta.getBootEntity(ArkDockUtils.buildGlobalId(UNIT_ARK, TYPE_TYPE, TYPE_MEMBER));
    		
    		eTypeEntity = meta.getBootEntity(ArkDockUtils.buildGlobalId(UNIT_ARK, TYPE_TYPE, TYPE_ENTITY));
    		
    		eEntityId = meta.getMember(eTypeEntity, MEMBER_ENTITY_ID);
    		eEntityGlobalId = meta.getMember(eTypeEntity, MEMBER_ENTITY_GLOBALID);
    		eEntityPrimType = meta.getMember(eTypeEntity, MEMBER_ENTITY_PRIMARYTYPE);
    		
    		
    		meta.initBootEntity(eUnitArk, eTypeUnit, this);
    		
    		meta.initBootEntity(eTypeType, eTypeType, this);
    		meta.initBootEntity(eTypeUnit, eTypeType, this);
    		meta.initBootEntity(eTypeMember, eTypeType, this);
    		meta.initBootEntity(eTypeEntity, eTypeType, this);
    		
    		meta.initBootEntity(eEntityId, eTypeMember, this);
    		meta.initBootEntity(eEntityGlobalId, eTypeMember, this);
    		meta.initBootEntity(eEntityPrimType, eTypeMember, this);
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
