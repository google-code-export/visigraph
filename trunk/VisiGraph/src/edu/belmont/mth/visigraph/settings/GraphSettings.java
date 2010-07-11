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
		showEdgeHandles    = new Property<Boolean>(GlobalSettings.defaultShowEdgeHandles,    "showEdgeHandles");
		showEdgeWeights    = new Property<Boolean>(GlobalSettings.defaultShowEdgeWeights,    "showEdgeWeights");
		showEdgeLabels     = new Property<Boolean>(GlobalSettings.defaultShowEdgeLabels,     "showEdgeLabels");
		showVertexWeights  = new Property<Boolean>(GlobalSettings.defaultShowVertexWeights,  "showVertexWeights");
		showVertexLabels   = new Property<Boolean>(GlobalSettings.defaultShowVertexLabels,   "showVertexLabels");
		showCaptions       = new Property<Boolean>(GlobalSettings.defaultShowCaptions,       "showCaptions");
		showCaptionHandles = new Property<Boolean>(GlobalSettings.defaultShowCaptionHandles, "showCaptionHandles");
		showCaptionEditors = new Property<Boolean>(GlobalSettings.defaultShowCaptionEditors, "showCaptionEdits");
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
