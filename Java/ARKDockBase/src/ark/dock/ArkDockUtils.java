package ark.dock;

import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ArkDockUtils implements ArkDockConsts {
	
	public static String strTimestamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd_HH-mm-ss_SSS");
		return sdf.format(new Date());
	}
	
	static PrintStream psLog = System.out;
	static ArkEventLevel logLevel = ArkEventLevel.INFO;

	public static synchronized void log(ArkEventLevel lvl, Object... obs) {
		if ( 0 < lvl.compareTo(logLevel) ) {
			return;
		}
		
		boolean first = true;

		for (Object o : obs) {
			String s = (null == o) ? "" : o.toString();

			if (first && !s.trim().isEmpty()) {
				first = false;
				psLog.print(strTimestamp());
				psLog.print(" ");
				psLog.print(lvl);
				psLog.print(" ");
			}
			psLog.print(s);
			psLog.print(" ");
		}

		if (!first) {
			psLog.println();
			psLog.flush();
		}
	}

	public static void log(Object... obs) {
		log(ArkEventLevel.INFO, obs);
	}

	public static void setLogLevel(ArkEventLevel lvl) {
		logLevel = lvl;
	}

	public static void setLogFile(String fName) throws Exception {
		if ( (null != psLog) && (psLog != System.out) ) {
			psLog.flush();
			psLog.close();
		}
		
		File f = new File(fName).getAbsoluteFile();
		File p = f.getParentFile();
		
		if (!p.exists() ) {
			p.mkdirs();
		}
		
		psLog = new PrintStream(fName + "_" + strTimestamp() + ".log");
	}
	
	private static final Map<Class<?>, Map<String, Enum<?>>> ENUM_MAP = new HashMap<Class<?>, Map<String,Enum<?>>>();

	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> T fromString(String str, Class<T> ec) {
		Map<String, Enum<?>> m;
		
		synchronized (ENUM_MAP) {
			m = ENUM_MAP.get(ec);
			
			if ( null == m ) {
				m = new TreeMap<>();
				for ( Enum<?> e : ec.getEnumConstants() ) {
					m.put(e.name(), e);
				}
				ENUM_MAP.put(ec, m);
			}
		}
		
		return (T) m.get(str);
	}

	public static <T extends Enum<T>> T fromString(String str, T defVal) {
		@SuppressWarnings("unchecked")
		T ret = (T) fromString(str, defVal.getClass());
		return (null == ret) ? defVal : ret;
	}

	public static StringBuilder collToString(String sep, Collection<?> coll) {
		if ((null == coll) || coll.isEmpty()) {
			return null;
		}

		StringBuilder sb = null;

		for (Object o : coll) {
			String s = toStringSafe(o);
			if (!isEmpty(s)) {
				if (null == sb) {
					sb = new StringBuilder();
				} else {
					sb.append(sep);
				}
				sb.append(s);
			}
		}

		return sb;
	}

	public static boolean isEmpty(String value) {
		return (null == value) || value.isEmpty();
	}

	public static String toStringSafe(Object value) {
		return (null == value) ? null : value.toString();
	}
}