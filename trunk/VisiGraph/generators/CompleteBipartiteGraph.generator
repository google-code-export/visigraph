import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.models.generators.*;

	public Graph generate(String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed)
	{
		Graph graph = new Graph(UserSettings.instance.defaultGraphName.get() + " " + toString(), areLoopsAllowed,areDirectedEdgesAllowed,areMultipleEdgesAllowed,areCyclesAllowed);
		Pattern pattern = Pattern.compile(getParametersValidatingExpression());
		Matcher matcher = pattern.matcher(params); matcher.find();
		
		int r = Integer.parseInt(matcher.group(1));
		int s = Integer.parseInt(matcher.group(2));

		for(int j = 0; j < r; ++j)
			graph.vertexes.add(new Vertex((j - (r / 2)) * UserSettings.instance.arrangeGridSpacing.get(), -UserSettings.instance.arrangeGridSpacing.get()));

		for(int j = 0; j < s; ++j)
			graph.vertexes.add(new Vertex((j - (s / 2)) * UserSettings.instance.arrangeGridSpacing.get(), UserSettings.instance.arrangeGridSpacing.get()));

		for(int j = 0; j < r; ++j)
			for(int k = 0; k < s; ++k)
				graph.edges.add(new Edge(false, graph.vertexes.get(j), graph.vertexes.get(k + r)));
        
		return graph;
	}
	
	public String toString                          ( ) { return "Complete bipartite graph"; }
	public String getParametersDescription          ( ) { return "[order of set A] [order of set B]"; }
	public String getParametersValidatingExpression ( ) { return "^\\s*(\\d+)\\s+(\\d+)\\s*$"; }
	
	public GeneratorBase.BooleanRule areLoopsAllowed         ( ) { return GeneratorBase.BooleanRule.DefaultFalse; }
	public GeneratorBase.BooleanRule areDirectedEdgesAllowed ( ) { return GeneratorBase.BooleanRule.ForcedFalse;  }
	public GeneratorBase.BooleanRule areMultipleEdgesAllowed ( ) { return GeneratorBase.BooleanRule.DefaultFalse; }
	public GeneratorBase.BooleanRule areCyclesAllowed        ( ) { return GeneratorBase.BooleanRule.ForcedTrue;   }
	public GeneratorBase.BooleanRule areParametersAllowed    ( ) { return GeneratorBase.BooleanRule.ForcedTrue;   }
        
return (GeneratorBase)this;









