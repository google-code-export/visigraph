/**
 * MainWindow.java
 */
package edu.belmont.mth.visigraph.gui.windows;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import edu.belmont.mth.visigraph.gui.dialogs.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.resources.*;
import edu.belmont.mth.visigraph.settings.*;

/**
 * @author Cameron Behar
 * 
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame
{
	private final JMenuBar	   menuBar;
	private final JMenu		   fileMenu;
	private final JMenuItem	    newGraphMenuItem;
	private final JMenuItem	    duplicateGraphMenuItem;
	private final JMenuItem	    openGraphMenuItem;
	private final JMenuItem	    saveGraphMenuItem;
	private final JMenuItem	    saveAsGraphMenuItem;
	private final JMenuItem	    printGraphMenuItem;
	private final JMenuItem	    exitGraphMenuItem;
	private final JMenu		   windowsMenu;
	private final JMenuItem	    cascadeMenuItem;
	private final JMenuItem	    showSideBySideMenuItem;
	private final JMenuItem	    showStackedMenuItem;
	private final JMenuItem	    tileWindowsMenuItem;
	private final JMenu		   helpMenu;
	private final JMenuItem	    helpContentsMenuItem;
	private final JMenuItem	    preferencesMenuItem;
	private final JMenuItem	    aboutVisiGraphMenuItem;
	private final MainWindow   thisFrame;
	private final JDesktopPane desktopPane;
	private final JFileChooser fileChooser;
	private UserSettings userSettings = UserSettings.instance;
	
	public MainWindow()
	{
		super(GlobalSettings.applicationName);
		this.thisFrame = this;
		this.setSize(new Dimension(userSettings.mainWindowWidth.get(), userSettings.mainWindowHeight.get()));
		this.setLocation(450, 200);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		desktopPane = new JDesktopPane();
		getContentPane().add(desktopPane, BorderLayout.CENTER);
		
		menuBar = new JMenuBar();
		
		fileChooser = new JFileChooser();
		
		fileMenu = new JMenu(StringBundle.get("file_menu_text"));
		menuBar.add(fileMenu);
		
		newGraphMenuItem = new JMenuItem(StringBundle.get("file_new_menu_text"));
		newGraphMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				Graph newGraph = NewGraphDialog.showDialog(thisFrame, thisFrame);
				if (newGraph != null)
				{
					GraphWindow graphWindow = new GraphWindow(newGraph);
					desktopPane.add(graphWindow);
					try
					{
						graphWindow.setMaximum(true);
						graphWindow.setSelected(true);
					}
					catch (PropertyVetoException e) { }
				}
			}
		});
		newGraphMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		fileMenu.add(newGraphMenuItem);
		
		duplicateGraphMenuItem = new JMenuItem(StringBundle.get("file_duplicate_menu_text"));
		duplicateGraphMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				JInternalFrame selectedFrame = desktopPane.getSelectedFrame();
				
				if(selectedFrame instanceof GraphWindow)
				{
					Graph graph = ((GraphWindow)selectedFrame).getGdc().getGraph();
					
					GraphWindow graphWindow = new GraphWindow(new Graph(graph.toString()));
					desktopPane.add(graphWindow);
					try
					{
						graphWindow.setMaximum(true);
						graphWindow.setSelected(true);
					}
					catch (PropertyVetoException e) { }
				}
			}
		});
		duplicateGraphMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
		fileMenu.add(duplicateGraphMenuItem);
		
		fileMenu.addSeparator();
		
		openGraphMenuItem = new JMenuItem(StringBundle.get("file_open_menu_text"));
		openGraphMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				GraphWindow graphWindow = null;
				
				fileChooser.resetChoosableFileFilters();
				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.setFileFilter(new FileNameExtensionFilter(StringBundle.get("visigraph_file_description"), "vsg"));
				fileChooser.setMultiSelectionEnabled(false);

				boolean success = false;
				
				while(!success)
				{ 
					if(fileChooser.showOpenDialog(thisFrame) == JFileChooser.APPROVE_OPTION)
					{
			            try
						{
			            	File selectedFile = fileChooser.getSelectedFile();    	
							Scanner scanner = new Scanner(selectedFile);
							StringBuilder sb = new StringBuilder();
							while(scanner.hasNextLine())
								sb.append(scanner.nextLine());

							scanner.close();
							
							Graph newGraph = new Graph(sb.toString());
							if (newGraph != null)
							{
								graphWindow = new GraphWindow(newGraph);
								graphWindow.setFile(selectedFile);
								desktopPane.add(graphWindow);
								try
								{
									graphWindow.setMaximum(true);
									graphWindow.setSelected(true);
								}
								catch (PropertyVetoException e) { }
							}
							
							success = true;
						}
						catch (IOException e) { success = false; }
					}
					else
						success = true;
				}
				
				if (graphWindow != null)
					graphWindow.setHasChanged(false);
			}
		});
		openGraphMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		fileMenu.add(openGraphMenuItem);
		
		saveGraphMenuItem = new JMenuItem(StringBundle.get("file_save_menu_text"));
		saveGraphMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg)
			{
				JInternalFrame selectedFrame = desktopPane.getSelectedFrame();
				GraphWindow graphWindow = ((GraphWindow)selectedFrame);
				
				if(graphWindow != null)
					try	{ graphWindow.save(); }
					catch (IOException e) { }
			}
		});
		saveGraphMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		fileMenu.add(saveGraphMenuItem);
		
		saveAsGraphMenuItem = new JMenuItem(StringBundle.get("file_save_as_menu_text"));
		saveAsGraphMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg)
			{
				JInternalFrame selectedFrame = desktopPane.getSelectedFrame();
				GraphWindow graphWindow = ((GraphWindow)selectedFrame);
				
				if(graphWindow != null)
					graphWindow.saveAs();
			}
		});
		fileMenu.add(saveAsGraphMenuItem);
		
		fileMenu.addSeparator();
		
		printGraphMenuItem = new JMenuItem(StringBundle.get("file_print_menu_text"));
		printGraphMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				JInternalFrame selectedFrame = desktopPane.getSelectedFrame();
				if(selectedFrame != null)
				{
					GraphWindow graphWindow = ((GraphWindow)selectedFrame);					
					try { graphWindow.getGdc().printGraph(); }
					catch (PrinterException e) { e.printStackTrace(); }
				}
			}
		});
		fileMenu.add(printGraphMenuItem);
		
		fileMenu.addSeparator();
		
		exitGraphMenuItem = new JMenuItem(System.getProperty("os.name").startsWith("Windows") ? StringBundle.get("file_exit_menu_text") : StringBundle.get("file_quit_menu_text"));
		exitGraphMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				System.exit(0);
			}
		});
		fileMenu.add(exitGraphMenuItem);
		
		windowsMenu = new JMenu(StringBundle.get("windows_menu_text"));
		menuBar.add(windowsMenu);
		
		cascadeMenuItem = new JMenuItem(StringBundle.get("windows_cascade_menu_text"));
		cascadeMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JInternalFrame[] frames = desktopPane.getAllFrames();
				
				for(int i = 0; i < frames.length; ++i)
				{
					try
					{
						frames[frames.length - i - 1].setMaximum(false);
						frames[frames.length - i - 1].setLocation(i * userSettings.cascadeWindowOffset.get(), i * userSettings.cascadeWindowOffset.get());
						frames[frames.length - i - 1].setSize(new Dimension(userSettings.graphWindowWidth.get(), userSettings.graphWindowHeight.get()));
					}
					catch (PropertyVetoException e1) { }
				}
			}
		});
		windowsMenu.add(cascadeMenuItem);
		
		showSideBySideMenuItem = new JMenuItem(StringBundle.get("windows_show_side_by_side_menu_text"));
		showSideBySideMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JInternalFrame[] frames = desktopPane.getAllFrames();
				
				if(frames.length > 0)
				{
					double frameWidth = desktopPane.getWidth() / frames.length;
					
					for(int i = 0; i < frames.length; ++i)
					{
						try
						{
							frames[i].setMaximum(false);
							frames[i].setLocation((int)(i * frameWidth), 0);
							frames[i].setSize((int)frameWidth, desktopPane.getHeight());
						}
						catch (PropertyVetoException e1) { }
					}
				}
			}
		});
		windowsMenu.add(showSideBySideMenuItem);
		
		showStackedMenuItem = new JMenuItem(StringBundle.get("windows_show_stacked_menu_text"));
		showStackedMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JInternalFrame[] frames = desktopPane.getAllFrames();
				if(frames.length > 0)
				{
					double frameHeight = desktopPane.getHeight() / frames.length;
				
					for(int i = 0; i < frames.length; ++i)
					{
						try
						{
							frames[i].setMaximum(false);
							frames[i].setLocation(0, (int)(i * frameHeight));
							frames[i].setSize(desktopPane.getWidth(), (int)frameHeight);
						}
						catch (PropertyVetoException e1) { }
					}
				}
			}
		});
		windowsMenu.add(showStackedMenuItem);
		
		tileWindowsMenuItem = new JMenuItem(StringBundle.get("windows_tile_menu_text"));
		tileWindowsMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JInternalFrame[] frames = desktopPane.getAllFrames();
				
				if(frames.length > 0)
				{
					int rows = (int) Math.round(Math.sqrt(frames.length));
					int columns = (int) Math.ceil(frames.length / (double) rows);
					double rowSpace = desktopPane.getHeight() / rows;
					double colSpace = desktopPane.getWidth() / columns;
					
					for (int i = 0; i < frames.length; ++i)
					{
						try
						{
							frames[i].setMaximum(false);
							frames[i].setLocation((int)((i % columns) * colSpace), (int)((i / columns) * rowSpace));
							frames[i].setSize((int)colSpace, (int)rowSpace);
						}
						catch (PropertyVetoException e1) { }
				}
				}
			}
		});
		windowsMenu.add(tileWindowsMenuItem);
		
		helpMenu = new JMenu(StringBundle.get("help_menu_text"));
		menuBar.add(helpMenu);
		
		helpContentsMenuItem = new JMenuItem(StringBundle.get("help_contents_menu_text"));
		helpMenu.add(helpContentsMenuItem);
		
		helpMenu.addSeparator();
		
		preferencesMenuItem = new JMenuItem(StringBundle.get("help_preferences_menu_text"));
		preferencesMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				PreferencesDialog.showDialog(thisFrame, thisFrame);
			}
		});
		helpMenu.add(preferencesMenuItem);
		
		helpMenu.addSeparator();
		
		aboutVisiGraphMenuItem = new JMenuItem(StringBundle.get("help_about_menu_text"));
		helpMenu.add(aboutVisiGraphMenuItem);
		
		setJMenuBar(menuBar);
		
		this.setVisible(true);
	}
}






