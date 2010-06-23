/**
 * Main.java
 */
package edu.belmont.mth.visigraph;

import javax.swing.*;

import edu.belmont.mth.visigraph.gui.MainWindow;

/**
 * @author Cameron Behar
 *
 */
public class Main
{
	public static void main(String[] args)
	{		
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } 
	    catch (Exception e) { }
	    
		new MainWindow();
	}
}
