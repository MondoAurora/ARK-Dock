package ark.dock.text;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dust.gen.DustGenUtils;

public class ArkDockTextRegex implements ArkDockTextConsts {

	Pattern pattern;
	Map<String, StringConverter> reader = new TreeMap<String, StringConverter>();

	public ArkDockTextRegex(String regex, int regexFlags, Map<String, StringConverter> converters) {
		this.pattern = Pattern.compile(regex, regexFlags);

		Matcher m = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>").matcher(regex);
		while (m.find()) {
			String name = m.group(1);
			reader.put(name, (null == converters) ? null : converters.get(name));
		}
	}

	public boolean matchAndRead(String from, Map<String, Object> target) {
		Matcher m = pattern.matcher(from);
		if ( !m.matches() ) {
			return false;
		}

		for (Map.Entry<String, StringConverter> re : reader.entrySet()) {
			String key = re.getKey();
			String gs = m.group(key);
			if ( !DustGenUtils.isEmpty(key) ) {
				StringConverter sc = re.getValue();
				target.put(key, (null == sc) ? gs : sc.fromStr(gs));
			}
		}

		return true;
	}
	
	public static class Loader<KeyType> extends ArkDockTextRegex {
		public final KeyType key;

		public Loader(KeyType key, String regex, int regexFlags, Map<String, StringConverter> converters) {
			super(regex, regexFlags, converters);
			this.key = key;
		}
	}
}
