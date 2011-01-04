import java.awt.*;
import java.util.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		if( g.vertices.isEmpty( ) )
			return null;
		
		// Reset vertex colors to uncolored (-1)
		g.suspendNotifications( true );
		
		for( Vertex vertex : g.vertices )
			vertex.color.set( -1 );
		
		g.suspendNotifications( false );
		
		Set setA = new HashSet( ), setB = new HashSet( );
		LinkedList queueA = new LinkedList( ), queueB = new LinkedList( );
		
		while( setA.size( ) + setB.size( ) < g.vertices.size( ) )
		{
			for( Vertex vertex : g.vertices )
				if( !setA.contains( vertex ) && !setB.contains( vertex ) )
				{
					queueA.addLast( vertex );
					break;
				}
			
			while( !queueA.isEmpty( ) )
			{
				while( !queueA.isEmpty( ) )
				{
					Vertex leaf = queueA.pop( );
					
					if( setB.contains( leaf ) )
						return "Graph is not two-colorable!";
					
					if( setA.add( leaf ) )
						for( Edge edge : g.getEdges( leaf ) )
							if( edge.isLoop )
								return "Graph is not two-colorable!";
							else
								queueB.add( edge.from == leaf ? edge.to : edge.from );
				}
				
				// Swap colors (i.e. sets and queues)
				Set tempSet = setA;
				setA = setB;
				setB = tempSet;
				
				LinkedList tempQueue = queueA;
				queueA = queueB;
				queueB = tempQueue;
			}
		}
		
		// Color the sets
		g.suspendNotifications( true );
		
		for( Vertex vertex : setA )
			vertex.color.set( 1 );
		
		for( Vertex vertex : setB )
			vertex.color.set( 0 );
		
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
				return "Attempts to set the color of every vertex in a given graph to one of two colors such that no two vertices of the same color are adjacent.";
			case Function.Attribute.SIDE_EFFECTS:
				return "Sets the color of each vertex to its appropriate set's chosen color, or <code>uncolored</code> if the graph is not two-colorable.";
			case Function.Attribute.CONSTRAINTS:
				return new String[ ] { "Graph must be bipartite (i.e. 2-chromatic)" };
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return false;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_GENERATORS:
				return new String[ ] { "Complete bipartite graph", "Crown graph" };
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Is bipartite" };
			case Function.Attribute.TAGS:
				return new String[ ] { "Colorings", "Coverings", "Graph evaluator", "Graph modifier", "Substructures" };
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
		return "Two-color vertices";
	}
	
return (Function) this;
