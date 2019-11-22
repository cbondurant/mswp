package edu.berea.walkerje.mswp.edit.gui.modal;

import edu.berea.walkerje.mswp.IRegion;
import edu.berea.walkerje.mswp.MSWPApp;
import edu.berea.walkerje.mswp.gfx.GameCanvas;
import edu.berea.walkerje.mswp.gfx.IImageProvider;
import edu.berea.walkerje.mswp.gfx.Sprite;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.ScrollPaneConstants;
import net.miginfocom.swing.MigLayout;

public class SpriteSelectionDialog extends JDialog {
	private static final long serialVersionUID = -5802179788968496176L;
	
	private final JPanel contentPanel = new JPanel();
	
	private class SelectionCanvas extends JPanel implements MouseMotionListener, MouseInputListener{
		private static final long serialVersionUID = 8437466569186763201L;

		IImageProvider imgProvider;
		
		float zoom = 1.0f;

		boolean selecting = false;
		
		BufferedImage tileBg;
		
		Point selectionBegin;
		Point selectionEnd;
		
		Point mouseLoc = new Point();
		
		Rectangle selection = null;
		Runnable onSelectingListener = ()->{};
		Runnable onFinalizeListener = ()->{};
		Runnable onMouseMoveListener = ()->{};
		
		public SelectionCanvas() {
			addMouseMotionListener(this);
			addMouseListener(this);
			
			try {
				tileBg = ImageIO.read(MSWPApp.readAsset("tilebg.png"));
			}catch(Exception e) {}
		}
		
		@Override
		public void mousePressed(MouseEvent arg0) {
			selecting = true;
			selectionBegin = arg0.getPoint();
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			if(selectionEnd == null) {
				selecting = false;
				return;
			}
			selectionBegin.x /= zoom;
			selectionBegin.y /= zoom;
			selectionEnd.x /= zoom;
			selectionEnd.y /= zoom;
			
			final int upperX = selectionBegin.x > selectionEnd.x ? selectionBegin.x : selectionEnd.x;
			final int upperY = selectionBegin.y > selectionEnd.y ? selectionBegin.y : selectionEnd.y;
			final int lowerX = selectionBegin.x < selectionEnd.x ? selectionBegin.x : selectionEnd.x;
			final int lowerY = selectionBegin.y < selectionEnd.y ? selectionBegin.y : selectionEnd.y;
			selectionBegin = selectionEnd = null;
			selection = new Rectangle(lowerX, lowerY, upperX - lowerX, upperY - lowerY);
			selecting = false;
			onFinalizeListener.run();
		}
		
		@Override
		public void mouseDragged(MouseEvent arg0) {
			selectionEnd = arg0.getPoint();
			repaint();
			onSelectingListener.run();
		}
		
		public Rectangle getSelection() {
			return selection;
		}
		
		public void setZoom(float zoom) {
			this.zoom = zoom;
			revalidate();
			repaint();
		}
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Dimension sz = getPreferredSize();
			Rectangle selection = getSelection();
			BufferedImage temp = GameCanvas.createBufferedImage(imgProvider.getExtentWidth(), imgProvider.getExtentHeight(), true);
			Graphics2D tempG = temp.createGraphics();
			
			//Draw tile background.
			int bgWidth = (int)(tileBg.getWidth());
			int bgHeight = (int)(tileBg.getHeight());
			int bgX = (int)(imgProvider.getExtentWidth() / bgWidth) + 1;
			int bgY = (int)(imgProvider.getExtentHeight() / bgHeight) + 1;
			
			for(int y = 0; y < bgY; y++) {
				for(int x = 0; x < bgX; x++) {
					int xPos = tileBg.getWidth() * x;
					int yPos = tileBg.getHeight() * y;
					tempG.drawImage(tileBg, xPos, yPos, null);
				}
			}
			
			tempG.drawImage(imgProvider.getImage(), 0, 0, imgProvider.getExtentWidth(), imgProvider.getExtentHeight(), null);
			
			tempG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
			tempG.setColor(Color.RED);
			if(selecting && selectionEnd != null && selectionEnd != null) {
				int beginX = (int) (selectionBegin.x / zoom);
				int beginY = (int) (selectionBegin.y / zoom);
				int endX = (int) (selectionEnd.x / zoom);
				int endY = (int) (selectionEnd.y / zoom);
				final int upperX = beginX > endX ? beginX : endX;
				final int upperY = beginY > endY ? beginY : endY;
				final int lowerX = beginX < endX ? beginX : endX;
				final int lowerY = beginY < endY ? beginY : endY;

				
				tempG.drawRect(lowerX, lowerY, upperX - lowerX - 1, upperY - lowerY - 1);
			}else if(selection != null) {
				tempG.drawRect(selection.x, selection.y, selection.width - 1, selection.height - 1);
			}
			tempG.dispose();
			g.drawImage(temp, 0, 0, sz.width, sz.height, null);
		}
		
		public Dimension getPreferredSize() {
			return new Dimension((int)(imgProvider.getExtentWidth() * zoom), (int)(imgProvider.getExtentHeight() * zoom));
		}
		
		public Dimension getSize() {
			return getPreferredSize();
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {}

		@Override
		public void mouseExited(MouseEvent arg0) {}
		
		@Override
		public void mouseMoved(MouseEvent arg0) {
			mouseLoc = arg0.getPoint();
			onMouseMoveListener.run();
		}
		
		@Override
		public void mouseClicked(MouseEvent arg0) {}
	}
	
	private SelectionCanvas displayCanvas;
	
	private Sprite result = null;
	
	public SpriteSelectionDialog(IImageProvider imageProvider) {
		this(imageProvider, null);
	}
	
	/**
	 * Create the dialog.
	 */
	public SpriteSelectionDialog(IImageProvider imageProvider, IRegion reg) {
		displayCanvas = new SelectionCanvas();
		displayCanvas.selection = reg != null ? new Rectangle(reg.getPositionX(), reg.getPositionY(), reg.getExtentWidth(), reg.getExtentWidth()) : null;
		displayCanvas.imgProvider = imageProvider;
		JScrollPane displayPane = new JScrollPane();
		
		setModalityType(ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 600, 500);
		setTitle("Select Sprite");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel zoomPanel = new JPanel();
			zoomPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			contentPanel.add(zoomPanel, BorderLayout.WEST);
			zoomPanel.setLayout(new BorderLayout(0, 0));
			
			JLabel lblZoom = new JLabel("Zoom");
			zoomPanel.add(lblZoom, BorderLayout.NORTH);
			
			JLabel lblZoomVal = new JLabel("100%");
			zoomPanel.add(lblZoomVal, BorderLayout.SOUTH);
			
			{
				UIManager.put("Slider.paintValue", false);
				JSlider sliderZoom = new JSlider(50, 500, 100);
				sliderZoom.addChangeListener((e)->{
					float zoomVal = (float)sliderZoom.getValue() / 100.0f;
					
					displayCanvas.setZoom(zoomVal);
					displayPane.revalidate();
					lblZoom.setText(String.format("%d%%", (int)sliderZoom.getValue()));
					lblZoom.repaint();
				});
				sliderZoom.setOrientation(SwingConstants.VERTICAL);
				zoomPanel.add(sliderZoom, BorderLayout.CENTER);
			}
		}
		{
			AdjustmentListener scrollListener = (e)->{
				displayPane.repaint();
				displayPane.revalidate();
				displayCanvas.revalidate();
				displayCanvas.repaint();
			};
			

			JPanel displayCanvasPane = new JPanel();
			displayCanvasPane.setLayout(new BorderLayout(0, 0));
			displayCanvasPane.add(displayCanvas, BorderLayout.CENTER);
			
			displayPane.getHorizontalScrollBar().addAdjustmentListener(scrollListener);
			displayPane.getVerticalScrollBar().addAdjustmentListener(scrollListener);
			displayPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			displayPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			displayPane.setViewportBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			
			contentPanel.add(displayPane, BorderLayout.CENTER);
			{
				displayPane.setViewportView(displayCanvasPane);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnCancel = new JButton("Cancel");
				btnCancel.addActionListener((e)->{
					dispose();
				});
				buttonPane.add(btnCancel);
			}
			{
				JButton btnConfirm = new JButton("Confirm");
				btnConfirm.addActionListener((e)->{
					if(displayCanvas.selection == null) {
						JOptionPane.showMessageDialog(this, "Cannot confirm a sprite with no selection!", "Make a Selection", JOptionPane.WARNING_MESSAGE);
					}else {
						result = new Sprite(displayCanvas.selection, imageProvider);
						dispose();
					}
				});
				buttonPane.add(btnConfirm);
			}
		}
		{
			JPanel infoPanel = new JPanel();
			getContentPane().add(infoPanel, BorderLayout.NORTH);
			infoPanel.setLayout(new MigLayout("", "[8px,grow][grow]", "[15px]"));
			
			JLabel lblMousePos = new JLabel("0,0");
			infoPanel.add(lblMousePos, "cell 0 0,alignx left,aligny top");
			JLabel lblSelection = new JLabel("0,0 | 0x0");
			infoPanel.add(lblSelection, "cell 1 0,alignx right");
				
			displayCanvas.onMouseMoveListener = ()->{
				lblMousePos.setText(String.format("%d, %d", (int)(displayCanvas.mouseLoc.x / displayCanvas.zoom), (int)(displayCanvas.mouseLoc.y / displayCanvas.zoom)));
				lblMousePos.repaint();
			};
			
			displayCanvas.onSelectingListener = ()->{
				int beginX = (int) (displayCanvas.selectionBegin.x / displayCanvas.zoom);
				int beginY = (int) (displayCanvas.selectionBegin.y / displayCanvas.zoom);
				int endX = (int) (displayCanvas.selectionEnd.x / displayCanvas.zoom);
				int endY = (int) (displayCanvas.selectionEnd.y / displayCanvas.zoom);
				final int upperX = beginX > endX ? beginX : endX;
				final int upperY = beginY > endY ? beginY : endY;
				final int lowerX = beginX < endX ? beginX : endX;
				final int lowerY = beginY < endY ? beginY : endY;
				
				final int width = upperX - lowerX;
				final int height = upperY - lowerY;
				
				lblSelection.setText(String.format("%d, %d | %d x %d", lowerX, lowerY, width, height));
				lblSelection.repaint();
			};
			
			displayCanvas.onFinalizeListener = ()->{
				Rectangle sel = displayCanvas.selection;
				lblSelection.setText(String.format("%d, %d | %d x %d", sel.x, sel.y, sel.width, sel.height));
			};
		}
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public Sprite getResult() {
		return result;
	}
}
