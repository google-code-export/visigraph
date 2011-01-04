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
		
		int n = Integer.parseInt( matcher.group( 1 ) );
		double radius = UserSettings.instance.arrangeCircleRadiusMultiplier.get( ) * (double) n;
		double degreesPerVertex = 2.0 * Math.PI / (double) n;
		
		for( int i = 0; i < n; ++i )
			graph.vertices.add( new Vertex( radius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ), radius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) ) );
		
		for( int i = 0; i < n; ++i )
			graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( i ), graph.vertices.get( ( i + 1 ) % n ) ) );
		
		graph.vertices.add( new Vertex( graph.vertices.get( 0 ).x.get( ), graph.vertices.get( 0 ).y.get( ) - UserSettings.instance.arrangeGridSpacing.get( ) ) );
		graph.edges.add( new Edge( false, graph.vertices.get( 0 ), graph.vertices.get( graph.vertices.size( ) - 1 ) ) );
		
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
				return new String[ ] { "<i>T</i><sub><i>n</i>,1</sub> tadpole graph (for <i>P<sub>n</sub></i>)" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs a <i>P<sub>n</sub></i> pan graph by connecting a pendant to one vertex of a <i>C<sub>n</sub></i> cycle graph.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[order of cycle]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{1,6}|[3-9])\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "3 \u2264 <code>order of cycle</code> \u2264 9,999,999" };
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
				return new String[ ] { "Cycle graph", "Tadpole graph" };
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
		return "Pan graph";
	}
        
return (Generator) this;
