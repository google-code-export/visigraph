/**
 * PreferencesDialog.java
 */
package edu.belmont.mth.visigraph.gui.dialogs;

import java.io.*;
import java.awt.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.regex.*;
import javax.swing.border.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.resources.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.gui.controls.*;

/**
 * @author Cameron Behar
 */
public class PreferencesDialog extends JDialog implements ActionListener
{
	private class FieldLabel extends JLabel
	{
		public FieldLabel( String label )
		{
			super.setMinimumSize( new Dimension( super.getMinimumSize( ).width, 20 ) );
			super.setText( label );
		}
	}
	
	private class Header extends JLabel
	{
		public Header( String label )
		{
			Font font = super.getFont( );
			super.setFont( new Font( font.getFontName( ), Font.BOLD, font.getSize( ) ) );
			super.setMinimumSize( new Dimension( 100, 32 ) );
			super.setBorder( BorderFactory.createEmptyBorder( 0, 9, 0, 0 ) );
			super.setText( label );
		}
	}
	
	private class LocaleWrapper implements Comparable<LocaleWrapper>
	{
		final Locale	locale;
		
		public LocaleWrapper( Locale locale )
		{
			this.locale = locale;
		}
		
		@Override
		public int compareTo( LocaleWrapper other )
		{
			return this.toString( ).compareTo( other.toString( ) );
		}
		
		@Override
		public String toString( )
		{
			return this.locale.getDisplayName( this.locale );
		}
	}
	
	private static PreferencesDialog	dialog;
	private static String				value;
	private static ValidatingTextField	defaultVertexWeightTextField;
	private static ValidatingTextField	defaultVertexColorTextField;
	private static JTextField			defaultVertexPrefixTextField;
	private static ValidatingTextField	defaultVertexRadiusTextField;
	private static JCheckBox			defaultVertexIsSelectedCheckBox;
	private static ValidatingTextField	defaultEdgeWeightTextField;
	private static ValidatingTextField	defaultEdgeColorTextField;
	private static JTextField			defaultEdgePrefixTextField;
	private static ValidatingTextField	defaultEdgeThicknessTextField;
	private static ValidatingTextField	defaultEdgeHandleRadiusRatioTextField;
	private static ValidatingTextField	defaultLoopDiameterTextField;
	private static JCheckBox			defaultEdgeIsSelectedCheckBox;
	private static JTextField			defaultCaptionTextTextField;
	private static ValidatingTextField	defaultCaptionFontSizeTextField;
	private static JCheckBox			defaultCaptionIsSelectedCheckBox;
	private static JCheckBox			defaultShowVertexWeightsCheckBox;
	private static JCheckBox			defaultShowVertexLabelsCheckBox;
	private static JCheckBox			defaultShowEdgeHandlesCheckBox;
	private static JCheckBox			defaultShowEdgeWeightsCheckBox;
	private static JCheckBox			defaultShowEdgeLabelsCheckBox;
	private static JCheckBox			defaultShowCaptionsCheckBox;
	private static JCheckBox			defaultShowCaptionHandlesCheckBox;
	private static JCheckBox			defaultShowCaptionEditorsCheckBox;
	private static ColorPicker			graphBackgroundColorPicker;
	private static ColorPicker			selectionBoxFillColorPicker;
	private static ColorPicker			selectionBoxLineColorPicker;
	private static ColorPicker			vertexLineColorPicker;
	private static ColorPicker			uncoloredVertexFillColorPicker;
	private static ColorPicker			selectedVertexFillColorPicker;
	private static ColorPicker			selectedVertexLineColorPicker;
	private static ColorPicker			draggingEdgeColorPicker;
	private static ColorPicker			edgeHandleColorPicker;
	private static ColorPicker			uncoloredEdgeLineColorPicker;
	private static ColorPicker			selectedEdgeColorPicker;
	private static ColorPicker			selectedEdgeHandleColorPicker;
	private static ColorPicker			captionTextColorPicker;
	private static ColorPicker			captionButtonFillColorPicker;
	private static ColorPicker			captionButtonLineColorPicker;
	private static ColorPicker			selectedCaptionLineColorPicker;
	private static JPanel				elementColorsPanel;
	private static JButton				addButton;
	private static JButton				removeButton;
	private static ValidatingTextField	vertexClickMarginTextField;
	private static ValidatingTextField	edgeHandleClickMarginTextField;
	private static ValidatingTextField	captionHandleClickMarginTextField;
	private static ValidatingTextField	captionEditorClickMarginTextField;
	private static ValidatingTextField	panDecelerationFactorTextField;
	private static JCheckBox			panOnDoubleClickCheckBox;
	private static JCheckBox			deselectVertexWithNewEdgeCheckBox;
	private static ValidatingTextField	zoomInFactorTextField;
	private static ValidatingTextField	zoomOutFactorTextField;
	private static ValidatingTextField	maximumZoomFactorTextField;
	private static ValidatingTextField	zoomGraphPaddingTextField;
	private static ValidatingTextField	scrollIncrementZoomTextField;
	private static ValidatingTextField	arrangeCircleRadiusMultiplierTextField;
	private static ValidatingTextField	arrangeGridSpacingTextField;
	private static ValidatingTextField	autoArrangeAttractiveForceTextField;
	private static ValidatingTextField	autoArrangeRepulsiveForceTextField;
	private static ValidatingTextField	autoArrangeDecelerationFactorTextField;
	private static ValidatingTextField	arrangeContractFactorTextField;
	private static ValidatingTextField	arrangeExpandFactorTextField;
	private static ValidatingTextField	undoLoggingIntervalTextField;
	private static ValidatingTextField	undoLoggingMaximumTextField;
	private static JCheckBox			useAntiAliasingCheckBox;
	private static JCheckBox			usePureStrokeCheckBox;
	private static JCheckBox			useBicubicInterpolationCheckBox;
	private static JCheckBox			useFractionalMetricsCheckBox;
	private static ValidatingTextField	vertexWeightPrecisionTextField;
	private static ValidatingTextField	edgeWeightPrecisionTextField;
	private static ValidatingTextField	mainWindowSizeTextField;
	private static ValidatingTextField	scriptLibraryWindowSizeTextField;
	private static ValidatingTextField	graphWindowSizeTextField;
	private static ValidatingTextField	cascadeWindowOffsetTextField;
	private static JComboBox			languageComboBox;
	private static JTextField			defaultGraphNameTextField;
	private static ValidatingTextField	directedEdgeArrowRatioTextField;
	private static ValidatingTextField	arrowKeyIncrementTextField;
	private static ValidatingTextField	edgeSnapMarginRatioTextField;
	private static ValidatingTextField	areCloseDistanceTextField;
	private static ValidatingTextField	paintToolMenuDelayTextField;
	
	public static String showDialog( Component owner )
	{
		dialog = new PreferencesDialog( JOptionPane.getFrameForComponent( owner ) );
		dialog.setVisible( true );
		return value;
	}
	
	private final int[ ]	columnWidths	= new int[ ] { 175, 165, 160 };
	private final Insets	fieldMargin		= new Insets( 2, 2, 2, 7 );
	
	private PreferencesDialog( Frame owner )
	{
		super( owner, StringBundle.get( "preferences_dialog_title" ), true );
		this.setResizable( false );
		
		JPanel inputPanel = new JPanel( new BorderLayout( ) );
		
		JTabbedPane tabPanel = new JTabbedPane( )
		{
			{
				this.setPreferredSize( new Dimension( 620, 520 ) );
				this.setBorder( new EmptyBorder( 7, 7, 0, 7 ) );
				
				this.addTab( StringBundle.get( "preferences_dialog_defaults_tab" ), PreferencesDialog.this.initializeDefaultsPanel( ) );
				this.addTab( StringBundle.get( "preferences_dialog_appearances_tab" ), PreferencesDialog.this.initializeAppearancesPanel( ) );
				this.addTab( StringBundle.get( "preferences_dialog_under_the_hood_tab" ), PreferencesDialog.this.initializeUnderTheHoodPanel( ) );
			}
		};
		inputPanel.add( tabPanel, BorderLayout.CENTER );
		
		this.loadPreferences( );
		
		// Create and initialize the buttons
		final JButton resetButton = new JButton( StringBundle.get( "preferences_dialog_reset_to_defaults_button_text" ) )
		{
			{
				this.setMinimumSize( new Dimension( 120, 28 ) );
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						PreferencesDialog.this.resetPreferences( );
					}
				} );
			}
		};
		final JButton okButton = new JButton( StringBundle.get( "ok_button_text" ) )
		{
			{
				this.setPreferredSize( new Dimension( 80, 28 ) );
				this.setActionCommand( "Ok" );
				this.addActionListener( PreferencesDialog.this );
				PreferencesDialog.this.getRootPane( ).setDefaultButton( this );
			}
		};
		final JButton cancelButton = new JButton( StringBundle.get( "cancel_button_text" ) )
		{
			{
				this.setPreferredSize( new Dimension( 80, 28 ) );
				this.addActionListener( PreferencesDialog.this );
			}
		};
		
		// Lay out the buttons from left to right
		JPanel buttonPanel = new JPanel( )
		{
			{
				this.setLayout( new BoxLayout( this, BoxLayout.LINE_AXIS ) );
				this.setBorder( BorderFactory.createEmptyBorder( -2, 9, 9, 13 ) );
				this.add( resetButton );
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
		value = null;
	}
	
	public void actionPerformed( ActionEvent e )
	{
		if( e.getActionCommand( ).equals( "Ok" ) )
		{
			if( !this.validatePreferences( ) )
				JOptionPane.showMessageDialog( this, StringBundle.get( "preferences_dialog_invalid_values_error_message" ), StringBundle.get( "preferences_dialog_invalid_values_error_title" ), JOptionPane.ERROR_MESSAGE );
			else
			{
				this.savePreferences( );
				value = "saved";
				PreferencesDialog.dialog.setVisible( false );
			}
		}
		else
			PreferencesDialog.dialog.setVisible( false );
	}
	
	private JScrollPane initializeAppearancesPanel( )
	{
		final JPanel panel = new JPanel( new GridBagLayout( ) );
		if( System.getProperty( "os.name" ).startsWith( "Windows" ) )
			panel.setBackground( Color.white );
		
		Header graphColorsHeader = new Header( StringBundle.get( "preferences_dialog_graph_colors_heading" ) );
		FieldLabel graphBackgroundLabel = new FieldLabel( StringBundle.get( "preferences_dialog_graph_background_label" ) );
		graphBackgroundColorPicker = new ColorPicker( );
		FieldLabel selectionBoxFillLabel = new FieldLabel( StringBundle.get( "preferences_dialog_graph_selection_box_fill_label" ) );
		selectionBoxFillColorPicker = new ColorPicker( );
		FieldLabel selectionBoxLineLabel = new FieldLabel( StringBundle.get( "preferences_dialog_graph_selection_box_line_label" ) );
		selectionBoxLineColorPicker = new ColorPicker( );
		
		Header vertexColorsHeader = new Header( StringBundle.get( "preferences_dialog_vertex_colors_heading" ) );
		FieldLabel vertexLineLabel = new FieldLabel( StringBundle.get( "preferences_dialog_vertex_line_label" ) );
		vertexLineColorPicker = new ColorPicker( );
		FieldLabel uncoloredVertexFillLabel = new FieldLabel( StringBundle.get( "preferences_dialog_vertex_uncolored_fill_label" ) );
		uncoloredVertexFillColorPicker = new ColorPicker( );
		FieldLabel selectedVertexFillLabel = new FieldLabel( StringBundle.get( "preferences_dialog_vertex_selected_fill_label" ) );
		selectedVertexFillColorPicker = new ColorPicker( );
		FieldLabel selectedVertexLineLabel = new FieldLabel( StringBundle.get( "preferences_dialog_vertex_selected_line_label" ) );
		selectedVertexLineColorPicker = new ColorPicker( );
		
		Header edgeColorsHeader = new Header( StringBundle.get( "preferences_dialog_edge_colors_heading" ) );
		FieldLabel draggingEdgeLabel = new FieldLabel( StringBundle.get( "preferences_dialog_edge_dragging_label" ) );
		draggingEdgeColorPicker = new ColorPicker( );
		FieldLabel edgeHandleLabel = new FieldLabel( StringBundle.get( "preferences_dialog_edge_uncolored_handle_label" ) );
		edgeHandleColorPicker = new ColorPicker( );
		FieldLabel uncoloredEdgeLineLabel = new FieldLabel( StringBundle.get( "preferences_dialog_edge_uncolored_line_label" ) );
		uncoloredEdgeLineColorPicker = new ColorPicker( );
		FieldLabel selectedEdgeLabel = new FieldLabel( StringBundle.get( "preferences_dialog_edge_selected_label" ) );
		selectedEdgeColorPicker = new ColorPicker( );
		FieldLabel selectedEdgeHandleLabel = new FieldLabel( StringBundle.get( "preferences_dialog_edge_selected_handle_label" ) );
		selectedEdgeHandleColorPicker = new ColorPicker( );
		
		Header captionColorsHeader = new Header( StringBundle.get( "preferences_dialog_caption_colors_heading" ) );
		FieldLabel captionTextLabel = new FieldLabel( StringBundle.get( "preferences_dialog_caption_text_label" ) );
		captionTextColorPicker = new ColorPicker( );
		FieldLabel captionButtonFillLabel = new FieldLabel( StringBundle.get( "preferences_dialog_caption_button_fill_label" ) );
		captionButtonFillColorPicker = new ColorPicker( );
		FieldLabel captionButtonLineLabel = new FieldLabel( StringBundle.get( "preferences_dialog_caption_button_line_label" ) );
		captionButtonLineColorPicker = new ColorPicker( );
		FieldLabel selectedCaptionLineLabel = new FieldLabel( StringBundle.get( "preferences_dialog_caption_selected_line_label" ) );
		selectedCaptionLineColorPicker = new ColorPicker( );
		
		Header sharedColorsHeader = new Header( StringBundle.get( "preferences_dialog_shared_colors_heading" ) );
		FieldLabel elementColorsLabel = new FieldLabel( StringBundle.get( "preferences_dialog_shared_colored_elements_label" ) );
		
		elementColorsPanel = new JPanel( new GridLayout( 0, 1, 0, 6 ) )
		{
			{
				this.setBackground( panel.getBackground( ) );
			}
		};
		
		addButton = new JButton( StringBundle.get( "preferences_dialog_add_button_text" ) )
		{
			{
				this.setPreferredSize( new Dimension( 80, 28 ) );
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						ColorPicker picker = new ColorPicker( );
						elementColorsPanel.add( picker );
						removeButton.setEnabled( true );
						elementColorsPanel.revalidate( );
					}
				} );
			}
		};
		removeButton = new JButton( StringBundle.get( "preferences_dialog_remove_button_text" ) )
		{
			{
				this.setPreferredSize( new Dimension( 80, 28 ) );
				this.setEnabled( false );
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						elementColorsPanel.remove( elementColorsPanel.getComponentCount( ) - 1 );
						if( elementColorsPanel.getComponentCount( ) == 1 )
							removeButton.setEnabled( false );
						elementColorsPanel.revalidate( );
					}
				} );
			}
		};
		
		ColorPicker elementColorColorPicker = new ColorPicker( );
		elementColorsPanel.add( elementColorColorPicker );
		
		JPanel buttonPanel = new JPanel( )
		{
			{
				this.setLayout( new BoxLayout( this, BoxLayout.LINE_AXIS ) );
				this.setBorder( BorderFactory.createEmptyBorder( 2, 9, 0, 13 ) );
				this.setBackground( panel.getBackground( ) );
				this.add( Box.createHorizontalGlue( ) );
				this.add( addButton );
				this.add( Box.createRigidArea( new Dimension( 10, 0 ) ) );
				this.add( removeButton );
			}
		};
		
		Component col0Padding = Box.createRigidArea( new Dimension( this.columnWidths[0], 7 ) );
		Component col1Padding = Box.createRigidArea( new Dimension( this.columnWidths[1], 7 ) );
		Component col2Padding = Box.createRigidArea( new Dimension( this.columnWidths[2], 7 ) );
		
		GroupLayout layout = new GroupLayout( panel );
		panel.setLayout( layout );
		
		layout.setAutoCreateGaps( true );
		layout.setAutoCreateContainerGaps( true );
		
		layout.setHorizontalGroup( layout.createSequentialGroup( ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING ).addComponent( graphColorsHeader ).addComponent( vertexColorsHeader ).addComponent( edgeColorsHeader ).addComponent( captionColorsHeader ).addComponent( sharedColorsHeader ).addComponent( col0Padding ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING ).addComponent( graphBackgroundLabel ).addComponent( selectionBoxFillLabel ).addComponent( selectionBoxLineLabel ).addComponent( vertexLineLabel ).addComponent( uncoloredVertexFillLabel ).addComponent( selectedVertexFillLabel ).addComponent( selectedVertexLineLabel ).addComponent( draggingEdgeLabel ).addComponent( edgeHandleLabel ).addComponent( uncoloredEdgeLineLabel ).addComponent( selectedEdgeLabel ).addComponent( selectedEdgeHandleLabel ).addComponent( captionTextLabel ).addComponent( captionButtonFillLabel ).addComponent( captionButtonLineLabel ).addComponent( selectedCaptionLineLabel ).addComponent( elementColorsLabel ).addComponent( col1Padding ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING ).addComponent( graphBackgroundColorPicker ).addComponent( selectionBoxFillColorPicker ).addComponent( selectionBoxLineColorPicker ).addComponent( vertexLineColorPicker ).addComponent( uncoloredVertexFillColorPicker ).addComponent( selectedVertexFillColorPicker ).addComponent( selectedVertexLineColorPicker ).addComponent( draggingEdgeColorPicker ).addComponent( edgeHandleColorPicker ).addComponent( uncoloredEdgeLineColorPicker ).addComponent( selectedEdgeColorPicker ).addComponent( selectedEdgeHandleColorPicker ).addComponent( captionTextColorPicker ).addComponent( captionButtonFillColorPicker ).addComponent( captionButtonLineColorPicker ).addComponent( selectedCaptionLineColorPicker ).addComponent( elementColorsPanel ).addComponent( buttonPanel ).addComponent( col2Padding ) ) );
		
		layout.setVerticalGroup( layout.createSequentialGroup( ).addComponent( graphColorsHeader ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( graphBackgroundLabel ).addComponent( graphBackgroundColorPicker ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( selectionBoxFillLabel ).addComponent( selectionBoxFillColorPicker ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( selectionBoxLineLabel ).addComponent( selectionBoxLineColorPicker ) ).addComponent( vertexColorsHeader ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( vertexLineLabel ).addComponent( vertexLineColorPicker ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( uncoloredVertexFillLabel ).addComponent( uncoloredVertexFillColorPicker ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( selectedVertexFillLabel ).addComponent( selectedVertexFillColorPicker ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( selectedVertexLineLabel ).addComponent( selectedVertexLineColorPicker ) ).addComponent( edgeColorsHeader ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( draggingEdgeLabel ).addComponent( draggingEdgeColorPicker ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( edgeHandleLabel ).addComponent( edgeHandleColorPicker ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( uncoloredEdgeLineLabel ).addComponent( uncoloredEdgeLineColorPicker ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( selectedEdgeLabel ).addComponent( selectedEdgeColorPicker ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( selectedEdgeHandleLabel ).addComponent( selectedEdgeHandleColorPicker ) ).addComponent( captionColorsHeader ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( captionTextLabel ).addComponent( captionTextColorPicker ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( captionButtonFillLabel ).addComponent( captionButtonFillColorPicker ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( captionButtonLineLabel ).addComponent( captionButtonLineColorPicker ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( selectedCaptionLineLabel ).addComponent( selectedCaptionLineColorPicker ) ).addComponent( sharedColorsHeader ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING ).addComponent( elementColorsLabel ).addComponent( elementColorsPanel ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( buttonPanel ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( col0Padding ).addComponent( col1Padding ).addComponent( col2Padding ) ) );
		
		JScrollPane scrollPane = new JScrollPane( panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		scrollPane.setBorder( null );
		
		return scrollPane;
	}
	
	private JScrollPane initializeDefaultsPanel( )
	{
		final JPanel panel = new JPanel( );
		if( System.getProperty( "os.name" ).startsWith( "Windows" ) )
			panel.setBackground( Color.white );
		
		Header vertexDefaultsHeader = new Header( StringBundle.get( "preferences_dialog_default_vertex_properties_heading" ) );
		FieldLabel defaultVertexWeightLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_vertex_weight_label" ) );
		defaultVertexWeightTextField = new ValidatingTextField( 8, "-?(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel defaultVertexColorLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_vertex_color_label" ) );
		defaultVertexColorTextField = new ValidatingTextField( 8, "-1|\\d+" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel defaultVertexPrefixLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_vertex_prefix_label" ) );
		defaultVertexPrefixTextField = new JTextField( 8 )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel defaultVertexRadiusLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_vertex_radius_label" ) );
		defaultVertexRadiusTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel defaultVertexIsSelectedLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_vertex_is_selected_label" ) );
		defaultVertexIsSelectedCheckBox = new JCheckBox( )
		{
			{
				this.setBackground( panel.getBackground( ) );
				this.setMinimumSize( new Dimension( 32, 26 ) );
			}
		};
		
		Header edgeDefaultsHeader = new Header( StringBundle.get( "preferences_dialog_default_edge_properties_heading" ) );
		FieldLabel defaultEdgeWeightLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_edge_weight_label" ) );
		defaultEdgeWeightTextField = new ValidatingTextField( 8, "-?(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel defaultEdgeColorLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_edge_color_label" ) );
		defaultEdgeColorTextField = new ValidatingTextField( 8, "-1|\\d+" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel defaultEdgePrefixLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_edge_prefix_label" ) );
		defaultEdgePrefixTextField = new JTextField( 8 )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel defaultEdgeThicknessLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_edge_thickness_label" ) );
		defaultEdgeThicknessTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel defaultEdgeHandleRadiusRatioLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_edge_handle_radius_ratio_label" ) );
		defaultEdgeHandleRadiusRatioTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel defaultLoopDiameterLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_loop_diameter_label" ) );
		defaultLoopDiameterTextField = new ValidatingTextField( 8, "-?(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel defaultEdgeIsSelectedLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_edge_is_selected_label" ) );
		defaultEdgeIsSelectedCheckBox = new JCheckBox( )
		{
			{
				this.setBackground( panel.getBackground( ) );
				this.setMinimumSize( new Dimension( 32, 26 ) );
			}
		};
		
		Header captionDefaultsHeader = new Header( StringBundle.get( "preferences_dialog_default_caption_properties_heading" ) );
		FieldLabel defaultCaptionTextLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_caption_text_label" ) );
		defaultCaptionTextTextField = new JTextField( 20 )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 175, this.getPreferredSize( ).height ) );
			}
		};
		FieldLabel defaultCaptionFontSizeLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_caption_font_size_label" ) );
		defaultCaptionFontSizeTextField = new ValidatingTextField( 8, "(?:\\d{1,3}(\\.\\d*)?|\\d{0,3}\\.\\d+)" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, this.getPreferredSize( ).height ) );
			}
		};
		FieldLabel defaultCaptionIsSelectedLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_caption_is_selected_label" ) );
		defaultCaptionIsSelectedCheckBox = new JCheckBox( )
		{
			{
				this.setBackground( panel.getBackground( ) );
				this.setMinimumSize( new Dimension( 32, 26 ) );
			}
		};
		
		Header viewportDefaultsHeader = new Header( StringBundle.get( "preferences_dialog_default_viewport_settings_heading" ) );
		FieldLabel defaultShowVertexWeightsLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_show_vertex_weights_label" ) );
		defaultShowVertexWeightsCheckBox = new JCheckBox( )
		{
			{
				this.setBackground( panel.getBackground( ) );
				this.setMinimumSize( new Dimension( 32, 26 ) );
			}
		};
		FieldLabel defaultShowVertexLabelsLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_show_vertex_labels_label" ) );
		defaultShowVertexLabelsCheckBox = new JCheckBox( )
		{
			{
				this.setBackground( panel.getBackground( ) );
				this.setMinimumSize( new Dimension( 32, 26 ) );
			}
		};
		FieldLabel defaultShowEdgeHandlesLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_show_edge_handles_label" ) );
		defaultShowEdgeHandlesCheckBox = new JCheckBox( )
		{
			{
				this.setBackground( panel.getBackground( ) );
				this.setMinimumSize( new Dimension( 32, 26 ) );
			}
		};
		FieldLabel defaultShowEdgeWeightsLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_show_edge_weights_label" ) );
		defaultShowEdgeWeightsCheckBox = new JCheckBox( )
		{
			{
				this.setBackground( panel.getBackground( ) );
				this.setMinimumSize( new Dimension( 32, 26 ) );
			}
		};
		FieldLabel defaultShowEdgeLabelsLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_show_edge_labels_label" ) );
		defaultShowEdgeLabelsCheckBox = new JCheckBox( )
		{
			{
				this.setBackground( panel.getBackground( ) );
				this.setMinimumSize( new Dimension( 32, 26 ) );
			}
		};
		FieldLabel defaultShowCaptionsLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_show_captions_label" ) );
		defaultShowCaptionsCheckBox = new JCheckBox( )
		{
			{
				this.setBackground( panel.getBackground( ) );
				this.setMinimumSize( new Dimension( 32, 26 ) );
			}
		};
		FieldLabel defaultShowCaptionHandlesLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_show_caption_handles_label" ) );
		defaultShowCaptionHandlesCheckBox = new JCheckBox( )
		{
			{
				this.setBackground( panel.getBackground( ) );
				this.setMinimumSize( new Dimension( 32, 26 ) );
			}
		};
		FieldLabel defaultShowCaptionEditorsLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_show_caption_editors_label" ) );
		defaultShowCaptionEditorsCheckBox = new JCheckBox( )
		{
			{
				this.setBackground( panel.getBackground( ) );
				this.setMinimumSize( new Dimension( 32, 26 ) );
			}
		};
		
		Component col0Padding = Box.createRigidArea( new Dimension( this.columnWidths[0], 7 ) );
		Component col1Padding = Box.createRigidArea( new Dimension( this.columnWidths[1], 7 ) );
		Component col2Padding = Box.createRigidArea( new Dimension( this.columnWidths[2], 7 ) );
		
		GroupLayout layout = new GroupLayout( panel );
		panel.setLayout( layout );
		
		layout.setAutoCreateGaps( true );
		layout.setAutoCreateContainerGaps( true );
		
		layout.setHorizontalGroup( layout.createSequentialGroup( ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING ).addComponent( vertexDefaultsHeader ).addComponent( edgeDefaultsHeader ).addComponent( captionDefaultsHeader ).addComponent( viewportDefaultsHeader ).addComponent( col0Padding ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING ).addComponent( defaultVertexWeightLabel ).addComponent( defaultVertexColorLabel ).addComponent( defaultVertexPrefixLabel ).addComponent( defaultVertexRadiusLabel ).addComponent( defaultVertexIsSelectedLabel ).addComponent( defaultEdgeWeightLabel ).addComponent( defaultEdgeColorLabel ).addComponent( defaultEdgePrefixLabel ).addComponent( defaultEdgeThicknessLabel ).addComponent( defaultEdgeHandleRadiusRatioLabel ).addComponent( defaultLoopDiameterLabel ).addComponent( defaultEdgeIsSelectedLabel ).addComponent( defaultCaptionTextLabel ).addComponent( defaultCaptionFontSizeLabel ).addComponent( defaultCaptionIsSelectedLabel ).addComponent( defaultShowEdgeHandlesLabel ).addComponent( defaultShowEdgeWeightsLabel ).addComponent( defaultShowEdgeLabelsLabel ).addComponent( defaultShowVertexWeightsLabel ).addComponent( defaultShowVertexLabelsLabel ).addComponent( defaultShowCaptionsLabel ).addComponent( defaultShowCaptionHandlesLabel ).addComponent( defaultShowCaptionEditorsLabel ).addComponent( col1Padding ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING ).addComponent( defaultVertexWeightTextField ).addComponent( defaultVertexColorTextField ).addComponent( defaultVertexPrefixTextField ).addComponent( defaultVertexRadiusTextField ).addComponent( defaultVertexIsSelectedCheckBox ).addComponent( defaultEdgeWeightTextField ).addComponent( defaultEdgeColorTextField ).addComponent( defaultEdgePrefixTextField ).addComponent( defaultEdgeThicknessTextField ).addComponent( defaultEdgeHandleRadiusRatioTextField ).addComponent( defaultLoopDiameterTextField ).addComponent( defaultEdgeIsSelectedCheckBox ).addComponent( defaultCaptionTextTextField ).addComponent( defaultCaptionFontSizeTextField ).addComponent( defaultCaptionIsSelectedCheckBox ).addComponent( defaultShowEdgeHandlesCheckBox ).addComponent( defaultShowEdgeWeightsCheckBox ).addComponent( defaultShowEdgeLabelsCheckBox ).addComponent( defaultShowVertexWeightsCheckBox ).addComponent( defaultShowVertexLabelsCheckBox ).addComponent( defaultShowCaptionsCheckBox ).addComponent( defaultShowCaptionHandlesCheckBox ).addComponent( defaultShowCaptionEditorsCheckBox ).addComponent( col2Padding ) ) );
		
		layout.setVerticalGroup( layout.createSequentialGroup( ).addComponent( vertexDefaultsHeader ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultVertexWeightLabel ).addComponent( defaultVertexWeightTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultVertexColorLabel ).addComponent( defaultVertexColorTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultVertexPrefixLabel ).addComponent( defaultVertexPrefixTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultVertexRadiusLabel ).addComponent( defaultVertexRadiusTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultVertexIsSelectedLabel ).addComponent( defaultVertexIsSelectedCheckBox ) ).addComponent( edgeDefaultsHeader ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultEdgeWeightLabel ).addComponent( defaultEdgeWeightTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultEdgeColorLabel ).addComponent( defaultEdgeColorTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultEdgePrefixLabel ).addComponent( defaultEdgePrefixTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultEdgeThicknessLabel ).addComponent( defaultEdgeThicknessTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultEdgeHandleRadiusRatioLabel ).addComponent( defaultEdgeHandleRadiusRatioTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultLoopDiameterLabel ).addComponent( defaultLoopDiameterTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultEdgeIsSelectedLabel ).addComponent( defaultEdgeIsSelectedCheckBox ) ).addComponent( captionDefaultsHeader ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultCaptionTextLabel ).addComponent( defaultCaptionTextTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultCaptionFontSizeLabel ).addComponent( defaultCaptionFontSizeTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultCaptionIsSelectedLabel ).addComponent( defaultCaptionIsSelectedCheckBox ) ).addComponent( viewportDefaultsHeader ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultShowEdgeHandlesLabel ).addComponent( defaultShowEdgeHandlesCheckBox ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultShowEdgeWeightsLabel ).addComponent( defaultShowEdgeWeightsCheckBox ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultShowEdgeLabelsLabel ).addComponent( defaultShowEdgeLabelsCheckBox ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultShowVertexWeightsLabel ).addComponent( defaultShowVertexWeightsCheckBox ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultShowVertexLabelsLabel ).addComponent( defaultShowVertexLabelsCheckBox ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultShowCaptionsLabel ).addComponent( defaultShowCaptionsCheckBox ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultShowCaptionHandlesLabel ).addComponent( defaultShowCaptionHandlesCheckBox ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultShowCaptionEditorsLabel ).addComponent( defaultShowCaptionEditorsCheckBox ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( col0Padding ).addComponent( col1Padding ).addComponent( col2Padding ) ) );
		
		JScrollPane scrollPane = new JScrollPane( panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		scrollPane.setBorder( null );
		
		return scrollPane;
	}
	
	private JScrollPane initializeUnderTheHoodPanel( )
	{
		final JPanel panel = new JPanel( new GridBagLayout( ) );
		if( System.getProperty( "os.name" ).startsWith( "Windows" ) )
			panel.setBackground( Color.white );
		
		Header clickingBehaviorHeader = new Header( StringBundle.get( "preferences_dialog_clicking_behavior_heading" ) );
		FieldLabel vertexClickMarginLabel = new FieldLabel( StringBundle.get( "preferences_dialog_vertex_click_margin_label" ) );
		vertexClickMarginTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel edgeHandleClickMarginLabel = new FieldLabel( StringBundle.get( "preferences_dialog_edge_handle_click_margin_label" ) );
		edgeHandleClickMarginTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel captionHandleClickMarginLabel = new FieldLabel( StringBundle.get( "preferences_dialog_caption_handle_click_margin_label" ) );
		captionHandleClickMarginTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel captionEditorClickMarginLabel = new FieldLabel( StringBundle.get( "preferences_dialog_caption_editor_click_margin_label" ) );
		captionEditorClickMarginTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel panDecelerationFactorLabel = new FieldLabel( StringBundle.get( "preferences_dialog_pan_deceleration_factor_label" ) );
		panDecelerationFactorTextField = new ValidatingTextField( 8, "-(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel panOnDoubleClickLabel = new FieldLabel( StringBundle.get( "preferences_dialog_pan_on_double_click_label" ) );
		panOnDoubleClickCheckBox = new JCheckBox( )
		{
			{
				this.setBackground( panel.getBackground( ) );
				this.setMinimumSize( new Dimension( 32, 26 ) );
			}
		};
		FieldLabel deselectVertexWithNewEdgeLabel = new FieldLabel( StringBundle.get( "preferences_dialog_deselect_vertex_with_new_edge_label" ) );
		deselectVertexWithNewEdgeCheckBox = new JCheckBox( )
		{
			{
				this.setBackground( panel.getBackground( ) );
				this.setMinimumSize( new Dimension( 32, 26 ) );
			}
		};
		
		Header zoomingBehaviorHeader = new Header( StringBundle.get( "preferences_dialog_zooming_behavior_heading" ) );
		FieldLabel zoomInFactorLabel = new FieldLabel( StringBundle.get( "preferences_dialog_zoom_in_factor_label" ) );
		zoomInFactorTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel zoomOutFactorLabel = new FieldLabel( StringBundle.get( "preferences_dialog_zoom_out_factor_label" ) );
		zoomOutFactorTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel maximumZoomFactorLabel = new FieldLabel( StringBundle.get( "preferences_dialog_maximum_zoom_factor_label" ) );
		maximumZoomFactorTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel zoomGraphPaddingLabel = new FieldLabel( StringBundle.get( "preferences_dialog_zoom_fit_padding_label" ) );
		zoomGraphPaddingTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel scrollZoomIncrementLabel = new FieldLabel( StringBundle.get( "preferences_dialog_scroll_zoom_increment_label" ) );
		scrollIncrementZoomTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		
		Header arrangingBehaviorHeader = new Header( StringBundle.get( "preferences_dialog_arranging_behavior_heading" ) );
		FieldLabel arrangeCircleRadiusMultiplierLabel = new FieldLabel( StringBundle.get( "preferences_dialog_arrange_circle_radius_multiplier_label" ) );
		arrangeCircleRadiusMultiplierTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel arrangeGridSpacingLabel = new FieldLabel( StringBundle.get( "preferences_dialog_arrange_grid_spacing_label" ) );
		arrangeGridSpacingTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel autoArrangeAttractiveForceLabel = new FieldLabel( StringBundle.get( "preferences_dialog_auto_arrange_attractive_force_label" ) );
		autoArrangeAttractiveForceTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel autoArrangeRepulsiveForceLabel = new FieldLabel( StringBundle.get( "preferences_dialog_auto_arrange_repulsive_force_label" ) );
		autoArrangeRepulsiveForceTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel autoArrangeDecelerationFactorLabel = new FieldLabel( StringBundle.get( "preferences_dialog_auto_arrange_deceleration_factor_label" ) );
		autoArrangeDecelerationFactorTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel arrangeContractFactorLabel = new FieldLabel( StringBundle.get( "preferences_dialog_auto_arrange_contract_factor_label" ) );
		arrangeContractFactorTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel arrangeExpandFactorLabel = new FieldLabel( StringBundle.get( "preferences_dialog_arrange_expand_factor_label" ) );
		arrangeExpandFactorTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		
		Header undoBehaviorHeader = new Header( StringBundle.get( "preferences_dialog_undo_behavior_heading" ) );
		FieldLabel undoLoggingIntervalLabel = new FieldLabel( StringBundle.get( "preferences_dialog_undo_logging_interval_label" ) );
		undoLoggingIntervalTextField = new ValidatingTextField( 8, "\\d{3,8}" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel undoLoggingMaximumLabel = new FieldLabel( StringBundle.get( "preferences_dialog_undo_logging_maximum_label" ) );
		undoLoggingMaximumTextField = new ValidatingTextField( 8, "\\d{0,3}" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		
		Header renderingSettingsHeader = new Header( StringBundle.get( "preferences_dialog_rendering_settings_heading" ) );
		FieldLabel useAntiAliasingLabel = new FieldLabel( StringBundle.get( "preferences_dialog_use_anti_aliasing_label" ) );
		useAntiAliasingCheckBox = new JCheckBox( )
		{
			{
				this.setBackground( panel.getBackground( ) );
				this.setMinimumSize( new Dimension( 32, 26 ) );
			}
		};
		FieldLabel usePureStrokeLabel = new FieldLabel( StringBundle.get( "preferences_dialog_use_pure_stroke_label" ) );
		usePureStrokeCheckBox = new JCheckBox( )
		{
			{
				this.setBackground( panel.getBackground( ) );
				this.setMinimumSize( new Dimension( 32, 26 ) );
			}
		};
		FieldLabel useBicubicInterpolationLabel = new FieldLabel( StringBundle.get( "preferences_dialog_use_bicubic_interpolation_label" ) );
		useBicubicInterpolationCheckBox = new JCheckBox( )
		{
			{
				this.setBackground( panel.getBackground( ) );
				this.setMinimumSize( new Dimension( 32, 26 ) );
			}
		};
		FieldLabel useFractionalMetricsLabel = new FieldLabel( StringBundle.get( "preferences_dialog_use_fractional_metrics_label" ) );
		useFractionalMetricsCheckBox = new JCheckBox( )
		{
			{
				this.setBackground( panel.getBackground( ) );
				this.setMinimumSize( new Dimension( 32, 26 ) );
			}
		};
		FieldLabel vertexWeightPrecisionLabel = new FieldLabel( StringBundle.get( "preferences_dialog_vertex_weight_precision_label" ) );
		vertexWeightPrecisionTextField = new ValidatingTextField( 8, "0*(\\d|1\\d)" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel edgeWeightPrecisionLabel = new FieldLabel( StringBundle.get( "preferences_dialog_edge_weight_precision_label" ) );
		edgeWeightPrecisionTextField = new ValidatingTextField( 8, "0*(\\d|1\\d)" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		
		Header windowSettingsHeader = new Header( StringBundle.get( "preferences_dialog_window_settings_heading" ) );
		FieldLabel mainWindowSizeLabel = new FieldLabel( StringBundle.get( "preferences_dialog_main_window_size_label" ) );
		mainWindowSizeTextField = new ValidatingTextField( 16, "(\\d+)\\s*,\\s*(\\d+)" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 140, 100 ) );
			}
		};
		FieldLabel scriptLibraryWindowSizeLabel = new FieldLabel( StringBundle.get( "preferences_dialog_script_library_window_size_label" ) );
		scriptLibraryWindowSizeTextField = new ValidatingTextField( 16, "(\\d+)\\s*,\\s*(\\d+)" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 140, 100 ) );
			}
		};
		FieldLabel graphWindowSizeLabel = new FieldLabel( StringBundle.get( "preferences_dialog_graph_window_size_label" ) );
		graphWindowSizeTextField = new ValidatingTextField( 16, "(\\d+)\\s*,\\s*(\\d+)" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 140, 100 ) );
			}
		};
		FieldLabel cascadeWindowOffsetLabel = new FieldLabel( StringBundle.get( "preferences_dialog_cascade_window_offset_label" ) );
		cascadeWindowOffsetTextField = new ValidatingTextField( 8, "\\d+" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		
		Header otherHeader = new Header( StringBundle.get( "preferences_dialog_other_heading" ) );
		FieldLabel languageLabel = new FieldLabel( StringBundle.get( "preferences_dialog_language_label" ) );
		languageComboBox = new JComboBox( )
		{
			{
				this.setMaximumSize( new Dimension( 140, 100 ) );
			}
		};
		
		Set<LocaleWrapper> locales = new TreeSet<LocaleWrapper>( );
		for( Locale locale : DateFormat.getAvailableLocales( ) )
			if( ResourceBundle.getBundle( "edu.belmont.mth.visigraph.resources.strings.Resources", locale ).getLocale( ).equals( locale ) )
				locales.add( new LocaleWrapper( locale ) );
		for( LocaleWrapper locale : locales )
			languageComboBox.addItem( locale );
		
		FieldLabel defaultGraphNameLabel = new FieldLabel( StringBundle.get( "preferences_dialog_default_graph_name_label" ) );
		defaultGraphNameTextField = new JTextField( 20 )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 140, 100 ) );
			}
		};
		FieldLabel directedEdgeArrowRatioLabel = new FieldLabel( StringBundle.get( "preferences_dialog_directed_edge_arrow_ratio_label" ) );
		directedEdgeArrowRatioTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel arrowKeyIncrementLabel = new FieldLabel( StringBundle.get( "preferences_dialog_arrow_key_increment_label" ) );
		arrowKeyIncrementTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel edgeSnapMarginRatioLabel = new FieldLabel( StringBundle.get( "preferences_dialog_edge_snap_margin_label" ) );
		edgeSnapMarginRatioTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel areCloseDistanceLabel = new FieldLabel( StringBundle.get( "preferences_dialog_are_close_distance_label" ) );
		areCloseDistanceTextField = new ValidatingTextField( 8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		FieldLabel paintToolMenuDelayLabel = new FieldLabel( StringBundle.get( "preferences_dialog_paint_tool_menu_delay_label" ) );
		paintToolMenuDelayTextField = new ValidatingTextField( 8, "\\d+" )
		{
			{
				this.setMargin( PreferencesDialog.this.fieldMargin );
				this.setMaximumSize( new Dimension( 70, 100 ) );
			}
		};
		
		Component col0Padding = Box.createRigidArea( new Dimension( this.columnWidths[0], 7 ) );
		Component col1Padding = Box.createRigidArea( new Dimension( this.columnWidths[1], 7 ) );
		Component col2Padding = Box.createRigidArea( new Dimension( this.columnWidths[2], 7 ) );
		
		GroupLayout layout = new GroupLayout( panel );
		panel.setLayout( layout );
		
		layout.setAutoCreateGaps( true );
		layout.setAutoCreateContainerGaps( true );
		
		layout.setHorizontalGroup( layout.createSequentialGroup( ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING ).addComponent( clickingBehaviorHeader ).addComponent( zoomingBehaviorHeader ).addComponent( arrangingBehaviorHeader ).addComponent( undoBehaviorHeader ).addComponent( renderingSettingsHeader ).addComponent( windowSettingsHeader ).addComponent( otherHeader ).addComponent( col0Padding ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING ).addComponent( vertexClickMarginLabel ).addComponent( edgeHandleClickMarginLabel ).addComponent( captionHandleClickMarginLabel ).addComponent( captionEditorClickMarginLabel ).addComponent( panDecelerationFactorLabel ).addComponent( panOnDoubleClickLabel ).addComponent( deselectVertexWithNewEdgeLabel ).addComponent( zoomInFactorLabel ).addComponent( zoomOutFactorLabel ).addComponent( maximumZoomFactorLabel ).addComponent( zoomGraphPaddingLabel ).addComponent( scrollZoomIncrementLabel ).addComponent( arrangeCircleRadiusMultiplierLabel ).addComponent( arrangeGridSpacingLabel ).addComponent( autoArrangeAttractiveForceLabel ).addComponent( autoArrangeRepulsiveForceLabel ).addComponent( autoArrangeDecelerationFactorLabel ).addComponent( arrangeContractFactorLabel ).addComponent( arrangeExpandFactorLabel ).addComponent( undoLoggingIntervalLabel ).addComponent( undoLoggingMaximumLabel ).addComponent( useAntiAliasingLabel ).addComponent( usePureStrokeLabel ).addComponent( useBicubicInterpolationLabel ).addComponent( useFractionalMetricsLabel ).addComponent( vertexWeightPrecisionLabel ).addComponent( edgeWeightPrecisionLabel ).addComponent( mainWindowSizeLabel ).addComponent( scriptLibraryWindowSizeLabel ).addComponent( graphWindowSizeLabel ).addComponent( cascadeWindowOffsetLabel ).addComponent( languageLabel ).addComponent( defaultGraphNameLabel ).addComponent( directedEdgeArrowRatioLabel ).addComponent( arrowKeyIncrementLabel ).addComponent( edgeSnapMarginRatioLabel ).addComponent( areCloseDistanceLabel ).addComponent( paintToolMenuDelayLabel ).addComponent( col1Padding ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING ).addComponent( vertexClickMarginTextField ).addComponent( edgeHandleClickMarginTextField ).addComponent( captionHandleClickMarginTextField ).addComponent( captionEditorClickMarginTextField ).addComponent( panDecelerationFactorTextField ).addComponent( panOnDoubleClickCheckBox ).addComponent( deselectVertexWithNewEdgeCheckBox ).addComponent( zoomInFactorTextField ).addComponent( zoomOutFactorTextField ).addComponent( maximumZoomFactorTextField ).addComponent( zoomGraphPaddingTextField ).addComponent( scrollIncrementZoomTextField ).addComponent( arrangeCircleRadiusMultiplierTextField ).addComponent( arrangeGridSpacingTextField ).addComponent( autoArrangeAttractiveForceTextField ).addComponent( autoArrangeRepulsiveForceTextField ).addComponent( autoArrangeDecelerationFactorTextField ).addComponent( arrangeContractFactorTextField ).addComponent( arrangeExpandFactorTextField ).addComponent( undoLoggingIntervalTextField ).addComponent( undoLoggingMaximumTextField ).addComponent( useAntiAliasingCheckBox ).addComponent( usePureStrokeCheckBox ).addComponent( useBicubicInterpolationCheckBox ).addComponent( useFractionalMetricsCheckBox ).addComponent( vertexWeightPrecisionTextField ).addComponent( edgeWeightPrecisionTextField ).addComponent( mainWindowSizeTextField ).addComponent( scriptLibraryWindowSizeTextField ).addComponent( graphWindowSizeTextField ).addComponent( cascadeWindowOffsetTextField ).addComponent( languageComboBox ).addComponent( defaultGraphNameTextField ).addComponent( directedEdgeArrowRatioTextField ).addComponent( arrowKeyIncrementTextField ).addComponent( edgeSnapMarginRatioTextField ).addComponent( areCloseDistanceTextField ).addComponent( paintToolMenuDelayTextField ).addComponent( col2Padding ) ) );
		
		layout.setVerticalGroup( layout.createSequentialGroup( ).addComponent( clickingBehaviorHeader ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( vertexClickMarginLabel ).addComponent( vertexClickMarginTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( edgeHandleClickMarginLabel ).addComponent( edgeHandleClickMarginTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( captionHandleClickMarginLabel ).addComponent( captionHandleClickMarginTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( captionEditorClickMarginLabel ).addComponent( captionEditorClickMarginTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( panDecelerationFactorLabel ).addComponent( panDecelerationFactorTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( panOnDoubleClickLabel ).addComponent( panOnDoubleClickCheckBox ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( deselectVertexWithNewEdgeLabel ).addComponent( deselectVertexWithNewEdgeCheckBox ) ).addComponent( zoomingBehaviorHeader ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( zoomInFactorLabel ).addComponent( zoomInFactorTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( zoomOutFactorLabel ).addComponent( zoomOutFactorTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( maximumZoomFactorLabel ).addComponent( maximumZoomFactorTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( zoomGraphPaddingLabel ).addComponent( zoomGraphPaddingTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( scrollZoomIncrementLabel ).addComponent( scrollIncrementZoomTextField ) ).addComponent( arrangingBehaviorHeader ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( arrangeCircleRadiusMultiplierLabel ).addComponent( arrangeCircleRadiusMultiplierTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( arrangeGridSpacingLabel ).addComponent( arrangeGridSpacingTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( autoArrangeAttractiveForceLabel ).addComponent( autoArrangeAttractiveForceTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( autoArrangeRepulsiveForceLabel ).addComponent( autoArrangeRepulsiveForceTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( autoArrangeDecelerationFactorLabel ).addComponent( autoArrangeDecelerationFactorTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( arrangeContractFactorLabel ).addComponent( arrangeContractFactorTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( arrangeExpandFactorLabel ).addComponent( arrangeExpandFactorTextField ) ).addComponent( undoBehaviorHeader ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( undoLoggingIntervalLabel ).addComponent( undoLoggingIntervalTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( undoLoggingMaximumLabel ).addComponent( undoLoggingMaximumTextField ) ).addComponent( renderingSettingsHeader ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( useAntiAliasingLabel ).addComponent( useAntiAliasingCheckBox ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( usePureStrokeLabel ).addComponent( usePureStrokeCheckBox ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( useBicubicInterpolationLabel ).addComponent( useBicubicInterpolationCheckBox ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( useFractionalMetricsLabel ).addComponent( useFractionalMetricsCheckBox ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( vertexWeightPrecisionLabel ).addComponent( vertexWeightPrecisionTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( edgeWeightPrecisionLabel ).addComponent( edgeWeightPrecisionTextField ) ).addComponent( windowSettingsHeader ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( mainWindowSizeLabel ).addComponent( mainWindowSizeTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( scriptLibraryWindowSizeLabel ).addComponent( scriptLibraryWindowSizeTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( graphWindowSizeLabel ).addComponent( graphWindowSizeTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( cascadeWindowOffsetLabel ).addComponent( cascadeWindowOffsetTextField ) ).addComponent( otherHeader ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( languageLabel ).addComponent( languageComboBox ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( defaultGraphNameLabel ).addComponent( defaultGraphNameTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( directedEdgeArrowRatioLabel ).addComponent( directedEdgeArrowRatioTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( arrowKeyIncrementLabel ).addComponent( arrowKeyIncrementTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( edgeSnapMarginRatioLabel ).addComponent( edgeSnapMarginRatioTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( areCloseDistanceLabel ).addComponent( areCloseDistanceTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( paintToolMenuDelayLabel ).addComponent( paintToolMenuDelayTextField ) ).addGroup( layout.createParallelGroup( GroupLayout.Alignment.CENTER ).addComponent( col0Padding ).addComponent( col1Padding ).addComponent( col2Padding ) ) );
		
		JScrollPane scrollPane = new JScrollPane( panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		scrollPane.setBorder( null );
		
		return scrollPane;
	}
	
	public void loadPreferences( )
	{
		defaultVertexWeightTextField.setText( UserSettings.instance.defaultVertexWeight.get( ).toString( ) );
		defaultVertexColorTextField.setText( UserSettings.instance.defaultVertexColor.get( ).toString( ) );
		defaultVertexPrefixTextField.setText( UserSettings.instance.defaultVertexPrefix.get( ).toString( ) );
		defaultVertexRadiusTextField.setText( UserSettings.instance.defaultVertexRadius.get( ).toString( ) );
		defaultVertexIsSelectedCheckBox.setSelected( UserSettings.instance.defaultVertexIsSelected.get( ) );
		defaultEdgeWeightTextField.setText( UserSettings.instance.defaultEdgeWeight.get( ).toString( ) );
		defaultEdgeColorTextField.setText( UserSettings.instance.defaultEdgeColor.get( ).toString( ) );
		defaultEdgePrefixTextField.setText( UserSettings.instance.defaultEdgePrefix.get( ).toString( ) );
		defaultEdgeThicknessTextField.setText( UserSettings.instance.defaultEdgeThickness.get( ).toString( ) );
		defaultEdgeHandleRadiusRatioTextField.setText( UserSettings.instance.defaultEdgeHandleRadiusRatio.get( ).toString( ) );
		defaultLoopDiameterTextField.setText( UserSettings.instance.defaultLoopDiameter.get( ).toString( ) );
		defaultEdgeIsSelectedCheckBox.setSelected( UserSettings.instance.defaultEdgeIsSelected.get( ) );
		defaultCaptionTextTextField.setText( UserSettings.instance.defaultCaptionText.get( ) );
		defaultCaptionFontSizeTextField.setText( UserSettings.instance.defaultCaptionFontSize.get( ).toString( ) );
		defaultCaptionIsSelectedCheckBox.setSelected( UserSettings.instance.defaultCaptionIsSelected.get( ) );
		defaultShowVertexWeightsCheckBox.setSelected( UserSettings.instance.defaultShowVertexWeights.get( ) );
		defaultShowVertexLabelsCheckBox.setSelected( UserSettings.instance.defaultShowVertexLabels.get( ) );
		defaultShowEdgeHandlesCheckBox.setSelected( UserSettings.instance.defaultShowEdgeHandles.get( ) );
		defaultShowEdgeWeightsCheckBox.setSelected( UserSettings.instance.defaultShowEdgeWeights.get( ) );
		defaultShowEdgeLabelsCheckBox.setSelected( UserSettings.instance.defaultShowEdgeLabels.get( ) );
		defaultShowCaptionsCheckBox.setSelected( UserSettings.instance.defaultShowCaptions.get( ) );
		defaultShowCaptionHandlesCheckBox.setSelected( UserSettings.instance.defaultShowCaptionHandles.get( ) );
		defaultShowCaptionEditorsCheckBox.setSelected( UserSettings.instance.defaultShowCaptionEditors.get( ) );
		graphBackgroundColorPicker.setColor( UserSettings.instance.graphBackground.get( ) );
		selectionBoxFillColorPicker.setColor( UserSettings.instance.selectionBoxFill.get( ) );
		selectionBoxLineColorPicker.setColor( UserSettings.instance.selectionBoxLine.get( ) );
		vertexLineColorPicker.setColor( UserSettings.instance.vertexLine.get( ) );
		uncoloredVertexFillColorPicker.setColor( UserSettings.instance.uncoloredVertexFill.get( ) );
		selectedVertexFillColorPicker.setColor( UserSettings.instance.selectedVertexFill.get( ) );
		selectedVertexLineColorPicker.setColor( UserSettings.instance.selectedVertexLine.get( ) );
		draggingEdgeColorPicker.setColor( UserSettings.instance.draggingEdge.get( ) );
		edgeHandleColorPicker.setColor( UserSettings.instance.edgeHandle.get( ) );
		uncoloredEdgeLineColorPicker.setColor( UserSettings.instance.uncoloredEdgeLine.get( ) );
		selectedEdgeColorPicker.setColor( UserSettings.instance.selectedEdge.get( ) );
		selectedEdgeHandleColorPicker.setColor( UserSettings.instance.selectedEdgeHandle.get( ) );
		captionTextColorPicker.setColor( UserSettings.instance.captionText.get( ) );
		captionButtonFillColorPicker.setColor( UserSettings.instance.captionButtonFill.get( ) );
		captionButtonLineColorPicker.setColor( UserSettings.instance.captionButtonLine.get( ) );
		selectedCaptionLineColorPicker.setColor( UserSettings.instance.selectedCaptionLine.get( ) );
		vertexClickMarginTextField.setText( UserSettings.instance.vertexClickMargin.get( ).toString( ) );
		edgeHandleClickMarginTextField.setText( UserSettings.instance.edgeHandleClickMargin.get( ).toString( ) );
		captionHandleClickMarginTextField.setText( UserSettings.instance.captionHandleClickMargin.get( ).toString( ) );
		captionEditorClickMarginTextField.setText( UserSettings.instance.captionEditorClickMargin.get( ).toString( ) );
		panDecelerationFactorTextField.setText( UserSettings.instance.panDecelerationFactor.get( ).toString( ) );
		panOnDoubleClickCheckBox.setSelected( UserSettings.instance.panOnDoubleClick.get( ) );
		deselectVertexWithNewEdgeCheckBox.setSelected( UserSettings.instance.deselectVertexWithNewEdge.get( ) );
		zoomInFactorTextField.setText( UserSettings.instance.zoomInFactor.get( ).toString( ) );
		zoomOutFactorTextField.setText( UserSettings.instance.zoomOutFactor.get( ).toString( ) );
		maximumZoomFactorTextField.setText( UserSettings.instance.maximumZoomFactor.get( ).toString( ) );
		zoomGraphPaddingTextField.setText( UserSettings.instance.zoomGraphPadding.get( ).toString( ) );
		scrollIncrementZoomTextField.setText( UserSettings.instance.scrollIncrementZoom.get( ).toString( ) );
		arrangeCircleRadiusMultiplierTextField.setText( UserSettings.instance.arrangeCircleRadiusMultiplier.get( ).toString( ) );
		arrangeGridSpacingTextField.setText( UserSettings.instance.arrangeGridSpacing.get( ).toString( ) );
		autoArrangeAttractiveForceTextField.setText( UserSettings.instance.autoArrangeAttractiveForce.get( ).toString( ) );
		autoArrangeRepulsiveForceTextField.setText( UserSettings.instance.autoArrangeRepulsiveForce.get( ).toString( ) );
		autoArrangeDecelerationFactorTextField.setText( UserSettings.instance.autoArrangeDecelerationFactor.get( ).toString( ) );
		arrangeContractFactorTextField.setText( UserSettings.instance.arrangeContractFactor.get( ).toString( ) );
		arrangeExpandFactorTextField.setText( UserSettings.instance.arrangeExpandFactor.get( ).toString( ) );
		undoLoggingIntervalTextField.setText( UserSettings.instance.undoLoggingInterval.get( ).toString( ) );
		undoLoggingMaximumTextField.setText( UserSettings.instance.undoLoggingMaximum.get( ).toString( ) );
		useAntiAliasingCheckBox.setSelected( UserSettings.instance.useAntiAliasing.get( ) );
		usePureStrokeCheckBox.setSelected( UserSettings.instance.usePureStroke.get( ) );
		useBicubicInterpolationCheckBox.setSelected( UserSettings.instance.useBicubicInterpolation.get( ) );
		useFractionalMetricsCheckBox.setSelected( UserSettings.instance.useFractionalMetrics.get( ) );
		vertexWeightPrecisionTextField.setText( UserSettings.instance.vertexWeightPrecision.get( ).toString( ) );
		edgeWeightPrecisionTextField.setText( UserSettings.instance.edgeWeightPrecision.get( ).toString( ) );
		mainWindowSizeTextField.setText( UserSettings.instance.mainWindowWidth.get( ) + ", " + UserSettings.instance.mainWindowHeight.get( ) );
		scriptLibraryWindowSizeTextField.setText( UserSettings.instance.scriptLibraryWindowWidth.get( ) + ", " + UserSettings.instance.scriptLibraryWindowHeight.get( ) );
		graphWindowSizeTextField.setText( UserSettings.instance.graphWindowWidth.get( ) + ", " + UserSettings.instance.graphWindowWidth.get( ) );
		cascadeWindowOffsetTextField.setText( UserSettings.instance.cascadeWindowOffset.get( ).toString( ) );
		defaultGraphNameTextField.setText( UserSettings.instance.defaultGraphName.get( ) );
		directedEdgeArrowRatioTextField.setText( UserSettings.instance.directedEdgeArrowRatio.get( ).toString( ) );
		arrowKeyIncrementTextField.setText( UserSettings.instance.arrowKeyIncrement.get( ).toString( ) );
		edgeSnapMarginRatioTextField.setText( UserSettings.instance.edgeSnapMarginRatio.get( ).toString( ) );
		areCloseDistanceTextField.setText( UserSettings.instance.areCloseDistance.get( ).toString( ) );
		paintToolMenuDelayTextField.setText( UserSettings.instance.paintToolMenuDelay.get( ).toString( ) );
		
		elementColorsPanel.removeAll( );
		for( Color color : UserSettings.instance.elementColors )
			elementColorsPanel.add( new ColorPicker( color ) );
		
		if( elementColorsPanel.getComponentCount( ) > 1 )
			removeButton.setEnabled( true );
		
		for( int i = 0; i < languageComboBox.getItemCount( ); ++i )
			if( ( (LocaleWrapper) languageComboBox.getItemAt( i ) ).locale.toString( ).equals( UserSettings.instance.language.get( ) ) )
			{
				languageComboBox.setSelectedIndex( i );
				break;
			}
	}
	
	public void resetPreferences( )
	{
		defaultVertexWeightTextField.setText( UserSettings.instance.defaultVertexWeight.getDefault( ).toString( ) );
		defaultVertexColorTextField.setText( UserSettings.instance.defaultVertexColor.getDefault( ).toString( ) );
		defaultVertexPrefixTextField.setText( UserSettings.instance.defaultVertexPrefix.getDefault( ).toString( ) );
		defaultVertexRadiusTextField.setText( UserSettings.instance.defaultVertexRadius.getDefault( ).toString( ) );
		defaultVertexIsSelectedCheckBox.setSelected( UserSettings.instance.defaultVertexIsSelected.getDefault( ) );
		defaultEdgeWeightTextField.setText( UserSettings.instance.defaultEdgeWeight.getDefault( ).toString( ) );
		defaultEdgeColorTextField.setText( UserSettings.instance.defaultEdgeColor.getDefault( ).toString( ) );
		defaultEdgePrefixTextField.setText( UserSettings.instance.defaultEdgePrefix.getDefault( ).toString( ) );
		defaultEdgeThicknessTextField.setText( UserSettings.instance.defaultEdgeThickness.getDefault( ).toString( ) );
		defaultEdgeHandleRadiusRatioTextField.setText( UserSettings.instance.defaultEdgeHandleRadiusRatio.getDefault( ).toString( ) );
		defaultLoopDiameterTextField.setText( UserSettings.instance.defaultLoopDiameter.getDefault( ).toString( ) );
		defaultEdgeIsSelectedCheckBox.setSelected( UserSettings.instance.defaultEdgeIsSelected.getDefault( ) );
		defaultCaptionTextTextField.setText( UserSettings.instance.defaultCaptionText.getDefault( ) );
		defaultCaptionFontSizeTextField.setText( UserSettings.instance.defaultCaptionFontSize.getDefault( ).toString( ) );
		defaultCaptionIsSelectedCheckBox.setSelected( UserSettings.instance.defaultCaptionIsSelected.getDefault( ) );
		defaultShowVertexWeightsCheckBox.setSelected( UserSettings.instance.defaultShowVertexWeights.getDefault( ) );
		defaultShowVertexLabelsCheckBox.setSelected( UserSettings.instance.defaultShowVertexLabels.getDefault( ) );
		defaultShowEdgeHandlesCheckBox.setSelected( UserSettings.instance.defaultShowEdgeHandles.getDefault( ) );
		defaultShowEdgeWeightsCheckBox.setSelected( UserSettings.instance.defaultShowEdgeWeights.getDefault( ) );
		defaultShowEdgeLabelsCheckBox.setSelected( UserSettings.instance.defaultShowEdgeLabels.getDefault( ) );
		defaultShowCaptionsCheckBox.setSelected( UserSettings.instance.defaultShowCaptions.getDefault( ) );
		defaultShowCaptionHandlesCheckBox.setSelected( UserSettings.instance.defaultShowCaptionHandles.getDefault( ) );
		defaultShowCaptionEditorsCheckBox.setSelected( UserSettings.instance.defaultShowCaptionEditors.getDefault( ) );
		graphBackgroundColorPicker.setColor( UserSettings.instance.graphBackground.getDefault( ) );
		selectionBoxFillColorPicker.setColor( UserSettings.instance.selectionBoxFill.getDefault( ) );
		selectionBoxLineColorPicker.setColor( UserSettings.instance.selectionBoxLine.getDefault( ) );
		vertexLineColorPicker.setColor( UserSettings.instance.vertexLine.getDefault( ) );
		uncoloredVertexFillColorPicker.setColor( UserSettings.instance.uncoloredVertexFill.getDefault( ) );
		selectedVertexFillColorPicker.setColor( UserSettings.instance.selectedVertexFill.getDefault( ) );
		selectedVertexLineColorPicker.setColor( UserSettings.instance.selectedVertexLine.getDefault( ) );
		draggingEdgeColorPicker.setColor( UserSettings.instance.draggingEdge.getDefault( ) );
		edgeHandleColorPicker.setColor( UserSettings.instance.edgeHandle.getDefault( ) );
		uncoloredEdgeLineColorPicker.setColor( UserSettings.instance.uncoloredEdgeLine.getDefault( ) );
		selectedEdgeColorPicker.setColor( UserSettings.instance.selectedEdge.getDefault( ) );
		selectedEdgeHandleColorPicker.setColor( UserSettings.instance.selectedEdgeHandle.getDefault( ) );
		captionTextColorPicker.setColor( UserSettings.instance.captionText.getDefault( ) );
		captionButtonFillColorPicker.setColor( UserSettings.instance.captionButtonFill.getDefault( ) );
		captionButtonLineColorPicker.setColor( UserSettings.instance.captionButtonLine.getDefault( ) );
		selectedCaptionLineColorPicker.setColor( UserSettings.instance.selectedCaptionLine.getDefault( ) );
		vertexClickMarginTextField.setText( UserSettings.instance.vertexClickMargin.getDefault( ).toString( ) );
		edgeHandleClickMarginTextField.setText( UserSettings.instance.edgeHandleClickMargin.getDefault( ).toString( ) );
		captionHandleClickMarginTextField.setText( UserSettings.instance.captionHandleClickMargin.getDefault( ).toString( ) );
		captionEditorClickMarginTextField.setText( UserSettings.instance.captionEditorClickMargin.getDefault( ).toString( ) );
		panDecelerationFactorTextField.setText( UserSettings.instance.panDecelerationFactor.getDefault( ).toString( ) );
		panOnDoubleClickCheckBox.setSelected( UserSettings.instance.panOnDoubleClick.getDefault( ) );
		deselectVertexWithNewEdgeCheckBox.setSelected( UserSettings.instance.deselectVertexWithNewEdge.getDefault( ) );
		zoomInFactorTextField.setText( UserSettings.instance.zoomInFactor.getDefault( ).toString( ) );
		zoomOutFactorTextField.setText( UserSettings.instance.zoomOutFactor.getDefault( ).toString( ) );
		maximumZoomFactorTextField.setText( UserSettings.instance.maximumZoomFactor.getDefault( ).toString( ) );
		zoomGraphPaddingTextField.setText( UserSettings.instance.zoomGraphPadding.getDefault( ).toString( ) );
		scrollIncrementZoomTextField.setText( UserSettings.instance.scrollIncrementZoom.getDefault( ).toString( ) );
		arrangeCircleRadiusMultiplierTextField.setText( UserSettings.instance.arrangeCircleRadiusMultiplier.getDefault( ).toString( ) );
		arrangeGridSpacingTextField.setText( UserSettings.instance.arrangeGridSpacing.getDefault( ).toString( ) );
		autoArrangeAttractiveForceTextField.setText( UserSettings.instance.autoArrangeAttractiveForce.getDefault( ).toString( ) );
		autoArrangeRepulsiveForceTextField.setText( UserSettings.instance.autoArrangeRepulsiveForce.getDefault( ).toString( ) );
		autoArrangeDecelerationFactorTextField.setText( UserSettings.instance.autoArrangeDecelerationFactor.getDefault( ).toString( ) );
		arrangeContractFactorTextField.setText( UserSettings.instance.arrangeContractFactor.getDefault( ).toString( ) );
		arrangeExpandFactorTextField.setText( UserSettings.instance.arrangeExpandFactor.getDefault( ).toString( ) );
		undoLoggingIntervalTextField.setText( UserSettings.instance.undoLoggingInterval.getDefault( ).toString( ) );
		undoLoggingMaximumTextField.setText( UserSettings.instance.undoLoggingMaximum.getDefault( ).toString( ) );
		useAntiAliasingCheckBox.setSelected( UserSettings.instance.useAntiAliasing.getDefault( ) );
		usePureStrokeCheckBox.setSelected( UserSettings.instance.usePureStroke.getDefault( ) );
		useBicubicInterpolationCheckBox.setSelected( UserSettings.instance.useBicubicInterpolation.getDefault( ) );
		useFractionalMetricsCheckBox.setSelected( UserSettings.instance.useFractionalMetrics.getDefault( ) );
		vertexWeightPrecisionTextField.setText( UserSettings.instance.vertexWeightPrecision.getDefault( ).toString( ) );
		edgeWeightPrecisionTextField.setText( UserSettings.instance.edgeWeightPrecision.getDefault( ).toString( ) );
		mainWindowSizeTextField.setText( UserSettings.instance.mainWindowWidth.getDefault( ) + ", " + UserSettings.instance.mainWindowHeight.getDefault( ) );
		scriptLibraryWindowSizeTextField.setText( UserSettings.instance.scriptLibraryWindowWidth.getDefault( ) + ", " + UserSettings.instance.scriptLibraryWindowHeight.getDefault( ) );
		graphWindowSizeTextField.setText( UserSettings.instance.graphWindowWidth.getDefault( ) + ", " + UserSettings.instance.graphWindowWidth.getDefault( ) );
		cascadeWindowOffsetTextField.setText( UserSettings.instance.cascadeWindowOffset.getDefault( ).toString( ) );
		defaultGraphNameTextField.setText( UserSettings.instance.defaultGraphName.getDefault( ) );
		directedEdgeArrowRatioTextField.setText( UserSettings.instance.directedEdgeArrowRatio.getDefault( ).toString( ) );
		arrowKeyIncrementTextField.setText( UserSettings.instance.arrowKeyIncrement.getDefault( ).toString( ) );
		edgeSnapMarginRatioTextField.setText( UserSettings.instance.edgeSnapMarginRatio.getDefault( ).toString( ) );
		areCloseDistanceTextField.setText( UserSettings.instance.areCloseDistance.getDefault( ).toString( ) );
		paintToolMenuDelayTextField.setText( UserSettings.instance.paintToolMenuDelay.getDefault( ).toString( ) );
		
		elementColorsPanel.removeAll( );
		for( Color color : GlobalSettings.defaultElementColors )
			elementColorsPanel.add( new ColorPicker( color ) );
		elementColorsPanel.updateUI( );
		
		if( elementColorsPanel.getComponentCount( ) > 1 )
			removeButton.setEnabled( true );
		
		for( int i = 0; i < languageComboBox.getItemCount( ); ++i )
			if( ( (LocaleWrapper) languageComboBox.getItemAt( i ) ).locale.toString( ).equals( UserSettings.instance.language.getDefault( ) ) )
			{
				languageComboBox.setSelectedIndex( i );
				break;
			}
	}
	
	public void savePreferences( )
	{
		UserSettings.instance.defaultVertexWeight.set( new Double( defaultVertexWeightTextField.getText( ) ) );
		UserSettings.instance.defaultVertexColor.set( new Integer( defaultVertexColorTextField.getText( ) ) );
		UserSettings.instance.defaultVertexPrefix.set( defaultVertexPrefixTextField.getText( ) );
		UserSettings.instance.defaultVertexRadius.set( new Double( defaultVertexRadiusTextField.getText( ) ) );
		UserSettings.instance.defaultVertexIsSelected.set( defaultVertexIsSelectedCheckBox.isSelected( ) );
		UserSettings.instance.defaultEdgeWeight.set( new Double( defaultEdgeWeightTextField.getText( ) ) );
		UserSettings.instance.defaultEdgeColor.set( new Integer( defaultEdgeColorTextField.getText( ) ) );
		UserSettings.instance.defaultEdgePrefix.set( defaultEdgePrefixTextField.getText( ) );
		UserSettings.instance.defaultEdgeThickness.set( new Double( defaultEdgeThicknessTextField.getText( ) ) );
		UserSettings.instance.defaultEdgeHandleRadiusRatio.set( new Double( defaultEdgeHandleRadiusRatioTextField.getText( ) ) );
		UserSettings.instance.defaultLoopDiameter.set( new Double( defaultLoopDiameterTextField.getText( ) ) );
		UserSettings.instance.defaultEdgeIsSelected.set( defaultEdgeIsSelectedCheckBox.isSelected( ) );
		UserSettings.instance.defaultCaptionText.set( defaultCaptionTextTextField.getText( ) );
		UserSettings.instance.defaultCaptionFontSize.set( new Double( defaultCaptionFontSizeTextField.getText( ) ) );
		UserSettings.instance.defaultCaptionIsSelected.set( defaultCaptionIsSelectedCheckBox.isSelected( ) );
		UserSettings.instance.defaultShowVertexWeights.set( defaultShowVertexWeightsCheckBox.isSelected( ) );
		UserSettings.instance.defaultShowVertexLabels.set( defaultShowVertexLabelsCheckBox.isSelected( ) );
		UserSettings.instance.defaultShowEdgeHandles.set( defaultShowEdgeHandlesCheckBox.isSelected( ) );
		UserSettings.instance.defaultShowEdgeWeights.set( defaultShowEdgeWeightsCheckBox.isSelected( ) );
		UserSettings.instance.defaultShowEdgeLabels.set( defaultShowEdgeLabelsCheckBox.isSelected( ) );
		UserSettings.instance.defaultShowCaptions.set( defaultShowCaptionsCheckBox.isSelected( ) );
		UserSettings.instance.defaultShowCaptionHandles.set( defaultShowCaptionHandlesCheckBox.isSelected( ) );
		UserSettings.instance.defaultShowCaptionEditors.set( defaultShowCaptionEditorsCheckBox.isSelected( ) );
		UserSettings.instance.graphBackground.set( graphBackgroundColorPicker.getColor( ) );
		UserSettings.instance.selectionBoxFill.set( selectionBoxFillColorPicker.getColor( ) );
		UserSettings.instance.selectionBoxLine.set( selectionBoxLineColorPicker.getColor( ) );
		UserSettings.instance.vertexLine.set( vertexLineColorPicker.getColor( ) );
		UserSettings.instance.uncoloredVertexFill.set( uncoloredVertexFillColorPicker.getColor( ) );
		UserSettings.instance.selectedVertexFill.set( selectedVertexFillColorPicker.getColor( ) );
		UserSettings.instance.selectedVertexLine.set( selectedVertexLineColorPicker.getColor( ) );
		UserSettings.instance.draggingEdge.set( draggingEdgeColorPicker.getColor( ) );
		UserSettings.instance.edgeHandle.set( edgeHandleColorPicker.getColor( ) );
		UserSettings.instance.uncoloredEdgeLine.set( uncoloredEdgeLineColorPicker.getColor( ) );
		UserSettings.instance.selectedEdge.set( selectedEdgeColorPicker.getColor( ) );
		UserSettings.instance.selectedEdgeHandle.set( selectedEdgeHandleColorPicker.getColor( ) );
		UserSettings.instance.captionText.set( captionTextColorPicker.getColor( ) );
		UserSettings.instance.captionButtonFill.set( captionButtonFillColorPicker.getColor( ) );
		UserSettings.instance.captionButtonLine.set( captionButtonLineColorPicker.getColor( ) );
		UserSettings.instance.selectedCaptionLine.set( selectedCaptionLineColorPicker.getColor( ) );
		UserSettings.instance.vertexClickMargin.set( new Double( vertexClickMarginTextField.getText( ) ) );
		UserSettings.instance.edgeHandleClickMargin.set( new Double( edgeHandleClickMarginTextField.getText( ) ) );
		UserSettings.instance.captionHandleClickMargin.set( new Double( captionHandleClickMarginTextField.getText( ) ) );
		UserSettings.instance.captionEditorClickMargin.set( new Double( captionEditorClickMarginTextField.getText( ) ) );
		UserSettings.instance.panDecelerationFactor.set( new Double( panDecelerationFactorTextField.getText( ) ) );
		UserSettings.instance.panOnDoubleClick.set( panOnDoubleClickCheckBox.isSelected( ) );
		UserSettings.instance.deselectVertexWithNewEdge.set( deselectVertexWithNewEdgeCheckBox.isSelected( ) );
		UserSettings.instance.zoomInFactor.set( new Double( zoomInFactorTextField.getText( ) ) );
		UserSettings.instance.zoomOutFactor.set( new Double( zoomOutFactorTextField.getText( ) ) );
		UserSettings.instance.maximumZoomFactor.set( new Double( maximumZoomFactorTextField.getText( ) ) );
		UserSettings.instance.zoomGraphPadding.set( new Double( zoomGraphPaddingTextField.getText( ) ) );
		UserSettings.instance.scrollIncrementZoom.set( new Double( scrollIncrementZoomTextField.getText( ) ) );
		UserSettings.instance.arrangeCircleRadiusMultiplier.set( new Double( arrangeCircleRadiusMultiplierTextField.getText( ) ) );
		UserSettings.instance.arrangeGridSpacing.set( new Double( arrangeGridSpacingTextField.getText( ) ) );
		UserSettings.instance.autoArrangeAttractiveForce.set( new Double( autoArrangeAttractiveForceTextField.getText( ) ) );
		UserSettings.instance.autoArrangeRepulsiveForce.set( new Double( autoArrangeRepulsiveForceTextField.getText( ) ) );
		UserSettings.instance.autoArrangeDecelerationFactor.set( new Double( autoArrangeDecelerationFactorTextField.getText( ) ) );
		UserSettings.instance.arrangeContractFactor.set( new Double( arrangeContractFactorTextField.getText( ) ) );
		UserSettings.instance.arrangeExpandFactor.set( new Double( arrangeExpandFactorTextField.getText( ) ) );
		UserSettings.instance.undoLoggingInterval.set( new Integer( undoLoggingIntervalTextField.getText( ) ) );
		UserSettings.instance.undoLoggingMaximum.set( new Integer( undoLoggingMaximumTextField.getText( ) ) );
		UserSettings.instance.useAntiAliasing.set( useAntiAliasingCheckBox.isSelected( ) );
		UserSettings.instance.usePureStroke.set( usePureStrokeCheckBox.isSelected( ) );
		UserSettings.instance.useBicubicInterpolation.set( useBicubicInterpolationCheckBox.isSelected( ) );
		UserSettings.instance.useFractionalMetrics.set( useFractionalMetricsCheckBox.isSelected( ) );
		UserSettings.instance.vertexWeightPrecision.set( new Integer( vertexWeightPrecisionTextField.getText( ) ) );
		UserSettings.instance.edgeWeightPrecision.set( new Integer( edgeWeightPrecisionTextField.getText( ) ) );
		
		Matcher matcher = Pattern.compile( mainWindowSizeTextField.getValidatingExpression( ) ).matcher( mainWindowSizeTextField.getText( ) );
		matcher.find( );
		UserSettings.instance.mainWindowWidth.set( new Integer( matcher.group( 1 ) ) );
		UserSettings.instance.mainWindowHeight.set( new Integer( matcher.group( 2 ) ) );
		
		matcher = Pattern.compile( scriptLibraryWindowSizeTextField.getValidatingExpression( ) ).matcher( scriptLibraryWindowSizeTextField.getText( ) );
		matcher.find( );
		UserSettings.instance.scriptLibraryWindowWidth.set( new Integer( matcher.group( 1 ) ) );
		UserSettings.instance.scriptLibraryWindowHeight.set( new Integer( matcher.group( 2 ) ) );
		
		matcher = Pattern.compile( graphWindowSizeTextField.getValidatingExpression( ) ).matcher( graphWindowSizeTextField.getText( ) );
		matcher.find( );
		UserSettings.instance.graphWindowWidth.set( new Integer( matcher.group( 1 ) ) );
		UserSettings.instance.graphWindowHeight.set( new Integer( matcher.group( 2 ) ) );
		
		UserSettings.instance.cascadeWindowOffset.set( new Integer( cascadeWindowOffsetTextField.getText( ) ) );
		UserSettings.instance.language.set( ( (LocaleWrapper) languageComboBox.getSelectedItem( ) ).locale.toString( ) );
		UserSettings.instance.defaultGraphName.set( defaultGraphNameTextField.getText( ) );
		UserSettings.instance.directedEdgeArrowRatio.set( new Double( directedEdgeArrowRatioTextField.getText( ) ) );
		UserSettings.instance.arrowKeyIncrement.set( new Double( arrowKeyIncrementTextField.getText( ) ) );
		UserSettings.instance.edgeSnapMarginRatio.set( new Double( edgeSnapMarginRatioTextField.getText( ) ) );
		UserSettings.instance.areCloseDistance.set( new Double( areCloseDistanceTextField.getText( ) ) );
		UserSettings.instance.paintToolMenuDelay.set( new Integer( paintToolMenuDelayTextField.getText( ) ) );
		
		UserSettings.instance.elementColors.clear( );
		for( Component component : elementColorsPanel.getComponents( ) )
			if( component instanceof ColorPicker )
				UserSettings.instance.elementColors.add( ( (ColorPicker) component ).getColor( ) );
		
		File userSettingsFile = new File( "UserSettings.json" );
		
		try
		{
			if( !userSettingsFile.exists( ) )
				userSettingsFile.createNewFile( );
			
			FileWriter fileWriter = new FileWriter( userSettingsFile );
			fileWriter.write( UserSettings.instance.toString( ) );
			fileWriter.close( );
		}
		catch( IOException ex )
		{
			DebugUtilities.logException( "An exception occurred while saving preferences to file.", ex );
			JOptionPane.showMessageDialog( null, String.format( StringBundle.get( "preferences_dialog_unable_to_save_error_message" ), userSettingsFile.getAbsolutePath( ) ), GlobalSettings.applicationName, JOptionPane.ERROR_MESSAGE );
		}
	}
	
	public boolean validatePreferences( )
	{
		return ( defaultVertexWeightTextField.isValid( ) && defaultVertexColorTextField.isValid( ) && defaultVertexRadiusTextField.isValid( ) && defaultEdgeWeightTextField.isValid( ) && defaultEdgeColorTextField.isValid( ) && defaultEdgeThicknessTextField.isValid( ) && defaultEdgeHandleRadiusRatioTextField.isValid( ) && defaultLoopDiameterTextField.isValid( ) && defaultCaptionFontSizeTextField.isValid( ) && vertexClickMarginTextField.isValid( ) && edgeHandleClickMarginTextField.isValid( ) && captionHandleClickMarginTextField.isValid( ) && captionEditorClickMarginTextField.isValid( ) && panDecelerationFactorTextField.isValid( ) && zoomInFactorTextField.isValid( ) && zoomOutFactorTextField.isValid( ) && maximumZoomFactorTextField.isValid( ) && zoomGraphPaddingTextField.isValid( ) && scrollIncrementZoomTextField.isValid( ) && arrangeCircleRadiusMultiplierTextField.isValid( ) && arrangeGridSpacingTextField.isValid( ) && autoArrangeAttractiveForceTextField.isValid( ) && autoArrangeRepulsiveForceTextField.isValid( ) && autoArrangeDecelerationFactorTextField.isValid( ) && arrangeContractFactorTextField.isValid( ) && arrangeExpandFactorTextField.isValid( ) && undoLoggingIntervalTextField.isValid( ) && undoLoggingMaximumTextField.isValid( ) && vertexWeightPrecisionTextField.isValid( ) && edgeWeightPrecisionTextField.isValid( ) && mainWindowSizeTextField.isValid( ) && scriptLibraryWindowSizeTextField.isValid( ) && graphWindowSizeTextField.isValid( ) && cascadeWindowOffsetTextField.isValid( ) && directedEdgeArrowRatioTextField.isValid( ) && arrowKeyIncrementTextField.isValid( ) && edgeSnapMarginRatioTextField.isValid( ) && areCloseDistanceTextField.isValid( ) && paintToolMenuDelayTextField.isValid( ) );
	}
}
