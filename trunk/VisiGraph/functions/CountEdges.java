import java.awt.*;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		int selectedEdgeCount = 0;
		
		for ( Edge e : g.edges )
			if ( e.isSelected.get( ) )
				++selectedEdgeCount;
		
		return Integer.toString( selectedEdgeCount > 0 ? selectedEdgeCount : g.edges.size( ) );
	}
	
	public boolean allowsDynamicEvaluation( ) { return true;          }
	public boolean allowsOneTimeEvaluation( ) { return true;          }
	public String  toString               ( ) { return "Count edges"; }
	
return (FunctionBase) this;