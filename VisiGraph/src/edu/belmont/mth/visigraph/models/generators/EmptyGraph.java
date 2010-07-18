/**
 * EmptyGraph.java
 */
package edu.belmont.mth.visigraph.models.generators;

import java.awt.geom.*;
import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;

/**
 * @author Cameron Behar
 *
 */
public class EmptyGraph implements GeneratorBase
{	
	@Override
	public Graph generate(String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed)
	{
		Graph graph = new Graph(UserSettings.instance.defaultGraphName.get() + " " + toString(), areLoopsAllowed,areDirectedEdgesAllowed,areMultipleEdgesAllowed,areCyclesAllowed);
		Pattern pattern = Pattern.compile(getParametersValidatingExpression());
		Matcher matcher = pattern.matcher(params); matcher.find();
		
		int n = (matcher.group(0).trim().length() > 0 ? Integer.parseInt(matcher.group(1)) : 0);
		
		int rows = (int) Math.round(Math.sqrt(n));
		int columns = (int) Math.ceil(n / (double) rows);
		Point2D.Double location = new Point2D.Double((columns / 2.0) * -UserSettings.instance.arrangeGridSpacing.get(), (rows / 2.0) * -UserSettings.instance.arrangeGridSpacing.get());
		
		for (int row = 0; row < rows; ++row)
		for(int col = 0; (row < rows - 1 && col < columns) || (row == rows - 1 && col < (n % columns == 0 ? columns : n % columns)); ++col)
		graph.vertexes.add(new Vertex(graph.nextVertexId(), location.x + UserSettings.instance.arrangeGridSpacing.get() * col, location.y + UserSettings.instance.arrangeGridSpacing.get() * row));
		
		return graph;
	}
	
	@Override
	public String toString()
	{
		return "(Empty graph)";
	}
	
	@Override
	public String getParametersDescription()
	{
		return "[order (optional)]";
	}
	
	@Override
	public String getParametersValidatingExpression()
	{
		return "^\\s*(\\d+)?\\s*$";
	}
	
	@Override
	public BooleanRule areLoopsAllowed()
	{
		return BooleanRule.DefaultFalse;
	}
	
	@Override
	public BooleanRule areMultipleEdgesAllowed()
	{
		return BooleanRule.DefaultFalse;
	}
	
	@Override
	public BooleanRule areDirectedEdgesAllowed()
	{
		return BooleanRule.DefaultFalse;
	}
	
	@Override
	public BooleanRule areCyclesAllowed()
	{
		return BooleanRule.DefaultTrue;
	}
	
	@Override
	public BooleanRule areParametersAllowed()
	{
		return BooleanRule.DefaultTrue;
	}
}
