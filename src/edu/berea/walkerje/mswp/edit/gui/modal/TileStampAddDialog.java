package edu.berea.walkerje.mswp.edit.gui.modal;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.JScrollPane;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import edu.berea.walkerje.mswp.edit.TileStamp;
import edu.berea.walkerje.mswp.edit.gui.TileStampView;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JSlider;

@SuppressWarnings("serial")
public class TileStampAddDialog extends JDialog {
	private JScrollPane previewScrollPane;
	private JPanel previewPane;
	private JPanel namePanel;
	private JPanel previewContentPanel;
	private JPanel scalePanel;
	private JLabel lblName;
	private JTextField txtName;
	private JPanel buttonPanel;
	private JButton btnConfirm;
	private JButton btnCancel;
	private JSlider slider;

	private TileStampView preview;
	
	private TileStamp result;
	
	/**
	 * Create the panel.
	 */
	public TileStampAddDialog(Container c, TileStamp stamp) {
		setTitle("Create Tile Stamp");
		setModalityType(ModalityType.APPLICATION_MODAL);
		initComponents(stamp);
		setLocationRelativeTo(c);
	}
	
	private void initComponents(TileStamp stamp) {
		preview = new TileStampView(stamp);
		setLayout(new BorderLayout(0, 0));
		
		namePanel = new JPanel();
		add(namePanel, BorderLayout.NORTH);
		namePanel.setLayout(new MigLayout("", "[][grow]", "[]"));
		
		lblName = new JLabel("Name:");
		namePanel.add(lblName, "cell 0 0,alignx trailing");
		
		txtName = new JTextField();
		namePanel.add(txtName, "cell 1 0,growx");
		txtName.setColumns(10);
		
		previewContentPanel = new JPanel();
		add(previewContentPanel, BorderLayout.CENTER);
		previewContentPanel.setLayout(new BorderLayout(0, 0));
		
		previewScrollPane = new JScrollPane();
		previewContentPanel.add(previewScrollPane);
		
		previewPane = new JPanel();
		previewPane.add(preview);
		previewScrollPane.setViewportView(previewPane);
		
		scalePanel = new JPanel();
		previewContentPanel.add(scalePanel, BorderLayout.SOUTH);
		
		slider = new JSlider();
		slider.setMinimum(50);
		slider.setValue(100);
		slider.setMaximum(500);
		slider.addChangeListener((e)->{
			preview.setScale((float)(slider.getValue() / 100.0f));
			preview.revalidate();
			preview.repaint();
		});
		scalePanel.add(slider);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		add(buttonPanel, BorderLayout.SOUTH);
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener((e)->{
			dispose();
		});
		buttonPanel.add(btnCancel);
		
		btnConfirm = new JButton("Confirm");
		btnConfirm.addActionListener((e)->{
			if(txtName.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Cannot create a tile stamp with no name!", "Could Not Create", JOptionPane.WARNING_MESSAGE);
			}else {
				stamp.setAssetName(txtName.getText());
				result = stamp;
				dispose();
			}
		});
		buttonPanel.add(btnConfirm);
		pack();
	}

	
	public TileStamp getResult() {
		return result;
	}
}
