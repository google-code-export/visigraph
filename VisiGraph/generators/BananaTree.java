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
		int k = Integer.parseInt( matcher.group( 2 ) ) - 1;
		double radius = UserSettings.instance.arrangeCircleRadiusMultiplier.get( ) * (double) k;
		double degreesPerVertex = 2.0 * Math.PI / (double) k;
		double bundleSpacing = 3.0 * radius;
		
		LinkedList firstBananas = new LinkedList( );
		
		for( int i = 0; i < n; ++i )
		{
			Vertex hub = new Vertex( 0.0 + i * bundleSpacing, 0.0 );
			graph.vertices.add( hub );
			
			for( int j = 0; j < k; ++j )
			{
				Vertex banana = new Vertex( radius * Math.cos( degreesPerVertex * j - Math.PI / 2.0 ) + i * bundleSpacing, radius * Math.sin( degreesPerVertex * j - Math.PI / 2.0 ) );
				graph.vertices.add( banana );
				graph.edges.add( new Edge( false, hub, banana ) );
				if( j == 0 )
					firstBananas.add( banana );
			}
		}
		
		Vertex root = new Vertex( firstBananas.getFirst( ).x.get( ) + ( firstBananas.getLast( ).x.get( ) - firstBananas.getFirst( ).x.get( ) ) / 2.0, -bundleSpacing );
		graph.vertices.add( root );
		
		for( Vertex banana : firstBananas )
			graph.edges.add( new Edge( false, root, banana ) );
		
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
			case Generator.Attribute.DESCRIPTION:
				return "Constructs a <i>B<sub>n,k</sub></i> banana tree by connecting one leaf of each of <i>n</i> copies of an <i>S<sub>k</sub></i> star graph with a single root vertex that is distinct from all the stars.</p><p>Each bundle's vertices are placed according to the user-defined \"Arrange circle radius multiplier\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[bundles] [bananas per bundle]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{1,6}|[3-9])\\s+0*([1-9]\\d{1,6}|[3-9])\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "3 \u2264 <code>bundles</code> \u2264 9,999,999", "3 \u2264 <code>bananas per bundle</code> \u2264 9,999,999" };
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
				return new String[ ] { "Firecracker graph", "Star graph" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Bridged graph", "Tree" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Banana tree";
	}
	
return (Generator) this;
