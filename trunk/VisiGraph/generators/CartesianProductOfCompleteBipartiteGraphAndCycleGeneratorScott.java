import java.awt.*;
import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.models.generators.*;

	public Graph generate( String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed, Component owner )
	{
		Graph graph = new Graph( UserSettings.instance.defaultGraphName.get( ) + " " + this.toString( ), areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed );
		Pattern pattern = Pattern.compile( (String) this.getAttribute( Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION ) );
		Matcher matcher = pattern.matcher( params );
		matcher.find( );
		
		int r = Integer.parseInt( matcher.group( 1 ) );
		int s = Integer.parseInt( matcher.group( 2 ) );
		int n = Integer.parseInt( matcher.group( 3 ) );
		
		int north = (int) Math.floor( r / 2.0 );
		int east = (int) Math.ceil( s / 2.0 );
		int south = (int) Math.ceil( r / 2.0 );
		int west = (int) Math.floor( s / 2.0 );
		
		int linkHeight = ( north + 1 + south + 1 ) * 50;
		
		// Add all the links plus the ends of the chain
		for( int i = -1; i <= n; ++i )
		{
			// Add all the vertices of one bipartite graph (or end)
			for( int j = 0; j < north; ++j )
				graph.vertices.add( new Vertex( ( j - north ) * 50.0 + 25.0, ( i == -1 || i == n ? 0.0 : j - north ) * 50.0 + ( linkHeight + 100 ) * i ) );
			
			for( int j = 0; j < south; ++j )
				graph.vertices.add( new Vertex( ( j + 1 ) * 50.0 - 25.0, ( i == -1 || i == n ? 0.0 : j + 1 ) * 50.0 + ( linkHeight + 100 ) * i ) );
			
			for( int j = 0; j < west; ++j )
				graph.vertices.add( new Vertex( ( j - west ) * 50.0, ( linkHeight + 100 ) * i ) );
			
			for( int j = 0; j < east; ++j )
				graph.vertices.add( new Vertex( ( j + 1 ) * 50.0, ( linkHeight + 100 ) * i ) );
			
			// Add all the edges for that bipartite graph
			if( i != -1 && i != n )
				for( int j = 0; j < r; ++j )
					for( int k = 0; k < s; ++k )
						graph.edges.add( new Edge( false, graph.vertices.get( graph.vertices.size( ) - r - s + j ), graph.vertices.get( graph.vertices.size( ) - s + k ) ) );
			
			// Add the edges between this bipartite graph and the previous one
			if( i > -1 )
				for( int j = 0; j < r + s; ++j )
					graph.edges.add( new Edge( false, graph.vertices.get( graph.vertices.size( ) - r - s - j - 1 ), graph.vertices.get( graph.vertices.size( ) - j - 1 ) ) );
		}
		
		// For aesthetic reasons, make the vertices at the end of the chains smaller
		for( int i = 0; i < r + s; ++i )
		{
			graph.vertices.get( i ).radius.set( 2.0 );
			graph.vertices.get( graph.vertices.size( ) - i - 1 ).radius.set( 2.0 );
		}
		
		return graph;
	}
	
	public Object getAttribute( Generator.Attribute attribute )
	{
		switch( attribute )
		{
			case Generator.Attribute.AUTHOR:
				return "Cameron Behar";
			case Generator.Attribute.VERSION:
				return "20110130";
			case Generator.Attribute.ISOMORPHISMS:
				return new String[ ] { "<i>K<sub>r,s</sub></i> complete bipartite graph \u25A1 <i>C<sub>n</sub></i> cycle graph" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs the Cartesian product of a <i>K<sub>r,s</sub></i> complete bipartite graph and a <i>C<sub>n</sub></i> cycle graph (with one link's edges cut, for clarity), as drawn by Emmy Scott to minimize crossings.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[order of set A] [order of set B] [order of cycle]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{1,6}|[2-9])\\s+0*([1-9]\\d{1,6}|[2-9])\\s+0*([1-9]\\d{0,7})\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "2 \u2264 <code>order of set A</code> \u2264 9,999,999", "2 \u2264 <code>order of set B</code> \u2264 9,999,999", "1 \u2264 <code>order of cycle</code> \u2264 9,999,999" };
			case Generator.Attribute.ARE_LOOPS_ALLOWED:
				return Generator.BooleanRule.DEFAULT_FALSE;
			case Generator.Attribute.ARE_MULTIPLE_EDGES_ALLOWED:
				return Generator.BooleanRule.DEFAULT_FALSE;
			case Generator.Attribute.ARE_DIRECTED_EDGES_ALLOWED:
				return Generator.BooleanRule.FORCED_FALSE;
			case Generator.Attribute.ARE_CYCLES_ALLOWED:
				return Generator.BooleanRule.FORCED_TRUE;
			case Generator.Attribute.ARE_PARAMETERS_ALLOWED:
				return true;
			case Generator.Attribute.RELATED_GENERATORS:
				return new String[ ] { "Cartesian product of (two graphs)", "Cartesian product of a complete bipartite graph and cycle (Behar)", "Complete bipartite graph", "Cycle graph" };
			case Generator.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Count crossings" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Cartesian product" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Cartesian product of a complete bipartite graph and cycle (Scott)";
	}

return (Generator) this;
