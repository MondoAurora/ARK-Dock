package ark.dock;

import dust.gen.DustGenConsts;

public interface ArkDockConsts extends DustGenConsts {
    public static final String TOKEN_SEP = "_";
    
    enum TokenSegment {
    	UNIT, TYPE, ID;
    }
    
    interface MetaProvider {
        DustEntity getUnit(String unitId);
        DustEntity getType(DustEntity unit, String typeId);
        DustEntity getMember(DustEntity type, String itemId);
        
        DustMemberDef getMemberDef(DustEntity member, Object value, Object hint);
    }

    public interface ArkDockAgent<ActionCtxType> extends DustAgent {
    	void setActionCtx(ActionCtxType ctx);
    	ActionCtxType getActionCtx();
    }

    public abstract class ArkDockAgentBase<ActionCtxType> implements ArkDockAgent<ActionCtxType> {
    	protected ActionCtxType ctx;
    	
    	public void setActionCtx(ActionCtxType ctx) {
    		this.ctx = ctx;
    	}
    	public ActionCtxType getActionCtx() {
    		return ctx;
    	}
    }

    public abstract class ArkDockAgentWrapper<ActionCtxType, BinOjbType> implements ArkDockAgent<ActionCtxType> {
    	protected BinOjbType obj;
    	
    	protected abstract BinOjbType createBinObj();
    	
    	public final BinOjbType getBinObj() {
    		if ( null == obj ) {
    			obj = createBinObj();
    			// TODO register to entity
    		}
    		
    		return obj;
    	}
    }

    public interface HintProvider {
    	Object getHint(DustEntity target, DustEntity token, Object val);
    }

}