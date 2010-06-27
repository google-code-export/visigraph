/**
 * CaptionDisplayView.java
 */
package edu.belmont.mth.visigraph.views;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;

/**
 * @author Cameron Behar
 *
 */
public class CaptionDisplayView
{	
	public static void paint(Graphics2D g2D, Palette p, GraphDisplaySettings s, Caption c)
	{
		// Load the default image bundle used for the handle and editor images
		ResourceBundle images = ResourceBundle.getBundle("edu.belmont.mth.visigraph.resources.ImageBundle");
		
		// Draw each line in the text
		g2D.setColor(c.isSelected.get() ? p.selectedCaptionText.get() : p.captionText.get());
		String[] lines = c.text.get().split("\\n");
		for(int i = 0; i < lines.length; ++i)
			g2D.drawString(lines[i], c.x.get().floatValue(), c.y.get().floatValue() + i * 12);
		
		// Draw the handle
		if(s.showCaptionHandles.get())
		{
			Rectangle2D.Double handle = getHandleRectangle(c);
			g2D.drawImage(c.isSelected.get() ? (Image)images.getObject("caption_handle_selected") : (Image)images.getObject("caption_handle_unselected"), (int)handle.x, (int)handle.y, 13, 13, null);
		}
		
		// Draw the edit button
		if(s.showCaptionEditors.get() && c.isSelected.get())
		{
			Rectangle2D.Double editor = getEditorRectangle(c);
			g2D.drawImage((Image)images.getObject("caption_edit_selected"), (int)editor.x, (int)editor.y, 13, 13, null);
		}
	}
	
	public static boolean wasHandleClicked(Caption c, Point p, double scale)
	{
		Rectangle2D.Double handle = getHandleRectangle(c);
		handle.x -= GlobalSettings.defaultCaptionHandleClickMargin / scale;
		handle.y -= GlobalSettings.defaultCaptionHandleClickMargin / scale;
		handle.width  += 2.0 * GlobalSettings.defaultCaptionHandleClickMargin / scale;
		handle.height += 2.0 * GlobalSettings.defaultCaptionHandleClickMargin / scale;
		
		return handle.contains(p);
	}
	
	public static boolean wasHandleSelected(Caption c, Rectangle r)
	{
		return getHandleRectangle(c).intersects(r);
	}
	
	public static boolean wasEditorClicked(Caption c, Point p, double scale)
	{
		Rectangle2D.Double editor = getEditorRectangle(c);
		editor.x -= GlobalSettings.defaultCaptionEditorClickMargin / scale;
		editor.y -= GlobalSettings.defaultCaptionEditorClickMargin / scale;
		editor.width  += 2.0 * GlobalSettings.defaultCaptionEditorClickMargin / scale;
		editor.height += 2.0 * GlobalSettings.defaultCaptionEditorClickMargin / scale;
		
		return editor.contains(p);
	}
	
	public static Rectangle2D.Double getHandleRectangle(Caption c)
	{
		return new Rectangle2D.Double(c.x.get() - 16.0, c.y.get() - 11.0, 13.0, 13.0);
	}
	
	public static Rectangle2D.Double getEditorRectangle(Caption c)
	{
		return new Rectangle2D.Double(c.x.get() - 16.0, c.y.get() + 4.0, 13.0, 13.0);
	}
}
