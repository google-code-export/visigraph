/**
 * BinaryTreeGraph.java
 */
package edu.belmont.mth.visigraph.models.generators;

import edu.belmont.mth.visigraph.models.Edge;
import edu.belmont.mth.visigraph.models.Graph;
import edu.belmont.mth.visigraph.models.Vertex;
import edu.belmont.mth.visigraph.utilities.RegexUtilities;

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
