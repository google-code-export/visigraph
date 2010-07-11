/**
 * CompleteBipartiteGraph.java
 */
package edu.belmont.mth.visigraph.models.generators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;

/**
 * @author Cameron Behar
 *
 */
public class CompleteBipartiteGraphGenerator extends GraphGeneratorBase
{
	public Graph generate(String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed)
	{
		Graph ret = super.generate(params, areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed);
		
		Pattern pattern = Pattern.compile(getParametersValidatingExpression());
		Matcher matcher = pattern.matcher(params); matcher.find();
		int r = Integer.parseInt(matcher.group(1));
		int s = Integer.parseInt(matcher.group(2));
		
		for(int j = 0; j < r; ++j)
			ret.vertexes.add(new Vertex(ret.nextVertexId(), (j - (r / 2)) * UserSettings.instance.arrangeGridSpacing.get(), -UserSettings.instance.arrangeGridSpacing.get()));
		
		for(int j = 0; j < s; ++j)
			ret.vertexes.add(new Vertex(ret.nextVertexId(), (j - (s / 2)) * UserSettings.instance.arrangeGridSpacing.get(), UserSettings.instance.arrangeGridSpacing.get()));
		
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
	
	public String getParametersValidatingExpression()
	{
		return "^\\s*(\\d+)\\s+(\\d+)\\s*$";
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
