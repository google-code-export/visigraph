import java.awt.*;
import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.models.generators.*;

	public Graph generate( String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed, Component owner )
	{
		Graph graph = new Graph( UserSettings.instance.defaultGraphName.get( ) + " " + this.toString( ), areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed );
		Pattern pattern = Pattern.compile( (String) this.getAttribute( Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION ) );
		Matcher matcher = pattern.matcher( params );
		matcher.find( );
		
		int n = Integer.parseInt( matcher.group( 1 ) );
		
		for( int i = 0; i < n; ++i )
			graph.vertices.add( new Vertex( 0.0, 0.0 ) );
		
		GraphUtilities.arrangeCircle( graph );
		
		for( int i = 0; i < n; ++i )
		{
			int start = ( areDirectedEdgesAllowed ? 0 : i + ( areLoopsAllowed ? 0 : 1 ) );
			for( int j = start; j < n; ++j )
				graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( i ), graph.vertices.get( j ) ) );
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
				return new String[ ] { "<i>C</i><sub>n,1,2,...,<code>floor(<i>n</i>/2)</code></sub> circulant graph" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs a <i>K<sub>n</sub></i> complete graph by connecting each pair of <i>n</i> vertices with an edge.  For directed graphs, this includes both an edge from <i>A</i> to <i>B</i> and—in the opposite direction—from <i>B</i> to <i>A</i>.  For pseudographs, this also includes an edge from each vertex to itself (i.e. loops).</p><p>Vertices are placed according to the user-defined \"Arrange grid spacing\" setting.";
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
				return Generator.BooleanRule.FORCED_TRUE;
			case Generator.Attribute.ARE_PARAMETERS_ALLOWED:
				return true;
			case Generator.Attribute.RELATED_GENERATORS:
				return new String[ ] { "Complete bipartite graph", "Complete k-partite graph", "Complete tripartite graph" };
			case Generator.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Connect completely" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Joined graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Complete graph";
	}
        
return (Generator) this;
