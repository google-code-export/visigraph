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
			
			// Load the dimensions of the incidence matrix
			int rows = Integer.parseInt( matcher.group( 1 ) );
			int cols = Integer.parseInt( matcher.group( 2 ) );
			String[ ] values = matcher.group( 3 ).split( "\\s+" );
			
			// Simple error-checking
			if( values.length != rows * cols )
				throw new Exception( "Incidence matrix dimensions must correspond to specified the number of vertices and edges." );
			
			// Load the incidence matrix, itself
			int[ ][ ] matrix = new int[rows][cols];
			for( int row = 0; row < rows; ++row )
				for( int col = 0; col < cols; ++col )
					matrix[row][col] = Integer.parseInt( values[cols * row + col] );
			
			// Determine if the incidence matrix has any loops, multi-edges, orphaned edges, or hyper-edges
			areLoopsAllowed = false;
			areMultipleEdgesAllowed = false;
			Set colStrings = new HashSet( );
			for( int col = 0; col < cols; ++col )
			{
				int incidentVertexCount = 0;
				StringBuilder colString = new StringBuilder( rows );
				for( int row = 0; row < rows; ++row )
				{
					incidentVertexCount += matrix[row][col];
					colString.append( matrix[row][col] + " " );
					if( matrix[row][col] == 2 )
						areLoopsAllowed = true;
					else if( matrix[row][col] < 0 )
						throw new Exception( "Incidence matrix contains a negative count at row #" + ( row + 1 ) + " column #" + ( col + 1 ) + "." );
				}
				
				switch( incidentVertexCount )
				{
					case 0:
						throw new Exception( "Incidence matrix contains a disconnected edge at column #" + ( col + 1 ) + "." );
					case 1:
						throw new Exception( "Incidence matrix contains an orphaned edge at column #" + ( col + 1 ) + "." );
					case 2:
						break;
					default:
						throw new Exception( "Incidence matrix contains a hyper-edge at column #" + ( col + 1 ) + "." );
				}
				
				int oldSetSize = colStrings.size( );
				colStrings.add( colString.toString( ) );
				if( colStrings.size( ) == oldSetSize )
					areMultipleEdgesAllowed = true;
			}
			
			// Initialize the graph
			graph = new Graph( "Untitled graph from incidence matrix", areLoopsAllowed, false, areMultipleEdgesAllowed, true );
			
			// Add the vertices
			for( int i = 0; i < rows; ++i )
				graph.vertices.add( new Vertex( 0, 0, UserSettings.instance.defaultVertexPrefix.get( ) + i ) );
			
			// Go through the columns, adding edges to the graph as we go
			for( int col = 0; col < cols; ++col )
			{
				ArrayList incidentVertices = new ArrayList( 2 );
				for( int row = 0; row < rows; ++row )
					for( int i = 0; i < matrix[row][col]; ++i )
						incidentVertices.add( graph.vertices.get( row ) );
				
				Edge newEdge = new Edge( false, incidentVertices.get( 0 ), incidentVertices.get( 1 ) );
				newEdge.label.set( UserSettings.instance.defaultEdgePrefix.get( ) + col );
				graph.edges.add( newEdge );
			}
			
			// Clean the graph up a bit
			LayoutUtilities.arrangeCircle( graph.vertices );
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
				return "20110105";
			case Generator.Attribute.DESCRIPTION:
				return "Constructs a graph from the specified incidence matrix.  An incidence matrix of a finite graph <i>G</i> on <i>m</i> vertices and <i>n</i> edges is a <i>m</i> \u00D7 <i>n</i> matrix where <i>a<sub>i,j</sub></i> > 0 if vertex <i>i</i> and edge <i>j</i> are incident and 0 otherwise. If <i>a<sub>i,j</sub></i> = 2, edge <i>j</i> is a loop; otherwise, <i>a<sub>i,j</sub></i> = 0 or 1.</p><p>Vertices are placed in a circle according to the user-defined \"Arrange circle radius multiplier\" setting.";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[order] [size] : [1,1] [1,2] [1,3] [...] [2,1] [2,2] [2,3] [...] [3,1] [3,2] [3,3] [... etc.]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*0*([1-9]\\d{0,6})\\s+0*([1-9]\\d{0,6})\\s*:\\s*(0*[0-2](\\s+0*[0-2])*)\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "Incidence matrix dimensions must correspond to specified the number of vertices and edges", "Incidence matrix can contain neither hyper- nor orphan- edges", "1 \u2264 <code>order</code> \u2264 9,999,999", "1 \u2264 <code>size</code> \u2264 9,999,999" };
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
				return new String[ ] { "From adjacency matrix..." };
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
		return "From incidence matrix...";
	}

return (Generator) this;
