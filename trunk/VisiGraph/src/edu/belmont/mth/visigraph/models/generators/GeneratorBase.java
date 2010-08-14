/**
 * GeneratorBase.java
 */
package edu.belmont.mth.visigraph.models.generators;

import edu.belmont.mth.visigraph.models.*;

/**
 * The {@code GeneratorBase} interface defines the minimal implementation requirements of graph-generating algorithms.
 * 
 * @author Cameron Behar
 */
public interface GeneratorBase
{
	/**
	 * Returns whether or not loops should be allowed in the graph and whether or not this rule should be subject to change
	 * 
	 * @return a {@link BooleanRule} indicating both the rule and its enforcement policy
	 */
	public BooleanRule areLoopsAllowed( );
	
	/**
	 * Returns whether or not multi-edges should be allowed in the graph and whether or not this rule should be subject to change
	 * 
	 * @return a {@link BooleanRule} indicating both the rule and its enforcement policy
	 */
	public BooleanRule areMultipleEdgesAllowed( );
	
	/**
	 * Returns whether or not the graph should be directed and whether or not this rule should be subject to change
	 * 
	 * @return a {@link BooleanRule} indicating both the rule and its enforcement policy
	 */
	public BooleanRule areDirectedEdgesAllowed( );
	
	/**
	 * Returns whether or not cycles should be allowed in the graph and whether or not this rule should be subject to change
	 * 
	 * @return a {@link BooleanRule} indicating both the rule and its enforcement policy
	 */
	public BooleanRule areCyclesAllowed( );
	
	/**
	 * Returns whether or not parameters should be allowed and whether or not this rule should be subject to change
	 * 
	 * @return a {@link BooleanRule} indicating both the rule and its enforcement policy
	 */
	public BooleanRule areParametersAllowed( );
	
	/**
	 * Returns a simple description of the input format for parameters
	 * 
	 * @return a {@code String} describing the input format for parameters
	 */
	public String getParametersDescription( );
	
	/**
	 * Returns a regular expression used to validate parameter input formatting
	 * 
	 * @return the validating regular expression as a {@code String}
	 */
	public String getParametersValidatingExpression( );
	
	/**
	 * Generates a graph from the specified parameters and rules.
	 * 
	 * @param params a {@code String} of parameters for the generation algorithm
	 * @param areLoopsAllowed a {@code boolean} indicating whether or not loops are to be allowed in the graph
	 * @param areDirectedEdgesAllowed a {@code boolean} indicating whether the graph is to be directed or undirected
	 * @param areMultipleEdgesAllowed a {@code boolean} indicating whether or not multi-edges are to be allowed in the graph
	 * @param areCyclesAllowed a {@code boolean} indicating whether or not cycles are to be allowed in the graph
	 * 
	 * @return the newly generated {@code Graph} object
	 */
	public Graph generate( String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed );
	
	/**
	 * A boolean rule indicating whether an action should be allowed or prohibited, and whether or not this rule can be overridden
	 */
	public enum BooleanRule
	{
		ForcedTrue, DefaultTrue, ForcedFalse, DefaultFalse;
		
		public boolean isTrue( )
		{
			return this == ForcedTrue || this == DefaultTrue;
		}
		
		public boolean isForced( )
		{
			return this == ForcedTrue || this == ForcedFalse;
		}
	}
}
