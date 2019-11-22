package edu.berea.walkerje.mswp.edit.gui.modal;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.berea.walkerje.mswp.edit.Project;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import java.io.File;

public class ProjectCreateDialog extends JDialog {
	private static final long serialVersionUID = -6054140867118773927L;
	
	private final JPanel contentPanel = new JPanel();
	private JLabel lblFolder;
	private JTextField txtFolder;
	private JButton btnChoose;
	private JPanel buttonPanel;
	private JPanel infoPanel;
	private JButton btnConfirm;
	private JButton btnCancel;
	private JLabel lblName;
	private JTextField txtName;

	private Project result = null;
	
	/**
	 * Create the dialog.
	 */
	public ProjectCreateDialog(JFrame parent) {
		super(parent);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Create Project");
		setSize(400, 200);
		setResizable(false);
		setLocationRelativeTo(parent);
		initComponents();
	}

	private void initComponents() {
		setContentPane(contentPanel);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		infoPanel = new JPanel();
		contentPanel.add(infoPanel, BorderLayout.CENTER);
		infoPanel.setLayout(new MigLayout("", "[][grow][]", "[][]"));
		
		lblFolder = new JLabel("Folder: ");
		infoPanel.add(lblFolder, "cell 0 0");
		
		txtFolder = new JTextField();
		txtFolder.setEditable(false);
		infoPanel.add(txtFolder, "cell 1 0,growx");
		txtFolder.setColumns(10);
		
		btnChoose = new JButton("Choose");
		infoPanel.add(btnChoose, "cell 2 0");
		
		lblName = new JLabel("Name: ");
		infoPanel.add(lblName, "cell 0 1,alignx trailing");
		
		txtName = new JTextField();
		infoPanel.add(txtName, "cell 1 1,growx");
		btnChoose.addActionListener((e)->{
			JFileChooser chooser = new JFileChooser(new File("."));
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			final int status = chooser.showOpenDialog(this);
			if(status == JFileChooser.APPROVE_OPTION) {
				txtFolder.setText(chooser.getSelectedFile().getPath());
			}
		});
		
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
			if(txtName.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Cannot create a project with no name!", "Cannot create Project", JOptionPane.WARNING_MESSAGE);
			}else if(txtFolder.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "No Directory has been chosen to create the project in!", "Cannot create Project", JOptionPane.WARNING_MESSAGE);
			}else {
				File destFile = resolveFile();
				destFile.mkdirs();
				result = Project.createInstance(txtName.getText(), resolveFile());
				dispose();
			}
		});
		buttonPanel.add(btnConfirm);
	}
	
	private File resolveFile() {
		return new File(new File(txtFolder.getText()), txtName.getText());
	}
	
	public Project getResult() {
		return result;
	}
}
