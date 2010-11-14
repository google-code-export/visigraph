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
	
	public static void showDialog(Component owner)
	{
		dialog = new AboutDialog(JOptionPane.getFrameForComponent(owner));
		dialog.setVisible(true);
		return;
	}
	
	private AboutDialog(Frame owner)
	{
		super(owner, StringBundle.get("about_dialog_title"), true);
		this.setResizable(false);
		
		JPanel inputPanel = new JPanel(new GridBagLayout());
		inputPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
		
		JLabel appIconLabel = new JLabel(ImageIconBundle.get("app_icon_128x128")) { { setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24)); } };
		inputPanel.add( appIconLabel, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 0; gridy = 0; gridheight = 13; } } );
		
		JLabel appTitleLabel = new JLabel(GlobalSettings.applicationName) { { setFont(new Font(getFont().getFamily(), Font.BOLD, 18)); } };
		inputPanel.add( appTitleLabel, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 1; gridy = 1; } } );
		
		JLabel appByLabel = new JLabel(String.format(StringBundle.get("about_dialog_by_label"), GlobalSettings.applicationAuthor)) { { setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); } };
		inputPanel.add( appByLabel, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 1; gridy = 2; } } );
		
		JLabel appBuildLabel = new JLabel(String.format(StringBundle.get("about_dialog_version_label"), GlobalSettings.applicationVersion)) { { setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); } };
		inputPanel.add( appBuildLabel, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 1; gridy = 3; } } );
		
		inputPanel.add( Box.createVerticalStrut(15), new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 1; gridy = 4; } } );
		
		JLabel appDescriptionLabel = new JLabel(GlobalSettings.applicationDescription) { { setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); } };
		inputPanel.add( appDescriptionLabel, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 1; gridy = 5; } } );
		
		inputPanel.add( Box.createVerticalStrut(5), new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 1; gridy = 6; } } );
		
		JLabel appCopyrightLine0Label = new JLabel(String.format(StringBundle.get("about_dialog_copyright_line_0"), Calendar.getInstance().get(Calendar.YEAR), GlobalSettings.applicationAuthor, GlobalSettings.applicationName)) { { setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); } };
		inputPanel.add( appCopyrightLine0Label, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 1; gridy = 7; } } );
		
		JLabel appCopyrightLine1Label = new JLabel(StringBundle.get("about_dialog_copyright_line_1"));
		inputPanel.add( appCopyrightLine1Label, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 1; gridy = 8; } } );
		
		JLabel appCopyrightLine2Label = new JLabel(String.format(StringBundle.get("about_dialog_copyright_line_2"), GlobalSettings.applicationWebsite));
		inputPanel.add( appCopyrightLine2Label, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 1; gridy = 9; } } );
		
		inputPanel.add( Box.createVerticalStrut(5), new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 1; gridy = 10; } } );
		
		JLabel appIncludesLine0Label = new JLabel(StringBundle.get("about_dialog_includes_line_0")) { { setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); } };
		inputPanel.add( appIncludesLine0Label, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 1; gridy = 11; } } );
		
		JLabel appIncludesLine1Label = new JLabel(StringBundle.get("about_dialog_includes_line_1"));
		inputPanel.add( appIncludesLine1Label, new GridBagConstraints() { { fill = GridBagConstraints.HORIZONTAL; gridx = 1; gridy = 12; } } );
		
		//Create and initialize the buttons
		final Frame finalOwner = owner; 
		final JButton debugButton = new JButton(StringBundle.get("debug_button_text"))
		{
			{
				setPreferredSize(new Dimension(80, 28));
				addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						DebugDialog.showDialog(finalOwner);
					}
				});
			}
		};
		
		final JButton okButton = new JButton(StringBundle.get("ok_button_text"))
		{
			{
				setPreferredSize(new Dimension(80, 28));
				setActionCommand("Ok");
			}
		};
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);
		
		//Lay out the buttons from left to right
		JPanel buttonPanel = new JPanel()
		{
			{
				setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
				setBorder(BorderFactory.createEmptyBorder(-2, 9, 9, 13));
				add(debugButton);
				add(Box.createHorizontalGlue());
				add(okButton);
			}
		};
		
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
		setLocationRelativeTo(owner);
	}

	public void actionPerformed(ActionEvent e)
	{
		AboutDialog.dialog.setVisible(false);
	}
}















