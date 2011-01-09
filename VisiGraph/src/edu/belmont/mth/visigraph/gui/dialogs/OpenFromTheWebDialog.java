/**
 * OpenFromWebDialog.java
 */
package edu.belmont.mth.visigraph.gui.dialogs;

import java.io.*;
import java.awt.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.regex.*;
import javax.swing.event.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.resources.*;
import edu.belmont.mth.visigraph.utilities.*;

/**
 * @author Cameron Behar
 */
public class OpenFromTheWebDialog extends JDialog implements ActionListener
{
	private static JList				graphsList;
	private static JButton				okButton;
	private static String				value;
	private static OpenFromTheWebDialog	dialog;
	
	public static String showDialog( Component owner )
	{
		dialog = new OpenFromTheWebDialog( JOptionPane.getFrameForComponent( owner ) );
		dialog.setVisible( true );
		return value;
	}
	
	private OpenFromTheWebDialog( Frame owner )
	{
		super( owner, StringBundle.get( "open_from_the_web_dialog_title" ), true );
		this.setResizable( false );
		
		graphsList = new JList( new String[ ] { StringBundle.get( "open_from_the_web_dialog_loading_graphs_label" ) } )
		{
			{
				this.setEnabled( false );
				this.setAutoscrolls( true );
				this.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
				this.addListSelectionListener( new ListSelectionListener( )
				{
					@Override
					public void valueChanged( ListSelectionEvent event )
					{
						okButton.setEnabled( true );
					}
				} );
				this.addMouseListener( new MouseAdapter( )
				{
					@Override
					public void mousePressed( MouseEvent event )
					{
						if( event.getClickCount( ) > 1 )
						{
							try
							{
								OpenFromTheWebDialog.this.accept( );
							}
							catch( Exception ex )
							{
								DebugUtilities.logException( "An exception occurred while downloading files.", ex );
								JOptionPane.showMessageDialog( OpenFromTheWebDialog.this, StringBundle.get( "an_exception_occurred_while_downloading_files_dialog_message" ), GlobalSettings.applicationName, JOptionPane.ERROR_MESSAGE );
							}
							finally
							{
								OpenFromTheWebDialog.this.setCursor( Cursor.getDefaultCursor( ) );
							}
							
							OpenFromTheWebDialog.dialog.setVisible( false );
						}
					}
				} );
			}
		};
		
		final JScrollPane graphsScrollPane = new JScrollPane( graphsList );
		
		JPanel inputPanel = new JPanel( new BorderLayout( ) )
		{
			{
				this.setBorder( BorderFactory.createEmptyBorder( 14, 14, 3, 14 ) );
				this.setPreferredSize( new Dimension( 350, 150 ) );
				this.add( graphsScrollPane );
			}
		};
		
		// Create and initialize the buttons
		okButton = new JButton( StringBundle.get( "download_button_text" ) )
		{
			{
				this.setPreferredSize( new Dimension( 100, 28 ) );
				this.setActionCommand( "Download" );
				this.addActionListener( OpenFromTheWebDialog.this );
				this.setEnabled( false );
				OpenFromTheWebDialog.this.getRootPane( ).setDefaultButton( this );
			}
		};
		final JButton cancelButton = new JButton( StringBundle.get( "cancel_button_text" ) )
		{
			{
				this.setPreferredSize( new Dimension( 80, 28 ) );
				this.addActionListener( OpenFromTheWebDialog.this );
			}
		};
		
		// Lay out the buttons from left to right
		JPanel buttonPanel = new JPanel( )
		{
			{
				this.setLayout( new BoxLayout( this, BoxLayout.LINE_AXIS ) );
				this.setBorder( BorderFactory.createEmptyBorder( -2, 9, 9, 13 ) );
				this.add( Box.createHorizontalGlue( ) );
				this.add( okButton );
				this.add( Box.createRigidArea( new Dimension( 10, 0 ) ) );
				this.add( cancelButton );
			}
		};
		
		// Put everything together, using the content pane's BorderLayout
		Container contentPanel = this.getContentPane( );
		contentPanel.setLayout( new BorderLayout( 9, 9 ) );
		contentPanel.add( inputPanel, BorderLayout.CENTER );
		contentPanel.add( buttonPanel, BorderLayout.PAGE_END );
		
		Dimension size = this.getPreferredSize( );
		size.width += 40;
		size.height += 40;
		this.setPreferredSize( size );
		
		this.pack( );
		this.setLocationRelativeTo( owner );
		
		new Thread( "graphsLoader" )
		{
			@Override
			public void run( )
			{
				OpenFromTheWebDialog.this.loadGraphs( );
			}
		}.start( );
	}
	
	private void accept( ) throws Exception
	{
		this.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
		this.downloadGraphs( );
		
		String filename = ( (String) graphsList.getSelectedValue( ) ).substring( 1 );
		value = String.format( "graphs/%s.vsg", filename );
	}
	
	public void actionPerformed( ActionEvent e )
	{
		if( e.getActionCommand( ).equals( "Download" ) )
			try
			{
				this.accept( );
			}
			catch( Exception ex )
			{
				DebugUtilities.logException( "An exception occurred while downloading files.", ex );
				JOptionPane.showMessageDialog( this, StringBundle.get( "an_exception_occurred_while_downloading_files_dialog_message" ), GlobalSettings.applicationName, JOptionPane.ERROR_MESSAGE );
			}
			finally
			{
				this.setCursor( Cursor.getDefaultCursor( ) );
			}
		
		OpenFromTheWebDialog.dialog.setVisible( false );
	}
	
	public void downloadGraphs( ) throws Exception
	{
		File folder = new File( "graphs" );
		
		if( !folder.exists( ) || !folder.isDirectory( ) )
			folder.mkdir( );
		
		String filename = ( (String) graphsList.getSelectedValue( ) ).substring( 1 );
		WebUtilities.downloadFile( String.format( GlobalSettings.applicationGraphsFileUrl, URLEncoder.encode( filename, "UTF-8" ).replace( "+", "%20" ) ), String.format( "graphs/%s.vsg", filename ) );
	}
	
	public void loadGraphs( )
	{
		try
		{
			URLConnection conn = new URL( GlobalSettings.applicationGraphsDirectoryUrl ).openConnection( );
			BufferedReader in = new BufferedReader( new InputStreamReader( conn.getInputStream( ), "UTF-8" ) );
			String line = null;
			
			Vector<String> graphs = new Vector<String>( );
			while( ( line = in.readLine( ) ) != null )
			{
				Pattern pattern = Pattern.compile( "^.*<li><a\\shref=\"([^\\r\\n]+?)\\.vsg\">([^\\r\\n]+?)\\.vsg</a></li>.*$" );
				Matcher matcher = pattern.matcher( line );
				matcher.find( );
				
				if( matcher.matches( ) && URLDecoder.decode( matcher.group( 1 ), "UTF-8" ).equals( matcher.group( 2 ) ) )
					graphs.add( " " + matcher.group( 2 ) );
			}
			
			OpenFromTheWebDialog.graphsList.setListData( graphs );
			graphsList.setEnabled( true );
			in.close( );
		}
		catch( Throwable ex )
		{
			DebugUtilities.logException( "An exception occurred while downloading the standard list of graphs.", ex );
		}
		
	}
}
