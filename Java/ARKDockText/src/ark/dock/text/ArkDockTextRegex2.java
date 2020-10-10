package ark.dock.text;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ark.dock.ArkDockModel;
import ark.dock.ArkDockModelMeta;
import dust.gen.DustGenLog;
import dust.gen.DustGenUtils;

public class ArkDockTextRegex2 implements ArkDockTextConsts {

	class GroupReader {
		String key;
		DustEntity token;
		StringConverter cvt;

		public GroupReader(String key, DustEntity token, StringConverter cvt) {
			super();
			this.key = key;
			this.token = token;
			this.cvt = cvt;
		}

		boolean update(DustEntity target, Matcher m, HintProvider hp) {
			String gs = m.group(key);

			if ( !DustGenUtils.isEmpty(gs) ) {
				Object val = (null != cvt) ? cvt.fromStr(gs) : gs;
				Object hint = (null == hp) ? null : hp.getHint(target, token, val);
				model.setMember(target, token, val, hint);
				return true;
			}
			return false;
		}
	}

	ArkDockModel model;
	Pattern pattern;

	Map<String, GroupReader> reader = new TreeMap<String, GroupReader>();

	public ArkDockTextRegex2(ArkDockModel model_, String regex, int regexFlags,
			Map<DustEntity, StringConverter> converters) {
		this.model = model_;
		this.pattern = Pattern.compile(regex, regexFlags);

		ArkDockModelMeta meta = model.getMeta();
		Matcher m = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>").matcher(regex);
		while (m.find()) {
			String name = m.group(1);
			DustEntity e = meta.getEntity(name);

			if ( null == e ) {
				DustGenLog.log(DustEventLevel.WARNING, "Missing token for name", name, "in regex", regex);
			} else {
				reader.put(name, new GroupReader(name, e, (null == converters) ? null : converters.get(e)));
			}
		}
	}

	public boolean matchAndRead(String from, DustEntity target, HintProvider hp) {
		Matcher m = pattern.matcher(from);
		if ( !m.matches() ) {
			return false;
		}

		for (GroupReader gr : reader.values()) {
			gr.update(target, m, hp);
		}

		return true;
	}
}
