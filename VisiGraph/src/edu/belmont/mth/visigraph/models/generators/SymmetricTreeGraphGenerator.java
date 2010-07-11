/**
 * BinaryTreeGraph.java
 */
package edu.belmont.mth.visigraph.models.generators;

import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;

/**
 * @author Cameron Behar
 *
 */
public class SymmetricTreeGraphGenerator extends GraphGeneratorBase
{
	@Override
	public Graph generate(Graph graph, Matcher matcher)
	{
		int levelCount = Integer.parseInt(matcher.group(1));
		int fanOut = Integer.parseInt(matcher.group(2));
		
		buildTree(graph, levelCount, fanOut, 0.0, 0.0);
		
		return graph;
	}
	
	public Vertex buildTree(Graph g, int level, int fanOut, double x, double y)
	{
		Vertex root = new Vertex(g.nextVertexId(), x, y);
		g.vertexes.add(root);
		
		if(level > 0)
		{
			double radiansPerBranch = (2 * Math.PI) / fanOut; 
			
			for(int i = 0; i < fanOut; ++i)
			{
				Vertex branch = buildTree(g, level - 1, fanOut, x + 100.0 * Math.pow(level, 1.3) * Math.cos(radiansPerBranch * i), y + 100.0 * Math.pow(level, 1.3) * Math.sin(radiansPerBranch * i));
				g.edges.add(new Edge(false, root, branch));
			}
		}
		
		return root;
	}

	@Override
	public String getDescription()
	{
		return "Symmetric tree";
	}	

	@Override
	public String getParametersDescription()
	{
		return "[recursions] [fan-out]";
	}
	
	@Override
	public String getParametersValidatingExpression()
	{
		return "^\\s*(\\d+)\\s*(\\d+)\\s*$";
	}
	
	@Override
	public BooleanRule areLoopsAllowed()
	{
		return BooleanRule.ForcedFalse;
	}
	
	@Override
	public BooleanRule areDirectedEdgesAllowed()
	{
		return BooleanRule.ForcedFalse;
	}

	@Override
	public BooleanRule areMultipleEdgesAllowed()
	{
		return BooleanRule.ForcedFalse;
	}
	
	@Override
	public BooleanRule areCyclesAllowed()
	{
		return BooleanRule.ForcedFalse;
	}

	@Override
	public BooleanRule areParametersAllowed()
	{
		return BooleanRule.ForcedTrue;
	}
}
