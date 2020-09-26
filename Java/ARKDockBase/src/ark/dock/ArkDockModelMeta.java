package ark.dock;

import ark.dock.ArkDockConsts.MetaProvider;
import dust.gen.DustGenException;
import dust.gen.DustGenFactory;
import dust.gen.DustGenTranslator;
import dust.gen.DustGenUtils;

public class ArkDockModelMeta extends ArkDockModel implements MetaProvider, ArkDockTokens {

	public final ArkDockTokens.Meta tokMeta;
	public final ArkDockTokens.Text tokText;
	public final ArkDockTokens.Gen tokGen;

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

	public ArkDockModelMeta() {
		parent = null;
		meta = this;

		tokMeta = new ArkDockTokens.Meta(this);
		tokText = new ArkDockTokens.Text(this);
		tokGen = new ArkDockTokens.Gen(this);
	}

	@Override
	public DustEntity getUnit(String unitId) {
		return getEntity(tokMeta.eUnitArk, tokMeta.eTypeUnit, unitId, true);
	}

	@Override
	public DustEntity getType(DustEntity unit, String typeId) {
		return getEntity(unit, tokMeta.eTypeType, typeId, true);
	}

	@Override
	public DustEntity getMember(DustEntity type, String itemId) {
		String globalId = DustGenUtils.sbAppend(null, TOKEN_SEP, true,
				((ArkDockEntity) type).globalId.replace(TYPE_TYPE, TYPE_MEMBER), itemId).toString();
		DustEntity ret = getEntity(globalId);

		if ( null == ret ) {
			ArkDockEntity e = getBootEntity(globalId);

			if ( null != tokMeta ) {
				initBootEntity(e, tokMeta.eTypeMember, tokMeta);
			}

			factMemberDef.get(e, type);
			ret = e;
		}

		return ret;
	}
	
	void initBootMember(DustEntity entity, ArkDockTokens.Meta mt, DustValType vt, DustCollType ct) {
		initBootEntity(entity, mt.eTypeMember, mt);
		
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
	
	public void consolidateMeta() {
		DustGenTranslator<DustCollType, DustEntity> trCollType = new DustGenTranslator<DustCollType, DustEntity>(
				DustCollType.values(), new DustEntity[] {tokMeta.eConstColltypeOne, tokMeta.eConstColltypeArr, tokMeta.eConstColltypeSet, tokMeta.eConstColltypeMap, });
		DustGenTranslator<DustValType, DustEntity> trValType = new DustGenTranslator<DustValType, DustEntity>(
				DustValType.values(), new DustEntity[] {tokMeta.eConstValtypeInt, tokMeta.eConstValtypeReal, tokMeta.eConstValtypeRef, tokMeta.eConstValtypeRaw});

		for ( ArkMemberDef amd : factMemberDef.values() ) {
			DustEntity eM = amd.getDefEntity();
			DustEntity eT = amd.getTypeEntity();
			
			setMember(eM, tokMeta.eMemberCollType, trCollType.getRight(ArkDockUtils.getCollType(amd)), null);
			setMember(eM, tokMeta.eMemberValType, trValType.getRight(ArkDockUtils.getValType(amd, DustValType.RAW)), null);
			
			boolean added = (boolean) accessMember(DustDialogCmd.CHK, eT, tokGen.eCollMember, eM, null);
			if ( !added ) {
				accessMember(DustDialogCmd.ADD, eT, tokGen.eCollMember, eM, KEY_APPEND);
			}
		}
	}
}