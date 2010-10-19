import java.awt.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public Object evaluate(Graphics2D g2D, Graph g)
	{
		Set selectedEdges = new HashSet(g.edges.size( ));
		
		for(Edge e : g.edges)
			if(e.isSelected.get( ))
				selectedEdges.add(e);
		
		if(selectedEdges.size() == 0)
			selectedEdges.addAll(g.edges);
			
		for(Edge e : selectedEdges)
			e.straighten( );
			
		return null;
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
		return "Straighten edges";
	}
	
return (FunctionBase)this;