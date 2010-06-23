/**
 * IsEulerianFunction.java
 */
package edu.belmont.mth.visigraph.models.functions;

import java.awt.*;

import edu.belmont.mth.visigraph.models.*;

/**
 * @author Cameron Behar
 *
 */
public class IsEulerianFunction extends AbstractFunction
{
	public Object evaluate(Graphics2D g2D, Palette p, Graph g)
	{
		for(Vertex vertex : g.vertexes)
			if(g.getEdges(vertex).size() % 2 == 1)
				return false;
		
		return (new IsConnectedFunction()).evaluate(g2D, p, g);
	}

	public String getDescription()
	{
		return "Is Eulerian";
	}	
}
