package ark.dock;

public interface ArkDockTokensTools extends ArkDockConsts {

	String TYPE_COLL = "Coll";

	String MEMBER_COLL_MEMBERS = "Members";

	public class Geometry implements ArkDockConsts {
		public final DustEntity eUnit;

		public final DustEntity eTypeNative;

		public final DustEntity eGeomPoint;
		public final DustEntity eGeomPolygons;
		public final DustEntity eGeomBBox;
		public final DustEntity eGeomBBoxMembers;

		public Geometry(ArkDockModelMeta meta) {
			eUnit = meta.getUnit("Geometry");

			eTypeNative = meta.getType(eUnit, "Native");

			eGeomPoint = meta.getMember(eTypeNative, "Point");
			eGeomPolygons = meta.getMember(eTypeNative, "Polygons");
			eGeomBBox = meta.getMember(eTypeNative, "BBox");
			eGeomBBoxMembers = meta.getMember(eTypeNative, "BBoxMembers");
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

	public class Generic implements ArkDockConsts {
		public final DustEntity eUnit;

		public final DustEntity eTypeColl;

		public final DustEntity eCollMember;

		public Generic(ArkDockModelMeta meta) {
			eUnit = meta.getUnit("Generic");

			eTypeColl = meta.getType(eUnit, "Collection");

			eCollMember = meta.getMember(eTypeColl, "Members");
		}
	}

}
