package ark.dock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import dust.gen.DustGenConsts.DustEntity;
import dust.gen.DustGenUtils;

public class ArkDockModel implements ArkDockConsts, Iterable<DustEntity> {

	class ModelEntity implements DustEntity {
		final String id;
		final String globalId;
		Map<DustEntity, Object> data = new HashMap<>();

		public ModelEntity(String globalId) {
			this.globalId = globalId;
			this.id = globalId.substring(globalId.lastIndexOf(TOKEN_SEP) + 1);
		}

		@Override
		public String toString() {
			return globalId;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public <RetType> RetType accessMember(DustDialogCmd cmd, DustEntity member, RetType value, Object hint) {
			Object ret;

			Object val = data.get(member);
			MetaMemberInfo mi = meta.getMemberInfo(member, value, hint);
			DustCollType ct = (null == mi) ? null : mi.getCollType();

			Object rv;

			switch ( cmd ) {
			case GET:
				ret = value;

				rv = ArkDockUtils.resolveValue(mi, val, hint);
				if ( null != rv ) {
					ret = rv;
				}
				break;
			case SET:
			case ADD:
				if ( null == val ) {
					if ( ct == DustCollType.MAP ) {
						Map m = (Map) ArkDockUtils.createContainer(DustCollType.MAP, data, member);
						m.put(hint, value);
					} else {
						data.put(member, value);
					}
					ret = true;
				} else {
					if ( (cmd == DustDialogCmd.SET) && DustGenUtils.isEqual(val, value) ) {
						ret = false;
					} else {
						ret = ArkDockUtils.setValue(cmd, mi, data, member, val, value, hint);
					}
				}
				break;
			case CHK:
				ret = DustGenUtils.isEqual(value, ArkDockUtils.resolveValue(mi, val, hint));
				break;
			case DEL:
				if ( (null == hint) || (null == mi) || (ct == DustCollType.ONE) ) {
					ret = (null != data.remove(member));
				} else {
					rv = ArkDockUtils.resolveValue(mi, val, hint);
					if ( null != rv ) {
						switch ( mi.getCollType() ) {
						case ARR:
							((ArrayList) val).remove((int) hint);
							break;
						case MAP:
							((Map) val).remove(hint);
							break;
						case SET:
							((Set) val).remove(hint);
							break;
						default:
							break;
						}
						ret = true;
					} else {
						ret = false;
					}
				}
				break;
			default:
				ret = false;
				break;
			}

			return (RetType) ret;
		}

		@SuppressWarnings("rawtypes")
		public int getCount(DustEntity member) {
			Object val = data.get(member);
			
			if ( null != val ) {
				MetaMemberInfo mi = meta.getMemberInfo(member, null, null);
				DustCollType ct = (null == mi) ? null : mi.getCollType();
				
				if ( (null != ct) ) {
					switch ( ct ) {
					case ARR:
						return ( val instanceof ArrayList) ? ((ArrayList)val).size() : 1;
					case MAP:
						return ( val instanceof Map) ? ((Map)val).size() : 1;
					case SET:
						return ( val instanceof Set) ? ((Set)val).size() : 1;
					default:
						break;
					}
				}
				
				return 1;
			}
			
			
			return 0;
		}
	}

	MetaProvider meta;
	ArkDockModel parent;

	Map<String, ModelEntity> entities = new HashMap<>();

	public ArkDockModel(ArkDockModel parent_) {
		this.parent = parent_;
		meta = parent.getMeta();
	}

	protected ArkDockModel() {
	}

	public MetaProvider getMeta() {
		return meta;
	}

	public ArkDockModel getParent() {
		return parent;
	}

	public DustEntity getEntity(DustEntity unit, DustEntity type, String itemId, boolean createIfMissing) {
		String globalId = ArkDockUtils.buildGlobalId(((ModelEntity) unit).id, ((ModelEntity) type).id, itemId);
		return getEntity(globalId, createIfMissing);
	}

	public DustEntity getEntity(String globalId, boolean createIfMissing) {
		ModelEntity e = entities.get(globalId);

		if ( createIfMissing && (null == e) ) {
			e = new ModelEntity(globalId);
			entities.put(globalId, e);
			initEntity(e);
		}

		return e;
	}

	protected void initEntity(ModelEntity e) {
	}

	public int getCount(DustEntity e, DustEntity member) {
		return (e instanceof ModelEntity) ? ((ModelEntity) e).getCount(member) : 0;
	}

	public String getId(DustEntity e) {
		return (e instanceof ModelEntity) ? ((ModelEntity) e).id : null;
	}

	public String getGlobalId(DustEntity e) {
		return (e instanceof ModelEntity) ? ((ModelEntity) e).globalId : null;
	}

	@SuppressWarnings("unchecked")
	public <RetType> RetType accessMember(DustDialogCmd cmd, DustEntity e, DustEntity member, RetType value,
			Object hint) {
		Object ret = (cmd == DustDialogCmd.GET) ? value : false;

		if ( null != e ) {
			ret = ((ModelEntity) e).accessMember(cmd, member, value, hint);
		}

		return (RetType) ret;
	}

	public boolean setMember(DustEntity e, DustEntity member, Object value, Object hint) {
		return (boolean) accessMember(DustDialogCmd.SET, e, member, value, hint);
	}

	public <RetType> RetType getMember(DustEntity e, DustEntity member, RetType defValue, Object hint) {
		return accessMember(DustDialogCmd.GET, e, member, defValue, hint);
	}

	public Iterator<DustEntity> getContent(DustEntity e, DustEntity member) {
		return ((ModelEntity) e).data.keySet().iterator();
	}

	class ContentReader implements Iterator<DustEntity> {
		Iterator<ModelEntity> mi;

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
			DustException.throwException(null, "Should not remove Entity in this way! Use the API.");
		}
	}

	@Override
	public Iterator<DustEntity> iterator() {
		return new ContentReader();
	}
}
