package ark.dock;

import dust.gen.DustGenConsts;

public interface ArkDockConsts extends DustGenConsts {
    public static final String TOKEN_SEP = "_";
    
    interface MetaProvider {
        DustEntity getUnit(String unitId);
        DustEntity getType(DustEntity unit, String typeId);
        DustEntity getMember(DustEntity type, String itemId);
        
        DustMemberDef getMemberDef(DustEntity member, Object value, Object hint);
    }

    public interface ArkAgent<ActionCtxType> extends DustAgent {
    	void setActionCtx(ActionCtxType ctx);
    	ActionCtxType getActionCtx();
    }

}