/**
 * AboutDialog.java
 */
package edu.belmont.mth.visigraph.gui.dialogs;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.resources.*;

/**
 * @author Cameron Behar
 */
public class AboutDialog extends JDialog implements ActionListener
{
	private static AboutDialog	dialog;
	
	public static void showDialog( Component owner )
	{
		dialog = new AboutDialog( JOptionPane.getFrameForComponent( owner ) );
		dialog.setVisible( true );
	}
	
	private AboutDialog( Frame owner )
	{
		super( owner, String.format( StringBundle.get( "about_dialog_title" ), GlobalSettings.applicationName ), true );
		this.setResizable( false );
		
		JPanel inputPanel = new JPanel( new GridBagLayout( ) )
		{
			{
				this.setBorder( BorderFactory.createEmptyBorder( 8, 0, 8, 0 ) );
			}
		};
		
		JLabel appIconLabel = new JLabel( ImageIconBundle.get( "app_icon_128x128" ) )
		{
			{
				this.setBorder( BorderFactory.createEmptyBorder( 0, 0, 0, 24 ) );
			}
		};
		inputPanel.add( appIconLabel, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 0;
				this.gridy = 0;
				this.gridheight = 13;
			}
		} );
		
		JLabel appTitleLabel = new JLabel( GlobalSettings.applicationName )
		{
			{
				this.setFont( new Font( this.getFont( ).getFamily( ), Font.BOLD, 18 ) );
			}
		};
		inputPanel.add( appTitleLabel, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 1;
				this.gridy = 1;
			}
		} );
		
		JLabel appByLabel = new JLabel( String.format( StringBundle.get( "about_dialog_by_label" ), GlobalSettings.applicationAuthor ) )
		{
			{
				this.setBorder( BorderFactory.createEmptyBorder( 0, 10, 0, 0 ) );
			}
		};
		inputPanel.add( appByLabel, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 1;
				this.gridy = 2;
			}
		} );
		
		JLabel appBuildLabel = new JLabel( String.format( StringBundle.get( "about_dialog_version_label" ), GlobalSettings.applicationVersion ) )
		{
			{
				this.setBorder( BorderFactory.createEmptyBorder( 0, 10, 0, 0 ) );
			}
		};
		inputPanel.add( appBuildLabel, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 1;
				this.gridy = 3;
			}
		} );
		
		inputPanel.add( Box.createVerticalStrut( 15 ), new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 1;
				this.gridy = 4;
			}
		} );
		
		JLabel appDescriptionLabel = new JLabel( GlobalSettings.applicationDescription )
		{
			{
				this.setBorder( BorderFactory.createEmptyBorder( 0, 10, 0, 0 ) );
			}
		};
		inputPanel.add( appDescriptionLabel, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 1;
				this.gridy = 5;
			}
		} );
		
		inputPanel.add( Box.createVerticalStrut( 5 ), new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 1;
				this.gridy = 6;
			}
		} );
		
		JLabel appCopyrightLine0Label = new JLabel( String.format( StringBundle.get( "about_dialog_copyright_line_0" ), Calendar.getInstance( ).get( Calendar.YEAR ), GlobalSettings.applicationAuthor, GlobalSettings.applicationName ) )
		{
			{
				this.setBorder( BorderFactory.createEmptyBorder( 0, 10, 0, 0 ) );
			}
		};
		inputPanel.add( appCopyrightLine0Label, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 1;
				this.gridy = 7;
			}
		} );
		
		JLabel appCopyrightLine1Label = new JLabel( StringBundle.get( "about_dialog_copyright_line_1" ) );
		inputPanel.add( appCopyrightLine1Label, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 1;
				this.gridy = 8;
			}
		} );
		
		JLabel appCopyrightLine2Label = new JLabel( String.format( StringBundle.get( "about_dialog_copyright_line_2" ), GlobalSettings.applicationWebsite ) );
		inputPanel.add( appCopyrightLine2Label, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 1;
				this.gridy = 9;
			}
		} );
		
		inputPanel.add( Box.createVerticalStrut( 5 ), new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 1;
				this.gridy = 10;
			}
		} );
		
		JLabel appIncludesLine0Label = new JLabel( StringBundle.get( "about_dialog_includes_line_0" ) )
		{
			{
				this.setBorder( BorderFactory.createEmptyBorder( 0, 10, 0, 0 ) );
			}
		};
		inputPanel.add( appIncludesLine0Label, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 1;
				this.gridy = 11;
			}
		} );
		
		JLabel appIncludesLine1Label = new JLabel( StringBundle.get( "about_dialog_includes_line_1" ) );
		inputPanel.add( appIncludesLine1Label, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 1;
				this.gridy = 12;
			}
		} );
		
		// Create and initialize the buttons
		final JButton debugButton = new JButton( StringBundle.get( "debug_button_text" ) )
		{
			{
				this.setPreferredSize( new Dimension( 90, 28 ) );
				this.setActionCommand( "Debug" );
				this.addActionListener( AboutDialog.this );
			}
		};
		final JButton closeButton = new JButton( StringBundle.get( "close_button_text" ) )
		{
			{
				this.setPreferredSize( new Dimension( 80, 28 ) );
				this.setActionCommand( "Close" );
				this.addActionListener( AboutDialog.this );
				AboutDialog.this.getRootPane( ).setDefaultButton( this );
			}
		};
		
		// Lay out the buttons from left to right
		JPanel buttonPanel = new JPanel( )
		{
			{
				this.setLayout( new BoxLayout( this, BoxLayout.LINE_AXIS ) );
				this.setBorder( BorderFactory.createEmptyBorder( -2, 9, 9, 13 ) );
				this.add( debugButton );
				this.add( Box.createHorizontalGlue( ) );
				this.add( closeButton );
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
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		if( e.getActionCommand( ).equals( "Debug" ) )
			DebugDialog.showDialog( AboutDialog.this.getOwner( ) );
		else if( e.getActionCommand( ).equals( "Close" ) )
			AboutDialog.dialog.setVisible( false );
	}
}
