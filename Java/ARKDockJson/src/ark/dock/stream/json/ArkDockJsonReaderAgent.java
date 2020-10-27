package ark.dock.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ark.dock.ArkDockVisitor;
import ark.dock.json.ArkDockJsonConsts.JsonBlock;
import ark.dock.json.ArkDockJsonConsts.JsonContext;
import dust.gen.DustGenConsts.DustAgentAction;
import dust.gen.DustGenConsts.DustAgent;
import dust.gen.DustGenConsts.DustResultType;

@SuppressWarnings("rawtypes")
public class ArkDockJsonReaderAgent implements DustAgent {

	private final ArkDockVisitor<JsonContext> visitor;

	private final JsonContext ctx;

	private Object root;

	private String key;
	private JsonBlock blockType;
	private Object blockOb;

	public ArkDockJsonReaderAgent(ArkDockVisitor<JsonContext> visitor) {
		this.visitor = visitor;
		this.ctx = visitor.getActionCtx();
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
				((List) blockOb).add(ob);
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

	private void startBlock(JsonBlock b, Object ob) {
		blockType = b;
		blockOb = ob;
		visitor.setProcCtx(ob);

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