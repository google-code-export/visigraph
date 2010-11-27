import java.io.*;
import java.awt.*;
import java.util.*;
import java.util.List;
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
			
			// Load the dimensions of the incidence matrix
			int rows = Integer.parseInt( matcher.group( 1 ) );
			int cols = Integer.parseInt( matcher.group( 2 ) );
			
			Scanner in = new Scanner( params.substring( params.indexOf( ':' ) + 1 ) );
			
			// Load the incidence matrix, itself
			int[ ][ ] mat = new int[rows][cols];
			for ( int row = 0; row < rows; ++row )
				for ( int col = 0; col < cols; ++col )
					mat[row][col] = in.nextInt( );
			
			in.close( );
			
			// Determine if the incidence matrix has any loops, multi-edges, orphaned edges, or hyper-edges
			areLoopsAllowed = false;
			areMultipleEdgesAllowed = false;
			Set colStrings = new HashSet( );
			for ( int col = 0; col < cols; ++col )
			{
				int incidentVertexCount = 0;
				StringBuilder colString = new StringBuilder( rows );
				for ( int row = 0; row < rows; ++row )
				{
					incidentVertexCount += mat[row][col];
					colString.append( mat[row][col] + " " );
					if ( mat[row][col] == 2 )
						areLoopsAllowed = true;
					else if ( mat[row][col] < 0 )
						throw new Exception( "Incidence matrix contains a negative count at row #" + row + " column #" + col + "." );
				}
				
				switch ( incidentVertexCount )
				{
					case 0:  throw new Exception( "Incidence matrix contains a disconnected edge at column #" + col + "." );
					case 1:  throw new Exception( "Incidence matrix contains an orphaned edge at column #" + col + "." );
					case 2:  break;
					default: throw new Exception( "Incidence matrix contains a hyper-edge at column #" + col + "." );
				}
				
				int oldSetSize = colStrings.size( );
				colStrings.add( colString.toString( ) );
				if ( colStrings.size( ) == oldSetSize )
					areMultipleEdgesAllowed = true;
			}
			
			// Initialize the graph
			graph = new Graph( "Untitled graph from incidence matrix", areLoopsAllowed, false, areMultipleEdgesAllowed, true );
			
			// Add the vertices
			for ( int i = 0; i < rows; ++i )
				graph.vertexes.add( new Vertex( 0, 0, UserSettings.instance.defaultVertexPrefix.get( ) + i ) );
			
			// Go through the columns, adding edges to the graph as we go
			for ( int col = 0; col < cols; ++col )
			{
				List incidentVertexes = new ArrayList( 2 );
				for ( int row = 0; row < rows; ++row )
					for ( int i = 0; i < mat[row][col]; ++i )
						incidentVertexes.add( graph.vertexes.get( row ) );
				
				Edge newEdge = new Edge( false, incidentVertexes.get( 0 ), incidentVertexes.get( 1 ) );
				newEdge.label.set( UserSettings.instance.defaultEdgePrefix.get( ) + col );
				graph.edges.add( newEdge );
			}
			
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

	public String toString                          ( ) { return "From incidence matrix..."; }
    public String getParametersDescription          ( ) { return "[vertices] [edges] : [0,0] [0,1] [0,2] [...] [1,0] [1,1] [1,2] [...] [2,0] [2,1] [2,2] [... etc.]"; }
    public String getParametersValidatingExpression ( ) { return "^\\s*(\\d+)\\s+(\\d+)\\s*:\\s*\\d+(\\s+\\d+)*\\s*$"; }
    
	public GeneratorBase.BooleanRule areLoopsAllowed         ( ) { return GeneratorBase.BooleanRule.ForcedTrue; }
	public GeneratorBase.BooleanRule areDirectedEdgesAllowed ( ) { return GeneratorBase.BooleanRule.ForcedTrue; }
	public GeneratorBase.BooleanRule areMultipleEdgesAllowed ( ) { return GeneratorBase.BooleanRule.ForcedTrue; }
	public GeneratorBase.BooleanRule areCyclesAllowed        ( ) { return GeneratorBase.BooleanRule.ForcedTrue; }
	public GeneratorBase.BooleanRule areParametersAllowed    ( ) { return GeneratorBase.BooleanRule.ForcedTrue; }

return (GeneratorBase)this;
