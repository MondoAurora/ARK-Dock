package ark.dock;

public interface ArkDockDslDust extends ArkDockConsts, ArkDockBootConsts {

	public class DslNative {
		public final DustEntity unit;

		public final DustEntity typNative;

		public final DustEntity memNativeValType;
		public final DustEntity memNativeCollType;
		public final DustEntity memNativeValueOne;
		public final DustEntity memNativeValueArr;
		
		public final DustEntity typBinary;

		public final DustEntity memBinaryId;

		public DslNative() {
			ArkDockDslBuilder meta = ArkDock.getDslBuilder(UNITNAME_NATIVE);
			
			unit = meta.getUnit();

			typNative = meta.getType(TYPENAME_NATIVE);

			memNativeValType = meta.defineMember(typNative, "ValType", DustValType.REF, DustCollType.ONE);
			memNativeCollType = meta.defineMember(typNative, MEMBERNAME_NATIVE_COLLTYPE, DustValType.REF, DustCollType.ONE);
			memNativeValueOne = meta.defineMember(typNative, MEMBERNAME_NATIVE_VALUEONE, DustValType.RAW, DustCollType.ONE);
			memNativeValueArr = meta.defineMember(typNative, "ValueArr", DustValType.RAW, DustCollType.ARR);
			
			typBinary = meta.getType(TYPENAME_BINARY);
			memBinaryId = meta.defineMember(typBinary, MEMBERNAME_BINARY_NAME, DustValType.RAW, DustCollType.ONE);
			
//			meta.initMember(memNativeValType, DustValType.REF, DustCollType.ONE);
//			meta.initMember(memNativeCollType, DustValType.REF, DustCollType.ONE);
//			meta.initMember(memNativeValueOne, DustValType.RAW, DustCollType.ONE);
//			meta.initMember(memNativeValueArr, DustValType.RAW, DustCollType.ARR);
//			meta.initMember(memBinaryId, DustValType.RAW, DustCollType.ONE);
		}
	}
}
