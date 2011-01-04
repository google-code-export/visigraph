/**
 * Vertex.java
 */
package edu.belmont.mth.visigraph.models;

import java.util.*;
import java.awt.geom.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;

/**
 * The {@code Vertex} class represents one of the two fundamental mathematical elements in a graph along with edges. When used together with edges,
 * vertices can be related to each other to form a graph.
 * <p/>
 * As an {@link ObservableModel}, this class supports multiple subscribing {@link Observer}s. Whenever a change is made to any of its properties,
 * notification of that change is automatically propagated upwards until it hits the {@code Graph} level and triggers an
 * {@link ObservableModel#notifyObservers(Object)} call.
 * 
 * @author Cameron Behar
 * @see Graph
 * @see Vertex
 * @see Edge
 */
public class Vertex extends ObservableModel
{
	/**
	 * The universally unique identifier of this {@code Vertex}, used in the serialization and deserialization of edges
	 */
	public final Property<UUID>		id;
	
	/**
	 * The location of this {@code Vertex} along the horizontal (x) axis
	 */
	public final Property<Double>	x;
	
	/**
	 * The location of this {@code Vertex} along the inverted vertical (-y) axis
	 */
	public final Property<Double>	y;
	
	/**
	 * A {@code String} containing this {@code Vertex}'s label
	 */
	public final Property<String>	label;
	
	/**
	 * This {@code Vertex}'s radius, in pixels
	 */
	public final Property<Double>	radius;
	
	/**
	 * An {@code Integer} representing this {@code Vertex}'s numeric color
	 */
	public final Property<Integer>	color;
	
	/**
	 * A {@code Boolean} indicating whether or not this {@code Vertex} is selected
	 */
	public final Property<Boolean>	isSelected;
	
	/**
	 * A {@code Double} representing this {@code Vertex}'s numeric weight
	 */
	public final Property<Double>	weight;
	
	/**
	 * A catch-all {@code String} that can be used to store this {@code Vertex}'s metadata
	 */
	public final Property<String>	tag;
	
	/**
	 * Constructs a {@code Vertex} at (0.0, 0.0) with the user's default properties
	 */
	public Vertex( )
	{
		this( 0.0, 0.0 );
	}
	
	/**
	 * Constructs a {@code Vertex} at the specified location
	 * 
	 * @param x the location of this {@code Vertex} along the horizontal (x) axis
	 * @param y the location of this {@code Vertex} along the inverted vertical (-y) axis
	 */
	public Vertex( double x, double y )
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
	public Vertex( double x, double y, String label )
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
	public Vertex( double x, double y, String label, double radius )
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
	public Vertex( double x, double y, String label, double radius, int color )
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
	public Vertex( double x, double y, String label, double radius, int color, boolean isSelected )
	{
		this.id = new Property<UUID>( UUID.randomUUID( ) );
		this.x = new Property<Double>( x );
		this.y = new Property<Double>( y );
		this.label = new Property<String>( label );
		this.radius = new Property<Double>( radius );
		this.color = new Property<Integer>( color );
		this.isSelected = new Property<Boolean>( isSelected );
		this.weight = new Property<Double>( UserSettings.instance.defaultVertexWeight.get( ) );
		this.tag = new Property<String>( null );
	}
	
	/**
	 * Constructs a {@code Vertex} from the specified {@code Map} of properties
	 * 
	 * @param members a {@code Map} of property names to values
	 */
	public Vertex( Map<String, Object> members )
	{
		this.id = new Property<UUID>( UUID.fromString( (String) members.get( "id" ) ) );
		this.x = new Property<Double>( (Double) members.get( "x" ) );
		this.y = new Property<Double>( (Double) members.get( "y" ) );
		this.label = new Property<String>( (String) members.get( "label" ) );
		this.radius = new Property<Double>( (Double) members.get( "radius" ) );
		this.color = new Property<Integer>( (Integer) members.get( "color" ) );
		this.isSelected = new Property<Boolean>( (Boolean) members.get( "isSelected" ) );
		this.weight = new Property<Double>( (Double) members.get( "weight" ) );
		this.tag = new Property<String>( members.containsKey( "tag" ) ? (String) members.get( "tag" ) : null );
	}
	
	/**
	 * Constructs a {@code Vertex} from the specified JSON text
	 * 
	 * @param json the JSON text from which to construct this caption
	 * @see #toString()
	 */
	public Vertex( String json )
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
		return new Point2D.Double( this.x.get( ), this.y.get( ) );
	}
	
	/**
	 * Returns a {@code boolean} indicating whether or not the specified {@code Edge} is incident to this {@code Vertex}. A vertex is said to be
	 * incident to an edge if and only if the edge goes from- or to- the vertex.
	 * 
	 * @param edge the {@code Edge} with which to test this vertex for incidence
	 * @return {@code true} if the specified {@code Edge} is incident to this {@code Vertex}, {@code false} otherwise
	 */
	public boolean isIncident( Edge edge )
	{
		return edge.isIncident( this );
	}
	
	/**
	 * Returns a string representation of this vertex in JSON format. This method can be used to serialize a vertex, which may later be deserialized
	 * using the class's string constructor. For more information on the JSON format (used throughout VisiGraph for serialization), visit <a
	 * href="http://www.json.org/">here</a>.
	 * 
	 * @return this {@code Vertex} serialized as a {@code String}
	 * @see #Vertex(String)
	 */
	@Override
	public String toString( )
	{
		Map<String, Object> members = new HashMap<String, Object>( );
		
		members.put( "id", this.id );
		members.put( "x", this.x );
		members.put( "y", this.y );
		members.put( "label", this.label );
		members.put( "radius", this.radius );
		members.put( "color", this.color );
		members.put( "isSelected", this.isSelected );
		members.put( "weight", this.weight );
		
		return JsonUtilities.formatObject( members );
	}
}
