/**
 * EdgeDisplayView.java
 */
package edu.belmont.mth.visigraph.views;

import java.awt.*;
import java.awt.geom.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;

/**
 * @author Cameron Behar
 *
 */
public class EdgeDisplayView
{	
	public static void paintEdge(Graphics2D g2D, Palette p, GraphDisplaySettings s, Edge e)
	{
		// Decide where we should draw the handle and/or arrow head
		Point2D apparentHandleLocation = e.isLinear() ? GeometryUtilities.midpoint(e.from, e.to) : e.getHandlePoint2D();
		double handleRadius = e.thickness.get() * GlobalSettings.defaultEdgeHandleRadiusRatio;
		
		Stroke oldStroke = g2D.getStroke();
		
		// Set the edge-specific stroke
		g2D.setStroke(new BasicStroke(e.thickness.get().floatValue()));
		g2D.setPaint(e.isSelected.get() ? p.selectedEdgeLine.get() : p.getElementColor(e.color.get()));

		// Draw the edge
		if (e.isLinear())
			g2D.draw(e.getLine());
		else
			g2D.draw(e.getArc());
		
		// Return the stroke to what it was before
		g2D.setStroke(oldStroke);
		
		// Draw handle
		if(s.showEdgeHandles.get())
			g2D.fill(new Ellipse2D.Double(apparentHandleLocation.getX() - handleRadius, apparentHandleLocation.getY() - handleRadius, handleRadius * 2.0, handleRadius * 2.0));
		
		// Draw arrow head for directed edges
		if(e.isDirected)
		{
			Point2D.Double[] arrowPoint = new Point2D.Double[3];
			double tangentAngle;
			
			if(e.isLinear())
				tangentAngle = Math.atan2(e.to.y.get() - e.from.y.get(), e.to.x.get() - e.from.x.get());
			else
			{
				tangentAngle = Math.atan2(e.handleY.get() - e.getCenter().getY(), e.handleX.get() - e.getCenter().getX());
				
				// We need to calculate these angles so that we know in which order whether to flip the angle by 180 degrees
				double fromAngle = Math.atan2(e.from.y.get() - e.getCenter().getY(), e.from.x.get() - e.getCenter().getX());
				double toAngle = Math.atan2(e.to.y.get() - e.getCenter().getY(), e.to.x.get() - e.getCenter().getX());
				
				if (GeometryUtilities.angleBetween(fromAngle, tangentAngle) >= GeometryUtilities.angleBetween(fromAngle, toAngle))
					tangentAngle += Math.PI;
				
				tangentAngle += Math.PI / 2;
			}
			
			// 
			for(int i = 0; i < 3; ++i)
			{
				double theta = tangentAngle + i * 2.0 * Math.PI / 3.0;
				arrowPoint[i] = new Point2D.Double(GlobalSettings.defaultDirectedEdgeArrowSize * Math.cos(theta) + apparentHandleLocation.getX(), GlobalSettings.defaultDirectedEdgeArrowSize * Math.sin(theta) + apparentHandleLocation.getY());
			}
			
			Path2D.Double path = new Path2D.Double();
			path.moveTo(arrowPoint[0].x, arrowPoint[0].y);
			path.lineTo(arrowPoint[1].x, arrowPoint[1].y);
			path.lineTo(arrowPoint[2].x, arrowPoint[2].y);
			path.lineTo(arrowPoint[0].x, arrowPoint[0].y);
			g2D.fill(path);
		}

		// Draw edge label
		if(s.showEdgeLabels.get())
			g2D.drawString(e.label.get(), (float)(e.handleX.get() + handleRadius + 1), (float)(e.handleY.get() - handleRadius - 1));

		// Draw edge weight label
		if(s.showEdgeWeights.get())
			g2D.drawString(e.weight.get().toString(), (float)(e.handleX.get() - handleRadius - 12), (float)(e.handleY.get() + handleRadius + 12));
	}
	
	public static boolean wasClicked(Edge edge, Point point, double scale)
	{
		return (Point2D.distance(edge.handleX.get(), edge.handleY.get(), point.x, point.y) <= edge.thickness.get() * GlobalSettings.defaultEdgeHandleRadiusRatio + GlobalSettings.defaultEdgeHandleClickMargin / scale);
	}
	
	public static boolean wasSelected(Edge edge, Rectangle selection)
	{
		return selection.contains(edge.handleX.get(), edge.handleY.get());
	}
}
