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
		
		for( int i = 0; i < r; ++i )
			graph.vertices.add( new Vertex( ( i - ( r / 2 ) ) * UserSettings.instance.arrangeGridSpacing.get( ), -UserSettings.instance.arrangeGridSpacing.get( ) ) );
		
		for( int i = 0; i < s; ++i )
			graph.vertices.add( new Vertex( ( i - ( s / 2 ) ) * UserSettings.instance.arrangeGridSpacing.get( ), UserSettings.instance.arrangeGridSpacing.get( ) ) );
		
		for( int i = 0; i < r; ++i )
			for( int j = 0; j < s; ++j )
				graph.edges.add( new Edge( false, graph.vertices.get( i ), graph.vertices.get( j + r ) ) );
		
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
				return new String[ ] { "<i>K\u0305<sub>r</sub></i> empty graph + <i>K\u0305<sub>s</sub></i> empty graph", "<i>K</i><sub>2,<i>r,s</i></sub> complete k-partite graph", "<i>C</i><sub>n,1,3,...,2\u00B7<code>floor(<i>n</i>/2)</code>+1</sub> circulant graph (for <i>K<sub>n,n</sub></i>)", "Utility graph (for <i>K</i><sub>3,3</sub>)" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs a <i>K<sub>r,s</sub></i> complete bipartite graph by connecting each pair of vertices from one set of size <i>r</i> to a disjoint set of size <i>s</i>.</p><p>Vertices are placed according to the user-defined \"Arrange grid spacing\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[order of set A] [order of set B]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{0,6})\\s+0*([1-9]\\d{0,6})\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "1 \u2264 <code>order of set A</code> \u2264 9,999,999", "1 \u2264 <code>order of set B</code> \u2264 9,999,999" };
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
				return new String[ ] { "Complete graph", "Complete k-partite graph", "Complete tripartite graph", "Crown graph" };
			case Generator.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Connect completely", "Is bipartite", "Two-color vertices" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Joined graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Complete bipartite graph";
	}
        
return (Generator) this;
