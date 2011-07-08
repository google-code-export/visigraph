/**
 * DebugDialog.java
 */
package edu.belmont.mth.visigraph.gui.dialogs;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.text.html.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.resources.*;
import edu.belmont.mth.visigraph.utilities.*;

/**
 * @author Cameron Behar
 */
public class DebugDialog extends JDialog implements ActionListener
{
	private static DebugDialog	dialog;
	
	public static void showDialog( Component owner )
	{
		dialog = new DebugDialog( JOptionPane.getFrameForComponent( owner ) );
		dialog.setVisible( true );
	}
	
	private DebugDialog( Frame owner )
	{
		super( owner, GlobalSettings.applicationName + " debug console", true );
		this.setResizable( false );
		
		JPanel inputPanel = new JPanel( new GridBagLayout( ) )
		{
			{
				this.setBorder( BorderFactory.createEmptyBorder( 8, 0, 8, 0 ) );
			}
		};
		
		final HTMLEditorKit html = new HTMLEditorKit( );
		JEditorPane logPane = new JEditorPane( )
		{
			{
				this.setEditorKit( html );
				this.setBackground( Color.white );
				this.setEditable( false );
			}
		};
		
		try
		{
			html.read( new StringReader( DebugUtilities.getLog( ) ), logPane.getDocument( ), 0 );
		}
		catch( Exception e )
		{
			DebugUtilities.logException( "An exception occurred while loading the debug console.", e );
			e.printStackTrace( );
		}
		
		inputPanel.add( new JScrollPane( logPane )
		{
			{
				this.setPreferredSize( new Dimension( 700, 500 ) );
			}
		}, new GridBagConstraints( )
		{
			{
				this.gridx = 0;
				this.gridy = 0;
			}
		} );
		
		final JButton closeButton = new JButton( StringBundle.get( "close_button_text" ) )
		{
			{
				this.setPreferredSize( new Dimension( 80, 28 ) );
				this.setActionCommand( "Close" );
				this.addActionListener( DebugDialog.this );
				DebugDialog.this.getRootPane( ).setDefaultButton( this );
			}
		};
		
		JPanel buttonPanel = new JPanel( )
		{
			{
				this.setLayout( new BoxLayout( this, BoxLayout.LINE_AXIS ) );
				this.setBorder( BorderFactory.createEmptyBorder( -2, 9, 9, 13 ) );
				this.add( Box.createHorizontalGlue( ) );
				this.add( closeButton );
			}
		};
		
		// Put everything together, using the content pane's BorderLayout
		Container contentPanel = this.getContentPane( );
		contentPanel.setLayout( new BorderLayout( 9, 0 ) );
		contentPanel.add( inputPanel, BorderLayout.CENTER );
		contentPanel.add( buttonPanel, BorderLayout.PAGE_END );
		
		Dimension size = this.getPreferredSize( );
		size.width += 40;
		size.height += 40;
		this.setPreferredSize( size );
		
		this.pack( );
		this.setLocationRelativeTo( owner );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		if( e.getActionCommand( ).equals( "Close" ) )
			DebugDialog.dialog.setVisible( false );
	}
}
