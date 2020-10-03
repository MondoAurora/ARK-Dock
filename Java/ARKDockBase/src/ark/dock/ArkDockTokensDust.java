package ark.dock;

public interface ArkDockTokensDust extends ArkDockConsts {

	public class Native implements ArkDockConsts {
		public final DustEntity eUnit;

		public final DustEntity eTypeNative;

		public final DustEntity eNativeValType;
		public final DustEntity eNativeCollType;
		public final DustEntity eNativeValueOne;
		public final DustEntity eNativeValueArr;
		
		public final DustEntity eTypeBinary;

		public final DustEntity eBinaryId;

		public Native(ArkDockModelMeta meta) {
			eUnit = meta.getUnit("Native");

			eTypeNative = meta.getType(eUnit, "Native");

			eNativeValType = meta.getMember(eTypeNative, "ValType");
			eNativeCollType = meta.getMember(eTypeNative, "CollType");
			eNativeValueOne = meta.getMember(eTypeNative, "ValueOne");
			eNativeValueArr = meta.getMember(eTypeNative, "ValueArr");
			
			eTypeBinary = meta.getType(eUnit, "Binary");
			eBinaryId = meta.getMember(eTypeBinary, "Id");
			
			meta.initMember(eNativeValType, DustValType.REF, DustCollType.ONE);
			meta.initMember(eNativeCollType, DustValType.REF, DustCollType.ONE);
			meta.initMember(eNativeValueOne, DustValType.RAW, DustCollType.ONE);
			meta.initMember(eNativeValueArr, DustValType.RAW, DustCollType.ARR);
			meta.initMember(eBinaryId, DustValType.RAW, DustCollType.ONE);
		}
	}

}
