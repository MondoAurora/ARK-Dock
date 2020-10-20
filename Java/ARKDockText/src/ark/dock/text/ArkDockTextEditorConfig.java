package ark.dock.text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.TreeMap;

import dust.gen.DustGenUtils;

public class ArkDockTextEditorConfig implements ArkDockTextConsts, ArkDockTextConsts.EditorConfigValues {
	enum EditorConfigLineType {
		Section, Entry, Comment, Unknown
	}

	public static class EditorConfigContext {
		Map<String, Object> data = new TreeMap<>();

		EditorConfigLineType lineType = EditorConfigLineType.Unknown;
		long lineNum = 0;
		
		@SuppressWarnings("unchecked")
		public <RetType> RetType get(String key) {
			return (RetType) data.get(key);
		}

		void reset() {
			lineNum = 0;
			lineType = EditorConfigLineType.Unknown;
			data.clear();
		}

		void load(Map<String, String> src, String... keys) {
			for (String k : keys) {
				data.put(k, src.get(k));
			}
		}

		private void optCloseSection(ArkDockAgent<EditorConfigContext> target) throws Exception {
			data.put(KEY, null);
			data.put(VALUE, null);

			if ( null != data.get(SECTION) ) {
				target.agentAction(DustAgentAction.END);
				data.put(SECTION, null);
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = DustGenUtils.sbAppend(null, " ", true, "(", lineNum, ")", lineType, data);
			return sb.toString();
		}
	}

	public static void read(ArkDockAgent<EditorConfigContext> target, String fileName) throws Exception {
		read(target, new FileInputStream(fileName), ENCODING_UTF8);
	}

	public static void read(ArkDockAgent<EditorConfigContext> target, InputStream is, String charset) throws Exception {
		read(target, new InputStreamReader(is, charset));
	}

	public static void read(ArkDockAgent<EditorConfigContext> target, Reader reader) throws Exception {
		EditorConfigContext ctx = target.getActionCtx();
		boolean localCtx = (null == ctx);

		try (BufferedReader br = new BufferedReader(reader)) {
			if ( localCtx ) {
				ctx = new EditorConfigContext();
				target.setActionCtx(ctx);
			} else {
				ctx.reset();
			}

			ArkDockTextRegex rxSect = new ArkDockTextRegex("\\[(?<section>.*)\\]", 0, null);
			ArkDockTextRegex rxEntry = new ArkDockTextRegex("(?<key>[^=]*)=(?<value>.*)", 0, null);

			target.agentAction(DustAgentAction.INIT);

			Map<String, String> rxData = new TreeMap<>();

			for (String line = br.readLine(); null != line; line = br.readLine()) {
				++ctx.lineNum;
				
				if ( !DustGenUtils.isEmpty(line) ) {
					char chStart = line.charAt(0);

					if ( (';' == chStart) || ('#' == chStart) ) {
						ctx.lineType = EditorConfigLineType.Comment;
						ctx.data.put(EditorConfigValues.VALUE, line.substring(1));
					} else if ( rxSect.matchAndRead(line, rxData) ) {
						ctx.optCloseSection(target);
						ctx.load(rxData, SECTION);
						ctx.lineType = EditorConfigLineType.Section;
						target.agentAction(DustAgentAction.BEGIN);
					} else if ( rxEntry.matchAndRead(line, rxData) ) {
						ctx.load(rxData, KEY, VALUE);
						ctx.lineType = EditorConfigLineType.Entry;
						target.agentAction(DustAgentAction.PROCESS);
					}
				}
			}

			ctx.optCloseSection(target);
		} finally {
			target.agentAction(DustAgentAction.RELEASE);

			if ( localCtx ) {
				target.setActionCtx(null);
			}
		}
	}
}
