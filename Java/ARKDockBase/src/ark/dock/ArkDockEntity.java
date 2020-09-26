package ark.dock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import dust.gen.DustGenConsts.DustEntity;
import dust.gen.DustGenUtils;

class ArkDockEntity implements DustEntity, ArkDockConsts {
	final ArkDockModel model;
	final String id;
	final String globalId;
	Map<DustEntity, Object> data = new HashMap<>();

	public ArkDockEntity(ArkDockModel model, String globalId) {
		this.model = model;
		this.globalId = globalId;
		this.id = globalId.substring(globalId.lastIndexOf(ArkDockModel.TOKEN_SEP) + 1);
	}

	@Override
	public String toString() {
		return globalId;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <RetType> RetType accessMember(DustDialogCmd cmd, DustEntity member, Object value, Object hint) {
		Object ret;

		Object val = data.get(member);
		DustMemberDef md = model.meta.getMemberDef(member, value, hint);
		DustCollType ct = (null == md) ? null : md.getCollType();

		Object rv;

		switch ( cmd ) {
		case GET:
			ret = value;

			rv = ArkDockUtils.resolveValue(md, val, hint);
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
					ret = ArkDockUtils.setValue(cmd, md, data, member, val, value, hint);
				}
			}
			break;
		case CHK:
			ret = DustGenUtils.isEqual(value, ArkDockUtils.resolveValue(md, val, hint));
			break;
		case DEL:
			if ( (null == hint) || (null == md) || (ct == DustCollType.ONE) ) {
				ret = (null != data.remove(member));
			} else {
				rv = ArkDockUtils.resolveValue(md, val, hint);
				if ( null != rv ) {
					switch ( md.getCollType() ) {
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
			DustMemberDef md = model.meta.getMemberDef(member, null, null);
			DustCollType ct = (null == md) ? null : md.getCollType();

			if ( (null != ct) ) {
				switch ( ct ) {
				case ARR:
					return (val instanceof ArrayList) ? ((ArrayList) val).size() : 1;
				case MAP:
					return (val instanceof Map) ? ((Map) val).size() : 1;
				case SET:
					return (val instanceof Set) ? ((Set) val).size() : 1;
				default:
					break;
				}
			}

			return 1;
		}

		return 0;
	}
}