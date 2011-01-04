import java.awt.*;
import java.util.*;
import java.util.List;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	class Leaf
	{
		Vertex	vertex;
		Edge	edge;
		int		distance;
		
		public Leaf( Vertex vertex, Edge edge, int distance )
		{
			this.vertex = vertex;
			this.edge = edge;
			this.distance = distance;
		}
		
		public Leaf( Vertex vertex, int distance )
		{
			this.vertex = vertex;
			this.distance = distance;
		}
	}
	
	private double colorShortestDistanceBetween( Graph g, Vertex from, Vertex to, int color )
	{
		Map distances = new HashMap( );
		
		LinkedList leaves = new LinkedList( );
		leaves.addLast( new Leaf( from, 0 ) );
		
		while( !leaves.isEmpty( ) )
		{
			Leaf leaf = leaves.pop( );
			
			if( !distances.containsKey( leaf.vertex ) || leaf.distance < distances.get( leaf.vertex ).distance )
			{
				distances.put( leaf.vertex, leaf );
				
				if( leaf.vertex == to )
					break;
				
				for( Edge edge : g.getEdges( leaf.vertex ) )
					if( !edge.isLoop )
						leaves.addLast( new Leaf( ( edge.from == leaf.vertex ? edge.to : edge.from ), edge, leaf.distance + 1 ) );
			}
		}
		
		if( !distances.containsKey( to ) )
			return Integer.MAX_VALUE;
		
		g.suspendNotifications( true );
		
		Leaf lastLeaf = distances.get( to );
		to.color.set( color );
		
		while( lastLeaf.edge != null )
		{
			Vertex other = ( lastLeaf.vertex == lastLeaf.edge.from ? lastLeaf.edge.to : lastLeaf.edge.from );
			
			other.color.set( color );
			lastLeaf.edge.color.set( color );
			lastLeaf = distances.get( other );
		}
		
		g.suspendNotifications( false );
		
		return distances.get( to ).distance;
	}
	
	public String evaluate( Graphics2D g2d, Graph g, Component owner )
	{
		if( g.vertices.isEmpty( ) )
			return "No vertices found!";
		
		// Get the two selected vertices
		List selectedVertices = g.getSelectedVertices( );
		
		if( selectedVertices.size( ) != 2 )
			return "Must select exactly two vertices!";
		
		// Then reset all vertex and edge colors to uncolored (-1)
		g.suspendNotifications( true );
		
		for( Vertex vertex : g.vertices )
			vertex.color.set( -1 );
		
		for( Edge edge : g.edges )
			edge.color.set( -1 );
		
		g.suspendNotifications( false );
		
		// Dijkstra's algorithm
		int distanceA = this.colorShortestDistanceBetween( g, selectedVertices.get( 0 ), selectedVertices.get( 1 ), 0 );
		
		if( !g.areDirectedEdgesAllowed )
			return ( distanceA == Integer.MAX_VALUE ? "\u221E" : Integer.toString( distanceA ) );
		
		int distanceB = this.colorShortestDistanceBetween( g, selectedVertices.get( 1 ), selectedVertices.get( 0 ), ( distanceA == Integer.MAX_VALUE ? 0 : 1 ) );
		
		return "{" + ( distanceA == Integer.MAX_VALUE ? "\u221E" : Integer.toString( distanceA ) ) + ", " + ( distanceB == Integer.MAX_VALUE ? "\u221E" : Integer.toString( distanceB ) ) + "}";
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
				return "Colors all vertices and edges along the geodesic of two selected vertices and returns the length thereof.</p><p>Using Dijkstra's algorithm, the shortest unweighted path is found in <code><i>O</i>((|<i>V</i>| + |<i>E</i>|) <i>log</i> |<i>V</i>|)</code> time.";
			case Function.Attribute.INPUT:
				return "The two selected vertices.";
			case Function.Attribute.SIDE_EFFECTS:
				return "All vertices and edges along the shortest path between the two selected vertices (ignoring edge weights) will have their colors set to <code>0</code> or <code>1</code>.  All other vertex and edge colors will be reset to <code>uncolored</code> (i.e. <code>-1</code>).  <i>Note: for directed graphs, this function will attempt to find paths in both directions.</i>";
			case Function.Attribute.OUTPUT:
				return "The length(s) of the shortest geodesic(s) between the two vertices.";
			case Function.Attribute.CONSTRAINTS:
				return new String[ ] { "Exactly two vertices must be selected." };
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return false;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Color shortest path between (weighted)" };
			case Function.Attribute.TAGS:
				return new String[ ] { "Connectivity", "Graph evaluator", "Network flow", "Path finding" };
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
		return "Color shortest path between (unweighted)";
	}
	
return (Function) this;
