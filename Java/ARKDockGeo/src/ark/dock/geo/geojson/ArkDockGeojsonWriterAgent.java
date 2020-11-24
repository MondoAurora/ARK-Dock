package ark.dock.geo.geojson;

import ark.dock.ArkDockConsts.ArkDockAgentDefault;
import ark.dock.geo.geojson.ArkDockGeojsonConsts.GeojsonContext;
import ark.dock.io.json.ArkDockJsonConsts;
import ark.dock.io.json.ArkDockJsonUtils;
import dust.gen.DustGenDevUtils;
import dust.gen.DustGenUtils;

public class ArkDockGeojsonWriterAgent extends ArkDockAgentDefault<GeojsonContext> implements ArkDockJsonConsts, ArkDockGeojsonConsts, DustGenDevUtils {
	ArkDockAgent<JsonContext> jsonAgent;
	
	public static String defCoordSys;
	
	private String coordSys;
	private boolean writeCS;

	public void setTarget(ArkDockAgent<JsonContext> jsonAgent_) {
		this.jsonAgent = jsonAgent_;
	}
	
	public void setCoordSys(String coordSys) {
		this.coordSys = coordSys;
	}
	
	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {
		DustResultType ret = DustResultType.READ;
		JsonContext ctxJson = jsonAgent.getActionCtx();
		
		switch ( action ) {
		case INIT:
			jsonAgent.agentAction(DustAgentAction.INIT);
			if ( DustGenUtils.isEmpty(coordSys)) {
				coordSys = defCoordSys;
			}
			writeCS = !DustGenUtils.isEmpty(coordSys);
			break;
		case BEGIN:
			ctxJson.block = JsonBlock.Object;
			jsonAgent.agentAction(DustAgentAction.BEGIN);
			ArkDockJsonUtils.sendSimpleEntry(jsonAgent, GeojsonKey.type, GeojsonObjectType.FeatureCollection);
			if ( writeCS ) {
				ArkDockJsonUtils.sendSimpleEntry(jsonAgent, GeojsonKey.coordSys, coordSys);				
				writeCS = false;
			}
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
			if ( writeCS ) {
				ArkDockJsonUtils.sendSimpleEntry(jsonAgent, GeojsonKey.coordSys, coordSys);				
			}

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
