/**
 * BinaryTreeGraph.java
 */
package edu.belmont.mth.visigraph.models.generators;

import java.util.Vector;

import edu.belmont.mth.visigraph.models.Edge;
import edu.belmont.mth.visigraph.models.Graph;
import edu.belmont.mth.visigraph.models.Vertex;
import edu.belmont.mth.visigraph.settings.GlobalSettings;

/**
 * @author Cameron Behar
 *
 */
public class SymmetricTreeGraphGenerator extends AbstractGraphGenerator
{
	public boolean allowCycles()
	{
		return false;
	}

	public boolean allowLoops()
	{
		return false;
	}

	public boolean allowMultipleEdges()
	{
		return false;
	}

	public boolean forceAllowCycles()
	{
		return true;
	}

	public boolean forceAllowDirectedEdges()
	{
		return true;
	}

	public boolean forceAllowLoops()
	{
		return true;
	}

	public boolean forceAllowMultipleEdges()
	{
		return true;
	}

	public Graph generate(String args, boolean allowLoops, boolean allowDirectedEdges, boolean allowMultipleEdges, boolean allowCycles)
	{
		String[] parts = args.split("\\s+");
		int levelCount = Integer.parseInt(parts[0]);
		int fanOut = Integer.parseInt(parts[1]);
		Vector<Vector<Vertex>> levels = new Vector<Vector<Vertex>>();
		
		Graph ret = super.generate(args, allowLoops, allowDirectedEdges, allowMultipleEdges, allowCycles);
		 
		Vector<Vertex> level = new Vector<Vertex>();
		Vertex root = new Vertex(ret.nextVertexId());
		ret.vertexes.add(root);
		level.add(root);
		levels.add(level);
		
		for(int i = 1; i < levelCount; ++i)
		{
			level = new Vector<Vertex>();
			
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
		
		double rowSpace = GlobalSettings.arrangeBoxSize.getHeight() / (levels.size() - 1.0);
		
		for(int row = 0; row < levels.size(); ++row)
		{
			level = levels.get(row); 
			double y = row * rowSpace - (GlobalSettings.arrangeBoxSize.getHeight() / 2.0);
			double colSpace = GlobalSettings.arrangeBoxSize.getWidth() / (level.size());
			
			for(int col = 0; col < level.size(); ++col)
			{
				Vertex vertex = level.get(col);
				double x = (col + .5) * colSpace - (GlobalSettings.arrangeBoxSize.getWidth() / 2.0);
				
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
}
