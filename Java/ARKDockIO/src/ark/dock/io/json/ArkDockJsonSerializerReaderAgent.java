package ark.dock.io.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ark.dock.ArkDock;
import ark.dock.ArkDockConsts.ArkDockAgentDefault;
import ark.dock.ArkDockUnit;
import ark.dock.ArkDockUtils;
import ark.dock.ArkDockVisitor;
import ark.dock.ArkDockVisitor.VisitorAware;
import ark.dock.io.json.ArkDockJsonConsts.JsonContext;
import dust.gen.DustGenFactory;
import dust.gen.DustGenLog;
import dust.gen.DustGenUtils;

/**
 * As reading JSON, types "should be OK", state management is checked by setting
 * newState in all valid cases. Everything else is an error in the JSON input
 * and exception is thrown.
 */
@SuppressWarnings("incomplete-switch")
public class ArkDockJsonSerializerReaderAgent extends ArkDockAgentDefault<JsonContext>
		implements ArkDockJsonConsts, VisitorAware<JsonContext> {
	enum ReadState {
		Init, ReadHeader, ReadDelete, ReadSerial
	}

	final DslModel dslModel;
	final DslIdea dslIdea;

	private ArkDockVisitor<JsonContext> visitor;
	private ArkDockJsonReaderAgent jsonReader;

	private Map<String, Object> header;
	ArrayList<ReadState> payloadSections;
	int payloadIdx;

	ReadState readState;

	String memberId;
	DustEntity eMember;
	DustMemberDef md = null;

	DustGenFactory<DustEntity, DustMemberDef> factMemberDef = new DustGenFactory<DustEntity, DustMemberDef>(null) {
		private static final long serialVersionUID = 1L;

		@Override
		protected DustMemberDef createItem(DustEntity key, Object hint) {
			return ArkDock.getMind().getMemberDef(key, hint, null);
		}
	};

	enum SerReadState {
		ReadSerialObject, ReadEntityId, ReadEntityObject, ReadMemberId// , ReadMemberValue
	}

	class SerialReader extends ArkDockAgentDefault<JsonContext> {
		SerReadState readState = SerReadState.ReadSerialObject;

		String globalId;
		DustEntity eTarget;
		Map<DustEntity, Object> newEntity = new HashMap<>();

		void selectEntity(Object id) {
			globalId = (String) id;
			eTarget = ArkDock.getByGlobalId(globalId);
			
			if ( null == eTarget ) {
				newEntity.clear();
			}
			
			DustGenLog.log("Serial read - Reading entity", globalId, "found:", null != eTarget);
		}

		private void selectMember(Object id) {
			memberId = (String) id;
			eMember = ArkDock.getByGlobalId(memberId);
			md = null;
			
			DustGenLog.log("Serial read - Select member", memberId, "found:", eMember);
		}

		@SuppressWarnings("rawtypes")
		private void setValue(Object root) {
			if ( null == eTarget ) {
				newEntity.put(eMember, root);
			} else {
				if ( root instanceof ArrayList ) {
					ArkDock.access(DustDialogCmd.DEL, eTarget, eMember, null, null);
					for (Object v : (ArrayList) root) {
						setValueSingle(v, KEY_APPEND);
					}
				} else if ( root instanceof Map ) {
					ArkDock.access(DustDialogCmd.DEL, eTarget, eMember, null, null);
					for (Object e : ((Map) root).entrySet()) {
						setValueSingle(((Map.Entry) e).getValue(), ((Map.Entry) e).getKey());
					}
				} else {
					setValueSingle(root, null);
				}
			}
		}

		private void setValueSingle(Object val, Object key) {
			if ( null == eTarget ) {
				newEntity.put(eMember, val);
			} else {
				if ( null == md ) {
					md = ArkDock.getMind().getMemberDef(eMember, val, key);
				}
				if ( md.getValType() == DustValType.REF ) {
					val = ArkDock.getByGlobalId((String) val);
				}
				ArkDock.access((null == key) ? DustDialogCmd.SET : DustDialogCmd.ADD, eTarget, eMember, val, key);
			}
			
			if ( null == key ) {
				DustGenLog.log("Serial read - Set Single value", val);
			} else {
				DustGenLog.log("Serial read - Set Multi key:", key, "value", val);
			}
		}

		private void endEntityObject() {
			if ( null == eTarget ) {
				String strType = (String) newEntity.remove(dslModel.memEntityPrimaryType);
				String strId = (String) newEntity.remove(dslModel.memEntityId);
				String strGlobalId = (String) newEntity.remove(dslModel.memEntityGlobalId);
				
				String unit = ArkDockUtils.getSegment(strGlobalId, TokenSegment.UNIT);
				DustEntity eType = ArkDock.getByGlobalId(strType);
				
				eTarget = ArkDock.getMind().getEntity(unit, eType, strId, true);
				
				for ( Map.Entry<DustEntity, Object> e : newEntity.entrySet() ) {
					eMember = e.getKey();
					setValue(e.getValue());
				}
			}
			
			DustGenLog.log("Serial read - End Entity");
		}

		private void beginSerialObject() {
			DustGenLog.log("Serial read - Begin serial read");
		}

		private void endSerialObject() {
			DustGenLog.log("Serial read - End serial read");
		}

		@Override
		public DustResultType agentAction(DustAgentAction action) throws Exception {
			SerReadState newState = null;

			switch ( action ) {
			case INIT:
				newState = SerReadState.ReadSerialObject;
				break;
			case BEGIN:
				switch ( readState ) {
				case ReadSerialObject:
					if ( ctx.block == JsonBlock.Object ) {
						beginSerialObject();
						newState = SerReadState.ReadEntityId;
					}
					break;
				case ReadEntityId:
					if ( ctx.block == JsonBlock.Entry ) {
						selectEntity(ctx.param);
						newState = SerReadState.ReadEntityObject;
					}
					break;
				case ReadEntityObject:
					if ( ctx.block == JsonBlock.Object ) {
						newState = SerReadState.ReadMemberId;
					}
					break;
				case ReadMemberId:
					if ( ctx.block == JsonBlock.Entry ) {
						selectMember(ctx.param);
						return relayJson(true);
					}
					break;
				}
				break;
			case END:
				switch ( readState ) {
				case ReadEntityId:
					if ( ctx.block == JsonBlock.Entry ) {
						newState = readState;
					} else if ( ctx.block == JsonBlock.Object ) {
						newState = SerReadState.ReadSerialObject;
						endSerialObject();
					}
					break;
				case ReadEntityObject:
					if ( ctx.block == JsonBlock.Object ) {
						newState = SerReadState.ReadEntityId;
					}
					break;
				case ReadMemberId:
					if ( ctx.block == JsonBlock.Entry ) {
						setValue(jsonReader.getRoot());
						newState = SerReadState.ReadMemberId;
					} else if ( ctx.block == JsonBlock.Object ) {
						endEntityObject();
						newState = SerReadState.ReadEntityId;
					}
					break;
				}
				break;
			case RELEASE:
				newState = SerReadState.ReadEntityId;
				break;
			}

			if ( null != newState ) {
				readState = newState;
			} else {
				DustGenLog.log("Serial read - Unhandled event State:", readState, "Action:", action, "Context:",
						getActionCtx());
//				DustGenException.throwException(null, action, ctx, "Unhandled event from state", readState);
			}

			return DustResultType.ACCEPT_READ;
		}
	}

	SerialReader serReader;

	DustGenFactory<String, ArrayList<DustEntityDelta>> factPostponedDelta = new DustGenFactory<String, ArrayList<DustEntityDelta>>(
			null) {
		private static final long serialVersionUID = 1L;

		@Override
		protected ArrayList<DustEntityDelta> createItem(String key, Object hint) {
			return new ArrayList<>();
		}
	};

	public ArkDockJsonSerializerReaderAgent(ArkDockUnit uTarget) {
		dslModel = ArkDock.getDsl(DslModel.class);
		dslIdea = ArkDock.getDsl(DslIdea.class);		
	}

	@Override
	public void setVisitor(ArkDockVisitor<JsonContext> visitor) {
		this.visitor = visitor;
	}

	public Object getHeader() {
		return header;
	}

	@SuppressWarnings("unchecked")
	private void setHeader(Object h) {
		header = (Map<String, Object>) h;

		if ( null != header ) {
			String sections = (String) header.get("PayloadSections");
			if ( !DustGenUtils.isEmpty(sections) ) {
				payloadSections = new ArrayList<>();
				for (String ps : sections.split(",")) {
					payloadSections.add(DustGenUtils.fromString(ps.trim(), ReadState.class));
				}
			}
		}
	}

	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {
		DustGenLog.log("State:", readState, "Action:", action, "Context:", getActionCtx());
		ReadState newState = null;

		switch ( action ) {
		case INIT:
			readState = ReadState.Init;
			return DustResultType.ACCEPT_READ;
		case RELEASE:
			readState = null;
			return DustResultType.ACCEPT;

		case BEGIN:
			switch ( readState ) {
			case Init:
				if ( ctx.block == JsonBlock.Array ) {
					newState = ReadState.ReadHeader;
				}
				break;
			case ReadHeader:
				relayJson(false);
				break;
			case ReadSerial:
				relayChild();
				break;
			}
			break;
		case PROCESS:
			if ( readState == ReadState.ReadHeader ) {
				setHeader(ctx.param);
				newState = ReadState.ReadSerial;
			}
			break;
		case END:
			switch ( readState ) {
			case Init:
				newState = readState;
				break;
			case ReadHeader:
				setHeader(jsonReader.getRoot());
				newState = ReadState.ReadSerial;
				break;
			case ReadSerial:
				newState = (ctx.block == JsonBlock.Array) ? ReadState.Init : ReadState.ReadSerial;
				break;
			}
			break;
		}

		if ( null != newState ) {
			readState = newState;
		} else {
//			DustGenException.throwException(null, action, ctx, "Unhandled event from state", readState);
		}

		return DustResultType.ACCEPT_READ;
	}

	public DustResultType relayChild() throws Exception {
		DustAgent agent = null;
		boolean reset = false;

		switch ( readState ) {
		case ReadSerial:
			if ( null == serReader ) {
				serReader = new SerialReader();
				serReader.setActionCtx(getActionCtx());
			} else {
				reset = true;
			}
			agent = serReader;
			break;
		default:
			break;
		}

		if ( reset ) {
			agent.agentAction(DustAgentAction.RELEASE);
		}
		agent.agentAction(DustAgentAction.INIT);

		return visitor.setRelay(agent, false);
	}

	public DustResultType relayJson(boolean shareCtx) throws Exception {
		if ( null == jsonReader ) {
			jsonReader = new ArkDockJsonReaderAgent(visitor);
			jsonReader.setActionCtx(getActionCtx());
		} else {
			jsonReader.agentAction(DustAgentAction.RELEASE);
		}

		jsonReader.agentAction(DustAgentAction.INIT);
		return visitor.setRelay(jsonReader, shareCtx);
	}
}
