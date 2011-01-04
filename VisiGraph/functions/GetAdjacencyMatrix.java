import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.image.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public class AdjacencyMatrixDialog extends JDialog
	{
		private static int[ ][ ] graphToMatrix( Graph graph )
		{
			Map vertexIndices = new HashMap( );
			for( int i = 0; i < graph.vertices.size( ); ++i )
				vertexIndices.put( graph.vertices.get( i ), i );
			
			int[ ][ ] matrix = new int[graph.vertices.size( )][graph.vertices.size( )];
			for( Edge edge : graph.edges )
			{
				++matrix[vertexIndices.get( edge.from )][vertexIndices.get( edge.to )];
				if( !edge.isDirected && !edge.isLoop )
					++matrix[vertexIndices.get( edge.to )][vertexIndices.get( edge.from )];
			}
			
			return matrix;
		}
		
		private static Image matrixToImage( int[ ][ ] matrix, int multiplier )
		{
			BufferedImage image = new BufferedImage( matrix.length, matrix.length, BufferedImage.TYPE_INT_ARGB );
			
			int[ ] imageMatrix = ( (DataBufferInt) image.getRaster( ).getDataBuffer( ) ).getData( );
			Arrays.fill( imageMatrix, Color.white.getRGB( ) );
			
			int BLACK = Color.BLACK.getRGB( );
			for( int row = 0; row < matrix.length; ++row )
				for( int col = 0; col < matrix.length; ++col )
					if( matrix[row][col] > 0 )
						imageMatrix[matrix.length * row + col] = ( matrix[row][col] > 0 ? BLACK : WHITE );
			
			BufferedImage scaledImage = new BufferedImage( multiplier * matrix.length, multiplier * matrix.length, BufferedImage.TYPE_INT_ARGB );
			Graphics2D g2D = (Graphics2D) scaledImage.getGraphics( );
			g2D.drawImage( image, 0, 0, scaledImage.getWidth( ), scaledImage.getHeight( ), 0, 0, matrix.length, matrix.length, null );
			g2D.dispose( );
			
			return scaledImage;
		}
		
		private static String matrixToString( int[ ][ ] matrix )
		{
			String newLine = System.getProperty( "line.separator" );
			
			StringBuilder sb = new StringBuilder( );
			for( int row = 0; row < matrix.length; ++row )
			{
				for( int col = 0; col < matrix.length; ++col )
					sb.append( matrix[row][col] + " " );
				
				sb.deleteCharAt( sb.length( ) - 1 );
				sb.append( newLine );
			}
			
			return sb.substring( 0, sb.length( ) - newLine.length( ) );
		}
		
		public AdjacencyMatrixDialog( Graph graph, Frame owner )
		{
			super( owner, "Adjacency matrix of " + graph.name.get( ), true );
			this.setResizable( false );
			
			int[ ][ ] matrix = graphToMatrix( graph );
			
			JTextArea textArea = new JTextArea( matrixToString( matrix ), 15, 30 );
			textArea.setAutoscrolls( true );
			
			JLabel imageArea = new JLabel( new ImageIcon( matrixToImage( matrix, Math.max( 1, Math.round( 256 / (float) matrix.length ) ) ) ) );
			imageArea.setBorder( BorderFactory.createLoweredBevelBorder( ) );
			
			JPanel imagePanel = new JPanel( );
			imagePanel.add( imageArea );
			imagePanel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
			
			JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, new JScrollPane( textArea ), imagePanel );
			this.add( splitPane );
			
			this.pack( );
			this.setLocationRelativeTo( owner );
			this.setVisible( true );
		}
	}
	
	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		if( g.vertices.isEmpty( ) )
		{
			JOptionPane.showMessageDialog( owner, "The adjacency matrix for the K\u0305\u2080 empty graph is empty!", "Empty adjacency matrix!", JOptionPane.ERROR_MESSAGE );
			return null;
		}
		
		new AdjacencyMatrixDialog( g, JOptionPane.getFrameForComponent( owner ) );
		return null;
	}
	
	public Object getAttribute( Function.Attribute attribute )
	{
		switch( attribute )
		{
			case Function.Attribute.AUTHOR:
				return "Cameron Behar";
			case Function.Attribute.VERSION:
				return "20110101";
			case Function.Attribute.DESCRIPTION:
				return "Displays a dialog with the adjacency matrix for a given graph in text and image form.  The adjacency matrix of a finite graph <i>G</i> on <i>n</i> vertices is the <i>n</i> \u00D7 <i>n</i> matrix where the non-diagonal entry <i>a<sub>i,j</sub></i> is the number of edges from vertex <i>i</i> to vertex <i>j</i>, and the diagonal entry <i>a<sub>i,i</sub></i> is the number of edges from vertex <i>i</i> to itself (i.e. loops).";
			case Function.Attribute.OUTPUT:
				return "The adjacency matrix for the graph.";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return false;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_GENERATORS:
				return new String[ ] { "From adjacency matrix...", "From incidence matrix..." };
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Get incidence matrix..." };
			case Function.Attribute.TAGS:
				return new String[ ] { "Graph evaluator" };
			default:
				return null;
		}
	}
	
	public boolean isApplicable( boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed )
	{
		return true;
	}
	
	public String toString( )
	{
		return "Get adjacency matrix...";
	}
	
return (Function) this;
