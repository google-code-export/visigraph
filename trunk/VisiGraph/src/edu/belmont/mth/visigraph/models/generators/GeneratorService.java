/**
 * GeneratorService.java
 */
package edu.belmont.mth.visigraph.models.generators;

import java.io.*;
import bsh.Interpreter;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.models.generators.Generator.*;

/**
 * The {@code GeneratorService} class provides universal access to a singleton list of graph generators compiled using BeanShell from the "generators"
 * folder of the application's local directory.
 * 
 * @author Cameron Behar
 */
public class GeneratorService
{
	/**
	 * The singleton instance of {@code GeneratorService}
	 */
	public final static GeneratorService	instance	= new GeneratorService( );
	
	/**
	 * Validates a {@code Generator} to ensure {@link Generator#getAttribute(Attribute)} returns sufficient and logically consistent values.
	 * <p/>
	 * This method does not return a value, but instead throws an {@code Exception} if the specified {@code Generator} implements insufficient or
	 * inconsistent attributes. Because loops, multiple edges, and cycles are not mutually exclusive concepts, we must provide this sanity-check to
	 * ensure that no {@code Generator} allows inconsistency or ambiguity. The logic used to validate internal consistency follows:
	 * <p/>
	 * <ul>
	 * <li>If cycles are allowed ...
	 * <ul>
	 * <li>... by force OR by default, ...
	 * <ul>
	 * <li>... loops may be allowed OR disallowed, either by force OR by default, AND
	 * <li>... multiple edges may be allowed OR disallowed, either by force OR by default.
	 * </ul>
	 * </ul>
	 * <li>If cycles are disallowed ...
	 * <ul>
	 * <li>... by force, ...
	 * <ul>
	 * <li>... loops must also be disallowed by force, AND
	 * <li>... multiple edges must also be disallowed by force.
	 * </ul>
	 * </ul>
	 * <ul>
	 * <li>... by default, ...
	 * <ul>
	 * <li>... loops must also be disallowed, either by force OR by default, AND
	 * <li>... multiple edges must also be disallowed, either by force OR by default.
	 * </ul>
	 * </ul>
	 * </ul>
	 * 
	 * @param generator The {@code Generator} to be validated
	 * @throws Exception If the specified {@code Generator} implements insufficient or inconsistent attributes.
	 */
	private static void validateGenerator( Generator generator ) throws Exception
	{
		// Check minimum requirements
		if( !( generator.getAttribute( Attribute.ARE_LOOPS_ALLOWED ) instanceof BooleanRule ) )
			throw new Exception( "The \"" + generator + "\" generator's attributes are insufficient: getAttribute( ) must return a BooleanRule for ARE_LOOPS_ALLOWED." );
		
		if( !( generator.getAttribute( Attribute.ARE_MULTIPLE_EDGES_ALLOWED ) instanceof BooleanRule ) )
			throw new Exception( "The \"" + generator + "\" generator's attributes are insufficient: getAttribute( ) must return a BooleanRule for ARE_MULTIPLE_EDGES_ALLOWED." );
		
		if( !( generator.getAttribute( Attribute.ARE_DIRECTED_EDGES_ALLOWED ) instanceof BooleanRule ) )
			throw new Exception( "The \"" + generator + "\" generator's attributes are insufficient: getAttribute( ) must return a BooleanRule for ARE_DIRECTED_EDGES_ALLOWED." );
		
		if( !( generator.getAttribute( Attribute.ARE_CYCLES_ALLOWED ) instanceof BooleanRule ) )
			throw new Exception( "The \"" + generator + "\" generator's attributes are insufficient: getAttribute( ) must return a BooleanRule for ARE_CYCLES_ALLOWED." );
		
		if( !( generator.getAttribute( Attribute.ARE_PARAMETERS_ALLOWED ) instanceof Boolean ) )
			throw new Exception( "The \"" + generator + "\" generator's attributes are insufficient: getAttribute( ) must return a BooleanRule for ARE_PARAMETERS_ALLOWED." );
		
		if( (Boolean) generator.getAttribute( Attribute.ARE_PARAMETERS_ALLOWED ) )
		{
			if( !( generator.getAttribute( Attribute.PARAMETERS_DESCRIPTION ) instanceof String ) )
				throw new Exception( "The \"" + generator + "\" generator's attributes are insufficient: if parameters are allowed, getAttribute( ) must also return a String for PARAMETERS_DESCRIPTION." );
			
			if( !( generator.getAttribute( Attribute.PARAMETERS_VALIDATION_EXPRESSION ) instanceof String ) )
				throw new Exception( "The \"" + generator + "\" generator's attributes are insufficient: if parameters are allowed, getAttribute( ) must also return a String for PARAMETERS_VALIDATION_EXPRESSION." );
		}
		
		// Check logical consistency
		if( !( (BooleanRule) generator.getAttribute( Attribute.ARE_CYCLES_ALLOWED ) ).isTrue( ) )
			if( ( (BooleanRule) generator.getAttribute( Attribute.ARE_CYCLES_ALLOWED ) ).isForced( ) )
			{
				if( generator.getAttribute( Attribute.ARE_LOOPS_ALLOWED ) != BooleanRule.FORCED_FALSE )
					throw new Exception( "The \"" + generator + "\" generator's attributes are inconsistent: if cycles are disallowed by force, loops must also be disallowed by force." );
				if( generator.getAttribute( Attribute.ARE_MULTIPLE_EDGES_ALLOWED ) != BooleanRule.FORCED_FALSE )
					throw new Exception( "The \"" + generator + "\" generator's attributes are inconsistent: if cycles are disallowed by force, multiple edges must also be disallowed by force." );
			}
			else
			{
				if( ( (BooleanRule) generator.getAttribute( Attribute.ARE_LOOPS_ALLOWED ) ).isTrue( ) )
					throw new Exception( "The \"" + generator + "\" generator's attributes are inconsistent: if cycles are disallowed by default, loops must also be disallowed, either by force or by default." );
				if( ( (BooleanRule) generator.getAttribute( Attribute.ARE_MULTIPLE_EDGES_ALLOWED ) ).isTrue( ) )
					throw new Exception( "The \"" + generator + "\" generator's attributes are inconsistent: if cycles are disallowed by default, multiple edges must also be disallowed, either by force or by default." );
			}
		
		// Check type consistency
		if( generator.getAttribute( Attribute.AUTHOR ) != null && !( generator.getAttribute( Attribute.AUTHOR ) instanceof String ) )
			throw new Exception( "The \"" + generator + "\" generator attributes are inconsistently typed: getAttribute( ) must return a String or null for AUTHOR." );
		
		if( generator.getAttribute( Attribute.VERSION ) != null && !( generator.getAttribute( Attribute.VERSION ) instanceof String ) )
			throw new Exception( "The \"" + generator + "\" generator attributes are inconsistently typed: getAttribute( ) must return a String or null for VERSION." );
		
		if( generator.getAttribute( Attribute.ISOMORPHISMS ) != null && !( generator.getAttribute( Attribute.ISOMORPHISMS ) instanceof String[ ] ) )
			throw new Exception( "The \"" + generator + "\" generator attributes are inconsistently typed: getAttribute( ) must return an array of Strings or null for ISOMORPHISMS." );
		
		if( generator.getAttribute( Attribute.DESCRIPTION ) != null && !( generator.getAttribute( Attribute.DESCRIPTION ) instanceof String ) )
			throw new Exception( "The \"" + generator + "\" generator attributes are inconsistently typed: getAttribute( ) must return a String or null for DESCRIPTION." );
		
		if( generator.getAttribute( Attribute.CONSTRAINTS ) != null && !( generator.getAttribute( Attribute.CONSTRAINTS ) instanceof String[ ] ) )
			throw new Exception( "The \"" + generator + "\" generator attributes are inconsistently typed: getAttribute( ) must return an array of Strings or null for CONSTRAINTS." );
		
		if( generator.getAttribute( Attribute.RELATED_GENERATORS ) != null && !( generator.getAttribute( Attribute.RELATED_GENERATORS ) instanceof String[ ] ) )
			throw new Exception( "The \"" + generator + "\" generator attributes are inconsistently typed: getAttribute( ) must return an array of Strings or null for RELATED_GENERATORS." );
		
		if( generator.getAttribute( Attribute.RELATED_FUNCTIONS ) != null && !( generator.getAttribute( Attribute.RELATED_FUNCTIONS ) instanceof String[ ] ) )
			throw new Exception( "The \"" + generator + "\" generator attributes are inconsistently typed: getAttribute( ) must return an array of Strings or null for RELATED_FUNCTIONS." );
		
		if( generator.getAttribute( Attribute.TAGS ) != null && !( generator.getAttribute( Attribute.TAGS ) instanceof String[ ] ) )
			throw new Exception( "The \"" + generator + "\" generator attributes are inconsistently typed: getAttribute( ) must return an array of Strings or null for TAGS." );
	}
	
	/**
	 * The {@code ObservableList} of available generators, compiled at runtime
	 */
	public final ObservableList<Generator>	generators;
	
	/**
	 * Constructs the singleton instance of {@code GeneratorService}, populating the list of graph generators with scripts compiled using BeanShell
	 * from the "generators" folder of the application's local directory
	 */
	private GeneratorService( )
	{
		this.generators = new ObservableList<Generator>( );
		
		// Load standard library generators
		this.generators.add( new EmptyGraph( ) );
		
		// Load external scripted generators
		File folder = new File( "generators" );
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
					Generator generator = (Generator) new Interpreter( ).source( "generators/" + filename );
					validateGenerator( generator );
					this.generators.add( generator );
				}
				catch( Throwable ex )
				{
					DebugUtilities.logException( String.format( "An exception occurred while compiling %s.", filename ), ex );
				}
	}
}
