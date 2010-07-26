/**
 * PreferencesDialog.java
 */
package edu.belmont.mth.visigraph.gui.dialogs;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;

import edu.belmont.mth.visigraph.gui.controls.ColorPicker;
import edu.belmont.mth.visigraph.gui.controls.ValidatingTextField;
import edu.belmont.mth.visigraph.resources.StringBundle;
import edu.belmont.mth.visigraph.settings.UserSettings;

/**
 * @author Cameron Behar
 *
 */
@SuppressWarnings("serial")
public class PreferencesDialog extends JDialog implements ActionListener
{
	private static PreferencesDialog dialog;
	private static String			 value;
	
	private static ValidatingTextField defaultVertexWeightTextField;
	private static ValidatingTextField defaultVertexColorTextField;
	private static JTextField 		   defaultVertexPrefixTextField;
	private static ValidatingTextField defaultVertexRadiusTextField;
	private static JCheckBox           defaultVertexIsSelectedCheckBox;
	private static ValidatingTextField defaultEdgeWeightTextField;
	private static ValidatingTextField defaultEdgeColorTextField;
	private static JTextField 		   defaultEdgePrefixTextField;
	private static ValidatingTextField defaultEdgeThicknessTextField;
	private static ValidatingTextField defaultEdgeHandleRadiusRatioTextField;
	private static ValidatingTextField defaultLoopDiameterTextField;
	private static JCheckBox           defaultEdgeIsSelectedCheckBox;
	private static JTextField 		   defaultCaptionTextTextField;
	private static ValidatingTextField defaultCaptionFontSizeTextField;
	private static JCheckBox           defaultCaptionIsSelectedCheckBox;
	private static JCheckBox           defaultShowVertexWeightsCheckBox;
	private static JCheckBox           defaultShowVertexLabelsCheckBox;
	private static JCheckBox           defaultShowEdgeHandlesCheckBox;
	private static JCheckBox           defaultShowEdgeWeightsCheckBox;
	private static JCheckBox           defaultShowEdgeLabelsCheckBox;
	private static JCheckBox           defaultShowCaptionsCheckBox;
	private static JCheckBox           defaultShowCaptionHandlesCheckBox;
	private static JCheckBox           defaultShowCaptionEditorsCheckBox;
	private static ColorPicker		   graphBackgroundColorPicker;
	private static ColorPicker 		   selectionBoxFillColorPicker;
	private static ColorPicker 		   selectionBoxLineColorPicker;
	private static ColorPicker		   vertexLineColorPicker;
	private static ColorPicker		   selectedVertexFillColorPicker;
	private static ColorPicker		   selectedVertexLineColorPicker;
	private static ColorPicker 		   draggingEdgeColorPicker;
	private static ColorPicker 		   edgeHandleColorPicker;
	private static ColorPicker 		   selectedEdgeColorPicker;
	private static ColorPicker 	   	   selectedEdgeHandleColorPicker;
	private static ColorPicker 		   captionTextColorPicker;
	private static ColorPicker 		   captionButtonFillColorPicker;
	private static ColorPicker 		   captionButtonLineColorPicker;
	private static ColorPicker 		   selectedCaptionLineColorPicker;
	private static ColorPicker 		   uncoloredElementColorPicker;
	private static JPanel              elementColorsPanel;
	private static JButton			   addButton;
	private static JButton			   removeButton;
	private static ValidatingTextField vertexClickMarginTextField;
	private static ValidatingTextField edgeHandleClickMarginTextField;
	private static ValidatingTextField captionHandleClickMarginTextField;
	private static ValidatingTextField captionEditorClickMarginTextField;
	private static ValidatingTextField panDecelerationFactorTextField;
	private static ValidatingTextField zoomInFactorTextField;
	private static ValidatingTextField zoomOutFactorTextField;
	private static ValidatingTextField maximumZoomFactorTextField;
	private static ValidatingTextField zoomGraphPaddingTextField;
	private static ValidatingTextField scrollIncrementZoomTextField;
	private static ValidatingTextField arrangeCircleRadiusMultiplierTextField;
	private static ValidatingTextField arrangeGridSpacingTextField;
	private static ValidatingTextField autoArrangeAttractiveForceTextField;
	private static ValidatingTextField autoArrangeRepulsiveForceTextField;
	private static ValidatingTextField autoArrangeDecelerationFactorTextField;
	private static ValidatingTextField arrangeContractFactorTextField;
	private static ValidatingTextField arrangeExpandFactorTextField;
	private static JCheckBox           useAntiAliasingCheckBox;
	private static JCheckBox           usePureStrokeCheckBox;
	private static JCheckBox           useBicubicInterpolationCheckBox;
	private static JCheckBox           useFractionalMetricsCheckBox;
	private static ValidatingTextField mainWindowSizeTextField;
	private static ValidatingTextField graphWindowSizeTextField;
	private static ValidatingTextField cascadeWindowOffsetTextField;
	private static JTextField 		   defaultGraphNameTextField;
	private static ValidatingTextField directedEdgeArrowRatioTextField;
	private static ValidatingTextField arrowKeyIncrementTextField;
	private static ValidatingTextField edgeSnapMarginRatioTextField;
	private static ValidatingTextField areCloseDistanceTextField;
	private static ValidatingTextField paintToolMenuDelayTextField;
	private static UserSettings		   userSettings = UserSettings.instance;
	
	private final int[] columnWidths = new int[] { 125, 165, 160 };
	private final Insets fieldMargin = new Insets(2, 2, 2, 7);
	
	public static String showDialog(Component frameComp, Component locationComp)
	{
		Frame frame = JOptionPane.getFrameForComponent(frameComp);
		dialog = new PreferencesDialog(frame, locationComp);
		dialog.setVisible(true);
		return value;
	}
	
	private PreferencesDialog(Frame frame, Component locationComp)
	{
		super(frame, StringBundle.get("preferences_dialog_title"), true);
		this.setResizable(false);
		
		JPanel inputPanel = new JPanel(new BorderLayout());
		
		JTabbedPane tabPanel = new JTabbedPane();
		tabPanel.setPreferredSize(new Dimension(570, 520));
		tabPanel.setBorder(new EmptyBorder(7, 7, 0, 7));
		
		tabPanel.addTab(StringBundle.get("preferences_dialog_defaults_tab"),       initializeDefaultsPanel());
		tabPanel.addTab(StringBundle.get("preferences_dialog_appearances_tab"),    initializeAppearancesPanel());
		tabPanel.addTab(StringBundle.get("preferences_dialog_under_the_hood_tab"), initializeUnderTheHoodPanel());
		
		inputPanel.add(tabPanel, BorderLayout.CENTER);
		
		loadPreferences();
		
		//Create and initialize the buttons
		final JButton okButton = new JButton(StringBundle.get("ok_button_text"));
		okButton.setPreferredSize(new Dimension(80, 28));
		okButton.setActionCommand("Ok");
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);
		
		JButton cancelButton = new JButton(StringBundle.get("cancel_button_text"));
		cancelButton.setPreferredSize(new Dimension(80, 28));
		cancelButton.addActionListener(this);
		
		//Lay out the buttons from left to right
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(-2, 9, 9, 13));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(cancelButton);
		
		inputPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		//Put everything together, using the content pane's BorderLayout
		Container contentPanel = getContentPane();
		contentPanel.setLayout(new BorderLayout(9, 9));
		contentPanel.add(inputPanel, BorderLayout.CENTER);
		contentPanel.add(buttonPanel, BorderLayout.PAGE_END);
		
		Dimension size = this.getPreferredSize();
		size.width += 40;
		size.height += 40;
		setPreferredSize(size);
		
		pack();
		setLocationRelativeTo(locationComp);
		value = null;
	}
	
	private JScrollPane initializeDefaultsPanel()
	{
		JPanel panel = new JPanel();
		if(System.getProperty("os.name").startsWith("Windows"))
			panel.setBackground(Color.white);
		
		Header vertexDefaultsHeader = new Header(StringBundle.get("preferences_dialog_default_vertex_properties_heading"));
		FieldLabel defaultVertexWeightLabel = new FieldLabel(StringBundle.get("preferences_dialog_default_vertex_weight_label")); defaultVertexWeightTextField = new ValidatingTextField(8, "-?(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?");	defaultVertexWeightTextField.setMargin(fieldMargin); defaultVertexWeightTextField.setMaximumSize(new Dimension(70, 100));
		FieldLabel defaultVertexColorLabel  = new FieldLabel(StringBundle.get("preferences_dialog_default_vertex_color_label"));  defaultVertexColorTextField  = new ValidatingTextField(8, "-1|\\d+");													defaultVertexColorTextField .setMargin(fieldMargin); defaultVertexColorTextField .setMaximumSize(new Dimension(70, 100));
		FieldLabel defaultVertexPrefixLabel = new FieldLabel(StringBundle.get("preferences_dialog_default_vertex_prefix_label")); defaultVertexPrefixTextField = new JTextField(8);																		defaultVertexPrefixTextField.setMargin(fieldMargin); defaultVertexPrefixTextField.setMaximumSize(new Dimension(70, 100));
		FieldLabel defaultVertexRadiusLabel = new FieldLabel(StringBundle.get("preferences_dialog_default_vertex_radius_label")); defaultVertexRadiusTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?");	defaultVertexRadiusTextField.setMargin(fieldMargin); defaultVertexRadiusTextField.setMaximumSize(new Dimension(70, 100));
		FieldLabel defaultVertexIsSelectedLabel = new FieldLabel(StringBundle.get("preferences_dialog_default_vertex_is_selected_label"));
		defaultVertexIsSelectedCheckBox = new JCheckBox();
		defaultVertexIsSelectedCheckBox.setBackground(panel.getBackground());
		defaultVertexIsSelectedCheckBox.setMinimumSize(new Dimension(32, 26));
		
		Header edgeDefaultsHeader = new Header(StringBundle.get("preferences_dialog_default_edge_properties_heading"));
		FieldLabel defaultEdgeWeightLabel    = new FieldLabel(StringBundle.get("preferences_dialog_default_edge_weight_label"));        defaultEdgeWeightTextField    = new ValidatingTextField(8, "-?(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); defaultEdgeWeightTextField   .setMargin(fieldMargin); defaultEdgeWeightTextField   .setMaximumSize(new Dimension(70, 100));
		FieldLabel defaultEdgeColorLabel     = new FieldLabel(StringBundle.get("preferences_dialog_default_edge_color_label"));         defaultEdgeColorTextField     = new ValidatingTextField(8, "-1|\\d+");													 defaultEdgeColorTextField    .setMargin(fieldMargin); defaultEdgeColorTextField    .setMaximumSize(new Dimension(70, 100));
		FieldLabel defaultEdgePrefixLabel    = new FieldLabel(StringBundle.get("preferences_dialog_default_edge_prefix_label"));        defaultEdgePrefixTextField    = new JTextField(8);																		 defaultEdgePrefixTextField   .setMargin(fieldMargin); defaultEdgePrefixTextField   .setMaximumSize(new Dimension(70, 100));
		FieldLabel defaultEdgeThicknessLabel = new FieldLabel(StringBundle.get("preferences_dialog_default_edge_thickness_label"));     defaultEdgeThicknessTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?");	 defaultEdgeThicknessTextField.setMargin(fieldMargin); defaultEdgeThicknessTextField.setMaximumSize(new Dimension(70, 100));
		FieldLabel defaultEdgeHandleRadiusRatioLabel = new FieldLabel(StringBundle.get("preferences_dialog_default_edge_handle_radius_ratio_label")); defaultEdgeHandleRadiusRatioTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); defaultEdgeHandleRadiusRatioTextField.setMargin(fieldMargin); defaultEdgeHandleRadiusRatioTextField.setMaximumSize(new Dimension(70, 100));
		FieldLabel defaultLoopDiameterLabel  = new FieldLabel(StringBundle.get("preferences_dialog_default_loop_diameter_label")); defaultLoopDiameterTextField  = new ValidatingTextField(8, "-?(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); defaultLoopDiameterTextField .setMargin(fieldMargin); defaultLoopDiameterTextField .setMaximumSize(new Dimension(70, 100));
		FieldLabel defaultEdgeIsSelectedLabel = new FieldLabel(StringBundle.get("preferences_dialog_default_edge_is_selected_label"));
		defaultEdgeIsSelectedCheckBox     = new JCheckBox();
		defaultEdgeIsSelectedCheckBox.setBackground(panel.getBackground());
		defaultEdgeIsSelectedCheckBox.setMinimumSize(new Dimension(32, 26));
		
		Header captionDefaultsHeader = new Header(StringBundle.get("preferences_dialog_default_caption_properties_heading"));
		FieldLabel defaultCaptionTextLabel     = new FieldLabel(StringBundle.get("preferences_dialog_default_caption_text_label"));      defaultCaptionTextTextField     = new JTextField(20);                                                   defaultCaptionTextTextField    .setMargin(fieldMargin); defaultCaptionTextTextField    .setMaximumSize(new Dimension(175, defaultVertexWeightTextField.getPreferredSize().height));
		FieldLabel defaultCaptionFontSizeLabel = new FieldLabel(StringBundle.get("preferences_dialog_default_caption_font_size_label")); defaultCaptionFontSizeTextField = new ValidatingTextField(8, "(?:\\d{1,3}(\\.\\d*)?|\\d{0,3}\\.\\d+)"); defaultCaptionFontSizeTextField.setMargin(fieldMargin); defaultCaptionFontSizeTextField.setMaximumSize(new Dimension(70, defaultVertexWeightTextField.getPreferredSize().height));
		FieldLabel defaultCaptionIsSelectedLabel = new FieldLabel(StringBundle.get("preferences_dialog_default_caption_is_selected_label"));
		defaultCaptionIsSelectedCheckBox = new JCheckBox();
		defaultCaptionIsSelectedCheckBox.setBackground(panel.getBackground());
		defaultCaptionIsSelectedCheckBox.setMinimumSize(new Dimension(32, 26));

		Header viewportDefaultsHeader = new Header(StringBundle.get("preferences_dialog_default_viewport_settings_heading"));
		FieldLabel defaultShowVertexWeightsLabel = new FieldLabel(StringBundle.get("preferences_dialog_default_show_vertex_weights_label"));
		defaultShowVertexWeightsCheckBox = new JCheckBox();
		defaultShowVertexWeightsCheckBox.setBackground(panel.getBackground());
		defaultShowVertexWeightsCheckBox.setMinimumSize(new Dimension(32, 26));
		FieldLabel defaultShowVertexLabelsLabel = new FieldLabel(StringBundle.get("preferences_dialog_default_show_vertex_labels_label"));
		defaultShowVertexLabelsCheckBox = new JCheckBox();
		defaultShowVertexLabelsCheckBox.setBackground(panel.getBackground());
		defaultShowVertexLabelsCheckBox.setMinimumSize(new Dimension(32, 26));
		FieldLabel defaultShowEdgeHandlesLabel = new FieldLabel(StringBundle.get("preferences_dialog_default_show_edge_handles_label"));
		defaultShowEdgeHandlesCheckBox = new JCheckBox();
		defaultShowEdgeHandlesCheckBox.setBackground(panel.getBackground());
		defaultShowEdgeHandlesCheckBox.setMinimumSize(new Dimension(32, 26));
		FieldLabel defaultShowEdgeWeightsLabel = new FieldLabel(StringBundle.get("preferences_dialog_default_show_edge_weights_label"));
		defaultShowEdgeWeightsCheckBox = new JCheckBox();
		defaultShowEdgeWeightsCheckBox.setBackground(panel.getBackground());
		defaultShowEdgeWeightsCheckBox.setMinimumSize(new Dimension(32, 26));
		FieldLabel defaultShowEdgeLabelsLabel = new FieldLabel(StringBundle.get("preferences_dialog_default_show_edge_labels_label"));
		defaultShowEdgeLabelsCheckBox = new JCheckBox();
		defaultShowEdgeLabelsCheckBox.setBackground(panel.getBackground());
		defaultShowEdgeLabelsCheckBox.setMinimumSize(new Dimension(32, 26));
		FieldLabel defaultShowCaptionsLabel = new FieldLabel(StringBundle.get("preferences_dialog_default_show_captions_label"));
		defaultShowCaptionsCheckBox = new JCheckBox();
		defaultShowCaptionsCheckBox.setBackground(panel.getBackground());
		defaultShowCaptionsCheckBox.setMinimumSize(new Dimension(32, 26));
		FieldLabel defaultShowCaptionHandlesLabel = new FieldLabel(StringBundle.get("preferences_dialog_default_show_caption_handles_label"));
		defaultShowCaptionHandlesCheckBox = new JCheckBox();
		defaultShowCaptionHandlesCheckBox.setBackground(panel.getBackground());
		defaultShowCaptionHandlesCheckBox.setMinimumSize(new Dimension(32, 26));
		FieldLabel defaultShowCaptionEditorsLabel = new FieldLabel(StringBundle.get("preferences_dialog_default_show_caption_editors_label"));
		defaultShowCaptionEditorsCheckBox = new JCheckBox();
		defaultShowCaptionEditorsCheckBox.setBackground(panel.getBackground());
		defaultShowCaptionEditorsCheckBox.setMinimumSize(new Dimension(32, 26));
		
		Component col0Padding = Box.createRigidArea(new Dimension(columnWidths[0], 7));
		Component col1Padding = Box.createRigidArea(new Dimension(columnWidths[1], 7));
		Component col2Padding = Box.createRigidArea(new Dimension(columnWidths[2], 7));
		
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(vertexDefaultsHeader)
					.addComponent(edgeDefaultsHeader)
					.addComponent(captionDefaultsHeader)
					.addComponent(viewportDefaultsHeader)
					.addComponent(col0Padding))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(defaultVertexWeightLabel)
					.addComponent(defaultVertexColorLabel)
					.addComponent(defaultVertexPrefixLabel)
					.addComponent(defaultVertexRadiusLabel)
					.addComponent(defaultVertexIsSelectedLabel)
					.addComponent(defaultEdgeWeightLabel)
					.addComponent(defaultEdgeColorLabel)
					.addComponent(defaultEdgePrefixLabel)
					.addComponent(defaultEdgeThicknessLabel)
					.addComponent(defaultEdgeHandleRadiusRatioLabel)
					.addComponent(defaultLoopDiameterLabel)
					.addComponent(defaultEdgeIsSelectedLabel)
					.addComponent(defaultCaptionTextLabel)
					.addComponent(defaultCaptionFontSizeLabel)
					.addComponent(defaultCaptionIsSelectedLabel)
					.addComponent(defaultShowEdgeHandlesLabel)
					.addComponent(defaultShowEdgeWeightsLabel)
					.addComponent(defaultShowEdgeLabelsLabel)
					.addComponent(defaultShowVertexWeightsLabel)
					.addComponent(defaultShowVertexLabelsLabel)
					.addComponent(defaultShowCaptionsLabel)
					.addComponent(defaultShowCaptionHandlesLabel)
					.addComponent(defaultShowCaptionEditorsLabel)
					.addComponent(col1Padding))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(defaultVertexWeightTextField)
					.addComponent(defaultVertexColorTextField)
					.addComponent(defaultVertexPrefixTextField)
					.addComponent(defaultVertexRadiusTextField)
					.addComponent(defaultVertexIsSelectedCheckBox)
					.addComponent(defaultEdgeWeightTextField)
					.addComponent(defaultEdgeColorTextField)
					.addComponent(defaultEdgePrefixTextField)
					.addComponent(defaultEdgeThicknessTextField)
					.addComponent(defaultEdgeHandleRadiusRatioTextField)
					.addComponent(defaultLoopDiameterTextField)
					.addComponent(defaultEdgeIsSelectedCheckBox)
					.addComponent(defaultCaptionTextTextField)
					.addComponent(defaultCaptionFontSizeTextField)
					.addComponent(defaultCaptionIsSelectedCheckBox)
					.addComponent(defaultShowEdgeHandlesCheckBox)
					.addComponent(defaultShowEdgeWeightsCheckBox)
					.addComponent(defaultShowEdgeLabelsCheckBox)
					.addComponent(defaultShowVertexWeightsCheckBox)
					.addComponent(defaultShowVertexLabelsCheckBox)
					.addComponent(defaultShowCaptionsCheckBox)
					.addComponent(defaultShowCaptionHandlesCheckBox)
					.addComponent(defaultShowCaptionEditorsCheckBox)
					.addComponent(col2Padding)));
		
		layout.setVerticalGroup(
			layout.createSequentialGroup()
			 	.addComponent(vertexDefaultsHeader)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultVertexWeightLabel).addComponent(defaultVertexWeightTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultVertexColorLabel).addComponent(defaultVertexColorTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultVertexPrefixLabel).addComponent(defaultVertexPrefixTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultVertexRadiusLabel).addComponent(defaultVertexRadiusTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultVertexIsSelectedLabel).addComponent(defaultVertexIsSelectedCheckBox))
			 	.addComponent(edgeDefaultsHeader)
			 	.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultEdgeWeightLabel).addComponent(defaultEdgeWeightTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultEdgeColorLabel).addComponent(defaultEdgeColorTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultEdgePrefixLabel).addComponent(defaultEdgePrefixTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultEdgeThicknessLabel).addComponent(defaultEdgeThicknessTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultEdgeHandleRadiusRatioLabel).addComponent(defaultEdgeHandleRadiusRatioTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultLoopDiameterLabel).addComponent(defaultLoopDiameterTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultEdgeIsSelectedLabel).addComponent(defaultEdgeIsSelectedCheckBox))
				.addComponent(captionDefaultsHeader)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultCaptionTextLabel).addComponent(defaultCaptionTextTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultCaptionFontSizeLabel).addComponent(defaultCaptionFontSizeTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultCaptionIsSelectedLabel).addComponent(defaultCaptionIsSelectedCheckBox))
				.addComponent(viewportDefaultsHeader)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultShowEdgeHandlesLabel).addComponent(defaultShowEdgeHandlesCheckBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultShowEdgeWeightsLabel).addComponent(defaultShowEdgeWeightsCheckBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultShowEdgeLabelsLabel).addComponent(defaultShowEdgeLabelsCheckBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultShowVertexWeightsLabel).addComponent(defaultShowVertexWeightsCheckBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultShowVertexLabelsLabel).addComponent(defaultShowVertexLabelsCheckBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultShowCaptionsLabel).addComponent(defaultShowCaptionsCheckBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultShowCaptionHandlesLabel).addComponent(defaultShowCaptionHandlesCheckBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultShowCaptionEditorsLabel).addComponent(defaultShowCaptionEditorsCheckBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(col0Padding).addComponent(col1Padding).addComponent(col2Padding)));
		
		JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(null);
		
		return scrollPane;
	}
	
	private JScrollPane initializeAppearancesPanel()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		if(System.getProperty("os.name").startsWith("Windows"))
			panel.setBackground(Color.white);
		
		Header graphColorsHeader = new Header(StringBundle.get("preferences_dialog_graph_colors_heading"));
		FieldLabel graphBackgroundLabel  = new FieldLabel(StringBundle.get("preferences_dialog_graph_background_label"));         graphBackgroundColorPicker  = new ColorPicker();
		FieldLabel selectionBoxFillLabel = new FieldLabel(StringBundle.get("preferences_dialog_graph_selection_box_fill_label")); selectionBoxFillColorPicker = new ColorPicker();
		FieldLabel selectionBoxLineLabel = new FieldLabel(StringBundle.get("preferences_dialog_graph_selection_box_line_label")); selectionBoxLineColorPicker = new ColorPicker();
		
		Header vertexColorsHeader = new Header(StringBundle.get("preferences_dialog_vertex_colors_heading"));
		FieldLabel vertexLineLabel         = new FieldLabel(StringBundle.get("preferences_dialog_vertex_line_label"));          vertexLineColorPicker         = new ColorPicker();
		FieldLabel selectedVertexFillLabel = new FieldLabel(StringBundle.get("preferences_dialog_vertex_selected_fill_label")); selectedVertexFillColorPicker = new ColorPicker();
		FieldLabel selectedVertexLineLabel = new FieldLabel(StringBundle.get("preferences_dialog_vertex_selected_line_label")); selectedVertexLineColorPicker = new ColorPicker();
			
		Header edgeColorsHeader = new Header(StringBundle.get("preferences_dialog_edge_colors_heading"));
		FieldLabel draggingEdgeLabel        = new FieldLabel(StringBundle.get("preferences_dialog_edge_dragging_label"));         draggingEdgeColorPicker        = new ColorPicker();
		FieldLabel edgeHandleLabel 			= new FieldLabel(StringBundle.get("preferences_dialog_edge_uncolored_handle_label")); edgeHandleColorPicker		  = new ColorPicker();
		FieldLabel selectedEdgeLabel        = new FieldLabel(StringBundle.get("preferences_dialog_edge_selected_label"));         selectedEdgeColorPicker        = new ColorPicker();
		FieldLabel selectedEdgeHandleLabel  = new FieldLabel(StringBundle.get("preferences_dialog_edge_selected_handle_label"));  selectedEdgeHandleColorPicker  = new ColorPicker();
			
		Header captionColorsHeader = new Header(StringBundle.get("preferences_dialog_caption_colors_heading"));
		FieldLabel captionTextLabel         = new FieldLabel(StringBundle.get("preferences_dialog_caption_text_label"));        captionTextColorPicker         = new ColorPicker();
		FieldLabel captionButtonFillLabel   = new FieldLabel(StringBundle.get("preferences_dialog_caption_button_fill_label")); captionButtonFillColorPicker   = new ColorPicker();
		FieldLabel captionButtonLineLabel   = new FieldLabel(StringBundle.get("preferences_dialog_caption_button_line_label")); captionButtonLineColorPicker   = new ColorPicker();
		FieldLabel selectedCaptionLineLabel = new FieldLabel(StringBundle.get("preferences_dialog_caption_selected_line_label"));       selectedCaptionLineColorPicker = new ColorPicker();
			
		Header sharedColorsHeader = new Header(StringBundle.get("preferences_dialog_shared_colors_heading"));
		FieldLabel uncoloredElementLabel = new FieldLabel(StringBundle.get("preferences_dialog_shared_uncolored_element_label")); uncoloredElementColorPicker = new ColorPicker();
		FieldLabel elementColorsLabel    = new FieldLabel(StringBundle.get("preferences_dialog_shared_colored_elements_label"));
		
		elementColorsPanel = new JPanel(new LayoutManager()
		{
			int hgap = 0, vgap = 6;
			
			@Override
			public void addLayoutComponent(String arg0, Component arg1)
			{ }

			@Override
			public void layoutContainer(Container target)
			{
				synchronized (target.getTreeLock())
				{
					Insets insets = target.getInsets();
					int maxheight = target.getHeight() - (insets.top + insets.bottom + vgap * 2);
					int nmembers = target.getComponentCount();
					int x = insets.left + hgap, y = 0;
					int colw = 0, start = 0;
					
					boolean ltr = target.getComponentOrientation().isLeftToRight();
					
					for (int i = 0; i < nmembers; i++)
					{
						Component m = target.getComponent(i);
						if (m.isVisible())
						{
							Dimension d = m.getPreferredSize();

							m.setSize(d.width, d.height);
							
							if ((y == 0) || ((y + d.height) <= maxheight))
							{
								if (y > 0)
									y += vgap;
								
								y += d.height;
								colw = Math.max(colw, d.width);
							}
							else
							{
								moveComponents(target, insets.left + hgap, y, maxheight - x, colw, start, i, ltr);
								moveComponents(target, x, insets.top + vgap, colw, maxheight - y, start, i, ltr);
								y = d.height;
								x += hgap + colw;
								colw = d.width;
								start = i;
							}
						}
					}
					moveComponents(target, x, insets.top + vgap, colw, maxheight - y, start, nmembers, ltr);
				}
			}

			@Override
			public Dimension minimumLayoutSize(Container target)
			{
				synchronized (target.getTreeLock())
				{
					Dimension dim = new Dimension(0, 0);
					int nmembers = target.getComponentCount();
					
					for (int i = 0; i < nmembers; i++)
					{
						Component m = target.getComponent(i);
						if (m.isVisible())
						{
							Dimension d = m.getMinimumSize();
							dim.width = Math.max(dim.width, d.width);
							if (i > 0) dim.height += vgap;

							dim.height += d.height;
						}
					}
					Insets insets = target.getInsets();
					dim.width += insets.left + insets.right + hgap * 2;
					dim.height += insets.top + insets.bottom + vgap * 2;
					return dim;
				}
			}

			@Override
			public Dimension preferredLayoutSize(Container target)
			{
				synchronized (target.getTreeLock())
				{
					Dimension dim = new Dimension(0, 0);
					int nmembers = target.getComponentCount();
					boolean firstVisibleComponent = true;
					
					for (int i = 0; i < nmembers; i++)
					{
						Component m = target.getComponent(i);
						if (m.isVisible())
						{
							Dimension d = m.getPreferredSize();
							dim.width = Math.max(dim.width, d.width);
							if (firstVisibleComponent) firstVisibleComponent = false;
							else dim.height += vgap;
							
							dim.height += d.height;
						}
					}
					
					Insets insets = target.getInsets();
					dim.width += insets.left + insets.right + hgap * 2;
					dim.height += insets.top + insets.bottom + vgap * 2;
					return dim;
				}
			}

			@Override
			public void removeLayoutComponent(Component arg0)
			{ }
			
			private void moveComponents(Container target, int x, int y, int width, int height, int colStart, int colEnd, boolean ltr)
			{
				synchronized (target.getTreeLock())
				{
					y += height / 2;
					for (int i = colStart; i < colEnd; i++)
					{
						Component m = target.getComponent(i);
						if (m.isVisible())
						{
							if (ltr) m.setLocation(x + (width - m.getWidth()) / 2, y);
							else     m.setLocation(x + (width - m.getWidth()) / 2, target.getHeight() - y - m.getHeight());

							y += m.getHeight() + vgap;
						}
					}
				}
			}
		} );
		elementColorsPanel.setBackground(panel.getBackground());
		elementColorsPanel.setBorder(BorderFactory.createEmptyBorder(-6, 0, 0, 0));
		
		addButton = new JButton(StringBundle.get("preferences_dialog_add_button_text"));
		addButton.setPreferredSize(new Dimension(80, 28));
		
		removeButton = new JButton(StringBundle.get("preferences_dialog_remove_button_text"));
		removeButton.setPreferredSize(new Dimension(80, 28));
		removeButton.setEnabled(false);
		
		addButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				ColorPicker picker = new ColorPicker();
				elementColorsPanel.add(picker);
				removeButton.setEnabled(true);
				elementColorsPanel.revalidate();
			}
		} );
		removeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				elementColorsPanel.remove(elementColorsPanel.getComponentCount() - 1);
				if(elementColorsPanel.getComponentCount() == 1) removeButton.setEnabled(false);
				elementColorsPanel.revalidate();
			}
		} );
		
		ColorPicker elementColorColorPicker = new ColorPicker();
		elementColorsPanel.add(elementColorColorPicker);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(2, 9, 0, 13));
		buttonPanel.setBackground(panel.getBackground());
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(addButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(removeButton);
		
		Component col0Padding = Box.createRigidArea(new Dimension(columnWidths[0], 7));
		Component col1Padding = Box.createRigidArea(new Dimension(columnWidths[1], 7));
		Component col2Padding = Box.createRigidArea(new Dimension(columnWidths[2], 7));
		
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(graphColorsHeader)
					.addComponent(vertexColorsHeader)
					.addComponent(edgeColorsHeader)
					.addComponent(captionColorsHeader)
					.addComponent(sharedColorsHeader)
					.addComponent(col0Padding))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(graphBackgroundLabel)
					.addComponent(selectionBoxFillLabel)
					.addComponent(selectionBoxLineLabel)
					.addComponent(vertexLineLabel)
					.addComponent(selectedVertexFillLabel)
					.addComponent(selectedVertexLineLabel)
					.addComponent(draggingEdgeLabel)
					.addComponent(edgeHandleLabel)
					.addComponent(selectedEdgeLabel)
					.addComponent(selectedEdgeHandleLabel)
					.addComponent(captionTextLabel)
					.addComponent(captionButtonFillLabel)
					.addComponent(captionButtonLineLabel)
					.addComponent(selectedCaptionLineLabel)
					.addComponent(uncoloredElementLabel)
					.addComponent(elementColorsLabel)
					.addComponent(col1Padding))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(graphBackgroundColorPicker)
					.addComponent(selectionBoxFillColorPicker)
					.addComponent(selectionBoxLineColorPicker)
					.addComponent(vertexLineColorPicker)
					.addComponent(selectedVertexFillColorPicker)
					.addComponent(selectedVertexLineColorPicker)
					.addComponent(draggingEdgeColorPicker)
					.addComponent(edgeHandleColorPicker)
					.addComponent(selectedEdgeColorPicker)
					.addComponent(selectedEdgeHandleColorPicker)
					.addComponent(captionTextColorPicker)
					.addComponent(captionButtonFillColorPicker)
					.addComponent(captionButtonLineColorPicker)
					.addComponent(selectedCaptionLineColorPicker)
					.addComponent(uncoloredElementColorPicker)
					.addComponent(elementColorsPanel)
					.addComponent(buttonPanel)
					.addComponent(col2Padding)));
		
		layout.setVerticalGroup(
			layout.createSequentialGroup()
			 	.addComponent(graphColorsHeader)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(graphBackgroundLabel).addComponent(graphBackgroundColorPicker))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(selectionBoxFillLabel).addComponent(selectionBoxFillColorPicker))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(selectionBoxLineLabel).addComponent(selectionBoxLineColorPicker))
				.addComponent(vertexColorsHeader)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(vertexLineLabel).addComponent(vertexLineColorPicker))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(selectedVertexFillLabel).addComponent(selectedVertexFillColorPicker))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(selectedVertexLineLabel).addComponent(selectedVertexLineColorPicker))
				.addComponent(edgeColorsHeader)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(draggingEdgeLabel).addComponent(draggingEdgeColorPicker))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(edgeHandleLabel).addComponent(edgeHandleColorPicker))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(selectedEdgeLabel).addComponent(selectedEdgeColorPicker))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(selectedEdgeHandleLabel).addComponent(selectedEdgeHandleColorPicker))
				.addComponent(captionColorsHeader)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(captionTextLabel).addComponent(captionTextColorPicker))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(captionButtonFillLabel).addComponent(captionButtonFillColorPicker))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(captionButtonLineLabel).addComponent(captionButtonLineColorPicker))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(selectedCaptionLineLabel).addComponent(selectedCaptionLineColorPicker))
				.addComponent(sharedColorsHeader)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(uncoloredElementLabel).addComponent(uncoloredElementColorPicker))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(elementColorsLabel).addComponent(elementColorsPanel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(buttonPanel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(col0Padding).addComponent(col1Padding).addComponent(col2Padding)));
		
		JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(null);
		
		return scrollPane;
	}
	
	private JScrollPane initializeUnderTheHoodPanel()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		if(System.getProperty("os.name").startsWith("Windows"))
			panel.setBackground(Color.white);
		
		Header clickingBehaviorHeader = new Header(StringBundle.get("preferences_dialog_clicking_behavior_heading"));
		FieldLabel vertexClickMarginLabel        = new FieldLabel(StringBundle.get("preferences_dialog_vertex_click_margin_label"));         vertexClickMarginTextField        = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?");  vertexClickMarginTextField       .setMargin(fieldMargin); vertexClickMarginTextField       .setMaximumSize(new Dimension(70, 100));
		FieldLabel edgeHandleClickMarginLabel    = new FieldLabel(StringBundle.get("preferences_dialog_edge_handle_click_margin_label"));    edgeHandleClickMarginTextField    = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?");  edgeHandleClickMarginTextField   .setMargin(fieldMargin); edgeHandleClickMarginTextField   .setMaximumSize(new Dimension(70, 100));
		FieldLabel captionHandleClickMarginLabel = new FieldLabel(StringBundle.get("preferences_dialog_caption_handle_click_margin_label")); captionHandleClickMarginTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?");  captionHandleClickMarginTextField.setMargin(fieldMargin); captionHandleClickMarginTextField.setMaximumSize(new Dimension(70, 100));
		FieldLabel captionEditorClickMarginLabel = new FieldLabel(StringBundle.get("preferences_dialog_caption_editor_click_margin_label")); captionEditorClickMarginTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?");  captionEditorClickMarginTextField.setMargin(fieldMargin); captionEditorClickMarginTextField.setMaximumSize(new Dimension(70, 100));
		FieldLabel panDecelerationFactorLabel    = new FieldLabel(StringBundle.get("preferences_dialog_pan_deceleration_factor_label"));     panDecelerationFactorTextField    = new ValidatingTextField(8, "-(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); panDecelerationFactorTextField   .setMargin(fieldMargin); panDecelerationFactorTextField   .setMaximumSize(new Dimension(70, 100));
		
		Header zoomingBehaviorHeader = new Header(StringBundle.get("preferences_dialog_zooming_behavior_heading"));
		FieldLabel zoomInFactorLabel        = new FieldLabel(StringBundle.get("preferences_dialog_zoom_in_factor_label"));        zoomInFactorTextField        = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); zoomInFactorTextField       .setMargin(fieldMargin); zoomInFactorTextField       .setMaximumSize(new Dimension(70, 100));
		FieldLabel zoomOutFactorLabel       = new FieldLabel(StringBundle.get("preferences_dialog_zoom_out_factor_label"));       zoomOutFactorTextField       = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); zoomOutFactorTextField      .setMargin(fieldMargin); zoomOutFactorTextField      .setMaximumSize(new Dimension(70, 100));
		FieldLabel maximumZoomFactorLabel   = new FieldLabel(StringBundle.get("preferences_dialog_maximum_zoom_factor_label"));   maximumZoomFactorTextField   = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); maximumZoomFactorTextField  .setMargin(fieldMargin); maximumZoomFactorTextField  .setMaximumSize(new Dimension(70, 100));
		FieldLabel zoomGraphPaddingLabel    = new FieldLabel(StringBundle.get("preferences_dialog_zoom_fit_padding_label"));      zoomGraphPaddingTextField    = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); zoomGraphPaddingTextField   .setMargin(fieldMargin); zoomGraphPaddingTextField   .setMaximumSize(new Dimension(70, 100));
		FieldLabel scrollZoomIncrementLabel = new FieldLabel(StringBundle.get("preferences_dialog_scroll_zoom_increment_label")); scrollIncrementZoomTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); scrollIncrementZoomTextField.setMargin(fieldMargin); scrollIncrementZoomTextField.setMaximumSize(new Dimension(70, 100));
		
		Header arrangingBehaviorHeader = new Header(StringBundle.get("preferences_dialog_arranging_behavior_heading"));
		FieldLabel arrangeCircleRadiusMultiplierLabel = new FieldLabel(StringBundle.get("preferences_dialog_arrange_circle_radius_multiplier_label")); arrangeCircleRadiusMultiplierTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); arrangeCircleRadiusMultiplierTextField.setMargin(fieldMargin); arrangeCircleRadiusMultiplierTextField.setMaximumSize(new Dimension(70, 100));
		FieldLabel arrangeGridSpacingLabel            = new FieldLabel(StringBundle.get("preferences_dialog_arrange_grid_spacing_label"));             arrangeGridSpacingTextField            = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); arrangeGridSpacingTextField           .setMargin(fieldMargin); arrangeGridSpacingTextField           .setMaximumSize(new Dimension(70, 100));
		FieldLabel autoArrangeAttractiveForceLabel    = new FieldLabel(StringBundle.get("preferences_dialog_auto_arrange_attractive_force_label"));    autoArrangeAttractiveForceTextField    = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); autoArrangeAttractiveForceTextField   .setMargin(fieldMargin); autoArrangeAttractiveForceTextField   .setMaximumSize(new Dimension(70, 100));
		FieldLabel autoArrangeRepulsiveForceLabel     = new FieldLabel(StringBundle.get("preferences_dialog_auto_arrange_repulsive_force_label"));     autoArrangeRepulsiveForceTextField     = new ValidatingTextField(8, "-(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); autoArrangeRepulsiveForceTextField   .setMargin(fieldMargin); autoArrangeRepulsiveForceTextField    .setMaximumSize(new Dimension(70, 100));
		FieldLabel autoArrangeDecelerationFactorLabel = new FieldLabel(StringBundle.get("preferences_dialog_auto_arrange_deceleration_factor_label")); autoArrangeDecelerationFactorTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); autoArrangeDecelerationFactorTextField.setMargin(fieldMargin); autoArrangeDecelerationFactorTextField.setMaximumSize(new Dimension(70, 100));
		FieldLabel arrangeContractFactorLabel         = new FieldLabel(StringBundle.get("preferences_dialog_auto_arrange_contract_factor_label"));     arrangeContractFactorTextField         = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); arrangeContractFactorTextField        .setMargin(fieldMargin); arrangeContractFactorTextField        .setMaximumSize(new Dimension(70, 100));
		FieldLabel arrangeExpandFactorLabel           = new FieldLabel(StringBundle.get("preferences_dialog_arrange_expand_factor_label"));            arrangeExpandFactorTextField           = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); arrangeExpandFactorTextField          .setMargin(fieldMargin); arrangeExpandFactorTextField          .setMaximumSize(new Dimension(70, 100));
		
		Header renderingSettingsHeader = new Header(StringBundle.get("preferences_dialog_rendering_settings_heading"));
		FieldLabel useAntiAliasingLabel = new FieldLabel(StringBundle.get("preferences_dialog_use_anti_aliasing_label"));
		useAntiAliasingCheckBox = new JCheckBox();
		useAntiAliasingCheckBox.setBackground(panel.getBackground());
		useAntiAliasingCheckBox.setMinimumSize(new Dimension(32, 26));
		FieldLabel usePureStrokeLabel = new FieldLabel(StringBundle.get("preferences_dialog_use_pure_stroke_label"));
		usePureStrokeCheckBox = new JCheckBox();
		usePureStrokeCheckBox.setBackground(panel.getBackground());
		usePureStrokeCheckBox.setMinimumSize(new Dimension(32, 26));
		FieldLabel useBicubicInterpolationLabel = new FieldLabel(StringBundle.get("preferences_dialog_use_bicubic_interpolation_label"));
		useBicubicInterpolationCheckBox = new JCheckBox();
		useBicubicInterpolationCheckBox.setBackground(panel.getBackground());
		useBicubicInterpolationCheckBox.setMinimumSize(new Dimension(32, 26));
		FieldLabel useFractionalMetricsLabel = new FieldLabel(StringBundle.get("preferences_dialog_use_fractional_metrics_label"));
		useFractionalMetricsCheckBox = new JCheckBox();
		useFractionalMetricsCheckBox.setBackground(panel.getBackground());
		useFractionalMetricsCheckBox.setMinimumSize(new Dimension(32, 26));
		
		Header windowSettingsHeader = new Header(StringBundle.get("preferences_dialog_window_settings_heading"));
		FieldLabel mainWindowSizeLabel      = new FieldLabel(StringBundle.get("preferences_dialog_main_window_size_label"));      mainWindowSizeTextField      = new ValidatingTextField(16, "(\\d+)\\s*,\\s*(\\d+)"); mainWindowSizeTextField     .setMargin(fieldMargin); mainWindowSizeTextField     .setMaximumSize(new Dimension(140, 100));
		FieldLabel graphWindowSizeLabel     = new FieldLabel(StringBundle.get("preferences_dialog_graph_window_size_label"));     graphWindowSizeTextField     = new ValidatingTextField(16, "(\\d+)\\s*,\\s*(\\d+)"); graphWindowSizeTextField    .setMargin(fieldMargin); graphWindowSizeTextField    .setMaximumSize(new Dimension(140, 100));
		FieldLabel cascadeWindowOffsetLabel = new FieldLabel(StringBundle.get("preferences_dialog_cascade_window_offset_label")); cascadeWindowOffsetTextField = new ValidatingTextField(8, "\\d+");  cascadeWindowOffsetTextField.setMargin(fieldMargin); cascadeWindowOffsetTextField.setMaximumSize(new Dimension(70, 100));
			
		Header otherHeader = new Header(StringBundle.get("preferences_dialog_other_heading"));
		FieldLabel defaultGraphNameLabel       = new FieldLabel(StringBundle.get("preferences_dialog_default_graph_name_label"));        defaultGraphNameTextField       = new JTextField(20);             defaultGraphNameTextField      .setMargin(fieldMargin); defaultGraphNameTextField      .setMaximumSize(new Dimension(175, 100));
		FieldLabel directedEdgeArrowRatioLabel = new FieldLabel(StringBundle.get("preferences_dialog_directed_edge_arrow_ratio_label")); directedEdgeArrowRatioTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); directedEdgeArrowRatioTextField.setMargin(fieldMargin); directedEdgeArrowRatioTextField.setMaximumSize(new Dimension(70, 100));
		FieldLabel arrowKeyIncrementLabel      = new FieldLabel(StringBundle.get("preferences_dialog_arrow_key_increment_label"));       arrowKeyIncrementTextField      = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); arrowKeyIncrementTextField     .setMargin(fieldMargin); arrowKeyIncrementTextField     .setMaximumSize(new Dimension(70, 100));
		FieldLabel edgeSnapMarginRatioLabel    = new FieldLabel(StringBundle.get("preferences_dialog_edge_snap_margin_label"));          edgeSnapMarginRatioTextField    = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); edgeSnapMarginRatioTextField   .setMargin(fieldMargin); edgeSnapMarginRatioTextField   .setMaximumSize(new Dimension(70, 100));
		FieldLabel areCloseDistanceLabel       = new FieldLabel(StringBundle.get("preferences_dialog_are_close_distance_label"));        areCloseDistanceTextField       = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); areCloseDistanceTextField      .setMargin(fieldMargin); areCloseDistanceTextField      .setMaximumSize(new Dimension(70, 100));
		FieldLabel paintToolMenuDelayLabel     = new FieldLabel(StringBundle.get("preferences_dialog_paint_tool_menu_delay_label"));     paintToolMenuDelayTextField     = new ValidatingTextField(8, "\\d+"); paintToolMenuDelayTextField    .setMargin(fieldMargin); paintToolMenuDelayTextField    .setMaximumSize(new Dimension(70, 100));
		
		Component col0Padding = Box.createRigidArea(new Dimension(columnWidths[0], 7));
		Component col1Padding = Box.createRigidArea(new Dimension(columnWidths[1], 7));
		Component col2Padding = Box.createRigidArea(new Dimension(columnWidths[2], 7));
		
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(clickingBehaviorHeader)
					.addComponent(zoomingBehaviorHeader)
					.addComponent(arrangingBehaviorHeader)
					.addComponent(renderingSettingsHeader)
					.addComponent(windowSettingsHeader)
					.addComponent(otherHeader)
					.addComponent(col0Padding))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(vertexClickMarginLabel)
					.addComponent(edgeHandleClickMarginLabel)
					.addComponent(captionHandleClickMarginLabel)
					.addComponent(captionEditorClickMarginLabel)
					.addComponent(panDecelerationFactorLabel)
					.addComponent(zoomInFactorLabel)
					.addComponent(zoomOutFactorLabel)
					.addComponent(maximumZoomFactorLabel)
					.addComponent(zoomGraphPaddingLabel)
					.addComponent(scrollZoomIncrementLabel)
					.addComponent(arrangeCircleRadiusMultiplierLabel)
					.addComponent(arrangeGridSpacingLabel)
					.addComponent(autoArrangeAttractiveForceLabel)
					.addComponent(autoArrangeRepulsiveForceLabel)
					.addComponent(autoArrangeDecelerationFactorLabel)
					.addComponent(arrangeContractFactorLabel)
					.addComponent(arrangeExpandFactorLabel)
					.addComponent(useAntiAliasingLabel)
					.addComponent(usePureStrokeLabel)
					.addComponent(useBicubicInterpolationLabel)
					.addComponent(useFractionalMetricsLabel)
					.addComponent(mainWindowSizeLabel)
					.addComponent(graphWindowSizeLabel)
					.addComponent(cascadeWindowOffsetLabel)
					.addComponent(defaultGraphNameLabel)
					.addComponent(directedEdgeArrowRatioLabel)
					.addComponent(arrowKeyIncrementLabel)
					.addComponent(edgeSnapMarginRatioLabel)
					.addComponent(areCloseDistanceLabel)
					.addComponent(paintToolMenuDelayLabel)
					.addComponent(col1Padding))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(vertexClickMarginTextField)
					.addComponent(edgeHandleClickMarginTextField)
					.addComponent(captionHandleClickMarginTextField)
					.addComponent(captionEditorClickMarginTextField)
					.addComponent(panDecelerationFactorTextField)
					.addComponent(zoomInFactorTextField)
					.addComponent(zoomOutFactorTextField)
					.addComponent(maximumZoomFactorTextField)
					.addComponent(zoomGraphPaddingTextField)
					.addComponent(scrollIncrementZoomTextField)
					.addComponent(arrangeCircleRadiusMultiplierTextField)
					.addComponent(arrangeGridSpacingTextField)
					.addComponent(autoArrangeAttractiveForceTextField)
					.addComponent(autoArrangeRepulsiveForceTextField)
					.addComponent(autoArrangeDecelerationFactorTextField)
					.addComponent(arrangeContractFactorTextField)
					.addComponent(arrangeExpandFactorTextField)
					.addComponent(useAntiAliasingCheckBox)
					.addComponent(usePureStrokeCheckBox)
					.addComponent(useBicubicInterpolationCheckBox)
					.addComponent(useFractionalMetricsCheckBox)
					.addComponent(mainWindowSizeTextField)
					.addComponent(graphWindowSizeTextField)
					.addComponent(cascadeWindowOffsetTextField)
					.addComponent(defaultGraphNameTextField)
					.addComponent(directedEdgeArrowRatioTextField)
					.addComponent(arrowKeyIncrementTextField)
					.addComponent(edgeSnapMarginRatioTextField)
					.addComponent(areCloseDistanceTextField)
					.addComponent(paintToolMenuDelayTextField)
					.addComponent(col2Padding)));
		
		layout.setVerticalGroup(
			layout.createSequentialGroup()
			 	.addComponent(clickingBehaviorHeader)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(vertexClickMarginLabel).addComponent(vertexClickMarginTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(edgeHandleClickMarginLabel).addComponent(edgeHandleClickMarginTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(captionHandleClickMarginLabel).addComponent(captionHandleClickMarginTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(captionEditorClickMarginLabel).addComponent(captionEditorClickMarginTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(panDecelerationFactorLabel).addComponent(panDecelerationFactorTextField))
				.addComponent(zoomingBehaviorHeader)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(zoomInFactorLabel).addComponent(zoomInFactorTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(zoomOutFactorLabel).addComponent(zoomOutFactorTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(maximumZoomFactorLabel).addComponent(maximumZoomFactorTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(zoomGraphPaddingLabel).addComponent(zoomGraphPaddingTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(scrollZoomIncrementLabel).addComponent(scrollIncrementZoomTextField))
				.addComponent(arrangingBehaviorHeader)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(arrangeCircleRadiusMultiplierLabel).addComponent(arrangeCircleRadiusMultiplierTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(arrangeGridSpacingLabel).addComponent(arrangeGridSpacingTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(autoArrangeAttractiveForceLabel).addComponent(autoArrangeAttractiveForceTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(autoArrangeRepulsiveForceLabel).addComponent(autoArrangeRepulsiveForceTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(autoArrangeDecelerationFactorLabel).addComponent(autoArrangeDecelerationFactorTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(arrangeContractFactorLabel).addComponent(arrangeContractFactorTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(arrangeExpandFactorLabel).addComponent(arrangeExpandFactorTextField))
				.addComponent(renderingSettingsHeader)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(useAntiAliasingLabel).addComponent(useAntiAliasingCheckBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(usePureStrokeLabel).addComponent(usePureStrokeCheckBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(useBicubicInterpolationLabel).addComponent(useBicubicInterpolationCheckBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(useFractionalMetricsLabel).addComponent(useFractionalMetricsCheckBox))
				.addComponent(windowSettingsHeader)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(mainWindowSizeLabel).addComponent(mainWindowSizeTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(graphWindowSizeLabel).addComponent(graphWindowSizeTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(cascadeWindowOffsetLabel).addComponent(cascadeWindowOffsetTextField))
				.addComponent(otherHeader)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultGraphNameLabel).addComponent(defaultGraphNameTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(directedEdgeArrowRatioLabel).addComponent(directedEdgeArrowRatioTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(arrowKeyIncrementLabel).addComponent(arrowKeyIncrementTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(edgeSnapMarginRatioLabel).addComponent(edgeSnapMarginRatioTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(areCloseDistanceLabel).addComponent(areCloseDistanceTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(paintToolMenuDelayLabel).addComponent(paintToolMenuDelayTextField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(col0Padding).addComponent(col1Padding).addComponent(col2Padding)));
		
		JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(null);
		
		return scrollPane;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("Ok"))
		{
			if(!validatePreferences( ))
			{
				JOptionPane.showMessageDialog(this, StringBundle.get("preferences_dialog_invalid_values_error_message"), StringBundle.get("preferences_dialog_invalid_values_error_title"), JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				savePreferences( );
				value = "saved";
				PreferencesDialog.dialog.setVisible(false);
			}
		}
		else
			PreferencesDialog.dialog.setVisible(false);
	}
	
	public boolean validatePreferences()
	{
		return (defaultVertexWeightTextField			.isValid( ) && 
				defaultVertexColorTextField				.isValid( ) && 
				defaultVertexRadiusTextField			.isValid( ) && 
				defaultEdgeWeightTextField				.isValid( ) && 
				defaultEdgeColorTextField				.isValid( ) && 
				defaultEdgeThicknessTextField			.isValid( ) && 
				defaultEdgeHandleRadiusRatioTextField	.isValid( ) && 
				defaultLoopDiameterTextField			.isValid( ) &&
				defaultCaptionFontSizeTextField			.isValid( ) &&
				vertexClickMarginTextField				.isValid( ) && 
				edgeHandleClickMarginTextField			.isValid( ) && 
				captionHandleClickMarginTextField		.isValid( ) && 
				captionEditorClickMarginTextField		.isValid( ) &&
				panDecelerationFactorTextField			.isValid( ) &&
				zoomInFactorTextField					.isValid( ) && 
				zoomOutFactorTextField					.isValid( ) && 
				maximumZoomFactorTextField				.isValid( ) && 
				zoomGraphPaddingTextField				.isValid( ) && 
				scrollIncrementZoomTextField			.isValid( ) && 
				arrangeCircleRadiusMultiplierTextField	.isValid( ) && 
				arrangeGridSpacingTextField				.isValid( ) && 
				autoArrangeAttractiveForceTextField		.isValid( ) && 
				autoArrangeRepulsiveForceTextField		.isValid( ) && 
				autoArrangeDecelerationFactorTextField	.isValid( ) && 
				arrangeContractFactorTextField			.isValid( ) &&
				arrangeExpandFactorTextField			.isValid( ) &&
				mainWindowSizeTextField					.isValid( ) && 
				graphWindowSizeTextField				.isValid( ) && 
				cascadeWindowOffsetTextField			.isValid( ) && 
				directedEdgeArrowRatioTextField			.isValid( ) && 
				arrowKeyIncrementTextField				.isValid( ) && 
				edgeSnapMarginRatioTextField			.isValid( ) && 
				areCloseDistanceTextField				.isValid( ) && 
				paintToolMenuDelayTextField				.isValid( ));
	}
	
	public void loadPreferences()
	{
		defaultVertexWeightTextField			.setText( userSettings.defaultVertexWeight.get( ).toString( ) );
		defaultVertexColorTextField				.setText( userSettings.defaultVertexColor.get( ).toString( ) );
		defaultVertexPrefixTextField			.setText( userSettings.defaultVertexPrefix.get( ).toString( ) );
		defaultVertexRadiusTextField			.setText( userSettings.defaultVertexRadius.get( ).toString( ) );
		defaultVertexIsSelectedCheckBox			.setSelected( userSettings.defaultVertexIsSelected.get( ) );
		defaultEdgeWeightTextField				.setText( userSettings.defaultEdgeWeight.get( ).toString( ) );
		defaultEdgeColorTextField				.setText( userSettings.defaultEdgeColor.get( ).toString( ) );
		defaultEdgePrefixTextField				.setText( userSettings.defaultEdgePrefix.get( ).toString( ) );
		defaultEdgeThicknessTextField			.setText( userSettings.defaultEdgeThickness.get( ).toString( ) );
		defaultEdgeHandleRadiusRatioTextField	.setText( userSettings.defaultEdgeHandleRadiusRatio.get( ).toString( ) );
		defaultLoopDiameterTextField			.setText( userSettings.defaultLoopDiameter.get( ).toString( ) );
		defaultEdgeIsSelectedCheckBox			.setSelected( userSettings.defaultEdgeIsSelected.get( ) );
		defaultCaptionTextTextField				.setText( userSettings.defaultCaptionText.get( ) );
		defaultCaptionFontSizeTextField			.setText( userSettings.defaultCaptionFontSize.get( ).toString( ) );
		defaultCaptionIsSelectedCheckBox		.setSelected( userSettings.defaultCaptionIsSelected.get( ) );
		defaultShowVertexWeightsCheckBox		.setSelected( userSettings.defaultShowVertexWeights.get( ) );
		defaultShowVertexLabelsCheckBox			.setSelected( userSettings.defaultShowVertexLabels.get( ) );
		defaultShowEdgeHandlesCheckBox			.setSelected( userSettings.defaultShowEdgeHandles.get( ) );
		defaultShowEdgeWeightsCheckBox			.setSelected( userSettings.defaultShowEdgeWeights.get( ) );
		defaultShowEdgeLabelsCheckBox			.setSelected( userSettings.defaultShowEdgeLabels.get( ) );
		defaultShowCaptionsCheckBox				.setSelected( userSettings.defaultShowCaptions.get( ) );
		defaultShowCaptionHandlesCheckBox		.setSelected( userSettings.defaultShowCaptionHandles.get( ) );
		defaultShowCaptionEditorsCheckBox		.setSelected( userSettings.defaultShowCaptionEditors.get( ) );
		graphBackgroundColorPicker				.setColor( userSettings.graphBackground.get( ) );
		selectionBoxFillColorPicker				.setColor( userSettings.selectionBoxFill.get( ) );
		selectionBoxLineColorPicker				.setColor( userSettings.selectionBoxLine.get( ) );
		vertexLineColorPicker					.setColor( userSettings.vertexLine.get( ) );
		selectedVertexFillColorPicker			.setColor( userSettings.selectedVertexFill.get( ) );
		selectedVertexLineColorPicker			.setColor( userSettings.selectedVertexLine.get( ) );
		draggingEdgeColorPicker					.setColor( userSettings.draggingEdge.get( ) );
		edgeHandleColorPicker					.setColor( userSettings.edgeHandle.get( ) );
		selectedEdgeColorPicker					.setColor( userSettings.selectedEdge.get( ) );
		selectedEdgeHandleColorPicker			.setColor( userSettings.selectedEdgeHandle.get( ) );
		captionTextColorPicker					.setColor( userSettings.captionText.get( ) );
		captionButtonFillColorPicker			.setColor( userSettings.captionButtonFill.get( ) );
		captionButtonLineColorPicker			.setColor( userSettings.captionButtonLine.get( ) );
		selectedCaptionLineColorPicker			.setColor( userSettings.selectedCaptionLine.get( ) );
		uncoloredElementColorPicker				.setColor( userSettings.uncoloredElementFill.get( ) );
		vertexClickMarginTextField				.setText( userSettings.vertexClickMargin.get( ).toString( ) );
		edgeHandleClickMarginTextField			.setText( userSettings.edgeHandleClickMargin.get( ).toString( ) );
		captionHandleClickMarginTextField		.setText( userSettings.captionHandleClickMargin.get( ).toString( ) );
		captionEditorClickMarginTextField		.setText( userSettings.captionEditorClickMargin.get( ).toString( ) );
		panDecelerationFactorTextField			.setText( userSettings.panDecelerationFactor.get( ).toString( ) );
		zoomInFactorTextField					.setText( userSettings.zoomInFactor.get( ).toString( ) );
		zoomOutFactorTextField					.setText( userSettings.zoomOutFactor.get( ).toString( ) );
		maximumZoomFactorTextField				.setText( userSettings.maximumZoomFactor.get( ).toString( ) );
		zoomGraphPaddingTextField				.setText( userSettings.zoomGraphPadding.get( ).toString( ) );
		scrollIncrementZoomTextField			.setText( userSettings.scrollIncrementZoom.get( ).toString( ) );
		arrangeCircleRadiusMultiplierTextField	.setText( userSettings.arrangeCircleRadiusMultiplier.get( ).toString( ) );
		arrangeGridSpacingTextField				.setText( userSettings.arrangeGridSpacing.get( ).toString( ) );
		autoArrangeAttractiveForceTextField		.setText( userSettings.autoArrangeAttractiveForce.get( ).toString( ) );
		autoArrangeRepulsiveForceTextField		.setText( userSettings.autoArrangeRepulsiveForce.get( ).toString( ) );
		autoArrangeDecelerationFactorTextField	.setText( userSettings.autoArrangeDecelerationFactor.get( ).toString( ) );
		arrangeContractFactorTextField			.setText( userSettings.arrangeContractFactor.get( ).toString( ) );
		arrangeExpandFactorTextField			.setText( userSettings.arrangeExpandFactor.get( ).toString( ) );
		useAntiAliasingCheckBox					.setSelected( userSettings.useAntiAliasing.get( ) );
		usePureStrokeCheckBox					.setSelected( userSettings.usePureStroke.get( ) );
		useBicubicInterpolationCheckBox			.setSelected( userSettings.useBicubicInterpolation.get( ) );
		useFractionalMetricsCheckBox			.setSelected( userSettings.useFractionalMetrics.get( ) );
		mainWindowSizeTextField					.setText( userSettings.mainWindowWidth.get( ) + ", " + userSettings.mainWindowHeight.get( ) );
		graphWindowSizeTextField				.setText( userSettings.graphWindowWidth.get( ) + ", " + userSettings.graphWindowWidth.get( ) );
		cascadeWindowOffsetTextField			.setText( userSettings.cascadeWindowOffset.get( ).toString( ) );
		defaultGraphNameTextField				.setText( userSettings.defaultGraphName.get( ) );
		directedEdgeArrowRatioTextField			.setText( userSettings.directedEdgeArrowRatio.get( ).toString( ) );
		arrowKeyIncrementTextField				.setText( userSettings.arrowKeyIncrement.get( ).toString( ) );
		edgeSnapMarginRatioTextField			.setText( userSettings.edgeSnapMarginRatio.get( ).toString( ) );
		areCloseDistanceTextField				.setText( userSettings.areCloseDistance.get( ).toString( ) );
		paintToolMenuDelayTextField				.setText( userSettings.paintToolMenuDelay.get( ).toString( ) );

		elementColorsPanel.removeAll();
		for(Color color : userSettings.elementColors)
			elementColorsPanel.add(new ColorPicker(color));
		
		if(elementColorsPanel.getComponentCount() > 1)
			removeButton.setEnabled(true);
	}
	
	public void savePreferences()
	{
		userSettings.defaultVertexWeight			.set( new Double ( defaultVertexWeightTextField.getText( ) ) );
		userSettings.defaultVertexColor				.set( new Integer( defaultVertexColorTextField.getText( ) ) );
		userSettings.defaultVertexPrefix			.set(              defaultVertexPrefixTextField.getText( )   );
		userSettings.defaultVertexRadius			.set( new Double ( defaultVertexRadiusTextField.getText( ) ) );
		userSettings.defaultVertexIsSelected		.set(              defaultVertexIsSelectedCheckBox.isSelected( ) );
		userSettings.defaultEdgeWeight				.set( new Double ( defaultEdgeWeightTextField.getText( ) ) );
		userSettings.defaultEdgeColor				.set( new Integer( defaultEdgeColorTextField.getText( ) ) );
		userSettings.defaultEdgePrefix				.set(              defaultEdgePrefixTextField.getText( )   );
		userSettings.defaultEdgeThickness			.set( new Double ( defaultEdgeThicknessTextField.getText( ) ) );
		userSettings.defaultEdgeHandleRadiusRatio	.set( new Double ( defaultEdgeHandleRadiusRatioTextField.getText( ) ) );
		userSettings.defaultLoopDiameter			.set( new Double ( defaultLoopDiameterTextField.getText( ) ) );
		userSettings.defaultEdgeIsSelected			.set( 			   defaultEdgeIsSelectedCheckBox.isSelected( ) );
		userSettings.defaultCaptionText				.set(              defaultCaptionTextTextField.getText( )   );
		userSettings.defaultCaptionFontSize			.set( new Double( defaultCaptionFontSizeTextField.getText( ) ) );
		userSettings.defaultCaptionIsSelected		.set( 			   defaultCaptionIsSelectedCheckBox.isSelected( ) );
		userSettings.defaultShowVertexWeights		.set( 			   defaultShowVertexWeightsCheckBox.isSelected( ) );
		userSettings.defaultShowVertexLabels		.set( 			   defaultShowVertexLabelsCheckBox.isSelected( ) );
		userSettings.defaultShowEdgeHandles			.set( 			   defaultShowEdgeHandlesCheckBox.isSelected( ) );
		userSettings.defaultShowEdgeWeights			.set( 			   defaultShowEdgeWeightsCheckBox.isSelected( ) );
		userSettings.defaultShowEdgeLabels			.set( 			   defaultShowEdgeLabelsCheckBox.isSelected( ) );
		userSettings.defaultShowCaptions			.set( 			   defaultShowCaptionsCheckBox.isSelected( ) );
		userSettings.defaultShowCaptionHandles		.set( 			   defaultShowCaptionHandlesCheckBox.isSelected( ) );
		userSettings.defaultShowCaptionEditors		.set( 			   defaultShowCaptionEditorsCheckBox.isSelected( ) );
		userSettings.graphBackground				.set( 			   graphBackgroundColorPicker.getColor( ) );
		userSettings.selectionBoxFill				.set( 			   selectionBoxFillColorPicker.getColor( ) );
		userSettings.selectionBoxLine				.set( 			   selectionBoxLineColorPicker.getColor( ) );
		userSettings.vertexLine						.set( 			   vertexLineColorPicker.getColor( ) );
		userSettings.selectedVertexFill				.set( 			   selectedVertexFillColorPicker.getColor( ) );
		userSettings.selectedVertexLine				.set( 			   selectedVertexLineColorPicker.getColor( ) );
		userSettings.draggingEdge					.set( 			   draggingEdgeColorPicker.getColor( ) );
		userSettings.edgeHandle						.set( 			   edgeHandleColorPicker.getColor( ) );
		userSettings.selectedEdge					.set( 			   selectedEdgeColorPicker.getColor( ) );
		userSettings.selectedEdgeHandle				.set( 			   selectedEdgeHandleColorPicker.getColor( ) );
		userSettings.captionText					.set( 			   captionTextColorPicker.getColor( ) );
		userSettings.captionButtonFill				.set( 			   captionButtonFillColorPicker.getColor( ) );
		userSettings.captionButtonLine				.set( 			   captionButtonLineColorPicker.getColor( ) );
		userSettings.selectedCaptionLine			.set( 			   selectedCaptionLineColorPicker.getColor( ) );
		userSettings.uncoloredElementFill			.set( 			   uncoloredElementColorPicker.getColor( ) );
		userSettings.vertexClickMargin				.set( new Double ( vertexClickMarginTextField.getText( ) ) );
		userSettings.edgeHandleClickMargin			.set( new Double ( edgeHandleClickMarginTextField.getText( ) ) );
		userSettings.captionHandleClickMargin		.set( new Double ( captionHandleClickMarginTextField.getText( ) ) );
		userSettings.captionEditorClickMargin		.set( new Double ( captionEditorClickMarginTextField.getText( ) ) );
		userSettings.panDecelerationFactor			.set( new Double ( panDecelerationFactorTextField.getText( ) ) );
		userSettings.zoomInFactor					.set( new Double ( zoomInFactorTextField.getText( ) ) );
		userSettings.zoomOutFactor					.set( new Double ( zoomOutFactorTextField.getText( ) ) );
		userSettings.maximumZoomFactor				.set( new Double ( maximumZoomFactorTextField.getText( ) ) );
		userSettings.zoomGraphPadding				.set( new Double ( zoomGraphPaddingTextField.getText( ) ) );
		userSettings.scrollIncrementZoom			.set( new Double ( scrollIncrementZoomTextField.getText( ) ) );
		userSettings.arrangeCircleRadiusMultiplier	.set( new Double ( arrangeCircleRadiusMultiplierTextField.getText( ) ) );
		userSettings.arrangeGridSpacing				.set( new Double ( arrangeGridSpacingTextField.getText( ) ) );
		userSettings.autoArrangeAttractiveForce		.set( new Double ( autoArrangeAttractiveForceTextField.getText( ) ) );
		userSettings.autoArrangeRepulsiveForce		.set( new Double ( autoArrangeRepulsiveForceTextField.getText( ) ) );
		userSettings.autoArrangeDecelerationFactor	.set( new Double ( autoArrangeDecelerationFactorTextField.getText( ) ) );
		userSettings.arrangeContractFactor			.set( new Double ( arrangeContractFactorTextField.getText( ) ) );
		userSettings.arrangeExpandFactor			.set( new Double ( arrangeExpandFactorTextField.getText( ) ) );
		userSettings.useAntiAliasing				.set(              useAntiAliasingCheckBox.isSelected( ) );
		userSettings.usePureStroke					.set(              usePureStrokeCheckBox.isSelected( ) );
		userSettings.useBicubicInterpolation		.set(              useBicubicInterpolationCheckBox.isSelected( ) );
		userSettings.useFractionalMetrics			.set(              useFractionalMetricsCheckBox.isSelected( ) );
		
		Matcher matcher = Pattern.compile(mainWindowSizeTextField.getValidatingExpression( )).matcher(mainWindowSizeTextField.getText( )); matcher.find();
		userSettings.mainWindowWidth				.set( new Integer( matcher.group(1) ) );
		userSettings.mainWindowHeight				.set( new Integer( matcher.group(2) ) );
		
		matcher = Pattern.compile(graphWindowSizeTextField.getValidatingExpression( )).matcher(graphWindowSizeTextField.getText( )); matcher.find();
		userSettings.graphWindowWidth				.set( new Integer( matcher.group(1) ) );
		userSettings.graphWindowHeight				.set( new Integer( matcher.group(2) ) );
		
		userSettings.cascadeWindowOffset			.set( new Integer( cascadeWindowOffsetTextField.getText( ) ) );
		userSettings.defaultGraphName				.set(              defaultGraphNameTextField.getText( )   );
		userSettings.directedEdgeArrowRatio			.set( new Double ( directedEdgeArrowRatioTextField.getText( ) ) );
		userSettings.arrowKeyIncrement				.set( new Double ( arrowKeyIncrementTextField.getText( ) ) );
		userSettings.edgeSnapMarginRatio			.set( new Double ( edgeSnapMarginRatioTextField.getText( ) ) );
		userSettings.areCloseDistance				.set( new Double ( areCloseDistanceTextField.getText( ) ) );
		userSettings.paintToolMenuDelay				.set( new Integer( paintToolMenuDelayTextField.getText( ) ) );
		
		userSettings.elementColors.clear();
		for(Component component : elementColorsPanel.getComponents())
			if(component instanceof ColorPicker)
				userSettings.elementColors.add(((ColorPicker)component).getColor());
		
		File userSettingsFile = new File("UserSettings.json");
		
		try
		{
			if(!userSettingsFile.exists())
				userSettingsFile.createNewFile();
			
			FileWriter fileWriter = new FileWriter(userSettingsFile);
			fileWriter.write(userSettings.toString());
			fileWriter.close();
		}
		catch (IOException exception)
		{
			System.out.println(String.format(StringBundle.get("preferences_dialog_unable_to_save_error_message"), userSettingsFile.getAbsolutePath()));
		}
	}
	
	private class Header extends JLabel
	{
		public Header(String label)
		{
			Font font = super.getFont();
			super.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize()));
			super.setMinimumSize(new Dimension(100, 32));
			super.setBorder(BorderFactory.createEmptyBorder(0, 9, 0, 0));
			super.setText(label);
		}
	}
	
	private class FieldLabel extends JLabel
	{
		public FieldLabel(String label)
		{
			super.setMinimumSize(new Dimension(super.getMinimumSize().width, 20));
			super.setText(label);
		}
	}
}















