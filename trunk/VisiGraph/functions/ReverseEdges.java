import java.util.*;
import java.awt.Frame;
import java.awt.Graphics2D;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		if ( !g.areDirectedEdgesAllowed )
			return "Cannot reverse undirected edges!";
		
		List selectedEdges = new LinkedList( );
		
		for ( Edge e : g.edges )
			if ( e.isSelected.get( ) && e.from != e.to )
				selectedEdges.add( e );
		
		if ( selectedEdges.isEmpty( ) )
			selectedEdges.addAll( g.edges );
		
		g.suspendNotifications( true );
		
		for ( Edge e : selectedEdges )
		{
			g.edges.remove( e );
			Edge reversedE = new Edge( e.isDirected, e.to, e.from, e.weight.get( ), e.color.get( ), e.label.get( ), e.isSelected.get( ) );
			reversedE.thickness.set( e.thickness.get( ) );
			reversedE.handleX.set( e.handleX.get( ) );
			reversedE.handleY.set( e.handleY.get( ) );
			reversedE.tag.set( e.tag.get( ) );
			g.edges.add( reversedE );
			reversedE.refresh( );
		}
		
		g.suspendNotifications( false );
		
		return null;
	}
	
	public boolean allowsDynamicEvaluation( ) { return false;           }
	public boolean allowsOneTimeEvaluation( ) { return true;            }
	public String  toString               ( ) { return "Reverse edges"; }
	
return (FunctionBase)this;