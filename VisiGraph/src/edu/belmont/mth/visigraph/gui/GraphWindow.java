/**
 * GraphWindow.java
 */
package edu.belmont.mth.visigraph.gui;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import edu.belmont.mth.visigraph.controllers.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.GlobalSettings;

/**
 * @author Cameron Behar
 * 
 */
@SuppressWarnings("serial")
public class GraphWindow extends JInternalFrame
{
	GraphDisplayController gdc;
	
	public GraphWindow(Graph g)
	{
		super("", true, true, true, true);
		this.setSize(GlobalSettings.defaultGraphWindowSize);
		this.add(gdc = new GraphDisplayController(g));
		this.addInternalFrameListener(new InternalFrameListener() { public void internalFrameActivated(InternalFrameEvent arg0) { gdc.zoomFit(); } public void internalFrameClosed(InternalFrameEvent e) { } public void internalFrameClosing(InternalFrameEvent e) { } public void internalFrameDeactivated(InternalFrameEvent e) { } public void internalFrameDeiconified(InternalFrameEvent e) { } public void internalFrameIconified(InternalFrameEvent e) { } public void internalFrameOpened(InternalFrameEvent e) { }});
		this.updateTitle();
		this.setVisible(true);
		this.requestFocus();
		this.toFront();
	}
	
	public void updateTitle()
	{
		this.setTitle(GlobalSettings.applicationName + " - " + gdc.getGraph().name.get());
	}
}
