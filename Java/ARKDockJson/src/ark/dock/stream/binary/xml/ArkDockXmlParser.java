package ark.dock.stream.binary.xml;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ark.dock.ArkDockConsts.ArkDockAgent;
import dust.gen.DustGenConsts.DustAgentAction;

public class ArkDockXmlParser implements ArkDockXmlConsts {

	static SAXParserFactory factory = SAXParserFactory.newInstance();

	public static void parse(File f, ArkDockAgent<XmlContext> listener) throws Exception {
		if ( f.isFile() ) {

			SAXParser saxParser = factory.newSAXParser();
			XmlContext ctxOrig = listener.getActionCtx();
			final XmlContext ctx = ( null == ctxOrig ) ? new XmlContext() : ctxOrig;
			if ( null == ctxOrig ) {
				listener.setActionCtx(ctx);
			}

			DefaultHandler handler = new DefaultHandler() {
				@Override
				public void startDocument() throws SAXException {
					send(DustAgentAction.INIT);
				}

				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {
					ctx.item = XmlItem.Element;
					ctx.name = qName;
					send(DustAgentAction.BEGIN);
					
					int ac = attributes.getLength();
					
					if ( 0 < ac ) {
						ctx.item = XmlItem.Attribute;
						for ( int i = 0; i < ac; ++i ) {
							ctx.name = attributes.getQName(i);
							ctx.value = attributes.getValue(i);
							send(DustAgentAction.PROCESS);
						}
						ctx.value = "";
					}
				}

				public void endElement(String uri, String localName, String qName) throws SAXException {
					ctx.item = XmlItem.Element;
					ctx.name = qName;
					send(DustAgentAction.END);
				}

				public void characters(char ch[], int start, int length) throws SAXException {
					String str =  new String(ch, start, length);
					
					if ( !str.trim().isEmpty() ) {
						ctx.item = XmlItem.Text;
						ctx.value = str;
						send(DustAgentAction.PROCESS);
						ctx.value = "";
					}
				}

				@Override
				public void endDocument() throws SAXException {
					send(DustAgentAction.RELEASE);
				}
				
				void send(DustAgentAction action) throws SAXException {
					try {
						listener.agentAction(action);
					} catch (Exception e) {
						throw new SAXException(e);
					}
				}
			};

			saxParser.parse(f, handler);
		}
	}

	public static void main(String argv[]) {
		try {
			XmlDump dump = new XmlDump();
			
			parse(new File(argv[0]), dump);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
