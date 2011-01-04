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
			if( col > 0 )
				graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( col - 1 ), graph.vertices.get( col ) ) );
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
				return "20110101";
			case Generator.Attribute.ISOMORPHISMS:
				return new String[ ] { "<i>T</i><sub><i>n</i>-1,1</sub> symmetric tree" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs a <i>P<sub>n</sub></i> path graph by connecting <i>n</i> vertices consecutively in a single line such that two have a degree of one and all others have degrees of two.</p><p>Vertices are placed according to the user-defined \"Arrange grid spacing\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[order]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{0,6})\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "1 \u2264 <code>order</code> \u2264 9,999,999" };
			case Generator.Attribute.ARE_LOOPS_ALLOWED:
				return Generator.BooleanRule.DEFAULT_FALSE;
			case Generator.Attribute.ARE_MULTIPLE_EDGES_ALLOWED:
				return Generator.BooleanRule.DEFAULT_FALSE;
			case Generator.Attribute.ARE_DIRECTED_EDGES_ALLOWED:
				return Generator.BooleanRule.DEFAULT_FALSE;
			case Generator.Attribute.ARE_CYCLES_ALLOWED:
				return Generator.BooleanRule.DEFAULT_TRUE;
			case Generator.Attribute.ARE_PARAMETERS_ALLOWED:
				return true;
			case Generator.Attribute.RELATED_GENERATORS:
				return new String[ ] { "Book graph", "Centipede tree", "Grid graph", "Ladder graph", "Lollipop graph", "Prism graph", "Stacked book graph", "Symmetric tree", "Tadpole graph" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Bridged graph", "Tree" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Path graph";
	}
        
return (Generator) this;
