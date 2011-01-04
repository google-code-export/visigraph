import java.awt.*;
import java.util.*;
import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.models.generators.*;

	class VertexRange
	{
		int	row, col, height;
		
		public VertexRange( int row, int col, int height )
		{
			this.row = row;
			this.col = col;
			this.height = height;
		}
	}
	
	public Graph generate( String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed, Component owner )
	{
		Graph graph = new Graph( UserSettings.instance.defaultGraphName.get( ) + " " + this.toString( ), areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed );
		Pattern pattern = Pattern.compile( (String) this.getAttribute( Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION ) );
		Matcher matcher = pattern.matcher( params );
		matcher.find( );
		
		int cols = Integer.parseInt( matcher.group( 1 ) );
		int rows = 1 << ( cols - 1 );
		
		int[ ] multipliers = new int[cols];
		
		multipliers[0] = 0;
		for( int col = 1; col < cols; ++col )
			multipliers[col] = ( 1 << ( cols - col - 1 ) ) + multipliers[col - 1];
		
		for( int row = 0; row < rows; ++row )
			for( int col = 0; col < cols; ++col )
				graph.vertices.add( new Vertex( UserSettings.instance.arrangeGridSpacing.get( ) * multipliers[col], UserSettings.instance.arrangeGridSpacing.get( ) * row ) );
		
		LinkedList queue = new LinkedList( );
		queue.addLast( new VertexRange( 0, 0, rows ) );
		
		while( !queue.isEmpty( ) )
		{
			VertexRange range = queue.pop( );
			
			for( int i = 0; i < range.height; ++i )
				graph.edges.add( new Edge( true, graph.vertices.get( ( range.row + i ) * cols + range.col % cols ), graph.vertices.get( ( range.row + i ) * cols + ( range.col + 1 ) % cols ) ) );
			
			for( int i = 0; i < range.height; ++i )
				graph.edges.add( new Edge( true, graph.vertices.get( ( range.row + i ) * cols + range.col % cols ), graph.vertices.get( ( range.row + ( i + range.height / 2 ) % range.height ) * cols + ( range.col + 1 ) % cols ) ) );
			
			if( range.height > 2 )
			{
				queue.addLast( new VertexRange( range.row, range.col + 1, range.height / 2 ) );
				queue.addLast( new VertexRange( range.row + range.height / 2, range.col + 1, range.height / 2 ) );
			}
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
				return "Constructs a <i>B<sub>n</sub></i> butterfly graph whose vertices are pairs (<i>w</i>, <i>i</i>) where <i>b</i> is a binary string of length <i>n</i> and <i>i</i> is an integer in the range 0 to <i>n</i> and with directed edges from vertex (<i>w</i>, <i>i</i>) to (<i>w</i>', <i>i</i> + 1) iff <i>w</i>' is identical to <i>w</i> in all bits with the possible exception of the (<i>i</i> + 1)th bit counted from the <i>left</i>.</p><p>Vertices are placed according to the user-defined \"Arrange grid spacing\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[bits]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([2-9]|1\\d|20)\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "2 \u2264 <code>bits</code> \u2264 20" };
			case Generator.Attribute.ARE_LOOPS_ALLOWED:
				return Generator.BooleanRule.DEFAULT_FALSE;
			case Generator.Attribute.ARE_MULTIPLE_EDGES_ALLOWED:
				return Generator.BooleanRule.DEFAULT_FALSE;
			case Generator.Attribute.ARE_DIRECTED_EDGES_ALLOWED:
				return Generator.BooleanRule.FORCED_TRUE;
			case Generator.Attribute.ARE_CYCLES_ALLOWED:
				return Generator.BooleanRule.DEFAULT_FALSE;
			case Generator.Attribute.ARE_PARAMETERS_ALLOWED:
				return true;
			case Generator.Attribute.TAGS:
				return new String[ ] { "Directed graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Butterfly network";
	}
        
return (Generator) this;
