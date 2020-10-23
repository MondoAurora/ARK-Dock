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
	
	ArkDockDsl.Net tokNet;
	ArkDockDsl.Native tokNative;
	ArkDockDsl.Dialog tokDialog;
	
	URL url;
	String method;

	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {
		DustResultType ret = super.agentAction(action);

		switch ( action ) {
		case INIT:
			mod = getMind().modMain;
			tokNet = mod.getDsl(ArkDockDsl.Net.class);
			tokNative = mod.getDsl(ArkDockDsl.Native.class);
			tokDialog = mod.getDsl(ArkDockDsl.Dialog.class);

			DustEntity def = getDef();
			DustEntity eSvc = mod.getMember(def, mod.getMeta().tokGeneric.eLinkSource, null, null);

			Long port = mod.getMember(eSvc, tokNet.memServicePort, 8080L, null);
			DustEntity eHost = mod.getMember(eSvc, tokNet.memServiceHost, null, null);
			String hostName = mod.getMember(eHost, tokNet.memHostName, "", null);

			String path = mod.getMember(def, tokNet.memClientPath, "", null);
			method = mod.getId(mod.getMember(def, tokNet.memClientMethod, tokNet.tagHttpMethodGET, null));

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
			mod.setMember(getDef(), tokDialog.memActionResponse, r, null);

			break;
		default:
			break;
		}

		return ret;
	}
}
