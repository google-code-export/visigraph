/**
 * Vertex.java
 */
package edu.belmont.mth.visigraph.models;

import java.awt.geom.*;
import java.util.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;

/**
 * @author Cameron Behar
 *
 */
public class Vertex extends ObservableBase
{	
	public final int               id;
	public final Property<Double>  x;
	public final Property<Double>  y;
	public final Property<String>  label;
	public final Property<Double>  radius;
	public final Property<Integer> color;
	public final Property<Boolean> isSelected;
	public final Property<Double>  weight; 
	
	public Vertex(int id)
	{
		this(id, GlobalSettings.defaultVertexLocation.getX(), GlobalSettings.defaultVertexLocation.getY());
	}
	
	public Vertex(int id, double x, double y)
	{
		this(id, x, y, GlobalSettings.defaultVertexPrefix + id);
	}
	
	public Vertex(int id, double x, double y, String label)
	{
		this(id, x, y, label, GlobalSettings.defaultVertexRadius);
	}
	
	public Vertex(int id, double x, double y, String label, double radius)
	{
		this(id, x, y, label, radius, GlobalSettings.defaultVertexColor);
	}
	
	public Vertex(int id, double x, double y, String label, double radius, int color)
	{
		this(id, x, y, label, radius, color, GlobalSettings.defaultVertexIsSelected);
	}
	
	public Vertex(int id, double x, double y, String label, double radius, int color, boolean isSelected)
	{
		this.id = id;
		this.x = new Property<Double>(x, "x"); 
		this.y = new Property<Double>(y, "y");
		this.label = new Property<String>(label, "label");
		this.radius = new Property<Double>(radius, "radius");
		this.color = new Property<Integer>(color, "color");
		this.isSelected = new Property<Boolean>(isSelected, "isSelected");
		this.weight = new Property<Double>(GlobalSettings.defaultVertexWeight, "weight");
	}

	public Vertex(Map<String, Object> members)
	{
		this.id = new Integer(members.get("id").toString());
		this.x = new Property<Double>(new Double(members.get("x").toString()), "x");
		this.y = new Property<Double>(new Double(members.get("y").toString()), "y");
		this.label = new Property<String>(members.get("label").toString(), "label");
		this.radius = new Property<Double>(new Double(members.get("radius").toString()), "radius");
		this.color = new Property<Integer>(new Integer(members.get("color").toString()), "color");
		this.isSelected = new Property<Boolean>(new Boolean(members.get("isSelected").toString()), "isSelected");
		this.weight = new Property<Double>(new Double(members.get("weight").toString()), "weight");
	}
	
	public Vertex(String json)
	{
		this(JsonUtilities.parseObject(json));
	}
	
	public Point2D getPoint2D()
	{
		return new Point2D.Double(x.get(), y.get());
	}

	public String toString()
	{
		HashMap<String, Object> members = new HashMap<String, Object>();
		
		members.put("id",         id        );
		members.put("x",          x         );
		members.put("y",          y         );
		members.put("label",      label     );
		members.put("radius",     radius    );
		members.put("color",      color     );
		members.put("isSelected", isSelected);
		members.put("weight",     weight    );
		
		return JsonUtilities.formatObject(members);
	}
}
