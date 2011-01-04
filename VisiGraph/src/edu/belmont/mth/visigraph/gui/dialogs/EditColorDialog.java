/**
 * EditColorDialog.java
 */
package edu.belmont.mth.visigraph.gui.dialogs;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import edu.belmont.mth.visigraph.resources.*;
import edu.belmont.mth.visigraph.settings.UserSettings;

/**
 * @author Cameron Behar
 */
public class EditColorDialog extends JDialog implements ActionListener
{
	private static EditColorDialog	dialog;
	private static JButton			okButton;
	private static JLabel			colorTextLabel;
	private static Integer			tempValue;
	private static Integer			value;
	
	public static Integer showDialog( Component owner, Integer defaultColor )
	{
		dialog = new EditColorDialog( JOptionPane.getFrameForComponent( owner ), defaultColor );
		dialog.setVisible( true );
		return value;
	}
	
	private EditColorDialog( Frame owner, Integer defaultColor )
	{
		super( owner, StringBundle.get( "edit_color_dialog_title" ), true );
		
		JPanel inputPanel = new JPanel( new GridBagLayout( ) );
		
		inputPanel.add( Box.createRigidArea( new Dimension( 9, 9 ) ), new GridBagConstraints( )
		{
			{
				this.gridx = 0;
				this.gridy = 0;
			}
		} );
		
		colorTextLabel = new JLabel( StringBundle.get( "edit_color_dialog_text" ) )
		{
			{
				this.setHorizontalAlignment( SwingConstants.LEFT );
				this.setVerticalAlignment( SwingConstants.TOP );
				this.setPreferredSize( new Dimension( this.getPreferredSize( ).width, 20 ) );
			}
		};
		inputPanel.add( colorTextLabel, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 0;
				this.gridy = 1;
			}
		} );
		
		final ButtonGroup radioGroup = new ButtonGroup( );
		final JPanel radioPanel = new JPanel( new GridLayout( 0, (int) Math.ceil( UserSettings.instance.elementColors.size( ) / 5.0 ), 15, 0 ) );
		final ActionListener radioActionListener = new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent event )
			{
				EditColorDialog.tempValue = Integer.parseInt( event.getActionCommand( ) );
				okButton.setEnabled( true );
			}
		};
		
		JPanel defaultSwatchPanel = new JPanel( new GridLayout( 0, 2, 0, 0 ) )
		{
			{
				radioPanel.add( this );
			}
		};
		
		defaultSwatchPanel.add( new JRadioButton( "(-)", defaultColor != null && defaultColor == -1 )
		{
			{
				radioGroup.add( this );
				this.setActionCommand( Integer.toString( -1 ) );
				this.addActionListener( radioActionListener );
			}
		} );
		
		BufferedImage defaultSwatch = new BufferedImage( 56, 14, BufferedImage.TYPE_INT_ARGB );
		Graphics2D defaultG2D = (Graphics2D) defaultSwatch.getGraphics( );
		defaultG2D.setColor( UserSettings.instance.getVertexColor( -1 ) );
		defaultG2D.fillRect( 0, 0, 56, 14 );
		defaultG2D.setColor( UserSettings.instance.getEdgeColor( -1 ) );
		defaultG2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		defaultG2D.fillPolygon( new int[ ] { 0, 0, 56 }, new int[ ] { 0, 14, 14 }, 3 );
		defaultG2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
		defaultG2D.setColor( new Color( 0, 0, 0, 127 ) );
		defaultG2D.drawRect( 0, 0, 55, 13 );
		defaultG2D.dispose( );
		defaultSwatchPanel.add( new JLabel( new ImageIcon( defaultSwatch ) ) );
		
		for( int i = 0; i < UserSettings.instance.elementColors.size( ); ++i )
		{
			final JPanel swatchPanel = new JPanel( new GridLayout( 0, 2, 0, 0 ) )
			{
				{
					radioPanel.add( this );
				}
			};
			
			JRadioButton radioButton = new JRadioButton( "(" + i + ")", defaultColor != null && defaultColor == i )
			{
				{
					radioGroup.add( this );
					swatchPanel.add( this );
					this.addActionListener( radioActionListener );
				}
			};
			radioButton.setActionCommand( Integer.toString( i ) );
			radioButton.setForeground( UserSettings.instance.getVertexColor( i ) );
			
			BufferedImage swatch = new BufferedImage( 56, 14, BufferedImage.TYPE_INT_ARGB );
			Graphics2D g2D = (Graphics2D) swatch.getGraphics( );
			g2D.setColor( UserSettings.instance.getVertexColor( i ) );
			g2D.fillRect( 0, 0, 56, 14 );
			g2D.setColor( new Color( 0, 0, 0, 127 ) );
			g2D.drawRect( 0, 0, 55, 13 );
			g2D.dispose( );
			swatchPanel.add( new JLabel( new ImageIcon( swatch ) ) );
		}
		
		inputPanel.add( radioPanel, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 0;
				this.gridy = 2;
				this.gridwidth = 2;
			}
		} );
		
		// Create and initialize the buttons
		okButton = new JButton( StringBundle.get( "ok_button_text" ) )
		{
			{
				this.setPreferredSize( new Dimension( 80, 28 ) );
				this.setActionCommand( "Ok" );
				this.addActionListener( EditColorDialog.this );
				EditColorDialog.this.getRootPane( ).setDefaultButton( this );
			}
		};
		okButton.setEnabled( defaultColor != null && defaultColor >= -1 && defaultColor < UserSettings.instance.elementColors.size( ) );
		final JButton cancelButton = new JButton( StringBundle.get( "cancel_button_text" ) )
		{
			{
				this.setPreferredSize( new Dimension( 80, 28 ) );
				this.addActionListener( EditColorDialog.this );
			}
		};
		
		// Lay out the buttons from left to right
		JPanel buttonPanel = new JPanel( )
		{
			{
				this.setLayout( new BoxLayout( this, BoxLayout.LINE_AXIS ) );
				this.setBorder( BorderFactory.createEmptyBorder( 3, 10, 10, 10 ) );
				this.add( Box.createHorizontalGlue( ) );
				this.add( okButton );
				this.add( Box.createRigidArea( new Dimension( 10, 0 ) ) );
				this.add( cancelButton );
			}
		};
		inputPanel.add( buttonPanel, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 0;
				this.gridy = 3;
				this.gridwidth = 3;
			}
		} );
		
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
		this.setResizable( false );
		tempValue = defaultColor;
		value = null;
	}
	
	public void actionPerformed( ActionEvent e )
	{
		if( "Ok".equals( e.getActionCommand( ) ) )
			value = tempValue;
		
		EditColorDialog.dialog.setVisible( false );
	}
}
