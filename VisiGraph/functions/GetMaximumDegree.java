import java.awt.*;
import java.util.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate(Graphics2D g2D, Graph g, Component owner)
	{
		int maxDegreeAll = 0;
		int maxDegreeSelected = 0;
		boolean anySelected = false;
		
		for(Vertex v : g.vertexes)
		{
			int degree = g.getNeighbors(v).size();
			
			if(degree > maxDegreeAll)
				maxDegreeAll = degree;
			
			if(v.isSelected.get())
			{
				anySelected = true;
				if(degree > maxDegreeSelected)
					maxDegreeSelected = degree;
			}
		}
		
		return (anySelected ? maxDegreeSelected : maxDegreeAll).toString();
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
		return "Get maximum degree";
	}

return (FunctionBase)this;
