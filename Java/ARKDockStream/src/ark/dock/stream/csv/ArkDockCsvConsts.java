package ark.dock.stream.csv;

import java.util.Map;
import java.util.TreeMap;

import ark.dock.stream.ArkDockStreamConsts;
import dust.gen.DustGenUtils;

public interface ArkDockCsvConsts extends ArkDockStreamConsts {

	public class CsvContext {
		public String key;
		public String value;

		@Override
		public String toString() {
			StringBuilder sb = DustGenUtils.sbAppend(null, ": ", true, key, value);
			return sb.toString();
		}
	}

	public abstract class CsvDataCollector extends ArkDockAgentDefault<ArkDockCsvParser.CsvContext> {
		private final Map<String, String> csvData = new TreeMap<>();

		protected abstract void processRow(Map<String, String> csvData);

		@Override
		public DustResultType agentAction(DustAgentAction action) throws Exception {
			ArkDockCsvParser.CsvContext ctx;

			switch ( action ) {
			case BEGIN:
				csvData.clear();
				break;
			case PROCESS:
				ctx = getActionCtx();
				csvData.put(ctx.key, ctx.value);
				break;
			case END:
				processRow(csvData);
				break;
			default:
				break;
			}

			return DustResultType.ACCEPT_READ;
		}
	}
}
