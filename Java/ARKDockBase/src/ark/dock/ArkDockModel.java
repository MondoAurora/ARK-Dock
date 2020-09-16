package ark.dock;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import dust.gen.DustGenUtils;

public class ArkDockModel implements ArkDockConsts {
    public static final String TOKEN_SEP = "_";
    
    public static final String UNIT_ARK = "Ark";
    
    public static final String TYPE_UNIT = "Unit";
    public static final String TYPE_TYPE = "Type";
    public static final String TYPE_MEMBER = "Member";
    
    public static final String TYPE_GEOM = "Geom";
    
    public static final String MEMBER_GEOM_POINT = "Point";
    public static final String MEMBER_GEOM_POLYGONS = "Polygons";
    
    class ModelEntity extends DustEntity {
        final String id;
        final String globalId;
        Map<String, Object> data = new TreeMap<>();
        
        public ModelEntity(String globalId) {
            this.globalId = globalId;
            this.id = globalId.substring(globalId.lastIndexOf(TOKEN_SEP) + 1);
        }
        
        @Override
        public String getGlobalId() {
            return globalId;
        }
    }
    
    public final DustEntity eUnitArk;
    public final DustEntity eTypeUnit;
    public final DustEntity eTypeType;
    public final DustEntity eTypeMember;
    
    public final DustEntity eTypeGeom;
    public final DustEntity eMemberGeomPoint;
    public final DustEntity eMemberGeomPolygons;
    
    Map<String, ModelEntity> entities = new HashMap<>();
    
    public static String buildGlobalId(String unitId, String typeId, String id) {
        return DustGenUtils.sbAppend(null, TOKEN_SEP, true, unitId, typeId, id).toString();
    }
   
    public ArkDockModel() {
        eUnitArk = getEntity(buildGlobalId(UNIT_ARK, TYPE_UNIT, UNIT_ARK), true);
        eTypeUnit = getEntity(buildGlobalId(UNIT_ARK, TYPE_TYPE, TYPE_UNIT), true);
        eTypeType = getEntity(buildGlobalId(UNIT_ARK, TYPE_TYPE, TYPE_TYPE), true);
        eTypeMember = getEntity(buildGlobalId(UNIT_ARK, TYPE_TYPE, TYPE_MEMBER), true);
        
        eTypeGeom = getEntity(buildGlobalId(UNIT_ARK, TYPE_TYPE, TYPE_GEOM), true);
        eMemberGeomPoint = getMember(eTypeGeom, MEMBER_GEOM_POINT);
        eMemberGeomPolygons = getMember(eTypeGeom, MEMBER_GEOM_POLYGONS);
    }

    public DustEntity getMember(DustEntity type, String itemId) {
        String globalId = DustGenUtils.sbAppend(null, TOKEN_SEP, true, ((ModelEntity)type).globalId.replace(TYPE_TYPE, TYPE_MEMBER), itemId).toString();
        return getEntity(globalId, true);
    }

    public DustEntity getEntity(DustEntity unit, DustEntity type, String itemId, boolean createIfMissing) {
        String globalId = buildGlobalId(((ModelEntity)unit).id, ((ModelEntity)type).id, itemId);
        return getEntity(globalId, createIfMissing);
    }

    public DustEntity getEntity(String globalId, boolean createIfMissing) {
        ModelEntity e = entities.get(globalId);
        
        if ( createIfMissing && (null == e) ) {
            e = new ModelEntity(globalId);
            entities.put(globalId, e);
        }
        
        return e;
    }
    
    public void setMember(DustEntity e, DustEntity member, Object value) {
        ((ModelEntity)e).data.put(((ModelEntity)member).globalId, value);
    }

    @SuppressWarnings("unchecked")
    public <RetType> RetType getMember(DustEntity e, DustEntity member, RetType defValue) {
        RetType ret = defValue;
        
        if ( null != e ) {
            Object v = ((ModelEntity)e).data.get(((ModelEntity)member).globalId);
            if ( null != v ) {
                ret = (RetType) v;
            }
        }
        
        return ret;
    }
    
}
