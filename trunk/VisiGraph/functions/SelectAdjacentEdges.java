import java.util.*;
import java.awt.Graphics2D;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g )
	{
		Set edges = new HashSet( );
		
		for ( Vertex v : g.vertexes )
			if ( v.isSelected.get( ) )
				edges.addAll( g.getEdges( v ) );
		
		g.selectAll( false );
		
		for ( Edge e : edges )
			e.isSelected.set( true );
		
		return edges.size( ) > 0 ? edges.size( ) + " edges selected" : "No vertices selected!";
	}
	
	public boolean allowsDynamicEvaluation( )
	{
		return false;
	}
	
	public boolean allowsOneTimeEvaluation( )
	{
		return true;
	}
	
	public String toString( )
	{
		return "Select adjacent edges";
	}
	
return (FunctionBase)this;