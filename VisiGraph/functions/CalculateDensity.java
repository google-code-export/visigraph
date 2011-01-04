import java.awt.*;
import java.util.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		return ( g.vertices.size( ) < 2 ? "n/a" : String.format( "%.5f", new Object[ ] { ( ( g.areDirectedEdgesAllowed ? 1 : 2 ) * g.edges.size( ) ) / (double) ( g.vertices.size( ) * ( g.vertices.size( ) - 1 ) ) } ) );
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
				return "Returns the density of a given graph.  Per the maximum number of edges for a simple graph (<code>\u00BD|<i>V</i>|(|<i>V</i>| - 1)</code>), a graph's <i>density</i> is defined as <code><i>D</i> = 2|<i>E</i>|/(|<i>V</i>|(|<i>V</i>| - 1))</code>.";
			case Function.Attribute.OUTPUT:
				return "The density of the graph, rounded to five decimal places.  <i>Note: multigraphs and pseudographs are counted using the same formula (allowing a density > 1); directed graphs are counted as having half-edges.</i>";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return true;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_GENERATORS:
				return new String[ ] { "Random graph (Gilbert)" };
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Count edges", "Count vertices" };
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
		return "Calculate density";
	}
	
return (Function) this;
