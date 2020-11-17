package ark.dock.io.json;

import java.util.Map;

public class ArkDockJsonUtils implements ArkDockJsonConsts {

	@SuppressWarnings("rawtypes")
	public static void sendMultiEntry(ArkDockAgent<JsonContext> jsonAgent, Map src, Object... keys) throws Exception {
		for (Object k : keys) {
			Object v = src.get(k.toString());
			if ( null != v ) {
				sendSimpleEntry(jsonAgent, k, v);
			}
		}
	}

	public static void sendSimpleEntry(ArkDockAgent<JsonContext> jsonAgent, Object name, Object value) throws Exception {
		JsonContext ctx = jsonAgent.getActionCtx();

		ctx.block = JsonBlock.Entry;
		ctx.param = name;
		jsonAgent.agentAction(DustAgentAction.BEGIN);

		ctx.param = value;
		jsonAgent.agentAction(DustAgentAction.PROCESS);
		jsonAgent.agentAction(DustAgentAction.END);
	}

}
