package ark.dock;

import dust.gen.DustGenLog;
import dust.gen.DustGenUtils;

public class ArkDockMindUtils extends DustGenUtils implements ArkDockDslConsts {
	

	public static void optUpdateMeta(DustEntity entity) {
		ArkDockMind mind = ArkDock.getMind();

		if ( entity instanceof ArkDockEntity ) {
			ArkDockEntity e = (ArkDockEntity) entity;
			String globalId = (String) e.data.get(mind.memEntityGlobalId);
			DustEntity pt = (DustEntity) e.data.get(mind.memEntityPrimType);

			if ( mind.typType == pt ) {
				String unit = globalId.split("_")[0];
				DustEntity eU = mind.getUnit(unit, null).eUnit;

				mind.factTypeDef.get(e, eU);
			} else if ( mind.typMember == pt ) {
				DustEntity eT = (DustEntity) e.data.get(mind.memEntityOwner);

				if ( null == eT ) {
					DustGenLog.log(DustEventLevel.WARNING, "Missing type for", globalId);
				}
				ArkMemberDef amd = mind.factMemberDef.get(e, eT);

//				DustCollType ct = mind.trCollType.getLeft((DustEntity) e.data.get(mind.dslIdea.eMemberCollType));
//				if ( null != ct ) {
//					if ( null == amd.ct ) {
//						amd.ct = ct;
//					} else if ( ct != amd.ct ) {
//						DustGenLog.log(DustEventLevel.WARNING, "Conflict in member", e.globalId, "new Collection type",
//								ct, "original", amd.ct);
//					}
//				}
//				DustValType vt = mind.trValType.getLeft((DustEntity) e.data.get(mind.dslIdea.eMemberValType));
//				if ( null != vt ) {
//					if ( null == amd.vt ) {
//						amd.vt = vt;
//					} else if ( vt != amd.vt ) {
//						DustGenLog.log(DustEventLevel.WARNING, "Conflict in member", e.globalId, "new Value type", vt,
//								"original", amd.vt);
//					}
//				}
			}
		}
	}

	public static void consolidateMeta() {
		ArkDockMind mind = ArkDock.getMind();
		
		for (ArkMemberDef amd : mind.factMemberDef.values()) {
			DustEntity eM = amd.getDefEntity();
			DustEntity eT = amd.getTypeEntity();

//			setMember(eM, mind.dslIdea.eMemberCollType, mind.trCollType.getRight(ArkDockUtils.getCollType(amd)), null);
//			setMember(eM, mind.dslIdea.eMemberValType, mind.trValType.getRight(ArkDockUtils.getValType(amd, DustValType.RAW)),
//					null);
//
//			setMember(eM, mind.dslModel.memEntityOwner, eT, null);
//			boolean added = (boolean) accessMember(DustDialogCmd.CHK, eT, mind.dslGeneric.memCollMember, eM, null);
//			if ( !added ) {
//				accessMember(DustDialogCmd.ADD, eT, mind.dslGeneric.memCollMember, eM, KEY_APPEND);
//			}
		}
	}
}