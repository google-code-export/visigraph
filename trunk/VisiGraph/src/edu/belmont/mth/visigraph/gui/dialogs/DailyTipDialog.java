/**
 * DailyTipDialog.java
 */
package edu.belmont.mth.visigraph.gui.dialogs;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.WebUtilities;
import edu.belmont.mth.visigraph.resources.*;

/**
 * @author Cameron Behar
 */
public class DailyTipDialog extends JDialog implements ActionListener
{
	private static DailyTipDialog	dialog;
	private static JEditorPane		dailyTipPane;
	private static JCheckBox		showOnStartupCheckBox;
	
	public static void showDialog( Component owner )
	{
		dialog = new DailyTipDialog( JOptionPane.getFrameForComponent( owner ) );
		dialog.setVisible( true );
	}
	
	private DailyTipDialog( Frame owner )
	{
		super( owner, StringBundle.get( "daily_tip_dialog_title" ), true );
		this.setResizable( false );
		
		dailyTipPane = new JEditorPane( "text/html", "<p>Loading...</p>" )
		{
			{
				this.setEditable( false );
				this.setHighlighter( null );
				this.setAutoscrolls( true );
				this.setMargin( new Insets( 5, 15, 15, 15 ) );
				this.addHyperlinkListener( new HyperlinkListener( )
				{
					@Override
					public void hyperlinkUpdate( HyperlinkEvent event )
					{
						if( event.getEventType( ) == HyperlinkEvent.EventType.ACTIVATED )
						{
							if( event.getDescription( ).equals( "previous" ) || event.getDescription( ).equals( "next" ) )
								DailyTipDialog.this.refreshDailyTip( event.getDescription( ).equals( "next" ) );
							else
								WebUtilities.launchBrowser( event.getDescription( ) );
						}
					}
				} );
			}
		};
		JScrollPane editorScrollPane = new JScrollPane( dailyTipPane )
		{
			{
				this.setPreferredSize( new Dimension( 400, 247 ) ); // Golden ratio for aesthetic purposes
			}
		};
		this.refreshDailyTip( true );
		
		// Create and initialize the buttons
		showOnStartupCheckBox = new JCheckBox( StringBundle.get( "daily_tip_dialog_show_on_startup_label" ) )
		{
			{
				this.setBorder( BorderFactory.createEmptyBorder( 5, 0, 0, 0 ) );
				this.setSelected( UserSettings.instance.showDailyTipsOnStartup.get( ) );
			}
		};
		final JButton okButton = new JButton( StringBundle.get( "ok_button_text" ) )
		{
			{
				this.setPreferredSize( new Dimension( 80, 28 ) );
				this.setActionCommand( "Ok" );
				this.addActionListener( DailyTipDialog.this );
				DailyTipDialog.this.getRootPane( ).setDefaultButton( this );
			}
		};
		
		// Lay out the buttons from left to right
		JPanel buttonPanel = new JPanel( )
		{
			{
				this.setLayout( new BoxLayout( this, BoxLayout.LINE_AXIS ) );
				this.setBorder( BorderFactory.createEmptyBorder( -2, 9, 9, 13 ) );
				this.add( DailyTipDialog.showOnStartupCheckBox );
				this.add( Box.createHorizontalGlue( ) );
				this.add( okButton );
			}
		};
		
		// Put everything together, using the content pane's BorderLayout
		Container contentPanel = this.getContentPane( );
		contentPanel.setLayout( new BorderLayout( 9, 9 ) );
		contentPanel.add( editorScrollPane, BorderLayout.CENTER );
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
		if( showOnStartupCheckBox.isSelected( ) != UserSettings.instance.showDailyTipsOnStartup.get( ) )
		{
			UserSettings.instance.showDailyTipsOnStartup.set( showOnStartupCheckBox.isSelected( ) );
			UserSettings.instance.saveToFile( );
		}
		
		DailyTipDialog.dialog.setVisible( false );
	}
	
	private void refreshDailyTip( boolean increment )
	{
		String[ ] dailyTips = StringBundle.get( "daily_tip_dialog_tip_contents" ).split( "\\|" );
		UserSettings.instance.dailyTipIndex.set( ( UserSettings.instance.dailyTipIndex.get( ) + dailyTips.length + ( increment ? 1 : -1 ) ) % dailyTips.length );
		UserSettings.instance.saveToFile( );
		
		StringBuilder sb = new StringBuilder( );
		
		// Title
		sb.append( String.format( "<h1>%s</h1>", StringBundle.get( "daily_tip_dialog_tip_title" ) ) );
		
		// Tip contents
		sb.append( String.format( "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;%s</p>", dailyTips[UserSettings.instance.dailyTipIndex.get( )].replace( "\n", "</p><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" ) ) );
		
		// Previous / Next hyperlinks
		sb.append( String.format( "<table width=\"100%%\"><tr/></tr><tr><td><a href=\"previous\">\u2190 %s</a></td><td align=\"right\"><a href=\"next\">%s \u2192</a></td></tr></table>", StringBundle.get( "daily_tip_dialog_previous_button_text" ), StringBundle.get( "daily_tip_dialog_next_button_text" ) ) );
		
		// Set text and scroll to top
		dailyTipPane.setText( sb.toString( ) );
		dailyTipPane.setCaretPosition( 0 );
	}
}
