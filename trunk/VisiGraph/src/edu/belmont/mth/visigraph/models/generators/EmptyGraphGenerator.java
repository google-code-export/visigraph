/**
 * EmptyGraph.java
 */
package edu.belmont.mth.visigraph.models.generators;

/**
 * @author Cameron Behar
 * 
 */
public class EmptyGraphGenerator extends AbstractGraphGenerator
{
	public boolean allowParameters()
	{
		return false;
	}
	
	public String getDescription()
	{
		return "Empty graph";
	}
}
