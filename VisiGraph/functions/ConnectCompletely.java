import java.awt.*;
import java.util.*;
import java.util.List;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;
import edu.belmont.mth.visigraph.utilities.GeometryUtilities;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		if ( !g.areCyclesAllowed )
			return "Cannot embed cycles in a tree!";
		
		List selectedVertexes = new LinkedList( );
		
		for ( Vertex v : g.vertexes )
			if ( v.isSelected.get( ) )
				selectedVertexes.add( v );
		
		if ( selectedVertexes.isEmpty( ) )
			selectedVertexes = g.vertexes;
		
		List newEdges = new LinkedList( );
		
		for ( int i = 0; i < selectedVertexes.size( ); ++i )
		{
			int start = ( g.areDirectedEdgesAllowed ? 0 : i + ( g.areLoopsAllowed ? 0 : 1 ) );
			for ( int j = start; j < selectedVertexes.size( ); ++j )
				if ( g.getEdges( selectedVertexes.get( i ), selectedVertexes.get( j ) ).size( ) < 1 )
					newEdges.add( new Edge( g.areDirectedEdgesAllowed, selectedVertexes.get( i ), selectedVertexes.get( j ) ) );
		}
		
		g.suspendNotifications( true );
		g.edges.addAll( newEdges );
		g.suspendNotifications( false );
		
		return null;
	}
	
	public boolean allowsDynamicEvaluation( ) { return false;                }
	public boolean allowsOneTimeEvaluation( ) { return true;                 }
	public String  toString               ( ) { return "Connect completely"; }
	
return (FunctionBase) this;