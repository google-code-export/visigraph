import java.awt.*;
import java.util.*;
import java.util.List;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	class Leaf
	{
		Vertex	vertex;
		int		distance;
		
		public Leaf( Vertex vertex, int distance )
		{
			this.vertex = vertex;
			this.distance = distance;
		}
	}
	
	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		List selectedVertices = g.getSelectedVertices( );
		if( selectedVertices.isEmpty( ) )
			return "No vertices selected!";
		else if( selectedVertices.size( ) > 1 )
			return "More than one vertex selected!";
		
		List leaves = new LinkedList( );
		Map vertexDistances = new HashMap( );
		
		for( Vertex vertex : g.vertices )
			vertexDistances.put( vertex, Integer.MAX_VALUE );
		
		leaves.add( new Leaf( selectedVertices.get( 0 ), 0 ) );
		
		while( !leaves.isEmpty( ) )
		{
			Leaf leaf = (Leaf) leaves.pop( );
			
			if( leaf.distance < vertexDistances.get( leaf.vertex ) )
			{
				vertexDistances.put( leaf.vertex, leaf.distance );
				
				for( Vertex neighbor : g.getNeighbors( leaf.vertex ) )
					leaves.add( new Leaf( neighbor, leaf.distance + 1 ) );
			}
		}
		
		int maxDistance = 0;
		for( Integer distance : vertexDistances.values( ) )
			if( distance > maxDistance )
				maxDistance = distance;
		
		return ( maxDistance == Integer.MAX_VALUE ? "\u221E" : Integer.toString( maxDistance ) );
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
				return "Returns the eccentricity of the selected vertex without respect to edge weights.  The <i>eccentricity</i> of a vertex is the longest shortest-path from it to any other vertex in the graph.</p><p>Using Dijkstra's algorithm, the specified vertex's eccentricity is found in <code><i>O</i>((|<i>V</i>| + |<i>E</i>|) <i>log</i> |<i>V</i>|)</code> time.";
			case Function.Attribute.INPUT:
				return "The selected vertex.";
			case Function.Attribute.OUTPUT:
				return "The eccentricity of the specified vertex (using geodesic distances).  <i>Note: for graphs with more than one (strongly) connected component this function always returns \u221E.</i>";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return false;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Calculate diameter (unweighted)", "Calculate diameter (weighted)", "Calculate eccentricity (weighted)", "Calculate radius (unweighted)", "Calculate radius (weighted)" };
			case Function.Attribute.TAGS:
				return new String[ ] { "Connectivity", "Graph evaluator", "Network flow", "Path finding" };
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
		return "Calculate eccentricity (unweighted)";
	}
	
return (Function) this;
