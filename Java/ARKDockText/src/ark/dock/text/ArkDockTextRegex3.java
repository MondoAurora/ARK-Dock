package ark.dock.text;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dust.gen.DustGenUtils;

public class ArkDockTextRegex3 implements ArkDockTextConsts {

	Pattern pattern;
	Map<String, StringConverter> reader = new TreeMap<String, StringConverter>();

	public ArkDockTextRegex3(String regex, int regexFlags, Map<String, StringConverter> converters) {
		this.pattern = Pattern.compile(regex, regexFlags);

		Matcher m = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>").matcher(regex);
		while (m.find()) {
			String name = m.group(1);
			reader.put(name, (null == converters) ? null : converters.get(name));
		}
	}

	public ArkDockTextRegex3(String regex, int regexFlags) {
		this(regex, regexFlags, null);
	}

	public ArkDockTextRegex3(String regex) {
		this(regex, 0, null);
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

//	public static class Loader<KeyType> extends ArkDockTextRegex {
//		public final KeyType key;
//
//		public Loader(KeyType key, String regex, int regexFlags, Map<String, StringConverter> converters) {
//			super(regex, regexFlags, converters);
//			this.key = key;
//		}
//	}

	public static class Parser {
		TextMatchListener listener;
		boolean callOnFail;
		Map<String, Object> content;
		int count;
		ArrayList<ArkDockTextRegex3> arrRx;
		
		public Parser(TextMatchListener listener, boolean callOnFail, ArkDockTextRegex3... rxs) {
			this.content = new TreeMap<>();
			
			arrRx = new ArrayList<>(count = rxs.length);
			for (ArkDockTextRegex3 rx : rxs) {
				arrRx.add(rx);
			}
		}

		public void read(Reader reader) throws Exception {

			try (BufferedReader br = new BufferedReader(reader)) {
				for (String line = br.readLine(); null != line; line = br.readLine()) {
					content.clear();
					content.put("", line);
					int match = -1;
					for (int i = 0; (i < count) && ( -1 == match); ++i) {
						if ( arrRx.get(i).matchAndRead(line, content)) {
							match = i;
						}
					}
					
					if ( callOnFail || (-1 != match) ) {
//						listener.processMatch(match, content);
					}
				}
			}
		}
	}
}
