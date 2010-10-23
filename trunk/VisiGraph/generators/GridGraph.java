import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.models.generators.*;

	public Graph generate(String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed)
	{
		Graph graph = new Graph(UserSettings.instance.defaultGraphName.get() + " " + toString(), areLoopsAllowed,areDirectedEdgesAllowed,areMultipleEdgesAllowed,areCyclesAllowed);
		Pattern pattern = Pattern.compile(getParametersValidatingExpression());
		Matcher matcher = pattern.matcher(params); matcher.find();
		
		int rows = Integer.parseInt(matcher.group(1));
		int cols = Integer.parseInt(matcher.group(2));
        
        for(int row = 0; row < rows; ++row)
        {
        	for(int col = 0; col < cols; ++col)
        	{
				graph.vertexes.add(new Vertex(col * UserSettings.instance.arrangeGridSpacing.get(), row * UserSettings.instance.arrangeGridSpacing.get()));
				if(col > 0) graph.edges.add(new Edge(false, graph.vertexes.get(graph.vertexes.size() - 1), graph.vertexes.get(graph.vertexes.size() - 1    - 1)));
				if(row > 0) graph.edges.add(new Edge(false, graph.vertexes.get(graph.vertexes.size() - 1), graph.vertexes.get(graph.vertexes.size() - cols - 1)));
			}
		}
			
		return graph;
	}
	
	public String toString                          ( ) { return "Grid graph"; }
	public String getParametersDescription          ( ) { return "[rows] [columns]"; }
	public String getParametersValidatingExpression ( ) { return "^\\s*(\\d+)\\s+(\\d+)\\s*$"; }
	
	public GeneratorBase.BooleanRule areLoopsAllowed         ( ) { return GeneratorBase.BooleanRule.DefaultFalse; }
	public GeneratorBase.BooleanRule areDirectedEdgesAllowed ( ) { return GeneratorBase.BooleanRule.ForcedFalse;  }
	public GeneratorBase.BooleanRule areMultipleEdgesAllowed ( ) { return GeneratorBase.BooleanRule.DefaultFalse; }
	public GeneratorBase.BooleanRule areCyclesAllowed        ( ) { return GeneratorBase.BooleanRule.ForcedTrue;   }
	public GeneratorBase.BooleanRule areParametersAllowed    ( ) { return GeneratorBase.BooleanRule.ForcedTrue;   }
        
return (GeneratorBase)this;









