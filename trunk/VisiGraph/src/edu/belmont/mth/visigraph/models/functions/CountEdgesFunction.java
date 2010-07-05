/**
 * CountEdgesFunction.java
 */
package edu.belmont.mth.visigraph.models.functions;

import java.awt.*;
import edu.belmont.mth.visigraph.models.*;

/**
 * @author Cameron Behar
 *
 */
public class CountEdgesFunction extends FunctionBase
{
	public Object evaluate(Graphics2D g2D, Palette p, Graph g)
	{
		int selectedEdgeCount = 0;
		
		for(Edge edge : g.edges)
			if(edge.isSelected.get())
				++selectedEdgeCount;
		
		return (selectedEdgeCount > 0 ? selectedEdgeCount : g.edges.size());
	}

	public String getDescription()
	{
		return "Count edges";
	}	
}
