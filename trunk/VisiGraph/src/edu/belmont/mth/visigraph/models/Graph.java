/**
 * Graph.java
 */
package edu.belmont.mth.visigraph.models;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;
import edu.belmont.mth.visigraph.settings.GlobalSettings;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.views.Observer;

/**
 * @author Cameron Behar
 * 
 */
public class Graph extends Observable
{
	public final Property<String>        name;
	public final ObservableList<Vertex>  vertexes;
	public final ObservableList<Edge>    edges;
	public final ObservableList<Caption> captions;
	public final boolean				 allowLoops;
	public final boolean				 allowMultipleEdges;
	public final boolean				 allowDirectedEdges;
	public final boolean				 allowCycles;
	protected boolean					 notificationsSuspended;
	protected Observer					 vertexListObserver;
	protected Observer					 edgeListObserver;
	protected Observer					 captionListObserver;
	
	public Graph()
	{
		this(GlobalSettings.defaultGraphName, true, true, true, true);
	}
	
	public Graph(String name, boolean allowLoops, boolean allowDirectedEdges, boolean allowMultipleEdges, boolean allowCycles)
	{
		this.name = new Property<String>(name, "name");
		
		this.allowLoops         = allowLoops;
		this.allowDirectedEdges = allowDirectedEdges;
		this.allowMultipleEdges = allowMultipleEdges;
		this.allowCycles        = allowCycles;
		
		this.notificationsSuspended = false;
		this.vertexListObserver     = new Observer()
		{
			public void hasChanged(Object source)
			{
				if (edges != null)
					fixEdges();
				if (!notificationsSuspended)
					notifyObservers(source);
			}
		};
		this.edgeListObserver       = new Observer()
		{
			public void hasChanged(Object source)
			{
				if (!notificationsSuspended)
					notifyObservers(source);
			}
		};
		this.captionListObserver    = new Observer()
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
		
		this.allowLoops         = new Boolean(members.get("allowLoops").toString());
		this.allowDirectedEdges = new Boolean(members.get("allowDirectedEdges").toString());
		this.allowMultipleEdges = new Boolean(members.get("allowMultipleEdges").toString());
		this.allowCycles        = new Boolean(members.get("allowCycles").toString());
		
		this.notificationsSuspended = false;
		this.vertexListObserver     = new Observer()
		{
			public void hasChanged(Object source)
			{
				if (edges != null)
					fixEdges();
				if (!notificationsSuspended)
					notifyObservers(source);
			}
		};
		this.edgeListObserver       = new Observer()
		{
			public void hasChanged(Object source)
			{
				if (!notificationsSuspended)
					notifyObservers(source);
			}
		};
		this.captionListObserver    = new Observer()
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
			vertexMap.put(this.vertexes.get(i).id, this.vertexes.get(i));
		
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
	
	protected void fixEdges()
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
	
	public int nextEdgeId()
	{
		return edges.size();
	}
	
	public int nextVertexId()
	{
		return vertexes.size();
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
		
		members.put("name",               name              );
		members.put("vertexes",           vertexes          );
		members.put("edges",              edges             );
		members.put("captions",           captions          );
		members.put("allowLoops",         allowLoops        );
		members.put("allowMultipleEdges", allowMultipleEdges);
		members.put("allowDirectedEdges", allowDirectedEdges);
		members.put("allowCycles",        allowCycles       );
		
		return JsonUtilities.formatObject(members);
	}

	public void union(Graph graph)
	{
		suspendNotifications(true);
		
		Map<Vertex, Vertex> newVertexes = new HashMap<Vertex, Vertex>();
		
		for(Vertex vertex : graph.vertexes)
		{
			Vertex newVertex = new Vertex(this.nextVertexId());
			newVertex.x.set(vertex.x.get());
			newVertex.y.set(vertex.y.get());
			newVertex.label.set(vertex.label.get());
			newVertex.color.set(vertex.color.get());
			newVertex.weight.set(vertex.weight.get());
			newVertex.radius.set(vertex.radius.get());
			newVertex.isSelected.set(vertex.isSelected.get());
			this.vertexes.add(newVertex);
			newVertexes.put(vertex, newVertex);
		}
		
		for(Edge edge : graph.edges)
		{
			Edge newEdge = new Edge(this.nextEdgeId(), edge.isDirected && this.allowDirectedEdges, newVertexes.get(edge.from), newVertexes.get(edge.to));
			newEdge.handleX.set(edge.handleX.get());
			newEdge.handleY.set(edge.handleY.get());
			newEdge.label.set(edge.label.get());
			newEdge.color.set(edge.color.get());
			newEdge.weight.set(edge.weight.get());
			newEdge.handleRadius.set(edge.handleRadius.get());
			newEdge.isSelected.set(edge.isSelected.get());
			newEdge.thickness.set(newEdge.thickness.get());
			this.edges.add(newEdge);
		}
		
		for(Caption caption : graph.captions)
		{
			Caption newCaption = new Caption(caption.x.get(), caption.y.get());
			newCaption.text.set(caption.text.get());
			newCaption.isSelected.set(caption.isSelected.get());
			this.captions.add(newCaption);
		}
		
		suspendNotifications(false);
		
		notifyObservers(this);
	}
}


