import java.awt.*;
import java.util.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		if( g.edges.isEmpty( ) )
			return Boolean.FALSE.toString( );
		
		if( !g.areCyclesAllowed )
			return Boolean.FALSE.toString( );
		
		if( g.areLoopsAllowed )
			for( Edge edge : g.edges )
				if( edge.isLoop )
					return Boolean.TRUE.toString( );
		
		if( g.areDirectedEdgesAllowed )
		{
			for( Collection component : GraphUtilities.findStronglyConnectedComponents( g ) )
				if( component.size( ) > 1 )
					return Boolean.TRUE.toString( );
		}
		else
		{
			LinkedList visiting = new LinkedList( );
			Set unvisitedVertices = new HashSet( g.vertices );
			Set visitedEdges = new HashSet( );
			
			while( !unvisitedVertices.isEmpty( ) )
			{
				visiting.addLast( unvisitedVertices.iterator( ).next( ) );
				
				while( !visiting.isEmpty( ) )
				{
					Vertex vertex = visiting.removeFirst( );
					if( !unvisitedVertices.contains( vertex ) )
						return Boolean.TRUE.toString( );
					unvisitedVertices.remove( vertex );
					
					for( Edge edge : g.getEdges( vertex ) )
						if( !visitedEdges.contains( edge ) )
						{
							visitedEdges.add( edge );
							visiting.addLast( edge.from == vertex ? edge.to : edge.from );
						}
				}
			}
		}
		
		return Boolean.FALSE.toString( );
	}
	
	public Object getAttribute( Function.Attribute attribute )
	{
		switch( attribute )
		{
			case Function.Attribute.AUTHOR:
				return "Cameron Behar";
			case Function.Attribute.VERSION:
				return "20110101";
			case Function.Attribute.DESCRIPTION:
				return "Returns a Boolean value indicating whether or not a given graph contains at least one cycle.</p><p>For undirected graphs, a simple breadth-first search is used to detect cycles in <code><i>O</i>(|<i>V</i>| + |<i>E</i>|)</code> time.  For directed graphs, " + GlobalSettings.applicationName + "'s built-in implementation of Tarjan's strongly connected components algorithm is used to perform the test in <code><i>O</i>(|<i>V</i>| + |<i>E</i>|)</code> time.";
			case Function.Attribute.OUTPUT:
				return "<code>True</code> if the graph contains cycles, <code>false</code> otherwise.";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return true;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_GENERATORS:
				return new String[ ] { "Color strongly connected components", "Count strongly connected components", "Cycle graph" };
			case Function.Attribute.TAGS:
				return new String[ ] { "Connectivity", "Graph evaluator", "Network flow", "Substructures" };
			default:
				return null;
		}
	}
	
	public boolean isApplicable( boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed )
	{
		return true;
	}
	
	public String toString( )
	{
		return "Is cyclic";
	}
	
return (Function) this;
