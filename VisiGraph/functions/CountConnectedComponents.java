import java.awt.*;
import java.util.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.models.functions.*;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		return GraphUtilities.countConnectedComponents( g ) + ( g.areDirectedEdgesAllowed ? " (weakly)" : "" );
	}
	
	public boolean allowsDynamicEvaluation( ) { return true;                         }
	public boolean allowsOneTimeEvaluation( ) { return true;                         }
	public String  toString               ( ) { return "Count connected components"; }
	
return (FunctionBase) this;