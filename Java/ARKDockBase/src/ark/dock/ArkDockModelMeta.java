package ark.dock;

import ark.dock.ArkDockConsts.MetaProvider;
import dust.gen.DustGenFactory;
import dust.gen.DustGenUtils;

public class ArkDockModelMeta extends ArkDockModel implements MetaProvider, ArkDockTokens {

	public final ArkDockTokens.Meta mt;

	class MemberInfo implements MetaMemberInfo {
		final DustEntity member;
		final DustEntity type;

		DustValType vt;
		DustCollType ct;

		public MemberInfo(DustEntity member, DustEntity type) {
			this.type = type;
			this.member = member;
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

		mt = new Meta(this);
	}

	@Override
	public DustEntity getUnit(String unitId) {
		return getEntity(mt.eUnitArk, mt.eTypeUnit, unitId, true);
	}

	@Override
	public DustEntity getType(DustEntity unit, String typeId) {
		return getEntity(unit, mt.eTypeType, typeId, true);
	}

	@Override
	public DustEntity getMember(DustEntity type, String itemId) {
		String globalId = DustGenUtils.sbAppend(null, TOKEN_SEP, true,
				((ArkDockEntity) type).globalId.replace(TYPE_TYPE, TYPE_MEMBER), itemId).toString();
		DustEntity ret = getEntity(globalId);

		if ( null == ret ) {
			ArkDockEntity e = getBootEntity(globalId);

			if ( null != mt ) {
				initBootEntity(e, mt.eTypeMember, mt);
			}

			factMemberInfo.get(ret, type);
			ret = e;
		}

		return ret;
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
}