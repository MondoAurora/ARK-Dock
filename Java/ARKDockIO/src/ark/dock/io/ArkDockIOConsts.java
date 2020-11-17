package ark.dock.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import ark.dock.ArkDockConsts;

public interface ArkDockIOConsts extends ArkDockDslIO, ArkDockConsts {
	
	public abstract class ArkDockIOSource<ActionCtxType> implements ArkDockContextAware<ActionCtxType> {
    	protected ActionCtxType ctx;
		
    	public void setActionCtx(ActionCtxType ctx) {
    		this.ctx = ctx;
    	}
    	public ActionCtxType getActionCtx() {
    		return ctx;
    	}
    	
		public abstract DustResultType write(ArkDockAgent<ActionCtxType> target) throws Exception;
	}
	
	
	public static abstract class ArkDockIOConnector<ActionCtxType> {
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

		public DustResultType write(ArkDockIOSource<ActionCtxType> source, Writer target) throws Exception {
			return DustResultType.REJECT;
		}

		public DustResultType write(ArkDockIOSource<ActionCtxType> source, OutputStream target) throws Exception {
			return DustResultType.REJECT;
		}
	}

}
