import java.awt.*;
import java.util.*;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		Random random = new Random( );
		List selectedEdges = new LinkedList( );
		
		for( Edge e : g.edges )
			if( e.isSelected.get( ) )
				selectedEdges.add( e );
		
		if( selectedEdges.isEmpty( ) )
			selectedEdges = g.edges;
		
		for( Edge e : selectedEdges )
			e.weight.set( random.nextDouble( ) );
		
		return null;
	}
	
	public boolean allowsDynamicEvaluation( ) { return false;                          }
	public boolean allowsOneTimeEvaluation( ) { return true;                           }
	public String  toString               ( ) { return "Assign edge weights randomly"; }
	
return (FunctionBase) this;