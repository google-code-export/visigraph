import java.util.*;
import java.awt.Frame;
import java.awt.Graphics2D;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		List selectedEdges = new LinkedList( );
		
		for ( Edge e : g.edges )
			if ( e.isSelected.get( ) )
				selectedEdges.add( e );
		
		if ( selectedEdges.isEmpty( ) )
			selectedEdges = g.edges;
		
		for ( Edge e : selectedEdges )
			if ( e.thickness.get( ) > 0.1 )
				e.thickness.set( 0.5 * e.thickness.get( ) );
		
		return null;
	}
	
	public boolean allowsDynamicEvaluation( ) { return false;        }
	public boolean allowsOneTimeEvaluation( ) { return true;         }
	public String  toString               ( ) { return "Thin edges"; }
	
return (FunctionBase) this;