/**
 * GraphDisplaySettings.java
 */
package edu.belmont.mth.visigraph.settings;

import java.util.HashMap;

import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.utilities.JsonUtilities;

/**
 * @author Cameron Behar
 *
 */
public class GraphSettings extends ObservableBase
{	
	public final Property<Boolean> showEdgeHandles;
	public final Property<Boolean> showEdgeWeights;
	public final Property<Boolean> showEdgeLabels;
	public final Property<Boolean> showVertexWeights;
	public final Property<Boolean> showVertexLabels;
	public final Property<Boolean> showCaptions;
	public final Property<Boolean> showCaptionHandles;
	public final Property<Boolean> showCaptionEditors;
	
	public GraphSettings()
	{
		UserSettings settings = UserSettings.instance;
		
		showEdgeHandles    = new Property<Boolean>(settings.defaultShowEdgeHandles.get(),    "showEdgeHandles");
		showEdgeWeights    = new Property<Boolean>(settings.defaultShowEdgeWeights.get(),    "showEdgeWeights");
		showEdgeLabels     = new Property<Boolean>(settings.defaultShowEdgeLabels.get(),     "showEdgeLabels");
		showVertexWeights  = new Property<Boolean>(settings.defaultShowVertexWeights.get(),  "showVertexWeights");
		showVertexLabels   = new Property<Boolean>(settings.defaultShowVertexLabels.get(),   "showVertexLabels");
		showCaptions       = new Property<Boolean>(settings.defaultShowCaptions.get(),       "showCaptions");
		showCaptionHandles = new Property<Boolean>(settings.defaultShowCaptionHandles.get(), "showCaptionHandles");
		showCaptionEditors = new Property<Boolean>(settings.defaultShowCaptionEditors.get(), "showCaptionEdits");
	}
	
	public GraphSettings(GraphSettings graphSettings)
	{
		showEdgeHandles    = new Property<Boolean>(graphSettings.showEdgeHandles.get(),    "showEdgeHandles");
		showEdgeWeights    = new Property<Boolean>(graphSettings.showEdgeWeights.get(),    "showEdgeWeights");
		showEdgeLabels     = new Property<Boolean>(graphSettings.showEdgeLabels.get(),     "showEdgeLabels");
		showVertexWeights  = new Property<Boolean>(graphSettings.showVertexWeights.get(),  "showVertexWeights");
		showVertexLabels   = new Property<Boolean>(graphSettings.showVertexLabels.get(),   "showVertexLabels");
		showCaptions       = new Property<Boolean>(graphSettings.showCaptions.get(),       "showCaptions");
		showCaptionHandles = new Property<Boolean>(graphSettings.showCaptionHandles.get(), "showCaptionHandles");
		showCaptionEditors = new Property<Boolean>(graphSettings.showCaptionEditors.get(), "showCaptionEdits");
	}

	@Override
	public String toString()
	{
		HashMap<String, Object> members = new HashMap<String, Object>();
		
		members.put("showEdgeHandles",    showEdgeHandles   );
		members.put("showEdgeWeights",    showEdgeWeights   );
		members.put("showEdgeLabels",     showEdgeLabels    );
		members.put("showVertexWeights",  showVertexWeights );
		members.put("showVertexLabels",   showVertexLabels  );
		members.put("showCaptions",       showCaptions      );
		members.put("showCaptionHandles", showCaptionHandles);
		members.put("showCaptionEditors", showCaptionEditors);
		
		return JsonUtilities.formatObject(members);
	}
}
