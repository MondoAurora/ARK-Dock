package ark.dock.json;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import ark.dock.ArkDockModelSerializer.SerializeAgent;
import dust.gen.DustGenConsts.DustAgentAction;
import dust.gen.DustGenConsts.DustEntityContext;
import dust.gen.DustGenConsts.DustResultType;
import dust.gen.DustGenLog;
import dust.gen.DustGenUtils;

public class ArkDockJsonSerializerWriter extends SerializeAgent<DustEntityContext> {
	enum JsonHeader {
		ArkJsonInfo, VersionInfo, Entities
	}
	
	interface JsonFormatter {
		void toJson(Object data, Writer target) throws Exception;
	}
	
	static String HEADER = "{\n  \"ArkJsonInfo\":null,\n  \"VersionInfo\":null,\n  \"Entities\" : {";
	static String FOOTER = "  }\n}";
	
	Writer target;
	
	Map<Class<?>, JsonFormatter> formatters;
	
	boolean contEntity;
	
	public ArkDockJsonSerializerWriter(Writer target, Map<JsonHeader, Map<String, Object>> header) {
		this.target = target;
		setEventCtx(new DustEntityContext());
	}
	
	public ArkDockJsonSerializerWriter(File file, Map<JsonHeader, Map<String, Object>> header) throws Exception {
		this(new PrintWriter(file, "UTF-8"), header);
	}
	
	public ArkDockJsonSerializerWriter(String fileName, Map<JsonHeader, Map<String, Object>> header) throws Exception {
		this(new File(fileName), header);
	}
	
	public void addFormatter(Class<?> c, JsonFormatter fmt) {
		if ( null == formatters ) {
			formatters = new HashMap<>();
		}
		
		formatters.put(c, fmt);
	}

	void writeHeader() throws Exception {
		target.write(HEADER);
	}
	
	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {
		DustEntityContext ctx = getEventCtx();
		StringBuilder line = null;
		String closeLine = "";
		
		switch ( action ) {
		case INIT:
			writeHeader();
			contEntity = false;
			break;
		case BEGIN:
			switch ( ctx.block ) {
			case Entity:
				if ( contEntity ) {
					closeLine = ",";
				} else {
					contEntity = true;
				}
				line = DustGenUtils.sbAppend(null, "", true, closeLine, "\n   \"", JSONValue.escape(ctx.member.toString()), "\" : ");
				target.write(line.toString());
				break;
			case Member:
				break;
			}
			break;
		case END:
			break;
		case PROCESS:
			break;
		case RELEASE:
			target.write(FOOTER);

			break;
		}
		DustGenLog.log(action, getEventCtx());
		return DustResultType.ACCEPT_READ;
	}

}
