/**
 * VertexDisplayView.java
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
public class VertexDisplayView
{	
	public static void paint(Graphics2D g2D, GraphSettings s, Vertex v)
	{
		// Draw vertex center
		Ellipse2D.Double center = new Ellipse2D.Double(v.x.get() - v.radius.get(), v.y.get() - v.radius.get(), v.radius.get() * 2, v.radius.get() * 2);
		g2D.setPaint((v.isSelected.get() && v.color.get() < 0) ? UserSettings.instance.selectedVertexFill.get() : UserSettings.instance.getElementColor(v.color.get()));
		g2D.fill(center);
		
		// Draw vertex outline
		Ellipse2D.Double outline = new Ellipse2D.Double(v.x.get() - v.radius.get(), v.y.get() - v.radius.get(), v.radius.get() * 2, v.radius.get() * 2);
		g2D.setPaint(v.isSelected.get() ? UserSettings.instance.selectedVertexLine.get() : UserSettings.instance.vertexLine.get());
		g2D.draw(outline);
		
		// Compute label offset
		double offset = v.radius.get() / Math.sqrt(2.0) + 1;
		
		// Draw label
		if(s.showVertexLabels.get())
			g2D.drawString(v.label.get(), (float)(v.x.get() + offset), (float)(v.y.get() + offset + 10));
		
		// Draw weight
		if(s.showVertexWeights.get())
			g2D.drawString(v.weight.get().toString(), (float)(v.x.get() + v.radius.get() + 3f), v.y.get().floatValue());
	}

	public static boolean wasClicked(Vertex vertex, Point point, double scale)
	{
		return (Point2D.distance(vertex.x.get(), vertex.y.get(), point.x, point.y) <= vertex.radius.get() + UserSettings.instance.vertexClickMargin.get() / scale);
	}
	
	public static boolean wasSelected(Vertex vertex, Rectangle selection)
	{
		return selection.contains(vertex.x.get(), vertex.y.get());
	}
}
