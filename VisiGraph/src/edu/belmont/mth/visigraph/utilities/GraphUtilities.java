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
	
	public static boolean arrangeTensors( Graph graph )
	{
		ArrayList<Vertex> selected = new ArrayList<Vertex>( );
		
		for ( Vertex vertex : graph.vertexes )
			if ( vertex.isSelected.get( ) )
				selected.add( vertex );
		
		boolean noneWereSelected = ( selected.size( ) < 1 );
		
		if ( noneWereSelected )
			selected.addAll( graph.vertexes );
		
		HashMap<Vertex, Point2D.Double> forces = new HashMap<Vertex, Point2D.Double>( );
		
		// Initialize the hashmap of forces
		for ( Vertex v : selected )
			forces.put( v, new Point2D.Double( 0, 0 ) );
		
		// Calculate all repulsive forces
		for ( int i = 0; i < selected.size( ); ++i )
			for ( int j = i + 1; j < selected.size( ); ++j )
			{
				Vertex v0 = selected.get( i );
				Vertex v1 = selected.get( j );
				
				double xDiff = v1.x.get( ) - v0.x.get( );
				double yDiff = v1.y.get( ) - v0.y.get( );
				double distanceSquared = ( xDiff * xDiff + yDiff * yDiff );
				double xForce = UserSettings.instance.autoArrangeRepulsiveForce.get( ) * ( xDiff / distanceSquared );
				double yForce = UserSettings.instance.autoArrangeRepulsiveForce.get( ) * ( yDiff / distanceSquared );
				
				Point2D.Double force0 = forces.get( v0 );
				force0.x += xForce; force0.y += yForce;
				
				// And because every action has an opposite and equal reaction
				Point2D.Double force1 = forces.get( v1 );
				force1.x -= xForce; force1.y -= yForce;
			}

		// Calculate all attractive forces
		for ( Edge edge : graph.edges )
			if ( edge.from != edge.to && ( noneWereSelected || edge.from.isSelected.get( ) || edge.to.isSelected.get( ) ) )
			{
				double xDiff = edge.to.x.get( ) - edge.from.x.get( );
				double yDiff = edge.to.y.get( ) - edge.from.y.get( );
				double distanceSquared = ( xDiff * xDiff + yDiff * yDiff );
				double xForce = UserSettings.instance.autoArrangeAttractiveForce.get( ) * xDiff * distanceSquared;
				double yForce = UserSettings.instance.autoArrangeAttractiveForce.get( ) * yDiff * distanceSquared;
				
				if ( noneWereSelected || edge.from.isSelected.get( ) )
				{
					Point2D.Double force = forces.get( edge.from );
					force.x += xForce; force.y += yForce;
				}
				
				// And because every action has an opposite and equal reaction
				if ( noneWereSelected || edge.to.isSelected.get( ) )
				{
					Point2D.Double force = forces.get( edge.to );
					force.x -= xForce; force.y -= yForce;
				}
			}
		
		double netForce = 0.0;
		
		// Apply all net forces
		for ( Vertex v : selected )
		{
			Point2D.Double force = forces.get( v );
			v.x.set( v.x.get( ) + force.x );
			v.y.set( v.y.get( ) + force.y );
			
			// And whilst I know I should be using the magnitude, this is quicker
			netForce += Math.abs( force.x ) + Math.abs( force.y );
		}
		
		return !noneWereSelected && netForce / selected.size( ) < 0.1;
	}
	
	public static void alignHorizontally( Graph graph )
	{
		double centerY = 0.0, selectedCount = 0.0;
		
		for ( Vertex v : graph.vertexes )
			if (v.isSelected.get( ) )
			{
				centerY += v.y.get( );
				++selectedCount;
			}
		
		centerY /= selectedCount;
		
		for ( Vertex v : graph.vertexes )
			if (v.isSelected.get( ) )
				v.y.set( centerY );
	}
	
	public static void alignVertically( Graph graph )
	{
		double centerX = 0.0, selectedCount = 0.0;
		
		for ( Vertex v : graph.vertexes )
			if (v.isSelected.get( ) )
			{
				centerX += v.x.get( );
				++selectedCount;
			}
		
		centerX /= selectedCount;
		
		for ( Vertex v : graph.vertexes )
			if (v.isSelected.get( ) )
				v.x.set( centerX );
	}
	
	public static void distributeHorizontally( Graph graph )
	{
		Vector<Vertex> selectedVertexes = new Vector<Vertex>( );
		
		for ( Vertex v : graph.vertexes )
			if (v.isSelected.get( ) )
				selectedVertexes.add( v );
		
		Collections.sort( selectedVertexes, new Comparator<Vertex>( )
		{
			public int compare( Vertex v1, Vertex v2 )
			{
				return new Double( Math.signum( v1.x.get( ) - v2.x.get( ) ) ).intValue( );
			}
		} );
		double spacing = ( selectedVertexes.lastElement( ).x.get( ) - selectedVertexes.firstElement( ).x.get( ) ) / (double) ( selectedVertexes.size( ) - 1 );
		double currentX = selectedVertexes.firstElement( ).x.get( ) - spacing;
		
		for ( Vertex vertex : selectedVertexes )
			vertex.x.set( currentX += spacing );
	}
	
	public static void distributeVertically( Graph graph )
	{
		Vector<Vertex> selectedVertexes = new Vector<Vertex>( );
		
		for ( Vertex v : graph.vertexes )
			if (v.isSelected.get( ) )
				selectedVertexes.add( v );
		
		Collections.sort( selectedVertexes, new Comparator<Vertex>( )
		{
			public int compare( Vertex v1, Vertex v2 )
			{
				return new Double( Math.signum( v1.y.get( ) - v2.y.get( ) ) ).intValue( );
			}
		} );
		double spacing = ( selectedVertexes.lastElement( ).y.get( ) - selectedVertexes.firstElement( ).y.get( ) ) / (double) ( selectedVertexes.size( ) - 1 );
		double currentY = selectedVertexes.firstElement( ).y.get( ) - spacing;
		
		for ( Vertex vertex : selectedVertexes )
			vertex.y.set( currentY += spacing );
	}
	
	public static void rotateLeft90( Graph graph )
	{
		int selectedElementCount = 0;
		Point2D.Double centroid = new Point2D.Double( );
		
		for ( Vertex vertex : graph.vertexes )
			if ( vertex.isSelected.get( ) )
			{
				centroid.x += vertex.x.get( );
				centroid.y += vertex.y.get( );
				++selectedElementCount;
			}
		
		for ( Edge edge : graph.edges )
			if ( edge.isSelected.get( ) )
			{
				centroid.x += edge.handleX.get( );
				centroid.y += edge.handleY.get( );
				++selectedElementCount;
			}
		
		centroid.x /= (double) selectedElementCount;
		centroid.y /= (double) selectedElementCount;
		
		for ( Edge edge : graph.edges )
			if ( edge.isSelected.get( ) )
				edge.suspendNotifications( true );
		
		for ( Vertex vertex : graph.vertexes )
			if ( vertex.isSelected.get( ) )
			{
				double oldVertexX = vertex.x.get( );
				vertex.x.set( centroid.x - ( centroid.y - vertex.y.get( ) ) );
				vertex.y.set( centroid.y + ( centroid.x - oldVertexX ) );
			}
		
		for ( Edge edge : graph.edges )
			if ( edge.isSelected.get( ) )
			{
				double oldEdgeHandleX = edge.handleX.get( );
				edge.handleX.set( centroid.x - ( centroid.y - edge.handleY.get( ) ) );
				edge.handleY.set( centroid.y + ( centroid.x - oldEdgeHandleX ) );
				edge.suspendNotifications( false );
				edge.refresh( );
			}
	}
	
	public static void rotateRight90( Graph graph )
	{
		int selectedElementCount = 0;
		Point2D.Double centroid = new Point2D.Double( );
		
		for ( Vertex vertex : graph.vertexes )
			if ( vertex.isSelected.get( ) )
			{
				centroid.x += vertex.x.get( );
				centroid.y += vertex.y.get( );
				++selectedElementCount;
			}
		
		for ( Edge edge : graph.edges )
			if ( edge.isSelected.get( ) )
			{
				centroid.x += edge.handleX.get( );
				centroid.y += edge.handleY.get( );
				++selectedElementCount;
			}
		
		centroid.x /= (double) selectedElementCount;
		centroid.y /= (double) selectedElementCount;
		
		for ( Edge edge : graph.edges )
			if ( edge.isSelected.get( ) )
				edge.suspendNotifications( true );
		
		for ( Vertex vertex : graph.vertexes )
			if ( vertex.isSelected.get( ) )
			{
				double oldVertexX = vertex.x.get( );
				vertex.x.set( centroid.x + ( centroid.y - vertex.y.get( ) ) );
				vertex.y.set( centroid.y - ( centroid.x - oldVertexX ) );
			}
		
		for ( Edge edge : graph.edges )
			if ( edge.isSelected.get( ) )
			{
				double oldEdgeHandleX = edge.handleX.get( );
				edge.handleX.set( centroid.x + ( centroid.y - edge.handleY.get( ) ) );
				edge.handleY.set( centroid.y - ( centroid.x - oldEdgeHandleX ) );
				edge.suspendNotifications( false );
				edge.refresh( );
			}
	}
	
	public static void flipHorizontally( Graph graph )
	{
		int selectedElementCount = 0;
		double centerX = 0.0;
		
		for ( Vertex vertex : graph.vertexes )
			if ( vertex.isSelected.get( ) )
			{
				centerX += vertex.x.get( );
				++selectedElementCount;
			}
		
		for ( Edge edge : graph.edges )
			if ( edge.isSelected.get( ) )
			{
				centerX += edge.handleX.get( );
				++selectedElementCount;
			}
		
		centerX /= (double) selectedElementCount;
		
		for ( Edge edge : graph.edges )
			if ( edge.isSelected.get( ) )
				edge.suspendNotifications( true );
		
		for ( Vertex vertex : graph.vertexes )
			if ( vertex.isSelected.get( ) )
				vertex.x.set( 2.0 * centerX - vertex.x.get( ) );
		
		for ( Edge edge : graph.edges )
			if ( edge.isSelected.get( ) )
			{
				edge.handleX.set( 2.0 * centerX - edge.handleX.get( ) );
				edge.suspendNotifications( false );
				edge.refresh( );
			}
	}
	
	public static void flipVertically( Graph graph )
	{
		int selectedElementCount = 0;
		double centerY = 0.0;
		
		for ( Vertex vertex : graph.vertexes )
			if ( vertex.isSelected.get( ) )
			{
				centerY += vertex.y.get( );
				++selectedElementCount;
			}
		
		for ( Edge edge : graph.edges )
			if ( edge.isSelected.get( ) )
			{
				centerY += edge.handleY.get( );
				++selectedElementCount;
			}
		
		centerY /= (double) selectedElementCount;
		
		for ( Edge edge : graph.edges )
			if ( edge.isSelected.get( ) )
				edge.suspendNotifications( true );
		
		for ( Vertex vertex : graph.vertexes )
			if ( vertex.isSelected.get( ) )
				vertex.y.set( 2.0 * centerY - vertex.y.get( ) );
		
		for ( Edge edge : graph.edges )
			if ( edge.isSelected.get( ) )
			{
				edge.handleY.set( 2.0 * centerY - edge.handleY.get( ) );
				edge.suspendNotifications( false );
				edge.refresh( );
			}
	}
		
	public static void scale( Graph graph, double factor )
	{
		int selectedElementCount = 0;
		Point2D.Double centroid = new Point2D.Double( );
		
		for ( Vertex vertex : graph.vertexes )
			if ( vertex.isSelected.get( ) )
			{
				centroid.x += vertex.x.get( );
				centroid.y += vertex.y.get( );
				++selectedElementCount;
			}
		
		if ( selectedElementCount == 0 )
			return;
		
		centroid.x /= (double) selectedElementCount;
		centroid.y /= (double) selectedElementCount;
		
		for ( Vertex vertex : graph.vertexes )
			if ( vertex.isSelected.get( ) )
			{
				vertex.x.set( factor * ( vertex.x.get( ) - centroid.x ) + centroid.x );
				vertex.y.set( factor* ( vertex.y.get( ) - centroid.y ) + centroid.y );
			}
	}
}











