package ark.dock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dust.gen.DustGenConsts.DustEntity;
import dust.gen.DustGenUtils;

public class ArkDockModel implements ArkDockConsts, Iterable<DustEntity> {

    class ModelEntity implements DustEntity {
        final String id;
        final String globalId;
        Map<DustEntity, Object> data = new HashMap<>();

        public ModelEntity(String globalId) {
            this.globalId = globalId;
            this.id = globalId.substring(globalId.lastIndexOf(TOKEN_SEP) + 1);
        }

        @Override
        public String getId() {
            return id;
        }
        
        @Override
        public String getGlobalId() {
            return globalId;
        }

        @Override
        public String toString() {
            return globalId;
        }
    }

    MetaProvider meta;
    ArkDockModel parent;

    Map<String, ModelEntity> entities = new HashMap<>();

    public static String buildGlobalId(String unitId, String typeId, String id) {
        return DustGenUtils.sbAppend(null, TOKEN_SEP, true, unitId, typeId, id).toString();
    }

    public ArkDockModel(ArkDockModel parent_) {
        this.parent = parent_;
        meta = parent.getMeta();
    }

    protected ArkDockModel() {
    }

    public MetaProvider getMeta() {
        return meta;
    }

    public ArkDockModel getParent() {
        return parent;
    }

    public DustEntity getEntity(DustEntity unit, DustEntity type, String itemId, boolean createIfMissing) {
        String globalId = buildGlobalId(((ModelEntity) unit).id, ((ModelEntity) type).id, itemId);
        return getEntity(globalId, createIfMissing);
    }

    public DustEntity getEntity(String globalId, boolean createIfMissing) {
        ModelEntity e = entities.get(globalId);

        if (createIfMissing && (null == e)) {
            e = new ModelEntity(globalId);
            entities.put(globalId, e);
        }

        return e;
    }
    
    public Iterator<DustEntity> getContent(DustEntity e, DustEntity member) {
        return ((ModelEntity) e).data.keySet().iterator();
    }

    public void setMember(DustEntity e, DustEntity member, Object value) {
        ((ModelEntity) e).data.put(member, value);
    }

    @SuppressWarnings("unchecked")
    public <RetType> RetType getMember(DustEntity e, DustEntity member, RetType defValue) {
        RetType ret = defValue;

        if (null != e) {
            Object v = ((ModelEntity) e).data.get(member);
            if (null != v) {
                ret = (RetType) v;
            }
        }

        return ret;
    }

    class ContentReader implements Iterator<DustEntity> {
        Iterator<ModelEntity> mi;

        public ContentReader() {
            mi = entities.values().iterator();
        }

        @Override
        public boolean hasNext() {
            return mi.hasNext();
        }

        @Override
        public DustEntity next() {
            return mi.next();
        }

        @Override
        public void remove() {
            DustException.throwException(null, "Should not remove Entity in this way! Use the API.");
        }
    }

    @Override
    public Iterator<DustEntity> iterator() {
        return new ContentReader();
    }
}
