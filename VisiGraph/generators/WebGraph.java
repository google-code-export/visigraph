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
		double radius = UserSettings.instance.arrangeCircleRadiusMultiplier.get( ) * n;
		double degreesPerVertex = 2.0 * Math.PI / n;
		
		for( int i = 0; i < n; ++i )
			graph.vertices.add( new Vertex( radius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ), radius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) ) );
		
		for( int i = 0; i < n; ++i )
			graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( i ), graph.vertices.get( ( i + 1 ) % n ) ) );
		
		for( int i = 0; i < n; ++i )
			graph.vertices.add( new Vertex( 2.0 * radius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ), 2.0 * radius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) ) );
		
		for( int i = 0; i < n; ++i )
			graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( i + n ), graph.vertices.get( ( i + 1 ) % n + n ) ) );
		
		for( int i = 0; i < n; ++i )
			graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( i ), graph.vertices.get( i + n ) ) );
		
		for( int i = 0; i < n; ++i )
			graph.vertices.add( new Vertex( 3.0 * radius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ), 3.0 * radius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) ) );
		
		for( int i = 0; i < n; ++i )
			graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( i + n ), graph.vertices.get( i + 2 * n ) ) );
		
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
			case Generator.Attribute.DESCRIPTION:
				return "Constructs a <i>W<sub>n</sub></i> web graph by connecting a pendant vertex to each vertex in a <i>Y<sub>n</sub></i> prism graph's outer cycle.</p><p>The inner cycle's vertices are placed according to the user-defined \"Arrange circle radius multiplier\" setting, with the outer cycle graph's vertices placed along the concentric circle with twice that radius, and the pendants along the concentric circle with 1.5x that radius.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[spokes]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{1,6}|[3-9])\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "3 \u2264 <code>spokes</code> \u2264 9,999,999" };
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
				return new String[ ] { "Prism graph" };
			case Generator.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Add pendants" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Bridged graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Web graph";
	}
        
return (Generator) this;
