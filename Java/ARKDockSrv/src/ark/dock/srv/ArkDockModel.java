package ark.dock.srv;

import java.util.HashMap;
import java.util.Map;

import ark.dock.ArkDockUtils;

public class ArkDockModel implements ArkDockSrvConsts {
    Map<String, Map<String, Object>> entities = new HashMap<String, Map<String, Object>>();

    public void test(long count) {
        long s = entities.size();
        for (long l = 0; l < count; ++l) {
            entities.put(String.valueOf(s + l), new HashMap<>());
            
            if ( 0 == (l % 100000) ) {
                ArkDockUtils.log(ArkEventLevel.TRACE, l);
            }
        }
        
        ArkDockUtils.log(ArkEventLevel.INFO, "Model count ", entities.size());
    }
}
