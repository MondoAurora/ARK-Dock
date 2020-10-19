package ark.dock.geo.las;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ark.dock.ArkDockModelMeta;
import ark.dock.ArkDockModelSerializer;
import ark.dock.ArkDockModelSerializer.SerializeAgent;
import dust.gen.DustGenException;
import dust.gen.DustGenLog;
import dust.gen.DustGenUtils;

public class ArkDockGeoLasParser implements ArkDockGeoLasConsts {
	class ReadContext {
		ByteBuffer buf;
		SerializeAgent<DustEntityContext> receiver;
		DustEntityContext ctx;
		boolean localCtx;

		public ReadContext(SerializeAgent<DustEntityContext> receiver, InputStream in, ByteOrder order)
				throws Exception {
			this.receiver = receiver;
			ctx = receiver.getActionCtx();
			if ( localCtx = (null == ctx) ) {
				ctx = new DustEntityContext();
				receiver.setActionCtx(ctx);
			}

			byte[] data = new byte[400];
			if ( in.read(data, 0, 400) < 400 ) {
				throw new Exception("unable to read file contents");
			}

			buf = ByteBuffer.wrap(data);
			buf.order(order);

			receiver.agentAction(DustAgentAction.INIT);
		}

		public void release() {
			try {
				receiver.agentAction(DustAgentAction.RELEASE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if ( localCtx ) {
				receiver.setActionCtx(null);
			}
		}
		
		public DustResultType sendEntityAction(DustEntity e, DustAgentAction action) throws Exception {
			ctx.block = EntityBlock.Entity;
			ctx.entityId = e;
			return receiver.agentAction(action);
		}

		public DustResultType sendNum(DustEntity mem, BinNumType type) throws Exception {
			return sendMember(mem, getNum(type), type);
		}
		
		public DustResultType sendNumArr(DustEntity mem, BinNumType type, int count) throws Exception {
			DustResultType ret;
			ctx.block = EntityBlock.Member;
			ctx.member = mem;
			ctx.collType = DustCollType.ARR;
			
			if ( DustGenUtils.isReadOn(ret = receiver.agentAction(DustAgentAction.BEGIN)) ) {
				for (int i = 0; i < count; ++i) {
					ret = sendVal(getNum(type), type);
				}
			}
			receiver.agentAction(DustAgentAction.END);

			return ret;
		}

		public DustResultType sendString(DustEntity mem, int len) throws Exception {
			byte[] chBuf = new byte[len + 1];

			buf.get(chBuf, 0, len);
			chBuf[len] = 0;
			return sendMember(mem, new String(chBuf), null);
		}

		private Object getNum(BinNumType type) {
			switch ( type ) {
			case Byte:
				return buf.get();
			case Short:
				return buf.getShort();
			case Int:
				return buf.getInt();
			case Double:
				return buf.getDouble();
			}

			return DustGenException.throwException(null, "Unhandled type", type);
		}

		private DustResultType sendVal(Object val, BinNumType nt) throws Exception {
			ctx.value = val;
			ctx.valType = (null == nt) ? DustValType.RAW : nt.isReal() ? DustValType.REAL : DustValType.INT;
			return receiver.agentAction(DustAgentAction.PROCESS);
		}

		private DustResultType sendMember(DustEntity mem, Object val, BinNumType nt) throws Exception {
			DustResultType ret;
			ctx.block = EntityBlock.Member;
			ctx.member = mem;
			ctx.collType = DustCollType.ONE;
			
			if ( DustGenUtils.isReadOn(ret = receiver.agentAction(DustAgentAction.BEGIN)) ) {
				ret = sendVal(val, nt);
			}
			receiver.agentAction(DustAgentAction.END);

			return ret;
		}
	}

	ArkDockModelMeta meta;
	LasHeader tokLasHdr;

	public ArkDockGeoLasParser(ArkDockModelMeta meta) {
		this.meta = meta;
		tokLasHdr = new LasHeader(meta);
	}

	public void loadHeader(InputStream in, SerializeAgent<DustEntityContext> receiver) throws Exception {
		ReadContext ctx = new ReadContext(receiver, in, ByteOrder.LITTLE_ENDIAN);

		try {
			ctx.sendEntityAction(tokLasHdr.typLasHeader, DustAgentAction.BEGIN);
			
			ctx.sendString(tokLasHdr.memFileSignature, 4);
			ctx.sendNum(tokLasHdr.memFileSourceID, BinNumType.Short);
			ctx.sendNum(tokLasHdr.memGlobalEncoding, BinNumType.Short);

			ctx.sendNum(tokLasHdr.memProjIdGUID1, BinNumType.Int);
			ctx.sendNum(tokLasHdr.memProjIdGUID2, BinNumType.Short);
			ctx.sendNum(tokLasHdr.memProjIdGUID3, BinNumType.Short);
			ctx.sendString(tokLasHdr.memProjIdGUID4, 8);

			ctx.sendNum(tokLasHdr.memVerMajor, BinNumType.Byte);
			ctx.sendNum(tokLasHdr.memVerMinor, BinNumType.Byte);

			ctx.sendString(tokLasHdr.memSysId, 32);
			ctx.sendString(tokLasHdr.memGenSw, 32);

			ctx.sendNum(tokLasHdr.memCreateDayOfYear, BinNumType.Short);
			ctx.sendNum(tokLasHdr.memCreateYear, BinNumType.Short);

			ctx.sendNum(tokLasHdr.memHdrSize, BinNumType.Short);
			ctx.sendNum(tokLasHdr.memPtDataOffset, BinNumType.Int);
			ctx.sendNum(tokLasHdr.memVLRNum, BinNumType.Int);
			ctx.sendNum(tokLasHdr.memPtDataRecFmt, BinNumType.Byte);
			ctx.sendNum(tokLasHdr.memPtDataRecLen, BinNumType.Short);

			ctx.sendNum(tokLasHdr.memPtRecNum, BinNumType.Int);
			ctx.sendNumArr(tokLasHdr.memPtByRetNum, BinNumType.Int, 5);

			ctx.sendNum(tokLasHdr.memScaleX, BinNumType.Double);
			ctx.sendNum(tokLasHdr.memScaleY, BinNumType.Double);
			ctx.sendNum(tokLasHdr.memScaleZ, BinNumType.Double);
			ctx.sendNum(tokLasHdr.memOffsetX, BinNumType.Double);
			ctx.sendNum(tokLasHdr.memOffsetY, BinNumType.Double);
			ctx.sendNum(tokLasHdr.memOffsetZ, BinNumType.Double);
			ctx.sendNum(tokLasHdr.memMaxX, BinNumType.Double);
			ctx.sendNum(tokLasHdr.memMinX, BinNumType.Double);
			ctx.sendNum(tokLasHdr.memMaxY, BinNumType.Double);
			ctx.sendNum(tokLasHdr.memMinY, BinNumType.Double);
			ctx.sendNum(tokLasHdr.memMaxZ, BinNumType.Double);
			ctx.sendNum(tokLasHdr.memMinZ, BinNumType.Double);
		} finally {
			ctx.sendEntityAction(tokLasHdr.typLasHeader, DustAgentAction.END);

			ctx.release();
		}
	}

	public static void main(String[] args) throws Exception {
		String fName = args[0];
		DustGenLog.log("Reading", fName);

		try (FileInputStream in = new FileInputStream(fName)) {
			ArkDockModelMeta meta = new ArkDockModelMeta();

			ArkDockGeoLasParser reader = new ArkDockGeoLasParser(meta);
			ArkDockModelSerializer.Dump receiver = new ArkDockModelSerializer.Dump();

			reader.loadHeader(in, receiver);
		}
	}
}
