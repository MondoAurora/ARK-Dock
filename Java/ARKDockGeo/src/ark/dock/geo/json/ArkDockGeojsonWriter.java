package ark.dock.geo.json;

import java.io.Writer;

import ark.dock.json.ArkDockJsonConsts;
import dust.gen.DustGenDevUtils;

public class ArkDockGeojsonWriter implements ArkDockJsonConsts, ArkDockGeojsonConsts, DustGenDevUtils {
	
	GeojsonType listType;
	boolean inFeatureList = false;

	public ArkDockGeojsonWriter(Writer writer, GeojsonObjectSource obSrc) throws Exception {
		add(obSrc);
	}
	
	public void add(GeojsonObjectSource obSrc) throws Exception {
	}
	
	public void close() {
	}
}
