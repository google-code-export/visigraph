import java.awt.*;
import java.util.*;
import java.util.List;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		List selectedVertices = g.getSelectedVertices( );
		if( selectedVertices.isEmpty( ) )
			selectedVertices = g.vertices;
		
		Map vertexDegrees = new HashMap( );
		for( Vertex vertex : selectedVertices )
			vertexDegrees.put( vertex, 0 );
		
		for( Edge edge : g.edges )
		{
			if( !edge.isDirected && !edge.isLoop )
			{
				Integer outDegrees = vertexDegrees.get( edge.from );
				if( outDegrees != null )
					vertexDegrees.put( edge.from, outDegrees + 1 );
			}
			
			Integer inDegrees = vertexDegrees.get( edge.to );
			if( inDegrees != null )
				vertexDegrees.put( edge.to, inDegrees + 1 );
		}
		
		int minDegrees = Integer.MAX_VALUE;
		for( Integer degrees : vertexDegrees.values( ) )
			if( degrees < minDegrees )
				minDegrees = degrees;
		
		return Integer.toString( minDegrees );
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
				return "Returns the minimum indegree of the selected vertices in the graph.  The <i>indegree</i> of a vertex is the number of head endpoints in a directed graph incident to that vertex.";
			case Function.Attribute.INPUT:
				return "The selected vertices; if none are selected, the entire vertex set will be used.";
			case Function.Attribute.OUTPUT:
				return "The minimum indegree of the selected vertices.";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return true;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Get average degree", "Get average indegree", "Get average outdegree", "Get maximum degree", "Get maximum indegree", "Get maximum outdegree", "Get minimum degree", "Get minimum outdegree" };
			case Function.Attribute.TAGS:
				return new String[ ] { "Connectivity", "Graph evaluator", "Network flow" };
			default:
				return null;
		}
	}
	
	public boolean isApplicable( boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed )
	{
		return areDirectedEdgesAllowed;
	}
	
	public String toString( )
	{
		return "Get minimum indegree";
	}
	
return (Function) this;
