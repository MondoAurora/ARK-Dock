package ark.dock.io.json;

import java.io.Reader;

import org.json.simple.parser.JSONParser;

import ark.dock.ArkDockConsts.ArkDockAgent;
import ark.dock.io.ArkDockIOUtils;
import ark.dock.io.json.ArkDockJsonConsts.JsonContentDispatcher;
import ark.dock.io.json.ArkDockJsonConsts.JsonContext;
import dust.gen.DustGenConsts.DustAgentAction;
import dust.gen.DustGenConsts.DustResultType;

public class ArkDockIOConnJson extends ArkDockIOUtils.IoConnector<JsonContext> {

	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {
		return DustResultType.REJECT;
	}

	@Override
	public boolean isText() {
		return true;
	}
	
	@Override
	public JsonContext createContext() {
		return new JsonContext();
	}
	
	@Override
	public DustResultType read(Reader source, ArkDockAgent<JsonContext> processor) throws Exception {
		JSONParser p = new JSONParser();
		JsonContentDispatcher h = new JsonContentDispatcher(processor);
		p.parse(source, h);
		return DustResultType.ACCEPT;
	}
}