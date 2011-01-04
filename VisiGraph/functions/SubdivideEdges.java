import java.awt.*;
import java.util.*;
import java.util.List;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		List selectedEdges = g.getSelectedEdges( );
		if( selectedEdges.isEmpty( ) )
			selectedEdges = new ArrayList( g.edges );
		
		g.suspendNotifications( true );
		
		for( Edge edge : selectedEdges )
		{
			Vertex newVertex = new Vertex( edge.handleX.get( ), edge.handleY.get( ) );
			newVertex.color.set( edge.color.get( ) );
			newVertex.isSelected.set( edge.isSelected.get( ) );
			
			g.vertices.add( newVertex );
			g.edges.remove( edge );
			
			Edge newEdge1 = new Edge( edge.isDirected, edge.from, newVertex, edge.weight.get( ) / 2.0, edge.color.get( ), edge.label.get( ) + " (1)", edge.isSelected.get( ) );
			newEdge1.thickness.set( edge.thickness.get( ) );
			g.edges.add( newEdge1 );
			
			Edge newEdge2 = new Edge( edge.isDirected, newVertex, edge.to, edge.weight.get( ) / 2.0, edge.color.get( ), edge.label.get( ) + " (2)", edge.isSelected.get( ) );
			newEdge2.thickness.set( edge.thickness.get( ) );
			g.edges.add( newEdge2 );
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
				return "Removes each selected edge from a given graph, then inserts a vertex at its old handle location and adds two new edges to bridge the gap previously connected by the selected edge.";
			case Function.Attribute.INPUT:
				return "The selected edges; if none are selected, the entire edge set will be used.";
			case Function.Attribute.SIDE_EFFECTS:
				return "Each selected edge is removed before being replaced by a new vertex and two 'cloned' daughter edges.";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return false;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_GENERATORS:
				return new String[ ] { "Gear graph" };
			case Function.Attribute.TAGS:
				return new String[ ] { "Graph modifier" };
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
		return "Subdivide edges";
	}
	
return (Function) this;
