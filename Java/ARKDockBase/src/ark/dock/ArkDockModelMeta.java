package ark.dock;

import ark.dock.ArkDockConsts.MetaProvider;
import dust.gen.DustGenUtils;

public class ArkDockModelMeta extends ArkDockModel implements MetaProvider {
    public final DustEntity eUnitArk;
    
    final DustEntity eTypeUnit;
    final DustEntity eTypeType;
    final DustEntity eTypeMember;
    
    public ArkDockModelMeta() {
        parent = null;
        meta = this;
        
        eUnitArk = getEntity(buildGlobalId(UNIT_ARK, TYPE_UNIT, UNIT_ARK), true);
        eTypeUnit = getEntity(buildGlobalId(UNIT_ARK, TYPE_TYPE, TYPE_UNIT), true);
        eTypeType = getEntity(buildGlobalId(UNIT_ARK, TYPE_TYPE, TYPE_TYPE), true);
        eTypeMember = getEntity(buildGlobalId(UNIT_ARK, TYPE_TYPE, TYPE_MEMBER), true);
    }
    
    @Override
    public DustEntity getUnit(String unitId) {
        return getEntity(eUnitArk, eTypeUnit, unitId, true);
    }
    
    @Override
    public DustEntity getType(DustEntity unit, String typeId) {
        return getEntity(unit, eTypeType, typeId, true);
    }
    
    @Override
    public DustEntity getMember(DustEntity type, String itemId) {
        String globalId = DustGenUtils.sbAppend(null, TOKEN_SEP, true, ((ModelEntity)type).globalId.replace(TYPE_TYPE, TYPE_MEMBER), itemId).toString();
        return getEntity(globalId, true);
    }

}