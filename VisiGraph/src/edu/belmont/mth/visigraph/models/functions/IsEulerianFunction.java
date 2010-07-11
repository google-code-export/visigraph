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
public class IsEulerianFunction extends FunctionBase
{
	public Object evaluate(Graphics2D g2D, Graph g)
	{
		for(Vertex vertex : g.vertexes)
			if(g.getEdges(vertex).size() % 2 == 1)
				return false;
		
		return (new IsConnectedFunction()).evaluate(g2D, g);
	}

	public String getDescription()
	{
		return "Is Eulerian";
	}	
}
