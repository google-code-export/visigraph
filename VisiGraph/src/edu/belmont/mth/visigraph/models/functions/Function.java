/**
 * Function.java
 */
package edu.belmont.mth.visigraph.models.functions;

import java.awt.*;
import edu.belmont.mth.visigraph.models.*;

/**
 * The {@code Function} interface defines the minimal implementation requirements of graph-evaluating algorithms.
 * 
 * @author Cameron Behar
 */
public interface Function
{
	/**
	 * A code representing a known function attribute
	 */
	public enum Attribute
	{
		AUTHOR, VERSION, DESCRIPTION, INPUT, SIDE_EFFECTS, OUTPUT, CONSTRAINTS, ALLOWS_DYNAMIC_EVALUATION, ALLOWS_ONE_TIME_EVALUATION, RELATED_GENERATORS, RELATED_FUNCTIONS, TAGS
	}
	
	/**
	 * Evaluates the function on the specified graph and returns its result as a {@code String}
	 * 
	 * @param g2D the {@code Graphics2D} context onto which the graph has been rendered
	 * @param g the {@code Graph} on which to evaluate this {@code Function}
	 * @param owner a {@code Component} used to show more complex modal dialogs, if necessary
	 * @return a {@code String} representing this {@code Function}'s result
	 */
	public String evaluate( Graphics2D g2D, Graph g, Component owner );
	
	/**
	 * Returns the specified attribute of this {@code Function}. All implementations must return values for, at least:
	 * <p>
	 * <ul>
	 * <li>{@code ALLOWS_DYNAMIC_EVALUATION}</li>
	 * <li>{@code ALLOWS_ONE_TIME_EVALUATION}</li>
	 * </ul>
	 * 
	 * @param attribute the queried {@link Attribute}
	 * @return an {@code Object} representing the value for this {@code Function}'s specified attribute
	 */
	public Object getAttribute( Attribute attribute );
	
	/**
	 * Returns a {@code boolean} indicating whether or not this function can be evaluated on a graph with the specified combination of rules
	 * 
	 * @return {@code true} if this function can be applied to a graph with the specified rules, {@code false} otherwise
	 */
	public boolean isApplicable( boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed );
}
