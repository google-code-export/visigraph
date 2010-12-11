import java.awt.*;
import java.util.*;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2d, Graph g, Component owner )
	{
		if ( g.vertexes.size( ) < 1 )
			return "No vertices found!";
		
		// Get the two selected vertices		
		LinkedList selected = new LinkedList( );
		for ( Vertex v : g.vertexes )
			if ( v.isSelected.get( ) )
				selected.addLast( v );
		
		if ( selected.size( ) != 2 )
			return "Must select exactly two vertices!";
		
		// Then reset all vertex and edge colors to the default color (-1)
		for ( Vertex v : g.vertexes )
			v.color.set( -1 );
		
		for ( Edge e : g.edges )
			e.color.set( -1 );
		
		// Dijkstra's algorithm
		int distanceA = colorShortestDistanceBetween( g, selected.get( 0 ), selected.get( 1 ), 0 );
		
		if ( !g.areDirectedEdgesAllowed )
			return ( distanceA == Integer.MAX_VALUE ? "\u221E" : Integer.toString( distanceA ) );
		
		int distanceB = colorShortestDistanceBetween( g, selected.get( 1 ), selected.get( 0 ), ( distanceA == Integer.MAX_VALUE ? 0 : 1 ) );
		
		return "{" + ( distanceA == Integer.MAX_VALUE ? "\u221E" : Integer.toString( distanceA ) ) + ", " + ( distanceB == Integer.MAX_VALUE ? "\u221E" : Integer.toString( distanceB ) ) + "}";
	}
	
	private double colorShortestDistanceBetween( Graph g, Vertex from, Vertex to, int color )
	{
		HashMap distances = new HashMap( );
		
		LinkedList leaves = new LinkedList( );
		leaves.addLast( new Leaf( from, 0 ) );
		
		while ( !leaves.isEmpty( ) )
		{
			Leaf leaf = leaves.pop( );
			
			if ( !distances.containsKey( leaf.vertex ) || leaf.distance < distances.get( leaf.vertex ).distance )
			{
				distances.put( leaf.vertex, leaf );
				
				if ( leaf.vertex == to )
					break;
				
				for ( Edge edge : g.getEdges( leaf.vertex ) )
					if ( !edge.isLoop )
						leaves.addLast( new Leaf( ( edge.from == leaf.vertex ? edge.to : edge.from ), edge, leaf.distance + 1 ) );
			}
		}
		
		if ( !distances.containsKey( to ) )
			return Integer.MAX_VALUE;
		
		g.suspendNotifications( true );
		
		Leaf lastLeaf = distances.get( to );
		to.color.set( color );
		
		while ( lastLeaf.edge != null )
		{
			Vertex other = ( lastLeaf.vertex == lastLeaf.edge.from ? lastLeaf.edge.to : lastLeaf.edge.from );
			
			other.color.set( color );
			lastLeaf.edge.color.set( color );
			lastLeaf = distances.get( other );
		}
		
		g.suspendNotifications( false );
		
		return distances.get( to ).distance;
	}
	
	class Leaf
	{
		Vertex vertex;
		Edge edge;
		int distance;
		
		public Leaf( Vertex vertex, int distance )
		{
			this.vertex = vertex;
			this.distance = distance;
		}
		
		public Leaf( Vertex vertex, Edge edge, int distance )
		{
			this.vertex = vertex;
			this.edge = edge;
			this.distance = distance;
		}
	}
	
	public boolean allowsDynamicEvaluation( ) { return false;                                      }
	public boolean allowsOneTimeEvaluation( ) { return true;                                       }
	public String  toString               ( ) { return "Color shortest path between (unweighted)"; }
	
return (FunctionBase) this;
