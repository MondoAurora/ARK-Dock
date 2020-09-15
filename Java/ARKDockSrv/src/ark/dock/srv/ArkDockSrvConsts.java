package ark.dock.srv;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ark.dock.ArkDockConsts;
import ark.dock.ArkDockUtils;

public interface ArkDockSrvConsts extends ArkDockConsts {
	
	public enum ArkDockSrvCmd {
		stop, send, ping, put, get
	}
	
	public enum ArkDockSrvEnv {
		userId
	}


	public enum ArkSrvParams implements DustHasDefault<String> {
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

	public static class ArkCookieManager<T extends Enum<T>> {
		Map<T, Cookie> pv = new HashMap<>();

		protected ArkCookieManager(HttpServletRequest request, Class<T> tc) {
			Cookie[] cc = request.getCookies();
			if (null != cc) {
				for (Cookie c : cc) {
					T key = ArkDockUtils.fromString(c.getName(), tc);

					if (null != key) {
						pv.put(key, c);
					}
				}
			}
		}

		public String getString(T key) {
			Cookie c = pv.get(key);
			return (null == c) ? null : c.getValue();
		}

		public Cookie setCookie(T key, Object value) {
			Cookie c = pv.get(key);

			if (null == c) {
				if (null != value) {
					c = new Cookie(key.name(), value.toString());
					pv.put(key, c);
				}
			} else {
				c.setValue(ArkDockUtils.toStringSafe(value));
			}

			if (null != c) {
				c.setMaxAge(-1);
				c.setPath("/");
			}
			
			return c;
		}

		public void update(HttpServletResponse response) {
			for (Cookie c : pv.values()) {
				response.addCookie(c);
			}
		}
	}

	public static class ArkSrvCookieMgr extends ArkCookieManager<ArkDockSrvEnv> {
		public ArkSrvCookieMgr(HttpServletRequest request) {
			super(request, ArkDockSrvEnv.class);
		}
	}
}