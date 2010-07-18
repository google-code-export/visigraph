/**
 * GraphGeneratorBase.java
 */
package edu.belmont.mth.visigraph.models.generators;

import edu.belmont.mth.visigraph.models.*;

/**
 * @author Cameron Behar
 *
 */
public interface GeneratorBase
{	
	public     BooleanRule areLoopsAllowed();
	
	public     BooleanRule areMultipleEdgesAllowed();
	
	public     BooleanRule areDirectedEdgesAllowed();
	
	public     BooleanRule areCyclesAllowed();
	
	public     BooleanRule areParametersAllowed();
	
	public     String      getParametersDescription();
	
	public     String	   getParametersValidatingExpression();
	
	public     Graph       generate(String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed);

	public enum BooleanRule
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
