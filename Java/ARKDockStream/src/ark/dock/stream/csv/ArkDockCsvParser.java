package ark.dock.stream.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;

import dust.gen.DustGenLog;

public class ArkDockCsvParser implements ArkDockCsvConsts {

	String separator;

	public ArkDockCsvParser(String separator_) {
		this.separator = separator_;
	}

	public void parse(File f, ArkDockAgent<CsvContext> listener, ArrayList<String> colNames) throws Exception {
		if ( f.isFile() ) {
			try ( FileReader fr = new FileReader(f) ) {
				parse(fr, listener, colNames);
			}
		}
	}

	public void parse(Reader r, ArkDockAgent<CsvContext> listener, ArrayList<String> colNames) throws Exception {
		int cc = (null == colNames) ? 0 : colNames.size();
		CsvContext ctx = null;
		CsvContext ctxOrig = listener.getActionCtx();
		if ( null == ctxOrig ) {
			ctx = new CsvContext();
			listener.setActionCtx(ctx);
		}

		try (BufferedReader br = new BufferedReader(r)) {
		    for(String line; (line = br.readLine()) != null; ) {
				String[] sl = line.split(separator);

				if ( null == colNames ) {
					cc = sl.length;
					colNames = new ArrayList<>(cc);
					for (String c : sl) {
						colNames.add(c);
					}
					listener.agentAction(DustAgentAction.INIT);
				} else {
					if ( sl.length != cc ) {
						DustGenLog.log(DustEventLevel.ERROR, "line item count does not match", colNames, line);
					} else {
						listener.agentAction(DustAgentAction.BEGIN);
						for (int i = 0; i < cc; ++i) {
							ctx.key = colNames.get(i);
							ctx.value = sl[i];
							listener.agentAction(DustAgentAction.PROCESS);
						}
						listener.agentAction(DustAgentAction.END);
					}
				}
			}
		} finally {
			if ( null != colNames ) {
				listener.agentAction(DustAgentAction.RELEASE);
				if ( null == ctxOrig ) {
					listener.setActionCtx(ctxOrig);
				}
				colNames = null;
			}
		}
	}
}
