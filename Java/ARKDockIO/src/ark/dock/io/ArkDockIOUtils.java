package ark.dock.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import ark.dock.ArkDockConsts.ArkDockAgent;
import ark.dock.ArkDockConsts.ArkDockAgentDefault;
import ark.dock.ArkDockVisitor;
import ark.dock.ArkDockVisitor.VisitorAware;
import dust.gen.DustGenLog;
import dust.gen.DustGenUtils;

public class ArkDockIOUtils extends DustGenUtils {

	public static abstract class IoConnector<ActionCtxType> extends ArkDockAgentDefault<ActionCtxType> {
		public abstract boolean isText();

		public abstract ActionCtxType createContext();

		public String getEncoding() {
			return ENCODING_UTF8;
		}

		public DustResultType read(Reader source, ArkDockAgent<ActionCtxType> processor) throws Exception {
			return DustResultType.REJECT;
		}

		public DustResultType read(InputStream source, ArkDockAgent<ActionCtxType> processor) throws Exception {
			return DustResultType.REJECT;
		}
	}

	static class ReadContext<ActionCtxType> {
		IoConnector<ActionCtxType> connector;
		ArkDockAgent<ActionCtxType> processor;

		ArkDockVisitor<ActionCtxType> localVisitor;
		ActionCtxType localCtx;
		
		

		@SuppressWarnings("unchecked")
		public ReadContext(IoConnector<ActionCtxType> connector, ArkDockAgent<ActionCtxType> processor) {
			this.connector = connector;
			this.processor = processor;

			ActionCtxType ctx = processor.getActionCtx();

			if ( null == ctx ) {
				ctx = localCtx = connector.createContext();
				processor.setActionCtx(ctx);
			}
			if ( processor instanceof VisitorAware ) {
				localVisitor = new ArkDockVisitor<ActionCtxType>(ctx, processor);
				((VisitorAware<ActionCtxType>)processor).setVisitor(localVisitor);
			}
		}

		@SuppressWarnings("unchecked")
		public void close() {
			if ( null != localCtx ) {
				processor.setActionCtx(null);
			}
			if ( null != localVisitor ) {
				((VisitorAware<ActionCtxType>)processor).setVisitor(null);
			}
		}
		
		ArkDockAgent<ActionCtxType> getRootAgent() {
			return (null == localVisitor) ? processor : localVisitor;
		}

		public DustResultType read(Reader source) throws Exception {
			try {
				return connector.read(source, getRootAgent());
			} finally {
				close();
			}
		}

		public DustResultType read(InputStream source) throws Exception {
			try {
				return connector.read(source, getRootAgent());
			} finally {
				close();
			}
		}

	}

	public static <ActionCtxType> DustResultType read(IoConnector<ActionCtxType> connector,
			ArkDockAgent<ActionCtxType> processor, String fileName) throws Exception {
		return read(connector, processor, new File(fileName));
	}

	public static <ActionCtxType> DustResultType read(IoConnector<ActionCtxType> connector,
			ArkDockAgent<ActionCtxType> processor, File f) throws Exception {
		DustResultType ret = DustResultType.REJECT;

		if ( f.isFile() ) {
//			DustGenLog.log("Reading file", f.getAbsolutePath());
			try (FileInputStream fin = new FileInputStream(f)) {
				ret = connector.isText()
						? read(connector, processor, new InputStreamReader(fin, connector.getEncoding()))
						: read(connector, processor, fin);
			}
		}

		return ret;
	}

	public static <ActionCtxType> DustResultType read(IoConnector<ActionCtxType> connector,
			ArkDockAgent<ActionCtxType> processor, InputStream source) throws Exception {
		return new ReadContext<ActionCtxType>(connector, processor).read(source);
	}

	public static <ActionCtxType> DustResultType read(IoConnector<ActionCtxType> connector,
			ArkDockAgent<ActionCtxType> processor, Reader source) throws Exception {
		return new ReadContext<ActionCtxType>(connector, processor).read(source);
	}
}
