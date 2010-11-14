/**
 * FunctionBase.java
 */
package edu.belmont.mth.visigraph.models.functions;

import java.awt.*;
import edu.belmont.mth.visigraph.models.*;

/**
 * The {@code FunctionBase} interface defines the minimal implementation requirements of graph-evaluating functions.
 * 
 * @author Cameron Behar
 */
public interface FunctionBase
{	
	/**
	 * Evaluates the function on the specified graph and returns its result as a {@code String}
	 * 
	 * @param g2D the {@code Graphics2D} context onto which the graph has been rendered
	 * @param g the {@code Graph} on which to evaluate this {@code Function} 
	 * @param owner a {@code Component} used to show modal dialogs requesting more parameters from the user, if necessary
	 * 
	 * @return a {@code String} representing this {@code Function}'s result
	 */
	public String evaluate(Graphics2D g2D, Graph g, Component owner);
	
	/**
	 * Returns a {@code boolean} indicating whether or not this function may be evaluated dynamically. For subclasses that modify the graph in any
	 * way, or otherwise perform computationally expensive operations, it is advised that this method always return {@code false}.
	 * 
	 * @return {@code true} if dynamic evaluation is allowed, {@code false} otherwise
	 */
	public boolean allowsDynamicEvaluation();
	
	/**
	 * Returns a {@code boolean} indicating whether or not this function may be evaluated statically. For subclasses designed to evaluate graphs
	 * progressively over time it is advised that this method always return {@code true}.
	 * 
	 * @return {@code true} if one-time evaluation is allowed, {@code false} otherwise
	 */
	public boolean allowsOneTimeEvaluation();
}
