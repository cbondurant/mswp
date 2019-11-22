package edu.berea.walkerje.mswp.edit.gui;

import edu.berea.walkerje.mswp.edit.gui.modal.SpriteAddDialog;
import edu.berea.walkerje.mswp.edit.gui.modal.SpriteAnimationAddDialog;
import edu.berea.walkerje.mswp.gfx.Sprite;
import edu.berea.walkerje.mswp.gfx.SpriteAnimation;
import edu.berea.walkerje.mswp.gfx.Spritesheet;
import net.miginfocom.swing.MigLayout;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JSlider;

public class SpritesheetAssetView extends JPanel {
	private static final long serialVersionUID = 6700636086784321360L;

	private Timer spriteUpdater;
	
	private JPanel contentListPane = new JPanel();
	
	/**
	 * Create the panel.
	 */
	public SpritesheetAssetView(Spritesheet sheet) {
		setLayout(new BorderLayout(0, 0));
		
		JPanel topPanel = new JPanel();
		add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel controlPanel = new JPanel();
		topPanel.add(controlPanel, BorderLayout.NORTH);
		controlPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel controlButtonPanel = new JPanel();
		controlPanel.add(controlButtonPanel, BorderLayout.NORTH);
		
		JButton btnAddAnimation = new JButton("Add Animation");
		controlButtonPanel.add(btnAddAnimation);
		
		JButton btnAddSprite = new JButton("Add Sprite");
		controlButtonPanel.add(btnAddSprite);
		
		JPanel zoomPanel = new JPanel();
		controlPanel.add(zoomPanel, BorderLayout.SOUTH);
		zoomPanel.setLayout(new BorderLayout(0, 0));
		
		JSlider slider = new JSlider();
		slider.setValue(75);
		slider.setMinimum(50);
		slider.setMaximum(500);
		zoomPanel.add(slider, BorderLayout.CENTER);
		slider.addChangeListener((e)->{
			for(Component comp : contentListPane.getComponents()) {
				if(comp instanceof SpriteView) {
					SpriteView view = (SpriteView)comp;
					view.setScale((float)(slider.getValue() / 100.0f));
					view.revalidate();
					view.repaint();
				}
			}
		});
		
		JLabel lblZoom = new JLabel("Zoom");
		lblZoom.setHorizontalAlignment(SwingConstants.CENTER);
		
		zoomPanel.add(lblZoom, BorderLayout.NORTH);
		
		JPanel infoPanel = new JPanel();
		topPanel.add(infoPanel, BorderLayout.CENTER);
		infoPanel.setLayout(new MigLayout("", "[][grow][][grow]", "[]"));
		
		JLabel lblResolution = new JLabel("Resolution:");
		infoPanel.add(lblResolution, "cell 0 0");
		
		JLabel lblResolutionVal = new JLabel(String.format("%d x %d", sheet.getExtentWidth(), sheet.getExtentHeight()));
		infoPanel.add(lblResolutionVal, "cell 1 0");
		
		JLabel lblSprites = new JLabel("Sprites:");
		infoPanel.add(lblSprites, "flowx,cell 2 0");
		
		JLabel lblSpritesVal = new JLabel(String.format("%d", sheet.getSprites().size()));
		infoPanel.add(lblSpritesVal, "cell 3 0");
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		contentListPane.setLayout(new WrapLayout(FlowLayout.LEFT));
		scrollPane.setViewportView(contentListPane);
		
		MouseListener viewListener = new MouseListener() {
			private MouseListener thisInst = this;
			
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getSource() instanceof SpriteView) {
					SpriteView view = (SpriteView)arg0.getSource();
					if(view.getProvider() instanceof SpriteAnimation) {
						SpriteAnimation anim = (SpriteAnimation)view.getProvider();
						if(arg0.getButton() == MouseEvent.BUTTON1) {
							SpriteAnimationAddDialog d = new SpriteAnimationAddDialog(sheet, anim, view.getToolTipText());
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									if(!d.isVisible()) {
										if(d.getResult() != null) {
											sheet.getSprites().remove(view.getToolTipText());
											sheet.addSprite(d.getResultName(), d.getResult());
											rebuildPreview(sheet, (float)(slider.getValue() / 100.0f), thisInst);
										}
									}else SwingUtilities.invokeLater(this);
								}
							});
						}else if(arg0.getButton() == MouseEvent.BUTTON3) {
							final String confirmFormat = String.format("Are you sure you would like to delete the sprite animation named \"%s\"?", view.getToolTipText());
							int confirmation = JOptionPane.showConfirmDialog(null, confirmFormat, "Delete Sprite Animation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
							if(confirmation == JOptionPane.YES_OPTION) {
								sheet.getSprites().remove(view.getToolTipText());
								rebuildPreview(sheet, (float)(slider.getValue() / 100.0f), thisInst);
							}
						}
					}else if(view.getProvider() instanceof Sprite) {
						Sprite sprite = (Sprite)view.getProvider();
						if(arg0.getButton() == MouseEvent.BUTTON1) {
							SpriteAddDialog d = new SpriteAddDialog(sheet, sprite);
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									if(!d.isVisible()) {
										if(d.getResult() != null) {
											sheet.getSprites().remove(view.getToolTipText());
											sheet.addSprite(d.getResultName(), d.getResult());
											rebuildPreview(sheet, (float)(slider.getValue() / 100.0f), thisInst);
										}
									}else SwingUtilities.invokeLater(this);
								}
							});
						}else if(arg0.getButton() == MouseEvent.BUTTON3) {
							final String confirmFormat = String.format("Are you sure you would like to delete the sprite named \"%s\"?", view.getToolTipText());
							int confirmation = JOptionPane.showConfirmDialog(null, confirmFormat, "Delete Sprite", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
							if(confirmation == JOptionPane.YES_OPTION) {
								sheet.getSprites().remove(view.getToolTipText());
								rebuildPreview(sheet, (float)(slider.getValue() / 100.0f), thisInst);
							}
						}
					}
				}
			}
			
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
		};
		
		btnAddAnimation.addActionListener((e)->{
			SpriteAnimationAddDialog d = new SpriteAnimationAddDialog(sheet);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if(!d.isVisible()) {
						if(d.getResult() != null) {
							sheet.addSprite(d.getResultName(), d.getResult());
							rebuildPreview(sheet, (float)(slider.getValue() / 100.0f), viewListener);
						}
					}else SwingUtilities.invokeLater(this);
				}
			});
		});
		
		btnAddSprite.addActionListener((e)->{
			SpriteAddDialog d = new SpriteAddDialog(sheet);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if(!d.isVisible()) {
						if(d.getResult() != null) {
							sheet.addSprite(d.getResultName(), d.getResult());
							rebuildPreview(sheet, (float)(slider.getValue() / 100.0f), viewListener);
						}
					}else SwingUtilities.invokeLater(this);
				}
			});
		});
		
		spriteUpdater = new Timer(10, (e)->{
			for(Component comp : contentListPane.getComponents()) {
				if(comp instanceof SpriteView) {
					((SpriteView)comp).repaint();
				}
			}
		});
		
		rebuildPreview(sheet, 1, viewListener);
		spriteUpdater.setRepeats(true);
		spriteUpdater.start();	
	}
	
	private void rebuildPreview(Spritesheet sheet, float zoom, MouseListener clickListener) {
		contentListPane.removeAll();
		sheet.getSprites().forEach((k,v)->{
			SpriteView view = new SpriteView(v);
			view.addMouseListener(clickListener);
			view.setScale(zoom);
			view.setToolTipText(k);
			contentListPane.add(view);
		});
	}
	
	public void stopUpdatingSprites() {
		spriteUpdater.stop();
	}
}