package ark.dock;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import dust.gen.DustGenException;
import dust.gen.DustGenFactory;
import dust.gen.DustGenTranslator;

public class ArkDockMind implements ArkDockDslConsts, ArkDockDsl, ArkDockBootConsts {

	final DustGenFactory<Class<?>, Object> factDslWrap = new DustGenFactory<Class<?>, Object>(null) {
		private static final long serialVersionUID = 1L;

		protected Object createItem(Class<?> key, Object hint) {
			try {
				return key.newInstance();
			} catch (Throwable e) {
				return DustGenException.throwException(e, "Failed to instantiate token container for class", key);
			}
		};
	};

	final DustGenFactory<String, ArkDockDslBuilder> factDslBuilder = new DustGenFactory<String, ArkDockDslBuilder>(
			null) {
		private static final long serialVersionUID = 1L;

		protected ArkDockDslBuilder createItem(String key, Object hint) {
			ArkDockDslBuilder ret;

			ArkDockUnit u = factUnit.peek(key);

			if ( null == u ) {
				ret = new ArkDockDslBuilder(key);
			} else {
				ret = new ArkDockDslBuilder(u);
			}

			factUnit.put(key, ret);

			return ret;
		};
	};

	DustGenFactory<String, ArkDockUnit> factUnit = new DustGenFactory<String, ArkDockUnit>(null) {
		private static final long serialVersionUID = 1L;

		@Override
		protected ArkDockUnit createItem(String key, Object hint) {
			return new ArkDockUnit(key, (null == hint) ? mainUnit : (ArkDockUnit) hint);
		}
	};

	private final Map<String, ArkDockAgent<?>> agents = new TreeMap<>();

	DustGenFactory<DustEntity, ArkMemberDef> factMemberDef = new DustGenFactory<DustEntity, ArkMemberDef>(null) {
		private static final long serialVersionUID = 1L;

		@Override
		protected ArkMemberDef createItem(DustEntity key, Object hint) {
			return new ArkMemberDef((ArkDockEntity) key, (ArkDockEntity) hint);
		}
	};

	DustGenFactory<DustEntity, ArkTypeDef> factTypeDef = new DustGenFactory<DustEntity, ArkTypeDef>(null) {
		private static final long serialVersionUID = 1L;

		@Override
		protected ArkTypeDef createItem(DustEntity key, Object hint) {
			return new ArkTypeDef((ArkDockEntity) key, (ArkDockEntity) hint);
		}
	};

	DustGenFactory<DustEntity, ArkTagDef> factTagDef = new DustGenFactory<DustEntity, ArkTagDef>(null) {
		private static final long serialVersionUID = 1L;

		@Override
		protected ArkTagDef createItem(DustEntity key, Object hint) {
			ArkDockEntity e = (ArkDockEntity) key;
			ArkTagDef ret = new ArkTagDef(e);

			ret.single = e.accessMember(DustDialogCmd.CHK, memEntityTags, tagTagSingle, null);

			ArkDockEntity r = ArkDockMindUtils.getRoot(key);
			if ( null != r ) {
				ret.setRoot(get(r));
			}

			return ret;
		}
	};

	final ArkDockEntity typUnit;
	final ArkDockEntity memEntityId;
	final ArkDockEntity memEntityGlobalId;
	final ArkDockEntity memEntityPrimaryType;
	final ArkDockEntity memEntityTags;
	final ArkDockEntity memEntityOwner;

	final ArkDockEntity typType;
	final ArkDockEntity typMember;
	final ArkDockEntity typTag;
	final ArkDockEntity typAgent;
	
	final ArkDockEntity tagColltype;
	final ArkDockEntity tagColltypeOne;
	final ArkDockEntity tagValtype;

	final ArkDockEntity memBinaryName;
	final ArkDockEntity memNativeCollType;
	final ArkDockEntity memNativeValueOne;

	final ArkDockEntity memCollMember;

	final ArkDockEntity tagTagSingle;

	final DslDialog dslDialog;

	DustGenTranslator<DustCollType, DustEntity> trCollType = new DustGenTranslator<DustCollType, DustEntity>();
	DustGenTranslator<DustValType, DustEntity> trValType = new DustGenTranslator<DustValType, DustEntity>();
	DustGenTranslator<DustDialogCmd, DustEntity> trAccCmd = new DustGenTranslator<DustDialogCmd, DustEntity>();

	public final ArkDockUnit mainUnit;

	class BootItem {
		ArkDockDslBuilder db;

		String id;
		String type;
		String parent;
		String globalId;

		ArkDockEntity entity;

		public BootItem(ArkDockDslBuilder db, String typeId, String itemId, String parentId) {
			this.db = db;
			this.type = typeId;
			this.parent = parentId;
			this.id = (null == parent) ? itemId : parentId + TOKEN_SEP + itemId;

			this.globalId = ArkDockUtils.buildGlobalId(db.unitName, type, id);

			entity = new ArkDockEntity(db);
			db.entities.put(globalId, entity);
		}

		void updateEntity(Map<String, BootItem> boot) {
			initEntityBoot(entity, globalId, boot.get(type).entity, id,
					(null == parent) ? null : boot.get(parent).entity);
		}
	}

	class Bootloader {
		Map<String, BootItem> boot = new TreeMap<String, BootItem>();

		ArkDockEntity createBootEntity(ArkDockDslBuilder db, String typeId, String itemId, String parentId) {
			BootItem be = new BootItem(db, typeId, itemId, parentId);
			boot.put(itemId, be);

			return be.entity;
		}

		ArkDockEntity createBootEntity(ArkDockDslBuilder db, String typeId, String itemId) {
			return createBootEntity(db, typeId, itemId, null);
		}

		void updateEntities() {
			for (BootItem bi : boot.values()) {
				bi.updateEntity(boot);
			}
		}
	}

	public ArkDockMind(String mainUnitName) {
		ArkDock.setMind(this);

		Bootloader bl = new Bootloader();

		ArkDockDslBuilder dbModel = factDslBuilder.get(UNITNAME_MODEL);
		typUnit = bl.createBootEntity(dbModel, TYPENAME_TYPE, TYPENAME_UNIT);
		bl.createBootEntity(dbModel, TYPENAME_TYPE, TYPENAME_ENTITY);
		memEntityId = bl.createBootEntity(dbModel, TYPENAME_MEMBER, MEMBERNAME_ENTITY_ID, TYPENAME_ENTITY);
		memEntityGlobalId = bl.createBootEntity(dbModel, TYPENAME_MEMBER, MEMBERNAME_ENTITY_GLOBALID, TYPENAME_ENTITY);
		memEntityPrimaryType = bl.createBootEntity(dbModel, TYPENAME_MEMBER, MEMBERNAME_ENTITY_PRIMARYTYPE,
				TYPENAME_ENTITY);
		memEntityOwner = bl.createBootEntity(dbModel, TYPENAME_MEMBER, MEMBERNAME_ENTITY_OWNER, TYPENAME_ENTITY);
		memEntityTags = bl.createBootEntity(dbModel, TYPENAME_MEMBER, MEMBERNAME_ENTITY_TAGS, TYPENAME_ENTITY);

		typUnit.data.put(memEntityId, TYPENAME_UNIT);
		dbModel.eUnit = bl.createBootEntity(dbModel, TYPENAME_UNIT, UNITNAME_MODEL);

		ArkDockDslBuilder dbIdea = factDslBuilder.get(UNITNAME_IDEA);
		typType = bl.createBootEntity(dbIdea, TYPENAME_TYPE, TYPENAME_TYPE);
		typAgent = bl.createBootEntity(dbIdea, TYPENAME_TYPE, TYPENAME_AGENT);
		typMember = bl.createBootEntity(dbIdea, TYPENAME_TYPE, TYPENAME_MEMBER);
		typTag = bl.createBootEntity(dbIdea, TYPENAME_TYPE, TYPENAME_TAG);
		
		tagColltype = bl.createBootEntity(dbIdea, TYPENAME_TAG, TAGNAME_COLLTYPE);
		tagColltypeOne = bl.createBootEntity(dbIdea, TYPENAME_TAG, TAGNAME_COLLTYPE_ONE, TAGNAME_COLLTYPE);
		tagValtype = bl.createBootEntity(dbIdea, TYPENAME_TAG, TAGNAME_VALTYPE);

		ArkDockDslBuilder dbNative = factDslBuilder.get(UNITNAME_NATIVE);
		bl.createBootEntity(dbNative, TYPENAME_TYPE, TYPENAME_NATIVE);
		memNativeCollType = bl.createBootEntity(dbNative, TYPENAME_MEMBER, MEMBERNAME_NATIVE_COLLTYPE, TYPENAME_NATIVE);
		memNativeValueOne = bl.createBootEntity(dbNative, TYPENAME_MEMBER, MEMBERNAME_NATIVE_VALUEONE, TYPENAME_NATIVE);
		bl.createBootEntity(dbNative, TYPENAME_TYPE, TYPENAME_BINARY);
		memBinaryName = bl.createBootEntity(dbNative, TYPENAME_MEMBER, MEMBERNAME_BINARY_NAME, TYPENAME_BINARY);

		ArkDockDslBuilder dbGeneric = factDslBuilder.get(UNITNAME_NATIVE);
		tagTagSingle = bl.createBootEntity(dbGeneric, TYPENAME_TAG, TAGNAME_SINGLE);

		bl.updateEntities();

		getDsl(DslModel.class);
		DslIdea dslIdea = getDsl(DslIdea.class);

		dbModel.eUnit = dbModel.getEntity(typUnit, UNITNAME_MODEL, true);

		trCollType.add(DustCollType.ONE, dslIdea.tagColltypeOne);
		trCollType.add(DustCollType.ARR, dslIdea.tagColltypeArr);
		trCollType.add(DustCollType.SET, dslIdea.tagColltypeSet);
		trCollType.add(DustCollType.MAP, dslIdea.tagColltypeMap);

		trValType.add(DustValType.INT, dslIdea.tagValtypeInt);
		trValType.add(DustValType.REAL, dslIdea.tagValtypeReal);
		trValType.add(DustValType.REF, dslIdea.tagValtypeRef);
		trValType.add(DustValType.RAW, dslIdea.tagValtypeRaw);

		getDsl(DslNarrative.class);
		
		dslDialog = getDsl(DslDialog.class);
		trAccCmd.add(DustDialogCmd.CHK, dslDialog.tagCommandCHK);
		trAccCmd.add(DustDialogCmd.GET, dslDialog.tagCommandGET);
		trAccCmd.add(DustDialogCmd.SET, dslDialog.tagCommandSET);
		trAccCmd.add(DustDialogCmd.ADD, dslDialog.tagCommandADD);
		trAccCmd.add(DustDialogCmd.DEL, dslDialog.tagCommandDEL);
		
		DslGeneric dslGen = getDsl(DslGeneric.class);
		memCollMember = (ArkDockEntity) dslGen.memCollMember;

		getDsl(DslText.class);

		mainUnit = factUnit.get(mainUnitName);
	}

	@SuppressWarnings("unchecked")
	public <DslType> DslType getDsl(Class<DslType> dslClass) {
		return (DslType) factDslWrap.get(dslClass);
	}

	protected ArkDockDslBuilder getDslBuilder(String unitName) {
		return factDslBuilder.get(unitName);
	}

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

	void initEntity(DustEntity entity, String unitId, DustEntity type, String itemId, DustEntity owner) {
		String globalId = ArkDockUtils.buildGlobalId(unitId, ArkDock.getId(type), itemId);
		initEntityBoot(entity, globalId, type, itemId, owner);
	}

	void initEntityBoot(DustEntity entity, String globalId, DustEntity type, String itemId, DustEntity owner) {
		ArkDockEntity e = (ArkDockEntity) entity;

		e.data.put(memEntityId, itemId);
		e.data.put(memEntityGlobalId, globalId);
		e.data.put(memEntityPrimaryType, type);
		if ( null != owner ) {
			e.data.put(memEntityOwner, owner);
		}
	}

	public ArkDockUnit getMainUnit() {
		return mainUnit;
	}

	public final ArkDockUnit peekUnit(String unitName) {
		return factUnit.peek(unitName);
	}

	public final ArkDockUnit getUnit(String unitName, ArkDockUnit parent) {
		return factUnit.get(unitName, parent);
	}

	public final DustEntity getEntity(DustEntity eType, String id, boolean createIfMissing) {
		return mainUnit.getEntity(eType, id, createIfMissing);
	}

	public final DustEntity getEntity(String unitName, DustEntity eType, String id, boolean createIfMissing) {
		return factUnit.get(unitName).getEntity(eType, id, createIfMissing);
	}

	public final DustEntity getTagByOwner(DustEntity e, DustEntity tagRoot) {
		Object tags = ((ArkDockEntity) e).data.get(memEntityTags);

		if ( null != tags ) {
			Set<ArkTagDef> opts = factTagDef.get(tagRoot).getSiblings(false);
			if ( null != opts ) {
				if ( tags instanceof DustEntity ) {
					for ( ArkTagDef atd : opts ) {
						if ( e == atd.tag ) {
							return e;
						}
					}
				} else {
					@SuppressWarnings("unchecked")
					Set<DustEntity> ts = (Set<DustEntity>) tags;
					for ( ArkTagDef atd : opts ) {
						if ( ts.contains(atd.tag)) {
							return atd.tag;
						}
					}
				}
			}
		}
		
		return null;
	}

	public final DustEntity getAgent(String agentName) {
		return getEntity(typAgent, agentName, false);
	}

	protected ArkDockAgent<?> createAgent(String agentName) throws Exception {
		DustEntity eAgent = getAgent(agentName);

		String cName = mainUnit.getMember(eAgent, memBinaryName, null, null);
		Class<?> c = Class.forName(cName);

		ArkDockAgent<?> agent = (ArkDockAgent<?>) c.newInstance();

		return agent;
	}

	public final DustResultType initAgent(String agentName) throws Exception {
		@SuppressWarnings("unchecked")
		ArkDockAgent<DustEntityContext> agent = (ArkDockAgent<DustEntityContext>) createAgent(agentName);

		DustEntity eAgent = getAgent(agentName);

		mainUnit.setMember(eAgent, memNativeCollType, tagColltypeOne, null);
		mainUnit.setMember(eAgent, memNativeValueOne, agent, null);

		DustResultType res = sendAgent(agent, DustAgentAction.INIT, eAgent, null, null, null);

		agents.put(agentName, agent);

		return res;
	}

	public final DustResultType sendAgent(String agentName, DustAgentAction cmd) throws Exception {
		return sendAgent(agentName, cmd, null, null, null, null);
	}

	public final DustResultType sendAgent(String agentName, DustAgentAction cmd, DustEntity e, DustEntity m, Object val,
			Object key) throws Exception {
		@SuppressWarnings("unchecked")
		ArkDockAgent<DustEntityContext> agent = (ArkDockAgent<DustEntityContext>) agents.get(agentName);
		return sendAgent(agent, cmd, e, m, val, key);
	}

	public final DustResultType sendAgent(ArkDockAgent<DustEntityContext> agent, DustAgentAction cmd, DustEntity e,
			DustEntity m, Object val, Object key) throws Exception {
		DustEntityContext ctx = new DustEntityContext();

		ctx.entity = e;
		ctx.member = m;
		ctx.value = val;
		ctx.key = key;

		try {
			agent.setActionCtx(ctx);
			return agent.agentAction(cmd);
		} finally {
			agent.setActionCtx(null);
		}
	}
}
