/**
 * CycleGraphGenerator.java
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
public class CycleGraphGenerator extends GraphGeneratorBase
{
	public Graph generate(String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed)
	{
		Graph ret = super.generate(params, areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed);
		
		Pattern pattern = Pattern.compile(getParametersValidatingExpression());
		Matcher matcher = pattern.matcher(params); matcher.find();
		int n = Integer.parseInt(matcher.group(1));
		double radius = UserSettings.instance.arrangeCircleRadiusMultiplier.get() * n;
		double degreesPerVertex = 2 * Math.PI / n;
		
		for(int i = 0; i < n; ++i)
			ret.vertexes.add(new Vertex(i,radius * Math.cos(degreesPerVertex * i - Math.PI / 2.0), radius * Math.sin(degreesPerVertex * i - Math.PI / 2.0)));
		
		for(int i = 0; i < n; ++i)
			ret.edges.add(new Edge(false, ret.vertexes.get(i), ret.vertexes.get((i + 1) % n)));
			
		return ret;
	}
	
	public String getDescription()
	{
		return "Cycle graph";
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
