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
		int k = Integer.parseInt( matcher.group( 2 ) ) - 1;
		double radius = UserSettings.instance.arrangeCircleRadiusMultiplier.get( ) * (double) k;
		double degreesPerVertex = 2.0 * Math.PI / (double) k;
		double bundleSpacing = 3.0 * radius;
		
		LinkedList firstFirecrackers = new LinkedList( );
		
		for( int i = 0; i < n; ++i )
		{
			Vertex hub = new Vertex( 0.0 + i * bundleSpacing, 0.0 );
			graph.vertices.add( hub );
			
			for( int j = 0; j < k; ++j )
			{
				Vertex firecracker = new Vertex( radius * Math.cos( degreesPerVertex * j - Math.PI / 2.0 ) + i * bundleSpacing, radius * Math.sin( degreesPerVertex * j - Math.PI / 2.0 ) );
				graph.vertices.add( firecracker );
				graph.edges.add( new Edge( false, hub, firecracker ) );
				if( j == 0 )
					firstFirecrackers.add( firecracker );
			}
		}
		
		for( int i = 0; i < firstFirecrackers.size( ) - 1; ++i )
			graph.edges.add( new Edge( false, (Vertex) firstFirecrackers.get( i ), (Vertex) firstFirecrackers.get( i + 1 ) ) );
		
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
				return new String[ ] { "<i>C<sub>n</sub></i> centipede tree (for <i>F</i><sub><i>n</i>,2</sub>)" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs an <i>F<sub>n,k</sub></i> firecracker graph by connecting one vertex of each of <i>n</i> copies of an <i>S<sub>k</sub></i> star graph in a row with edges.</p><p>Each bundle's vertices are placed according to the user-defined \"Arrange circle radius multiplier\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[bundles] [crackers]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{1,6}|[2-9])\\s+0*([1-9]\\d{1,6}|[2-9])\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "2 \u2264 <code>bundles</code> \u2264 9,999,999", "2 \u2264 <code>crackers</code> \u2264 9,999,999" };
			case Generator.Attribute.ARE_LOOPS_ALLOWED:
				return Generator.BooleanRule.DEFAULT_FALSE;
			case Generator.Attribute.ARE_MULTIPLE_EDGES_ALLOWED:
				return Generator.BooleanRule.DEFAULT_FALSE;
			case Generator.Attribute.ARE_DIRECTED_EDGES_ALLOWED:
				return Generator.BooleanRule.FORCED_FALSE;
			case Generator.Attribute.ARE_CYCLES_ALLOWED:
				return Generator.BooleanRule.DEFAULT_FALSE;
			case Generator.Attribute.ARE_PARAMETERS_ALLOWED:
				return true;
			case Generator.Attribute.RELATED_GENERATORS:
				return new String[ ] { "Banana tree", "Centipede tree", "Star graph" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Bridged graph", "Tree" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Firecracker graph";
	}
    
return (Generator) this;
