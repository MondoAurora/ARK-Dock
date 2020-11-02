package ark.dock.gui.swing;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import ark.dock.ArkDock;
import ark.dock.ArkDockUnit;
import dust.gen.DustGenUtils;

public class ArkDockSwingFrame extends JFrame implements ArkDockSwingConsts {
	private static final long serialVersionUID = 1L;

	public ArkDockSwingFrame() {
		// TODO Auto-generated constructor stub
	}

	public static class Agent extends SwingAgent<ArkDockSwingFrame> {
		@Override
		protected ArkDockSwingFrame createBinObj() throws Exception {
			ArkDockSwingFrame frm = new ArkDockSwingFrame();

			DustEntity def = getDef();

			String title = ArkDock.access(DustDialogCmd.GET, def, ArkDock.getDsl(DslText.class).memTextName,
					"App frame", null);

			frm.setTitle(title);

			frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			return frm;
		}

		@Override
		public DustResultType agentAction(DustAgentAction action) throws Exception {
			DustResultType ret = super.agentAction(action);

			if ( !DustGenUtils.isReject(ret) ) {
				DustEntity e;
				DustEntity mColl;
				DustEntity mNat;
				DustEntity def = getDef();
				ArkDockSwingFrame frame = getBinObj();
				ArkDockUnit unit = ArkDock.getMind().getMainUnit();

				switch ( action ) {
				case BEGIN:
					JPanel cp = new JPanel(new BorderLayout());
					frame.setContentPane(cp);

					mColl = ArkDock.getDsl(DslGeneric.class).memCollMember;
					mNat = ArkDock.getDsl(DslNative.class).memNativeValueOne;

					int cnt = unit.getCount(def, mColl);

					for (int i = 0; i < cnt; ++i) {
						e = unit.getMember(def, mColl, null, i);

						SwingAgent<?> a = unit.getMember(e, mNat, null, i);

						cp.add(a.getBinObj(), BorderLayout.CENTER);
					}

					frame.pack();
					frame.setVisible(true);

					ret = DustResultType.ACCEPT_READ;
				default:
					break;
				}
			}
			return ret;
		}
	}

}