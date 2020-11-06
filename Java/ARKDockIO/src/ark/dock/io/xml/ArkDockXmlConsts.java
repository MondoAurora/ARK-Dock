package ark.dock.io.xml;

import ark.dock.io.ArkDockIOConsts;
import dust.gen.DustGenUtils;

public interface ArkDockXmlConsts extends ArkDockIOConsts {
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
}
