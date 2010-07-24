/**
 * StringBundle.java
 */
package edu.belmont.mth.visigraph.resources;

import java.util.*;

/**
 * @author Cameron Behar
 *
 */
public class StringBundle
{																										
	private static final ResourceBundle	instance = ResourceBundle.getBundle("edu.belmont.mth.visigraph.resources.strings.Resources");
	
	private StringBundle()
	{}
	
	public static String get(String key)
	{
		try { return instance.getString(key); }
		catch (MissingResourceException e) { return '!' + key + '!'; }
	}
}

