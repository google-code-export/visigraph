/**
 * IsConnectedFunction.java
 */
package edu.belmont.mth.visigraph.models.functions;

import java.awt.*;
import java.util.*;
import edu.belmont.mth.visigraph.models.*;

/**
 * @author Cameron Behar
 *
 */
public class IsConnectedFunction extends FunctionBase
{
	public Object evaluate(Graphics2D g2D, Graph g)
	{
		if(g.vertexes.size() < 1)
			return false;
		
		HashSet<Vertex> covered = new HashSet<Vertex>();
		coverRecursively(g.vertexes.get(0), g, covered);
		
		return g.vertexes.size() == covered.size();
	}

	private void coverRecursively(Vertex vertex, Graph g, HashSet<Vertex> covered)
	{
		covered.add(vertex);
		
		Set<Vertex> neighbors = g.getNeighbors(vertex);
		
		for(Vertex neighbor : neighbors)
			if(!covered.contains(neighbor))
				coverRecursively(neighbor, g, covered);
	}
	
	public String getDescription()
	{
		return "Is connected";
	}	
}
