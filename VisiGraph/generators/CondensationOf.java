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

	public Graph generate( String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed, Component owner )
	{
		Graph graph = null;
		
		try
		{
			Pattern pattern = Pattern.compile( (String) this.getAttribute( Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION ) );
			Matcher matcher = pattern.matcher( params );
			matcher.find( );
			
			File file = new File( matcher.group( 1 ) );
			
			if( !file.exists( ) )
			{
				JOptionPane.showMessageDialog( owner, "The specified file, \"" + file.getPath( ) + "\", does not exist!", "Invalid file path!", JOptionPane.ERROR_MESSAGE );
				return null;
			}
			else if( !file.isFile( ) )
			{
				JOptionPane.showMessageDialog( owner, "\"" + file.getPath( ) + "\" is not a file!", "Invalid file path!", JOptionPane.ERROR_MESSAGE );
				return null;
			}
			else
			{
				Scanner scanner = new Scanner( file );
				StringBuilder sb = new StringBuilder( );
				while( scanner.hasNextLine( ) )
					sb.append( scanner.nextLine( ) );
				
				scanner.close( );
				
				Graph oldGraph = new Graph( sb.toString( ) );
				if( oldGraph != null )
				{
					if( !oldGraph.areDirectedEdgesAllowed )
					{
						JOptionPane.showMessageDialog( owner, "Source graph must be directed!", "Invalid graph!", JOptionPane.ERROR_MESSAGE );
						return null;
					}
					if( !oldGraph.areCyclesAllowed )
						return oldGraph;
					
					graph = new Graph( "Condensation of " + oldGraph.name.get( ), oldGraph.areLoopsAllowed, true, true, true );
					
					Map vertexComponents = new HashMap( ); // Maps old vertices to old components
					Map componentVertices = new HashMap( ); // Maps old components to new vertices
					Collection components = GraphUtilities.findStronglyConnectedComponents( oldGraph );
					
					for( Collection component : components )
					{
						StringBuilder newLabel = new StringBuilder( );
						double newRadius = 0.0;
						Point2D.Double newLocation = new Point2D.Double( );
						Integer newColor = component.iterator( ).next( ).color.get( );
						Boolean newIsSelected = component.iterator( ).next( ).isSelected.get( );
						double newWeight = 0.0;
						
						for( Vertex vertex : component )
						{
							newLabel.append( vertex.label.get( ) + ", " );
							newRadius += vertex.radius.get( );
							newLocation.x += vertex.x.get( );
							newLocation.y += vertex.y.get( );
							if( newColor != vertex.color.get( ) )
								newColor = null;
							if( newIsSelected != vertex.isSelected.get( ) )
								isSelected = null;
							newWeight += vertex.weight.get( );
							vertexComponents.put( vertex, component );
						}
						newLocation.x /= (double) component.size( );
						newLocation.y /= (double) component.size( );
						
						Vertex newVertex = new Vertex( newLocation.x, newLocation.y, newLabel.substring( 0, newLabel.length( ) - 2 ), newRadius, ( newColor == null ? UserSettings.instance.defaultVertexColor.get( ) : newColor ), ( newIsSelected == null ? UserSettings.instance.defaultVertexIsSelected.get( ) : newIsSelected ) );
						newVertex.weight.set( newWeight );
						componentVertices.put( component, newVertex );
						graph.vertices.add( newVertex );
					}
					
					for( Edge edge : oldGraph.edges )
						if( !edge.isLoop && vertexComponents.get( edge.from ) != vertexComponents.get( edge.to ) )
						{
							Edge newEdge = new Edge( true, componentVertices.get( vertexComponents.get( edge.from ) ), componentVertices.get( vertexComponents.get( edge.to ) ), edge.weight.get( ), edge.color.get( ), edge.label.get( ), edge.isSelected.get( ) );
							newEdge.thickness.set( edge.thickness.get( ) );
							newEdge.tag.set( edge.tag.get( ) );
							graph.edges.add( newEdge );
						}
				}
			}
		}
		catch( Throwable t )
		{
			JOptionPane.showMessageDialog( owner, "An exception occurred while loading the specified " + GlobalSettings.applicationName + " file!", "Invalid file format!", JOptionPane.ERROR_MESSAGE );
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
				return "Constructs the condensation of a directed graph by coalscing the vertices of each strongly connected component into a single vertex, resulting in a directed acylcic graph.</p><p>In coalescing each strongly connected component:<ul><li>locations are averaged to find the centroid,</li><li>labels are concatenated,</li><li>radii are summed,</li><li>colors remain if all the same, otherwise reset to default,</li><li>weights are summed</li><li>selections remain if all the same, otherwise reset to default, and</li><li>tags are ignored.</li></ul>";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[*.vsg file path]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*(.+\\.vsg)\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "File path must point to an existing, valid " + GlobalSettings.applicationName + " file", "Source graph must be directed" };
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
				return new String[ ] { "Complement of (another graph)", "Line graph of (another graph)" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Derived graph", "Directed graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Condensation of (another graph)";
	}
        
return (Generator) this;
