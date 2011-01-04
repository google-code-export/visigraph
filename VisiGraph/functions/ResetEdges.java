import java.awt.*;
import java.util.List;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		List selectedEdges = g.getSelectedEdges( );
		if( selectedEdges.isEmpty( ) )
			selectedEdges = g.edges;
		
		g.suspendNotifications( true );
		
		for( Edge edge : selectedEdges )
			edge.reset( );
		
		g.suspendNotifications( false );
		
		return null;
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
				return "Resets the handle of each edge in a given graph to its default position.";
			case Function.Attribute.INPUT:
				return "The selected edges; if none are selected, the entire edge set will be used.";
			case Function.Attribute.SIDE_EFFECTS:
				return "Each selected edge's handle is relocated to its default position.  For simple edges, the handle is moved to the midpoint of the edge's two vertices.  For loops, the handle is moved according to the user-defined \"Loop diameter\" setting.";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return false;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Reverse edges" };
			case Function.Attribute.TAGS:
				return new String[ ] { "Graph modifier" };
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
		return "Reset edges";
	}
	
return (Function) this;
