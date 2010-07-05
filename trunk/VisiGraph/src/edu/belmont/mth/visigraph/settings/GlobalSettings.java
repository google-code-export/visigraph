/**
 * GlobalSettings.java
 */
package edu.belmont.mth.visigraph.settings;

import java.awt.*;
import java.awt.geom.*;
import edu.belmont.mth.visigraph.models.functions.*;
import edu.belmont.mth.visigraph.models.generators.*;

/**
 * @author Cameron Behar
 * 
 */
public class GlobalSettings
{
	public static final String					 defaultGraphName							= "Untitled";
	public static final String					 applicationName							= "VisiGraph";
	
	public static final Color					 defaultGraphBackgroundDisplayColor			= new Color(250, 250, 250);
	public static final Color					 defaultGraphTextDisplayColor				= Color.black;
	public static final Color					 defaultGraphSelectionBoxFillDisplayColor	= new Color(150, 150, 255, 100);
	public static final Color					 defaultGraphSelectionBoxLineDisplayColor	= new Color(150, 150, 255, 200);
	
	public static final Color					 defaultVertexLineDisplayColor				= Color.black;
	
	public static final Color					 defaultDraggingHandleEdgeLineDisplayColor	= Color.lightGray;
	public static final Color					 defaultDraggingEdgeLineDisplayColor		= Color.lightGray;
	public static final Color					 defaultUncoloredEdgeHandleDisplayColor		= Color.lightGray;
	public static final Color					 defaultEdgeLabelDisplayColor				= Color.black;
	
	public static final Color					 defaultSelectedVertexFillDisplayColor		= new Color(150, 150, 200);
	public static final Color					 defaultSelectedVertexLineDisplayColor		= Color.blue;
	public static final Color					 defaultSelectedvertexLabelDisplayColor		= Color.black;
	
	public static final Color					 defaultSelectedEdgeLineDisplayColor		= Color.blue;
	public static final Color					 defaultSelectedEdgeHandleDisplayColor		= Color.blue;
	public static final Color					 defaultSelectedEdgeLabelDisplayColor		= Color.blue;
	
	public static final double					 defaultDirectedEdgeArrowRatio				= 4.0;
	
	public static final Color					 defaultCaptionTextDisplayColor				= Color.black;
	public static final Color					 defaultSelectedCaptionTextDisplayColor		= new Color(0, 0, 150);
	
	public static final Color					 defaultCrossingDisplayColor				= Color.red;
	public static final double					 defaultCrossingRadius						= 2.5;
	
	// Initialized with black, gray, and 6 colors--past 6 hues with no saturation or brightness difference, some of the colors become difficult to distinguish.
	public static final Color[]					 defaultElementDisplayColors				= new Color[] { new Color(193, 193, 193), new Color(185, 122, 27), Color.getHSBColor(0, .95f, .95f), Color.getHSBColor(1/6f, .95f, .95f), Color.getHSBColor(2/6f, .95f, .95f), Color.getHSBColor(3/6f, .95f, .95f), Color.getHSBColor(4/6f, .95f, .95f), Color.getHSBColor(5/6f, .95f, .95f) };
	public static final Color					 defaultElementDisplayColor					= Color.black;
	
	public static final boolean					 defaultShowCrossings						= false;
	public static final boolean					 defaultShowEdgeHandles						= true;
	public static final boolean					 defaultShowEdgeWeights						= false;
	public static final boolean					 defaultShowEdgeLabels						= false;
	public static final boolean					 defaultShowVertexWeights					= false;
	public static final boolean					 defaultShowVertexLabels					= true;
	public static final boolean					 defaultShowCaptions						= true;
	public static final boolean					 defaultShowCaptionHandles					= true;
	public static final boolean					 defaultShowCaptionEdits					= true;
	
	public static final boolean					 drawAntiAliased							= true;
	public static final boolean					 drawStrokePure								= true;
	public static final boolean					 drawBicubicInterpolation					= true;
	
	public static final double					 defaultEdgeWeight							= 1.0;
	public static final int						 defaultEdgeColor							= -1;
	public static final String					 defaultEdgePrefix							= "e";
	public static final boolean					 defaultEdgeIsSelected						= false;
	public static final double					 defaultEdgeThickness						= 2.0;
	public static final double					 defaultEdgeHandleRadiusRatio				= 1.5;
	public static final double					 defaultEdgeHandleClickMargin				= 2.5;
	
	public static final Point2D					 defaultVertexLocation						= new Point2D.Double(0.0, 0.0);
	public static final double					 defaultVertexWeight						= 1.0;
	public static final int						 defaultVertexColor							= -1;
	public static final String					 defaultVertexPrefix						= "v";
	public static final boolean					 defaultVertexIsSelected					= false;
	public static final double					 defaultVertexRadius						= 5.0;
	public static final double					 defaultVertexClickMargin					= 2.5;
	
	public static final String					 defaultCaptionText							= "";
	public static final boolean					 defaultCaptionIsSelected					= false;
	public static final double					 defaultCaptionHandleClickMargin			= 2.5;
	public static final double					 defaultCaptionEditorClickMargin			= 2.5;
	
	public static final double					 snapGridSize								= 50.0;
	public static final double					 edgeSnapToLineMarginRatio					= 0.05;
	public static final double					 areCloseDistance							= 0.01;
	
	public static final double					 arrangeCircleRadiusMultiplier				= 10.0;
	public static final double					 arrangeGridSpacing							= 100.0;
	
	public static final double					 attractiveForce							= 0.0000001;
	public static final double					 repulsiveForce								= -80.0;
	public static final double					 applyForcesDecelerationFactor				= 1.1;
	
	public static final double					 zoomInFactor								= 1.2;
	public static final double					 zoomOutFactor								= 0.8;
	public static final double					 maximumZoomFactor							= 2.0;
	
	public static final double					 zoomGraphPadding							= 20;
	
	public static final double					 arrowKeyIncrement							= 50;
	public static final double					 scrollIncrementZoom						= 0.1;
	
	public static final Dimension				 defaultMainWindowSize						= new Dimension(1000, 700);
	public static final Dimension				 defaultGraphWindowSize						= new Dimension(640, 480);
	
	public static final int						 cascadeWindowsOffset						= 15;
	
	public static final double					 defaultLoopDiameter						= 50.0;
	
	public static final int						 paintToolButtonDelay						= 750;

	public static final GraphGeneratorBase[] 	 allGraphGenerators							= new GraphGeneratorBase[] { new EmptyGraphGenerator(), new CycleGraphGenerator(), new CompleteGraphGenerator(), new CompleteBipartiteGraphGenerator(), new SymmetricTreeGraphGenerator(), new CartesianProductOfCompleteBipartiteGraphAndCycleGeneratorScott(), new CartesianProductOfCompleteBipartiteGraphAndCycleGeneratorBehar() };
	public static final FunctionBase[]		 	 allFunctions								= new FunctionBase[] { new CountEdgesFunction(), new CountVertexesFunction(), new CountCrossingsFunction(), new IsEulerianFunction(), new IsConnectedFunction() };
}
