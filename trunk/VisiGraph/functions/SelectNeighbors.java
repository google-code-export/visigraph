import java.util.*;
import java.awt.Graphics2D;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g )
	{
		Set neighbors = new HashSet( );
		
		for ( Vertex v : g.vertexes )
			if ( v.isSelected.get( ) )
				neighbors.addAll( g.getNeighbors( v ) );
		
		g.selectAll( false );
		
		for ( Vertex v : neighbors )
			v.isSelected.set( true );
		
		return neighbors.size( ) > 0 ? neighbors.size( ) + " neighbors selected" : "No vertices selected!";
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
		return "Select neighbors";
	}
	
return (FunctionBase)this;