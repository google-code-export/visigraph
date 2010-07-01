/**
 * GraphWindow.java
 */
package edu.belmont.mth.visigraph.gui;

import java.io.File;

import javax.swing.*;
import javax.swing.event.*;
import edu.belmont.mth.visigraph.controllers.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;

/**
 * @author Cameron Behar
 * 
 */
@SuppressWarnings("serial")
public class GraphWindow extends JInternalFrame
{
	private GraphDisplayController gdc;
	private File file;
	
	public GraphWindow(Graph g)
	{
		super("", true, true, true, true);
		this.file = null;
		this.setSize(GlobalSettings.defaultGraphWindowSize);
		this.add(setGdc(new GraphDisplayController(g)));
		this.addInternalFrameListener(new InternalFrameListener() { public void internalFrameActivated(InternalFrameEvent arg0) { getGdc().zoomFit(); } public void internalFrameClosed(InternalFrameEvent e) { } public void internalFrameClosing(InternalFrameEvent e) { } public void internalFrameDeactivated(InternalFrameEvent e) { } public void internalFrameDeiconified(InternalFrameEvent e) { } public void internalFrameIconified(InternalFrameEvent e) { } public void internalFrameOpened(InternalFrameEvent e) { }});
		this.updateTitle();
		this.setVisible(true);
		this.requestFocus();
		this.toFront();
	}
	
	public void updateTitle()
	{
		this.setTitle(GlobalSettings.applicationName + " - " + getGdc().getGraph().name.get());
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
		this.gdc = gdc;
		return gdc;
	}


	public GraphDisplayController getGdc()
	{
		return gdc;
	}
}
