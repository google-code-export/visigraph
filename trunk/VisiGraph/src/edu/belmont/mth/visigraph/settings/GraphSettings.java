/**
 * GraphDisplaySettings.java
 */
package edu.belmont.mth.visigraph.settings;

import java.util.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.utilities.*;

/**
 * @author Cameron Behar
 */
public class GraphSettings extends ObservableModel
{
	public final Property<Boolean>	showEdgeHandles;
	public final Property<Boolean>	showEdgeWeights;
	public final Property<Boolean>	showEdgeLabels;
	public final Property<Boolean>	showVertexWeights;
	public final Property<Boolean>	showVertexLabels;
	public final Property<Boolean>	showCaptions;
	public final Property<Boolean>	showCaptionHandles;
	public final Property<Boolean>	showCaptionEditors;
	
	public GraphSettings( )
	{
		UserSettings settings = UserSettings.instance;
		
		this.showEdgeHandles = new Property<Boolean>( settings.defaultShowEdgeHandles.get( ) );
		this.showEdgeWeights = new Property<Boolean>( settings.defaultShowEdgeWeights.get( ) );
		this.showEdgeLabels = new Property<Boolean>( settings.defaultShowEdgeLabels.get( ) );
		this.showVertexWeights = new Property<Boolean>( settings.defaultShowVertexWeights.get( ) );
		this.showVertexLabels = new Property<Boolean>( settings.defaultShowVertexLabels.get( ) );
		this.showCaptions = new Property<Boolean>( settings.defaultShowCaptions.get( ) );
		this.showCaptionHandles = new Property<Boolean>( settings.defaultShowCaptionHandles.get( ) );
		this.showCaptionEditors = new Property<Boolean>( settings.defaultShowCaptionEditors.get( ) );
	}
	
	public GraphSettings( GraphSettings graphSettings )
	{
		this.showEdgeHandles = new Property<Boolean>( graphSettings.showEdgeHandles.get( ) );
		this.showEdgeWeights = new Property<Boolean>( graphSettings.showEdgeWeights.get( ) );
		this.showEdgeLabels = new Property<Boolean>( graphSettings.showEdgeLabels.get( ) );
		this.showVertexWeights = new Property<Boolean>( graphSettings.showVertexWeights.get( ) );
		this.showVertexLabels = new Property<Boolean>( graphSettings.showVertexLabels.get( ) );
		this.showCaptions = new Property<Boolean>( graphSettings.showCaptions.get( ) );
		this.showCaptionHandles = new Property<Boolean>( graphSettings.showCaptionHandles.get( ) );
		this.showCaptionEditors = new Property<Boolean>( graphSettings.showCaptionEditors.get( ) );
	}
	
	@Override
	public String toString( )
	{
		Map<String, Object> members = new HashMap<String, Object>( );
		
		members.put( "showEdgeHandles", this.showEdgeHandles );
		members.put( "showEdgeWeights", this.showEdgeWeights );
		members.put( "showEdgeLabels", this.showEdgeLabels );
		members.put( "showVertexWeights", this.showVertexWeights );
		members.put( "showVertexLabels", this.showVertexLabels );
		members.put( "showCaptions", this.showCaptions );
		members.put( "showCaptionHandles", this.showCaptionHandles );
		members.put( "showCaptionEditors", this.showCaptionEditors );
		
		return JsonUtilities.formatObject( members );
	}
}
