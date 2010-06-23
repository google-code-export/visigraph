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
public class GraphDisplaySettings extends Observable
{	
	public final Property<Boolean> showEdgeHandles;
	public final Property<Boolean> showEdgeWeights;
	public final Property<Boolean> showEdgeLabels;
	public final Property<Boolean> showVertexWeights;
	public final Property<Boolean> showVertexLabels;
	public final Property<Boolean> showCaptions;
	public final Property<Boolean> showCaptionHandles;
	public final Property<Boolean> showCaptionEditors;
	
	public GraphDisplaySettings()
	{
		showEdgeHandles    = new Property<Boolean>(GlobalSettings.defaultShowEdgeHandles,    "showEdgeHandles");
		showEdgeWeights    = new Property<Boolean>(GlobalSettings.defaultShowEdgeWeights,    "showEdgeWeights");
		showEdgeLabels     = new Property<Boolean>(GlobalSettings.defaultShowEdgeLabels,     "showEdgeLabels");
		showVertexWeights  = new Property<Boolean>(GlobalSettings.defaultShowVertexWeights,  "showVertexWeights");
		showVertexLabels   = new Property<Boolean>(GlobalSettings.defaultShowVertexLabels,   "showVertexLabels");
		showCaptions       = new Property<Boolean>(GlobalSettings.defaultShowCaptions,       "showCaptions");
		showCaptionHandles = new Property<Boolean>(GlobalSettings.defaultShowCaptionHandles, "showCaptionHandles");
		showCaptionEditors = new Property<Boolean>(GlobalSettings.defaultShowCaptionEdits,   "showCaptionEdits");
	}
	
	public GraphDisplaySettings(GraphDisplaySettings graphDisplaySettings)
	{
		showEdgeHandles    = new Property<Boolean>(graphDisplaySettings.showEdgeHandles.get(), "showEdgeHandles");
		showEdgeWeights    = new Property<Boolean>(graphDisplaySettings.showEdgeWeights.get(), "showEdgeWeights");
		showEdgeLabels     = new Property<Boolean>(graphDisplaySettings.showEdgeLabels.get(), "showEdgeLabels");
		showVertexWeights  = new Property<Boolean>(graphDisplaySettings.showVertexWeights.get(), "showVertexWeights");
		showVertexLabels   = new Property<Boolean>(graphDisplaySettings.showVertexLabels.get(), "showVertexLabels");
		showCaptions       = new Property<Boolean>(graphDisplaySettings.showCaptions.get(), "showCaptions");
		showCaptionHandles = new Property<Boolean>(graphDisplaySettings.showCaptionHandles.get(), "showCaptionHandles");
		showCaptionEditors = new Property<Boolean>(graphDisplaySettings.showCaptionEditors.get(), "showCaptionEdits");
	}

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
