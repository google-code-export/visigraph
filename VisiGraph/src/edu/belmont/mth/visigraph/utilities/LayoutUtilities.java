/**
 * LayoutUtilities.java
 */
package edu.belmont.mth.visigraph.utilities;

import java.util.*;
import java.awt.geom.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;

/**
 * @author Cameron Behar
 */
public class LayoutUtilities
{
	public static void alignHorizontally( Collection<Vertex> vertices )
	{
		double centerY = 0.0;
		for( Vertex vertex : vertices )
			centerY += vertex.y.get( );
		
		centerY /= vertices.size( );
		
		for( Vertex vertex : vertices )
			vertex.y.set( centerY );
	}
	
	public static void alignVertically( Collection<Vertex> vertices )
	{
		double centerX = 0.0;
		for( Vertex vertex : vertices )
			centerX += vertex.x.get( );
		
		centerX /= vertices.size( );
		
		for( Vertex vertex : vertices )
			vertex.x.set( centerX );
	}
	
	public static void arrangeCircle( Collection<Vertex> vertices )
	{
		Point2D.Double centroid = new Point2D.Double( 0.0, 0.0 );
		for( Vertex vertex : vertices )
		{
			centroid.x += vertex.x.get( );
			centroid.y += vertex.y.get( );
		}
		
		centroid.x /= vertices.size( );
		centroid.y /= vertices.size( );
		
		double radius = UserSettings.instance.arrangeCircleRadiusMultiplier.get( ) * vertices.size( );
		double degreesPerVertex = 2.0 * Math.PI / vertices.size( );
		
		int i = 0;
		for( Vertex vertex : vertices )
		{
			vertex.x.set( radius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ) + centroid.x );
			vertex.y.set( radius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) + centroid.y );
			++i;
		}
	}
	
	public static void arrangeGrid( Collection<Vertex> vertices )
	{
		int rows = (int) Math.round( Math.sqrt( vertices.size( ) ) );
		arrangeGrid( vertices, rows );
	}
	
	public static void arrangeGrid( Collection<Vertex> vertices, int rows )
	{
		int columns = (int) Math.ceil( vertices.size( ) / (double) rows );
		
		double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
		for( Vertex vertex : vertices )
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
		
		int row = 0, col = 0;
		for( Vertex vertex : vertices )
		{
			vertex.x.set( UserSettings.instance.arrangeGridSpacing.get( ) * col + offset.x );
			vertex.y.set( UserSettings.instance.arrangeGridSpacing.get( ) * row + offset.y );
			
			if( ++col >= columns )
			{
				col = 0;
				++row;
			}
		}
	}
	
	public static double arrangeTensors( Collection<Vertex> vertices, Collection<Edge> edges, double attractiveForce, double repulsiveForce, double speed, Map<Vertex, Point2D> velocities )
	{
		if( vertices.isEmpty( ) )
			return 0.0;
		
		double totalKineticEnergy = 0;
		
		// Initialize the map of net forces
		Map<Vertex, Point2D.Double> forces = new HashMap<Vertex, Point2D.Double>( );
		for( Vertex vertex : vertices )
			forces.put( vertex, new Point2D.Double( 0.0, 0.0 ) );
		
		// Apply all repulsive forces
		for( Vertex vertexA : vertices )
			for( Vertex vertexB : vertices )
				if( vertexA == vertexB )
					break;
				else
				{
					final double xDiff = vertexA.x.get( ) - vertexB.x.get( ), yDiff = vertexA.y.get( ) - vertexB.y.get( );
					final double distanceSquared = Math.max( 900.0, xDiff * xDiff + yDiff * yDiff );
					final double force = ( 8987551787.0 * repulsiveForce * vertexA.weight.get( ) * repulsiveForce * vertexB.weight.get( ) ) / distanceSquared;
					final double angle = Math.atan2( yDiff, xDiff );
					final double xForce = -force * Math.cos( angle ), yForce = -force * Math.sin( angle );
					
					forces.get( vertexA ).x -= xForce;
					forces.get( vertexA ).y -= yForce;
					forces.get( vertexB ).x += xForce;
					forces.get( vertexB ).y += yForce;
				}
		
		// Apply all attractive forces
		for( Edge edge : edges )
			if( !edge.isLoop && ( forces.containsKey( edge.from ) || forces.containsKey( edge.to ) ) )
			{
				final double xDiff = edge.from.x.get( ) - edge.to.x.get( ), yDiff = edge.from.y.get( ) - edge.to.y.get( );
				final double distanceSquared = Math.max( 900.0, xDiff * xDiff + yDiff * yDiff );
				final double force = attractiveForce * ( distanceSquared - edge.weight.get( ) * 10.0 );
				final double angle = Math.atan2( yDiff, xDiff );
				final double xForce = force * Math.cos( angle ), yForce = force * Math.sin( angle );
				
				if( forces.containsKey( edge.from ) )
				{
					forces.get( edge.from ).x -= xForce;
					forces.get( edge.from ).y -= yForce;
				}
				
				if( forces.containsKey( edge.to ) )
				{
					forces.get( edge.to ).x += xForce;
					forces.get( edge.to ).y += yForce;
				}
			}
		
		// Apply all net forces
		for( Vertex vertex : vertices )
		{
			if( !velocities.containsKey( vertex ) )
				velocities.put( vertex, new Point2D.Double( 0, 0 ) );
			
			Point2D velocity = velocities.get( vertex );
			velocity.setLocation( ( velocity.getX( ) + forces.get( vertex ).x ) * 0.85, ( velocity.getY( ) + forces.get( vertex ).y ) * 0.85 );
			totalKineticEnergy += vertex.weight.get( ) * velocity.distanceSq( 0, 0 );
		}
		
		// Apply all velocities
		for( Vertex vertex : vertices )
		{
			vertex.x.set( vertex.x.get( ) + speed * velocities.get( vertex ).getX( ) );
			vertex.y.set( vertex.y.get( ) + speed * velocities.get( vertex ).getY( ) );
		}
		
		return totalKineticEnergy;
	}
	
	public static void arrangeTree( Collection<Vertex> roots, Graph graph )
	{
		Set<Vertex> placedVertices = new HashSet<Vertex>( );
		LinkedList<List<Vertex>> levels = new LinkedList<List<Vertex>>( );
		
		// First we need to add all root vertices to the tree
		levels.add( new LinkedList<Vertex>( ) );
		for( Vertex vertex : roots )
		{
			levels.get( 0 ).add( vertex );
			placedVertices.add( vertex );
		}
		
		// While the last level has vertices, add all their neighbors to the next level that haven't yet been otherwise added
		while( !levels.getLast( ).isEmpty( ) )
		{
			levels.add( new LinkedList<Vertex>( ) );
			
			for( Vertex vertex : levels.get( levels.size( ) - 2 ) )
				for( Vertex neighbor : graph.getNeighbors( vertex ) )
					if( !placedVertices.contains( neighbor ) )
					{
						levels.get( levels.size( ) - 1 ).add( neighbor );
						placedVertices.add( neighbor );
					}
		}
		
		// If there were any nodes that weren't added yet, give them their own level
		if( placedVertices.size( ) < graph.vertices.size( ) )
			for( Vertex vertex : levels.getLast( ) )
				if( !placedVertices.contains( vertex ) )
				{
					levels.getLast( ).add( vertex );
					placedVertices.add( vertex );
				}
		
		// If the last level is empty, remove it
		if( levels.getLast( ).isEmpty( ) )
			levels.removeLast( );
		
		// Now for the layout!
		double largestWidth = 0.0;
		for( List<Vertex> level : levels )
			largestWidth = Math.max( largestWidth, level.size( ) * 150.0 );
		
		double y = 0.0;
		for( List<Vertex> level : levels )
		{
			double colSpace = largestWidth / ( level.size( ) );
			
			for( int col = 0; col < level.size( ); ++col )
			{
				Vertex vertex = level.get( col );
				double x = ( col + 0.5 ) * colSpace - largestWidth / 2.0;
				
				vertex.x.set( x );
				vertex.y.set( y );
			}
			
			y += 150.0;
		}
	}
	
	public static void distributeHorizontally( Collection<Vertex> vertices )
	{
		LinkedList<Vertex> sortedVertices = new LinkedList<Vertex>( vertices );
		Collections.sort( sortedVertices, new Comparator<Vertex>( )
		{
			public int compare( Vertex v1, Vertex v2 )
			{
				return (int) Math.round( Math.signum( v1.x.get( ) - v2.x.get( ) ) );
			}
		} );
		
		double spacing = ( sortedVertices.getLast( ).x.get( ) - sortedVertices.getFirst( ).x.get( ) ) / ( sortedVertices.size( ) - 1 );
		double currentX = sortedVertices.getFirst( ).x.get( ) - spacing;
		
		for( Vertex vertex : sortedVertices )
			vertex.x.set( currentX += spacing );
	}
	
	public static void distributeVertically( Collection<Vertex> vertices )
	{
		LinkedList<Vertex> sortedVertices = new LinkedList<Vertex>( vertices );
		Collections.sort( sortedVertices, new Comparator<Vertex>( )
		{
			public int compare( Vertex v1, Vertex v2 )
			{
				return (int) Math.round( Math.signum( v1.y.get( ) - v2.y.get( ) ) );
			}
		} );
		
		double spacing = ( sortedVertices.getLast( ).y.get( ) - sortedVertices.getFirst( ).y.get( ) ) / ( sortedVertices.size( ) - 1 );
		double currentY = sortedVertices.getFirst( ).y.get( ) - spacing;
		
		for( Vertex vertex : sortedVertices )
			vertex.y.set( currentY += spacing );
	}
	
	public static void flipHorizontally( Collection<Vertex> vertices, Collection<Edge> edges )
	{
		double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
		for( Vertex vertex : vertices )
		{
			minX = Math.min( minX, vertex.x.get( ) );
			maxX = Math.max( maxX, vertex.x.get( ) );
		}
		
		for( Edge edge : edges )
		{
			minX = Math.min( minX, edge.handleX.get( ) );
			maxX = Math.max( maxX, edge.handleX.get( ) );
		}
		
		double centerX = ( minX + maxX ) / 2.0;
		
		for( Edge edge : edges )
			edge.suspendNotifications( true );
		
		for( Vertex vertex : vertices )
			vertex.x.set( 2.0 * centerX - vertex.x.get( ) );
		
		for( Edge edge : edges )
		{
			edge.handleX.set( 2.0 * centerX - edge.handleX.get( ) );
			edge.suspendNotifications( false );
			edge.refresh( );
		}
	}
	
	public static void flipVertically( Collection<Vertex> vertices, Collection<Edge> edges )
	{
		double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
		for( Vertex vertex : vertices )
		{
			minY = Math.min( minY, vertex.y.get( ) );
			maxY = Math.max( maxY, vertex.y.get( ) );
		}
		
		for( Edge edge : edges )
		{
			minY = Math.min( minY, edge.handleY.get( ) );
			maxY = Math.max( maxY, edge.handleY.get( ) );
		}
		
		double centerY = ( minY + maxY ) / 2.0;
		
		for( Edge edge : edges )
			edge.suspendNotifications( true );
		
		for( Vertex vertex : vertices )
			vertex.y.set( 2.0 * centerY - vertex.y.get( ) );
		
		for( Edge edge : edges )
		{
			edge.handleY.set( 2.0 * centerY - edge.handleY.get( ) );
			edge.suspendNotifications( false );
			edge.refresh( );
		}
	}
	
	public static void rotateLeft90( Collection<Vertex> vertices, Collection<Edge> edges )
	{
		// Find the center
		double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
		for( Vertex vertex : vertices )
		{
			minX = Math.min( minX, vertex.x.get( ) );
			maxX = Math.max( maxX, vertex.x.get( ) );
			
			minY = Math.min( minY, vertex.y.get( ) );
			maxY = Math.max( maxY, vertex.y.get( ) );
		}
		
		Point2D.Double center = new Point2D.Double( ( minX + maxX ) / 2.0, ( minY + maxY ) / 2.0 );
		
		// Perform the rotation
		for( Edge edge : edges )
			edge.suspendNotifications( true );
		
		for( Vertex vertex : vertices )
		{
			double oldVertexX = vertex.x.get( );
			vertex.x.set( center.x - ( center.y - vertex.y.get( ) ) );
			vertex.y.set( center.y + ( center.x - oldVertexX ) );
		}
		
		for( Edge edge : edges )
		{
			double oldEdgeHandleX = edge.handleX.get( );
			edge.handleX.set( center.x - ( center.y - edge.handleY.get( ) ) );
			edge.handleY.set( center.y + ( center.x - oldEdgeHandleX ) );
			edge.suspendNotifications( false );
			edge.refresh( );
		}
	}
	
	public static void rotateRight90( Collection<Vertex> vertices, Collection<Edge> edges )
	{
		// Find the center
		double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
		for( Vertex vertex : vertices )
		{
			minX = Math.min( minX, vertex.x.get( ) );
			maxX = Math.max( maxX, vertex.x.get( ) );
			
			minY = Math.min( minY, vertex.y.get( ) );
			maxY = Math.max( maxY, vertex.y.get( ) );
		}
		
		for( Edge edge : edges )
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
		for( Edge edge : edges )
			edge.suspendNotifications( true );
		
		for( Vertex vertex : vertices )
		{
			double oldVertexX = vertex.x.get( );
			vertex.x.set( center.x + ( center.y - vertex.y.get( ) ) );
			vertex.y.set( center.y - ( center.x - oldVertexX ) );
		}
		
		for( Edge edge : edges )
		{
			double oldEdgeHandleX = edge.handleX.get( );
			edge.handleX.set( center.x + ( center.y - edge.handleY.get( ) ) );
			edge.handleY.set( center.y - ( center.x - oldEdgeHandleX ) );
			edge.suspendNotifications( false );
			edge.refresh( );
		}
	}
	
	public static void scale( Collection<Vertex> vertices, double factor )
	{
		Point2D.Double centroid = new Point2D.Double( );
		for( Vertex vertex : vertices )
		{
			centroid.x += vertex.x.get( );
			centroid.y += vertex.y.get( );
		}
		
		centroid.x /= vertices.size( );
		centroid.y /= vertices.size( );
		
		for( Vertex vertex : vertices )
		{
			vertex.x.set( factor * ( vertex.x.get( ) - centroid.x ) + centroid.x );
			vertex.y.set( factor * ( vertex.y.get( ) - centroid.y ) + centroid.y );
		}
	}
}
