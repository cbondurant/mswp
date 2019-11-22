package edu.berea.walkerje.mswp.edit.gui.modal;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import edu.berea.walkerje.mswp.IRegion;
import edu.berea.walkerje.mswp.edit.gui.SpriteView;
import edu.berea.walkerje.mswp.gfx.ISpriteProvider;
import edu.berea.walkerje.mswp.gfx.Sprite;
import edu.berea.walkerje.mswp.gfx.Spritesheet;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JTextField;

public class SpriteAddDialog extends JDialog {
	private static final long serialVersionUID = 6914233964898918069L;
	
	private ISpriteProvider result = null;
	private String resultName = "";
	private JTextField txtFieldName;
	
	public SpriteAddDialog(Spritesheet source) {
		this(source, null);
	}
	
	/**
	 * Create the dialog.
	 */
	public SpriteAddDialog(Spritesheet source, ISpriteProvider spr) {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Add Sprite");
		setBounds(100, 100, 450, 300);
		JPanel contentPane = new JPanel();
		
		SpriteView preview = new SpriteView(spr != null ? spr : new Sprite(IRegion.wrap(source), source));
		
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JScrollPane previewScrollPane = new JScrollPane();
		contentPane.add(previewScrollPane, BorderLayout.CENTER);
		
		JPanel previewContentPane = new JPanel();
		previewScrollPane.setViewportView(previewContentPane);
		previewContentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		previewContentPane.add(preview);
		
		JPanel zoomPane = new JPanel();
		contentPane.add(zoomPane, BorderLayout.WEST);
		zoomPane.setLayout(new BorderLayout(0, 0));
		
		JLabel lblZoom = new JLabel("Zoom");
		zoomPane.add(lblZoom, BorderLayout.NORTH);
		
		JSlider slider = new JSlider();
		slider.setValue(100);
		slider.setMinimum(50);
		slider.setMaximum(500);
		slider.addChangeListener((e)->{
			preview.setScale((float)(slider.getValue() / 100.0f));
			preview.repaint();
			preview.revalidate();
		});
		slider.setOrientation(SwingConstants.VERTICAL);
		zoomPane.add(slider, BorderLayout.CENTER);
		
		JPanel optionPane = new JPanel();
		contentPane.add(optionPane, BorderLayout.SOUTH);
		optionPane.setLayout(new MigLayout("", "[][grow][]", "[][]"));
		
		JLabel lblSpriteProperties = new JLabel("");
		optionPane.add(lblSpriteProperties, "cell 1 0,alignx center");
		
		JButton btnSelectSprite = new JButton("Select Sprite");
		btnSelectSprite.addActionListener((e)->{
			SpriteSelectionDialog selectDialog = new SpriteSelectionDialog(source, preview.getProvider().getCurrentSprite());
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if(!selectDialog.isVisible()) {
						if(selectDialog.getResult() != null) {
							preview.setProvider(selectDialog.getResult());
							preview.repaint();
							Sprite sprite = preview.getProvider().getCurrentSprite();
							lblSpriteProperties.setText(String.format("%d, %d | %d x %d", sprite.getPositionX(), sprite.getPositionY(), sprite.getExtentWidth(), sprite.getExtentHeight()));
							lblSpriteProperties.repaint();
						}
					}else SwingUtilities.invokeLater(this);
				}
			});
		});
		optionPane.add(btnSelectSprite, "cell 2 0,alignx right");
		
		JLabel lblName = new JLabel("Name: ");
		optionPane.add(lblName, "cell 0 1,alignx trailing");
		
		txtFieldName = new JTextField();
		optionPane.add(txtFieldName, "cell 1 1,growx");
		txtFieldName.setColumns(10);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener((e)->{
			dispose();
		});
		optionPane.add(btnCancel, "flowx,cell 2 1");
		
		JButton btnConfirm = new JButton("Confirm");
		btnConfirm.addActionListener((e)->{
			if(txtFieldName.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Cannot confirm a sprite with no name!", "No Sprite Name", JOptionPane.WARNING_MESSAGE);
			}else {
				result = preview.getProvider();
				resultName = txtFieldName.getText();
				dispose();
			}
		});
		optionPane.add(btnConfirm, "cell 2 1");
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public ISpriteProvider getResult() {
		return result;
	}
	
	public String getResultName() {
		return resultName;
	}
}
