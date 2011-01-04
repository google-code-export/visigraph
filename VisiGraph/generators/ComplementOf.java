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
					graph = new Graph( "Complement of " + oldGraph.name.get( ), oldGraph.areLoopsAllowed, oldGraph.areDirectedEdgesAllowed, oldGraph.areMultipleEdgesAllowed, true );
					
					graph.vertices.addAll( oldGraph.vertices );
					
					Map vertexEdges = new HashMap( );
					
					for( Edge edge : oldGraph.edges )
					{
						if( !vertexEdges.containsKey( edge.from ) )
							vertexEdges.put( edge.from, new HashSet( ) );
						( (Set) vertexEdges.get( edge.from ) ).add( edge.to );
						
						if( !edge.isDirected )
						{
							if( !vertexEdges.containsKey( edge.to ) )
								vertexEdges.put( edge.to, new HashSet( ) );
							( (Set) vertexEdges.get( edge.to ) ).add( edge.from );
						}
					}
					
					if( graph.areDirectedEdgesAllowed )
					{
						for( int i = 0; i < graph.vertices.size( ); ++i )
							for( int j = 0; j < graph.vertices.size( ); ++j )
								if( i != j && !( vertexEdges.containsKey( graph.vertices.get( i ) ) && ( (Set) vertexEdges.get( graph.vertices.get( i ) ) ).contains( graph.vertices.get( j ) ) ) )
									graph.edges.add( new Edge( true, graph.vertices.get( i ), graph.vertices.get( j ) ) );
						
						if( graph.areLoopsAllowed )
							for( int i = 0; i < graph.vertices.size( ); ++i )
								if( !( vertexEdges.containsKey( graph.vertices.get( i ) ) && ( (Set) vertexEdges.get( graph.vertices.get( i ) ) ).contains( graph.vertices.get( i ) ) ) )
									graph.edges.add( new Edge( true, graph.vertices.get( i ), graph.vertices.get( i ) ) );
					}
					else
					{
						int offset = ( graph.areLoopsAllowed ? 0 : 1 );
						for( int i = 0; i < graph.vertices.size( ); ++i )
							for( int j = i + offset; j < graph.vertices.size( ); ++j )
								if( !( vertexEdges.containsKey( graph.vertices.get( i ) ) && ( (Set) vertexEdges.get( graph.vertices.get( i ) ) ).contains( graph.vertices.get( j ) ) ) )
									graph.edges.add( new Edge( false, graph.vertices.get( i ), graph.vertices.get( j ) ) );
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
				return "Constructs the complement of a graph (<i>G\u0305</i>) by connecting an identical vertex set using only those edges not present in <i>G</i>.";
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
				return new String[ ] { "Condensation of (another graph)", "Line graph of (another graph)" };
			case Generator.Attribute.TAGS:
				return new String[ ] { "Derived graph" };
			default:
				return null;
		}
	}
	
	public String toString( )
	{
		return "Complement of (another graph)";
	}
        
return (Generator) this;
