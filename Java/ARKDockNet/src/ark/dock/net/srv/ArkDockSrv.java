package ark.dock.net.srv;

import java.awt.geom.Path2D;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import ark.dock.ArkDock;
import ark.dock.ArkDockDslBuilder;
import ark.dock.ArkDockUtils;
import dust.gen.DustGenInitParams;
import dust.gen.DustGenLog;

public class ArkDockSrv implements ArkDockSrvConsts {

    ArkDockDslBuilder model = ArkDock.getDslBuilder("Test");
    DustEntity eTestType = model.getType("Test");
    DustEntity eTestMember = model.getMember(eTestType, "TestValue");
    
	Server server;
	Thread shutdownProcess = new Thread() {
		@Override
		public synchronized void run() {
			if ( null == server ) {
				return;
			}
			
			try {
				Server s = server;
				server = null;
				DustGenLog.log("Shutting down ArkDockSrv on port", port);
				s.stop();
				DustGenLog.log("ArkDockSrv shutdown OK.");
			} catch (Exception ex) {
				DustGenLog.log(DustEventLevel.ERROR, "Failed to stop ArkDockSrv");
			}
		}
	};
	
	int port;
	
	ArrayList<File> roots = new ArrayList<>();
	
	public class CmdHandler extends AbstractHandler {
		@SuppressWarnings("unchecked")
		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
			response.setContentType("text/html; charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);

			String cmd = target;

			if (cmd.startsWith("/")) {
				cmd = cmd.substring(1);
			}

			PrintWriter out = null;
            Reader in = request.getReader();
            JSONObject data = null;
            JSONObject clientData = null;
            JSONObject respData = null;
            String id;
            DustEntity e;

			switch (ArkDockUtils.fromString(cmd, ArkDockSrvCmd.ping)) {
			case send:
				data = (JSONObject) JSONValue.parse(in);
				clientData = (JSONObject) data.get("data");
				// testing echo
				
				respData = clientData;
									
                break;
            case put:
                data = (JSONObject) JSONValue.parse(in);
                clientData = (JSONObject) data.get("data");
                
                id = (String) clientData.get("id");
                e = model.getEntity(eTestType, id, true);
                model.accessMember(DustDialogCmd.SET, e, eTestMember, clientData, null);
                
                break;
            case get:
                id = request.getParameter("id");
                e = model.getEntity(eTestType, id, false);
                respData = (JSONObject) model.accessMember(DustDialogCmd.GET, e, eTestMember, null, null);

                break;
            case ping:
                
              long l = System.currentTimeMillis();
              data = (JSONObject) JSONValue.parse(new FileReader("C:\\Temp\\sample_loc.json"));

              JSONArray features = (JSONArray) data.get("features");
              
              int cnt = features.size();
              double x, y;
              
              for ( Object o : features ) {
                  JSONObject loc = (JSONObject) o;
                  JSONObject geo = (JSONObject) loc.get("geometry");
                  JSONArray polygons = (JSONArray) geo.get("coordinates");
                  Set<Path2D.Double> locPolys = new HashSet<>();
                  
                  for ( Object p : polygons ) {
                      JSONArray p1 = (JSONArray) p;
                      for ( Object p2 : (JSONArray) p1 ) {
                          JSONArray path = (JSONArray) p2;
                          Path2D.Double pp = null;
                          for ( Object pt : path ) {
                              JSONArray point = (JSONArray) pt;
                              x = (Double) point.get(0);
                              y = (Double) point.get(1);
                              if ( null == pp ) {
                                  pp = new Path2D.Double();
                                  pp.moveTo(x, y);
                              } else {
                                  pp.lineTo(x, y);
                              }
                          }
                          locPolys.add(pp);
                      }
                  }
              }
              respData = new JSONObject();
              respData.put("time", System.currentTimeMillis() - l);
              respData.put("count", cnt);

                break;
            case stop:
				ArkDockSrv.this.stop();
				break;
			default:
				break;
			}
			
            response.setStatus(HttpServletResponse.SC_OK);

            if ( null != respData ) {
                response.setContentType("text/json; charset=utf-8");
                
                out = response.getWriter();
                
                JSONObject ret = new JSONObject();
                ret.put("status", (null == respData) ? "error" : "OK");
                ret.put("data", respData);
                
                String str = ret.toJSONString();
                DustGenLog.log(DustEventLevel.INFO, "Sending response", str);
                out.println(str);
            }
			
			baseRequest.setHandled(true);
		}
	}

	public class RootHandler extends AbstractHandler {
		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {

			if (target.endsWith("/")) {
				target = target.substring(0, target.length() - 1);
			}
			
			for ( File root : roots ) {
				if ( serveContent(root, target, response) ) {
					return;
				}
			}
			
			DustGenLog.log(DustEventLevel.ERROR, "Missing file", target);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}

		private boolean serveContent(File root, String target, HttpServletResponse response) {
			File f = new File(root, target);

			if (f.isDirectory()) {
				f = new File(f, "/index.html");
			}
			
			if (f.exists()) {
				try (OutputStream out = response.getOutputStream()) {
					Path path = f.toPath();
					Files.copy(path, out);
					out.flush();
					response.setContentType("text/html; charset=utf-8");
					response.setStatus(HttpServletResponse.SC_OK);
					return true;
				} catch (IOException e) {
					DustGenLog.log(DustEventLevel.ERROR, e, "occured when sending file", f.getAbsolutePath());
				}
			}
			
			return false;
		}
	}

	public ArkDockSrv(DustGenInitParams<ArkSrvParams> params) throws Exception {
		String rootPaths = params.getString(ArkSrvParams.root);
		
		for ( String rp : rootPaths.split(";")) {
			File root = new File(rp.trim());

			if (!root.exists() || !root.isDirectory()) {
				throw new Exception("Missing root directory at " + root.getAbsolutePath());
			}
			
			roots.add(root);
		}
		

		DustEventLevel lvl = ArkDockUtils.fromString(params.getString(ArkSrvParams.logLevel), DustEventLevel.INFO);
		DustGenLog.setLogLevel(lvl);
		
		String s = params.getString(ArkSrvParams.logFile);
		if ( !ArkDockUtils.isEmpty(s) ) {
		    DustGenLog.setLogFile(s);
		}

		HandlerList hl = new HandlerList();
		ContextHandler ch = new ContextHandler("/cmd");
		ch.setHandler(new CmdHandler());
		hl.addHandler(ch);
		hl.addHandler(new RootHandler());

		server = new Server(port = params.getInt(ArkSrvParams.port));
		server.setHandler(hl);
	}

	public void launch() throws Exception {
		server.start();
		
		Runtime.getRuntime().addShutdownHook(shutdownProcess);
		
		server.join();
	}

	public void stop() {
		shutdownProcess.start();
	}

	public static void main(String[] args) throws Exception {
		DustGenInitParams<ArkSrvParams> params = new DustGenInitParams<>(args, ArkSrvParams.class);

		ArkDockSrv srv = new ArkDockSrv(params);

		srv.launch();
		
		
	}
}
