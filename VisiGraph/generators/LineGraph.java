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
		
		for( int i = 0; i <= n; ++i )
			graph.vertices.add( new Vertex( 0, 0 ) );
		
		for( int i = 0; i < n; ++i )
		{
				graph.edges.add( new Edge( areDirectedEdgesAllowed, 
						graph.vertices.get( i ), 
						graph.vertices.get( i + 1 ) ) );
		}
		
		LayoutUtilities.arrangeGrid( graph.vertices, 1 );
		
		return graph;
	}
	
	public Object getAttribute( Generator.Attribute attribute )
	{
		switch( attribute )
		{
			case Generator.Attribute.AUTHOR:
				return "William H. Hooper";
			case Generator.Attribute.VERSION:
				return "20110107";
			case Generator.Attribute.ISOMORPHISMS:
				return new String[ ] { };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs a line graph with <i>n + 1</i> vertices and <i>n</i> edges.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[length]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{0,6})\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { 
						"1 \u2264 <code>order</code> \u2264 9,999,999" };
			case Generator.Attribute.ARE_LOOPS_ALLOWED:
				return Generator.BooleanRule.DEFAULT_FALSE;
			case Generator.Attribute.ARE_MULTIPLE_EDGES_ALLOWED:
				return Generator.BooleanRule.DEFAULT_FALSE;
			case Generator.Attribute.ARE_DIRECTED_EDGES_ALLOWED:
				return Generator.BooleanRule.DEFAULT_FALSE;
			case Generator.Attribute.ARE_CYCLES_ALLOWED:
				return Generator.BooleanRule.DEFAULT_FALSE;
			case Generator.Attribute.ARE_PARAMETERS_ALLOWED:
				return true;
			case Generator.Attribute.RELATED_GENERATORS:
				return new String[ ] { };
			case Generator.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Joined graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Line graph";
	}
        
return (Generator) this;
