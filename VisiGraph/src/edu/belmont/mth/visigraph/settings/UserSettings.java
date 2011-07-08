/**
 * z * UserSettings.java
 */
package edu.belmont.mth.visigraph.settings;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.resources.StringBundle;
import edu.belmont.mth.visigraph.utilities.*;

/**
 * @author Cameron Behar
 */
public class UserSettings extends ObservableModel
{
	public final static UserSettings	instance	= new UserSettings( );
	
	public final Property<Double>		defaultVertexWeight;
	public final Property<Integer>		defaultVertexColor;
	public final Property<String>		defaultVertexPrefix;
	public final Property<Double>		defaultVertexRadius;
	public final Property<Boolean>		defaultVertexIsSelected;
	public final Property<Double>		defaultEdgeWeight;
	public final Property<Integer>		defaultEdgeColor;
	public final Property<String>		defaultEdgePrefix;
	public final Property<Double>		defaultEdgeThickness;
	public final Property<Double>		defaultEdgeHandleRadiusRatio;
	public final Property<Double>		defaultLoopDiameter;
	public final Property<Boolean>		defaultEdgeIsSelected;
	public final Property<String>		defaultCaptionText;
	public final Property<Double>		defaultCaptionFontSize;
	public final Property<Boolean>		defaultCaptionIsSelected;
	public final Property<Boolean>		defaultShowVertexWeights;
	public final Property<Boolean>		defaultShowVertexLabels;
	public final Property<Boolean>		defaultShowEdgeHandles;
	public final Property<Boolean>		defaultShowEdgeWeights;
	public final Property<Boolean>		defaultShowEdgeLabels;
	public final Property<Boolean>		defaultShowCaptions;
	public final Property<Boolean>		defaultShowCaptionHandles;
	public final Property<Boolean>		defaultShowCaptionEditors;
	public final Property<Color>		graphBackground;
	public final Property<Color>		selectionBoxFill;
	public final Property<Color>		selectionBoxLine;
	public final Property<Color>		vertexLine;
	public final Property<Color>		selectedVertexFill;
	public final Property<Color>		selectedVertexLine;
	public final Property<Color>		draggingEdge;
	public final Property<Color>		edgeHandle;
	public final Property<Color>		selectedEdge;
	public final Property<Color>		selectedEdgeHandle;
	public final Property<Color>		captionText;
	public final Property<Color>		captionButtonFill;
	public final Property<Color>		captionButtonLine;
	public final Property<Color>		selectedCaptionLine;
	public final Property<Color>		uncoloredEdgeLine;
	public final Property<Color>		uncoloredVertexFill;
	public final ObservableList<Color>	elementColors;
	public final Property<Double>		vertexClickMargin;
	public final Property<Double>		edgeHandleClickMargin;
	public final Property<Double>		captionHandleClickMargin;
	public final Property<Double>		captionEditorClickMargin;
	public final Property<Double>		panDecelerationFactor;
	public final Property<Boolean>		panOnDoubleClick;
	public final Property<Boolean>		deselectVertexWithNewEdge;
	public final Property<Double>		zoomInFactor;
	public final Property<Double>		zoomOutFactor;
	public final Property<Double>		maximumZoomFactor;
	public final Property<Double>		zoomGraphPadding;
	public final Property<Double>		scrollIncrementZoom;
	public final Property<Double>		arrangeCircleRadiusMultiplier;
	public final Property<Double>		arrangeGridSpacing;
	public final Property<Double>		autoArrangeAttractiveForce;
	public final Property<Double>		autoArrangeRepulsiveForce;
	public final Property<Double>		autoArrangeDecelerationFactor;
	public final Property<Double>		arrangeContractFactor;
	public final Property<Double>		arrangeExpandFactor;
	public final Property<Integer>		undoLoggingInterval;
	public final Property<Integer>		undoLoggingMaximum;
	public final Property<Boolean>		useAntiAliasing;
	public final Property<Boolean>		usePureStroke;
	public final Property<Boolean>		useBicubicInterpolation;
	public final Property<Boolean>		useFractionalMetrics;
	public final Property<Integer>		vertexWeightPrecision;
	public final Property<Integer>		edgeWeightPrecision;
	public final Property<Integer>		mainWindowWidth;
	public final Property<Integer>		mainWindowHeight;
	public final Property<Integer>		scriptLibraryWindowWidth;
	public final Property<Integer>		scriptLibraryWindowHeight;
	public final Property<Integer>		graphWindowWidth;
	public final Property<Integer>		graphWindowHeight;
	public final Property<Integer>		cascadeWindowOffset;
	public final Property<String>		language;
	public final Property<String>		defaultGraphName;
	public final Property<Double>		directedEdgeArrowRatio;
	public final Property<Double>		arrowKeyIncrement;
	public final Property<Double>		edgeSnapMarginRatio;
	public final Property<Double>		areCloseDistance;
	public final Property<Integer>		paintToolMenuDelay;
	public final Property<Boolean>		showDailyTipsOnStartup;
	public final Property<Integer>		dailyTipIndex;
	
	private UserSettings( )
	{
		this.defaultVertexWeight = new Property<Double>( GlobalSettings.defaultVertexWeight );
		this.defaultVertexColor = new Property<Integer>( GlobalSettings.defaultVertexColor );
		this.defaultVertexPrefix = new Property<String>( GlobalSettings.defaultVertexPrefix );
		this.defaultVertexRadius = new Property<Double>( GlobalSettings.defaultVertexRadius );
		this.defaultVertexIsSelected = new Property<Boolean>( GlobalSettings.defaultVertexIsSelected );
		this.defaultEdgeWeight = new Property<Double>( GlobalSettings.defaultEdgeWeight );
		this.defaultEdgeColor = new Property<Integer>( GlobalSettings.defaultEdgeColor );
		this.defaultEdgePrefix = new Property<String>( GlobalSettings.defaultEdgePrefix );
		this.defaultEdgeThickness = new Property<Double>( GlobalSettings.defaultEdgeThickness );
		this.defaultEdgeHandleRadiusRatio = new Property<Double>( GlobalSettings.defaultEdgeHandleRadiusRatio );
		this.defaultLoopDiameter = new Property<Double>( GlobalSettings.defaultLoopDiameter );
		this.defaultEdgeIsSelected = new Property<Boolean>( GlobalSettings.defaultEdgeIsSelected );
		this.defaultCaptionText = new Property<String>( GlobalSettings.defaultCaptionText );
		this.defaultCaptionFontSize = new Property<Double>( GlobalSettings.defaultCaptionFontSize );
		this.defaultCaptionIsSelected = new Property<Boolean>( GlobalSettings.defaultCaptionIsSelected );
		this.defaultShowVertexWeights = new Property<Boolean>( GlobalSettings.defaultShowVertexWeights );
		this.defaultShowVertexLabels = new Property<Boolean>( GlobalSettings.defaultShowVertexLabels );
		this.defaultShowEdgeHandles = new Property<Boolean>( GlobalSettings.defaultShowEdgeHandles );
		this.defaultShowEdgeWeights = new Property<Boolean>( GlobalSettings.defaultShowEdgeWeights );
		this.defaultShowEdgeLabels = new Property<Boolean>( GlobalSettings.defaultShowEdgeLabels );
		this.defaultShowCaptions = new Property<Boolean>( GlobalSettings.defaultShowCaptions );
		this.defaultShowCaptionHandles = new Property<Boolean>( GlobalSettings.defaultShowCaptionHandles );
		this.defaultShowCaptionEditors = new Property<Boolean>( GlobalSettings.defaultShowCaptionEditors );
		this.graphBackground = new Property<Color>( GlobalSettings.defaultGraphBackgroundFill );
		this.selectionBoxFill = new Property<Color>( GlobalSettings.defaultSelectionBoxFill );
		this.selectionBoxLine = new Property<Color>( GlobalSettings.defaultSelectionBoxLine );
		this.vertexLine = new Property<Color>( GlobalSettings.defaultVertexLine );
		this.selectedVertexFill = new Property<Color>( GlobalSettings.defaultSelectedVertexFill );
		this.selectedVertexLine = new Property<Color>( GlobalSettings.defaultSelectedVertexLine );
		this.draggingEdge = new Property<Color>( GlobalSettings.defaultDraggingEdgeLine );
		this.edgeHandle = new Property<Color>( GlobalSettings.defaultEdgeHandleFill );
		this.selectedEdge = new Property<Color>( GlobalSettings.defaultSelectedEdgeLine );
		this.selectedEdgeHandle = new Property<Color>( GlobalSettings.defaultSelectedEdgeHandleFill );
		this.captionText = new Property<Color>( GlobalSettings.defaultCaptionTextColor );
		this.captionButtonFill = new Property<Color>( GlobalSettings.defaultCaptionButtonFill );
		this.captionButtonLine = new Property<Color>( GlobalSettings.defaultCaptionButtonLine );
		this.selectedCaptionLine = new Property<Color>( GlobalSettings.defaultSelectedCaptionLineColor );
		this.uncoloredEdgeLine = new Property<Color>( GlobalSettings.defaultUncoloredEdgeLine );
		this.uncoloredVertexFill = new Property<Color>( GlobalSettings.defaultUncoloredVertexFill );
		this.vertexClickMargin = new Property<Double>( GlobalSettings.defaultVertexClickMargin );
		this.edgeHandleClickMargin = new Property<Double>( GlobalSettings.defaultEdgeHandleClickMargin );
		this.captionHandleClickMargin = new Property<Double>( GlobalSettings.defaultCaptionHandleClickMargin );
		this.captionEditorClickMargin = new Property<Double>( GlobalSettings.defaultCaptionEditorClickMargin );
		this.panDecelerationFactor = new Property<Double>( GlobalSettings.defaultPanDecelerationFactor );
		this.panOnDoubleClick = new Property<Boolean>( GlobalSettings.defaultPanOnDoubleClick );
		this.deselectVertexWithNewEdge = new Property<Boolean>( GlobalSettings.defaultDeselectVertexWithNewEdge );
		this.zoomInFactor = new Property<Double>( GlobalSettings.defaultZoomInFactor );
		this.zoomOutFactor = new Property<Double>( GlobalSettings.defaultZoomOutFactor );
		this.maximumZoomFactor = new Property<Double>( GlobalSettings.defaultMaximumZoomFactor );
		this.zoomGraphPadding = new Property<Double>( GlobalSettings.defaultZoomGraphPadding );
		this.scrollIncrementZoom = new Property<Double>( GlobalSettings.defaultScrollIncrementZoom );
		this.arrangeCircleRadiusMultiplier = new Property<Double>( GlobalSettings.defaultArrangeCircleRadiusMultiplier );
		this.arrangeGridSpacing = new Property<Double>( GlobalSettings.defaultArrangeGridSpacing );
		this.autoArrangeAttractiveForce = new Property<Double>( GlobalSettings.defaultAutoArrangeAttractiveForce );
		this.autoArrangeRepulsiveForce = new Property<Double>( GlobalSettings.defaultAutoArrangeRepulsiveForce );
		this.autoArrangeDecelerationFactor = new Property<Double>( GlobalSettings.defaultAutoArrangeDecelerationFactor );
		this.arrangeContractFactor = new Property<Double>( GlobalSettings.defaultArrangeContractFactor );
		this.arrangeExpandFactor = new Property<Double>( GlobalSettings.defaultArrangeExpandFactor );
		this.undoLoggingInterval = new Property<Integer>( GlobalSettings.defaultUndoLoggingInterval );
		this.undoLoggingMaximum = new Property<Integer>( GlobalSettings.defaultUndoLoggingMaximum );
		this.useAntiAliasing = new Property<Boolean>( GlobalSettings.defaultUseAntiAliasing );
		this.usePureStroke = new Property<Boolean>( GlobalSettings.defaultUsePureStroke );
		this.useBicubicInterpolation = new Property<Boolean>( GlobalSettings.defaultUseBicubicInterpolation );
		this.useFractionalMetrics = new Property<Boolean>( GlobalSettings.defaultUseFractionalMetrics );
		this.vertexWeightPrecision = new Property<Integer>( GlobalSettings.defaultVertexWeightPrecision );
		this.edgeWeightPrecision = new Property<Integer>( GlobalSettings.defaultEdgeWeightPrecision );
		this.mainWindowWidth = new Property<Integer>( GlobalSettings.defaultMainWindowWidth );
		this.mainWindowHeight = new Property<Integer>( GlobalSettings.defaultMainWindowHeight );
		this.scriptLibraryWindowWidth = new Property<Integer>( GlobalSettings.defaultScriptLibraryWindowWidth );
		this.scriptLibraryWindowHeight = new Property<Integer>( GlobalSettings.defaultScriptLibraryWindowHeight );
		this.graphWindowWidth = new Property<Integer>( GlobalSettings.defaultGraphWindowWidth );
		this.graphWindowHeight = new Property<Integer>( GlobalSettings.defaultGraphWindowHeight );
		this.cascadeWindowOffset = new Property<Integer>( GlobalSettings.defaultCascadeWindowOffset );
		this.language = new Property<String>( GlobalSettings.defaultLanguage );
		this.defaultGraphName = new Property<String>( GlobalSettings.defaultGraphName );
		this.directedEdgeArrowRatio = new Property<Double>( GlobalSettings.defaultDirectedEdgeArrowRatio );
		this.arrowKeyIncrement = new Property<Double>( GlobalSettings.defaultArrowKeyIncrement );
		this.edgeSnapMarginRatio = new Property<Double>( GlobalSettings.defaultEdgeSnapMarginRatio );
		this.areCloseDistance = new Property<Double>( GlobalSettings.defaultAreCloseDistance );
		this.paintToolMenuDelay = new Property<Integer>( GlobalSettings.defaultPaintToolMenuDelay );
		this.showDailyTipsOnStartup = new Property<Boolean>( GlobalSettings.defaultShowDailyTipsOnStartup );
		this.dailyTipIndex = new Property<Integer>( GlobalSettings.defaultDailyTipIndex );
		
		this.elementColors = new ObservableList<Color>( );
		this.elementColors.addAll( Arrays.asList( GlobalSettings.defaultElementColors ) );
		this.elementColors.addObserver( new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				UserSettings.this.setChanged( );
				UserSettings.this.notifyObservers( arg );
			}
		} );
		
		this.fromFile( new File( "UserSettings.json" ) );
	}
	
	public void fromFile( File file )
	{
		if( file.exists( ) )
		{
			try
			{
				Scanner in = new Scanner( file );
				
				StringBuilder sb = new StringBuilder( );
				while( in.hasNextLine( ) )
					sb.append( in.nextLine( ) );
				
				this.fromString( sb.toString( ) );
				
				in.close( );
			}
			catch( IOException ex )
			{
				DebugUtilities.logException( "An exception occurred while loading user settings.", ex );
			}
		}
	}
	
	public void fromString( String json )
	{
		Map<String, Object> members = JsonUtilities.parseObject( json );
		
		if( members.containsKey( "defaultVertexWeight" ) )
			this.defaultVertexWeight.set( (Double) members.get( "defaultVertexWeight" ) );
		if( members.containsKey( "defaultVertexColor" ) )
			this.defaultVertexColor.set( (Integer) members.get( "defaultVertexColor" ) );
		if( members.containsKey( "defaultVertexPrefix" ) )
			this.defaultVertexPrefix.set( (String) members.get( "defaultVertexPrefix" ) );
		if( members.containsKey( "defaultVertexRadius" ) )
			this.defaultVertexRadius.set( (Double) members.get( "defaultVertexRadius" ) );
		if( members.containsKey( "defaultVertexIsSelected" ) )
			this.defaultVertexIsSelected.set( (Boolean) members.get( "defaultVertexIsSelected" ) );
		if( members.containsKey( "defaultEdgeWeight" ) )
			this.defaultEdgeWeight.set( (Double) members.get( "defaultEdgeWeight" ) );
		if( members.containsKey( "defaultEdgeColor" ) )
			this.defaultEdgeColor.set( (Integer) members.get( "defaultEdgeColor" ) );
		if( members.containsKey( "defaultEdgePrefix" ) )
			this.defaultEdgePrefix.set( (String) members.get( "defaultEdgePrefix" ) );
		if( members.containsKey( "defaultEdgeThickness" ) )
			this.defaultEdgeThickness.set( (Double) members.get( "defaultEdgeThickness" ) );
		if( members.containsKey( "defaultEdgeHandleRadiusRatio" ) )
			this.defaultEdgeHandleRadiusRatio.set( (Double) members.get( "defaultEdgeHandleRadiusRatio" ) );
		if( members.containsKey( "defaultLoopDiameter" ) )
			this.defaultLoopDiameter.set( (Double) members.get( "defaultLoopDiameter" ) );
		if( members.containsKey( "defaultEdgeIsSelected" ) )
			this.defaultEdgeIsSelected.set( (Boolean) members.get( "defaultEdgeIsSelected" ) );
		if( members.containsKey( "defaultCaptionText" ) )
			this.defaultCaptionText.set( (String) members.get( "defaultCaptionText" ) );
		if( members.containsKey( "defaultCaptionFontSize" ) )
			this.defaultCaptionFontSize.set( (Double) members.get( "defaultCaptionFontSize" ) );
		if( members.containsKey( "defaultCaptionIsSelected" ) )
			this.defaultCaptionIsSelected.set( (Boolean) members.get( "defaultCaptionIsSelected" ) );
		if( members.containsKey( "defaultShowVertexWeights" ) )
			this.defaultShowVertexWeights.set( (Boolean) members.get( "defaultShowVertexWeights" ) );
		if( members.containsKey( "defaultShowVertexLabels" ) )
			this.defaultShowVertexLabels.set( (Boolean) members.get( "defaultShowVertexLabels" ) );
		if( members.containsKey( "defaultShowEdgeHandles" ) )
			this.defaultShowEdgeHandles.set( (Boolean) members.get( "defaultShowEdgeHandles" ) );
		if( members.containsKey( "defaultShowEdgeWeights" ) )
			this.defaultShowEdgeWeights.set( (Boolean) members.get( "defaultShowEdgeWeights" ) );
		if( members.containsKey( "defaultShowEdgeLabels" ) )
			this.defaultShowEdgeLabels.set( (Boolean) members.get( "defaultShowEdgeLabels" ) );
		if( members.containsKey( "defaultShowCaptions" ) )
			this.defaultShowCaptions.set( (Boolean) members.get( "defaultShowCaptions" ) );
		if( members.containsKey( "defaultShowCaptionHandles" ) )
			this.defaultShowCaptionHandles.set( (Boolean) members.get( "defaultShowCaptionHandles" ) );
		if( members.containsKey( "defaultShowCaptionEditors" ) )
			this.defaultShowCaptionEditors.set( (Boolean) members.get( "defaultShowCaptionEditors" ) );
		if( members.containsKey( "graphBackground" ) )
			this.graphBackground.set( (Color) members.get( "graphBackground" ) );
		if( members.containsKey( "selectionBoxFill" ) )
			this.selectionBoxFill.set( (Color) members.get( "selectionBoxFill" ) );
		if( members.containsKey( "selectionBoxLine" ) )
			this.selectionBoxLine.set( (Color) members.get( "selectionBoxLine" ) );
		if( members.containsKey( "vertexLine" ) )
			this.vertexLine.set( (Color) members.get( "vertexLine" ) );
		if( members.containsKey( "selectedVertexFill" ) )
			this.selectedVertexFill.set( (Color) members.get( "selectedVertexFill" ) );
		if( members.containsKey( "selectedVertexLine" ) )
			this.selectedVertexLine.set( (Color) members.get( "selectedVertexLine" ) );
		if( members.containsKey( "draggingEdge" ) )
			this.draggingEdge.set( (Color) members.get( "draggingEdge" ) );
		if( members.containsKey( "edgeHandle" ) )
			this.edgeHandle.set( (Color) members.get( "edgeHandle" ) );
		if( members.containsKey( "selectedEdge" ) )
			this.selectedEdge.set( (Color) members.get( "selectedEdge" ) );
		if( members.containsKey( "selectedEdgeHandle" ) )
			this.selectedEdgeHandle.set( (Color) members.get( "selectedEdgeHandle" ) );
		if( members.containsKey( "captionText" ) )
			this.captionText.set( (Color) members.get( "captionText" ) );
		if( members.containsKey( "captionButtonFill" ) )
			this.captionButtonFill.set( (Color) members.get( "captionButtonFill" ) );
		if( members.containsKey( "captionButtonLine" ) )
			this.captionButtonLine.set( (Color) members.get( "captionButtonLine" ) );
		if( members.containsKey( "selectedCaptionLine" ) )
			this.selectedCaptionLine.set( (Color) members.get( "selectedCaptionLine" ) );
		if( members.containsKey( "uncoloredEdgeLine" ) )
			this.uncoloredEdgeLine.set( (Color) members.get( "uncoloredEdgeLine" ) );
		if( members.containsKey( "uncoloredVertexFill" ) )
			this.uncoloredVertexFill.set( (Color) members.get( "uncoloredVertexFill" ) );
		if( members.containsKey( "vertexClickMargin" ) )
			this.vertexClickMargin.set( (Double) members.get( "vertexClickMargin" ) );
		if( members.containsKey( "edgeHandleClickMargin" ) )
			this.edgeHandleClickMargin.set( (Double) members.get( "edgeHandleClickMargin" ) );
		if( members.containsKey( "captionHandleClickMargin" ) )
			this.captionHandleClickMargin.set( (Double) members.get( "captionHandleClickMargin" ) );
		if( members.containsKey( "captionEditorClickMargin" ) )
			this.captionEditorClickMargin.set( (Double) members.get( "captionEditorClickMargin" ) );
		if( members.containsKey( "panDecelerationFactor" ) )
			this.panDecelerationFactor.set( (Double) members.get( "panDecelerationFactor" ) );
		if( members.containsKey( "panOnDoubleClick" ) )
			this.panOnDoubleClick.set( (Boolean) members.get( "panOnDoubleClick" ) );
		if( members.containsKey( "deselectVertexWithNewEdge" ) )
			this.deselectVertexWithNewEdge.set( (Boolean) members.get( "deselectVertexWithNewEdge" ) );
		if( members.containsKey( "zoomInFactor" ) )
			this.zoomInFactor.set( (Double) members.get( "zoomInFactor" ) );
		if( members.containsKey( "zoomOutFactor" ) )
			this.zoomOutFactor.set( (Double) members.get( "zoomOutFactor" ) );
		if( members.containsKey( "maximumZoomFactor" ) )
			this.maximumZoomFactor.set( (Double) members.get( "maximumZoomFactor" ) );
		if( members.containsKey( "zoomGraphPadding" ) )
			this.zoomGraphPadding.set( (Double) members.get( "zoomGraphPadding" ) );
		if( members.containsKey( "scrollIncrementZoom" ) )
			this.scrollIncrementZoom.set( (Double) members.get( "scrollIncrementZoom" ) );
		if( members.containsKey( "arrangeCircleRadiusMultiplier" ) )
			this.arrangeCircleRadiusMultiplier.set( (Double) members.get( "arrangeCircleRadiusMultiplier" ) );
		if( members.containsKey( "arrangeGridSpacing" ) )
			this.arrangeGridSpacing.set( (Double) members.get( "arrangeGridSpacing" ) );
		if( members.containsKey( "autoArrangeAttractiveForce" ) )
			this.autoArrangeAttractiveForce.set( (Double) members.get( "autoArrangeAttractiveForce" ) );
		if( members.containsKey( "autoArrangeRepulsiveForce" ) )
			this.autoArrangeRepulsiveForce.set( (Double) members.get( "autoArrangeRepulsiveForce" ) );
		if( members.containsKey( "autoArrangeDecelerationFactor" ) )
			this.autoArrangeDecelerationFactor.set( (Double) members.get( "autoArrangeDecelerationFactor" ) );
		if( members.containsKey( "arrangeContractFactor" ) )
			this.arrangeContractFactor.set( (Double) members.get( "arrangeContractFactor" ) );
		if( members.containsKey( "arrangeExpandFactor" ) )
			this.arrangeExpandFactor.set( (Double) members.get( "arrangeExpandFactor" ) );
		if( members.containsKey( "undoLoggingInterval" ) )
			this.undoLoggingInterval.set( (Integer) members.get( "undoLoggingInterval" ) );
		if( members.containsKey( "undoLoggingMaximum" ) )
			this.undoLoggingMaximum.set( (Integer) members.get( "undoLoggingMaximum" ) );
		if( members.containsKey( "useAntiAliasing" ) )
			this.useAntiAliasing.set( (Boolean) members.get( "useAntiAliasing" ) );
		if( members.containsKey( "usePureStroke" ) )
			this.usePureStroke.set( (Boolean) members.get( "usePureStroke" ) );
		if( members.containsKey( "useBicubicInterpolation" ) )
			this.useBicubicInterpolation.set( (Boolean) members.get( "useBicubicInterpolation" ) );
		if( members.containsKey( "useFractionalMetrics" ) )
			this.useFractionalMetrics.set( (Boolean) members.get( "useFractionalMetrics" ) );
		if( members.containsKey( "vertexWeightPrecision" ) )
			this.vertexWeightPrecision.set( (Integer) members.get( "vertexWeightPrecision" ) );
		if( members.containsKey( "edgeWeightPrecision" ) )
			this.edgeWeightPrecision.set( (Integer) members.get( "edgeWeightPrecision" ) );
		if( members.containsKey( "mainWindowWidth" ) )
			this.mainWindowWidth.set( (Integer) members.get( "mainWindowWidth" ) );
		if( members.containsKey( "mainWindowHeight" ) )
			this.mainWindowHeight.set( (Integer) members.get( "mainWindowHeight" ) );
		if( members.containsKey( "scriptLibraryWindowWidth" ) )
			this.scriptLibraryWindowWidth.set( (Integer) members.get( "scriptLibraryWindowWidth" ) );
		if( members.containsKey( "scriptLibraryWindowHeight" ) )
			this.scriptLibraryWindowHeight.set( (Integer) members.get( "scriptLibraryWindowHeight" ) );
		if( members.containsKey( "graphWindowWidth" ) )
			this.graphWindowWidth.set( (Integer) members.get( "graphWindowWidth" ) );
		if( members.containsKey( "graphWindowHeight" ) )
			this.graphWindowHeight.set( (Integer) members.get( "graphWindowHeight" ) );
		if( members.containsKey( "cascadeWindowOffset" ) )
			this.cascadeWindowOffset.set( (Integer) members.get( "cascadeWindowOffset" ) );
		if( members.containsKey( "language" ) )
			this.language.set( (String) members.get( "language" ) );
		if( members.containsKey( "defaultGraphName" ) )
			this.defaultGraphName.set( (String) members.get( "defaultGraphName" ) );
		if( members.containsKey( "directedEdgeArrowRatio" ) )
			this.directedEdgeArrowRatio.set( (Double) members.get( "directedEdgeArrowRatio" ) );
		if( members.containsKey( "arrowKeyIncrement" ) )
			this.arrowKeyIncrement.set( (Double) members.get( "arrowKeyIncrement" ) );
		if( members.containsKey( "edgeSnapMarginRatio" ) )
			this.edgeSnapMarginRatio.set( (Double) members.get( "edgeSnapMarginRatio" ) );
		if( members.containsKey( "areCloseDistance" ) )
			this.areCloseDistance.set( (Double) members.get( "areCloseDistance" ) );
		if( members.containsKey( "paintToolMenuDelay" ) )
			this.paintToolMenuDelay.set( (Integer) members.get( "paintToolMenuDelay" ) );
		if( members.containsKey( "showDailyTipsOnStartup" ) )
			this.showDailyTipsOnStartup.set( (Boolean) members.get( "showDailyTipsOnStartup" ) );
		if( members.containsKey( "dailyTipIndex" ) )
			this.dailyTipIndex.set( (Integer) members.get( "dailyTipIndex" ) );
		
		this.elementColors.clear( );
		if( members.containsKey( "elementColors" ) )
			for( Object color : (Iterable<?>) members.get( "elementColors" ) )
				this.elementColors.add( (Color) color );
	}
	
	public Color getEdgeColor( int i )
	{
		return ( i < 0 || i >= this.elementColors.size( ) ? this.uncoloredEdgeLine.get( ) : this.elementColors.get( i ) );
	}
	
	public Color getVertexColor( int i )
	{
		return ( i < 0 || i >= this.elementColors.size( ) ? this.uncoloredVertexFill.get( ) : this.elementColors.get( i ) );
	}
	
	public void saveToFile( )
	{
		File userSettingsFile = new File( "UserSettings.json" );
		
		try
		{
			if( !userSettingsFile.exists( ) )
				userSettingsFile.createNewFile( );
			
			FileWriter fileWriter = new FileWriter( userSettingsFile );
			fileWriter.write( UserSettings.instance.toString( ) );
			fileWriter.close( );
		}
		catch( IOException ex )
		{
			DebugUtilities.logException( "An exception occurred while saving preferences to file.", ex );
			JOptionPane.showMessageDialog( null, String.format( StringBundle.get( "preferences_dialog_unable_to_save_error_message" ), userSettingsFile.getAbsolutePath( ) ), GlobalSettings.applicationName, JOptionPane.ERROR_MESSAGE );
		}
	}
	
	@Override
	public String toString( )
	{
		HashMap<String, Object> members = new HashMap<String, Object>( );
		
		members.put( "defaultVertexWeight", this.defaultVertexWeight );
		members.put( "defaultVertexColor", this.defaultVertexColor );
		members.put( "defaultVertexPrefix", this.defaultVertexPrefix );
		members.put( "defaultVertexRadius", this.defaultVertexRadius );
		members.put( "defaultVertexIsSelected", this.defaultVertexIsSelected );
		members.put( "defaultEdgeWeight", this.defaultEdgeWeight );
		members.put( "defaultEdgeColor", this.defaultEdgeColor );
		members.put( "defaultEdgePrefix", this.defaultEdgePrefix );
		members.put( "defaultEdgeThickness", this.defaultEdgeThickness );
		members.put( "defaultEdgeHandleRadiusRatio", this.defaultEdgeHandleRadiusRatio );
		members.put( "defaultLoopDiameter", this.defaultLoopDiameter );
		members.put( "defaultEdgeIsSelected", this.defaultEdgeIsSelected );
		members.put( "defaultCaptionText", this.defaultCaptionText );
		members.put( "defaultCaptionFontSize", this.defaultCaptionFontSize );
		members.put( "defaultCaptionIsSelected", this.defaultCaptionIsSelected );
		members.put( "defaultShowVertexWeights", this.defaultShowVertexWeights );
		members.put( "defaultShowVertexLabels", this.defaultShowVertexLabels );
		members.put( "defaultShowEdgeHandles", this.defaultShowEdgeHandles );
		members.put( "defaultShowEdgeWeights", this.defaultShowEdgeWeights );
		members.put( "defaultShowEdgeLabels", this.defaultShowEdgeLabels );
		members.put( "defaultShowCaptions", this.defaultShowCaptions );
		members.put( "defaultShowCaptionHandles", this.defaultShowCaptionHandles );
		members.put( "defaultShowCaptionEditors", this.defaultShowCaptionEditors );
		members.put( "graphBackground", this.graphBackground );
		members.put( "selectionBoxFill", this.selectionBoxFill );
		members.put( "selectionBoxLine", this.selectionBoxLine );
		members.put( "vertexLine", this.vertexLine );
		members.put( "selectedVertexFill", this.selectedVertexFill );
		members.put( "selectedVertexLine", this.selectedVertexLine );
		members.put( "draggingEdge", this.draggingEdge );
		members.put( "edgeHandle", this.edgeHandle );
		members.put( "selectedEdge", this.selectedEdge );
		members.put( "selectedEdgeHandle", this.selectedEdgeHandle );
		members.put( "captionText", this.captionText );
		members.put( "captionButtonFill", this.captionButtonFill );
		members.put( "captionButtonLine", this.captionButtonLine );
		members.put( "selectedCaptionLine", this.selectedCaptionLine );
		members.put( "uncoloredEdgeLine", this.uncoloredEdgeLine );
		members.put( "uncoloredVertexFill", this.uncoloredVertexFill );
		members.put( "elementColors", this.elementColors );
		members.put( "vertexClickMargin", this.vertexClickMargin );
		members.put( "edgeHandleClickMargin", this.edgeHandleClickMargin );
		members.put( "captionHandleClickMargin", this.captionHandleClickMargin );
		members.put( "captionEditorClickMargin", this.captionEditorClickMargin );
		members.put( "panDecelerationFactor", this.panDecelerationFactor );
		members.put( "panOnDoubleClick", this.panOnDoubleClick );
		members.put( "deselectVertexWithNewEdge", this.deselectVertexWithNewEdge );
		members.put( "zoomInFactor", this.zoomInFactor );
		members.put( "zoomOutFactor", this.zoomOutFactor );
		members.put( "maximumZoomFactor", this.maximumZoomFactor );
		members.put( "zoomGraphPadding", this.zoomGraphPadding );
		members.put( "scrollIncrementZoom", this.scrollIncrementZoom );
		members.put( "arrangeCircleRadiusMultiplier", this.arrangeCircleRadiusMultiplier );
		members.put( "arrangeGridSpacing", this.arrangeGridSpacing );
		members.put( "autoArrangeAttractiveForce", this.autoArrangeAttractiveForce );
		members.put( "autoArrangeRepulsiveForce", this.autoArrangeRepulsiveForce );
		members.put( "autoArrangeDecelerationFactor", this.autoArrangeDecelerationFactor );
		members.put( "arrangeContractFactor", this.arrangeContractFactor );
		members.put( "arrangeExpandFactor", this.arrangeExpandFactor );
		members.put( "undoLoggingInterval", this.undoLoggingInterval );
		members.put( "undoLoggingMaximum", this.undoLoggingMaximum );
		members.put( "useAntiAliasing", this.useAntiAliasing );
		members.put( "usePureStroke", this.usePureStroke );
		members.put( "useBicubicInterpolation", this.useBicubicInterpolation );
		members.put( "useFractionalMetrics", this.useFractionalMetrics );
		members.put( "vertexWeightPrecision", this.vertexWeightPrecision );
		members.put( "edgeWeightPrecision", this.edgeWeightPrecision );
		members.put( "mainWindowWidth", this.mainWindowWidth );
		members.put( "mainWindowHeight", this.mainWindowHeight );
		members.put( "scriptLibraryWindowWidth", this.scriptLibraryWindowWidth );
		members.put( "scriptLibraryWindowHeight", this.scriptLibraryWindowHeight );
		members.put( "graphWindowWidth", this.graphWindowWidth );
		members.put( "graphWindowHeight", this.graphWindowHeight );
		members.put( "cascadeWindowOffset", this.cascadeWindowOffset );
		members.put( "language", this.language );
		members.put( "defaultGraphName", this.defaultGraphName );
		members.put( "directedEdgeArrowRatio", this.directedEdgeArrowRatio );
		members.put( "arrowKeyIncrement", this.arrowKeyIncrement );
		members.put( "edgeSnapMarginRatio", this.edgeSnapMarginRatio );
		members.put( "areCloseDistance", this.areCloseDistance );
		members.put( "paintToolMenuDelay", this.paintToolMenuDelay );
		members.put( "showDailyTipsOnStartup", this.showDailyTipsOnStartup );
		members.put( "dailyTipIndex", this.dailyTipIndex );
		
		return JsonUtilities.formatObject( members );
	}
}
