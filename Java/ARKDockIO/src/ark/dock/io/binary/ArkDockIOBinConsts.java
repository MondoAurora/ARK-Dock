package ark.dock.io.binary;

import ark.dock.io.ArkDockIOConsts;

public interface ArkDockIOBinConsts extends ArkDockIOConsts {
	enum BinNumType {
		Byte, Short, Int, Double;

		public boolean isReal() {
			return Double == this;
		}
	}
}
