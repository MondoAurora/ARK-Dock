package ark.dock.stream.xml;

import ark.dock.stream.ArkDockStreamConsts;
import dust.gen.DustGenLog;
import dust.gen.DustGenUtils;

public interface ArkDockXmlConsts extends ArkDockStreamConsts {
	enum XmlItem {
		Element, Attribute, Text
	}

	public class XmlContext {
		public XmlItem item;
		public String name;
		public String value;

		@Override
		public String toString() {
			StringBuilder sb = DustGenUtils.sbAppend(null, ": ", true, item, name, value);
			return sb.toString();
		}
	}

	class XmlDump extends ArkDockAgentDefault<XmlContext> {

		@Override
		public DustResultType agentAction(DustAgentAction action) throws Exception {
			XmlContext ctx = getActionCtx();
			
			DustGenLog.log(action, ctx);
			
			return DustResultType.ACCEPT_READ;
		}
		
	}
	
}
