/**
 * EditCaptionDialog.java
 */
package edu.belmont.mth.visigraph.gui.dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import edu.belmont.mth.visigraph.resources.*;

/**
 * @author Cameron Behar
 *
 */
@SuppressWarnings("serial")
public class EditCaptionDialog extends JDialog implements ActionListener
{
	private static EditCaptionDialog	  dialog;
	private static JLabel				  captionTextLabel;
	private static JTextArea			  captionTextArea;
	private static JSlider				  captionFontSizeSlider;
	private static Value                  value;
	
	public static Value showDialog(Component owner, String defaultText, double defaultSize)
	{
		dialog = new EditCaptionDialog(JOptionPane.getFrameForComponent(owner), defaultText, defaultSize);
		dialog.setVisible(true);
		return value;
	}
	
	private EditCaptionDialog(Frame owner, String defaultText, final double defaultSize)
	{
		super(owner, StringBundle.get("edit_caption_dialog_title"), true);
		
		JPanel inputPanel = new JPanel( new GridBagLayout( ) );
		
		inputPanel.add( Box.createRigidArea(new Dimension(9, 9)), new GridBagConstraints() { { gridx = 0; gridy = 0; } } );
		
		captionTextLabel = new JLabel(StringBundle.get("edit_caption_dialog_text")) { { setHorizontalAlignment(SwingConstants.LEFT); setVerticalAlignment(SwingConstants.TOP); setPreferredSize(new Dimension(getPreferredSize().width, 20)); } };
		inputPanel.add( captionTextLabel, new GridBagConstraints( ) { { fill = GridBagConstraints.HORIZONTAL; gridx = 0; gridy = 1; } } );
		
		captionTextArea = new JTextArea(defaultText) { { setFont(captionTextLabel.getFont()); } };
		JScrollPane captionTextAreaScrollPane = new JScrollPane(captionTextArea) { { setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); setPreferredSize(new Dimension(300, 150)); } };
		inputPanel.add( captionTextAreaScrollPane, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 0; gridy = 2; gridwidth = 2; } } );
		
		captionFontSizeSlider = new JSlider(SwingConstants.HORIZONTAL, 8, 100, 8)
		{
			{
				setMajorTickSpacing(10); 
				setPaintTicks(true);
				addChangeListener(new ChangeListener()
				{
					@Override
					public void stateChanged(ChangeEvent e)
					{
						if(captionFontSizeSlider != null)
						{
							captionFontSizeSlider.setToolTipText(captionFontSizeSlider.getValue() + "%");
							captionTextArea.setFont(new Font(captionTextArea.getFont().getFamily(), captionTextArea.getFont().getStyle(), (int)Math.round(Math.pow(captionFontSizeSlider.getValue() * 0.3155, 2))));
						}
					}
				});
				setBorder(BorderFactory.createEmptyBorder(10, 0, -2, 0));
				setValue(9);
				setValue((int)(Math.round(Math.sqrt(defaultSize) / 0.315)));
			}
		};
		inputPanel.add(captionFontSizeSlider, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 0; gridy = 3; gridwidth = 2; } });
		
		//Create and initialize the buttons
		final JButton okButton = new JButton(StringBundle.get("ok_button_text")) { { setPreferredSize(new Dimension(80, 28)); setActionCommand("Ok"); } };
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);
		
		final JButton cancelButton = new JButton(StringBundle.get("cancel_button_text")) { { setPreferredSize(new Dimension(80, 28)); } };
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
		inputPanel.add(buttonPanel, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 0; gridy = 5; gridwidth = 3; } });
		
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
		this.setLocationRelativeTo(owner);
		this.setResizable(false);
		value = null;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if ("Ok".equals(e.getActionCommand()))
			value = new Value(captionTextArea.getText(), Math.pow(captionFontSizeSlider.getValue() * 0.3155, 2.0));
		
		EditCaptionDialog.dialog.setVisible(false);
	}

	public class Value
	{
		private String text;
		private double size;
		
		public Value(String text, double size)
		{
			this.text = text;
			this.size = size;
		}
		
		public String getText( )           { return text; }
		public void   setText(String text) { this.text = text; }
		
		public double getSize( )           { return size; }
		public void   setSize(double size) { this.size = size; }
	}
}


