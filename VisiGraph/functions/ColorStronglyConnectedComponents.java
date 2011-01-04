import java.awt.*;
import java.util.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		Collection components = GraphUtilities.findStronglyConnectedComponents( g );
		
		g.suspendNotifications( true );
		
		int color = 0;
		for( Collection component : components )
		{
			for( Vertex vertex : component )
				vertex.color.set( color );
			++color;
		}
		
		for( Edge edge : g.edges )
			edge.color.set( edge.from.color.get( ) == edge.to.color.get( ) ? edge.from.color.get( ) : -1 );
		
		g.suspendNotifications( false );
		
		return Integer.toString( components.size( ) );
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
				return "Colors the vertices and edges of a given graph according to their membership to strongly connected components and returns the total number of strongly connected components found.  Vertices in a directed graph are said to be members of the same <i>strongly connected component</i> iff there exists a path from one to the other and vice-versa.</p><p>" + GlobalSettings.applicationName + "'s built-in implementation of Tarjan's strongly connected components algorithm is used to perform the test in <code><i>O</i>(|<i>V</i>| + |<i>E</i>|)</code> time.";
			case Function.Attribute.SIDE_EFFECTS:
				return "The color of each vertex and edge will be set to the color incrementally-chosen for its strongly connected component.  If an edge does not belong to a strongly connected component, its color will be set to <code>uncolored</code> (i.e. <code>-1</code>)";
			case Function.Attribute.OUTPUT:
				return "The total number of strongly connected components found.";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return false;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_GENERATORS:
				return new String[ ] { "Condensation of (another graph)" };
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Color connected components", "Color weakly connected components", "Connect completely", "Count connected components", "Count strongly connected components", "Count weakly connected components" };
			case Function.Attribute.TAGS:
				return new String[ ] { "Connectivity", "Graph evaluator", "Graph modifier", "Substructures" };
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
		return "Color strongly connected components";
	}
	
return (Function) this;
