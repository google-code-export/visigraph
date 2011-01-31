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
		
		Vertex hub = new Vertex( 0.0, 0.0 );
		graph.vertices.add( hub );
		
		for( int i = 0; i < n; ++i )
		{
			Vertex corner = new Vertex( radius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ), radius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) );
			graph.vertices.add( corner );
			graph.edges.add( new Edge( false, hub, corner ) );
		}
		
		hub = new Vertex( UserSettings.instance.arrangeGridSpacing.get( ), UserSettings.instance.arrangeGridSpacing.get( ) );
		graph.vertices.add( hub );
		
		for( int i = 0; i < n; ++i )
		{
			Vertex corner = new Vertex( radius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ) + hub.x.get( ), radius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) + hub.y.get( ) );
			graph.vertices.add( corner );
			graph.edges.add( new Edge( false, hub, corner ) );
		}
		
		for( int i = 0; i < n + 1; ++i )
			graph.edges.add( new Edge( false, graph.vertices.get( i ), graph.vertices.get( i + n + 1 ) ) );
		
		return graph;
	}
	
	public Object getAttribute( Generator.Attribute attribute )
	{
		switch( attribute )
		{
			case Generator.Attribute.AUTHOR:
				return "Cameron Behar";
			case Generator.Attribute.VERSION:
				return "20110130";
			case Generator.Attribute.ISOMORPHISMS:
				return new String[ ] { "<i>S</i><sub><i>n</i>+1</sub> star graph \u25A1 <i>P</i><sub>2</sub> path graph", "<i>B</i><sub><i>n</i>,2</sub> stacked book graph" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs a <i>B<sub>n</sub></i> book graph by taking the Cartesian product of an <i>S</i><sub><i>n</i>+1</sub> star graph and a <i>P</i><sub>2</sub> path graph.</p><p>The star graphs' vertices are placed according to the user-defined \"Arrange circle radius multiplier\" setting, with the copies' x- and y-offsets supplied by the \"Arrange grid spacing\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[pages]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{1,6}|[3-9])\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "3 \u2264 <code>pages</code> \u2264 9,999,999" };
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
				return new String[ ] { "Cartesian product of (two graphs)", "Path graph", "Stacked book graph", "Star graph" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Cartesian product" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Book graph";
	}
        
return (Generator) this;
