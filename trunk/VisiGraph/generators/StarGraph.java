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
		
		Vertex hub = new Vertex( 0.0, 0.0 );
		graph.vertices.add( hub );
		
		int leaves = Integer.parseInt( matcher.group( 1 ) ) - 1;
		if( leaves > 0 )
		{
			double radius = UserSettings.instance.arrangeCircleRadiusMultiplier.get( ) * (double) leaves;
			double degreesPerVertex = 2.0 * Math.PI / (double) leaves;
			
			for( int i = 0; i < leaves; ++i )
			{
				Vertex corner = new Vertex( radius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ), radius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) );
				graph.vertices.add( corner );
				graph.edges.add( new Edge( false, hub, corner ) );
			}
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
				return new String[ ] { "<i>K\u0305<sub>n</sub></i> empty graph + the singleton graph", "<i>S</i><sub>1,<i>n</i>-1</sub> symmetric tree", "Claw graph (for <i>S</i><sub>4</sub>)" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs an <i>S<sub>n</sub></i> star graph by connecting <i>n</i> - 1 pendants to the singleton graph.</p><p>Pendants are placed according to the user-defined \"Arrange circle radius multiplier\" setting.";
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
				return Generator.BooleanRule.FORCED_FALSE;
			case Generator.Attribute.ARE_CYCLES_ALLOWED:
				return Generator.BooleanRule.DEFAULT_TRUE;
			case Generator.Attribute.ARE_PARAMETERS_ALLOWED:
				return true;
			case Generator.Attribute.RELATED_GENERATORS:
				return new String[ ] { "Banana tree", "Book graph", "Firecracker graph", "Gear graph", "Helm graph", "Stacked book graph", "Wheel graph" };
			case Generator.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Add pendants" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Bridged graph", "Joined graph", "Tree" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Star graph";
	}
        
return (Generator) this;
