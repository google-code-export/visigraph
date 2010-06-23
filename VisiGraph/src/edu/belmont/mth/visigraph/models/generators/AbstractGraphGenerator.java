/**
 * AbstractGraphGenerator.java
 */
package edu.belmont.mth.visigraph.models.generators;

import edu.belmont.mth.visigraph.models.Graph;
import static edu.belmont.mth.visigraph.settings.GlobalSettings.*;

/**
 * @author Cameron Behar
 *
 */
public abstract class AbstractGraphGenerator
{	
	public boolean allowLoops()
	{
		return defaultAllowLoops;
	}
	
	public boolean forceAllowLoops()
	{
		return defaultForceAllowLoops;
	}
	
	public boolean allowMultipleEdges()
	{
		return defaultAllowMultipleEdges;
	}
	
	public boolean forceAllowMultipleEdges()
	{
		return defaultForceAllowMultipleEdges;
	}
	
	public boolean allowDirectedEdges()
	{
		return defaultAllowDirectedEdges;
	}
	
	public boolean forceAllowDirectedEdges()
	{
		return defaultForceAllowDirectedEdges;
	}
	
	public boolean allowCycles()
	{
		return defaultAllowCycles;
	}
	
	public boolean forceAllowCycles()
	{
		return defaultForceAllowCycles;
	}
	
	public boolean allowParameters()
	{
		return defaultAllowParameters;
	}
	
	public abstract String getDescription();
	
	public Graph generate(String args, boolean allowLoops, boolean allowDirectedEdges, boolean allowMultipleEdges, boolean allowCycles)
	{
		return new Graph("Untitled " + getDescription(), allowLoops, allowDirectedEdges, allowMultipleEdges, allowCycles);
	}
	
	public String toString()
	{
		return getDescription();
	}
}
