package ark.dock;

public interface ArkDockDslMind extends ArkDockConsts {
	
	public class DslModel {
		public final DustEntity unit;

		public final DustEntity typUnit;

		public final DustEntity typEntity;
		public final DustEntity memEntityUnit;
		public final DustEntity memEntityId;
		public final DustEntity memEntityGlobalId;
		public final DustEntity memEntityPrimType;
		public final DustEntity memEntityOwner;
		public final DustEntity memEntityTags;

		public DslModel(ArkDockModelMeta meta) {
			unit = meta.getBootEntity("Model", "Unit", "Model");
			
			typUnit = meta.getBootEntity("Model", "Type", "Unit");
			typEntity = meta.getBootEntity("Model", "Type", "Entity");
			
			meta.factTypeDef.get(typUnit, unit);
			meta.factTypeDef.get(typEntity, unit);

			memEntityUnit = meta.getMember(typEntity, "Unit");
			memEntityId = meta.getMember(typEntity, "Id");
			memEntityGlobalId = meta.getMember(typEntity, "GlobalId");
			memEntityPrimType = meta.getMember(typEntity, "PrimaryType");
			memEntityOwner = meta.getMember(typEntity, "Owner");
			memEntityTags = meta.getMember(typEntity, "Tags");
		}
	}
	
	public class DslIdea {
		public final DustEntity unit;

		public final DustEntity typType;
		
		public final DustEntity typAgent;
		public final DustEntity eAgentUpdates;

		public final DustEntity typMember;
		public final DustEntity memMemberOptions;
		public final DustEntity eMemberCollType;
		public final DustEntity eMemberValType;

		public final DustEntity typTag;

		public final DustEntity typConst;
		
		
		public final DustEntity tagBool;
		public final DustEntity tagBoolTrue;
		public final DustEntity tagBoolFalse;

		public final DustEntity tagValtype;
		public final DustEntity tagValtypeInt;
		public final DustEntity tagValtypeReal;
		public final DustEntity tagValtypeRef;
		public final DustEntity tagValtypeRaw;

		public final DustEntity tagColltype;
		public final DustEntity tagColltypeOne;
		public final DustEntity tagColltypeArr;
		public final DustEntity tagColltypeSet;
		public final DustEntity tagColltypeMap;

		public DslIdea(ArkDockModelMeta meta) {
			unit = meta.getBootEntity("Idea", "Unit", "Idea");

			typType = meta.getBootEntity("Idea", "Type", "Type");
			typAgent = meta.getBootEntity("Idea", "Type", "Agent");
			typMember = meta.getBootEntity("Idea", "Type", "Member");
			typTag = meta.getBootEntity("Idea", "Type", "Tag");

			meta.factTypeDef.get(typType, unit);
			meta.factTypeDef.get(typAgent, unit);
			meta.factTypeDef.get(typMember, unit);
			meta.factTypeDef.get(typTag, unit);
			
			meta.typType = typType;
			meta.typMember = typMember;
			meta.typTag = typTag;
			
			memMemberOptions = meta.getMember(typMember, "Options");
			eMemberCollType = meta.getMember(typMember, "CollType");
			eMemberValType = meta.getMember(typMember, "ValType");

			eAgentUpdates = meta.getMember(typAgent, "Updates");

			typConst = meta.getBootEntity("Idea", "Type", "Const");
			
			tagBool = meta.defineTag(unit, "Boolean", null);
			tagBoolFalse = meta.getEntity(unit, typTag, "False", true);
			tagBoolTrue = meta.getEntity(unit, typTag, "True", true);
			
			tagValtype = meta.getEntity(unit, typTag, "ValType", true);
			tagValtypeInt = meta.getEntity(unit, typTag, "Int", true);
			tagValtypeReal = meta.getEntity(unit, typTag, "Real", true);
			tagValtypeRef = meta.getEntity(unit, typTag, "Ref", true);
			tagValtypeRaw = meta.getEntity(unit, typTag, "Raw", true);

			tagColltype = meta.getEntity(unit, typTag, "CollType", true);
			tagColltypeOne = meta.getEntity(unit, typTag, "One", true);
			tagColltypeArr = meta.getEntity(unit, typTag, "Arr", true);
			tagColltypeSet = meta.getEntity(unit, typTag, "Set", true);
			tagColltypeMap = meta.getEntity(unit, typTag, "Map", true);
		}
	}
	
	public class DslNarrative {
		public final DustEntity unit;

		public DslNarrative(ArkDockModelMeta meta) {
			unit = meta.getUnit("Narrative");
		}
	}
	
	public class DslDialog {
		public final DustEntity unit;

		public final DustEntity typAction;

		public final DustEntity memActionResponse;

		public DslDialog(ArkDockModelMeta meta) {
			unit = meta.getUnit("Dialog");
			
			typAction = meta.getType(unit, "Action");

			memActionResponse = meta.getMember(typAction, "Response");
		}
	}
	
}
