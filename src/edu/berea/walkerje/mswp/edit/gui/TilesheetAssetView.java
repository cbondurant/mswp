package edu.berea.walkerje.mswp.edit.gui;

import edu.berea.walkerje.mswp.gfx.Tilesheet;

import net.miginfocom.swing.MigLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

public class TilesheetAssetView extends JPanel {
	private static final long serialVersionUID = 1330855847720366196L;

	/**
	 * Create the panel.
	 */
	public TilesheetAssetView(Tilesheet sheet) {
		setLayout(new BorderLayout(0, 0));
		
		JPanel infoPanel = new JPanel();
		add(infoPanel, BorderLayout.NORTH);
		infoPanel.setLayout(new MigLayout("", "[][100.00,fill]", "[][][][]"));
		
		JLabel lblTotalTiles = new JLabel("Total Tiles: ");
		infoPanel.add(lblTotalTiles, "cell 0 0,alignx left");
		
		JLabel lblTotalTilesVal = new JLabel(new Integer(sheet.getTilesX() * sheet.getTilesY()).toString());
		infoPanel.add(lblTotalTilesVal, "cell 1 0");
		
		JLabel lblTileRes = new JLabel("Tile Resolution:");
		infoPanel.add(lblTileRes, "flowy,cell 0 1,alignx left");
		
		JLabel lblTileResVal = new JLabel(String.format("%d x %d", sheet.getTileExtentWidth(), sheet.getTileExtentHeight()));
		infoPanel.add(lblTileResVal, "cell 1 1");
		
		JLabel lblSheetRes = new JLabel("Sheet Resolution:");
		infoPanel.add(lblSheetRes, "cell 0 2,alignx left");
		
		JLabel lblSheetResVal = new JLabel(String.format("%d x %d", sheet.getExtentWidth(), sheet.getExtentHeight()));
		infoPanel.add(lblSheetResVal, "cell 1 2");
		
		JLabel lblViewScale = new JLabel("View Scale: ");
		infoPanel.add(lblViewScale, "cell 0 3");
		
		JSpinner spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(new Float(1), new Float(1), new Float(5), new Float(0.5)));
		
		infoPanel.add(spinner, "cell 1 3,growx");
		
		JPanel tilePanelContents = new JPanel();
		add(tilePanelContents, BorderLayout.CENTER);
		tilePanelContents.setLayout(new WrapLayout(FlowLayout.LEFT, 2, 2));
		
		for(int y = 0; y < sheet.getTilesY(); y++) {
			for(int x = 0; x < sheet.getTilesX(); x++) {
				SpriteView view = new SpriteView(sheet.get(x, y).getSpriteProvider());
				final int id = (y * sheet.getTilesX()) + x;
				view.setToolTipText(String.format("Coordinate: x%d y%d ID: %d", x, y, id));
				tilePanelContents.add(view);
			}
		}
		
		spinner.addChangeListener(e->{
			for(Component c : tilePanelContents.getComponents()) {
				if(c instanceof SpriteView) {
					SpriteView view = (SpriteView)c;
					view.setScale((float)spinner.getValue());
				}
			}
			tilePanelContents.revalidate();
			tilePanelContents.repaint();
		});
	}
}