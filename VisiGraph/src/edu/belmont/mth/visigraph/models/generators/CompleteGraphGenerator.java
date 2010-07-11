/**
 * CompleteGraph.java
 */
package edu.belmont.mth.visigraph.models.generators;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;

/**
 * @author Cameron Behar
 * 
 */
public class CompleteGraphGenerator extends GraphGeneratorBase
{
	public Graph generate(String args, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed)
	{
		Graph ret = super.generate(args, areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed);
		
		String[] params = args.split("\\s+");
		int n = Integer.parseInt(params[0]);
		double radius = UserSettings.instance.arrangeCircleRadiusMultiplier.get() * n;
		double degreesPerVertex = 2 * Math.PI / n;
		
		for(int i = 0; i < n; ++i)
			ret.vertexes.add(new Vertex(i, radius * Math.cos(degreesPerVertex * i - Math.PI / 2.0), radius * Math.sin(degreesPerVertex * i - Math.PI / 2.0)));
		
		for(int i = 0; i < n; ++i)
			for(int j = i + 1; j < n; ++j)
				ret.edges.add(new Edge(false, ret.vertexes.get(i), ret.vertexes.get(j)));
		
		return ret;
	}
	
	public String getDescription()
	{
		return "Complete graph";
	}

	public String getParametersDescription()
	{
		return "[order]";
	}
	
	public String getParametersValidatingExpression()
	{
		return "^\\s*(\\d+)\\s*$";
	}
	
	public BooleanRule areLoopsAllowed()
	{
		return BooleanRule.DefaultFalse;
	}
	
	public BooleanRule areDirectedEdgesAllowed()
	{
		return BooleanRule.ForcedFalse;
	}

	public BooleanRule areMultipleEdgesAllowed()
	{
		return BooleanRule.DefaultFalse;
	}
	
	public BooleanRule areCyclesAllowed()
	{
		return BooleanRule.ForcedTrue;
	}

	public BooleanRule areParametersAllowed()
	{
		return BooleanRule.ForcedTrue;
	}
}
