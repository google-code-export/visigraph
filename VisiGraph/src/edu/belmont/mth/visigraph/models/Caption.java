/**
 * Caption.java
 */
package edu.belmont.mth.visigraph.models;

import java.util.*;

import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;

/**
 * The {@code Caption} class is used to insert basic textual annotations into the graph itself in order to highlight points of interest. Although not
 * technically related to the graph's mathematical structure, when positioned alongside vertices and edges, captions can provide insight and notes
 * into the graph's structure that might otherwise be unclear. Along with {@code Vertex}es and {@code Edge}s, a list of these classes is maintained
 * in each {@code Graph} object, and so persists through serialization and deserialization.
 * <p/>
 * As an {@link ObservableModel}, this class also supports multiple subscribing {@link Observer}s. Whenever a change is made to any of its properties,
 * notification of that change is automatically propagated upwards until it hits the {@code Graph} level and triggers a
 * {@link ObservableModel#notifyObservers(Object)} call.
 * 
 * @author Cameron Behar
 * 
 * @see {@link Graph}, {@link Vertex}, {@link Edge}
 */
public class Caption extends ObservableModel
{
	/**
	 * The location of this {@code Caption}'s upper-left-hand corner along the horizontal (x) axis
	 */
	public final Property<Double> x;
	
	/**
	 * The location of this {@code Caption}'s upper-left-hand corner along the inverted vertical (-y) axis
	 */
	public final Property<Double> y;
	
	/**
	 * A {@code String} containing this {@code Caption}'s text
	 */
	public final Property<String> text;
	
	/**
	 * The font size, in pixels, of this {@code Caption}'s text
	 */
	public final Property<Double> size;
	
	/**
	 * A {@code Boolean} indicating whether or not this {@code Caption} is selected
	 */
	public final Property<Boolean> isSelected;
	
	/**
	 * A catch-all {@code String} that can be used to store this {@code Caption}'s metadata
	 */
	public final Property<String> tag;
	
	/**
	 * Constructs an empty {@code Caption} at the specified location
	 * 
	 * @param x the location of this {@code Caption}'s upper-left-hand corner along the horizontal (x) axis
	 * @param y the location of this {@code Caption}'s upper-left-hand corner along the inverted vertical (-y) axis
	 */
	public Caption ( double x, double y )
	{
		this( x, y, UserSettings.instance.defaultCaptionText.get( ) );
	}
	
	/**
	 * Constructs a {@code Caption} with the specified text at the specified location
	 * 
	 * @param x the location of this {@code Caption}'s upper-left-hand corner along the horizontal (x) axis
	 * @param y the location of this {@code Caption}'s upper-left-hand corner along the inverted vertical (-y) axis
	 * @param text a {@code String} containing this {@code Caption}'s text
	 */
	public Caption ( double x, double y, String text )
	{
		this( x, y, text, UserSettings.instance.defaultCaptionFontSize.get( ) );
	}
	
	/**
	 * Constructs a {@code Caption} with the specified text and font size at the specified location
	 * 
	 * @param x the location of this {@code Caption}'s upper-left-hand corner along the horizontal (x) axis
	 * @param y the location of this {@code Caption}'s upper-left-hand corner along the inverted vertical (-y) axis
	 * @param text a {@code String} containing this {@code Caption}'s text
	 * @param size the font size, in pixels, of this {@code Caption}'s text
	 */
	public Caption ( double x, double y, String text, double size )
	{
		this( x, y, text, size, UserSettings.instance.defaultCaptionIsSelected.get( ) );
	}
	
	/**
	 * Constructs a {@code Caption} with the specified text, font size, and selection at the specified location
	 * 
	 * @param x the location of this {@code Caption}'s upper-left-hand corner along the horizontal (x) axis
	 * @param y the location of this {@code Caption}'s upper-left-hand corner along the inverted vertical (-y) axis
	 * @param text a {@code String} containing this {@code Caption}'s text
	 * @param size the font size, in pixels, of this {@code Caption}'s text
	 * @param isSelected a {@code boolean} indicating whether or not this {@code Caption} will be initialized selected
	 */
	public Caption ( double x, double y, String text, double size, boolean isSelected )
	{
		this.x = new Property<Double>( x );
		this.y = new Property<Double>( y );
		this.text = new Property<String>( text );
		this.size = new Property<Double>( size );
		this.isSelected = new Property<Boolean>( isSelected );
		this.tag = new Property<String>( null );
	}
	
	/**
	 * Constructs a {@code Caption} from the specified map of properties
	 * 
	 * @param members a {@code Map} of property names to values
	 */
	public Caption ( Map<String, Object> members )
	{
		this.x = new Property<Double>( (Double) members.get( "x" ) );
		this.y = new Property<Double>( (Double) members.get( "y" ) );
		this.text = new Property<String>( (String) members.get( "text" ) );
		this.size = new Property<Double>( (Double) members.get( "size" ) );
		this.isSelected = new Property<Boolean>( (Boolean) members.get( "isSelected" ) );
		this.tag = new Property<String>( members.containsKey( "tag" ) ? (String) members.get( "tag" ) : null );
	}
	
	/**
	 * Constructs a {@code Caption} from the specified JSON text
	 * 
	 * @param json the JSON text from which to construct this caption
	 * 
	 * @see #toString()
	 */
	public Caption ( String json )
	{
		this( JsonUtilities.parseObject( json ) );
	}
	
	/**
	 * Returns a string representation of this caption in JSON format. This method can be used to serialize a caption, which may later be deserialized
	 * using the class's string constructor. For more information on the JSON format (used throughout VisiGraph for serialization), visit <a
	 * href="http://www.json.org/">here</a>.
	 * 
	 * @return this {@code Caption} serialized as a {@code String}
	 * 
	 * @see #Caption(String)
	 */
	@Override
	public String toString( )
	{
		HashMap<String, Object> members = new HashMap<String, Object>( );
		
		members.put( "x", x );
		members.put( "y", y );
		members.put( "text", text );
		members.put( "size", size );
		members.put( "isSelected", isSelected );
		
		return JsonUtilities.formatObject( members );
	}
}
