package ark.dock.io.json;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ark.dock.ArkDock;
import ark.dock.ArkDockMindUtils;
import ark.dock.ArkDockUnit;
import ark.dock.ArkDockUtils;
import ark.dock.ArkDockVisitor;
import dust.gen.DustGenConsts;
import dust.gen.DustGenDevUtils.DevTimer;
import dust.gen.DustGenException;
import dust.gen.DustGenFactory;
import dust.gen.DustGenLog;

public class ArkDockJsonSerializerReader implements DustGenConsts.DustAgent, ArkDockJsonConsts {
	enum ReadState {
		Init, Header, /*HeaderContent,*/ Entity, EntityContent, EntityContentValue, EntityContentCustom
	}

	private ArkDockUnit target;
//	private ArkDockDslBuilder meta;

	private final ArkDockVisitor<JsonContext> visitor;
	private final JsonContext ctx = new JsonContext();

	Map<DustEntity, JsonFormatter> formatters;

	ArkDockJsonReaderAgent jsonReader;
	Object header;

	ReadState readState;

	String globalId;
	DustEntity eTarget;
	Map<DustEntity, Object> newEntity = new HashMap<>();
	
	final DslNative dslNative;
	final DslModel dslModel;

	DustGenFactory<DustEntity, DustMemberDef> factMemberDef = new DustGenFactory<DustEntity, DustMemberDef>(null) {
		private static final long serialVersionUID = 1L;

		@Override
		protected DustMemberDef createItem(DustEntity key, Object hint) {
			return ArkDock.getMind().getMemberDef(key, hint, null);
		}
	};

	DustGenFactory<String, ArrayList<DustEntityDelta>> factPostponedDelta = new DustGenFactory<String, ArrayList<DustEntityDelta>>(
			null) {
		private static final long serialVersionUID = 1L;

		@Override
		protected ArrayList<DustEntityDelta> createItem(String key, Object hint) {
			return new ArrayList<>();
		}
	};

	String memberId;
	DustEntity eMember;
	DustMemberDef md = null;

	public ArkDockJsonSerializerReader() {
		this.visitor = new ArkDockVisitor<>(ctx, this);
		jsonReader = new ArkDockJsonReaderAgent(visitor);
		
		dslNative = ArkDock.getDsl(DslNative.class);
		dslModel = ArkDock.getDsl(DslModel.class);

	}

	public void addFormatter(DustEntity member, JsonFormatter fmt) {
		if ( null == formatters ) {
			formatters = new HashMap<>();
		}

		formatters.put(member, fmt);
	}

	public void parse(Reader r, ArkDockUnit target) throws IOException, ParseException {
		DevTimer parseTimer = new DevTimer("Parse");

		this.target = target;
//		this.meta = target.getMeta();
		
		JSONParser p = new JSONParser();
		JsonContentDispatcher h = new JsonContentDispatcher(visitor);
		p.parse(r, h);

		parseTimer.log();
	}

	/**
	 * As reading JSON, types "should be OK", state management is checked by setting
	 * newState in all valid cases. Everything else is an error in the JSON input
	 * and exception is thrown.
	 */
	@SuppressWarnings({ "incomplete-switch" })
	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {
		ReadState newState = null;

		switch ( action ) {
		case INIT:
			newState = ReadState.Init;
			break;
		case BEGIN:
			switch ( readState ) {
			case Entity:
				switch ( ctx.block ) {
				case Object:
					newState = ReadState.EntityContent;
					break;
				}
				break;
			case EntityContentValue:
				switch ( ctx.block ) {
				case Array:
				case Object:
					jsonReader.agentAction(DustAgentAction.INIT);
					visitor.setRelay(jsonReader, false);
					newState = ReadState.EntityContentCustom;
					break;
				}
				break;
			case EntityContent:
				switch ( ctx.block ) {
				case Entry:
					memberId = (String) ctx.param;
//					setCurrMember(meta.getEntity(memberId));
					setCurrMember(ArkDock.getByGlobalId(memberId));
					newState = ReadState.EntityContentValue;
					break;
				case Array:
				case Object:
					visitor.setRelay(jsonReader, false);
					newState = ReadState.EntityContentCustom;
					break;
				}

				break;
			case Header:
				visitor.setRelay(jsonReader, false);
				newState = ReadState.Header;
//				newState = ReadState.HeaderContent;
				break;
			case Init:
				switch ( ctx.block ) {
				case Array:
					newState = ReadState.Header;
					break;
				case Entry:
					globalId = (String) ctx.param;
					newState = ReadState.Entity;
					eTarget = target.getEntity(globalId);
					if ( null == eTarget ) {
						DustGenLog.log(DustEventLevel.INFO, "Reading new entity", globalId);
						newEntity.clear();
					}
					break;
				case Object:
					newState = readState;
					break;
				}
				break;
			}
			break;
		case END:
			switch ( readState ) {
			case Init:
				switch ( ctx.block ) {
				case Object:
				case Array:
					newState = readState;
					break;
				}
				break;
//			case HeaderContent:
			case Header:
				header = jsonReader.getRoot();
				newState = ReadState.Init;
				break;
			case Entity:
				if ( null == eTarget ) {
					String unit = ArkDockUtils.getSegment(globalId, TokenSegment.UNIT);
					DustEntity eT = (DustEntity) newEntity.get(dslModel.memEntityPrimaryType);
					String id = (String) newEntity.get(dslModel.memEntityId);
					
//					DustEntity eU = meta.getUnit(unit);
//					eTarget = target.getEntity(eU, eT, id, true);
					eTarget = ArkDock.getMind().getUnit(unit, null).getEntity(eT, id, true);
					
					DustEntity nativeKey = null;
					Object nativeOb = null;

					for (Map.Entry<DustEntity, Object> e : newEntity.entrySet()) {
						DustEntity key = e.getKey();
						
						if ( (key == dslNative.memNativeValueOne) || (key == dslNative.memNativeValueArr)) {
							nativeKey = key;
							nativeOb = e.getValue();
						} else {
							setCurrMember(key);
							setValueCustom(e.getValue());
						}
					}
					
					if ( null != nativeOb ) {
						DustEntity nt = target.getMember(eTarget, dslNative.memNativeValType, null, null);
						JsonFormatter fmt = formatters.get(nt);
						if ( null != fmt ) {
							nativeOb = fmt.fromParsedData(nativeOb);
						}
						target.setMember(eTarget, nativeKey, nativeOb, null);
					}
					
					ArrayList<DustEntityDelta> pl = factPostponedDelta.peek(globalId);
					if ( null != pl ) {
						for ( DustEntityDelta d : pl ) {
							target.accessMember(eTarget, d);
						}
					}
				}
				ArkDockMindUtils.optUpdateMeta(eTarget);
//				newState = ReadState.Init;
				newState = readState;
				break;
			case EntityContent:
				newState = ReadState.Entity;
				break;
			case EntityContentValue:
				newState = ReadState.EntityContent;
				break;
			case EntityContentCustom:
				Object r = jsonReader.getRoot();
				setValueCustom(r);

				newState = ReadState.EntityContent;
				break;
			}
			break;
		case PROCESS:
			switch ( readState ) {
			case Header:
				header = ctx.param;
				newState = readState;
//				newState = ReadState.Init;
				break;
			case EntityContentValue:
				setValue(ctx.param);
				newState = ReadState.EntityContentValue;
				break;
			default:
				break;

			}
			break;
		case RELEASE:
			newState = ReadState.Init;
			break;
		}

		if ( null != newState ) {
			readState = newState;
		} else {
			DustGenException.throwException(null, action, ctx, " Unhandled event from state", readState);
		}

		return DustResultType.ACCEPT_READ;
	}

	private void setCurrMember(DustEntity eM) {
		eMember = eM;

		if ( null == eMember ) {
			DustGenLog.log(DustEventLevel.WARNING, "Reading unknown member", memberId);
			md = null;
		} else {
			md = factMemberDef.get(eMember);
		}
	}

	@SuppressWarnings("rawtypes")
	private void setValueCustom(Object object) {
		DustCollType ct = ArkDockUtils.getCollType(md);
		switch ( ct ) {
		case ARR:
		case SET:
			ArrayList al = (ArrayList) object;
			int size = al.size();
			for (int i = 0; i < size; ++i) {
				setValue(al.get(i), DustDialogCmd.ADD, (ct == DustCollType.ARR) ? i : null);
			}
			break;
		case MAP:
			for (Object o : ((Map) object).entrySet()) {
				Map.Entry e = (Map.Entry) o;
				DustEntity k = target.getEntity((String) e.getKey());
				setValue(e.getValue(), DustDialogCmd.ADD, k);
			}
			break;
		case ONE:
			setValue(object);
			break;
		}
	}
	void setValue(Object val) {
		setValue(val, DustDialogCmd.SET, null);
	}

	void setValue(Object val, DustDialogCmd cmd, Object key) {
		if ( null != formatters ) {
			JsonFormatter fmt = formatters.get(eMember);
			if ( null != fmt ) {
				val = fmt.fromParsedData(val);
			}
		}
		DustValType vt = md.getValType();

		if ( null != vt ) {
			switch ( vt ) {
			case REF: {
				if ( val instanceof String ) {
					String refId = (String) val;
					DustEntity eRef = target.getEntity(refId);
					
					if ( null == eRef ) {
						eRef = ArkDock.getByGlobalId(refId);
					}

					if ( null == eRef ) {
						factPostponedDelta.get(refId)
								.add(new DustEntityDelta(cmd, eTarget, globalId, eMember, val, key));
						return;
					} else {
						val = eRef;
					}
				}
			}
				break;
			default:
				break;
			}
		}

		if ( null == eTarget ) {
			if ( cmd == DustDialogCmd.SET ) {
				newEntity.put(eMember, val);
			} else {
				factPostponedDelta.get(globalId).add(new DustEntityDelta(cmd, eTarget, globalId, eMember, val, key));
			}
		} else {
			target.accessMember(cmd, eTarget, eMember, val, key);
		}
	}

}
