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

import ark.dock.ArkDock;
import ark.dock.ArkDockConsts.ArkDockAgentWrapper;
import ark.dock.ArkDockUnit;
import ark.dock.ArkDockUtils;
import dust.gen.DustGenFactory;
import dust.gen.DustGenShutdown;

public class ArkDockServerAgent extends ArkDockAgentWrapper<Server> implements DustGenShutdown.ShutdownAware, ArkDockSrvConsts {

	class CtrlHandler extends AbstractHandler {
		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
			
			String prefix = "/srvctl/";

			if ( target.startsWith(prefix) ) {
				String str = target.substring(prefix.length());
				ArkDockSrvCmd cmd = ArkDockUtils.fromString(str, ArkDockSrvCmd.ping);
				
				try {
					switch ( cmd ) {
					case stop:
						stopSrv();
						break;
					default:
						break;
					}
					response.setContentType("text/html; charset=utf-8");
					response.setStatus(HttpServletResponse.SC_OK);
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
	
	Server server;

	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {
		DustResultType ret = super.agentAction(action);

		switch ( action ) {
		case INIT:
			server = getBinObj();

			if ( null != server ) {
				server.start();
				DustGenShutdown.register(this);
			}
			
			break;
		case END:
			stopSrv();
			break;
		default:
			break;
		}

		return ret;
	}

	@Override
	protected Server createBinObj() throws Exception {
		ArkDockUnit unit = ArkDock.getMind().getMainUnit();
		
		DslNet dslNet = ArkDock.getDsl(DslNet.class);
		DslGeneric dslGen = ArkDock.getDsl(DslGeneric.class);
		DslNative dslNative = ArkDock.getDsl(DslNative.class);

		DustEntity def = getDef();
		DustEntity eSvc;
		DustEntity m = dslGen.memCollMember;
		DustEntity e;

		HandlerList handlers = null;

		int cnt = unit.getCount(def, m);
		for (int i = 0; i < cnt; ++i) {
			e = unit.getMember(def, m, null, i);

			HandlerAgent<? extends AbstractHandler> ha = unit.getMember(e, dslNative.memNativeValueOne, null, i);
			if ( null != ha ) {
				if ( null == server ) {
					server = new Server();
					handlers = new HandlerList();
					handlers.addHandler(new CtrlHandler());
					server.setHandler(handlers);
				}
				handlers.addHandler(ha.getBinObj());

				eSvc = unit.getMember(e, dslGen.memLinkTarget, null, null);
				Long port = unit.getMember(eSvc, dslNet.memServicePort, 8080L, null);

				factChannels.get(port.intValue());
			}
		}
		
		return server;
	}

	@Override
	public void shutdown() throws Exception {
		Server server = getBinObj();
		
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
