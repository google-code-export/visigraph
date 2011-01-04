import java.awt.*;
import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.models.generators.*;

	public Graph generate( String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed, Component owner )
	{
		Graph graph = new Graph( UserSettings.instance.defaultGraphName.get( ) + " " + this.toString( ), areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed );
		
		int row = 0;
		ArrayList sets = new ArrayList( );
		for( String sizeString : params.trim( ).split( "\\s+" ) )
		{
			int size = Integer.parseInt( sizeString );
			
			sets.add( new LinkedList( ) );
			for( int i = 0; i < size; ++i )
			{
				Vertex vertex = new Vertex( ( i - ( size / 2 ) + ( size % 2 == 0 ? 0.5 : 0.0 ) ) * UserSettings.instance.arrangeGridSpacing.get( ), 2.0 * row * UserSettings.instance.arrangeGridSpacing.get( ) );
				sets.get( row ).add( vertex );
				graph.vertices.add( vertex );
			}
			
			++row;
		}
		
		for( int i = 0; i < sets.size( ); ++i )
			for( int j = i + 1; j < sets.size( ); ++j )
				for( Vertex from : sets.get( i ) )
					for( Vertex to : sets.get( j ) )
						graph.edges.add( new Edge( false, from, to ) );
		
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
				return new String[ ] { "<i>K\u0305<sub>p</sub></i> empty graph + <i>K\u0305<sub>q</sub></i> empty graph + <i>K\u0305<sub>r</sub></i> empty graph", "<i>K</i><sub>3,<i>p,q,r</i></sub> complete k-partite graph", "<i>C</i><sub>4,<i>r</i></sub> cone graph (for <i>K</i><sub>2,2,<i>r</i></sub>)" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs a <i>K<sub>p,q,r</sub></i> complete tripartite graph by joining the vertices of the empty graphs <i>K\u0305<sub>p</sub></i>, <i>K\u0305<sub>q</sub></i>, and <i>K\u0305<sub>r</sub></i>.</p><p>Vertices are placed according to the user-defined \"Arrange grid spacing\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[order of set A] [order of set B] [order of set C]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{0,6})\\s+0*([1-9]\\d{0,6})\\s+0*([1-9]\\d{0,6})\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "1 \u2264 <code>order of set A</code> \u2264 9,999,999", "1 \u2264 <code>order of set B</code> \u2264 9,999,999", "1 \u2264 <code>order of set C</code> \u2264 9,999,999" };
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
				return new String[ ] { "Complete bipartite graph", "Complete graph", "Complete k-partite graph" };
			case Generator.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Connect completely" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Joined graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Complete tripartite graph";
	}
        
return (Generator) this;
