/**
 * EdgeSvgView.java
 */
package edu.belmont.mth.visigraph.views.svg;

import java.awt.geom.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;
import static edu.belmont.mth.visigraph.utilities.GeometryUtilities.*;

/**
 * @author Cameron Behar
 */
public class EdgeSvgView
{
	public static String format( Edge e, GraphSettings s, double xOffset, double yOffset )
	{
		StringBuilder sb = new StringBuilder( );
		Point2D apparentHandleLocation = e.isLinear( ) ? GeometryUtilities.midpoint( e.from, e.to ) : e.getHandlePoint2D( );
		double handleRadius = e.thickness.get( ) * UserSettings.instance.defaultEdgeHandleRadiusRatio.get( );
		
		if( e.isLoop )
		{
			Point2D center = GeometryUtilities.midpoint( e.from.x.get( ), e.from.y.get( ), apparentHandleLocation.getX( ), apparentHandleLocation.getY( ) );
			
			sb.append( "<circle " );
			sb.append( "cx=\"" + ( center.getX( ) + xOffset ) + "\" " );
			sb.append( "cy=\"" + ( center.getY( ) + yOffset ) + "\" " );
			sb.append( "r=\"" + center.distance( apparentHandleLocation ) + "\" " );
			sb.append( "style=\"fill:none;stroke:" + SvgUtilities.formatColor( ( e.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.getEdgeColor( e.color.get( ) ), UserSettings.instance.selectedEdge.get( ) ) : UserSettings.instance.getEdgeColor( e.color.get( ) ) ) ) + ";stroke-width:" + e.thickness.get( ) + "\"/>\r\n" );
		}
		else if( e.isLinear( ) )
		{
			sb.append( "<line " );
			sb.append( "x1=\"" + ( e.from.x.get( ) + xOffset ) + "\" " );
			sb.append( "y1=\"" + ( e.from.y.get( ) + yOffset ) + "\" " );
			sb.append( "x2=\"" + ( e.to.x.get( ) + xOffset ) + "\" " );
			sb.append( "y2=\"" + ( e.to.y.get( ) + yOffset ) + "\" " );
			sb.append( "style=\"stroke:" + SvgUtilities.formatColor( ( e.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.getEdgeColor( e.color.get( ) ), UserSettings.instance.selectedEdge.get( ) ) : UserSettings.instance.getEdgeColor( e.color.get( ) ) ) ) + ";stroke-width:" + e.thickness.get( ) + "\"/>\r\n" );
		}
		else
		{
			double fromAngle = angle( -( e.from.y.get( ) - e.getCenter( ).getY( ) ), e.from.x.get( ) - e.getCenter( ).getX( ) );
			double handleAngle = angle( -( e.handleY.get( ) - e.getCenter( ).getY( ) ), e.handleX.get( ) - e.getCenter( ).getX( ) );
			double toAngle = angle( -( e.to.y.get( ) - e.getCenter( ).getY( ) ), e.to.x.get( ) - e.getCenter( ).getX( ) );
			boolean isClockwise = angleBetween( fromAngle, handleAngle ) < angleBetween( fromAngle, toAngle );
			
			sb.append( "<path " );
			
			if( isClockwise )
				sb.append( "d=\"M " + ( e.from.x.get( ) + xOffset ) + "," + ( e.from.y.get( ) + yOffset ) + " " );
			else
				sb.append( "d=\"M " + ( e.to.x.get( ) + xOffset ) + "," + ( e.to.y.get( ) + yOffset ) + " " );
			
			double radius = e.getCenter( ).distance( e.handleX.get( ), e.handleY.get( ) );
			sb.append( "A " + radius + "," + radius + " 0 0,0 " );
			
			if( isClockwise )
				sb.append( ( e.to.x.get( ) + xOffset ) + "," + ( e.to.y.get( ) + yOffset ) + "\" " );
			else
				sb.append( ( e.from.x.get( ) + xOffset ) + "," + ( e.from.y.get( ) + yOffset ) + "\" " );
			
			sb.append( "style=\"fill:none;stroke:" + SvgUtilities.formatColor( ( e.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.getEdgeColor( e.color.get( ) ), UserSettings.instance.selectedEdge.get( ) ) : UserSettings.instance.getEdgeColor( e.color.get( ) ) ) ) + ";stroke-width:" + e.thickness.get( ) + "\"/>\r\n" );
		}
		
		if( s.showEdgeHandles.get( ) )
		{
			sb.append( "<circle " );
			sb.append( "cx=\"" + ( apparentHandleLocation.getX( ) + xOffset ) + "\" " );
			sb.append( "cy=\"" + ( apparentHandleLocation.getY( ) + yOffset ) + "\" " );
			sb.append( "r=\"" + handleRadius + "\" " );
			
			String handleColor;
			if( e.color.get( ) == -1 )
				handleColor = SvgUtilities.formatColor( e.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.edgeHandle.get( ), UserSettings.instance.selectedEdgeHandle.get( ) ) : UserSettings.instance.edgeHandle.get( ) );
			else
				handleColor = SvgUtilities.formatColor( e.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.getEdgeColor( e.color.get( ) ), UserSettings.instance.selectedEdgeHandle.get( ) ) : UserSettings.instance.getEdgeColor( e.color.get( ) ) );
			
			sb.append( "stroke=\"" + handleColor + "\" " );
			sb.append( "stroke-width=\"1\" " );
			sb.append( "style=\"fill:" + handleColor + ";\"/>\r\n" );
		}
		
		// Draw arrow head for directed edges
		if( e.isDirected )
		{
			Point2D.Double[] arrowPoint = new Point2D.Double[3];
			double tangentAngle;
			
			if( e.isLinear( ) )
				tangentAngle = Math.atan2( e.to.y.get( ) - e.from.y.get( ), e.to.x.get( ) - e.from.x.get( ) );
			else
			{
				tangentAngle = Math.atan2( e.handleY.get( ) - e.getCenter( ).getY( ), e.handleX.get( ) - e.getCenter( ).getX( ) );
				
				// We need to calculate these angles so that we know in which order whether to flip the angle by 180 degrees
				double fromAngle = Math.atan2( e.from.y.get( ) - e.getCenter( ).getY( ), e.from.x.get( ) - e.getCenter( ).getX( ) );
				double toAngle = Math.atan2( e.to.y.get( ) - e.getCenter( ).getY( ), e.to.x.get( ) - e.getCenter( ).getX( ) );
				
				if( GeometryUtilities.angleBetween( fromAngle, tangentAngle ) >= GeometryUtilities.angleBetween( fromAngle, toAngle ) )
					tangentAngle += Math.PI;
				
				tangentAngle += Math.PI / 2;
			}
			
			for( int i = 0; i < 3; ++i )
			{
				double theta = tangentAngle + i * 2.0 * Math.PI / 3.0;
				arrowPoint[i] = new Point2D.Double( e.thickness.get( ) * UserSettings.instance.directedEdgeArrowRatio.get( ) * Math.cos( theta ) + apparentHandleLocation.getX( ), e.thickness.get( ) * UserSettings.instance.directedEdgeArrowRatio.get( ) * Math.sin( theta ) + apparentHandleLocation.getY( ) );
			}
			
			sb.append( "<path d=\"" );
			sb.append( "M " + ( arrowPoint[0].x + xOffset ) + "," + ( arrowPoint[0].y + yOffset ) + " " );
			sb.append( "L " + ( arrowPoint[1].x + xOffset ) + "," + ( arrowPoint[1].y + yOffset ) + " " );
			sb.append( "L " + ( arrowPoint[2].x + xOffset ) + "," + ( arrowPoint[2].y + yOffset ) + " " );
			sb.append( "L " + ( arrowPoint[0].x + xOffset ) + "," + ( arrowPoint[0].y + yOffset ) + "\" " );
			sb.append( "style=\"fill:" + SvgUtilities.formatColor( e.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.edgeHandle.get( ), UserSettings.instance.selectedEdgeHandle.get( ) ) : UserSettings.instance.edgeHandle.get( ) ) + ";\"/>\r\n" );
		}
		
		if( s.showEdgeLabels.get( ) )
		{
			sb.append( "<text " );
			sb.append( "x=\"" + ( apparentHandleLocation.getX( ) + 2.0 * handleRadius + xOffset ) + "\" " );
			sb.append( "y=\"" + ( apparentHandleLocation.getY( ) + 2.0 * handleRadius + yOffset ) + "\" " );
			sb.append( "fill=\"" + SvgUtilities.formatColor( ( e.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.getEdgeColor( e.color.get( ) ), UserSettings.instance.selectedEdge.get( ) ) : UserSettings.instance.getEdgeColor( e.color.get( ) ) ) ) + "\">" + SvgUtilities.formatString( e.label.get( ) ) + "</text>\r\n" );
		}
		
		if( s.showEdgeWeights.get( ) )
		{
			sb.append( "<text " );
			sb.append( "x=\"" + ( apparentHandleLocation.getX( ) - 1.5 * handleRadius + xOffset ) + "\" " );
			sb.append( "y=\"" + ( apparentHandleLocation.getY( ) + 9.0 * handleRadius + yOffset ) + "\" " );
			sb.append( "fill=\"" + SvgUtilities.formatColor( ( e.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.getEdgeColor( e.color.get( ) ), UserSettings.instance.selectedEdge.get( ) ) : UserSettings.instance.getEdgeColor( e.color.get( ) ) ) ) + "\">" + SvgUtilities.formatString( String.format( "%." + UserSettings.instance.edgeWeightPrecision.get( ) + "f", e.weight.get( ) ) ) + "</text>\r\n" );
		}
		
		return sb.toString( );
	}
}
