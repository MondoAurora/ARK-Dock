package ark.dock.geo.geojson;

import java.util.Map;

import ark.dock.ArkDockVisitor;
import ark.dock.io.json.ArkDockJsonReaderAgent;
import dust.gen.DustGenUtils;

@SuppressWarnings("unchecked")
public class ArkDockGeojsonObjectReaderAgent extends ArkDockJsonReaderAgent implements ArkDockGeojsonConsts {

	ArkDockAgent<GeojsonContext> processor;
	ArkDockVisitor<JsonContext> visitor;

	GeojsonKey readingKey;
	boolean startingFeature;

	ArkDockGeojsonObjectReaderAgent featureReader;
	ArkDockGeojsonGeometryReaderAgent geometryReader;

	GeojsonContext procCtx = new GeojsonContext();

//	String name = "top";

	public ArkDockGeojsonObjectReaderAgent(ArkDockAgent<GeojsonContext> processor) {
		this.processor = processor;
	}

	@Override
	public void setVisitor(ArkDockVisitor<JsonContext> visitor) {
		super.setVisitor(visitor);
		this.visitor = visitor;
	}

	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {
		ArkDockJsonReaderAgent relay = null;
		boolean callSuper = true;

//		DustGenLog.log("object", action, ctx);

		switch ( action ) {
		case INIT:
			break;
		case BEGIN:
			switch ( ctx.block ) {
			case Entry:
				readingKey = DustGenUtils.fromString((String) ctx.param, GeojsonKey.class);
				break;
			case Object:
				if ( GeojsonKey.geometry == readingKey ) {
					if ( null == geometryReader ) {
						geometryReader = new ArkDockGeojsonGeometryReaderAgent();
					}
					relay = geometryReader;
//					DustGenLog.log(name, "reading geometry");
				} else if ( GeojsonKey.features == readingKey ) {
					if ( null == featureReader ) {
						processor.setActionCtx(procCtx);
						sendFeatColl(DustAgentAction.BEGIN);
						featureReader = new ArkDockGeojsonObjectReaderAgent(processor);
						featureReader.procCtx = procCtx;
//						featureReader.name = "child";
					}
					relay = featureReader;
					startingFeature = true;
//					DustGenLog.log(name, "Ob reading feature");
				}

				break;
			default:
				break;
			}
			break;
		case PROCESS:
			if ( GeojsonKey.type == readingKey ) {
				GeojsonObjectType tOb = DustGenUtils.fromString((String) ctx.param, GeojsonObjectType.class);
				if ( null != tOb ) {
					procCtx.typeObj = tOb;
				}
			}
			break;
		case END:
			if ( GeojsonKey.features == readingKey ) {
				if ( startingFeature ) {
					startingFeature = false;
					procCtx.typeObj = GeojsonObjectType.Feature;
					procCtx.data = (Map<String, Object>) featureReader.getRoot();
					processor.agentAction(DustAgentAction.PROCESS);
//					DustGenLog.log(name, "end reading feature");
					callSuper = false;
				} else {
					readingKey = null;
				}
			} else if ( GeojsonKey.geometry == readingKey ) {
				procCtx.geometry = geometryReader.geo;
				readingKey = null;
				callSuper = false;
//				DustGenLog.log(name, "end reading geometry");
			}
			break;
		case RELEASE:
			if ( null != featureReader ) {
				sendFeatColl(DustAgentAction.END);
				processor.setActionCtx(null);
				featureReader = null;
			}
			if ( null != geometryReader ) {
				geometryReader = null;
			}
			readingKey = null;
			break;
		default:
			break;
		}

		if ( null != relay ) {
			relay.setActionCtx(ctx);
			relay.setVisitor(visitor);
			relay.agentAction(DustAgentAction.INIT);
			return visitor.setRelay(relay, false);
		} else if ( callSuper ) {
			return super.agentAction(action);
		} else {
			return DustResultType.READ;
		}
	}

	public void sendFeatColl(DustAgentAction action) throws Exception {
		procCtx.typeObj = GeojsonObjectType.FeatureCollection;
		procCtx.data = (Map<String, Object>) getRoot();
		processor.agentAction(action);
	}

}
