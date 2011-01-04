import java.awt.*;
import java.util.*;
import java.util.List;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		List selectedEdges = g.getSelectedEdges( );
		if( selectedEdges.isEmpty( ) )
			selectedEdges = g.edges;
		
		g.suspendNotifications( true );
		
		for( Edge edge : selectedEdges )
			edge.weight.set( ( Math.abs( edge.from.x.get( ) - edge.to.x.get( ) ) + Math.abs( edge.from.y.get( ) - edge.to.y.get( ) ) ) / 10.0 );
		
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
				return "Sets each selected edge's weight to a constant fraction <i>k</i> of its rectilinear length.  Rectilinear length is computed using Manhattan distance, which is given by <code>|\u0394x| + |\u0394y|</code>.";
			case Function.Attribute.INPUT:
				return "The selected edges; if none are selected, the entire edge set will be used.";
			case Function.Attribute.SIDE_EFFECTS:
				return "The weight of each selected edge will be set to 1/10th that edge's rectilinear length (ignoring its linearity).  <i>Note: for loops, this measure will always be zero.</i>";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return false;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Assign edge weights by Euclidean length", "Assign edge weights randomly" };
			case Function.Attribute.TAGS:
				return new String[ ] { "Graph modifier", "Network flow", "Path finding" };
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
		return "Assign edge weights by rectilinear length";
	}
	
return (Function) this;
