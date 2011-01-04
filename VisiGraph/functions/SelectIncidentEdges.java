import java.awt.*;
import java.util.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		Set edgesToSelect = new HashSet( );
		for( Vertex vertex : g.vertices )
			if( vertex.isSelected.get( ) )
				edgesToSelect.addAll( g.getEdges( vertex ) );
		
		g.suspendNotifications( true );
		
		g.selectAll( false );
		for( Edge edge : edgesToSelect )
			edge.isSelected.set( true );
		
		g.suspendNotifications( false );
		
		return ( edgesToSelect.isEmpty( ) ? "No vertices selected!" : edgesToSelect.size( ) + " edges selected" );
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
				return "Selects all edges incident to a selected vertex in a given graph.  An edge is said to be <i>incident</i> to a vertex if it connects it to another vertex or itself (a loop).";
			case Function.Attribute.INPUT:
				return "The selected vertices.";
			case Function.Attribute.SIDE_EFFECTS:
				return "Sets the isSelected flags of each selected vertex's incident edges to <code>true</code>.";
			case Function.Attribute.OUTPUT:
				return "The number of unique incident edges selected.";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return false;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Select neighbors" };
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
		return "Select incident edges";
	}
	
return (Function) this;
