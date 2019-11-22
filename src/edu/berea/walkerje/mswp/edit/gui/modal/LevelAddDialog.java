package edu.berea.walkerje.mswp.edit.gui.modal;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import edu.berea.walkerje.mswp.play.GameLevel;

@SuppressWarnings("serial")
public class LevelAddDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JPanel buttonPanel;
	private JPanel selectPanel;
	private final JButton btnConfirm = new JButton("Confirm");
	private final JButton btnCancel = new JButton("Cancel");
	private final JLabel lblTilesX = new JLabel("Level Width: ");
	private final JSpinner spinnerTilesX = new JSpinner();
	private final JLabel lblTilesY = new JLabel("Level Height: ");
	private final JSpinner spinnerTilesY = new JSpinner();
	private final JPanel namePanel = new JPanel();
	private final JLabel lblName = new JLabel("Name: ");
	private final JTextField txtName = new JTextField();
	private final JLabel lblTileWidth = new JLabel("Tile Width: ");
	private final JLabel lblTileHeight = new JLabel("Tile Height: ");
	private final JSpinner spinnerTileWidth = new JSpinner();
	private final JSpinner spinner = new JSpinner();
	
	private GameLevel result = null;
	
	/**
	 * Create the dialog.
	 */
	public LevelAddDialog(JFrame parent) {
		super(parent);
		setTitle("Add Level");
		setModalityType(ModalityType.APPLICATION_MODAL);
		initComponents();
		pack();
		setResizable(false);
		setLocationRelativeTo(parent);
	}
	
	private void initComponents() {
		setContentPane(contentPanel);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		buttonPanel = new JPanel();
		contentPanel.add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		btnCancel.addActionListener((e)->{
			dispose();
		});
		buttonPanel.add(btnCancel);
		
		btnConfirm.addActionListener((e)->{
			if(txtName.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Cannot create a level with no name!", "Cannot Create Level", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			result = new GameLevel(txtName.getText(), (int)spinnerTilesX.getValue(), (int)spinnerTilesY.getValue(), (int)spinnerTileWidth.getValue(), (int)spinner.getValue());
			dispose();
		});
		buttonPanel.add(btnConfirm);
		
		selectPanel = new JPanel();
		contentPanel.add(selectPanel, BorderLayout.CENTER);
		selectPanel.setLayout(new MigLayout("", "[][grow][][grow]", "[][]"));
		
		selectPanel.add(lblTilesX, "cell 0 0");
		spinnerTilesX.setModel(new SpinnerNumberModel(8, 1, 256, 8));
		
		selectPanel.add(spinnerTilesX, "cell 1 0,alignx center");
		
		selectPanel.add(lblTilesY, "cell 2 0");
		spinnerTilesY.setModel(new SpinnerNumberModel(8, 1, 256, 8));
		
		selectPanel.add(spinnerTilesY, "cell 3 0,alignx center");
		
		selectPanel.add(lblTileWidth, "cell 0 1");
		spinnerTileWidth.setModel(new SpinnerNumberModel(32, 4, 256, 16));
		
		selectPanel.add(spinnerTileWidth, "cell 1 1,alignx center");
		
		selectPanel.add(lblTileHeight, "cell 2 1");
		spinner.setModel(new SpinnerNumberModel(32, 9, 256, 16));
		
		selectPanel.add(spinner, "cell 3 1,alignx center");
		
		contentPanel.add(namePanel, BorderLayout.NORTH);
		namePanel.setLayout(new MigLayout("", "[48px][124px,grow]", "[19px]"));
		
		namePanel.add(lblName, "cell 0 0,alignx left,aligny center");
		
		txtName.setHorizontalAlignment(SwingConstants.CENTER);
		txtName.setColumns(10);
		namePanel.add(txtName, "cell 1 0,growx,aligny top");
	}
	
	public GameLevel getResult() {
		return result;
	}
}
