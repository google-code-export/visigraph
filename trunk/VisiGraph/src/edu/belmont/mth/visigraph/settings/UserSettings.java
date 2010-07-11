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
	public final Property<Double>  panDecelerationFactor;
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
		this.panDecelerationFactor			= new Property<Double> (GlobalSettings.defaultPanDecelerationFactor,			"panDecelerationFactor"			);
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
		
		if(members.containsKey("defaultVertexWeight"))			defaultVertexWeight			 .set( (Double)  members.get( "defaultVertexWeight"				) );
		if(members.containsKey("defaultVertexColor"))			defaultVertexColor			 .set( (Integer) members.get( "defaultVertexColor"				) );
		if(members.containsKey("defaultVertexPrefix"))			defaultVertexPrefix			 .set( (String)  members.get( "defaultVertexPrefix"				) );
		if(members.containsKey("defaultVertexRadius"))			defaultVertexRadius			 .set( (Double)  members.get( "defaultVertexRadius"				) );
		if(members.containsKey("defaultVertexIsSelected"))		defaultVertexIsSelected		 .set( (Boolean) members.get( "defaultVertexIsSelected"			) );
		if(members.containsKey("defaultEdgeWeight"))			defaultEdgeWeight			 .set( (Double)  members.get( "defaultEdgeWeight"				) );
		if(members.containsKey("defaultEdgeColor"))				defaultEdgeColor			 .set( (Integer) members.get( "defaultEdgeColor"				) );
		if(members.containsKey("defaultEdgePrefix"))			defaultEdgePrefix			 .set( (String)  members.get( "defaultEdgePrefix"				) );
		if(members.containsKey("defaultEdgeThickness"))			defaultEdgeThickness		 .set( (Double)  members.get( "defaultEdgeThickness"			) );
		if(members.containsKey("defaultEdgeHandleRadiusRatio"))	defaultEdgeHandleRadiusRatio .set( (Double)  members.get( "defaultEdgeHandleRadiusRatio"	) );
		if(members.containsKey("defaultLoopDiameter"))			defaultLoopDiameter			 .set( (Double)  members.get( "defaultLoopDiameter"				) );
		if(members.containsKey("defaultEdgeIsSelected"))		defaultEdgeIsSelected		 .set( (Boolean) members.get( "defaultEdgeIsSelected"			) );
		if(members.containsKey("defaultCaptionText"))			defaultCaptionText			 .set( (String)  members.get( "defaultCaptionText"				) );
		if(members.containsKey("defaultCaptionIsSelected"))		defaultCaptionIsSelected	 .set( (Boolean) members.get( "defaultCaptionIsSelected"		) );
		if(members.containsKey("defaultShowVertexWeights"))		defaultShowVertexWeights	 .set( (Boolean) members.get( "defaultShowVertexWeights"		) );
		if(members.containsKey("defaultShowVertexLabels"))		defaultShowVertexLabels		 .set( (Boolean) members.get( "defaultShowVertexLabels"			) );
		if(members.containsKey("defaultShowEdgeHandles"))		defaultShowEdgeHandles		 .set( (Boolean) members.get( "defaultShowEdgeHandles"			) );
		if(members.containsKey("defaultShowEdgeWeights"))		defaultShowEdgeWeights		 .set( (Boolean) members.get( "defaultShowEdgeWeights"			) );
		if(members.containsKey("defaultShowCaptions"))			defaultShowCaptions			 .set( (Boolean) members.get( "defaultShowCaptions"				) );
		if(members.containsKey("defaultShowCaptionHandles"))	defaultShowCaptionHandles	 .set( (Boolean) members.get( "defaultShowCaptionHandles"		) );
		if(members.containsKey("defaultShowCaptionEditors"))	defaultShowCaptionEditors	 .set( (Boolean) members.get( "defaultShowCaptionEditors"		) );
		if(members.containsKey("graphBackground"))				graphBackground				 .set( (Color)   members.get( "graphBackground"					) );
		if(members.containsKey("selectionBoxFill"))				selectionBoxFill			 .set( (Color)   members.get( "selectionBoxFill"				) );
		if(members.containsKey("selectionBoxLine"))				selectionBoxLine			 .set( (Color)   members.get( "selectionBoxLine"				) );
		if(members.containsKey("vertexLine"))					vertexLine					 .set( (Color)   members.get( "vertexLine"		 				) );
		if(members.containsKey("selectedVertexFill"))			selectedVertexFill			 .set( (Color)   members.get( "selectedVertexFill"				) );
		if(members.containsKey("selectedVertexLine"))			selectedVertexLine			 .set( (Color)   members.get( "selectedVertexLine"				) );
		if(members.containsKey("draggingHandleEdge"))			draggingHandleEdge			 .set( (Color)   members.get( "draggingHandleEdge"				) );
		if(members.containsKey("draggingEdge"))					draggingEdge				 .set( (Color)   members.get( "draggingEdge"					) );
		if(members.containsKey("uncoloredEdgeHandle"))			uncoloredEdgeHandle			 .set( (Color)   members.get( "uncoloredEdgeHandle"				) );
		if(members.containsKey("selectedEdge"))					selectedEdge				 .set( (Color)   members.get( "selectedEdge"					) );
		if(members.containsKey("selectedEdgeHandle"))			selectedEdgeHandle			 .set( (Color)   members.get( "selectedEdgeHandle"				) );
		if(members.containsKey("captionText"))					captionText					 .set( (Color)   members.get( "captionText"						) );
		if(members.containsKey("selectedCaptionText"))			selectedCaptionText			 .set( (Color)   members.get( "selectedCaptionText"				) );
		if(members.containsKey("uncoloredElementFill"))			uncoloredElementFill		 .set( (Color)   members.get( "uncoloredElementFill"			) );
		if(members.containsKey("vertexClickMargin"))			vertexClickMargin			 .set( (Double)  members.get( "vertexClickMargin"				) );
		if(members.containsKey("edgeHandleClickMargin"))		edgeHandleClickMargin		 .set( (Double)  members.get( "edgeHandleClickMargin"			) );
		if(members.containsKey("captionHandleClickMargin"))		captionHandleClickMargin	 .set( (Double)  members.get( "captionHandleClickMargin"		) );
		if(members.containsKey("captionEditorClickMargin"))		captionEditorClickMargin	 .set( (Double)  members.get( "captionEditorClickMargin"		) );
		if(members.containsKey("panDecelerationFactor"))		panDecelerationFactor		 .set( (Double)  members.get( "panDecelerationFactor"			) );
		if(members.containsKey("zoomInFactor"))					zoomInFactor				 .set( (Double)  members.get( "zoomInFactor"					) );
		if(members.containsKey("zoomOutFactor"))				zoomOutFactor				 .set( (Double)  members.get( "zoomOutFactor"					) );
		if(members.containsKey("maximumZoomFactor"))			maximumZoomFactor			 .set( (Double)  members.get( "maximumZoomFactor"				) );
		if(members.containsKey("zoomGraphPadding"))				zoomGraphPadding			 .set( (Double)  members.get( "zoomGraphPadding"				) );
		if(members.containsKey("scrollIncrementZoom"))			scrollIncrementZoom			 .set( (Double)  members.get( "scrollIncrementZoom"				) );
		if(members.containsKey("arrangeCircleRadiusMultiplier"))arrangeCircleRadiusMultiplier.set( (Double)  members.get( "arrangeCircleRadiusMultiplier"	) );
		if(members.containsKey("arrangeGridSpacing"))			arrangeGridSpacing			 .set( (Double)  members.get( "arrangeGridSpacing"				) );
		if(members.containsKey("autoArrangeAttractiveForce"))	autoArrangeAttractiveForce	 .set( (Double)  members.get( "autoArrangeAttractiveForce"		) );
		if(members.containsKey("autoArrangeRepulsiveForce"))	autoArrangeRepulsiveForce	 .set( (Double)  members.get( "autoArrangeRepulsiveForce"		) );
		if(members.containsKey("autoArrangeDecelerationFactor"))autoArrangeDecelerationFactor.set( (Double)  members.get( "autoArrangeDecelerationFactor"	) );
		if(members.containsKey("useAntiAliasing"))				useAntiAliasing				 .set( (Boolean) members.get( "useAntiAliasing"					) );
		if(members.containsKey("usePureStroke"))				usePureStroke				 .set( (Boolean) members.get( "usePureStroke"					) );
		if(members.containsKey("useBicubicInterpolation"))		useBicubicInterpolation		 .set( (Boolean) members.get( "useBicubicInterpolation"			) );
		if(members.containsKey("mainWindowWidth"))				mainWindowWidth				 .set( (Integer) members.get( "mainWindowWidth"					) );
		if(members.containsKey("mainWindowHeight"))				mainWindowHeight			 .set( (Integer) members.get( "mainWindowHeight"				) );
		if(members.containsKey("graphWindowWidth"))				graphWindowWidth			 .set( (Integer) members.get( "graphWindowWidth"				) );
		if(members.containsKey("graphWindowHeight"))			graphWindowHeight			 .set( (Integer) members.get( "graphWindowHeight"				) );
		if(members.containsKey("cascadeWindowOffset"))			cascadeWindowOffset			 .set( (Integer) members.get( "cascadeWindowOffset"				) );
		if(members.containsKey("defaultGraphName"))				defaultGraphName			 .set( (String)  members.get( "defaultGraphName"				) );
		if(members.containsKey("directedEdgeArrowRatio"))		directedEdgeArrowRatio		 .set( (Double)  members.get( "directedEdgeArrowRatio"			) );
		if(members.containsKey("arrowKeyIncrement"))			arrowKeyIncrement			 .set( (Double)  members.get( "arrowKeyIncrement"				) );
		if(members.containsKey("edgeSnapMarginRatio"))			edgeSnapMarginRatio			 .set( (Double)  members.get( "edgeSnapMarginRatio"				) );
		if(members.containsKey("areCloseDistance"))				areCloseDistance			 .set( (Double)  members.get( "areCloseDistance"				) );
		if(members.containsKey("paintToolMenuDelay"))			paintToolMenuDelay			 .set( (Integer) members.get( "paintToolMenuDelay"				) );
		
		elementColors.clear();
		if(members.containsKey("elementColors"))
			for(Object color : (Iterable<?>)members.get("elementColors"))
				elementColors.add((Color) color);
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
		members.put("panDecelerationFactor",		panDecelerationFactor			);
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














