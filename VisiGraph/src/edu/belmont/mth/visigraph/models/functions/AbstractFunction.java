/**
 * AbstractFunction.java
 */
package edu.belmont.mth.visigraph.models.functions;

import java.awt.*;

import edu.belmont.mth.visigraph.models.*;

/**
 * @author Cameron Behar
 *
 */
public abstract class AbstractFunction implements Comparable<AbstractFunction>
{	
	public abstract Object evaluate(Graphics2D g2D, Palette p, Graph g);
	
	public abstract String getDescription();
	
	public String toString()
	{
		return getDescription();
	}
	
	public int compareTo(AbstractFunction f)
	{
		return getDescription().compareTo(f.getDescription());
	}
}
