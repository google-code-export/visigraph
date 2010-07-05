/**
 * GraphGeneratorBase.java
 */
package edu.belmont.mth.visigraph.models.generators;

import edu.belmont.mth.visigraph.models.Graph;

/**
 * @author Cameron Behar
 *
 */
public abstract class GraphGeneratorBase
{	
	public abstract BooleanRule areLoopsAllowed();
	
	public abstract BooleanRule areMultipleEdgesAllowed();
	
	public abstract BooleanRule areDirectedEdgesAllowed();
	
	public abstract BooleanRule areCyclesAllowed();
	
	public abstract BooleanRule areParametersAllowed();
	
	public abstract String      getParametersDescription();
	
	public abstract void        validateParameters(String args);
	
	public abstract String      getDescription();
	
	public          Graph       generate(String args, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed)
	{
		return new Graph("Untitled " + getDescription(), areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed);
	}
	
	public          String      toString()
	{
		return getDescription();
	}

	public enum     BooleanRule
	{
		ForcedTrue, DefaultTrue, ForcedFalse, DefaultFalse;
		
		public boolean isTrue()
		{
			return this == ForcedTrue || this == DefaultTrue;
		}
		
		public boolean isForced()
		{
			return this == ForcedTrue || this == ForcedFalse;
		}
	}
}
