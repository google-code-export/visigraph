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
		double outerRadius = ( n == 3 ? 2.75 : 2.0 ) * innerRadius;
		double degreesPerVertex = 2.0 * Math.PI / (double) n;
		
		for( int i = 0; i < n; ++i )
			graph.vertices.add( new Vertex( innerRadius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ), innerRadius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) ) );
		
		for( int i = 0; i < n; ++i )
			graph.vertices.add( new Vertex( outerRadius * Math.cos( degreesPerVertex * i + ( degreesPerVertex - Math.PI ) / 2.0 ), outerRadius * Math.sin( degreesPerVertex * i + ( degreesPerVertex - Math.PI ) / 2.0 ) ) );
		
		for( int i = 0; i < n; ++i )
			graph.edges.add( new Edge( false, graph.vertices.get( i ), graph.vertices.get( ( i + 1 ) % n ) ) );
		
		for( int i = 0; i < n; ++i )
			graph.edges.add( new Edge( false, graph.vertices.get( i + n ), graph.vertices.get( ( i + 1 ) % n + n ) ) );
		
		for( int i = 0; i < n; ++i )
		{
			graph.edges.add( new Edge( false, graph.vertices.get( i ), graph.vertices.get( i + n ) ) );
			graph.edges.add( new Edge( false, graph.vertices.get( ( i + 1 ) % n ), graph.vertices.get( i + n ) ) );
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
				return new String[ ] { "Skeleton of an <i>n</i>-antiprism", "<i>C</i><sub>2<i>n</i>,1,2</sub> circulant graph" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs an <i>A<sub>n</sub></i> antiprism graph from the skeleton of an <i>n</i>-antiprism.  An antiprism is a polyhedron composed of two parallel copies of some particular <i>n</i>-sided polygon, connected by an alternating band of triangles.  Antiprisms differ from prisms in that their bases are twisted relative to each other such that the side faces are triangles, rather than quadrilaterals.</p><p>The antiprism's inner cycle of vertices are placed according to the user-defined \"Arrange circle radius multiplier\" setting, with the outer cycle placed along the concentric circle with twice that radius (or 2.75x for <i>n</i> = 3).";
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
				return new String[ ] { "Circulant graph", "Crossed prism graph", "Cycle graph", "Möbius ladder graph", "Prism graph" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Skeleton graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Antiprism graph";
	}
        
return (Generator) this;
