import java.awt.*;
import java.util.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		if( g.vertices.isEmpty( ) )
			return "n/a";
		
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
						return Boolean.FALSE.toString( );
					
					if( setA.add( leaf ) )
						for( Edge edge : g.getEdges( leaf ) )
							if( edge.isLoop )
								return Boolean.FALSE.toString( );
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
				return "Returns a Boolean value indicating whether or not a given graph is bipartite (i.e. 2-chromatic).  A graph is said to be <i>bipartite</i> if its vertices can be divided into two disjoint sets such that no two vertices in the same set are neighbors.";
			case Function.Attribute.OUTPUT:
				return "<code>True</code> if the graph is bipartite, <code>false</code> otherwise.";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return true;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_GENERATORS:
				return new String[ ] { "Complete bipartite graph", "Complete k-partite graph", "Complete tripartite graph", "Crown graph" };
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Two-color vertices" };
			case Function.Attribute.TAGS:
				return new String[ ] { "Colorings", "Coverings", "Graph evaluator", "Substructures" };
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
		return "Is bipartite";
	}
	
return (Function) this;
