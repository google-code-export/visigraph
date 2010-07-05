/**
 * GraphSvgView.java
 */
package edu.belmont.mth.visigraph.views.svg;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;

/**
 * @author Cameron Behar
 *
 */
public class GraphSvgView
{	
	public static String format(Graph g, Palette p, GraphSettings s)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<?xml version=\"1.0\" standalone=\"no\"?>\r\n");
		sb.append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\r\n");
		sb.append("<svg width=\"100%\" height=\"100%\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">\r\n");
		
		for(Edge edge : g.edges)
			sb.append(EdgeSvgView.format(edge, p, s));
		
		for(Vertex vertex : g.vertexes)
			sb.append(VertexSvgView.format(vertex, p, s));
		
		for(Caption caption : g.captions)
			sb.append(CaptionSvgView.format(caption, p, s));
		
		sb.append("\n</svg>");
		
		return sb.toString();
	}
}
