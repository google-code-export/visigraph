import java.awt.*;
import java.util.*;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;
import edu.belmont.mth.visigraph.utilities.GeometryUtilities;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		// TODO: Use arc-lengths, when applicaple
		List selectedEdges = new LinkedList( );
		
		for( Edge e : g.edges )
			if( e.isSelected.get( ) )
				selectedEdges.add( e );
		
		if( selectedEdges.isEmpty( ) )
			selectedEdges = g.edges;
		
		for( Edge e : selectedEdges )
			e.weight.set( ( Math.abs( e.from.x.get( ) - e.to.x.get( ) ) + Math.abs( e.from.y.get( ) - e.to.y.get( ) ) ) / 10.0 );
		
		return null;
	}
	
	public boolean allowsDynamicEvaluation( ) { return false;                                       }
	public boolean allowsOneTimeEvaluation( ) { return true;                                        }
	public String  toString               ( ) { return "Assign edge weights by rectilinear length"; }
	
return (FunctionBase) this;