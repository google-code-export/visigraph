import java.awt.*;
import java.util.*;
import java.util.List;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	class Leaf
	{
		Vertex	vertex;
		double	distance;
		
		public Leaf( Vertex vertex, double distance )
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
			vertexDistances.put( vertex, Double.POSITIVE_INFINITY );
		
		leaves.add( new Leaf( selectedVertices.get( 0 ), 0 ) );
		
		while( !leaves.isEmpty( ) )
		{
			Leaf leaf = (Leaf) leaves.pop( );
			
			if( leaf.distance < vertexDistances.get( leaf.vertex ) )
			{
				vertexDistances.put( leaf.vertex, leaf.distance );
				
				for( Edge edge : g.getEdgesFrom( leaf.vertex ) )
				{
					if( edge.weight.get( ) < 0 )
						return "Graph contains negative edge weights!";
					
					if( !edge.isLoop )
						leaves.add( new Leaf( edge.from == leaf.vertex ? edge.to : edge.from, leaf.distance + edge.weight.get( ) ) );
				}
			}
		}
		
		double maxDistance = 0.0;
		for( Double distance : vertexDistances.values( ) )
			if( distance > maxDistance )
				maxDistance = distance;
		
		return ( maxDistance == Double.POSITIVE_INFINITY ? "\u221E" : String.format( "%.5f", new Object[ ] { maxDistance } ) );
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
				return "Returns the eccentricity of the selected vertex taking each edge's weight into consideration.  The <i>eccentricity</i> of a vertex is the longest shortest-path from it to any other vertex in the graph.</p><p>Using Dijkstra's algorithm, the specified vertex's eccentricity is found in <code><i>O</i>((|<i>V</i>| + |<i>E</i>|) <i>log</i> |<i>V</i>|)</code> time.";
			case Function.Attribute.INPUT:
				return "The selected vertex.";
			case Function.Attribute.OUTPUT:
				return "The eccentricity of the specified vertex rounded to five decimal places.  <i>Note: for graphs with more than one (strongly) connected component this function always returns \u221E.</i>";
			case Function.Attribute.CONSTRAINTS:
				return new String[ ] { "The graph must not contain negative edge weights" };
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return false;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Calculate diameter (unweighted)", "Calculate diameter (weighted)", "Calculate eccentricity (unweighted)", "Calculate radius (unweighted)", "Calculate radius (weighted)" };
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
		return "Calculate eccentricity (weighted)";
	}
	
return (Function) this;
