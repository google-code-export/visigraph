/**
 * NewGraphDialog.java
 */
package edu.belmont.mth.visigraph.gui.dialogs;

import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.resources.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.gui.controls.*;
import edu.belmont.mth.visigraph.models.generators.*;
import edu.belmont.mth.visigraph.models.generators.Generator.*;

/**
 * @author Cameron Behar
 */
public class NewGraphDialog extends JDialog implements ActionListener
{
	private static NewGraphDialog		dialog;
	private static JComboBox			generatorComboBox;
	private static JLabel				generatorParametersLabel;
	private static ValidatingTextField	generatorParametersField;
	private static JButton				loadParametersFromFileButton;
	private static JCheckBox			allowLoopsCheckBox;
	private static JCheckBox			allowDirectedEdgesCheckBox;
	private static JCheckBox			allowMultipleEdgesCheckBox;
	private static JCheckBox			allowCyclesCheckBox;
	private static JButton				okButton;
	private static JButton				cancelButton;
	private static Graph				value;
	private static JFileChooser			fileChooser;
	
	public static Graph showDialog( Component owner )
	{
		dialog = new NewGraphDialog( JOptionPane.getFrameForComponent( owner ) );
		dialog.setVisible( true );
		return value;
	}
	
	private NewGraphDialog( Frame owner )
	{
		super( owner, StringBundle.get( "new_graph_dialog_title" ), true );
		
		fileChooser = new JFileChooser( );
		
		JPanel inputPanel = new JPanel( new GridBagLayout( )
		{
			{
				this.rowHeights = new int[ ] { 9, 28, 28, 28, 28 };
			}
		} );
		
		JLabel generatorLabel = new JLabel( StringBundle.get( "new_graph_dialog_family_label" ) )
		{
			{
				this.setHorizontalAlignment( SwingConstants.RIGHT );
			}
		};
		inputPanel.add( generatorLabel, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 0;
				this.gridy = 1;
			}
		} );
		
		generatorComboBox = new JComboBox( );
		for( Generator generator : GeneratorService.instance.generators )
			generatorComboBox.addItem( generator );
		generatorComboBox.addItemListener( new ItemListener( )
		{
			public void itemStateChanged( ItemEvent e )
			{
				NewGraphDialog.this.generatorChanged( e.getItem( ) );
			}
		} );
		inputPanel.add( generatorComboBox, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 1;
				this.gridy = 1;
				this.gridwidth = 2;
			}
		} );
		
		generatorParametersLabel = new JLabel( StringBundle.get( "new_graph_dialog_parameters_label" ) )
		{
			{
				this.setHorizontalAlignment( SwingConstants.RIGHT );
			}
		};
		inputPanel.add( generatorParametersLabel, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 0;
				this.gridy = 2;
			}
		} );
		
		JPanel parametersPanel = new JPanel( new GridBagLayout( ) );
		generatorParametersField = new ValidatingTextField( 0, ".*" )
		{
			{
				this.setPreferredSize( new Dimension( 315, this.getPreferredSize( ).height ) );
			}
		};
		parametersPanel.add( generatorParametersField );
		loadParametersFromFileButton = new JButton( "..." )
		{
			{
				this.setPreferredSize( new Dimension( 25, generatorParametersField.getPreferredSize( ).height + 2 ) );
				this.setToolTipText( StringBundle.get( "new_graph_dialog_load_parameters_from_file_tooltip" ) );
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						fileChooser.resetChoosableFileFilters( );
						fileChooser.setAcceptAllFileFilterUsed( true );
						fileChooser.setMultiSelectionEnabled( false );
						
						if( fileChooser.showOpenDialog( dialog ) == JFileChooser.APPROVE_OPTION )
							try
							{
								generatorParametersField.setText( "" );
								
								StringBuilder sb = new StringBuilder( );
								
								Scanner in = new Scanner( fileChooser.getSelectedFile( ) );
								while( in.hasNextLine( ) )
									sb.append( in.nextLine( ) ).append( " " );
								in.close( );
								
								generatorParametersField.setText( sb.toString( ) );
							}
							catch( IOException ex )
							{
								DebugUtilities.logException( "An exception occurred while loading parameters from file.", ex );
							}
					}
				} );
			}
		};
		parametersPanel.add( loadParametersFromFileButton );
		
		inputPanel.add( parametersPanel, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 1;
				this.gridy = 2;
				this.gridwidth = 2;
			}
		} );
		
		allowLoopsCheckBox = new JCheckBox( StringBundle.get( "new_graph_dialog_allow_loops_label" ) )
		{
			{
				this.setPreferredSize( new Dimension( 160, this.getPreferredSize( ).height ) );
				this.addItemListener( new ItemListener( )
				{
					@Override
					public void itemStateChanged( ItemEvent e )
					{
						if( allowLoopsCheckBox != null )
						{
							Generator generator = (Generator) generatorComboBox.getSelectedItem( );
							
							if( !( (BooleanRule) generator.getAttribute( Attribute.ARE_CYCLES_ALLOWED ) ).isForced( ) )
								if( allowLoopsCheckBox.isSelected( ) )
								{
									allowCyclesCheckBox.setEnabled( false );
									allowCyclesCheckBox.setSelected( true );
								}
								else if( !allowMultipleEdgesCheckBox.isSelected( ) )
									allowCyclesCheckBox.setEnabled( true );
						}
					}
				} );
			}
		};
		inputPanel.add( allowLoopsCheckBox, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 1;
				this.gridy = 3;
			}
		} );
		
		allowDirectedEdgesCheckBox = new JCheckBox( StringBundle.get( "new_graph_dialog_allow_directed_edges_label" ) )
		{
			{
				this.setPreferredSize( new Dimension( 160, this.getPreferredSize( ).height ) );
			}
		};
		inputPanel.add( allowDirectedEdgesCheckBox, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 2;
				this.gridy = 3;
			}
		} );
		
		allowMultipleEdgesCheckBox = new JCheckBox( StringBundle.get( "new_graph_dialog_allow_multiple_edges_label" ) )
		{
			{
				this.setPreferredSize( new Dimension( 160, this.getPreferredSize( ).height ) );
				this.addItemListener( new ItemListener( )
				{
					@Override
					public void itemStateChanged( ItemEvent e )
					{
						if( allowMultipleEdgesCheckBox != null )
						{
							Generator generator = (Generator) generatorComboBox.getSelectedItem( );
							
							if( !( (BooleanRule) generator.getAttribute( Attribute.ARE_CYCLES_ALLOWED ) ).isForced( ) )
								if( allowMultipleEdgesCheckBox.isSelected( ) )
								{
									allowCyclesCheckBox.setEnabled( false );
									allowCyclesCheckBox.setSelected( true );
								}
								else if( !allowLoopsCheckBox.isSelected( ) )
									allowCyclesCheckBox.setEnabled( true );
						}
					}
				} );
			}
		};
		inputPanel.add( allowMultipleEdgesCheckBox, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 1;
				this.gridy = 4;
			}
		} );
		
		allowCyclesCheckBox = new JCheckBox( StringBundle.get( "new_graph_dialog_allow_cycles_label" ) )
		{
			{
				this.setPreferredSize( new Dimension( 160, this.getPreferredSize( ).height ) );
				this.addItemListener( new ItemListener( )
				{
					@Override
					public void itemStateChanged( ItemEvent e )
					{
						if( allowCyclesCheckBox != null )
						{
							Generator generator = (Generator) generatorComboBox.getSelectedItem( );
							
							if( !( (BooleanRule) generator.getAttribute( Attribute.ARE_LOOPS_ALLOWED ) ).isForced( ) )
								if( allowCyclesCheckBox.isSelected( ) )
									allowLoopsCheckBox.setEnabled( true );
								else
								{
									allowLoopsCheckBox.setEnabled( false );
									allowLoopsCheckBox.setSelected( false );
								}
							
							if( !( (BooleanRule) generator.getAttribute( Attribute.ARE_MULTIPLE_EDGES_ALLOWED ) ).isForced( ) )
								if( allowCyclesCheckBox.isSelected( ) )
									allowMultipleEdgesCheckBox.setEnabled( true );
								else
								{
									allowMultipleEdgesCheckBox.setEnabled( false );
									allowMultipleEdgesCheckBox.setSelected( false );
								}
						}
					}
				} );
			}
		};
		inputPanel.add( allowCyclesCheckBox, new GridBagConstraints( )
		{
			{
				this.fill = GridBagConstraints.HORIZONTAL;
				this.gridx = 2;
				this.gridy = 4;
			}
		} );
		
		// Create and initialize the buttons
		okButton = new JButton( StringBundle.get( "ok_button_text" ) )
		{
			{
				this.setPreferredSize( new Dimension( 80, 28 ) );
				this.setActionCommand( "Ok" );
				this.addActionListener( NewGraphDialog.this );
				NewGraphDialog.this.getRootPane( ).setDefaultButton( this );
			}
		};
		cancelButton = new JButton( StringBundle.get( "cancel_button_text" ) )
		{
			{
				this.setPreferredSize( new Dimension( 80, 28 ) );
				this.addActionListener( NewGraphDialog.this );
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
		size.width = Math.max( size.width, 400 ) + 40;
		size.height += 40;
		this.setPreferredSize( size );
		
		this.pack( );
		this.setResizable( false );
		this.setLocationRelativeTo( owner );
		this.generatorChanged( generatorComboBox.getSelectedObjects( )[0] );
		value = null;
	}
	
	public void actionPerformed( ActionEvent e )
	{
		if( "Ok".equals( e.getActionCommand( ) ) )
		{
			if( !generatorParametersField.isValid( ) )
				return;
			
			Generator generator = (Generator) generatorComboBox.getSelectedObjects( )[0];
			value = generator.generate( generatorParametersField.getText( ), allowLoopsCheckBox.isSelected( ), allowDirectedEdgesCheckBox.isSelected( ), allowMultipleEdgesCheckBox.isSelected( ), allowCyclesCheckBox.isSelected( ), this );
		}
		
		NewGraphDialog.dialog.setVisible( false );
	}
	
	private void generatorChanged( Object item )
	{
		if( item instanceof Generator )
		{
			Generator generator = (Generator) item;
			
			generatorParametersLabel.setEnabled( (Boolean) generator.getAttribute( Attribute.ARE_PARAMETERS_ALLOWED ) );
			generatorParametersField.setEnabled( (Boolean) generator.getAttribute( Attribute.ARE_PARAMETERS_ALLOWED ) );
			loadParametersFromFileButton.setEnabled( (Boolean) generator.getAttribute( Attribute.ARE_PARAMETERS_ALLOWED ) );
			
			allowLoopsCheckBox.setSelected( ( (BooleanRule) generator.getAttribute( Attribute.ARE_LOOPS_ALLOWED ) ).isTrue( ) );
			allowLoopsCheckBox.setEnabled( !( (BooleanRule) generator.getAttribute( Attribute.ARE_LOOPS_ALLOWED ) ).isForced( ) );
			
			allowDirectedEdgesCheckBox.setSelected( ( (BooleanRule) generator.getAttribute( Attribute.ARE_DIRECTED_EDGES_ALLOWED ) ).isTrue( ) );
			allowDirectedEdgesCheckBox.setEnabled( !( (BooleanRule) generator.getAttribute( Attribute.ARE_DIRECTED_EDGES_ALLOWED ) ).isForced( ) );
			
			allowMultipleEdgesCheckBox.setSelected( ( (BooleanRule) generator.getAttribute( Attribute.ARE_MULTIPLE_EDGES_ALLOWED ) ).isTrue( ) );
			allowMultipleEdgesCheckBox.setEnabled( !( (BooleanRule) generator.getAttribute( Attribute.ARE_MULTIPLE_EDGES_ALLOWED ) ).isForced( ) );
			
			allowCyclesCheckBox.setSelected( ( (BooleanRule) generator.getAttribute( Attribute.ARE_CYCLES_ALLOWED ) ).isTrue( ) );
			allowCyclesCheckBox.setEnabled( !( (BooleanRule) generator.getAttribute( Attribute.ARE_CYCLES_ALLOWED ) ).isForced( ) );
			
			generatorParametersField.setText( "" );
			if( (Boolean) generator.getAttribute( Attribute.ARE_PARAMETERS_ALLOWED ) )
			{
				generatorParametersField.setValidatingExpression( (String) generator.getAttribute( Attribute.PARAMETERS_VALIDATION_EXPRESSION ) );
				generatorParametersField.setToolTipText( (String) generator.getAttribute( Attribute.PARAMETERS_DESCRIPTION ) );
				generatorParametersField.requestFocus( );
			}
		}
	}
}
