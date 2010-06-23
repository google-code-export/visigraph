/**
 * CursorBundle.java
 */
package edu.belmont.mth.visigraph.resources;

import java.awt.*;
import java.net.*;
import java.util.*;

/**
 * @author Cameron Behar
 * 
 */
public class CursorBundle extends ResourceBundle
{
	private String							fileSuffix;
	private static HashMap<String, Cursor>	map	= new HashMap<String, Cursor>();
	
	public CursorBundle()
	{
		this("");
	}
	
	protected CursorBundle(String suffix)
	{
		fileSuffix = suffix;
	}
	
	@Override
	public Enumeration<String> getKeys()
	{
		return (new Vector<String>(map.keySet())).elements();
	}
	
	@Override
	protected final Object handleGetObject(String key)
	{
		return loadCursor(key, ".gif");
	}
	
	private Cursor loadCursor(String filename, String extension)
	{
		String imageName = filename + fileSuffix + extension;
		
		Cursor cursor = map.get(imageName);
		
		if (cursor != null)
			return cursor;
		
		URL url = getClass().getResource("cursors/" + imageName);
		
		if (url == null)
			return null;
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		cursor = toolkit.createCustomCursor(toolkit.getImage(url), new Point(1, 1), "");
		map.put(imageName, cursor);
		
		return cursor;
	}
}