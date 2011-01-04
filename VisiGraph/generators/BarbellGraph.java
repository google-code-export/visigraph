import java.awt.*;
import java.util.*;
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
			graph.vertices.add( new Vertex( radius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ), -radius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) - radius - UserSettings.instance.arrangeGridSpacing.get( ) / 2.0 ) );
		
		for( int i = 0; i < n; ++i )
			graph.vertices.add( new Vertex( radius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ), radius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) + radius + UserSettings.instance.arrangeGridSpacing.get( ) / 2.0 ) );
		
		for( int i = 0; i < n; ++i )
		{
			int start = i + ( areLoopsAllowed ? 0 : 1 );
			for( int j = start; j < n; ++j )
			{
				graph.edges.add( new Edge( false, graph.vertices.get( i ), graph.vertices.get( j ) ) );
				graph.edges.add( new Edge( false, graph.vertices.get( i + n ), graph.vertices.get( j + n ) ) );
			}
		}
		
		graph.edges.add( new Edge( false, graph.vertices.get( 0 ), graph.vertices.get( n ) ) );
		
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
				return "Constructs a <i>B<sub>n</sub></i> barbell graph by connecting two <i>K<sub>n</sub></i> complete graphs by a bridge.</p><p>The complete graphs' vertices are placed according to the user-defined \"Arrange circle radius multiplier\" setting, with the bridge's length supplied by the \"Arrange grid spacing\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[order of complete graphs]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{1,6}|[3-9])\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "3 \u2264 <code>order of complete graphs</code> \u2264 9,999,999" };
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
				return new String[ ] { "Complete graph" };
			case Generator.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Connect completely" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Bridged graph", "Joined graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Barbell graph";
	}
        
return (Generator) this;
