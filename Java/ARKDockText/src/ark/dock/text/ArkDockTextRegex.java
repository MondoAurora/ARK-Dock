package ark.dock.text;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dust.gen.DustGenUtils;

public class ArkDockTextRegex implements ArkDockTextConsts {

	Pattern pattern;
	Set<String> reader = new TreeSet<>();

	public ArkDockTextRegex(String regex, int regexFlags, Map<String, StringConverter> converters) {
		this.pattern = Pattern.compile(regex, regexFlags);

		Matcher m = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>").matcher(regex);
		while (m.find()) {
			reader.add( m.group(1));
		}
	}

	public ArkDockTextRegex(String regex, int regexFlags) {
		this(regex, regexFlags, null);
	}

	public ArkDockTextRegex(String regex) {
		this(regex, 0, null);
	}

	public boolean matchAndRead(String from, Map<String, String> target) {
		Matcher m = pattern.matcher(from);
		if ( !m.matches() ) {
			return false;
		}

		for (String key : reader) {
			String gs = m.group(key);
			if ( !DustGenUtils.isEmpty(key) ) {
				target.put(key, gs);
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
		Map<String, String> content;
		int count;
		ArrayList<ArkDockTextRegex> arrRx;
		
		public Parser(TextMatchListener listener, boolean callOnFail, ArkDockTextRegex... rxs) {
			this.listener = listener;
			this.callOnFail = callOnFail;
			
			arrRx = new ArrayList<>(count = rxs.length);
			for (ArkDockTextRegex rx : rxs) {
				arrRx.add(rx);
			}
			
			this.content = new TreeMap<>();
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
						listener.processMatch(match, content);
					}
				}
			}
		}
	}
}
