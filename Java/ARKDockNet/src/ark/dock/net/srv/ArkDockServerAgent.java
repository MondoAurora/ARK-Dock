package ark.dock.net.srv;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;

import ark.dock.ArkDockDsl;
import ark.dock.ArkDockMind.ArkDockMindContext;
import ark.dock.ArkDockMind.BaseAgent;
import ark.dock.ArkDockModel;
import ark.dock.ArkDockUtils;
import dust.gen.DustGenFactory;
import dust.gen.DustGenShutdown;

public class ArkDockServerAgent extends BaseAgent<ArkDockMindContext> implements DustGenShutdown.ShutdownAware, ArkDockSrvConsts {
	ArkDockDsl.Net tokNet;
	Server server;

	class CtrlHandler extends AbstractHandler {
		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
			
			String prefix = "/srvctl/";

			if ( target.startsWith(prefix) ) {
				String str = target.substring(prefix.length());
				ArkDockSrvCmd cmd = ArkDockUtils.fromString(str, ArkDockSrvCmd.ping);
				
				response.setContentType("text/html; charset=utf-8");
				response.setStatus(HttpServletResponse.SC_OK);

				try {
					switch ( cmd ) {
					case stop:
						stopSrv();
						break;
					default:
						break;
					}
					baseRequest.setHandled(true);
				} catch (Throwable ex) {
					throw new ServletException(ex);
				}
			} else {
				baseRequest.setHandled(false);
				return;
			}
		}
	}

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
			stopSrv();
			break;
		default:
			break;
		}

		return ret;
	}

	public void init() throws Exception {
		ArkDockModel mod = getMind().modMain;
		tokNet = new ArkDockDsl.Net(mod.getMeta());

		DustEntity def = getDef();
		DustEntity eSvc;
		DustEntity m = getDsl(ArkDockDsl.Generic.class).eCollMember;
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
					handlers.addHandler(new CtrlHandler());
				}
				handlers.addHandler(ha.getWrapOb());

				eSvc = mod.getMember(e, getDsl(ArkDockDsl.Generic.class).eLinkTarget, null, null);
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
		if ( (null != server) && (!server.isStopping()) ) {
			server.stop();
			server = null;
		}
	}

	void stopSrv() throws Exception {
		DustGenShutdown.remove(this);
		shutdown();
	}
}
