import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.models.generators.*;

	public Graph generate( String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed, Component owner )
	{
		Graph graph = null;
		
		try
		{
			Pattern pattern = Pattern.compile( getParametersValidatingExpression( ) );
			Matcher matcher = pattern.matcher( params ); matcher.find( );
			Scanner in = new Scanner( params );
			
			// Load the dimensions of the adjacency matrix
			int n = Integer.parseInt( matcher.group( 1 ) );
			
			Scanner in = new Scanner( params.substring( params.indexOf( ':' ) + 1 ) );
			
			// Load the adjacency matrix, itself
			int[ ][ ] mat = new int[n][n];
			for ( int row = 0; row < n; ++row )
				for ( int col = 0; col < n; ++col )
					mat[row][col] = in.nextInt( );
			
			// Determine if the adjacency matrix has loops
			areLoopsAllowed = false;
			for ( int rowCol = 0; rowCol < n && !areLoopsAllowed; ++rowCol )
				if ( mat[rowCol][rowCol] > 0 )
					areLoopsAllowed = true;
			
			// Determine if the adjacency matrix has directed edges
			areDirectedEdgesAllowed = false;
			for ( int row = 0; row < n && !areDirectedEdgesAllowed; ++row )
				for ( int col = 0; col < n && !areDirectedEdgesAllowed; ++col )
					if ( row != col && mat[row][col] != mat[col][row] )
						areDirectedEdgesAllowed = true;
			
			// Determine if the adjacency matrix has multi-edges
			areMultipleEdgesAllowed = false;
			for ( int row = 0; row < n && !areMultipleEdgesAllowed; ++row )
				for ( int col = 0; col < n && !areMultipleEdgesAllowed; ++col )
					if ( mat[row][col] > 1 )
						areMultipleEdgesAllowed = true;
			
			// Initialize the graph
			graph = new Graph( "Untitled graph from adjacency matrix", areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, true );
			
			// Add the vertices
			for ( int i = 0; i < n; ++i )
				graph.vertexes.add( new Vertex( 0, 0, UserSettings.instance.defaultVertexPrefix.get( ) + i ) );
			
			// Go through matrix, adding edges to the graph as we go
			for ( int row = 0; row < n; ++row )
				for ( int col = 0; col < n; ++col )
					if ( areDirectedEdgesAllowed || ( row > col ) || ( row == col && areLoopsAllowed ) )
						for ( int i = 0; i < mat[row][col]; ++i )
							graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertexes.get( row ), graph.vertexes.get( col ) ) );
			
			// Clean up the graph a bit
			GraphUtilities.arrangeCircle( graph );
		}
		catch ( Exception ex )
		{
			if ( ex.getMessage( ) == null )
				JOptionPane.showMessageDialog( null, "An unexpected exception occurred while importing the specified data.", "Invalid data format!", JOptionPane.ERROR_MESSAGE );
			else
				JOptionPane.showMessageDialog( null, "An exception occurred while constructing the graph for the specified data:\n     " + ex.getMessage( ), "Invalid data format!", JOptionPane.ERROR_MESSAGE );
			
			return null;
		}
		
		return graph;
	}

	public String toString                          ( ) { return "From adjacency matrix...";                                                                  }
    public String getParametersDescription          ( ) { return "[vertices] : [0,0] [0,1] [0,2] [...] [1,0] [1,1] [1,2] [...] [2,0] [2,1] [2,2] [... etc.]"; }
    public String getParametersValidatingExpression ( ) { return "^\\s*(\\d+)\\s*:\\s*\\d+(\\s+\\d+)*\\s*$";                                                  }
    
	public GeneratorBase.BooleanRule areLoopsAllowed         ( ) { return GeneratorBase.BooleanRule.ForcedTrue; }
	public GeneratorBase.BooleanRule areDirectedEdgesAllowed ( ) { return GeneratorBase.BooleanRule.ForcedTrue; }
	public GeneratorBase.BooleanRule areMultipleEdgesAllowed ( ) { return GeneratorBase.BooleanRule.ForcedTrue; }
	public GeneratorBase.BooleanRule areCyclesAllowed        ( ) { return GeneratorBase.BooleanRule.ForcedTrue; }
	public GeneratorBase.BooleanRule areParametersAllowed    ( ) { return GeneratorBase.BooleanRule.ForcedTrue; }

return (GeneratorBase) this;
