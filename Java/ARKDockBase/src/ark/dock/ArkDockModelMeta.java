package ark.dock;

import ark.dock.ArkDockConsts.MetaProvider;
import dust.gen.DustGenException;
import dust.gen.DustGenFactory;
import dust.gen.DustGenLog;
import dust.gen.DustGenTranslator;

public class ArkDockModelMeta extends ArkDockModel implements MetaProvider {

	public final ArkDockTokensMind.Model tokModel;
	public final ArkDockTokensMind.Idea tokIdea;

	DustEntity eTypeType;
	DustEntity eTypeMember;

	public final ArkDockTokensTools.Text tokText;
	public final ArkDockTokensTools.Generic tokGeneric;

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

	public ArkDockModelMeta() {
		parent = null;
		meta = this;

		tokIdea = new ArkDockTokensMind.Idea(this);
		tokModel = new ArkDockTokensMind.Model(this);
		tokText = new ArkDockTokensTools.Text(this);
		tokGeneric = new ArkDockTokensTools.Generic(this);

		initEntity(tokModel.eUnit, tokModel.eTypeUnit);
		initEntity(tokIdea.eUnit, tokModel.eTypeUnit);

		initEntity(tokIdea.eTypeType, tokIdea.eTypeType);
		initEntity(tokModel.eTypeUnit, tokIdea.eTypeType);
		initEntity(tokIdea.eTypeMember, tokIdea.eTypeType);
		initEntity(tokModel.eTypeEntity, tokIdea.eTypeType);
		initEntity(tokIdea.eTypeConst, tokIdea.eTypeType);

		initBootMember(tokIdea.eMemberOptions, DustValType.REF, DustCollType.SET);
		initBootMember(tokIdea.eMemberCollType, DustValType.REF, DustCollType.ONE);
		initBootMember(tokIdea.eMemberValType, DustValType.REF, DustCollType.ONE);

		initBootMember(tokModel.eEntityId, DustValType.RAW, DustCollType.ONE);
		initBootMember(tokModel.eEntityGlobalId, DustValType.RAW, DustCollType.ONE);
		initBootMember(tokModel.eEntityPrimType, DustValType.REF, DustCollType.ONE);
		initBootMember(tokModel.eEntityOwner, DustValType.REF, DustCollType.ONE);

		initEntity(tokIdea.eConstFalse, tokIdea.eTypeConst);
		initEntity(tokIdea.eConstTrue, tokIdea.eTypeConst);

		initEntity(tokIdea.eConstValtypeInt, tokIdea.eTypeConst);
		initEntity(tokIdea.eConstValtypeReal, tokIdea.eTypeConst);
		initEntity(tokIdea.eConstValtypeRef, tokIdea.eTypeConst);
		initEntity(tokIdea.eConstValtypeRaw, tokIdea.eTypeConst);
		initEntity(tokIdea.eConstColltypeOne, tokIdea.eTypeConst);
		initEntity(tokIdea.eConstColltypeArr, tokIdea.eTypeConst);
		initEntity(tokIdea.eConstColltypeSet, tokIdea.eTypeConst);
		initEntity(tokIdea.eConstColltypeMap, tokIdea.eTypeConst);
		
		
		initBootMember(tokGeneric.eCollMember, DustValType.REF, DustCollType.ARR);


		trCollType = new DustGenTranslator<DustCollType, DustEntity>(DustCollType.values(),
				new DustEntity[] { tokIdea.eConstColltypeOne, tokIdea.eConstColltypeArr, tokIdea.eConstColltypeSet,
						tokIdea.eConstColltypeMap, });
		trValType = new DustGenTranslator<DustValType, DustEntity>(DustValType.values(),
				new DustEntity[] { tokIdea.eConstValtypeInt, tokIdea.eConstValtypeReal, tokIdea.eConstValtypeRef,
						tokIdea.eConstValtypeRaw });
	}

	@Override
	public DustEntity getUnit(String unitId) {
		DustEntity ret = getBootEntity(unitId, "Unit", unitId);
		initEntity(ret, tokModel.eTypeUnit);
		return ret;
	}

	@Override
	public DustEntity getType(DustEntity unit, String typeId) {
		DustEntity ret = getEntity(unit, tokIdea.eTypeType, typeId, true);
		factTypeDef.get(ret, unit);
		return ret;
	}

	@Override
	public DustEntity getMember(DustEntity type, String itemId) {
		String memberId = getId(type) + TOKEN_SEP + itemId;
		
		ArkDockEntity ret = getEntity(factTypeDef.get(type).unit, eTypeMember, memberId, false);

		if ( null == ret ) {
			ret = getEntity(factTypeDef.get(type).unit, eTypeMember, memberId, true);
//			if ( null != tokModel ) {
//				ret.data.put(tokModel.eEntityPrimType, eTypeMember);
//			}
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
		ArkDockEntity e = (ArkDockEntity) entity;

		if ( null != tokModel ) {
			e.data.put(tokModel.eEntityId, e.id);
			e.data.put(tokModel.eEntityGlobalId, e.globalId);
			e.data.put(tokModel.eEntityPrimType, type);
		}
	}

	private void initBootMember(DustEntity entity, DustValType vt, DustCollType ct) {
		initEntity(entity, tokIdea.eTypeMember);

		ArkMemberDef amd = getMemberDef(entity, null, null);
		amd.vt = vt;
		amd.ct = ct;
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
			DustEntity pt = (DustEntity) e.data.get(tokModel.eEntityPrimType);

			if ( eTypeType == pt ) {
				String unit = e.globalId.split("_")[0];
				DustEntity eU = getUnit(unit);

				factTypeDef.get(e, eU);
			} else if ( eTypeMember == pt ) {
				DustEntity eT = getMember(e, tokModel.eEntityOwner, null, null);
				
				if (null == eT) {
					DustGenLog.log(DustEventLevel.WARNING, "Missing type for", e.globalId);						
				}
				ArkMemberDef amd = factMemberDef.get(e, eT);
				
				DustCollType ct = trCollType.getLeft((DustEntity) e.data.get(tokIdea.eMemberCollType));
				if (null != ct) {
					if (null == amd.ct) {
						amd.ct = ct;
					} else if (ct != amd.ct) {
						DustGenLog.log(DustEventLevel.WARNING, "Conflict in member", e.globalId, "new Collection type", ct, "original", amd.ct);						
					}
				}
				DustValType vt = trValType.getLeft((DustEntity) e.data.get(tokIdea.eMemberValType));
				if (null != vt) {
					if (null == amd.vt) {
						amd.vt = vt;
					} else if (vt != amd.vt) {
						DustGenLog.log(DustEventLevel.WARNING, "Conflict in member", e.globalId, "new Value type", vt, "original", amd.vt);						
					}
				}
			}
		}
	}

	public void consolidateMeta() {

		for (ArkMemberDef amd : factMemberDef.values()) {
			DustEntity eM = amd.getDefEntity();
			DustEntity eT = amd.getTypeEntity();

			setMember(eM, tokIdea.eMemberCollType, trCollType.getRight(ArkDockUtils.getCollType(amd)), null);
			setMember(eM, tokIdea.eMemberValType, trValType.getRight(ArkDockUtils.getValType(amd, DustValType.RAW)),
					null);

			setMember(eM, tokModel.eEntityOwner, eT, null);
			boolean added = (boolean) accessMember(DustDialogCmd.CHK, eT, tokGeneric.eCollMember, eM, null);
			if ( !added ) {
				accessMember(DustDialogCmd.ADD, eT, tokGeneric.eCollMember, eM, KEY_APPEND);
			}
		}
	}
}