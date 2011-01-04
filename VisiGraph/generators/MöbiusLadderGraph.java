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
		double innerRadius = UserSettings.instance.arrangeCircleRadiusMultiplier.get( ) * (double) n;
		double outerRadius = 2.0 * innerRadius;
		double degreesPerVertex = 2.0 * Math.PI / (double) n;
		
		for( int i = 0; i < n; ++i )
			graph.vertices.add( new Vertex( innerRadius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ), innerRadius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) ) );
		
		for( int i = 0; i < n; ++i )
			graph.vertices.add( new Vertex( outerRadius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ), outerRadius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) ) );
		
		for( int i = 0; i < n - 1; ++i )
			graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( i ), graph.vertices.get( ( i + 1 ) % n ) ) );
		
		for( int i = 0; i < n - 1; ++i )
			graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( i + n ), graph.vertices.get( ( i + 1 ) % n + n ) ) );
		
		for( int i = 0; i < n; ++i )
			graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( i ), graph.vertices.get( i + n ) ) );
		
		graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( 0 ), graph.vertices.get( 2 * n - 1 ) ) );
		graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( n ), graph.vertices.get( n - 1 ) ) );
		
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
				return new String[ ] { "<i>C</i><sub>2<i>n</i>,1,<i>n</i></sub> circulant graph", "<i>H</i><sub>2^(2<i>n</i>)+1</sub> Haar graph (for <i>M</i><sub>2<i>n</i>-1</sub>)" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs an <i>M<sub>n</sub></i> Möbius ladder graph by swapping the end-vertices of two parallel edges of a <i>Y<sub>n</sub></i> prism graph.</p><p>The Möbius ladder's inner cycle of vertices are placed according to the user-defined \"Arrange circle radius multiplier\" setting, with the outer cycle placed along the concentric circle with twice that radius.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[rungs]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{1,6}|[4-9])\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "4 \u2264 <code>rungs</code> \u2264 9,999,999" };
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
				return new String[ ] { "Antiprism graph", "Path graph", "Ladder graph" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Cartesian product" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Möbius ladder graph";
	}
        
return (Generator) this;
