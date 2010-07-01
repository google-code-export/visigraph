/**
 * Graph.java
 */
package edu.belmont.mth.visigraph.models;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.Map.*;
import edu.belmont.mth.visigraph.settings.GlobalSettings;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.views.ObserverBase;

/**
 * @author Cameron Behar
 * 
 */
public class Graph extends ObservableBase
{
	public final Property<String>        name;
	public final ObservableList<Vertex>  vertexes;
	public final ObservableList<Edge>    edges;
	public final ObservableList<Caption> captions;
	public final boolean				 areLoopsAllowed;
	public final boolean				 areMultipleEdgesAllowed;
	public final boolean				 areDirectedEdgesAllowed;
	public final boolean				 areCyclesAllowed;
	private boolean					 	 notificationsSuspended;
	private ObserverBase				 vertexListObserver;
	private ObserverBase				 edgeListObserver;
	private ObserverBase				 captionListObserver;
	
	public Graph()
	{
		this(GlobalSettings.defaultGraphName, true, true, true, true);
	}
	
	public Graph(String name, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed)
	{
		this.name = new Property<String>(name, "name");
		
		this.areLoopsAllowed         = areLoopsAllowed;
		this.areDirectedEdgesAllowed = areDirectedEdgesAllowed;
		this.areMultipleEdgesAllowed = areMultipleEdgesAllowed;
		this.areCyclesAllowed        = areCyclesAllowed;
		
		this.notificationsSuspended = false;
		this.vertexListObserver     = new ObserverBase()
		{
			public void hasChanged(Object source)
			{
				if (edges != null)
					fixEdges();
				if (!notificationsSuspended)
					notifyObservers(source);
			}
		};
		this.edgeListObserver       = new ObserverBase()
		{
			public void hasChanged(Object source)
			{
				if (!notificationsSuspended)
					notifyObservers(source);
			}
		};
		this.captionListObserver    = new ObserverBase()
		{
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
	
	@SuppressWarnings("unchecked")
	public Graph(String json)
	{
		Map<String, Object> members = JsonUtilities.parseObject(json);
		
		this.name = new Property<String>(members.get("name").toString(), "name");
		
		this.areLoopsAllowed         = new Boolean(members.get("areLoopsAllowed").toString());
		this.areDirectedEdgesAllowed = new Boolean(members.get("areDirectedEdgesAllowed").toString());
		this.areMultipleEdgesAllowed = new Boolean(members.get("areMultipleEdgesAllowed").toString());
		this.areCyclesAllowed        = new Boolean(members.get("areCyclesAllowed").toString());
		
		this.notificationsSuspended = false;
		this.vertexListObserver     = new ObserverBase()
		{
			public void hasChanged(Object source)
			{
				if (edges != null)
					fixEdges();
				if (!notificationsSuspended)
					notifyObservers(source);
			}
		};
		this.edgeListObserver       = new ObserverBase()
		{
			public void hasChanged(Object source)
			{
				if (!notificationsSuspended)
					notifyObservers(source);
			}
		};
		this.captionListObserver    = new ObserverBase()
		{
			public void hasChanged(Object source)
			{
				if (!notificationsSuspended)
					notifyObservers(source);
			}
		};
		
		this.vertexes = new ObservableList<Vertex>("vertexes");
		for(Object vertex : (Iterable<?>)members.get("vertexes"))
			if(vertex instanceof Map<?, ?>)
				this.vertexes.add(new Vertex((Map<String, Object>)vertex));
		this.vertexes.addObserver(vertexListObserver);
		
		Map<Integer, Vertex> vertexMap = new HashMap<Integer, Vertex>();
		for(int i = 0; i < this.vertexes.size(); ++i)
			vertexMap.put(this.vertexes.get(i).id.get(), this.vertexes.get(i));
		
		this.edges = new ObservableList<Edge>("edges");
		for(Object edge : (Iterable<?>)members.get("edges"))
			if(edge instanceof Map<?, ?>)
				this.edges.add(new Edge((Map<String, Object>)edge, vertexMap));
		this.edges.addObserver(edgeListObserver);
		
		this.captions = new ObservableList<Caption>("captions");
		for(Object caption : (Iterable<?>)members.get("captions"))
			if(caption instanceof Map<?, ?>)
				this.captions.add(new Caption((Map<String, Object>)caption));
		this.captions.addObserver(captionListObserver);
	}

	public void selectAll()
	{
		for (Edge edge : edges)
			edge.isSelected.set(true);
		
		for (Vertex vertex : vertexes)
			vertex.isSelected.set(true);
		
		for (Caption caption : captions)
			caption.isSelected.set(true);
	}
	
	public void deselectAll()
	{
		for (Edge edge : edges)
			edge.isSelected.set(false);
		
		for (Vertex vertex : vertexes)
			vertex.isSelected.set(false);
		
		for (Caption caption : captions)
			caption.isSelected.set(false);
	}
	
	public void removeSelected()
	{
		// Delete all selected vertexes
		int i = 0;
		while(i < vertexes.size())
		{
			if(vertexes.get(i).isSelected.get())
				vertexes.remove(i);
			else
				++i;
		}
		
		// Delete all selected edges
		i = 0;
		while(i < edges.size())
		{
			if(edges.get(i).isSelected.get())
				edges.remove(i);
			else
				++i;
		}
		
		// Delete all selected captions
		i = 0;
		while(i < captions.size())
		{
			if(captions.get(i).isSelected.get())
				captions.remove(i);
			else
				++i;
		}
	}
	
	public void moveSelected(double x, double y)
	{
		HashMap<Edge, Point2D> edgeHandles = new HashMap<Edge, Point2D>();
		
		// We have to split setting the edge handles into two parts because when we move its vertices, the handle will be reset to the midpoint.
		// If we are also moving adjacent vertices, we'll just let the handles of now linear edges be automatically reset (to maintain linearity).
		for(Edge edge : edges)
			if(edge.isSelected.get() && (!(edge.from.isSelected.get() || edge.to.isSelected.get()) || !edge.isLinear()))
				edgeHandles.put(edge, new Point2D.Double(edge.handleX.get() + x, edge.handleY.get() + y));
		
		// Moves the vertices (resetting their edges to simple lines)
		for(Vertex vertex : vertexes)
			if(vertex.isSelected.get())
			{
				vertex.x.set(vertex.x.get() + x);
				vertex.y.set(vertex.y.get() + y);
				//vertex.x.set(Math.round((vertex.x.get() + x) / GlobalSettings.snapGridSize) * GlobalSettings.snapGridSize);
				//vertex.y.set(Math.round((vertex.y.get() + y) / GlobalSettings.snapGridSize) * GlobalSettings.snapGridSize);
			}

		// Now move the handles to where they should go
		for(Entry<Edge, Point2D> entry : edgeHandles.entrySet())
		{
			entry.getKey().handleX.set(entry.getValue().getX());
			entry.getKey().handleY.set(entry.getValue().getY());
			//entry.getKey().handleX.set(Math.round(entry.getValue().getX() / GlobalSettings.snapGridSize) * GlobalSettings.snapGridSize);
			//entry.getKey().handleY.set(Math.round(entry.getValue().getY() / GlobalSettings.snapGridSize) * GlobalSettings.snapGridSize);
		}
		
		// Move the captions
		for(Caption caption : captions)
			if(caption.isSelected.get())
			{
				caption.x.set(caption.x.get() + x);
				caption.y.set(caption.y.get() + y);
				//caption.x.set(Math.round((caption.x.get() + x) / GlobalSettings.snapGridSize) * GlobalSettings.snapGridSize);
				//caption.y.set(Math.round((caption.y.get() + y) / GlobalSettings.snapGridSize) * GlobalSettings.snapGridSize);
			}
	}
	
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
			notifyObservers("edges\tremove\t" + originalSize + "\t" + edges.size());
	}
	
	public Vector<Point2D> getCrossings()
	{
		Vector<Point2D> ret = new Vector<Point2D>();
		
		for (int i = 0; i < edges.size(); ++i)
			for (int j = i + 1; j < edges.size(); ++j)
				ret.addAll(edges.get(i).getCrossings(edges.get(j)));
		
		return ret;
	}
	
	public Set<Edge> getEdges(Vertex v)
	{
		HashSet<Edge> edgeSet = new HashSet<Edge>();
		
		for (Edge edge : edges)
			if (edge.from == v || (edge.to == v && !edge.isDirected))
				edgeSet.add(edge);
		
		return edgeSet;
	}
	
	public Set<Edge> getEdges(Vertex from, Vertex to)
	{
		HashSet<Edge> edgeSet = new HashSet<Edge>();
		
		for (Edge edge : edges)
			if ((edge.from == from && edge.to == to) || (edge.from == to && edge.to == from && !edge.isDirected))
				edgeSet.add(edge);
		
		return edgeSet;
	}
	
	public Set<Vertex> getNeighbors(Vertex v)
	{
		HashSet<Vertex> ret = new HashSet<Vertex>();
		
		for(Edge edge : getEdges(v))
			ret.add((edge.from == v) ? edge.to : edge.from);
		
		return ret;
	}
	
	public boolean areConnected(Vertex from, Vertex to)
	{
		Set<Vertex> visited = new HashSet<Vertex>();
		Stack<Vertex> toVisit = new Stack<Vertex>();
		
		toVisit.push(to);
		
		while(!toVisit.isEmpty())
		{
			Vertex vertex = toVisit.pop();
			if(vertex == from) return true;
			visited.add(vertex);
			
			Set<Vertex> neighbors = getNeighbors(vertex);
			
			for(Vertex neighbor : neighbors)
				if(!visited.contains(neighbor))
					toVisit.push(neighbor);
		}
		
		return false;
	}
	
	public int nextVertexId()
	{
		int maxInt = -1;
		
		for(Vertex vertex : vertexes)
			if(vertex.id.get() > maxInt)
				maxInt = vertex.id.get();
		
		return maxInt + 1;
	}
	
	public boolean suspendNotifications(boolean s)
	{
		boolean ret = notificationsSuspended;
		notificationsSuspended = s;
		return ret;
	}
	
	public String toString()
	{
		HashMap<String, Object> members = new HashMap<String, Object>();
		
		members.put("name",                    name                   );
		members.put("vertexes",                vertexes               );
		members.put("edges",                   edges                  );
		members.put("captions",                captions               );
		members.put("areLoopsAllowed",         areLoopsAllowed        );
		members.put("areDirectedEdgesAllowed", areDirectedEdgesAllowed);
		members.put("areMultipleEdgesAllowed", areMultipleEdgesAllowed);
		members.put("areCyclesAllowed",        areCyclesAllowed       );
		
		return JsonUtilities.formatObject(members);
	}

	public void union(Graph graph)
	{
		suspendNotifications(true);
		
		Map<Integer, Vertex> newVertexes = new HashMap<Integer, Vertex>();
		
		for(Vertex vertex : graph.vertexes)
		{
			Vertex newVertex = new Vertex(vertex.toString());
			newVertexes.put(newVertex.id.get(), newVertex);
			newVertex.id.set(this.nextVertexId());
			this.vertexes.add(newVertex);
		}
		
		for(Edge edge : graph.edges)
		{
			Edge newEdge = new Edge(edge.toString(), newVertexes);
			this.edges.add(newEdge);
		}
		
		for(Caption caption : graph.captions)
		{
			Caption newCaption = new Caption(caption.toString());
			this.captions.add(newCaption);
		}
		
		suspendNotifications(false);
		
		notifyObservers(this);
	}
}













