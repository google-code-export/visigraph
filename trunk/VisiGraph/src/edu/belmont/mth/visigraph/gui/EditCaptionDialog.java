/**
 * EditCaptionDialog.java
 */
package edu.belmont.mth.visigraph.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @author Cameron Behar
 *
 */
@SuppressWarnings("serial")
public class EditCaptionDialog extends JDialog implements ActionListener
{
	private static EditCaptionDialog	dialog;
	private static JLabel				captionTextLabel;
	private static JTextArea			captionTextArea;
	private static String				value;
	
	public static String showDialog(Component frameComp, Component locationComp, String defaultText)
	{
		Frame frame = JOptionPane.getFrameForComponent(frameComp);
		dialog = new EditCaptionDialog(frame, locationComp, defaultText);
		dialog.setVisible(true);
		return value;
	}
	
	private EditCaptionDialog(Frame frame, Component locationComp, String defaultText)
	{
		super(frame, "Edit caption", true);
		
		GridBagLayout gbl = new GridBagLayout();
		JPanel inputPanel = new JPanel(gbl);
		
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		inputPanel.add(Box.createRigidArea(new Dimension(9, 9)), gridBagConstraints);
		
		captionTextLabel = new JLabel("Caption text:");
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
		gridBagConstraints.gridwidth = 1;
		JScrollPane captionTextAreaScrollPane = new JScrollPane(captionTextArea);
		captionTextAreaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		captionTextAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		captionTextAreaScrollPane.setPreferredSize(new Dimension(300, 150));
		inputPanel.add(captionTextAreaScrollPane, gridBagConstraints);

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
		setPreferredSize(size);
		
		pack();
		setLocationRelativeTo(locationComp);
		value = null;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if ("Ok".equals(e.getActionCommand()))
			value = captionTextArea.getText();
		
		EditCaptionDialog.dialog.setVisible(false);
	}
}