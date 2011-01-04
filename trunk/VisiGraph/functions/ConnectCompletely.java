import java.awt.*;
import java.util.*;
import java.util.List;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		List selectedVertices = g.getSelectedVertices( );
		if( selectedVertices.isEmpty( ) )
			selectedVertices = g.vertices;
		
		List newEdges = new LinkedList( );
		for( int i = 0; i < selectedVertices.size( ); ++i )
		{
			int start = ( g.areDirectedEdgesAllowed ? 0 : i );
			for( int j = start; j < selectedVertices.size( ); ++j )
				if( ( i != j || g.areLoopsAllowed ) && g.getEdges( selectedVertices.get( i ), selectedVertices.get( j ) ).isEmpty( ) )
					newEdges.add( new Edge( g.areDirectedEdgesAllowed, selectedVertices.get( i ), selectedVertices.get( j ) ) );
		}
		
		g.suspendNotifications( true );
		g.edges.addAll( newEdges );
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
				return "Connects each pair of selected non-neighbors with at least one edge such that the selected vertices form a <i>clique</i>.";
			case Function.Attribute.INPUT:
				return "The selected vertices; if none are selected, the entire vertex set will be used.";
			case Function.Attribute.SIDE_EFFECTS:
				return "Adds an edge between each pair of selected non-neighbors (in both directions for directed graphs).  For pseudographs, also adds a loop to each selected vertex without one already.";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return false;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_GENERATORS:
				return new String[ ] { "Complete bipartite graph", "Complete graph", "Complete k-partite graph", "Complete tripartite graph" };
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Color connected components", "Color strongly connected components", "Color weakly connected components", "Count connected components", "Count strongly connected components", "Count weakly connected components" };
			case Function.Attribute.TAGS:
				return new String[ ] { "Connectivity", "Graph modifier" };
			default:
				return null;
		}
	}
	
	public boolean isApplicable( boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed )
	{
		return areCyclesAllowed;
	}
	
	public String toString( )
	{
		return "Connect completely";
	}
	
return (Function) this;
