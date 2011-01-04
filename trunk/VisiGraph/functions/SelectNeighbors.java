import java.awt.*;
import java.util.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		Set neighbors = new HashSet( );
		for( Vertex vertex : g.vertices )
			if( vertex.isSelected.get( ) )
				neighbors.addAll( g.getNeighbors( vertex ) );
		
		g.suspendNotifications( true );
		
		g.selectAll( false );
		for( Vertex neighbor : neighbors )
			neighbor.isSelected.set( true );
		
		g.suspendNotifications( false );
		
		return ( neighbors.isEmpty( ) ? "No vertices selected!" : neighbors.size( ) + " neighbors selected" );
	}
	
	
	public Object getAttribute( Function.Attribute attribute )
	{
		switch( attribute )
		{
			case Function.Attribute.AUTHOR:
				return "Cameron Behar";
			case Function.Attribute.VERSION:
				return "20110101";
			case Function.Attribute.DESCRIPTION:
				return "Selects all neighbors of the selected vertices in a given graph.  Two vertices are said to be <i>neighbors</i> if there is at least one edge incident to both of them.";
			case Function.Attribute.INPUT:
				return "The selected vertices.";
			case Function.Attribute.SIDE_EFFECTS:
				return "Sets the isSelected flags of each selected vertex's neighbors to <code>true</code>.";
			case Function.Attribute.OUTPUT:
				return "The number of unique neighbors selected.";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return false;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Select incident edges" };
			case Function.Attribute.TAGS:
				return new String[ ] { "Connectivity", "Graph evaluator", "Graph modifier", "Network flow", "Path finding", "Visibility" };
			default:
				return null;
		}
	}
	
	public boolean isApplicable( boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed )
	{
		return true;
	}
	
	public String toString( )
	{
		return "Select neighbors";
	}
	
return (Function) this;
