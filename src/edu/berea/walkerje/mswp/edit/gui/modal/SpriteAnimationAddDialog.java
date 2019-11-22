package edu.berea.walkerje.mswp.edit.gui.modal;

import edu.berea.walkerje.mswp.edit.gui.SpriteView;
import edu.berea.walkerje.mswp.gfx.SpriteAnimation;
import edu.berea.walkerje.mswp.gfx.Spritesheet;

import net.miginfocom.swing.MigLayout;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class SpriteAnimationAddDialog extends JDialog {
	private static final long serialVersionUID = -6381413503901253440L;

	private int curFrameIndex = 0;
	
	private SpriteView spriteView;
	
	private JPanel previewPanel;
	
	private JSlider frameSlider;
	
	private JPanel previewContentPane;
	
	private JSpinner spinner;
	
	private SpriteAnimation result;
	private String resultName;
	private JTextField txtAnimationName;
	
	/**
	 * @wbp.parser.constructor
	 */
	public SpriteAnimationAddDialog(Spritesheet sheet) {
		this(sheet, null, "");
	}
	
	/**
	 * Create the dialog.
	 */
	public SpriteAnimationAddDialog(Spritesheet sheet, SpriteAnimation prevAnim, String name) {
		SpriteAnimation animation = prevAnim == null ? new SpriteAnimation() : prevAnim;
		spriteView = new SpriteView(animation);
		
		setTitle("Modify Sprite Animation");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 550, 400);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel controlPanel = new JPanel();
		getContentPane().add(controlPanel, BorderLayout.SOUTH);
		controlPanel.setLayout(new BorderLayout(0, 0));

		JPanel scrubberPanel = new JPanel();
		controlPanel.add(scrubberPanel, BorderLayout.NORTH);
		scrubberPanel.setLayout(new BorderLayout(0, 0));
									//min, max, value
		frameSlider = new JSlider();
		frameSlider.setPaintTicks(true);
		frameSlider.setPaintLabels(true);
		scrubberPanel.add(frameSlider, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		controlPanel.add(buttonPanel, BorderLayout.CENTER);
		buttonPanel.setLayout(new MigLayout("", "[][][][grow]", "[][]"));
		
		JLabel lblCurrentFrame_1 = new JLabel("Current Frame");
		buttonPanel.add(lblCurrentFrame_1, "flowy,cell 0 0");
		
		JButton btnSetSprite = new JButton("Set Sprite");
		buttonPanel.add(btnSetSprite, "cell 1 0,growx");
		
		JButton btnAddFrame = new JButton("  Add Frame  ");
		buttonPanel.add(btnAddFrame, "cell 3 0,alignx right");
		
		JLabel lblLengthms = new JLabel("Length (ms): ");
		buttonPanel.add(lblLengthms, "cell 0 1");
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(200, 1, 100000, 250));
		buttonPanel.add(spinner, "cell 1 1");
		
		JButton btnDeleteFrame = new JButton("Delete Frame");
		buttonPanel.add(btnDeleteFrame, "cell 3 1,alignx right");
		
		JPanel finishButtonPanel = new JPanel();
		controlPanel.add(finishButtonPanel, BorderLayout.SOUTH);
		finishButtonPanel.setLayout(new MigLayout("", "[][grow][grow][]", "[]"));
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener((e)->{
			dispose();
		});
		
		JLabel lblName = new JLabel("Name: ");
		finishButtonPanel.add(lblName, "cell 0 0,alignx trailing");
		
		txtAnimationName = new JTextField();
		finishButtonPanel.add(txtAnimationName, "cell 1 0,growx");
		txtAnimationName.setColumns(10);
		finishButtonPanel.add(btnCancel, "cell 2 0,alignx right");
		txtAnimationName.setText(name);
		
		JButton btnConfirm = new JButton("Confirm");
		finishButtonPanel.add(btnConfirm, "cell 3 0");
		
		previewPanel = new JPanel();
		
		getContentPane().add(previewPanel, BorderLayout.CENTER);
		previewPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel zoomPanel = new JPanel();
		zoomPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		previewPanel.add(zoomPanel, BorderLayout.WEST);
		zoomPanel.setLayout(new BorderLayout(0, 0));
		
		JSlider slider = new JSlider();
		slider.setValue(100);
		slider.setMinimum(50);
		slider.setMaximum(500);
		slider.setOrientation(SwingConstants.VERTICAL);
		slider.addChangeListener((e)->{
			spriteView.setScale(slider.getValue() / 100.0f);
			SwingUtilities.invokeLater(()->{
				rebuildPreview(animation);
			});
		});
		zoomPanel.add(slider, BorderLayout.CENTER);
		
		JLabel lblZoom = new JLabel("Zoom");
		zoomPanel.add(lblZoom, BorderLayout.NORTH);
		
		previewContentPane = new JPanel();
		previewPanel.add(previewContentPane, BorderLayout.CENTER);
		previewContentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel infoPanel = new JPanel();
		getContentPane().add(infoPanel, BorderLayout.NORTH);
		infoPanel.setLayout(new MigLayout("", "[55px][grow][][grow][][grow]", "[15px]"));
		
		JLabel lblFrames = new JLabel("Frames:");
		infoPanel.add(lblFrames, "cell 0 0,alignx left,aligny center");
		
		JLabel lblFramesVal = new JLabel(String.format("%d", animation.getTotalSprites()));
		infoPanel.add(lblFramesVal, "cell 1 0,alignx left");
		
		JLabel lblDuration = new JLabel("Duration (ms): ");
		infoPanel.add(lblDuration, "cell 2 0");
		
		JLabel lblDurationVal = new JLabel(String.format("%d", animation.getAnimationLength()));
		infoPanel.add(lblDurationVal, "cell 3 0,alignx left");
		
		JLabel lblCurrentFrame = new JLabel("Current: ");
		infoPanel.add(lblCurrentFrame, "cell 4 0");
		
		JLabel lblCurrentFrameVal = new JLabel("0");//Default to frame index 0.
		infoPanel.add(lblCurrentFrameVal, "cell 5 0,alignx left");
		
		btnSetSprite.addActionListener((e)->{
			if(animation.getFrames().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Cannot set the sprite of a frame when there is no frame!", "Cannot Set Sprite", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			SpriteSelectionDialog d = new SpriteSelectionDialog(sheet, animation.getFrames().get(curFrameIndex).sprite.getCurrentSprite());
			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if(!d.isVisible()) {
						if(d.getResult() != null) {
							animation.getFrames().get(curFrameIndex).sprite = d.getResult();
							rebuildPreview(animation);
						}
					}else SwingUtilities.invokeLater(this);
				}
			});
		});
		
		btnAddFrame.addActionListener((e)->{
			SpriteSelectionDialog d = new SpriteSelectionDialog(sheet);
			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if(!d.isVisible()) {
						if(d.getResult() != null) {
							animation.addFrame(SpriteAnimation.createFrame(d.getResult(), 200));
							rebuildPreview(animation);
							lblFramesVal.setText(String.format("%d", animation.getTotalSprites()));
							lblDurationVal.setText(String.format("%d", animation.getAnimationLength()));
						}
					}else SwingUtilities.invokeLater(this);
				}
			});
		});
		
		btnDeleteFrame.addActionListener((e)->{
			if(animation.getFrames().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Cannot delete a frame from an empty animation.", "Cannot Delete Animation Frame", JOptionPane.WARNING_MESSAGE);
			}else {
				animation.getFrames().remove(curFrameIndex);
				curFrameIndex--;
				if(curFrameIndex < 0)
					curFrameIndex = 0;
				rebuildPreview(animation);
				lblFramesVal.setText(String.format("%d", animation.getTotalSprites()));
			}
		});
		
		btnConfirm.addActionListener((e)->{
			if(txtAnimationName.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Cannot confirm an animation with no name!", "Cannot Confirm Sprite Animation", JOptionPane.WARNING_MESSAGE);
			}else if(animation.getFrames().isEmpty()){
				JOptionPane.showMessageDialog(this, "Cannot confirm an animation with no frames!", "Cannot Confirm Sprite Animation", JOptionPane.WARNING_MESSAGE);
			}else {
				result = animation;
				resultName = txtAnimationName.getText();
				dispose();
			}
		});
		
		frameSlider.addChangeListener((e)->{
			curFrameIndex = (int)frameSlider.getValue();
			lblCurrentFrameVal.setText(String.format("%d", curFrameIndex));
			SwingUtilities.invokeLater(()->{
				rebuildPreview(animation);
			});
		});
		
		spinner.addChangeListener((e)->{
			if(!animation.getFrames().isEmpty()) {
				animation.getFrames().get(curFrameIndex).duration = (Integer)spinner.getValue();
				lblDurationVal.setText(String.format("%d", animation.getAnimationLength()));
			}
		});
		
		rebuildPreview(animation);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public SpriteAnimation getResult() {
		return result;
	}
	
	public String getResultName() {
		return resultName;
	}
	
	private void rebuildSlider(SpriteAnimation anim) {
		if(!anim.getFrames().isEmpty())
			spinner.setValue((int)anim.getFrames().get(curFrameIndex).duration);
		final int oneMinusTotal = anim.getTotalSprites() - 1;
		frameSlider.setMaximum(oneMinusTotal > -1 ? oneMinusTotal : 0);
		frameSlider.setMinimum(0);
		frameSlider.setValue(curFrameIndex);
		frameSlider.revalidate();
		frameSlider.repaint();
	}
	
	private void rebuildPreview(SpriteAnimation anim) {
		previewContentPane.removeAll();
		if(curFrameIndex > anim.getTotalSprites()) {
			curFrameIndex = anim.getTotalSprites() - 1;
		}
		
		if(curFrameIndex < 0)
			curFrameIndex = 0;
		
		if(!anim.getFrames().isEmpty() && curFrameIndex > -1) {
			previewContentPane.add(spriteView);
			spriteView.setProvider(anim.getFrames().get(curFrameIndex).sprite);
			spriteView.repaint();
			spriteView.revalidate();
		}
		rebuildSlider(anim);
		previewContentPane.repaint();
	}
}
