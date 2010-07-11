/**
 * GraphWindow.java
 */
package edu.belmont.mth.visigraph.gui.windows;

import java.io.*;
import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.belmont.mth.visigraph.controllers.*;
import edu.belmont.mth.visigraph.controllers.GraphDisplayController.GraphChangeEvent;
import edu.belmont.mth.visigraph.controllers.GraphDisplayController.GraphChangeEventListener;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.views.svg.GraphSvgView;

/**
 * @author Cameron Behar
 * 
 */
@SuppressWarnings("serial")
public class GraphWindow extends JInternalFrame implements GraphChangeEventListener
{
	private JFileChooser			fileChooser;
	private GraphDisplayController	gdc;
	private boolean					hasChanged;
	private File					file;
	private UserSettings			userSettings	= UserSettings.instance;
	
	public GraphWindow(Graph g)
	{
		super("", true, true, true, true);
		this.file = null;
		this.setSize(new Dimension(userSettings.graphWindowWidth.get(), userSettings.graphWindowHeight.get()));
		this.add(setGdc(new GraphDisplayController(g)));
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addInternalFrameListener(new InternalFrameListener()
		{
			public void internalFrameActivated(InternalFrameEvent arg0)
			{
				getGdc().zoomFit();
			}
			
			public void internalFrameClosed(InternalFrameEvent e)
			{}
			
			public void internalFrameClosing(InternalFrameEvent e)
			{
				closingWindow(e);
			}
			
			public void internalFrameDeactivated(InternalFrameEvent e)
			{}
			
			public void internalFrameDeiconified(InternalFrameEvent e)
			{}
			
			public void internalFrameIconified(InternalFrameEvent e)
			{}
			
			public void internalFrameOpened(InternalFrameEvent e)
			{}
		});
		this.hasChanged = true;
		this.updateTitle();
		this.fileChooser = new JFileChooser();
		this.setVisible(true);
		this.requestFocus();
		this.toFront();
	}
	
	public void updateTitle()
	{
		this.setTitle(GlobalSettings.applicationName + " - " + getGdc().getGraph().name.get() + (hasChanged ? "*" : ""));
	}
	
	public void graphChangeEventOccurred(GraphChangeEvent evt)
	{
		setHasChanged(true);
	}
	
	public File getFile()
	{
		return this.file;
	}
	
	public void setFile(File file)
	{
		this.file = file;
	}
	
	public GraphDisplayController setGdc(GraphDisplayController gdc)
	{
		if (gdc != null)
			gdc.removeGraphChangeListener(this);
		
		this.gdc = gdc;
		this.gdc.addGraphChangeListener(this);
		
		return gdc;
	}
	
	public GraphDisplayController getGdc()
	{
		return gdc;
	}
	
	public boolean getHasChanged()
	{
		return hasChanged;
	}
	
	public void setHasChanged(boolean hasChanged)
	{
		if (this.hasChanged != hasChanged)
		{
			this.hasChanged = hasChanged;
			updateTitle();
		}
	}
	
	public void closingWindow(InternalFrameEvent e)
	{
		if (hasChanged)
		{
			int result = JOptionPane.showInternalConfirmDialog(this, "Do you want to save changes to \"" + gdc.getGraph().name.get() + "\"?", GlobalSettings.applicationName, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			switch (result)
			{
				case JOptionPane.YES_OPTION:
					try
					{
						save();
						dispose();
					}
					catch (IOException ex)
					{
						JOptionPane.showInternalMessageDialog(this, "An exception occurred while trying to save \"" + gdc.getGraph().name.get() + "\"!");
					}
					break;
				case JOptionPane.NO_OPTION:
					dispose();
					break;
				case JOptionPane.CANCEL_OPTION:
					break;
			}
		}
		else
			dispose();
	}
	
	public void saveAs()
	{
		fileChooser.resetChoosableFileFilters();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Portable Network Graphics File", "png"));
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Scalable Vector Graphics File", "svg"));
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("VisiGraph Graph File", "vsg"));
		fileChooser.setMultiSelectionEnabled(false);
		
		boolean success = false;
		
		while (!success)
		{
			if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
			{
				try
				{
					File selectedFile = fileChooser.getSelectedFile();
					
					if (fileChooser.getFileFilter().getDescription().equals("VisiGraph Graph File") && !selectedFile.getName().endsWith(".vsg"))
						selectedFile = new File(selectedFile.getAbsolutePath() + ".vsg");
					else if (fileChooser.getFileFilter().getDescription().equals("Scalable Vector Graphics File") && !selectedFile.getName().endsWith(".svg"))
						selectedFile = new File(selectedFile.getAbsolutePath() + ".svg");
					else if (fileChooser.getFileFilter().getDescription().equals("Portable Network Graphics File") && !selectedFile.getName().endsWith(".png"))
						selectedFile = new File(selectedFile.getAbsolutePath() + ".png");
					
					saveFile(selectedFile);
					
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
	
	public void save() throws IOException
	{
		if (file == null)
			saveAs();
		else
			saveFile(file);
	}
	
	public void saveFile(File file) throws IOException
	{
		Graph graph = gdc.getGraph();
		GraphSettings settings = gdc.getSettings();
		
		if (file.getName().endsWith(".vsg"))
		{
			graph.name.set(file.getName().substring(0, file.getName().length() - 4));
			updateTitle();
			
			FileWriter fw = new FileWriter(file);
			fw.write(graph.toString());
			fw.close();
			
			setFile(file);
			setHasChanged(false);
		}
		else if (file.getName().endsWith(".svg"))
		{
			FileWriter fw = new FileWriter(file);
			fw.write(GraphSvgView.format(graph, settings));
			fw.close();
		}
		else if (file.getName().endsWith(".png"))
		{
			ImageIO.write(gdc.getImage(), "png", file);
		}
	}
}
