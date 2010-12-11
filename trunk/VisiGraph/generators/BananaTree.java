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
        
        Vector firstBananas = new Vector();
        
        for(int i = 0; i < n; ++i)
        {
	        Vertex hub = new Vertex(0.0 + i * bundleSpacing, 0.0);
	        graph.vertexes.add(hub);
	        
	        for(int j = 0; j < k; ++j)
	        {
	        	Vertex banana = new Vertex(radius * Math.cos(degreesPerVertex * j - Math.PI / 2.0) + i * bundleSpacing, radius * Math.sin(degreesPerVertex * j - Math.PI / 2.0));
	        	graph.vertexes.add(banana); graph.edges.add(new Edge(false, hub, banana));
	        	if(j == 0) firstBananas.add(banana);
	        }
        }
        
        Vertex root = new Vertex(firstBananas.firstElement().x.get() + (firstBananas.lastElement().x.get() - firstBananas.firstElement().x.get()) / 2.0, -bundleSpacing);
        graph.vertexes.add(root);
        
        for(int i = 0; i < firstBananas.size(); ++i)
        	graph.edges.add(new Edge(false, root, (Vertex)firstBananas.get(i)));
        
        return graph;
    }
    
    public String toString                          ( ) { return "Banana tree"; }
    public String getParametersDescription          ( ) { return "[bundles] [bananas]"; }
    public String getParametersValidatingExpression ( ) { return "^\\s*0*([1-9]\\d{1,5}|[3-9])\\s+0*([1-9]\\d{1,5}|[3-9])\\s*$"; }
    
	public GeneratorBase.BooleanRule areLoopsAllowed         ( ) { return GeneratorBase.BooleanRule.DefaultFalse; }
	public GeneratorBase.BooleanRule areDirectedEdgesAllowed ( ) { return GeneratorBase.BooleanRule.ForcedFalse;  }
	public GeneratorBase.BooleanRule areMultipleEdgesAllowed ( ) { return GeneratorBase.BooleanRule.DefaultFalse; }
	public GeneratorBase.BooleanRule areCyclesAllowed        ( ) { return GeneratorBase.BooleanRule.DefaultFalse; }
	public GeneratorBase.BooleanRule areParametersAllowed    ( ) { return GeneratorBase.BooleanRule.ForcedTrue;   }
        
return (GeneratorBase) this;