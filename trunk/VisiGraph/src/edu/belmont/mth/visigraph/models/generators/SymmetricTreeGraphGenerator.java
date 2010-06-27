/**
 * BinaryTreeGraph.java
 */
package edu.belmont.mth.visigraph.models.generators;

import java.util.Vector;
import edu.belmont.mth.visigraph.models.Edge;
import edu.belmont.mth.visigraph.models.Graph;
import edu.belmont.mth.visigraph.models.Vertex;

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
		Vector<Vector<Vertex>> levels = new Vector<Vector<Vertex>>();
		 
		Vertex root = new Vertex(ret.nextVertexId());
		ret.vertexes.add(root);
		
		Vector<Vertex> roots = new Vector<Vertex>();
		roots.add(root);
		levels.add(roots);
		
		for(int i = 1; i < levelCount; ++i)
		{
			Vector<Vertex> level = new Vector<Vertex>();
			
			for(Vertex parent : levels.lastElement())
			{
				for(int j = 0; j < fanOut; ++j)
				{
					Vertex child = new Vertex(ret.nextVertexId());
					ret.vertexes.add(child);
					ret.edges.add(new Edge(ret.nextEdgeId(), false, parent, child));
					level.add(child);
				}
			}
			
			levels.add(level);
		}
		
		// Now for layout!
		
		double y = 0.0;
		double largestWidth = 0;
		for (Vector<Vertex> level : levels)
			largestWidth = Math.max(largestWidth, level.size() * 150.0);
		
		for (int row = 0; row < levels.size(); ++row)
		{
			Vector<Vertex> level = levels.get(row);
			y += 150;
			double colSpace = largestWidth / (level.size());
			
			for (int col = 0; col < level.size(); ++col)
			{
				Vertex vertex = level.get(col);
				double x = (col + .5) * colSpace - largestWidth / 2.0;
				
				vertex.x.set(x);
				vertex.y.set(y);
			}
		}
		
		return ret;
	}

	public String getDescription()
	{
		return "Symmetric tree";
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
