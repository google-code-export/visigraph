/**
 * Edge.java
 */
package edu.belmont.mth.visigraph.models;

import java.util.*;
import java.awt.geom.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;
import static edu.belmont.mth.visigraph.utilities.GeometryUtilities.*;

/**
 * The {@code Edge} class represents the second of the two fundamental mathematical elements in a graph along with vertices. Edges can be used to
 * define relationships between vertices, which together join to form a graph.
 * <p/>
 * As an {@link ObservableModel}, this class supports multiple subscribing {@link Observer}s. Whenever a change is made to any of its properties,
 * notification of that change is automatically propagated upwards until it hits the {@code Graph} level and triggers an
 * {@link ObservableModel#notifyObservers(Object)} call.
 * 
 * @author Cameron Behar
 * @see Graph
 * @see Vertex
 * @see Caption
 */
public class Edge extends ObservableModel
{
	/**
	 * A {@code Boolean} indicating whether or not this {@code Edge} is directed
	 */
	public final boolean			isDirected;
	
	/**
	 * The {@code Vertex} from which this {@code Edge} originates—for undirected edges interchangeable with {@link #to}
	 */
	public final Vertex				from;
	
	/**
	 * The {@code Vertex} to which this {@code Edge} goes—for undirected edges interchangeable with {@link #from}
	 */
	public final Vertex				to;
	
	/**
	 * A {@code Boolean} indicating whether or not this {@code Edge} is a loop
	 */
	public final boolean			isLoop;
	
	/**
	 * A {@code Double} representing this {@code Edge}'s numeric weight
	 */
	public final Property<Double>	weight;
	
	/**
	 * An {@code Integer} representing this {@code Edge}'s numeric color
	 */
	public final Property<Integer>	color;
	
	/**
	 * A {@code String} containing this {@code Edge}'s label
	 */
	public final Property<String>	label;
	
	/**
	 * A {@code Boolean} indicating whether or not this {@code Edge} is selected
	 */
	public final Property<Boolean>	isSelected;
	
	/**
	 * This {@code Edge}'s thickness, in pixels
	 */
	public final Property<Double>	thickness;
	
	/**
	 * The location of this {@code Edge}'s handle along the horizontal (x) axis
	 */
	public final Property<Double>	handleX;
	
	/**
	 * The location of this {@code Edge}'s handle along the inverted vertical (-y) axis
	 */
	public final Property<Double>	handleY;
	
	/**
	 * A catch-all {@code String} that can be used to store this {@code Edge}'s metadata
	 */
	public final Property<String>	tag;
	
	/**
	 * An {@code Arc2D} representing this {@code Edge}'s arc
	 * 
	 * @see {@link #getArc()}
	 */
	private final Arc2D.Double		arc;
	
	/**
	 * A {@code Line2D} representing this {@code Edge}'s line
	 * 
	 * @see {@link #getLine()}
	 */
	private Line2D.Double			line;
	
	/**
	 * A {@code Point2D} representing the center of this {@code Edge}'s arc-circle
	 * 
	 * @see {@link #getCenter()}
	 */
	private final Point2D.Double	center;
	
	/**
	 * A {@code boolean} flag indicating whether this {@code Edge} is represented by a straight line or curving arc
	 */
	private boolean					isLinear;
	
	/**
	 * A {@code boolean} flag indicating whether notifications are to be sent on to any of this edge's subscribed {@link Observer}s, or merely caught
	 * and handled internally
	 */
	private boolean					notificationsSuspended;
	
	/**
	 * An {@code Observer} used to notify this {@code Edge} of changes to either of its vertices (specifically, in order to recalculate the {@code
	 * arc}, {@code line}, and handle when one of them moves)
	 */
	private final Observer			vertexObserver	= new Observer( )
													{
														@Override
														public void update( Observable o, Object arg )
														{
															// Only reset the edge if the one of the vertices moved
															if( !Edge.this.notificationsSuspended && Edge.this.from != null && Edge.this.to != null && Edge.this.handleX != null && Edge.this.handleY != null && arg instanceof Property<?> )
															{
																Property<?> propertyChanged = (Property<?>) arg;
																
																if( propertyChanged == Edge.this.from.x || propertyChanged == Edge.this.from.y || propertyChanged == Edge.this.to.x || propertyChanged == Edge.this.to.y )
																{
																	if( Edge.this.isLoop )
																	{
																		Edge.this.suspendNotifications( true );
																		Edge.this.handleX.set( Edge.this.from.x.get( ) + Edge.this.line.x1 - Edge.this.handleX.get( ) );
																		Edge.this.handleY.set( Edge.this.from.y.get( ) + Edge.this.line.y1 - Edge.this.handleY.get( ) );
																		Edge.this.suspendNotifications( false );
																	}
																	else if( Edge.this.isLinear )
																		Edge.this.reset( );
																	else
																	{
																		Point2D midpoint = midpoint( Edge.this.line.x1, Edge.this.line.y1, Edge.this.line.x2, Edge.this.line.y2 );
																		
																		double distance = Edge.this.line.getP1( ).distance( Edge.this.line.getP2( ) );
																		double lineAngle = angle( Edge.this.line.y2 - Edge.this.line.y1, Edge.this.line.x2 - Edge.this.line.x1 );
																		
																		double handleAngle = angle( Edge.this.handleY.get( ) - midpoint.getY( ), Edge.this.handleX.get( ) - midpoint.getX( ) ) - lineAngle;
																		double handleRadiusRatio = midpoint.distance( Edge.this.handleX.get( ), Edge.this.handleY.get( ) ) / distance;
																		
																		Point2D newMidpoint = midpoint( Edge.this.from.x.get( ), Edge.this.from.y.get( ), Edge.this.to.x.get( ), Edge.this.to.y.get( ) );
																		
																		double newDistance = distance( Edge.this.from, Edge.this.to );
																		double newLineAngle = angle( Edge.this.to.y.get( ) - Edge.this.from.y.get( ), Edge.this.to.x.get( ) - Edge.this.from.x.get( ) );
																		
																		double newHandleAngle = handleAngle + newLineAngle;
																		double newHandleRadius = handleRadiusRatio * newDistance;
																		
																		Edge.this.suspendNotifications( true );
																		Edge.this.handleX.set( newMidpoint.getX( ) + newHandleRadius * Math.cos( newHandleAngle ) );
																		Edge.this.handleY.set( newMidpoint.getY( ) + newHandleRadius * Math.sin( newHandleAngle ) );
																		Edge.this.suspendNotifications( false );
																	}
																	
																	Edge.this.refresh( );
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
	public Edge( boolean isDirected, Vertex from, Vertex to )
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
	public Edge( boolean isDirected, Vertex from, Vertex to, double weight )
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
	public Edge( boolean isDirected, Vertex from, Vertex to, double weight, int color )
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
	public Edge( boolean isDirected, Vertex from, Vertex to, double weight, int color, String label )
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
	public Edge( boolean isDirected, Vertex from, Vertex to, double weight, int color, String label, boolean isSelected )
	{
		this.notificationsSuspended = true;
		
		this.isDirected = isDirected;
		
		this.from = from;
		this.from.addObserver( this.vertexObserver );
		
		this.to = to;
		this.to.addObserver( this.vertexObserver );
		
		this.isLoop = ( this.from == this.to );
		
		this.weight = new Property<Double>( weight );
		this.color = new Property<Integer>( color );
		this.label = new Property<String>( label );
		this.isSelected = new Property<Boolean>( isSelected );
		this.thickness = new Property<Double>( UserSettings.instance.defaultEdgeThickness.get( ) );
		
		this.handleX = new Property<Double>( 0.0 )
		{
			@Override
			public void set( final Double value )
			{
				if( super.get( ) == null || !super.get( ).equals( value ) )
				{
					super.set( value );
					Edge.this.refresh( );
				}
			}
		};
		this.handleY = new Property<Double>( 0.0 )
		{
			@Override
			public void set( final Double value )
			{
				if( super.get( ) == null || !super.get( ).equals( value ) )
				{
					super.set( value );
					Edge.this.refresh( );
				}
			}
		};
		this.tag = new Property<String>( null );
		
		this.arc = new Arc2D.Double( );
		this.line = new Line2D.Double( );
		this.center = new Point2D.Double( );
		
		this.notificationsSuspended = false;
		
		this.reset( );
	}
	
	/**
	 * Constructs a {@code Vertex} from the specified {@code Map} of properties and {@code Vertex} {@code Map}
	 * 
	 * @param members a {@code Map} of property names to values
	 * @param vertices a {@code Map} of {@code String} ids to vertices
	 */
	public Edge( Map<String, Object> members, Map<String, Vertex> vertices )
	{
		this.notificationsSuspended = true;
		
		this.isDirected = (Boolean) members.get( "isDirected" );
		
		this.from = vertices.get( members.get( "from.id" ) );
		this.from.addObserver( this.vertexObserver );
		
		this.to = vertices.get( members.get( "to.id" ) );
		this.to.addObserver( this.vertexObserver );
		
		this.isLoop = ( this.from == this.to );
		
		this.weight = new Property<Double>( (Double) members.get( "weight" ) );
		this.color = new Property<Integer>( (Integer) members.get( "color" ) );
		this.label = new Property<String>( (String) members.get( "label" ) );
		this.isSelected = new Property<Boolean>( (Boolean) members.get( "isSelected" ) );
		this.thickness = new Property<Double>( (Double) members.get( "thickness" ) );
		
		this.isLinear = members.containsKey( "isLinear" ) ? (Boolean) members.get( "isLinear" ) : false;
		this.handleX = new Property<Double>( this.isLinear ? 0.0 : (Double) members.get( "handleX" ) )
		{
			@Override
			public void set( final Double value )
			{
				if( super.get( ) == null || !super.get( ).equals( value ) )
				{
					super.set( value );
					Edge.this.refresh( );
				}
			}
		};
		this.handleY = new Property<Double>( this.isLinear ? 0.0 : (Double) members.get( "handleY" ) )
		{
			@Override
			public void set( final Double value )
			{
				if( super.get( ) == null || !super.get( ).equals( value ) )
				{
					super.set( value );
					Edge.this.refresh( );
				}
			}
		};
		this.tag = new Property<String>( members.containsKey( "tag" ) ? (String) members.get( "tag" ) : null );
		
		this.arc = new Arc2D.Double( );
		this.line = new Line2D.Double( );
		this.center = new Point2D.Double( );
		
		this.notificationsSuspended = false;
		
		if( this.isLinear )
			this.reset( );
		
		this.refresh( );
	}
	
	/**
	 * Constructs an {@code Edge} from the specified JSON text and {@code Vertex} {@code Map}
	 * 
	 * @param json the JSON text from which to construct this caption
	 * @param vertices a {@code Map} of {@code String} ids to vertices
	 * @see #toString()
	 */
	public Edge( String json, Map<String, Vertex> vertices )
	{
		this( JsonUtilities.parseObject( json ), vertices );
	}
	
	/**
	 * Returns this {@code Edge}'s arc as an {@code Arc2D}. If this {@code Edge} is not an arc, this method will return the last valid arc before the
	 * {@code Edge} became linear.
	 * 
	 * @return an {@code Arc2D} representing this {@code Edge}'s arc
	 * @see #getCenter()
	 * @see #getLine()
	 */
	public Arc2D getArc( )
	{
		return (Arc2D) this.arc.clone( );
	}
	
	/**
	 * Returns a {@code Point2D} representing the center of this {@code Edge}'s arc-circle. If this {@code Edge} is not an arc, this method will
	 * return the last valid center before the {@code Edge} became linear.
	 * 
	 * @return a {@code Point2D} representing the center of this {@code Edge}'s arc-circle
	 * @see #getArc()
	 * @see #getLine()
	 */
	public Point2D getCenter( )
	{
		return (Point2D) this.center.clone( );
	}
	
	/**
	 * Returns the location of this {@code Edge}'s handle as a {@code Point2D} in the (x, -y) coordinate space
	 * 
	 * @return a {@code Point2D} representing this {@code Edge}'s handle in the (x, -y) coordinate space
	 * @see #handleX
	 * @see #handleY
	 */
	public Point2D getHandlePoint2D( )
	{
		return new Point2D.Double( this.handleX.get( ), this.handleY.get( ) );
	}
	
	/**
	 * Returns this {@code Edge}'s line as a {@code Line2D}. If this {@code Edge} is not linear, this method will still return a line segment
	 * representing this {@code Edge}, were it in fact linear.
	 * 
	 * @return a {@code Line2D} representing this {@code Edge's} line
	 * @see #getArc()
	 * @see #getCenter()
	 */
	public Line2D getLine( )
	{
		return (Line2D) this.line.clone( );
	}
	
	/**
	 * Returns a {@code boolean} indicating whether or not the specified {@code Edge} is adjacent to this {@code Edge}. Two edges are said to be
	 * adjacent to each other if they share at least one common vertex.
	 * 
	 * @param edge the {@code Edge} to which to compare vertices for coincidence
	 * @return {@code true} if the specified {@code Vertex} is adjacent to this {@code Edge}, {@code false} otherwise
	 */
	public boolean isAdjacent( Edge edge )
	{
		return this.isIncident( edge.from ) || this.isIncident( edge.to );
	}
	
	/**
	 * Returns a {@code boolean} indicating whether or not the specified {@code Vertex} is adjacent to this {@code Edge}. A vertex is said to be
	 * incident to an edge if and only if the edge goes from- or to- the vertex.
	 * 
	 * @param vertex the {@code Vertex} to which to this edge for incidence
	 * @return {@code true} if the specified {@code Vertex} is incident to this {@code Edge}, {@code false} otherwise
	 */
	public boolean isIncident( Vertex vertex )
	{
		return this.from == vertex || this.to == vertex;
	}
	
	/**
	 * Returns a {@code boolean} indicating whether or not the this {@code Edge} is to be treated as a straight line segment between its two vertices.
	 * 
	 * @return {@code true} if this {@code Edge} is straight, {@code false} otherwise
	 */
	public boolean isLinear( )
	{
		return this.isLinear;
	}
	
	/**
	 * Recalculates this {@code Edge}'s line and determines its linearity before making calls to update the arc, if necessary.
	 * 
	 * @see #getLine()
	 * @see #isLinear()
	 * @see #getArc()
	 */
	public void refresh( )
	{
		if( !this.notificationsSuspended && this.handleX != null && this.handleY != null )
		{
			// Determine linearity
			this.line = new Line2D.Double( this.from.x.get( ), this.from.y.get( ), this.to.x.get( ), this.to.y.get( ) );
			double handleDistanceFromLine = this.line.ptLineDist( this.handleX.get( ), this.handleY.get( ) );
			double snapToLineMargin = UserSettings.instance.edgeSnapMarginRatio.get( ) * distance( this.from, this.to );
			this.isLinear = ( handleDistanceFromLine <= snapToLineMargin );
			
			if( !this.isLinear )
			{
				this.updateCenter( );
				this.updateArc( );
			}
		}
	}
	
	/**
	 * Resets this {@code Edge}'s handle to its default position. For simple edges, this is the midpoint of the edge's two vertices. For loops, it is
	 * a consistent horizontal offset from the vertex defined by the "Loop diameter" setting.
	 */
	public void reset( )
	{
		if( this.isLoop )
		{
			this.handleX.set( this.from.x.get( ) + UserSettings.instance.defaultLoopDiameter.get( ) );
			this.handleY.set( this.from.y.get( ) );
		}
		else
		{
			Point2D midpoint = midpoint( this.from.x.get( ), this.from.y.get( ), this.to.x.get( ), this.to.y.get( ) );
			this.handleX.set( midpoint.getX( ) );
			this.handleY.set( midpoint.getY( ) );
			this.isLinear = true;
		}
		
		this.refresh( );
	}
	
	/**
	 * Temporarily suspends the notification all of property changes to subscribed {@link Observer}s. Most often this method is called when performing
	 * a large number of batch operations on an edge, so that subscribers are not overloaded with a multitude of notifications.
	 * 
	 * @param suspend a {@code boolean} indicating whether to suspend or reenable notifications to subscribed {@code Observer}s
	 * @return {@code true} if notifications were previously suspended, {@code false} otherwise
	 * @see ObservableModel
	 * @see Observer
	 * @see ObservableList
	 */
	public boolean suspendNotifications( boolean suspend )
	{
		boolean ret = this.notificationsSuspended;
		this.notificationsSuspended = suspend;
		return ret;
	}
	
	/**
	 * Returns a string representation of this edge in JSON format. This method can be used to serialize a edge, which may later be deserialized
	 * using the class's string constructor. For more information on the JSON format (used throughout VisiGraph for serialization), visit <a
	 * href="http://www.json.org/">here</a>.
	 * 
	 * @see #Edge(String, Map)
	 */
	@Override
	public String toString( )
	{
		Map<String, Object> members = new HashMap<String, Object>( );
		
		members.put( "isDirected", this.isDirected );
		members.put( "from.id", this.from.id );
		members.put( "to.id", this.to.id );
		members.put( "weight", this.weight );
		members.put( "color", this.color );
		members.put( "label", this.label );
		members.put( "isSelected", this.isSelected );
		members.put( "thickness", this.thickness );
		members.put( "isLinear", this.isLinear );
		
		if( !this.isLinear )
		{
			members.put( "handleX", this.handleX );
			members.put( "handleY", this.handleY );
		}
		
		return JsonUtilities.formatObject( members );
	}
	
	/**
	 * Recalculates this {@code Edge}'s arc to match up with the handle's coordinates
	 */
	private void updateArc( )
	{
		double radius = this.center.distance( this.from.x.get( ), this.from.y.get( ) );
		
		this.arc.setFrameFromCenter( this.center, new Point2D.Double( this.center.x - radius, this.center.y - radius ) );
		
		// We need to calculate these angles so that we know in which order to pass the from and to points because the sector will always be drawn
		// counter-clockwise
		double fromAngle = angle( -( this.from.y.get( ) - this.center.y ), this.from.x.get( ) - this.center.x );
		double handleAngle = angle( -( this.handleY.get( ) - this.center.y ), this.handleX.get( ) - this.center.x );
		double toAngle = angle( -( this.to.y.get( ) - this.center.y ), this.to.x.get( ) - this.center.x );
		
		if( angleBetween( fromAngle, handleAngle ) < angleBetween( fromAngle, toAngle ) )
			this.arc.setAngles( this.from.getPoint2D( ), this.to.getPoint2D( ) );
		else
			this.arc.setAngles( this.to.getPoint2D( ), this.from.getPoint2D( ) );
	}
	
	/**
	 * Recalculates this {@code Edge}'s arc-circle center to match up with the arc's size and location
	 */
	private void updateCenter( )
	{
		if( this.isLoop )
			this.center.setLocation( midpoint( this.from.x.get( ), this.from.y.get( ), this.handleX.get( ), this.handleY.get( ) ) );
		else
		{
			double x0 = this.from.x.get( ), y0 = this.from.y.get( );
			double x1 = this.handleX.get( ), y1 = this.handleY.get( );
			double x2 = this.to.x.get( ), y2 = this.to.y.get( );
			double divisor = 2.0 * determinant( new double[ ][ ] { { x0, y0, 1 }, { x1, y1, 1 }, { x2, y2, 1 } } );
			
			double h = determinant( new double[ ][ ] { { x0 * x0 + y0 * y0, y0, 1 }, { x1 * x1 + y1 * y1, y1, 1 }, { x2 * x2 + y2 * y2, y2, 1 } } ) / divisor;
			double k = determinant( new double[ ][ ] { { x0, x0 * x0 + y0 * y0, 1 }, { x1, x1 * x1 + y1 * y1, 1 }, { x2, x2 * x2 + y2 * y2, 1 } } ) / divisor;
			
			this.center.setLocation( h, k );
		}
	}
}
