/**
 * CartesianProductOfCompleteBipartiteGraphAndCycleBehar.java
 */
package edu.belmont.mth.visigraph.models.generators;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.utilities.RegexUtilities;

/**
 * @author Cameron Behar
 *
 */
public class CartesianProductOfCompleteBipartiteGraphAndCycleGeneratorBehar extends GraphGeneratorBase
{	
	public Graph generate(String args, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed)
	{
		Graph ret = super.generate(args, areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed);
		
		String[] params = args.split("\\s+");
		int r = Integer.parseInt(params[0]);
		int s = Integer.parseInt(params[1]);
		int n = Integer.parseInt(params[2]);
		
		int northwest = (int) Math.ceil (r / 2.0);
		int northeast = (int) Math.ceil (s / 2.0);
		int southeast = (int) Math.floor(r / 2.0);
		int southwest = (int) Math.floor(s / 2.0);
		
		int linkHeight = (Math.max(r, s) + 2) * 50;
		
		// Add all the links plus the ends of the chain
		for(int i = -1; i <= n; ++i)
		{
			double linkY = (linkHeight + 100) * i;
			
			// Add all the vertices of one bipartite graph (or end)
			for(int j = 0; j < northwest; ++j)
				ret.vertexes.add(new Vertex(ret.nextVertexId(), (j - northwest) * 50.0, (j - northwest) * (i % 2 == 0 ? 50.0 : -50.0) + linkY));
			
			for(int j = 0; j < southeast; ++j)
				ret.vertexes.add(new Vertex(ret.nextVertexId(), (j + 1)         * 50.0, (j + 1)         * (i % 2 == 0 ? 50.0 : -50.0) + linkY));
			
			for(int j = 0; j < southwest; ++j)
				ret.vertexes.add(new Vertex(ret.nextVertexId(), (j - southwest)  * 50.0, (j - southwest)  * (i % 2 == 0 ? -50.0 : 50.0) + linkY));
			
			for(int j = 0; j < northeast; ++j)
				ret.vertexes.add(new Vertex(ret.nextVertexId(), (j + 1)     * 50.0, (j + 1)     * (i % 2 == 0 ? -50.0 : 50.0) + linkY));
			
			// Add all the edges for that bipartite graph
			if(i != -1 && i != n)
				for(int j = 0; j < r; ++j)
					for(int k = 0; k < s; ++k)
						ret.edges.add(new Edge(false, ret.vertexes.get(ret.vertexes.size() - r - s + j), ret.vertexes.get(ret.vertexes.size() - s + k)));
			
			// Add the edges between this bipartite graph and the previous one
			if(i > -1)
			{
				for(int j = 0; j < r + s; ++j)
				{
					Edge edge = new Edge(false, ret.vertexes.get(ret.vertexes.size() - r - s - j - 1), ret.vertexes.get(ret.vertexes.size() - j - 1));
					
					// If this edge links outside vertices, we need to adjust the handles
					if(edge.to.y.get() > linkY)
						edge.handleX.set(edge.handleX.get() + (edge.to.y.get() - edge.from.y.get()) / (edge.handleX.get() > 0 ? 2.0 : -2.0));
					
					ret.edges.add(edge);	
				}
			}
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
		return "Cartesian product of a complete bipartite graph and cycle (Behar)";
	}

	public String getParametersDescription()
	{
		return "[order of NW-SE axes] [order of NE-SW axes] [order of cycle]";
	}
	
	public void validateParameters(String args)
	{
		if(args.trim().isEmpty())
			throw new IllegalArgumentException();
		
		String[] params = args.split("\\s+");
		
		if(params.length != 3)
			throw new IllegalArgumentException();
		
		if(!RegexUtilities.isPositiveInteger(params[0]))
			throw new IllegalArgumentException();
		
		if(!RegexUtilities.isPositiveInteger(params[1]))
			throw new IllegalArgumentException();
		
		if(!RegexUtilities.isPositiveInteger(params[2]))
			throw new IllegalArgumentException();
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
