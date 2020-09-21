package ark.dock;

import dust.gen.DustGenConsts;

public interface ArkDockConsts extends DustGenConsts {
    public static final String TOKEN_SEP = "_";
    
    interface MetaMemberInfo {
    	DustEntity getType();
    	DustEntity getMember();
    	
    	DustCollType getCollType();
    	DustValType getValType();
    }
            
    interface MetaProvider {
        DustEntity getUnit(String unitId);
        DustEntity getType(DustEntity unit, String typeId);
        DustEntity getMember(DustEntity type, String itemId);
        
        MetaMemberInfo getMemberInfo(DustEntity member, Object value, Object hint);
    }

}