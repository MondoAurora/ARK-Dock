package ark.dock;

public interface ArkDockDslDust extends ArkDockConsts {

	public class DslNative implements ArkDockConsts {
		public final DustEntity unit;

		public final DustEntity typNative;

		public final DustEntity memNativeValType;
		public final DustEntity memNativeCollType;
		public final DustEntity memNativeValueOne;
		public final DustEntity memNativeValueArr;
		
		public final DustEntity typBinary;

		public final DustEntity memBinaryId;

		public DslNative(ArkDockModelMeta meta) {
			unit = meta.getUnit("Native");

			typNative = meta.getType(unit, "Native");

			memNativeValType = meta.getMember(typNative, "ValType");
			memNativeCollType = meta.getMember(typNative, "CollType");
			memNativeValueOne = meta.getMember(typNative, "ValueOne");
			memNativeValueArr = meta.getMember(typNative, "ValueArr");
			
			typBinary = meta.getType(unit, "Binary");
			memBinaryId = meta.getMember(typBinary, "Id");
			
			meta.initMember(memNativeValType, DustValType.REF, DustCollType.ONE);
			meta.initMember(memNativeCollType, DustValType.REF, DustCollType.ONE);
			meta.initMember(memNativeValueOne, DustValType.RAW, DustCollType.ONE);
			meta.initMember(memNativeValueArr, DustValType.RAW, DustCollType.ARR);
			meta.initMember(memBinaryId, DustValType.RAW, DustCollType.ONE);
		}
	}
}
