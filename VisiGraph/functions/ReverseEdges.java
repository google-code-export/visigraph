import java.awt.*;
import java.util.*;
import java.util.List;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		List selectedEdges = g.getSelectedEdges( );
		if( selectedEdges.isEmpty( ) )
			selectedEdges = g.edges;
		
		g.suspendNotifications( true );
		
		for( Edge edge : selectedEdges )
		{
			g.edges.remove( edge );
			Edge reversedEdge = new Edge( true, edge.to, edge.from, edge.weight.get( ), edge.color.get( ), edge.label.get( ), edge.isSelected.get( ) );
			reversedEdge.thickness.set( edge.thickness.get( ) );
			reversedEdge.handleX.set( edge.handleX.get( ) );
			reversedEdge.handleY.set( edge.handleY.get( ) );
			reversedEdge.tag.set( edge.tag.get( ) );
			g.edges.add( reversedEdge );
			reversedEdge.refresh( );
		}
		
		g.suspendNotifications( false );
		
		return null;
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
				return "Replaces each selected edge in a given graph with an edge traveling in the opposite direction.";
			case Function.Attribute.INPUT:
				return "The selected edges; if none are selected, the entire edge set will be used.";
			case Function.Attribute.SIDE_EFFECTS:
				return "Each selected edge will be replaced by an edge in all ways identical except direction.";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return false;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_GENERATORS:
				return new String[ ] { "RELATED_GENERATORS" };
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "RELATED_FUNCTIONS" };
			case Function.Attribute.TAGS:
				return new String[ ] { "Connectivity", "Graph modifier", "Network flow", "Path finding" };
			default:
				return null;
		}
	}
	
	public boolean isApplicable( boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed )
	{
		return areDirectedEdgesAllowed && areCyclesAllowed;
	}
	
	public String toString( )
	{
		return "Reverse edges";
	}
	
return (Function) this;
