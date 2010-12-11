import java.awt.*;
import java.util.*;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;
import edu.belmont.mth.visigraph.utilities.GeometryUtilities;

	public String evaluate( Graphics2D g2D, Graph g, Component owner )
	{
		return ( g.vertexes.size( ) < 2 ? "\u00D8" : String.format( "%.5f", new Object[ ] { ( 2 * g.edges.size( ) ) / (double) ( g.vertexes.size( ) * ( g.vertexes.size( ) - 1 ) ) } ) );
	}
	
	public boolean allowsDynamicEvaluation( ) { return true;                }
	public boolean allowsOneTimeEvaluation( ) { return true;                }
	public String  toString               ( ) { return "Calculate density"; }
	
return (FunctionBase) this;