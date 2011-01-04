/**
 * GraphSvgView.java
 */
package edu.belmont.mth.visigraph.views.svg;

import java.awt.geom.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.views.display.*;

/**
 * @author Cameron Behar
 */
public class GraphSvgView
{
	public static String format( Graph g, GraphSettings s )
	{
		StringBuilder sb = new StringBuilder( );
		Rectangle2D rect = GraphDisplayView.getBounds( g );
		
		sb.append( "<?xml version=\"1.0\" standalone=\"no\"?>\r\n" );
		sb.append( "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\r\n" );
		sb.append( String.format( "<svg width=\"%1$s\" height=\"%2$s\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">\r\n", rect.getWidth( ) + UserSettings.instance.zoomGraphPadding.get( ), rect.getHeight( ) + UserSettings.instance.zoomGraphPadding.get( ) ) );
		
		for( Edge edge : g.edges )
			sb.append( EdgeSvgView.format( edge, s, UserSettings.instance.zoomGraphPadding.get( ) / 2.0 - rect.getX( ), UserSettings.instance.zoomGraphPadding.get( ) / 2.0 - rect.getY( ) ) );
		
		for( Vertex vertex : g.vertices )
			sb.append( VertexSvgView.format( vertex, s, UserSettings.instance.zoomGraphPadding.get( ) / 2.0 - rect.getX( ), UserSettings.instance.zoomGraphPadding.get( ) / 2.0 - rect.getY( ) ) );
		
		for( Caption caption : g.captions )
			sb.append( CaptionSvgView.format( caption, s, UserSettings.instance.zoomGraphPadding.get( ) / 2.0 - rect.getX( ), UserSettings.instance.zoomGraphPadding.get( ) / 2.0 - rect.getY( ) ) );
		
		sb.append( "\n</svg>" );
		
		return sb.toString( );
	}
}
