/**
 * Caption.java
 */
package edu.belmont.mth.visigraph.models;

import java.util.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;

/**
 * @author Cameron Behar
 *
 */
public class Caption extends ObservableBase
{	
	public final Property<Double> x;
	public final Property<Double> y;
	public final Property<String> text;
	public final Property<Boolean> isSelected;
	
	public Caption(double x, double y)
	{
		this(x, y, GlobalSettings.defaultCaptionText);
	}
	
	public Caption(double x, double y, String text)
	{
		this(x, y, text, GlobalSettings.defaultCaptionIsSelected);
	}
	
	public Caption(double x, double y, String text, boolean isSelected)
	{
		this.x = new Property<Double>(x, "x");
		this.y = new Property<Double>(y, "y");
		this.text = new Property<String>(text, "text");
		this.isSelected = new Property<Boolean>(isSelected, "isSelected");
	}

	public Caption(Map<String, Object> members)
	{
		this.x = new Property<Double>(new Double(members.get("x").toString()), "x");
		this.y = new Property<Double>(new Double(members.get("y").toString()), "y");
		this.text = new Property<String>(members.get("text").toString(), "text");
		this.isSelected = new Property<Boolean>(new Boolean(members.get("isSelected").toString()), "isSelected");
	}
	
	public Caption(String json)
	{
		this(JsonUtilities.parseObject(json));
	}

	public String toString()
	{
		HashMap<String, Object> members = new HashMap<String, Object>();
		
		members.put("x",          x         );
		members.put("y",          y         );
		members.put("text",       text      );
		members.put("isSelected", isSelected);
		
		return JsonUtilities.formatObject(members);
	}
}
