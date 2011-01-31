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
		
		int rows = Integer.parseInt( matcher.group( 1 ) );
		int cols = Integer.parseInt( matcher.group( 2 ) );
		
		for( int row = 0; row < rows; ++row )
			for( int col = 0; col < cols; ++col )
			{
				graph.vertices.add( new Vertex( col * UserSettings.instance.arrangeGridSpacing.get( ), row * UserSettings.instance.arrangeGridSpacing.get( ) ) );
				if( col > 0 )
					graph.edges.add( new Edge( false, graph.vertices.get( graph.vertices.size( ) - 1 ), graph.vertices.get( graph.vertices.size( ) - 1 - 1 ) ) );
				if( row > 0 )
					graph.edges.add( new Edge( false, graph.vertices.get( graph.vertices.size( ) - 1 ), graph.vertices.get( graph.vertices.size( ) - cols - 1 ) ) );
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
				return new String[ ] { "<i>M<sub>m,n</sub></i> mesh graph", "<i>P<sub>m</sub></i> path graph \u25A1 <i>P<sub>n</sub></i> path graph" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs a <i>G<sub>m,n</sub></i> grid graph by taking the Cartesian product of a <i>P<sub>m</sub></i> path graph and a <i>P<sub>n</sub></i> path graph.</p><p>Vertices are placed according to the user-defined \"Arrange grid spacing\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[rows] [columns]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{0,6})\\s+0*([1-9]\\d{0,6})\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "1 \u2264 <code>rows</code> \u2264 9,999,999", "1 \u2264 <code>columns</code> \u2264 9,999,999" };
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
				return new String[ ] { "Cartesian product of (two graphs)", "Path graph" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Cartesian product" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Grid graph";
	}
        
return (Generator) this;
