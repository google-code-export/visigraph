/**
 * FunctionBase.java
 */
package edu.belmont.mth.visigraph.models.functions;

import java.awt.*;
import edu.belmont.mth.visigraph.models.*;

/**
 * @author Cameron Behar
 *
 */
public abstract class FunctionBase implements Comparable<FunctionBase>
{	
	public abstract Object evaluate(Graphics2D g2D, Graph g);
	
	public abstract String getDescription();
	
	@Override
	public String toString()
	{
		return getDescription();
	}
	
	@Override
	public int compareTo(FunctionBase f)
	{
		return getDescription().compareTo(f.getDescription());
	}
}
