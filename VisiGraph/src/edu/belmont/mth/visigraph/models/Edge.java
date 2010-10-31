/**
 * Edge.java
 */
package edu.belmont.mth.visigraph.models;

import java.awt.geom.*;
import java.util.*;

import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;
import static edu.belmont.mth.visigraph.utilities.GeometryUtilities.*;

/**
 * The {@code Edge} class represents the second of the two fundamental mathematical elements in a graph along with vertices. Edges can be used to
 * define relationships between vertices, which together join to form a graph.
 * <p/>
 * As an {@link ObservableModel}, this class supports multiple subscribing {@link Observer}s. Whenever a change is made to any of its properties,
 * notification of that change is automatically propagated upwards until it hits the {@code Graph} level and triggers a
 * {@link ObservableModel#notifyObservers(Object)} call.
 * 
 * @author Cameron Behar
 * 
 * @see {@link Graph}, {@link Vertex}, {@link Caption}
 */
public class Edge extends ObservableModel
{
	/**
	 * A {@code Boolean} indicating whether or not this {@code Edge} is directed
	 */
	public final boolean isDirected;
	
	/**
	 * The {@code Vertex} from which this {@code Edge} originates—for undirected edges interchangeable with {@link #to}
	 */
	public final Vertex from;
	
	/**
	 * The {@code Vertex} to which this {@code Edge} goes—for undirected edges interchangeable with {@link #from}
	 */
	public final Vertex to;
	
	/**
	 * A {@code Double} representing this {@code Edge}'s numeric weight
	 */
	public final Property<Double> weight;
	
	/**
	 * An {@code Integer} representing this {@code Edge}'s numeric color
	 */
	public final Property<Integer> color;
	
	/**
	 * A {@code String} containing this {@code Edge}'s label
	 */
	public final Property<String> label;
	
	/**
	 * A {@code Boolean} indicating whether or not this {@code Edge} is selected
	 */
	public final Property<Boolean> isSelected;
	
	/**
	 * This {@code Edge}'s thickness, in pixels
	 */
	public final Property<Double> thickness;
	
	/**
	 * The location of this {@code Edge}'s handle along the horizontal (x) axis
	 */
	public final Property<Double> handleX;
	
	/**
	 * The location of this {@code Edge}'s handle along the inverted vertical (-y) axis
	 */
	public final Property<Double> handleY;
	
	/**
	 * A catch-all {@code String} that can be used to store this {@code Edge}'s metadata
	 */
	public final Property<String> tag;
	
	/**
	 * An {@code Arc2D} representing this {@code Edge}'s arc
	 * 
	 * @see {@link #getArc()}
	 */
	private Arc2D.Double arc;
	
	/**
	 * A {@code Line2D} representing this {@code Edge}'s line
	 * 
	 * @see {@link #getLine()}
	 */
	private Line2D.Double line;
	
	/**
	 * A {@code Point2D} representing the center of this {@code Edge}'s arc-circle
	 * 
	 * @see {@link #getCenter()}
	 */
	private Point2D.Double center;
	
	/**
	 * A {@code boolean} flag indicating whether this {@code Edge} is represented by a straight line or curving arc
	 */
	private boolean isLinear;
	
	/**
	 * A {@code boolean} flag indicating whether notifications are to be sent on to any of this edge's subscribed {@link Observer}s, or merely caught
	 * and handled internally
	 */
	private boolean notificationsSuspended;
	
	/**
	 * An {@code Observer} used to notify this {@code Edge} of changes to either of its vertices (specifically, in order to recalculate the {@code
	 * arc}, {@code line}, and handle when one of them moves)
	 */
	private Observer vertexObserver = new Observer( )
	{
		@Override
		public void update( Observable o, Object arg )
		{
			// Only reset the edge if the one of the vertexes moved
			if ( !notificationsSuspended && from != null && to != null && handleX != null && handleY != null && arg instanceof Property<?> )
			{
				Property<?> propertyChanged = (Property<?>) arg;
				
				if ( propertyChanged == from.x || propertyChanged == from.y || propertyChanged == to.x || propertyChanged == to.y )
				{
					if ( !isLinear )
					{
						Point2D midpoint = midpoint( line.x1, line.y1, line.x2, line.y2 );
						
						double distance = line.getP1( ).distance( line.getP2( ) );
						double lineAngle = angle( line.y2 - line.y1, line.x2 - line.x1 );
						
						double handleAngle = angle( handleY.get( ) - midpoint.getY( ), handleX.get( ) - midpoint.getX( ) ) - lineAngle;
						double handleRadiusRatio = midpoint.distance( handleX.get( ), handleY.get( ) ) / distance;
						
						Point2D newMidpoint = midpoint( from.x.get( ), from.y.get( ), to.x.get( ), to.y.get( ) );
						
						double newDistance = distance( from, to );
						double newLineAngle = angle( to.y.get( ) - from.y.get( ), to.x.get( ) - from.x.get( ) );
						
						double newHandleAngle = handleAngle + newLineAngle;
						double newHandleRadius = handleRadiusRatio * newDistance;
						
						suspendNotifications( true );
						handleX.set( newMidpoint.getX( ) + newHandleRadius * Math.cos( newHandleAngle ) );
						handleY.set( newMidpoint.getY( ) + newHandleRadius * Math.sin( newHandleAngle ) );
						suspendNotifications( false );
					}
					
					fixHandle( );
				}
			}
		}
	};
	
	
	/**
	 * Constructs a directed or undirected {@code Edge} between the two specified vertices
	 * 
	 * @param isDirected a {@code boolean} indicating whether this {@code Edge} is to directed or undirected.
	 * @param from the {@code Vertex} from which this {@code Edge} originates—for undirected edges interchangeable with {@link #to}
	 * @param to the {@code Vertex} to which this {@code Edge} goes—for undirected edges interchangeable with {@link #from}
	 */
	public Edge ( boolean isDirected, Vertex from, Vertex to )
	{
		this( isDirected, from, to, UserSettings.instance.defaultEdgeWeight.get( ) );
	}
	
	/**
	 * Constructs a directed or undirected {@code Edge} between the two specified vertices with the specified numeric weight
	 * 
	 * @param isDirected a {@code boolean} indicating whether this {@code Edge} is to directed or undirected.
	 * @param from the {@code Vertex} from which this {@code Edge} originates—for undirected edges interchangeable with {@link #to}
	 * @param to the {@code Vertex} to which this {@code Edge} goes—for undirected edges interchangeable with {@link #from}
	 * @param weight a {@code double} representing this {@code Edge}'s numeric weight
	 */
	public Edge ( boolean isDirected, Vertex from, Vertex to, double weight )
	{
		this( isDirected, from, to, weight, UserSettings.instance.defaultEdgeColor.get( ) );
	}
	
	/**
	 * Constructs a directed or undirected {@code Edge} between the two specified vertices with the specified numeric weight and color
	 * 
	 * @param isDirected a {@code boolean} indicating whether this {@code Edge} is to directed or undirected.
	 * @param from the {@code Vertex} from which this {@code Edge} originates—for undirected edges interchangeable with {@link #to}
	 * @param to the {@code Vertex} to which this {@code Edge} goes—for undirected edges interchangeable with {@link #from}
	 * @param weight a {@code double} representing this {@code Edge}'s numeric weight
	 * @param color an {@code int} representing this {@code Edge}'s numeric color
	 */
	public Edge ( boolean isDirected, Vertex from, Vertex to, double weight, int color )
	{
		this( isDirected, from, to, weight, color, UserSettings.instance.defaultEdgePrefix.get( ) );
	}
	
	/**
	 * Constructs a directed or undirected {@code Edge} between the two specified vertices with the specified numeric weight, color, and label
	 * 
	 * @param isDirected a {@code boolean} indicating whether this {@code Edge} is to directed or undirected.
	 * @param from the {@code Vertex} from which this {@code Edge} originates—for undirected edges interchangeable with {@link #to}
	 * @param to the {@code Vertex} to which this {@code Edge} goes—for undirected edges interchangeable with {@link #from}
	 * @param weight a {@code double} representing this {@code Edge}'s numeric weight
	 * @param color an {@code int} representing this {@code Edge}'s numeric color
	 * @param label a {@code String} containing this {@code Edge}'s label
	 */
	public Edge ( boolean isDirected, Vertex from, Vertex to, double weight, int color, String label )
	{
		this( isDirected, from, to, weight, color, label, UserSettings.instance.defaultEdgeIsSelected.get( ) );
	}
	
	/**
	 * Constructs a directed or undirected {@code Edge} between the two specified vertices with the specified numeric weight, color, label, and
	 * selection
	 * 
	 * @param isDirected a {@code boolean} indicating whether this {@code Edge} is to directed or undirected.
	 * @param from the {@code Vertex} from which this {@code Edge} originates—for undirected edges interchangeable with {@link #to}
	 * @param to the {@code Vertex} to which this {@code Edge} goes—for undirected edges interchangeable with {@link #from}
	 * @param weight a {@code double} representing this {@code Edge}'s numeric weight
	 * @param color an {@code int} representing this {@code Edge}'s numeric color
	 * @param label a {@code String} containing this {@code Edge}'s label
	 * @param isSelected a {@code boolean} indicating whether or not this {@code Edge} will be initialized selected
	 */
	public Edge ( boolean isDirected, Vertex from, Vertex to, double weight, int color, String label, boolean isSelected )
	{
		notificationsSuspended = true;
		
		this.isDirected = isDirected;
		
		this.from = from;
		this.from.addObserver( vertexObserver );
		
		this.to = to;
		this.to.addObserver( vertexObserver );
		
		this.weight     = new Property<Double> ( weight     );
		this.color      = new Property<Integer>( color      );
		this.label      = new Property<String> ( label      );
		this.isSelected = new Property<Boolean>( isSelected );
		this.thickness  = new Property<Double> ( UserSettings.instance.defaultEdgeThickness.get( ) );
		
		this.handleX = new Property<Double>( 0.0 )
		{
			@Override
			public void set( final Double value )
			{
				if ( super.get( ) == null || !super.get( ).equals( value ) )
				{
					super.set( value );
					refresh( );
				}
			}
		};
		this.handleY = new Property<Double>( 0.0 )
		{
			@Override
			public void set( final Double value )
			{
				if ( super.get( ) == null || !super.get( ).equals( value ) )
				{
					super.set( value );
					refresh( );
				}
			}
		};
		this.tag     = new Property<String>( null );
		
		this.arc    = new Arc2D.Double( );
		this.line   = new Line2D.Double( );
		this.center = new Point2D.Double( );
		
		notificationsSuspended = false;
		
		isLinear = true;
		fixHandle( );
		refresh( );
	}
	
	/**
	 * Constructs a {@code Vertex} from the specified {@code Map} of properties and {@code Vertex} {@code Map}
	 * 
	 * @param members a {@code Map} of property names to values
	 * @param vertexes a {@code Map} of {@code String} ids to vertices
	 */
	public Edge ( Map<String, Object> members, Map<String, Vertex> vertexes )
	{
		notificationsSuspended = true;
		
		this.isDirected = (Boolean) members.get( "isDirected" );
		
		this.from = vertexes.get( (String) members.get( "from.id" ) );
		this.from.addObserver( vertexObserver );
		
		this.to = vertexes.get( (String) members.get( "to.id" ) );
		this.to.addObserver( vertexObserver );
		
		this.weight     = new Property<Double> ( (Double)  members.get( "weight"     ) );
		this.color      = new Property<Integer>( (Integer) members.get( "color"      ) );
		this.label      = new Property<String> ( (String)  members.get( "label"      ) );
		this.isSelected = new Property<Boolean>( (Boolean) members.get( "isSelected" ) );
		this.thickness  = new Property<Double> ( (Double)  members.get( "thickness"  ) );
		
		this.isLinear = members.containsKey( "isLinear" ) ? (Boolean) members.get( "isLinear" ) : false;
		this.handleX = new Property<Double>( this.isLinear ? 0.0 : (Double) members.get( "handleX" ) )
		{
			@Override
			public void set( final Double value )
			{
				if ( super.get( ) == null || !super.get( ).equals( value ) )
				{
					super.set( value );
					refresh( );
				}
			}
		};
		this.handleY = new Property<Double>( this.isLinear ? 0.0 : (Double) members.get( "handleY" ) )
		{
			@Override
			public void set( final Double value )
			{
				if ( super.get( ) == null || !super.get( ).equals( value ) )
				{
					super.set( value );
					refresh( );
				}
			}
		};
		this.tag     = new Property<String>( members.containsKey( "tag" ) ? (String) members.get( "tag" ) : null );
		
		this.arc    = new Arc2D.Double( );
		this.line   = new Line2D.Double( );
		this.center = new Point2D.Double( );
		
		notificationsSuspended = false;
		
		fixHandle( );
		refresh( );
	}
	
	/**
	 * Constructs an {@code Edge} from the specified JSON text and {@code Vertex} {@code Map}
	 * 
	 * @param json the JSON text from which to construct this caption
	 * @param vertexes a {@code Map} of {@code String} ids to vertices
	 * 
	 * @see #toString()
	 */
	public Edge ( String json, Map<String, Vertex> vertexes )
	{
		this( JsonUtilities.parseObject( json ), vertexes );
	}
	
	/**
	 * Resets this {@code Edge}'s handle to the midpoint of the line between its vertices when this {@code Edge} is linear, or to a user-specified
	 * relative location when it is a loop
	 */
	public void fixHandle( )
	{
		if ( from == to )
		{
			// Reset a loop handle
			handleX.set( from.x.get( ) + UserSettings.instance.defaultLoopDiameter.get( ) );
			handleY.set( from.y.get( ) );
		}
		else if ( isLinear )
		{
			// Reset a standard edge handle
			Point2D midpoint = midpoint( from.x.get( ), from.y.get( ), to.x.get( ), to.y.get( ) );
			handleX.set( midpoint.getX( ) );
			handleY.set( midpoint.getY( ) );
		}
		
		refresh( );
	}
	
	/**
	 * Returns this {@code Edge}'s arc as an {@code Arc2D}. If this {@code Edge} is not an arc, this method will return the last valid arc before the
	 * {@code Edge} became linear.
	 * 
	 * @return an {@code Arc2D} representing this {@code Edge}'s arc
	 * 
	 * @see {@link #getCenter()}, {@link #getLine()}
	 */
	public Arc2D getArc( )
	{
		return (Arc2D) arc.clone( );
	}
	
	/**
	 * Returns a {@code Point2D} representing the center of this {@code Edge}'s arc-circle. If this {@code Edge} is not an arc, this method will
	 * return the last valid center before the {@code Edge} became linear.
	 * 
	 * @return a {@code Point2D} representing the center of this {@code Edge}'s arc-circle
	 * 
	 * @see {@link #getArc()}, {@link #getLine()}
	 */
	public Point2D getCenter( )
	{
		return (Point2D) center.clone( );
	}
	
	/**
	 * Returns the location of this {@code Edge}'s handle as a {@code Point2D} in the (x, -y) coordinate space
	 * 
	 * @return a {@code Point2D} representing this {@code Edge}'s handle in the (x, -y) coordinate space
	 * 
	 * @see {@link #handleX}, {@link #handleY}
	 */
	public Point2D getHandlePoint2D( )
	{
		return new Point2D.Double( handleX.get( ), handleY.get( ) );
	}
	
	/**
	 * Returns this {@code Edge}'s line as a {@code Line2D}. If this {@code Edge} is not linear, this method will still return a line segment
	 * representing this {@code Edge}, were it in fact linear.
	 * 
	 * @return a {@code Line2D} representing this {@code Edge's} line
	 * 
	 * @see {@link #getArc()}, {@link #getCenter()}
	 */
	public Line2D getLine( )
	{
		return (Line2D) line.clone( );
	}
	
	/**
	 * Returns a {@code boolean} indicating whether or not the specified {@code Edge} is adjacent to this {@code Edge}. Two edges are said to be
	 * adjacent to each other if they share at least one common vertex.
	 * 
	 * @param edge the {@code Edge} to which to compare vertices for coincidence
	 * 
	 * @return {@code true} the specified {@code Edge} is adjacent to this {@code Edge}, {@code false} otherwise
	 */
	public boolean isAdjacent( Edge edge )
	{
		return from == edge.from || from == edge.to || to == edge.from || to == edge.to;
	}
	
	/**
	 * Returns a {@code boolean} indicating whether or not the this {@code Edge} is to be treated as a straight line segment between its two vertices.
	 * 
	 * @return {@code true} if this {@code Edge} is straight, {@code false} otherwise
	 */
	public boolean isLinear( )
	{
		return isLinear;
	}
	
	/**
	 * Recalculates this {@code Edge}'s line and determines its linearity before making calls to update the arc, if necessary.
	 * 
	 * @see {@link #getLine()}, {@link #isLinear()}, {@link #getArc()}
	 */
	public void refresh( )
	{
		if ( !notificationsSuspended && handleX != null && handleY != null )
		{
			// Determine linearity
			line = new Line2D.Double( from.x.get( ), from.y.get( ), to.x.get( ), to.y.get( ) );
			double handleDistanceFromLine = line.ptLineDist( handleX.get( ), handleY.get( ) );
			double snapToLineMargin = UserSettings.instance.edgeSnapMarginRatio.get( ) * distance( from, to );
			isLinear = ( handleDistanceFromLine <= snapToLineMargin );
			
			if ( !isLinear )
			{
				updateCenter( );
				updateArc( );
			}
		}
	}
	
	/**
	 * Repositions this {@code Edge}'s handle so that the edge is a straight line segment between its two vertices, provided they are not the same
	 * (i.e. the edge is a loop).
	 */
	public void straighten( )
	{
		if ( from != to )
		{
			isLinear = true;
			fixHandle( );
		}
	}
	
	/**
	 * Temporarily suspends the notification all of property changes to subscribed {@link Observer}s. Most often this method is called when performing
	 * a large number of batch operations on an edge, so that subscribers are not overloaded with a multitude of notifications.
	 * 
	 * @param suspend a {@code boolean} indicating whether to suspend or reenable notifications to subscribed {@code Observer}s
	 * 
	 * @return {@code true} if notifications were previously suspended, {@code false} otherwise
	 * 
	 * @see {@link ObservableModel}, {@link Observer}, {@link ObservableList}
	 */
	public boolean suspendNotifications( boolean suspend )
	{
		boolean ret = notificationsSuspended;
		notificationsSuspended = suspend;
		return ret;
	}
	
	/**
	 * Recalculates this {@code Edge}'s arc to match up with the handle's coordinates
	 */
	private void updateArc( )
	{
		double radius = center.distance( from.x.get( ), from.y.get( ) );
		
		arc.setFrameFromCenter( center, new Point2D.Double( center.x - radius, center.y - radius ) );
		
		// We need to calculate these angles so that we know in which order to pass the from and to points because the sector will always be drawn
		// counter-clockwise
		double fromAngle   = angle( -( from.y. get( ) - center.y ), from.x. get( ) - center.x );
		double handleAngle = angle( -( handleY.get( ) - center.y ), handleX.get( ) - center.x );
		double toAngle     = angle( -( to.y.   get( ) - center.y ), to.x.   get( ) - center.x );
		
		if ( angleBetween( fromAngle, handleAngle ) < angleBetween( fromAngle, toAngle ) )
			arc.setAngles( from.getPoint2D( ), to.getPoint2D( ) );
		else
			arc.setAngles( to.getPoint2D( ), from.getPoint2D( ) );
	}
	
	/**
	 * Recalculates this {@code Edge}'s arc-circle center to match up with the arc's size and location
	 */
	private void updateCenter( )
	{
		if ( from == to )
		{
			center.setLocation( midpoint( from.x.get( ), from.y.get( ), handleX.get( ), handleY.get( ) ) );
		}
		else
		{
			double x0 = from.x. get( ), y0 = from.y. get( );
			double x1 = handleX.get( ), y1 = handleY.get( );
			double x2 = to.x.   get( ), y2 = to.y.   get( );
			
			double h = determinant( new double[ ][ ] { { x0 * x0 + y0 * y0, y0, 1 }, { x1 * x1 + y1 * y1, y1, 1 }, { x2 * x2 + y2 * y2, y2, 1 } } ) / ( 2.0 * determinant( new double[ ][ ] { { x0, y0, 1 }, { x1, y1, 1 }, { x2, y2, 1 } } ) );
			double k = determinant( new double[ ][ ] { { x0, x0 * x0 + y0 * y0, 1 }, { x1, x1 * x1 + y1 * y1, 1 }, { x2, x2 * x2 + y2 * y2, 1 } } ) / ( 2.0 * determinant( new double[ ][ ] { { x0, y0, 1 }, { x1, y1, 1 }, { x2, y2, 1 } } ) );
			
			center.setLocation( h, k );
		}
	}
	
	/**
	 * Returns a string representation of this edge in JSON format. This method can be used to serialize a edge, which may later be deserialized
	 * using the class's string constructor. For more information on the JSON format (used throughout VisiGraph for serialization), visit <a
	 * href="http://www.json.org/">here</a>.
	 * 
	 * @see #Edge(String)
	 */
	@Override
	public String toString( )
	{
		HashMap<String, Object> members = new HashMap<String, Object>( );
		
		members.put( "isDirected", isDirected );
		members.put( "from.id",    from.id    );
		members.put( "to.id",      to.id      );
		members.put( "weight",     weight     );
		members.put( "color",      color      );
		members.put( "label",      label      );
		members.put( "isSelected", isSelected );
		members.put( "thickness",  thickness  );
		members.put( "isLinear",   isLinear   );
		
		if ( !isLinear )
		{
			members.put( "handleX", handleX );
			members.put( "handleY", handleY );
		}
		
		return JsonUtilities.formatObject( members );
	}
}
