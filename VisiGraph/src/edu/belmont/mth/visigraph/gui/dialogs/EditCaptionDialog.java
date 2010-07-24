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
	
	public static Value showDialog(Component frameComp, Component locationComp, String defaultText, double defaultSize)
	{
		Frame frame = JOptionPane.getFrameForComponent(frameComp);
		dialog = new EditCaptionDialog(frame, locationComp, defaultText, defaultSize);
		dialog.setVisible(true);
		return value;
	}
	
	private EditCaptionDialog(Frame frame, Component locationComp, String defaultText, double defaultSize)
	{
		super(frame, StringBundle.get("edit_caption_dialog_title"), true);
		
		GridBagLayout gbl = new GridBagLayout();
		JPanel inputPanel = new JPanel(gbl);
		
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		inputPanel.add(Box.createRigidArea(new Dimension(9, 9)), gridBagConstraints);
		
		captionTextLabel = new JLabel(StringBundle.get("edit_caption_dialog_text"));
		captionTextLabel.setHorizontalAlignment(SwingConstants.LEFT);
		captionTextLabel.setVerticalAlignment(SwingConstants.TOP);
		captionTextLabel.setPreferredSize(new Dimension(captionTextLabel.getPreferredSize().width, 20));
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		inputPanel.add(captionTextLabel, gridBagConstraints);
		
		captionTextArea = new JTextArea(defaultText);
		captionTextArea.setFont(captionTextLabel.getFont());
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 2;
		JScrollPane captionTextAreaScrollPane = new JScrollPane(captionTextArea);
		captionTextAreaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		captionTextAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		captionTextAreaScrollPane.setPreferredSize(new Dimension(300, 150));
		inputPanel.add(captionTextAreaScrollPane, gridBagConstraints);
		
		captionFontSizeSlider = new JSlider(SwingConstants.HORIZONTAL, 8, 100, 8);
		captionFontSizeSlider.setMajorTickSpacing(10);
		captionFontSizeSlider.setPaintTicks(true);
		captionFontSizeSlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				captionFontSizeSlider.setToolTipText(captionFontSizeSlider.getValue() + "%");
				captionTextArea.setFont(new Font(captionTextArea.getFont().getFamily(), captionTextArea.getFont().getStyle(), (int)Math.round(Math.pow(captionFontSizeSlider.getValue() * 0.3155, 2))));
			}
		});
		captionFontSizeSlider.setBorder(BorderFactory.createEmptyBorder(10, 0, -2, 0));
		captionFontSizeSlider.setValue(9);
		captionFontSizeSlider.setValue((int)(Math.round(Math.sqrt(defaultSize) / 0.315)));
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 2;
		inputPanel.add(captionFontSizeSlider, gridBagConstraints);
		
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
		this.setLocationRelativeTo(locationComp);
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


