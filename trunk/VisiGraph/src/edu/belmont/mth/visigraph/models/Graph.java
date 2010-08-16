/**
 * Graph.java
 */
package edu.belmont.mth.visigraph.models;

import java.awt.geom.*;
import java.util.*;
import java.util.Map.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;

/**
 * The {@code Graph} class represents the highest level data-structure in graph theory, containing—fundamentally and by definition—a list of vertices
 * and a corresponding list of edges connecting those vertices. Presently, this class supports not only simple, undirected graphs but also graphs with
 * loops (psuedographs), multi-edges (multigraphs), directed edges (digraphs), and every combination of the three. The {@code Graph} class also
 * supports acyclic graphs (trees), though multi-edges and loops must then be prohibited, as they inherently form cycles.
 * <p/>
 * In addition to the mathematical properties of a graph, the {@code Graph} class also includes a name property to help the user distinguish one graph
 * from another, and a list of captions used to point out regions of interest from within the graph itself. In order for this class to be saved to
 * file or shared through the web it must be fully serializable, and indeed is: with the {@link #toString()} method used for serialization using JSON
 * and the {@link #Graph(String)} constructor used for deserialization.
 * <p/>
 * As an {@link ObservableModel}, this class also supports multiple subscribing {@link Observer}s. Whenever a change is made to any part of a {@code
 * Graph}, notification of that change is automatically propagated upwards until it hits the {@code Graph} level. Be it the addition or removal of an
 * edge or even a change to one of the properties of a single vertex, all changes below the graph will trigger a
 * {@link ObservableModel#notifyObservers(Object)} call.
 * 
 * @author Cameron Behar
 * 
 * @see {@link Vertex}, {@link Edge}, {@link Caption}
 */
public class Graph extends ObservableModel
{
	/**
	 * The name of this graph used for identification and serialization
	 */
	public final Property<String> name;
	
	/**
	 * An unordered list of this graph's vertices, whose connecting edges are contained in this graph's {@link #edges} member
	 */
	public final ObservableList<Vertex> vertexes;
	
	/**
	 * An unordered list of this graph's edges, whose respective vertices are specified in each {@link Vertex} class
	 */
	public final ObservableList<Edge> edges;
	
	/**
	 * An unordered list of this graph's captions, used only for presentation purposes
	 */
	public final ObservableList<Caption> captions;
	
	/**
	 * A {@code boolean} indicating whether or not loops are allowed in this graph. A loop is an edge from a vertex to itself, and are only allowed in
	 * psuedographs.
	 */
	public final boolean areLoopsAllowed;
	
	/**
	 * A {@code boolean} indicating whether or not multi-edges are allowed in this graph. Multi-edges are distinct edges that connect connect the same
	 * two vertices in the same direction, and are only allowed in multigraphs.
	 */
	public final boolean areMultipleEdgesAllowed;
	
	/**
	 * A {@code boolean} indicating whether or not this graph is a directed graph, or digraph. Digraphs are graphs whose edges may only go in one
	 * direction (i.e. from A to B, but not necessarily B to A).
	 */
	public final boolean areDirectedEdgesAllowed;
	
	/**
	 * A {@code boolean} indicating whether or not cycles are allowed in this graph. Trees, for instance, are by definition acyclic.
	 */
	public final boolean areCyclesAllowed;
	
	/**
	 * A {@code boolean} flag indicating whether notifications are to be sent on to any of this graph's subscribed {@link Observer}s, or merely caught
	 * and handled internally
	 */
	private boolean notificationsSuspended;
	
	/**
	 * An {@code Observer} used to notify this Graph's subscribed {@code Observer}s of changes (e.g. after adding or removing a vertex), and to ensure
	 * that no edge continues to exist once one of its vertices has been removed
	 */
	private Observer vertexListObserver;
	
	/**
	 * An {@code Observer} used to notify this Graph's subscribed {@code Observer}s of changes (e.g. after adding or removing an edge)
	 */
	private Observer edgeListObserver;
	
	/**
	 * An {@code Observer} used to notify this Graph's subscribed {@code Observer}s of changes (e.g. after adding or removing a caption)
	 */
	private Observer captionListObserver;
	
	/**
	 * Constructs an empty undirected graph allowing loops, multi-edges, and cycles.
	 */
	public Graph ( )
	{
		this( UserSettings.instance.defaultGraphName.get( ), true, false, true, true );
	}
	
	/**
	 * Constructs a graph from the specified JSON string. This constructor can be used to deserialize a graph serialized by Graph's toString method.
	 * 
	 * @param json the json text from which to construct this graph
	 * 
	 * @see #toString()
	 */
	@SuppressWarnings( "unchecked" )
	public Graph ( String json )
	{
		Map<String, Object> members = JsonUtilities.parseObject( json );
		
		this.name = new Property<String>( (String) members.get( "name" ) );
		
		this.areLoopsAllowed         = (Boolean) members.get( "areLoopsAllowed" );
		this.areDirectedEdgesAllowed = (Boolean) members.get( "areDirectedEdgesAllowed" );
		this.areMultipleEdgesAllowed = (Boolean) members.get( "areMultipleEdgesAllowed" );
		this.areCyclesAllowed        = (Boolean) members.get( "areCyclesAllowed" );
		
		this.notificationsSuspended = false;
		this.vertexListObserver  = new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				if ( edges != null )
					fixEdges( );
				
				if ( !notificationsSuspended )
				{
					setChanged( );
					notifyObservers( arg );
				}
			}
		};
		this.edgeListObserver    = new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				if ( !notificationsSuspended )
				{
					setChanged( );
					notifyObservers( arg );
				}
			}
		};
		this.captionListObserver = new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				if ( !notificationsSuspended )
				{
					setChanged( );
					notifyObservers( arg );
				}
			}
		};
		
		Map<String, Vertex> idToVertexMap = new HashMap<String, Vertex>( );
		
		this.vertexes = new ObservableList<Vertex>( );
		for ( Object vertex : (Iterable<?>) members.get( "vertexes" ) )
			if ( vertex instanceof Map<?, ?> )
			{
				// This is the map of the vertex's properties
				Map<String, Object> vertexPropertyMap = (Map<String, Object>) vertex;
				
				// First we need to create the vertex from the map of properties
				Vertex newVertex = new Vertex( vertexPropertyMap );
				
				// Then we need to add the vertex to this graph
				this.vertexes.add( newVertex );
				
				// And finally, we have to record the vertex's original id, so that we can match it up when adding the edges later
				idToVertexMap.put( (String) vertexPropertyMap.get( "id" ), newVertex );
			}
		this.vertexes.addObserver( vertexListObserver );
		
		this.edges = new ObservableList<Edge>( );
		for ( Object edge : (Iterable<?>) members.get( "edges" ) )
			if ( edge instanceof Map<?, ?> )
				this.edges.add( new Edge( (Map<String, Object>) edge, idToVertexMap ) );
		this.edges.addObserver( edgeListObserver );
		
		this.captions = new ObservableList<Caption>( );
		for ( Object caption : (Iterable<?>) members.get( "captions" ) )
			if ( caption instanceof Map<?, ?> )
				this.captions.add( new Caption( (Map<String, Object>) caption ) );
		this.captions.addObserver( captionListObserver );
	}
	
	/**
	 * Constructs an empty graph with the name and settings specified. When not loading a graph from JSON string, this is the preferred constructor
	 * form.
	 * 
	 * @param name a {@code String} identifying this graph
	 * @param areLoopsAllowed a {@code boolean} indicating whether or not to allow loops in this graph
	 * @param areDirectedEdgesAllowed a {@code boolean} indicating whether or not this graph is a directed graph
	 * @param areMultipleEdgesAllowed a {@code boolean} indicating whether or not to allow multi-edges in this graph
	 * @param areCyclesAllowed a {@code boolean} indicating whether or not to allow cycles in this graph
	 */
	public Graph ( String name, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed )
	{
		this.name = new Property<String>( name );
		
		this.areLoopsAllowed = areLoopsAllowed;
		this.areDirectedEdgesAllowed = areDirectedEdgesAllowed;
		this.areMultipleEdgesAllowed = areMultipleEdgesAllowed;
		this.areCyclesAllowed = areCyclesAllowed;
		
		this.notificationsSuspended = false;
		this.vertexListObserver  = new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				if ( edges != null )
					fixEdges( );
				
				if ( !notificationsSuspended )
				{
					setChanged( );
					notifyObservers( arg );
				}
			}
		};
		this.edgeListObserver    = new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				if ( !notificationsSuspended )
				{
					setChanged( );
					notifyObservers( arg );
				}
			}
		};
		this.captionListObserver = new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				if ( !notificationsSuspended )
				{
					setChanged( );
					notifyObservers( arg );
				}
			}
		};
		
		this.vertexes = new ObservableList<Vertex>( );
		this.vertexes.addObserver( vertexListObserver );
		
		this.edges = new ObservableList<Edge>( );
		this.edges.addObserver( edgeListObserver );
		
		this.captions = new ObservableList<Caption>( );
		this.captions.addObserver( captionListObserver );
	}
	
	/**
	 * Returns a {@code boolean} indicating whether or not there exists a path between the two vertices. Although implemented using a relatively fast
	 * algorithm, the method still has a worst-case performance of O(|E| + |V|log|V|), where E is the set of all edges in the graph, and V is the set
	 * of all vertices. Caution must therefore be used when calling this method, especially where performance is a consideration.
	 * 
	 * @param from the vertex from which the path begins
	 * @param to the vertex at which the path ends
	 * 
	 * @return {@code true} if there exists a path between the two vertices in the specified direction, {@code false} otherwise
	 * 
	 * @see {@link Vertex}, {@link Edge}
	 */
	public boolean areConnected( Vertex from, Vertex to )
	{
		Set<Vertex> visited = new HashSet<Vertex>( );
		Stack<Vertex> toVisit = new Stack<Vertex>( );
		
		toVisit.push( to );
		
		while ( !toVisit.isEmpty( ) )
		{
			Vertex vertex = toVisit.pop( );
			if ( vertex == from )
				return true;
			visited.add( vertex );
			
			Set<Vertex> neighbors = getNeighbors( vertex );
			
			for ( Vertex neighbor : neighbors )
				if ( !visited.contains( neighbor ) )
					toVisit.push( neighbor );
		}
		
		return false;
	}
	
	/**
	 * Removes any edge in this graph that has had one of its vertices removed, and is therefore no longer valid.
	 * 
	 * @see {@link Vertex}, {@link Edge}
	 */
	private void fixEdges( )
	{
		suspendNotifications( true );
		
		int index = 0, originalSize = edges.size( );
		while ( index < edges.size( ) )
			if ( !vertexes.contains( edges.get( index ).from ) || !vertexes.contains( edges.get( index ).to ) )
				edges.remove( index );
			else
				++index;
		
		suspendNotifications( false );
		
		if ( edges.size( ) != originalSize )
		{
			setChanged( );
			notifyObservers( edges );
		}
	}
	
	/**
	 * Returns the set of all edges coincident to a given vertex. In an undirected graph this includes all edges, both from- and to-, the specified
	 * vertex. In digraphs, this includes edges from the specified vertex, but not those to it.
	 * 
	 * @param vertex the vertex to which the edges are coincident
	 * 
	 * @return the edges coincident to the specified vertex
	 * 
	 * @see {@link Vertex}, {@link Edge}
	 */
	public Set<Edge> getEdges( Vertex vertex )
	{
		HashSet<Edge> edgeSet = new HashSet<Edge>( );
		
		for ( Edge edge : edges )
			if ( edge.from == vertex || ( edge.to == vertex && !edge.isDirected ) )
				edgeSet.add( edge );
		
		return edgeSet;
	}
	
	/**
	 * Returns the set of all edges from one specified vertex to another. In a simple graph, this method will return at most one edge—in multigraphs,
	 * possibly more. Note that for digraphs and multi-digraphs, the returned set will not include edges going in the opposite direction (from {@code
	 * to} to {@code from}).
	 * 
	 * @param from the vertex from which the edges begin
	 * @param to the vertex with at the edges end
	 * 
	 * @return the edges to and from the specified vertices
	 * 
	 * @see {@link Vertex}, {@link Edge}
	 */
	public Set<Edge> getEdges( Vertex from, Vertex to )
	{
		HashSet<Edge> edgeSet = new HashSet<Edge>( );
		
		for ( Edge edge : edges )
			if ( ( edge.from == from && edge.to == to ) || ( edge.from == to && edge.to == from && !edge.isDirected ) )
				edgeSet.add( edge );
		
		return edgeSet;
	}
	
	/**
	 * Returns the set of all vertices that are neighbors of a given vertex. In an undirected graph, one vertex is another vertex's neighbor if the
	 * two share at least one common edge. In digraphs, vertex A is only vertex B's neighbor if there exists and edge from B to A, regardless of how
	 * many edges exist from A to B.
	 * 
	 * @param vertex the vertex to which the returned vertices are neighbors
	 * 
	 * @return the neighbors of the specified vertex
	 * 
	 * @see {@link Vertex}, {@link Edge}
	 */
	public Set<Vertex> getNeighbors( Vertex vertex )
	{
		HashSet<Vertex> ret = new HashSet<Vertex>( );
		
		for ( Edge edge : getEdges( vertex ) )
			ret.add( ( edge.from == vertex ) ? edge.to : edge.from );
		
		return ret;
	}
	
	/**
	 * Removes all elements from this graph that have their {@code isSelected} properties set to {@code true}.
	 * 
	 * @see {@link Vertex}, {@link Edge}, {@link Caption}
	 */
	public void removeSelected( )
	{
		// Delete all selected vertexes
		int i = 0;
		while ( i < vertexes.size( ) )
			if ( vertexes.get( i ).isSelected.get( ) )
				vertexes.remove( i );
			else
				++i;
		
		// Delete all selected edges
		i = 0;
		while ( i < edges.size( ) )
			if ( edges.get( i ).isSelected.get( ) )
				edges.remove( i );
			else
				++i;
		
		// Delete all selected captions
		i = 0;
		while ( i < captions.size( ) )
			if ( captions.get( i ).isSelected.get( ) )
				captions.remove( i );
			else
				++i;
	}
	
	/**
	 * Sets the {@code isSelected} property of all elements in this graph to {@code true} or <true>false}, depending upon the parameter.
	 * 
	 * @param select a {@code boolean} indicating whether to select or deselect all graph elements
	 * 
	 * @see {@link Vertex}, {@link Edge}, {@link Caption}
	 */
	public void selectAll( boolean select )
	{
		for ( Edge edge : edges )
			edge.isSelected.set( select );
		
		for ( Vertex vertex : vertexes )
			vertex.isSelected.set( select );
		
		for ( Caption caption : captions )
			caption.isSelected.set( select );
	}
	
	/**
	 * Temporarily suspends the notification all of property changes to subscribed {@link Observer}s. Most often this method is called when performing
	 * a large number of batch operations on a graph, so that subscribers are not overloaded with a multitude of notifications.
	 * 
	 * @param suspend a {@code boolean} indicating whether to suspend or reenable notifications to subscribed Observers
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
	 * Returns a string representation of this graph in JSON format. This method can be used to serialize a graph, which may later be deserialized
	 * using the class's string constructor. For more information on the JSON format (used throughout VisiGraph for serialization), visit <a
	 * href="http://www.json.org/">here</a>.
	 * 
	 * @return this {@code Graph} serialized as a {@code String}
	 * 
	 * @see #Graph(String)
	 */
	@Override
	public String toString( )
	{
		HashMap<String, Object> members = new HashMap<String, Object>( );
		
		members.put( "name",                    name                    );
		members.put( "vertexes",                vertexes                );
		members.put( "edges",                   edges                   );
		members.put( "captions",                captions                );
		members.put( "areLoopsAllowed",         areLoopsAllowed         );
		members.put( "areDirectedEdgesAllowed", areDirectedEdgesAllowed );
		members.put( "areMultipleEdgesAllowed", areMultipleEdgesAllowed );
		members.put( "areCyclesAllowed",        areCyclesAllowed        );
		
		return JsonUtilities.formatObject( members );
	}
	
	/**
	 * Translates all elements in this graph with {@code isSelected} properties set to {@code true} by a specified offset.
	 * 
	 * @param x the horizontal offset to be added to each selected element's location
	 * @param y the vertical offset to be added to each selected element's location
	 * 
	 * @see {@link Vertex}, {@link Edge}, {@link Caption}
	 */
	public void translateSelected( double x, double y )
	{
		HashMap<Edge, Point2D> edgeHandles = new HashMap<Edge, Point2D>( );
		
		// We have to split setting the edge handles into two parts because when we move its vertices, the handle will be reset to the midpoint.
		// If we are also moving adjacent vertices, we'll just let the handles of now linear edges be automatically reset (to maintain linearity).
		for ( Edge edge : edges )
			if ( edge.isSelected.get( ) && ( !( edge.from.isSelected.get( ) || edge.to.isSelected.get( ) ) || !edge.isLinear( ) ) )
				edgeHandles.put( edge, new Point2D.Double( edge.handleX.get( ) + x, edge.handleY.get( ) + y ) );
		
		// Moves the vertices (resetting their edges to simple lines)
		for ( Vertex vertex : vertexes )
			if ( vertex.isSelected.get( ) )
			{
				vertex.x.set( vertex.x.get( ) + x );
				vertex.y.set( vertex.y.get( ) + y );
			}
		
		// Now move the handles to where they should go
		for ( Entry<Edge, Point2D> entry : edgeHandles.entrySet( ) )
		{
			entry.getKey( ).handleX.set( entry.getValue( ).getX( ) );
			entry.getKey( ).handleY.set( entry.getValue( ).getY( ) );
		}
		
		// Move the captions
		for ( Caption caption : captions )
			if ( caption.isSelected.get( ) )
			{
				caption.x.set( caption.x.get( ) + x );
				caption.y.set( caption.y.get( ) + y );
			}
	}
	
	/**
	 * Performs a set-union with the specified graph, merging the two without conflicts. Note that the ids of the specified graph's vertices will be
	 * re-assigned, so that they do not conflict with existing vertex ids.
	 * 
	 * @param graph the graph with which to merge
	 */
	public void union( Graph graph )
	{
		suspendNotifications( true );
		
		Map<String, Vertex> newVertexes = new HashMap<String, Vertex>( );
		
		for ( Vertex vertex : graph.vertexes )
		{
			Vertex newVertex = new Vertex( vertex.toString( ) );
			newVertexes.put( vertex.id.get().toString( ), newVertex );
			newVertex.id.set( UUID.randomUUID( ) );
			this.vertexes.add( newVertex );
		}
		
		for ( Edge edge : graph.edges )
		{
			Edge newEdge = new Edge( edge.toString( ), newVertexes );
			this.edges.add( newEdge );
		}
		
		for ( Caption caption : graph.captions )
		{
			Caption newCaption = new Caption( caption.toString( ) );
			this.captions.add( newCaption );
		}
		
		suspendNotifications( false );
		
		setChanged( );
		notifyObservers( this );
	}
}
