package edu.berea.walkerje.mswp.edit.gui;

import javax.swing.JPanel;

import edu.berea.walkerje.mswp.edit.Editor;
import edu.berea.walkerje.mswp.gfx.GameCanvas;
import edu.berea.walkerje.mswp.play.GameLevel;
import net.miginfocom.swing.MigLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class GameLevelAssetView extends JPanel {
	private static final long serialVersionUID = 4956186828279630531L;
	
	private JPanel buttonPanel;
	private JButton btnEdit;
	private JPanel infoPanel;
	private JLabel lblTilesX;
	private JLabel lblTilesY;
	private JLabel lblTilesXVal;
	private JLabel lblTilesYVal;
	
	private Editor editor;
	private GameLevel level;
	private JButton btnExportPng;

	/**
	 * Create the panel.
	 */
	public GameLevelAssetView(Editor editor, GameLevel level) {
		this.editor = editor;
		this.level = level;
		initComponents();
	}
	
	/**
	 * Initializes the components of this view.
	 */
	private void initComponents() {
		setLayout(new BorderLayout(0, 0));
		
		buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.NORTH);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnEdit = new JButton("Edit");
		btnEdit.addActionListener((e)->{
			editor.openLevelEditor(level);
		});
		buttonPanel.add(btnEdit);
		
		btnExportPng = new JButton("Export PNG");
		btnExportPng.addActionListener((e)->{
			
			JFileChooser chooser = new JFileChooser();
			final int status = chooser.showSaveDialog(this);
			
			if(status == JFileChooser.APPROVE_OPTION){
				final int imgWidth = level.getTilesX() * level.getTilesDrawWidth();
				final int imgHeight = level.getTilesY() * level.getTilesDrawHeight();
				
				BufferedImage img = GameCanvas.createBufferedImage(imgWidth, imgHeight, true);
				level.paint(img.getGraphics());
				try {
					ImageIO.write(img, "png", chooser.getSelectedFile());
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(this, "Could not export level as a PNG!", "Failed to Export", JOptionPane.WARNING_MESSAGE);
					e1.printStackTrace();
				}
			}
		});
		buttonPanel.add(btnExportPng);
		
		infoPanel = new JPanel();
		add(infoPanel, BorderLayout.CENTER);
		infoPanel.setLayout(new MigLayout("", "[][]", "[][]"));
		
		lblTilesX = new JLabel("Tiles X:");
		infoPanel.add(lblTilesX, "cell 0 0");
		
		lblTilesXVal = new JLabel(String.format("%d", level.getTilesX()));
		infoPanel.add(lblTilesXVal, "cell 1 0");
		
		lblTilesY = new JLabel("Tiles Y:");
		infoPanel.add(lblTilesY, "cell 0 1");
		
		lblTilesYVal = new JLabel(String.format("%d", level.getTilesY()));
		infoPanel.add(lblTilesYVal, "cell 1 1");
	}
}