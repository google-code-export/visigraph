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
	
	public static Graph showDialog(Component owner)
	{
		dialog = new NewGraphDialog(JOptionPane.getFrameForComponent(owner));
		dialog.setVisible(true);
		return value;
	}
	
	private NewGraphDialog(Frame owner)
	{
		super(owner, StringBundle.get("new_graph_dialog_title"), true);
		
		JPanel inputPanel = new JPanel( new GridBagLayout() { { rowHeights = new int[] { 9, 28, 28, 28, 28 }; } } );
		
		JLabel generatorLabel = new JLabel(StringBundle.get("new_graph_dialog_family_label")) { { setHorizontalAlignment(SwingConstants.RIGHT); } };
		inputPanel.add(generatorLabel, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 0; gridy = 1; } } );
		
		generatorComboBox = new JComboBox();
		for(GeneratorBase generator : GeneratorService.instance.generators)
			generatorComboBox.addItem(generator);
		generatorComboBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				generatorChanged(e.getItem());
			}
		});
		inputPanel.add(generatorComboBox, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 1; gridy = 1; gridwidth = 2; } } );
		
		generatorParametersLabel = new JLabel(StringBundle.get("new_graph_dialog_parameters_label")) { { setHorizontalAlignment(SwingConstants.RIGHT); } };
		inputPanel.add(generatorParametersLabel, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 0; gridy = 2; } });
		
		generatorParametersField = new ValidatingTextField(10, ".*");
		inputPanel.add(generatorParametersField, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 1; gridy = 2; gridwidth = 2; } } );
		
		allowLoopsCheckBox = new JCheckBox(StringBundle.get("new_graph_dialog_allow_loops_label"))
		{
			{
				setPreferredSize(new Dimension(160, getPreferredSize().height));
				addItemListener(new ItemListener()
				{
					@Override
					public void itemStateChanged(ItemEvent e)
					{
						if(allowLoopsCheckBox != null)
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
					}
				});
			}
		};
		inputPanel.add(allowLoopsCheckBox, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 1; gridy = 3; } } );
		
		allowDirectedEdgesCheckBox = new JCheckBox(StringBundle.get("new_graph_dialog_allow_directed_edges_label")) { { setPreferredSize(new Dimension(160, getPreferredSize().height)); } };
		inputPanel.add(allowDirectedEdgesCheckBox, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 2; gridy = 3; } } );
		
		allowMultipleEdgesCheckBox = new JCheckBox(StringBundle.get("new_graph_dialog_allow_multiple_edges_label"))
		{
			{
				setPreferredSize(new Dimension(160, getPreferredSize().height));
				addItemListener(new ItemListener()
				{
					@Override
					public void itemStateChanged(ItemEvent e)
					{
						if(allowMultipleEdgesCheckBox != null)
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
					} 
				});
			}
		};
		inputPanel.add(allowMultipleEdgesCheckBox, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 1; gridy = 4; } } );
		
		allowCyclesCheckBox = new JCheckBox(StringBundle.get("new_graph_dialog_allow_cycles_label"))
		{
			{
				setPreferredSize(new Dimension(160, getPreferredSize().height));
				addItemListener(new ItemListener()
				{
					@Override
					public void itemStateChanged(ItemEvent e)
					{
						if(allowCyclesCheckBox != null)
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
					} 
				});
			}
		};
		inputPanel.add(allowCyclesCheckBox, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 2; gridy = 4; } } );
		
		//Create and initialize the buttons
		okButton = new JButton(StringBundle.get("ok_button_text")) { { setPreferredSize(new Dimension(80, 28)); setActionCommand("Ok"); } };
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);
		
		cancelButton = new JButton(StringBundle.get("cancel_button_text")) { { setPreferredSize(new Dimension(80, 28)); } };
		cancelButton.addActionListener(this);
		
		//Lay out the buttons from left to right
		JPanel buttonPanel = new JPanel()
		{
			{
				setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
				setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
				add(Box.createHorizontalGlue());
				add(okButton);
				add(Box.createRigidArea(new Dimension(10, 0)));
				add(cancelButton);
			}
		};
		inputPanel.add(buttonPanel, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 0; gridy = 5; gridwidth = 3; } } );
		
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
		setLocationRelativeTo(owner);
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
			value = generator.generate(generatorParametersField.getText(), allowLoopsCheckBox.isSelected(), allowDirectedEdgesCheckBox.isSelected(), allowMultipleEdgesCheckBox.isSelected(), allowCyclesCheckBox.isSelected(), this);
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
