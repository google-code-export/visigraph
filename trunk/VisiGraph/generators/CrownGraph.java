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
		
		for( int j = 0; j < n; ++j )
			graph.vertices.add( new Vertex( ( j - ( n / 2 ) ) * UserSettings.instance.arrangeGridSpacing.get( ), -UserSettings.instance.arrangeGridSpacing.get( ) ) );
		
		for( int j = 0; j < n; ++j )
			graph.vertices.add( new Vertex( ( j - ( n / 2 ) ) * UserSettings.instance.arrangeGridSpacing.get( ), UserSettings.instance.arrangeGridSpacing.get( ) ) );
		
		for( int j = 0; j < n; ++j )
			for( int k = 0; k < n; ++k )
				if( j != k )
					graph.edges.add( new Edge( false, graph.vertices.get( j ), graph.vertices.get( k + n ) ) );
		
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
				return new String[ ] { "<i>H</i><sub>3\u00B72^(<i>n</i>-2)-1</sub> Haar graph" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs a <i>C<sub>n</sub></i> crown graph by removing all vertical edges from a <i>K<sub>n,n</sub></i> complete bipartite graph.</p><p>Vertices are placed according to the user-defined \"Arrange grid spacing\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[order of each set]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{1,6}|[3-9])\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "1 \u2264 <code>order of each set</code> \u2264 9,999,999" };
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
				return new String[ ] { "Complete bipartite graph" };
			case Generator.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Is bipartite", "Two-color vertices" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Crown graph";
	}
        
return (Generator) this;
