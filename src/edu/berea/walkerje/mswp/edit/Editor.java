package edu.berea.walkerje.mswp.edit;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.event.CFocusListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.theme.ThemeMap;
import edu.berea.walkerje.mswp.edit.gui.EditorToolDialog;
import edu.berea.walkerje.mswp.edit.gui.GameLevelEditor;
import edu.berea.walkerje.mswp.edit.gui.ProjectOutline;
import edu.berea.walkerje.mswp.edit.gui.modal.LevelAddDialog;
import edu.berea.walkerje.mswp.edit.gui.modal.TilesheetImportDialog;
import edu.berea.walkerje.mswp.play.GameLevel;

@SuppressWarnings("serial")
public class Editor extends JFrame {
	private CControl dockController;
	private DefaultSingleCDockable projectOutlinePanel;
	
	private ProjectOutline projectOutline;
	
	private EditorToolDialog toolDialog;
	
	private HashMap<String, DefaultSingleCDockable> editorPanes = new HashMap<String, DefaultSingleCDockable>();
	
	/**
	 * Create the frame.
	 */
	public Editor(Project activeProject) {
		setTitle(String.format("MSWP Project: \"%s\"", activeProject.getProjectName()));
		toolDialog = new EditorToolDialog(this, activeProject);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH);		
		
		setLocationRelativeTo(null);
		
		{	//Initialize menu bar in its own scope for personal clarity.
			JMenuBar menuBar = new JMenuBar();
	        setJMenuBar(menuBar);
	        
	        JMenu mnFile = new JMenu("File");
	        menuBar.add(mnFile);
	        
	        JMenuItem mntmSave = new JMenuItem("Save");
	        mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
	        mntmSave.addActionListener(e->{
	        	try {
					Project.write(activeProject);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	        });
	        mnFile.add(mntmSave);
	        
	        JMenu mnImport = new JMenu("Import");
	        mnFile.add(mnImport);
	        
	        JMenuItem mntmSpritesheet = new JMenuItem("Spritesheet");
	        mnImport.add(mntmSpritesheet);
	        
	        JMenuItem mntmTilesheet = new JMenuItem("Tilesheet");
	        mntmTilesheet.addActionListener((e)->{
	        	TilesheetImportDialog d = new TilesheetImportDialog(this);
	        	d.setVisible(true);
	        	SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if(!d.isVisible()) {
							if(d.getResult() != null) {
								activeProject.addAsset(d.getResult());
								projectOutline.rebuildTreeModel();
								toolDialog.rebuildToolPanels();
							}
						}else SwingUtilities.invokeLater(this);
					}
	        	});
	        });
	        mnImport.add(mntmTilesheet);
	        
	        JMenu mnEdit = new JMenu("Edit");
	        menuBar.add(mnEdit);
	        
	        JMenu mnAdd = new JMenu("Add");
	        mnEdit.add(mnAdd);
	        
	        JMenuItem mntmLevel = new JMenuItem("Level");
	        mntmLevel.addActionListener((e)->{
	        	LevelAddDialog d = new LevelAddDialog(this);
	        	d.setVisible(true);
	        	SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if(!d.isVisible()) {
							if(d.getResult() != null) {
								GameLevel res = d.getResult();
								activeProject.addAsset(res);
					        	projectOutline.rebuildTreeModel();
					        	openLevelEditor(res);
							}
						}else SwingUtilities.invokeLater(this);
					}
	        	});
	        });
	        mnAdd.add(mntmLevel);
	        
	        JMenu mnView = new JMenu("View");
	        menuBar.add(mnView);
	        
	        JRadioButtonMenuItem rdbtnmntmTools = new JRadioButtonMenuItem("Tools");
	        rdbtnmntmTools.setSelected(true);
	        rdbtnmntmTools.addChangeListener((e)->{
	        	toolDialog.setVisible(rdbtnmntmTools.isSelected());
	        });
	        rdbtnmntmTools.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK));
	        mnView.add(rdbtnmntmTools);
	        
	        JRadioButtonMenuItem rdbtnmntmOutline = new JRadioButtonMenuItem("Outline");
	        rdbtnmntmOutline.setSelected(true);
	        rdbtnmntmOutline.addChangeListener((e)->{
	        	projectOutlinePanel.setVisible(rdbtnmntmOutline.isSelected());
	        });
	        rdbtnmntmOutline.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
	        mnView.add(rdbtnmntmOutline);
	        
			toolDialog.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent arg0) {
					rdbtnmntmTools.setSelected(false);
				}
			});
		}
		
		//NOTE: The order in which we use CControl is 110% necessary.
		//Changing the order will probably break something.
		dockController = new CControl(this);
		dockController.setTheme(ThemeMap.KEY_ECLIPSE_THEME);
        setContentPane(dockController.getContentArea());
        
		projectOutlinePanel = wrapChildPanel("Project Outline", projectOutline = new ProjectOutline(this, activeProject), false);
		dockController.addDockable(projectOutlinePanel);
		projectOutlinePanel.setLocation(CLocation.base().minimalWest());
		projectOutlinePanel.setVisible(true);
	}
	
	/**
	 * Returns an instance of the Tool Dialog.
	 * @return
	 */
	public EditorToolDialog getToolDialog() {
		return toolDialog;
	}
	
	/**
	 * Returns an instance of the Project Outline.
	 * @return
	 */
	public ProjectOutline getOutline() {
		return projectOutline;
	}
	
	/**
	 * Rebuilds the project outline in the editor.
	 */
	public void rebuildOutline() {
		projectOutline.rebuildTreeModel();
	}
	
	/**
	 * Opens a Level Editor for the specified level.
	 * If an editor already exists for the level, it shows it instead.
	 * @param level
	 */
	public void openLevelEditor(GameLevel level) {
		if(editorPanes.containsKey(level.getAssetName())) {
			editorPanes.get(level.getAssetName()).setVisible(true);
			return;
		}
		
		DefaultSingleCDockable dock = wrapChildPanel(String.format("Level \"%s\"", level.getAssetName()), new GameLevelEditor(toolDialog, level), true);
		//Note for future self: You MUST add KeyListeners to the DefaultSingleCDockable for level editors to get key events.
		//Adding the key listener to the GameLevelEditorPanel does not work on its own.
		dock.addFocusListener(new CFocusListener() {
			public void focusGained(CDockable arg0) {
				//Change the edited level when an editor pain gains focus.
				toolDialog.getToolState().setCurrentLevel(level);
			}
			public void focusLost(CDockable arg0) {}
		});
		editorPanes.put(level.getAssetName(), dock);
		
		dockController.addDockable(dock);
		dock.setLocation(CLocation.base().normal());
		dock.setVisible(true);
	}
	
	/**
	 * Wraps a child JPanel into a dockable region. 
	 * @param title
	 * @param panel
	 * @param closable
	 * @return
	 */
	private DefaultSingleCDockable wrapChildPanel(String title, JPanel panel, boolean closable) {
        DefaultSingleCDockable dockable = new DefaultSingleCDockable( title, title, panel );
        dockable.setCloseable(closable);
        return dockable;
	}
}