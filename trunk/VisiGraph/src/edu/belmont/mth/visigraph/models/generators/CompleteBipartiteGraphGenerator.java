/**
 * CompleteBipartiteGraph.java
 */
package edu.belmont.mth.visigraph.models.generators;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.GlobalSettings;
import edu.belmont.mth.visigraph.utilities.RegexUtilities;

/**
 * @author Cameron Behar
 *
 */
public class CompleteBipartiteGraphGenerator extends GraphGeneratorBase
{
	public Graph generate(String args, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed)
	{
		Graph ret = super.generate(args, areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed);
		
		String[] params = args.split("\\s+");
		int r = Integer.parseInt(params[0]);
		int s = Integer.parseInt(params[1]);
		
		for(int j = 0; j < r; ++j)
			ret.vertexes.add(new Vertex(ret.nextVertexId(), (j - (r / 2)) * GlobalSettings.arrangeGridSpacing, -GlobalSettings.arrangeGridSpacing));
		
		for(int j = 0; j < s; ++j)
			ret.vertexes.add(new Vertex(ret.nextVertexId(), (j - (s / 2)) * GlobalSettings.arrangeGridSpacing, GlobalSettings.arrangeGridSpacing));
		
		for(int j = 0; j < r; ++j)
			for(int k = 0; k < s; ++k)
				ret.edges.add(new Edge(false, ret.vertexes.get(j), ret.vertexes.get(k + r)));
		
		return ret;
	}
	
	public String getDescription()
	{
		return "Complete bipartite graph";
	}

	public String getParametersDescription()
	{
		return "[order of set A] [order of set B]";
	}
	
	public void validateParameters(String args)
	{
		if(args.trim().isEmpty())
			throw new IllegalArgumentException();
		
		String[] params = args.split("\\s+");
		
		if(params.length != 2)
			throw new IllegalArgumentException();
		
		if(!RegexUtilities.isPositiveInteger(params[0]))
			throw new IllegalArgumentException();
		
		if(!RegexUtilities.isPositiveInteger(params[1]))
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
