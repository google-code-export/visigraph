/**
 * PreferencesDialog.java
 */
package edu.belmont.mth.visigraph.gui;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;

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
	private static JCheckBox           defaultCaptionIsSelectedCheckBox;
	private static JCheckBox           defaultShowVertexWeightsCheckBox;
	private static JCheckBox           defaultShowVertexLabelsCheckBox;
	private static JCheckBox           defaultShowEdgeHandlesCheckBox;
	private static JCheckBox           defaultShowEdgeWeightsCheckBox;
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
	private static ColorPicker 	   	   selectedEdgeHandleColorPicker;
	private static ColorPicker 		   captionTextColorPicker;
	private static ColorPicker 		   selectedCaptionTextColorPicker;
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
	private static JCheckBox           useAntiAliasingCheckBox;
	private static JCheckBox           usePureStrokeCheckBox;
	private static JCheckBox           useBicubicInterpolationCheckBox;
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
		
		loadPreferences();
		
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
		JPanel panel = new JPanel();
		panel.setBackground(Color.white);
		
		Header vertexDefaultsHeader = new Header("Vertex properties:");
		FieldLabel defaultVertexWeightLabel = new FieldLabel("Weight:"); defaultVertexWeightTextField = new ValidatingTextField(8, "-?(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?");	defaultVertexWeightTextField.setMargin(fieldMargin); defaultVertexWeightTextField.setMaximumSize(new Dimension(70, 100));
		FieldLabel defaultVertexColorLabel  = new FieldLabel("Color:");  defaultVertexColorTextField  = new ValidatingTextField(8, "-1|\\d+");													defaultVertexColorTextField .setMargin(fieldMargin); defaultVertexColorTextField .setMaximumSize(new Dimension(70, 100));
		FieldLabel defaultVertexPrefixLabel = new FieldLabel("Prefix:"); defaultVertexPrefixTextField = new JTextField(8);																		defaultVertexPrefixTextField.setMargin(fieldMargin); defaultVertexPrefixTextField.setMaximumSize(new Dimension(70, 100));
		FieldLabel defaultVertexRadiusLabel = new FieldLabel("Radius:"); defaultVertexRadiusTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?");	defaultVertexRadiusTextField.setMargin(fieldMargin); defaultVertexRadiusTextField.setMaximumSize(new Dimension(70, 100));
		FieldLabel defaultVertexIsSelectedLabel = new FieldLabel("Is selected:");
		defaultVertexIsSelectedCheckBox = new JCheckBox();
		defaultVertexIsSelectedCheckBox.setBackground(panel.getBackground());
		defaultVertexIsSelectedCheckBox.setMinimumSize(new Dimension(32, 26));
		
		Header edgeDefaultsHeader = new Header("Edge properties:");
		FieldLabel defaultEdgeWeightLabel    = new FieldLabel("Weight:");        defaultEdgeWeightTextField    = new ValidatingTextField(8, "-?(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); defaultEdgeWeightTextField   .setMargin(fieldMargin); defaultEdgeWeightTextField   .setMaximumSize(new Dimension(70, 100));
		FieldLabel defaultEdgeColorLabel     = new FieldLabel("Color:");         defaultEdgeColorTextField     = new ValidatingTextField(8, "-1|\\d+");													 defaultEdgeColorTextField    .setMargin(fieldMargin); defaultEdgeColorTextField    .setMaximumSize(new Dimension(70, 100));
		FieldLabel defaultEdgePrefixLabel    = new FieldLabel("Prefix:");        defaultEdgePrefixTextField    = new JTextField(8);																		 defaultEdgePrefixTextField   .setMargin(fieldMargin); defaultEdgePrefixTextField   .setMaximumSize(new Dimension(70, 100));
		FieldLabel defaultEdgeThicknessLabel = new FieldLabel("Thickness:");     defaultEdgeThicknessTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?");	 defaultEdgeThicknessTextField.setMargin(fieldMargin); defaultEdgeThicknessTextField.setMaximumSize(new Dimension(70, 100));
		FieldLabel defaultEdgeHandleRadiusRatioLabel = new FieldLabel("Edge handle radius ratio:"); defaultEdgeHandleRadiusRatioTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); defaultEdgeHandleRadiusRatioTextField.setMargin(fieldMargin); defaultEdgeHandleRadiusRatioTextField.setMaximumSize(new Dimension(70, 100));
		FieldLabel defaultLoopDiameterLabel  = new FieldLabel("Loop diameter:"); defaultLoopDiameterTextField  = new ValidatingTextField(8, "-?(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); defaultLoopDiameterTextField .setMargin(fieldMargin); defaultLoopDiameterTextField .setMaximumSize(new Dimension(70, 100));
		FieldLabel defaultEdgeIsSelectedLabel = new FieldLabel("Is selected:");
		defaultEdgeIsSelectedCheckBox     = new JCheckBox();
		defaultEdgeIsSelectedCheckBox.setBackground(panel.getBackground());
		defaultEdgeIsSelectedCheckBox.setMinimumSize(new Dimension(32, 26));
		
		Header captionDefaultsHeader = new Header("Caption properties:");
		FieldLabel defaultCaptionTextLabel = new FieldLabel("Text:"); defaultCaptionTextTextField = new JTextField(20); defaultCaptionTextTextField.setMargin(fieldMargin); defaultCaptionTextTextField.setMaximumSize(new Dimension(175, defaultVertexWeightTextField.getPreferredSize().height));
		FieldLabel defaultCaptionIsSelectedLabel = new FieldLabel("Is selected:");
		defaultCaptionIsSelectedCheckBox = new JCheckBox();
		defaultCaptionIsSelectedCheckBox.setBackground(panel.getBackground());
		defaultCaptionIsSelectedCheckBox.setMinimumSize(new Dimension(32, 26));

		Header viewportDefaultsHeader = new Header("Viewport settings:");
		FieldLabel defaultShowVertexWeightsLabel = new FieldLabel("Show vertex weights:");
		defaultShowVertexWeightsCheckBox = new JCheckBox();
		defaultShowVertexWeightsCheckBox.setBackground(panel.getBackground());
		defaultShowVertexWeightsCheckBox.setMinimumSize(new Dimension(32, 26));
		FieldLabel defaultShowVertexLabelsLabel = new FieldLabel("Show vertex labels:");
		defaultShowVertexLabelsCheckBox = new JCheckBox();
		defaultShowVertexLabelsCheckBox.setBackground(panel.getBackground());
		defaultShowVertexLabelsCheckBox.setMinimumSize(new Dimension(32, 26));
		FieldLabel defaultShowEdgeHandlesLabel = new FieldLabel("Show edge handles:");
		defaultShowEdgeHandlesCheckBox = new JCheckBox();
		defaultShowEdgeHandlesCheckBox.setBackground(panel.getBackground());
		defaultShowEdgeHandlesCheckBox.setMinimumSize(new Dimension(32, 26));
		FieldLabel defaultShowEdgeWeightsLabel = new FieldLabel("Show edge weights:");
		defaultShowEdgeWeightsCheckBox = new JCheckBox();
		defaultShowEdgeWeightsCheckBox.setBackground(panel.getBackground());
		defaultShowEdgeWeightsCheckBox.setMinimumSize(new Dimension(32, 26));
		FieldLabel defaultShowCaptionsLabel = new FieldLabel("Show captions:");
		defaultShowCaptionsCheckBox = new JCheckBox();
		defaultShowCaptionsCheckBox.setBackground(panel.getBackground());
		defaultShowCaptionsCheckBox.setMinimumSize(new Dimension(32, 26));
		FieldLabel defaultShowCaptionHandlesLabel = new FieldLabel("Show caption handles:");
		defaultShowCaptionHandlesCheckBox = new JCheckBox();
		defaultShowCaptionHandlesCheckBox.setBackground(panel.getBackground());
		defaultShowCaptionHandlesCheckBox.setMinimumSize(new Dimension(32, 26));
		FieldLabel defaultShowCaptionEditorsLabel = new FieldLabel("Show caption editors:");
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
					.addComponent(defaultCaptionIsSelectedLabel)
					.addComponent(defaultShowEdgeHandlesLabel)
					.addComponent(defaultShowEdgeWeightsLabel)
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
					.addComponent(defaultCaptionIsSelectedCheckBox)
					.addComponent(defaultShowEdgeHandlesCheckBox)
					.addComponent(defaultShowEdgeWeightsCheckBox)
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
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultCaptionIsSelectedLabel).addComponent(defaultCaptionIsSelectedCheckBox))
				.addComponent(viewportDefaultsHeader)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultShowEdgeHandlesLabel).addComponent(defaultShowEdgeHandlesCheckBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(defaultShowEdgeWeightsLabel).addComponent(defaultShowEdgeWeightsCheckBox))
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
		panel.setBackground(Color.white);
		
		Header graphColorsHeader = new Header("Graph colors:");
		FieldLabel graphBackgroundLabel  = new FieldLabel("Background:");         graphBackgroundColorPicker  = new ColorPicker();
		FieldLabel selectionBoxFillLabel = new FieldLabel("Selection box fill:"); selectionBoxFillColorPicker = new ColorPicker();
		FieldLabel selectionBoxLineLabel = new FieldLabel("Selection box line:"); selectionBoxLineColorPicker = new ColorPicker();
		
		Header vertexColorsHeader = new Header("Vertex colors:");
		FieldLabel vertexLineLabel         = new FieldLabel("Line:");          vertexLineColorPicker         = new ColorPicker();
		FieldLabel selectedVertexFillLabel = new FieldLabel("Selected fill:"); selectedVertexFillColorPicker = new ColorPicker();
		FieldLabel selectedVertexLineLabel = new FieldLabel("Selected line:"); selectedVertexLineColorPicker = new ColorPicker();
			
		Header edgeColorsHeader = new Header("Edge colors:");
		FieldLabel draggingHandleEdgeLabel  = new FieldLabel("Dragging handle:");  draggingHandleEdgeColorPicker  = new ColorPicker();
		FieldLabel draggingEdgeLabel        = new FieldLabel("Dragging:");         draggingEdgeColorPicker        = new ColorPicker();
		FieldLabel uncoloredEdgeHandleLabel = new FieldLabel("Uncolored handle:"); uncoloredEdgeHandleColorPicker = new ColorPicker();
		FieldLabel selectedEdgeLabel        = new FieldLabel("Selected:");         selectedEdgeColorPicker        = new ColorPicker();
		FieldLabel selectedEdgeHandleLabel  = new FieldLabel("Selected handle:");  selectedEdgeHandleColorPicker  = new ColorPicker();
			
		Header captionColorsHeader = new Header("Caption colors:");
		FieldLabel captionTextLabel         = new FieldLabel("Caption text:");  captionTextColorPicker         = new ColorPicker();
		FieldLabel selectedCaptionTextLabel = new FieldLabel("Selected text:"); selectedCaptionTextColorPicker = new ColorPicker();
			
		Header sharedColorsHeader = new Header("Shared colors:");
		FieldLabel uncoloredElementLabel = new FieldLabel("Uncolored element:"); uncoloredElementColorPicker = new ColorPicker();
		FieldLabel elementColorsLabel    = new FieldLabel("Colored elements:");
		
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
		
		addButton = new JButton("Add");
		addButton.setPreferredSize(new Dimension(80, 28));
		
		removeButton = new JButton("Remove");
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
					.addComponent(draggingHandleEdgeLabel)
					.addComponent(draggingEdgeLabel)
					.addComponent(uncoloredEdgeHandleLabel)
					.addComponent(selectedEdgeLabel)
					.addComponent(selectedEdgeHandleLabel)
					.addComponent(captionTextLabel)
					.addComponent(selectedCaptionTextLabel)
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
					.addComponent(draggingHandleEdgeColorPicker)
					.addComponent(draggingEdgeColorPicker)
					.addComponent(uncoloredEdgeHandleColorPicker)
					.addComponent(selectedEdgeColorPicker)
					.addComponent(selectedEdgeHandleColorPicker)
					.addComponent(captionTextColorPicker)
					.addComponent(selectedCaptionTextColorPicker)
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
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(draggingHandleEdgeLabel).addComponent(draggingHandleEdgeColorPicker))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(draggingEdgeLabel).addComponent(draggingEdgeColorPicker))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(uncoloredEdgeHandleLabel).addComponent(uncoloredEdgeHandleColorPicker))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(selectedEdgeLabel).addComponent(selectedEdgeColorPicker))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(selectedEdgeHandleLabel).addComponent(selectedEdgeHandleColorPicker))
				.addComponent(captionColorsHeader)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(captionTextLabel).addComponent(captionTextColorPicker))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(selectedCaptionTextLabel).addComponent(selectedCaptionTextColorPicker))
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
		panel.setBackground(Color.white);
		
		Header clickingBehaviorHeader = new Header("Clicking behavior:");
		FieldLabel vertexClickMarginLabel        = new FieldLabel("Vertex click margin:");         vertexClickMarginTextField        = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?");  vertexClickMarginTextField       .setMargin(fieldMargin); vertexClickMarginTextField       .setMaximumSize(new Dimension(70, 100));
		FieldLabel edgeHandleClickMarginLabel    = new FieldLabel("Edge handle click margin:");    edgeHandleClickMarginTextField    = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?");  edgeHandleClickMarginTextField   .setMargin(fieldMargin); edgeHandleClickMarginTextField   .setMaximumSize(new Dimension(70, 100));
		FieldLabel captionHandleClickMarginLabel = new FieldLabel("Caption handle click margin:"); captionHandleClickMarginTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?");  captionHandleClickMarginTextField.setMargin(fieldMargin); captionHandleClickMarginTextField.setMaximumSize(new Dimension(70, 100));
		FieldLabel captionEditorClickMarginLabel = new FieldLabel("Caption editor click margin:"); captionEditorClickMarginTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?");  captionEditorClickMarginTextField.setMargin(fieldMargin); captionEditorClickMarginTextField.setMaximumSize(new Dimension(70, 100));
		FieldLabel panDecelerationFactorLabel    = new FieldLabel("Pan deceleration factor:");     panDecelerationFactorTextField    = new ValidatingTextField(8, "-(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); panDecelerationFactorTextField   .setMargin(fieldMargin); panDecelerationFactorTextField   .setMaximumSize(new Dimension(70, 100));
		
		Header zoomingBehaviorHeader = new Header("Zooming behavior:");
		FieldLabel zoomInFactorLabel        = new FieldLabel("Zoom in factor:");        zoomInFactorTextField        = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); zoomInFactorTextField       .setMargin(fieldMargin); zoomInFactorTextField       .setMaximumSize(new Dimension(70, 100));
		FieldLabel zoomOutFactorLabel       = new FieldLabel("Zoom out factor:");       zoomOutFactorTextField       = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); zoomOutFactorTextField      .setMargin(fieldMargin); zoomOutFactorTextField      .setMaximumSize(new Dimension(70, 100));
		FieldLabel maximumZoomFactorLabel   = new FieldLabel("Maximum zoom factor:");   maximumZoomFactorTextField   = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); maximumZoomFactorTextField  .setMargin(fieldMargin); maximumZoomFactorTextField  .setMaximumSize(new Dimension(70, 100));
		FieldLabel zoomGraphPaddingLabel    = new FieldLabel("Zoom fit padding:");      zoomGraphPaddingTextField    = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); zoomGraphPaddingTextField   .setMargin(fieldMargin); zoomGraphPaddingTextField   .setMaximumSize(new Dimension(70, 100));
		FieldLabel scrollZoomIncrementLabel = new FieldLabel("Scroll zoom increment:"); scrollIncrementZoomTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); scrollIncrementZoomTextField.setMargin(fieldMargin); scrollIncrementZoomTextField.setMaximumSize(new Dimension(70, 100));
		
		Header arrangingBehaviorHeader = new Header("Arranging behavior:");
		FieldLabel arrangeCircleRadiusMultiplierLabel = new FieldLabel("Arrange circle radius multiplier:"); arrangeCircleRadiusMultiplierTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); arrangeCircleRadiusMultiplierTextField.setMargin(fieldMargin); arrangeCircleRadiusMultiplierTextField.setMaximumSize(new Dimension(70, 100));
		FieldLabel arrangeGridSpacingLabel            = new FieldLabel("Arrange grid spacing:");             arrangeGridSpacingTextField            = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); arrangeGridSpacingTextField           .setMargin(fieldMargin); arrangeGridSpacingTextField           .setMaximumSize(new Dimension(70, 100));
		FieldLabel autoArrangeAttractiveForceLabel    = new FieldLabel("Auto-arrange attractive force:");    autoArrangeAttractiveForceTextField    = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); autoArrangeAttractiveForceTextField   .setMargin(fieldMargin); autoArrangeAttractiveForceTextField   .setMaximumSize(new Dimension(70, 100));
		FieldLabel autoArrangeRepulsiveForceLabel     = new FieldLabel("Auto-arrange repulsive force:");     autoArrangeRepulsiveForceTextField     = new ValidatingTextField(8, "-(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); autoArrangeRepulsiveForceTextField    .setMargin(fieldMargin); autoArrangeRepulsiveForceTextField    .setMaximumSize(new Dimension(70, 100));
		FieldLabel autoArrangeDecelerationFactorLabel = new FieldLabel("Auto-arrange deceleration factor:"); autoArrangeDecelerationFactorTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); autoArrangeDecelerationFactorTextField.setMargin(fieldMargin); autoArrangeDecelerationFactorTextField.setMaximumSize(new Dimension(70, 100));
			
		Header renderingSettingsHeader = new Header("Rendering settings:");
		FieldLabel useAntiAliasingLabel = new FieldLabel("Use anti-aliasing:");
		useAntiAliasingCheckBox = new JCheckBox();
		useAntiAliasingCheckBox.setBackground(panel.getBackground());
		useAntiAliasingCheckBox.setMinimumSize(new Dimension(32, 26));
		FieldLabel usePureStrokeLabel = new FieldLabel("Use pure stroke:");
		usePureStrokeCheckBox = new JCheckBox();
		usePureStrokeCheckBox.setBackground(panel.getBackground());
		usePureStrokeCheckBox.setMinimumSize(new Dimension(32, 26));
		FieldLabel useBicubicInterpolationLabel = new FieldLabel("Use bicubic interpolation:");
		useBicubicInterpolationCheckBox = new JCheckBox();
		useBicubicInterpolationCheckBox.setBackground(panel.getBackground());
		useBicubicInterpolationCheckBox.setMinimumSize(new Dimension(32, 26));
		
		Header windowSettingsHeader = new Header("Window settings:");
		FieldLabel mainWindowSizeLabel      = new FieldLabel("Main window size:");      mainWindowSizeTextField      = new ValidatingTextField(16, "\\d+\\s*,\\s*\\d+"); mainWindowSizeTextField     .setMargin(fieldMargin); mainWindowSizeTextField     .setMaximumSize(new Dimension(140, 100));
		FieldLabel graphWindowSizeLabel     = new FieldLabel("Graph window size:");     graphWindowSizeTextField     = new ValidatingTextField(16, "\\d+\\s*,\\s*\\d+"); graphWindowSizeTextField    .setMargin(fieldMargin); graphWindowSizeTextField    .setMaximumSize(new Dimension(140, 100));
		FieldLabel cascadeWindowOffsetLabel = new FieldLabel("Cascade window offset:"); cascadeWindowOffsetTextField = new ValidatingTextField(8, "\\d+");  cascadeWindowOffsetTextField.setMargin(fieldMargin); cascadeWindowOffsetTextField.setMaximumSize(new Dimension(70, 100));
			
		Header otherHeader = new Header("Other:");
		FieldLabel defaultGraphNameLabel       = new FieldLabel("Default graph name:");        defaultGraphNameTextField       = new JTextField(20);             defaultGraphNameTextField      .setMargin(fieldMargin); defaultGraphNameTextField      .setMaximumSize(new Dimension(175, 100));
		FieldLabel directedEdgeArrowRatioLabel = new FieldLabel("Directed edge arrow ratio:"); directedEdgeArrowRatioTextField = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); directedEdgeArrowRatioTextField.setMargin(fieldMargin); directedEdgeArrowRatioTextField.setMaximumSize(new Dimension(70, 100));
		FieldLabel arrowKeyIncrementLabel      = new FieldLabel("Arrow key increment:");       arrowKeyIncrementTextField      = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); arrowKeyIncrementTextField     .setMargin(fieldMargin); arrowKeyIncrementTextField     .setMaximumSize(new Dimension(70, 100));
		FieldLabel edgeSnapMarginRatioLabel    = new FieldLabel("Edge snap margin:");          edgeSnapMarginRatioTextField    = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); edgeSnapMarginRatioTextField   .setMargin(fieldMargin); edgeSnapMarginRatioTextField   .setMaximumSize(new Dimension(70, 100));
		FieldLabel areCloseDistanceLabel       = new FieldLabel("Are close distance:");        areCloseDistanceTextField       = new ValidatingTextField(8, "(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?"); areCloseDistanceTextField      .setMargin(fieldMargin); areCloseDistanceTextField      .setMaximumSize(new Dimension(70, 100));
		FieldLabel paintToolMenuDelayLabel     = new FieldLabel("Paint tool menu delay:");     paintToolMenuDelayTextField     = new ValidatingTextField(8, "\\d+"); paintToolMenuDelayTextField    .setMargin(fieldMargin); paintToolMenuDelayTextField    .setMaximumSize(new Dimension(70, 100));
		
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
					.addComponent(useAntiAliasingLabel)
					.addComponent(usePureStrokeLabel)
					.addComponent(useBicubicInterpolationLabel)
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
					.addComponent(useAntiAliasingCheckBox)
					.addComponent(usePureStrokeCheckBox)
					.addComponent(useBicubicInterpolationCheckBox)
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
				.addComponent(renderingSettingsHeader)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(useAntiAliasingLabel).addComponent(useAntiAliasingCheckBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(usePureStrokeLabel).addComponent(usePureStrokeCheckBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(useBicubicInterpolationLabel).addComponent(useBicubicInterpolationCheckBox))
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
				JOptionPane.showMessageDialog(this, "Unable to save preferences because one or more field values are invalid.", "Invalid values!", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				savePreferences( );
				value = "value";
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
		defaultCaptionIsSelectedCheckBox		.setSelected( userSettings.defaultCaptionIsSelected.get( ) );
		defaultShowVertexWeightsCheckBox		.setSelected( userSettings.defaultShowVertexWeights.get( ) );
		defaultShowVertexLabelsCheckBox			.setSelected( userSettings.defaultShowVertexLabels.get( ) );
		defaultShowEdgeHandlesCheckBox			.setSelected( userSettings.defaultShowEdgeHandles.get( ) );
		defaultShowEdgeWeightsCheckBox			.setSelected( userSettings.defaultShowEdgeWeights.get( ) );
		defaultShowCaptionsCheckBox				.setSelected( userSettings.defaultShowCaptions.get( ) );
		defaultShowCaptionHandlesCheckBox		.setSelected( userSettings.defaultShowCaptionHandles.get( ) );
		defaultShowCaptionEditorsCheckBox		.setSelected( userSettings.defaultShowCaptionEditors.get( ) );
		graphBackgroundColorPicker				.setColor( userSettings.graphBackground.get( ) );
		selectionBoxFillColorPicker				.setColor( userSettings.selectionBoxFill.get( ) );
		selectionBoxLineColorPicker				.setColor( userSettings.selectionBoxLine.get( ) );
		vertexLineColorPicker					.setColor( userSettings.vertexLine.get( ) );
		selectedVertexFillColorPicker			.setColor( userSettings.selectedVertexFill.get( ) );
		selectedVertexLineColorPicker			.setColor( userSettings.selectedVertexLine.get( ) );
		draggingHandleEdgeColorPicker			.setColor( userSettings.draggingHandleEdge.get( ) );
		draggingEdgeColorPicker					.setColor( userSettings.draggingEdge.get( ) );
		uncoloredEdgeHandleColorPicker			.setColor( userSettings.uncoloredEdgeHandle.get( ) );
		selectedEdgeColorPicker					.setColor( userSettings.selectedEdge.get( ) );
		selectedEdgeHandleColorPicker			.setColor( userSettings.selectedEdgeHandle.get( ) );
		captionTextColorPicker					.setColor( userSettings.captionText.get( ) );
		selectedCaptionTextColorPicker			.setColor( userSettings.selectedCaptionText.get( ) );
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
		useAntiAliasingCheckBox					.setSelected( userSettings.useAntiAliasing.get( ) );
		usePureStrokeCheckBox					.setSelected( userSettings.usePureStroke.get( ) );
		useBicubicInterpolationCheckBox			.setSelected( userSettings.useBicubicInterpolation.get( ) );
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
		userSettings.defaultVertexIsSelected		.set( defaultVertexIsSelectedCheckBox.isSelected( ) );
		userSettings.defaultEdgeWeight				.set( new Double ( defaultEdgeWeightTextField.getText( ) ) );
		userSettings.defaultEdgeColor				.set( new Integer( defaultEdgeColorTextField.getText( ) ) );
		userSettings.defaultEdgePrefix				.set(              defaultEdgePrefixTextField.getText( )   );
		userSettings.defaultEdgeThickness			.set( new Double ( defaultEdgeThicknessTextField.getText( ) ) );
		userSettings.defaultEdgeHandleRadiusRatio	.set( new Double ( defaultEdgeHandleRadiusRatioTextField.getText( ) ) );
		userSettings.defaultLoopDiameter			.set( new Double ( defaultLoopDiameterTextField.getText( ) ) );
		userSettings.defaultEdgeIsSelected			.set( defaultEdgeIsSelectedCheckBox.isSelected( ) );
		userSettings.defaultCaptionText				.set(              defaultCaptionTextTextField.getText( )   );
		userSettings.defaultCaptionIsSelected		.set( defaultCaptionIsSelectedCheckBox.isSelected( ) );
		userSettings.defaultShowVertexWeights		.set( defaultShowVertexWeightsCheckBox.isSelected( ) );
		userSettings.defaultShowVertexLabels		.set( defaultShowVertexLabelsCheckBox.isSelected( ) );
		userSettings.defaultShowEdgeHandles			.set( defaultShowEdgeHandlesCheckBox.isSelected( ) );
		userSettings.defaultShowEdgeWeights			.set( defaultShowEdgeWeightsCheckBox.isSelected( ) );
		userSettings.defaultShowCaptions			.set( defaultShowCaptionsCheckBox.isSelected( ) );
		userSettings.defaultShowCaptionHandles		.set( defaultShowCaptionHandlesCheckBox.isSelected( ) );
		userSettings.defaultShowCaptionEditors		.set( defaultShowCaptionEditorsCheckBox.isSelected( ) );
		userSettings.graphBackground				.set( graphBackgroundColorPicker.getColor( ) );
		userSettings.selectionBoxFill				.set( selectionBoxFillColorPicker.getColor( ) );
		userSettings.selectionBoxLine				.set( selectionBoxLineColorPicker.getColor( ) );
		userSettings.vertexLine						.set( vertexLineColorPicker.getColor( ) );
		userSettings.selectedVertexFill				.set( selectedVertexFillColorPicker.getColor( ) );
		userSettings.selectedVertexLine				.set( selectedVertexLineColorPicker.getColor( ) );
		userSettings.draggingHandleEdge				.set( draggingHandleEdgeColorPicker.getColor( ) );
		userSettings.draggingEdge					.set( draggingEdgeColorPicker.getColor( ) );
		userSettings.uncoloredEdgeHandle			.set( uncoloredEdgeHandleColorPicker.getColor( ) );
		userSettings.selectedEdge					.set( selectedEdgeColorPicker.getColor( ) );
		userSettings.selectedEdgeHandle				.set( selectedEdgeHandleColorPicker.getColor( ) );
		userSettings.captionText					.set( captionTextColorPicker.getColor( ) );
		userSettings.selectedCaptionText			.set( selectedCaptionTextColorPicker.getColor( ) );
		userSettings.uncoloredElementFill			.set( uncoloredElementColorPicker.getColor( ) );
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
		userSettings.useAntiAliasing				.set( useAntiAliasingCheckBox.isSelected( ) );
		userSettings.usePureStroke					.set( usePureStrokeCheckBox.isSelected( ) );
		userSettings.useBicubicInterpolation		.set( useBicubicInterpolationCheckBox.isSelected( ) );
		
		String[] dimensions = mainWindowSizeTextField.getText( ).split(",");
		userSettings.mainWindowWidth				.set( new Integer( dimensions[0].trim() ) );
		userSettings.mainWindowHeight				.set( new Integer( dimensions[1].trim() ) );
		dimensions = graphWindowSizeTextField.getText( ).split(",");
		userSettings.graphWindowWidth				.set( new Integer( dimensions[0].trim() ) );
		userSettings.graphWindowHeight				.set( new Integer( dimensions[1].trim() ) );
		
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
			System.out.println("Unable to save user settings to file \"" + userSettingsFile.getAbsolutePath() + "\".");
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















