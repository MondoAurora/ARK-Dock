package ark.dock;

import dust.gen.DustGenConsts;
import dust.gen.DustGenException;
import dust.gen.DustGenLog;

public interface ArkDockConsts extends DustGenConsts {

  String TOKEN_SEP = "_";
//  String TOKEN_SEP = ":";
    
    enum TokenSegment {
    	UNIT, TYPE, ID;
    }
    
    interface DslBuilder {
        DustEntity getUnit();
        DustEntity getType(String typeId);
        DustEntity getMember(DustEntity type, String itemId);
        
        DustMemberDef getMemberDef(DustEntity member, Object value, Object hint);
    }

    public interface ArkDockContextAware<ActionCtxType> {
    	void setActionCtx(ActionCtxType ctx);
    	ActionCtxType getActionCtx();
    }

    public interface ArkDockAgent<ActionCtxType> extends DustAgent, ArkDockContextAware<ActionCtxType> {
    }

    public abstract class ArkDockAgentDefault<ActionCtxType> implements ArkDockAgent<ActionCtxType> {
    	protected ActionCtxType ctx;
		
    	public void setActionCtx(ActionCtxType ctx) {
    		this.ctx = ctx;
    	}
    	public ActionCtxType getActionCtx() {
    		return ctx;
    	}
    }

    public abstract class ArkDockAgentBase extends ArkDockAgentDefault<DustEntityContext> {
		private DustEntity eDef;
		
		public DustEntity getDef() {
			return eDef;
		}

		@Override
		public DustResultType agentAction(DustAgentAction action) throws Exception {
			switch ( action ) {
			case INIT:
				eDef = getActionCtx().entity;
				return DustResultType.ACCEPT;
			default:
				break;
			}
			return DustResultType.ACCEPT_PASS;
		}

    }

    public abstract class ArkDockAgentWrapper<BinOjbType> extends ArkDockAgentBase {
    	protected BinOjbType obj;
    	
    	protected abstract BinOjbType createBinObj() throws Exception;
    	
    	public final BinOjbType getBinObj() {
    		if ( null == obj ) {
    			try {
					obj = createBinObj();
	    			// TODO register to entity
				} catch (Throwable e) {
					DustGenException.throwException(e);
				}
    		}
    		
    		return obj;
    	}
    }
    
	class DumpAgent<ActionCtxType> extends ArkDockAgentDefault<ActionCtxType> {
		@Override
		public DustResultType agentAction(DustAgentAction action) throws Exception {
			DustGenLog.log(action, getActionCtx());
			return DustResultType.ACCEPT_READ;
		}
	}

}