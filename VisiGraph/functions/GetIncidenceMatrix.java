import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.image.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;
import edu.belmont.mth.visigraph.models.functions.sdfafds.IncidenceMatrixDialog;
import edu.belmont.mth.visigraph.models.functions.sdfafds.IncidenceMatrixDialog.ArrayComparator;

	public class IncidenceMatrixDialog extends JDialog
	{
		public class ArrayComparator implements Comparator
		{
			public int compare( Object o0, Object o1 )
			{
				int[ ] a0 = (int[ ]) o0, a1 = (int[ ]) o1;
				
				for( int i = 0; i < a0.length; ++i )
					if( a1[i] != a0[i] )
						return a1[i] - a0[i];
				
				return 0;
			}
			
			public boolean equals( Object obj )
			{
				return false;
			}
		}
		
		private static int[ ][ ] graphToMatrix( Graph graph )
		{
			Map vertexIndices = new HashMap( );
			for( int i = 0; i < graph.vertices.size( ); ++i )
				vertexIndices.put( graph.vertices.get( i ), i );
			
			int[ ][ ] matrix = new int[graph.edges.size( )][graph.vertices.size( )];
			for( int i = 0; i < graph.edges.size( ); ++i )
			{
				++matrix[i][vertexIndices.get( graph.edges.get( i ).from )];
				++matrix[i][vertexIndices.get( graph.edges.get( i ).to )];
			}
			
			Arrays.sort( matrix, new ArrayComparator( ) );
			
			return matrix;
		}
		
		private static Image matrixToImage( int[ ][ ] matrix, int multiplier )
		{
			BufferedImage image = new BufferedImage( matrix.length, matrix[0].length, BufferedImage.TYPE_INT_ARGB );
			
			int[ ] imageMatrix = ( (DataBufferInt) image.getRaster( ).getDataBuffer( ) ).getData( );
			Arrays.fill( imageMatrix, Color.white.getRGB( ) );
			
			int BLACK = Color.BLACK.getRGB( );
			for( int row = 0; row < matrix[0].length; ++row )
				for( int col = 0; col < matrix.length; ++col )
					if( matrix[col][row] > 0 )
						imageMatrix[matrix.length * row + col] = ( matrix[col][row] > 0 ? BLACK : WHITE );
			
			BufferedImage scaledImage = new BufferedImage( multiplier * matrix.length, multiplier * matrix[0].length, BufferedImage.TYPE_INT_ARGB );
			Graphics2D g2D = (Graphics2D) scaledImage.getGraphics( );
			g2D.drawImage( image, 0, 0, scaledImage.getWidth( ), scaledImage.getHeight( ), 0, 0, matrix.length, matrix[0].length, null );
			g2D.dispose( );
			
			return scaledImage;
		}
		
		private static String matrixToString( int[ ][ ] matrix )
		{
			String newLine = System.getProperty( "line.separator" );
			
			StringBuilder sb = new StringBuilder( );
			for( int row = 0; row < matrix[0].length; ++row )
			{
				for( int col = 0; col < matrix.length; ++col )
					sb.append( matrix[col][row] + " " );
				
				sb.deleteCharAt( sb.length( ) - 1 );
				sb.append( newLine );
			}
			
			return sb.substring( 0, sb.length( ) - newLine.length( ) );
		}
		
		public IncidenceMatrixDialog( Graph graph, Frame owner )
		{
			super( owner, "Incidence matrix of " + graph.name.get( ), true );
			this.setResizable( false );
			
			int[ ][ ] matrix = graphToMatrix( graph );
			
			JTextArea textArea = new JTextArea( matrixToString( matrix ), 15, 30 );
			textArea.setAutoscrolls( true );
			
			JLabel imageArea = new JLabel( new ImageIcon( matrixToImage( matrix, Math.max( 1, Math.round( 256 / (float) Math.max( matrix.length, matrix[0].length ) ) ) ) ) );
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
		if( g.edges.isEmpty( ) )
		{
			// What, no subscripts allowed? Unicode ftw!
			String digits = Integer.toString( g.vertices.size( ) );
			StringBuilder sb = new StringBuilder( digits.length( ) );
			for( int i = 0; i < digits.length( ); ++i )
				sb.append( (char) ( '\u2080' + digits.charAt( i ) - '\u0030' ) );
			
			JOptionPane.showMessageDialog( owner, "The incidence matrix for the K\u0305" + sb + " empty graph is empty!", "Empty incidence matrix!", JOptionPane.ERROR_MESSAGE );
			return null;
		}
		
		new IncidenceMatrixDialog( g, JOptionPane.getFrameForComponent( owner ) );
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
				return "Displays a dialog with an incidence matrix for a given graph in text and image form.  An incidence matrix of a finite graph <i>G</i> on <i>m</i> vertices and <i>n</i> edges is a <i>m</i> \u00D7 <i>n</i> matrix where <i>a<sub>i,j</sub></i> > 0 if vertex <i>i</i> and edge <i>j</i> are incident and 0 otherwise. If <i>a<sub>i,j</sub></i> = 2, edge <i>j</i> is a loop; otherwise, <i>a<sub>i,j</sub></i> = 0 or 1.";
			case Function.Attribute.OUTPUT:
				return "An incidence matrix for the graph.";
			case Function.Attribute.ALLOWS_DYNAMIC_EVALUATION:
				return false;
			case Function.Attribute.ALLOWS_ONE_TIME_EVALUATION:
				return true;
			case Function.Attribute.RELATED_GENERATORS:
				return new String[ ] { "From adjacency matrix...", "From incidence matrix..." };
			case Function.Attribute.RELATED_FUNCTIONS:
				return new String[ ] { "Get adjacency matrix...", "Get unweighted distance matrix...", "Get weighted distance matrix..." };
			case Function.Attribute.TAGS:
				return new String[ ] { "Graph evaluator" };
			default:
				return null;
		}
	}
	
	public boolean isApplicable( boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed )
	{
		return !areDirectedEdgesAllowed;
	}
	
	public String toString( )
	{
		return "Get incidence matrix...";
	}
	
return (Function) this;
