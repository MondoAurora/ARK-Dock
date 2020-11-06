package ark.dock.io;

import ark.dock.ArkDock;
import ark.dock.ArkDockConsts;
import ark.dock.ArkDockDsl;
import ark.dock.ArkDockDslBuilder;

public interface ArkDockDslIO extends ArkDockDsl {
	
	public class DslStream implements ArkDockConsts{
		public final DustEntity unit;

		public final DustEntity typStream;
		public final DustEntity memStreamUrl;

		public final DustEntity tagFileMissing;
		
		public DslStream() {
			ArkDockDslBuilder meta = ArkDock.getDslBuilder("Stream");
			unit = meta.getUnit();

			typStream = meta.getType("Stream");
			memStreamUrl = meta.defineMember(typStream, "Url", DustValType.RAW, DustCollType.ONE);
			
			tagFileMissing = meta.defineTag("FileMissing", null);
		}
	}
	
	public class DslStreamBin implements ArkDockConsts{
		public final DustEntity unit;

		public final DustEntity typStreamDef;
		public final DustEntity memStreamStartBlock;
		public final DustEntity memStreamSwitches;

		public final DustEntity typSwitchDef;
		public final DustEntity memSwitchOffset;
		public final DustEntity memSwitchCount;

		public final DustEntity typBinBlockDef;
		public final DustEntity typBinItemDef;
		public final DustEntity typBinBitfield;
		
		public final DustEntity tagBinItemUnsigned;
		
		public final DustEntity tagBinEndian;
		public final DustEntity tagBinEndianLittle;
		public final DustEntity tagBinEndianBig;
		
		public final DustEntity tagBinItemType;
		public final DustEntity tagBinItemTypeRaw;
		public final DustEntity tagBinItemTypeInt1;
		public final DustEntity tagBinItemTypeInt2;
		public final DustEntity tagBinItemTypeInt4;
		public final DustEntity tagBinItemTypeInt8;
		public final DustEntity tagBinItemTypeReal4;
		public final DustEntity tagBinItemTypeReal8;
		public final DustEntity tagBinItemTypeBits;
		
		public DslStreamBin() {
			ArkDockDslBuilder meta = ArkDock.getDslBuilder("StreamBin");
			unit = meta.getUnit();

			typStreamDef = meta.getType("StreamDef");
			memStreamStartBlock = meta.defineMember(typStreamDef, "StartBlock", DustValType.REF, DustCollType.ONE);
			memStreamSwitches = meta.defineMember(typStreamDef, "Switches", DustValType.REF, DustCollType.SET);
			
			typSwitchDef = meta.getType("SwitchDef");
			memSwitchOffset = meta.defineMember(typSwitchDef, "Offset", DustValType.REF, DustCollType.ONE);
			memSwitchCount = meta.defineMember(typSwitchDef, "Count", DustValType.REF, DustCollType.ONE);

			typBinBlockDef = meta.getType("BlockDef");
			typBinItemDef = meta.getType("Item");
			typBinBitfield = meta.getType("Bitfield");
			
			tagBinItemUnsigned = meta.defineTag("Unsigned", null);
			
			tagBinEndian = meta.defineTag("Endian", null);
			tagBinEndianLittle = meta.defineTag("Little", tagBinEndian);
			tagBinEndianBig = meta.defineTag("Big", tagBinEndian);
			
			tagBinItemType = meta.defineTag("ItemType", null);
			tagBinItemTypeRaw = meta.defineTag("Raw", tagBinItemType);
			tagBinItemTypeInt1 = meta.defineTag("Int1", tagBinItemType);
			tagBinItemTypeInt2 = meta.defineTag("Int2", tagBinItemType);
			tagBinItemTypeInt4 = meta.defineTag("Int4", tagBinItemType);
			tagBinItemTypeInt8 = meta.defineTag("Int8", tagBinItemType);
			tagBinItemTypeReal4 = meta.defineTag("Real4", tagBinItemType);
			tagBinItemTypeReal8 = meta.defineTag("Real8", tagBinItemType);
			tagBinItemTypeBits = meta.defineTag("Bits", tagBinItemType);
		}
	}
}
