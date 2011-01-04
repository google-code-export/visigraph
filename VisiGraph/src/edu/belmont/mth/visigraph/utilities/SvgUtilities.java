/**
 * SvgUtilities.java
 */
package edu.belmont.mth.visigraph.utilities;

import java.awt.*;

/**
 * @author Cameron Behar
 */
public class SvgUtilities
{
	public static String formatColor( Color c )
	{
		return "rgb(" + c.getRed( ) + "," + c.getGreen( ) + "," + c.getBlue( ) + ")";
	}
	
	public static String formatString( String s )
	{
		s = s.replace( "&", "&amp;" );
		s = s.replace( "<", "&lt;" );
		s = s.replace( ">", "&gt;" );
		s = s.replace( "\"", "&quot;" );
		return s;
	}
}
