/**
 * CompleteBipartiteGraph.java
 */
package edu.belmont.mth.visigraph.models.generators;

import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;

/**
 * @author Cameron Behar
 *
 */
public class CompleteBipartiteGraphGenerator extends GraphGeneratorBase
{
	@Override
	public Graph generate(Graph graph, Matcher matcher)
	{
		int r = Integer.parseInt(matcher.group(1));
		int s = Integer.parseInt(matcher.group(2));
		
		for(int j = 0; j < r; ++j)
			graph.vertexes.add(new Vertex(graph.nextVertexId(), (j - (r / 2)) * UserSettings.instance.arrangeGridSpacing.get(), -UserSettings.instance.arrangeGridSpacing.get()));
		
		for(int j = 0; j < s; ++j)
			graph.vertexes.add(new Vertex(graph.nextVertexId(), (j - (s / 2)) * UserSettings.instance.arrangeGridSpacing.get(), UserSettings.instance.arrangeGridSpacing.get()));
		
		for(int j = 0; j < r; ++j)
			for(int k = 0; k < s; ++k)
				graph.edges.add(new Edge(false, graph.vertexes.get(j), graph.vertexes.get(k + r)));
		
		return graph;
	}
	
	@Override
	public String getDescription()
	{
		return "Complete bipartite graph";
	}

	@Override
	public String getParametersDescription()
	{
		return "[order of set A] [order of set B]";
	}
	
	@Override
	public String getParametersValidatingExpression()
	{
		return "^\\s*(\\d+)\\s+(\\d+)\\s*$";
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
