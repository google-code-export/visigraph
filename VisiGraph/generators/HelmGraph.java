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
		
		int spokes = Integer.parseInt( matcher.group( 1 ) );
		double innerRadius = UserSettings.instance.arrangeCircleRadiusMultiplier.get( ) * (double) spokes;
		double outerRadius = 2.0 * innerRadius;
		double degreesPerVertex = 2.0 * Math.PI / (double) spokes;
		
		Vertex hub = new Vertex( 0.0, 0.0 );
		graph.vertices.add( hub );
		
		for( int i = 0; i < spokes; ++i )
		{
			Vertex vertex = new Vertex( innerRadius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ), innerRadius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) );
			graph.vertices.add( vertex );
			graph.edges.add( new Edge( false, hub, vertex ) );
		}
		
		for( int i = 0; i < spokes; ++i )
			graph.edges.add( new Edge( false, graph.vertices.get( i + 1 ), graph.vertices.get( ( i + 1 ) % spokes + 1 ) ) );
		
		for( int i = 0; i < spokes; ++i )
		{
			Vertex corner = new Vertex( outerRadius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ), outerRadius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) );
			graph.vertices.add( corner );
			graph.edges.add( new Edge( false, graph.vertices.get( i + 1 ), corner ) );
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
			case Generator.Attribute.DESCRIPTION:
				return "Constructs an <i>H<sub>n</sub></i> helm graph by connecting a pendant vertex to each vertex in a <i>W</i><sub><i>n</i>+1</sub> wheel graph's outer cycle.</p><p>The wheel graph's outer vertices are placed according to the user-defined \"Arrange circle radius multiplier\" setting, with their pendant vertices placed along the concentric circle with twice that radius.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[spokes]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([3-9]|[1-9]\\d{1,6})\\s*$";
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
				return new String[ ] { "Cycle graph", "Star graph", "Web graph", "Wheel graph" };
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
		return "Helm graph";
	}
    
return (Generator) this;
