import java.awt.*;
import java.util.*;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		Vertex selectedVertex = null;
		
		for ( Vertex v : g.vertexes )
			if ( v.isSelected.get( ) )
			{
				if ( selectedVertex != null )
					return "More than one vertex selected!";
				else
					selectedVertex = v;
			}
		
		if ( selectedVertex == null )
			return "No vertices selected!";
		
		LinkedList leaves = new LinkedList( );
		HashMap vertexDistances = new HashMap( );
		
		for ( Vertex v : g.vertexes )
			vertexDistances.put( v, Integer.MAX_VALUE );
		
		leaves.add( new Leaf( selectedVertex, 0 ) );
		
		while ( !leaves.isEmpty( ) )
		{
			Leaf leaf = (Leaf) leaves.pop( );
			
			if ( leaf.distance < vertexDistances.get( leaf.vertex ) )
			{
				vertexDistances.put( leaf.vertex, leaf.distance );
				
				for ( Vertex neighbor : g.getNeighbors( leaf.vertex ) )
					leaves.add( new Leaf( neighbor, leaf.distance + 1 ) );
			}
		}
		
		int maxDistance = 0;
		for ( Integer distance : vertexDistances.values( ) )
			if ( distance > maxDistance )
				maxDistance = distance;
		
		return ( maxDistance == Integer.MAX_VALUE ? "\u221E" : Integer.toString( maxDistance ) );
	}
	
	class Leaf
	{
		Vertex vertex;
		int distance;
		
		public Leaf ( Vertex vertex, int distance )
		{
			this.vertex = vertex;
			this.distance = distance;
		}
	}
	
	public boolean allowsDynamicEvaluation( ) { return false;                    }
	public boolean allowsOneTimeEvaluation( ) { return true;                     }
	public String  toString               ( ) { return "Calculate eccentricity"; }
	
return (FunctionBase) this;