import java.awt.*;
import javax.swing.*;
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
		
		for( String offsetString : matcher.group( 2 ).trim( ).split( "\\s+" ) )
		{
			int offset = Integer.parseInt( offsetString );
			if( offset > n / 2 )
			{
				JOptionPane.showMessageDialog( owner, "Offset must be \u2264 floor(order / 2)!", "Invalid offset!", JOptionPane.ERROR_MESSAGE );
				return null;
			}
			
			for( int i = 0; i < n; ++i )
				graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( i ), graph.vertices.get( ( i + offset ) % n ) ) );
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
				return new String[ ] { "<i>A<sub>n</sub></i> antiprism graph (for <i>C</i><sub>2<i>n</i>,1,2</sub>)", "<i>K<sub>n,n</sub></i> complete bipartite graph (for <i>C</i><sub>n,1,3,...,2\u00B7<code>floor(<i>n</i>/2)</code>+1</sub>)", "<i>K<sub>n</sub></i> complete graph (for <i>C</i><sub><i>n</i>,1,2,...,<code>floor(<i>n</i>/2)</code></sub>)", "<i>C<sub>n</sub></i> cycle graph (for <i>C</i><sub><i>n</i>,1</sub>)", "<i>M<sub>n</sub></i> Möbius ladder graph (for <i>C</i><sub>2<i>n</i>,1,<i>n</i></sub>)", "<i>Y<sub>n</sub></i> prism graph (for <i>C</i><sub>2<i>n</i>,2,<i>n</i></sub>)" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs a <i>C<sub>n,l</sub></i> circulant graph by connecting the <i>i</i>th vertex of <i>n</i> vertices to the (<i>i</i> + <i>j</i>)th and (<i>i</i> - <i>j</i>)th vertices (mod <i>n</i>) for each <i>j</i> in a list <i>l</i>.</p><p>Vertices are placed according to the user-defined \"Arrange circle radius multiplier\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[order] : [offset 0] [offset 1] [offset 2] ...";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{1,6}|[3-9])\\s*:(\\s*0*([1-9]\\d{0,6})(\\s+0*([1-9]\\d{0,6}))*)\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "3 \u2264 <code>order</code> \u2264 9,999,999", "1 \u2264 <code>offset</code> \u2264 <code>floor(order / 2)</code>" };
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
				return new String[ ] { "Antiprism graph", "Complete bipartite graph", "Complete graph", "Cycle graph", "Möbius ladder graph", "Prism graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Circulant graph";
	}
        
return (Generator) this;
