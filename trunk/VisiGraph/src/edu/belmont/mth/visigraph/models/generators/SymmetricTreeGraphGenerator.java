/**
 * BinaryTreeGraph.java
 */
package edu.belmont.mth.visigraph.models.generators;

import edu.belmont.mth.visigraph.models.*;

/**
 * @author Cameron Behar
 *
 */
public class SymmetricTreeGraphGenerator extends GraphGeneratorBase
{
	public Graph generate(String args, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed)
	{
		Graph ret = super.generate(args, areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed);
		
		String[] parts = args.split("\\s+");
		int levelCount = Integer.parseInt(parts[0]);
		int fanOut = Integer.parseInt(parts[1]);
		
		buildTree(ret, levelCount, fanOut, 0.0, 0.0);
		
		return ret;
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

	public String getDescription()
	{
		return "Symmetric tree";
	}	

	public String getParametersDescription()
	{
		return "[recursions] [fan-out]";
	}
	
	public String getParametersValidatingExpression()
	{
		return "^\\s*(\\d+),?\\s*(\\d+)\\s*$";
	}
	
	public BooleanRule areLoopsAllowed()
	{
		return BooleanRule.ForcedFalse;
	}
	
	public BooleanRule areDirectedEdgesAllowed()
	{
		return BooleanRule.ForcedFalse;
	}

	public BooleanRule areMultipleEdgesAllowed()
	{
		return BooleanRule.ForcedFalse;
	}
	
	public BooleanRule areCyclesAllowed()
	{
		return BooleanRule.ForcedFalse;
	}

	public BooleanRule areParametersAllowed()
	{
		return BooleanRule.ForcedTrue;
	}
}
