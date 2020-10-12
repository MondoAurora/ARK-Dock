package ark.dock.srv;

import org.eclipse.jetty.server.handler.AbstractHandler;

import ark.dock.ArkDockConsts;
import ark.dock.ArkDockMind.WrapAgent;
import dust.gen.DustGenUtils;

public interface ArkDockSrvConsts extends ArkDockConsts {
	
	String CONTENT_TYPE_TEXT_UTF8 = "text/html; charset=utf-8";
	String CONTENT_TYPE_JSON = "application/json; charset=utf-8";

	
	public enum ArkDockSrvCmd {
		stop, send, ping, put, get
	}
	
	public enum ArkDockSrvEnv {
		userId
	}

	public enum ArkSrvParams implements DustGenUtils.HasDefault<String> {
		port("8080"), root("webRoot"), logLevel(DustEventLevel.INFO.name()), logFile(null);

		final String defVal;

		ArkSrvParams(String d) {
			this.defVal = d;
		}

		ArkSrvParams() {
			this.defVal = name();
		}

		@Override
		public String getDefault() {
			return defVal;
		}
	}

	public abstract class HandlerAgent<HandlerType extends AbstractHandler> extends WrapAgent<HandlerType> {

	}

}