package ark.dock.gui.swing;

import java.awt.Component;

import ark.dock.ArkDockConsts;
import ark.dock.ArkDockDsl;

public interface ArkDockSwingConsts extends ArkDockConsts, ArkDockDsl {

	public abstract class SwingAgent<CompType extends Component> extends ArkDockAgentWrapper<CompType> {}
}
