import java.awt.*;
import java.util.*;
import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.models.generators.*;

	class VertexPair
	{
		Vertex	from, to;
		
		public VertexPair( Vertex from, Vertex to )
		{
			this.from = from;
			this.to = to;
		}
	}
	
	public Graph generate( String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed, Component owner )
	{
		Graph graph = new Graph( UserSettings.instance.defaultGraphName.get( ) + " " + this.toString( ), areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed );
		Pattern pattern = Pattern.compile( (String) this.getAttribute( Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION ) );
		Matcher matcher = pattern.matcher( params );
		matcher.find( );
		
		int order = Integer.parseInt( matcher.group( 1 ) );
		int size = Integer.parseInt( matcher.group( 2 ) );
		
		for( int i = 0; i < order; ++i )
			graph.vertices.add( new Vertex( 0.0, 0.0 ) );
		
		GraphUtilities.arrangeCircle( graph );
		
		LinkedList possibleEdges = new LinkedList( );
		for( int i = 0; i < order; ++i )
		{
			int start = ( areDirectedEdgesAllowed ? 0 : i );
			for( int j = start; j < order; ++j )
				if( i != j || areLoopsAllowed )
					possibleEdges.add( new VertexPair( graph.vertices.get( i ), graph.vertices.get( j ) ) );
		}
		
		if( !areMultipleEdgesAllowed && size > possibleEdges.size( ) )
			size = possibleEdges.size( );
		
		Random random = new Random( );
		for( int i = 0; i < size; ++i )
		{
			int index = random.nextInt( possibleEdges.size( ) );
			VertexPair pair = ( areMultipleEdgesAllowed ? possibleEdges.get( index ) : possibleEdges.remove( index ) );
			graph.edges.add( new Edge( areDirectedEdgesAllowed, pair.from, pair.to ) );
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
			case Generator.Attribute.DESCRIPTION:
				return "Constructs a <i>G<sub>m,n</sub></i> random graph using the method introduced by Paul Erd\u0151s and Alfr\u00E9d R\u00E9nyi whereby <i>m</i> vertices are connected by <i>n</i> randomly-chosen edges.</p><p>Vertices are placed in a circle according to the user-defined \"Arrange circle radius multiplier\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[order] [size]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{0,6})\\s+0*([1-9]\\d{0,6})\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "1 \u2264 <code>order</code> \u2264 9,999,999", "1 \u2264 <code>size</code> \u2264 9,999,999; truncated if > # of possible edges and multiple edges are disallowed" };
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
				return new String[ ] { "Random graph (Gilbert)" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Random graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Random graph (Erd\u0151s–R\u00E9nyi)";
	}
    
return (Generator) this;
