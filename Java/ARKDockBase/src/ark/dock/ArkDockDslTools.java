package ark.dock;

public interface ArkDockDslTools extends ArkDockConsts {

	public class DslGeometry implements ArkDockConsts {
		public final DustEntity unit;

		public final DustEntity typNative;

		public final DustEntity tagNativeType;
		public final DustEntity tagNativePoint;
		public final DustEntity tagNativePath;
		public final DustEntity tagNativePolygon;
		
		public final DustEntity memGeomPoint;
		public final DustEntity memGeomPolygon;
		public final DustEntity memGeomPolygons;
		public final DustEntity memGeomBBox;
		public final DustEntity memGeomBBoxMembers;
		
		

		public final DustEntity typInfo;
		
		public final DustEntity typPath; // requires collection of InfoData points
		public final DustEntity typArea; // requires Range
		public final DustEntity typImage; // requires Binary
		public final DustEntity typComposite; // requires collection of inclusions
		
		public final DustEntity typInclude; // requires ref to target, collection of parameters

		public final DustEntity memInfoData;

		public final DustEntity tagRole;
		public final DustEntity tagRolePlace;
		public final DustEntity tagRoleRotate;
		public final DustEntity tagRoleScale;

		public final DustEntity tagMeasure;
		public final DustEntity tagCartesianX;
		public final DustEntity tagCartesianY;
		public final DustEntity tagCartesianZ;
		
		public final DustEntity tagGcsLat;		
		public final DustEntity tagGcsLong;
		public final DustEntity tagGcsElev;
		
		public final DustEntity tagAngleTheta;

		public DslGeometry(ArkDockModelMeta meta) {
			unit = meta.getUnit("Geometry");

			typNative = meta.getType(unit, "Native");

			tagNativeType = meta.defineTag(unit, "NativeType", null);
			tagNativePoint = meta.defineTag(unit, "Point", tagNativeType);
			tagNativePath = meta.defineTag(unit, "Path", tagNativeType);
			tagNativePolygon = meta.defineTag(unit, "Polygon", tagNativeType);

			memGeomPoint = meta.getMember(typNative, "Point");
			memGeomPolygon = meta.getMember(typNative, "Polygon");
			memGeomPolygons = meta.getMember(typNative, "Polygons");
			memGeomBBox = meta.getMember(typNative, "BBox");
			memGeomBBoxMembers = meta.getMember(typNative, "BBoxMembers");
			
			
			typInfo = meta.getType(unit, "Info");
			typPath = meta.getType(unit, "Path");
			typArea = meta.getType(unit, "Area");
			typImage = meta.getType(unit, "Image");
			typComposite = meta.getType(unit, "Composite");

			typInclude = meta.getType(unit, "Include");

//			memInfoData = meta.getMember(typInfo, "Data");
			memInfoData = meta.defineMember(typInfo, "Data", DustValType.REAL, DustCollType.MAP);

			
			tagRole = meta.defineTag(unit, "Role", null);
			tagRolePlace = meta.defineTag(unit, "Place", tagRole);
			tagRoleRotate = meta.defineTag(unit, "Rotate", tagRole);
			tagRoleScale = meta.defineTag(unit, "Scale", tagRole);

			tagMeasure = meta.defineTag(unit, "Measure", null);
			tagCartesianX = meta.defineTag(unit, "CartesianX", tagMeasure);
			tagCartesianY = meta.defineTag(unit, "CartesianY", tagMeasure);
			tagCartesianZ = meta.defineTag(unit, "CartesianZ", tagMeasure);
			
			tagGcsLat = meta.defineTag(unit, "Latitude", tagMeasure);
			tagGcsLong = meta.defineTag(unit, "Longitude", tagMeasure);
			tagGcsElev = meta.defineTag(unit, "Elevation", tagMeasure);
			
			tagAngleTheta = meta.defineTag(unit, "Theta", tagMeasure);			
		}
	}

	public class DslText implements ArkDockConsts {
		public final DustEntity unit;

		public final DustEntity typText;

		public final DustEntity memTextName;
		public final DustEntity memTextDesc;

		public DslText(ArkDockModelMeta meta) {
			unit = meta.getUnit("Text");
			
			typText = meta.getType(unit, "Text");

			memTextName = meta.getMember(typText, "Name");
			memTextDesc = meta.getMember(typText, "Desc");
		}
	}

	public class DslNet implements ArkDockConsts {
		public final DustEntity unit;

		public final DustEntity typHost;
		public final DustEntity memHostName;
		public final DustEntity memHostIPv4;
		public final DustEntity memHostIPv6;
		
		public final DustEntity typService;
		public final DustEntity memServiceHost;
		public final DustEntity memServicePath;
		public final DustEntity memServicePort;
		
		public final DustEntity typClient;
		public final DustEntity memClientPath;
		public final DustEntity memClientMethod;
		
		public final DustEntity tagHttpMethod;
		public final DustEntity tagHttpMethodGET;
		public final DustEntity tagHttpMethodHEAD;
		public final DustEntity tagHttpMethodPOST;
		public final DustEntity tagHttpMethodPUT;
		public final DustEntity tagHttpMethodDELETE;
		public final DustEntity tagHttpMethodCONNECT;
		public final DustEntity tagHttpMethodOPTIONS;
		public final DustEntity tagHttpMethodTRACE;
		public final DustEntity tagHttpMethodPATCH;
				
		public DslNet(ArkDockModelMeta meta) {
			unit = meta.getUnit("Net");
			
			typHost = meta.getType(unit, "Host");
			memHostName = meta.defineMember(typHost, "Name", DustValType.RAW, DustCollType.ONE);
			memHostIPv4 = meta.defineMember(typHost, "IPv4", DustValType.RAW, DustCollType.ONE);
			memHostIPv6 = meta.defineMember(typHost, "IPv6", DustValType.RAW, DustCollType.ONE);
			
			typService = meta.getType(unit, "Service");
			memServiceHost = meta.defineMember(typService, "Host", DustValType.REF, DustCollType.ONE);
			memServicePath = meta.defineMember(typService, "Path", DustValType.RAW, DustCollType.ONE);
			memServicePort = meta.defineMember(typService, "Port", DustValType.INT, DustCollType.ONE);
			
			typClient = meta.getType(unit, "Client");
			memClientPath = meta.defineMember(typClient, "Path", DustValType.RAW, DustCollType.ONE);
			memClientMethod = meta.defineMember(typClient, "Method", DustValType.REF, DustCollType.ONE);
			
			tagHttpMethod = meta.defineTag(unit, "Method", null);
			tagHttpMethodGET = meta.defineTag(unit, "GET", tagHttpMethod);
			tagHttpMethodHEAD = meta.defineTag(unit, "HEAD", tagHttpMethod);
			tagHttpMethodPOST = meta.defineTag(unit, "POST", tagHttpMethod);
			tagHttpMethodPUT = meta.defineTag(unit, "PUT", tagHttpMethod);
			tagHttpMethodDELETE = meta.defineTag(unit, "DELETE", tagHttpMethod);
			tagHttpMethodCONNECT = meta.defineTag(unit, "CONNECT", tagHttpMethod);
			tagHttpMethodOPTIONS = meta.defineTag(unit, "OPTIONS", tagHttpMethod);
			tagHttpMethodTRACE = meta.defineTag(unit, "TRACE", tagHttpMethod);
			tagHttpMethodPATCH = meta.defineTag(unit, "PATCH", tagHttpMethod);
		}
	};

	public class DslGeneric implements ArkDockConsts {
		public final DustEntity unit;

		public final DustEntity typColl;
		public final DustEntity memCollMember;
		public final DustEntity memCollSize;
		public final DustEntity memCollCount;

		public final DustEntity typLink;
		public final DustEntity memLinkSource;
		public final DustEntity memLinkTarget;

		public final DustEntity typConnected;
		public final DustEntity memConnectedRequires;
		public final DustEntity memConnectedExtends;

		public final DustEntity typRange;
		public final DustEntity memMinInt;
		public final DustEntity memMaxInt;
		public final DustEntity memMinReal;
		public final DustEntity memMaxReal;
		public final DustEntity memMinRef;
		public final DustEntity memMaxRef;
		public final DustEntity memMinRaw;
		public final DustEntity memMaxRaw;

		public DslGeneric(ArkDockModelMeta meta) {
			unit = meta.getUnit("Generic");

			typColl = meta.getType(unit, "Collection");
			memCollMember = meta.defineMember(typColl, "Members", DustValType.REF, DustCollType.ARR);
			memCollSize = meta.defineMember(typColl, "Size", DustValType.INT, DustCollType.ONE);
			memCollCount = meta.defineMember(typColl, "Count", DustValType.INT, DustCollType.ONE);
			
			typLink = meta.getType(unit, "Link");
			memLinkSource = meta.defineMember(typLink, "Source", DustValType.REF, DustCollType.ONE);
			memLinkTarget = meta.defineMember(typLink, "Target", DustValType.REF, DustCollType.ONE);
			
			typConnected = meta.getType(unit, "Connected");
			memConnectedRequires = meta.defineMember(typConnected, "Requires", DustValType.REF, DustCollType.SET);
			memConnectedExtends = meta.defineMember(typConnected, "Extends", DustValType.REF, DustCollType.SET);

			typRange = meta.getType(unit, "Range");
			memMinInt = meta.defineMember(typRange, "MinInt", DustValType.INT, DustCollType.ONE);
			memMaxInt = meta.defineMember(typRange, "MaxInt", DustValType.REF, DustCollType.ONE);
			memMinReal = meta.defineMember(typRange, "MinReal", DustValType.REAL, DustCollType.ONE);
			memMaxReal = meta.defineMember(typRange, "MaxReal", DustValType.REAL, DustCollType.ONE);
			memMinRef = meta.defineMember(typRange, "MinRef", DustValType.REF, DustCollType.ONE);
			memMaxRef = meta.defineMember(typRange, "MaxRef", DustValType.REF, DustCollType.ONE);
			memMinRaw = meta.defineMember(typRange, "MinRaw", DustValType.RAW, DustCollType.ONE);
			memMaxRaw = meta.defineMember(typRange, "MaxRaw", DustValType.RAW, DustCollType.ONE);
		}
	}

}
