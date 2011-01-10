/**
 * LineGraph.java
 */
package edu.belmont.mth.visigraph.models.generators;

import java.awt.*;
import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.LayoutUtilities;

/**
 * @author William H. Hooper
 *         A simple example of an embedded (rather than interpreted)
 *         graph generator. To add this to the New Graph... menu,
 *         edit GeneratorService.java and add the generator below
 *         the line:
 *         // --add embedded generators here--
 */
public class LineGraph implements Generator
{
	@Override
	public Graph generate( String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed, Component owner )
	{
		Graph graph = new Graph( UserSettings.instance.defaultGraphName.get( ) + " " + this.toString( ), areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed );
		Pattern pattern = Pattern.compile( (String) this.getAttribute( Attribute.PARAMETERS_VALIDATION_EXPRESSION ) );
		Matcher matcher = pattern.matcher( params );
		matcher.find( );
		
		if( matcher.group( 1 ) != null )
		{
			int length = Integer.parseInt( matcher.group( 1 ) );
			for( int i = 0; i <= length; ++i )
				graph.vertices.add( new Vertex( 0, 0 ) );
			for( int i = 0; i < length; ++i )
			{
				graph.edges.add( new Edge( false, graph.vertices.get( i ), graph.vertices.get( i + 1 ) ) );
			}
		}
		
		LayoutUtilities.arrangeGrid( graph.vertices, 1 );
		
		return graph;
	}
	
	@Override
	public Object getAttribute( Attribute attribute )
	{
		switch( attribute )
		{
			case AUTHOR:
				return "William H. Hooper";
			case VERSION:
				return "20110107";
			case ISOMORPHISMS:
				return new String[ ] { };
			case DESCRIPTION:
				return "Constructs a graph by adding <i>n + 1</i> vertices and <i>n</i> edges to the null graph.";
			case PARAMETERS_DESCRIPTION:
				return "[length]";
			case PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*(\\d{1,7})?\\s*$";
			case CONSTRAINTS:
				return new String[ ] { "0 \u2264 <code>order</code> \u2264 9,999,999" };
			case ARE_LOOPS_ALLOWED:
				return BooleanRule.DEFAULT_FALSE;
			case ARE_MULTIPLE_EDGES_ALLOWED:
				return BooleanRule.DEFAULT_FALSE;
			case ARE_DIRECTED_EDGES_ALLOWED:
				return BooleanRule.DEFAULT_FALSE;
			case ARE_CYCLES_ALLOWED:
				return BooleanRule.DEFAULT_TRUE;
			case ARE_PARAMETERS_ALLOWED:
				return true;
			case RELATED_GENERATORS:
				return new String[ ] { "Empty graph", "Complete graph", "Null graph", "Singleton graph" };
			default:
				return null;
		}
	}
	
	@Override
	public String toString( )
	{
		return "(Line graph)";
	}
}
