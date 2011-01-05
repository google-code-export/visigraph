/**
 * GraphUtilities.java
 */
package edu.belmont.mth.visigraph.utilities;

import java.util.*;
import edu.belmont.mth.visigraph.models.*;

/**
 * @author Cameron Behar
 */
public class GraphUtilities
{
	private static class StronglyConnectedComponentsFinder
	{
		private int										index		= 0;
		private final Stack<Vertex>						stack		= new Stack<Vertex>( );
		private final Map<Vertex, Integer>				indices		= new HashMap<Vertex, Integer>( );
		private final Map<Vertex, Integer>				lowLinks	= new HashMap<Vertex, Integer>( );
		private final Map<Vertex, Boolean>				isOnStack	= new HashMap<Vertex, Boolean>( );
		private final Collection<Collection<Vertex>>	components	= new LinkedList<Collection<Vertex>>( );
		
		public Collection<Collection<Vertex>> find( Graph graph )
		{
			if( graph.vertices.isEmpty( ) )
				return this.components;
			
			for( Vertex vertex : graph.vertices )
				this.isOnStack.put( vertex, false );
			
			for( Vertex vertex : graph.vertices )
				if( !this.indices.containsKey( vertex ) )
					this.tarjansAlgorithm( vertex, graph );
			
			return this.components;
		}
		
		private void tarjansAlgorithm( Vertex from, Graph graph )
		{
			this.indices.put( from, this.index );
			this.lowLinks.put( from, this.index );
			++this.index;
			
			this.stack.push( from );
			this.isOnStack.put( from, true );
			
			for( Edge edge : graph.getEdgesFrom( from ) )
			{
				Vertex to = edge.to;
				
				if( !this.indices.containsKey( to ) )
				{
					this.tarjansAlgorithm( to, graph );
					this.lowLinks.put( from, Math.min( this.lowLinks.get( from ), this.lowLinks.get( to ) ) );
				}
				else if( this.isOnStack.get( to ) )
					this.lowLinks.put( from, Math.min( this.lowLinks.get( from ), this.indices.get( to ) ) );
			}
			
			if( this.lowLinks.get( from ).equals( this.indices.get( from ) ) )
			{
				Vertex to;
				List<Vertex> component = new LinkedList<Vertex>( );
				
				do
				{
					to = this.stack.pop( );
					this.isOnStack.put( to, false );
					component.add( to );
				} while( to != from );
				
				this.components.add( component );
			}
		}
	}
	
	private static class WeaklyConnectedComponentsFinder
	{
		private static class Node
		{
			public Node		parent	= null;
			public Vertex	vertex;
			public int		rank	= 0;
			
			public Node( Vertex vertex )
			{
				this.vertex = vertex;
			}
		}
		
		private final Random	random	= new Random( );
		
		public Collection<Collection<Vertex>> find( Graph graph )
		{
			if( graph.vertices.isEmpty( ) )
				return new LinkedList<Collection<Vertex>>( );
			
			Map<Vertex, Node> vertexNodes = new HashMap<Vertex, Node>( );
			
			for( Vertex vertex : graph.vertices )
				vertexNodes.put( vertex, new Node( vertex ) );
			
			for( Edge edge : graph.edges )
				if( !edge.isLoop )
					this.unionSets( vertexNodes.get( edge.from ), vertexNodes.get( edge.to ) );
			
			Map<Node, Collection<Vertex>> components = new HashMap<Node, Collection<Vertex>>( );
			for( Node node : vertexNodes.values( ) )
				if( node.parent == null )
					components.put( node, new LinkedList<Vertex>( ) );
			for( Node node : vertexNodes.values( ) )
				components.get( node.parent == null ? node : this.findParent( node.parent ) ).add( node.vertex );
			
			return components.values( );
		}
		
		private Node findParent( Node node )
		{
			Node root = node;
			
			while( root.parent != null )
				root = root.parent;
			
			Node nextNode;
			while( node.parent != null )
			{
				nextNode = node.parent;
				node.parent = root;
				node = nextNode;
			}
			
			return root;
		}
		
		private void unionSets( Node a, Node b )
		{
			if( a != null && b != null )
			{
				Node rootA = this.findParent( a );
				Node rootB = this.findParent( b );
				
				if( rootA != rootB )
					if( rootA.rank > rootB.rank || this.random.nextBoolean( ) )
						rootA.parent = rootB;
					else
						rootB.parent = rootA;
			}
		}
	}
	
	public static Collection<Collection<Vertex>> findStronglyConnectedComponents( Graph graph )
	{
		return ( graph.areDirectedEdgesAllowed ? new StronglyConnectedComponentsFinder( ).find( graph ) : new WeaklyConnectedComponentsFinder( ).find( graph ) );
	}
	
	public static Collection<Collection<Vertex>> findWeaklyConnectedComponents( Graph graph )
	{
		return new WeaklyConnectedComponentsFinder( ).find( graph );
	}
	
	public static double[ ][ ] getDistanceMatrix( Graph graph, boolean weighted )
	{
		double[ ][ ] distances = new double[graph.vertices.size( )][graph.vertices.size( )];
		
		// Initialize the distance matrix
		for( int i = 0; i < graph.vertices.size( ); ++i )
			for( int j = 0; j < graph.vertices.size( ); ++j )
				distances[i][j] = ( i == j ? 0.0 : Double.POSITIVE_INFINITY );
		
		// Add non-loop edges to the distance matrix
		Map<Vertex, Integer> indices = new HashMap<Vertex, Integer>( );
		for( int i = 0; i < graph.vertices.size( ); ++i )
			indices.put( graph.vertices.get( i ), i );
		
		for( Edge edge : graph.edges )
			if( !edge.isLoop )
			{
				int from = indices.get( edge.from ), to = indices.get( edge.to );
				
				if( weighted )
				{
					if( edge.weight.get( ) < distances[from][to] )
						distances[from][to] = edge.weight.get( );
					
					if( !edge.isDirected && edge.weight.get( ) < distances[to][from] )
						distances[to][from] = edge.weight.get( );
				}
				else
				{
					distances[from][to] = 1.0;
					
					if( !edge.isDirected )
						distances[to][from] = 1.0;
				}
			}
		
		// Run the Roy-Floyd-Warshall algorithm
		for( int k = 0; k < graph.vertices.size( ); ++k )
			for( int i = 0; i < graph.vertices.size( ); ++i )
				for( int j = 0; j < graph.vertices.size( ); ++j )
					distances[i][j] = Math.min( distances[i][j], distances[i][k] + distances[k][j] );
		
		return distances;
	}
}
