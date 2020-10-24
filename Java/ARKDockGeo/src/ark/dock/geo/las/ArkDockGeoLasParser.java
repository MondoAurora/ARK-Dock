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
	DslLasHeader dslLasHdr;

	public ArkDockGeoLasParser(ArkDockModelMeta meta) {
		this.meta = meta;
		dslLasHdr = new DslLasHeader(meta);
	}

	public void loadHeader(InputStream in, SerializeAgent<DustEntityContext> receiver) throws Exception {
		ReadContext ctx = new ReadContext(receiver, in, ByteOrder.LITTLE_ENDIAN);

		try {
			ctx.sendEntityAction(dslLasHdr.typLasHeader, DustAgentAction.BEGIN);
			
			ctx.sendString(dslLasHdr.memFileSignature, 4);
			ctx.sendNum(dslLasHdr.memFileSourceID, BinNumType.Short);
			ctx.sendNum(dslLasHdr.memGlobalEncoding, BinNumType.Short);

			ctx.sendNum(dslLasHdr.memProjIdGUID1, BinNumType.Int);
			ctx.sendNum(dslLasHdr.memProjIdGUID2, BinNumType.Short);
			ctx.sendNum(dslLasHdr.memProjIdGUID3, BinNumType.Short);
			ctx.sendString(dslLasHdr.memProjIdGUID4, 8);

			ctx.sendNum(dslLasHdr.memVerMajor, BinNumType.Byte);
			ctx.sendNum(dslLasHdr.memVerMinor, BinNumType.Byte);

			ctx.sendString(dslLasHdr.memSysId, 32);
			ctx.sendString(dslLasHdr.memGenSw, 32);

			ctx.sendNum(dslLasHdr.memCreateDayOfYear, BinNumType.Short);
			ctx.sendNum(dslLasHdr.memCreateYear, BinNumType.Short);

			ctx.sendNum(dslLasHdr.memHdrSize, BinNumType.Short);
			ctx.sendNum(dslLasHdr.memPtDataOffset, BinNumType.Int);
			ctx.sendNum(dslLasHdr.memVLRNum, BinNumType.Int);
			ctx.sendNum(dslLasHdr.memPtDataRecFmt, BinNumType.Byte);
			ctx.sendNum(dslLasHdr.memPtDataRecLen, BinNumType.Short);

			ctx.sendNum(dslLasHdr.memPtRecNum, BinNumType.Int);
			ctx.sendNumArr(dslLasHdr.memPtByRetNum, BinNumType.Int, 5);

			ctx.sendNum(dslLasHdr.memScaleX, BinNumType.Double);
			ctx.sendNum(dslLasHdr.memScaleY, BinNumType.Double);
			ctx.sendNum(dslLasHdr.memScaleZ, BinNumType.Double);
			ctx.sendNum(dslLasHdr.memOffsetX, BinNumType.Double);
			ctx.sendNum(dslLasHdr.memOffsetY, BinNumType.Double);
			ctx.sendNum(dslLasHdr.memOffsetZ, BinNumType.Double);
			ctx.sendNum(dslLasHdr.memMaxX, BinNumType.Double);
			ctx.sendNum(dslLasHdr.memMinX, BinNumType.Double);
			ctx.sendNum(dslLasHdr.memMaxY, BinNumType.Double);
			ctx.sendNum(dslLasHdr.memMinY, BinNumType.Double);
			ctx.sendNum(dslLasHdr.memMaxZ, BinNumType.Double);
			ctx.sendNum(dslLasHdr.memMinZ, BinNumType.Double);
		} finally {
			ctx.sendEntityAction(dslLasHdr.typLasHeader, DustAgentAction.END);

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
