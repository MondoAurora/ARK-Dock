package ark.dock.io.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ark.dock.ArkDockConsts.ArkDockAgentDefault;
import ark.dock.ArkDockVisitor;
import ark.dock.ArkDockVisitor.VisitorAware;
import ark.dock.io.json.ArkDockJsonConsts.JsonContext;

@SuppressWarnings("rawtypes")
public class ArkDockJsonReaderAgent extends ArkDockAgentDefault<JsonContext> implements ArkDockJsonConsts, VisitorAware<JsonContext> {

	private ArkDockVisitor<JsonContext> visitor;

	private Object root;

	private String key;
	private JsonBlock blockType;
	private Object blockOb;
	
	public ArkDockJsonReaderAgent() {
	}

	public ArkDockJsonReaderAgent(ArkDockVisitor<JsonContext> visitor) {
		this.visitor = visitor;
		this.ctx = visitor.getActionCtx();
	}

	@Override
	public void setVisitor(ArkDockVisitor<JsonContext> visitor) {
		this.visitor = visitor;
	}
	
	public Object getRoot() {
		return root;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {
		Object ob;
		
		switch ( action ) {
		case INIT:
			root = null;
			key = null;
			blockType = null;
			blockOb = null;
			break;
		case BEGIN:
			switch ( ctx.block ) {
			case Entry:
				key = (String) ctx.param;
				visitor.setProcCtx(blockOb);
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
			ob = visitor.getProcCtx();
			blockOb = visitor.getProcCtxNeighbor(true);
			if ( blockOb instanceof Map ) {
//				((Map) blockOb).put(key, ob);
				blockType = JsonBlock.Object;
			} else if ( blockOb instanceof List ) {
//				((List) blockOb).add(ob);
				blockType = JsonBlock.Array;
			} else {
				blockType = null;
			}
			break;
		case PROCESS:
			ob = ctx.param;
			if ( blockType == JsonBlock.Object ) {
				((Map) blockOb).put(key, ob);
			} else if ( blockType == JsonBlock.Array ) {
				((List) blockOb).add(ob);
			} else if ( null == root ) {
				root = ob;
			}

			break;
		default:
			break;
		}

		return DustResultType.ACCEPT_READ;
	}

	@SuppressWarnings("unchecked")
	private void startBlock(JsonBlock b, Object ob) {
		Object currBlock = blockOb;
		
		blockType = b;
		blockOb = ob;
		visitor.setProcCtx(ob);

		if ( null == root ) {
			root = ob;
		}
		
		if ( currBlock instanceof Map ) {
			((Map) currBlock).put(key, ob);
		} else if ( currBlock instanceof List ) {
			((List) currBlock).add(ob);
		}
	}

	protected List createArr(String key) {
		return new ArrayList();
	}

	protected Map createMap(String key) {
		return new TreeMap();
	}
}