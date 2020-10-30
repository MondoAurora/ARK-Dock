package ark.dock.stream.binary;

public interface ArkDockStreamBinConsts {
	enum BinNumType {
		Byte, Short, Int, Double;

		public boolean isReal() {
			return Double == this;
		}
	}
}
