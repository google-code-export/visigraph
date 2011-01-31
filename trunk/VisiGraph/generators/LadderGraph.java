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
		
		int n = Integer.parseInt( matcher.group( 1 ) );
		
		for( int col = 0; col < n; ++col )
		{
			graph.vertices.add( new Vertex( col * UserSettings.instance.arrangeGridSpacing.get( ), 0.0 ) );
			graph.vertices.add( new Vertex( col * UserSettings.instance.arrangeGridSpacing.get( ), UserSettings.instance.arrangeGridSpacing.get( ) ) );
			graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( graph.vertices.size( ) - 1 ), graph.vertices.get( graph.vertices.size( ) - 2 ) ) );
			
			if( col > 0 )
			{
				graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( graph.vertices.size( ) - 1 ), graph.vertices.get( graph.vertices.size( ) - 3 ) ) );
				graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( graph.vertices.size( ) - 2 ), graph.vertices.get( graph.vertices.size( ) - 4 ) ) );
			}
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
				return new String[ ] { "<i>P<sub>n</sub></i> path graph \u25A1 <i>P</i><sub>2</sub> path graph", "<i>G</i><sub><i>n</i>,2</sub> grid graph" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs an <i>L<sub>n</sub></i> ladder graph by taking the Cartesian product of a <i>P<sub>n</sub></i> path graph and a <i>P</i><sub>2</sub> path graph.</p><p>Vertices are placed according to the user-defined \"Arrange grid spacing\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[rungs]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{0,6})\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "1 \u2264 <code>rungs</code> \u2264 9,999,999" };
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
				return new String[ ] { "Cartesian product of (two graphs)", "Centipede tree", "Grid graph", "Path graph" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Cartesian product" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Ladder graph";
	}
        
return (Generator) this;
