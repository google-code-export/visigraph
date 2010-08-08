import java.awt.*;
import java.util.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate(Graphics2D g2D, Graph g)
	{
		if(g.vertexes.size() < 1)
			return "false";
		
		HashSet covered = new HashSet();
		coverRecursively(g.vertexes.get(0), g, covered);
		
		return (g.vertexes.size() == covered.size()) + "";
	}

	private void coverRecursively(Vertex vertex, Graph g, Set covered)
	{
		covered.add(vertex);
		
		Set neighbors = g.getNeighbors(vertex);
		
		Object[] neighborArray = neighbors.toArray();
		
		for(int i = 0; i < neighborArray.length; ++i)
			if(!covered.contains(neighborArray[i]))
				coverRecursively(neighborArray[i], g, covered);
	}
	
	public boolean allowsDynamicEvaluation()
	{
		return true;
	}
	
	public boolean allowsOneTimeEvaluation()
	{
		return true;
	}
	
	public String toString()
	{
		return "Is connected";
	}
	
return (FunctionBase)this;