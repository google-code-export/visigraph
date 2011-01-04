/**
 * Graph.java
 */
package edu.belmont.mth.visigraph.models;

import java.util.*;
import java.util.Map.*;
import java.awt.geom.*;
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
 * edge or even a change to one of the properties of a single vertex, all changes below the graph will trigger an
 * {@link ObservableModel#notifyObservers(Object)} call.
 * 
 * @author Cameron Behar
 * @see Vertex
 * @see Edge
 * @see Caption
 */
public class Graph extends ObservableModel
{
	/**
	 * The name of this graph used for identification and serialization
	 */
	public final Property<String>			name;
	
	/**
	 * An ordered set of this graph's vertices, whose connecting edges are contained in this graph's {@link #edges} member
	 */
	public final List<Vertex>				vertices;
	
	/**
	 * An ordered set of this graph's edges, whose respective vertices are specified in each {@link Edge} class
	 */
	public final List<Edge>					edges;
	
	/**
	 * An ordered set of this graph's captions, used only for presentation purposes
	 */
	public final List<Caption>				captions;
	
	/**
	 * A catch-all {@code String} that can be used to store this {@code Graph}'s metadata
	 */
	public final Property<String>			tag;
	
	/**
	 * A {@code boolean} indicating whether or not loops are allowed in this graph. A loop is an edge from a vertex to itself, and are only allowed in
	 * psuedographs.
	 */
	public final boolean					areLoopsAllowed;
	
	/**
	 * A {@code boolean} indicating whether or not multi-edges are allowed in this graph. Multi-edges are distinct edges that connect connect the same
	 * two vertices in the same direction, and are only allowed in multigraphs.
	 */
	public final boolean					areMultipleEdgesAllowed;
	
	/**
	 * A {@code boolean} indicating whether or not this graph is a directed graph, or digraph. Digraphs are graphs whose edges may only go in one
	 * direction (i.e. from A to B, but not necessarily B to A).
	 */
	public final boolean					areDirectedEdgesAllowed;
	
	/**
	 * A {@code boolean} indicating whether or not cycles are allowed in this graph. Trees, for instance, are by definition acyclic.
	 */
	public final boolean					areCyclesAllowed;
	
	/**
	 * A {@code Map} of vertices to edge-incidence lists
	 */
	private final Map<Vertex, Set<Edge>>	incidences;
	
	/**
	 * An {@code Observer} used to notify this graph's subscribed {@code Observer}s of changes to any of its elements' properties
	 */
	private final Observer					elementObserver;
	
	/**
	 * A {@code boolean} flag indicating whether notifications are to be sent on to any of this graph's subscribed {@link Observer}s, or merely caught
	 * and handled internally
	 */
	private boolean							notificationsSuspended;
	
	/**
	 * Constructs an empty undirected graph allowing loops, multi-edges, and cycles.
	 */
	public Graph( )
	{
		this( UserSettings.instance.defaultGraphName.get( ), true, false, true, true );
	}
	
	/**
	 * Constructs a graph from the specified map of attributes.
	 * 
	 * @param attributes the attributes of this graph, keyed with Strings
	 */
	@SuppressWarnings( "unchecked" )
	public Graph( Map<String, Object> attributes )
	{
		this( (String) attributes.get( "name" ), (Boolean) attributes.get( "areLoopsAllowed" ), (Boolean) attributes.get( "areDirectedEdgesAllowed" ), (Boolean) attributes.get( "areMultipleEdgesAllowed" ), (Boolean) attributes.get( "areCyclesAllowed" ) );
		
		this.tag.set( (String) attributes.get( "tag" ) );
		
		Map<String, Vertex> idToVertexMap = new HashMap<String, Vertex>( );
		
		for( Object vertex : (Iterable<?>) attributes.get( "vertices" ) )
			if( vertex instanceof Map<?, ?> )
			{
				Map<String, Object> vertexPropertyMap = (Map<String, Object>) vertex;
				Vertex newVertex = new Vertex( vertexPropertyMap );
				this.vertices.add( newVertex );
				idToVertexMap.put( (String) vertexPropertyMap.get( "id" ), newVertex );
			}
		
		for( Object edge : (Iterable<?>) attributes.get( "edges" ) )
			if( edge instanceof Map<?, ?> )
				this.edges.add( new Edge( (Map<String, Object>) edge, idToVertexMap ) );
		
		for( Object caption : (Iterable<?>) attributes.get( "captions" ) )
			if( caption instanceof Map<?, ?> )
				this.captions.add( new Caption( (Map<String, Object>) caption ) );
	}
	
	/**
	 * Constructs a graph from the specified JSON string. This constructor can be used to deserialize a graph serialized by Graph's toString method.
	 * 
	 * @param json the json text from which to construct this graph
	 * @see #toString()
	 */
	public Graph( String json )
	{
		this( JsonUtilities.parseObject( json ) );
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
	@SuppressWarnings( "serial" )
	public Graph( String name, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed )
	{
		this.name = new Property<String>( name );
		this.tag = new Property<String>( null );
		
		this.areLoopsAllowed = areLoopsAllowed;
		this.areDirectedEdgesAllowed = areDirectedEdgesAllowed;
		this.areMultipleEdgesAllowed = areMultipleEdgesAllowed;
		this.areCyclesAllowed = areCyclesAllowed;
		
		this.notificationsSuspended = false;
		this.elementObserver = new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				Graph.this.setChanged( );
				
				if( !Graph.this.notificationsSuspended )
					Graph.this.notifyObservers( arg );
			}
		};
		
		this.incidences = new HashMap<Vertex, Set<Edge>>( );
		
		this.vertices = new ArrayList<Vertex>( )
		{
			@Override
			public void add( int index, Vertex element )
			{
				if( this.contains( element ) )
					return;
				
				Graph.this.suspendNotifications( true );
				
				super.add( element );
				Graph.this.incidences.put( element, new HashSet<Edge>( ) );
				element.addObserver( Graph.this.elementObserver );
				
				Graph.this.suspendNotifications( false );
				
				Graph.this.setChanged( );
				Graph.this.notifyObservers( this );
			}
			
			@Override
			public boolean add( Vertex e )
			{
				int originalSize = super.size( );
				this.add( super.size( ), e );
				return originalSize != super.size( );
			}
			
			@Override
			public boolean addAll( Collection<? extends Vertex> c )
			{
				return this.addAll( this.size( ), c );
			}
			
			@Override
			public boolean addAll( int index, Collection<? extends Vertex> c )
			{
				int originalSize = super.size( );
				for( Vertex vertex : c )
					this.add( index++, vertex );
				return originalSize != super.size( );
			}
			
			@Override
			public void clear( )
			{
				Graph.this.suspendNotifications( true );
				
				for( Vertex vertex : this )
					vertex.deleteObserver( Graph.this.elementObserver );
				super.clear( );
				Graph.this.incidences.clear( );
				Graph.this.edges.clear( );
				
				Graph.this.suspendNotifications( false );
				
				Graph.this.setChanged( );
				Graph.this.notifyObservers( this );
			}
			
			@Override
			public boolean contains( Object o )
			{
				return Graph.this.incidences.containsKey( o );
			}
			
			@Override
			public boolean containsAll( Collection<?> c )
			{
				for( Object o : c )
					if( !this.contains( o ) )
						return false;
				
				return true;
			}
			
			@Override
			public Vertex remove( int index )
			{
				Graph.this.suspendNotifications( true );
				
				Vertex removedVertex = super.remove( index );
				
				// Normally, we would just work off the backing store directly, but since we'll also be modifying the vertex's edge-set, we have to
				// use the
				// getEdges() method to clone it first so we don't run into a ConcurrentModificationException
				Graph.this.edges.removeAll( Graph.this.getEdges( removedVertex ) );
				
				Graph.this.incidences.remove( removedVertex );
				removedVertex.deleteObserver( Graph.this.elementObserver );
				
				Graph.this.suspendNotifications( false );
				
				Graph.this.setChanged( );
				Graph.this.notifyObservers( this );
				
				return removedVertex;
			}
			
			@Override
			public boolean remove( Object o )
			{
				int index = this.indexOf( o );
				
				if( index == -1 )
					return false;
				this.remove( index );
				
				return true;
			}
			
			@Override
			public boolean removeAll( Collection<?> c )
			{
				int originalSize = this.size( );
				for( Object o : c )
					this.remove( o );
				return originalSize != this.size( );
			}
			
			@Override
			public boolean retainAll( Collection<?> c )
			{
				List<Vertex> removedVertices = new LinkedList<Vertex>( );
				for( Vertex vertex : this )
					if( !c.contains( vertex ) )
						removedVertices.add( vertex );
				
				return this.removeAll( removedVertices );
			}
			
			@Override
			public Vertex set( int index, Vertex element )
			{
				if( this.get( index ) == element )
					return element;
				else if( this.contains( element ) )
					return null;
				
				Graph.this.suspendNotifications( true );
				
				Vertex oldVertex = this.get( index );
				// Normally, we would just work off the backing store directly, but since we'll also be modifying the vertex's edge-set, we have to
				// use the
				// getEdges() method to clone it first so we don't run into a ConcurrentModificationException
				Graph.this.edges.removeAll( Graph.this.getEdges( oldVertex ) );
				Graph.this.incidences.remove( oldVertex );
				oldVertex.deleteObserver( Graph.this.elementObserver );
				
				super.set( index, element );
				
				Graph.this.incidences.put( element, new HashSet<Edge>( ) );
				element.addObserver( Graph.this.elementObserver );
				
				Graph.this.suspendNotifications( false );
				
				Graph.this.setChanged( );
				Graph.this.notifyObservers( this );
				
				return oldVertex;
			}
		};
		this.edges = new ArrayList<Edge>( )
		{
			@Override
			public boolean add( Edge e )
			{
				int originalSize = this.size( );
				this.add( this.size( ), e );
				return originalSize != this.size( );
			}
			
			@Override
			public void add( int index, Edge element )
			{
				if( !this.isValidAddition( element ) )
					return;
				
				Graph.this.suspendNotifications( true );
				
				super.add( index, element );
				Graph.this.incidences.get( element.from ).add( element );
				if( !element.isLoop )
					Graph.this.incidences.get( element.to ).add( element );
				element.addObserver( Graph.this.elementObserver );
				
				Graph.this.suspendNotifications( false );
				
				Graph.this.setChanged( );
				Graph.this.notifyObservers( this );
			}
			
			@Override
			public boolean addAll( Collection<? extends Edge> c )
			{
				return this.addAll( this.size( ), c );
			}
			
			@Override
			public boolean addAll( int index, Collection<? extends Edge> c )
			{
				int originalSize = this.size( );
				for( Edge edge : c )
					this.add( index++, edge );
				return originalSize != this.size( );
			}
			
			@Override
			public void clear( )
			{
				Graph.this.suspendNotifications( true );
				
				for( Edge edge : this )
					edge.deleteObserver( Graph.this.elementObserver );
				for( Set<Edge> edgeMapValue : Graph.this.incidences.values( ) )
					edgeMapValue.clear( );
				super.clear( );
				
				Graph.this.suspendNotifications( false );
				
				Graph.this.setChanged( );
				Graph.this.notifyObservers( this );
			}
			
			@Override
			public boolean contains( Object o )
			{
				return ( o instanceof Edge && Graph.this.incidences.containsKey( ( (Edge) o ).from ) && Graph.this.incidences.get( ( (Edge) o ).from ).contains( o ) );
			}
			
			@Override
			public boolean containsAll( Collection<?> c )
			{
				for( Object o : c )
					if( !this.contains( o ) )
						return false;
				
				return true;
			}
			
			private boolean isValidAddition( Edge element )
			{
				if( !Graph.this.incidences.containsKey( element.from ) || !Graph.this.incidences.containsKey( element.to ) )
					return false;
				else if( !Graph.this.areLoopsAllowed && element.isLoop )
					return false;
				else if( Graph.this.areDirectedEdgesAllowed != element.isDirected )
					return false;
				else if( this.contains( element ) )
					return false;
				else if( !Graph.this.areMultipleEdgesAllowed && !Graph.this.getEdges( element.from, element.to ).isEmpty( ) )
					return false;
				else if( !Graph.this.areCyclesAllowed && Graph.this.areConnected( element.from, element.to ) )
					return false;
				
				return true;
			}
			
			@Override
			public Edge remove( int index )
			{
				Graph.this.suspendNotifications( true );
				
				Edge removedEdge = super.remove( index );
				Graph.this.incidences.get( removedEdge.from ).remove( removedEdge );
				if( !removedEdge.isLoop )
					Graph.this.incidences.get( removedEdge.to ).remove( removedEdge );
				removedEdge.deleteObserver( Graph.this.elementObserver );
				
				Graph.this.suspendNotifications( false );
				
				Graph.this.setChanged( );
				Graph.this.notifyObservers( this );
				
				return removedEdge;
			}
			
			@Override
			public boolean remove( Object o )
			{
				int index = this.indexOf( o );
				
				if( index == -1 )
					return false;
				this.remove( index );
				
				return true;
			}
			
			@Override
			public boolean removeAll( Collection<?> c )
			{
				int originalSize = this.size( );
				for( Object o : c )
					this.remove( o );
				return originalSize != this.size( );
			}
			
			@Override
			public boolean retainAll( Collection<?> c )
			{
				List<Edge> removedEdges = new LinkedList<Edge>( );
				for( Edge edge : this )
					if( !c.contains( edge ) )
						removedEdges.add( edge );
				
				return this.removeAll( removedEdges );
			}
			
			@Override
			public Edge set( int index, Edge element )
			{
				if( this.get( index ) == element )
					return element;
				else if( !this.isValidAddition( element ) )
					return null;
				
				Graph.this.suspendNotifications( true );
				
				Edge oldEdge = this.get( index );
				Graph.this.incidences.get( oldEdge.from ).remove( oldEdge );
				if( !oldEdge.isLoop )
					Graph.this.incidences.get( oldEdge.to ).remove( oldEdge );
				oldEdge.deleteObserver( Graph.this.elementObserver );
				
				super.set( index, element );
				
				Graph.this.incidences.get( element.from ).add( element );
				if( !element.isLoop )
					Graph.this.incidences.get( element.to ).add( element );
				element.addObserver( Graph.this.elementObserver );
				
				Graph.this.suspendNotifications( false );
				
				Graph.this.setChanged( );
				Graph.this.notifyObservers( this );
				
				return oldEdge;
			}
		};
		this.captions = new ArrayList<Caption>( )
		{
			@Override
			public boolean add( Caption e )
			{
				int originalSize = this.size( );
				this.add( this.size( ), e );
				return originalSize != this.size( );
			}
			
			@Override
			public void add( int index, Caption element )
			{
				if( this.contains( element ) )
					return;
				
				Graph.this.suspendNotifications( true );
				
				super.add( index, element );
				element.addObserver( Graph.this.elementObserver );
				
				Graph.this.suspendNotifications( false );
				
				Graph.this.setChanged( );
				Graph.this.notifyObservers( this );
			}
			
			@Override
			public boolean addAll( Collection<? extends Caption> c )
			{
				return this.addAll( this.size( ), c );
			}
			
			@Override
			public boolean addAll( int index, Collection<? extends Caption> c )
			{
				int originalSize = this.size( );
				for( Caption caption : c )
					this.add( index++, caption );
				return originalSize != this.size( );
			}
			
			@Override
			public void clear( )
			{
				Graph.this.suspendNotifications( true );
				
				for( Caption caption : this )
					caption.deleteObserver( Graph.this.elementObserver );
				super.clear( );
				
				Graph.this.suspendNotifications( false );
				
				Graph.this.setChanged( );
				Graph.this.notifyObservers( this );
			}
			
			@Override
			public Caption remove( int index )
			{
				Graph.this.suspendNotifications( true );
				
				Caption removedCaption = super.remove( index );
				removedCaption.deleteObserver( Graph.this.elementObserver );
				
				Graph.this.suspendNotifications( false );
				
				Graph.this.setChanged( );
				Graph.this.notifyObservers( this );
				
				return removedCaption;
			}
			
			@Override
			public boolean remove( Object o )
			{
				int index = this.indexOf( o );
				
				if( index == -1 )
					return false;
				this.remove( index );
				
				return true;
			}
			
			@Override
			public boolean removeAll( Collection<?> c )
			{
				int originalSize = this.size( );
				for( Object o : c )
					this.remove( o );
				return originalSize != this.size( );
			}
			
			@Override
			public boolean retainAll( Collection<?> c )
			{
				List<Caption> removedCaptions = new LinkedList<Caption>( );
				for( Caption caption : this )
					if( !c.contains( caption ) )
						removedCaptions.add( caption );
				
				return this.removeAll( removedCaptions );
			}
			
			@Override
			public Caption set( int index, Caption element )
			{
				if( this.get( index ) == element )
					return element;
				else if( this.contains( element ) )
					return null;
				
				Graph.this.suspendNotifications( true );
				
				Caption oldCaption = this.get( index );
				oldCaption.deleteObserver( Graph.this.elementObserver );
				super.set( index, element );
				element.addObserver( Graph.this.elementObserver );
				
				Graph.this.suspendNotifications( false );
				
				Graph.this.setChanged( );
				Graph.this.notifyObservers( this );
				
				return oldCaption;
			}
		};
	}
	
	/**
	 * Returns a {@code boolean} indicating whether or not there exists a path between the two vertices. Although implemented using a relatively fast
	 * algorithm, the method still has a worst-case performance of O(|E| + |V|log|V|), where E is the set of all edges in the graph, and V is the set
	 * of all vertices. Caution must therefore be used when calling this method, especially where performance is a consideration.
	 * 
	 * @param from the vertex from which the path begins
	 * @param to the vertex at which the path ends
	 * @return {@code true} if there exists a path between the two vertices in the specified direction, {@code false} otherwise
	 * @see Vertex
	 * @see Edge
	 */
	public boolean areConnected( Vertex from, Vertex to )
	{
		Set<Vertex> visited = new HashSet<Vertex>( );
		Stack<Vertex> toVisit = new Stack<Vertex>( );
		
		toVisit.push( to );
		
		while( !toVisit.isEmpty( ) )
		{
			Vertex vertex = toVisit.pop( );
			if( vertex == from )
				return true;
			visited.add( vertex );
			
			Set<Vertex> neighbors = this.getNeighbors( vertex );
			
			for( Vertex neighbor : neighbors )
				if( !visited.contains( neighbor ) )
					toVisit.push( neighbor );
		}
		
		return false;
	}
	
	/**
	 * Returns the set of all edges incident to a given vertex. In both directed and undirected graphs this includes all edges, both from- and to-,
	 * the specified vertex.
	 * 
	 * @param vertex the vertex to which the edges are incident
	 * @return the edges incident to the specified vertex
	 * @see Vertex
	 * @see Edge
	 */
	public Set<Edge> getEdges( Vertex vertex )
	{
		return new HashSet<Edge>( this.incidences.get( vertex ) );
	}
	
	/**
	 * Returns the set of all edges going from one specified vertex to another. In a simple graph, this method will return at most one edge—in
	 * multigraphs, possibly more. Note that for digraphs and multi-digraphs, the returned set will not include edges going in the opposite direction
	 * (from {@code to} to {@code from}).
	 * 
	 * @param from the vertex from which the edges go
	 * @param to the vertex to which the edges go
	 * @return the edges bridging the specified vertices
	 * @see Vertex
	 * @see Edge
	 */
	public Set<Edge> getEdges( Vertex from, Vertex to )
	{
		Set<Edge> spanningEdges = new HashSet<Edge>( );
		for( Edge edge : this.incidences.get( from ) )
			if( ( edge.from == from && edge.to == to ) || ( !edge.isDirected && edge.from == to && edge.to == from ) )
				spanningEdges.add( edge );
		
		return spanningEdges;
	}
	
	/**
	 * Returns the set of all edges coming from a given vertex. In an undirected graph this includes all edges incident to the specified vertex. In
	 * digraphs, this includes edges from the specified vertex, but not those to it.
	 * 
	 * @param vertex the vertex from which the edges go
	 * @return the edges going from the specified vertex
	 * @see Vertex
	 * @see Edge
	 */
	public Set<Edge> getEdgesFrom( Vertex vertex )
	{
		if( !this.areDirectedEdgesAllowed )
			return this.getEdges( vertex );
		
		Set<Edge> edgesFrom = new HashSet<Edge>( );
		for( Edge edge : this.incidences.get( vertex ) )
			if( edge.from == vertex )
				edgesFrom.add( edge );
		
		return edgesFrom;
	}
	
	/**
	 * Returns the set of all edges going to a given vertex. In an undirected graph this includes all edges incident to the specified vertex. In
	 * digraphs, this includes edges to the specified vertex, but not those from it.
	 * 
	 * @param vertex the vertex to which the edges go
	 * @return the edges going to the specified vertex
	 * @see Vertex
	 * @see Edge
	 */
	public Set<Edge> getEdgesTo( Vertex vertex )
	{
		if( !this.areDirectedEdgesAllowed )
			return this.getEdges( vertex );
		
		Set<Edge> edgesTo = new HashSet<Edge>( );
		for( Edge edge : this.incidences.get( vertex ) )
			if( edge.to == vertex )
				edgesTo.add( edge );
		
		return edgesTo;
	}
	
	/**
	 * Returns the set of all vertices that are neighbors of a given vertex. In an undirected graph, one vertex is another vertex's neighbor iff the
	 * two share at least one common edge. In digraphs, vertex A is only vertex B's neighbor if there exists and edge from B to A, regardless of how
	 * many edges exist from A to B.
	 * 
	 * @param vertex the vertex to which the returned vertices are neighbors
	 * @return the neighbors of the specified vertex
	 * @see Vertex
	 * @see Edge
	 */
	public Set<Vertex> getNeighbors( Vertex vertex )
	{
		Set<Vertex> neighbors = new HashSet<Vertex>( );
		for( Edge edge : this.getEdgesFrom( vertex ) )
			neighbors.add( ( edge.from == vertex ) ? edge.to : edge.from );
		
		return neighbors;
	}
	
	/**
	 * Returns a list of all captions in this graph with isSelected flags set to {@code true}
	 * 
	 * @return the selected captions in this graph
	 * @see #hasSelectedCaptions()
	 * @see #getSelectedEdges()
	 * @see #getSelectedVertices()
	 */
	public List<Caption> getSelectedCaptions( )
	{
		List<Caption> selected = new ArrayList<Caption>( this.captions.size( ) );
		
		for( Caption caption : this.captions )
			if( caption.isSelected.get( ) )
				selected.add( caption );
		
		return selected;
	}
	
	/**
	 * Returns a list of all edges in this graph with isSelected flags set to {@code true}
	 * 
	 * @return the selected edges in this graph
	 * @see #hasSelectedEdges()
	 * @see #getSelectedCaptions()
	 * @see #getSelectedVertices()
	 */
	public List<Edge> getSelectedEdges( )
	{
		List<Edge> selected = new ArrayList<Edge>( this.edges.size( ) );
		
		for( Edge edge : this.edges )
			if( edge.isSelected.get( ) )
				selected.add( edge );
		
		return selected;
	}
	
	/**
	 * Returns a list of all vertices in this graph with isSelected flags set to {@code true}
	 * 
	 * @return the selected vertices in this graph
	 * @see #hasSelectedVertices()
	 * @see #getSelectedCaptions()
	 * @see #getSelectedEdges()
	 */
	public List<Vertex> getSelectedVertices( )
	{
		List<Vertex> selected = new ArrayList<Vertex>( this.vertices.size( ) );
		
		for( Vertex vertex : this.vertices )
			if( vertex.isSelected.get( ) )
				selected.add( vertex );
		
		return selected;
	}
	
	/**
	 * Returns a {@code boolean} indicating whether or not the graph has at least one caption with its isSelected flags set to {@code true}.
	 * 
	 * @return {@code true} if the graph contains at least one selected caption, {@code false} otherwise
	 * @see #getSelectedCaptions()
	 * @see #hasSelectedEdges()
	 * @see #hasSelectedVertices()
	 */
	public boolean hasSelectedCaptions( )
	{
		for( Caption caption : this.captions )
			if( caption.isSelected.get( ) )
				return true;
		
		return false;
	}
	
	/**
	 * Returns a {@code boolean} indicating whether or not the graph has at least one edge with its isSelected flags set to {@code true}.
	 * 
	 * @return {@code true} if the graph contains at least one selected edge, {@code false} otherwise
	 * @see #getSelectedEdges()
	 * @see #hasSelectedCaptions()
	 * @see #hasSelectedVertices()
	 */
	public boolean hasSelectedEdges( )
	{
		for( Edge edge : this.edges )
			if( edge.isSelected.get( ) )
				return true;
		
		return false;
	}
	
	/**
	 * Returns a {@code boolean} indicating whether or not the graph has at least one vertex with its isSelected flags set to {@code true}.
	 * 
	 * @return {@code true} if the graph contains at least one selected vertex, {@code false} otherwise
	 * @see #getSelectedVertices()
	 * @see #hasSelectedCaptions()
	 * @see #hasSelectedEdges()
	 */
	public boolean hasSelectedVertices( )
	{
		for( Vertex vertex : this.vertices )
			if( vertex.isSelected.get( ) )
				return true;
		
		return false;
	}
	
	/**
	 * Sets the {@code isSelected} property of all elements in this graph to {@code true} or <true>false}, depending upon the parameter.
	 * 
	 * @param select a {@code boolean} indicating whether to select or deselect all graph elements
	 * @see Vertex
	 * @see Edge
	 * @see Caption
	 */
	public void selectAll( boolean select )
	{
		for( Edge edge : this.edges )
			edge.isSelected.set( select );
		
		for( Vertex vertex : this.vertices )
			vertex.isSelected.set( select );
		
		for( Caption caption : this.captions )
			caption.isSelected.set( select );
	}
	
	/**
	 * Temporarily suspends the notification all of property changes to subscribed {@link Observer}s. Most often this method is called when performing
	 * a large number of batch operations on a graph, so that subscribers are not overloaded with a multitude of notifications.
	 * 
	 * @param suspend a {@code boolean} indicating whether to suspend or reenable notifications to subscribed Observers
	 * @return {@code true} if notifications were previously suspended, {@code false} otherwise
	 * @see ObservableModel
	 * @see Observer
	 * @see ObservableList
	 */
	public boolean suspendNotifications( boolean suspend )
	{
		boolean wasSuspended = this.notificationsSuspended;
		this.notificationsSuspended = suspend;
		return wasSuspended;
	}
	
	/**
	 * Returns a string representation of this graph in JSON format. This method can be used to serialize a graph, which may later be deserialized
	 * using the class's string constructor. For more information on the JSON format (used throughout VisiGraph for serialization), visit <a
	 * href="http://www.json.org/">here</a>.
	 * 
	 * @return this {@code Graph} serialized as a {@code String}
	 * @see #Graph(String)
	 */
	@Override
	public String toString( )
	{
		Map<String, Object> members = new HashMap<String, Object>( );
		
		members.put( "name", this.name );
		members.put( "vertices", this.vertices );
		members.put( "edges", this.edges );
		members.put( "captions", this.captions );
		members.put( "areLoopsAllowed", this.areLoopsAllowed );
		members.put( "areDirectedEdgesAllowed", this.areDirectedEdgesAllowed );
		members.put( "areMultipleEdgesAllowed", this.areMultipleEdgesAllowed );
		members.put( "areCyclesAllowed", this.areCyclesAllowed );
		
		return JsonUtilities.formatObject( members );
	}
	
	/**
	 * Translates all elements in this graph with {@code isSelected} properties set to {@code true} by a specified offset.
	 * 
	 * @param x the horizontal offset to be added to each selected element's location
	 * @param y the vertical offset to be added to each selected element's location
	 * @see Vertex
	 * @see Edge
	 * @see Caption
	 */
	public void translateSelected( double x, double y )
	{
		Map<Edge, Point2D> edgeHandles = new HashMap<Edge, Point2D>( );
		
		// We have to split setting the edge handles into two parts because when we move its vertices, the handle will be reset to the midpoint.
		// If we are also moving adjacent vertices, we'll just let the handles of now linear edges be automatically reset (to maintain linearity).
		for( Edge edge : this.getSelectedEdges( ) )
			if( !( edge.from.isSelected.get( ) || edge.to.isSelected.get( ) ) || !edge.isLinear( ) )
				edgeHandles.put( edge, new Point2D.Double( edge.handleX.get( ) + x, edge.handleY.get( ) + y ) );
		
		// Moves the vertices (resetting their edges to simple lines)
		for( Vertex vertex : this.getSelectedVertices( ) )
		{
			vertex.x.set( vertex.x.get( ) + x );
			vertex.y.set( vertex.y.get( ) + y );
		}
		
		// Now move the handles to where they should go
		for( Entry<Edge, Point2D> entry : edgeHandles.entrySet( ) )
		{
			entry.getKey( ).handleX.set( entry.getValue( ).getX( ) );
			entry.getKey( ).handleY.set( entry.getValue( ).getY( ) );
		}
		
		// Move the captions
		for( Caption caption : this.getSelectedCaptions( ) )
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
		this.suspendNotifications( true );
		
		Map<String, Vertex> newVertices = new HashMap<String, Vertex>( );
		
		for( Vertex vertex : graph.vertices )
		{
			Vertex newVertex = new Vertex( vertex.toString( ) );
			newVertices.put( vertex.id.get( ).toString( ), newVertex );
			newVertex.id.set( UUID.randomUUID( ) );
			this.vertices.add( newVertex );
		}
		
		for( Edge edge : graph.edges )
			this.edges.add( new Edge( edge.toString( ), newVertices ) );
		
		for( Caption caption : graph.captions )
			this.captions.add( new Caption( caption.toString( ) ) );
		
		this.suspendNotifications( false );
		
		this.setChanged( );
		this.notifyObservers( this );
	}
}
