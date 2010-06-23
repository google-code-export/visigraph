/**
 * CompleteGraph.java
 */
package edu.belmont.mth.visigraph.models.generators;

import edu.belmont.mth.visigraph.models.Edge;
import edu.belmont.mth.visigraph.models.Graph;
import edu.belmont.mth.visigraph.models.Vertex;
import edu.belmont.mth.visigraph.settings.GlobalSettings;

/**
 * @author Cameron Behar
 * 
 */
public class CompleteGraphGenerator extends AbstractGraphGenerator
{
	public boolean forceAllowCycles()
	{
		return true;
	}
	
	public boolean forceAllowDirectedEdges()
	{
		return true;
	}
	
	public Graph generate(String args, boolean allowLoops, boolean allowDirectedEdges, boolean allowMultipleEdges, boolean allowCycles)
	{
		String[] params = args.split("\\s+");
		int n = Integer.parseInt(params[0]);
		double degreesPerVertex = 2 * Math.PI / n;

		Graph ret = super.generate(args, allowLoops, allowDirectedEdges, allowMultipleEdges, allowCycles);
		
		for(int i = 0; i < n; ++i)
			ret.vertexes.add(new Vertex(i, GlobalSettings.arrangeCircleRadius * Math.cos(degreesPerVertex * i - Math.PI / 2.0), GlobalSettings.arrangeCircleRadius * Math.sin(degreesPerVertex * i - Math.PI / 2.0)));
		
		for(int i = 0; i < n; ++i)
			for(int j = i + 1; j < n; ++j)
				ret.edges.add(new Edge(ret.nextEdgeId(), false, ret.vertexes.get(i), ret.vertexes.get(j)));
		
		return ret;
	}
	
	public String getDescription()
	{
		return "Complete graph";
	}
}
