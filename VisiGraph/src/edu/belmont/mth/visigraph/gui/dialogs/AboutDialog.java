/**
 * AboutDialog.java
 */
package edu.belmont.mth.visigraph.gui.dialogs;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import edu.belmont.mth.visigraph.resources.*;
import edu.belmont.mth.visigraph.settings.*;

/**
 * @author Cameron Behar
 *
 */
@SuppressWarnings("serial")
public class AboutDialog extends JDialog implements ActionListener
{
	private static AboutDialog dialog;
	
	public static void showDialog(Component frameComp, Component locationComp)
	{
		Frame frame = JOptionPane.getFrameForComponent(frameComp);
		dialog = new AboutDialog(frame, locationComp);
		dialog.setVisible(true);
		return;
	}
	
	private AboutDialog(Frame frame, Component locationComp)
	{
		super(frame, StringBundle.get("about_dialog_title"), true);
		this.setResizable(false);
		
		JPanel inputPanel = new JPanel(new GridBagLayout());
		inputPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		
		JLabel appIconLabel = new JLabel(ImageIconBundle.get("app_icon_128x128"));
		appIconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24));
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 13;
		inputPanel.add( appIconLabel, gridBagConstraints );
		
		JLabel appTitleLabel = new JLabel(GlobalSettings.applicationName);
		appTitleLabel.setFont(new Font(appTitleLabel.getFont().getFamily(), Font.BOLD, 18));
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridheight = 1;
		inputPanel.add( appTitleLabel, gridBagConstraints );
		
		JLabel appByLabel = new JLabel(String.format(StringBundle.get("about_dialog_by_label"), GlobalSettings.applicationAuthor));
		appByLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		inputPanel.add( appByLabel, gridBagConstraints );
		
		JLabel appBuildLabel = new JLabel(String.format(StringBundle.get("about_dialog_version_label"), GlobalSettings.applicationVersion));
		appBuildLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		inputPanel.add( appBuildLabel, gridBagConstraints );
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		inputPanel.add( Box.createVerticalStrut(15), gridBagConstraints );
		
		JLabel appDescriptionLabel = new JLabel(GlobalSettings.applicationDescription);
		appDescriptionLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		inputPanel.add( appDescriptionLabel, gridBagConstraints );
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 6;
		inputPanel.add( Box.createVerticalStrut(5), gridBagConstraints );
		
		JLabel appCopyrightLine0Label = new JLabel(String.format(StringBundle.get("about_dialog_copyright_line_0"), Calendar.getInstance().get(Calendar.YEAR), GlobalSettings.applicationAuthor, GlobalSettings.applicationName));
		appCopyrightLine0Label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 7;
		inputPanel.add( appCopyrightLine0Label, gridBagConstraints );
		
		JLabel appCopyrightLine1Label = new JLabel(StringBundle.get("about_dialog_copyright_line_1"));
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 8;
		inputPanel.add( appCopyrightLine1Label, gridBagConstraints );
		
		JLabel appCopyrightLine2Label = new JLabel(String.format(StringBundle.get("about_dialog_copyright_line_2"), GlobalSettings.applicationWebsite));
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 9;
		inputPanel.add( appCopyrightLine2Label, gridBagConstraints );
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 10;
		inputPanel.add( Box.createVerticalStrut(5), gridBagConstraints );
		
		JLabel appIncludesLine0Label = new JLabel(StringBundle.get("about_dialog_includes_line_0"));
		appIncludesLine0Label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 11;
		inputPanel.add( appIncludesLine0Label, gridBagConstraints );
		
		JLabel appIncludesLine1Label = new JLabel(StringBundle.get("about_dialog_includes_line_1"));
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 12;
		inputPanel.add( appIncludesLine1Label, gridBagConstraints );
		
		//Create and initialize the buttons
		final JButton okButton = new JButton(StringBundle.get("ok_button_text"));
		okButton.setPreferredSize(new Dimension(80, 28));
		okButton.setActionCommand("Ok");
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);
		
		//Lay out the buttons from left to right
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(-2, 9, 9, 13));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(okButton);
		
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
	}

	public void actionPerformed(ActionEvent e)
	{
		AboutDialog.dialog.setVisible(false);
	}
}















