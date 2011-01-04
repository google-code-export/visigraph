import java.awt.*;
import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.models.generators.*;

	public Graph generate( String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed, Component owner )
	{
		Graph graph = new Graph( UserSettings.instance.defaultGraphName.get( ) + " " + this.toString( ), areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed );
		Pattern pattern = Pattern.compile( (String) this.getAttribute( Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION ) );
		Matcher matcher = pattern.matcher( params );
		matcher.find( );
		
		int n = Integer.parseInt( matcher.group( 1 ) );
		
		for( int i = 0; i < n; ++i )
			graph.vertices.add( new Vertex( 0.0, 0.0 ) );
		
		GraphUtilities.arrangeCircle( graph );
		
		for( int i = 0; i < n; ++i )
			graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( i ), graph.vertices.get( ( i + 1 ) % n ) ) );
		
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
				return new String[ ] { "<i>C</i><sub><i>n</i>,1</sub> circulant graph", "<i>K</i><sub>3</sub> complete graph for (for <i>C</i><sub>3</sub>)", "<i>K</i><sub>3,1</sub> bipartite Kneser graph (for <i>C</i><sub>6</sub>)", "<i>H</i><sub>2</sub> Hadamard graph (for <i>C</i><sub>8</sub>)", "<i>H</i><sub>2^(<i>n</i>-1)+1</sub> Haar graph (for <i>C</i><sub>2<i>n</i></sub>)" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs a <i>C<sub>n</sub></i> cycle graph by connecting <i>n</i> vertices consecutively in a single cycle.</p><p>Vertices are placed according to the user-defined \"Arrange circle radius multiplier\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[order]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{1,6}|[3-9])\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "3 \u2264 <code>order</code> \u2264 9,999,999" };
			case Generator.Attribute.ARE_LOOPS_ALLOWED:
				return Generator.BooleanRule.DEFAULT_FALSE;
			case Generator.Attribute.ARE_MULTIPLE_EDGES_ALLOWED:
				return Generator.BooleanRule.DEFAULT_FALSE;
			case Generator.Attribute.ARE_DIRECTED_EDGES_ALLOWED:
				return Generator.BooleanRule.DEFAULT_FALSE;
			case Generator.Attribute.ARE_CYCLES_ALLOWED:
				return Generator.BooleanRule.FORCED_TRUE;
			case Generator.Attribute.ARE_PARAMETERS_ALLOWED:
				return true;
			case Generator.Attribute.RELATED_GENERATORS:
				return new String[ ] { "Cartesian product of a complete bipartite graph and cycle (Behar)", "Cartesian product of a complete bipartite graph and cycle (Scott)", "Circulant graph", "Cone graph", "Friendship graph", "Gear graph", "Helm graph", "Lollipop graph", "Pan graph", "Prism graph", "Web graph", "Wheel graph" };
			case Generator.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Is cyclic" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Cycle graph";
	}
        
return (Generator) this;
