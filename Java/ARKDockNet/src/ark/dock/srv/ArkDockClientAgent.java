package ark.dock.srv;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import ark.dock.ArkDockMind.BaseAgent;
import ark.dock.ArkDockModel;
import ark.dock.ArkDockTokens;

@SuppressWarnings("rawtypes")
public class ArkDockClientAgent extends BaseAgent implements ArkDockSrvConsts {
	ArkDockTokens.Net tokNet;
	ArkDockTokens.Native tokNative;
	URL url;
	String method;

	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {
		DustResultType ret = super.agentAction(action);

		switch ( action ) {
		case INIT:
			ArkDockModel mod = getMind().modMain;
			tokNet = new ArkDockTokens.Net(mod.getMeta());
			tokNative = new ArkDockTokens.Native(mod.getMeta());

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
			getMind().modMain.setMember(getDef(), getMind().tokDialog.memActionResponse, r, null);

			break;
		default:
			break;
		}

		return ret;
	}
}
