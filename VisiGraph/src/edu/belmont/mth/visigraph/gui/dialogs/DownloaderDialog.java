/**
 * DownloaderDialog.java
 */
package edu.belmont.mth.visigraph.gui.dialogs;

import java.io.*;
import java.awt.*;
import java.util.*;
import java.beans.*;
import javax.swing.*;
import java.util.List;
import edu.belmont.mth.visigraph.resources.StringBundle;
import edu.belmont.mth.visigraph.utilities.*;

/**
 * @author Cameron Behar
 */
public class DownloaderDialog
{
	public static class DownloadData
	{
		public final String	url, destination, name;
		
		public DownloadData( String url, String destination, String name )
		{
			this.url = url;
			this.destination = destination;
			this.name = name;
		}
	}
	
	private static class DownloadFiles extends SwingWorker<Void, DownloadData>
	{
		private final List<DownloadData>	downloads;
		
		DownloadFiles( List<DownloadData> downloads )
		{
			this.downloads = downloads;
		}
		
		@Override
		public Void doInBackground( )
		{
			this.setProgress( 0 );
			
			for( DownloadData download : this.downloads )
			{
				if( this.isCancelled( ) )
					return null;
				
				try
				{
					File folder = new File( download.destination.substring( 0, download.destination.indexOf( '/' ) ) );
					if( !folder.exists( ) || !folder.isDirectory( ) )
						folder.mkdir( );
					
					WebUtilities.downloadFile( download.url, download.destination );
					
					this.publish( download );
				}
				catch( Throwable t )
				{
					DebugUtilities.logException( "An exception occurred while downloading " + download.url, t );
				}
				
				this.setProgress( this.getProgress( ) + 1 );
			}
			
			return null;
		}
		
		@Override
		public void done( )
		{
			Toolkit.getDefaultToolkit( ).beep( );
		}
		
		@Override
		public void process( List<DownloadData> downloads )
		{
			if( this.isCancelled( ) )
				return;
			
			for( DownloadData download : downloads )
			{
				progressMonitor.setNote( download.name );
				downloadListener.downloaded( download );
			}
		}
	}
	
	public static interface DownloadListener extends EventListener
	{
		public void downloaded( DownloadData download );
	}
	
	private static ProgressMonitor	progressMonitor;
	private static DownloadFiles	operation;
	private static DownloadListener	downloadListener;
	
	public static void showDialog( Component owner, List<DownloadData> downloads, DownloadListener downloadListener )
	{
		progressMonitor = new ProgressMonitor( owner, StringBundle.get( "downloader_dialog_message" ), "", 0, downloads.size( ) );
		progressMonitor.setProgress( 0 );
		
		DownloaderDialog.downloadListener = downloadListener;
		
		operation = new DownloadFiles( downloads );
		operation.addPropertyChangeListener( new PropertyChangeListener( )
		{
			@Override
			public void propertyChange( PropertyChangeEvent event )
			{
				if( progressMonitor.isCanceled( ) )
					operation.cancel( true );
				else if( event.getPropertyName( ).equals( "progress" ) )
					progressMonitor.setProgress( (Integer) event.getNewValue( ) );
			}
		} );
		operation.execute( );
	}
}
