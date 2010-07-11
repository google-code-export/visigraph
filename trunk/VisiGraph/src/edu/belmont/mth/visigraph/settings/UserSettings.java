/**
 * UserSettings.java
 */
package edu.belmont.mth.visigraph.settings;

import java.awt.*;
import java.util.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.utilities.JsonUtilities;
import edu.belmont.mth.visigraph.views.ObserverBase;

/**
 * @author Cameron Behar
 *
 */
public class UserSettings extends ObservableBase
{
	public final static UserSettings instance = new UserSettings();
	
	public final Property<Double>  defaultVertexWeight;
	public final Property<Integer> defaultVertexColor;
	public final Property<String>  defaultVertexPrefix;
	public final Property<Double>  defaultVertexRadius;
	public final Property<Boolean> defaultVertexIsSelected;
	public final Property<Double>  defaultEdgeWeight;
	public final Property<Integer> defaultEdgeColor;
	public final Property<String>  defaultEdgePrefix;
	public final Property<Double>  defaultEdgeThickness;
	public final Property<Double>  defaultEdgeHandleRadiusRatio;
	public final Property<Double>  defaultLoopDiameter;
	public final Property<Boolean> defaultEdgeIsSelected;
	public final Property<String>  defaultCaptionText;
	public final Property<Boolean> defaultCaptionIsSelected;
	public final Property<Boolean> defaultShowVertexWeights;
	public final Property<Boolean> defaultShowVertexLabels;
	public final Property<Boolean> defaultShowEdgeHandles;
	public final Property<Boolean> defaultShowEdgeWeights;
	public final Property<Boolean> defaultShowCaptions;
	public final Property<Boolean> defaultShowCaptionHandles;
	public final Property<Boolean> defaultShowCaptionEditors;
	public final Property<Color>   graphBackground;
	public final Property<Color>   selectionBoxFill;
	public final Property<Color>   selectionBoxLine;
	public final Property<Color>   vertexLine;
	public final Property<Color>   selectedVertexFill;
	public final Property<Color>   selectedVertexLine;
	public final Property<Color>   draggingHandleEdge;
	public final Property<Color>   draggingEdge;
	public final Property<Color>   uncoloredEdgeHandle;
	public final Property<Color>   selectedEdge;
	public final Property<Color>   selectedEdgeHandle;
	public final Property<Color>   captionText;
	public final Property<Color>   selectedCaptionText;
	public final Property<Color>   uncoloredElementFill;
	public final ObservableList<Color> elementColors;
	public final Property<Double>  vertexClickMargin;
	public final Property<Double>  edgeHandleClickMargin;
	public final Property<Double>  captionHandleClickMargin;
	public final Property<Double>  captionEditorClickMargin;
	public final Property<Double>  zoomInFactor;
	public final Property<Double>  zoomOutFactor;
	public final Property<Double>  maximumZoomFactor;
	public final Property<Double>  zoomGraphPadding;
	public final Property<Double>  scrollIncrementZoom;
	public final Property<Double>  arrangeCircleRadiusMultiplier;
	public final Property<Double>  arrangeGridSpacing;
	public final Property<Double>  autoArrangeAttractiveForce;
	public final Property<Double>  autoArrangeRepulsiveForce;
	public final Property<Double>  autoArrangeDecelerationFactor;
	public final Property<Boolean> useAntiAliasing;
	public final Property<Boolean> usePureStroke;
	public final Property<Boolean> useBicubicInterpolation;
	public final Property<Integer> mainWindowWidth;
	public final Property<Integer> mainWindowHeight;
	public final Property<Integer> graphWindowWidth;
	public final Property<Integer> graphWindowHeight;
	public final Property<Integer> cascadeWindowOffset;
	public final Property<String>  defaultGraphName;
	public final Property<Double>  directedEdgeArrowRatio;
	public final Property<Double>  arrowKeyIncrement;
	public final Property<Double>  edgeSnapMarginRatio;
	public final Property<Double>  areCloseDistance;
	public final Property<Integer> paintToolMenuDelay;
	
	private UserSettings ( )
	{
		this.defaultVertexWeight			= new Property<Double> (GlobalSettings.defaultVertexWeight,						"defaultVertexWeight"			);
		this.defaultVertexColor				= new Property<Integer>(GlobalSettings.defaultVertexColor,						"defaultVertexColor"			);
		this.defaultVertexPrefix			= new Property<String> (GlobalSettings.defaultVertexPrefix,						"defaultVertexPrefix"			);
		this.defaultVertexRadius			= new Property<Double> (GlobalSettings.defaultVertexRadius,						"defaultVertexRadius"			);
		this.defaultVertexIsSelected		= new Property<Boolean>(GlobalSettings.defaultVertexIsSelected,					"defaultVertexIsSelected"		);
		this.defaultEdgeWeight				= new Property<Double> (GlobalSettings.defaultEdgeWeight,						"defaultEdgeWeight"				);
		this.defaultEdgeColor				= new Property<Integer>(GlobalSettings.defaultEdgeColor,						"defaultEdgeColor"				);
		this.defaultEdgePrefix				= new Property<String> (GlobalSettings.defaultEdgePrefix,						"defaultEdgePrefix"				);
		this.defaultEdgeThickness			= new Property<Double> (GlobalSettings.defaultEdgeThickness,					"defaultEdgeThickness"			);
		this.defaultEdgeHandleRadiusRatio	= new Property<Double> (GlobalSettings.defaultEdgeHandleRadiusRatio,			"defaultEdgeHandleRadiusRatio"	);
		this.defaultLoopDiameter			= new Property<Double> (GlobalSettings.defaultLoopDiameter,						"defaultLoopDiameter"			);
		this.defaultEdgeIsSelected			= new Property<Boolean>(GlobalSettings.defaultEdgeIsSelected,					"defaultEdgeIsSelected"			);
		this.defaultCaptionText				= new Property<String> (GlobalSettings.defaultCaptionText,						"defaultCaptionText"			);
		this.defaultCaptionIsSelected		= new Property<Boolean>(GlobalSettings.defaultCaptionIsSelected,				"defaultCaptionIsSelected"		);
		this.defaultShowVertexWeights		= new Property<Boolean>(GlobalSettings.defaultShowVertexWeights,				"defaultShowVertexWeights"		);
		this.defaultShowVertexLabels		= new Property<Boolean>(GlobalSettings.defaultShowVertexLabels,					"defaultShowVertexLabels"		);
		this.defaultShowEdgeHandles			= new Property<Boolean>(GlobalSettings.defaultShowEdgeHandles,					"defaultShowEdgeHandles"		);
		this.defaultShowEdgeWeights			= new Property<Boolean>(GlobalSettings.defaultShowEdgeWeights,					"defaultShowEdgeWeights"		);
		this.defaultShowCaptions			= new Property<Boolean>(GlobalSettings.defaultShowCaptions,						"defaultShowCaptions"			);
		this.defaultShowCaptionHandles		= new Property<Boolean>(GlobalSettings.defaultShowCaptionHandles,				"defaultShowCaptionHandles"		);
		this.defaultShowCaptionEditors		= new Property<Boolean>(GlobalSettings.defaultShowCaptionEditors,				"defaultShowCaptionEditors"		);
		this.graphBackground				= new Property<Color>  (GlobalSettings.defaultGraphBackgroundFill,				"graphBackground"				);
		this.selectionBoxFill				= new Property<Color>  (GlobalSettings.defaultSelectionBoxFill,					"selectionBoxFill"				);
		this.selectionBoxLine				= new Property<Color>  (GlobalSettings.defaultSelectionBoxLine,					"selectionBoxLine"				);
		this.vertexLine						= new Property<Color>  (GlobalSettings.defaultVertexLine,						"vertexLine"					);
		this.selectedVertexFill				= new Property<Color>  (GlobalSettings.defaultSelectedVertexFill,				"selectedVertexFill"			);
		this.selectedVertexLine				= new Property<Color>  (GlobalSettings.defaultSelectedVertexLine,				"selectedVertexLine"			);
		this.draggingHandleEdge				= new Property<Color>  (GlobalSettings.defaultDraggingHandleEdgeLine,			"draggingHandleEdge"			);
		this.draggingEdge					= new Property<Color>  (GlobalSettings.defaultDraggingEdgeLine,					"draggingEdge"					);
		this.uncoloredEdgeHandle			= new Property<Color>  (GlobalSettings.defaultUncoloredEdgeHandleFill,			"uncoloredEdgeHandle"			);
		this.selectedEdge					= new Property<Color>  (GlobalSettings.defaultSelectedEdgeLine,					"selectedEdge"					);
		this.selectedEdgeHandle				= new Property<Color>  (GlobalSettings.defaultSelectedEdgeHandleFill,			"selectedEdgeHandle"			);
		this.captionText					= new Property<Color>  (GlobalSettings.defaultCaptionTextDisplayColor,			"captionText"					);
		this.selectedCaptionText			= new Property<Color>  (GlobalSettings.defaultSelectedCaptionTextDisplayColor,	"selectedCaptionText"			);
		this.uncoloredElementFill			= new Property<Color>  (GlobalSettings.defaultUncoloredElementFill,				"uncoloredElement"				);
		this.vertexClickMargin				= new Property<Double> (GlobalSettings.defaultVertexClickMargin,				"vertexClickMargin"				);
		this.edgeHandleClickMargin			= new Property<Double> (GlobalSettings.defaultEdgeHandleClickMargin,			"edgeHandleClickMargin"			);
		this.captionHandleClickMargin		= new Property<Double> (GlobalSettings.defaultCaptionHandleClickMargin,			"captionHandleClickMargin"		);
		this.captionEditorClickMargin		= new Property<Double> (GlobalSettings.defaultCaptionEditorClickMargin,			"captionEditorClickMargin"		);
		this.zoomInFactor					= new Property<Double> (GlobalSettings.defaultZoomInFactor,						"zoomInFactor"					);
		this.zoomOutFactor					= new Property<Double> (GlobalSettings.defaultZoomOutFactor,					"zoomOutFactor"					);
		this.maximumZoomFactor				= new Property<Double> (GlobalSettings.defaultMaximumZoomFactor,				"maximumZoomFactor"				);
		this.zoomGraphPadding				= new Property<Double> (GlobalSettings.defaultZoomGraphPadding,					"zoomGraphPadding"				);
		this.scrollIncrementZoom			= new Property<Double> (GlobalSettings.defaultScrollIncrementZoom,				"scrollIncrementZoom"			);
		this.arrangeCircleRadiusMultiplier	= new Property<Double> (GlobalSettings.defaultArrangeCircleRadiusMultiplier,	"arrangeCircleRadiusMultiplier"	);
		this.arrangeGridSpacing				= new Property<Double> (GlobalSettings.defaultArrangeGridSpacing,				"arrangeGridSpacing"			);
		this.autoArrangeAttractiveForce		= new Property<Double> (GlobalSettings.defaultAutoArrangeAttractiveForce,		"autoArrangeAttractiveForce"	);
		this.autoArrangeRepulsiveForce		= new Property<Double> (GlobalSettings.defaultAutoArrangeRepulsiveForce,		"autoArrangeRepulsiveForce"		);
		this.autoArrangeDecelerationFactor	= new Property<Double> (GlobalSettings.defaultAutoArrangeDecelerationFactor,	"autoArrangeDecelerationFactor"	);
		this.useAntiAliasing				= new Property<Boolean>(GlobalSettings.defaultUseAntiAliasing,					"useAntiAliasing"				);
		this.usePureStroke					= new Property<Boolean>(GlobalSettings.defaultUsePureStroke,					"usePureStroke"					);
		this.useBicubicInterpolation		= new Property<Boolean>(GlobalSettings.drawBicubicInterpolation,				"useBicubicInterpolation"		);
		this.mainWindowWidth				= new Property<Integer>(GlobalSettings.defaultMainWindowWidth,					"mainWindowWidth"				);
		this.mainWindowHeight				= new Property<Integer>(GlobalSettings.defaultMainWindowHeight,					"mainWindowHeight"				);
		this.graphWindowWidth				= new Property<Integer>(GlobalSettings.defaultGraphWindowWidth,					"graphWindowWidth"				);
		this.graphWindowHeight				= new Property<Integer>(GlobalSettings.defaultGraphWindowHeight,				"graphWindowHeight"				);
		this.cascadeWindowOffset			= new Property<Integer>(GlobalSettings.defaultCascadeWindowOffset,				"cascadeWindowOffset"			);
		this.defaultGraphName				= new Property<String> (GlobalSettings.defaultGraphName,						"defaultGraphName"				);
		this.directedEdgeArrowRatio			= new Property<Double> (GlobalSettings.defaultDirectedEdgeArrowRatio,			"directedEdgeArrowRatio"		);
		this.arrowKeyIncrement				= new Property<Double> (GlobalSettings.defaultArrowKeyIncrement,				"arrowKeyIncrement"				);
		this.edgeSnapMarginRatio			= new Property<Double> (GlobalSettings.defaultEdgeSnapMarginRatio,				"edgeSnapMarginRatio"			);
		this.areCloseDistance				= new Property<Double> (GlobalSettings.defaultAreCloseDistance,					"areCloseDistance"				);
		this.paintToolMenuDelay				= new Property<Integer>(GlobalSettings.defaultPaintToolMenuDelay,				"paintToolMenuDelay"			);
		
		this.elementColors = new ObservableList<Color>("elementColors");
		this.elementColors.addAll(Arrays.asList(GlobalSettings.defaultElementColors));
		this.elementColors.addObserver(new ObserverBase()
		{
			public void hasChanged(Object source)
			{
				notifyObservers(source);
			}
		} );
	}
	
	public Color getElementColor(int i)
	{
		return (i < 0 || i >= elementColors.size() ? uncoloredElementFill.get() : elementColors.get(i));
	}
	
	public void fromString (String json)
	{
		Map<String, Object> members = JsonUtilities.parseObject(json);
		
		if(members.containsKey("defaultVertexWeight"))			defaultVertexWeight			 .set( new Double ( members.get( "defaultVertexWeight"				).toString( ) ) );
		if(members.containsKey("defaultVertexColor"))			defaultVertexColor			 .set( new Integer( members.get( "defaultVertexColor"				).toString( ) ) );
		if(members.containsKey("defaultVertexPrefix"))			defaultVertexPrefix			 .set(              members.get( "defaultVertexPrefix"				).toString( )   );
		if(members.containsKey("defaultVertexRadius"))			defaultVertexRadius			 .set( new Double ( members.get( "defaultVertexRadius"				).toString( ) ) );
		if(members.containsKey("defaultVertexIsSelected"))		defaultVertexIsSelected		 .set( new Boolean( members.get( "defaultVertexIsSelected"			).toString( ) ) );
		if(members.containsKey("defaultEdgeWeight"))			defaultEdgeWeight			 .set( new Double ( members.get( "defaultEdgeWeight"				).toString( ) ) );
		if(members.containsKey("defaultEdgeColor"))				defaultEdgeColor			 .set( new Integer( members.get( "defaultEdgeColor"					).toString( ) ) );
		if(members.containsKey("defaultEdgePrefix"))			defaultEdgePrefix			 .set(              members.get( "defaultEdgePrefix"				).toString( )   );
		if(members.containsKey("defaultEdgeThickness"))			defaultEdgeThickness		 .set( new Double ( members.get( "defaultEdgeThickness"				).toString( ) ) );
		if(members.containsKey("defaultEdgeHandleRadiusRatio"))	defaultEdgeHandleRadiusRatio .set( new Double ( members.get( "defaultEdgeHandleRadiusRatio"		).toString( ) ) );
		if(members.containsKey("defaultLoopDiameter"))			defaultLoopDiameter			 .set( new Double ( members.get( "defaultLoopDiameter"				).toString( ) ) );
		if(members.containsKey("defaultEdgeIsSelected"))		defaultEdgeIsSelected		 .set( new Boolean( members.get( "defaultEdgeIsSelected"			).toString( ) ) );
		if(members.containsKey("defaultCaptionText"))			defaultCaptionText			 .set(              members.get( "defaultCaptionText"				).toString( )   );
		if(members.containsKey("defaultCaptionIsSelected"))		defaultCaptionIsSelected	 .set( new Boolean( members.get( "defaultCaptionIsSelected"			).toString( ) ) );
		if(members.containsKey("defaultShowVertexWeights"))		defaultShowVertexWeights	 .set( new Boolean( members.get( "defaultShowVertexWeights"			).toString( ) ) );
		if(members.containsKey("defaultShowVertexLabels"))		defaultShowVertexLabels		 .set( new Boolean( members.get( "defaultShowVertexLabels"			).toString( ) ) );
		if(members.containsKey("defaultShowEdgeHandles"))		defaultShowEdgeHandles		 .set( new Boolean( members.get( "defaultShowEdgeHandles"			).toString( ) ) );
		if(members.containsKey("defaultShowEdgeWeights"))		defaultShowEdgeWeights		 .set( new Boolean( members.get( "defaultShowEdgeWeights"			).toString( ) ) );
		if(members.containsKey("defaultShowCaptions"))			defaultShowCaptions			 .set( new Boolean( members.get( "defaultShowCaptions"				).toString( ) ) );
		if(members.containsKey("defaultShowCaptionHandles"))	defaultShowCaptionHandles	 .set( new Boolean( members.get( "defaultShowCaptionHandles"		).toString( ) ) );
		if(members.containsKey("defaultShowCaptionEditors"))	defaultShowCaptionEditors	 .set( new Boolean( members.get( "defaultShowCaptionEditors"		).toString( ) ) );
		if(members.containsKey("graphBackground"))				graphBackground				 .set( new Color  ( new Integer( members.get( "graphBackground"		).toString( ) ) ) );
		if(members.containsKey("selectionBoxFill"))				selectionBoxFill			 .set( new Color  ( new Integer( members.get( "selectionBoxFill"	).toString( ) ) ) );
		if(members.containsKey("selectionBoxLine"))				selectionBoxLine			 .set( new Color  ( new Integer( members.get( "selectionBoxLine"	).toString( ) ) ) );
		if(members.containsKey("vertexLine"))					vertexLine					 .set( new Color  ( new Integer( members.get( "vertexLine"			).toString( ) ) ) );
		if(members.containsKey("selectedVertexFill"))			selectedVertexFill			 .set( new Color  ( new Integer( members.get( "selectedVertexFill"	).toString( ) ) ) );
		if(members.containsKey("selectedVertexLine"))			selectedVertexLine			 .set( new Color  ( new Integer( members.get( "selectedVertexLine"	).toString( ) ) ) );
		if(members.containsKey("draggingHandleEdge"))			draggingHandleEdge			 .set( new Color  ( new Integer( members.get( "draggingHandleEdge"	).toString( ) ) ) );
		if(members.containsKey("draggingEdge"))					draggingEdge				 .set( new Color  ( new Integer( members.get( "draggingEdge"		).toString( ) ) ) );
		if(members.containsKey("uncoloredEdgeHandle"))			uncoloredEdgeHandle			 .set( new Color  ( new Integer( members.get( "uncoloredEdgeHandle"	).toString( ) ) ) );
		if(members.containsKey("selectedEdge"))					selectedEdge				 .set( new Color  ( new Integer( members.get( "selectedEdge"		).toString( ) ) ) );
		if(members.containsKey("selectedEdgeHandle"))			selectedEdgeHandle			 .set( new Color  ( new Integer( members.get( "selectedEdgeHandle"	).toString( ) ) ) );
		if(members.containsKey("captionText"))					captionText					 .set( new Color  ( new Integer( members.get( "captionText"			).toString( ) ) ) );
		if(members.containsKey("selectedCaptionText"))			selectedCaptionText			 .set( new Color  ( new Integer( members.get( "selectedCaptionText"	).toString( ) ) ) );
		if(members.containsKey("uncoloredElementFill"))			uncoloredElementFill		 .set( new Color  ( new Integer( members.get( "uncoloredElementFill").toString( ) ) ) );
		if(members.containsKey("vertexClickMargin"))			vertexClickMargin			 .set( new Double ( members.get( "vertexClickMargin"				).toString( ) ) );
		if(members.containsKey("edgeHandleClickMargin"))		edgeHandleClickMargin		 .set( new Double ( members.get( "edgeHandleClickMargin"			).toString( ) ) );
		if(members.containsKey("captionHandleClickMargin"))		captionHandleClickMargin	 .set( new Double ( members.get( "captionHandleClickMargin"			).toString( ) ) );
		if(members.containsKey("captionEditorClickMargin"))		captionEditorClickMargin	 .set( new Double ( members.get( "captionEditorClickMargin"			).toString( ) ) );
		if(members.containsKey("zoomInFactor"))					zoomInFactor				 .set( new Double ( members.get( "zoomInFactor"						).toString( ) ) );
		if(members.containsKey("zoomOutFactor"))				zoomOutFactor				 .set( new Double ( members.get( "zoomOutFactor"					).toString( ) ) );
		if(members.containsKey("maximumZoomFactor"))			maximumZoomFactor			 .set( new Double ( members.get( "maximumZoomFactor"				).toString( ) ) );
		if(members.containsKey("zoomGraphPadding"))				zoomGraphPadding			 .set( new Double ( members.get( "zoomGraphPadding"					).toString( ) ) );
		if(members.containsKey("scrollIncrementZoom"))			scrollIncrementZoom			 .set( new Double ( members.get( "scrollIncrementZoom"				).toString( ) ) );
		if(members.containsKey("arrangeCircleRadiusMultiplier"))arrangeCircleRadiusMultiplier.set( new Double ( members.get( "arrangeCircleRadiusMultiplier"	).toString( ) ) );
		if(members.containsKey("arrangeGridSpacing"))			arrangeGridSpacing			 .set( new Double ( members.get( "arrangeGridSpacing"				).toString( ) ) );
		if(members.containsKey("autoArrangeAttractiveForce"))	autoArrangeAttractiveForce	 .set( new Double ( members.get( "autoArrangeAttractiveForce"		).toString( ) ) );
		if(members.containsKey("autoArrangeRepulsiveForce"))	autoArrangeRepulsiveForce	 .set( new Double ( members.get( "autoArrangeRepulsiveForce"		).toString( ) ) );
		if(members.containsKey("autoArrangeDecelerationFactor"))autoArrangeDecelerationFactor.set( new Double ( members.get( "autoArrangeDecelerationFactor"	).toString( ) ) );
		if(members.containsKey("useAntiAliasing"))				useAntiAliasing				 .set( new Boolean( members.get( "useAntiAliasing"					).toString( ) ) );
		if(members.containsKey("usePureStroke"))				usePureStroke				 .set( new Boolean( members.get( "usePureStroke"					).toString( ) ) );
		if(members.containsKey("useBicubicInterpolation"))		useBicubicInterpolation		 .set( new Boolean( members.get( "useBicubicInterpolation"			).toString( ) ) );
		if(members.containsKey("mainWindowWidth"))				mainWindowWidth				 .set( new Integer( members.get( "mainWindowWidth"					).toString( ) ) );
		if(members.containsKey("mainWindowHeight"))				mainWindowHeight			 .set( new Integer( members.get( "mainWindowHeight"					).toString( ) ) );
		if(members.containsKey("graphWindowWidth"))				graphWindowWidth			 .set( new Integer( members.get( "graphWindowWidth"					).toString( ) ) );
		if(members.containsKey("graphWindowHeight"))			graphWindowHeight			 .set( new Integer( members.get( "graphWindowHeight"				).toString( ) ) );
		if(members.containsKey("cascadeWindowOffset"))			cascadeWindowOffset			 .set( new Integer( members.get( "cascadeWindowOffset"				).toString( ) ) );
		if(members.containsKey("defaultGraphName"))				defaultGraphName			 .set(              members.get( "defaultGraphName"					).toString( )   );
		if(members.containsKey("directedEdgeArrowRatio"))		directedEdgeArrowRatio		 .set( new Double ( members.get( "directedEdgeArrowRatio"			).toString( ) ) );
		if(members.containsKey("arrowKeyIncrement"))			arrowKeyIncrement			 .set( new Double ( members.get( "arrowKeyIncrement"				).toString( ) ) );
		if(members.containsKey("edgeSnapMarginRatio"))			edgeSnapMarginRatio			 .set( new Double ( members.get( "edgeSnapMarginRatio"				).toString( ) ) );
		if(members.containsKey("areCloseDistance"))				areCloseDistance			 .set( new Double ( members.get( "areCloseDistance"					).toString( ) ) );
		if(members.containsKey("paintToolMenuDelay"))			paintToolMenuDelay			 .set( new Integer( members.get( "paintToolMenuDelay"				).toString( ) ) );
		
		elementColors.clear();
		if(members.containsKey("elementColors"))
			for(Object color : (Iterable<?>)members.get("elementColors"))
				elementColors.add(new Color(new Integer(color.toString())));
	}
	
	public String toString ( )
	{
		HashMap<String, Object> members = new HashMap<String, Object>();
		
		members.put("defaultVertexWeight",			defaultVertexWeight				);
		members.put("defaultVertexColor",			defaultVertexColor				);
		members.put("defaultVertexPrefix",			defaultVertexPrefix				);
		members.put("defaultVertexRadius",			defaultVertexRadius				);
		members.put("defaultVertexIsSelected",		defaultVertexIsSelected			);
		members.put("defaultEdgeWeight",			defaultEdgeWeight				);
		members.put("defaultEdgeColor",				defaultEdgeColor				);
		members.put("defaultEdgePrefix",			defaultEdgePrefix				);
		members.put("defaultEdgeThickness",			defaultEdgeThickness			);
		members.put("defaultEdgeHandleRadiusRatio",	defaultEdgeHandleRadiusRatio	);
		members.put("defaultLoopDiameter",			defaultLoopDiameter				);
		members.put("defaultEdgeIsSelected",		defaultEdgeIsSelected			);
		members.put("defaultCaptionText",			defaultCaptionText				);
		members.put("defaultCaptionIsSelected",		defaultCaptionIsSelected		);
		members.put("defaultShowVertexWeights",		defaultShowVertexWeights		);
		members.put("defaultShowVertexLabels",		defaultShowVertexLabels			);
		members.put("defaultShowEdgeHandles",		defaultShowEdgeHandles			);
		members.put("defaultShowEdgeWeights",		defaultShowEdgeWeights			);
		members.put("defaultShowCaptions",			defaultShowCaptions				);
		members.put("defaultShowCaptionHandles",	defaultShowCaptionHandles		);
		members.put("defaultShowCaptionEditors",	defaultShowCaptionEditors		);
		members.put("graphBackground",				graphBackground					);
		members.put("selectionBoxFill",				selectionBoxFill				);
		members.put("selectionBoxLine",				selectionBoxLine				);
		members.put("vertexLine",					vertexLine						);
		members.put("selectedVertexFill",			selectedVertexFill				);
		members.put("selectedVertexLine",			selectedVertexLine				);
		members.put("draggingHandleEdge",			draggingHandleEdge				);
		members.put("draggingEdge",					draggingEdge					);
		members.put("uncoloredEdgeHandle",			uncoloredEdgeHandle				);
		members.put("selectedEdge",					selectedEdge					);
		members.put("selectedEdgeHandle",			selectedEdgeHandle				);
		members.put("captionText",					captionText						);
		members.put("selectedCaptionText",			selectedCaptionText				);
		members.put("uncoloredElementFill",			uncoloredElementFill			);
		members.put("elementColors",				elementColors					);
		members.put("vertexClickMargin",			vertexClickMargin				);
		members.put("edgeHandleClickMargin",		edgeHandleClickMargin			);
		members.put("captionHandleClickMargin",		captionHandleClickMargin		);
		members.put("captionEditorClickMargin",		captionEditorClickMargin		);
		members.put("zoomInFactor",					zoomInFactor					);
		members.put("zoomOutFactor",				zoomOutFactor					);
		members.put("maximumZoomFactor",			maximumZoomFactor				);
		members.put("zoomGraphPadding",				zoomGraphPadding				);
		members.put("scrollIncrementZoom",			scrollIncrementZoom				);
		members.put("arrangeCircleRadiusMultiplier",arrangeCircleRadiusMultiplier	);
		members.put("arrangeGridSpacing",			arrangeGridSpacing				);
		members.put("autoArrangeAttractiveForce",	autoArrangeAttractiveForce		);
		members.put("autoArrangeRepulsiveForce",	autoArrangeRepulsiveForce		);
		members.put("autoArrangeDecelerationFactor",autoArrangeDecelerationFactor	);
		members.put("useAntiAliasing",				useAntiAliasing					);
		members.put("usePureStroke",				usePureStroke					);
		members.put("useBicubicInterpolation",		useBicubicInterpolation			);
		members.put("mainWindowWidth",				mainWindowWidth					);
		members.put("mainWindowHeight",				mainWindowHeight				);
		members.put("graphWindowWidth",				graphWindowWidth				);
		members.put("graphWindowHeight",			graphWindowHeight				);
		members.put("cascadeWindowOffset",			cascadeWindowOffset				);
		members.put("defaultGraphName",				defaultGraphName				);
		members.put("directedEdgeArrowRatio",		directedEdgeArrowRatio			);
		members.put("arrowKeyIncrement",			arrowKeyIncrement				);
		members.put("edgeSnapMarginRatio",			edgeSnapMarginRatio				);
		members.put("areCloseDistance",				areCloseDistance				);
		members.put("paintToolMenuDelay",			paintToolMenuDelay				);

		return JsonUtilities.formatObject(members);
	}
}














