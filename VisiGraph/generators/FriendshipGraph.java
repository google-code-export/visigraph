import java.awt.*;
import java.util.regex.*;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.models.generators.*;

	public Graph generate( String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed, Component owner )
	{
		Graph graph = new Graph( UserSettings.instance.defaultGraphName.get( ) + " " + toString( ), areLoopsAllowed, areDirectedEdgesAllowed, areMultipleEdgesAllowed, areCyclesAllowed );
		Pattern pattern = Pattern.compile( getParametersValidatingExpression( ) );
		Matcher matcher = pattern.matcher( params ); matcher.find( );
		
		int n = Integer.parseInt( matcher.group( 1 ) ) * 2;
		double radius = UserSettings.instance.arrangeCircleRadiusMultiplier.get( ) * n;
		double degreesPerVertex = 2 * Math.PI / n;
		
		Vertex hub = new Vertex( 0.0, 0.0 );
		graph.vertexes.add( hub );
		
		for ( int i = 0; i < n; ++i )
		{
			Vertex corner = new Vertex( radius * Math.cos( degreesPerVertex * i - Math.PI / 2.0 ), radius * Math.sin( degreesPerVertex * i - Math.PI / 2.0 ) );
			graph.vertexes.add( corner );
			graph.edges.add( new Edge( false, hub, corner ) );
		}
		
		for ( int i = 1; i < n; i += 2 )
			graph.edges.add( new Edge( false, graph.vertexes.get( i ), graph.vertexes.get( i + 1 ) ) );
		
		return graph;
    }
    
    public String toString                          ( ) { return "Friendship graph";                  }
    public String getParametersDescription          ( ) { return "[cycles]";                          }
    public String getParametersValidatingExpression ( ) { return "^\\s*0*([1-9]\\d{1,5}|[2-9])\\s*$"; }
    
	public GeneratorBase.BooleanRule areLoopsAllowed         ( ) { return GeneratorBase.BooleanRule.DefaultFalse; }
	public GeneratorBase.BooleanRule areDirectedEdgesAllowed ( ) { return GeneratorBase.BooleanRule.ForcedFalse;  }
	public GeneratorBase.BooleanRule areMultipleEdgesAllowed ( ) { return GeneratorBase.BooleanRule.DefaultFalse; }
	public GeneratorBase.BooleanRule areCyclesAllowed        ( ) { return GeneratorBase.BooleanRule.ForcedTrue;   }
	public GeneratorBase.BooleanRule areParametersAllowed    ( ) { return GeneratorBase.BooleanRule.ForcedTrue;   }
        
return (GeneratorBase) this;


