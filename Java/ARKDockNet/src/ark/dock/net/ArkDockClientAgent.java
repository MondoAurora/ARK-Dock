package ark.dock.net;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import ark.dock.ArkDockMind.BaseAgent;
import ark.dock.net.srv.ArkDockSrvConsts;
import ark.dock.ArkDockModel;
import ark.dock.ArkDockDsl;

@SuppressWarnings("rawtypes")
public class ArkDockClientAgent extends BaseAgent implements ArkDockSrvConsts {
	ArkDockModel mod;
	
	ArkDockDsl.DslNet dslNet;
	ArkDockDsl.DslNative dslNative;
	ArkDockDsl.DslDialog dslDialog;
	
	URL url;
	String method;

	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {
		DustResultType ret = super.agentAction(action);

		switch ( action ) {
		case INIT:
			mod = getMind().modMain;
			dslNet = mod.getDsl(ArkDockDsl.DslNet.class);
			dslNative = mod.getDsl(ArkDockDsl.DslNative.class);
			dslDialog = mod.getDsl(ArkDockDsl.DslDialog.class);

			DustEntity def = getDef();
			DustEntity eSvc = mod.getMember(def, mod.getMeta().dslGeneric.memLinkSource, null, null);

			Long port = mod.getMember(eSvc, dslNet.memServicePort, 8080L, null);
			DustEntity eHost = mod.getMember(eSvc, dslNet.memServiceHost, null, null);
			String hostName = mod.getMember(eHost, dslNet.memHostName, "", null);

			String path = mod.getMember(def, dslNet.memClientPath, "", null);
			method = mod.getId(mod.getMember(def, dslNet.memClientMethod, dslNet.tagHttpMethodGET, null));

			url = new URL("http://" + hostName + ":" + port + "/" + path);

			break;
		case PROCESS:
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(method);
			Reader r = null;
			try {
				r = new InputStreamReader(conn.getInputStream());
			} catch (Exception ex) {
				// no problem, may get no response
			}
			mod.setMember(getDef(), dslDialog.memActionResponse, r, null);

			break;
		default:
			break;
		}

		return ret;
	}
}
