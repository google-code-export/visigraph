import java.awt.*;
import java.util.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		if( g.vertices.isEmpty( ) )
			return Boolean.FALSE.toString( );
		
		if( g.areLoopsAllowed )
			for( Edge edge : g.edges )
				if( edge.isLoop )
					return Boolean.FALSE.toString( );
		
		for( Vertex vertex : g.vertices )
			if( g.getEdges( vertex ).size( ) != 5 || g.getNeighbors( vertex ).size( ) != 5 )
				return Boolean.FALSE.toString( );
		
		return Boolean.TRUE.toString( );
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
				return "Returns a Boolean value indicating whether or not a given graph is quintic.  A graph is said to be <i>quintic</i> iff it is 5-regular.";
			case Function.Attribute.OUTPUT:
				return "<code>True</code> if the graph is quintic, <code>false</code> otherwise.";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return true;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.TAGS:
				return new String[ ] { "Graph evaluator", "Network flow", "Symmetry" };
			default:
				return null;
		}
	}
	
	public boolean isApplicable( boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed )
	{
		return !areDirectedEdgesAllowed;
	}
	
	public String toString( )
	{
		return "Is quintic";
	}
	
return (Function) this;