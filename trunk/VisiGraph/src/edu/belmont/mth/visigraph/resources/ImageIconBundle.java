/**
 * ImageBundle.java
 */
package edu.belmont.mth.visigraph.resources;

import java.net.*;
import java.util.*;
import javax.swing.ImageIcon;

/**
 * @author Cameron Behar
 * 
 */
public class ImageIconBundle extends ResourceBundle
{
	private String								fileSuffix;
	private static HashMap<String, ImageIcon>	map	= new HashMap<String, ImageIcon>();
	
	public ImageIconBundle()
	{
		this("");
	}
	
	private ImageIconBundle(String suffix)
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
		return loadImageIcon(key, ".png");
	}
	
	private ImageIcon loadImageIcon(String filename, String extension)
	{
		String imageName = filename + fileSuffix + extension;
		
		ImageIcon icon = map.get(imageName);
		
		if (icon != null)
			return icon;
		
		URL url = getClass().getResource("images/" + imageName);
		
		if (url == null)
			return null;
		
		icon = new ImageIcon(url);
		map.put(imageName, icon);
		
		return icon;
	}
}