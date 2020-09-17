package ark.dock;

import dust.gen.DustGenConsts;

public interface ArkDockConsts extends DustGenConsts {
    public static final String TOKEN_SEP = "_";
    
    public static final String UNIT_ARK = "Ark";
    
    public static final String TYPE_UNIT = "Unit";
    public static final String TYPE_TYPE = "Type";
    public static final String TYPE_MEMBER = "Member";
    
    public static final String TYPE_GEOM = "Geom";
        
    interface MetaProvider {
        DustEntity getUnit(String unitId);
        DustEntity getType(DustEntity unit, String typeId);
        DustEntity getMember(DustEntity type, String itemId);
    }

}