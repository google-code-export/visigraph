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
		
		int m = Integer.parseInt( matcher.group( 1 ) );
		int n = Integer.parseInt( matcher.group( 2 ) );
		double radius = UserSettings.instance.arrangeCircleRadiusMultiplier.get( ) * (double) m;
		double degreesPerVertex = 2.0 * Math.PI / (double) m;
		
		Vertex hub = new Vertex( 0.0, 0.0 );
		graph.vertices.add( hub );
		
		for( int i = 0; i < m; ++i )
		{
			Vertex corner = new Vertex( radius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ), radius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) );
			graph.vertices.add( corner );
			graph.edges.add( new Edge( false, hub, corner ) );
		}
		
		for( int i = 1; i < n; ++i )
		{
			hub = new Vertex( i * UserSettings.instance.arrangeGridSpacing.get( ), i * UserSettings.instance.arrangeGridSpacing.get( ) );
			graph.vertices.add( hub );
			
			for( int j = 0; j < m; ++j )
			{
				Vertex corner = new Vertex( radius * Math.cos( degreesPerVertex * j - Math.PI / 2.0 ) + hub.x.get( ), radius * Math.sin( degreesPerVertex * j - Math.PI / 2.0 ) + hub.y.get( ) );
				graph.vertices.add( corner );
				graph.edges.add( new Edge( false, hub, corner ) );
			}
			
			for( int j = 0; j < m + 1; ++j )
				graph.edges.add( new Edge( false, graph.vertices.get( j + ( m + 1 ) * ( i - 1 ) ), graph.vertices.get( j + ( m + 1 ) * i ) ) );
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
				return new String[ ] { "<i>S</i><sub><i>m</i>+1</sub> star graph \u00D7 <i>P<sub>n</sub></i> path graph", "<i>B<sub>m</sub></i> book graph (for <i>B</i><sub><i>m</i>,2</sub>)" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs a <i>B<sub>m,n</sub></i> stacked book graph by taking the Cartesian product of an <i>S</i><sub><i>m</i>+1</sub> star graph and <i>P</i><sub>n</sub> path graph.</p><p>The star graphs' vertices are placed according to the user-defined \"Arrange circle radius multiplier\" setting, with the copies' x- and y-offsets supplied by the \"Arrange grid spacing\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[pages] [copies]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{1,6}|[3-9])\\s+0*([1-9]\\d{1,6}|[2-9])\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "3 \u2264 <code>pages</code> \u2264 9,999,999", "2 \u2264 <code>copies</code> \u2264 9,999,999" };
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
				return new String[ ] { "Book graph", "Path graph", "Star graph" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Cartesian product" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Stacked book graph";
	}
        
return (Generator) this;
