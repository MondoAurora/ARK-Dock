package ark.dock;

import java.util.HashSet;
import java.util.Set;

import dust.gen.DustGenException;
import dust.gen.DustGenUtils;

public interface ArkDockDslConsts extends ArkDockConsts {

	class ArkTypeDef {
		final ArkDockEntity unit;
		final ArkDockEntity type;

		public ArkTypeDef(ArkDockEntity type, ArkDockEntity unit) {
			this.type = type;
			this.unit = unit;
		}
	}

	class ArkTagDef {
		final ArkDockEntity tag;
		boolean single;
		private ArkTagDef root;
		private Set<ArkTagDef> children;

		public ArkTagDef(ArkDockEntity tag) {
			this.tag = tag;
		}
		
		public void setRoot(ArkTagDef root) {
			this.root = root;
			
			if ( null == root.children) {
				root.children = new HashSet<>();
			}
			root.children.add(this);
		}
		
		Set<ArkTagDef> getSiblings(boolean onlyForSingle) {
			return ((null != root) && (root.single || !onlyForSingle) && (null != root.children)) ? root.children : null;
		}
	}

	class ArkMemberDef implements DustMemberDef {
		final ArkDockEntity member;
		final ArkDockEntity type;

		DustValType vt;
		DustCollType ct;

		public ArkMemberDef(ArkDockEntity member, ArkDockEntity type) {
			this.type = type;
			this.member = member;
			if ( null == member ) {
				DustGenException.throwException(null, "should not be here");
			}
		}

		void update(DustValType vt_, DustCollType ct_) {
			if ( vt != vt_ ) {
				if ( null == vt ) {
					vt = vt_;
				} else {
					DustGenException.throwException(null, "Overriding extisting", vt, "with", vt_, "in member def",
							member);
				}
			}
			
			if ( ct != ct_ ) {
				if ( null == ct ) {
					ct = ct_;
				} else {
					DustGenException.throwException(null, "Overriding extisting", ct, "with", ct_, "in member def",
							member);
				}
			}
		}

		@Override
		public ArkDockEntity getTypeEntity() {
			return type;
		}

		@Override
		public ArkDockEntity getDefEntity() {
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
		
		@Override
		public String toString() {
			return DustGenUtils.sbAppend(null, " ", true, "MemberDef", member, "vt:", vt, "ct:", ct).toString();
		}
	}
}
