import java.awt.*;
import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.models.generators.*;

	public Graph generate(String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed, Component owner)
	{
		Graph graph = new Graph(UserSettings.instance.defaultGraphName.get() + " " + toString(), areLoopsAllowed,areDirectedEdgesAllowed,areMultipleEdgesAllowed,areCyclesAllowed);
		Pattern pattern = Pattern.compile(getParametersValidatingExpression());
		Matcher matcher = pattern.matcher(params); matcher.find();
		
        int m = Integer.parseInt(matcher.group(1));
        int n = Integer.parseInt(matcher.group(2));
        double radius = UserSettings.instance.arrangeCircleRadiusMultiplier.get() * m;
        double degreesPerVertex = 2 * Math.PI / m;
        
        for(int i = 0; i < m; ++i)
        	graph.vertexes.add(new Vertex(radius * Math.cos(degreesPerVertex * i - Math.PI / 2.0), radius * Math.sin(degreesPerVertex * i - Math.PI / 2.0)));
        
        for(int i = 0; i < m; ++i)
			for(int j = i + 1; j < m; ++j)
				graph.edges.add(new Edge(false, graph.vertexes.get(i), graph.vertexes.get(j)));
        	
        Vertex previousVertex = graph.vertexes.get(0);
        
        for(int i = 1; i < n + 1; ++i)
        {
        	graph.vertexes.add(new Vertex(previousVertex.x.get(), previousVertex.y.get() - 50.0));
        	graph.edges.add(new Edge(false, previousVertex, graph.vertexes.get(graph.vertexes.size() - 1)));
        	previousVertex = graph.vertexes.get(graph.vertexes.size() - 1);
        }
                
        return graph;
    }
    
    public String toString                          ( ) { return "Lollipop graph"; }
    public String getParametersDescription          ( ) { return "[order of lollipop] [order of stick]"; }
    public String getParametersValidatingExpression ( ) { return "^\\s*0*([1-9]\\d{1,5}|[3-9])\\s+(\\d+)\\s*$"; }
    
	public GeneratorBase.BooleanRule areLoopsAllowed         ( ) { return GeneratorBase.BooleanRule.DefaultFalse; }
	public GeneratorBase.BooleanRule areDirectedEdgesAllowed ( ) { return GeneratorBase.BooleanRule.ForcedFalse;  }
	public GeneratorBase.BooleanRule areMultipleEdgesAllowed ( ) { return GeneratorBase.BooleanRule.DefaultFalse; }
	public GeneratorBase.BooleanRule areCyclesAllowed        ( ) { return GeneratorBase.BooleanRule.ForcedTrue;   }
	public GeneratorBase.BooleanRule areParametersAllowed    ( ) { return GeneratorBase.BooleanRule.ForcedTrue;   }
        
return (GeneratorBase)this;


