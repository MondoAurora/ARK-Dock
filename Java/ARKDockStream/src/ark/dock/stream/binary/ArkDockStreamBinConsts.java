package ark.dock.stream.binary;

import ark.dock.stream.ArkDockStreamConsts;

public interface ArkDockStreamBinConsts extends ArkDockStreamConsts {
	enum BinNumType {
		Byte, Short, Int, Double;

		public boolean isReal() {
			return Double == this;
		}
	}
}
