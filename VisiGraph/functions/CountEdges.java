import java.awt.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate(Graphics2D g2D, Graph g)
	{
		int selectedEdgeCount = 0;
		
		for(int i = 0; i < g.edges.size(); ++i)
			if(g.edges.get(i).isSelected.get())
				++selectedEdgeCount;
		
		return (selectedEdgeCount > 0 ? selectedEdgeCount : g.edges.size()) + "";
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
		return "Count edges";
	}
	
return (FunctionBase)this;