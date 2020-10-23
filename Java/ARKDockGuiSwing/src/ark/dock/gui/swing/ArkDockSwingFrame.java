package ark.dock.gui.swing;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import ark.dock.ArkDockDsl;

public class ArkDockSwingFrame extends JFrame implements ArkDockSwingConsts {
	private static final long serialVersionUID = 1L;
	
	public ArkDockSwingFrame() {
		// TODO Auto-generated constructor stub
	}

	public static class Agent extends SwingAgent<ArkDockSwingFrame> {
		@Override
		protected ArkDockSwingFrame createComponent() {
			ArkDockSwingFrame frm = new ArkDockSwingFrame();

			DustEntity def = getDef();

			String title = getMind().modMain.getMember(def, getDsl(ArkDockDsl.Text.class).eTextName, "App frame", null);

			frm.setTitle(title);

			frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			return frm;
		}

		@Override
		public DustResultType agentAction(DustAgentAction action) throws Exception {
			DustEntity e;
			DustEntity m;
			DustEntity def = getDef();
			ArkDockSwingFrame frame = getComponent();
			
			switch ( action ) {
			case BEGIN:
				JPanel cp = new JPanel(new BorderLayout());
				frame.setContentPane(cp);
				
				m = getDsl(ArkDockDsl.Generic.class).eCollMember;
				
				int cnt = getMind().modMain.getCount(def, m);
				
				for ( int i = 0; i < cnt; ++i ) {
					e = getMind().modMain.getMember(def, m, null, i);
					
					SwingAgent<?> a = getMind().modMain.getMember(e, getMind().tokNative.eNativeValueOne, null, i);
					
					cp.add(a.getComponent(), BorderLayout.CENTER);
				}
				
		        frame.pack();
		        frame.setVisible(true);

				return DustResultType.ACCEPT_READ;
			default:
				return super.agentAction(action);
			}

		}
	}

}