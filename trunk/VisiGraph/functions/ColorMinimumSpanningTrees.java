import java.awt.*;
import java.util.*;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		if ( g.vertexes.isEmpty( ) )
			return "No vertices found!";
		
		if ( g.areDirectedEdgesAllowed )
			return "Cannot obtain for directed graphs!";
		
		g.suspendNotifications( true );
		
		// First, we reset all vertex and edge colors to the default color (-1)
		for ( Vertex v : g.vertexes )
			v.color.set( -1 );
		
		for ( Edge e : g.edges )
			e.color.set( -1 );
		
		// Initialize the list of vertices not yet visited
		HashSet unvisited = new HashSet( g.vertexes );
		
		// Initialize the queue of vertex-edge pairs about to be visited (sorted in ascending order by weight)
		PriorityQueue visiting = new PriorityQueue( );
		
		// Initialize the list of spanning tree weights
		ArrayList treeWeights = new ArrayList( );
		
		// Initialize the current tree's color
		int color = -1;
		
		// While there are still vertices to be visited by a minimum spanning tree, perform Prim's algorithm...
		while ( !unvisited.isEmpty( ) )
		{
			// Switch to the next color
			++color;
			
			// Set this minimum spanning tree's weight
			treeWeights.add( 0.0 );
			
			// Add an unvisited vertex to the queue with a null edge (s.t. weight = 0)
			NeighborPair root = new NeighborPair( );
			root.vertex = (Vertex) unvisited.iterator( ).next( );
			visiting.add( root );
			
			// While there are still connected vertices to be visited...
			while ( !visiting.isEmpty( ) )
			{
				// Remove the next-closest neighbor of the current minimum spanning tree from the queue
				NeighborPair pair = (NeighborPair) visiting.remove( );
				
				// If it hasn't already been visited...
				if ( unvisited.contains( pair.vertex ) )
				{
					// Mark it as visited
					unvisited.remove( pair.vertex );
					
					// Recolor it and the edge it came through
					pair.vertex.color.set( color );
					if ( pair.edge != null )
					{
						pair.edge.color.set( color );
						treeWeights.set( color, treeWeights.get( color ) + pair.edge.weight.get( ) );
					}
					
					// And add all its neighbors to the queue
					for ( Edge edge : g.getEdges( pair.vertex ) )
					{
						NeighborPair np = new NeighborPair();
						np.vertex = ( edge.from == pair.vertex ? edge.to : edge.from );
						np.edge = edge;
						visiting.add( np );
					}
				}
			}
		}
		
		g.suspendNotifications( false );
		
		// Build the string of total weights
		StringBuilder sb = new StringBuilder( );
		for( Double weight : treeWeights )
			sb.append( String.format( "%.5f", new Object[ ] { weight } ) ).append( ", " );
		
		// Return the number of minimum spanning trees found
		return ( color == 0 ? "1 tree" : ( color + 1 ) + " disjoint trees" ) + " found (" + sb.substring( 0, sb.length( ) - 2 ) + ")";
	}

	public boolean allowsDynamicEvaluation( ) { return false;                          }
	public boolean allowsOneTimeEvaluation( ) { return true;                           }
	public String  toString               ( ) { return "Color minimum spanning trees"; }
	
	class NeighborPair implements Comparable
	{
		Vertex vertex;
		Edge edge;
		
		public int compareTo( Object o )
		{
			return (int) Math.signum( ( edge == null ? 0 : edge.weight.get( ) ) - ( o.edge == null ? 0 : o.edge.weight.get( ) ) );
		}
	}
	
return (FunctionBase)this;
