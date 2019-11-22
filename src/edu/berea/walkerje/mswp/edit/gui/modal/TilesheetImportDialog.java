package edu.berea.walkerje.mswp.edit.gui.modal;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.image.VolatileImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import edu.berea.walkerje.mswp.IRegion;
import edu.berea.walkerje.mswp.gfx.GameCanvas;
import edu.berea.walkerje.mswp.gfx.Tilesheet;

public class TilesheetImportDialog extends JDialog {
	private static final long serialVersionUID = -8666291656666472693L;
	
	private final JPanel contentPanel = new JPanel();
	private JPanel buttonPanel;
	private JPanel txtPanel;
	private JPanel infoPanel;
	private JButton btnConfirm;
	private JButton btnCancel;
	private JLabel lblFile;
	private JTextField txtFileName;
	private JButton btnChoose;
	private JLabel lblTileWidth;
	private JSpinner spinnerTileWidth;
	private JLabel lblTileHeight;
	private JSpinner spinnerTileHeight;
	private JLabel lblName;
	private JTextField txtName;
	
	private Tilesheet result = null;
	
	/**
	 * Create the dialog.
	 */
	public TilesheetImportDialog(JFrame parent) {
		super(parent);
		setTitle("Import Tilesheet");
		setModalityType(ModalityType.APPLICATION_MODAL);
		initComponents();
		pack();
		setLocationRelativeTo(parent);
	}

	private void initComponents() {
		setContentPane(contentPanel);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		buttonPanel = new JPanel();
		contentPanel.add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener((e)->{
			dispose();
		});
		buttonPanel.add(btnCancel);
		
		btnConfirm = new JButton("Confirm");
		btnConfirm.addActionListener((e)->{
			if(txtFileName.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Cannot import a tilesheet with no image!", "Cannot Import", JOptionPane.WARNING_MESSAGE);
			}else if(txtName.getText().isEmpty()){
				JOptionPane.showMessageDialog(this, "Cannot import a tilesheet with no name!", "Cannot Import", JOptionPane.WARNING_MESSAGE);
			}else {
				try {
					VolatileImage img = GameCanvas.copyBufferedImage(ImageIO.read(new File(txtFileName.getText())));
					int tileWidth = (int)spinnerTileWidth.getValue();
					int tileHeight = (int)spinnerTileHeight.getValue();
					result = new Tilesheet(txtName.getText(), img, IRegion.wrap(tileWidth, tileHeight));
					dispose();
				}catch(Exception exc) {
					JOptionPane.showMessageDialog(this, "Could not load tilesheet image!", "Cannot Import", JOptionPane.WARNING_MESSAGE);
					exc.printStackTrace();
				}
			}
		});
		buttonPanel.add(btnConfirm);
		
		txtPanel = new JPanel();
		contentPanel.add(txtPanel, BorderLayout.NORTH);
		txtPanel.setLayout(new MigLayout("", "[][grow][]", "[][]"));
		
		lblFile = new JLabel("File: ");
		txtPanel.add(lblFile, "cell 0 0,alignx trailing");
		
		txtFileName = new JTextField();
		txtFileName.setEditable(false);
		txtPanel.add(txtFileName, "cell 1 0,growx");
		txtFileName.setColumns(10);
		
		btnChoose = new JButton("Choose");
		btnChoose.addActionListener((e)->{
			JFileChooser chooser = new JFileChooser(new File("."));
			final int status = chooser.showOpenDialog(this);
			if(status == JFileChooser.APPROVE_OPTION) {
				txtFileName.setText(chooser.getSelectedFile().getPath());
			}
		});
		txtPanel.add(btnChoose, "cell 2 0");
		
		lblName = new JLabel("Name: ");
		txtPanel.add(lblName, "cell 0 1,alignx trailing");
		
		txtName = new JTextField();
		txtPanel.add(txtName, "cell 1 1,growx");
		txtName.setColumns(10);
		
		infoPanel = new JPanel();
		contentPanel.add(infoPanel, BorderLayout.CENTER);
		infoPanel.setLayout(new MigLayout("", "[][grow][][grow]", "[]"));
		
		lblTileWidth = new JLabel("Tile Width: ");
		infoPanel.add(lblTileWidth, "cell 0 0");
		
		spinnerTileWidth = new JSpinner();
		spinnerTileWidth.setModel(new SpinnerNumberModel(16, 4, 512, 16));
		infoPanel.add(spinnerTileWidth, "flowx,cell 1 0,alignx center");
		
		lblTileHeight = new JLabel("Tile Height: ");
		infoPanel.add(lblTileHeight, "cell 2 0");
		
		spinnerTileHeight = new JSpinner();
		spinnerTileHeight.setModel(new SpinnerNumberModel(16, 4, 512, 16));
		infoPanel.add(spinnerTileHeight, "cell 3 0,alignx center");
	}
	
	public Tilesheet getResult() {
		return result;
	}
}
