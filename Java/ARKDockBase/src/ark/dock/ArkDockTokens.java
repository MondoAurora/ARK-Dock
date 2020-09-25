package ark.dock;

public interface ArkDockTokens extends ArkDockConsts {

	String UNIT_ARK = "Ark";

	String TYPE_UNIT = "Unit";
	String TYPE_TYPE = "Type";
	String TYPE_MEMBER = "Member";
	String TYPE_ENTITY = "Entity";
	String TYPE_CONST = "Const";

	String MEMBER_MEMBER_OPTIONS = "Options";
	String MEMBER_MEMBER_VALTYPE = "ValType";
	String MEMBER_MEMBER_COLLTYPE = "CollType";

	String MEMBER_ENTITY_ID = "Id";
	String MEMBER_ENTITY_GLOBALID = "GlobalId";
	String MEMBER_ENTITY_PRIMARYTYPE = "PrimaryType";

	String CONST_TRUE = "True";
	String CONST_FALSE = "False";

	String CONST_COLLTYPE_ONE = "ONE";
	String CONST_COLLTYPE_ARR = "ARR";
	String CONST_COLLTYPE_SET = "SET";
	String CONST_COLLTYPE_MAP = "MAP";
	
	String CONST_VALTYPE_INT = "INT";
	String CONST_VALTYPE_REAL = "REAL";
	String CONST_VALTYPE_REF = "REF";
	String CONST_VALTYPE_RAW = "RAW";
	
	String TYPE_GEOM = "Geom";

	String MEMBER_GEOM_POINT = "Point";
	String MEMBER_GEOM_POLYGONS = "Polygons";
	String MEMBER_GEOM_BBOX = "BBox";
	String MEMBER_GEOM_BBOXMEMBERS = "BBoxMembers";

	String TYPE_TEXT = "Text";

	String MEMBER_TEXT_NAME = "Name";

	String TYPE_COLL = "Coll";

	String MEMBER_COLL_MEMBERS = "Members";

	public class Meta implements ArkDockConsts {

		public final DustEntity eUnitArk;

		public final DustEntity eTypeUnit;

		public final DustEntity eTypeType;

		public final DustEntity eTypeMember;
		public final DustEntity eMemberOptions;
		public final DustEntity eMemberCollType;
		public final DustEntity eMemberValType;

		public final DustEntity eTypeEntity;
		public final DustEntity eEntityId;
		public final DustEntity eEntityGlobalId;
		public final DustEntity eEntityPrimType;

		public final DustEntity eTypeConst;
		public final DustEntity eConstTrue;
		public final DustEntity eConstFalse;

		public final DustEntity eConstValtypeInt;
		public final DustEntity eConstValtypeReal;
		public final DustEntity eConstValtypeRef;
		public final DustEntity eConstValtypeRaw;

		public final DustEntity eConstColltypeOne;
		public final DustEntity eConstColltypeArr;
		public final DustEntity eConstColltypeSet;
		public final DustEntity eConstColltypeMap;

		public Meta(ArkDockModelMeta meta) {
			eUnitArk = meta.getBootEntity(ArkDockUtils.buildGlobalId(UNIT_ARK, TYPE_UNIT, UNIT_ARK));
			eTypeUnit = meta.getBootEntity(ArkDockUtils.buildGlobalId(UNIT_ARK, TYPE_TYPE, TYPE_UNIT));
			eTypeType = meta.getBootEntity(ArkDockUtils.buildGlobalId(UNIT_ARK, TYPE_TYPE, TYPE_TYPE));

			eTypeMember = meta.getBootEntity(ArkDockUtils.buildGlobalId(UNIT_ARK, TYPE_TYPE, TYPE_MEMBER));
			eMemberOptions = meta.getMember(eTypeMember, MEMBER_MEMBER_OPTIONS);
			eMemberCollType = meta.getMember(eTypeMember, MEMBER_MEMBER_COLLTYPE);
			eMemberValType = meta.getMember(eTypeMember, MEMBER_MEMBER_VALTYPE);

			eTypeEntity = meta.getBootEntity(ArkDockUtils.buildGlobalId(UNIT_ARK, TYPE_TYPE, TYPE_ENTITY));
			eEntityId = meta.getMember(eTypeEntity, MEMBER_ENTITY_ID);
			eEntityGlobalId = meta.getMember(eTypeEntity, MEMBER_ENTITY_GLOBALID);
			eEntityPrimType = meta.getMember(eTypeEntity, MEMBER_ENTITY_PRIMARYTYPE);

			eTypeConst = meta.getBootEntity(ArkDockUtils.buildGlobalId(UNIT_ARK, TYPE_TYPE, TYPE_CONST));
			eConstFalse = meta.getMember(eTypeConst, CONST_FALSE);
			eConstTrue = meta.getMember(eTypeConst, CONST_TRUE);
			
			eConstValtypeInt = meta.getMember(eTypeConst, CONST_VALTYPE_INT);
			eConstValtypeReal = meta.getMember(eTypeConst, CONST_VALTYPE_REAL);
			eConstValtypeRef = meta.getMember(eTypeConst, CONST_VALTYPE_REF);
			eConstValtypeRaw = meta.getMember(eTypeConst, CONST_VALTYPE_RAW);

			eConstColltypeOne = meta.getMember(eTypeConst, CONST_COLLTYPE_ONE);
			eConstColltypeArr = meta.getMember(eTypeConst, CONST_COLLTYPE_ARR);
			eConstColltypeSet = meta.getMember(eTypeConst, CONST_COLLTYPE_SET);
			eConstColltypeMap = meta.getMember(eTypeConst, CONST_COLLTYPE_MAP);

			meta.initBootEntity(eUnitArk, eTypeUnit, this);

			meta.initBootEntity(eTypeType, eTypeType, this);
			meta.initBootEntity(eTypeUnit, eTypeType, this);
			meta.initBootEntity(eTypeMember, eTypeType, this);
			meta.initBootEntity(eTypeEntity, eTypeType, this);
			meta.initBootEntity(eTypeConst, eTypeType, this);

			meta.initBootMember(eMemberOptions, this, DustValType.REF, DustCollType.SET);
			meta.initBootMember(eMemberCollType, this, DustValType.REF, DustCollType.ONE);
			meta.initBootMember(eMemberValType, this, DustValType.REF, DustCollType.ONE);
			
			meta.initBootMember(eEntityId, this, DustValType.RAW, DustCollType.ONE);
			meta.initBootMember(eEntityGlobalId, this, DustValType.RAW, DustCollType.ONE);
			meta.initBootMember(eEntityPrimType, this, DustValType.REF, DustCollType.ONE);

			meta.initBootEntity(eConstFalse, eTypeConst, this);
			meta.initBootEntity(eConstTrue, eTypeConst, this);
			
			meta.initBootEntity(eConstValtypeInt, eTypeConst, this);
			meta.initBootEntity(eConstValtypeReal, eTypeConst, this);
			meta.initBootEntity(eConstValtypeRef, eTypeConst, this);
			meta.initBootEntity(eConstValtypeRaw, eTypeConst, this);
			meta.initBootEntity(eConstColltypeOne, eTypeConst, this);
			meta.initBootEntity(eConstColltypeArr, eTypeConst, this);
			meta.initBootEntity(eConstColltypeSet, eTypeConst, this);
			meta.initBootEntity(eConstColltypeMap, eTypeConst, this);
		}
	}

	public class Geometry implements ArkDockConsts {
		public final DustEntity eTypeGeom;

		public final DustEntity eGeomPoint;
		public final DustEntity eGeomPolygons;
		public final DustEntity eGeomBBox;
		public final DustEntity eGeomBBoxMembers;

		public Geometry(ArkDockModelMeta meta) {
			eTypeGeom = meta.getType(meta.tokMeta.eUnitArk, TYPE_GEOM);

			eGeomPoint = meta.getMember(eTypeGeom, MEMBER_GEOM_POINT);
			eGeomPolygons = meta.getMember(eTypeGeom, MEMBER_GEOM_POLYGONS);
			eGeomBBox = meta.getMember(eTypeGeom, MEMBER_GEOM_BBOX);
			eGeomBBoxMembers = meta.getMember(eTypeGeom, MEMBER_GEOM_BBOXMEMBERS);
		}
	}

	public class Text implements ArkDockConsts {
		public final DustEntity eTypeText;

		public final DustEntity eTextName;

		public Text(ArkDockModelMeta meta) {
			eTypeText = meta.getType(meta.tokMeta.eUnitArk, TYPE_TEXT);

			eTextName = meta.getMember(eTypeText, MEMBER_TEXT_NAME);
		}
	}

	public class Gen implements ArkDockConsts {
		public final DustEntity eTypeColl;

		public final DustEntity eCollMember;

		public Gen(ArkDockModelMeta meta) {
			eTypeColl = meta.getType(meta.tokMeta.eUnitArk, TYPE_COLL);

			eCollMember = meta.getMember(eTypeColl, MEMBER_COLL_MEMBERS);
		}
	}

}
