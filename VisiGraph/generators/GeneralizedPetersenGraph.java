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
		int k = Integer.parseInt( matcher.group( 2 ) );
		
		if( k > ( n - 1 ) / 2 )
		{
			JOptionPane.showMessageDialog( owner, "Offset must be \u2264 floor((order of outer cycle - 1) / 2)!", "Invalid offset!", JOptionPane.ERROR_MESSAGE );
			return null;
		}
		
		double innerRadius = UserSettings.instance.arrangeCircleRadiusMultiplier.get( ) * (double) n;
		double outerRadius = 2.0 * innerRadius;
		double degreesPerVertex = 2.0 * Math.PI / (double) n;
		
		for( int i = 0; i < n; ++i )
			graph.vertices.add( new Vertex( innerRadius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ), innerRadius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) ) );
		
		for( int i = 0; i < n; ++i )
			graph.vertices.add( new Vertex( outerRadius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ), outerRadius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) ) );
		
		for( int i = 0; i < n; ++i )
			graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( i ), graph.vertices.get( ( i + k ) % n ) ) );
		
		for( int i = 0; i < n; ++i )
			graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( i + n ), graph.vertices.get( ( i + 1 ) % n + n ) ) );
		
		for( int i = 0; i < n; ++i )
			graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( i ), graph.vertices.get( i + n ) ) );
		
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
				return new String[ ] { "<i>Y<sub>n</sub></i> prism graph (for <i>P</i><sub>1,<i>n</i></sub>)" };
			case Generator.Attribute.DESCRIPTION:
				return "Contructs a <i>G<sub>n,k</sub></i> generalized Petersen graph by connecting the <i>i</i>th vertex of a <i>C<sub>n</sub></i> cycle graph to the corresponding <i>i</i>th vertex of a <i>C<sub>n,k</sub></i> circulant graph for all <i>i</i> \u2264 <i>n</i>.</p><p>The circulant graph's vertices are placed according to the user-defined \"Arrange circle radius multiplier\" setting, with the cycle graph's vertices placed along the concentric circle with twice that radius.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[order of outer cycle] [offset]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{1,6}|[3-9])\\s+0*([1-9]\\d{0,6})\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "3 \u2264 <code>order of outer cycle</code> \u2264 9,999,999", "1 \u2264 <code>offset</code> \u2264 <code>floor((order of outer cycle - 1)/2)</code>" };
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
				return new String[ ] { "Circulant graph", "Cycle graph", "Prism graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Generalized Petersen graph";
	}
        
return (Generator) this;
