package edu.berea.walkerje.mswp.edit.gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JSlider;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import edu.berea.walkerje.mswp.EAssetType;
import edu.berea.walkerje.mswp.edit.Editor;
import edu.berea.walkerje.mswp.edit.Project;
import edu.berea.walkerje.mswp.edit.TileStamp;

@SuppressWarnings("serial")
public class TileStampAssetView extends JPanel {
	private JPanel previewPane;
	private JPanel infoPanel;
	private JPanel scalePanel;
	private JSlider scaleSlider;
	private JLabel lblSize;
	private JLabel lblSizeVal;

	private TileStampView preview;
	private Editor editor;
	
	/**
	 * Create the panel.
	 */
	public TileStampAssetView(Editor e, TileStamp stamp) {
		this.editor = e;
		initComponents(stamp);
	}
	
	/**
	 * Initializes this stamp view.
	 * @param stamp
	 */
	private void initComponents(TileStamp stamp) {
		preview = new TileStampView(stamp);
		preview.addMouseListener(new MouseAdapter() {
			/**
			 * Handles tool performance and rect selection status.
			 */
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == 3) {//right-click
					final String msg = String.format("Are you sure you want to delete the tile stamp called \"%s\"?", stamp.getAssetName());
					final int status = JOptionPane.showConfirmDialog(editor, msg, "Remove Stamp", JOptionPane.YES_NO_OPTION);
					if(status == JOptionPane.YES_OPTION) {
						Project.getCurrentProject().getAssets()[EAssetType.TILE_STAMP.ordinal()].remove(stamp.getAssetName());
						editor.rebuildOutline();
						editor.getOutline().swapViewPane(null);
						editor.getToolDialog().rebuildToolPanels();
					}
				}
			}
		});
		setLayout(new BorderLayout(0, 0));
		
		previewPane = new JPanel();
		previewPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		previewPane.add(preview, BorderLayout.CENTER);
		add(previewPane, BorderLayout.CENTER);
		
		infoPanel = new JPanel();
		add(infoPanel, BorderLayout.NORTH);
		infoPanel.setLayout(new MigLayout("", "[][]", "[]"));
		
		lblSize = new JLabel("Size: ");
		infoPanel.add(lblSize, "cell 0 0");
		
		lblSizeVal = new JLabel(String.format("%d x %d", stamp.getExtentWidth(), stamp.getExtentHeight()));
		infoPanel.add(lblSizeVal, "cell 1 0");
		
		scalePanel = new JPanel();
		add(scalePanel, BorderLayout.SOUTH);
		
		scaleSlider = new JSlider();
		scaleSlider.setValue(100);
		scaleSlider.setMinimum(50);
		scaleSlider.setMaximum(500);
		scaleSlider.addChangeListener((e)->{
			preview.setScale((float)(scaleSlider.getValue() / 100.0f));
			preview.revalidate();
			preview.repaint();
		});
		
		scalePanel.add(scaleSlider);
	}

}
