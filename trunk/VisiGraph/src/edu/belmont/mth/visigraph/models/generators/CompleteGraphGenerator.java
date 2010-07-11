/**
 * CompleteGraph.java
 */
package edu.belmont.mth.visigraph.models.generators;

import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;

/**
 * @author Cameron Behar
 * 
 */
public class CompleteGraphGenerator extends GraphGeneratorBase
{
	@Override
	public Graph generate(Graph graph, Matcher matcher)
	{
		int n = Integer.parseInt(matcher.group(1));
		
		double radius = UserSettings.instance.arrangeCircleRadiusMultiplier.get() * n;
		double degreesPerVertex = 2 * Math.PI / n;
		
		for(int i = 0; i < n; ++i)
			graph.vertexes.add(new Vertex(i, radius * Math.cos(degreesPerVertex * i - Math.PI / 2.0), radius * Math.sin(degreesPerVertex * i - Math.PI / 2.0)));
		
		for(int i = 0; i < n; ++i)
			for(int j = i + 1; j < n; ++j)
				graph.edges.add(new Edge(false, graph.vertexes.get(i), graph.vertexes.get(j)));
		
		return graph;
	}
	
	@Override
	public String getDescription()
	{
		return "Complete graph";
	}

	@Override
	public String getParametersDescription()
	{
		return "[order]";
	}
	
	@Override
	public String getParametersValidatingExpression()
	{
		return "^\\s*(\\d+)\\s*$";
	}
	
	@Override
	public BooleanRule areLoopsAllowed()
	{
		return BooleanRule.DefaultFalse;
	}
	
	@Override
	public BooleanRule areDirectedEdgesAllowed()
	{
		return BooleanRule.ForcedFalse;
	}

	@Override
	public BooleanRule areMultipleEdgesAllowed()
	{
		return BooleanRule.DefaultFalse;
	}

	@Override
	public BooleanRule areCyclesAllowed()
	{
		return BooleanRule.ForcedTrue;
	}

	@Override
	public BooleanRule areParametersAllowed()
	{
		return BooleanRule.ForcedTrue;
	}
}
