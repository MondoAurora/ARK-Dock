package ark.dock;

public interface ArkDockTokensTools extends ArkDockConsts {

	public class Geometry implements ArkDockConsts {
		public final DustEntity eUnit;

		public final DustEntity eTypeNative;

		public final DustEntity tagNativeType;
		public final DustEntity tagNativePoint;
		public final DustEntity tagNativePath;
		public final DustEntity tagNativePolygon;
		
		public final DustEntity eGeomPoint;
		public final DustEntity eGeomPolygon;
		public final DustEntity eGeomPolygons;
		public final DustEntity eGeomBBox;
		public final DustEntity eGeomBBoxMembers;
		
		

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

		public Geometry(ArkDockModelMeta meta) {
			eUnit = meta.getUnit("Geometry");

			eTypeNative = meta.getType(eUnit, "Native");

			tagNativeType = meta.defineTag(eUnit, "NativeType", null);
			tagNativePoint = meta.defineTag(eUnit, "Point", tagNativeType);
			tagNativePath = meta.defineTag(eUnit, "Path", tagNativeType);
			tagNativePolygon = meta.defineTag(eUnit, "Polygon", tagNativeType);

			eGeomPoint = meta.getMember(eTypeNative, "Point");
			eGeomPolygon = meta.getMember(eTypeNative, "Polygon");
			eGeomPolygons = meta.getMember(eTypeNative, "Polygons");
			eGeomBBox = meta.getMember(eTypeNative, "BBox");
			eGeomBBoxMembers = meta.getMember(eTypeNative, "BBoxMembers");
			
			
			typInfo = meta.getType(eUnit, "Info");
			typPath = meta.getType(eUnit, "Path");
			typArea = meta.getType(eUnit, "Area");
			typImage = meta.getType(eUnit, "Image");
			typComposite = meta.getType(eUnit, "Composite");

			typInclude = meta.getType(eUnit, "Include");

//			memInfoData = meta.getMember(typInfo, "Data");
			memInfoData = meta.defineMember(typInfo, "Data", DustValType.REAL, DustCollType.MAP);

			
			tagRole = meta.defineTag(eUnit, "Role", null);
			tagRolePlace = meta.defineTag(eUnit, "Place", tagRole);
			tagRoleRotate = meta.defineTag(eUnit, "Rotate", tagRole);
			tagRoleScale = meta.defineTag(eUnit, "Scale", tagRole);

			tagMeasure = meta.defineTag(eUnit, "Measure", null);
			tagCartesianX = meta.defineTag(eUnit, "CartesianX", tagMeasure);
			tagCartesianY = meta.defineTag(eUnit, "CartesianY", tagMeasure);
			tagCartesianZ = meta.defineTag(eUnit, "CartesianZ", tagMeasure);
			
			tagGcsLat = meta.defineTag(eUnit, "Latitude", tagMeasure);
			tagGcsLong = meta.defineTag(eUnit, "Longitude", tagMeasure);
			tagGcsElev = meta.defineTag(eUnit, "Elevation", tagMeasure);
			
			tagAngleTheta = meta.defineTag(eUnit, "Theta", tagMeasure);			
		}
	}

	public class Text implements ArkDockConsts {
		public final DustEntity eUnit;

		public final DustEntity eTypeText;

		public final DustEntity eTextName;

		public Text(ArkDockModelMeta meta) {
			eUnit = meta.getUnit("Text");
			
			eTypeText = meta.getType(eUnit, "Text");

			eTextName = meta.getMember(eTypeText, "Name");
		}
	}

	public class Net implements ArkDockConsts {
		public final DustEntity eUnit;

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
				
		public Net(ArkDockModelMeta meta) {
			eUnit = meta.getUnit("Net");
			
			typHost = meta.getType(eUnit, "Host");
			memHostName = meta.defineMember(typHost, "Name", DustValType.RAW, DustCollType.ONE);
			memHostIPv4 = meta.defineMember(typHost, "IPv4", DustValType.RAW, DustCollType.ONE);
			memHostIPv6 = meta.defineMember(typHost, "IPv6", DustValType.RAW, DustCollType.ONE);
			
			typService = meta.getType(eUnit, "Service");
			memServiceHost = meta.defineMember(typService, "Host", DustValType.REF, DustCollType.ONE);
			memServicePath = meta.defineMember(typService, "Path", DustValType.RAW, DustCollType.ONE);
			memServicePort = meta.defineMember(typService, "Port", DustValType.INT, DustCollType.ONE);
			
			typClient = meta.getType(eUnit, "Client");
			memClientPath = meta.defineMember(typClient, "Path", DustValType.RAW, DustCollType.ONE);
			memClientMethod = meta.defineMember(typClient, "Method", DustValType.REF, DustCollType.ONE);
			
			tagHttpMethod = meta.defineTag(eUnit, "Method", null);
			tagHttpMethodGET = meta.defineTag(eUnit, "GET", tagHttpMethod);
			tagHttpMethodHEAD = meta.defineTag(eUnit, "HEAD", tagHttpMethod);
			tagHttpMethodPOST = meta.defineTag(eUnit, "POST", tagHttpMethod);
			tagHttpMethodPUT = meta.defineTag(eUnit, "PUT", tagHttpMethod);
			tagHttpMethodDELETE = meta.defineTag(eUnit, "DELETE", tagHttpMethod);
			tagHttpMethodCONNECT = meta.defineTag(eUnit, "CONNECT", tagHttpMethod);
			tagHttpMethodOPTIONS = meta.defineTag(eUnit, "OPTIONS", tagHttpMethod);
			tagHttpMethodTRACE = meta.defineTag(eUnit, "TRACE", tagHttpMethod);
			tagHttpMethodPATCH = meta.defineTag(eUnit, "PATCH", tagHttpMethod);
		}
	};

	public class Generic implements ArkDockConsts {
		public final DustEntity eUnit;

		public final DustEntity eTypeColl;
		public final DustEntity eCollMember;
		public final DustEntity eCollSize;
		public final DustEntity eCollCount;

		public final DustEntity eTypeLink;
		public final DustEntity eLinkSource;
		public final DustEntity eLinkTarget;

		public final DustEntity eTypeConnected;
		public final DustEntity eConnectedRequires;
		public final DustEntity eConnectedExtends;

		public final DustEntity typRange;
		public final DustEntity memMinInt;
		public final DustEntity memMaxInt;
		public final DustEntity memMinReal;
		public final DustEntity memMaxReal;
		public final DustEntity memMinRef;
		public final DustEntity memMaxRef;
		public final DustEntity memMinRaw;
		public final DustEntity memMaxRaw;

		public Generic(ArkDockModelMeta meta) {
			eUnit = meta.getUnit("Generic");

			eTypeColl = meta.getType(eUnit, "Collection");
			eCollMember = meta.defineMember(eTypeColl, "Members", DustValType.REF, DustCollType.ARR);
			eCollSize = meta.defineMember(eTypeColl, "Size", DustValType.INT, DustCollType.ONE);
			eCollCount = meta.defineMember(eTypeColl, "Count", DustValType.INT, DustCollType.ONE);
			
			eTypeLink = meta.getType(eUnit, "Link");
			eLinkSource = meta.defineMember(eTypeLink, "Source", DustValType.REF, DustCollType.ONE);
			eLinkTarget = meta.defineMember(eTypeLink, "Target", DustValType.REF, DustCollType.ONE);
			
			eTypeConnected = meta.getType(eUnit, "Connected");
			eConnectedRequires = meta.defineMember(eTypeConnected, "Requires", DustValType.REF, DustCollType.SET);
			eConnectedExtends = meta.defineMember(eTypeConnected, "Extends", DustValType.REF, DustCollType.SET);

			typRange = meta.getType(eUnit, "Range");
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
