import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
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
					graph = new Graph( "Line graph of " + oldGraph.name.get( ), oldGraph.areLoopsAllowed, oldGraph.areDirectedEdgesAllowed, oldGraph.areMultipleEdgesAllowed, true );
					
					Map edgeVertices = new HashMap( );
					for( Edge edge : oldGraph.edges )
					{
						Vertex vertex = new Vertex( edge.handleX.get( ), edge.handleY.get( ), edge.label.get( ), UserSettings.instance.defaultVertexRadius.get( ) * ( edge.thickness.get( ) / UserSettings.instance.defaultEdgeThickness.get( ) ), edge.color.get( ), edge.isSelected.get( ) );
						vertex.weight.set( edge.weight.get( ) );
						vertex.tag.set( edge.tag.get( ) );
						edgeVertices.put( edge, vertex );
						graph.vertices.add( vertex );
					}
					
					if( oldGraph.areDirectedEdgesAllowed )
						for( Vertex vertex : oldGraph.vertices )
							for( Edge edgeTo : oldGraph.getEdgesFrom( vertex ) )
								for( Edge edgeFrom : oldGraph.getEdgesTo( vertex ) )
									graph.edges.add( new Edge( true, edgeVertices.get( edgeFrom ), edgeVertices.get( edgeTo ) ) );
					else
						for( Vertex vertex : oldGraph.vertices )
						{
							Object[ ] edges = oldGraph.getEdges( vertex ).toArray( );
							for( int i = 0; i < edges.length; ++i )
								for( int j = i + 1; j < edges.length; ++j )
									if( graph.getEdges( edgeVertices.get( edges[i] ), edgeVertices.get( edges[j] ) ).isEmpty( ) )
										graph.edges.add( new Edge( false, edgeVertices.get( edges[i] ), edgeVertices.get( edges[j] ) ) );
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
				return "20110130";
			case Generator.Attribute.DESCRIPTION:
				return "Constructs the line graph of a graph by replacing each edge in <i>G</i> with a vertex and connecting each pair of these vertices if their corresponding edges in <i>G</i> are adjacent (i.e. share at least one vertex).";
			case Generator.Attribute.PARAMETERS_DESCRIPTION:
				return "[*.vsg file path]";
			case Generator.Attribute.PARAMETERS_VALIDATION_EXPRESSION:
				return "^\\s*(.+\\.vsg)\\s*$";
			case Generator.Attribute.CONSTRAINTS:
				return new String[ ] { "File path must point to an existing, valid " + GlobalSettings.applicationName + " file" };
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
				return new String[ ] { "Cartesian product of (two graphs)", "Complement of (another graph)", "Condensation of (another graph)" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Derived graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Line graph of (another graph)";
	}
        
return (Generator) this;
