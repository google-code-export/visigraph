/**
 * EmptyGraph.java
 */
package edu.belmont.mth.visigraph.models.generators;

import java.awt.geom.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;

/**
 * @author Cameron Behar
 * 
 */
public class EmptyGraphGenerator extends GraphGeneratorBase
{
	public Graph generate(String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed)
	{
		Graph ret = super.generate(params, areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed);
		
		Pattern pattern = Pattern.compile(getParametersValidatingExpression());
		Matcher matcher = pattern.matcher(params); matcher.find();
		int n = (matcher.group(0).trim().length() > 0 ? Integer.parseInt(matcher.group(1)) : 0);
		
		int rows = (int) Math.round(Math.sqrt(n));
		int columns = (int) Math.ceil(n / (double) rows);
		Point2D.Double location = new Point2D.Double((columns / 2.0) * -UserSettings.instance.arrangeGridSpacing.get(), (rows / 2.0) * -UserSettings.instance.arrangeGridSpacing.get());
		
		for (int row = 0; row < rows; ++row)
			for(int col = 0; (row < rows - 1 && col < columns) || (row == rows - 1 && col < (n % columns == 0 ? columns : n % columns)); ++col)
				ret.vertexes.add(new Vertex(ret.nextVertexId(), location.x + UserSettings.instance.arrangeGridSpacing.get() * col, location.y + UserSettings.instance.arrangeGridSpacing.get() * row));
		
		return ret;
	}
	
	public String getDescription()
	{
		return "Empty graph";
	}
	
	public String getParametersDescription()
	{
		return "[order (optional)]";
	}
	
	public String getParametersValidatingExpression()
	{
		return "^\\s*(\\d+)?\\s*$";
	}
	
	public BooleanRule areLoopsAllowed()
	{
		return BooleanRule.DefaultFalse;
	}

	public BooleanRule areMultipleEdgesAllowed()
	{
		return BooleanRule.DefaultFalse;
	}

	public BooleanRule areDirectedEdgesAllowed()
	{
		return BooleanRule.DefaultFalse;
	}
	
	public BooleanRule areCyclesAllowed()
	{
		return BooleanRule.DefaultTrue;
	}
	
	public BooleanRule areParametersAllowed()
	{
		return BooleanRule.DefaultTrue;
	}
}
