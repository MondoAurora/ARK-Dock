package ark.dock.geo.geojson;

import ark.dock.ArkDockVisitor;
import ark.dock.io.json.ArkDockJsonReaderAgent;
import dust.gen.DustGenUtils;

public class ArkDockGeojsonObjectReaderAgent extends ArkDockJsonReaderAgent implements ArkDockGeojsonConsts {

	GeojsonProcessor processor;
	ArkDockVisitor<JsonContext> visitor;

	JsonContext ctx;
	GeojsonKey readingKey;

	ArkDockGeojsonObjectReaderAgent featureReader;
	ArkDockGeojsonGeometryReaderAgent geometryReader;

	public ArkDockGeojsonObjectReaderAgent(GeojsonProcessor processor) {
		this.processor = processor;
	}
	
	@Override
	public void setVisitor(ArkDockVisitor<JsonContext> visitor) {
		super.setVisitor(visitor);
		this.visitor = visitor;
	}

	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {

		switch ( action ) {
		case INIT:
			ctx = getActionCtx();
			break;
		case BEGIN:
			switch ( ctx.block ) {
			case Entry:
				readingKey = DustGenUtils.fromString((String) ctx.param, GeojsonKey.class);
				break;
			case Array:
				if ( GeojsonKey.features == readingKey) {
					if ( null == featureReader ) {
						featureReader = new ArkDockGeojsonObjectReaderAgent(processor);
						featureReader.setVisitor(visitor);
						featureReader.setActionCtx(ctx);
					}
					
					visitor.setRelay(featureReader, false);
				}
				break;
			case Object:
				if ( GeojsonKey.geometry == readingKey) {
					if ( null == geometryReader ) {
						geometryReader = new ArkDockGeojsonGeometryReaderAgent();
					}
					visitor.setRelay(geometryReader, false);
				}
				break;
			default:
				break;
			}
			break;
		case END:
			break;
		default:
			break;
		}

		return super.agentAction(action);
	}

}
