/**
 * Generator.java
 */
package edu.belmont.mth.visigraph.models.generators;

import java.awt.*;
import edu.belmont.mth.visigraph.models.*;

/**
 * The {@code Generator} interface defines the minimal implementation requirements of graph-generating algorithms.
 * 
 * @author Cameron Behar
 */
public interface Generator
{
	/**
	 * A code representing a known generator attribute
	 */
	public enum Attribute
	{
		AUTHOR, VERSION, ISOMORPHISMS, DESCRIPTION, PARAMETERS_DESCRIPTION, PARAMETERS_VALIDATION_EXPRESSION, CONSTRAINTS, ARE_LOOPS_ALLOWED, ARE_MULTIPLE_EDGES_ALLOWED, ARE_DIRECTED_EDGES_ALLOWED, ARE_CYCLES_ALLOWED, ARE_PARAMETERS_ALLOWED, RELATED_GENERATORS, RELATED_FUNCTIONS, TAGS
	}
	
	/**
	 * A boolean rule indicating whether an action should be allowed or prohibited, and whether or not this rule can be overridden
	 */
	public enum BooleanRule
	{
		FORCED_TRUE, DEFAULT_TRUE, FORCED_FALSE, DEFAULT_FALSE;
		
		public boolean isForced( )
		{
			return this == FORCED_TRUE || this == FORCED_FALSE;
		}
		
		public boolean isTrue( )
		{
			return this == FORCED_TRUE || this == DEFAULT_TRUE;
		}
	}
	
	/**
	 * Generates a graph from the specified parameters and rules.
	 * 
	 * @param params a {@code String} of parameters for the generation algorithm
	 * @param areLoopsAllowed a {@code boolean} indicating whether or not loops are to be allowed in the graph
	 * @param areDirectedEdgesAllowed a {@code boolean} indicating whether the graph is to be directed or undirected
	 * @param areMultipleEdgesAllowed a {@code boolean} indicating whether or not multi-edges are to be allowed in the graph
	 * @param areCyclesAllowed a {@code boolean} indicating whether or not cycles are to be allowed in the graph
	 * @param owner a {@code Component} used to show modal dialogs requesting more parameters from the user, if necessary
	 * @return the newly generated {@code Graph} object
	 */
	public Graph generate( String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed, Component owner );
	
	/**
	 * Returns the specified attribute of this {@code Generator}. All implementations must return values for, at least:
	 * <p>
	 * <ul>
	 * <li>{@code ARE_LOOPS_ALLOWED}</li>
	 * <li>{@code ARE_MULTIPLE_EDGES_ALLOWED}</li>
	 * <li>{@code ARE_DIRECTED_EDGES_ALLOWED}</li>
	 * <li>{@code ARE_CYCLES_ALLOWED}</li>
	 * <li>{@code ARE_PARAMETERS_ALLOWED}</li>
	 * </ul>
	 * <p>
	 * ... and if the {@code ARE_PARAMETERS_ALLOWED} attribute is set to {@code true}:
	 * <ul>
	 * <li>{@code PARAMETERS_DESCRIPTION}</li>
	 * <li>{@code PARAMETERS_VALIDATION_EXPRESSION}</li>
	 * </ul>
	 * 
	 * @param attribute the queried {@link Attribute}
	 * @return an {@code Object} representing the value for this {@code Generator}'s specified attribute
	 */
	public Object getAttribute( Attribute attribute );
}
