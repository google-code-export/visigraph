import java.awt.*;
import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.models.generators.*;
import edu.belmont.mth.visigraph.models.generators.Generator.*;

	public Graph generate( String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed, Component owner )
	{
		Graph graph = new Graph( UserSettings.instance.defaultGraphName.get( ) + " " + this.toString( ), areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed );
		Pattern pattern = Pattern.compile( (String) this.getAttribute( Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION ) );
		Matcher matcher = pattern.matcher( params );
		matcher.find( );
		
		int m = Integer.parseInt( matcher.group( 1 ) );
		int n = Integer.parseInt( matcher.group( 2 ) );
		double radius = UserSettings.instance.arrangeCircleRadiusMultiplier.get( ) * (double) m;
		double degreesPerVertex = 2.0 * Math.PI / (double) m;
		
		for( int i = 0; i < m; ++i )
			graph.vertices.add( new Vertex( radius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ), radius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) ) );
		
		for( int i = 0; i < m; ++i )
			graph.edges.add( new Edge( false, graph.vertices.get( i ), graph.vertices.get( ( i + 1 ) % m ) ) );
		
		Vertex previousVertex = graph.vertices.get( 0 );
		
		for( int i = 1; i < n + 1; ++i )
		{
			graph.vertices.add( new Vertex( previousVertex.x.get( ), previousVertex.y.get( ) - UserSettings.instance.arrangeGridSpacing.get( ) ) );
			graph.edges.add( new Edge( false, previousVertex, graph.vertices.get( graph.vertices.size( ) - 1 ) ) );
			previousVertex = graph.vertices.get( graph.vertices.size( ) - 1 );
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
				return new String[ ] { "<i>L</i><sub>3,<i>n</i></sub> lollipop graph (for <i>T</i><sub>3,<i>n</i></sub>)", "<i>P<sub>m</sub></i> pan graph (for <i>T</i><sub><i>m</i>,1</sub>)", "Paw graph (for <i>S</i><sub>3,1</sub>)", "Banner graph (for <i>S</i><sub>4,1</sub>)" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs an <i>T<sub>m,n</sub></i> tadpole graph by connecting one vertex of a <i>C<sub>m</sub></i> cycle graph to the end of a <i>P<sub>n</sub></i> path graph.</p><p>The cycle graphs' vertices are placed according to the user-defined \"Arrange circle radius multiplier\" setting, and the path graph's vertices are placed according to the \"Arrange grid spacing\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[order of cycle] [order of tail]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{1,6}|[3-9])\\s+0*([1-9]\\d{0,6})\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "3 \u2264 <code>order of cycle</code> \u2264 9,999,999", "1 \u2264 <code>order of tail</code> \u2264 9,999,999" };
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
				return new String[ ] { "Cycle graph", "Lollipop graph", "Path graph" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Bridged graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Tadpole graph";
	}
        
return (Generator) this;
