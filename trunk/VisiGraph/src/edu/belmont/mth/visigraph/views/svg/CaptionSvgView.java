/**
 * CaptionSvgView.java
 */
package edu.belmont.mth.visigraph.views.svg;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;

/**
 * @author Cameron Behar
 *
 */
public class CaptionSvgView
{	
	public static String format(Caption c, GraphSettings s, double xOffset, double yOffset)
	{
		StringBuilder sb = new StringBuilder();
		
		String[] lines = c.text.get().split("\\n");
		for(int i = 0; i < lines.length; ++i)
		{	
			sb.append("<text ");
			sb.append("x=\"" + (c.x.get() + xOffset) + "\" ");
			sb.append("y=\"" + (c.y.get() + (i + 0.9f) * c.size.get() - 10 + yOffset) + "\" ");
			sb.append("style=\"font-size:" + c.size.get() + "pt;\" ");
			sb.append("fill=\"" + SvgUtilities.formatColor(c.isSelected.get() ? ColorUtilities.blend(UserSettings.instance.captionText.get(), UserSettings.instance.selectedCaptionLine.get()) : UserSettings.instance.captionText.get()) + "\">" + SvgUtilities.formatString(lines[i]) + "</text>\r\n");
		}
		
		return sb.toString();
	}
}
