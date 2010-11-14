import java.awt.*;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate(Graphics2D g2D, Graph g, Component owner)
	{
		int selectedVertexCount = 0;
		
		for(int i = 0; i < g.vertexes.size(); ++i)
			if(g.vertexes.get(i).isSelected.get())
				++selectedVertexCount;
		
		return (selectedVertexCount > 0 ? selectedVertexCount : g.vertexes.size()) + "";
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
		return "Count vertices";
	}
        
return (FunctionBase)this;