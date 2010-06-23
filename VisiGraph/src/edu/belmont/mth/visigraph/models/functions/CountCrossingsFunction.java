/**
 * CountCrossingsFunction.java
 */
package edu.belmont.mth.visigraph.models.functions;

import java.awt.*;
import java.awt.geom.*;
import java.util.Vector;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.GlobalSettings;

/**
 * @author Cameron Behar
 *
 */
public class CountCrossingsFunction extends AbstractFunction
{
	public Object evaluate(Graphics2D g2D, Palette p, Graph g)
	{
		Vector<Point2D> crossings = g.getCrossings();
		
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
}
