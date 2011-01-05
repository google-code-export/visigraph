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
		
		double[ ][ ] distances = GraphUtilities.getDistanceMatrix( g, true );
		
		double maxEccentricity = Double.NEGATIVE_INFINITY;
		for( int i = 0; i < g.vertices.size( ); ++i )
			for( int j = 0; j < g.vertices.size( ); ++j )
				if( i == j && distances[i][j] < 0.0 )
					return "Cannot have negative cycles!";
				else
					maxEccentricity = Math.max( maxEccentricity, distances[i][j] );
		
		return ( maxEccentricity == Double.POSITIVE_INFINITY ? "\u221E" : String.format( "%.5f", new Object[ ] { maxEccentricity } ) );
	}
	
	public Object getAttribute( Function.Attribute attribute )
	{
		switch( attribute )
		{
			case Function.Attribute.AUTHOR:
				return "Cameron Behar";
			case Function.Attribute.VERSION:
				return "20110105";
			case Function.Attribute.DESCRIPTION:
				return "Returns the diameter of a given graph taking each edge's weight into consideration.  The <i>diameter</i> of a graph is the maximum eccentricity of any vertex in the graph (i.e. the longest shortest-path between any pair of vertices).</p><p>Before attempting to find the diameter, a quicker connectivity test is run to determine whether it is first finite.  For undirected graphs, " + GlobalSettings.applicationName + "'s built-in implementation of a union-find algorithm is used with union-by-rank and path-flattening optimizations to perform the test in <code><i>O</i>(|<i>V</i>|\u03B1(|<i>E</i>|))</code> time where <code>\u03B1(<i>n</i>)</code> is the inverse Ackermann function.  For directed graphs, " + GlobalSettings.applicationName + "'s built-in implementation of Tarjan's strongly connected components algorithm is used to perform the test in <code><i>O</i>(|<i>V</i>| + |<i>E</i>|)</code> time.  If multiple (strongly) connected components are found, the graph's diameter is infinite and the function returns \u221E.</p><p>If, however the graph's diameter is found to be finite, " + GlobalSettings.applicationName + "'s built-in implementation of the Roy-Floyd-Warshall algorithm is used to efficiently compute the weighted distance between each pair of vertices in <code><i>O</i>(|<i>V</i>|<sup>3</sup>)</code> time.  The longest of these paths is found in <code><i>O</i>(|<i>V</i>|<sup>2</sup>)</code> time and returned.";
			case Function.Attribute.OUTPUT:
				return "The diameter of the graph rounded to five decimal places.  <i>Note: for graphs with more than one (strongly) connected component this function always returns \u221E.</i>";
			case Function.Attribute.CONSTRAINTS:
				return new String[ ] { "The graph must not contain negative cycles" };
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return false;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Calculate diameter (unweighted)", "Calculate eccentricity (unweighted)", "Calculate eccentricity (weighted)", "Calculate radius (unweighted)", "Calculate radius (weighted)", "Count connected components", "Count strongly connected componenets" };
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
		return "Calculate diameter (weighted)";
	}
	
return (Function) this;
