/**
 * NewGraphDialog.java
 */
package edu.belmont.mth.visigraph.gui.dialogs;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.gui.controls.*;
import edu.belmont.mth.visigraph.models.generators.*;
import edu.belmont.mth.visigraph.resources.*;

/**
 * @author Cameron Behar
 * 
 */
@SuppressWarnings("serial")
public class NewGraphDialog extends JDialog implements ActionListener
{
	private static NewGraphDialog		dialog;
	private static JComboBox			generatorComboBox;
	private static JLabel				generatorParametersLabel;
	private static ValidatingTextField	generatorParametersField;
	private static JCheckBox			allowLoopsCheckBox;
	private static JCheckBox			allowDirectedEdgesCheckBox;
	private static JCheckBox			allowMultipleEdgesCheckBox;
	private static JCheckBox			allowCyclesCheckBox;
	private static JButton				okButton;
	private static JButton				cancelButton;
	private static Graph				value;
	
	public static Graph showDialog(Component frameComp, Component locationComp)
	{
		Frame frame = JOptionPane.getFrameForComponent(frameComp);
		dialog = new NewGraphDialog(frame, locationComp);
		dialog.setVisible(true);
		return value;
	}
	
	private NewGraphDialog(Frame frame, Component locationComp)
	{
		super(frame, StringBundle.get("new_graph_dialog_title"), true);
		
		GridBagLayout gbl = new GridBagLayout();
		gbl.rowHeights = new int[] { 9, 28, 28, 28, 28 };
		JPanel inputPanel = new JPanel(gbl);
		
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		
		JLabel generatorLabel = new JLabel(StringBundle.get("new_graph_dialog_family_label"));
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 1;
		generatorLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		inputPanel.add(generatorLabel, gridBagConstraints);
		
		generatorComboBox = new JComboBox();
		for(GeneratorBase generator : GeneratorService.instance.generators)
			generatorComboBox.addItem(generator);
		generatorComboBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent arg)
			{
				generatorChanged(arg.getItem());
			}
		});
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 2;
		inputPanel.add(generatorComboBox, gridBagConstraints);
		
		generatorParametersLabel = new JLabel(StringBundle.get("new_graph_dialog_parameters_label"));
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 1;
		generatorParametersLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		inputPanel.add(generatorParametersLabel, gridBagConstraints);
		
		generatorParametersField = new ValidatingTextField(10, ".*");
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 2;
		inputPanel.add(generatorParametersField, gridBagConstraints);
		
		allowLoopsCheckBox = new JCheckBox(StringBundle.get("new_graph_dialog_allow_loops_label"));
		allowLoopsCheckBox.setPreferredSize(new Dimension(160, allowLoopsCheckBox.getPreferredSize().height));
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 1;
		allowLoopsCheckBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent arg)
			{
				GeneratorBase generator = (GeneratorBase)generatorComboBox.getSelectedItem();
				
				if(!generator.areCyclesAllowed().isForced())
				{
					if(allowLoopsCheckBox.isSelected())
					{
						allowCyclesCheckBox.setEnabled(false);
						allowCyclesCheckBox.setSelected(true);
					}
					else
					{
						
						if(!allowMultipleEdgesCheckBox.isSelected())
							allowCyclesCheckBox.setEnabled(true);
					}
				}
			}
		});
		inputPanel.add(allowLoopsCheckBox, gridBagConstraints);
		
		allowDirectedEdgesCheckBox = new JCheckBox(StringBundle.get("new_graph_dialog_allow_directed_edges_label"));
		allowDirectedEdgesCheckBox.setPreferredSize(new Dimension(160, allowDirectedEdgesCheckBox.getPreferredSize().height));
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 1;
		inputPanel.add(allowDirectedEdgesCheckBox, gridBagConstraints);
		
		allowMultipleEdgesCheckBox = new JCheckBox(StringBundle.get("new_graph_dialog_allow_multiple_edges_label"));
		allowMultipleEdgesCheckBox.setPreferredSize(new Dimension(160, allowMultipleEdgesCheckBox.getPreferredSize().height));
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 1;
		allowMultipleEdgesCheckBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				GeneratorBase generator = (GeneratorBase)generatorComboBox.getSelectedItem();
				
				if(!generator.areCyclesAllowed().isForced())
				{
					if(allowMultipleEdgesCheckBox.isSelected())
					{
						allowCyclesCheckBox.setEnabled(false);
						allowCyclesCheckBox.setSelected(true);
					}
					else
					{
						if(!allowLoopsCheckBox.isSelected())
							allowCyclesCheckBox.setEnabled(true);
					}
				}
			} 
		});
		inputPanel.add(allowMultipleEdgesCheckBox, gridBagConstraints);
		
		allowCyclesCheckBox = new JCheckBox(StringBundle.get("new_graph_dialog_allow_cycles_label"));
		allowCyclesCheckBox.setPreferredSize(new Dimension(160, allowCyclesCheckBox.getPreferredSize().height));
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 1;
		allowCyclesCheckBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				GeneratorBase generator = (GeneratorBase)generatorComboBox.getSelectedItem();
				
				if(!generator.areLoopsAllowed().isForced())
				{
					if(allowCyclesCheckBox.isSelected())
					{
						allowLoopsCheckBox.setEnabled(true);
					}
					else
					{
						allowLoopsCheckBox.setEnabled(false);
						allowLoopsCheckBox.setSelected(false);
					}
				}
				
				if(!generator.areMultipleEdgesAllowed().isForced())
				{
					if(allowCyclesCheckBox.isSelected())
					{
						allowMultipleEdgesCheckBox.setEnabled(true);
					}
					else
					{
						allowMultipleEdgesCheckBox.setEnabled(false);
						allowMultipleEdgesCheckBox.setSelected(false);
					}
				}
			} 
		});
		inputPanel.add(allowCyclesCheckBox, gridBagConstraints);
		
		//Create and initialize the buttons
		okButton = new JButton(StringBundle.get("ok_button_text"));
		okButton.setPreferredSize(new Dimension(80, 28));
		okButton.setActionCommand("Ok");
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);
		
		cancelButton = new JButton(StringBundle.get("cancel_button_text"));
		cancelButton.setPreferredSize(new Dimension(80, 28));
		cancelButton.addActionListener(this);
		
		//Lay out the buttons from left to right
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(cancelButton);
		
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridwidth = 3;
		inputPanel.add(buttonPanel, gridBagConstraints);
		
		//Put everything together, using the content pane's BorderLayout
		Container contentPanel = getContentPane();
		contentPanel.setLayout(new BorderLayout(9, 9));
		contentPanel.add(inputPanel, BorderLayout.CENTER);
		contentPanel.add(buttonPanel, BorderLayout.PAGE_END);
		
		Dimension size = this.getPreferredSize();
		size.width = (int)Math.max(size.width, 400) + 40;
		size.height += 40;
		this.setPreferredSize(size);
		
		this.pack();
		this.setResizable(false);
		setLocationRelativeTo(locationComp);
		generatorChanged(generatorComboBox.getSelectedObjects()[0]);
		value = null;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if ("Ok".equals(e.getActionCommand()))
		{
			if(!generatorParametersField.isValid())
				return;
			
			GeneratorBase generator = (GeneratorBase)generatorComboBox.getSelectedObjects()[0];
			value = generator.generate(generatorParametersField.getText(), allowLoopsCheckBox.isSelected(), allowDirectedEdgesCheckBox.isSelected(), allowMultipleEdgesCheckBox.isSelected(), allowCyclesCheckBox.isSelected());
		}
		
		NewGraphDialog.dialog.setVisible(false);
	}
	
	private void generatorChanged(Object item)
	{
		if (item instanceof GeneratorBase)
		{
			GeneratorBase generator = (GeneratorBase) item;
			
			generatorParametersLabel.setEnabled(generator.areParametersAllowed().isTrue());
			generatorParametersField.setEnabled(generator.areParametersAllowed().isTrue());
			
			allowLoopsCheckBox.setSelected(generator.areLoopsAllowed().isTrue());
			allowLoopsCheckBox.setEnabled(!generator.areLoopsAllowed().isForced());
			
			allowDirectedEdgesCheckBox.setSelected(generator.areDirectedEdgesAllowed().isTrue());
			allowDirectedEdgesCheckBox.setEnabled(!generator.areDirectedEdgesAllowed().isForced());
			
			allowMultipleEdgesCheckBox.setSelected(generator.areMultipleEdgesAllowed().isTrue());
			allowMultipleEdgesCheckBox.setEnabled(!generator.areMultipleEdgesAllowed().isForced());
			
			allowCyclesCheckBox.setSelected(generator.areCyclesAllowed().isTrue());
			allowCyclesCheckBox.setEnabled(!generator.areCyclesAllowed().isForced());
			
			generatorParametersField.setText("");
			generatorParametersField.setValidatingExpression(generator.getParametersValidatingExpression());
			generatorParametersField.setToolTipText(generator.getParametersDescription());
			generatorParametersField.requestFocus();
		}
	}
}
