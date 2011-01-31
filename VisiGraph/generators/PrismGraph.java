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
		
		for( int i = 0; i < n; ++i )
			graph.edges.add( new Edge( false, graph.vertices.get( i ), graph.vertices.get( ( i + 1 ) % n ) ) );
		
		for( int i = 0; i < n; ++i )
			graph.edges.add( new Edge( false, graph.vertices.get( i + n ), graph.vertices.get( ( i + 1 ) % n + n ) ) );
		
		for( int i = 0; i < n; ++i )
			graph.edges.add( new Edge( false, graph.vertices.get( i ), graph.vertices.get( i + n ) ) );
		
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
				return new String[ ] { "Skeleton of an <i>n</i>-prism", "<i>P<sub>n</sub></i> path graph \u25A1 <i>C</i><sub>2</sub> cycle graph", "<i>C<sub>n</sub></i> circular ladder graph", "<i>P</i><sub>1,<i>n</i></sub> generalized Petersen graph" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs a <i>Y<sub>n</sub></i> prism graph by taking the Cartesian product of a <i>C<sub>n</sub></i> cycle graph and a <i>P</i><sub>2</sub> path graph.</p><p>The prism graph's inner cycle of vertices are placed according to the user-defined \"Arrange circle radius multiplier\" setting, with the outer cycle placed along the concentric circle with twice that radius.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[order of base]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{1,6}|[3-9])\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "3 \u2264 <code>order of base</code> \u2264 9,999,999" };
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
				return new String[ ] { "Antiprism graph", "Cartesian product of (two graphs)", "Crossed prism graph", "Cycle graph", "Generalized petersen graph", "Ladder graph", "Möbius ladder graph", "Web graph" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Cartesian product", "Skeleton graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Prism graph";
	}
        
return (Generator) this;
