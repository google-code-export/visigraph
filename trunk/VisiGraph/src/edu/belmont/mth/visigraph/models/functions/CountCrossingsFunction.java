/**
 * CountCrossingsFunction.java
 */
package edu.belmont.mth.visigraph.models.functions;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.GlobalSettings;
import edu.belmont.mth.visigraph.utilities.GeometryUtilities;

/**
 * @author Cameron Behar
 *
 */
public class CountCrossingsFunction extends FunctionBase
{
	public Object evaluate(Graphics2D g2D, Palette p, Graph g)
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
		
		Vector<Point2D> crossings = new Vector<Point2D>();
		
		for (int i = 0; i < edges.size(); ++i)
			for (int j = i + 1; j < edges.size(); ++j)
				crossings.addAll(getCrossings(edges.get(i), edges.get(j)));
		
		if(g2D != null)
		{
			g2D.setColor(GlobalSettings.defaultCrossingDisplayColor);
		
			for(Point2D crossing : crossings)
				g2D.fill(new Ellipse2D.Double(crossing.getX() - GlobalSettings.defaultCrossingRadius, crossing.getY() - GlobalSettings.defaultCrossingRadius, 2.0 * GlobalSettings.defaultCrossingRadius, 2.0 * GlobalSettings.defaultCrossingRadius));
		}
		
		return crossings.size();
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
}
