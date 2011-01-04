import java.awt.*;
import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.models.generators.*;

	public Vertex buildTree( Graph g, int level, int fanOut, double x, double y )
	{
		Vertex root = new Vertex( x, y );
		g.vertices.add( root );
		
		if( level > 0 )
		{
			double radiansPerBranch = ( 2 * Math.PI ) / (double) fanOut;
			
			for( int i = 0; i < fanOut; ++i )
			{
				Vertex branch = this.buildTree( g, level - 1, fanOut, x + 100.0 * Math.pow( level, 1.3 ) * Math.cos( radiansPerBranch * i ), y + 100.0 * Math.pow( level, 1.3 ) * Math.sin( radiansPerBranch * i ) );
				g.edges.add( new Edge( false, root, branch ) );
			}
		}
		
		return root;
	}
	
	public Graph generate( String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed, Component owner )
	{
		Graph graph = new Graph( UserSettings.instance.defaultGraphName.get( ) + " " + this.toString( ), areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed );
		Pattern pattern = Pattern.compile( (String) this.getAttribute( Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION ) );
		Matcher matcher = pattern.matcher( params );
		matcher.find( );
		
		int levelCount = Integer.parseInt( matcher.group( 1 ) );
		int fanOut = Integer.parseInt( matcher.group( 2 ) );
		
		this.buildTree( graph, levelCount, fanOut, 0.0, 0.0 );
		
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
				return new String[ ] { "<i>P<sub>n</sub></i> path graph (for <i>T</i><sub><i>n</i>-1,1</sub>)", "<i>S<sub>n</sub></i> star graph (for <i>S</i><sub>1,<i>n</i>-1</sub>)" };
			case Generator.Attribute.DESCRIPTION:
				return "Constructs a <i>T<sub>m,n</sub></i> symmetric tree by recursively connecting <i>n</i> pendants to each leaf of a <i>T</i><sub><i>m</i>-1,<i>n</i></sub> symmetric tree, where <i>T</i><sub>0,<i>n</i></sub> is the singleton graph.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[levels] [fan-out]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]|10)\\s*0*([1-9]\\d{0,6})\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "1 \u2264 <code>levels</code> \u2264 10", "1 \u2264 <code>fan-out</code> \u2264 9,999,999" };
			case Generator.Attribute.ARE_LOOPS_ALLOWED:
				return Generator.BooleanRule.FORCED_FALSE;
			case Generator.Attribute.ARE_MULTIPLE_EDGES_ALLOWED:
				return Generator.BooleanRule.FORCED_FALSE;
			case Generator.Attribute.ARE_DIRECTED_EDGES_ALLOWED:
				return Generator.BooleanRule.FORCED_FALSE;
			case Generator.Attribute.ARE_CYCLES_ALLOWED:
				return Generator.BooleanRule.FORCED_FALSE;
			case Generator.Attribute.ARE_PARAMETERS_ALLOWED:
				return true;
			case Generator.Attribute.RELATED_GENERATORS:
				return new String[ ] { "Path graph", "Star graph" };
			case Generator.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Add pendants" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Bridged graph", "Tree" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Symmetric tree";
	}
        
return (Generator) this;
