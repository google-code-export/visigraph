/**
 * CountCrossingsFunction.java
 */
package edu.belmont.mth.visigraph.models.functions;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.utilities.*;

/**
 * @author Cameron Behar
 *
 */
public class CountCrossingsFunction extends FunctionBase
{
	public Object evaluate(Graphics2D g2D, Graph g)
	{
		Vector<Edge> edges = new Vector<Edge>();
		
		for(Edge edge : g.edges)
			if(edge.isSelected.get())
				edges.add(edge);
		
		if(edges.size() < 2)
		{
			edges.clear();
			edges.addAll(g.edges);
		}
		
		int crossingsCount = 0;
		Vector<CrossingMarker> crossingMarkers = new Vector<CrossingMarker>();
		
		for (int i = 0; i < edges.size(); ++i)
			for (int j = i + 1; j < edges.size(); ++j)
			{
				Vector<Point2D> crossings = getCrossings(edges.get(i), edges.get(j));
				crossingsCount += crossings.size();
				if(crossings.size() > 0)
					crossingMarkers.add(new CrossingMarker(crossings, (edges.get(i).thickness.get() + edges.get(j).thickness.get()) / 2.0));
			}
		
		if(g2D != null)
		{
			g2D.setColor(Color.red);
		
			for(CrossingMarker crossing : crossingMarkers)
			{
				double markerRadii = crossing.getThickness() * 1.5;
				for(Point2D location : crossing.getLocations())
					g2D.fill(new Ellipse2D.Double(location.getX() - markerRadii, location.getY() - markerRadii, 2.0 * markerRadii, 2.0 * markerRadii));
			}
		}
		
		return crossingsCount;
	}

	public String getDescription()
	{
		return "Count crossings";
	}	

	private static Vector<Point2D> getCrossings(Edge e0, Edge e1)
	{
		// Note: we do not count crossings between adjacent (coincident) edges as they are unnecessary
		if (e0.isAdjacent(e1))
		{
			return new Vector<Point2D>();
		}
		else if (e0.isLinear())
		{
			if (e1.isLinear())
				return GeometryUtilities.getCrossings(e0.getLine(), e1.getLine());
			else
				return GeometryUtilities.getCrossings(e0.getLine(), e1.getArc(), e1.getCenter());
		}
		else if (e1.isLinear())
			return GeometryUtilities.getCrossings(e1.getLine(), e0.getArc(), e0.getCenter());
		else
			return GeometryUtilities.getCrossings(e0.getArc(), e0.getCenter(), e1.getArc(), e1.getCenter());
	}

	private class CrossingMarker
	{
		private Vector<Point2D> locations;
		private double          thickness;
		
		public CrossingMarker(Vector<Point2D> locations, double thickness)
		{
			setLocations(locations);
			setThickness(thickness);
		}
		
		public Vector<Point2D> getLocations()
		{
			return locations;
		}
		
		public void setLocations(Vector<Point2D> locations)
		{
			this.locations = locations;
		}
		
		public double getThickness()
		{
			return thickness;
		}
		
		public void setThickness(double radius)
		{
			this.thickness = radius;
		}
	}
}
