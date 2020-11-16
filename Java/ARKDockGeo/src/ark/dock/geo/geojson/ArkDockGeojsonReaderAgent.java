package ark.dock.geo.geojson;

import java.util.Map;
import java.util.Stack;

import ark.dock.ArkDockVisitor;
import ark.dock.geo.geojson.ArkDockGeojsonGeometry.GeoColl;
import ark.dock.io.json.ArkDockJsonReaderAgent;
import dust.gen.DustGenUtils;

@SuppressWarnings("unchecked")
public class ArkDockGeojsonReaderAgent extends ArkDockJsonReaderAgent implements ArkDockGeojsonConsts {

	@SuppressWarnings({ "rawtypes" })
	public class GeometryReader extends ArkDockJsonReaderAgent {

		GeojsonKey readingKey;

		GeojsonGeometryType typeGeom;

		ArkDockGeojsonGeometry geo;
		Stack<ArkDockGeojsonGeometry> readStack;

		int coordIdx;

		@Override
		public DustResultType agentAction(DustAgentAction action) throws Exception {
			DustResultType ret = DustResultType.READ;
			boolean callSuper = true;

			switch ( action ) {
			case INIT:
				geo = null;
				break;
			case BEGIN:
				switch ( ctx.block ) {
				case Entry:
					readingKey = DustGenUtils.fromString((String) ctx.param, GeojsonKey.class);
					break;
				case Array:
					if ( readingKey == GeojsonKey.coordinates ) {
						if ( null != geo ) {
							if ( null == readStack ) {
								readStack = new Stack();
							}
							readStack.push(geo);
						}
						geo = ArkDockGeojsonGeometry.forType(typeGeom);
						coordIdx = 0;
						typeGeom = typeGeom.childType;
						callSuper = false;
					}
					break;
				default:
					break;
				}
				break;
			case PROCESS:
				if ( GeojsonKey.type == readingKey ) {
					typeGeom = DustGenUtils.fromString((String) ctx.param, GeojsonGeometryType.class);
				} else if ( readingKey == GeojsonKey.coordinates ) {
					((ArkDockGeojsonGeometry.Point) geo).addCoord(coordIdx++, (Double) ctx.param);
					callSuper = false;
				}
				break;
			case END:
				if (( ctx.block == JsonBlock.Array ) && ( readingKey == GeojsonKey.coordinates ) ) {
					if ( (null != readStack) && !readStack.isEmpty() ) {
						GeoColl g = (GeoColl) readStack.pop();
						g.addInfo(geo);
						geo = g;
						typeGeom = geo.getType().childType;
					}
					callSuper = false;
				}
				break;
			case RELEASE:
				break;
			default:
				break;
			}

			if ( callSuper ) {
				super.agentAction(action);
			}
			
			return ret;
		}
	}

	ArkDockAgent<GeojsonContext> processor;
	ArkDockVisitor<JsonContext> visitor;

	GeojsonKey readingKey;
	boolean startingFeature;

	ArkDockGeojsonReaderAgent featureReader;
	GeometryReader geometryReader;

	GeojsonContext procCtx = new GeojsonContext();

	public ArkDockGeojsonReaderAgent(ArkDockAgent<GeojsonContext> processor) {
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
						geometryReader = new GeometryReader();
					}
					relay = geometryReader;
				} else if ( GeojsonKey.features == readingKey ) {
					if ( null == featureReader ) {
						processor.setActionCtx(procCtx);
						sendFeatColl(DustAgentAction.BEGIN);
						featureReader = new ArkDockGeojsonReaderAgent(processor);
						featureReader.procCtx = procCtx;
					}
					relay = featureReader;
					startingFeature = true;
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
					callSuper = false;
				} else {
					readingKey = null;
				}
			} else if ( GeojsonKey.geometry == readingKey ) {
				procCtx.geometry = geometryReader.geo;
				readingKey = null;
				callSuper = false;
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
