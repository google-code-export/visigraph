/**
 * Vertex.java
 */
package edu.belmont.mth.visigraph.models;

import java.awt.geom.*;
import java.util.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.views.ObserverBase;

/**
 * The {@code Vertex} class represents one of the two fundamental mathematical elements in a graph along with edges. When used together with edges,
 * vertices can be related to each other to form a graph.
 * <p/>
 * As an {@link ObservableBase}, this class supports multiple subscribing {@link ObserverBase}s. Whenever a change is made to any of its properties,
 * notification of that change is automatically propagated upwards until it hits the {@code Graph} level and triggers a
 * {@link ObservableBase#notifyObservers(Object)} call.
 * 
 * @author Cameron Behar
 * 
 * @see {@link Graph}, {@link Vertex}, {@link Edge}
 */
public class Vertex extends ObservableBase
{
	/**
	 * The universally unique identifier of this {@code Vertex}, used in the serialization and deserialization of edges
	 */
	public final UUID id;
	
	/**
	 * The location of this {@code Vertex} along the horizontal (x) axis
	 */
	public final Property<Double> x;
	
	/**
	 * The location of this {@code Vertex} along the inverted vertical (-y) axis
	 */
	public final Property<Double> y;
	
	/**
	 * A {@code String} containing this {@code Vertex}'s label
	 */
	public final Property<String> label;
	
	/**
	 * This {@code Vertex}'s radius, in pixels
	 */
	public final Property<Double> radius;
	
	/**
	 * An {@code Integer} representing this {@code Vertex}'s numeric color
	 */
	public final Property<Integer> color;
	
	/**
	 * A {@code Boolean} indicating whether or not this {@code Vertex} is selected
	 */
	public final Property<Boolean> isSelected;
	
	/**
	 * A {@code Double} representing this {@code Vertex}'s numeric weight
	 */
	public final Property<Double> weight;
	
	/**
	 * Constructs a {@code Vertex} at (0.0, 0.0) with the user's default properties
	 */
	public Vertex ( )
	{
		this( 0.0, 0.0 );
	}
	
	/**
	 * Constructs a {@code Vertex} at the specified location
	 * 
	 * @param x the location of this {@code Vertex} along the horizontal (x) axis
	 * @param y the location of this {@code Vertex} along the inverted vertical (-y) axis
	 */
	public Vertex ( double x, double y )
	{
		this( x, y, UserSettings.instance.defaultVertexPrefix.get( ) );
	}
	
	/**
	 * Constructs a {@code Vertex} with the specified label at the specified location
	 * 
	 * @param x the location of this {@code Vertex} along the horizontal (x) axis
	 * @param y the location of this {@code Vertex} along the inverted vertical (-y) axis
	 * @param label a {@code String} containing this {@code Vertex}'s label
	 */
	public Vertex ( double x, double y, String label )
	{
		this( x, y, label, UserSettings.instance.defaultVertexRadius.get( ) );
	}
	
	/**
	 * Constructs a {@code Vertex} with the specified label and radius at the specified location
	 * 
	 * @param x the location of this {@code Vertex} along the horizontal (x) axis
	 * @param y the location of this {@code Vertex} along the inverted vertical (-y) axis
	 * @param label a {@code String} containing this {@code Vertex}'s label
	 * @param radius the radius of this {@code Vertex}, in pixels
	 */
	public Vertex ( double x, double y, String label, double radius )
	{
		this( x, y, label, radius, UserSettings.instance.defaultVertexColor.get( ) );
	}
	
	/**
	 * Constructs a {@code Vertex} with the specified label, radius, and color at the specified location
	 * 
	 * @param x the location of this {@code Vertex} along the horizontal (x) axis
	 * @param y the location of this {@code Vertex} along the inverted vertical (-y) axis
	 * @param label a {@code String} containing this {@code Vertex}'s label
	 * @param radius the radius of this {@code Vertex}, in pixels
	 * @param color the numeric color of this {@code Vertex}
	 */
	public Vertex ( double x, double y, String label, double radius, int color )
	{
		this( x, y, label, radius, color, UserSettings.instance.defaultVertexIsSelected.get( ) );
	}
	
	/**
	 * Constructs a {@code Vertex} with the specified label, radius, color, and selection at the specified location
	 * 
	 * @param x the location of this {@code Vertex} along the horizontal (x) axis
	 * @param y the location of this {@code Vertex} along the inverted vertical (-y) axis
	 * @param label a {@code String} containing this {@code Vertex}'s label
	 * @param radius the radius of this {@code Vertex}, in pixels
	 * @param color the numeric color of this {@code Vertex}
	 * @param isSelected a {@code boolean} indicating whether or not this {@code Vertex} will be initialized selected
	 */
	public Vertex ( double x, double y, String label, double radius, int color, boolean isSelected )
	{
		this.id         = UUID.randomUUID( );
		this.x          = new Property<Double> ( x          );
		this.y          = new Property<Double> ( y          );
		this.label      = new Property<String> ( label      );
		this.radius     = new Property<Double> ( radius     );
		this.color      = new Property<Integer>( color      );
		this.isSelected = new Property<Boolean>( isSelected );
		this.weight     = new Property<Double> ( UserSettings.instance.defaultVertexWeight.get( ) );
	}
	
	/**
	 * Constructs a {@code Vertex} from the specified {@code Map} of properties
	 * 
	 * @param members a {@code Map} of property names to values
	 */
	public Vertex ( Map<String, Object> members )
	{
		this.id         = UUID.randomUUID( );
		this.x          = new Property<Double> ( (Double)  members.get( "x"          ) );
		this.y          = new Property<Double> ( (Double)  members.get( "y"          ) );
		this.label      = new Property<String> ( (String)  members.get( "label"      ) );
		this.radius     = new Property<Double> ( (Double)  members.get( "radius"     ) );
		this.color      = new Property<Integer>( (Integer) members.get( "color"      ) );
		this.isSelected = new Property<Boolean>( (Boolean) members.get( "isSelected" ) );
		this.weight     = new Property<Double> ( (Double)  members.get( "weight"     ) );
	}
	
	/**
	 * Constructs a {@code Vertex} from the specified JSON text
	 * 
	 * @param json the JSON text from which to construct this caption
	 * 
	 * @see #toString()
	 */
	public Vertex ( String json )
	{
		this( JsonUtilities.parseObject( json ) );
	}
	
	/**
	 * Returns this {@code Vertex}'s location as a {@link Point2D} in (x, -y) coordinate space
	 * 
	 * @return this {@code Vertex}'s location
	 */
	public Point2D getPoint2D( )
	{
		return new Point2D.Double( x.get( ), y.get( ) );
	}
	
	/**
	 * Returns a string representation of this vertex in JSON format. This method can be used to serialize a vertex, which may later be deserialized
	 * using the class's string constructor. For more information on the JSON format (used throughout VisiGraph for serialization), visit <a
	 * href="http://www.json.org/">here</a>.
	 * 
	 * @return this {@code Vertex} serialized as a {@code String}
	 * 
	 * @see #Vertex(String)
	 */
	@Override
	public String toString( )
	{
		HashMap<String, Object> members = new HashMap<String, Object>( );
		
		members.put( "id",         id         );
		members.put( "x",          x          );
		members.put( "y",          y          );
		members.put( "label",      label      );
		members.put( "radius",     radius     );
		members.put( "color",      color      );
		members.put( "isSelected", isSelected );
		members.put( "weight",     weight     );
		
		return JsonUtilities.formatObject( members );
	}
}
