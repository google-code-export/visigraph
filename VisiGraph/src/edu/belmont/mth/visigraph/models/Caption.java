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
		this(x, y, UserSettings.instance.defaultCaptionText.get());
	}
	
	public Caption(double x, double y, String text)
	{
		this(x, y, text, UserSettings.instance.defaultCaptionIsSelected.get());
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
		this.x = new Property<Double>((Double)members.get("x"), "x");
		this.y = new Property<Double>((Double)members.get("y"), "y");
		this.text = new Property<String>((String)members.get("text"), "text");
		this.isSelected = new Property<Boolean>((Boolean)members.get("isSelected"), "isSelected");
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
