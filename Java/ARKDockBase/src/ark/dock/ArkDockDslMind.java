package ark.dock;

public interface ArkDockDslMind extends ArkDockConsts {
	
	public class Model {
		public final DustEntity eUnit;

		public final DustEntity eTypeUnit;

		public final DustEntity eTypeEntity;
		public final DustEntity eEntityUnit;
		public final DustEntity eEntityId;
		public final DustEntity eEntityGlobalId;
		public final DustEntity eEntityPrimType;
		public final DustEntity eEntityOwner;
		public final DustEntity eEntityTags;

		public Model(ArkDockModelMeta meta) {
			eUnit = meta.getBootEntity("Model", "Unit", "Model");
			
			eTypeUnit = meta.getBootEntity("Model", "Type", "Unit");
			eTypeEntity = meta.getBootEntity("Model", "Type", "Entity");
			
			meta.factTypeDef.get(eTypeUnit, eUnit);
			meta.factTypeDef.get(eTypeEntity, eUnit);

			eEntityUnit = meta.getMember(eTypeEntity, "Unit");
			eEntityId = meta.getMember(eTypeEntity, "Id");
			eEntityGlobalId = meta.getMember(eTypeEntity, "GlobalId");
			eEntityPrimType = meta.getMember(eTypeEntity, "PrimaryType");
			eEntityOwner = meta.getMember(eTypeEntity, "Owner");
			eEntityTags = meta.getMember(eTypeEntity, "Tags");
		}
	}
	
	public class Idea {
		public final DustEntity eUnit;

		public final DustEntity eTypeType;
		
		public final DustEntity eTypeAgent;
		public final DustEntity eAgentUpdates;

		public final DustEntity eTypeMember;
		public final DustEntity eMemberOptions;
		public final DustEntity eMemberCollType;
		public final DustEntity eMemberValType;

		public final DustEntity eTypeTag;

		public final DustEntity eTypeConst;
		
		
		public final DustEntity tagBoolean;
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
			eUnit = meta.getBootEntity("Idea", "Unit", "Idea");

			eTypeType = meta.getBootEntity("Idea", "Type", "Type");
			eTypeAgent = meta.getBootEntity("Idea", "Type", "Agent");
			eTypeMember = meta.getBootEntity("Idea", "Type", "Member");
			eTypeTag = meta.getBootEntity("Idea", "Type", "Tag");

			meta.factTypeDef.get(eTypeType, eUnit);
			meta.factTypeDef.get(eTypeAgent, eUnit);
			meta.factTypeDef.get(eTypeMember, eUnit);
			meta.factTypeDef.get(eTypeTag, eUnit);
			
			meta.eTypeType = eTypeType;
			meta.eTypeMember = eTypeMember;
			meta.eTypeTag = eTypeTag;
			
			eMemberOptions = meta.getMember(eTypeMember, "Options");
			eMemberCollType = meta.getMember(eTypeMember, "CollType");
			eMemberValType = meta.getMember(eTypeMember, "ValType");

			eAgentUpdates = meta.getMember(eTypeAgent, "Updates");

			eTypeConst = meta.getBootEntity("Idea", "Type", "Const");
			
			tagBoolean = meta.defineTag(eUnit, "Boolean", null);
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
	
	public class Narrative {
		public final DustEntity eUnit;

		public Narrative(ArkDockModelMeta meta) {
			eUnit = meta.getUnit("Narrative");
		}
	}
	
	public class Dialog {
		public final DustEntity eUnit;

		public final DustEntity typAction;

		public final DustEntity memActionResponse;

		public Dialog(ArkDockModelMeta meta) {
			eUnit = meta.getUnit("Dialog");
			
			typAction = meta.getType(eUnit, "Action");

			memActionResponse = meta.getMember(typAction, "Response");
		}
	}
	
}
