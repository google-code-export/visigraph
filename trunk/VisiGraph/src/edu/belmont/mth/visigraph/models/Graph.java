/**
 * Graph.java
 */
package edu.belmont.mth.visigraph.models;

import java.awt.geom.*;
import java.util.*;
import java.util.Map.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.views.*;

/**
 * The <code>Graph</code> class represents the highest level data-structure in graph theory, containing—fundamentally and by definition—a list
 * of vertices and a corresponding list of edges connecting those vertices. Presently, this class supports not only simple, undirected graphs
 * but also graphs with loops (psuedographs), multi-edges (multigraphs), directed edges (digraphs), and every combination of the three. The
 * <code>Graph</code> class also supports acyclic graphs (trees), though multi-edges and loops must then be prohibited, as they inherently form
 * cycles.
 * <p/>
 * In addition to the mathematical properties of a graph, the <code>Graph</code> class also includes a name property to help the user distinguish one
 * graph from another, and a list of captions used to point out regions of interest from within the graph itself. In order for this class to be saved
 * to file or shared through the web it must be fully serializable, and indeed is: with the {@link #toString()} method used for serialization using
 * JSON and the {@link #Graph(String)} constructor used for deserialization.
 * <p/>
 * As an {@link ObservableBase}, this class also supports multiple subscribing {@link ObserverBase}s. Whenever a change is made to any part of a
 * <code>Graph</code>, notification of that change is automatically propagated upwards until it hits the <code>Graph</code> level. Be it the addition
 * or removal of an edge or even a change to one of the properties of a single vertex, all changes below the graph will trigger a
 * {@link #notifyObservers(Object)} call.
 * 
 * @author Cameron Behar
 */
public class Graph extends ObservableBase
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
	 * A <code>boolean</code> indicating whether or not loops are allowed in this graph. A loop is an edge from a vertex to itself, and are only
	 * allowed in psuedographs.
	 */
	public final boolean areLoopsAllowed;
	
	/**
	 * A <code>boolean</code> indicating whether or not multi-edges are allowed in this graph. Multi-edges are distinct edges that connect connect the
	 * same two vertices in the same direction, and are only allowed in multigraphs.
	 */
	public final boolean areMultipleEdgesAllowed;
	
	/**
	 * A <code>boolean</code> indicating whether or not this graph is a directed graph, or digraph. Digraphs are graphs whose edges may only go in one
	 * direction (i.e. from A to B, but not necessarily B to A).
	 */
	public final boolean areDirectedEdgesAllowed;
	
	/**
	 * A <code>boolean</code> indicating whether or not cycles are allowed in this graph. Trees, for instance, are by definition acyclic.
	 */
	public final boolean areCyclesAllowed;
	
	/**
	 * A <code>boolean</code> flag indicating whether notifications are to be sent on to any of this graph's {@link ObserverBase}s, or merely caught
	 * and handled internally
	 */
	private boolean notificationsSuspended;
	
	/**
	 * An <code>ObserverBase</code> used to notify this Graph's subscribed <code>ObserverBase</code>s of changes (e.g. after adding or removing a
	 * vertex), and to ensure that no edge continues to exist once one of its vertices has been removed
	 */
	private ObserverBase vertexListObserver;
	
	/**
	 * An <code>ObserverBase</code> used to notify this Graph's subscribed <code>ObserverBase</code>s of changes (e.g. after adding or removing an
	 * edge)
	 */
	private ObserverBase edgeListObserver;
	
	/**
	 * An <code>ObserverBase</code> used to notify this Graph's subscribed <code>ObserverBase</code>s of changes (e.g. after adding or removing a
	 * caption)
	 */
	private ObserverBase captionListObserver;
	
	/**
	 * Constructs an empty undirected graph allowing loops, multi-edges, and cycles.
	 */
	public Graph()
	{
		this(UserSettings.instance.defaultGraphName.get(), true, false, true, true);
	}
	
	/**
	 * Constructs an empty graph with the name and settings specified. When not loading a graph from JSON string, this is the preferred constructor
	 * form.
	 */
	public Graph(String name, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed)
	{
		this.name = new Property<String>(name, "name");
		
		this.areLoopsAllowed = areLoopsAllowed;
		this.areDirectedEdgesAllowed = areDirectedEdgesAllowed;
		this.areMultipleEdgesAllowed = areMultipleEdgesAllowed;
		this.areCyclesAllowed = areCyclesAllowed;
		
		this.notificationsSuspended = false;
		this.vertexListObserver = new ObserverBase()
		{
			@Override
			public void hasChanged(Object source)
			{
				if (edges != null)
					fixEdges();
				if (!notificationsSuspended)
					notifyObservers(source);
			}
		};
		this.edgeListObserver = new ObserverBase()
		{
			@Override
			public void hasChanged(Object source)
			{
				if (!notificationsSuspended)
					notifyObservers(source);
			}
		};
		this.captionListObserver = new ObserverBase()
		{
			@Override
			public void hasChanged(Object source)
			{
				if (!notificationsSuspended)
					notifyObservers(source);
			}
		};
		
		this.vertexes = new ObservableList<Vertex>("vertexes");
		this.vertexes.addObserver(vertexListObserver);
		
		this.edges = new ObservableList<Edge>("edges");
		this.edges.addObserver(edgeListObserver);
		
		this.captions = new ObservableList<Caption>("captions");
		this.captions.addObserver(captionListObserver);
	}
	
	/**
	 * Constructs a graph from the specified JSON string. This constructor can be used to deserialize a graph serialized by Graph's toString method.
	 * 
	 * @see #toString()
	 */
	@SuppressWarnings("unchecked")
	public Graph(String json)
	{
		Map<String, Object> members = JsonUtilities.parseObject(json);
		
		this.name = new Property<String>((String) members.get("name"), "name");
		
		this.areLoopsAllowed = (Boolean) members.get("areLoopsAllowed");
		this.areDirectedEdgesAllowed = (Boolean) members.get("areDirectedEdgesAllowed");
		this.areMultipleEdgesAllowed = (Boolean) members.get("areMultipleEdgesAllowed");
		this.areCyclesAllowed = (Boolean) members.get("areCyclesAllowed");
		
		this.notificationsSuspended = false;
		this.vertexListObserver = new ObserverBase()
		{
			@Override
			public void hasChanged(Object source)
			{
				if (edges != null)
					fixEdges();
				
				if (!notificationsSuspended)
					notifyObservers(source);
			}
		};
		this.edgeListObserver = new ObserverBase()
		{
			@Override
			public void hasChanged(Object source)
			{
				if (!notificationsSuspended)
					notifyObservers(source);
			}
		};
		this.captionListObserver = new ObserverBase()
		{
			@Override
			public void hasChanged(Object source)
			{
				if (!notificationsSuspended)
					notifyObservers(source);
			}
		};
		
		this.vertexes = new ObservableList<Vertex>("vertexes");
		for (Object vertex : (Iterable<?>) members.get("vertexes"))
			if (vertex instanceof Map<?, ?>)
				this.vertexes.add(new Vertex((Map<String, Object>) vertex));
		this.vertexes.addObserver(vertexListObserver);
		
		Map<Integer, Vertex> vertexMap = new HashMap<Integer, Vertex>();
		for (int i = 0; i < this.vertexes.size(); ++i)
			vertexMap.put(this.vertexes.get(i).id.get(), this.vertexes.get(i));
		
		this.edges = new ObservableList<Edge>("edges");
		for (Object edge : (Iterable<?>) members.get("edges"))
			if (edge instanceof Map<?, ?>)
				this.edges.add(new Edge((Map<String, Object>) edge, vertexMap));
		this.edges.addObserver(edgeListObserver);
		
		this.captions = new ObservableList<Caption>("captions");
		for (Object caption : (Iterable<?>) members.get("captions"))
			if (caption instanceof Map<?, ?>)
				this.captions.add(new Caption((Map<String, Object>) caption));
		this.captions.addObserver(captionListObserver);
	}
	
	/**
	 * Sets the <code>isSelected</code> property of all elements in this graph to <code>true</code> or <true>false</code>, depending upon the
	 * parameter.
	 * 
	 * @param select
	 *            a <code>boolean</code> indicating whether to select or deselect all graph elements
	 * 
	 * @see {@link Vertex}, {@link Edge}, {@link Caption}
	 */
	public void selectAll(boolean select)
	{
		for (Edge edge : edges)
			edge.isSelected.set(select);
		
		for (Vertex vertex : vertexes)
			vertex.isSelected.set(select);
		
		for (Caption caption : captions)
			caption.isSelected.set(select);
	}
	
	/**
	 * Removes all elements from this graph that have their <code>isSelected</code> properties set to <code>true</code>.
	 * 
	 * @see {@link Vertex}, {@link Edge}, {@link Caption}
	 */
	public void removeSelected()
	{
		// Delete all selected vertexes
		int i = 0;
		while (i < vertexes.size())
		{
			if (vertexes.get(i).isSelected.get())
				vertexes.remove(i);
			else
				++i;
		}
		
		// Delete all selected edges
		i = 0;
		while (i < edges.size())
		{
			if (edges.get(i).isSelected.get())
				edges.remove(i);
			else
				++i;
		}
		
		// Delete all selected captions
		i = 0;
		while (i < captions.size())
		{
			if (captions.get(i).isSelected.get())
				captions.remove(i);
			else
				++i;
		}
	}
	
	/**
	 * Translates all elements in this graph with <code>isSelected</code> properties set to <code>true</code> by a specified offset.
	 * 
	 * @param x
	 *            the horizontal offset to be added to each selected element's location
	 * @param y
	 *            the vertical offset to be added to each selected element's location
	 * 
	 * @see {@link Vertex}, {@link Edge}, {@link Caption}
	 */
	public void translateSelected(double x, double y)
	{
		HashMap<Edge, Point2D> edgeHandles = new HashMap<Edge, Point2D>();
		
		// We have to split setting the edge handles into two parts because when we move its vertices, the handle will be reset to the midpoint.
		// If we are also moving adjacent vertices, we'll just let the handles of now linear edges be automatically reset (to maintain linearity).
		for (Edge edge : edges)
			if (edge.isSelected.get() && (!(edge.from.isSelected.get() || edge.to.isSelected.get()) || !edge.isLinear()))
				edgeHandles.put(edge, new Point2D.Double(edge.handleX.get() + x, edge.handleY.get() + y));
		
		// Moves the vertices (resetting their edges to simple lines)
		for (Vertex vertex : vertexes)
			if (vertex.isSelected.get())
			{
				vertex.x.set(vertex.x.get() + x);
				vertex.y.set(vertex.y.get() + y);
			}
		
		// Now move the handles to where they should go
		for (Entry<Edge, Point2D> entry : edgeHandles.entrySet())
		{
			entry.getKey().handleX.set(entry.getValue().getX());
			entry.getKey().handleY.set(entry.getValue().getY());
		}
		
		// Move the captions
		for (Caption caption : captions)
			if (caption.isSelected.get())
			{
				caption.x.set(caption.x.get() + x);
				caption.y.set(caption.y.get() + y);
			}
	}
	
	/**
	 * Removes any edge in this graph that has had one of its vertices removed, and is therefore no longer valid.
	 * 
	 * @see {@link Vertex}, {@link Edge}
	 */
	private void fixEdges()
	{
		suspendNotifications(true);
		
		int index = 0, originalSize = edges.size();
		while (index < edges.size())
			if (!vertexes.contains(edges.get(index).from) || !vertexes.contains(edges.get(index).to))
				edges.remove(index);
			else
				++index;
		
		suspendNotifications(false);
		
		if (edges.size() != originalSize)
			notifyObservers(edges);
	}
	
	/**
	 * Returns the set of all edges coincident to a given vertex. In an undirected graph this includes all edges, both from- and to-, the specified
	 * vertex. In digraphs, this includes edges from the specified vertex, but not those to it.
	 * 
	 * @param vertex
	 *            the vertex to which the edges are coincident
	 * 
	 * @return the edges coincident to the specified vertex
	 * 
	 * @see {@link Vertex}, {@link Edge}
	 */
	public Set<Edge> getEdges(Vertex v)
	{
		HashSet<Edge> edgeSet = new HashSet<Edge>();
		
		for (Edge edge : edges)
			if (edge.from == v || (edge.to == v && !edge.isDirected))
				edgeSet.add(edge);
		
		return edgeSet;
	}
	
	/**
	 * Returns the set of all edges from one specified vertex to another. In a simple graph, this method will return at most one edge—in multigraphs,
	 * possibly more. Note that for digraphs and multi-digraphs, the returned set will not include edges going in the opposite direction (from
	 * <code>to</code> to <code>from</code>).
	 * 
	 * @param from
	 *            the vertex from which the edges begin
	 * @param to
	 *            the vertex with at the edges end
	 * 
	 * @return the edges to and from the specified vertices
	 * 
	 * @see {@link Vertex}, {@link Edge}
	 */
	public Set<Edge> getEdges(Vertex from, Vertex to)
	{
		HashSet<Edge> edgeSet = new HashSet<Edge>();
		
		for (Edge edge : edges)
			if ((edge.from == from && edge.to == to) || (edge.from == to && edge.to == from && !edge.isDirected))
				edgeSet.add(edge);
		
		return edgeSet;
	}
	
	/**
	 * Returns the set of all vertices that are neighbors of a given vertex. In an undirected graph, one vertex is another vertex's neighbor if the
	 * two share at least one common edge. In digraphs, vertex A is only vertex B's neighbor if there exists and edge from B to A, regardless of how
	 * many edges exist from A to B.
	 * 
	 * @param v
	 *            the vertex to which the returned vertices are neighbors
	 * 
	 * @return the neighbors of the specified vertex
	 * 
	 * @see {@link Vertex}, {@link Edge}
	 */
	public Set<Vertex> getNeighbors(Vertex v)
	{
		HashSet<Vertex> ret = new HashSet<Vertex>();
		
		for (Edge edge : getEdges(v))
			ret.add((edge.from == v) ? edge.to : edge.from);
		
		return ret;
	}
	
	/**
	 * Returns a <code>boolean</code> indicating whether or not there exists a path between the two vertices. Although implemented using a relatively
	 * fast algorithm, the method still has a worst-case performance of O(|E| + |V|log|V|), where E is the set of all edges in the graph, and V is the
	 * set of all vertices. Caution must therefore be used when calling this method, especially where performance is a consideration.
	 * 
	 * @param from
	 *            the vertex from which the path begins
	 * @param to
	 *            the vertex at which the path ends
	 * 
	 * @return <code>true</code> if there exists a path between the two vertices in the specified direction; otherwise, <code>false</code>
	 * 
	 * @see {@link Vertex}, {@link Edge}
	 */
	public boolean areConnected(Vertex from, Vertex to)
	{
		Set<Vertex> visited = new HashSet<Vertex>();
		Stack<Vertex> toVisit = new Stack<Vertex>();
		
		toVisit.push(to);
		
		while (!toVisit.isEmpty())
		{
			Vertex vertex = toVisit.pop();
			if (vertex == from)
				return true;
			visited.add(vertex);
			
			Set<Vertex> neighbors = getNeighbors(vertex);
			
			for (Vertex neighbor : neighbors)
				if (!visited.contains(neighbor))
					toVisit.push(neighbor);
		}
		
		return false;
	}
	
	/**
	 * Returns an id exactly one more than the highest id in this graph's set of vertices. This id can then be used by a new vertex, which when added,
	 * will not conflict with other existing vertex ids.
	 * <p/>
	 * Note that this method does not guarantee that the id returned is the lowest available id, only that it is unique and will not cause future
	 * collision when this graph is serialized and then deserialized. This means that if an id has been skipped, it will not be returned until there
	 * are no higher ids in this graph's list. Also, when looking at performance, it is important to consider that this method operates in O(|V|),
	 * where V is the set of all vertices in the graph. Caution must therefore be used when using this method to generate large graphs, as the sum
	 * impact of all calls to this method may seriously inhibit performance.
	 * 
	 * @return an <code>int</code> containing the new vertex's id
	 * 
	 * @see {@link Vertex}
	 */
	public int nextVertexId()
	{
		int maxInt = -1;
		
		for (Vertex vertex : vertexes)
			if (vertex.id.get() > maxInt)
				maxInt = vertex.id.get();
		
		return maxInt + 1;
	}
	
	/**
	 * Temporarily suspends the notification all of property changes to subscribed {@link ObserverBase}s. Most often this method is called when
	 * performing a large number of batch operations on a graph, so that subscribers are not overloaded with a multitude of notifications.
	 * 
	 * @param s
	 *            a <code>boolean</code> indicating whether to suspend or reenable notifications to subscribed ObserverBases
	 * 
	 * @see {@link ObservableBase}, {@link ObserverBase}, {@link ObservableList}
	 */
	public boolean suspendNotifications(boolean s)
	{
		boolean ret = notificationsSuspended;
		notificationsSuspended = s;
		return ret;
	}
	
	/**
	 * Returns a string representation of this graph in JSON format. This method can be used to serialize a graph, which may later be deserialized
	 * using the class's string constructor. For more information on the JSON format (used throughout VisiGraph for serialization), visit <a
	 * href="http://www.json.org/">here</a>.
	 * 
	 * @see #Graph(String)
	 */
	@Override
	public String toString()
	{
		HashMap<String, Object> members = new HashMap<String, Object>();
		
		members.put("name", name);
		members.put("vertexes", vertexes);
		members.put("edges", edges);
		members.put("captions", captions);
		members.put("areLoopsAllowed", areLoopsAllowed);
		members.put("areDirectedEdgesAllowed", areDirectedEdgesAllowed);
		members.put("areMultipleEdgesAllowed", areMultipleEdgesAllowed);
		members.put("areCyclesAllowed", areCyclesAllowed);
		
		return JsonUtilities.formatObject(members);
	}
	
	/**
	 * Performs a set-union with the specified graph, merging the two without conflicts. Note that the ids of the specified graph's vertices will be
	 * re-assigned, so that they do not conflict with existing vertex ids.
	 * 
	 * @param g
	 *            the graph with which to merge
	 * 
	 * @return the edges coincident to the specified vertex
	 */
	public void union(Graph g)
	{
		suspendNotifications(true);
		
		Map<Integer, Vertex> newVertexes = new HashMap<Integer, Vertex>();
		
		for (Vertex vertex : g.vertexes)
		{
			Vertex newVertex = new Vertex(vertex.toString());
			newVertexes.put(newVertex.id.get(), newVertex);
			newVertex.id.set(this.nextVertexId());
			this.vertexes.add(newVertex);
		}
		
		for (Edge edge : g.edges)
		{
			Edge newEdge = new Edge(edge.toString(), newVertexes);
			this.edges.add(newEdge);
		}
		
		for (Caption caption : g.captions)
		{
			Caption newCaption = new Caption(caption.toString());
			this.captions.add(newCaption);
		}
		
		suspendNotifications(false);
		
		notifyObservers(this);
	}
}
