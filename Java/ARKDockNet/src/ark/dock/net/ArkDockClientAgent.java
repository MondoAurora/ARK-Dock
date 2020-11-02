package ark.dock.net;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import ark.dock.ArkDock;
import ark.dock.ArkDockConsts.ArkDockAgentBase;
import ark.dock.ArkDockUnit;
import ark.dock.net.srv.ArkDockSrvConsts;

public class ArkDockClientAgent extends ArkDockAgentBase implements ArkDockSrvConsts {
	ArkDockUnit mod;
	
	DslNet dslNet;
	DslNative dslNative;
	DslDialog dslDialog;
	DslGeneric dslGeneric;
	
	URL url;
	String method;

	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {
		DustResultType ret = super.agentAction(action);

		switch ( action ) {
		case INIT:
			mod = ArkDock.getMind().getMainUnit();
			
			dslNet = ArkDock.getDsl(DslNet.class);
			dslNative = ArkDock.getDsl(DslNative.class);
			dslDialog = ArkDock.getDsl(DslDialog.class);
			dslGeneric = ArkDock.getDsl(DslGeneric.class);

			DustEntity def = getDef();
			DustEntity eSvc = mod.getMember(def, dslGeneric.memLinkSource, null, null);

			Long port = mod.getMember(eSvc, dslNet.memServicePort, 8080L, null);
			DustEntity eHost = mod.getMember(eSvc, dslNet.memServiceHost, null, null);
			String hostName = mod.getMember(eHost, dslNet.memHostName, "", null);

			String path = mod.getMember(def, dslNet.memClientPath, "", null);
			method = ArkDock.getId(mod.getMember(def, dslNet.memClientMethod, dslNet.tagHttpMethodGET, null));
			int split = method.lastIndexOf(TOKEN_SEP);
			if ( -1 != split ) {
				method = method.substring(split + 1);
			}

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
			mod.setMember(getDef(), dslDialog.memActionRespRaw, r, null);

			break;
		default:
			break;
		}

		return ret;
	}
}
