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
	public static String format(Caption c, Palette p, GraphSettings s)
	{
		StringBuilder sb = new StringBuilder();
		
		String[] lines = c.text.get().split("\\n");
		for(int i = 0; i < lines.length; ++i)
		{	
			sb.append("<text ");
			sb.append("x=\"" + c.x.get() + "\" ");
			sb.append("y=\"" + (c.y.get() + i * 12) + "\" ");
			sb.append("fill=\"" + SvgUtilities.formatColor(c.isSelected.get() ? p.selectedCaptionText.get() : p.captionText.get()) + "\">" + SvgUtilities.formatString(lines[i]) + "</text>\r\n");
		}
		
		return sb.toString();
	}
}
