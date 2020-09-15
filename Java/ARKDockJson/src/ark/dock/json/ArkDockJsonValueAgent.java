package ark.dock.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ark.dock.json.ArkDockJsonConsts.JsonBlock;
import ark.dock.json.ArkDockJsonConsts.JsonContext;
import dust.gen.DustGenAgentSmart;
import dust.gen.DustGenConsts.DustAgentAction;
import dust.gen.DustGenConsts.DustGenAgent;
import dust.gen.DustGenConsts.DustResultType;

@SuppressWarnings("rawtypes")
public class ArkDockJsonValueAgent implements DustGenAgent {
    
    private final DustGenAgentSmart relayAgent;
    private final JsonContext ctx;
    
    private Object root;
    
    private String key;
    private JsonBlock blockType;
    private Object blockOb;
    
    public ArkDockJsonValueAgent(DustGenAgentSmart relayAgent, JsonContext ctx) {
        this.relayAgent = relayAgent;
        this.ctx = ctx;
    }
    
    public Object getRoot() {
        return root;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public DustResultType agentAction(DustAgentAction action) throws Exception {                
        switch (action) {
        case INIT:
            root = null;
            key = null;
            blockType = null;
            blockOb = null;
            break;
        case BEGIN:
            switch ( ctx.getBlock() ) {
            case Entry:
                key = (String) ctx.getParam();
                relayAgent.setCtxOb(blockOb);
                break;
            case Array:
                startBlock(JsonBlock.Array, createArr(key));
                break;
            case Object:
                startBlock(JsonBlock.Object, createMap(key));
                break;
            }                    
            break;
        case END:
            blockOb = relayAgent.getCtxNeighbor(true);
            if ( blockOb instanceof Map ) {
                blockType = JsonBlock.Object;
            } else if ( blockOb instanceof List ) {
                blockType = JsonBlock.Array;
            } else  {
                blockType = null;
            } 
            break;
        case PROCESS:
            Object ob = ctx.getParam();
            if ( blockType == JsonBlock.Object ) {
                ((Map)blockOb).put(key, ob);
            } else if ( blockType == JsonBlock.Array ) {
                ((List)blockOb).add(ob);
            } else  if ( null == root ) {
                root = ob;
            }
            
            break;
        default:
            break;
        }
        
        return DustResultType.ACCEPT_READ;
    }
    
    private void startBlock(JsonBlock b, Object ob) {
        blockType = b;
        blockOb = ob;
        relayAgent.setCtxOb(ob);
        
        if ( null == root ) {
            root = ob;
        }
    }
    
    protected List createArr(String key) {
        return new ArrayList();
    }
    
    protected Map createMap(String key) {
        return new TreeMap();
    }
}