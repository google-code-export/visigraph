import java.awt.*;
import java.util.*;
import java.util.regex.*;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.models.generators.*;

	public Graph generate( String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed, Component owner )
	{
		Graph graph = new Graph( UserSettings.instance.defaultGraphName.get( ) + " " + toString( ), areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed );
		Pattern pattern = Pattern.compile( getParametersValidatingExpression( ) );
		Matcher matcher = pattern.matcher( params );
		matcher.find( );
		
		int bits = Integer.parseInt( matcher.group( 1 ) );
		
		int rows = 1 << ( bits - 1 );
		int cols = bits;
		
		int[ ] multipliers = new int[cols];
		
		multipliers[0] = 0;
		for ( int col = 1; col < cols; ++col )
			multipliers[col] = ( 1 << ( cols - col - 1 ) ) + multipliers[col - 1];
		
		for ( int row = 0; row < rows; ++row )
			for ( int col = 0; col < cols; ++col )
				graph.vertexes.add( new Vertex( UserSettings.instance.arrangeGridSpacing.get( ) * multipliers[col], UserSettings.instance.arrangeGridSpacing.get( ) * row ) );
		
		LinkedList queue = new LinkedList( );
		queue.addLast( new VertexRange( 0, 0, rows ) );
		
		while ( !queue.isEmpty( ) )
		{
			VertexRange range = queue.pop( );
			
			for ( int i = 0; i < range.height; ++i )
				graph.edges.add( new Edge( true, graph.vertexes.get( ( range.row + i ) * cols + range.col % cols ), graph.vertexes.get( ( range.row + i ) * cols + ( range.col + 1 ) % cols ) ) );
			
			for ( int i = 0; i < range.height; ++i )
				graph.edges.add( new Edge( true, graph.vertexes.get( ( range.row + i ) * cols + range.col % cols ), graph.vertexes.get( ( range.row + ( i + range.height / 2 ) % range.height ) * cols + ( range.col + 1 ) % cols ) ) );
			
			if ( range.height > 2 )
			{
				queue.addLast( new VertexRange( range.row, range.col + 1, range.height / 2 ) );
				queue.addLast( new VertexRange( range.row + range.height / 2, range.col + 1, range.height / 2 ) );
			}
		}
		
		return graph;
    }
    
	class VertexRange
	{
		int row, col, height;
		
		public VertexRange ( int row, int col, int height )
		{
			this.row = row;
			this.col = col;
			this.height = height;
		}
	}
	
    public String toString                          ( ) { return "Butterfly network";        }
    public String getParametersDescription          ( ) { return "[bits]";                   }
    public String getParametersValidatingExpression ( ) { return "^\\s*0*([2-9]|1\\d)\\s*$"; }
    
	public GeneratorBase.BooleanRule areLoopsAllowed         ( ) { return GeneratorBase.BooleanRule.DefaultFalse; }
	public GeneratorBase.BooleanRule areDirectedEdgesAllowed ( ) { return GeneratorBase.BooleanRule.ForcedTrue;   }
	public GeneratorBase.BooleanRule areMultipleEdgesAllowed ( ) { return GeneratorBase.BooleanRule.DefaultFalse; }
	public GeneratorBase.BooleanRule areCyclesAllowed        ( ) { return GeneratorBase.BooleanRule.DefaultFalse; }
	public GeneratorBase.BooleanRule areParametersAllowed    ( ) { return GeneratorBase.BooleanRule.ForcedTrue;   }
        
return (GeneratorBase) this;
