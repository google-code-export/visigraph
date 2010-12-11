import java.awt.*;
import java.util.*;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		ArrayList selectedEdges = new ArrayList( );
		
		g.suspendNotifications( true );
		
		for ( Edge e : g.edges )
			if ( e.isSelected.get( ) )
				selectedEdges.add( e );
		
		if ( selectedEdges.isEmpty( ) )
			selectedEdges.addAll( g.edges );
		
		for ( Edge e : selectedEdges )
		{
			Vertex newVertex = new Vertex( e.handleX.get( ), e.handleY.get( ) );
			newVertex.color.set( e.color.get( ) );
			newVertex.isSelected.set( e.isSelected.get( ) );
			
			g.vertexes.add( newVertex );
			g.edges.remove( e );
			
			Edge newEdge1 = new Edge( e.isDirected, e.from, newVertex, e.weight.get( ) / 2.0, e.color.get( ), e.label.get( ) + " (1)", e.isSelected.get( ) );
			newEdge1.thickness.set( e.thickness.get( ) );
			g.edges.add( newEdge1 );
			
			Edge newEdge2 = new Edge( e.isDirected, newVertex, e.to, e.weight.get( ) / 2.0, e.color.get( ), e.label.get( ) + " (2)", e.isSelected.get( ) );
			newEdge2.thickness.set( e.thickness.get( ) );
			g.edges.add( newEdge2 );
		}
		
		g.suspendNotifications( false );
		
		return null;
	}
	
	public boolean allowsDynamicEvaluation( ) { return false;             }
	public boolean allowsOneTimeEvaluation( ) { return true;              }
	public String  toString               ( ) { return "Subdivide edges"; }
	
return (FunctionBase)this;