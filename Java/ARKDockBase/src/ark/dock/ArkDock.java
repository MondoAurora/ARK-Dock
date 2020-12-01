package ark.dock;

import dust.gen.DustGenException;

public class ArkDock implements ArkDockDslConsts, ArkDockDsl {
	private static ArkDockMind THE_MIND = null;

	protected static synchronized void setMind(ArkDockMind mind) {
		if ( THE_MIND != mind ) {
			if ( null == THE_MIND ) {
				THE_MIND = mind;
			} else {
				DustGenException.throwException(null, "Attempt to call ArkDockMind.setMind() with ", mind,
						"but it already has", THE_MIND);
			}
		}
	}

	public static <DslType> DslType getDsl(Class<DslType> dslClass) {
		return THE_MIND.getDsl(dslClass);
	}

	public static ArkDockDslBuilder getDslBuilder(String dslName) {
		return THE_MIND.factDslBuilder.get(dslName);
	}

	@SuppressWarnings("unchecked")
	public static <MindType extends ArkDockMind> MindType getMind() {
		return (MindType) THE_MIND;
	}

	public static DustEntity getByGlobalId(String globalId) {
		DustEntity ret = THE_MIND.factUnit.get(ArkDockUtils.getSegment(globalId, TokenSegment.UNIT)).getEntity(globalId);
		return ret;
	}

	public static <RetType> RetType access(DustDialogCmd cmd, DustEntity e, DustEntity member, Object value,
			Object hint) {
		ArkDockEntity ae = (ArkDockEntity) e;
		return ae.unit.accessMember(cmd, e, member, value, hint);
	}

	public static <RetType> RetType accessMember(DustEntity entity, DustEntityDelta delta) {
		ArkDockEntity ae = (ArkDockEntity) entity;
		return ae.unit.accessMember(entity, delta);
	}

	public static String formatEntity(DustEntity e) {
		return getGlobalId(e);
	}

	public static String getGlobalId(DustEntity e) {
		return (null == e) ? null : (String) ((ArkDockEntity) e).data.get(THE_MIND.memEntityGlobalId);
	}

	public static String getId(DustEntity e) {
		return (null == e) ? null : (String) ((ArkDockEntity) e).data.get(THE_MIND.memEntityId);
	}

	public static DustEntity getPrimaryType(DustEntity e) {
		return (null == e) ? null : (DustEntity) ((ArkDockEntity) e).data.get(THE_MIND.memEntityPrimaryType);
	}

	public static final DustEntity getTagByOwner(DustEntity e, DustEntity tagRoot) {
		return (null == e) ? null : THE_MIND.getTagByOwner(e, tagRoot);
	}

}
