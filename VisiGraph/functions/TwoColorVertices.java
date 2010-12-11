import java.awt.*;
import java.util.*;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;
import edu.belmont.mth.visigraph.utilities.GeometryUtilities;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		if ( g.vertexes.isEmpty( ) )
			return null;
		
		// Reset vertex colors to default (-1)
		for ( Vertex vertex : g.vertexes )
			vertex.color.set( -1 );
		
		HashSet    setA   = new HashSet   ( ), setB   = new HashSet   ( );
		LinkedList queueA = new LinkedList( ), queueB = new LinkedList( );
		
		queueA.addLast( g.vertexes.get( 0 ) );
		
		while ( !queueA.isEmpty( ) )
		{
			while ( !queueA.isEmpty( ) )
			{
				Vertex leaf = queueA.pop( );
				
				if ( setB.contains( leaf ) )
					return "Graph is not two-colorable!";
				
				if ( setA.add( leaf ) )
					for ( Vertex neighbor : g.getNeighbors( leaf ) )
						queueB.add( neighbor );
			}
			
			// Swap colors (i.e. sets and queues)
			HashSet tempSet = setA;
			setA = setB; setB = tempSet;
			
			LinkedList tempQueue = queueA;
			queueA = queueB; queueB = tempQueue;
		}
		
		// Color the sets
		for ( Vertex vertex : setA )
			vertex.color.set( 1 );
		
		for ( Vertex vertex : setB )
			vertex.color.set( 0 );
		
		return null;
	}
	
	public boolean allowsDynamicEvaluation( ) { return false;                }
	public boolean allowsOneTimeEvaluation( ) { return true;                 }
	public String  toString               ( ) { return "Two-color vertices"; }
	
return (FunctionBase) this;