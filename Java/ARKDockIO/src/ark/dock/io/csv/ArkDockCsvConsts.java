package ark.dock.io.csv;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import ark.dock.io.ArkDockIOConsts;
import dust.gen.DustGenUtils;

public interface ArkDockCsvConsts extends ArkDockIOConsts {

	public class CsvContext {
		public ArrayList<String> colNames;
		
		public String key;
		public String value;
		
		public void reset() {
			colNames = null;
		}

		@Override
		public String toString() {
			StringBuilder sb = DustGenUtils.sbAppend(null, ": ", true, key, value);
			return sb.toString();
		}
	}

	public abstract class CsvDataCollector extends ArkDockAgentDefault<ArkDockIOConnCsv.CsvContext> {
		private final Map<String, String> csvData = new TreeMap<>();

		protected abstract void processRow(Map<String, String> csvData);

		@Override
		public DustResultType agentAction(DustAgentAction action) throws Exception {
			ArkDockIOConnCsv.CsvContext ctx;

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
