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
		
		int n = Integer.parseInt( matcher.group( 1 ) ) * 2;
		double radius = UserSettings.instance.arrangeCircleRadiusMultiplier.get( ) * (double) n;
		double degreesPerVertex = 2.0 * Math.PI / (double) n;
		
		Vertex hub = new Vertex( 0.0, 0.0 );
		graph.vertices.add( hub );
		
		for( int i = 0; i < n; ++i )
		{
			Vertex corner = new Vertex( radius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ), radius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) );
			graph.vertices.add( corner );
			graph.edges.add( new Edge( false, hub, corner ) );
		}
		
		for( int i = 1; i < n; i += 2 )
			graph.edges.add( new Edge( false, graph.vertices.get( i ), graph.vertices.get( i + 1 ) ) );
		
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
				return new String[ ] { "<i>W<sub>n</sub></i> Dutch windmill graph", "<i>W</i><sub>3,<i>n</i></sub> windmill graph", "Butterfly graph (for <i>F</i><sub>2</sub>)" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs an <i>F<sub>n</sub></i> friendship graph by creating <i>n</i> copies of a <i>C<sub>3</sub></i> cycle graph with one vertex in common.</p><p>All vertices except the shared hub are placed according to the user-defined \"Arrange circle radius multiplier\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[cycles]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{1,6}|[2-9])\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "2 \u2264 <code>cycles</code> \u2264 9,999,999" };
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
				return new String[ ] { "Cycle graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Friendship graph";
	}
        
return (Generator) this;
