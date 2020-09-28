package ark.dock;

public interface ArkDockTokensMind extends ArkDockConsts {
	
	public class Model {
		public final DustEntity eUnit;

		public final DustEntity eTypeUnit;

		public final DustEntity eTypeEntity;
		public final DustEntity eEntityUnit;
		public final DustEntity eEntityId;
		public final DustEntity eEntityGlobalId;
		public final DustEntity eEntityPrimType;

		public Model(ArkDockModelMeta meta) {
			eUnit = meta.getBootEntity(ArkDockUtils.buildGlobalId("Model", "Unit", "Model"));
			
			eTypeUnit = meta.getBootEntity(ArkDockUtils.buildGlobalId("Model", "Type", "Unit"));
			eTypeEntity = meta.getBootEntity(ArkDockUtils.buildGlobalId("Model", "Type", "Entity"));
			
			meta.factTypeDef.get(eTypeUnit, eUnit);
			meta.factTypeDef.get(eTypeEntity, eUnit);

			eEntityUnit = meta.getMember(eTypeEntity, "Unit");
			eEntityId = meta.getMember(eTypeEntity, "Id");
			eEntityGlobalId = meta.getMember(eTypeEntity, "GlobalId");
			eEntityPrimType = meta.getMember(eTypeEntity, "PrimaryType");
		}
	}
	
	public class Idea {
		public final DustEntity eUnit;

		public final DustEntity eTypeType;

		public final DustEntity eTypeMember;
		public final DustEntity eMemberOptions;
		public final DustEntity eMemberCollType;
		public final DustEntity eMemberValType;

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

		public Idea(ArkDockModelMeta meta) {
			eUnit = meta.getBootEntity(ArkDockUtils.buildGlobalId("Idea", "Unit", "Idea"));

			eTypeType = meta.getBootEntity(ArkDockUtils.buildGlobalId("Idea", "Type", "Type"));
			eTypeMember = meta.getBootEntity(ArkDockUtils.buildGlobalId("Idea", "Type", "Member"));

			meta.factTypeDef.get(eTypeType, eUnit);
			meta.factTypeDef.get(eTypeMember, eUnit);
			meta.eTypeType = eTypeType;
			meta.eTypeMember = eTypeMember;
			
			eMemberOptions = meta.getMember(eTypeMember, "Options");
			eMemberCollType = meta.getMember(eTypeMember, "CollType");
			eMemberValType = meta.getMember(eTypeMember, "ValType");

			eTypeConst = meta.getBootEntity(ArkDockUtils.buildGlobalId("Idea", "Type", "Const"));
			eConstFalse = meta.getEntity(eUnit, eTypeConst, "False", true);
			eConstTrue = meta.getEntity(eUnit, eTypeConst, "True", true);
			
			eConstValtypeInt = meta.getEntity(eUnit, eTypeConst, "Int", true);
			eConstValtypeReal = meta.getEntity(eUnit, eTypeConst, "Real", true);
			eConstValtypeRef = meta.getEntity(eUnit, eTypeConst, "Ref", true);
			eConstValtypeRaw = meta.getEntity(eUnit, eTypeConst, "Raw", true);

			eConstColltypeOne = meta.getEntity(eUnit, eTypeConst, "One", true);
			eConstColltypeArr = meta.getEntity(eUnit, eTypeConst, "Arr", true);
			eConstColltypeSet = meta.getEntity(eUnit, eTypeConst, "Set", true);
			eConstColltypeMap = meta.getEntity(eUnit, eTypeConst, "Map", true);
		}
	}
}
