import java.awt.*;
import java.util.*;
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
		double p = Double.parseDouble( matcher.group( 2 ) );
		
		for( int i = 0; i < n; ++i )
			graph.vertices.add( new Vertex( 0.0, 0.0 ) );
		
		GraphUtilities.arrangeCircle( graph );
		
		Random random = new Random( );
		for( int i = 0; i < n; ++i )
		{
			int start = ( areDirectedEdgesAllowed ? 0 : i );
			for( int j = start; j < n; ++j )
				if( ( i != j || areLoopsAllowed ) && random.nextDouble( ) < p )
					graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( i ), graph.vertices.get( j ) ) );
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
				return "Constructs a <i>G<sub>n,\u03C1</sub></i> random graph using the method introduced by Edgar Gilbert whereby <i>n</i> vertices are connected including each possible edge in the graph with a probability <i>\u03C1</i>.</p><p>Vertices are placed in a circle according to the user-defined \"Arrange circle radius multiplier\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[order] [probability]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{0,6})\\s+0*(1(\\.0+)?|0?\\.\\d{1,10}0*)\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "1 \u2264 <code>order</code> \u2264 9,999,999", "0.0 \u2264 <code>probability</code> \u2264 1.0" };
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
				return new String[ ] { "Random graph (Erd\u0151s–R\u00E9nyi)" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Random graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Random graph (Gilbert)";
	}
        
return (Generator) this;
