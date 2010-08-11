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
	public final UUID id;
	public final Property<Double>  x;
	public final Property<Double>  y;
	public final Property<String>  label;
	public final Property<Double>  radius;
	public final Property<Integer> color;
	public final Property<Boolean> isSelected;
	public final Property<Double>  weight; 
	
	public Vertex()
	{
		this(0.0, 0.0);
	}
	
	public Vertex(double x, double y)
	{
		this(x, y, UserSettings.instance.defaultVertexPrefix.get());
	}
	
	public Vertex(double x, double y, String label)
	{
		this(x, y, label, UserSettings.instance.defaultVertexRadius.get());
	}
	
	public Vertex(double x, double y, String label, double radius)
	{
		this(x, y, label, radius, UserSettings.instance.defaultVertexColor.get());
	}
	
	public Vertex(double x, double y, String label, double radius, int color)
	{
		this(x, y, label, radius, color, UserSettings.instance.defaultVertexIsSelected.get());
	}
	
	public Vertex(double x, double y, String label, double radius, int color, boolean isSelected)
	{
		this.id			= UUID.randomUUID();
		this.x          = new Property<Double>(x, "x"); 
		this.y          = new Property<Double>(y, "y");
		this.label      = new Property<String>(label, "label");
		this.radius     = new Property<Double>(radius, "radius");
		this.color      = new Property<Integer>(color, "color");
		this.isSelected = new Property<Boolean>(isSelected, "isSelected");
		this.weight     = new Property<Double>(UserSettings.instance.defaultVertexWeight.get(), "weight");
	}

	public Vertex(Map<String, Object> members)
	{
		this.id			= UUID.randomUUID();
		this.x          = new Property<Double>((Double)members.get("x"), "x");
		this.y          = new Property<Double>((Double)members.get("y"), "y");
		this.label      = new Property<String>(members.get("label").toString(), "label");
		this.radius     = new Property<Double>((Double)members.get("radius"), "radius");
		this.color      = new Property<Integer>((Integer)members.get("color"), "color");
		this.isSelected = new Property<Boolean>((Boolean)members.get("isSelected"), "isSelected");
		this.weight     = new Property<Double>((Double)members.get("weight"), "weight");
	}
	
	public Vertex(String json)
	{
		this(JsonUtilities.parseObject(json));
	}
	
	public Point2D getPoint2D()
	{
		return new Point2D.Double(x.get(), y.get());
	}

	@Override
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
