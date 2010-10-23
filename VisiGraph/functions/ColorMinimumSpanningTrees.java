import java.awt.*;
import java.util.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate(Graphics2D g2D, Graph g)
	{
		if(g.vertexes.size() < 1)
			return "No vertices found!";
		
		for(int i = 0; i < g.vertexes.size(); ++i)
			g.vertexes.get(i).color.set(-1);
		
		for(int i = 0; i < g.edges.size(); ++i)
			g.edges.get(i).color.set(-1);
		
		HashSet    covered  = new HashSet();
		LinkedList covering = new LinkedList(); 
		int        color    = -1;
		
		while(covered.size() < g.vertexes.size())
		{
			++color;
			
			Vertex root = null;
			
			for(int i = 0; i < g.vertexes.size(); ++i)
			{
				if(!covered.contains(g.vertexes.get(i)))
				{
					root = g.vertexes.get(i);
					break;
				}
			}
			
			covering.addLast(root);
			covered.add(root);
			root.color.set(color);
			
			while(covering.size() > 0)
			{
				Vertex vertex = (Vertex)covering.removeFirst();
				
				Set neighbors = g.getNeighbors(vertex);
				Object[] neighborArray = neighbors.toArray();
			
				for(int i = 0; i < neighborArray.length; ++i)
					if(!covered.contains(neighborArray[i]))
					{
						covering.addLast(neighborArray[i]);
						covered.add(neighborArray[i]);
						((Vertex)neighborArray[i]).color.set(color);
						
						Set edge = g.getEdges(vertex, (Vertex)neighborArray[i]);
						edge.iterator().next().color.set(color);
					}
				
			}
		}
		
		return color == 0 ? "1 tree found" : (color + 1) + " disjoint trees found";
	}

	public boolean allowsDynamicEvaluation()
	{
		return false;
	}
	
	public boolean allowsOneTimeEvaluation()
	{
		return true;
	}
	
	public String toString()
	{
		return "Color minimum spanning trees";
	}
	
return (FunctionBase)this;