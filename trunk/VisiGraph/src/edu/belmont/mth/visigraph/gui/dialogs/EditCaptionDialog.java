/**
 * EditCaptionDialog.java
 */
package edu.belmont.mth.visigraph.gui.dialogs;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import edu.belmont.mth.visigraph.resources.*;

/**
 * @author Cameron Behar
 */
public class EditCaptionDialog extends JDialog implements ActionListener
{
	public class Value
	{
		private String	text;
		private double	size;
		
		public Value( String text, double size )
		{
			this.text = text;
			this.size = size;
		}
		
		public double getSize( )
		{
			return this.size;
		}
		
		public String getText( )
		{
			return this.text;
		}
		
		public void setSize( double size )
		{
			this.size = size;
		}
		
		public void setText( String text )
		{
			this.text = text;
		}
	}
	
	private static EditCaptionDialog	dialog;
	private static JLabel				captionTextLabel;
	private static JTextArea			captionTextArea;
	private static JSlider				captionFontSizeSlider;
	private static Value				value;
	
	public static Value showDialog( Component owner, String defaultText, double defaultSize )
	{
		dialog = new EditCaptionDialog( JOptionPane.getFrameForComponent( owner ), defaultText, defaultSize );
		dialog.setVisible( true );
		return value;
	}
	
	private EditCaptionDialog( Frame owner, String defaultText, final double defaultSize )
	{
		super( owner, StringBundle.get( "edit_caption_dialog_title" ), true );
		
		JPanel inputPanel = new JPanel( new GridBagLayout( ) );
		
		inputPanel.add( Box.createRigidArea( new Dimension( 9, 9 ) ), new GridBagConstraints( )
		{
			{
				this.gridx = 0;
				this.gridy = 0;
			}
		} );
		
		captionTextLabel = new JLabel( StringBundle.get( "edit_caption_dialog_text" ) )
		{
			{
				this.setHorizontalAlignment( SwingConstants.LEFT );
				this.setVerticalAlignment( SwingConstants.TOP );
				this.setPreferredSize( new Dimension( this.getPreferredSize( ).width, 20 ) );
			}
		};
		inputPanel.add( captionTextLabel, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 0;
				this.gridy = 1;
			}
		} );
		
		captionTextArea = new JTextArea( defaultText )
		{
			{
				this.setFont( captionTextLabel.getFont( ) );
			}
		};
		JScrollPane captionTextAreaScrollPane = new JScrollPane( captionTextArea )
		{
			{
				this.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
				this.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );
				this.setPreferredSize( new Dimension( 300, 150 ) );
			}
		};
		inputPanel.add( captionTextAreaScrollPane, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 0;
				this.gridy = 2;
				this.gridwidth = 2;
			}
		} );
		
		captionFontSizeSlider = new JSlider( SwingConstants.HORIZONTAL, 8, 100, 8 )
		{
			{
				this.setMajorTickSpacing( 10 );
				this.setPaintTicks( true );
				this.addChangeListener( new ChangeListener( )
				{
					@Override
					public void stateChanged( ChangeEvent e )
					{
						if( captionFontSizeSlider != null )
						{
							captionFontSizeSlider.setToolTipText( captionFontSizeSlider.getValue( ) + "%" );
							captionTextArea.setFont( new Font( captionTextArea.getFont( ).getFamily( ), captionTextArea.getFont( ).getStyle( ), (int) Math.round( Math.pow( captionFontSizeSlider.getValue( ) * 0.3155, 2 ) ) ) );
						}
					}
				} );
				this.setBorder( BorderFactory.createEmptyBorder( 10, 0, -2, 0 ) );
				this.setValue( 9 );
				this.setValue( (int) ( Math.round( Math.sqrt( defaultSize ) / 0.315 ) ) );
			}
		};
		inputPanel.add( captionFontSizeSlider, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 0;
				this.gridy = 3;
				this.gridwidth = 2;
			}
		} );
		
		// Create and initialize the buttons
		final JButton okButton = new JButton( StringBundle.get( "ok_button_text" ) )
		{
			{
				this.setPreferredSize( new Dimension( 80, 28 ) );
				this.setActionCommand( "Ok" );
				this.addActionListener( EditCaptionDialog.this );
				EditCaptionDialog.this.getRootPane( ).setDefaultButton( this );
			}
		};
		final JButton cancelButton = new JButton( StringBundle.get( "cancel_button_text" ) )
		{
			{
				this.setPreferredSize( new Dimension( 80, 28 ) );
				this.addActionListener( EditCaptionDialog.this );
			}
		};
		
		// Lay out the buttons from left to right
		JPanel buttonPanel = new JPanel( )
		{
			{
				this.setLayout( new BoxLayout( this, BoxLayout.LINE_AXIS ) );
				this.setBorder( BorderFactory.createEmptyBorder( 0, 10, 10, 10 ) );
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
				this.gridy = 5;
				this.gridwidth = 3;
			}
		} );
		
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
		this.setResizable( false );
		value = null;
	}
	
	public void actionPerformed( ActionEvent e )
	{
		if( "Ok".equals( e.getActionCommand( ) ) )
			value = new Value( captionTextArea.getText( ), Math.pow( captionFontSizeSlider.getValue( ) * 0.3155, 2.0 ) );
		
		EditCaptionDialog.dialog.setVisible( false );
	}
}
