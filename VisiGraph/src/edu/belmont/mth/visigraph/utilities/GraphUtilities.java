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
 * 
 */
public class GraphUtilities
{
	// Layout methods
	
	public static void arrangeCircle( Graph graph )
	{
		ArrayList<Vertex> selected = new ArrayList<Vertex>( );
		
		for ( Vertex vertex : graph.vertexes )
			if ( vertex.isSelected.get( ) )
				selected.add( vertex );
		
		if ( selected.size( ) < 1 )
			selected.addAll( graph.vertexes );
		
		double centroidX = 0, centroidY = 0;
		for ( Vertex vertex : selected )
		{
			centroidX += vertex.x.get( );
			centroidY += vertex.y.get( );
		}
		
		centroidX /= selected.size( );
		centroidY /= selected.size( );
		
		double radius = UserSettings.instance.arrangeCircleRadiusMultiplier.get( ) * selected.size( );
		double degreesPerVertex = 2 * Math.PI / selected.size( );
		
		for ( int i = 0; i < selected.size( ); ++i )
		{
			selected.get( i ).x.set( radius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ) + centroidX );
			selected.get( i ).y.set( radius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) + centroidY );
		}
	}
	
	public static void arrangeGrid( Graph graph )
	{
		ArrayList<Vertex> selected = new ArrayList<Vertex>( );
		
		for ( Vertex vertex : graph.vertexes )
			if ( vertex.isSelected.get( ) )
				selected.add( vertex );
		
		if ( selected.size( ) < 1 )
			selected.addAll( graph.vertexes );
		
		double centroidX = 0, centroidY = 0;
		for ( Vertex vertex : selected )
		{
			centroidX += vertex.x.get( );
			centroidY += vertex.y.get( );
		}
		
		centroidX /= selected.size( );
		centroidY /= selected.size( );
		
		int n = selected.size( );
		int rows = (int) Math.round( Math.sqrt( n ) );
		int columns = (int) Math.ceil( n / (double) rows );
		Point2D.Double location = new Point2D.Double( ( columns / 2.0 ) * -UserSettings.instance.arrangeGridSpacing.get( ), ( rows / 2.0 ) * -UserSettings.instance.arrangeGridSpacing.get( ) );
		
		for ( int row = 0; row < rows; ++row )
			for ( int col = 0; ( row < rows - 1 && col < columns ) || ( row == rows - 1 && col < ( n % columns == 0 ? columns : n % columns ) ); ++col )
			{
				selected.get( row * columns + col ).x.set( location.x + UserSettings.instance.arrangeGridSpacing.get( ) * col + centroidX );
				selected.get( row * columns + col ).y.set( location.y + UserSettings.instance.arrangeGridSpacing.get( ) * row + centroidY );
			}
	}
	
	public static void arrangeTree( Graph graph )
	{
		Vector<Vertex> allNodes = new Vector<Vertex>( );
		Vector<Vector<Vertex>> levels = new Vector<Vector<Vertex>>( );
		
		// First we need to add all selected vertexes to a root level of the tree
		levels.add( new Vector<Vertex>( ) );
		for ( Vertex vertex : graph.vertexes )
			if ( vertex.isSelected.get( ) )
			{
				levels.get( 0 ).add( vertex );
				allNodes.add( vertex );
			}
		
		// While the last level has vertexes, add all their neighbors to the next level that haven't yet been otherwise added
		while ( levels.lastElement( ).size( ) > 0 )
		{
			levels.add( new Vector<Vertex>( ) );
			
			for ( Vertex vertex : levels.get( levels.size( ) - 2 ) )
				for ( Vertex neighbor : graph.getNeighbors( vertex ) )
					if ( !allNodes.contains( neighbor ) )
					{
						levels.lastElement( ).add( neighbor );
						allNodes.add( neighbor );
					}
		}
		
		// If there were any nodes that weren't added yet, give them their own level
		if ( allNodes.size( ) < graph.vertexes.size( ) )
			for ( Vertex vertex : levels.get( levels.size( ) - 1 ) )
				if ( !allNodes.contains( vertex ) )
				{
					levels.lastElement( ).add( vertex );
					allNodes.add( vertex );
				}
		
		// If the last level is empty, remove it
		if ( levels.lastElement( ).size( ) == 0 )
			levels.remove( levels.size( ) - 1 );
		
		// Now for the layout!
		double y = 0.0;
		double largestWidth = 0;
		for ( Vector<Vertex> level : levels )
			largestWidth = Math.max( largestWidth, level.size( ) * 150.0 );
		
		for ( int row = 0; row < levels.size( ); ++row )
		{
			Vector<Vertex> level = levels.get( row );
			y += 150;
			double colSpace = largestWidth / ( level.size( ) );
			
			for ( int col = 0; col < level.size( ); ++col )
			{
				Vertex vertex = level.get( col );
				double x = ( col + .5 ) * colSpace - largestWidth / 2.0;
				
				vertex.x.set( x );
				vertex.y.set( y );
			}
		}
	}
	
	private static HashMap<Vertex, Point2D.Double> velocities = new HashMap<Vertex, Point2D.Double>( );
	
	public static boolean arrangeTensors( Graph graph, double speed )
	{
		ArrayList<Vertex> selected = new ArrayList<Vertex>( );
		
		for ( Vertex vertex : graph.vertexes )
			if ( vertex.isSelected.get( ) )
				selected.add( vertex );
		
		if ( selected.size( ) == 0 )
			selected.addAll( graph.vertexes );
		
		double charge = 0.0005;
		double spring = 0.01;
		double damping = 0.85;
		
		double totalKineticEnergy = 0;
		
		for ( Vertex v : selected )
		{
			Point2D.Double netForce = new Point2D.Double( 0, 0 );
			
			for ( Vertex w : selected )
				if ( v != w )
				{
					double xDiff = w.x.get( ) - v.x.get( );
					double yDiff = w.y.get( ) - v.y.get( );
					double distanceSquared = ( xDiff * xDiff + yDiff * yDiff );
					double force = 8987551787.0 * ( ( charge * v.weight.get( ) ) * ( charge * w.weight.get( ) ) / distanceSquared );
					double angle = Math.atan2( yDiff, xDiff );
					
					netForce.setLocation( netForce.x - force * Math.cos( angle ), netForce.y - force * Math.sin( angle ) );
				}
			
			for ( Edge e : graph.getEdges( v ) )
				if ( !e.isLoop )
				{
					double xDiff = e.from.x.get( ) - e.to.x.get( );
					double yDiff = e.from.y.get( ) - e.to.y.get( );
					double distance = Math.sqrt( xDiff * xDiff + yDiff * yDiff );
					double force = spring * ( distance /*- e.weight.get( ) * 100*/);
					double angle = Math.atan2( yDiff, xDiff );
					
					if ( e.from == v )
						force = -force;
					
					netForce.setLocation( netForce.x + force * Math.cos( angle ), netForce.y + force * Math.sin( angle ) );
				}
			
			if ( !velocities.containsKey( v ) )
				velocities.put( v, new Point2D.Double( 0, 0 ) );
			
			Point2D.Double velocity = velocities.get( v );
			velocity.setLocation( ( velocity.x + netForce.x ) * damping, ( velocity.y + netForce.y ) * damping );
			totalKineticEnergy += v.weight.get( ) * velocity.distanceSq( 0, 0 );
		}
		
		/*
		 * // Initialize the hashmap of forces
		 * for ( Vertex v : selected )
		 * forces.put( v, new Point2D.Double( 0, 0 ) );
		 * 
		 * // Calculate all repulsive forces
		 * for ( int i = 0; i < selected.size( ); ++i )
		 * for ( int j = i + 1; j < selected.size( ); ++j )
		 * {
		 * Vertex v0 = selected.get( i );
		 * Vertex v1 = selected.get( j );
		 * 
		 * double xDiff = v1.x.get( ) - v0.x.get( );
		 * double yDiff = v1.y.get( ) - v0.y.get( );
		 * double distanceSquared = ( xDiff * xDiff + yDiff * yDiff );
		 * double xForce = UserSettings.instance.autoArrangeRepulsiveForce.get( ) * ( xDiff / distanceSquared );
		 * double yForce = UserSettings.instance.autoArrangeRepulsiveForce.get( ) * ( yDiff / distanceSquared );
		 * 
		 * Point2D.Double force0 = forces.get( v0 );
		 * force0.x += xForce; force0.y += yForce;
		 * 
		 * // And because every action has an opposite and equal reaction
		 * Point2D.Double force1 = forces.get( v1 );
		 * force1.x -= xForce; force1.y -= yForce;
		 * }
		 * 
		 * // Calculate all attractive forces
		 * for ( Edge edge : graph.edges )
		 * if ( edge.from != edge.to && ( noneWereSelected || edge.from.isSelected.get( ) || edge.to.isSelected.get( ) ) )
		 * {
		 * double xDiff = edge.to.x.get( ) - edge.from.x.get( );
		 * double yDiff = edge.to.y.get( ) - edge.from.y.get( );
		 * double distanceSquared = ( xDiff * xDiff + yDiff * yDiff );
		 * double xForce = UserSettings.instance.autoArrangeAttractiveForce.get( ) * xDiff * distanceSquared;
		 * double yForce = UserSettings.instance.autoArrangeAttractiveForce.get( ) * yDiff * distanceSquared;
		 * 
		 * if ( noneWereSelected || edge.from.isSelected.get( ) )
		 * {
		 * Point2D.Double force = forces.get( edge.from );
		 * force.x += xForce; force.y += yForce;
		 * }
		 * 
		 * // And because every action has an opposite and equal reaction
		 * if ( noneWereSelected || edge.to.isSelected.get( ) )
		 * {
		 * Point2D.Double force = forces.get( edge.to );
		 * force.x -= xForce; force.y -= yForce;
		 * }
		 * }
		 */

		// Apply all net forces
		for ( Vertex v : selected )
		{
			v.x.set( v.x.get( ) + speed * velocities.get( v ).x );
			v.y.set( v.y.get( ) + speed * velocities.get( v ).y );
		}
		
		return totalKineticEnergy >= 0.01;
	}
	
	public static void alignHorizontally( Graph graph )
	{
		double centerY = 0.0;
		List<Vertex> selectedVertexes = new ArrayList<Vertex>( );
		
		for ( Vertex v : graph.vertexes )
			if ( v.isSelected.get( ) )
				selectedVertexes.add( v );
		
		if ( selectedVertexes.size( ) < 1 )
		{
			for ( Edge e : graph.edges )
				if ( e.isSelected.get( ) )
					return;
			
			for ( Caption c : graph.captions )
				if ( c.isSelected.get( ) )
					return;
			
			selectedVertexes.addAll( graph.vertexes );
		}
		
		for ( Vertex v : selectedVertexes )
			centerY += v.y.get( );
		
		centerY /= selectedVertexes.size( );
		
		for ( Vertex v : selectedVertexes )
			v.y.set( centerY );
	}
	
	public static void alignVertically( Graph graph )
	{
		double centerX = 0.0;
		List<Vertex> selectedVertexes = new ArrayList<Vertex>( );
		
		for ( Vertex v : graph.vertexes )
			if ( v.isSelected.get( ) )
				selectedVertexes.add( v );
		
		if ( selectedVertexes.size( ) < 1 )
		{
			for ( Edge e : graph.edges )
				if ( e.isSelected.get( ) )
					return;
			
			for ( Caption c : graph.captions )
				if ( c.isSelected.get( ) )
					return;
			
			selectedVertexes.addAll( graph.vertexes );
		}
		
		for ( Vertex v : selectedVertexes )
			centerX += v.x.get( );
		
		centerX /= selectedVertexes.size( );
		
		for ( Vertex v : selectedVertexes )
			v.x.set( centerX );
	}
	
	public static void distributeHorizontally( Graph graph )
	{
		List<Vertex> selectedVertexes = new ArrayList<Vertex>( );
		
		for ( Vertex v : graph.vertexes )
			if ( v.isSelected.get( ) )
				selectedVertexes.add( v );
		
		if ( selectedVertexes.size( ) < 1 )
		{
			for ( Edge e : graph.edges )
				if ( e.isSelected.get( ) )
					return;
			
			for ( Caption c : graph.captions )
				if ( c.isSelected.get( ) )
					return;
			
			selectedVertexes.addAll( graph.vertexes );
		}
		
		Collections.sort( selectedVertexes, new Comparator<Vertex>( )
		{
			public int compare( Vertex v1, Vertex v2 )
			{
				return new Double( Math.signum( v1.x.get( ) - v2.x.get( ) ) ).intValue( );
			}
		} );
		
		double spacing = ( selectedVertexes.get( selectedVertexes.size( ) - 1 ).x.get( ) - selectedVertexes.get( 0 ).x.get( ) ) / (double) ( selectedVertexes.size( ) - 1 );
		double currentX = selectedVertexes.get( 0 ).x.get( ) - spacing;
		
		for ( Vertex vertex : selectedVertexes )
			vertex.x.set( currentX += spacing );
	}
	
	public static void distributeVertically( Graph graph )
	{
		List<Vertex> selectedVertexes = new ArrayList<Vertex>( );
		
		for ( Vertex v : graph.vertexes )
			if ( v.isSelected.get( ) )
				selectedVertexes.add( v );
		
		if ( selectedVertexes.size( ) < 1 )
		{
			for ( Edge e : graph.edges )
				if ( e.isSelected.get( ) )
					return;
			
			for ( Caption c : graph.captions )
				if ( c.isSelected.get( ) )
					return;
			
			selectedVertexes.addAll( graph.vertexes );
		}
		
		Collections.sort( selectedVertexes, new Comparator<Vertex>( )
		{
			public int compare( Vertex v1, Vertex v2 )
			{
				return new Double( Math.signum( v1.y.get( ) - v2.y.get( ) ) ).intValue( );
			}
		} );
		
		double spacing = ( selectedVertexes.get( selectedVertexes.size( ) - 1 ).y.get( ) - selectedVertexes.get( 0 ).y.get( ) ) / (double) ( selectedVertexes.size( ) - 1 );
		double currentY = selectedVertexes.get( 0 ).y.get( ) - spacing;
		
		for ( Vertex vertex : selectedVertexes )
			vertex.y.set( currentY += spacing );
	}
	
	public static void rotateLeft90( Graph graph )
	{
		Point2D.Double centroid = new Point2D.Double( );
		
		List<Vertex> selectedVertexes = new ArrayList<Vertex>( );
		for ( Vertex v : graph.vertexes )
			if ( v.isSelected.get( ) )
				selectedVertexes.add( v );
		
		if ( selectedVertexes.size( ) < 1 )
		{
			for ( Edge e : graph.edges )
				if ( e.isSelected.get( ) )
					return;
			
			for ( Caption c : graph.captions )
				if ( c.isSelected.get( ) )
					return;
			
			selectedVertexes.addAll( graph.vertexes );
		}
		
		List<Edge> selectedEdges = new ArrayList<Edge>( );
		for ( Edge e : graph.edges )
			if ( e.isSelected.get( ) )
				selectedEdges.add( e );
		
		for ( Vertex v : selectedVertexes )
		{
			centroid.x += v.x.get( );
			centroid.y += v.y.get( );
		}
		
		for ( Edge e : selectedEdges )
		{
			centroid.x += e.handleX.get( );
			centroid.y += e.handleY.get( );
		}
		
		centroid.x /= (double) selectedVertexes.size( ) + selectedEdges.size( );
		centroid.y /= (double) selectedVertexes.size( ) + selectedEdges.size( );
		
		for ( Edge e : selectedEdges )
			e.suspendNotifications( true );
		
		for ( Vertex v : selectedVertexes )
		{
			double oldVertexX = v.x.get( );
			v.x.set( centroid.x - ( centroid.y - v.y.get( ) ) );
			v.y.set( centroid.y + ( centroid.x - oldVertexX ) );
		}
		
		for ( Edge e : selectedEdges )
		{
			double oldEdgeHandleX = e.handleX.get( );
			e.handleX.set( centroid.x - ( centroid.y - e.handleY.get( ) ) );
			e.handleY.set( centroid.y + ( centroid.x - oldEdgeHandleX ) );
			e.suspendNotifications( false );
			e.refresh( );
		}
	}
	
	public static void rotateRight90( Graph graph )
	{
		Point2D.Double centroid = new Point2D.Double( );
		
		List<Vertex> selectedVertexes = new ArrayList<Vertex>( );
		for ( Vertex v : graph.vertexes )
			if ( v.isSelected.get( ) )
				selectedVertexes.add( v );
		
		if ( selectedVertexes.size( ) < 1 )
		{
			for ( Edge e : graph.edges )
				if ( e.isSelected.get( ) )
					return;
			
			for ( Caption c : graph.captions )
				if ( c.isSelected.get( ) )
					return;
			
			selectedVertexes.addAll( graph.vertexes );
		}
		
		List<Edge> selectedEdges = new ArrayList<Edge>( );
		for ( Edge e : graph.edges )
			if ( e.isSelected.get( ) )
				selectedEdges.add( e );
		
		for ( Vertex v : selectedVertexes )
		{
			centroid.x += v.x.get( );
			centroid.y += v.y.get( );
		}
		
		for ( Edge e : selectedEdges )
		{
			centroid.x += e.handleX.get( );
			centroid.y += e.handleY.get( );
		}
		
		centroid.x /= (double) selectedVertexes.size( ) + selectedEdges.size( );
		centroid.y /= (double) selectedVertexes.size( ) + selectedEdges.size( );
		
		for ( Edge e : selectedEdges )
			e.suspendNotifications( true );
		
		for ( Vertex v : selectedVertexes )
		{
			double oldVertexX = v.x.get( );
			v.x.set( centroid.x + ( centroid.y - v.y.get( ) ) );
			v.y.set( centroid.y - ( centroid.x - oldVertexX ) );
		}
		
		for ( Edge e : selectedEdges )
		{
			double oldEdgeHandleX = e.handleX.get( );
			e.handleX.set( centroid.x + ( centroid.y - e.handleY.get( ) ) );
			e.handleY.set( centroid.y - ( centroid.x - oldEdgeHandleX ) );
			e.suspendNotifications( false );
			e.refresh( );
		}
	}
	
	public static void flipHorizontally( Graph graph )
	{
		double centerX = 0.0;
		List<Vertex> selectedVertexes = new ArrayList<Vertex>( );
		for ( Vertex v : graph.vertexes )
			if ( v.isSelected.get( ) )
				selectedVertexes.add( v );
		
		if ( selectedVertexes.size( ) < 1 )
		{
			for ( Edge e : graph.edges )
				if ( e.isSelected.get( ) )
					return;
			
			for ( Caption c : graph.captions )
				if ( c.isSelected.get( ) )
					return;
			
			selectedVertexes.addAll( graph.vertexes );
		}
		
		List<Edge> selectedEdges = new ArrayList<Edge>( );
		for ( Edge e : graph.edges )
			if ( e.isSelected.get( ) )
				selectedEdges.add( e );
		
		for ( Vertex v : selectedVertexes )
			centerX += v.x.get( );
		
		for ( Edge e : selectedEdges )
			centerX += e.handleX.get( );
		
		centerX /= (double) selectedVertexes.size( ) + selectedEdges.size( );
		
		for ( Edge e : selectedEdges )
			e.suspendNotifications( true );
		
		for ( Vertex v : selectedVertexes )
			v.x.set( 2.0 * centerX - v.x.get( ) );
		
		for ( Edge e : selectedEdges )
		{
			e.handleX.set( 2.0 * centerX - e.handleX.get( ) );
			e.suspendNotifications( false );
			e.refresh( );
		}
	}
	
	public static void flipVertically( Graph graph )
	{
		double centerY = 0.0;
		List<Vertex> selectedVertexes = new ArrayList<Vertex>( );
		for ( Vertex v : graph.vertexes )
			if ( v.isSelected.get( ) )
				selectedVertexes.add( v );
		
		if ( selectedVertexes.size( ) < 1 )
		{
			for ( Edge e : graph.edges )
				if ( e.isSelected.get( ) )
					return;
			
			for ( Caption c : graph.captions )
				if ( c.isSelected.get( ) )
					return;
			
			selectedVertexes.addAll( graph.vertexes );
		}
		
		List<Edge> selectedEdges = new ArrayList<Edge>( );
		for ( Edge e : graph.edges )
			if ( e.isSelected.get( ) )
				selectedEdges.add( e );
		
		for ( Vertex v : selectedVertexes )
			centerY += v.y.get( );
		
		for ( Edge e : selectedEdges )
			centerY += e.handleY.get( );
		
		centerY /= (double) selectedVertexes.size( ) + selectedEdges.size( );
		
		for ( Edge e : selectedEdges )
			e.suspendNotifications( true );
		
		for ( Vertex v : selectedVertexes )
			v.y.set( 2.0 * centerY - v.y.get( ) );
		
		for ( Edge e : selectedEdges )
		{
			e.handleY.set( 2.0 * centerY - e.handleY.get( ) );
			e.suspendNotifications( false );
			e.refresh( );
		}
	}
	
	public static void scale( Graph graph, double factor )
	{
		Point2D.Double centroid = new Point2D.Double( );
		List<Vertex> selectedVertexes = new ArrayList<Vertex>( );
		
		for ( Vertex v : graph.vertexes )
			if ( v.isSelected.get( ) )
				selectedVertexes.add( v );
		
		if ( selectedVertexes.size( ) < 1 )
		{
			for ( Edge e : graph.edges )
				if ( e.isSelected.get( ) )
					return;
			
			for ( Caption c : graph.captions )
				if ( c.isSelected.get( ) )
					return;
			
			selectedVertexes.addAll( graph.vertexes );
		}
		
		for ( Vertex v : selectedVertexes )
		{
			centroid.x += v.x.get( );
			centroid.y += v.y.get( );
		}
		
		centroid.x /= (double) selectedVertexes.size( );
		centroid.y /= (double) selectedVertexes.size( );
		
		for ( Vertex v : selectedVertexes )
		{
			v.x.set( factor * ( v.x.get( ) - centroid.x ) + centroid.x );
			v.y.set( factor * ( v.y.get( ) - centroid.y ) + centroid.y );
		}
	}
	
	// countConnectedComponents method + supporting class and methods
	
	private static Random random = new Random( );
	
	public static int countConnectedComponents( Graph graph )
	{
		if ( graph.vertexes.size( ) < 1 )
			return 0;
		
		HashMap<Vertex, Node> vertexNodes = new HashMap<Vertex, Node>( );
		
		// Add all selected vertices
		for ( Vertex v : graph.vertexes )
			if ( v.isSelected.get( ) )
				vertexNodes.put( v, new Node( ) );
		
		// If no vertices were selected, add them all
		if ( vertexNodes.isEmpty( ) )
			for ( Vertex v : graph.vertexes )
				vertexNodes.put( v, new Node( ) );
		
		// Union vertex sets using selected edges
		boolean foundSelectedEdges = false;
		for ( Edge e : graph.edges )
			if ( e.isSelected.get( ) )
			{
				foundSelectedEdges = true;
				if ( !e.isLoop )
					union( vertexNodes.get( e.from ), vertexNodes.get( e.to ) );
			}
		
		// If no edges were selected, union vertex sets using all edges
		if ( !foundSelectedEdges )
			for ( Edge e : graph.edges )
				if ( !e.isLoop )
					union( vertexNodes.get( e.from ), vertexNodes.get( e.to ) );
		
		int connectedComponentCount = 0;
		
		for ( Node node : vertexNodes.values( ) )
			if ( node.parent == null )
				++connectedComponentCount;
		
		return connectedComponentCount;
	}
	
	private static Node find( Node node )
	{
		Node root = node;
		
		while ( root.parent != null )
			root = root.parent;
		
		Node nextNode;
		while ( node.parent != null )
		{
			nextNode = node.parent;
			node.parent = root;
			node = nextNode;
		}
		
		return root;
	}
	
	private static void union( Node a, Node b )
	{
		if ( a != null && b != null )
		{
			Node rootA = find( a );
			Node rootB = find( b );
			
			if ( rootA != rootB )
			{
				if ( rootA.rank > rootB.rank || random.nextBoolean( ) )
					rootA.parent = rootB;
				else
					rootB.parent = rootA;
			}
		}
	}
	
	private static class Node
	{
		public Node parent = null;
		public int rank = 0;
	}
}
