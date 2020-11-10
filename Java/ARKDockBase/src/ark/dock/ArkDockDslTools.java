package ark.dock;

public interface ArkDockDslTools extends ArkDockConsts {

	public class DslGeometry {
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
		
		public final DustEntity memGeomBBox2D;
		

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

		public DslGeometry() {
			ArkDockDslBuilder meta = ArkDock.getDslBuilder("Geometry");
			
			unit = meta.getUnit();

			typNative = meta.getType("Native");

			tagNativeType = meta.defineTag("NativeType", null);
			tagNativePoint = meta.defineTag("Point", tagNativeType);
			tagNativePath = meta.defineTag("Path", tagNativeType);
			tagNativePolygon = meta.defineTag("Polygon", tagNativeType);

			memGeomPoint = meta.getMember(typNative, "Point");
			memGeomPolygon = meta.getMember(typNative, "Polygon");
			memGeomPolygons = meta.getMember(typNative, "Polygons");
			memGeomBBox = meta.getMember(typNative, "BBox");
			memGeomBBoxMembers = meta.getMember(typNative, "BBoxMembers");
			
			memGeomBBox2D = meta.getMember(typNative, "BBox2D");
			
			
			typInfo = meta.getType("Info");
			typPath = meta.getType("Path");
			typArea = meta.getType("Area");
			typImage = meta.getType("Image");
			typComposite = meta.getType("Composite");

			typInclude = meta.getType("Include");

//			memInfoData = meta.getMember(typInfo, "Data");
			memInfoData = meta.defineMember(typInfo, "Data", DustValType.REAL, DustCollType.MAP);

			
			tagRole = meta.defineTag("Role", null);
			tagRolePlace = meta.defineTag("Place", tagRole);
			tagRoleRotate = meta.defineTag("Rotate", tagRole);
			tagRoleScale = meta.defineTag("Scale", tagRole);

			tagMeasure = meta.defineTag("Measure", null);
			tagCartesianX = meta.defineTag("CartesianX", tagMeasure);
			tagCartesianY = meta.defineTag("CartesianY", tagMeasure);
			tagCartesianZ = meta.defineTag("CartesianZ", tagMeasure);
			
			tagGcsLat = meta.defineTag("Latitude", tagMeasure);
			tagGcsLong = meta.defineTag("Longitude", tagMeasure);
			tagGcsElev = meta.defineTag("Elevation", tagMeasure);
			
			tagAngleTheta = meta.defineTag("Theta", tagMeasure);			
		}
	}

	public class DslText implements ArkDockConsts {
		public final DustEntity unit;

		public final DustEntity typText;

		public final DustEntity memTextName;
		public final DustEntity memTextDesc;

		public DslText() {
			ArkDockDslBuilder meta = ArkDock.getDslBuilder("Text");
			
			unit = meta.getUnit();
			
			typText = meta.getType("Text");

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
				
		public DslNet() {
			ArkDockDslBuilder meta = ArkDock.getDslBuilder("Net");
			unit = meta.getUnit();

			typHost = meta.getType("Host");
			memHostName = meta.defineMember(typHost, "Name", DustValType.RAW, DustCollType.ONE);
			memHostIPv4 = meta.defineMember(typHost, "IPv4", DustValType.RAW, DustCollType.ONE);
			memHostIPv6 = meta.defineMember(typHost, "IPv6", DustValType.RAW, DustCollType.ONE);
			
			typService = meta.getType("Service");
			memServiceHost = meta.defineMember(typService, "Host", DustValType.REF, DustCollType.ONE);
			memServicePath = meta.defineMember(typService, "Path", DustValType.RAW, DustCollType.ONE);
			memServicePort = meta.defineMember(typService, "Port", DustValType.INT, DustCollType.ONE);
			
			typClient = meta.getType("Client");
			memClientPath = meta.defineMember(typClient, "Path", DustValType.RAW, DustCollType.ONE);
			memClientMethod = meta.defineMember(typClient, "Method", DustValType.REF, DustCollType.ONE);
			
			tagHttpMethod = meta.defineTag("Method", null);
			tagHttpMethodGET = meta.defineTag("GET", tagHttpMethod);
			tagHttpMethodHEAD = meta.defineTag("HEAD", tagHttpMethod);
			tagHttpMethodPOST = meta.defineTag("POST", tagHttpMethod);
			tagHttpMethodPUT = meta.defineTag("PUT", tagHttpMethod);
			tagHttpMethodDELETE = meta.defineTag("DELETE", tagHttpMethod);
			tagHttpMethodCONNECT = meta.defineTag("CONNECT", tagHttpMethod);
			tagHttpMethodOPTIONS = meta.defineTag("OPTIONS", tagHttpMethod);
			tagHttpMethodTRACE = meta.defineTag("TRACE", tagHttpMethod);
			tagHttpMethodPATCH = meta.defineTag("PATCH", tagHttpMethod);
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

		public final DustEntity tagSingle;
		public final DustEntity tagAbstract;

		public DslGeneric() {
			ArkDockDslBuilder meta = ArkDock.getDslBuilder(ArkDockBootConsts.UNITNAME_GENERIC);
			unit = meta.getUnit();

			typColl = meta.getType("Collection");
			memCollMember = meta.defineMember(typColl, "Members", DustValType.REF, DustCollType.ARR);
			memCollSize = meta.defineMember(typColl, "Size", DustValType.INT, DustCollType.ONE);
			memCollCount = meta.defineMember(typColl, "Count", DustValType.INT, DustCollType.ONE);
			
			typLink = meta.getType("Link");
			memLinkSource = meta.defineMember(typLink, "Source", DustValType.REF, DustCollType.ONE);
			memLinkTarget = meta.defineMember(typLink, "Target", DustValType.REF, DustCollType.ONE);
			
			typConnected = meta.getType("Connected");
			memConnectedRequires = meta.defineMember(typConnected, "Requires", DustValType.REF, DustCollType.SET);
			memConnectedExtends = meta.defineMember(typConnected, "Extends", DustValType.REF, DustCollType.SET);

			typRange = meta.getType("Range");
			memMinInt = meta.defineMember(typRange, "MinInt", DustValType.INT, DustCollType.ONE);
			memMaxInt = meta.defineMember(typRange, "MaxInt", DustValType.REF, DustCollType.ONE);
			memMinReal = meta.defineMember(typRange, "MinReal", DustValType.REAL, DustCollType.ONE);
			memMaxReal = meta.defineMember(typRange, "MaxReal", DustValType.REAL, DustCollType.ONE);
			memMinRef = meta.defineMember(typRange, "MinRef", DustValType.REF, DustCollType.ONE);
			memMaxRef = meta.defineMember(typRange, "MaxRef", DustValType.REF, DustCollType.ONE);
			memMinRaw = meta.defineMember(typRange, "MinRaw", DustValType.RAW, DustCollType.ONE);
			memMaxRaw = meta.defineMember(typRange, "MaxRaw", DustValType.RAW, DustCollType.ONE);
			
			tagAbstract = meta.defineTag(ArkDockBootConsts.TAGNAME_ABSTRACT, null);
			tagSingle = meta.defineTag(ArkDockBootConsts.TAGNAME_SINGLE, null);

		}
	}

}
