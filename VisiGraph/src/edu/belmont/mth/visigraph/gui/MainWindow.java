/**
 * MainWindow.java
 */
package edu.belmont.mth.visigraph.gui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.GlobalSettings;

/**
 * @author Cameron Behar
 * 
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame implements ClipboardOwner
{
	protected final JMenuBar	 menuBar;
	protected final JMenu		 fileMenu;
	protected final JMenuItem	  newGraphMenuItem;
	protected final JMenuItem	  duplicateGraphMenuItem;
	protected final JMenuItem	  openGraphMenuItem;
	protected final JMenuItem	  saveGraphMenuItem;
	protected final JMenuItem	  exitGraphMenuItem;
	protected final JMenu		 editMenu;
	protected final JMenuItem	  cutMenuItem;
	protected final JMenuItem	  copyMenuItem;
	protected final JMenuItem	  pasteMenuItem;
	protected final JMenu		 windowsMenu;
	protected final JMenuItem	  cascadeMenuItem;
	protected final JMenuItem	  showSideBySideMenuItem;
	protected final JMenuItem	  showStackedMenuItem;
	protected final JMenuItem	  tileWindowsMenuItem;
	protected final JMenu		 helpMenu;
	protected final JMenuItem	  helpContentsMenuItem;
	protected final JMenuItem	  aboutVisiGraphMenuItem;
	protected final MainWindow   thisFrame;
	protected final JDesktopPane desktopPane;
	protected final JFileChooser fileChooser;
	
	public MainWindow()
	{
		super(GlobalSettings.applicationName);
		thisFrame = this;
		this.setSize(GlobalSettings.defaultMainWindowSize);
		this.setLocation(450, 200);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		desktopPane = new JDesktopPane();
		getContentPane().add(desktopPane, BorderLayout.CENTER);
		
		menuBar = new JMenuBar();
		
		fileChooser = new JFileChooser();
		
		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		newGraphMenuItem = new JMenuItem("New...");
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
		fileMenu.add(newGraphMenuItem);
		
		duplicateGraphMenuItem = new JMenuItem("Duplicate");
		duplicateGraphMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				JInternalFrame selectedFrame = desktopPane.getSelectedFrame();
				
				if(selectedFrame instanceof GraphWindow)
				{
					Graph graph = ((GraphWindow)selectedFrame).gdc.getGraph();
					
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
		fileMenu.add(duplicateGraphMenuItem);
		
		fileMenu.add(new JSeparator());
		
		openGraphMenuItem = new JMenuItem("Open...");
		openGraphMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				fileChooser.setFileFilter(new FileNameExtensionFilter("VisiGraph Graph File", "vsg"));
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
								GraphWindow graphWindow = new GraphWindow(newGraph);
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
			}
		});
		fileMenu.add(openGraphMenuItem);
		
		saveGraphMenuItem = new JMenuItem("Save...");
		saveGraphMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				JInternalFrame selectedFrame = desktopPane.getSelectedFrame();
				
				if(selectedFrame instanceof GraphWindow)
				{
					GraphWindow graphWindow = ((GraphWindow)selectedFrame);
					Graph graph = graphWindow.gdc.getGraph();

					fileChooser.setFileFilter(new FileNameExtensionFilter("VisiGraph Graph File", "vsg"));
					fileChooser.setMultiSelectionEnabled(false);

					boolean success = false;
					
					while(!success)
					{ 
						if(fileChooser.showSaveDialog(thisFrame) == JFileChooser.APPROVE_OPTION)
						{
				            try
							{
				            	File selectedFile = fileChooser.getSelectedFile();
				            	
				            	if(!selectedFile.getName().endsWith(".vsg"))
				            		selectedFile = new File(selectedFile.getAbsolutePath() + ".vsg");
				            	
				            	graph.name.set(selectedFile.getName().substring(0, selectedFile.getName().length() - 4));
				            	graphWindow.updateTitle();
				            	
								FileWriter fw = new FileWriter(selectedFile);
								fw.write(graph.toString());
								fw.close();
								success = true;
							}
							catch (IOException e)
							{
								success = false;
							}
						}
						else
							success = true;
					}
				}
			}
		});
		fileMenu.add(saveGraphMenuItem);
		
		fileMenu.add(new JSeparator());
		
		exitGraphMenuItem = new JMenuItem("Exit");
		exitGraphMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				System.exit(0);
			}
		});
		fileMenu.add(exitGraphMenuItem);
		
		editMenu = new JMenu("Edit");
		menuBar.add(editMenu);
		
		cutMenuItem = new JMenuItem("Cut");
		cutMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				JInternalFrame selectedFrame = desktopPane.getSelectedFrame();
				
				if(selectedFrame instanceof GraphWindow)
				{
					GraphWindow graphWindow = ((GraphWindow)selectedFrame);
					Graph graph = graphWindow.gdc.getGraph();
					
					// Make a copy of graph containing only selected elements, then remove them from the original
					Graph copy = new Graph("cut", graph.areLoopsAllowed, graph.areDirectedEdgesAllowed, graph.areMultipleEdgesAllowed, graph.areCyclesAllowed);
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
							copy.vertexes.add(vertex);
					
					for(Edge edge : graph.edges)
						if(edge.isSelected.get() && edge.from.isSelected.get() && edge.to.isSelected.get())
							copy.edges.add(edge);
					
					for(Caption caption : graph.captions)
						if(caption.isSelected.get())
							copy.captions.add(caption);
					
					// Delete all selected elements from the original graph
					int i = 0;
					while(i < graph.edges.size())
					{
						Edge edge = graph.edges.get(i);
						if(edge.isSelected.get())
							graph.edges.remove(i);
						else
							++i;
					}
					
					i = 0;
					while(i < graph.vertexes.size())
					{
						if(graph.vertexes.get(i).isSelected.get())
							graph.vertexes.remove(i);
						else
							++i;
					}
					
					i = 0;
					while(i < graph.captions.size())
					{
						if(graph.captions.get(i).isSelected.get())
							graph.captions.remove(i);
						else
							++i;
					}
					
					// Send the JSON to the clipboard
					StringSelection stringSelection = new StringSelection( copy.toString() );
				    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				    clipboard.setContents( stringSelection, thisFrame );
				}
			}
		});
		cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		editMenu.add(cutMenuItem);
		
		copyMenuItem = new JMenuItem("Copy");
		copyMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				JInternalFrame selectedFrame = desktopPane.getSelectedFrame();
				
				if(selectedFrame instanceof GraphWindow)
				{
					GraphWindow graphWindow = ((GraphWindow)selectedFrame);
					Graph graph = graphWindow.gdc.getGraph();
					
					// Make a copy of graph containing only selected elements
					Graph copy = new Graph("copy", graph.areLoopsAllowed, graph.areDirectedEdgesAllowed, graph.areMultipleEdgesAllowed, graph.areCyclesAllowed);
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
							copy.vertexes.add(vertex);
					
					for(Edge edge : graph.edges)
						if(edge.isSelected.get() && edge.from.isSelected.get() && edge.to.isSelected.get())
							copy.edges.add(edge);
					
					for(Caption caption : graph.captions)
						if(caption.isSelected.get())
							copy.captions.add(caption);
					
					// Send the JSON to the clipboard
					StringSelection stringSelection = new StringSelection( copy.toString() );
				    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				    clipboard.setContents( stringSelection, thisFrame );
				}
			}
		});
		copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		editMenu.add(copyMenuItem);
		
		pasteMenuItem = new JMenuItem("Paste");
		pasteMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				String result = "";
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				
				Transferable contents = clipboard.getContents(null);
				boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
				
				if ( hasTransferableText )
				{
					try
					{
						result = (String)contents.getTransferData(DataFlavor.stringFlavor);
						
						JInternalFrame selectedFrame = desktopPane.getSelectedFrame();
						
						if(selectedFrame instanceof GraphWindow)
						{
							GraphWindow graphWindow = ((GraphWindow)selectedFrame);
							Graph graph = graphWindow.gdc.getGraph();
							graph.deselectAll();
							graph.union(new Graph(result));
						}
					}
					catch (Exception ex) 
					{
						System.out.println(ex);
						ex.printStackTrace();
					}
				}	
			}
		});
		pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		editMenu.add(pasteMenuItem);
		
		windowsMenu = new JMenu("Windows");
		menuBar.add(windowsMenu);
		
		cascadeMenuItem = new JMenuItem("Cascade");
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
						frames[frames.length - i - 1].setLocation(i * GlobalSettings.cascadeWindowsOffset, i * GlobalSettings.cascadeWindowsOffset);
						frames[frames.length - i - 1].setSize(GlobalSettings.defaultGraphWindowSize);
					}
					catch (PropertyVetoException e1) { }
				}
			}
		});
		windowsMenu.add(cascadeMenuItem);
		
		showSideBySideMenuItem = new JMenuItem("Show side by side");
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
		
		showStackedMenuItem = new JMenuItem("Show stacked");
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
		
		tileWindowsMenuItem = new JMenuItem("Tile");
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
		
		helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);
		
		helpContentsMenuItem = new JMenuItem("Help contents...");
		helpMenu.add(helpContentsMenuItem);
		
		helpMenu.add(new JSeparator());
		
		aboutVisiGraphMenuItem = new JMenuItem("About...");
		helpMenu.add(aboutVisiGraphMenuItem);
		
		setJMenuBar(menuBar);
		
		this.setVisible(true);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable transferable)
	{
		// Who cares?
	}
}






