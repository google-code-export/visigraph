/**
 * PreferencesDialog.java
 */
package edu.belmont.mth.visigraph.gui;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;

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
	private static JCheckBox           defaultCaptionIsSelectedCheckBox;
	private static JCheckBox           defaultShowEdgeHandlesCheckBox;
	private static JCheckBox           defaultShowEdgeWeightsCheckBox;
	private static JCheckBox           defaultShowVertexWeightsCheckBox;
	private static JCheckBox           defaultShowVertexLabelsCheckBox;
	private static JCheckBox           defaultShowCaptionsCheckBox;
	private static JCheckBox           defaultShowCaptionHandlesCheckBox;
	private static JCheckBox           defaultShowCaptionEditorsCheckBox;
	private static ColorPicker		   graphBackgroundColorPicker;
	private static ColorPicker 		   selectionBoxFillColorPicker;
	private static ColorPicker 		   selectionBoxLineColorPicker;
	private static ColorPicker		   vertexLineColorPicker;
	private static ColorPicker		   selectedVertexFillColorPicker;
	private static ColorPicker		   selectedVertexLineColorPicker;
	private static ColorPicker 		   draggingHandleEdgeColorPicker;
	private static ColorPicker 		   draggingEdgeColorPicker;
	private static ColorPicker 		   uncoloredEdgeHandleColorPicker;
	private static ColorPicker 		   selectedEdgeColorPicker;
	private static ColorPicker 		   selectedEdgeHandleColorPicker;
	private static ColorPicker 		   captionTextColorPicker;
	private static ColorPicker 		   selectedCaptionTextColorPicker;
	private static ColorPicker 		   uncoloredElementColorPicker;
	private static Stack<ColorPicker>  elementColorColorPickers;
	
	private static ValidatingTextField vertexClickMarginTextField;
	private static ValidatingTextField edgeHandleClickMarginTextField;
	private static ValidatingTextField captionHandleClickMarginTextField;
	private static ValidatingTextField captionEditorClickMarginTextField;
	
	private static ValidatingTextField zoomInFactorTextField;
	private static ValidatingTextField zoomOutFactorTextField;
	private static ValidatingTextField maximumZoomFactorTextField;
	private static ValidatingTextField zoomGraphPaddingTextField;
	private static ValidatingTextField scrollIncrementZoomTextField;
	
	private static JCheckBox           useAntiAliasingCheckBox;
	private static JCheckBox           usePureStrokeCheckBox;
	private static JCheckBox           useBicubicInterpolationCheckBox;
	
	private static ValidatingTextField arrangeCircleRadiusMultiplierTextField;
	private static ValidatingTextField arrangeGridSpacingTextField;
	private static ValidatingTextField autoArrangeAttractiveForceTextField;
	private static ValidatingTextField autoArrangeRepulsiveForceTextField;
	private static ValidatingTextField autoArrangeDecelerationFactorTextField;
	
	private static ValidatingTextField mainWindowSizeTextField;
	private static ValidatingTextField graphWindowSizeTextField;
	private static ValidatingTextField cascadeWindowOffsetTextField;
	
	private static JTextField 		   defaultGraphNameTextField;
	private static ValidatingTextField directedEdgeArrowRatioTextField;
	private static ValidatingTextField arrowKeyIncrementTextField;
	private static ValidatingTextField edgeSnapMarginRatioTextField;
	private static ValidatingTextField areCloseDistanceTextField;
	private static ValidatingTextField paintToolMenuDelayTextField;
	
	private final int[] columnWidths = new int[] { 120, 120, 260 };
	
	public static String showDialog(Component frameComp, Component locationComp)
	{
		Frame frame = JOptionPane.getFrameForComponent(frameComp);
		dialog = new PreferencesDialog(frame, locationComp);
		dialog.setVisible(true);
		return value;
	}
	
	private PreferencesDialog(Frame frame, Component locationComp)
	{
		super(frame, "Preferences", true);
		this.setResizable(false);
		
		JPanel inputPanel = new JPanel(new BorderLayout());
		
		JTabbedPane tabPanel = new JTabbedPane();
		tabPanel.setPreferredSize(new Dimension(570, 520));
		tabPanel.setBorder(new EmptyBorder(7, 7, 0, 7));
		
		tabPanel.addTab(" Defaults ",       initializeDefaultsPanel());
		tabPanel.addTab(" Appearances ",    initializeAppearancesPanel());
		tabPanel.addTab(" Under the Hood ", initializeUnderTheHoodPanel());
		
		inputPanel.add(tabPanel, BorderLayout.CENTER);
		
		//Create and initialize the buttons
		final JButton okButton = new JButton("Ok");
		okButton.setPreferredSize(new Dimension(80, 28));
		okButton.setActionCommand("Ok");
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);
		
		JButton cancelButton = new JButton("Cancel");
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
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.white);
		
		JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(null);
		
		Component col0 = new JPanel(); col0.setPreferredSize(new Dimension(columnWidths[0], 8)); col0.setSize(col0.getPreferredSize()); col0.setMaximumSize(col0.getPreferredSize()); col0.setMinimumSize(col0.getPreferredSize());
		col0.setBackground(Color.red); panel.add(col0, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		Component col1 = new JPanel(); col1.setPreferredSize(new Dimension(columnWidths[1], 8)); col1.setSize(col1.getPreferredSize()); col1.setMaximumSize(col1.getPreferredSize()); col1.setMinimumSize(col0.getPreferredSize());
		col1.setBackground(Color.blue); panel.add(col1, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		Component col2 = new JPanel(); col2.setPreferredSize(new Dimension(columnWidths[2], 8)); col2.setSize(col2.getPreferredSize()); col2.setMaximumSize(col2.getPreferredSize()); col2.setMinimumSize(col0.getPreferredSize());
		col2.setBackground(Color.red); panel.add(col2, new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		panel.add(new Header("Default vertex properties:"), new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(10, 20, 10, 10), 0, 0));
		
		defaultVertexWeightTextField = new ValidatingTextField(8, "-?(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?");
		panel.add(new JLabel("Weight: "),       new GridBagConstraints(1, 2, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultVertexWeightTextField, new GridBagConstraints(2, 2, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		defaultVertexColorTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Color: "),       new GridBagConstraints(1, 3, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultVertexColorTextField, new GridBagConstraints(2, 3, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		defaultVertexPrefixTextField = new JTextField(8);
		panel.add(new JLabel("Prefix: "),       new GridBagConstraints(1, 4, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultVertexPrefixTextField, new GridBagConstraints(2, 4, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		defaultVertexRadiusTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?");
		panel.add(new JLabel("Radius: "),       new GridBagConstraints(1, 5, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultVertexRadiusTextField, new GridBagConstraints(2, 5, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		defaultVertexIsSelectedCheckBox = new JCheckBox();
		defaultVertexIsSelectedCheckBox.setBackground(panel.getBackground());
		defaultVertexIsSelectedCheckBox.setPreferredSize(new Dimension(32, defaultVertexIsSelectedCheckBox.getPreferredSize().height));
		panel.add(new JLabel("Is selected: "),     new GridBagConstraints(1, 6, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultVertexIsSelectedCheckBox, new GridBagConstraints(2, 6, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		panel.add(new Header("Default edge properties:"), new GridBagConstraints(0, 7, 2, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(10, 20, 10, 10), 0, 0));
		
		defaultEdgeWeightTextField = new ValidatingTextField(8, "-?(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?");
		panel.add(new JLabel("Weight: "),     new GridBagConstraints(1, 8, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultEdgeWeightTextField, new GridBagConstraints(2, 8, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		defaultEdgeColorTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Color: "),     new GridBagConstraints(1, 9, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultEdgeColorTextField, new GridBagConstraints(2, 9, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		defaultEdgePrefixTextField = new JTextField(8);
		panel.add(new JLabel("Prefix: "),     new GridBagConstraints(1, 10, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultEdgePrefixTextField, new GridBagConstraints(2, 10, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		defaultEdgeThicknessTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?");
		panel.add(new JLabel("Thickness: "),     new GridBagConstraints(1, 11, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultEdgeThicknessTextField, new GridBagConstraints(2, 11, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		defaultEdgeHandleRadiusRatioTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?");
		panel.add(new JLabel("Handle radius ratio: "),   new GridBagConstraints(1, 12, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultEdgeHandleRadiusRatioTextField, new GridBagConstraints(2, 12, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		defaultLoopDiameterTextField = new ValidatingTextField(8, "-?(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?");
		panel.add(new JLabel("Loop diameter: "), new GridBagConstraints(1, 13, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultLoopDiameterTextField,  new GridBagConstraints(2, 13, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		defaultEdgeIsSelectedCheckBox = new JCheckBox();
		defaultEdgeIsSelectedCheckBox.setBackground(panel.getBackground());
		defaultEdgeIsSelectedCheckBox.setPreferredSize(new Dimension(32, defaultEdgeIsSelectedCheckBox.getPreferredSize().height));
		panel.add(new JLabel("Is selected: "),   new GridBagConstraints(1, 14, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultEdgeIsSelectedCheckBox, new GridBagConstraints(2, 14, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		panel.add(new Header("Default caption properties:"), new GridBagConstraints(0, 15, 2, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(10, 20, 10, 10), 0, 0));
		
		defaultCaptionTextTextField = new JTextField(20);
		panel.add(new JLabel("Text: "),        new GridBagConstraints(1, 16, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultCaptionTextTextField, new GridBagConstraints(2, 16, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		defaultCaptionIsSelectedCheckBox = new JCheckBox();
		defaultCaptionIsSelectedCheckBox.setBackground(panel.getBackground());
		defaultCaptionIsSelectedCheckBox.setPreferredSize(new Dimension(32, defaultCaptionIsSelectedCheckBox.getPreferredSize().height));
		panel.add(new JLabel("Is selected: "),      new GridBagConstraints(1, 17, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultCaptionIsSelectedCheckBox, new GridBagConstraints(2, 17, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		panel.add(new Header("Default viewport settings:"), new GridBagConstraints(0, 18, 2, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(10, 20, 10, 10), 0, 0));
		
		defaultShowEdgeHandlesCheckBox = new JCheckBox();
		defaultShowEdgeHandlesCheckBox.setBackground(panel.getBackground());
		defaultShowEdgeHandlesCheckBox.setPreferredSize(new Dimension(32, defaultCaptionIsSelectedCheckBox.getPreferredSize().height));
		panel.add(new JLabel("Show edge handles: "), new GridBagConstraints(1, 19, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultShowEdgeHandlesCheckBox,    new GridBagConstraints(2, 19, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		defaultShowEdgeWeightsCheckBox = new JCheckBox();
		defaultShowEdgeWeightsCheckBox.setBackground(panel.getBackground());
		defaultShowEdgeWeightsCheckBox.setPreferredSize(new Dimension(32, defaultCaptionIsSelectedCheckBox.getPreferredSize().height));
		panel.add(new JLabel("Show edge weights: "), new GridBagConstraints(1, 20, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultShowEdgeWeightsCheckBox,    new GridBagConstraints(2, 20, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		defaultShowVertexWeightsCheckBox = new JCheckBox();
		defaultShowVertexWeightsCheckBox.setBackground(panel.getBackground());
		defaultShowVertexWeightsCheckBox.setPreferredSize(new Dimension(32, defaultCaptionIsSelectedCheckBox.getPreferredSize().height));
		panel.add(new JLabel("Show vertex weights: "), new GridBagConstraints(1, 21, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultShowVertexWeightsCheckBox,    new GridBagConstraints(2, 21, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		defaultShowVertexLabelsCheckBox = new JCheckBox();
		defaultShowVertexLabelsCheckBox.setBackground(panel.getBackground());
		defaultShowVertexLabelsCheckBox.setPreferredSize(new Dimension(32, defaultCaptionIsSelectedCheckBox.getPreferredSize().height));
		panel.add(new JLabel("Show vertex labels: "), new GridBagConstraints(1, 22, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultShowVertexLabelsCheckBox,    new GridBagConstraints(2, 22, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		defaultShowCaptionsCheckBox = new JCheckBox();
		defaultShowCaptionsCheckBox.setBackground(panel.getBackground());
		defaultShowCaptionsCheckBox.setPreferredSize(new Dimension(32, defaultCaptionIsSelectedCheckBox.getPreferredSize().height));
		panel.add(new JLabel("Show captions: "), new GridBagConstraints(1, 23, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultShowCaptionsCheckBox,   new GridBagConstraints(2, 23, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		defaultShowCaptionHandlesCheckBox = new JCheckBox();
		defaultShowCaptionHandlesCheckBox.setBackground(panel.getBackground());
		defaultShowCaptionHandlesCheckBox.setPreferredSize(new Dimension(32, defaultCaptionIsSelectedCheckBox.getPreferredSize().height));
		panel.add(new JLabel("Show caption handles: "), new GridBagConstraints(1, 24, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultShowCaptionHandlesCheckBox,    new GridBagConstraints(2, 24, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		defaultShowCaptionEditorsCheckBox = new JCheckBox();
		defaultShowCaptionEditorsCheckBox.setBackground(panel.getBackground());
		defaultShowCaptionEditorsCheckBox.setPreferredSize(new Dimension(32, defaultCaptionIsSelectedCheckBox.getPreferredSize().height));
		panel.add(new JLabel("Show caption editors: "), new GridBagConstraints(1, 25, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultShowCaptionEditorsCheckBox,    new GridBagConstraints(2, 25, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		panel.add(Box.createRigidArea(new Dimension(1, 13)), new GridBagConstraints(2, 26, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		return scrollPane;
	}
	
	private JScrollPane initializeAppearancesPanel()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.white);
		
		JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(null);
		
		Component col0 = new JPanel(); col0.setPreferredSize(new Dimension(columnWidths[0], 8)); col0.setSize(col0.getPreferredSize()); col0.setMaximumSize(col0.getPreferredSize()); col0.setMinimumSize(col0.getPreferredSize());
		col0.setBackground(Color.red); panel.add(col0, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		Component col1 = new JPanel(); col1.setPreferredSize(new Dimension(columnWidths[1], 8)); col1.setSize(col1.getPreferredSize()); col1.setMaximumSize(col1.getPreferredSize()); col1.setMinimumSize(col0.getPreferredSize());
		col1.setBackground(Color.blue); panel.add(col1, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		Component col2 = new JPanel(); col2.setPreferredSize(new Dimension(columnWidths[2], 8)); col2.setSize(col2.getPreferredSize()); col2.setMaximumSize(col2.getPreferredSize()); col2.setMinimumSize(col0.getPreferredSize());
		col2.setBackground(Color.red); panel.add(col2, new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		panel.add(new Header("Graph colors:"), new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(10, 20, 10, 10), 0, 0));
		
		graphBackgroundColorPicker = new ColorPicker();
		panel.add(new JLabel("Graph background: "), new GridBagConstraints(1, 2, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(graphBackgroundColorPicker,       new GridBagConstraints(2, 2, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		selectionBoxFillColorPicker = new ColorPicker();
		panel.add(new JLabel("Selection box fill: "), new GridBagConstraints(1, 3, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(selectionBoxFillColorPicker,        new GridBagConstraints(2, 3, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		selectionBoxLineColorPicker = new ColorPicker();
		panel.add(new JLabel("Selection box line: "), new GridBagConstraints(1, 4, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(selectionBoxLineColorPicker,        new GridBagConstraints(2, 4, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		panel.add(new Header("Vertex colors:"), new GridBagConstraints(0, 5, 2, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(10, 20, 10, 10), 0, 0));
		
		vertexLineColorPicker = new ColorPicker();
		panel.add(new JLabel("Vertex line: "), new GridBagConstraints(1, 6, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(vertexLineColorPicker,       new GridBagConstraints(2, 6, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		selectedVertexFillColorPicker = new ColorPicker();
		panel.add(new JLabel("Selected vertex fill: "), new GridBagConstraints(1, 7, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(selectedVertexFillColorPicker,        new GridBagConstraints(2, 7, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		selectedVertexLineColorPicker = new ColorPicker();
		panel.add(new JLabel("Selected vertex line: "), new GridBagConstraints(1, 8, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(selectedVertexLineColorPicker,        new GridBagConstraints(2, 8, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		panel.add(new Header("Edge colors:"), new GridBagConstraints(0, 9, 2, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(10, 20, 10, 10), 0, 0));
		
		draggingHandleEdgeColorPicker = new ColorPicker();
		panel.add(new JLabel("Dragging handle edge: "), new GridBagConstraints(1, 10, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(draggingHandleEdgeColorPicker,        new GridBagConstraints(2, 10, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		draggingEdgeColorPicker = new ColorPicker();
		panel.add(new JLabel("Dragging edge: "), new GridBagConstraints(1, 11, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(draggingEdgeColorPicker,       new GridBagConstraints(2, 11, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		uncoloredEdgeHandleColorPicker = new ColorPicker();
		panel.add(new JLabel("Uncolored edge handle: "), new GridBagConstraints(1, 12, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(uncoloredEdgeHandleColorPicker,        new GridBagConstraints(2, 12, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		selectedEdgeColorPicker = new ColorPicker();
		panel.add(new JLabel("Selected edge: "), new GridBagConstraints(1, 13, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(selectedEdgeColorPicker,       new GridBagConstraints(2, 13, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		selectedEdgeHandleColorPicker = new ColorPicker();
		panel.add(new JLabel("Selected edge handle: "), new GridBagConstraints(1, 14, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(selectedEdgeHandleColorPicker,        new GridBagConstraints(2, 14, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		panel.add(new Header("Caption colors:"), new GridBagConstraints(0, 15, 2, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(10, 20, 10, 10), 0, 0));
		
		captionTextColorPicker = new ColorPicker();
		panel.add(new JLabel("Caption text: "), new GridBagConstraints(1, 16, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(captionTextColorPicker,       new GridBagConstraints(2, 16, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		selectedCaptionTextColorPicker = new ColorPicker();
		panel.add(new JLabel("Selected caption text: "), new GridBagConstraints(1, 17, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(selectedCaptionTextColorPicker,        new GridBagConstraints(2, 17, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		panel.add(new Header("Shared colors:"), new GridBagConstraints(0, 18, 2, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(10, 20, 10, 10), 0, 0));
		
		uncoloredElementColorPicker = new ColorPicker();
		panel.add(new JLabel("Uncolored element: "), new GridBagConstraints(1, 19, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(uncoloredElementColorPicker,       new GridBagConstraints(2, 19, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		panel.add(new JLabel("Element colors: "), new GridBagConstraints(1, 20, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		elementColorColorPickers = new Stack<ColorPicker>();
		
		//final JPanel elementColorsPanel = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.CENTER, 0, 4));
		final JPanel elementColorsPanel = new JPanel(new LayoutManager()
		{
			int hgap = 0, vgap = 4;
			
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
		elementColorsPanel.setBorder(null);
		
		JButton addButton = new JButton("Add");
		addButton.setPreferredSize(new Dimension(80, 28));
		
		final JButton removeButton = new JButton("Remove");
		removeButton.setPreferredSize(new Dimension(80, 28));
		removeButton.setEnabled(false);
		
		addButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				ColorPicker picker = new ColorPicker();
				elementColorsPanel.add(picker);
				elementColorColorPickers.push(picker);
				removeButton.setEnabled(true);
				elementColorsPanel.revalidate();
			}
		} );
		removeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				elementColorsPanel.remove(elementColorColorPickers.pop());
				if(elementColorColorPickers.size() == 1) removeButton.setEnabled(false);
				elementColorsPanel.revalidate();
			}
		} );
		
		ColorPicker elementColorColorPicker = new ColorPicker();
		elementColorsPanel.add(elementColorColorPicker);
		elementColorColorPickers.push(elementColorColorPicker);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(-2, 9, 9, 13));
		buttonPanel.setBackground(panel.getBackground());
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(addButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(removeButton);
		
		panel.add(elementColorsPanel, new GridBagConstraints(2, 20, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(-2, 2, 2, 7), 0, 0));
		
		panel.add(buttonPanel, new GridBagConstraints(2, 21, 1, 1, 1, 1, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		panel.add(Box.createRigidArea(new Dimension(1, 13)), new GridBagConstraints(2, 22, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		return scrollPane;
	}
	
	private JScrollPane initializeUnderTheHoodPanel()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.white);
		
		JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(null);
		
		Component col0 = new JPanel(); col0.setPreferredSize(new Dimension(columnWidths[0], 8)); col0.setSize(col0.getPreferredSize()); col0.setMaximumSize(col0.getPreferredSize()); col0.setMinimumSize(col0.getPreferredSize());
		col0.setBackground(Color.red); panel.add(col0, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		Component col1 = new JPanel(); col1.setPreferredSize(new Dimension(columnWidths[1], 8)); col1.setSize(col1.getPreferredSize()); col1.setMaximumSize(col1.getPreferredSize()); col1.setMinimumSize(col0.getPreferredSize());
		col1.setBackground(Color.blue); panel.add(col1, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		Component col2 = new JPanel(); col2.setPreferredSize(new Dimension(columnWidths[2], 8)); col2.setSize(col2.getPreferredSize()); col2.setMaximumSize(col2.getPreferredSize()); col2.setMinimumSize(col0.getPreferredSize());
		col2.setBackground(Color.red); panel.add(col2, new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		panel.add(new Header("Clicking:"), new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(10, 20, 10, 10), 0, 0));
		
		vertexClickMarginTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Vertex click margin: "), new GridBagConstraints(1, 2, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(vertexClickMarginTextField,          new GridBagConstraints(2, 2, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		edgeHandleClickMarginTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Edge handle click margin: "), new GridBagConstraints(1, 3, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(edgeHandleClickMarginTextField,           new GridBagConstraints(2, 3, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		captionHandleClickMarginTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Caption handle click margin: "), new GridBagConstraints(1, 4, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(captionHandleClickMarginTextField,           new GridBagConstraints(2, 4, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		captionEditorClickMarginTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Caption editor click margin: "), new GridBagConstraints(1, 5, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(captionEditorClickMarginTextField,           new GridBagConstraints(2, 5, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		panel.add(new Header("Zooming:"), new GridBagConstraints(0, 6, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(10, 20, 10, 10), 0, 0));
		
		zoomInFactorTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Zoom in factor: "), new GridBagConstraints(1, 7, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(zoomInFactorTextField,          new GridBagConstraints(2, 7, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		zoomOutFactorTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Zoom out factor: "), new GridBagConstraints(1, 8, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(zoomOutFactorTextField,          new GridBagConstraints(2, 8, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		maximumZoomFactorTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Maximum zoom factor: "), new GridBagConstraints(1, 9, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(maximumZoomFactorTextField,          new GridBagConstraints(2, 9, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		zoomGraphPaddingTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Zoom graph padding: "), new GridBagConstraints(1, 10, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(zoomGraphPaddingTextField,          new GridBagConstraints(2, 10, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		scrollIncrementZoomTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Scroll zoom increment: "), new GridBagConstraints(1, 11, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(scrollIncrementZoomTextField,          new GridBagConstraints(2, 11, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		panel.add(new Header("Rendering:"), new GridBagConstraints(0, 12, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(10, 20, 10, 10), 0, 0));
		
		useAntiAliasingCheckBox = new JCheckBox();
		useAntiAliasingCheckBox.setBackground(panel.getBackground());
		useAntiAliasingCheckBox.setPreferredSize(new Dimension(32, useAntiAliasingCheckBox.getPreferredSize().height));
		panel.add(new JLabel("Use anti-aliasing: "), new GridBagConstraints(1, 13, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(useAntiAliasingCheckBox,           new GridBagConstraints(2, 13, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		usePureStrokeCheckBox = new JCheckBox();
		usePureStrokeCheckBox.setBackground(panel.getBackground());
		usePureStrokeCheckBox.setPreferredSize(new Dimension(32, usePureStrokeCheckBox.getPreferredSize().height));
		panel.add(new JLabel("Use pure stroke: "), new GridBagConstraints(1, 14, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(usePureStrokeCheckBox,           new GridBagConstraints(2, 14, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		useBicubicInterpolationCheckBox = new JCheckBox();
		useBicubicInterpolationCheckBox.setBackground(panel.getBackground());
		useBicubicInterpolationCheckBox.setPreferredSize(new Dimension(32, useBicubicInterpolationCheckBox.getPreferredSize().height));
		panel.add(new JLabel("Use bicubic interpolation: "), new GridBagConstraints(1, 15, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(useBicubicInterpolationCheckBox,           new GridBagConstraints(2, 15, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		panel.add(new Header("Arranging:"), new GridBagConstraints(0, 16, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(10, 20, 10, 10), 0, 0));
		
		arrangeCircleRadiusMultiplierTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Arrange circle radius multiplier: "), new GridBagConstraints(1, 17, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(arrangeCircleRadiusMultiplierTextField,           new GridBagConstraints(2, 17, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		arrangeGridSpacingTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Arrange grid spacing: "), new GridBagConstraints(1, 18, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(arrangeGridSpacingTextField,          new GridBagConstraints(2, 18, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		autoArrangeAttractiveForceTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Auto-arrange attractive force: "), new GridBagConstraints(1, 19, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(autoArrangeAttractiveForceTextField,           new GridBagConstraints(2, 19, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		autoArrangeRepulsiveForceTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Auto-arrange repulsive force: "), new GridBagConstraints(1, 20, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(autoArrangeRepulsiveForceTextField,           new GridBagConstraints(2, 20, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		autoArrangeDecelerationFactorTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Auto-arrange deceleration factor: "), new GridBagConstraints(1, 21, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(autoArrangeDecelerationFactorTextField,           new GridBagConstraints(2, 21, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		panel.add(new Header("Windows:"), new GridBagConstraints(0, 22, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(10, 20, 10, 10), 0, 0));
		
		mainWindowSizeTextField = new ValidatingTextField(16, "-1|\\d+");
		panel.add(new JLabel("Main window size: "), new GridBagConstraints(1, 23, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(mainWindowSizeTextField,          new GridBagConstraints(2, 23, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		graphWindowSizeTextField = new ValidatingTextField(16, "-1|\\d+");
		panel.add(new JLabel("Graph window size: "), new GridBagConstraints(1, 24, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(graphWindowSizeTextField,          new GridBagConstraints(2, 24, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		cascadeWindowOffsetTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Cascade window offset: "), new GridBagConstraints(1, 25, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(cascadeWindowOffsetTextField,          new GridBagConstraints(2, 25, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		panel.add(new Header("Other:"), new GridBagConstraints(0, 26, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(10, 20, 10, 10), 0, 0));
		
		defaultGraphNameTextField = new JTextField(20);
		panel.add(new JLabel("Default graph name: "), new GridBagConstraints(1, 27, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(defaultGraphNameTextField,          new GridBagConstraints(2, 27, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		directedEdgeArrowRatioTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Directed edge arrow ratio: "), new GridBagConstraints(1, 28, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(directedEdgeArrowRatioTextField,           new GridBagConstraints(2, 28, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		arrowKeyIncrementTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Arrow key increment: "), new GridBagConstraints(1, 29, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(arrowKeyIncrementTextField,          new GridBagConstraints(2, 29, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		edgeSnapMarginRatioTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Edge snap margin ratio: "), new GridBagConstraints(1, 30, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(edgeSnapMarginRatioTextField,           new GridBagConstraints(2, 30, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		areCloseDistanceTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Are close distance: "), new GridBagConstraints(1, 31, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(areCloseDistanceTextField,          new GridBagConstraints(2, 31, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		paintToolMenuDelayTextField = new ValidatingTextField(8, "-1|\\d+");
		panel.add(new JLabel("Paint tool menu delay: "), new GridBagConstraints(1, 32, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		panel.add(paintToolMenuDelayTextField,           new GridBagConstraints(2, 32, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		panel.add(Box.createRigidArea(new Dimension(1, 13)), new GridBagConstraints(0, 33, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 7), 0, 0));
		
		return scrollPane;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if ("Ok".equals(e.getActionCommand()))
			value = "value";
		
		PreferencesDialog.dialog.setVisible(false);
	}

	private class Header extends JLabel
	{
		public Header(String label)
		{
			Font font = super.getFont();
			super.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize()));
			super.setText(label);
		}
	}
}















