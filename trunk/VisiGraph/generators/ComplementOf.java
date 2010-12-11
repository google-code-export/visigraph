import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.generators.*;

	public Graph generate( String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed, Component owner )
	{
		Graph graph = null;
		Pattern pattern = Pattern.compile( getParametersValidatingExpression( ) );
		Matcher matcher = pattern.matcher( params ); matcher.find( );
		
		try
		{
			File file = new File( matcher.group( 1 ) );
			
			if ( !file.exists( ) )
			{
				JOptionPane.showMessageDialog( null, "The specified file, \"" + file.getPath( ) + "\", does not exist!", "Invalid file path!", JOptionPane.ERROR_MESSAGE );
				return null;
			}
			else if ( !file.isFile( ) )
			{
				JOptionPane.showMessageDialog( null, "\"" + file.getPath( ) + "\" is not a file!", "Invalid file path!", JOptionPane.ERROR_MESSAGE );
				return null;
			}
			else
			{
				Scanner scanner = new Scanner( file );
				StringBuilder sb = new StringBuilder( );
				while ( scanner.hasNextLine( ) )
					sb.append( scanner.nextLine( ) );
				
				scanner.close( );
				
				Graph newGraph = new Graph( sb.toString( ) );
				if ( newGraph != null )
				{
					graph = new Graph( "Complement of " + newGraph.name.get( ), newGraph.areLoopsAllowed, newGraph.areDirectedEdgesAllowed, newGraph.areMultipleEdgesAllowed, true );
					
					graph.vertexes.addAll( newGraph.vertexes );
					
					Map vertexEdges = new HashMap( );
					
					for ( Edge e : newGraph.edges )
					{
						if ( !vertexEdges.containsKey( e.from ) )
							vertexEdges.put( e.from, new HashSet( ) );
						( (Set) vertexEdges.get( e.from ) ).add( e.to );
						
						if ( !e.isDirected )
						{
							if ( !vertexEdges.containsKey( e.to ) )
								vertexEdges.put( e.to, new HashSet( ) );
							( (Set) vertexEdges.get( e.to ) ).add( e.from );
						}
					}
					
					if ( graph.areDirectedEdgesAllowed )
					{
						for ( int i = 0; i < graph.vertexes.size( ); ++i )
							for ( int j = 0; j < graph.vertexes.size( ); ++j )
								if ( i != j && !( vertexEdges.containsKey( graph.vertexes.get( i ) ) && ( (Set) vertexEdges.get( graph.vertexes.get( i ) ) ).contains( graph.vertexes.get( j ) ) ) )
									graph.edges.add( new Edge( true, graph.vertexes.get( i ), graph.vertexes.get( j ) ) );
						
						if ( graph.areLoopsAllowed )
							for ( int i = 0; i < graph.vertexes.size( ); ++i )
								if ( !( vertexEdges.containsKey( graph.vertexes.get( i ) ) && ( (Set) vertexEdges.get( graph.vertexes.get( i ) ) ).contains( graph.vertexes.get( i ) ) ) )
									graph.edges.add( new Edge( true, graph.vertexes.get( i ), graph.vertexes.get( i ) ) );
					}
					else
					{
						int offset = ( graph.areLoopsAllowed ? 0 : 1 );
						for ( int i = 0; i < graph.vertexes.size( ); ++i )
							for ( int j = i + offset; j < graph.vertexes.size( ); ++j )
								if ( !( vertexEdges.containsKey( graph.vertexes.get( i ) ) && ( (Set) vertexEdges.get( graph.vertexes.get( i ) ) ).contains( graph.vertexes.get( j ) ) ) )
									graph.edges.add( new Edge( false, graph.vertexes.get( i ), graph.vertexes.get( j ) ) );
					}
				}
			}
		}
		catch ( Throwable t )
		{
			JOptionPane.showMessageDialog( null, "An exception occurred while loading the specified VisiGraph file!", "Invalid file format!", JOptionPane.ERROR_MESSAGE );
			return null;
		}
		
		return graph;
    }
    
    public String toString                          ( ) { return "Complement of (another graph)"; }
    public String getParametersDescription          ( ) { return "[*.vsg file path]";             }
    public String getParametersValidatingExpression ( ) { return "^\\s*(.+\\.vsg)\\s*$";          }
    
	public GeneratorBase.BooleanRule areLoopsAllowed         ( ) { return GeneratorBase.BooleanRule.ForcedFalse; }
	public GeneratorBase.BooleanRule areDirectedEdgesAllowed ( ) { return GeneratorBase.BooleanRule.ForcedFalse; }
	public GeneratorBase.BooleanRule areMultipleEdgesAllowed ( ) { return GeneratorBase.BooleanRule.ForcedFalse; }
	public GeneratorBase.BooleanRule areCyclesAllowed        ( ) { return GeneratorBase.BooleanRule.ForcedFalse; }
	public GeneratorBase.BooleanRule areParametersAllowed    ( ) { return GeneratorBase.BooleanRule.ForcedTrue;  }
        
return (GeneratorBase) this;


