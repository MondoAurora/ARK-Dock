package ark.dock.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import ark.dock.ArkDockVisitor;
import ark.dock.ArkDockVisitor.VisitorAware;
import dust.gen.DustGenUtils;

public class ArkDockIOUtils extends DustGenUtils implements ArkDockIOConsts {

	static class ReadContext<ActionCtxType> {
		ArkDockIOConnector<ActionCtxType> connector;
		ArkDockAgent<ActionCtxType> processor;

		ArkDockVisitor<ActionCtxType> localVisitor;
		ActionCtxType localCtx;
		
		@SuppressWarnings("unchecked")
		public ReadContext(ArkDockIOConnector<ActionCtxType> connector, ArkDockAgent<ActionCtxType> processor) {
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

	public static <ActionCtxType> DustResultType read(ArkDockIOConnector<ActionCtxType> connector,
			ArkDockAgent<ActionCtxType> processor, String fileName) throws Exception {
		return read(connector, processor, new File(fileName));
	}

	public static <ActionCtxType> DustResultType read(ArkDockIOConnector<ActionCtxType> connector,
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

	public static <ActionCtxType> DustResultType read(ArkDockIOConnector<ActionCtxType> connector,
			ArkDockAgent<ActionCtxType> processor, InputStream source) throws Exception {
		return new ReadContext<ActionCtxType>(connector, processor).read(source);
	}

	public static <ActionCtxType> DustResultType read(ArkDockIOConnector<ActionCtxType> connector,
			ArkDockAgent<ActionCtxType> processor, Reader source) throws Exception {
		return new ReadContext<ActionCtxType>(connector, processor).read(source);
	}
	
	
	
	
	
	static class WriteContext<ActionCtxType> {
		ArkDockIOConnector<ActionCtxType> connector;
		ArkDockIOSource<ActionCtxType> source;

		ActionCtxType localCtx;
		
		public WriteContext(ArkDockIOConnector<ActionCtxType> connector, ArkDockIOSource<ActionCtxType> source) {
			this.connector = connector;
			this.source = source;

			ActionCtxType ctx = source.getActionCtx();

			if ( null == ctx ) {
				ctx = localCtx = connector.createContext();
				source.setActionCtx(ctx);
			}
		}

		public void close() {
			if ( null != localCtx ) {
				source.setActionCtx(null);
			}
		}
		
		public DustResultType write(Writer target) throws Exception {
			try {
				return connector.write(source, target);
			} finally {
				close();
			}
		}

		public DustResultType write(OutputStream target) throws Exception {
			try {
				return connector.write(source, target);
			} finally {
				close();
			}
		}
	}

	public static <ActionCtxType> DustResultType write(ArkDockIOConnector<ActionCtxType> connector,
			ArkDockIOSource<ActionCtxType> source, String fileName) throws Exception {
		return write(connector, source, new File(fileName));
	}

	public static <ActionCtxType> DustResultType write(ArkDockIOConnector<ActionCtxType> connector,
			ArkDockIOSource<ActionCtxType> source, File f) throws Exception {
		DustResultType ret = DustResultType.REJECT;
		
		DustGenUtils.ensureParents(f);

			try (FileOutputStream fout = new FileOutputStream(f)) {
				ret = connector.isText()
						? write(connector, source, new OutputStreamWriter(fout, connector.getEncoding()))
						: write(connector, source, fout);
				fout.flush();
				fout.close();
			}

		return ret;
	}

	public static <ActionCtxType> DustResultType write(ArkDockIOConnector<ActionCtxType> connector,
			ArkDockIOSource<ActionCtxType> source, OutputStream target) throws Exception {
		return new WriteContext<ActionCtxType>(connector, source).write(target);
	}

	public static <ActionCtxType> DustResultType write(ArkDockIOConnector<ActionCtxType> connector,
			ArkDockIOSource<ActionCtxType> source, Writer target) throws Exception {
		return new WriteContext<ActionCtxType>(connector, source).write(target);
	}
}
