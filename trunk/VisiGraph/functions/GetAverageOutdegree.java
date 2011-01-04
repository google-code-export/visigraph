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
			Integer outDegrees = vertexDegrees.get( edge.from );
			if( outDegrees != null )
				vertexDegrees.put( edge.from, outDegrees + 1 );
			
			if( !edge.isDirected && !edge.isLoop )
			{
				Integer inDegrees = vertexDegrees.get( edge.to );
				if( inDegrees != null )
					vertexDegrees.put( edge.to, inDegrees + 1 );
			}
		}
		
		double totalDegrees = 0;
		for( Integer degrees : vertexDegrees.values( ) )
			totalDegrees += degrees;
		
		return String.format( "%.5f", new Object[ ] { totalDegrees / vertexDegrees.size( ) } );
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
				return "Returns the average outdegree of the selected vertices in the graph.  The <i>outdegree</i> of a vertex is the number of tail endpoints in a directed graph incident to that vertex.";
			case Function.Attribute.INPUT:
				return "The selected vertices; if none are selected, the entire vertex set will be used.";
			case Function.Attribute.OUTPUT:
				return "The average outdegree of the selected vertices rounded to five decimal places.";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return true;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Get average degree", "Get average indegree", "Get maximum degree", "Get maximum indegree", "Get maximum outdegree", "Get minimum degree", "Get minimum indegree", "Get minimum outdegree" };
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
		return "Get average outdegree";
	}
	
return (Function) this;
