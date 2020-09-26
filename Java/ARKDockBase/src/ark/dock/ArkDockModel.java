package ark.dock;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dust.gen.DustGenConsts.DustEntity;
import dust.gen.DustGenException;
import dust.gen.DustGenUtils;

public class ArkDockModel implements ArkDockConsts, Iterable<DustEntity> {

	ArkDockModelMeta meta;
	ArkDockModel parent;

	Map<String, ArkDockEntity> entities = new HashMap<>();

	public ArkDockModel(ArkDockModel parent_) {
		this.parent = parent_;
		meta = parent.meta;
	}

	protected ArkDockModel() {
	}

	public ArkDockModelMeta getMeta() {
		return meta;
	}

	public ArkDockModel getParent() {
		return parent;
	}

	public DustEntity getEntity(DustEntity unit, DustEntity type, String itemId, boolean createIfMissing) {
		String globalId = ArkDockUtils.buildGlobalId(((ArkDockEntity) unit).id, ((ArkDockEntity) type).id, itemId);

		ArkDockEntity e = entities.get(globalId);

		if ( createIfMissing && (null == e) ) {
			e = new ArkDockEntity(this, globalId);
			e.accessMember(DustDialogCmd.SET, meta.tokMeta.eEntityId, itemId, null);
			e.accessMember(DustDialogCmd.SET, meta.tokMeta.eEntityGlobalId, globalId, null);
			e.accessMember(DustDialogCmd.SET, meta.tokMeta.eEntityPrimType, type, null);
			entities.put(globalId, e);
		}

		return e;
	}

	void initBootEntity(DustEntity entity, DustEntity type, ArkDockTokens.Meta mt) {
		ArkDockEntity e = (ArkDockEntity) entity;
		e.accessMember(DustDialogCmd.SET, mt.eEntityId, e.id, null);
		e.accessMember(DustDialogCmd.SET, mt.eEntityGlobalId, e.globalId, null);
		e.accessMember(DustDialogCmd.SET, mt.eEntityPrimType, type, null);
	}

	ArkDockEntity getBootEntity(String globalId) {
		ArkDockEntity e = entities.get(globalId);

		if ( null == e ) {
			e = new ArkDockEntity(this, globalId);
			entities.put(globalId, e);
		}

		return e;
	}

	public DustEntity getEntity(String globalId) {
		DustEntity e = entities.get(globalId);
		if ( (null == e) && (null != parent) ) {
			e = parent.getEntity(globalId);
		}
		return e;
	}

	public int getCount(DustEntity e, DustEntity member) {
		return (e instanceof ArkDockEntity) ? ((ArkDockEntity) e).getCount(member) : 0;
	}

	public String getId(DustEntity e) {
		return (e instanceof ArkDockEntity) ? ((ArkDockEntity) e).id : null;
	}

	public String getGlobalId(DustEntity e) {
		return (e instanceof ArkDockEntity) ? ((ArkDockEntity) e).globalId : null;
	}

	@SuppressWarnings("unchecked")
	public <RetType> RetType accessMember(DustDialogCmd cmd, DustEntity e, DustEntity member, Object value,
			Object hint) {
		Object ret = (cmd == DustDialogCmd.GET) ? value : false;

		if ( null != e ) {
			ret = ((ArkDockEntity) e).accessMember(cmd, member, value, hint);
		}

		return (RetType) ret;
	}

	private DustResultType doProcess(ArkAgent<? extends DustEntityContext> visitor, Object val, Object key)
			throws Exception {
		DustEntityContext ctx = visitor.getActionCtx();

		ctx.mKey = key;
		ctx.value = val;

		DustResultType ret = visitor.agentAction(DustAgentAction.PROCESS);

		if ( DustGenUtils.isReadOn(ret) && (ctx.valType == DustValType.REF) ) {
			DustEntityContext save = new DustEntityContext(ctx);
			ctx.reset();
			ret = doVisitEntity(visitor, (ArkDockEntity) val);
			ctx.load(save);
		}

		return ret;
	}

	@SuppressWarnings("rawtypes")
	private DustResultType doVisitMember(ArkAgent<? extends DustEntityContext> visitor, DustEntity member)
			throws Exception {
		DustResultType ret = null;
		DustEntityContext ctx = visitor.getActionCtx();
		ArkDockEntity entity = (ArkDockEntity) ctx.entity;
		ctx.member = member;

		DustMemberDef md = meta.getMemberDef(member, ctx.value, ctx.mKey);
		ctx.valType = md.getValType();
		ctx.collType = md.getCollType();

		ctx.block = EntityBlock.Member;
		ret = visitor.agentAction(DustAgentAction.BEGIN);

		if ( DustGenUtils.isReadOn(ret) ) {
			try {
				Object val = entity.data.get(member);

				DustCollType actCollType;

				if ( (null == ctx.mKey) && (null != (actCollType = ArkDockUtils.getCollTypeForData(val))) ) {
					int idx = 0;
					switch ( actCollType ) {
					case ARR:
					case SET:
						for (Object o : (Collection) val) {
							ret = doProcess(visitor, o, idx++);
							if ( DustGenUtils.isReject(ret) ) {
								break;
							}
						}
						break;
					case MAP:
						for (Object o : ((Map) val).entrySet()) {
							Map.Entry mm = (Map.Entry) o;
							ret = doProcess(visitor, mm.getValue(), mm.getKey());
							if ( DustGenUtils.isReject(ret) ) {
								break;
							}
						}
						break;
					default:
						break;
					}
					ctx.mKey = null;
				} else {
					ret = doProcess(visitor, ArkDockUtils.resolveValue(md, val, ctx.mKey), ctx.mKey);
				}
			} finally {
				ctx.entity = entity;
				ctx.member = md.getDefEntity();
				ctx.valType = md.getValType();
				ctx.collType = md.getCollType();
				ctx.mKey = ctx.value = null;

				ctx.block = EntityBlock.Member;
				visitor.agentAction(DustAgentAction.END);
			}
		}

		return ret;
	}

	DustResultType doVisitEntity(ArkAgent<? extends DustEntityContext> visitor, ArkDockEntity entity)
			throws Exception {
		DustResultType ret = null;
		DustEntityContext ctx = visitor.getActionCtx();

		ctx.entity = entity;
		ctx.block = EntityBlock.Entity;
		ret = visitor.agentAction(DustAgentAction.BEGIN);

		Object eKey = ctx.entityId;

		if ( DustGenUtils.isReadOn(ret) ) {
			try {
				if ( null == ctx.member ) {
					for (DustEntity de : entity.data.keySet()) {
						ret = doVisitMember(visitor, de);
						if ( DustGenUtils.isReject(ret) ) {
							break;
						}
					}
					return ret;
				} else {
					ret = doVisitMember(visitor, ctx.member);
				}

			} finally {
				ctx.entity = entity;
				ctx.entityId = eKey;
				ctx.member = null;
				ctx.valType = null;
				ctx.collType = null;

				ctx.block = EntityBlock.Entity;
				visitor.agentAction(DustAgentAction.END);
			}
		}

		return ret;
	}

	public DustResultType visit(ArkAgent<? extends DustEntityContext> visitor, DustEntity e, DustEntity member,
			Object key) throws Exception {
		DustResultType ret = null;

		DustEntityContext ctx = visitor.getActionCtx();
		ctx.entity = e;
		ctx.member = member;
		ctx.mKey = key;

		visitor.agentAction(DustAgentAction.INIT);

		try {
			if ( null == e ) {
				for (ArkDockEntity me : entities.values()) {
					ret = doVisitEntity(visitor, me);
					if ( DustGenUtils.isReject(ret) ) {
						break;
					}
				}
			} else {
				ret = doVisitEntity(visitor, (ArkDockEntity) e);
			}
		} finally {
			ctx.reset();
			ctx.entity = e;
			ctx.member = member;
			ctx.mKey = key;
			visitor.agentAction(DustAgentAction.RELEASE);
		}

		return ret;
	}

	public boolean setMember(DustEntity e, DustEntity member, Object value, Object hint) {
		return (boolean) accessMember(DustDialogCmd.SET, e, member, value, hint);
	}

	public <RetType> RetType getMember(DustEntity e, DustEntity member, RetType defValue, Object hint) {
		return accessMember(DustDialogCmd.GET, e, member, defValue, hint);
	}

	public Iterator<DustEntity> getContent(DustEntity e, DustEntity member) {
		return ((ArkDockEntity) e).data.keySet().iterator();
	}

	class ContentReader implements Iterator<DustEntity> {
		Iterator<ArkDockEntity> mi;

		public ContentReader() {
			mi = entities.values().iterator();
		}

		@Override
		public boolean hasNext() {
			return mi.hasNext();
		}

		@Override
		public DustEntity next() {
			return mi.next();
		}

		@Override
		public void remove() {
			DustGenException.throwException(null, "Should not remove Entity in this way! Use the API.");
		}
	}

	@Override
	public Iterator<DustEntity> iterator() {
		return new ContentReader();
	}
}
