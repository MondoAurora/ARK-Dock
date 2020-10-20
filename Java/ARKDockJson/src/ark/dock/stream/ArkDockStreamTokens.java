package ark.dock.stream;

import ark.dock.ArkDockConsts;
import ark.dock.ArkDockModelMeta;
import ark.dock.ArkDockTokensDust;
import ark.dock.ArkDockTokensMind;
import ark.dock.ArkDockTokensTools;

public interface ArkDockStreamTokens extends ArkDockTokensMind, ArkDockTokensTools, ArkDockTokensDust {
	
	public class Stream implements ArkDockConsts{
		public final DustEntity eUnit;

		public final DustEntity typStream;
		public final DustEntity memStreamUrl;
		
		public Stream(ArkDockModelMeta meta) {
			eUnit = meta.getUnit("Stream");

			typStream = meta.getType(eUnit, "Stream");
			memStreamUrl = meta.defineMember(typStream, "Url", DustValType.RAW, DustCollType.ONE);
		}
	}
	
	public class StreamBin implements ArkDockConsts{
		public final DustEntity eUnit;

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
		
		public StreamBin(ArkDockModelMeta meta) {
			eUnit = meta.getUnit("StreamBin");

			typStreamDef = meta.getType(eUnit, "StreamDef");
			memStreamStartBlock = meta.defineMember(typStreamDef, "StartBlock", DustValType.REF, DustCollType.ONE);
			memStreamSwitches = meta.defineMember(typStreamDef, "Switches", DustValType.REF, DustCollType.SET);
			
			typSwitchDef = meta.getType(eUnit, "SwitchDef");
			memSwitchOffset = meta.defineMember(typSwitchDef, "Offset", DustValType.REF, DustCollType.ONE);
			memSwitchCount = meta.defineMember(typSwitchDef, "Count", DustValType.REF, DustCollType.ONE);

			typBinBlockDef = meta.getType(eUnit, "BlockDef");
			typBinItemDef = meta.getType(eUnit, "Item");
			typBinBitfield = meta.getType(eUnit, "Bitfield");
			
			tagBinItemUnsigned = meta.defineTag(eUnit, "Unsigned", null);
			
			tagBinEndian = meta.defineTag(eUnit, "Endian", null);
			tagBinEndianLittle = meta.defineTag(eUnit, "Little", tagBinEndian);
			tagBinEndianBig = meta.defineTag(eUnit, "Big", tagBinEndian);
			
			tagBinItemType = meta.defineTag(eUnit, "ItemType", null);
			tagBinItemTypeRaw = meta.defineTag(eUnit, "Raw", tagBinItemType);
			tagBinItemTypeInt1 = meta.defineTag(eUnit, "Int1", tagBinItemType);
			tagBinItemTypeInt2 = meta.defineTag(eUnit, "Int2", tagBinItemType);
			tagBinItemTypeInt4 = meta.defineTag(eUnit, "Int4", tagBinItemType);
			tagBinItemTypeInt8 = meta.defineTag(eUnit, "Int8", tagBinItemType);
			tagBinItemTypeReal4 = meta.defineTag(eUnit, "Real4", tagBinItemType);
			tagBinItemTypeReal8 = meta.defineTag(eUnit, "Real8", tagBinItemType);
			tagBinItemTypeBits = meta.defineTag(eUnit, "Bits", tagBinItemType);
		}
	}
}
