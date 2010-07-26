/**
 * Edge.java
 */
package edu.belmont.mth.visigraph.models;

import java.awt.geom.*;
import java.util.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.views.*;
import static edu.belmont.mth.visigraph.utilities.GeometryUtilities.*;

/**
 * @author Cameron Behar
 * 
 */
public class Edge extends ObservableBase
{
	public final boolean			isDirected;
	public final Vertex				from;
	public final Vertex				to;
	public final Property<Double>	weight;
	public final Property<Integer>	color;
	public final Property<String>	label;
	public final Property<Boolean>	isSelected;
	public final Property<Double>	thickness;
	public final Property<Double>	handleX;
	public final Property<Double>	handleY;
	
	private Arc2D.Double			arc;
	private Line2D.Double			line;
	private Point2D.Double			center;
	private boolean					isLinear;
	private boolean					notificationsSuspended;
	private ObserverBase			vertexObserver	= new ObserverBase()
													{
														@Override
														public void hasChanged(Object source)
														{
															// Only reset the edge if the one of the vertexes moved
															if (!notificationsSuspended && from != null && to != null && handleX != null && handleY != null && source instanceof Property<?>)
															{
																Property<?> propertyChanged = (Property<?>)source;
																
																if(propertyChanged == from.x || propertyChanged == from.y || propertyChanged == to.x || propertyChanged == to.y)
																{
																	if(!isLinear)
																	{
																		Point2D midpoint = midpoint(line.x1, line.y1, line.x2, line.y2);
																		
																		double distance = line.getP1().distance(line.getP2());
																		double lineAngle = angle(line.y2 - line.y1, line.x2 - line.x1);
																		
																		double handleAngle = angle(handleY.get() - midpoint.getY(), handleX.get() - midpoint.getX()) - lineAngle;
																		double handleRadiusRatio = midpoint.distance(handleX.get(), handleY.get()) / distance; 
																		
																		Point2D newMidpoint = midpoint(from.x.get(), from.y.get(), to.x.get(), to.y.get());
																		
																		double newDistance = distance(from, to);
																		double newLineAngle   = angle(to.y.get() - from.y.get(), to.x.get() - from.x.get());
																		
																		double newHandleAngle = handleAngle + newLineAngle;
																		double newHandleRadius = handleRadiusRatio * newDistance;
																		
																		suspendNotifications(true);
																		handleX.set(newMidpoint.getX() + newHandleRadius * Math.cos(newHandleAngle));
																		handleY.set(newMidpoint.getY() + newHandleRadius * Math.sin(newHandleAngle));
																		suspendNotifications(false);
																	}
																	
																	fixHandle();
																}
															}
														}
													};
	
	public Edge(boolean isDirected, Vertex from, Vertex to)
	{
		this(isDirected, from, to, UserSettings.instance.defaultEdgeWeight.get());
	}
	
	public Edge(boolean isDirected, Vertex from, Vertex to, double weight)
	{
		this(isDirected, from, to, weight, UserSettings.instance.defaultEdgeColor.get());
	}
	
	public Edge(boolean isDirected, Vertex from, Vertex to, double weight, int color)
	{
		this(isDirected, from, to, weight, color, UserSettings.instance.defaultEdgePrefix.get());
	}
	
	public Edge(boolean isDirected, Vertex from, Vertex to, double weight, int color, String label)
	{
		this(isDirected, from, to, weight, color, label, UserSettings.instance.defaultEdgeIsSelected.get());
	}
	
	public Edge(boolean isDirected, Vertex from, Vertex to, double weight, int color, String label, boolean isSelected)
	{
		notificationsSuspended = true;
		
		this.isDirected = isDirected;
		
		this.from = from;
		this.from.addObserver(vertexObserver);
		
		this.to = to;
		this.to.addObserver(vertexObserver);
		
		this.weight = new Property<Double>(weight, "weight");
		this.color = new Property<Integer>(color, "color");
		this.label = new Property<String>(label, "label");
		this.isSelected = new Property<Boolean>(isSelected, "isSelected");
		this.thickness = new Property<Double>(UserSettings.instance.defaultEdgeThickness.get(), "thickness");
		
		this.handleX = new Property<Double>(0.0, "handleX")
		{
			@Override
			public void set(final Double value)
			{
				if (super.get() == null || !super.get().equals(value))
				{
					super.set(value);
					refresh();
				}
			}
		};
		this.handleY = new Property<Double>(0.0, "handleY")
		{
			@Override
			public void set(final Double value)
			{
				if (super.get() == null || !super.get().equals(value))
				{
					super.set(value);
					refresh();
				}
			}
		};
		
		this.arc = new Arc2D.Double();
		this.line = new Line2D.Double();
		this.center = new Point2D.Double();
		
		notificationsSuspended = false;
		
		isLinear = true;
		fixHandle();
		refresh();
	}
	
	public Edge(Map<String, Object> members, Map<Integer, Vertex> vertexes)
	{
		notificationsSuspended = true;
		
		this.isDirected = (Boolean)members.get("isDirected");
		
		this.from = vertexes.get((Integer)members.get("from.id"));
		this.from.addObserver(vertexObserver);
		
		this.to = vertexes.get((Integer)members.get("to.id"));
		this.to.addObserver(vertexObserver);
		
		this.weight = new Property<Double>((Double)members.get("weight"), "weight");
		this.color = new Property<Integer>((Integer)members.get("color"), "color");
		this.label = new Property<String>((String)members.get("label"), "label");
		this.isSelected = new Property<Boolean>((Boolean)members.get("isSelected"), "isSelected");
		this.thickness = new Property<Double>((Double)members.get("thickness"), "thickness");
		
		this.isLinear = members.containsKey("isLinear") ? (Boolean)members.get("isLinear") : false;
		this.handleX = new Property<Double>(this.isLinear ? 0.0 : (Double)members.get("handleX"), "handleX")
		{
			@Override
			public void set(final Double value)
			{
				if (super.get() == null || !super.get().equals(value))
				{
					super.set(value);
					refresh();
				}
			}
		};
		this.handleY = new Property<Double>(this.isLinear ? 0.0 : (Double)members.get("handleY"), "handleY")
		{
			@Override
			public void set(final Double value)
			{
				if (super.get() == null || !super.get().equals(value))
				{
					super.set(value);
					refresh();
				}
			}
		};
		
		this.arc = new Arc2D.Double();
		this.line = new Line2D.Double();
		this.center = new Point2D.Double();
		
		notificationsSuspended = false;
		
		fixHandle();
		refresh();
	}
	
	public Edge(String json, Map<Integer, Vertex> vertexes)
	{
		this(JsonUtilities.parseObject(json), vertexes);
	}
	
	public void fixHandle()
	{
		if(from == to)
		{
			// Reset a loop handle
			handleX.set(from.x.get() + UserSettings.instance.defaultLoopDiameter.get());
			handleY.set(from.y.get());
		}
		else if(isLinear)
		{
			// Reset a standard edge handle
			Point2D midpoint = midpoint(from.x.get(), from.y.get(), to.x.get(), to.y.get());
			handleX.set(midpoint.getX());
			handleY.set(midpoint.getY());
		}
		
		refresh();
	}
	
	public Arc2D getArc()
	{
		return (Arc2D) arc.clone();
	}
	
	public Point2D getCenter()
	{
		return (Point2D) center.clone();
	}
	
	public Point2D getHandlePoint2D()
	{
		return new Point2D.Double(handleX.get(), handleY.get());
	}
	
	public Line2D getLine()
	{
		return (Line2D) line.clone();
	}
	
	public boolean isAdjacent(Edge e)
	{
		// Equivalent to isCoincident, tests whether the two edges share a vertex
		return from == e.from || from == e.to || to == e.from || to == e.to;
	}
	
	public boolean isLinear()
	{
		return isLinear;
	}
	
	public void refresh()
	{
		if (!notificationsSuspended && handleX != null && handleY != null)
		{
			// Determine linearity
			line = new Line2D.Double(from.x.get(), from.y.get(), to.x.get(), to.y.get());
			double handleDistanceFromLine = line.ptLineDist(handleX.get(), handleY.get());
			double snapToLineMargin = UserSettings.instance.edgeSnapMarginRatio.get() * distance(from, to);
			isLinear = (handleDistanceFromLine <= snapToLineMargin);
			
			if (!isLinear)
			{
				updateCenter();
				updateArc();
			}
		}
	}
	
	public boolean suspendNotifications(boolean s)
	{
		boolean ret = notificationsSuspended;
		notificationsSuspended = s;
		return ret;
	}
	
	private void updateArc()
	{
		double radius = center.distance(from.x.get(), from.y.get());
		
		arc.setFrameFromCenter(center, new Point2D.Double(center.x - radius, center.y - radius));
		
		// We need to calculate these angles so that we know in which order to pass the from and to points because the sector will always be drawn counter-clockwise
		double fromAngle   = angle(-(from.y.get()  - center.y), from.x.get()  - center.x);
		double handleAngle = angle(-(handleY.get() - center.y), handleX.get() - center.x);
		double toAngle     = angle(-(to.y.get()    - center.y), to.x.get()    - center.x);
		
		if (angleBetween(fromAngle, handleAngle) < angleBetween(fromAngle, toAngle))
			arc.setAngles(from.getPoint2D(), to.getPoint2D());
		else
			arc.setAngles(to.getPoint2D(), from.getPoint2D());
	}
	
	private void updateCenter()
	{
		if(from == to)
		{
			center.setLocation(midpoint(from.x.get(), from.y.get(), handleX.get(), handleY.get()));
		}
		else
		{
			double x0 = from.x.get(), y0 = from.y.get();
			double x1 = handleX.get(), y1 = handleY.get();
			double x2 = to.x.get(), y2 = to.y.get();
			
			double h = determinant(new double[][] {{ x0 * x0 + y0 * y0, y0, 1 }, 
												   { x1 * x1 + y1 * y1, y1, 1 }, 
												   { x2 * x2 + y2 * y2, y2, 1 } }) / 
												   (2.0 * determinant(new double[][] {{ x0, y0, 1 }, 
														   							  { x1, y1, 1 }, 
														   							  { x2, y2, 1 } }));
			
			double k = determinant(new double[][] {{ x0, x0 * x0 + y0 * y0, 1 }, 
												   { x1, x1 * x1 + y1 * y1, 1 }, 
												   { x2, x2 * x2 + y2 * y2, 1 } }) / 
												   (2.0 * determinant(new double[][] {{ x0, y0, 1 }, 
														   							  { x1, y1, 1 }, 
														   							  { x2, y2, 1 } }));
			
			center.setLocation(h, k);
		}
	}

	@Override
	public String toString()
	{
		HashMap<String, Object> members = new HashMap<String, Object>();
		
		members.put("isDirected",   isDirected  );
		members.put("from.id",      from.id     );
		members.put("to.id",        to.id       );
		members.put("weight",       weight      );
		members.put("color",        color       );
		members.put("label",        label       );
		members.put("isSelected",   isSelected  );
		members.put("thickness",    thickness   );
		members.put("isLinear",     isLinear    );
		
		if(!isLinear)
		{
			members.put("handleX",  handleX     );
			members.put("handleY",  handleY     );
		}
		
		return JsonUtilities.formatObject(members);
	}
}


















