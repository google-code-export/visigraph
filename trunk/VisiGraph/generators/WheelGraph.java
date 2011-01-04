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
		
		int spokes = Integer.parseInt( matcher.group( 1 ) ) - 1;
		double radius = UserSettings.instance.arrangeCircleRadiusMultiplier.get( ) * (double) spokes;
		double degreesPerVertex = 2.0 * Math.PI / (double) spokes;
		
		Vertex hub = new Vertex( 0.0, 0.0 );
		graph.vertices.add( hub );
		
		for( int i = 0; i < spokes; ++i )
		{
			Vertex corner = new Vertex( radius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ), radius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) );
			graph.vertices.add( corner );
			graph.edges.add( new Edge( false, hub, corner ) );
		}
		
		for( int i = 0; i < spokes; ++i )
			graph.edges.add( new Edge( false, graph.vertices.get( i + 1 ), graph.vertices.get( ( i + 1 ) % spokes + 1 ) ) );
		
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
				return new String[ ] { "1-skeleton of an (<i>n</i> - 1)-gonal pyramid", "<i>C</i><sub><i>n</i>-1</sub> cycle graph + the singleton graph" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs a <i>W<sub>n</sub></i> wheel graph by joining a <i>C</i><sub><i>n</i>-1</sub> cycle graph to the singleton graph.</p><p>The outer cycle's vertices are placed according to the user-defined \"Arrange circle radius multiplier\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[order]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([4-9]|[1-9]\\d{1,6})\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "4 \u2264 <code>order</code> \u2264 9,999,999" };
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
				return new String[ ] { "Cycle graph", "Gear graph", "Helm graph", "Star graph" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Joined graph", "Skeleton graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Wheel graph";
	}
    
return (Generator) this;
