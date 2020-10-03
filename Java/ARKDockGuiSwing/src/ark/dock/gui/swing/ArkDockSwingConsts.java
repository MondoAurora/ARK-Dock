package ark.dock.gui.swing;

import java.awt.Component;

import ark.dock.ArkDockConsts;
import ark.dock.ArkDockMind.BaseAgent;
import ark.dock.ArkDockMind.ArkDockMindContext;
import dust.gen.DustGenUtils;

public interface ArkDockSwingConsts extends ArkDockConsts {

	@SuppressWarnings("rawtypes")
	public abstract class SwingAgent<CompType extends Component> extends BaseAgent<ArkDockMindContext> {
		private CompType component;

		public CompType getComponent() {
			return component;
		}

		protected abstract CompType createComponent();

		@Override
		public DustResultType agentAction(DustAgentAction action) throws Exception {
			DustResultType ret = super.agentAction(action);

			if  ( DustGenUtils.isReject(ret) ) {
				return ret;
			}
			
			switch ( action ) {
			case INIT:
				component = createComponent();
				break;
			default:
				break;
			}
			
			return ret;
		}
	}
}
