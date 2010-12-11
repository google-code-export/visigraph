/**
 * CountCrossings.java
 */
package edu.belmont.mth.visigraph.models.functions;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.awt.geom.*;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.utilities.*;

/**
 * @author Cameron Behar
 */
public class CountCrossings implements FunctionBase
{
	/* ########################################################################################### */
	/* #  Note: This class should always be compiled; when scripted, it simply runs too slowly.  # */
	/* ########################################################################################### */

	@Override
	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		List<Edge> edges = new ArrayList<Edge>( );
		
		for ( Edge edge : g.edges )
			if ( edge.isSelected.get( ) )
				edges.add( edge );
		
		if ( edges.size( ) < 2 )
		{
			edges.clear( );
			edges.addAll( g.edges );
		}
		
		int crossingsCount = 0;
		g2D.setColor( Color.red );
		
		for ( int i = 0; i < edges.size( ); ++i )
			for ( int j = i + 1; j < edges.size( ); ++j )
			{
				List<Point2D> crossings = getCrossings( edges.get( i ), edges.get( j ) );
				crossingsCount += crossings.size( );
				
				if ( crossings.size( ) > 0 && g2D != null )
				{
					double markerRadius = edges.get( i ).thickness.get( ) + edges.get( j ).thickness.get( );
					double markerDiameter = 2 * markerRadius;
					
					for ( Point2D location : crossings )
						g2D.fill( new Ellipse2D.Double( location.getX( ) - markerRadius, location.getY( ) - markerRadius, markerDiameter, markerDiameter ) );
				}
			}
		
		return crossingsCount + "";
	}
	
	private static Vector<Point2D> getCrossings( Edge e0, Edge e1 )
	{
		// Note: we do not count crossings between adjacent (coincident) edges as they are unnecessary
		if ( e0.isAdjacent( e1 ) )
			return new Vector<Point2D>( );
		else if ( e0.isLinear( ) )
		{
			if ( e1.isLinear( ) )
				return GeometryUtilities.getCrossings( e0.getLine( ), e1.getLine( ) );
			else
				return GeometryUtilities.getCrossings( e0.getLine( ), e1.getArc( ), e1.getCenter( ) );
		}
		else if ( e1.isLinear( ) )
			return GeometryUtilities.getCrossings( e1.getLine( ), e0.getArc( ), e0.getCenter( ) );
		else
			return GeometryUtilities.getCrossings( e0.getArc( ), e0.getCenter( ), e1.getArc( ), e1.getCenter( ) );
	}
	
	public boolean allowsDynamicEvaluation( )
	{
		return true;
	}
	
	public boolean allowsOneTimeEvaluation( )
	{
		return true;
	}
	
	@Override
	public String toString( )
	{
		return "Count crossings";
	}
}
