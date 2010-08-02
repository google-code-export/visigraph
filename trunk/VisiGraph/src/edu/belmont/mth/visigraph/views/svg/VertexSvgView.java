/**
 * VertexSvgView.java
 */
package edu.belmont.mth.visigraph.views.svg;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;

/**
 * @author Cameron Behar
 *
 */
public class VertexSvgView
{	
	public static String format(Vertex v, GraphSettings s, double xOffset, double yOffset)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<circle ");
		sb.append("cx=\"" + (v.x.get() + xOffset) + "\" ");
		sb.append("cy=\"" + (v.y.get() + yOffset) + "\" ");
		sb.append("r=\"" + v.radius.get() + "\" ");
		sb.append("stroke=\"" + SvgUtilities.formatColor(v.isSelected.get() ? ColorUtilities.blend(UserSettings.instance.vertexLine.get(), UserSettings.instance.selectedVertexLine.get()) : UserSettings.instance.vertexLine.get()) + "\" ");
		sb.append("stroke-width=\"1\" ");
		sb.append("fill=\"" + SvgUtilities.formatColor(v.isSelected.get() ? ColorUtilities.blend(UserSettings.instance.getElementColor(v.color.get()), UserSettings.instance.selectedVertexFill.get()) : UserSettings.instance.getElementColor(v.color.get())) + "\" />\r\n");
		
		double offset = v.radius.get() / Math.sqrt(2.0) + 1;
		
		if(s.showVertexLabels.get())
		{
			sb.append("<text ");
			sb.append("x=\"" + (v.x.get() + offset + xOffset) + "\" ");
			sb.append("y=\"" + (v.y.get() + offset + 10 + yOffset) + "\" ");
			sb.append("fill=\"" + SvgUtilities.formatColor(v.isSelected.get() ? ColorUtilities.blend(UserSettings.instance.vertexLine.get(), UserSettings.instance.selectedVertexLine.get()) : UserSettings.instance.vertexLine.get()) + "\">" + SvgUtilities.formatString(v.label.get()) + "</text>\r\n");
		}
		
		if(s.showVertexWeights.get())
		{
			sb.append("<text ");
			sb.append("x=\"" + (v.x.get() + v.radius.get() + 3f + xOffset) + "\" ");
			sb.append("y=\"" + (v.y.get() + yOffset) + "\" ");
			sb.append("fill=\"" + SvgUtilities.formatColor(v.isSelected.get() ? ColorUtilities.blend(UserSettings.instance.vertexLine.get(), UserSettings.instance.selectedVertexLine.get()) : UserSettings.instance.vertexLine.get()) + "\">" + SvgUtilities.formatString(v.weight.get().toString()) + "</text>\r\n");
		}
		
		return sb.toString();
	}
}
