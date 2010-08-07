/**
 * Main.java
 */
package edu.belmont.mth.visigraph;

import java.io.*;
import java.util.*;
import javax.swing.*;
import edu.belmont.mth.visigraph.gui.windows.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.DebugUtilities;

/**
 * @author Cameron Behar
 * 
 */
public class Main
{
	public static void main(String[] args)
	{
		try
		{
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", GlobalSettings.applicationName);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception ex) { DebugUtilities.logException("An exception occurred while setting the system look and feel.", ex); }
		
		LoadPreferences();
		
		new MainWindow();
	}
	
	private static void LoadPreferences()
	{
		File userSettingsFile = new File("UserSettings.json");
		if(userSettingsFile.exists())
		{
			try
			{
				Scanner in = new Scanner(userSettingsFile);
				
				StringBuilder sb = new StringBuilder();
				while(in.hasNextLine())
					sb.append(in.nextLine());
				
				UserSettings.instance.fromString(sb.toString());
				
				in.close();
			}
			catch (IOException ex)
			{
				DebugUtilities.logException("An exception occurred while loading user settings.", ex);
			}
		}
	}
}










