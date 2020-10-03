package ark.dock.srv;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;

import ark.dock.ArkDockMind.BaseAgent;
import ark.dock.ArkDockModel;
import ark.dock.ArkDockTokens;
import dust.gen.DustGenFactory;
import dust.gen.DustGenShutdown;

@SuppressWarnings("rawtypes")
public class ArkDockServerAgent extends BaseAgent implements DustGenShutdown.ShutdownAware, ArkDockSrvConsts {
	ArkDockTokens.Net tokNet;
	Server server;

	DustGenFactory<Integer, ServerConnector> factChannels = new DustGenFactory<Integer, ServerConnector>(null) {
		private static final long serialVersionUID = 1L;

		protected ServerConnector createItem(Integer key, Object hint) {
			ServerConnector sc = new ServerConnector(server);
			sc.setPort(key);
			server.addConnector(sc);
			return sc;
		}
	};

	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {
		DustResultType ret = super.agentAction(action);

		switch ( action ) {
		case INIT:
			init();
			break;
		case BEGIN:
			break;
		case END:
			DustGenShutdown.remove(this);
			stopSrv();
			break;
		default:
			break;
		}

		return ret;
	}

	public void init() throws Exception {
		ArkDockModel mod = getMind().modMain;
		tokNet = new ArkDockTokens.Net(mod.getMeta());

		DustEntity def = getDef();
		DustEntity eSvc;
		DustEntity m = getMind().tokGeneric.eCollMember;
		DustEntity e;

		HandlerList handlers = null;

		int cnt = mod.getCount(def, m);
		for (int i = 0; i < cnt; ++i) {
			e = mod.getMember(def, m, null, i);

			HandlerAgent<? extends AbstractHandler> ha = mod.getMember(e, getMind().tokNative.eNativeValueOne, null, i);
			if ( null != ha ) {
				if ( null == server ) {
					server = new Server();
					handlers = new HandlerList();
				}
				handlers.addHandler(ha.getWrapOb());

				eSvc = mod.getMember(e, getMind().tokGeneric.eLinkTarget, null, null);
				Long port = mod.getMember(eSvc, tokNet.memServicePort, 8080L, null);

				factChannels.get(port.intValue());
			}
		}

		if ( null != server ) {
			server.setHandler(handlers);

			server.start();
			DustGenShutdown.register(this);
		}
	}

	@Override
	public void shutdown() throws Exception {
		stopSrv();
	}

	void stopSrv() throws Exception {
		if ( (null != server) && (!server.isStopping()) ) {
			server.stop();
			server = null;
		}
	}
}
