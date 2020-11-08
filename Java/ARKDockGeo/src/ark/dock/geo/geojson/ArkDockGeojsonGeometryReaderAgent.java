package ark.dock.geo.geojson;

import java.util.Stack;

import ark.dock.geo.geojson.ArkDockGeojsonConsts.GeojsonGeometryType;
import ark.dock.geo.geojson.ArkDockGeojsonConsts.GeojsonKey;
import ark.dock.io.json.ArkDockJsonReaderAgent;
import dust.gen.DustGenUtils;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ArkDockGeojsonGeometryReaderAgent extends ArkDockJsonReaderAgent {

	GeojsonKey readingKey;

	GeojsonGeometryType typeGeom;

	ArkDockGeojsonGeometry geo;
	Stack<ArkDockGeojsonGeometry> readStack;

	int coordIdx;

	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {
		DustResultType ret = DustResultType.READ;
		boolean callSuper = true;

//		DustGenLog.log("geometry", action, ctx);

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
					ArkDockGeojsonGeometry g = readStack.pop();
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
