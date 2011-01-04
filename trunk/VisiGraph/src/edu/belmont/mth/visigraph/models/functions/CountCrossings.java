/**
 * CountCrossings.java
 */
package edu.belmont.mth.visigraph.models.functions;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.awt.geom.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;

/**
 * @author Cameron Behar
 */
public class CountCrossings implements Function
{
	/* ######################################################################################## */
	/* # Note: This class should always be compiled; when scripted, it simply runs too slowly # */
	/* ######################################################################################## */

	private static List<Point2D> getCrossings( Edge e0, Edge e1 )
	{
		// Note: we do not count crossings between adjacent (coincident) edges as they are unnecessary
		if( e0.isAdjacent( e1 ) )
			return new LinkedList<Point2D>( );
		else if( e0.isLinear( ) )
		{
			if( e1.isLinear( ) )
				return GeometryUtilities.getCrossings( e0.getLine( ), e1.getLine( ) );
			else
				return GeometryUtilities.getCrossings( e0.getLine( ), e1.getArc( ), e1.getCenter( ) );
		}
		else if( e1.isLinear( ) )
			return GeometryUtilities.getCrossings( e1.getLine( ), e0.getArc( ), e0.getCenter( ) );
		else
			return GeometryUtilities.getCrossings( e0.getArc( ), e0.getCenter( ), e1.getArc( ), e1.getCenter( ) );
	}
	
	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		List<Edge> edges = g.getSelectedEdges( );
		if( edges.size( ) < 2 )
			edges = g.edges;
		
		int crossingsCount = 0;
		g2D.setColor( Color.red );
		
		for( int i = 0; i < edges.size( ); ++i )
			for( int j = i + 1; j < edges.size( ); ++j )
			{
				List<Point2D> crossings = getCrossings( edges.get( i ), edges.get( j ) );
				crossingsCount += crossings.size( );
				
				if( crossings.size( ) > 0 && g2D != null )
				{
					double markerRadius = edges.get( i ).thickness.get( ) + edges.get( j ).thickness.get( );
					double markerDiameter = 2 * markerRadius;
					
					for( Point2D location : crossings )
						g2D.fill( new Ellipse2D.Double( location.getX( ) - markerRadius, location.getY( ) - markerRadius, markerDiameter, markerDiameter ) );
				}
			}
		
		return crossingsCount + "";
	}
	
	public Object getAttribute( Function.Attribute attribute )
	{
		switch( attribute )
		{
			case AUTHOR:
				return "Cameron Behar";
			case VERSION:
				return "20110101";
			case DESCRIPTION:
				return "Highlights and counts all intersections, or <i>crossings</i>, between the selected edges in a given drawing of a graph.</p><p>Using a variety of geometric methods built into" + GlobalSettings.applicationName + ", crossings are found in <code><i>O</i>(|<i>E</i>|<sup>2</sup>)</code> time.";
			case INPUT:
				return "The selected edges; if none are selected, the entire edge set will be used.";
			case OUTPUT:
				return "The number of crossings found between the selected edges.  <i>Note: adjacent edges are not tested for crossings as such crossings are demonstrably trivial.</i>";
			case ALLOWS_DYNAMIC_EVALUATION:
				return true;
			case ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case TAGS:
				return new String[ ] { "Crossings", "Graph evaluator", "Planarity" };
			default:
				return null;
		}
	}
	
	public boolean isApplicable( boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed )
	{
		return true;
	}
	
	@Override
	public String toString( )
	{
		return "Count crossings";
	}
}
