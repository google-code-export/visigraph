import java.util.*;
import java.awt.Frame;
import java.awt.Graphics2D;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		Set selectedEdges = new HashSet( g.edges.size( ) );
		
		for(Edge e : g.edges)
			if(e.isSelected.get( ))
				selectedEdges.add(e);
		
		if(selectedEdges.size() < 1)
		{
			for ( Vertex v : g.vertexes )
				if ( v.isSelected.get( ) )
					return null;

			for ( Caption c : g.captions )
				if ( c.isSelected.get( ) )
					return null;

			selectedEdges.addAll(g.edges);
		}
			
		for(Edge e : selectedEdges)
			if ( e.thickness.get( ) < 1000 )
				e.thickness.set( 2.0 * e.thickness.get( ) );
		
		return null;
	}
	
	public boolean allowsDynamicEvaluation( )
	{
		return false;
	}
	
	public boolean allowsOneTimeEvaluation( )
	{
		return true;
	}
	
	public String toString( )
	{
		return "Thicken edges";
	}
	
return (FunctionBase)this;