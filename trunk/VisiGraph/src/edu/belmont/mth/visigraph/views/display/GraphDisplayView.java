/**
 * GraphDisplayView.java
 */
package edu.belmont.mth.visigraph.views.display;

import java.awt.*;
import java.awt.geom.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;

/**
 * @author Cameron Behar
 * 
 */
public class GraphDisplayView
{
	public static void paint(Graphics2D g2D, Graph graph, GraphSettings s)
	{
		// Draw all the edges first
		for(Edge edge : graph.edges)
			EdgeDisplayView.paintEdge(g2D, s, edge);
		
		// Then draw all the vertexes
		for(Vertex vertex : graph.vertexes)
			VertexDisplayView.paint(g2D, s, vertex);
		
		// Then draw all the captions
		if(s.showCaptions.get())
			for(Caption caption : graph.captions)
				CaptionDisplayView.paint(g2D, s, caption);
	}
	
	public static Rectangle2D getBounds(Graph graph)
	{
		if(graph.vertexes.size() <= 0)
			return null;
		
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
		
		for(Vertex vertex : graph.vertexes)
		{
			if(vertex.x.get() < minX)
				minX = vertex.x.get();
			
			if(vertex.y.get() < minY)
				minY = vertex.y.get();
			
			if(vertex.x.get() > maxX)
				maxX = vertex.x.get();
			
			if(vertex.y.get() > maxY)
				maxY = vertex.y.get();
		}
		
		return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
	}
}






