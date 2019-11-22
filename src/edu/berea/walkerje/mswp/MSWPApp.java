package edu.berea.walkerje.mswp;

import java.awt.EventQueue;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class MSWPApp {
	
	/**
	 * The main function used by MSWP.
	 * Instantiates and makes visible the Landing frame.
	 * @param args
	 */
	public static void main(String... args) {
//		System.setProperty("", value)
		//OpenGL Pipeline?
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					
					Landing frame = new Landing();
					frame.setVisible(true);
				}catch(Exception e) {//Catch generic exception.
					//Wrap stack trace from exception into a string.
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);//wrap StringWriter in PrintWriter
					e.printStackTrace(pw);//print exception stack trace to the print writer...
					//Then show an error message dialog.
					JOptionPane.showMessageDialog(null, sw.toString(), "Unhandled Exception", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
	
	/**
	 * Returns an input stream from some file in the asset path of this JAR classpath.
	 * @param path of the file, inside the asset package.
	 * @return an input stream for the given asset.
	 */
	public static InputStream readAsset(String path) {
		return MSWPApp.class.getResourceAsStream("asset/"+path);
	}
}