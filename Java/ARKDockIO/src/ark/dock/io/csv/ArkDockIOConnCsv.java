package ark.dock.io.csv;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;

import ark.dock.io.ArkDockIOUtils;
import ark.dock.io.csv.ArkDockCsvConsts.CsvContext;
import dust.gen.DustGenLog;

public class ArkDockIOConnCsv  extends ArkDockIOUtils.IoConnector<CsvContext> implements ArkDockCsvConsts {

	String separator;
	int skipHead;

	public ArkDockIOConnCsv(String separator_) {
		this.separator = separator_;
	}
	
	public ArkDockIOConnCsv(String separator_, int skipHead_) {
		this.separator = separator_;
		this.skipHead = skipHead_;
	}
	
	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {
		return DustResultType.REJECT;
	}

	@Override
	public boolean isText() {
		return true;
	}
	
	@Override
	public CsvContext createContext() {
		return new CsvContext();
	}
	
	@Override
	public DustResultType read(Reader source, ArkDockAgent<CsvContext> processor) throws Exception {
		CsvContext ctx = processor.getActionCtx();
		int cc = (null == ctx.colNames) ? 0 : ctx.colNames.size();
		boolean empty = true;
		boolean resetColNames = 0 == cc;

		try (BufferedReader br = new BufferedReader(source)) {
			int dropLines = skipHead;
		    for(String line; (line = br.readLine()) != null; ) {
		    	if ( 0 < dropLines ) {
		    		--dropLines;
		    		continue;
		    	}
		    	
				String[] sl = line.split(separator);

				if ( null == ctx.colNames ) {
					cc = sl.length;
					ctx.colNames = new ArrayList<>(cc);
					for (String c : sl) {
						ctx.colNames.add(c);
					}
				} else {
					if ( sl.length != cc ) {
						DustGenLog.log(DustEventLevel.ERROR, "line item count does not match", ctx.colNames, line);
					} else {
						if ( empty ) {
							processor.agentAction(DustAgentAction.INIT);
							empty = false;
						}
						processor.agentAction(DustAgentAction.BEGIN);
						for (int i = 0; i < cc; ++i) {
							ctx.key = ctx.colNames.get(i);
							ctx.value = sl[i];
							processor.agentAction(DustAgentAction.PROCESS);
						}
						processor.agentAction(DustAgentAction.END);
					}
				}
			}
		} finally {
			if ( !empty ) {
				processor.agentAction(DustAgentAction.RELEASE);
			}
			if ( resetColNames ) {
				ctx.colNames = null;
			}
		}
		
		return DustResultType.ACCEPT;
	}
}
