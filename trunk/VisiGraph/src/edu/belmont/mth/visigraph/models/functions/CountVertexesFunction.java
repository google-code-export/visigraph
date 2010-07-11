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
	public Object evaluate(Graphics2D g2D, Graph g)
	{
		int selectedVertexCount = 0;
		
		for(Vertex vertex : g.vertexes)
			if(vertex.isSelected.get())
				++selectedVertexCount;
		
		return (selectedVertexCount > 0 ? selectedVertexCount : g.vertexes.size());
	}

	public String getDescription()
	{
		return "Count vertices";
	}	
}
