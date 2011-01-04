/**
 * StringBundle.java
 */
package edu.belmont.mth.visigraph.resources;

import java.util.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;

/**
 * @author Cameron Behar
 */
public class StringBundle
{
	private static final ResourceBundle	instance;
	
	static
	{
		String[ ] localeParts = UserSettings.instance.language.get( ).split( "_" );
		switch( localeParts.length )
		{
			case 1:
				instance = ResourceBundle.getBundle( "edu.belmont.mth.visigraph.resources.strings.Resources", new Locale( localeParts[0] ) );
				break;
			case 2:
				instance = ResourceBundle.getBundle( "edu.belmont.mth.visigraph.resources.strings.Resources", new Locale( localeParts[0], localeParts[1] ) );
				break;
			case 3:
				instance = ResourceBundle.getBundle( "edu.belmont.mth.visigraph.resources.strings.Resources", new Locale( localeParts[0], localeParts[1], localeParts[2] ) );
				break;
			default:
				instance = ResourceBundle.getBundle( "edu.belmont.mth.visigraph.resources.strings.Resources" );
				break;
		}
	}
	
	public static String get( String key )
	{
		try
		{
			return instance.getString( key );
		}
		catch( MissingResourceException ex )
		{
			DebugUtilities.logException( String.format( "An exception occurred while trying to load resource %s.", key ), ex );
			return '!' + key + '!';
		}
	}
	
	private StringBundle( )
	{}
}
