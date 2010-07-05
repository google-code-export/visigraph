/**
 * Palette.java
 */
package edu.belmont.mth.visigraph.models;

import java.awt.*;
import java.util.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.views.*;

/**
 * @author Cameron Behar
 * 
 */
public class Palette extends ObservableBase
{
	public final Property<Color>	graphBackground;
	public final Property<Color>	graphText;
	public final Property<Color>	graphSelectionBoxFill;
	public final Property<Color>	graphSelectionBoxLine;
	
	public final Property<Color>	vertexLine;
	
	public final Property<Color>	draggingHandleEdgeLine;
	public final Property<Color>	draggingEdgeLine;
	public final Property<Color>	uncoloredEdgeHandle;
	
	public final Property<Color>	selectedVertexFill;
	public final Property<Color>	selectedVertexLine;
	
	public final Property<Color>	selectedEdgeLine;
	public final Property<Color>	selectedEdgeHandle;
	
	public final Property<Color>	captionText;
	public final Property<Color>	selectedCaptionText;
	
	private ObservableList<Color>	elementColors;
	
	public         Palette()
	{
		graphBackground        = new Property<Color>(GlobalSettings.defaultGraphBackgroundDisplayColor, "graphBackground");
		graphText              = new Property<Color>(GlobalSettings.defaultGraphTextDisplayColor, "graphText");
		graphSelectionBoxFill  = new Property<Color>(GlobalSettings.defaultGraphSelectionBoxFillDisplayColor, "graphSelectionBoxFill");
		graphSelectionBoxLine  = new Property<Color>(GlobalSettings.defaultGraphSelectionBoxLineDisplayColor, "graphSelectionBoxLine");
		
		vertexLine             = new Property<Color>(GlobalSettings.defaultVertexLineDisplayColor, "vertexLine");
		
		draggingHandleEdgeLine = new Property<Color>(GlobalSettings.defaultDraggingHandleEdgeLineDisplayColor, "draggingHandleEdgeLine");
		draggingEdgeLine       = new Property<Color>(GlobalSettings.defaultDraggingEdgeLineDisplayColor, "draggingEdgeLine");
		uncoloredEdgeHandle    = new Property<Color>(GlobalSettings.defaultUncoloredEdgeHandleDisplayColor, "uncoloredEdgeHandle");
		
		selectedVertexFill     = new Property<Color>(GlobalSettings.defaultSelectedVertexFillDisplayColor, "selectedVertexFill");
		selectedVertexLine     = new Property<Color>(GlobalSettings.defaultSelectedVertexLineDisplayColor, "selectedVertexLine");

		selectedEdgeLine       = new Property<Color>(GlobalSettings.defaultSelectedEdgeLineDisplayColor, "selectedEdgeLine");
		selectedEdgeHandle     = new Property<Color>(GlobalSettings.defaultSelectedEdgeHandleDisplayColor, "selectedEdgeHandle");
		
		captionText            = new Property<Color>(GlobalSettings.defaultCaptionTextDisplayColor, "captionText");
		selectedCaptionText    = new Property<Color>(GlobalSettings.defaultSelectedCaptionTextDisplayColor, "selectedCaptionText");
		
		elementColors          = new ObservableList<Color>("elementColors");
		for (Color c : GlobalSettings.defaultElementDisplayColors) elementColors.add(new Color(c.getRGB()));
		elementColors.addObserver(new ObserverBase()
		{
			public void hasChanged(Object source)
			{
				notifyObservers(source);
			}
		});
	}
	
	public         Palette(Palette p)
	{
		graphBackground        = new Property<Color>(p.graphBackground.       get(), "graphBackground"       );
		graphText              = new Property<Color>(p.graphText.             get(), "graphText"             );
		graphSelectionBoxFill  = new Property<Color>(p.graphSelectionBoxFill. get(), "graphSelectionBoxFill" );
		graphSelectionBoxLine  = new Property<Color>(p.graphSelectionBoxLine. get(), "graphSelectionBoxLine" );
		
		vertexLine             = new Property<Color>(p.vertexLine.            get(), "vertexLine"            );
		
		draggingHandleEdgeLine = new Property<Color>(p.draggingHandleEdgeLine.get(), "draggingHandleEdgeLine");
		draggingEdgeLine       = new Property<Color>(p.draggingEdgeLine.      get(), "draggingEdgeLine"      );
		uncoloredEdgeHandle    = new Property<Color>(p.uncoloredEdgeHandle.   get(), "uncoloredEdgeHandle"   );
		
		selectedVertexFill     = new Property<Color>(p.selectedVertexFill.    get(), "selectedVertexFill"    );
		selectedVertexLine     = new Property<Color>(p.selectedVertexLine.    get(), "selectedVertexLine"    );
		
		selectedEdgeLine       = new Property<Color>(p.selectedEdgeLine.      get(), "selectedEdgeLine"      );
		selectedEdgeHandle     = new Property<Color>(p.selectedEdgeHandle.    get(), "selectedEdgeHandle"    );
		
		captionText            = new Property<Color>(p.captionText.           get(), "captionText"           );
		selectedCaptionText    = new Property<Color>(p.selectedCaptionText.   get(), "selectedCaptionText"   );
		
		elementColors          = new ObservableList<Color>("elementColors");
		for (Color c : p.elementColors)
			addElementColor(c);
		elementColors.addObserver(new ObserverBase()
		{
			public void hasChanged(Object source)
			{
				notifyObservers(source);
			}
		});
	}
	
	public Color   getElementColor(int index)
	{
		return ((index > -1 && index < elementColors.size()) ? elementColors.get(index) : GlobalSettings.defaultElementDisplayColor);
	}
	
	public void    addElementColor(Color color)
	{
		elementColors.add(color);
	}
	
	public void    addElementColor(int index, Color color)
	{
		elementColors.add(index, color);
	}
	
	public void    addElementColors(Collection<? extends Color> colors)
	{
		elementColors.addAll(colors);
	}
	
	public int     getElementColorCount()
	{
		return elementColors.size();
	}
	
	public boolean removeElementColor(Color color)
	{
		return elementColors.remove(color);
	}
	
	public Color   removeElementColorAt(int index)
	{
		return elementColors.remove(index);
	}

	public String toString()
	{
		HashMap<String, Object> members = new HashMap<String, Object>();
		
		members.put("graphBackground",        graphBackground       );
		members.put("graphText",              graphText             );
		members.put("graphSelectionBoxFill",  graphSelectionBoxFill );
		members.put("graphSelectionBoxLine",  graphSelectionBoxLine );
		members.put("vertexLine",             vertexLine            );
		members.put("draggingHandleEdgeLine", draggingHandleEdgeLine);
		members.put("draggingEdgeLine",       draggingEdgeLine      );
		members.put("uncoloredEdgeHandle",    uncoloredEdgeHandle   );
		members.put("selectedVertexFill",     selectedVertexFill    );
		members.put("selectedVertexLine",     selectedVertexLine    );
		members.put("selectedEdgeLine",       selectedEdgeLine      );
		members.put("selectedEdgeHandle",     selectedEdgeHandle    );
		members.put("captionText",            captionText           );
		members.put("selectedCaptionText",    selectedCaptionText   );
		members.put("elementColors",          elementColors         );
		
		return JsonUtilities.formatObject(members);
	}
}
