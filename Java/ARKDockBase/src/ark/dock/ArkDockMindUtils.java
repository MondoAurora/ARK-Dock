package ark.dock;

import dust.gen.DustGenLog;
import dust.gen.DustGenUtils;

public class ArkDockMindUtils extends DustGenUtils implements ArkDockDslConsts {
	
	static ArkDockEntity getRoot(DustEntity entity) {
		ArkDockMind mind = ArkDock.getMind();
		
		ArkDockEntity op = null;
		ArkDockEntity o = (ArkDockEntity) ((ArkDockEntity) entity).data.get(mind.memEntityOwner);
		
		while ( null != o ) {
			op = o;
			o = (ArkDockEntity) op.data.get(mind.memEntityOwner);
		}
		
		return op;
	}

	public static void optUpdateMeta(DustEntity entity) {
		ArkDockMind mind = ArkDock.getMind();

		if ( entity instanceof ArkDockEntity ) {
			ArkDockEntity e = (ArkDockEntity) entity;
			String globalId = (String) e.data.get(mind.memEntityGlobalId);
			DustEntity pt = (DustEntity) e.data.get(mind.memEntityPrimaryType);

			if ( mind.typType == pt ) {
				String unit = ArkDockUtils.getSegment(globalId, TokenSegment.UNIT);
				DustEntity eU = mind.getUnit(unit, null).eUnit;

				mind.factTypeDef.get(e, eU);
			} else if ( mind.typMember == pt ) {
				DustEntity eT = (DustEntity) e.data.get(mind.memEntityOwner);

				if ( null == eT ) {
					DustGenLog.log(DustEventLevel.WARNING, "Missing type for", globalId);
				}
				ArkMemberDef amd = mind.factMemberDef.get(e, eT);
				
				DustEntity eCT = mind.getTagByOwner(e, mind.tagColltype);
				if ( null != eCT ) {
					DustCollType ct = mind.trCollType.getLeft(eCT);
					if ( null == amd.ct ) {
						amd.ct = ct;
					} else if ( ct != amd.ct ) {
						DustGenLog.log(DustEventLevel.WARNING, "Conflict in member", globalId, "new Collection type",
								ct, "original", amd.ct);
					}
				}
				
				DustEntity eVT = mind.getTagByOwner(e, mind.tagValtype);
				if ( null != eVT ) {
					DustValType vt = mind.trValType.getLeft(eVT);
					if ( null == amd.vt ) {
						amd.vt = vt;
					} else if ( vt != amd.vt ) {
						DustGenLog.log(DustEventLevel.WARNING, "Conflict in member", globalId, "new Value type",
								vt, "original", amd.vt);
					}
				}
			}
		}
	}

	public static void consolidateMeta() {
		ArkDockMind mind = ArkDock.getMind();
		
		for (ArkMemberDef amd : mind.factMemberDef.values()) {
			DustEntity eM = amd.getDefEntity();
			DustEntity eT = amd.getTypeEntity();
			
			DustCollType ct = amd.getCollType();
			if ( null != ct ) {
				ArkDock.access(DustDialogCmd.ADD, eM, mind.memEntityTags, mind.trCollType.getRight(ct), null);				
			}

			DustValType vt = amd.getValType();
			if ( null != vt ) {
				ArkDock.access(DustDialogCmd.ADD, eM, mind.memEntityTags, mind.trValType.getRight(vt), null);				
			}

			ArkDock.access(DustDialogCmd.SET, eM, mind.memEntityOwner, eT, null);
			boolean added = (boolean) ArkDock.access(DustDialogCmd.CHK, eT, mind.memCollMember, eM, null);
			if ( !added ) {
				ArkDock.access(DustDialogCmd.ADD, eT, mind.memCollMember, eM, KEY_APPEND);
			}
		}
	}
}