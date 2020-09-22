package ark.dock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dust.gen.DustGenUtils;

public class ArkDockUtils extends DustGenUtils implements ArkDockConsts {
	public static String buildGlobalId(String unitId, String typeId, String id) {
		return DustGenUtils.sbAppend(null, TOKEN_SEP, true, unitId, typeId, id).toString();
	}

	static DustCollType getCollTypeForHint(Object hint) {
		if ( null == hint ) {
			return DustCollType.SET;
		} else if ( hint instanceof Number ) {
			return DustCollType.ARR;
		} else if ( hint instanceof DustEntity ) {
			return DustCollType.MAP;
		}
		return DustException.throwException(null, "Should never get here");
	}

	static DustCollType getCollTypeForData(Object data) {
		if ( data instanceof ArrayList ) {
			return DustCollType.ARR;
		} else if ( data instanceof Set ) {
			return DustCollType.SET;
		} else if ( data instanceof Map ) {
			return DustCollType.MAP;
		}
		
		return null;
	}

	static DustValType getValTypeForValue(Object val) {
		if ( null == val ) {
			return null;
		} else if ( val instanceof DustEntity ) {
			return DustValType.REF;
		} else if ( val instanceof Number ) {
			return (val instanceof Long) ? DustValType.INT : DustValType.REAL;
		} else {
			return DustValType.RAW;
		}
	}

	static Object createContainer(DustCollType ct, Map<DustEntity, Object> data, DustEntity member) {
		Object container = createContainer(ct);
		data.put(member, container);
		return container;
	}

	static Object createContainer(DustCollType ct) {
		switch ( ct ) {
		case ARR:
			return new ArrayList<>();
		case MAP:
			return new HashMap<>();
		case SET:
			return new HashSet<>();
		case ONE:
			return null;
		default:
			return DustException.throwException(null, "Should never get here");
		}
	}

	@SuppressWarnings("rawtypes")
	static Object resolveValue(MetaMemberInfo mi, Object v, Object hint) {
		DustCollType ct = (null == mi) ? null : mi.getCollType();
		if ( (null == ct) || (null == v) ) {
			return v;
		}

		switch ( ct ) {
		case ARR:
			if ( v instanceof ArrayList ) {
				ArrayList arr = (ArrayList) v;
				int idx = ((Number) hint).intValue();
				return ((0 <= idx) && (idx < arr.size())) ? arr.get(idx) : null;
			} else {
				return v;
			}
		case MAP:
			return ((Map) v).get(hint);
		case SET:
			if ( v instanceof Set ) {
				Set set = (Set) v;
				return set.isEmpty() ? null : (null == hint) ? set.iterator().next() : set.contains(hint) ? hint : null;
			} else {
				return v;
			}
		case ONE:
			return v;
		default:
			return DustException.throwException(null, "Should never get here");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	static boolean setValue(DustDialogCmd cmd, MetaMemberInfo mi, Map<DustEntity, Object> data, DustEntity member,
			Object currVal, Object newVal, Object hint) {
		DustCollType ct = (null == mi) ? null : mi.getCollType();

		if ( (cmd == DustDialogCmd.ADD) && (null == ct) ) {
			((ArkDockModelMeta.MemberInfo) mi).ct = ct = ArkDockUtils.getCollTypeForHint(hint);
		}

		if ( (null == ct) || (ct == DustCollType.ONE) ) {
			if ( DustGenUtils.isEqual(currVal, newVal) ) {
				return false;
			} else {
				data.put(member, newVal);
				return true;
			}
		}

		switch ( ct ) {
		case ARR:
			ArrayList arr;

			if ( currVal instanceof ArrayList ) {
				arr = (ArrayList) currVal;
			} else {
				if ( (cmd == DustDialogCmd.SET) && (DustGenUtils.isEqual(currVal, newVal)) ) {
					return false;
				}
				arr = (ArrayList) createContainer(ct, data, member);
				arr.add(currVal);
			}

			int idx = ((Number) hint).intValue();
			if ( (0 <= idx) && (idx < arr.size()) ) {
				if ( cmd == DustDialogCmd.SET ) {
					if ( DustGenUtils.isEqual(arr.get(idx), newVal) ) {
						return false;
					}
					arr.set(idx, newVal);
				} else {
					arr.add(idx, newVal);
				}
			} else {
				arr.add(newVal);
			}
			return true;
		case MAP:
			Map map = (Map) currVal;
			if ( DustGenUtils.isEqual(map.get(hint), newVal) ) {
				return false;
			}
			map.put(hint, newVal);
			return true;
		case SET:
			Set set;
			if ( currVal instanceof Set ) {
				set = (Set) currVal;
				if ( cmd == DustDialogCmd.SET ) {
					set.clear();
				}
			} else {
				if ( (cmd == DustDialogCmd.SET) && (DustGenUtils.isEqual(currVal, newVal)) ) {
					return false;
				}
				set = (Set) createContainer(ct, data, member);
				set.add(currVal);
			}

			return set.add(newVal);
		default:
			return DustException.throwException(null, "Should never get here");
		}
	}
}