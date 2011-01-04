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
			Pattern pattern = Pattern.compile( (String) this.getAttribute( Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION ) );
			Matcher matcher = pattern.matcher( params );
			matcher.find( );
			
			// Load the dimensions of the adjacency matrix
			int n = Integer.parseInt( matcher.group( 1 ) );
			String[ ] values = matcher.group( 2 ).split( "\\s+" );
			
			// Simple error-checking
			if( values.length != n * n )
				throw new Exception( "Adjacency matrix must be square with dimensions corresponding to the specified number of vertices." );
			
			// Load the adjacency matrix, itself
			int[ ][ ] matrix = new int[n][n];
			for( int row = 0; row < n; ++row )
				for( int col = 0; col < n; ++col )
					matrix[row][col] = Integer.parseInt( values[n * row + col] );
			
			// Determine if the adjacency matrix has loops
			areLoopsAllowed = false;
			for( int rowCol = 0; rowCol < n && !areLoopsAllowed; ++rowCol )
				if( matrix[rowCol][rowCol] > 0 )
					areLoopsAllowed = true;
			
			// Determine if the adjacency matrix has directed edges
			areDirectedEdgesAllowed = false;
			for( int row = 0; row < n && !areDirectedEdgesAllowed; ++row )
				for( int col = 0; col < n && !areDirectedEdgesAllowed; ++col )
					if( row != col && matrix[row][col] != matrix[col][row] )
						areDirectedEdgesAllowed = true;
			
			// Determine if the adjacency matrix has multi-edges
			areMultipleEdgesAllowed = false;
			for( int row = 0; row < n && !areMultipleEdgesAllowed; ++row )
				for( int col = 0; col < n && !areMultipleEdgesAllowed; ++col )
					if( matrix[row][col] > 1 )
						areMultipleEdgesAllowed = true;
			
			// Initialize the graph
			graph = new Graph( "Untitled graph from adjacency matrix", areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, true );
			
			// Add the vertices
			for( int i = 0; i < n; ++i )
				graph.vertices.add( new Vertex( 0, 0, UserSettings.instance.defaultVertexPrefix.get( ) + i ) );
			
			// Go through matrix, adding edges to the graph as we go
			for( int row = 0; row < n; ++row )
			{
				int start = ( areDirectedEdgesAllowed ? 0 : row + ( areLoopsAllowed ? 0 : 1 ) );
				for( int col = start; col < n; ++col )
					for( int i = 0; i < matrix[row][col]; ++i )
						graph.edges.add( new Edge( areDirectedEdgesAllowed, graph.vertices.get( row ), graph.vertices.get( col ) ) );
			}			
			
			// Clean the graph up a bit
			GraphUtilities.arrangeCircle( graph );
		}
		catch( Exception ex )
		{
			if( ex.getMessage( ) == null )
				JOptionPane.showMessageDialog( owner, "An unexpected exception occurred while importing the specified data.", "Invalid data format!", JOptionPane.ERROR_MESSAGE );
			else
				JOptionPane.showMessageDialog( owner, "An exception occurred while constructing the graph for the specified data:\n     " + ex.getMessage( ), "Invalid data format!", JOptionPane.ERROR_MESSAGE );
			
			return null;
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
				return "Constructs a graph from the specified adjacency matrix.  The adjacency matrix of a finite graph <i>G</i> on <i>n</i> vertices is the <i>n</i> \u00D7 <i>n</i> matrix where the non-diagonal entry <i>a<sub>i,j</sub></i> is the number of edges from vertex <i>i</i> to vertex <i>j</i>, and the diagonal entry <i>a<sub>i,i</sub></i> is the number of edges from vertex <i>i</i> to itself (i.e. loops).</p><p>Vertices are placed in a circle according to the user-defined \"Arrange circle radius multiplier\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[order] : [1,1] [1,2] [1,3] [...] [2,1] [2,2] [2,3] [...] [3,1] [3,2] [3,3] [... etc.]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{0,6})\\s*:\\s*(0*\\d{1,7}(\\s+0*\\d{1,7})*)\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "Adjacency matrix must be square with dimensions corresponding to the specified number of vertices", "1 \u2264 <code>order</code> \u2264 9,999,999", "0 \u2264 edge multiplicity \u2264 9,999,999" };
			case Generator.Attribute.ARE_LOOPS_ALLOWED:
				return Generator.BooleanRule.FORCED_TRUE;
			case Generator.Attribute.ARE_MULTIPLE_EDGES_ALLOWED:
				return Generator.BooleanRule.FORCED_TRUE;
			case Generator.Attribute.ARE_DIRECTED_EDGES_ALLOWED:
				return Generator.BooleanRule.FORCED_TRUE;
			case Generator.Attribute.ARE_CYCLES_ALLOWED:
				return Generator.BooleanRule.FORCED_TRUE;
			case Generator.Attribute.ARE_PARAMETERS_ALLOWED:
				return true;
			case Generator.Attribute.RELATED_GENERATORS:
				return new String[ ] { "From incidence matrix..." };
			case Generator.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Get adjacency matrix...", "Get incidence matrix..." };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Imported graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "From adjacency matrix...";
	}

return (Generator) this;
