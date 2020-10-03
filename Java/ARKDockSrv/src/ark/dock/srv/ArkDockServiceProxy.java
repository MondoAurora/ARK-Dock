package ark.dock.srv;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.EnumSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import ark.dock.ArkDockModel;
import ark.dock.ArkDockModelSerializer;
import ark.dock.ArkDockUtils;
import ark.dock.geo.json.ArkDockGeojson2D;
import ark.dock.json.ArkDockJsonSerializerWriter;

public class ArkDockServiceProxy extends AbstractHandler implements ArkDockSrvConsts {
	
	private final ArkDockModel modMain;
	
	public ArkDockServiceProxy(ArkDockModel modMain) {
		super();
		this.modMain = modMain;
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String cmd = target;

		if (cmd.startsWith("/")) {
			cmd = cmd.substring(1);
		}

		PrintWriter out = null;
        String id;
        DustEntity e = null;

		switch (ArkDockUtils.fromString(cmd, ArkDockSrvCmd.ping)) {
        case get:
            id = request.getParameter("id");
            e = modMain.getEntity(id);
            break;
		default:
			break;
		}
		
        response.setStatus(HttpServletResponse.SC_OK);

        if ( null != e ) {
            response.setContentType("text/json; charset=utf-8");

            out = response.getWriter();
                    	
    		ArkDockJsonSerializerWriter sw = new ArkDockJsonSerializerWriter(out, null);
    		
    		EnumSet<ArkDockGeojson2D.FormatParam> params = EnumSet.of(ArkDockGeojson2D.FormatParam.FakeZCoord);
    		sw.addFormatter(new ArkDockGeojson2D.PointFormatter(params));
    		sw.addFormatter(new ArkDockGeojson2D.PathFormatter(params));
    		sw.addFormatter(new ArkDockGeojson2D.PolygonFormatter(params));
    		sw.addFormatter(new ArkDockGeojson2D.BBoxFormatter());

    		try {
				ArkDockModelSerializer.modelToAgent(e, sw, null);
			} catch (Exception e1) {
				throw new ServletException(e1);
			}    		
        }
		
		baseRequest.setHandled(true);
	}
	

	public static class Agent extends HandlerAgent<ArkDockServiceProxy> {
		@Override
		protected ArkDockServiceProxy createWrapOb() {
			return new ArkDockServiceProxy(getMind().modMain);
		}
	}
}
