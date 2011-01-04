/**
 * Main.java
 */
package edu.belmont.mth.visigraph;

import java.io.*;
import javax.swing.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.gui.windows.*;

/**
 * @author Cameron Behar
 */
public class Main
{
	private static void initializeLookAndFeel( )
	{
		try
		{
			System.setProperty( "java.awt.Window.locationByPlatform", "true" );
			System.setProperty( "apple.laf.useScreenMenuBar", "true" );
			System.setProperty( "com.apple.mrj.application.apple.menu.about.name", GlobalSettings.applicationName );
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName( ) );
		}
		catch( Exception ex )
		{
			DebugUtilities.logException( "An exception occurred while setting the system look and feel.", ex );
		}
	}
	
	public static void main( final String[ ] args )
	{
		initializeLookAndFeel( );
		
		SwingUtilities.invokeLater( new Runnable( )
		{
			public void run( )
			{
				MainWindow window = new MainWindow( );
				for( String arg : args )
					try
					{
						window.openFile( new File( arg ) );
					}
					catch( IOException ex )
					{
						DebugUtilities.logException( "An exception occurred while loading a graph from file.", ex );
					}
			}
		} );
	}
}
