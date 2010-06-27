/**
 * CountVertexesFunction.java
 */
package edu.belmont.mth.visigraph.models.functions;

import java.awt.*;
import edu.belmont.mth.visigraph.models.*;

/**
 * @author Cameron Behar
 *
 */
public class CountVertexesFunction extends FunctionBase
{
	public Object evaluate(Graphics2D g2D, Palette p, Graph g)
	{
		return g.vertexes.size();
	}

	public String getDescription()
	{
		return "Count vertices";
	}	
}
