import java.awt.*;
import java.util.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		if( g.vertices.isEmpty( ) )
			return "0";
		
		if( GraphUtilities.findStronglyConnectedComponents( g ).size( ) > 1 )
			return "\u221E";
		
		double[ ][ ] distances = new double[g.vertices.size( )][g.vertices.size( )];
		
		for( int i = 0; i < g.vertices.size( ); ++i )
			for( int j = 0; j < g.vertices.size( ); ++j )
				distances[i][j] = ( i == j ? 0.0 : Double.POSITIVE_INFINITY );
		
		Map indices = new HashMap( );
		for( int i = 0; i < g.vertices.size( ); ++i )
			indices.put( g.vertices.get( i ), i );
		
		for( Edge edge : g.edges )
			if( !edge.isLoop )
			{
				int from = indices.get( edge.from ), to = indices.get( edge.to );
				
				distances[from][to] = 1.0;
				
				if( !edge.isDirected )
					distances[to][from] = 1.0;
			}
		
		for( int k = 0; k < g.vertices.size( ); ++k )
			for( int i = 0; i < g.vertices.size( ); ++i )
				for( int j = 0; j < g.vertices.size( ); ++j )
					distances[i][j] = Math.min( distances[i][j], distances[i][k] + distances[k][j] );
		
		double maxEccentricity = Double.NEGATIVE_INFINITY;
		for( int i = 0; i < g.vertices.size( ); ++i )
			for( int j = 0; j < g.vertices.size( ); ++j )
				maxEccentricity = Math.max( maxEccentricity, distances[i][j] );
		
		return ( maxEccentricity == Double.POSITIVE_INFINITY ? "\u221E" : Long.toString( Math.round( maxEccentricity ) ) );
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
				return "Returns the diameter of a given graph without respect to the edges' weights.  The <i>diameter</i> of a graph is the maximum eccentricity of any vertex in the graph (i.e. the longest shortest-path between any pair of vertices).</p><p>Before attempting to find the diameter, a quicker connectivity test is run to determine whether it is first finite.  For undirected graphs, " + GlobalSettings.applicationName + "'s built-in implementation of a union-find algorithm is used with union-by-rank and path-flattening optimizations to perform the test in <code><i>O</i>(|<i>V</i>|\u03B1(|<i>E</i>|))</code> time where <code>\u03B1(<i>n</i>)</code> is the inverse Ackermann function.  For directed graphs, " + GlobalSettings.applicationName + "'s built-in implementation of Tarjan's strongly connected components algorithm is used to perform the test in <code><i>O</i>(|<i>V</i>| + |<i>E</i>|)</code> time.  If multiple (strongly) connected components are found, the graph's diameter is infinite and the function returns \u221E.</p><p>If, however the graph's diameter is found to be finite, an implementation of the Roy-Floyd-Warshall algorithm is used to efficiently compute the geodesic distance between each pair of vertices in <code><i>O</i>(|<i>V</i>|<sup>3</sup>)</code> time.  The longest of these geodesics is found in <code><i>O</i>(|<i>V</i>|<sup>2</sup>)</code> time and returned.";
			case Function.Attribute.OUTPUT:
				return "The diameter of the graph (using geodesic distances).  <i>Note: for graphs with more than one (strongly) connected component this function always returns \u221E.</i>";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return false;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Calculate diameter (weighted)", "Calculate eccentricity (unweighted)", "Calculate eccentricity (weighted)", "Calculate radius (unweighted)", "Calculate radius (weighted)", "Count connected components", "Count strongly connected componenets" };
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
		return "Calculate diameter (unweighted)";
	}
	
return (Function) this;
