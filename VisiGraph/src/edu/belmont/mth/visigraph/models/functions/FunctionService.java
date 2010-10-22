/**
 * FunctionService.java
 */
package edu.belmont.mth.visigraph.models.functions;

import java.io.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.utilities.DebugUtilities;
import bsh.Interpreter;

/**
 * The {@code FunctionService} class provides universal access to a Singleton list of functions compiled using BeanShell from the functions folder of
 * the application's local directory.
 * 
 * @author Cameron Behar
 */
public class FunctionService
{
	/**
	 * The Singleton instance of {@code FunctionService}
	 */
	public final static FunctionService instance = new FunctionService( );
	
	/**
	 * The {@code ObservableList} of available functions, compiled at runtime
	 */
	public final ObservableList<FunctionBase> functions;
	
	/**
	 * Constructs the Singleton instance of {@code FunctionService}, populating the list of functions with scripts compiled using BeanShell from the
	 * functions folder of the application's local directory
	 */
	private FunctionService ( )
	{
		functions = new ObservableList<FunctionBase>( );
		
		functions.add( new CountCrossings( ) );
		
		File folder = new File( "functions" );
		if ( folder.exists( ) )
			for ( String filename : folder.list( new FilenameFilter( ) { public boolean accept( File dir, String name ) { return name.endsWith( ".function" ) || name.endsWith( ".java" ); } } ) )
				try { functions.add( (FunctionBase) new Interpreter( ).source( "functions/" + filename ) );	}
				catch ( Exception ex ) { DebugUtilities.logException( String.format( "An exception occurred while compiling %s.", filename ), ex ); }
	}
}
