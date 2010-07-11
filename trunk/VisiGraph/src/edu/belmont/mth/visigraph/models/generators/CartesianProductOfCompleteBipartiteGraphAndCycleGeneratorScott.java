/**
 * ScottCartesianProductOfCompleteBipartiteGraphAndCycle.java
 */
package edu.belmont.mth.visigraph.models.generators;

import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;

/**
 * @author Cameron Behar
 *
 */
public class CartesianProductOfCompleteBipartiteGraphAndCycleGeneratorScott extends GraphGeneratorBase
{	
	@Override
	public Graph generate(Graph graph, Matcher matcher)
	{
		int r = Integer.parseInt(matcher.group(1));
		int s = Integer.parseInt(matcher.group(2));
		int n = Integer.parseInt(matcher.group(3));
		
		int north = (int) Math.floor(r / 2.0);
		int east  = (int) Math.ceil (s / 2.0);
		int south = (int) Math.ceil (r / 2.0);
		int west  = (int) Math.floor(s / 2.0);
		
		int linkHeight = (north + 1 + south + 1) * 50;
		
		// Add all the links plus the ends of the chain
		for(int i = -1; i <= n; ++i)
		{
			// Add all the vertices of one bipartite graph (or end)
			for(int j = 0; j < north; ++j)
				graph.vertexes.add(new Vertex(graph.nextVertexId(), (j - north) * 50.0 + 25.0, (i == -1 || i == n ? 0.0 : j - north) * 50.0 + (linkHeight + 100) * i));
			
			for(int j = 0; j < south; ++j)
				graph.vertexes.add(new Vertex(graph.nextVertexId(), (j + 1)     * 50.0 - 25.0, (i == -1 || i == n ? 0.0 : j + 1)     * 50.0 + (linkHeight + 100) * i));
			
			for(int j = 0; j < west; ++j)
				graph.vertexes.add(new Vertex(graph.nextVertexId(), (j - west) * 50.0, (linkHeight + 100) * i));
			
			for(int j = 0; j < east; ++j)
				graph.vertexes.add(new Vertex(graph.nextVertexId(), (j + 1)    * 50.0, (linkHeight + 100) * i));
			
			// Add all the edges for that bipartite graph
			if(i != -1 && i != n)
				for(int j = 0; j < r; ++j)
					for(int k = 0; k < s; ++k)
						graph.edges.add(new Edge(false, graph.vertexes.get(graph.vertexes.size() - r - s + j), graph.vertexes.get(graph.vertexes.size() - s + k)));
			
			// Add the edges between this bipartite graph and the previous one
			if(i > -1)
				for(int j = 0; j < r + s; ++j)
					graph.edges.add(new Edge(false, graph.vertexes.get(graph.vertexes.size() - r - s - j - 1), graph.vertexes.get(graph.vertexes.size() - j - 1)));
		}
		
		// For aesthetic reasons, make the vertices at the end of the chains smaller
		for(int i = 0; i < r + s; ++i)
		{
			graph.vertexes.get(i).radius.set(2.0);
			graph.vertexes.get(graph.vertexes.size() - i - 1).radius.set(2.0);
		}
		
		return graph;
	}
	
	@Override
	public String getDescription()
	{
		return "Cartesian product of a complete bipartite graph and cycle (Scott)";
	}

	@Override
	public String getParametersDescription()
	{
		return "[order of slant axes] [order of horizontal axes] [order of cycle]";
	}
	
	@Override
	public String getParametersValidatingExpression()
	{
		return "^\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)\\s*$";
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
