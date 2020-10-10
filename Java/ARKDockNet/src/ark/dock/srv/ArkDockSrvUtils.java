package ark.dock.srv;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ark.dock.ArkDockUtils;

public class ArkDockSrvUtils implements ArkDockSrvConsts {
	
	public static class CookieManager<T extends Enum<T>> {
		Map<T, Cookie> pv = new HashMap<>();

		protected CookieManager(HttpServletRequest request, Class<T> tc) {
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

	public static class ArkSrvCookieMgr extends CookieManager<ArkDockSrvEnv> {
		public ArkSrvCookieMgr(HttpServletRequest request) {
			super(request, ArkDockSrvEnv.class);
		}
	}
}