/**
 * GraphSvgView.java
 */
package edu.belmont.mth.visigraph.views.svg;

import java.awt.geom.Rectangle2D;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.views.display.GraphDisplayView;

/**
 * @author Cameron Behar
 *
 */
public class GraphSvgView
{	
	public static String format(Graph g, GraphSettings s)
	{
		StringBuilder sb = new StringBuilder();
		UserSettings userSettings = UserSettings.instance;
		Rectangle2D rect = GraphDisplayView.getBounds(g);
		
		sb.append("<?xml version=\"1.0\" standalone=\"no\"?>\r\n");
		sb.append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\r\n");
		sb.append(String.format("<svg width=\"%1$s\" height=\"%2$s\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">\r\n", rect.getWidth() + 2 * userSettings.zoomGraphPadding.get(), rect.getHeight() + 2 * userSettings.zoomGraphPadding.get()));
		
		for(Edge edge : g.edges)
			sb.append(EdgeSvgView.format(edge, s, userSettings.zoomGraphPadding.get() - rect.getX(), userSettings.zoomGraphPadding.get() - rect.getY()));
		
		for(Vertex vertex : g.vertexes)
			sb.append(VertexSvgView.format(vertex, s, userSettings.zoomGraphPadding.get() - rect.getX(), userSettings.zoomGraphPadding.get() - rect.getY()));
		
		for(Caption caption : g.captions)
			sb.append(CaptionSvgView.format(caption, s, userSettings.zoomGraphPadding.get() - rect.getX(), userSettings.zoomGraphPadding.get() - rect.getY()));
		
		sb.append("\n</svg>");
		
		return sb.toString();
	}
}
