import java.awt.*;
import java.util.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		return Integer.toString( GraphUtilities.findWeaklyConnectedComponents( g ).size( ) );
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
				return "Returns the total number of weakly connected components found in a given graph.  Vertices in a directed graph are said to be members of the same <i>weakly connected component</i> iff, ignoring edge directions, there exists a path from one to the other.</p><p>" + GlobalSettings.applicationName + "'s built-in implementation of a union-find algorithm is used with union-by-rank and path-flattening optimizations to perform the test in <code><i>O</i>(|<i>V</i>|\u03B1(|<i>E</i>|))</code> time where <code>\u03B1(<i>n</i>)</code> is the inverse Ackermann function.";
			case Function.Attribute.OUTPUT:
				return "The total number of weakly connected components found.";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return true;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_GENERATORS:
				return new String[ ] { "Condensation of (another graph)" };
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Color connected components", "Color strongly connected components", "Color weakly connected components", "Connect completely", "Count connected components", "Count strongly connected components" };
			case Function.Attribute.TAGS:
				return new String[ ] { "Connectivity", "Graph evaluator", "Substructures" };
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
		return "Count weakly connected components";
	}
	
return (Function) this;
