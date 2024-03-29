/**
 * DownloadsDialog.java
 */
package edu.belmont.mth.visigraph.gui.dialogs;

import java.io.*;
import java.awt.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.util.List;
import java.awt.event.*;
import java.util.regex.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.gui.dialogs.DownloaderDialog.DownloadData;
import edu.belmont.mth.visigraph.resources.*;
import edu.belmont.mth.visigraph.utilities.*;

/**
 * @author Cameron Behar
 */
public class DownloadsDialog extends JDialog implements ActionListener
{
	private static JPanel				generatorsPanel;
	private static JPanel				functionsPanel;
	private static DownloadsDialog		dialog;
	private static List<DownloadData>	value;
	
	public static List<DownloadData> showDialog( Component owner )
	{
		value = null;
		dialog = new DownloadsDialog( JOptionPane.getFrameForComponent( owner ) );
		dialog.setVisible( true );
		return value;
	}
	
	private DownloadsDialog( Frame owner )
	{
		super( owner, StringBundle.get( "downloads_dialog_title" ), true );
		this.setResizable( false );
		
		JLabel generatorsLabel = new JLabel( StringBundle.get( "downloads_dialog_available_generators_label" ) );
		generatorsPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) )
		{
			{
				this.setBackground( Color.white );
				this.setFont( this.getFont( ).deriveFont( Font.ITALIC ) );
				this.add( new JLabel( StringBundle.get( "downloads_dialog_loading_generators_label" ) )
				{
					{
						this.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
						this.setFont( new Font( this.getFont( ).getName( ), Font.ITALIC, this.getFont( ).getSize( ) ) );
						this.setForeground( Color.gray );
					}
				} );
			}
		};
		JScrollPane generatorsScrollPane = new JScrollPane( generatorsPanel )
		{
			{
				this.setMinimumSize( new Dimension( 400, 150 ) );
			}
		};
		
		JLabel functionsLabel = new JLabel( StringBundle.get( "downloads_dialog_available_functions_label" ) )
		{
			{
				this.setBorder( BorderFactory.createEmptyBorder( 5, 0, 0, 0 ) );
			}
		};
		functionsPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) )
		{
			{
				this.setBackground( Color.white );
				this.add( new JLabel( StringBundle.get( "downloads_dialog_loading_functions_label" ) )
				{
					{
						this.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
						this.setFont( this.getFont( ).deriveFont( Font.ITALIC ) );
						this.setForeground( Color.gray );
					}
				} );
			}
		};
		JScrollPane functionsScrollPane = new JScrollPane( functionsPanel )
		{
			{
				this.setMinimumSize( new Dimension( 400, 150 ) );
			}
		};
		
		JPanel inputPanel = new JPanel( )
		{
			{
				this.setBorder( BorderFactory.createEmptyBorder( 3, 8, -5, 8 ) );
			}
		};
		GroupLayout layout = new GroupLayout( inputPanel )
		{
			{
				this.setAutoCreateGaps( true );
				this.setAutoCreateContainerGaps( true );
			}
		};
		inputPanel.setLayout( layout );
		
		layout.setHorizontalGroup( layout.createSequentialGroup( ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING ).addComponent( generatorsLabel ).addComponent( generatorsScrollPane ).addComponent( functionsLabel ).addComponent( functionsScrollPane ) ) );
		layout.setVerticalGroup( layout.createSequentialGroup( ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING ).addComponent( generatorsLabel ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( generatorsScrollPane ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING ).addComponent( functionsLabel ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( functionsScrollPane ) ) );
		
		// Create and initialize the buttons
		final JButton downloadButton = new JButton( StringBundle.get( "download_button_text" ) )
		{
			{
				this.setPreferredSize( new Dimension( 100, 28 ) );
				this.setActionCommand( "Download" );
				this.addActionListener( DownloadsDialog.this );
				DownloadsDialog.this.getRootPane( ).setDefaultButton( this );
			}
		};
		final JButton cancelButton = new JButton( StringBundle.get( "cancel_button_text" ) )
		{
			{
				this.setPreferredSize( new Dimension( 80, 28 ) );
				this.addActionListener( DownloadsDialog.this );
			}
		};
		
		// Lay out the buttons from left to right
		JPanel buttonPanel = new JPanel( )
		{
			{
				this.setLayout( new BoxLayout( this, BoxLayout.LINE_AXIS ) );
				this.setBorder( BorderFactory.createEmptyBorder( -2, 9, 9, 13 ) );
				this.add( Box.createHorizontalGlue( ) );
				this.add( downloadButton );
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
		
		new Thread( "generatorsLoader" )
		{
			@Override
			public void run( )
			{
				DownloadsDialog.this.loadGenerators( );
			}
		}.start( );
		new Thread( "functionsLoader" )
		{
			@Override
			public void run( )
			{
				DownloadsDialog.this.loadFunctions( );
			}
		}.start( );
	}
	
	@Override
    public void actionPerformed( ActionEvent e )
	{
		if( e.getActionCommand( ).equals( "Download" ) )
			value = this.getSelectedDownloads( );
		
		DownloadsDialog.dialog.setVisible( false );
	}
	
	private List<DownloadData> getSelectedDownloads( )
	{
		List<DownloadData> ret = new ArrayList<DownloadData>( );
		
		for( Component component : generatorsPanel.getComponents( ) )
		{
			JCheckBox checkBox = (JCheckBox) component;
			
			if( checkBox.isSelected( ) )
				try
				{
					ret.add( new DownloadData( String.format( GlobalSettings.applicationGeneratorFileUrl, URLEncoder.encode( checkBox.getText( ), "UTF-8" ) ), String.format( "generators/%s.java", checkBox.getText( ) ), checkBox.getText( ) ) );
				}
				catch( Throwable t )
				{}
		}
		
		for( Component component : functionsPanel.getComponents( ) )
		{
			JCheckBox checkBox = (JCheckBox) component;
			
			if( checkBox.isSelected( ) )
				try
				{
					ret.add( new DownloadData( String.format( GlobalSettings.applicationFunctionFileUrl, URLEncoder.encode( checkBox.getText( ), "UTF-8" ) ), String.format( "functions/%s.java", checkBox.getText( ) ), checkBox.getText( ) ) );
				}
				catch( Throwable t )
				{}
		}
		
		return ret;
	}
	
	private void loadFunctions( )
	{
		try
		{
			URLConnection conn = new URL( GlobalSettings.applicationFunctionDirectoryUrl ).openConnection( );
			BufferedReader in = new BufferedReader( new InputStreamReader( conn.getInputStream( ), "UTF-8" ) );
			String line = null;
			
			LinkedList<JCheckBox> checkBoxes = new LinkedList<JCheckBox>( );
			while( ( line = in.readLine( ) ) != null )
			{
				Pattern pattern = Pattern.compile( "^.*<li><a\\shref=\"([^\\r\\n]+?)\\.java\">([^\\r\\n]+?)\\.java</a></li>.*$" );
				Matcher matcher = pattern.matcher( line );
				matcher.find( );
				
				if( matcher.matches( ) && URLDecoder.decode( matcher.group( 1 ), "UTF-8" ).equals( matcher.group( 2 ) ) )
					checkBoxes.add( new JCheckBox( matcher.group( 1 ) )
					{
						{
							this.setPreferredSize( new Dimension( 375, 23 ) );
							this.setBackground( functionsPanel.getBackground( ) );
						}
					} );
			}
			
			functionsPanel.setLayout( new GridLayout( 0, 1 ) );
			functionsPanel.removeAll( );
			for( JCheckBox checkBox : checkBoxes )
				functionsPanel.add( checkBox );
			functionsPanel.updateUI( );
			in.close( );
		}
		catch( Throwable ex )
		{
			DebugUtilities.logException( "An exception occurred while downloading the standard list of functions.", ex );
		}
	}
	
	private void loadGenerators( )
	{
		try
		{
			URLConnection conn = new URL( GlobalSettings.applicationGeneratorDirectoryUrl ).openConnection( );
			BufferedReader in = new BufferedReader( new InputStreamReader( conn.getInputStream( ), "UTF-8" ) );
			String line = null;
			
			LinkedList<JCheckBox> checkBoxes = new LinkedList<JCheckBox>( );
			while( ( line = in.readLine( ) ) != null )
			{
				Pattern pattern = Pattern.compile( "^.*<li><a\\shref=\"([^\\r\\n]+?)\\.java\">([^\\r\\n]+?)\\.java</a></li>.*$" );
				Matcher matcher = pattern.matcher( line );
				matcher.find( );
				
				if( matcher.matches( ) && URLDecoder.decode( matcher.group( 1 ), "UTF-8" ).equals( matcher.group( 2 ) ) )
					checkBoxes.add( new JCheckBox( matcher.group( 2 ) )
					{
						{
							this.setPreferredSize( new Dimension( 375, 23 ) );
							this.setBackground( generatorsPanel.getBackground( ) );
						}
					} );
			}
			
			generatorsPanel.setLayout( new GridLayout( 0, 1 ) );
			generatorsPanel.removeAll( );
			for( JCheckBox checkBox : checkBoxes )
				generatorsPanel.add( checkBox );
			generatorsPanel.updateUI( );
			in.close( );
		}
		catch( Throwable ex )
		{
			DebugUtilities.logException( "An exception occurred while downloading the standard list of generators.", ex );
		}
	}
}
