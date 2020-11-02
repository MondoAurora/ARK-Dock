package ark.dock;

public class ArkDockDslBuilder extends ArkDockUnit implements ArkDockDslConsts, ArkDockDsl {

	public ArkDockDslBuilder(String unitName) {
		super(unitName, null);
	}
	
	ArkDockDslBuilder(ArkDockUnit src) {
		super(src);
		src.entities.clear();
	}

	
	public DustEntity getType(String typeId) {
		DustEntity ret = getEntity(mind.typType, typeId, true);
		mind.factTypeDef.get(ret, eUnit);
		return ret;
	}

	public DustEntity getMember(DustEntity type, String itemId) {
		String memberId = ArkDock.getId(type) + TOKEN_SEP + itemId;

		ArkDockEntity ret = getEntity(mind.typMember, memberId, false);

		if ( null == ret ) {
			ret = getEntity(mind.typMember, memberId, true);
			mind.factMemberDef.get(ret, type);
		}

		return ret;
	}
	
	public DustEntity defineMember(DustEntity type, String memberId, DustValType vt, DustCollType ct) {
		memberId = ArkDock.getId(type) + TOKEN_SEP + memberId;

		DustEntity entity = getEntity(mind.typMember, memberId, false);
		
		if ( null == entity ) {
			entity = getEntity(mind.typMember, memberId, true);
			mind.initEntity(entity, unitName, mind.typMember, memberId, type);
//			ArkMemberDef amd = mind.factMemberDef.get(entity, type);
//			amd.vt = vt;
//			amd.ct = ct;
		}
		
		mind.factMemberDef.get(entity, type).update(vt, ct);

		return entity;
	}

	public DustEntity defineTag(String tagId, DustEntity parent) {
		if ( null != parent ) {
			tagId = ArkDock.getId(parent) + TOKEN_SEP + tagId;
		}
		DustEntity entity = getEntity(mind.typTag, tagId, false);
		
		if ( null == entity ) {
			entity = getEntity(mind.typTag, tagId, true);
			mind.initEntity(entity, unitName, mind.typTag, tagId, parent);
		}
		
		return entity;
	}

}