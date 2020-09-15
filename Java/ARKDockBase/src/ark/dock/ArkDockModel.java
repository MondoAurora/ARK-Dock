package ark.dock;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import dust.gen.DustGenLog;

public class ArkDockModel implements ArkDockConsts {
    
    public class Entity {
        Map<String, Object> data = new TreeMap<>();
    }
    
    Map<String, Entity> entities = new HashMap<>();
    
    public Entity getEntity(String type, String itemId) {
        String globalId = type + "_" + itemId;
        
        return getEntity(globalId, true);
    }

    public Entity getEntity(String globalId, boolean createIfMissing) {
        Entity e = entities.get(globalId);
        
        if ( null == e ) {
            e = new Entity();
            entities.put(globalId, e);
        }
        
        return e;
    }
    
    public void setMember(Entity e, String member, Object value) {
        e.data.put(member,  value);
    }

    public Object getMember(Entity e, String member, Object defValue) {
        Object ret = defValue;
        
        if ( null != e ) {
            Object v = e.data.get(member);
            if ( null != v ) {
                ret = v;
            }
        }
        
        return ret;
    }

    public void test(long count) {
        long s = entities.size();
        for (long l = 0; l < count; ++l) {
            entities.put(String.valueOf(s + l), new Entity());
            
            if ( 0 == (l % 100000) ) {
                DustGenLog.log(DustEventLevel.TRACE, l);
            }
        }
        
        DustGenLog.log(DustEventLevel.INFO, "Model count ", entities.size());
    }
    
    
}
