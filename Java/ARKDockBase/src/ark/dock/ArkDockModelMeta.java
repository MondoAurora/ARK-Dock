package ark.dock;

import ark.dock.ArkDockConsts.MetaProvider;
import dust.gen.DustGenFactory;
import dust.gen.DustGenTranslator;
import dust.gen.DustGenUtils;

public class ArkDockModelMeta extends ArkDockModel implements MetaProvider, ArkDockTokens {

	public final ArkDockTokens.Meta tokMeta;
	public final ArkDockTokens.Text tokText;
	public final ArkDockTokens.Gen tokGen;

	class MemberInfo implements MetaMemberInfo {
		final DustEntity member;
		final DustEntity type;

		DustValType vt;
		DustCollType ct;

		public MemberInfo(DustEntity member, DustEntity type) {
			this.type = type;
			this.member = member;
			if ( null == member ) {
				DustException.throwException(null, "should not be here");
			}
		}

		@Override
		public DustEntity getType() {
			return type;
		}

		@Override
		public DustEntity getMember() {
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

	DustGenFactory<DustEntity, MemberInfo> factMemberInfo = new DustGenFactory<DustEntity, MemberInfo>(null) {
		private static final long serialVersionUID = 1L;

		@Override
		protected MemberInfo createItem(DustEntity key, Object hint) {
			return new MemberInfo(key, (DustEntity) hint);
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

			factMemberInfo.get(e, type);
			ret = e;
		}

		return ret;
	}
	
	void initBootMember(DustEntity entity, ArkDockTokens.Meta mt, DustValType vt, DustCollType ct) {
		initBootEntity(entity, mt.eTypeMember, mt);
		
		MemberInfo mi = getMemberInfo(entity, null, null);
		mi.vt = vt;
		mi.ct = ct;
	}


	@Override
	public MemberInfo getMemberInfo(DustEntity member, Object value, Object hint) {
		MemberInfo mi = factMemberInfo.get(member);

		if ( (null == mi.ct) && (null != hint) ) {
			mi.ct = ArkDockUtils.getCollTypeForHint(hint);
		}

		if ( (null == mi.vt) && (null != value) ) {
			mi.vt = ArkDockUtils.getValTypeForValue(value);
		}

		return mi;
	}
	
	public void consolidateMeta() {
		DustGenTranslator<DustCollType, DustEntity> trCollType = new DustGenTranslator<DustCollType, DustEntity>(
				DustCollType.values(), new DustEntity[] {tokMeta.eConstColltypeOne, tokMeta.eConstColltypeArr, tokMeta.eConstColltypeSet, tokMeta.eConstColltypeMap, });
		DustGenTranslator<DustValType, DustEntity> trValType = new DustGenTranslator<DustValType, DustEntity>(
				DustValType.values(), new DustEntity[] {tokMeta.eConstValtypeInt, tokMeta.eConstValtypeReal, tokMeta.eConstValtypeRef, tokMeta.eConstValtypeRaw});

		for ( MetaMemberInfo mi : factMemberInfo.values() ) {
			DustEntity eM = mi.getMember();
			DustEntity eT = mi.getType();
			
			setMember(eM, tokMeta.eMemberCollType, trCollType.getRight(ArkDockUtils.getCollType(mi)), null);
			setMember(eM, tokMeta.eMemberValType, trValType.getRight(ArkDockUtils.getValType(mi, DustValType.RAW)), null);
			
			boolean added = (boolean) accessMember(DustDialogCmd.CHK, eT, tokGen.eCollMember, eM, null);
			if ( !added ) {
				accessMember(DustDialogCmd.ADD, eT, tokGen.eCollMember, eM, KEY_APPEND);
			}
		}
	}
}