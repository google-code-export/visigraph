/**
 * DownloadsDialog.java
 */
package edu.belmont.mth.visigraph.gui.dialogs;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.regex.*;
import javax.swing.*;
import edu.belmont.mth.visigraph.gui.layouts.*;
import edu.belmont.mth.visigraph.resources.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.DebugUtilities;
import edu.belmont.mth.visigraph.utilities.WebUtilities;

/**
 * @author Cameron Behar
 *
 */
@SuppressWarnings("serial")
public class DownloadsDialog extends JDialog implements ActionListener
{
	public final JCheckBox getLatestCheckBox;
	public final JPanel generatorsPanel;
	public final JPanel functionsPanel;
	
	private static DownloadsDialog dialog;
	
	public static void showDialog(Component frameComp, Component locationComp)
	{
		Frame frame = JOptionPane.getFrameForComponent(frameComp);
		dialog = new DownloadsDialog(frame, locationComp);
		dialog.setVisible(true);
		return;
	}
	
	private DownloadsDialog(Frame frame, Component locationComp)
	{
		super(frame, StringBundle.get("downloads_dialog_title"), true);
		this.setResizable(false);
		
		getLatestCheckBox = new JCheckBox(String.format(StringBundle.get("downloads_dialog_download_latest_label"), GlobalSettings.applicationName));
		
		JLabel generatorsLabel = new JLabel(StringBundle.get("downloads_dialog_available_generators_label"));
		generatorsPanel = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0));
		generatorsPanel.setBackground(Color.white);
		JScrollPane generatorsScrollPane = new JScrollPane(generatorsPanel);
		generatorsScrollPane.setMinimumSize(new Dimension(400, 150));
		
		JLabel functionsLabel = new JLabel(StringBundle.get("downloads_dialog_available_functions_label"));
		functionsPanel = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0));
		functionsPanel.setBackground(Color.white);
		JScrollPane functionsScrollPane = new JScrollPane(functionsPanel);
		functionsScrollPane.setMinimumSize(new Dimension(400, 150));
		
		JPanel inputPanel = new JPanel();
		inputPanel.setBorder(BorderFactory.createEmptyBorder(3, 8, -5, 8));
		
		GroupLayout layout = new GroupLayout(inputPanel);
		inputPanel.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(generatorsLabel)
					.addComponent(generatorsScrollPane)
					.addComponent(functionsLabel)
					.addComponent(functionsScrollPane)
					.addComponent(getLatestCheckBox)));
		
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(generatorsLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(generatorsScrollPane))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(functionsLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(functionsScrollPane))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(getLatestCheckBox)));
		
		//Create and initialize the buttons
		final JButton okButton = new JButton(StringBundle.get("download_button_text"));
		okButton.setPreferredSize(new Dimension(100, 28));
		okButton.setActionCommand("Download");
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);
		
		JButton cancelButton = new JButton(StringBundle.get("cancel_button_text"));
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
		
		new Thread("generatorsLoader") { @Override public void run( ) { loadGenerators( ); } }.start( );
		new Thread("functionsLoader" ) { @Override public void run( ) { loadFunctions ( ); } }.start( );
	}
	
	public void loadGenerators()
	{
		try
		{
			generatorsPanel.removeAll();
			
			URLConnection conn = new URL(GlobalSettings.applicationGeneratorDirectoryUrl).openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			
			while ((line = in.readLine()) != null) 
			{
				Pattern pattern = Pattern.compile("^.*<li><a\\shref=\"([^\\r\\n]+?)\\.generator\">([^\\r\\n]+?)\\.generator</a></li>.*$");
				Matcher matcher = pattern.matcher(line); matcher.find();
				
				if(matcher.matches() && matcher.group(1).equals(matcher.group(2)))
				{
					JCheckBox checkbox = new JCheckBox(matcher.group(1));
					checkbox.setPreferredSize(new Dimension(375, checkbox.getPreferredSize().height));
					checkbox.setBackground(generatorsPanel.getBackground());
					generatorsPanel.add(checkbox);
				}
			}
			
			generatorsPanel.updateUI();
			in.close();
		}
		catch (Throwable ex) { DebugUtilities.LogException("An exception occurred while downloading the standard list of generators.", ex); }
	}
	
	public void loadFunctions()
	{
		try
		{
			functionsPanel.removeAll();
			
			URLConnection conn = new URL(GlobalSettings.applicationFunctionDirectoryUrl).openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			
			while ((line = in.readLine()) != null) 
			{
				Pattern pattern = Pattern.compile("^.*<li><a\\shref=\"([^\\r\\n]+?)\\.function\">([^\\r\\n]+?)\\.function</a></li>.*$");
				Matcher matcher = pattern.matcher(line); matcher.find();
				
				if(matcher.matches() && matcher.group(1).equals(matcher.group(2)))
				{
					JCheckBox checkbox = new JCheckBox(matcher.group(1));
					checkbox.setPreferredSize(new Dimension(375, checkbox.getPreferredSize().height));
					checkbox.setBackground(functionsPanel.getBackground());
					functionsPanel.add(checkbox);
				}
			}
			
			functionsPanel.updateUI();
			in.close();
		}
		catch (Throwable ex) { DebugUtilities.LogException("An exception occurred while downloading the standard list of functions.", ex); }
	}

	public void downloadGenerators() throws Exception
	{
		File folder = new File("generators");
		
		if(!folder.exists() || !folder.isDirectory())
			folder.mkdir();
		
		for(Component component : generatorsPanel.getComponents())
		{
			JCheckBox checkBox = (JCheckBox)component;
			
			if(checkBox.isSelected())
				WebUtilities.downloadFile(String.format(GlobalSettings.applicationGeneratorFileUrl, checkBox.getText()), String.format("generators/%s.generator", checkBox.getText()));
		}
	}
	
	public void downloadFunctions() throws Exception
	{
		File folder = new File("functions");

		if(!folder.exists() || !folder.isDirectory())
			folder.mkdir();
		
		for(Component component : functionsPanel.getComponents())
		{
			JCheckBox checkBox = (JCheckBox)component;
			
			if(checkBox.isSelected())
				WebUtilities.downloadFile(String.format(GlobalSettings.applicationFunctionFileUrl, checkBox.getText()), String.format("functions/%s.function", checkBox.getText()));
		}
	}
	
	public void downloadLatestVersion() throws Exception
	{
		if(getLatestCheckBox.isSelected())
		{
			URLConnection conn = new URL(GlobalSettings.applicationJarDirectoryUrl).openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null, latestJarUrl = null, latestJarFilename = null;
			
			while ((line = in.readLine()) != null) 
			{
				Pattern pattern = Pattern.compile("^.*<li><a\\shref=\"([^\\r\\n]+?\\.jar)\">([^\\r\\n]+?\\.jar)</a></li>.*$");
				Matcher matcher = pattern.matcher(line); matcher.find();
				
				if(matcher.matches())
				{
					latestJarUrl = matcher.group(1);
					latestJarFilename = matcher.group(2);
				}
			}
			
			in.close();
			
			WebUtilities.downloadFile(GlobalSettings.applicationJarDirectoryUrl + latestJarUrl, latestJarFilename);
		}
	}
	
 	public void actionPerformed(ActionEvent e)
	{
 		if (e.getActionCommand().equals("Download"))
		{
 			try
 			{
 				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
 				downloadGenerators();
 				downloadFunctions();
 				downloadLatestVersion();
 			}
 			catch(Exception ex)
 			{
 				DebugUtilities.LogException("An exception occurred while downloading files.", ex);
 				JOptionPane.showMessageDialog(this, StringBundle.get("an_exception_occurred_while_downloading_files_dialog_message"), GlobalSettings.applicationName, JOptionPane.ERROR_MESSAGE);
 			}
 			finally
 			{
 				setCursor(Cursor.getDefaultCursor());
 			}
 			
			DownloadsDialog.dialog.setVisible(false);
		}
		else
			DownloadsDialog.dialog.setVisible(false);
	}
}










