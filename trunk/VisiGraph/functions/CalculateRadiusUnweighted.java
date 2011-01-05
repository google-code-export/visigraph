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
		
		double[ ][ ] distances = GraphUtilities.getDistanceMatrix( g, false );
		
		double minEccentricity = Double.POSITIVE_INFINITY;
		for( int i = 0; i < g.vertices.size( ); ++i )
		{
			double eccentricity = Double.NEGATIVE_INFINITY;
			
			for( int j = 0; j < g.vertices.size( ); ++j )
				eccentricity = Math.max( eccentricity, distances[i][j] );
			
			minEccentricity = Math.min( minEccentricity, eccentricity );
		}
		
		for( int i = 0; i < g.vertices.size( ); ++i )
		{
			double eccentricity = Double.NEGATIVE_INFINITY;
			
			for( int j = 0; j < g.vertices.size( ); ++j )
				eccentricity = Math.max( eccentricity, distances[j][i] );
			
			minEccentricity = Math.min( minEccentricity, eccentricity );
		}
		
		return ( minEccentricity == Double.POSITIVE_INFINITY ? "\u221E" : Long.toString( Math.round( minEccentricity ) ) );
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
				return "Returns the radius of a given graph without respect to the edges' weights.  The <i>radius</i> of a graph is the minimum eccentricity of any vertex in the graph.</p><p>Before attempting to find the radius, a quicker connectivity test is run to determine whether it is first finite.  For undirected graphs, " + GlobalSettings.applicationName + "'s built-in implementation of a union-find algorithm is used with union-by-rank and path-flattening optimizations to perform the test in <code><i>O</i>(|<i>V</i>|\u03B1(|<i>E</i>|))</code> time where <code>\u03B1(<i>n</i>)</code> is the inverse Ackermann function.  For directed graphs, " + GlobalSettings.applicationName + "'s built-in implementation of Tarjan's strongly connected components algorithm is used to perform the test in <code><i>O</i>(|<i>V</i>| + |<i>E</i>|)</code> time.  If multiple (strongly) connected components are found, the graph's radius is infinite and the function returns \u221E.</p><p>If, however the graph's radius is found to be finite, " + GlobalSettings.applicationName + "'s built-in implementation of the Roy-Floyd-Warshall algorithm is used to efficiently compute the geodesic distance between each pair of vertices in <code><i>O</i>(|<i>V</i>|<sup>3</sup>)</code> time.  The minimimum eccentricity is then found in <code><i>O</i>(|<i>V</i>|<sup>2</sup>)</code> time and returned.";
			case Function.Attribute.OUTPUT:
				return "The radius of the graph (using geodesic distances).  <i>Note: for graphs with more than one (strongly) connected component this function always returns \u221E.</i>";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return false;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Calculate diameter (unweighted)", "Calculate diameter (weighted)", "Calculate eccentricity (unweighted)", "Calculate eccentricity (weighted)", "Calculate radius (weighted)", "Count connected components", "Count strongly connected componenets" };
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
		return "Calculate radius (unweighted)";
	}
	
return (Function) this;
