package ark.dock.geo.json;

import java.util.Map;

import ark.dock.json.ArkDockJsonConsts;
import ark.dock.json.ArkDockJsonWriterAgent;
import dust.gen.DustGenDevUtils;

public class ArkDockGeojsonWriter implements ArkDockJsonConsts, ArkDockGeojsonConsts, DustGenDevUtils {
	ArkDockJsonWriterAgent jsonAgent;

	JsonContext ctx;
	GeojsonType listType;

	public ArkDockGeojsonWriter(ArkDockJsonWriterAgent jsonAgent_) throws Exception {
		this.jsonAgent = jsonAgent_;
	}

	public void add(Map<GeojsonKey, Object> src) throws Exception {
		if ( null == ctx ) {
			ctx = new JsonContext();
			jsonAgent.setActionCtx(ctx);
			jsonAgent.agentAction(DustAgentAction.INIT);
		}

		GeojsonType type = (GeojsonType) src.get(GeojsonKey.type);

		ctx.block = JsonBlock.Object;
		ctx.param = type;
		jsonAgent.agentAction(DustAgentAction.BEGIN);

		if ( listType == GeojsonType.FeatureCollection ) {
			ArkDockJsonWriterAgent.sendSimpleEntry(jsonAgent, GeojsonKey.type, GeojsonType.Feature.name());
			ArkDockJsonWriterAgent.sendMultiEntry(jsonAgent, src, GeojsonKey.id, GeojsonKey.bbox,
					GeojsonKey.properties);

			ctx.block = JsonBlock.Entry;
			ctx.param = GeojsonKey.geometry;
			jsonAgent.agentAction(DustAgentAction.BEGIN);

			ctx.block = JsonBlock.Object;
			ctx.param = type;
			jsonAgent.agentAction(DustAgentAction.BEGIN);

			ArkDockJsonWriterAgent.sendSimpleEntry(jsonAgent, GeojsonKey.type, type.name());
		} else {
			ArkDockJsonWriterAgent.sendSimpleEntry(jsonAgent, GeojsonKey.type, type.name());
			ArkDockJsonWriterAgent.sendMultiEntry(jsonAgent, src, GeojsonKey.bbox);
		}

		switch ( type ) {
		case FeatureCollection:
//			ArkDockJsonWriterAgent.sendMultiEntry(jsonAgent, src, GeojsonKey.id, GeojsonKey.properties);

			ctx.block = JsonBlock.Entry;
			ctx.param = GeojsonKey.features;
			jsonAgent.agentAction(DustAgentAction.BEGIN);

			ctx.block = JsonBlock.Array;
			jsonAgent.agentAction(DustAgentAction.BEGIN);
			listType = type;
			break;

		case GeometryCollection:
			ctx.block = JsonBlock.Entry;
			ctx.param = GeojsonKey.geometries;
			jsonAgent.agentAction(DustAgentAction.BEGIN);

			ctx.block = JsonBlock.Array;
			jsonAgent.agentAction(DustAgentAction.BEGIN);
			listType = type;
			break;

		default:
			Object coordinates = src.get(GeojsonKey.coordinates);
			GeojsonType extType = type;

			ArkDockJsonWriterAgent.sendSimpleEntry(jsonAgent, GeojsonKey.coordinates, coordinates);

			ctx.block = JsonBlock.Entry;
			jsonAgent.agentAction(DustAgentAction.END);

			// close containers
			if ( listType == GeojsonType.FeatureCollection ) {
				ctx.block = JsonBlock.Object;
				ctx.param = type;
				jsonAgent.agentAction(DustAgentAction.END);

				ctx.block = JsonBlock.Entry;
				ctx.param = GeojsonKey.geometry;
				jsonAgent.agentAction(DustAgentAction.END);

				extType = GeojsonType.Feature;
			}

			ctx.block = JsonBlock.Object;
			ctx.param = extType;
			jsonAgent.agentAction(DustAgentAction.END);

			break;
		}
	}

	public void close() throws Exception {
		if ( null != listType ) {
			switch ( listType ) {
			case FeatureCollection:
				ctx.param = GeojsonKey.features;

				ctx.block = JsonBlock.Array;
				jsonAgent.agentAction(DustAgentAction.END);

				ctx.block = JsonBlock.Entry;
				jsonAgent.agentAction(DustAgentAction.END);

				ctx.block = JsonBlock.Object;
				ctx.param = GeojsonType.FeatureCollection;
				jsonAgent.agentAction(DustAgentAction.END);

				listType = null;
				break;
			case GeometryCollection:
				ctx.param = GeojsonKey.geometries;

				ctx.block = JsonBlock.Array;
				jsonAgent.agentAction(DustAgentAction.END);

				ctx.block = JsonBlock.Entry;
				jsonAgent.agentAction(DustAgentAction.END);

				ctx.param = GeojsonType.GeometryCollection;
				ctx.block = JsonBlock.Object;
				jsonAgent.agentAction(DustAgentAction.END);

				listType = GeojsonType.FeatureCollection;
				break;
			default:
				break;
			}
		}

		if ( null == listType ) {
			jsonAgent.agentAction(DustAgentAction.RELEASE);
		}
		
		ctx = null;
		jsonAgent.setActionCtx(null);
	}
}
