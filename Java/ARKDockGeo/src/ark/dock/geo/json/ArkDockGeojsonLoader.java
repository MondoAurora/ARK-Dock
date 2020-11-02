package ark.dock.geo.json;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ark.dock.ArkDock;
import ark.dock.ArkDockConsts;
import ark.dock.ArkDockDsl;
import ark.dock.ArkDockDslBuilder;
import ark.dock.ArkDockUnit;
import ark.dock.ArkDockUtils;
import ark.dock.geo.json.ArkDockGeojson2D.GeojsonBuilder2DDouble;
import ark.dock.geo.json.ArkDockGeojsonConsts.GeojsonKey;
import ark.dock.geo.json.ArkDockGeojsonConsts.GeojsonObjectArray;
import ark.dock.geo.json.ArkDockGeojsonConsts.GeojsonObjectSource;
import ark.dock.geo.json.ArkDockGeojsonConsts.GeojsonType;
import dust.gen.DustGenLog;
import dust.gen.DustGenTranslator;

public class ArkDockGeojsonLoader implements ArkDockConsts, ArkDockDsl {
	enum LoadSpecKey {
		PrimaryType, EntityId
	}

	DustGenTranslator<ArkDockGeojsonLoader.LoadSpecKey, String> specKeys = new DustGenTranslator<ArkDockGeojsonLoader.LoadSpecKey, String>();
	ArkDockUtils.TokenSplitter ts = new ArkDockUtils.TokenSplitter();

//	private final ArkDockDslBuilder modMeta;
	private final ArkDockUnit unit;
	
//	private final DustEntity eMainUnit;
	
	private final DslIdea dslIdea;
	private final DslModel dslModel;
	private final DslGeneric dslGeneric;
	private final DslNative dslNative;
	private final DslGeometry dslGeo;
	
	private DustEntity eContainer;
	
	public ArkDockGeojsonLoader(ArkDockUnit unit_) {
//	public ArkDockGeojsonLoader() {
//		this.eMainUnit = eMainUnit_;
//		this.modMain = modMain_;
		this.unit = unit_;
		
//		this.modMeta = modMain.getMeta();

		dslIdea = ArkDock.getDsl(DslIdea.class);
		dslModel = ArkDock.getDsl(DslModel.class);
		dslGeo = ArkDock.getDsl(DslGeometry.class);
		dslGeneric = ArkDock.getDsl(DslGeneric.class);
		dslNative = ArkDock.getDsl(DslNative.class);
		
		specKeys.add(LoadSpecKey.PrimaryType, ArkDock.getGlobalId(dslModel.memEntityPrimaryType));
		specKeys.add(LoadSpecKey.EntityId, ArkDock.getGlobalId(dslModel.memEntityId));
	}

	@SuppressWarnings("unchecked")
	private void processGeoJSONFeature(Map<GeojsonKey, Object> feature) {

		if ( !feature.isEmpty() ) {
			GeojsonType gjType = (GeojsonType) feature.get(GeojsonKey.type);

			if ( null == gjType ) {
				DustGenLog.log(DustEventLevel.WARNING, "No type specified", feature.toString());
				return;
			}
			if ( GeojsonType.FeatureCollection == gjType ) {
				DustGenLog.log(DustEventLevel.INFO, "Skipping", gjType, feature.toString());
				return;
			}

			Map<String, Object> props = (Map<String, Object>) feature.get(GeojsonKey.properties);

			String id = (String) props.get(specKeys.getRight(LoadSpecKey.EntityId));
			
			String strType = (String) props.get(specKeys.getRight(LoadSpecKey.PrimaryType));
			DustEntity eType = ArkDock.getByGlobalId(strType);
//			ts.split(strType);
//			DustEntity eType = ts.resolvePrimaryType(modMeta, null);
//
			DustEntity eFeature = unit.getEntity(eType, id, true);

			DustEntity eMember;

			for (Map.Entry<String, Object> p : props.entrySet()) {
				Object val = p.getValue();

				if ( null == val ) {
					continue;
				}

				String key = p.getKey();
				if ( specKeys.contains(key) ) {
					continue;
				}
				
				ts.split(key);
				ArkDockDslBuilder dslBuilder = ArkDock.getDslBuilder(ts.getSegment(TokenSegment.UNIT));
				eType = dslBuilder.getType(ts.getSegment(TokenSegment.TYPE));
				eMember = dslBuilder.getMember(eType, ts.getSegment(TokenSegment.ID));

				ArkDock.access(DustDialogCmd.SET, eFeature, eMember, val, null);
			}

			Object geometry = feature.get(GeojsonKey.coordinates);

			switch ( gjType ) {
			case Point:
				setNative(eFeature, true, dslGeo.tagNativePoint, geometry);
				break;
			case MultiPoint:
				setNative(eFeature, false, dslGeo.tagNativePoint, geometry);
				break;
			case LineString:
				setNative(eFeature, true, dslGeo.tagNativePath, geometry);
				break;
			case MultiLineString:
				setNative(eFeature, false, dslGeo.tagNativePath, geometry);
				break;
			case Polygon:
				setNative(eFeature, true, dslGeo.tagNativePolygon, geometry);
				break;
			case MultiPolygon:
				setNative(eFeature, false, dslGeo.tagNativePolygon, geometry);
				break;
			default:
				DustGenLog.log(DustEventLevel.WARNING, "Geometry not processed", eFeature, gjType, geometry);
				break;
			}
			
			if ( null != eContainer ) {
				ArkDock.access(DustDialogCmd.SET, eContainer, dslGeneric.memCollMember, eFeature, KEY_APPEND);
			}
		}
	}

	private void setNative(DustEntity eFeature, boolean isSingle, DustEntity geoValType, Object geometry) {
		ArkDock.access(DustDialogCmd.SET, eFeature, dslNative.memNativeValType, geoValType, null);
		ArkDock.access(DustDialogCmd.SET, eFeature, dslNative.memNativeCollType,
				isSingle ? dslIdea.tagColltypeOne : dslIdea.tagColltypeArr, null);
		if ( isSingle ) {
			ArkDock.access(DustDialogCmd.SET, eFeature, dslNative.memNativeValueOne, geometry, null);
		} else {
			for (Object p : (GeojsonObjectArray) geometry) {
				ArkDock.access(DustDialogCmd.ADD, eFeature, dslNative.memNativeValueArr, p, KEY_APPEND);
			}
		}
	}
	

	public void load(String mapName, DustEntity eContainer) throws Exception {
		this.eContainer = eContainer;
		
		GeojsonBuilder2DDouble bd = new GeojsonBuilder2DDouble(false);

		Map<GeojsonKey, Object> feature = new HashMap<>();
		GeojsonObjectSource obSrc = new GeojsonObjectSource() {
			@Override
			public Map<GeojsonKey, Object> getObToFill() {
				processGeoJSONFeature(feature);
				feature.clear();
				return feature;
			}
		};

		ArkDockGeojsonParser.parse(new File(mapName + ".geojson"), bd, obSrc);
		processGeoJSONFeature(feature);
	}
}