/**
 * EmptyGraph.java
 */
package edu.belmont.mth.visigraph.models.generators;

import java.awt.*;
import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;

/**
 * @author Cameron Behar
 */
public class EmptyGraph implements Generator
{
	public Graph generate( String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed, Component owner )
	{
		Graph graph = new Graph( UserSettings.instance.defaultGraphName.get( ) + " " + this.toString( ), areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed );
		Pattern pattern = Pattern.compile( (String) this.getAttribute( Attribute.PARAMETERS_VALIDATION_EXPRESSION ) );
		Matcher matcher = pattern.matcher( params );
		matcher.find( );
		
		if( matcher.group( 1 ) != null )
			for( int i = 0; i < Integer.parseInt( matcher.group( 1 ) ); ++i )
				graph.vertices.add( new Vertex( 0.0, 0.0 ) );
		
		GraphUtilities.arrangeGrid( graph );
		
		return graph;
	}
	
	public Object getAttribute( Attribute attribute )
	{
		switch( attribute )
		{
			case AUTHOR:
				return "Cameron Behar";
			case VERSION:
				return "20110101";
			case ISOMORPHISMS:
				return new String[ ] { "<i>K\u0305<sub>n</sub></i> complete graph", "Null graph (for <i>K\u0305</i><sub>0</sub>)", "Singleton graph (for <i>K\u0305</i><sub>1</sub>)" };
			case DESCRIPTION:
				return "Constructs a <i>K\u0305<sub>n</sub></i> empty graph by adding <i>n</i> vertices but no edges to the null graph.";
			case PARAMETERS_DESCRIPTION:
				return "[order (optional)]";
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
				return new String[ ] { "Complete graph", "Null graph", "Singleton graph" };
			default:
				return null;
		}
	}
	
	@Override
	public String toString( )
	{
		return "(Empty graph)";
	}
}
