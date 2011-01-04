/**
 * FunctionService.java
 */
package edu.belmont.mth.visigraph.models.functions;

import java.io.*;
import java.util.*;
import bsh.Interpreter;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.models.functions.Function.*;

/**
 * The {@code FunctionService} class provides universal access to a singleton list of functions compiled using BeanShell from the "functions" folder
 * of the application's local directory.
 * 
 * @author Cameron Behar
 */
public class FunctionService
{
	/**
	 * The singleton instance of {@code FunctionService}
	 */
	public final static FunctionService	instance	= new FunctionService( );
	
	/**
	 * Validates a {@code Function} to ensure {@link Function#getAttribute(Attribute)} returns sufficient and logically consistent values.
	 * <p/>
	 * This method does not return a value, but instead throws an {@code Exception} if the specified {@code Function} implements insufficient or
	 * inconsistent attributes.
	 * 
	 * @param function The {@code Function} to be validated
	 * @throws Exception If the specified {@code Function} implements insufficient or inconsistent attributes.
	 */
	private static void validateFunction( Function function ) throws Exception
	{
		// Check minimum requirements
		if( !( function.getAttribute( Attribute.ALLOWS_ONE_TIME_EVALUATION ) instanceof Boolean ) )
			throw new Exception( "The \"" + function + "\" function's attributes are insufficient: getAttribute( ) must return a Boolean for ALLOWS_ONE_TIME_EVALUATION." );
		
		if( !( function.getAttribute( Attribute.ALLOWS_DYNAMIC_EVALUATION ) instanceof Boolean ) )
			throw new Exception( "The \"" + function + "\" function's attributes are insufficient: getAttribute( ) must return a Boolean for ALLOWS_DYNAMIC_EVALUATION." );
		
		// Check logical consistency
		if( !(Boolean) function.getAttribute( Attribute.ALLOWS_ONE_TIME_EVALUATION ) && !(Boolean) function.getAttribute( Attribute.ALLOWS_DYNAMIC_EVALUATION ) )
			throw new Exception( "The \"" + function + "\" function's attributes are inconsistent: a function must allow one-time evaluation, dynamic evaluation, or both." );
		
		// Check type consistency
		if( function.getAttribute( Attribute.AUTHOR ) != null && !( function.getAttribute( Attribute.AUTHOR ) instanceof String ) )
			throw new Exception( "The \"" + function + "\" function's attributes are inconsistently typed: getAttribute( ) must return a String or null for AUTHOR." );
		
		if( function.getAttribute( Attribute.VERSION ) != null && !( function.getAttribute( Attribute.VERSION ) instanceof String ) )
			throw new Exception( "The \"" + function + "\" function's attributes are inconsistently typed: getAttribute( ) must return a String or null for VERSION." );
		
		if( function.getAttribute( Attribute.DESCRIPTION ) != null && !( function.getAttribute( Attribute.DESCRIPTION ) instanceof String ) )
			throw new Exception( "The \"" + function + "\" function's attributes are inconsistently typed: getAttribute( ) must return a String or null for DESCRIPTION." );
		
		if( function.getAttribute( Attribute.INPUT ) != null && !( function.getAttribute( Attribute.INPUT ) instanceof String ) )
			throw new Exception( "The \"" + function + "\" function's attributes are inconsistently typed: getAttribute( ) must return a String or null for INPUT." );
		
		if( function.getAttribute( Attribute.SIDE_EFFECTS ) != null && !( function.getAttribute( Attribute.SIDE_EFFECTS ) instanceof String ) )
			throw new Exception( "The \"" + function + "\" function's attributes are inconsistently typed: getAttribute( ) must return a String or null for SIDE_EFFECTS." );
		
		if( function.getAttribute( Attribute.OUTPUT ) != null && !( function.getAttribute( Attribute.OUTPUT ) instanceof String ) )
			throw new Exception( "The \"" + function + "\" function's attributes are inconsistently typed: getAttribute( ) must return a String or null for OUTPUT." );
		
		if( function.getAttribute( Attribute.CONSTRAINTS ) != null && !( function.getAttribute( Attribute.CONSTRAINTS ) instanceof String[ ] ) )
			throw new Exception( "The \"" + function + "\" function's attributes are inconsistently typed: getAttribute( ) must return an array of Strings or null for CONSTRAINTS." );
		
		if( function.getAttribute( Attribute.RELATED_GENERATORS ) != null && !( function.getAttribute( Attribute.RELATED_GENERATORS ) instanceof String[ ] ) )
			throw new Exception( "The \"" + function + "\" function's attributes are inconsistently typed: getAttribute( ) must return an array of Strings or null for RELATED_GENERATORS." );
		
		if( function.getAttribute( Attribute.RELATED_FUNCTIONS ) != null && !( function.getAttribute( Attribute.RELATED_FUNCTIONS ) instanceof String[ ] ) )
			throw new Exception( "The \"" + function + "\" function's attributes are inconsistently typed: getAttribute( ) must return an array of Strings or null for RELATED_FUNCTIONS." );
		
		if( function.getAttribute( Attribute.TAGS ) != null && !( function.getAttribute( Attribute.TAGS ) instanceof String[ ] ) )
			throw new Exception( "The \"" + function + "\" function's attributes are inconsistently typed: getAttribute( ) must return an array of Strings or null for TAGS." );
	}
	
	/**
	 * The {@code ObservableList} of available functions, compiled at runtime
	 */
	public final ObservableList<Function>	functions;
	
	/**
	 * Constructs the singleton instance of {@code FunctionService}, populating the list of functions with scripts compiled using BeanShell from the
	 * "functions" folder of the application's local directory
	 */
	private FunctionService( )
	{
		this.functions = new ObservableList<Function>( );
		
		// Load standard library functions
		this.functions.add( new CountCrossings( ) );
		
		// Load external scripted functions
		File folder = new File( "functions" );
		if( folder.exists( ) )
			for( String filename : folder.list( new FilenameFilter( )
			{
				public boolean accept( File dir, String name )
				{
					return name.endsWith( ".java" );
				}
			} ) )
				try
				{
					Function function = (Function) new Interpreter( ).source( "functions/" + filename );
					validateFunction( function );
					this.functions.add( function );
				}
				catch( Throwable ex )
				{
					DebugUtilities.logException( String.format( "An exception occurred while compiling %s.", filename ), ex );
				}
		
		// Sort functions lexicographically by name
		Collections.sort( this.functions, new Comparator<Function>( )
		{
			@Override
			public int compare( Function function0, Function function1 )
			{
				return function0.toString( ).compareTo( function1.toString( ) );
			}
		} );
	}
}
