import java.awt.*;
import java.util.*;
import java.util.regex.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.models.generators.*;

	public Graph generate(String params, boolean areLoopsAllowed, boolean areDirectedEdgesAllowed, boolean areMultipleEdgesAllowed, boolean areCyclesAllowed, Component owner)
	{
		Graph graph = new Graph(UserSettings.instance.defaultGraphName.get() + " " + toString(), areLoopsAllowed,areDirectedEdgesAllowed,areMultipleEdgesAllowed,areCyclesAllowed);
		Pattern pattern = Pattern.compile(getParametersValidatingExpression());
		Matcher matcher = pattern.matcher(params); matcher.find();
		
        int n = Integer.parseInt(matcher.group(1));
        int k = Integer.parseInt(matcher.group(2)) - 1;
        double radius = UserSettings.instance.arrangeCircleRadiusMultiplier.get() * k;
        double degreesPerVertex = 2 * Math.PI / k;
        double bundleSpacing = radius * 3;
        
        Vector firstFirecrackers = new Vector();
        
        for(int i = 0; i < n; ++i)
        {
	        Vertex hub = new Vertex(0.0 + i * bundleSpacing, 0.0);
	        graph.vertexes.add(hub);
	        
	        for(int j = 0; j < k; ++j)
	        {
	        	Vertex firecracker = new Vertex(radius * Math.cos(degreesPerVertex * j - Math.PI / 2.0) + i * bundleSpacing, radius * Math.sin(degreesPerVertex * j - Math.PI / 2.0));
	        	graph.vertexes.add(firecracker); graph.edges.add(new Edge(false, hub, firecracker));
	        	if(j == 0) firstFirecrackers.add(firecracker);
	        }
        }
        
        for(int i = 0; i < firstFirecrackers.size() - 1; ++i)
        	graph.edges.add(new Edge(false, (Vertex)firstFirecrackers.get(i), (Vertex)firstFirecrackers.get(i + 1)));
        
        return graph;
    }
    
    public String toString                          ( ) { return "Firecracker graph"; }
    public String getParametersDescription          ( ) { return "[bundles] [order of bundle]"; }
    public String getParametersValidatingExpression ( ) { return "^\\s*0*([1-9]\\d{1,5}|[2-9])\\s+0*([1-9]\\d{1,5}|[2-9])\\s*$"; }
    
	public GeneratorBase.BooleanRule areLoopsAllowed         ( ) { return GeneratorBase.BooleanRule.DefaultFalse; }
	public GeneratorBase.BooleanRule areDirectedEdgesAllowed ( ) { return GeneratorBase.BooleanRule.ForcedFalse;  }
	public GeneratorBase.BooleanRule areMultipleEdgesAllowed ( ) { return GeneratorBase.BooleanRule.DefaultFalse; }
	public GeneratorBase.BooleanRule areCyclesAllowed        ( ) { return GeneratorBase.BooleanRule.DefaultFalse; }
	public GeneratorBase.BooleanRule areParametersAllowed    ( ) { return GeneratorBase.BooleanRule.ForcedTrue;   }
        
return (GeneratorBase)this;