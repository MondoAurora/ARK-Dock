package ark.dock.geo.las;

import ark.dock.ArkDockConsts;
import ark.dock.ArkDockModelMeta;
import ark.dock.ArkDockDslDust;
import ark.dock.ArkDockDslMind;
import ark.dock.ArkDockDslTools;

// https://support.geocue.com/what-is-the-las-format/

public interface ArkDockDslGeoLas extends ArkDockDslMind, ArkDockDslTools, ArkDockDslDust {
	
	public class DslLasHeader implements ArkDockConsts{
		public final DustEntity eUnit;

		public final DustEntity typLasHeader;
		
		public final DustEntity memFileSignature;
		public final DustEntity memFileSourceID;
		public final DustEntity memGlobalEncoding;

		public final DustEntity memProjIdGUID1;
		public final DustEntity memProjIdGUID2;
		public final DustEntity memProjIdGUID3;
		public final DustEntity memProjIdGUID4;
		
		public final DustEntity memVerMajor;
		public final DustEntity memVerMinor;

		public final DustEntity memSysId;
		public final DustEntity memGenSw;
		
		public final DustEntity memCreateDayOfYear;
		public final DustEntity memCreateYear;

		public final DustEntity memHdrSize;
		public final DustEntity memPtDataOffset;
		public final DustEntity memVLRNum;
		public final DustEntity memPtDataRecFmt;
		public final DustEntity memPtDataRecLen;
		public final DustEntity memPtRecNum;
		public final DustEntity memPtByRetNum;
		public final DustEntity memScaleX;
		public final DustEntity memScaleY;
		public final DustEntity memScaleZ;
		public final DustEntity memOffsetX;
		public final DustEntity memOffsetY;
		public final DustEntity memOffsetZ;
		public final DustEntity memMaxX;
		public final DustEntity memMinX;
		public final DustEntity memMaxY;
		public final DustEntity memMinY;
		public final DustEntity memMaxZ;
		public final DustEntity memMinZ;
		
		public final DustEntity memVer10Reserved;
		public final DustEntity memVer11Reserved;
		
		public final DustEntity memWaveformDPROffset;
		public final DustEntity memExtVLROffset;
		public final DustEntity memExtVLRNum;
		public final DustEntity memPtRecNum64;
		public final DustEntity memPtByRetNum64;
		
		public final DustEntity tagHeaderVer;
		public final DustEntity tagHeaderVer10;
		public final DustEntity tagHeaderVer11;
		public final DustEntity tagHeaderVer12;
		public final DustEntity tagHeaderVer13;
		public final DustEntity tagHeaderVer14;
		

		public DslLasHeader(ArkDockModelMeta meta) {
			eUnit = meta.getUnit("Las");

			typLasHeader = meta.getType(eUnit, "Header");
			memFileSignature = meta.defineMember(typLasHeader, "FileSignature", DustValType.RAW, DustCollType.ONE);
			memFileSourceID = meta.defineMember(typLasHeader, "FileSourceID", DustValType.INT, DustCollType.ONE);
			memGlobalEncoding = meta.defineMember(typLasHeader, "GlobalEncoding", DustValType.INT, DustCollType.ONE);

			memProjIdGUID1 = meta.defineMember(typLasHeader, "ProjIdGUID1", DustValType.INT, DustCollType.ONE);
			memProjIdGUID2 = meta.defineMember(typLasHeader, "ProjIdGUID2", DustValType.INT, DustCollType.ONE);
			memProjIdGUID3 = meta.defineMember(typLasHeader, "ProjIdGUID3", DustValType.INT, DustCollType.ONE);
			memProjIdGUID4 = meta.defineMember(typLasHeader, "ProjIdGUID4", DustValType.RAW, DustCollType.ONE);

			memVerMajor = meta.defineMember(typLasHeader, "VerMajor", DustValType.INT, DustCollType.ONE);
			memVerMinor = meta.defineMember(typLasHeader, "VerMinor", DustValType.INT, DustCollType.ONE);

			memSysId = meta.defineMember(typLasHeader, "SysId", DustValType.RAW, DustCollType.ONE);
			memGenSw = meta.defineMember(typLasHeader, "GenSw", DustValType.RAW, DustCollType.ONE);
			
			memCreateDayOfYear = meta.defineMember(typLasHeader, "CreateDayOfYear", DustValType.INT, DustCollType.ONE);
			memCreateYear = meta.defineMember(typLasHeader, "CreateYear", DustValType.INT, DustCollType.ONE);
			
			memHdrSize = meta.defineMember(typLasHeader, "HdrSize", DustValType.INT, DustCollType.ONE);
			memPtDataOffset = meta.defineMember(typLasHeader, "PtDataOffset", DustValType.INT, DustCollType.ONE);
			memVLRNum = meta.defineMember(typLasHeader, "VLRNum", DustValType.INT, DustCollType.ONE);
			memPtDataRecFmt = meta.defineMember(typLasHeader, "PtDataRecFmt", DustValType.INT, DustCollType.ONE);
			memPtDataRecLen = meta.defineMember(typLasHeader, "PtDataRecLen", DustValType.INT, DustCollType.ONE);
			memPtRecNum = meta.defineMember(typLasHeader, "RecNum", DustValType.INT, DustCollType.ONE);
			memPtByRetNum = meta.defineMember(typLasHeader, "ByRetNum", DustValType.INT, DustCollType.ONE);
			
			memScaleX = meta.defineMember(typLasHeader, "ScaleX", DustValType.REAL, DustCollType.ONE);
			memScaleY = meta.defineMember(typLasHeader, "ScaleY", DustValType.REAL, DustCollType.ONE);
			memScaleZ = meta.defineMember(typLasHeader, "ScaleZ", DustValType.REAL, DustCollType.ONE);
			
			memOffsetX = meta.defineMember(typLasHeader, "OffsetX", DustValType.REAL, DustCollType.ONE);
			memOffsetY = meta.defineMember(typLasHeader, "OffsetY", DustValType.REAL, DustCollType.ONE);
			memOffsetZ = meta.defineMember(typLasHeader, "OffsetZ", DustValType.REAL, DustCollType.ONE);

			memMaxX = meta.defineMember(typLasHeader, "MaxX", DustValType.REAL, DustCollType.ONE);
			memMinX = meta.defineMember(typLasHeader, "MinX", DustValType.REAL, DustCollType.ONE);
			memMaxY = meta.defineMember(typLasHeader, "MaxY", DustValType.REAL, DustCollType.ONE);
			memMinY = meta.defineMember(typLasHeader, "MinY", DustValType.REAL, DustCollType.ONE);
			memMaxZ = meta.defineMember(typLasHeader, "MaxZ", DustValType.REAL, DustCollType.ONE);
			memMinZ = meta.defineMember(typLasHeader, "MinZ", DustValType.REAL, DustCollType.ONE);
			
			memVer10Reserved = meta.defineMember(typLasHeader, "Ver10Reserved", DustValType.INT, DustCollType.ONE);
			memVer11Reserved = meta.defineMember(typLasHeader, "Ver11Reserved", DustValType.INT, DustCollType.ONE);
			
			memWaveformDPROffset = meta.defineMember(typLasHeader, "WaveformDPROffset", DustValType.INT, DustCollType.ONE);
			memExtVLROffset = meta.defineMember(typLasHeader, "ExtVLROffset", DustValType.INT, DustCollType.ONE);
			memExtVLRNum = meta.defineMember(typLasHeader, "ExtVLRNum", DustValType.INT, DustCollType.ONE);
			memPtRecNum64 = meta.defineMember(typLasHeader, "PtRecNum64", DustValType.INT, DustCollType.ONE);
			memPtByRetNum64 = meta.defineMember(typLasHeader, "PtByRetNum64", DustValType.INT, DustCollType.ONE);
			
			tagHeaderVer = meta.defineTag(eUnit, "HeaderVer", null);
			tagHeaderVer10 = meta.defineTag(eUnit, "1.0", tagHeaderVer);
			tagHeaderVer11 = meta.defineTag(eUnit, "1.1", tagHeaderVer);
			tagHeaderVer12 = meta.defineTag(eUnit, "1.2", tagHeaderVer);
			tagHeaderVer13 = meta.defineTag(eUnit, "1.3", tagHeaderVer);
			tagHeaderVer14 = meta.defineTag(eUnit, "1.4", tagHeaderVer);
		}		
	}
	
	public class DslLasPoint {
		public final DustEntity eUnit;

		public final DustEntity typLasPoint;
		
		public final DustEntity memX;
		public final DustEntity memY;
		public final DustEntity memZ;
		public final DustEntity memIntensity;
		
		public final DustEntity memRetNum;
		public final DustEntity memRetCount;

		public final DustEntity memClassFlags;
		public final DustEntity memScanChannel;
		public final DustEntity memScanDirFlag;
		public final DustEntity memEdgeFlag;

		public final DustEntity memClassification;

		public final DustEntity memUserData;
		public final DustEntity memScanAngle;
		public final DustEntity memPtSrcId;
		public final DustEntity memGpsTime;
		
		public final DustEntity memRed;
		public final DustEntity memGreen;
		public final DustEntity memBlue;
		public final DustEntity memNIR;
		public final DustEntity memWavePackDescIdx;
		public final DustEntity memWaveDataOffset;
		public final DustEntity memWavePackSize;
		public final DustEntity memWaveLocRetPoint;
		public final DustEntity memParDx;
		public final DustEntity memParDy;
		public final DustEntity memParDz;

		public DslLasPoint(ArkDockModelMeta meta) {
			eUnit = meta.getUnit("Las");

			typLasPoint = meta.getType(eUnit, "Point");
			
			memX = meta.defineMember(typLasPoint, "X", DustValType.INT, DustCollType.ONE);
			memY = meta.defineMember(typLasPoint, "Y", DustValType.INT, DustCollType.ONE);
			memZ = meta.defineMember(typLasPoint, "Z", DustValType.INT, DustCollType.ONE);
			memIntensity = meta.defineMember(typLasPoint, "Intensity", DustValType.INT, DustCollType.ONE);
			memRetNum = meta.defineMember(typLasPoint, "RetNum", DustValType.INT, DustCollType.ONE);
			memRetCount = meta.defineMember(typLasPoint, "RetCount", DustValType.INT, DustCollType.ONE);
			memClassFlags = meta.defineMember(typLasPoint, "ClassFlags", DustValType.INT, DustCollType.ONE);
			memScanChannel = meta.defineMember(typLasPoint, "ScanChannel", DustValType.INT, DustCollType.ONE);
			memScanDirFlag = meta.defineMember(typLasPoint, "ScanDirFlag", DustValType.INT, DustCollType.ONE);
			memEdgeFlag = meta.defineMember(typLasPoint, "EdgeFlag", DustValType.INT, DustCollType.ONE);
			memClassification = meta.defineMember(typLasPoint, "Classification", DustValType.INT, DustCollType.ONE);
			
			memUserData = meta.defineMember(typLasPoint, "UserData", DustValType.INT, DustCollType.ONE);
			memScanAngle = meta.defineMember(typLasPoint, "ScanAngle", DustValType.INT, DustCollType.ONE);
			memPtSrcId = meta.defineMember(typLasPoint, "PtSrcId", DustValType.INT, DustCollType.ONE);
			memGpsTime = meta.defineMember(typLasPoint, "GpsTime", DustValType.INT, DustCollType.ONE);
			
			memRed = meta.defineMember(typLasPoint, "Red", DustValType.INT, DustCollType.ONE);
			memGreen = meta.defineMember(typLasPoint, "Green", DustValType.INT, DustCollType.ONE);
			memBlue = meta.defineMember(typLasPoint, "Blue", DustValType.INT, DustCollType.ONE);
			memNIR = meta.defineMember(typLasPoint, "NIR", DustValType.INT, DustCollType.ONE);
			
			memWavePackDescIdx = meta.defineMember(typLasPoint, "WavePackDescIdx", DustValType.INT, DustCollType.ONE);
			memWaveDataOffset = meta.defineMember(typLasPoint, "WaveDataOffset", DustValType.INT, DustCollType.ONE);
			memWavePackSize = meta.defineMember(typLasPoint, "WavePackSize", DustValType.INT, DustCollType.ONE);
			memWaveLocRetPoint = meta.defineMember(typLasPoint, "WaveLocRetPoint", DustValType.REAL, DustCollType.ONE);
			
			memParDx = meta.defineMember(typLasPoint, "ParDx", DustValType.REAL, DustCollType.ONE);
			memParDy = meta.defineMember(typLasPoint, "ParDy", DustValType.REAL, DustCollType.ONE);
			memParDz = meta.defineMember(typLasPoint, "ParDz", DustValType.REAL, DustCollType.ONE);
		}
	}
}
