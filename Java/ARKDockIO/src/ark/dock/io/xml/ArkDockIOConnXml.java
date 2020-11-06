package ark.dock.io.xml;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ark.dock.io.ArkDockIOUtils;
import ark.dock.io.xml.ArkDockXmlConsts.XmlContext;

public class ArkDockIOConnXml extends ArkDockIOUtils.IoConnector<XmlContext> implements ArkDockXmlConsts {

	static SAXParserFactory factory = SAXParserFactory.newInstance();
	
	static class XmlContentDispatcher extends DefaultHandler {
		XmlContext ctx;
        ArkDockAgent<XmlContext> processor;
        
        public XmlContentDispatcher(ArkDockAgent<XmlContext> processor) {
            this.processor = processor;
            this.ctx = processor.getActionCtx();
        }

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
				processor.agentAction(action);
			} catch (Exception e) {
				throw new SAXException(e);
			}
		}
	};
	
	@Override
	public boolean isText() {
		return false;
	}
	
	@Override
	public XmlContext createContext() {
		return new XmlContext();
	}

	@Override
	public DustResultType agentAction(DustAgentAction action) throws Exception {
		return DustResultType.REJECT;
	}
	
	public ArkDockIOConnXml() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public DustResultType read(InputStream source, ArkDockAgent<XmlContext> processor) throws Exception {
		SAXParser saxParser = factory.newSAXParser();
		
		XmlContentDispatcher handler = new XmlContentDispatcher(processor);
		saxParser.parse(source, handler);

		return DustResultType.ACCEPT;
	}
	
//	public static void parse(File f, ArkDockAgent<XmlContext> listener) throws Exception {
//		if ( f.isFile() ) {
//
//			SAXParser saxParser = factory.newSAXParser();
//			XmlContext ctxOrig = listener.getActionCtx();
//			final XmlContext ctx = ( null == ctxOrig ) ? new XmlContext() : ctxOrig;
//			if ( null == ctxOrig ) {
//				listener.setActionCtx(ctx);
//			}
//
//			XmlContentDispatcher handler = new XmlContentDispatcher(listener);
//
//			saxParser.parse(f, handler);
//		}
//	}
//
//	public static void main(String argv[]) {
//		try {
//			DumpAgent<XmlContext> dump = new DumpAgent<XmlContext>();
//			
//			parse(new File(argv[0]), dump);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

}
