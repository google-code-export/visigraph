/**
 * GraphUtilities.java
 */
package edu.belmont.mth.visigraph.utilities;

import java.util.*;
import java.awt.geom.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;

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
	
	public static void alignHorizontally( Graph graph )
	{
		List<Vertex> selectedVertices = graph.getSelectedVertices( );
		if( selectedVertices.isEmpty( ) )
			if( graph.hasSelectedCaptions( ) || graph.hasSelectedEdges( ) )
				return;
			else
				selectedVertices = graph.vertices;
		
		double centerY = 0.0;
		for( Vertex vertex : selectedVertices )
			centerY += vertex.y.get( );
		
		centerY /= selectedVertices.size( );
		
		for( Vertex vertex : selectedVertices )
			vertex.y.set( centerY );
	}
	
	public static void alignVertically( Graph graph )
	{
		List<Vertex> selectedVertices = graph.getSelectedVertices( );
		if( selectedVertices.isEmpty( ) )
			if( graph.hasSelectedCaptions( ) || graph.hasSelectedEdges( ) )
				return;
			else
				selectedVertices = graph.vertices;
		
		double centerX = 0.0;
		for( Vertex vertex : selectedVertices )
			centerX += vertex.x.get( );
		
		centerX /= selectedVertices.size( );
		
		for( Vertex vertex : selectedVertices )
			vertex.x.set( centerX );
	}
	
	public static void arrangeCircle( Graph graph )
	{
		List<Vertex> selectedVertices = graph.getSelectedVertices( );
		if( selectedVertices.isEmpty( ) )
			if( graph.hasSelectedCaptions( ) || graph.hasSelectedEdges( ) )
				return;
			else
				selectedVertices = graph.vertices;
		
		Point2D.Double centroid = new Point2D.Double( 0.0, 0.0 );
		for( Vertex vertex : selectedVertices )
		{
			centroid.x += vertex.x.get( );
			centroid.y += vertex.y.get( );
		}
		
		centroid.x /= selectedVertices.size( );
		centroid.y /= selectedVertices.size( );
		
		double radius = UserSettings.instance.arrangeCircleRadiusMultiplier.get( ) * selectedVertices.size( );
		double degreesPerVertex = 2.0 * Math.PI / selectedVertices.size( );
		
		for( int i = 0; i < selectedVertices.size( ); ++i )
		{
			selectedVertices.get( i ).x.set( radius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ) + centroid.x );
			selectedVertices.get( i ).y.set( radius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) + centroid.y );
		}
	}
	
	public static void arrangeGrid( Graph graph )
	{
		List<Vertex> selectedVertices = graph.getSelectedVertices( );
		if( selectedVertices.isEmpty( ) )
			if( graph.hasSelectedCaptions( ) || graph.hasSelectedEdges( ) )
				return;
			else
				selectedVertices = graph.vertices;
		
		int n = selectedVertices.size( );
		int rows = (int) Math.round( Math.sqrt( n ) );
		int columns = (int) Math.ceil( n / (double) rows );
		
		double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
		for( Vertex vertex : selectedVertices )
		{
			if( vertex.x.get( ) < minX )
				minX = vertex.x.get( );
			else if( vertex.x.get( ) > maxX )
				maxX = vertex.x.get( );
			
			if( vertex.y.get( ) < minY )
				minY = vertex.y.get( );
			else if( vertex.y.get( ) > maxY )
				maxY = vertex.y.get( );
		}
		
		Point2D.Double center = new Point2D.Double( ( minX + maxX ) / 2.0, ( minY + maxY ) / 2.0 );
		Point2D.Double offset = new Point2D.Double( center.x - ( ( columns - 1 ) * UserSettings.instance.arrangeGridSpacing.get( ) ) / 2.0, center.y - ( ( rows - 1 ) * UserSettings.instance.arrangeGridSpacing.get( ) ) / 2.0 );
		
		for( int row = 0; row < rows; ++row )
			for( int col = 0; ( row < rows - 1 && col < columns ) || ( row == rows - 1 && col < ( n % columns == 0 ? columns : n % columns ) ); ++col )
			{
				selectedVertices.get( row * columns + col ).x.set( UserSettings.instance.arrangeGridSpacing.get( ) * col + offset.x );
				selectedVertices.get( row * columns + col ).y.set( UserSettings.instance.arrangeGridSpacing.get( ) * row + offset.y );
			}
	}
	
	public static double arrangeTensors( Graph graph, double speed, Map<Vertex, Point2D> velocities )
	{
		List<Vertex> selectedVertices = graph.getSelectedVertices( );
		if( selectedVertices.isEmpty( ) )
			if( graph.hasSelectedCaptions( ) || graph.hasSelectedEdges( ) )
				return 0.0;
			else
				selectedVertices = graph.vertices;
		
		double totalKineticEnergy = 0;
		
		// Initialize the map of net forces
		Map<Vertex, Point2D.Double> forces = new HashMap<Vertex, Point2D.Double>( );
		for( Vertex vertex : selectedVertices )
			forces.put( vertex, new Point2D.Double( 0.0, 0.0 ) );
		
		// Apply all repulsive forces
		for( int i = 0; i < selectedVertices.size( ); ++i )
			for( int j = i + 1; j < selectedVertices.size( ); ++j )
			{
				double xDiff = selectedVertices.get( j ).x.get( ) - selectedVertices.get( i ).x.get( ), yDiff = selectedVertices.get( j ).y.get( ) - selectedVertices.get( i ).y.get( );
				double force = 8987551787.0 * ( ( UserSettings.instance.autoArrangeRepulsiveForce.get( ) * selectedVertices.get( i ).weight.get( ) ) * ( UserSettings.instance.autoArrangeRepulsiveForce.get( ) * selectedVertices.get( j ).weight.get( ) ) / Math.max( 30.0, xDiff * xDiff + yDiff * yDiff ) );
				double angle = Math.atan2( yDiff, xDiff );
				double xForce = -force * Math.cos( angle ), yForce = -force * Math.sin( angle );
				
				forces.get( selectedVertices.get( i ) ).x += xForce;
				forces.get( selectedVertices.get( i ) ).y += yForce;
				forces.get( selectedVertices.get( j ) ).x -= xForce;
				forces.get( selectedVertices.get( j ) ).y -= yForce;
			}
		
		// Apply all attractive forces
		for( Edge edge : graph.edges )
			if( !edge.isLoop && ( selectedVertices == graph.vertices || edge.from.isSelected.get( ) || edge.to.isSelected.get( ) ) )
			{
				double xDiff = edge.from.x.get( ) - edge.to.x.get( );
				double yDiff = edge.from.y.get( ) - edge.to.y.get( );
				double distance = Math.max( 30.0, Math.sqrt( xDiff * xDiff + yDiff * yDiff ) );
				double force = UserSettings.instance.autoArrangeAttractiveForce.get( ) * ( distance - edge.weight.get( ) * 10.0 );
				double angle = Math.atan2( yDiff, xDiff );
				double xForce = force * Math.cos( angle ), yForce = force * Math.sin( angle );
				
				if( selectedVertices == graph.vertices || edge.from.isSelected.get( ) )
				{
					forces.get( edge.from ).x -= xForce;
					forces.get( edge.from ).y -= yForce;
				}
				
				if( selectedVertices == graph.vertices || edge.to.isSelected.get( ) )
				{
					forces.get( edge.to ).x += xForce;
					forces.get( edge.to ).y += yForce;
				}
			}
		
		// Apply all net forces
		for( Vertex vertex : selectedVertices )
		{
			if( !velocities.containsKey( vertex ) )
				velocities.put( vertex, new Point2D.Double( 0, 0 ) );
			
			Point2D velocity = velocities.get( vertex );
			velocity.setLocation( ( velocity.getX( ) + forces.get( vertex ).x ) * 0.85, ( velocity.getY( ) + forces.get( vertex ).y ) * 0.85 );
			totalKineticEnergy += vertex.weight.get( ) * velocity.distanceSq( 0, 0 );
		}
		
		// Apply all velocities
		for( Vertex vertex : selectedVertices )
		{
			vertex.x.set( vertex.x.get( ) + speed * velocities.get( vertex ).getX( ) );
			vertex.y.set( vertex.y.get( ) + speed * velocities.get( vertex ).getY( ) );
		}
		
		return totalKineticEnergy;
	}
	
	public static void arrangeTree( Graph graph )
	{
		List<Vertex> allNodes = new ArrayList<Vertex>( );
		List<List<Vertex>> levels = new ArrayList<List<Vertex>>( );
		
		// First we need to add all selected vertices to a root level of the tree
		levels.add( new ArrayList<Vertex>( ) );
		for( Vertex vertex : graph.getSelectedVertices( ) )
		{
			levels.get( 0 ).add( vertex );
			allNodes.add( vertex );
		}
		
		// While the last level has vertices, add all their neighbors to the next level that haven't yet been otherwise added
		while( levels.get( levels.size( ) - 1 ).size( ) > 0 )
		{
			levels.add( new Vector<Vertex>( ) );
			
			for( Vertex vertex : levels.get( levels.size( ) - 2 ) )
				for( Vertex neighbor : graph.getNeighbors( vertex ) )
					if( !allNodes.contains( neighbor ) )
					{
						levels.get( levels.size( ) - 1 ).add( neighbor );
						allNodes.add( neighbor );
					}
		}
		
		// If there were any nodes that weren't added yet, give them their own level
		if( allNodes.size( ) < graph.vertices.size( ) )
			for( Vertex vertex : levels.get( levels.size( ) - 1 ) )
				if( !allNodes.contains( vertex ) )
				{
					levels.get( levels.size( ) - 1 ).add( vertex );
					allNodes.add( vertex );
				}
		
		// If the last level is empty, remove it
		if( levels.get( levels.size( ) - 1 ).size( ) == 0 )
			levels.remove( levels.size( ) - 1 );
		
		// Now for the layout!
		double y = 0.0;
		double largestWidth = 0.0;
		for( List<Vertex> level : levels )
			largestWidth = Math.max( largestWidth, level.size( ) * 150.0 );
		
		for( int row = 0; row < levels.size( ); ++row )
		{
			List<Vertex> level = levels.get( row );
			y += 150.0;
			double colSpace = largestWidth / ( level.size( ) );
			
			for( int col = 0; col < level.size( ); ++col )
			{
				Vertex vertex = level.get( col );
				double x = ( col + 0.5 ) * colSpace - largestWidth / 2.0;
				
				vertex.x.set( x );
				vertex.y.set( y );
			}
		}
	}
	
	public static void distributeHorizontally( Graph graph )
	{
		List<Vertex> selectedVertices = graph.getSelectedVertices( );
		if( selectedVertices.isEmpty( ) )
			if( graph.hasSelectedCaptions( ) || graph.hasSelectedEdges( ) )
				return;
			else
				selectedVertices = graph.vertices;
		
		Collections.sort( selectedVertices, new Comparator<Vertex>( )
		{
			public int compare( Vertex v1, Vertex v2 )
			{
				return (int) Math.round( Math.signum( v1.x.get( ) - v2.x.get( ) ) );
			}
		} );
		
		double spacing = ( selectedVertices.get( selectedVertices.size( ) - 1 ).x.get( ) - selectedVertices.get( 0 ).x.get( ) ) / ( selectedVertices.size( ) - 1 );
		double currentX = selectedVertices.get( 0 ).x.get( ) - spacing;
		
		for( Vertex vertex : selectedVertices )
			vertex.x.set( currentX += spacing );
	}
	
	public static void distributeVertically( Graph graph )
	{
		List<Vertex> selectedVertices = graph.getSelectedVertices( );
		if( selectedVertices.isEmpty( ) )
			if( graph.hasSelectedCaptions( ) || graph.hasSelectedEdges( ) )
				return;
			else
				selectedVertices = graph.vertices;
		
		Collections.sort( selectedVertices, new Comparator<Vertex>( )
		{
			public int compare( Vertex v1, Vertex v2 )
			{
				return (int) Math.round( Math.signum( v1.y.get( ) - v2.y.get( ) ) );
			}
		} );
		
		double spacing = ( selectedVertices.get( selectedVertices.size( ) - 1 ).y.get( ) - selectedVertices.get( 0 ).y.get( ) ) / ( selectedVertices.size( ) - 1 );
		double currentY = selectedVertices.get( 0 ).y.get( ) - spacing;
		
		for( Vertex vertex : selectedVertices )
			vertex.y.set( currentY += spacing );
	}
	
	public static Collection<Collection<Vertex>> findStronglyConnectedComponents( Graph graph )
	{
		return ( graph.areDirectedEdgesAllowed ? new StronglyConnectedComponentsFinder( ).find( graph ) : new WeaklyConnectedComponentsFinder( ).find( graph ) );
	}
	
	public static Collection<Collection<Vertex>> findWeaklyConnectedComponents( Graph graph )
	{
		return new WeaklyConnectedComponentsFinder( ).find( graph );
	}
	
	public static void flipHorizontally( Graph graph )
	{
		List<Vertex> selectedVertices = graph.getSelectedVertices( );
		List<Edge> selectedEdges = graph.getSelectedEdges( );
		
		if( selectedVertices.isEmpty( ) )
		{
			if( !selectedEdges.isEmpty( ) || graph.hasSelectedCaptions( ) )
				return;
			
			selectedVertices = graph.vertices;
			selectedEdges = graph.edges;
		}
		
		double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
		for( Vertex vertex : selectedVertices )
			if( vertex.x.get( ) < minX )
				minX = vertex.x.get( );
			else if( vertex.x.get( ) > maxX )
				maxX = vertex.x.get( );
		
		for( Edge edge : selectedEdges )
			if( edge.handleX.get( ) < minX )
				minX = edge.handleX.get( );
			else if( edge.handleX.get( ) > maxX )
				maxX = edge.handleX.get( );
		
		double centerX = ( minX + maxX ) / 2.0;
		
		if( selectedEdges.isEmpty( ) )
			for( Edge edge : graph.edges )
				if( edge.from.isSelected.get( ) && edge.to.isSelected.get( ) )
					selectedEdges.add( edge );
		
		for( Edge edge : selectedEdges )
			edge.suspendNotifications( true );
		
		for( Vertex vertex : selectedVertices )
			vertex.x.set( 2.0 * centerX - vertex.x.get( ) );
		
		for( Edge edge : selectedEdges )
		{
			edge.handleX.set( 2.0 * centerX - edge.handleX.get( ) );
			edge.suspendNotifications( false );
			edge.refresh( );
		}
	}
	
	public static void flipVertically( Graph graph )
	{
		List<Vertex> selectedVertices = graph.getSelectedVertices( );
		List<Edge> selectedEdges = graph.getSelectedEdges( );
		
		if( selectedVertices.isEmpty( ) )
		{
			if( !selectedEdges.isEmpty( ) || graph.hasSelectedCaptions( ) )
				return;
			
			selectedVertices = graph.vertices;
			selectedEdges = graph.edges;
		}
		
		double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
		for( Vertex vertex : selectedVertices )
			if( vertex.y.get( ) < minY )
				minY = vertex.y.get( );
			else if( vertex.y.get( ) > maxY )
				maxY = vertex.y.get( );
		
		for( Edge edge : selectedEdges )
			if( edge.handleY.get( ) < minY )
				minY = edge.handleY.get( );
			else if( edge.handleY.get( ) > maxY )
				maxY = edge.handleY.get( );
		
		double centerY = ( minY + maxY ) / 2.0;
		
		if( selectedEdges.isEmpty( ) )
			for( Edge edge : graph.edges )
				if( edge.from.isSelected.get( ) && edge.to.isSelected.get( ) )
					selectedEdges.add( edge );
		
		for( Edge edge : selectedEdges )
			edge.suspendNotifications( true );
		
		for( Vertex vertex : selectedVertices )
			vertex.y.set( 2.0 * centerY - vertex.y.get( ) );
		
		for( Edge edge : selectedEdges )
		{
			edge.handleY.set( 2.0 * centerY - edge.handleY.get( ) );
			edge.suspendNotifications( false );
			edge.refresh( );
		}
	}
	
	public static void rotateLeft90( Graph graph )
	{
		List<Vertex> selectedVertices = graph.getSelectedVertices( );
		List<Edge> selectedEdges = graph.getSelectedEdges( );
		
		if( selectedVertices.isEmpty( ) )
		{
			if( !selectedEdges.isEmpty( ) || graph.hasSelectedCaptions( ) )
				return;
			
			selectedVertices = graph.vertices;
		}
		
		// Find the center
		double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
		for( Vertex vertex : selectedVertices )
		{
			if( vertex.x.get( ) < minX )
				minX = vertex.x.get( );
			else if( vertex.x.get( ) > maxX )
				maxX = vertex.x.get( );
			
			if( vertex.y.get( ) < minY )
				minY = vertex.y.get( );
			else if( vertex.y.get( ) > maxY )
				maxY = vertex.y.get( );
		}
		
		for( Edge edge : selectedEdges )
		{
			if( edge.handleX.get( ) < minX )
				minX = edge.handleX.get( );
			else if( edge.handleX.get( ) > maxX )
				maxX = edge.handleX.get( );
			
			if( edge.handleY.get( ) < minY )
				minY = edge.handleY.get( );
			else if( edge.handleY.get( ) > maxY )
				maxY = edge.handleY.get( );
		}
		
		Point2D.Double center = new Point2D.Double( ( minX + maxX ) / 2.0, ( minY + maxY ) / 2.0 );
		
		// Perform the rotation
		for( Edge edge : selectedEdges )
			edge.suspendNotifications( true );
		
		for( Vertex vertex : selectedVertices )
		{
			double oldVertexX = vertex.x.get( );
			vertex.x.set( center.x - ( center.y - vertex.y.get( ) ) );
			vertex.y.set( center.y + ( center.x - oldVertexX ) );
		}
		
		for( Edge edge : selectedEdges )
		{
			double oldEdgeHandleX = edge.handleX.get( );
			edge.handleX.set( center.x - ( center.y - edge.handleY.get( ) ) );
			edge.handleY.set( center.y + ( center.x - oldEdgeHandleX ) );
			edge.suspendNotifications( false );
			edge.refresh( );
		}
	}
	
	public static void rotateRight90( Graph graph )
	{
		List<Vertex> selectedVertices = graph.getSelectedVertices( );
		List<Edge> selectedEdges = graph.getSelectedEdges( );
		
		if( selectedVertices.isEmpty( ) )
		{
			if( !selectedEdges.isEmpty( ) || graph.hasSelectedCaptions( ) )
				return;
			
			selectedVertices = graph.vertices;
		}
		
		// Find the center
		double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
		for( Vertex vertex : selectedVertices )
		{
			if( vertex.x.get( ) < minX )
				minX = vertex.x.get( );
			else if( vertex.x.get( ) > maxX )
				maxX = vertex.x.get( );
			
			if( vertex.y.get( ) < minY )
				minY = vertex.y.get( );
			else if( vertex.y.get( ) > maxY )
				maxY = vertex.y.get( );
		}
		
		for( Edge edge : selectedEdges )
		{
			if( edge.handleX.get( ) < minX )
				minX = edge.handleX.get( );
			else if( edge.handleX.get( ) > maxX )
				maxX = edge.handleX.get( );
			
			if( edge.handleY.get( ) < minY )
				minY = edge.handleY.get( );
			else if( edge.handleY.get( ) > maxY )
				maxY = edge.handleY.get( );
		}
		
		Point2D.Double center = new Point2D.Double( ( minX + maxX ) / 2.0, ( minY + maxY ) / 2.0 );
		
		// Perform the rotation
		for( Edge edge : selectedEdges )
			edge.suspendNotifications( true );
		
		for( Vertex vertex : selectedVertices )
		{
			double oldVertexX = vertex.x.get( );
			vertex.x.set( center.x + ( center.y - vertex.y.get( ) ) );
			vertex.y.set( center.y - ( center.x - oldVertexX ) );
		}
		
		for( Edge edge : selectedEdges )
		{
			double oldEdgeHandleX = edge.handleX.get( );
			edge.handleX.set( center.x + ( center.y - edge.handleY.get( ) ) );
			edge.handleY.set( center.y - ( center.x - oldEdgeHandleX ) );
			edge.suspendNotifications( false );
			edge.refresh( );
		}
	}
	
	public static void scale( Graph graph, double factor )
	{
		List<Vertex> selectedVertices = graph.getSelectedVertices( );
		if( selectedVertices.isEmpty( ) )
			if( graph.hasSelectedCaptions( ) || graph.hasSelectedEdges( ) )
				return;
			else
				selectedVertices = graph.vertices;
		
		Point2D.Double centroid = new Point2D.Double( );
		for( Vertex vertex : selectedVertices )
		{
			centroid.x += vertex.x.get( );
			centroid.y += vertex.y.get( );
		}
		
		centroid.x /= selectedVertices.size( );
		centroid.y /= selectedVertices.size( );
		
		for( Vertex vertex : selectedVertices )
		{
			vertex.x.set( factor * ( vertex.x.get( ) - centroid.x ) + centroid.x );
			vertex.y.set( factor * ( vertex.y.get( ) - centroid.y ) + centroid.y );
		}
	}
}
