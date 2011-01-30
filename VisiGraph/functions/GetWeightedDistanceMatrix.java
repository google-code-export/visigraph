import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.image.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public class DistanceMatrixDialog extends JDialog
	{
		private static Image matrixToImage( double[ ][ ] matrix, int multiplier )
		{
			BufferedImage image = new BufferedImage( matrix.length, matrix.length, BufferedImage.TYPE_INT_ARGB );
			
			int[ ] imageMatrix = ( (DataBufferInt) image.getRaster( ).getDataBuffer( ) ).getData( );
			Arrays.fill( imageMatrix, Color.white.getRGB( ) );
			
			double maxDistance = 0.0;
			for( int row = 0; row < matrix.length; ++row )
				for( int col = 0; col < matrix.length; ++col )
					if( matrix[row][col] < Double.POSITIVE_INFINITY )
						maxDistance = Math.max( maxDistance, matrix[row][col] );
			
			for( int row = 0; row < matrix.length; ++row )
				for( int col = 0; col < matrix.length; ++col )
					imageMatrix[matrix.length * row + col] = ( matrix[row][col] == Double.POSITIVE_INFINITY ? Color.gray.getRGB( ) : maxDistance == 0.0 ? 0xFFFFFF : Color.getHSBColor( (float) ( 2.0 / 3.0 * ( 1.0 - matrix[row][col] / maxDistance ) ), 1f, 1f ).getRGB( ) );
			
			BufferedImage scaledImage = new BufferedImage( multiplier * matrix.length, multiplier * matrix.length, BufferedImage.TYPE_INT_ARGB );
			Graphics2D g2D = (Graphics2D) scaledImage.getGraphics( );
			g2D.drawImage( image, 0, 0, scaledImage.getWidth( ), scaledImage.getHeight( ), 0, 0, matrix.length, matrix.length, null );
			g2D.dispose( );
			
			return scaledImage;
		}
		
		private static String matrixToString( double[ ][ ] matrix )
		{
			String newLine = System.getProperty( "line.separator" );
			
			StringBuilder sb = new StringBuilder( );
			for( int row = 0; row < matrix.length; ++row )
			{
				for( int col = 0; col < matrix.length; ++col )
					sb.append( matrix[row][col] == Double.POSITIVE_INFINITY ? "\u221E               " : String.format( "%-15.5f ", new Object[ ] { matrix[row][col] } ) );
				
				sb.deleteCharAt( sb.length( ) - 1 );
				sb.append( newLine );
			}
			
			return sb.substring( 0, sb.length( ) - newLine.length( ) );
		}
		
		public DistanceMatrixDialog( String name, double[ ][ ] matrix, Frame owner )
		{
			super( owner, "Distance matrix of " + name, true );
			this.setResizable( false );
			
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
			JOptionPane.showMessageDialog( owner, "The distance matrix for the K\u0305\u2080 empty graph is empty!", "Empty distance matrix!", JOptionPane.ERROR_MESSAGE );
			return null;
		}
		
		double[ ][ ] matrix = GraphUtilities.getDistanceMatrix( g, true );
		
		for( int rowCol = 0; rowCol < matrix.length; ++rowCol )
			if( matrix[rowCol][rowCol] < 0.0 )
			{
				JOptionPane.showMessageDialog( owner, "The graph contains negative cycles!", "Invalid distance matrix!", JOptionPane.ERROR_MESSAGE );
				return null;
			}
		
		new DistanceMatrixDialog( g.name.get( ), matrix, JOptionPane.getFrameForComponent( owner ) );
		return null;
	}
	
	public Object getAttribute( Function.Attribute attribute )
	{
		switch( attribute )
		{
			case Function.Attribute.AUTHOR:
				return "Cameron Behar";
			case Function.Attribute.VERSION:
				return "20110129";
			case Function.Attribute.DESCRIPTION:
				return "Displays a dialog with the weighted distance matrix for a given graph in text and image form.  The distance matrix of a finite graph <i>G</i> on <i>n</i> vertices is the <i>n</i> \u00D7 <i>n</i> matrix where the entry <i>d<sub>i,j</sub></i> is the weighted graph distance from vertex <i>i</i> to vertex <i>j</i>.</p><p>In the rendered image, the distance matrix is represented as a heatmap with finite-valued entries colored from blue to red according to each entry's value relative to the highest finite value in the matrix.  In contrast, entries with infinite values are colored gray.</p><p>" + GlobalSettings.applicationName + "'s built-in implementation of the Roy-Floyd-Warshall algorithm is used to efficiently compute the weighted distance between each pair of vertices in <code><i>O</i>(|<i>V</i>|<sup>3</sup>)</code> time.";
			case Function.Attribute.OUTPUT:
				return "The weighted distance matrix for the graph.";
			case Function.Attribute.CONSTRAINTS:
				return new String[ ] { "The graph must not contain negative cycles" };
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return false;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_GENERATORS:
				return new String[ ] { "From adjacency matrix...", "From incidence matrix..." };
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Get adjacency matrix...", "Get incidence matrix...", "Get unweighted distance matrix..." };
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
		return "Get weighted distance matrix...";
	}
	
return (Function) this;
