import java.awt.*;
import java.util.*;
import java.util.List;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	class NeighborPair implements Comparable
	{
		Vertex	vertex;
		Edge	edge;
		
		public int compareTo( Object o )
		{
			return (int) Math.signum( ( this.edge == null ? 0 : this.edge.weight.get( ) ) - ( o.edge == null ? 0 : o.edge.weight.get( ) ) );
		}
	}
	
	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		if( g.vertices.isEmpty( ) )
			return "No vertices found!";
		
		// First, we reset all vertex and edge colors to uncolored (-1)
		g.suspendNotifications( true );
		
		for( Vertex vertex : g.vertices )
			vertex.color.set( -1 );
		
		for( Edge edge : g.edges )
			edge.color.set( -1 );
		
		g.suspendNotifications( false );
		
		// Initialize the list of vertices not yet visited
		Set unvisited = new HashSet( g.vertices );
		
		// Initialize the queue of vertex-edge pairs about to be visited (sorted in ascending order by weight)
		PriorityQueue visiting = new PriorityQueue( );
		
		// Initialize the list of spanning tree weights
		List treeWeights = new ArrayList( );
		
		// Initialize the current tree's color
		int color = -1;
		
		g.suspendNotifications( true );
		
		// While there are still vertices to be visited by a minimum spanning tree, perform Prim's algorithm...
		while( !unvisited.isEmpty( ) )
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
			while( !visiting.isEmpty( ) )
			{
				// Remove the next-closest neighbor of the current minimum spanning tree from the queue
				NeighborPair pair = (NeighborPair) visiting.remove( );
				
				// If it hasn't already been visited...
				if( unvisited.contains( pair.vertex ) )
				{
					// Mark it as visited
					unvisited.remove( pair.vertex );
					
					// Recolor it and the edge it came through
					pair.vertex.color.set( color );
					if( pair.edge != null )
					{
						pair.edge.color.set( color );
						treeWeights.set( color, treeWeights.get( color ) + pair.edge.weight.get( ) );
					}
					
					// And add all its neighbors to the queue
					for( Edge edge : g.getEdges( pair.vertex ) )
					{
						NeighborPair np = new NeighborPair( );
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
	
	public Object getAttribute( Function.Attribute attribute )
	{
		switch( attribute )
		{
			case Function.Attribute.AUTHOR:
				return "Cameron Behar";
			case Function.Attribute.VERSION:
				return "20110101";
			case Function.Attribute.DESCRIPTION:
				return "Colors the vertices and edges of a given graph according to their membership in their connected component's weighted minimum spanning tree and returns the total number of disjoint minimum spanning trees found as well as their weights.</p><p>Prim's algorithm is used to find all minimum spanning trees in <code><i>O</i>(|<i>E</i>| <i>log</i> |<i>V</i>|)</code> time.";
			case Function.Attribute.SIDE_EFFECTS:
				return "The color of each vertex and edge will be set to the color incrementally-chosen for its minimum spanning tree.  Edges not part of a minimum spanning tree will be reset to <code>uncolored</code> (i.e. <code>color = -1</code>).";
			case Function.Attribute.OUTPUT:
				return "The number of disjoint minimum spanning trees found and their individual weights.";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return false;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.TAGS:
				return new String[ ] { "Connectivity", "Coverings", "Graph evaluator", "Graph modifier", "Network flow", "Path finding", "Substructures" };
			default:
				return null;
		}
	}
	
	public boolean isApplicable( boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed )
	{
		return !areDirectedEdgesAllowed;
	}
	
	public String toString( )
	{
		return "Color minimum spanning trees";
	}
	
return (Function) this;
