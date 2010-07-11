/**
 * GraphGeneratorBase.java
 */
package edu.belmont.mth.visigraph.models.generators;

import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;

/**
 * @author Cameron Behar
 *
 */
public abstract class GraphGeneratorBase
{	
	public    abstract BooleanRule areLoopsAllowed();
	
	public    abstract BooleanRule areMultipleEdgesAllowed();
	
	public    abstract BooleanRule areDirectedEdgesAllowed();
	
	public    abstract BooleanRule areCyclesAllowed();
	
	public    abstract BooleanRule areParametersAllowed();
	
	public    abstract String      getParametersDescription();
	
	public    abstract String	   getParametersValidatingExpression();
	
	public    abstract String      getDescription();
	
	protected abstract Graph       generate(Graph graph, Matcher matcher);
	
	public    final    Graph       generate(String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed)
	{
		Graph graph = new Graph("Untitled " + getDescription(), areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed);
		Pattern pattern = Pattern.compile(getParametersValidatingExpression());
		Matcher matcher = pattern.matcher(params); matcher.find();
		return generate(graph, matcher);
	}
	
	@Override
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
