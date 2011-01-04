/**
 * DebugUtilities.java
 */
package edu.belmont.mth.visigraph.utilities;

import java.util.*;

/**
 * @author Cameron Behar
 */
public class DebugUtilities
{
	private static final StringBuffer	buffer	= new StringBuffer( );
	
	public static String getLog( )
	{
		return String.format( "<html><body><font size=\"2\" face=\"Consolas, Andale Mono, Droid Sans Mono, Lucida Console, Monaco, Courier New, Courier\">%1$s</font></body></html>", buffer.toString( ) );
	}
	
	public static void logException( String message, Throwable exception )
	{
		buffer.append( "<b><font color=\"red\">" );
		buffer.append( String.format( "{Exception %1$tT} %2$s<br/>", Calendar.getInstance( ), message.replaceAll( "<", "&lt;" ).replaceAll( ">", "&gt;" ) ) );
		buffer.append( String.format( "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;%1$s<br/>", exception.toString( ).replaceAll( "<", "&lt;" ).replaceAll( ">", "&gt;" ) ) );
		buffer.append( "</font></b>" );
	}
	
	public static void logWarning( String message )
	{
		buffer.append( String.format( "{Warning %1$tT} %2$s<br/>", Calendar.getInstance( ), message.replaceAll( "<", "&lt;" ).replaceAll( ">", "&gt;" ) ) );
	}
	
	private DebugUtilities( )
	{}
}
