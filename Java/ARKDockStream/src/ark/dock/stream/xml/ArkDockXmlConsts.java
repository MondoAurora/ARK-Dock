package ark.dock.stream.xml;

import ark.dock.ArkDockConsts.ArkDockAgentBase;
import dust.gen.DustGenConsts.DustAgentAction;
import dust.gen.DustGenConsts.DustResultType;
import dust.gen.DustGenLog;
import dust.gen.DustGenUtils;

public interface ArkDockXmlConsts {
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

	class XmlDump extends ArkDockAgentBase<XmlContext> {

		@Override
		public DustResultType agentAction(DustAgentAction action) throws Exception {
			XmlContext ctx = getActionCtx();
			
			DustGenLog.log(action, ctx);
			
			return DustResultType.ACCEPT_READ;
		}
		
	}
	
}
