/**
 * ScottCartesianProductOfCompleteBipartiteGraphAndCycle.java
 */
package edu.belmont.mth.visigraph.models.generators;

import edu.belmont.mth.visigraph.models.*;

/**
 * @author Cameron Behar
 *
 */
public class CartesianProductOfCompleteBipartiteGraphAndCycleGeneratorScott extends GraphGeneratorBase
{	
	public Graph generate(String args, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed)
	{
		Graph ret = super.generate(args, areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed);
		
		String[] params = args.split("\\s+");
		int r = Integer.parseInt(params[0]);
		int s = Integer.parseInt(params[1]);
		int n = Integer.parseInt(params[2]);
		
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
				ret.vertexes.add(new Vertex(ret.nextVertexId(), (j - north) * 50.0 + 25.0, (i == -1 || i == n ? 0.0 : j - north) * 50.0 + (linkHeight + 100) * i));
			
			for(int j = 0; j < south; ++j)
				ret.vertexes.add(new Vertex(ret.nextVertexId(), (j + 1)     * 50.0 - 25.0, (i == -1 || i == n ? 0.0 : j + 1)     * 50.0 + (linkHeight + 100) * i));
			
			for(int j = 0; j < west; ++j)
				ret.vertexes.add(new Vertex(ret.nextVertexId(), (j - west) * 50.0, (linkHeight + 100) * i));
			
			for(int j = 0; j < east; ++j)
				ret.vertexes.add(new Vertex(ret.nextVertexId(), (j + 1)    * 50.0, (linkHeight + 100) * i));
			
			// Add all the edges for that bipartite graph
			if(i != -1 && i != n)
				for(int j = 0; j < r; ++j)
					for(int k = 0; k < s; ++k)
						ret.edges.add(new Edge(ret.nextEdgeId(), false, ret.vertexes.get(ret.vertexes.size() - r - s + j), ret.vertexes.get(ret.vertexes.size() - s + k)));
			
			// Add the edges between this bipartite graph and the previous one
			if(i > -1)
				for(int j = 0; j < r + s; ++j)
					ret.edges.add(new Edge(ret.nextEdgeId(), false, ret.vertexes.get(ret.vertexes.size() - r - s - j - 1), ret.vertexes.get(ret.vertexes.size() - j - 1)));
		}
		
		// For aesthetic reasons, make the vertices at the end of the chains smaller
		for(int i = 0; i < r + s; ++i)
		{
			ret.vertexes.get(i).radius.set(2.0);
			ret.vertexes.get(ret.vertexes.size() - i - 1).radius.set(2.0);
		}
		
		return ret;
	}
	
	public String getDescription()
	{
		return "Cartesian product of a complete bipartite graph and cycle (Scott)";
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
