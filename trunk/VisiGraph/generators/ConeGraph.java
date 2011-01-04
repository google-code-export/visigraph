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
		double innerRadius = UserSettings.instance.arrangeCircleRadiusMultiplier.get( ) * (double) m;
		double outerRadius = Math.max( UserSettings.instance.arrangeCircleRadiusMultiplier.get( ) * n, 2.0 * innerRadius );
		double degreesPerHubVertex = 2.0 * Math.PI / (double) m;
		double degreesPerCornerVertex = 2.0 * Math.PI / (double) n;
		
		for( int i = 0; i < m; ++i )
			graph.vertices.add( new Vertex( innerRadius * Math.cos( degreesPerHubVertex * i - Math.PI / 2.0 ), innerRadius * Math.sin( degreesPerHubVertex * i - Math.PI / 2.0 ) ) );
		
		for( int i = 0; i < m; ++i )
			graph.edges.add( new Edge( false, graph.vertices.get( i ), graph.vertices.get( ( i + 1 ) % m ) ) );
		
		for( int i = 0; i < n; ++i )
			graph.vertices.add( new Vertex( outerRadius * Math.cos( degreesPerCornerVertex * i - Math.PI / 2.0 ), outerRadius * Math.sin( degreesPerCornerVertex * i - Math.PI / 2.0 ) ) );
		
		for( int i = 0; i < m; ++i )
			for( int j = 0; j < n; ++j )
				graph.edges.add( new Edge( false, graph.vertices.get( i ), graph.vertices.get( j + m ) ) );
		
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
				return new String[ ] { "<i>C<sub>m</sub></i> cycle graph + <i>K\u0305<sub>n</sub></i> empty graph", "<i>K</i><sub>2,2,<i>n</i></sub> complete tripartite graph (for <i>C</i><sub>4,<i>n</i></sub>)" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs a <i>C<sub>m,n</sub></i> cone graph by joining a <i>C<sub>m</sub></i> cycle graph with a <i>K\u0305<sub>n</sub></i> empty graph.</p><p>The cycle's vertices are placed according to the user-defined \"Arrange circle radius multiplier\" setting, with the empty graph's vertices placed along the concentric circle with at least twice that radius.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[corners] [order of cycle]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{1,6}|[3-9])\\s+0*([1-9]\\d{1,6}|[3-9])\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "3 \u2264 <code>corners</code> \u2264 9,999,999", "3 \u2264 <code>order of cycle</code> \u2264 9,999,999" };
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
				return new String[ ] { "Complete tripartite graph", "Cycle graph", "Empty graph" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Joined graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Cone graph";
	}
        
return (Generator) this;
