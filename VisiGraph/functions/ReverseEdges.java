import java.util.*;
import java.awt.Frame;
import java.awt.Graphics2D;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		if ( !g.areDirectedEdgesAllowed )
			return "Cannot reverse undirected edges!";
		
		Set selectedEdges = new HashSet( g.edges.size( ) );
		
		for(Edge e : g.edges)
			if(e.isSelected.get( ) && e.from != e.to)
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
		{
			g.edges.remove( e );
			Edge reversedE = new Edge( e.isDirected, e.to, e.from, e.weight.get( ), e.color.get( ), e.label.get( ), e.isSelected.get( ) );
			reversedE.thickness.set( e.thickness.get( ) );
			reversedE.suspendNotifications( true );
			reversedE.handleX.set( e.handleX.get( ) );
			reversedE.handleY.set( e.handleY.get( ) );
			reversedE.suspendNotifications( false );
			reversedE.tag.set( e.tag.get( ) );
			g.edges.add( reversedE );
			reversedE.refresh( );
		}
		
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
		return "Reverse edges";
	}
	
return (FunctionBase)this;