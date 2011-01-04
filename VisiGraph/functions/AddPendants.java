import java.awt.*;
import java.util.*;
import java.util.List;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		List selectedVertices = g.getSelectedVertices( );
		if( selectedVertices.isEmpty( ) )
			if( g.hasSelectedEdges( ) || g.hasSelectedCaptions( ) )
				return null;
			else
				selectedVertices = new ArrayList( g.vertices );
		
		g.suspendNotifications( true );
		
		for( Vertex vertex : selectedVertices )
		{
			Vertex pendant = new Vertex( vertex.x.get( ) + UserSettings.instance.arrangeGridSpacing.get( ) / 2.0, vertex.y.get( ) + UserSettings.instance.arrangeGridSpacing.get( ) / 2.0 );
			g.vertices.add( pendant );
			g.edges.add( new Edge( g.areDirectedEdgesAllowed, vertex, pendant ) );
		}
		
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
				return "Adds a pendant to each selected vertex.  A <i>pendant</i> is a vertex with exactly one neighbor.";
			case Function.Attribute.INPUT:
				return "The selected vertices; if no elements are selected, the entire vertex set will be used.";
			case Function.Attribute.SIDE_EFFECTS:
				return "A pendant will be added to each selected vertex.  In the case of directed graphs, the pendant edge will span from the selected vertex to the pendant vertex.";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return false;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_GENERATORS:
				return new String[ ] { "Centipede tree", "Helm graph", "Pan graph", "Star graph", "Web graph" };
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
		return "Add pendants";
	}
	
return (Function) this;
