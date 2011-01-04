import java.awt.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		int selectedVertexCount = g.getSelectedVertices( ).size( );
		return Integer.toString( selectedVertexCount > 0 ? selectedVertexCount : g.vertices.size( ) );
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
				return "Returns the number of vertices selected or—if none are selected—the total number of vertices in the graph (i.e. the graph's <i>order</i>).";
			case Function.Attribute.INPUT:
				return "The selected vertices to count; if none are selected, the entire vertex set will be used.";
			case Function.Attribute.OUTPUT:
				return "The number of selected vertices.";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return true;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_GENERATORS:
				return new String[ ] { "Random graph (Erd\u0151s–R\u00E9nyi)" };
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Calculate density", "Count edges" };
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
		return "Count vertices";
	}
	
return (Function) this;
