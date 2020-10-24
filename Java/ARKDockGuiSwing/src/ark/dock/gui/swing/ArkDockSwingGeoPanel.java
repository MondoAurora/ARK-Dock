package ark.dock.gui.swing;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import ark.dock.ArkDockDsl;
import ark.dock.ArkDockModel;
import ark.dock.ArkDockModelMeta;
import ark.dock.ArkDockModelSerializer;
import ark.dock.ArkDockModelSerializer.SerializeAgent;
import ark.dock.geo.json.ArkDockGeojsonConsts.GeojsonPolygon;
import dust.gen.DustGenLog;
import dust.gen.DustGenTranslator;

public class ArkDockSwingGeoPanel extends JPanel implements ArkDockSwingConsts {
	private static final long serialVersionUID = 1L;
	Rectangle2D bbox;
	AffineTransform at = new AffineTransform();
	AffineTransform atInv = null;

	Color cArea = new Color(0, 255, 0, 100);
	BasicStroke stroke;

	private JPanel drawingPane;

	private final ArkDockDsl.DslNative dslNative;
	private final ArkDockModel modMain;

	DustGenTranslator<DustEntity, Shape> trContent = new DustGenTranslator<DustEntity, Shape>();
	Shape shpSel;
	Shape shpSelDrawn;

	private MouseListener ml = new MouseAdapter() {
		public void mouseReleased(MouseEvent e) {
			if ( SwingUtilities.isRightMouseButton(e) ) {
				reload();
			} else {
				if ( null == atInv ) {
					try {
						atInv = at.createInverse();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

				Point2D pt = new Point2D.Double(e.getX(), e.getY());
				atInv.transform(pt, pt);

				DustGenLog.log(DustEventLevel.INFO, "Click!", "[", pt.getX(), ",", pt.getY(), "]");

				shpSel = null;
				Rectangle2D rr;
				Double dist = null;

				for (Shape s : trContent.getRightAll()) {
					if ( s.contains(pt) ) {
						rr = s.getBounds2D();
						double dd = pt.distance(rr.getCenterX(), rr.getCenterY());
						if ( (null == dist) || (dd < dist) ) {
							shpSel = s;
							dist = dd;
						}
					}
				}
				shpSelDrawn = null;
			}

			drawingPane.revalidate();
			drawingPane.repaint();
		}
	};

	ComponentListener cl = new ComponentAdapter() {
		public void componentResized(ComponentEvent e) {
			updateAT();
		}
	};

	public ArkDockSwingGeoPanel(ArkDockModel modMain_) {
		super(new BorderLayout());

		this.modMain = modMain_;

		ArkDockModelMeta modMeta = modMain.getMeta();
		dslNative = new ArkDockDsl.DslNative(modMeta);

		JLabel instructionsLeft = new JLabel("Click left mouse button to select.");
		JLabel instructionsRight = new JLabel("Click right mouse button to reload.");
		JPanel instructionPanel = new JPanel(new GridLayout(0, 1));
		instructionPanel.setFocusable(true);
		instructionPanel.add(instructionsLeft);
		instructionPanel.add(instructionsRight);

		drawingPane = new DrawingPane();
		drawingPane.setBackground(Color.white);
		drawingPane.addMouseListener(ml);
		drawingPane.addComponentListener(cl);

		JScrollPane scroller = new JScrollPane(drawingPane);
		scroller.setPreferredSize(new Dimension(800, 600));

		add(instructionPanel, BorderLayout.PAGE_START);
		add(scroller, BorderLayout.CENTER);

		reload();
	}

	void reload() {
		trContent.clear();
		bbox = null;
		shpSel = null;

		SerializeAgent<DustEntityContext> target = new ArkDockModelSerializer.Dump() {
			@SuppressWarnings("unchecked")
			@Override
			public DustResultType agentAction(DustAgentAction action) throws Exception {
				if ( action == DustAgentAction.PROCESS ) {
					DustEntityContext ctx = getActionCtx();

					if ( (ctx.member == dslNative.memNativeValueOne) || (ctx.member == dslNative.memNativeValueArr) ) {
						Shape shp = null;

						if ( ctx.value instanceof GeojsonPolygon ) {
							shp = ((GeojsonPolygon<Path2D.Double>) ctx.value).getExterior();
						} else if ( ctx.value instanceof Shape ) {
							shp = (Shape) ctx.value;
						} else {
							DustGenLog.log(DustEventLevel.WARNING, "no known geometry passed", ctx.entity, ctx.value);
							return DustResultType.ACCEPT_READ;
						}
						trContent.add(ctx.entity, shp);

						Rectangle2D bb = shp.getBounds2D();
						if ( null == bbox ) {
							bbox = new Rectangle2D.Double();
							bbox.setRect(bb);
						} else {
							bbox.add(bb);
						}

						shpSel = shp;
					}
				}

				return DustResultType.ACCEPT_READ;
			}
		};

		try {
			ArkDockModelSerializer.modelToAgent(modMain, target, null);
			updateAT();
			DustGenLog.log(DustEventLevel.INFO, bbox);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void updateAT() {
		if ( trContent.isEmpty() ) {
			return;
		}
		
		int m = 20;
		int m2 = 2 * m;
		Dimension d = getSize();

		double dw = (double) (d.width - m2) / bbox.getWidth();
		double dh = (double) (d.height - m2) / bbox.getHeight();

		double dd = dw > dh ? dh : dw;

		double dm = (double) m / dd;

		AffineTransform atScale = AffineTransform.getScaleInstance(dd, -dd);

		double tx = bbox.getMinX();
		double ty = bbox.getMaxY();
		AffineTransform atShift = AffineTransform.getTranslateInstance(dm - tx, -dm - ty);

		at.setToIdentity();
		at.concatenate(atScale);
		at.concatenate(atShift);

		atInv = null;

		float w = 0.00003f;

		stroke = new BasicStroke(w, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f,
				new float[] { 10 * w, 10 * w }, 0.0f);

	}

	/** The component inside the scroll pane. */
	public class DrawingPane extends JPanel {
		private static final long serialVersionUID = 1L;

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			AffineTransform atOrig = g2d.getTransform();

			g2d.transform(at);

			double[] dp = new double[6];

			g2d.setColor(cArea);

			for (Shape s : trContent.getRightAll()) {
				DustEntity e = trContent.getLeft(s);

				if ( !e.toString().contains("Door") ) {
					g2d.fill(s);
				}
			}

			g2d.setTransform(atOrig);

			Rectangle2D rr;
			Point2D pt;

			for (Shape s : trContent.getRightAll()) {
				rr = s.getBounds2D();
				pt = new Point2D.Double(rr.getCenterX(), rr.getCenterY());
				at.transform(pt, pt);

				DustEntity e = trContent.getLeft(s);

				if ( e.toString().contains("Door") ) {
					g2d.setColor(Color.darkGray);
					PathIterator pi = s.getPathIterator(at);
					pi.currentSegment(dp);
					dp[2] = dp[0];
					dp[3] = dp[1];
					pi.next();
					pi.currentSegment(dp);

					g2d.drawLine((int) dp[0], (int) dp[1], (int) dp[2], (int) dp[3]);
				} else {
					g.setColor(Color.red);
				}
				g2d.drawString(modMain.getId(e), (int) pt.getX(), (int) pt.getY());
			}

			if ( null != shpSel ) {
				int r = 2;

				int count = 0;

				g.setColor(Color.blue);

				if ( null == shpSelDrawn ) {
					DustEntity eSel = trContent.getLeft(shpSel);
					DustGenLog.log(DustEventLevel.INFO, "Selected", modMain.getId(eSel), eSel);
				}

				for (PathIterator pi = shpSel.getPathIterator(null); !pi.isDone(); pi.next()) {
					++count;

					pi.currentSegment(dp);

					if ( null == shpSelDrawn ) {
						DustGenLog.log(DustEventLevel.INFO, "Point", count, "[", dp[0], ",", dp[1], "]");
					}

					at.transform(dp, 0, dp, 2, 1);

					int x = (int) dp[2];
					int y = (int) dp[3];

					g2d.drawOval(x - r, y - r, 2 * r, 2 * r);
					g2d.drawString(String.valueOf(count), x, y);
				}

				shpSelDrawn = shpSel;
			}
		}
	}

	public static class Agent extends SwingAgent<ArkDockSwingGeoPanel> {
		@Override
		protected ArkDockSwingGeoPanel createComponent() {
			return new ArkDockSwingGeoPanel(getActionCtx().mind.modMain);
		}
	}
	
}