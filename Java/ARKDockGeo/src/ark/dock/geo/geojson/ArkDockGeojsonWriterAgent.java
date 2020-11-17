package ark.dock.geo.geojson;

import ark.dock.ArkDockConsts.ArkDockAgentDefault;
import ark.dock.geo.geojson.ArkDockGeojsonConsts.GeojsonContext;
import ark.dock.io.json.ArkDockJsonConsts;
import ark.dock.io.json.ArkDockJsonUtils;
import dust.gen.DustGenDevUtils;

public class ArkDockGeojsonWriterAgent extends ArkDockAgentDefault<GeojsonContext> implements ArkDockJsonConsts, ArkDockGeojsonConsts, DustGenDevUtils {
	ArkDockAgent<JsonContext> jsonAgent;
//	ArrayList<String> dummy = new ArrayList<>();

	public ArkDockGeojsonWriterAgent(ArkDockAgent<JsonContext> jsonAgent_) throws Exception {
		this.jsonAgent = jsonAgent_;
	}
	
	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {
		DustResultType ret = DustResultType.READ;
		JsonContext ctxJson = jsonAgent.getActionCtx();
		
		switch ( action ) {
		case INIT:
			jsonAgent.agentAction(DustAgentAction.INIT);
			break;
		case BEGIN:
			ctxJson.block = JsonBlock.Object;
			jsonAgent.agentAction(DustAgentAction.BEGIN);
			ArkDockJsonUtils.sendSimpleEntry(jsonAgent, GeojsonKey.type, GeojsonObjectType.FeatureCollection);
			ctxJson.block = JsonBlock.Entry;
			ctxJson.param = GeojsonKey.features;
			jsonAgent.agentAction(DustAgentAction.BEGIN);

			ctxJson.block = JsonBlock.Array;
			jsonAgent.agentAction(DustAgentAction.BEGIN);

			break;
		case PROCESS:
			ctxJson.block = JsonBlock.Object;
			jsonAgent.agentAction(DustAgentAction.BEGIN);

			ArkDockJsonUtils.sendSimpleEntry(jsonAgent, GeojsonKey.type, GeojsonObjectType.Feature);
			ArkDockJsonUtils.sendMultiEntry(jsonAgent, ctx.data, GeojsonKey.id, GeojsonKey.bbox, GeojsonKey.properties);

			ctxJson.block = JsonBlock.Entry;
			ctxJson.param = GeojsonKey.geometry;
			jsonAgent.agentAction(DustAgentAction.BEGIN);

			ctxJson.block = JsonBlock.Object;
			jsonAgent.agentAction(DustAgentAction.BEGIN);
			
			ArkDockJsonUtils.sendSimpleEntry(jsonAgent, GeojsonKey.type, ctx.geometry.getType());
			
//			ArkDockJsonUtils.sendSimpleEntry(jsonAgent, GeojsonKey.coordinates, dummy);
			ctxJson.block = JsonBlock.Entry;
			ctxJson.param = GeojsonKey.coordinates;
			jsonAgent.agentAction(DustAgentAction.BEGIN);

			ctxJson.param = ctx.geometry;
			jsonAgent.agentAction(DustAgentAction.PROCESS);

			ctxJson.block = JsonBlock.Entry;
			ctxJson.param = GeojsonKey.coordinates;
			jsonAgent.agentAction(DustAgentAction.END);


			ctxJson.block = JsonBlock.Entry;
			jsonAgent.agentAction(DustAgentAction.END);

			ctxJson.block = JsonBlock.Object;
			jsonAgent.agentAction(DustAgentAction.END);

			ctxJson.block = JsonBlock.Entry;
			ctxJson.param = GeojsonKey.geometry;
			jsonAgent.agentAction(DustAgentAction.END);

			ctxJson.block = JsonBlock.Object;
			ctxJson.param = GeojsonObjectType.Feature;
			jsonAgent.agentAction(DustAgentAction.END);

			break;
		case END:
			ctxJson.param = GeojsonKey.features;

			ctxJson.block = JsonBlock.Array;
			jsonAgent.agentAction(DustAgentAction.END);

			ctxJson.block = JsonBlock.Entry;
			jsonAgent.agentAction(DustAgentAction.END);

			ctxJson.block = JsonBlock.Object;
			ctxJson.param = GeojsonObjectType.FeatureCollection;
			jsonAgent.agentAction(DustAgentAction.END);
			break;
		case RELEASE:
			jsonAgent.agentAction(DustAgentAction.RELEASE);
			break;
		}
		
		return ret;
	}
}
