package ark.dock;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import dust.gen.DustGenLog;
import dust.gen.DustGenUtils;

public abstract class ArkDockModelSerializer implements ArkDockConsts {

	public static abstract class SerializeAgent<SCType extends DustEntityContext> implements DustGenCtxAgent<SCType> {
		protected SCType ctx;

		@Override
		public SCType getEventCtx() {
			return ctx;
		}

		@Override
		public void setEventCtx(SCType ctx) {
			this.ctx = ctx;
		}
	}

	public static class Dump extends SerializeAgent<DustEntityContext> {
		public Dump() {
			setEventCtx(new DustEntityContext());
		}

		@Override
		public DustResultType agentAction(DustAgentAction action) throws Exception {
			DustGenLog.log(action, getEventCtx());
			return DustResultType.ACCEPT_READ;
		}
	}

	public static class ModelVisitor<SCType extends DustEntityContext> extends SerializeAgent<SCType> {
		final ArkDockModel model;
		final DustGenCtxAgent<SCType> target;
		final DustGenCtxAgent<SCType> filter;

		long nextId;
		final Map<DustEntity, Long> serIDs = new HashMap<>();
		final LinkedList<DustEntity> todo = new LinkedList<>();

		public ModelVisitor(ArkDockModel model, DustGenCtxAgent<SCType> target,
				DustGenCtxAgent<SCType> filter) {
			setEventCtx(target.getEventCtx());

			this.model = model;
			this.target = target;
			this.filter = filter;
			
			if ( null != filter ) {
				filter.setEventCtx(target.getEventCtx());
			}

			nextId = 1;
		}

		long getSerId(DustEntity entity, boolean addTodo) {
			Long ret = serIDs.get(entity);

			if ( null == ret ) {
				ret = nextId++;
				serIDs.put(entity, ret);
				if ( addTodo ) {
					todo.add(entity);
				}
			} else if ( !addTodo ) {
				todo.remove(entity);
			}

			return ret;
		}

		@Override
		public DustResultType agentAction(DustAgentAction action) throws Exception {
			if ( null != filter ) {
				DustResultType ret = filter.agentAction(action);
				if ( !DustGenUtils.isReadOn(ret) ) {
					if ( (action == DustAgentAction.BEGIN) && (ctx.block == EntityBlock.Entity) ) {
						todo.remove((DustEntity) ctx.entity);
					}
					return ret;
				}
			}

			switch ( action ) {
			case BEGIN:
				if ( ctx.block == EntityBlock.Entity ) {
					ctx.eKey = getSerId(ctx.entity, false);
				}
				break;
			case PROCESS:
				if ( ctx.valType == DustValType.REF ) {
					ctx.value = getSerId((DustEntity) ctx.value, true);
					target.agentAction(action);
					return DustResultType.ACCEPT;
				}
				break;
			case RELEASE:
				while (!todo.isEmpty()) {
					ArkDockEntity next = (ArkDockEntity) todo.removeFirst();
					model.doVisitEntity(this, next);
				}

				break;
			default:
				break;
			}

			return target.agentAction(action);
		}
	}

	public static <SCType extends DustEntityContext> void modelToAgent(ArkDockModel model,
			DustGenCtxAgent<SCType> target, DustGenCtxAgent<SCType> filter) throws Exception {

		ModelVisitor<SCType> mv = new ModelVisitor<>(model, target, filter);
		model.visit(mv, null, null, null);
	}

	public static <SCType extends DustEntityContext> void modelToAgent(DustEntity entity,
			DustGenCtxAgent<SCType> target, DustGenCtxAgent<SCType> filter) throws Exception {
		ArkDockEntity e = (ArkDockEntity) entity;

		ModelVisitor<SCType> mv = new ModelVisitor<>(e.model, target, filter);
		e.model.visit(mv, entity, null, null);
	}

}
