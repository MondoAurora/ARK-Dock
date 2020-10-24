package ark.dock;

import ark.dock.ArkDockConsts.MetaProvider;
import dust.gen.DustGenException;
import dust.gen.DustGenFactory;
import dust.gen.DustGenLog;
import dust.gen.DustGenTranslator;

public class ArkDockModelMeta extends ArkDockModel implements MetaProvider {

	public final ArkDockDslMind.DslModel dslModel;
	public final ArkDockDslMind.DslIdea dslIdea;
	public final ArkDockDslTools.DslText dslText;
	public final ArkDockDslTools.DslGeneric dslGeneric;

	DustEntity typType;
	DustEntity typMember;
	DustEntity typTag;

	DustGenTranslator<DustCollType, DustEntity> trCollType;
	DustGenTranslator<DustValType, DustEntity> trValType;
	
	class ArkMemberDef implements DustMemberDef {
		final DustEntity member;
		final DustEntity type;

		DustValType vt;
		DustCollType ct;

		public ArkMemberDef(DustEntity member, DustEntity type) {
			this.type = type;
			this.member = member;
			if ( null == member ) {
				DustGenException.throwException(null, "should not be here");
			}
		}

		@Override
		public DustEntity getTypeEntity() {
			return type;
		}

		@Override
		public DustEntity getDefEntity() {
			return member;
		}

		@Override
		public DustCollType getCollType() {
			return ct;
		}

		@Override
		public DustValType getValType() {
			return vt;
		}
	}

	DustGenFactory<DustEntity, ArkMemberDef> factMemberDef = new DustGenFactory<DustEntity, ArkMemberDef>(null) {
		private static final long serialVersionUID = 1L;

		@Override
		protected ArkMemberDef createItem(DustEntity key, Object hint) {
			return new ArkMemberDef(key, (DustEntity) hint);
		}
	};

	class ArkTypeDef {
		final DustEntity unit;
		final DustEntity type;

		DustValType vt;
		DustCollType ct;

		public ArkTypeDef(DustEntity type, DustEntity unit) {
			this.type = type;
			this.unit = unit;
		}
	}

	DustGenFactory<DustEntity, ArkTypeDef> factTypeDef = new DustGenFactory<DustEntity, ArkTypeDef>(null) {
		private static final long serialVersionUID = 1L;

		@Override
		protected ArkTypeDef createItem(DustEntity key, Object hint) {
			return new ArkTypeDef(key, (DustEntity) hint);
		}
	};

	final DustGenFactory<Class<?>, Object> factTokens = new DustGenFactory<Class<?>, Object>(null) {
		private static final long serialVersionUID = 1L;

		protected Object createItem(Class<?> key, Object hint) {
			try {
				return key.getConstructor(ArkDockModelMeta.class).newInstance(ArkDockModelMeta.this);
			} catch (Throwable e) {
				return DustGenException.throwException(e, "Failed to instantiate token container for class", key);
			}
		};
	};

	public ArkDockModelMeta() {
		parent = null;
		meta = this;

		dslIdea = getDsl(ArkDockDslMind.DslIdea.class);
		dslModel = getDsl(ArkDockDslMind.DslModel.class);
		
		dslText = getDsl(ArkDockDslTools.DslText.class);
		dslGeneric = getDsl(ArkDockDslTools.DslGeneric.class);

		initEntity(dslModel.unit, dslModel.typUnit);
		initEntity(dslIdea.unit, dslModel.typUnit);

		initEntity(dslIdea.typType, dslIdea.typType);
		initEntity(dslIdea.typAgent, dslIdea.typType);
		initEntity(dslModel.typUnit, dslIdea.typType);
		initEntity(dslIdea.typMember, dslIdea.typType);
		initEntity(dslModel.typEntity, dslIdea.typType);
		initEntity(dslIdea.typConst, dslIdea.typType);
		initEntity(dslIdea.typTag, dslIdea.typType);

		initMember(dslIdea.eAgentUpdates, DustValType.REF, DustCollType.SET);

		initMember(dslIdea.memMemberOptions, DustValType.REF, DustCollType.SET);
		initMember(dslIdea.eMemberCollType, DustValType.REF, DustCollType.ONE);
		initMember(dslIdea.eMemberValType, DustValType.REF, DustCollType.ONE);

		initMember(dslModel.memEntityId, DustValType.RAW, DustCollType.ONE);
		initMember(dslModel.memEntityGlobalId, DustValType.RAW, DustCollType.ONE);
		initMember(dslModel.memEntityPrimType, DustValType.REF, DustCollType.ONE);
		initMember(dslModel.memEntityOwner, DustValType.REF, DustCollType.ONE);
		initMember(dslModel.memEntityTags, DustValType.REF, DustCollType.SET);

		initEntity(dslIdea.tagBoolFalse, dslIdea.typTag, dslIdea.tagBool);
		initEntity(dslIdea.tagBoolTrue, dslIdea.typTag, dslIdea.tagBool);

		initEntity(dslIdea.tagValtypeInt, dslIdea.typTag, dslIdea.tagValtype);
		initEntity(dslIdea.tagValtypeReal, dslIdea.typTag, dslIdea.tagValtype);
		initEntity(dslIdea.tagValtypeRef, dslIdea.typTag, dslIdea.tagValtype);
		initEntity(dslIdea.tagValtypeRaw, dslIdea.typTag, dslIdea.tagValtype);
		
		initEntity(dslIdea.tagColltypeOne, dslIdea.typTag, dslIdea.tagColltype);
		initEntity(dslIdea.tagColltypeArr, dslIdea.typTag, dslIdea.tagColltype);
		initEntity(dslIdea.tagColltypeSet, dslIdea.typTag, dslIdea.tagColltype);
		initEntity(dslIdea.tagColltypeMap, dslIdea.typTag, dslIdea.tagColltype);

		initMember(dslGeneric.memCollMember, DustValType.REF, DustCollType.ARR);

		trCollType = new DustGenTranslator<DustCollType, DustEntity>(DustCollType.values(),
				new DustEntity[] { dslIdea.tagColltypeOne, dslIdea.tagColltypeArr, dslIdea.tagColltypeSet,
						dslIdea.tagColltypeMap, });
		trValType = new DustGenTranslator<DustValType, DustEntity>(DustValType.values(),
				new DustEntity[] { dslIdea.tagValtypeInt, dslIdea.tagValtypeReal, dslIdea.tagValtypeRef,
						dslIdea.tagValtypeRaw });
	}
	
	@SuppressWarnings("unchecked")
	public <DslType> DslType getDsl(Class<DslType> dslClass) {
		return (DslType) factTokens.get(dslClass);
	}

	@Override
	public DustEntity getUnit(String unitId) {
		DustEntity ret = getBootEntity(unitId, "Unit", unitId);
		initEntity(ret, dslModel.typUnit);
		return ret;
	}

	@Override
	public DustEntity getType(DustEntity unit, String typeId) {
		DustEntity ret = getEntity(unit, dslIdea.typType, typeId, true);
		factTypeDef.get(ret, unit);
		return ret;
	}

	@Override
	public DustEntity getMember(DustEntity type, String itemId) {
		String memberId = getId(type) + TOKEN_SEP + itemId;

		ArkDockEntity ret = getEntity(factTypeDef.get(type).unit, typMember, memberId, false);

		if ( null == ret ) {
			ret = getEntity(factTypeDef.get(type).unit, typMember, memberId, true);
			factMemberDef.get(ret, type);
		}

		return ret;
	}

	ArkDockEntity getBootEntity(String unitId, String typeId, String itemId) {
		String globalId = ArkDockUtils.buildGlobalId(unitId, typeId, itemId);
		ArkDockEntity e = entities.get(globalId);

		if ( null == e ) {
			e = new ArkDockEntity(this, globalId, itemId);
			entities.put(globalId, e);
		}

		return e;
	}

	void initEntity(DustEntity entity, DustEntity type) {
		initEntity( entity,  type, null);
	}

	void initEntity(DustEntity entity, DustEntity type, DustEntity owner) {
		ArkDockEntity e = (ArkDockEntity) entity;

		if ( null != dslModel ) {
			e.data.put(dslModel.memEntityId, e.id);
			e.data.put(dslModel.memEntityGlobalId, e.globalId);
			e.data.put(dslModel.memEntityPrimType, type);
			if ( null != owner) {
				e.data.put(dslModel.memEntityOwner, owner);
			}
		}
	}
	

	public void initMember(DustEntity entity, DustValType vt, DustCollType ct) {
		initEntity(entity, dslIdea.typMember);

		ArkMemberDef amd = getMemberDef(entity, null, null);
		amd.vt = vt;
		amd.ct = ct;
	}

	public DustEntity defineMember(DustEntity type, String itemId, DustValType vt, DustCollType ct) {
		DustEntity entity = getMember(type, itemId);
		initMember(entity, vt, ct);
		return entity;
	}

	public DustEntity defineTag(DustEntity unit, String tagId, DustEntity parent) {
		if ( null != parent ) {
			tagId = getId(parent) + TOKEN_SEP + tagId;
		}
		DustEntity entity = getEntity(unit, typTag, tagId, true);
		
		if ( null != parent ) {
			setMember(entity, dslModel.memEntityOwner, parent, null);
		}
		
		return entity;
	}

	@Override
	public ArkMemberDef getMemberDef(DustEntity member, Object value, Object hint) {
		ArkMemberDef amd = factMemberDef.get(member);

		if ( (null == amd.ct) && (null != hint) ) {
			amd.ct = ArkDockUtils.getCollTypeForHint(hint);
		}

		if ( (null == amd.vt) && (null != value) ) {
			amd.vt = ArkDockUtils.getValTypeForValue(value);
		}

		return amd;
	}

	public void optUpdateMeta(DustEntity entity) {
		if ( entity instanceof ArkDockEntity ) {
			ArkDockEntity e = (ArkDockEntity) entity;
			DustEntity pt = (DustEntity) e.data.get(dslModel.memEntityPrimType);

			if ( typType == pt ) {
				String unit = e.globalId.split("_")[0];
				DustEntity eU = getUnit(unit);

				factTypeDef.get(e, eU);
			} else if ( typMember == pt ) {
				DustEntity eT = getMember(e, dslModel.memEntityOwner, null, null);

				if ( null == eT ) {
					DustGenLog.log(DustEventLevel.WARNING, "Missing type for", e.globalId);
				}
				ArkMemberDef amd = factMemberDef.get(e, eT);

				DustCollType ct = trCollType.getLeft((DustEntity) e.data.get(dslIdea.eMemberCollType));
				if ( null != ct ) {
					if ( null == amd.ct ) {
						amd.ct = ct;
					} else if ( ct != amd.ct ) {
						DustGenLog.log(DustEventLevel.WARNING, "Conflict in member", e.globalId, "new Collection type",
								ct, "original", amd.ct);
					}
				}
				DustValType vt = trValType.getLeft((DustEntity) e.data.get(dslIdea.eMemberValType));
				if ( null != vt ) {
					if ( null == amd.vt ) {
						amd.vt = vt;
					} else if ( vt != amd.vt ) {
						DustGenLog.log(DustEventLevel.WARNING, "Conflict in member", e.globalId, "new Value type", vt,
								"original", amd.vt);
					}
				}
			}
		}
	}

	public void consolidateMeta() {

		for (ArkMemberDef amd : factMemberDef.values()) {
			DustEntity eM = amd.getDefEntity();
			DustEntity eT = amd.getTypeEntity();

			setMember(eM, dslIdea.eMemberCollType, trCollType.getRight(ArkDockUtils.getCollType(amd)), null);
			setMember(eM, dslIdea.eMemberValType, trValType.getRight(ArkDockUtils.getValType(amd, DustValType.RAW)),
					null);

			setMember(eM, dslModel.memEntityOwner, eT, null);
			boolean added = (boolean) accessMember(DustDialogCmd.CHK, eT, dslGeneric.memCollMember, eM, null);
			if ( !added ) {
				accessMember(DustDialogCmd.ADD, eT, dslGeneric.memCollMember, eM, KEY_APPEND);
			}
		}
	}
}