/**
 * EmptyGraph.java
 */
package edu.belmont.mth.visigraph.models.generators;

import java.awt.geom.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.GlobalSettings;

/**
 * @author Cameron Behar
 * 
 */
public class EmptyGraphGenerator extends AbstractGraphGenerator
{
	public Graph generate(String args, boolean allowLoops, boolean allowDirectedEdges, boolean allowMultipleEdges, boolean allowCycles)
	{
		String[] params = args.split("\\s+");
		int n = (params[0].trim().length() > 0 ? Integer.parseInt(params[0]) : 0);
		
		Graph ret = new Graph("Untitled " + getDescription(), allowLoops, allowDirectedEdges, allowMultipleEdges, allowCycles);
		
		int rows = (int) Math.round(Math.sqrt(n));
		int columns = (int) Math.ceil(n / (double) rows);
		Point2D.Double location = new Point2D.Double((columns / 2.0) * -GlobalSettings.arrangeGridSpacing, (rows / 2.0) * -GlobalSettings.arrangeGridSpacing);
		
		for (int row = 0; row < rows; ++row)
			for(int col = 0; (row < rows - 1 && col < columns) || (row == rows - 1 && col < (n % columns == 0 ? columns : n % columns)); ++col)
				ret.vertexes.add(new Vertex(ret.nextVertexId(), location.x + GlobalSettings.arrangeGridSpacing * col, location.y + GlobalSettings.arrangeGridSpacing * row));
		
		return ret;
	}
	
	public String getDescription()
	{
		return "Empty graph";
	}
}
