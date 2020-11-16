package ark.dock;

import java.util.ArrayList;
import java.util.Stack;

import dust.gen.DustGenException;
import dust.gen.DustGenUtils;

public class ArkDockVisitor<EventCtxType> implements ArkDockConsts, ArkDockConsts.ArkDockAgent<EventCtxType> {
	
	public interface VisitorAware<EventCtxType> {
		void setVisitor(ArkDockVisitor<EventCtxType> visitor);
	}
	
    class RelayInfo {
        int start;
        DustAgent parent;
        boolean shareCtx;
        
        RelayInfo(boolean shareCtx) {
            this.shareCtx = shareCtx;
// WARNING: this change breaks ArkDockJsonSerializerReader (going obsolete)
//            this.start = depth;
            this.start = shareCtx ? depth : (depth + 1);
            this.parent = current;
        }
    }
    
    int depth;
    
    DustAgent current;
    ArrayList<Object> procCtx;
    Stack<RelayInfo> relayStack;
    
    EventCtxType eventCtx;
    
    public ArkDockVisitor(EventCtxType eventCtx, DustAgent root) {
        this.current = root;
        this.eventCtx = eventCtx;
    }
    
    @Override
    public EventCtxType getActionCtx() {
		return eventCtx;
	}
    
    @Override
    public void setActionCtx(EventCtxType ctx) {
    	this.eventCtx = ctx;
    }
    
    public Object getProcCtx() {
        return getProcCtx(null);
    }
    
    public Object getProcCtx(DustGenUtils.ItemCreator<?> creator) {
        Object ret = ((null != procCtx) && (procCtx.size() > depth)) ? procCtx.get(depth) : null;
        
        if ( (null != creator) && (null == ret) ) {
            ret = creator.create();
            setProcCtx(ret);
        }
        
        return ret;
    }
    
    public void setProcCtx(Object ob) {
        if ( null == procCtx  ) {
            procCtx = new ArrayList<>();
        }
        
        for ( int i = procCtx.size(); i <= depth; ++i ) {
            procCtx.add(null);
        }
        
        procCtx.set(depth, ob);
    }
    
    public Object getProcCtxNeighbor(boolean up) {
        Object ret = null;
        
        if ( null != procCtx ) {
            int d = up ? depth - 1 : depth + 1;
            if ( (0 <= d) && (d < procCtx.size()) ) {
                ret = procCtx.get(d);
            }
        }
        
        return ret;
    }
    
    public DustResultType setRelay(DustAgent agent, boolean shareCtx) throws Exception {
        if ( null == relayStack ) {
            relayStack = new Stack<>();
        }
        
        relayStack.push(new RelayInfo(shareCtx));
        
        current = agent;
        
        if ( shareCtx ) {
            return DustResultType.READ;
        } else {
            return move(true);
//            return current.agentAction(DustAgentAction.BEGIN);
        }
    }
    
    private DustResultType move(boolean down) throws Exception {
    	DustResultType ret = DustResultType.NOTIMPLEMENTED;
    	
        if ( down ) {
            ++ depth;
            ret = current.agentAction(DustAgentAction.BEGIN);
        } else {
            if ( null != relayStack ) {
                Exception e = null;
                
                while ( !relayStack.isEmpty() && (depth == relayStack.peek().start) ) {
                    RelayInfo ri = relayStack.pop();
                    if ( !ri.shareCtx ) {
                        try {
                            ret = current.agentAction(DustAgentAction.END);
                        } catch ( Exception ex ) {
                            if ( null == e ) {
                                e = ex;
                            }
                        } finally {
                        	--depth;
                        }
                    }
                    current = ri.parent;
                    
                    try {
                        ret = current.agentAction(DustAgentAction.END);
                    } catch ( Exception ex ) {
                        if ( null == e ) {
                            e = ex;
                        }
                    }

                }
                
                if ( null != e ) {
                    throw e;
                }
            }
            
            --depth;
            
            if ( ret == DustResultType.NOTIMPLEMENTED ) {
            	ret = current.agentAction(DustAgentAction.END);
            }
        }
        
        return ret;
    }

    @Override
    public DustResultType agentAction(DustAgentAction action) throws Exception {
        switch (action) {
        case INIT:
            depth = 0;
            break;
        case RELEASE:
            if ( null != procCtx ) {
                procCtx.clear();
            }
            if ( 0 != depth ) {
                DustGenException.throwException(null, "invalid depth");
            }
            break;
        default:
            break;
        }
        
        switch (action) {
        case BEGIN:
            return move(true);
        case END:
            return move(false);
        default:
        	return current.agentAction(action);
        }
//        try {
//            if ( action == DustAgentAction.BEGIN ) {
//                move(true);
//            }
//            return current.agentAction(action);
//        } finally {
//            if ( action == DustAgentAction.END ) {
//                move(false);
//            }
//        }
    }
}
