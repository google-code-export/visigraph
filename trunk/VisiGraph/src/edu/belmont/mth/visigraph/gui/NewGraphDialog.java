/**
 * NewGraphDialog.java
 */
package edu.belmont.mth.visigraph.gui;

import javax.swing.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.generators.*;
import edu.belmont.mth.visigraph.settings.GlobalSettings;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Cameron Behar
 * 
 */
@SuppressWarnings("serial")
public class NewGraphDialog extends JDialog implements ActionListener
{
	protected static NewGraphDialog		dialog;
	protected static JComboBox			functionComboBox;
	protected static JLabel				functionParametersLabel;
	protected static JTextField			functionParametersField;
	protected static JCheckBox			allowLoopsCheckBox;
	protected static JCheckBox			allowDirectedEdgesCheckBox;
	protected static JCheckBox			allowMultipleEdgesCheckBox;
	protected static JCheckBox			allowCyclesCheckBox;
	protected static Graph				value;
	
	public static Graph showDialog(Component frameComp, Component locationComp)
	{
		Frame frame = JOptionPane.getFrameForComponent(frameComp);
		dialog = new NewGraphDialog(frame, locationComp);
		dialog.setVisible(true);
		return value;
	}
	
	private NewGraphDialog(Frame frame, Component locationComp)
	{
		super(frame, "New graph", true);
		
		GridBagLayout gbl = new GridBagLayout();
		gbl.rowHeights = new int[] { 9, 28, 28, 28, 28 };
		JPanel inputPanel = new JPanel(gbl);
		
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		
		JLabel functionLabel = new JLabel("Family: ");
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 1;
		functionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		inputPanel.add(functionLabel, gridBagConstraints);
		
		functionComboBox = new JComboBox(GlobalSettings.allGraphGenerators);
		functionComboBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent arg0)
			{
				functionChanged(arg0.getItem());
			}
		});
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 2;
		inputPanel.add(functionComboBox, gridBagConstraints);
		
		functionParametersLabel = new JLabel("Parameters: ");
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 1;
		functionParametersLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		inputPanel.add(functionParametersLabel, gridBagConstraints);
		
		functionParametersField = new JTextField();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 2;
		inputPanel.add(functionParametersField, gridBagConstraints);
		
		allowLoopsCheckBox = new JCheckBox("Allow loops");
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 1;
		allowLoopsCheckBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent arg0)
			{
				GraphGeneratorBase generator = (GraphGeneratorBase)functionComboBox.getSelectedItem();
				
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
		
		allowDirectedEdgesCheckBox = new JCheckBox("Allow directed edges");
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 1;
		inputPanel.add(allowDirectedEdgesCheckBox, gridBagConstraints);
		
		allowMultipleEdgesCheckBox = new JCheckBox("Allow multiple edges");
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 1;
		allowMultipleEdgesCheckBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				GraphGeneratorBase generator = (GraphGeneratorBase)functionComboBox.getSelectedItem();
				
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
		
		allowCyclesCheckBox = new JCheckBox("Allow cycles");
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 1;
		allowCyclesCheckBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				GraphGeneratorBase generator = (GraphGeneratorBase)functionComboBox.getSelectedItem();
				
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
		size.width += 40;
		size.height += 40;
		this.setPreferredSize(size);
		
		this.pack();
		this.setResizable(false);
		setLocationRelativeTo(locationComp);
		functionChanged(functionComboBox.getSelectedObjects()[0]);
		value = null;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if ("Ok".equals(e.getActionCommand()))
			value = ((GraphGeneratorBase)functionComboBox.getSelectedObjects()[0]).generate(functionParametersField.getText(), allowLoopsCheckBox.isSelected(), allowDirectedEdgesCheckBox.isSelected(), allowMultipleEdgesCheckBox.isSelected(), allowCyclesCheckBox.isSelected());
		
		NewGraphDialog.dialog.setVisible(false);
	}
	
	private void functionChanged(Object item)
	{
		if (item instanceof GraphGeneratorBase)
		{
			GraphGeneratorBase function = (GraphGeneratorBase) item;
			
			functionParametersLabel.setEnabled(function.areParametersAllowed().isTrue());
			functionParametersField.setEnabled(function.areParametersAllowed().isTrue());
			
			allowLoopsCheckBox.setSelected(function.areLoopsAllowed().isTrue());
			allowLoopsCheckBox.setEnabled(!function.areLoopsAllowed().isForced());
			
			allowDirectedEdgesCheckBox.setSelected(function.areDirectedEdgesAllowed().isTrue());
			allowDirectedEdgesCheckBox.setEnabled(!function.areDirectedEdgesAllowed().isForced());
			
			allowMultipleEdgesCheckBox.setSelected(function.areMultipleEdgesAllowed().isTrue());
			allowMultipleEdgesCheckBox.setEnabled(!function.areMultipleEdgesAllowed().isForced());
			
			allowCyclesCheckBox.setSelected(function.areCyclesAllowed().isTrue());
			allowCyclesCheckBox.setEnabled(!function.areCyclesAllowed().isForced());
			
			functionParametersField.setText("");
			functionParametersField.requestFocus();
		}
	}
}
