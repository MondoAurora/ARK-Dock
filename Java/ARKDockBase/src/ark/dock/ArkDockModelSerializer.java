package ark.dock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dust.gen.DustGenLog;
import dust.gen.DustGenUtils;

public abstract class ArkDockModelSerializer implements ArkDockConsts {

	public static abstract class SerializeAgent<SCType extends DustEntityContext> implements ArkDockAgent<SCType> {
		protected SCType ctx;

		@Override
		public SCType getActionCtx() {
			return ctx;
		}

		@Override
		public void setActionCtx(SCType ctx) {
			this.ctx = ctx;
		}
	}

	public static class Dump extends SerializeAgent<DustEntityContext> {
		public Dump() {
			setActionCtx(new DustEntityContext());
		}

		@Override
		public DustResultType agentAction(DustAgentAction action) throws Exception {
			DustGenLog.log(action, getActionCtx());
			return DustResultType.ACCEPT_READ;
		}
	}

	public static class ModelVisitor<SCType extends DustEntityContext> extends SerializeAgent<SCType> {
		final ArkDockModel model;
		final ArkDockAgent<SCType> target;
		final ArkDockAgent<SCType> filter;

		final Map<DustEntity, Object> serIDs = new HashMap<>();
		final Set<DustEntity> todo = new HashSet<>();

		public ModelVisitor(ArkDockModel model, ArkDockAgent<SCType> target,
				ArkDockAgent<SCType> filter) {
			setActionCtx(target.getActionCtx());

			this.model = model;
			this.target = target;
			this.filter = filter;
			
			if ( null != filter ) {
				filter.setActionCtx(target.getActionCtx());
			}
		}

		Object getSerId(DustEntity entity, boolean addTodo) {
			Object ret = serIDs.get(entity);

			if ( null == ret ) {
				ret = ((ArkDockEntity)entity).globalId;
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
					ctx.entityId = getSerId(ctx.entity, false);
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
					ArkDockEntity next = (ArkDockEntity) todo.iterator().next();
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
			ArkDockAgent<SCType> target, ArkDockAgent<SCType> filter) throws Exception {

		ModelVisitor<SCType> mv = new ModelVisitor<>(model, target, filter);
		model.visit(mv, null, null, null);
	}

	public static <SCType extends DustEntityContext> void modelToAgent(DustEntity entity,
			ArkDockAgent<SCType> target, ArkDockAgent<SCType> filter) throws Exception {
		ArkDockEntity e = (ArkDockEntity) entity;

		ModelVisitor<SCType> mv = new ModelVisitor<>(e.model, target, filter);
		e.model.visit(mv, entity, null, null);
	}

}
