/**
 * RegexUtilities.java
 */
package edu.belmont.mth.visigraph.utilities;

/**
 * @author Cameron Behar
 * 
 */
public class RegexUtilities
{
	public static boolean isNumeric( String s )
	{
		return s.matches( "-?(?:\\d+\\.?\\d*|\\d*\\.?\\d+)(?:[eE][+\\-\\x20]?\\d+)?" );
	}
	
	public static boolean isInteger( String s )
	{
		return s.matches( "-?\\d+" );
	}
	
	public static boolean isPositiveInteger( String s )
	{
		return s.matches( "\\d+" );
	}
}
