import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.geom.*;
import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.models.generators.*;

	class Circle
	{
		public double	x, y, radius;
		
		public Circle( double x, double y, double radius )
		{
			this.x = x;
			this.y = y;
			this.radius = radius;
		}
		
		public boolean contains( Point2D p )
		{
			return ( ( this.x - p.getX( ) ) * ( this.x - p.getX( ) ) + ( this.y - p.getY( ) ) * ( this.y - p.getY( ) ) <= this.radius * this.radius );
		}
	}

	class BoundingCircleFinder
	{
		public static Circle getBoundingCircle( Point2D.Double[ ] points )
		{
			if( points.length == 0 )
				return null;
			
			return getBoundingCircle( points, points.length, new Point2D.Double[3], 0 );
		}
		
		private static Circle getBoundingCircle( Point2D.Double[ ] points, int pointsCount, Point2D.Double[ ] bounds, int boundsCount )
		{
			Circle boundingCircle = null;
			
			// Base cases
			if( boundsCount == 3 )
				boundingCircle = getCircumscribingCircle( bounds[0], bounds[1], bounds[2] );
			else if( boundsCount == 2 && pointsCount == 0 )
				boundingCircle = getCircumscribingCircle( bounds[0], bounds[1] );
			else if( boundsCount == 1 && pointsCount == 1 )
				boundingCircle = getCircumscribingCircle( bounds[0], points[0] );
			else if( boundsCount == 0 && pointsCount == 1 )
				boundingCircle = new Circle( points[0].getX( ), points[0].getY( ), 0 );
			else
			{
				// Recursive case
				boundingCircle = getBoundingCircle( points, --pointsCount, bounds, boundsCount );
				
				if( !boundingCircle.contains( points[pointsCount] ) )
				{
					bounds[boundsCount++] = points[pointsCount];
					boundingCircle = getBoundingCircle( points, pointsCount, bounds, boundsCount );
				}
			}
			
			return boundingCircle;
		}
		
		private static Circle getCircumscribingCircle( Point2D.Double p1, Point2D.Double p2 )
		{
			double cx = ( p1.x + p2.x ) / 2.0, cy = ( p1.y + p2.y ) / 2.0;
			return new Circle( cx, cy, Math.sqrt( ( p1.x - cx ) * ( p1.x - cx ) + ( p1.y - cy ) * ( p1.y - cy ) ) );
		}
		
		private static Circle getCircumscribingCircle( Point2D.Double p1, Point2D.Double p2, Point2D.Double p3 )
		{
			double a = p2.x - p1.x, b = p2.y - p1.y, c = p3.x - p1.x, d = p3.y - p1.y;
			double e = a * ( p2.x + p1.x ) / 2.0 + b * ( p2.y + p1.y ) / 2.0;
			double f = c * ( p3.x + p1.x ) / 2.0 + d * ( p3.y + p1.y ) / 2.0;
			double determinant = a * d - b * c;
			
			double cx = ( d * e - b * f ) / determinant, cy = ( -c * e + a * f ) / determinant;
			
			return new Circle( cx, cy, Math.sqrt( ( p1.x - cx ) * ( p1.x - cx ) + ( p1.y - cy ) * ( p1.y - cy ) ) );
		}
	}
	
	public Graph generate( String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed, Component owner )
	{
		Graph graph = null;
		
		try
		{
			Pattern pattern = Pattern.compile( (String) this.getAttribute( Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION ) );
			Matcher matcher = pattern.matcher( params );
			matcher.find( );
			
			File file0 = new File( matcher.group( 1 ) ), file1 = new File( matcher.group( 2 ) );
			
			if( !file0.exists( ) )
			{
				JOptionPane.showMessageDialog( owner, "The specified file, \"" + file0.getPath( ) + "\", does not exist!", "Invalid file path!", JOptionPane.ERROR_MESSAGE );
				return null;
			}
			else if( !file0.isFile( ) )
			{
				JOptionPane.showMessageDialog( owner, "\"" + file0.getPath( ) + "\" is not a file!", "Invalid file path!", JOptionPane.ERROR_MESSAGE );
				return null;
			}
			else if( !file1.exists( ) )
			{
				JOptionPane.showMessageDialog( owner, "The specified file, \"" + file1.getPath( ) + "\", does not exist!", "Invalid file path!", JOptionPane.ERROR_MESSAGE );
				return null;
			}
			else if( !file1.isFile( ) )
			{
				JOptionPane.showMessageDialog( owner, "\"" + file1.getPath( ) + "\" is not a file!", "Invalid file path!", JOptionPane.ERROR_MESSAGE );
				return null;
			}
			else
			{
				Scanner scanner = new Scanner( file0 );
				StringBuilder sb = new StringBuilder( );
				while( scanner.hasNextLine( ) )
					sb.append( scanner.nextLine( ) );
				scanner.close( );
				Graph oldGraph0 = new Graph( sb.toString( ) );
				
				scanner = new Scanner( file1 );
				sb = new StringBuilder( );
				while( scanner.hasNextLine( ) )
					sb.append( scanner.nextLine( ) );
				scanner.close( );
				Graph oldGraph1 = new Graph( sb.toString( ) );
				
				if( oldGraph0 != null && oldGraph1 != null )
				{
					if( oldGraph0.areDirectedEdgesAllowed != oldGraph1.areDirectedEdgesAllowed )
					{
						JOptionPane.showMessageDialog( owner, "Source graphs must be mutually undirected or mutually directed!", "Invalid graphs!", JOptionPane.ERROR_MESSAGE );
						return null;
					}
					
					graph = new Graph( "Cartesian product of " + oldGraph0.name.get( ) + " and " + oldGraph1.name.get( ), oldGraph0.areLoopsAllowed || oldGraph1.areLoopsAllowed, oldGraph0.areDirectedEdgesAllowed, oldGraph0.areMultipleEdgesAllowed || oldGraph1.areMultipleEdgesAllowed, true );
					if( oldGraph0.vertices.isEmpty( ) || oldGraph1.vertices.isEmpty( ) )
						return graph;
					
					// Find the smallest circle containing all the vertices of oldGraph1
					Point2D.Double[ ] oldGraph1Points = new Point2D.Double[oldGraph1.vertices.size( )];
					for( int i = 0; i < oldGraph1.vertices.size( ); ++i )
						oldGraph1Points[i] = new Point2D.Double( oldGraph1.vertices.get( i ).x.get( ), oldGraph1.vertices.get( i ).y.get( ) );
					Circle oldGraph1BoundingCircle = BoundingCircleFinder.getBoundingCircle( oldGraph1Points );
					
					// Center oldGraph1 about (0, 0)
					oldGraph1.selectAll( true );
					oldGraph1.translateSelected( -oldGraph1BoundingCircle.x, -oldGraph1BoundingCircle.y );
					
					// Find the minimum distance between any two vertices in oldGraph0
					double minDistance = Double.POSITIVE_INFINITY;
					for( int i = 0; i < oldGraph0.vertices.size( ); ++i )
						for( int j = i + 1; j < oldGraph0.vertices.size( ); ++j )
							minDistance = Math.min( minDistance, GeometryUtilities.distance( oldGraph0.vertices.get( i ), oldGraph0.vertices.get( j ) ) );
					
					// Scale oldGraph0 such that the minimum distance between any two vertices is 2.5 times the radius of oldGraph1's bounding circle
					if( minDistance < 2.5 * oldGraph1BoundingCircle.radius )
						LayoutUtilities.scale( oldGraph0.vertices, ( 2.5 * oldGraph1BoundingCircle.radius ) / minDistance );
					
					// Prepare to keep track of each oldGraph0's vertex's copy of oldGraph1 in the new graph
					HashMap subgraphs = new HashMap( );
					
					// Union all the copies of oldGraph1 into one new graph based on vertex locations in oldGraph0
					for( Vertex vertex : oldGraph0.vertices )
					{
						oldGraph1.translateSelected( vertex.x.get( ), vertex.y.get( ) );
						graph.union( oldGraph1 );
						oldGraph1.translateSelected( -vertex.x.get( ), -vertex.y.get( ) );
						
						subgraphs.put( vertex, new ArrayList( ) );
						
						for( int i = graph.vertices.size( ) - oldGraph1.vertices.size( ); i < graph.vertices.size( ); ++i )
						{
							Vertex newVertex = graph.vertices.get( i );
							subgraphs.get( vertex ).add( newVertex );
							newVertex.label.set( newVertex.label.get( ) + "/" + vertex.label.get( ) );
						}
					}
					
					// Add edges between the copies of oldGraph1 as prescribed by the Cartesian product operation
					for( Edge edge : oldGraph0.edges )
					{
						ArrayList subgraph0 = subgraphs.get( edge.from ), subgraph1 = subgraphs.get( edge.to );
						
						for( int i = 0; i < subgraph0.size( ); ++i )
						{
							Edge newEdge = new Edge( edge.isDirected, subgraph0.get( i ), subgraph1.get( i ), edge.weight.get( ), edge.color.get( ), edge.label.get( ) ); 
							newEdge.thickness.set( edge.thickness.get( ) );
							graph.edges.add( newEdge );
						}
					}
				}
			}
		}
		catch( Throwable t )
		{
			JOptionPane.showMessageDialog( owner, "An exception occurred while loading the specified " + GlobalSettings.applicationName + " files!", "Invalid file format!", JOptionPane.ERROR_MESSAGE );
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
				return "20110130";
			case Generator.Attribute.DESCRIPTION:
				return "Constructs the Cartesian product of two directed- or two undirected graphs, <i>G</i> \u25A1 <i>H</i>, by unioning a copy of <i>H</i> in place of each vertex in <i>G</i> and connecting each corresponding pair of vertices in two such copies of <i>H</i> if the vertices they replaced in <i>G</i> were adjacent.</p><p>For the sake of clarity, <i>G</i> may be scaled such that the geometric distance between any two of its vertices is at least 2.5\u00D7 the radius of <i>V(H)</i>'s bounding circle.  Also, in merging the two graphs:<ul><li>vertex labels are concatenated, separated by a solidus,</li><li>vertex tags are ignored,</li><li>all other vertex properties are preserved from <i>H</i>, and</li><li>all edge properties are preserved from their respective graphs.</li></ul>";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[primary *.vsg file path] [secondary *.vsg file path]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*\"(.+\\.vsg)\"\\s+\"(.+\\.vsg)\"\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "File paths must point to existing, valid " + GlobalSettings.applicationName + " files", "Source graphs must be mutually undirected or mutually directed" };
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
				return new String[ ] { "Book graph", "Cartesian product of a complete bipartite graph and cycle (Behar)", "Cartesian product of a complete bipartite graph and cycle (Scott)", "Complement of (another graph)", "Condensation of (another graph)", "Grid graph", "Ladder graph", "Line graph of (another graph)", "Prism graph", "Stacked book graph" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Cartesian product", "Derived graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Cartesian product of (two graphs)";
	}
	
return (Generator) this;
