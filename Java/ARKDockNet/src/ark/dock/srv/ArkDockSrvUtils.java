package ark.dock.srv;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ark.dock.ArkDockUtils;
import dust.gen.DustGenLog;

public class ArkDockSrvUtils implements ArkDockSrvConsts {
	
	public static void sendRespContentFrom(HttpServletResponse response, File srcFile, String contentType, boolean allowCache) {
		try (OutputStream out = response.getOutputStream()) {
			Path path = srcFile.toPath();
			Files.copy(path, out);
			out.flush();
			response.setContentType(contentType);
			response.setStatus(HttpServletResponse.SC_OK);
			
			if ( !allowCache ) {
				setNoCache(response);
			}
		} catch (IOException e) {
			DustGenLog.log(DustEventLevel.ERROR, e, "occured when sending file", srcFile.getAbsolutePath());
		}
	}
	
	public static void setNoCache(HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache, no-store");
	    response.setHeader("Pragma", "no-cache");
	    response.setDateHeader("Expires", 0);
	}

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