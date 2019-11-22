package edu.berea.walkerje.mswp;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import edu.berea.walkerje.mswp.edit.Editor;
import edu.berea.walkerje.mswp.edit.Project;
import edu.berea.walkerje.mswp.edit.gui.modal.ProjectCreateDialog;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.border.BevelBorder;
import java.awt.Color;
import java.io.File;

public class Landing extends JFrame {
	private static final long serialVersionUID = 6917948415830071561L;
	
	private JPanel contentPane;
	
	/**
	 * Create the frame.
	 */
	public Landing() {
		setTitle("MSWP (My Silly World Project)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[grow]", "[][grow]"));
		
		JPanel logoPanel = new JPanel();
		logoPanel.setBackground(Color.LIGHT_GRAY);
		logoPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		contentPane.add(logoPanel, "cell 0 0,growx");
		
		JLabel mswpLogo = new JLabel("");
		logoPanel.add(mswpLogo);
		mswpLogo.setHorizontalAlignment(SwingConstants.CENTER);
		mswpLogo.setIcon(new ImageIcon(Landing.class.getResource("/edu/berea/walkerje/mswp/asset/mswp_logo.png")));
		
		JPanel contentPanel = new JPanel();
		contentPane.add(contentPanel, "cell 0 1,grow");
		contentPanel.setLayout(new MigLayout("", "[grow]", "[grow][]"));
		
		JPanel buttonPanel = new JPanel();
		contentPanel.add(buttonPanel, "cell 0 0,growx,aligny center");
		buttonPanel.setLayout(new MigLayout("", "[416px]", "[25px][][]"));
		
		JButton btnCreate = new JButton("Create");
		btnCreate.addActionListener(e -> {handleCreatePressed();});
		buttonPanel.add(btnCreate, "cell 0 0,growx,aligny center");
		
		JButton btnEdit = new JButton("Edit");
		btnEdit.addActionListener(e -> {handleEditPressed();});
		buttonPanel.add(btnEdit, "cell 0 1,growx,aligny top");
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(e -> {handleExitPressed();});
		buttonPanel.add(btnExit, "cell 0 2,growx");
		
		JLabel aboutLabel = new JLabel("Created by Jesse Walker, Fall 2019");
		contentPanel.add(aboutLabel, "cell 0 1,alignx center");

		setResizable(false);
		pack();
		setLocationRelativeTo(null);
	}

	//Event listeners for the various buttons.
	
	private void handleCreatePressed() {
		//Blah blah blah...
		Landing thisInst = this;
		ProjectCreateDialog d = new ProjectCreateDialog(this);
		d.setVisible(true);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if(!d.isVisible()) {
					if(d.getResult() != null) {
						new Editor(d.getResult()).setVisible(true);
						thisInst.dispose();
					}
				}else SwingUtilities.invokeLater(this);
			}
		});
	}
	
	private void handleEditPressed() {
		//Blah blah blah...
		//Open a dialog box for file searching, then dispose?
		JFileChooser fileChooser = new JFileChooser(new File("."));
		int chooseStatus = fileChooser.showOpenDialog(this);
		if(chooseStatus == JFileChooser.APPROVE_OPTION) {
			new Editor(new Project(fileChooser.getSelectedFile())).setVisible(true);//Editor is kept alive by AWT thread.
			dispose();
		}
	}
	
	private void handleExitPressed() {
		dispose();
	}
}
