package ark.dock;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface ArkDockConsts {

	public interface ArkHasDefault<T> {
		T getDefault();
	}

	public enum ArkEventLevel {
		CRITICAL, ERROR, WARNING, INFO, TRACE, DEBUG
	}

	public static class ArkInitParams<T extends Enum<T>> {
		Map<T, String> pv = new HashMap<>();

		public ArkInitParams(String[] args, Class<T> tc) {
			Pattern p = Pattern.compile("-(\\w*)=(\\S*)");
			for (String arg : args ) {
				Matcher m = p.matcher(arg);
				
				if (m.matches() ) {
					String k = m.group(1);
					T pk = ArkDockUtils.fromString(k, tc);
					if ( null != pk ) {
						pv.put(pk, m.group(2));
					}
				}
			}
		}

		@SuppressWarnings("unchecked")
		public String getString(T param) {
			String v = pv.get(param);
			return (null == v) ? ((ArkHasDefault<String>)param).getDefault() : v;
		}

		public int getInt(T param) {
			return Integer.parseInt(getString(param));
		}
	}
}