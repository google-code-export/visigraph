import java.awt.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		int selectedEdgeCount = g.getSelectedEdges( ).size( );
		return Integer.toString( selectedEdgeCount > 0 ? selectedEdgeCount : g.edges.size( ) );
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
				return "Returns the number of edges selected or—if none are selected—the total number of edges in the graph (i.e. the graph's <i>size</i>).";
			case Function.Attribute.INPUT:
				return "The selected edges to count; if none are selected, the entire edge set will be used.";
			case Function.Attribute.OUTPUT:
				return "The number of selected edges.";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return true;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_GENERATORS:
				return new String[ ] { "Random graph (Erd\u0151s–R\u00E9nyi)" };
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Calculate density", "Count vertices" };
			case Function.Attribute.TAGS:
				return new String[ ] { "Graph evaluator" };
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
		return "Count edges";
	}
	
return (Function) this;
