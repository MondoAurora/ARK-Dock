package ark.dock.io.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ark.dock.ArkDockConsts.ArkDockAgentDefault;
import ark.dock.ArkDockVisitor;
import ark.dock.ArkDockVisitor.VisitorAware;
import ark.dock.io.json.ArkDockJsonConsts.JsonContext;
import dust.gen.DustGenUtils;

@SuppressWarnings("rawtypes")
public class ArkDockJsonReaderAgent extends ArkDockAgentDefault<JsonContext>
		implements ArkDockJsonConsts, VisitorAware<JsonContext> {

	private ArkDockVisitor<JsonContext> visitor;

	private Object root;

	private String key;
	private Object container;

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
	
	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {
		switch ( action ) {
		case INIT:
			root = null;
			key = null;
			container = null;
			break;
		case BEGIN:
			Object newContainer = null;

			switch ( ctx.block ) {
			case Entry:
				key = (String) ctx.param;
				break;
			case Array:
				newContainer = createArr();
				break;
			case Object:
				newContainer = createMap();
				break;
			}

			if ( null != newContainer ) {
				store(newContainer);
				container = newContainer;
				visitor.setProcCtx(container);
			}

			break;
		case END:
			container = visitor.getProcCtx();
			break;
		case PROCESS:
			store(ctx.param);
			break;
		default:
			break;
		}

		return DustResultType.ACCEPT_READ;
	}
	
	@Override
	public String toString() {
		return DustGenUtils.toStringSafe(root);
	}

	@SuppressWarnings("unchecked")
	private void store(Object o) {
		if ( null == root ) {
			root = o;
		} else {
			if ( null == key ) {
				((List) container).add(o);
			} else {
				((Map) container).put(key, o);
				key = null;
			}
		}
	}

	protected List createArr() {
		return new ArrayList();
	}

	protected Map createMap() {
		return new TreeMap();
	}
}