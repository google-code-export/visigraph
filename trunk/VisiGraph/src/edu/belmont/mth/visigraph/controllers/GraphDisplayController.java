/**
 * GraphDisplayController.java
 */
package edu.belmont.mth.visigraph.controllers;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.print.*;
import java.util.*;
import java.util.Map.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.event.*;

import edu.belmont.mth.visigraph.utilities.DebugUtilities;
import edu.belmont.mth.visigraph.views.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.resources.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.gui.dialogs.*;
import edu.belmont.mth.visigraph.gui.layouts.*;
import edu.belmont.mth.visigraph.views.display.*;
import edu.belmont.mth.visigraph.models.functions.*;

/**
 * @author Cameron Behar
 * 
 */
@SuppressWarnings("serial")
public class GraphDisplayController extends JPanel implements ClipboardOwner
{
	private GraphDisplayController   thisGdc;
	
	private Graph				     graph;
	private GraphSettings 	  		 settings;
	
	private JPanel			         toolToolBarPanel;
	private ToolToolBar		          toolToolBar;
	private JPanel			         nonToolToolbarPanel;
	private ArrangeToolBar	          arrangeToolBar;
	private ViewToolBar		          viewToolBar;
	private ZoomToolBar		          zoomToolBar;
	private FunctionToolBar	          functionToolBar;								 
	private JPanel			         viewportPanel;
	private JComponent		          viewport;
	private ViewportPopupMenu	      viewportPopupMenu;
	private JPanel			          statusBar;
	private Map<FunctionBase, JLabel> selectedFunctionLabels;
	
	private Tool					  tool;
	private int					      paintColor;
	private boolean				      isMouseDownOnCanvas;
	private boolean				      isMouseDownOnPaintToolButton;
	private boolean				      pointerToolClickedObject;
	private boolean				      cutToolClickedObject;
	private boolean				      paintToolClickedObject;
	private boolean				      isMouseOverViewport;
	private Point					  currentMousePoint;
	private Point					  pastMousePoint;
	private Point					  pastPanPoint;
	private Vertex				      fromVertex;
	private AffineTransform		      transform;
	private Set<FunctionBase>	      functionsToBeRun;
	private SnapshotList			  undoHistory;
	private Timer					  undoTimer;
	private Timer					  panTimer;
	private EventListenerList		  graphChangeListenerList;
	private UserSettings			  userSettings = UserSettings.instance;
	private boolean					  isViewportInvalidated;
	private Timer					  viewportValidationTimer;
	
	public GraphDisplayController(Graph graph)
	{
		// Maintain instance
		thisGdc = this;
		
		// Initialize the list of GraphChangeEvent listeners
		graphChangeListenerList = new EventListenerList();
		
		// Add/bind graph
		loadGraph(graph);
		undoHistory = new SnapshotList(graph.toString());
		
		// Add/bind palette
		userSettings.addObserver(new ObserverBase()
		{
			@Override
			public void hasChanged(Object source)
			{
				hasSettingChanged(source);
			}
		});
				
		// Add/bind display settings
		settings = new GraphSettings();
		settings.addObserver(new ObserverBase()
		{
			@Override
			public void hasChanged(Object source)
			{
				hasSettingChanged(source);
			}
		});
		
		// Initialize the toolbar, buttons, and viewport
		initializeComponents();
		
		// Initialize the viewport's affine transform
		transform = new AffineTransform();
		
		// Initialize the viewport's frame delimiter
		isViewportInvalidated = true;
		viewportValidationTimer = new Timer(30, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if(isViewportInvalidated)
					repaint();
				
				isViewportInvalidated = false;
			}
		});
		viewportValidationTimer.start();
	}
	
	public void addGraphChangeListener(GraphChangeEventListener listener)
	{
		graphChangeListenerList.add(GraphChangeEventListener.class, listener);
	}
	
	public void cut()
	{
		copy();
		
		// Delete all selected elements from the original graph
		int i = 0;
		while(i < graph.edges.size())
		{
			Edge edge = graph.edges.get(i);
			if(edge.isSelected.get())
				graph.edges.remove(i);
			else
				++i;
		}
		
		i = 0;
		while(i < graph.vertexes.size())
		{
			if(graph.vertexes.get(i).isSelected.get())
				graph.vertexes.remove(i);
			else
				++i;
		}
		
		i = 0;
		while(i < graph.captions.size())
		{
			if(graph.captions.get(i).isSelected.get())
				graph.captions.remove(i);
			else
				++i;
		}
	}
	
	public void copy()
	{
		// Make a copy of graph containing only selected elements
		Graph copy = new Graph("copy", graph.areLoopsAllowed, graph.areDirectedEdgesAllowed, graph.areMultipleEdgesAllowed, graph.areCyclesAllowed);
		
		for(Vertex vertex : graph.vertexes)
			if(vertex.isSelected.get())
				copy.vertexes.add(vertex);
		
		for(Edge edge : graph.edges)
			if(edge.isSelected.get() && edge.from.isSelected.get() && edge.to.isSelected.get())
				copy.edges.add(edge);
		
		for(Caption caption : graph.captions)
			if(caption.isSelected.get())
				copy.captions.add(caption);
							
		// Send the JSON to the clipboard
		StringSelection stringSelection = new StringSelection( copy.toString() );
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents( stringSelection, thisGdc );
	}
	
	public void dispose()
	{
		undoTimer.stop();
	}
	
	private void fireGraphChangeEvent(GraphChangeEvent evt)
	{
		Object[] listeners = graphChangeListenerList.getListenerList();
		// Each listener occupies two elements - the first is the listener class
		// and the second is the listener instance
		for (int i = 0; i < listeners.length; i += 2)
			if (listeners[i] == GraphChangeEventListener.class)
				((GraphChangeEventListener) listeners[i + 1]).graphChangeEventOccurred(evt);
	}
	
	public RenderedImage getImage()
	{
	    int width = viewport.getWidth();
	    int height = viewport.getHeight();

	    // Create a buffered image in which to draw
	    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

	    // Create a graphics contents on the buffered image
	    Graphics g = bufferedImage.createGraphics();

	    // Draw graphics
	    viewport.paint(g);

	    // Graphics context no longer needed so dispose it
	    g.dispose();

	    return bufferedImage;
	}
	
	public Graph getGraph()
	{
		return graph;
	}
	
	public GraphSettings getSettings()
	{
		return settings;
	}
	
	public Rectangle getSelectionRectangle()
	{
		Rectangle ret = new Rectangle();
		ret.x = Math.min(pastMousePoint.x, currentMousePoint.x);
		ret.y = Math.min(pastMousePoint.y, currentMousePoint.y);
		ret.width = Math.abs(pastMousePoint.x - currentMousePoint.x);
		ret.height = Math.abs(pastMousePoint.y - currentMousePoint.y);
		return ret;
	}
	
	public void hasGraphChanged(Object source)
	{
		isViewportInvalidated = true;
		fireGraphChangeEvent(new GraphChangeEvent(graph));
	}
	
	public void hasSettingChanged(Object source)
	{
		isViewportInvalidated = true;

		if(toolToolBar != null)
			toolToolBar.refreshPaintMenu();
		
		if(viewToolBar != null)
			viewToolBar.refresh();
	}
	
	public void initializeComponents()
	{
		setLayout(new BorderLayout());
		setBackground(userSettings.graphBackground.get());
		setOpaque(true);
		
		toolToolBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		toolToolBarPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
		add(toolToolBarPanel, BorderLayout.WEST);
		
		toolToolBar = new ToolToolBar();
		toolToolBarPanel.add(toolToolBar);
		
		nonToolToolbarPanel = new JPanel(new WrapLayout(FlowLayout.LEFT));
		nonToolToolbarPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
		add(nonToolToolbarPanel, BorderLayout.NORTH);
		
		arrangeToolBar = new ArrangeToolBar();
		nonToolToolbarPanel.add(arrangeToolBar);
		
		viewToolBar = new ViewToolBar();
		nonToolToolbarPanel.add(viewToolBar);
		
		zoomToolBar = new ZoomToolBar();
		nonToolToolbarPanel.add(zoomToolBar);
		
		functionToolBar = new FunctionToolBar();
		nonToolToolbarPanel.add(functionToolBar);
		
		selectedFunctionLabels = new HashMap<FunctionBase, JLabel>();
		functionsToBeRun = new TreeSet<FunctionBase>();
		
		viewportPanel = new JPanel(new BorderLayout());
		viewportPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		add(viewportPanel, BorderLayout.CENTER);
		
		viewport = new JComponent()
		{
			@Override
			public void paintComponent(Graphics g)
			{
				paintViewport((Graphics2D) g);
			}
		};
		viewport.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent event)
			{
				try { viewportMousePressed(event); }
				catch (NoninvertibleTransformException e) { DebugUtilities.logException("An exception occurred while inverting transformation.", e); }
				
				if (event.getClickCount() > 1)
					try { viewportMouseDoubleClicked(event); }
					catch (NoninvertibleTransformException e) { DebugUtilities.logException("An exception occurred while inverting transformation.", e); }
			}
			
			@Override
			public void mouseReleased(MouseEvent event)
			{
				try { viewportMouseReleased(event); }
				catch (NoninvertibleTransformException e) { DebugUtilities.logException("An exception occurred while inverting transformation.", e); }
			}
			
			@Override
			public void mouseEntered(MouseEvent event)
			{
				isMouseOverViewport = true;
			}
			
			@Override
			public void mouseExited(MouseEvent event)
			{
				isMouseOverViewport = false;
			}
		});
		viewport.addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseDragged(MouseEvent event)
			{
				try { viewportMouseDragged(event); }
				catch (NoninvertibleTransformException e) { DebugUtilities.logException("An exception occurred while inverting transformation.", e); }
			}
			
			@Override
			public void mouseMoved(MouseEvent event)
			{
				try { transform.inverseTransform(event.getPoint(), currentMousePoint); }
				catch (NoninvertibleTransformException e) { DebugUtilities.logException("An exception occurred while inverting transformation.", e); }
			}
		});
		viewport.addMouseWheelListener(new MouseWheelListener()
		{
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				zoomCenter(new Point2D.Double(currentMousePoint.x, currentMousePoint.y), 1 - e.getWheelRotation() * userSettings.scrollIncrementZoom.get());
			}
		});
		viewport.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent arg0)
			{
				viewportKeyPressed(arg0);
			}
			
			public void keyReleased(KeyEvent arg0)
			{
			// Do nothing
			}
			
			public void keyTyped(KeyEvent arg0)
			{
			// Do nothing
			}
		});
		viewportPanel.add(viewport, BorderLayout.CENTER);
		viewportPopupMenu = new ViewportPopupMenu();
		
		statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		statusBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));
		add(statusBar, BorderLayout.SOUTH);
		
		undoTimer = new Timer(userSettings.undoLoggingInterval.get(), new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				undoHistory.add(graph.toString());
			}
		});
		undoTimer.start();
	}
	
	public void loadGraph(Graph graph)
	{
		this.graph = graph;
		graph.addObserver(new ObserverBase()
		{
			@Override
			public void hasChanged(Object source)
			{
				hasGraphChanged(source);
			}
		});
		
		isMouseDownOnCanvas = false;
		currentMousePoint = new Point(0, 0);
		pastMousePoint = new Point(0, 0);
		pastPanPoint = new Point(0, 0);
		pointerToolClickedObject = false;
		cutToolClickedObject = false;
		paintToolClickedObject = false;
		fromVertex = null;
	}
	
	@Override
	public void lostOwnership(Clipboard arg0, Transferable arg1)
	{
		// Who cares?
	}
	
	public void paintSelectionRectangle(Graphics2D g2D)
	{
		Rectangle selection = getSelectionRectangle();
		
		g2D.setColor(userSettings.selectionBoxFill.get());
		g2D.fillRect(selection.x, selection.y, selection.width, selection.height);
		
		g2D.setColor(userSettings.selectionBoxLine.get());
		g2D.drawRect(selection.x, selection.y, selection.width, selection.height);
	}
	
	public void paintViewport(Graphics2D g2D)
	{
		if (userSettings.useAntiAliasing.get())
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if (userSettings.usePureStroke.get())
			g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		
		if (userSettings.useBicubicInterpolation.get())
			g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		if (userSettings.useFractionalMetrics.get())
			g2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		
		// Apply any one-time functions
		Set<FunctionBase> run = new TreeSet<FunctionBase>();
		run.addAll(functionsToBeRun);
		functionsToBeRun.clear();
		
		for (FunctionBase function : run)
			JOptionPane.showInternalMessageDialog(viewport, function +": " + function.evaluate(g2D, graph), GlobalSettings.applicationName, JOptionPane.OK_OPTION + JOptionPane.INFORMATION_MESSAGE);
		
		// Clear everything
		super.paintComponent(g2D);
		
		// Apply the transformation
		AffineTransform original = g2D.getTransform();
		original.concatenate(transform);
		g2D.setTransform(original);
		
		// Paint the graph
		GraphDisplayView.paint(g2D, graph, settings);
		
		// Apply any selected functions
		for (Entry<FunctionBase, JLabel> entry : selectedFunctionLabels.entrySet())
			entry.getValue().setText(entry.getKey() + ": " + entry.getKey().evaluate(g2D, graph));
		
		// Paint controller-specific stuff
		if (isMouseDownOnCanvas)
			switch (tool)
			{
				case POINTER_TOOL: if (!pointerToolClickedObject) paintSelectionRectangle(g2D); break;
				case CUT_TOOL: if (!cutToolClickedObject) paintSelectionRectangle(g2D); break;
				case EDGE_TOOL:
					{
						// For the edge tool we might need to paint the temporary drag-edge
						if (fromVertex != null)
						{
							g2D.setColor(userSettings.draggingEdge.get());
							g2D.drawLine(fromVertex.x.get().intValue(), fromVertex.y.get().intValue(), currentMousePoint.x, currentMousePoint.y);
						}
						
						break;
					}
				case PAINT_TOOL: if (!paintToolClickedObject) paintSelectionRectangle(g2D); break;
			}
	}	
	
	public void paste()
	{
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		
		if ( hasTransferableText )
		{
			try
			{
				result = (String)contents.getTransferData(DataFlavor.stringFlavor);
				
				Graph pasted = new Graph(result);
				
				// Find the centroid
				double elementCount = 0.0;
				Point2D.Double centroid = new Point2D.Double(0.0, 0.0);
				
				for(Vertex vertex : pasted.vertexes)
				{
					centroid.x += vertex.x.get();
					centroid.y += vertex.y.get();
					++elementCount;
				}
				
				for(Edge edge : pasted.edges)
				{
					centroid.x += edge.handleX.get();
					centroid.y += edge.handleY.get();
					++elementCount;
				}
				
				for(Caption caption : pasted.captions)
				{
					centroid.x += caption.x.get();
					centroid.y += caption.y.get();
					++elementCount;
				}
				
				centroid.x /= elementCount;
				centroid.y /= elementCount;
				
				// Center everything around the mouse or in the center of the viewport
				Point2D.Double pastePoint = new Point2D.Double();
				
				if(isMouseOverViewport)
					pastePoint = new Point2D.Double(currentMousePoint.x, currentMousePoint.y);
				else
					transform.inverseTransform(new Point2D.Double(viewport.getWidth() / 2.0, viewport.getHeight() / 2.0), pastePoint);
				
				pasted.suspendNotifications(true);
				
				for(Edge edge : pasted.edges)
					edge.suspendNotifications(true);
				
				for(Vertex vertex : pasted.vertexes)
				{
					vertex.x.set(vertex.x.get() - centroid.x + pastePoint.x);
					vertex.y.set(vertex.y.get() - centroid.y + pastePoint.y);
				}
				
				for(Edge edge : pasted.edges)
				{
					edge.handleX.set(edge.handleX.get() - centroid.x + pastePoint.x);
					edge.handleY.set(edge.handleY.get() - centroid.y + pastePoint.y);
					edge.suspendNotifications(false);
				}
				
				for(Caption caption : pasted.captions)
				{
					caption.x.set(caption.x.get() - centroid.x + pastePoint.x);
					caption.y.set(caption.y.get() - centroid.y + pastePoint.y);
				}
				
				pasted.suspendNotifications(false);
				
				graph.deselectAll();
				graph.union(pasted);
			}
			catch (Exception ex) { DebugUtilities.logException("An exception occurred while painting the viewport.", ex); }
		}	
	}
	
	public void printGraph() throws PrinterException
	{
		ViewportPrinter pv = new ViewportPrinter();
		pv.print();
	}
	
	public void redo()
	{
		if(undoHistory.next() != null)
			loadGraph(new Graph(undoHistory.current()));
	}
	
	public void removeGraphChangeListener(GraphChangeEventListener listener)
	{
		graphChangeListenerList.remove(GraphChangeEventListener.class, listener);
	}
	
	public void selectAll()
	{
		graph.selectAll();
	}
	
	public void selectAllEdges()
	{
		for (Edge e : graph.edges)
			e.isSelected.set(true);
	}

	public void selectAllVertexes()
	{
		for (Vertex v : graph.vertexes)
			v.isSelected.set(true);
	}
	
	public void setTool(Tool tool)
	{
		this.tool = tool;
		if(toolToolBar != null)
			toolToolBar.refresh();
		
		Cursor cursor;

		switch (this.tool)
		{
			case POINTER_TOOL: cursor = CursorBundle.get("pointer_tool_cursor"); break;
			case VERTEX_TOOL:  cursor = CursorBundle.get("vertex_tool_cursor");  break;
			case EDGE_TOOL:    cursor = CursorBundle.get("edge_tool_cursor");    break;
			case CUT_TOOL:	   cursor = CursorBundle.get("cut_tool_cursor");	 break;
			case CAPTION_TOOL: cursor = CursorBundle.get("caption_tool_cursor"); break;
			case PAINT_TOOL:   cursor = CursorBundle.get("paint_tool_cursor");   break;
			default:           cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR); break;
		}
		
		setCursor(cursor);
	}
	
	public void undo()
	{
		if(undoHistory.previous() != null)
			loadGraph(new Graph(undoHistory.current()));
	}
	
	private void viewportKeyPressed(KeyEvent event)
	{
		switch (event.getKeyCode())
		{
			case KeyEvent.VK_BACK_SPACE: // Fall through...
			case KeyEvent.VK_DELETE:
				{
					if (tool == Tool.POINTER_TOOL)
						graph.removeSelected();
					
					break;
				}
			case KeyEvent.VK_ESCAPE:
				{
					graph.deselectAll();
					break;
				}
			case KeyEvent.VK_UP:
				{
					double increment = -userSettings.arrowKeyIncrement.get();
					if(event.isAltDown()) increment /= 10.0;
					if(event.isShiftDown()) increment *= 10.0;
					
					if((event.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0)
					{
						transform.translate(0.0, -increment);
						isViewportInvalidated = true;
					}
					else
					{
						graph.moveSelected(0, increment);
						for (Edge edge : graph.edges)
							if (edge.isSelected.get())
								edge.fixHandle();
					}
					
					break;
				}
			case KeyEvent.VK_RIGHT:
				{
					double increment = userSettings.arrowKeyIncrement.get();
					if(event.isAltDown()) increment /= 10.0;
					if(event.isShiftDown()) increment *= 10.0;
					
					if((event.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0)
					{
						transform.translate(-increment, 0.0);
						isViewportInvalidated = true;
					}
					else
					{
						graph.moveSelected(increment, 0);
						for (Edge edge : graph.edges)
							if (edge.isSelected.get())
								edge.fixHandle();
					}
					
					break;
				}
			case KeyEvent.VK_DOWN:
				{
					double increment = userSettings.arrowKeyIncrement.get();
					if(event.isAltDown()) increment /= 10.0;
					if(event.isShiftDown()) increment *= 10.0;
					
					if((event.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0)
					{
						transform.translate(0.0, -increment);
						isViewportInvalidated = true;
					}
					else
					{
						graph.moveSelected(0, increment);
						for (Edge edge : graph.edges)
							if (edge.isSelected.get())
								edge.fixHandle();
					}
					
					break;
				}
			case KeyEvent.VK_LEFT:
				{
					double increment = -userSettings.arrowKeyIncrement.get();
					if(event.isAltDown()) increment /= 10.0;
					if(event.isShiftDown()) increment *= 10.0;
					
					if((event.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0)
					{
						transform.translate(-increment, 0.0);
						isViewportInvalidated = true;
					}
					else
					{
						graph.moveSelected(increment, 0);
						for (Edge edge : graph.edges)
							if (edge.isSelected.get())
								edge.fixHandle();
					}
					
					break;
				}
		}
	}
	
	private void viewportMouseDragged(MouseEvent event) throws NoninvertibleTransformException
	{
		Point oldPoint = new Point(currentMousePoint);
		transform.inverseTransform(event.getPoint(), currentMousePoint);
		
		if (tool == Tool.POINTER_TOOL && pointerToolClickedObject)
		{
			double xDifference = currentMousePoint.getX() - oldPoint.x;
			double yDifference = currentMousePoint.getY() - oldPoint.y;
			graph.moveSelected(xDifference, yDifference);
		}
		
		isViewportInvalidated = true;
	}
	
	private void viewportMousePressed(MouseEvent event) throws NoninvertibleTransformException
	{
		if (!viewport.hasFocus())
			viewport.requestFocus();
		
		int modifiers = event.getModifiersEx();
		transform.inverseTransform(event.getPoint(), pastMousePoint);
		transform.inverseTransform(event.getPoint(), currentMousePoint);
		
		switch (tool)
		{
			case POINTER_TOOL:
				{
					boolean isShiftDown = ((modifiers & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK);
					pointerToolClickedObject = false;
					
					for(int i = graph.vertexes.size() - 1; i >= 0; --i)
					{
						Vertex vertex = graph.vertexes.get(i);
						if (VertexDisplayView.wasClicked(vertex, currentMousePoint, transform.getScaleX()))
						{
							if (!vertex.isSelected.get() && !isShiftDown)
								graph.deselectAll();
							
							vertex.isSelected.set(true);
							pointerToolClickedObject = true;
							break;
						}
					}
					
					if (!pointerToolClickedObject)
						for(int i = graph.edges.size() - 1; i >= 0; --i)
						{
							Edge edge = graph.edges.get(i);
							if (EdgeDisplayView.wasClicked(edge, currentMousePoint, transform.getScaleX()))
							{
								if (!edge.isSelected.get() && !isShiftDown)
									graph.deselectAll();
								
								edge.isSelected.set(true);
								pointerToolClickedObject = true;
								break;
							}
						}
					
					if (!pointerToolClickedObject)
						for(int i = graph.captions.size() - 1; i >= 0; --i)
						{
							Caption caption = graph.captions.get(i);
							if (CaptionDisplayView.wasHandleClicked(caption, currentMousePoint, transform.getScaleX()))
							{
								if (!caption.isSelected.get() && !isShiftDown)
									graph.deselectAll();
								
								caption.isSelected.set(true);
								pointerToolClickedObject = true;
								break;
							}
						}
					
					if (!pointerToolClickedObject)
						for(int i = graph.captions.size() - 1; i >= 0; --i)
						{
							Caption caption = graph.captions.get(i);
							if (CaptionDisplayView.wasEditorClicked(caption, currentMousePoint, transform.getScaleX()))
							{
								if (!caption.isSelected.get() && !isShiftDown)
									graph.deselectAll();
								
								EditCaptionDialog.Value newValue = EditCaptionDialog.showDialog(this, this, caption.text.get(), caption.size.get());
								if (newValue != null)
								{
									caption.text.set(newValue.getText());
									caption.size.set(newValue.getSize());
								}
								
								pointerToolClickedObject = true;
								break;
							}
						}
					
					if (!pointerToolClickedObject && !isShiftDown)
						graph.deselectAll();
					
					break;
				}
			case VERTEX_TOOL:
				{
					// Simply add a new vertex at the location left-clicked
					if (event.getButton() == MouseEvent.BUTTON1)
						graph.vertexes.add(new Vertex(graph.nextVertexId(), currentMousePoint.x, currentMousePoint.y));
					
					break;
				}
			case EDGE_TOOL:
				{
					if (event.getButton() == MouseEvent.BUTTON1)
					{
						// The procedure for adding an edge using the edge tool is to click a vertex the edge will come from and subsequently a vertex
						// the edge will go to
						boolean fromVertexClicked = false;
						
						for (Vertex vertex : graph.vertexes)
							if (VertexDisplayView.wasClicked(vertex, currentMousePoint, transform.getScaleX()))
								if (fromVertex == null)
								{
									// If the user has not yet defined a from Vertex, make this one so
									vertex.isSelected.set(true);
									fromVertex = vertex;
									fromVertexClicked = true;
									break;
								}
								else // If the user has already defined a from Vertex, try to add an edge between it and this one
								{
									// Only allow loops if the graph specifies to
									if(graph.areLoopsAllowed || vertex != fromVertex)
									{
										// Only allow multiple edges if the graph specifies to
										if(graph.areMultipleEdgesAllowed || graph.getEdges(fromVertex, vertex).size() == 0)
										{
											// Only allow a cycle if the graph specifies to
											if(graph.areCyclesAllowed || !graph.areConnected(fromVertex, vertex))
											{
												graph.edges.add(new Edge(graph.areDirectedEdgesAllowed, fromVertex, vertex));
											}
										}
									}
								}
						
						// If the user didn't click a from vertex (clicked a to Vertex or nothing), reset and deselect all
						if (!fromVertexClicked && event.getButton() == MouseEvent.BUTTON1)
						{
							fromVertex = null;
							graph.deselectAll();
						}
					}
					
					break;
				}
			case CAPTION_TOOL:
				{
					if (event.getButton() == MouseEvent.BUTTON1)
					{
						Caption caption = new Caption(currentMousePoint.x, currentMousePoint.y, "");
						graph.captions.add(caption);
						
						EditCaptionDialog.Value newValue = EditCaptionDialog.showDialog(this, this, userSettings.defaultCaptionText.get(), userSettings.defaultCaptionFontSize.get());
						if (newValue != null)
						{
							caption.text.set(newValue.getText());
							caption.size.set(newValue.getSize());
						}
						else
							graph.captions.remove(caption);
					}
					
					break;
				}
			case CUT_TOOL:
				{
					cutToolClickedObject = false;
					
					if (event.getButton() == MouseEvent.BUTTON1)
					{
						for (Vertex vertex : graph.vertexes)
							if (VertexDisplayView.wasClicked(vertex, currentMousePoint, transform.getScaleX()))
							{
								graph.vertexes.remove(vertex);
								cutToolClickedObject = true;
								break;
							}
						
						if (!cutToolClickedObject)
							for (Edge edge : graph.edges)
								if (EdgeDisplayView.wasClicked(edge, currentMousePoint, transform.getScaleX()))
								{
									graph.edges.remove(edge);
									cutToolClickedObject = true;
									break;
								}
						
						if (!cutToolClickedObject)
							for (Caption caption : graph.captions)
								if (CaptionDisplayView.wasHandleClicked(caption, currentMousePoint, transform.getScaleX()))
								{
									graph.captions.remove(caption);
									cutToolClickedObject = true;
									break;
								}
					}
					
					break;
				}
			case PAINT_TOOL:
				{
					paintToolClickedObject = false;
					
					if (event.getButton() == MouseEvent.BUTTON1)
					{
						for (Vertex vertex : graph.vertexes)
							if (VertexDisplayView.wasClicked(vertex, currentMousePoint, transform.getScaleX()))
							{
								vertex.color.set(paintColor);
								paintToolClickedObject = true;
								break;
							}
						
						if (!paintToolClickedObject)
							for (Edge edge : graph.edges)
								if (EdgeDisplayView.wasClicked(edge, currentMousePoint, transform.getScaleX()))
								{
									edge.color.set(paintColor);
									paintToolClickedObject = true;
									break;
								}
					}
				}
		}
		
		isMouseDownOnCanvas = true;
		isViewportInvalidated = true;
	}
	
	private void viewportMouseDoubleClicked(MouseEvent event) throws NoninvertibleTransformException
	{
		if (!viewport.hasFocus())
			viewport.requestFocus();
		
		transform.inverseTransform(event.getPoint(), pastPanPoint);
		
		if(userSettings.panOnDoubleClick.get())
		{
			if(panTimer != null)
				panTimer.stop();
			
			panTimer = new Timer(50, new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Timer timer = (Timer)e.getSource(); 
					
					try
					{
						Point2D focusPoint = new Point2D.Double();
						transform.inverseTransform(new Point(viewport.getWidth() / 2, viewport.getHeight() / 2), focusPoint);
						
						double xDelta = Math.round((pastMousePoint.x - focusPoint.getX()) / userSettings.panDecelerationFactor.get());
						double yDelta = Math.round((pastMousePoint.y - focusPoint.getY()) / userSettings.panDecelerationFactor.get());
						
						transform.translate(xDelta, yDelta);
						isViewportInvalidated = true;
						
						if(xDelta == 0 && yDelta == 0)
							timer.stop();
					}
					catch (Exception ex)
					{
						DebugUtilities.logException("An exception occurred while panning viewport.", ex);
						timer.stop();
					}
				} 
			} );
			panTimer.start();
		}
	}
	
 	private void viewportMouseReleased(MouseEvent event) throws NoninvertibleTransformException
	{
		switch (tool)
		{
			case POINTER_TOOL:
				{
					if (!pastMousePoint.equals(currentMousePoint))
						if (!pointerToolClickedObject)
						{
							Rectangle selection = getSelectionRectangle();
							
							for (Vertex vertex : graph.vertexes)
								if (VertexDisplayView.wasSelected(vertex, selection))
									vertex.isSelected.set(true);
							
							for (Edge edge : graph.edges)
								if (EdgeDisplayView.wasSelected(edge, selection))
									edge.isSelected.set(true);
							
							for (Caption caption : graph.captions)
								if (CaptionDisplayView.wasHandleSelected(caption, selection))
									caption.isSelected.set(true);
						}
						else
							for (Edge edge : graph.edges)
								if (edge.isSelected.get() && edge.isLinear())
									edge.fixHandle();
					
					if (event.getButton() == MouseEvent.BUTTON3)
					{
						boolean isVertexSelected = false;
						for (Vertex vertex : graph.vertexes)
							if(vertex.isSelected.get())
							{
								isVertexSelected = true;
								break;
							}
						
						boolean isEdgeSelected = false;
						for (Edge edge : graph.edges)
							if(edge.isSelected.get())
							{
								isEdgeSelected = true;
								break;
							}
						
						viewportPopupMenu.setVertexMenuEnabled(isVertexSelected);
						viewportPopupMenu.setEdgeMenuEnabled(isEdgeSelected);
						viewportPopupMenu.show(viewport, event.getPoint().x, event.getPoint().y);
					}
					
					break;
				}
			case EDGE_TOOL:
				{
					if (fromVertex != null)
					{
						for (Vertex vertex : graph.vertexes)
						{
							if (fromVertex != vertex && VertexDisplayView.wasClicked(vertex, currentMousePoint, transform.getScaleX()))
							{
								// Only allow multiple edges if the graph specifies to
								if(graph.areMultipleEdgesAllowed || graph.getEdges(fromVertex, vertex).size() == 0)
								{
									// Only allow a cycle if the graph specifies to
									if(graph.areCyclesAllowed || !graph.areConnected(fromVertex, vertex))
									{
										graph.edges.add(new Edge(graph.areDirectedEdgesAllowed, fromVertex, vertex));
										fromVertex = null;
										graph.deselectAll();
									}
								}
								break;
							}
						}
					}
					break;
				}
			case CUT_TOOL:
				{
					if (!cutToolClickedObject && !pastMousePoint.equals(currentMousePoint))
					{
						Rectangle selection = getSelectionRectangle();
						graph.deselectAll();
						
						for (Vertex vertex : graph.vertexes)
							if (VertexDisplayView.wasSelected(vertex, selection))
								vertex.isSelected.set(true);
						
						for (Edge edge : graph.edges)
							if (EdgeDisplayView.wasSelected(edge, selection))
								edge.isSelected.set(true);
						
						for (Caption caption : graph.captions)
							if (CaptionDisplayView.wasHandleSelected(caption, selection))
								caption.isSelected.set(true);
						
						graph.removeSelected();
					}
					
					break;
				}
			case PAINT_TOOL:
				{
					if (!paintToolClickedObject && !pastMousePoint.equals(currentMousePoint))
					{
						Rectangle selection = getSelectionRectangle();
						
						for (Vertex vertex : graph.vertexes)
							if (VertexDisplayView.wasSelected(vertex, selection))
								vertex.color.set(paintColor);
						
						for (Edge edge : graph.edges)
							if (EdgeDisplayView.wasSelected(edge, selection))
								edge.color.set(paintColor);
					}
					
					break;
				}
		}
		
		isMouseDownOnCanvas = false;
		isViewportInvalidated = true;
	}
	
	public void zoomCenter(Point2D.Double center, double factor)
	{
		transform.translate(Math.round(center.x), Math.round(center.y));
		
		transform.scale(factor, factor);
		if (transform.getScaleX() > userSettings.maximumZoomFactor.get())
			zoomMax();
		
		transform.translate(Math.round(-center.x), Math.round(-center.y));
		
		isViewportInvalidated = true;
	}
	
	public void zoomFit(Rectangle2D rectangle)
	{
		// First we need to reset and translate the graph to the viewport's center
		transform.setToIdentity();
		transform.translate(Math.round(viewport.getWidth() / 2.0), Math.round(viewport.getHeight() / 2.0));
		
		// We need to fit it to the viewport. So we want to scale according to the lowest viewport-to-graph dimension ratio.
		double widthRatio = (viewport.getWidth() - userSettings.zoomGraphPadding.get()) / rectangle.getWidth();
		double heightRatio = (viewport.getHeight() - userSettings.zoomGraphPadding.get()) / rectangle.getHeight();
		double minRatio = Math.min(widthRatio, heightRatio);
		
		if (minRatio < 1)
			transform.scale(minRatio, minRatio);
		
		// Only now that we've properly scaled can we translate to the graph's midpoint
		Point2D.Double graphCenter = new Point2D.Double(rectangle.getCenterX(), rectangle.getCenterY());
		transform.translate(Math.round(-graphCenter.x), Math.round(-graphCenter.y));
		
		// And of course, we want to refresh the viewport
		isViewportInvalidated = true;
	}
	
	public void zoomFit()
	{
		if (graph.vertexes.size() > 0)
			zoomFit(GraphDisplayView.getBounds(graph));
	}
	
	public void zoomOneToOne()
	{
		transform.setTransform(1, transform.getShearY(), transform.getShearX(), 1, (int)transform.getTranslateX(), (int)transform.getTranslateY());
		isViewportInvalidated = true;
	}
	
	public void zoomMax()
	{
		transform.setTransform(userSettings.maximumZoomFactor.get(), transform.getShearY(), transform.getShearX(), userSettings.maximumZoomFactor.get(), Math.round(transform.getTranslateX()), Math.round(transform.getTranslateY()));
		isViewportInvalidated = true;
	}

	public enum Tool
	{
		POINTER_TOOL, VERTEX_TOOL, EDGE_TOOL, CAPTION_TOOL, CUT_TOOL, PAINT_TOOL
	}
	
	private class ToolToolBar extends JToolBar
	{
		private JButton pointerToolButton;
		private JButton vertexToolButton;
		private JButton edgeToolButton;
		private JButton captionToolButton;
		private JButton cutToolButton;
		private JButton paintToolButton;
		private JPopupMenu paintMenu;
		private Timer	paintMenuTimer;
		
		public ToolToolBar()
		{
			this.setOrientation(SwingConstants.VERTICAL);
			this.setFloatable(false);
			
			pointerToolButton = new JButton(ImageIconBundle.get("pointer_tool_icon"));
			pointerToolButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					setTool(Tool.POINTER_TOOL);
					graph.deselectAll();
				}
			});
			pointerToolButton.setToolTipText(StringBundle.get("pointer_tool_tooltip"));
			pointerToolButton.setSelected(true);
			this.add(pointerToolButton);
			
			vertexToolButton = new JButton(ImageIconBundle.get("vertex_tool_icon"));
			vertexToolButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					setTool(Tool.VERTEX_TOOL);
					graph.deselectAll();
				}
			});
			vertexToolButton.setToolTipText(StringBundle.get("vertex_tool_tooltip"));
			this.add(vertexToolButton);
			
			edgeToolButton = new JButton(ImageIconBundle.get("edge_tool_icon"));
			edgeToolButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					setTool(Tool.EDGE_TOOL);
					graph.deselectAll();
				}
			});
			edgeToolButton.setToolTipText(StringBundle.get("edge_tool_tooltip"));
			this.add(edgeToolButton);
			
			captionToolButton = new JButton(ImageIconBundle.get("caption_tool_icon"));
			captionToolButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					setTool(Tool.CAPTION_TOOL);
					graph.deselectAll();
				}
			});
			captionToolButton.setToolTipText(StringBundle.get("caption_tool_tooltip"));
			this.add(captionToolButton);
			
			cutToolButton = new JButton(ImageIconBundle.get("cut_tool_icon"));
			cutToolButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					setTool(Tool.CUT_TOOL);
					graph.deselectAll();
				}
			});
			cutToolButton.setToolTipText(StringBundle.get("cut_tool_tooltip"));
			this.add(cutToolButton);
			
			paintToolButton = new JButton(ImageIconBundle.get("paint_tool_icon"));
			paintToolButton.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseExited(MouseEvent e)
				{
					isMouseDownOnPaintToolButton = false;
				}
				
				@Override
				public void mousePressed(MouseEvent event)
				{
					graph.deselectAll();
					setTool(Tool.PAINT_TOOL);
					isMouseDownOnPaintToolButton = true;
					paintMenuTimer.start();
				}
				
				@Override
				public void mouseReleased(MouseEvent event)
				{
					isMouseDownOnPaintToolButton = false;
				}
			});
			paintMenuTimer = new Timer(userSettings.paintToolMenuDelay.get(), new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					paintMenuTimer.stop();
					
					if (isMouseDownOnPaintToolButton)
						paintMenu.show(paintToolButton, 0, paintToolButton.getHeight());
				}
			});
			paintToolButton.setToolTipText(StringBundle.get("paint_tool_tooltip"));
			this.add(paintToolButton);
			
			setTool(Tool.POINTER_TOOL);
			
			paintMenu = new JPopupMenu();
			refreshPaintMenu();
			paintColor = -1;
			
			refresh();
		}
	
		public void refresh()
		{
			for (Component toolButton : this.getComponents())
				if (toolButton instanceof JButton)
					((JButton) toolButton).setSelected(false);
			
			switch (tool)
			{
				case POINTER_TOOL: pointerToolButton.setSelected(true); break;
				case VERTEX_TOOL:  vertexToolButton.setSelected(true);	break;
				case EDGE_TOOL:	   edgeToolButton.setSelected(true);	break;
				case CUT_TOOL:	   cutToolButton.setSelected(true);		break;
				case CAPTION_TOOL: captionToolButton.setSelected(true);	break;
				case PAINT_TOOL:   paintToolButton.setSelected(true);	break;
			}
		}
		
		public void refreshPaintMenu()
		{
			paintMenu.removeAll();
			
			ActionListener paintMenuItemActionListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					paintColor = paintMenu.getComponentIndex((Component) e.getSource()) - 1;				
					
					for (Component paintMenuItem : paintMenu.getComponents())
						if (paintMenuItem instanceof JMenuItem)
							((JCheckBoxMenuItem) paintMenuItem).setState(false);
					
					if (paintColor + 1 < paintMenu.getComponentCount() && paintMenu.getComponent(paintColor + 1) instanceof JMenuItem)
						((JCheckBoxMenuItem) paintMenu.getComponent(paintColor + 1)).setState(true);
				}
			};
			
			JCheckBoxMenuItem defaultPaintToolMenuItem = new JCheckBoxMenuItem(StringBundle.get("default_paint_tool_menu_item"));
			defaultPaintToolMenuItem.addActionListener(paintMenuItemActionListener);
			defaultPaintToolMenuItem.setSelected(true);
			paintMenu.add(defaultPaintToolMenuItem);
			
			for (int i = 0; i < userSettings.elementColors.size(); ++i)
			{
				JCheckBoxMenuItem paintToolMenuItem = new JCheckBoxMenuItem("\u2588\u2588\u2588\u2588\u2588\u2588  (" + i + ")");
				paintToolMenuItem.setForeground(userSettings.getElementColor(i));
				paintToolMenuItem.addActionListener(paintMenuItemActionListener);
				paintMenu.add(paintToolMenuItem);
			}
		}
	}
	
	private class ArrangeToolBar extends JToolBar
	{
		private JButton arrangeCircleButton;
		private JButton arrangeGridButton;
		private JButton arrangeTreeButton;
		private JButton arrangeWebButton;
		private JButton alignVerticallyButton;
		private JButton alignHorizontallyButton;
		private JButton distributeHorizontallyButton;
		private JButton distributeVerticallyButton;
		private JButton rotateLeft90Button;
		private JButton rotateRight90Button;
		private JButton flipHorizontallyButton;
		private JButton flipVerticallyButton;
		private JButton contractButton;
		private JButton expandButton;
		
		public ArrangeToolBar()
		{
			arrangeCircleButton = new JButton(ImageIconBundle.get("arrange_circle_icon"));
			arrangeCircleButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					ArrayList<Vertex> selected = new ArrayList<Vertex>();
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
							selected.add(vertex);
					
					if(selected.size() < 1)
						selected.addAll(graph.vertexes);
					
					double centroidX = 0, centroidY = 0;
					for(Vertex vertex : selected)
					{
						centroidX += vertex.x.get();
						centroidY += vertex.y.get();
					}
					
					centroidX /= selected.size();
					centroidY /= selected.size();
					
					double radius = userSettings.arrangeCircleRadiusMultiplier.get() * selected.size();
					double degreesPerVertex = 2 * Math.PI / selected.size();
					
					for (int i = 0; i < selected.size(); ++i)
					{
						selected.get(i).x.set(radius * Math.cos(degreesPerVertex * i - Math.PI / 2.0) + centroidX);
						selected.get(i).y.set(radius * Math.sin(degreesPerVertex * i - Math.PI / 2.0) + centroidY);
					}
					
					zoomFit();
				}
			});
			arrangeCircleButton.setToolTipText(StringBundle.get("arrange_circle_button_tooltip"));
			this.add(arrangeCircleButton);
			
			arrangeGridButton = new JButton(ImageIconBundle.get("arrange_grid_icon"));
			arrangeGridButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					ArrayList<Vertex> selected = new ArrayList<Vertex>();
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
							selected.add(vertex);
					
					if(selected.size() < 1)
						selected.addAll(graph.vertexes);
					
					double centroidX = 0, centroidY = 0;
					for(Vertex vertex : selected)
					{
						centroidX += vertex.x.get();
						centroidY += vertex.y.get();
					}
					
					centroidX /= selected.size();
					centroidY /= selected.size();
					
					int n = selected.size();
					int rows = (int) Math.round(Math.sqrt(n));
					int columns = (int) Math.ceil(n / (double) rows);
					Point2D.Double location = new Point2D.Double((columns / 2.0) * -userSettings.arrangeGridSpacing.get(), (rows / 2.0) * -userSettings.arrangeGridSpacing.get());
					
					for (int row = 0; row < rows; ++row)
						for(int col = 0; (row < rows - 1 && col < columns) || (row == rows - 1 && col < (n % columns == 0 ? columns : n % columns)); ++col)
						{
							selected.get(row * columns + col).x.set(location.x + userSettings.arrangeGridSpacing.get() * col + centroidX);
							selected.get(row * columns + col).y.set(location.y + userSettings.arrangeGridSpacing.get() * row + centroidY);
						}
					
					zoomFit();
				}
			});
			arrangeGridButton.setToolTipText(StringBundle.get("arrange_grid_button_tooltip"));
			this.add(arrangeGridButton);
			
			arrangeTreeButton = new JButton(ImageIconBundle.get("arrange_tree_icon"));
			arrangeTreeButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Vector<Vertex> allNodes = new Vector<Vertex>();
					Vector<Vector<Vertex>> levels = new Vector<Vector<Vertex>>();
					
					// First we need to add all selected vertexes to a root level of the tree
					levels.add(new Vector<Vertex>());
					for (Vertex vertex : graph.vertexes)
						if (vertex.isSelected.get())
						{
							levels.get(0).add(vertex);
							allNodes.add(vertex);
						}
					
					// While the last level has vertexes, add all their neighbors to the next level that haven't yet been otherwise added
					while (levels.lastElement().size() > 0)
					{
						levels.add(new Vector<Vertex>());
						
						for (Vertex vertex : levels.get(levels.size() - 2))
							for (Vertex neighbor : graph.getNeighbors(vertex))
								if (!allNodes.contains(neighbor))
								{
									levels.lastElement().add(neighbor);
									allNodes.add(neighbor);
								}
					}
					
					// If there were any nodes that weren't added yet, give them their own level
					if (allNodes.size() < graph.vertexes.size())
						for (Vertex vertex : levels.get(levels.size() - 1))
							if (!allNodes.contains(vertex))
							{
								levels.lastElement().add(vertex);
								allNodes.add(vertex);
							}
					
					// If the last level is empty, remove it
					if (levels.lastElement().size() == 0)
						levels.remove(levels.size() - 1);
					
					// Now for the layout!
					double y = 0.0;
					double largestWidth = 0;
					for (Vector<Vertex> level : levels)
						largestWidth = Math.max(largestWidth, level.size() * 150.0);
					
					for (int row = 0; row < levels.size(); ++row)
					{
						Vector<Vertex> level = levels.get(row);
						y += 150;
						double colSpace = largestWidth / (level.size());
						
						for (int col = 0; col < level.size(); ++col)
						{
							Vertex vertex = level.get(col);
							double x = (col + .5) * colSpace - largestWidth / 2.0;
							
							vertex.x.set(x);
							vertex.y.set(y);
						}
					}
					
					zoomFit();
				}
			});
			arrangeTreeButton.setToolTipText(StringBundle.get("arrange_tree_button_tooltip"));
			this.add(arrangeTreeButton);
			
			arrangeWebButton = new JButton(ImageIconBundle.get("arrange_web_icon"));
			arrangeWebButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					new Timer(50, new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							Timer timer = (Timer)e.getSource(); 
							
							timer.setDelay((int)(timer.getDelay() * userSettings.autoArrangeDecelerationFactor.get()));
							
							ArrayList<Vertex> selected = new ArrayList<Vertex>();
							
							for(Vertex vertex : graph.vertexes)
								if(vertex.isSelected.get())
									selected.add(vertex);
							
							boolean noneWereSelected = (selected.size() < 1);
							
							if(noneWereSelected)
								selected.addAll(graph.vertexes);
							
							HashMap<Vertex, Point2D.Double> forces = new HashMap<Vertex, Point2D.Double>();
						
							// Initialize the hashmap of forces
							for (int i = 0; i < selected.size(); ++i)
								forces.put(selected.get(i), new Point2D.Double(0, 0));
							
							// Calculate all repulsive forces
							for (int i = 0; i < selected.size(); ++i)
								for (int j = i + 1; j < selected.size(); ++j)
								{
									Vertex v0 = selected.get(i);
									Vertex v1 = selected.get(j);
									
									double xDiff = v1.x.get() - v0.x.get();
									double yDiff = v1.y.get() - v0.y.get();
									double distanceSquared = (xDiff * xDiff + yDiff * yDiff);
									double xForce = userSettings.autoArrangeRepulsiveForce.get() * (xDiff / distanceSquared);
									double yForce = userSettings.autoArrangeRepulsiveForce.get() * (yDiff / distanceSquared);
									
									Point2D.Double force0 = forces.get(v0);
									force0.x += xForce; force0.y += yForce;
									
									// And because every action has an opposite and equal reaction
									Point2D.Double force1 = forces.get(v1);
									force1.x -= xForce; force1.y -= yForce;
								}
							
							// Calculate all attractive forces
							for (Edge edge : graph.edges)
								if (edge.from != edge.to && (noneWereSelected || edge.from.isSelected.get() || edge.to.isSelected.get()))
								{
									double xDiff = edge.to.x.get() - edge.from.x.get();
									double yDiff = edge.to.y.get() - edge.from.y.get();
									double distanceSquared = (xDiff * xDiff + yDiff * yDiff);
									double xForce = userSettings.autoArrangeAttractiveForce.get() * xDiff * distanceSquared;
									double yForce = userSettings.autoArrangeAttractiveForce.get() * yDiff * distanceSquared;
									
									if(noneWereSelected || edge.from.isSelected.get())
									{
										Point2D.Double force = forces.get(edge.from);
										force.x += xForce; force.y += yForce;
									}
									
									// And because every action has an opposite and equal reaction
									if(noneWereSelected || edge.to.isSelected.get())
									{
										Point2D.Double force = forces.get(edge.to);
										force.x -= xForce; force.y -= yForce;
									}
								}
							
							double netForce = 0.0;
							
							// Apply all net forces
							for (Vertex v : selected)
							{
								Point2D.Double force = forces.get(v);
								v.x.set(v.x.get() + force.x);
								v.y.set(v.y.get() + force.y);
								
								// And whilst I know I should be using the magnitude, this is quicker
								netForce += Math.abs(force.x) + Math.abs(force.y);
							}
							
							if(timer.getDelay() >= 500 || (!noneWereSelected && netForce / selected.size() < 0.1))
								timer.stop(); 
						} 
					} ).start();	
				}
			});
			arrangeWebButton.setToolTipText(StringBundle.get("arrange_web_button_tooltip"));
			this.add(arrangeWebButton);
			
			this.add(new JToolBar.Separator());
			
			alignHorizontallyButton = new JButton(ImageIconBundle.get("align_horizontally_icon"));
			alignHorizontallyButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					double centerY = 0.0, selectedCount = 0.0;
					
					for(int i = 0; i < graph.vertexes.size(); ++i)
						if(graph.vertexes.get(i).isSelected.get())
						{
							centerY += graph.vertexes.get(i).y.get();
							++selectedCount;
						}
					
					centerY /= selectedCount;
					
					for(int i = 0; i < graph.vertexes.size(); ++i)
						if(graph.vertexes.get(i).isSelected.get())
							graph.vertexes.get(i).y.set(centerY);
				}
			});
			alignHorizontallyButton.setToolTipText(StringBundle.get("align_horizontally_button_tooltip"));
			this.add(alignHorizontallyButton);
			
			alignVerticallyButton = new JButton(ImageIconBundle.get("align_vertically_icon"));
			alignVerticallyButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					double centerX = 0.0, selectedCount = 0.0;
					
					for(int i = 0; i < graph.vertexes.size(); ++i)
						if(graph.vertexes.get(i).isSelected.get())
						{
							centerX += graph.vertexes.get(i).x.get();
							++selectedCount;
						}
					
					centerX /= selectedCount;
					
					for(int i = 0; i < graph.vertexes.size(); ++i)
						if(graph.vertexes.get(i).isSelected.get())
							graph.vertexes.get(i).x.set(centerX);
				}
			});
			alignVerticallyButton.setToolTipText(StringBundle.get("align_vertically_button_tooltip"));
			this.add(alignVerticallyButton);
			
			distributeHorizontallyButton = new JButton(ImageIconBundle.get("distribute_horizontally_icon"));
			distributeHorizontallyButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Vector<Vertex> selectedVertexes = new Vector<Vertex>();
					
					for(int i = 0; i < graph.vertexes.size(); ++i)
						if(graph.vertexes.get(i).isSelected.get())
							selectedVertexes.add(graph.vertexes.get(i));
					
					Collections.sort(selectedVertexes, new Comparator<Vertex>() { public int compare(Vertex v1, Vertex v2) { return new Double(Math.signum(v1.x.get() - v2.x.get())).intValue() ; } } );
					double spacing = (selectedVertexes.lastElement().x.get() - selectedVertexes.firstElement().x.get()) / (double)(selectedVertexes.size() - 1); 
					double currentX = selectedVertexes.firstElement().x.get() - spacing;
					
					for(Vertex vertex : selectedVertexes)
						vertex.x.set(currentX += spacing);
				}
			});
			distributeHorizontallyButton.setToolTipText(StringBundle.get("distribute_horizontally_button_tooltip"));
			this.add(distributeHorizontallyButton);
			
			distributeVerticallyButton = new JButton(ImageIconBundle.get("distribute_vertically_icon"));
			distributeVerticallyButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Vector<Vertex> selectedVertexes = new Vector<Vertex>();
					
					for(int i = 0; i < graph.vertexes.size(); ++i)
						if(graph.vertexes.get(i).isSelected.get())
							selectedVertexes.add(graph.vertexes.get(i));
					
					Collections.sort(selectedVertexes, new Comparator<Vertex>() { public int compare(Vertex v1, Vertex v2) { return new Double(Math.signum(v1.y.get() - v2.y.get())).intValue() ; } } );
					double spacing = (selectedVertexes.lastElement().y.get() - selectedVertexes.firstElement().y.get()) / (double)(selectedVertexes.size() - 1); 
					double currentY = selectedVertexes.firstElement().y.get() - spacing;
					
					for(Vertex vertex : selectedVertexes)
						vertex.y.set(currentY += spacing);
				}
			});
			distributeVerticallyButton.setToolTipText(StringBundle.get("distribute_vertically_button_tooltip"));
			this.add(distributeVerticallyButton);
			
			this.add(new JToolBar.Separator());
			
			rotateLeft90Button = new JButton(ImageIconBundle.get("rotate_left_90_icon"));
			rotateLeft90Button.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int selectedElementCount = 0;
					Point2D.Double centroid = new Point2D.Double();
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
						{
							centroid.x += vertex.x.get();
							centroid.y += vertex.y.get();
							++selectedElementCount;
						}
					
					for(Edge edge : graph.edges)
						if(edge.isSelected.get())
						{
							centroid.x += edge.handleX.get();
							centroid.y += edge.handleY.get();
							++selectedElementCount;
						}
					
					centroid.x /= (double)selectedElementCount;
					centroid.y /= (double)selectedElementCount;
					
					for(Edge edge : graph.edges)
						if(edge.isSelected.get())
							edge.suspendNotifications(true);
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
						{
							double oldVertexX = vertex.x.get();
							vertex.x.set(centroid.x - (centroid.y - vertex.y.get()));
							vertex.y.set(centroid.y + (centroid.x - oldVertexX));
						}
					
					for(Edge edge : graph.edges)
						if(edge.isSelected.get())
						{
							double oldEdgeHandleX = edge.handleX.get();
							edge.handleX.set(centroid.x - (centroid.y - edge.handleY.get()));
							edge.handleY.set(centroid.y + (centroid.x - oldEdgeHandleX));
							edge.suspendNotifications(false);
							edge.refresh();
						}
				}
			});
			rotateLeft90Button.setToolTipText(StringBundle.get("rotate_left_90_button_tooltip"));
			this.add(rotateLeft90Button);
			
			rotateRight90Button = new JButton(ImageIconBundle.get("rotate_right_90_icon"));
			rotateRight90Button.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int selectedElementCount = 0;
					Point2D.Double centroid = new Point2D.Double();
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
						{
							centroid.x += vertex.x.get();
							centroid.y += vertex.y.get();
							++selectedElementCount;
						}
					
					for(Edge edge : graph.edges)
						if(edge.isSelected.get())
						{
							centroid.x += edge.handleX.get();
							centroid.y += edge.handleY.get();
							++selectedElementCount;
						}
					
					centroid.x /= (double)selectedElementCount;
					centroid.y /= (double)selectedElementCount;
					
					for(Edge edge : graph.edges)
						if(edge.isSelected.get())
							edge.suspendNotifications(true);
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
						{
							double oldVertexX = vertex.x.get();
							vertex.x.set(centroid.x + (centroid.y - vertex.y.get()));
							vertex.y.set(centroid.y - (centroid.x - oldVertexX));
						}
					
					for(Edge edge : graph.edges)
						if(edge.isSelected.get())
						{
							double oldEdgeHandleX = edge.handleX.get();
							edge.handleX.set(centroid.x + (centroid.y - edge.handleY.get()));
							edge.handleY.set(centroid.y - (centroid.x - oldEdgeHandleX));
							edge.suspendNotifications(false);
							edge.refresh();
						}
				}
			});
			rotateRight90Button.setToolTipText(StringBundle.get("rotate_right_90_button_tooltip"));
			this.add(rotateRight90Button);
			
			flipHorizontallyButton = new JButton(ImageIconBundle.get("flip_horizontally_icon"));
			flipHorizontallyButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int selectedElementCount = 0;
					double centerX = 0.0;
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
						{
							centerX += vertex.x.get();
							++selectedElementCount;
						}
					
					for(Edge edge : graph.edges)
						if(edge.isSelected.get())
						{
							centerX += edge.handleX.get();
							++selectedElementCount;
						}
					
					centerX /= (double)selectedElementCount;
					
					for(Edge edge : graph.edges)
						if(edge.isSelected.get())
							edge.suspendNotifications(true);
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
							vertex.x.set(2.0 * centerX - vertex.x.get());
					
					for(Edge edge : graph.edges)
						if(edge.isSelected.get())
						{
							edge.handleX.set(2.0 * centerX - edge.handleX.get());
							edge.suspendNotifications(false);
							edge.refresh();
						}
				}
			});
			flipHorizontallyButton.setToolTipText(StringBundle.get("flip_horizontally_button_tooltip"));
			this.add(flipHorizontallyButton);
			
			flipVerticallyButton = new JButton(ImageIconBundle.get("flip_vertically_icon"));
			flipVerticallyButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int selectedElementCount = 0;
					double centerY = 0.0;
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
						{
							centerY += vertex.y.get();
							++selectedElementCount;
						}
					
					for(Edge edge : graph.edges)
						if(edge.isSelected.get())
						{
							centerY += edge.handleY.get();
							++selectedElementCount;
						}
					
					centerY /= (double)selectedElementCount;
					
					for(Edge edge : graph.edges)
						if(edge.isSelected.get())
							edge.suspendNotifications(true);
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
							vertex.y.set(2.0 * centerY - vertex.y.get());
					
					for(Edge edge : graph.edges)
						if(edge.isSelected.get())
						{
							edge.handleY.set(2.0 * centerY - edge.handleY.get());
							edge.suspendNotifications(false);
							edge.refresh();
						}
				}
			});
			flipVerticallyButton.setToolTipText(StringBundle.get("flip_vertically_button_tooltip"));
			this.add(flipVerticallyButton);
			
			this.add(new JToolBar.Separator());
			
			contractButton = new JButton(ImageIconBundle.get("contract_icon"));
			contractButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int selectedElementCount = 0;
					Point2D.Double centroid = new Point2D.Double();
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
						{
							centroid.x += vertex.x.get();
							centroid.y += vertex.y.get();
							++selectedElementCount;
						}
					
					if(selectedElementCount == 0)
						return;
					
					centroid.x /= (double)selectedElementCount;
					centroid.y /= (double)selectedElementCount;
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
						{
							vertex.x.set(userSettings.arrangeContractFactor.get() * (vertex.x.get() - centroid.x) + centroid.x);
							vertex.y.set(userSettings.arrangeContractFactor.get() * (vertex.y.get() - centroid.y) + centroid.y);
						}
				}
			});
			contractButton.setToolTipText(StringBundle.get("contract_button_tooltip"));
			this.add(contractButton);
			
			expandButton = new JButton(ImageIconBundle.get("expand_icon"));
			expandButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int selectedElementCount = 0;
					Point2D.Double centroid = new Point2D.Double();
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
						{
							centroid.x += vertex.x.get();
							centroid.y += vertex.y.get();
							++selectedElementCount;
						}
					
					if(selectedElementCount == 0)
						return;
					
					centroid.x /= (double)selectedElementCount;
					centroid.y /= (double)selectedElementCount;
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
						{
							vertex.x.set(userSettings.arrangeExpandFactor.get() * (vertex.x.get() - centroid.x) + centroid.x);
							vertex.y.set(userSettings.arrangeExpandFactor.get() * (vertex.y.get() - centroid.y) + centroid.y);
						}
				}
			});
			expandButton.setToolTipText(StringBundle.get("expand_button_tooltip"));
			this.add(expandButton);
		}
	}
	
	private class ViewToolBar extends JToolBar
	{
		private JButton showVertexLabelsButton;
		private JButton showVertexWeightsButton;
		private JButton showEdgeHandlesButton;
		private JButton showEdgeLabelsButton;
		private JButton showEdgeWeightsButton;
		
		public ViewToolBar()
		{
			showVertexLabelsButton = new JButton(ImageIconBundle.get("show_vertex_labels_icon"));
			showVertexLabelsButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					settings.showVertexLabels.set(!settings.showVertexLabels.get());
				}
			});
			showVertexLabelsButton.setToolTipText(StringBundle.get("show_vertex_labels_button_tooltip"));
			this.add(showVertexLabelsButton);
			
			showVertexWeightsButton = new JButton(ImageIconBundle.get("show_vertex_weights_icon"));
			showVertexWeightsButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					settings.showVertexWeights.set(!settings.showVertexWeights.get());
				}
			});
			showVertexWeightsButton.setToolTipText(StringBundle.get("show_vertex_weights_button_tooltip"));
			this.add(showVertexWeightsButton);
			
			showEdgeHandlesButton = new JButton(ImageIconBundle.get("show_edge_handles_icon"));
			showEdgeHandlesButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					settings.showEdgeHandles.set(!settings.showEdgeHandles.get());
				}
			});
			showEdgeHandlesButton.setToolTipText(StringBundle.get("show_edge_handles_button_tooltip"));
			this.add(showEdgeHandlesButton);
			
			showEdgeLabelsButton = new JButton(ImageIconBundle.get("show_edge_labels_icon"));
			showEdgeLabelsButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					settings.showEdgeLabels.set(!settings.showEdgeLabels.get());
				}
			});
			showEdgeLabelsButton.setToolTipText(StringBundle.get("show_edge_labels_button_tooltip"));
			this.add(showEdgeLabelsButton);
			
			showEdgeWeightsButton = new JButton(ImageIconBundle.get("show_edge_weights_icon"));
			showEdgeWeightsButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					settings.showEdgeWeights.set(!settings.showEdgeWeights.get());
				}
			});
			showEdgeWeightsButton.setToolTipText(StringBundle.get("show_edge_weights_button_tooltip"));
			this.add(showEdgeWeightsButton);
			
			this.refresh();
		}
		
		public void refresh()
		{
			if (showVertexLabelsButton != null && showVertexLabelsButton.isSelected() != settings.showVertexLabels.get())
				showVertexLabelsButton.setSelected(settings.showVertexLabels.get());
			
			if (showVertexWeightsButton != null && showVertexWeightsButton.isSelected() != settings.showVertexWeights.get())
				showVertexWeightsButton.setSelected(settings.showVertexWeights.get());
			
			if (showEdgeHandlesButton != null && showEdgeHandlesButton.isSelected() != settings.showEdgeHandles.get())
				showEdgeHandlesButton.setSelected(settings.showEdgeHandles.get());
			
			if (showEdgeLabelsButton != null && showEdgeLabelsButton.isSelected() != settings.showEdgeLabels.get())
				showEdgeLabelsButton.setSelected(settings.showEdgeLabels.get());
			
			if (showEdgeWeightsButton != null && showEdgeWeightsButton.isSelected() != settings.showEdgeWeights.get())
				showEdgeWeightsButton.setSelected(settings.showEdgeWeights.get());
		}
	}
	
	private class ZoomToolBar extends JToolBar
	{
		private JButton zoomGraphButton;
		private JButton zoomOneToOneButton;
		private JButton zoomInButton;
		private JButton zoomOutButton;
		
		public ZoomToolBar()
		{
			zoomGraphButton = new JButton(ImageIconBundle.get("zoom_graph_icon"));
			zoomGraphButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					zoomFit();
				}
			});
			zoomGraphButton.setToolTipText(StringBundle.get("zoom_graph_button_tooltip"));
			this.add(zoomGraphButton);
			
			zoomOneToOneButton = new JButton(ImageIconBundle.get("zoom_one_to_one_icon"));
			zoomOneToOneButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					zoomOneToOne();
				}
			});
			zoomOneToOneButton.setToolTipText(StringBundle.get("zoom_one_to_one_button_tooltip"));
			this.add(zoomOneToOneButton);
			
			zoomInButton = new JButton(ImageIconBundle.get("zoom_in_icon"));
			zoomInButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Point2D.Double viewportCenter = new Point2D.Double(viewport.getWidth() / 2.0, viewport.getHeight() / 2.0);
					Point2D.Double zoomCenter = new Point2D.Double();
					try { transform.inverseTransform(viewportCenter, zoomCenter); }
					catch (NoninvertibleTransformException ex) { DebugUtilities.logException("An exception occurred while inverting transformation.", ex); }
					
					zoomCenter(zoomCenter, userSettings.zoomInFactor.get());
				}
			});
			zoomInButton.setToolTipText(StringBundle.get("zoom_in_button_tooltip"));
			this.add(zoomInButton);
			
			zoomOutButton = new JButton(ImageIconBundle.get("zoom_out_icon"));
			zoomOutButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Point2D.Double viewportCenter = new Point2D.Double(viewport.getWidth() / 2.0, viewport.getHeight() / 2.0);
					Point2D.Double zoomCenter = new Point2D.Double();
					try { transform.inverseTransform(viewportCenter, zoomCenter); }
					catch (NoninvertibleTransformException ex) { DebugUtilities.logException("An exception occurred while inverting transformation.", ex); }
					
					zoomCenter(zoomCenter, userSettings.zoomOutFactor.get());
				}
			});
			zoomOutButton.setToolTipText(StringBundle.get("zoom_out_button_tooltip"));
			this.add(zoomOutButton);
		}
	}
	
	private class FunctionToolBar extends JToolBar
	{
		private JButton								  oneTimeFunctionsButton;
		private JPopupMenu							  oneTimeFunctionsMenu;
		private Map<JMenuItem, FunctionBase>		  oneTimeFunctionMenuItems;
		
		private JButton								  dynamicFunctionsButton;
		private JPopupMenu							  dynamicFunctionsMenu;
		private Map<JCheckBoxMenuItem, FunctionBase>  dynamicFunctionMenuItems;
		
		public FunctionToolBar()
		{
			oneTimeFunctionsButton = new JButton(ImageIconBundle.get("one_time_functions_icon"));
			oneTimeFunctionsButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					oneTimeFunctionsMenu.show(oneTimeFunctionsButton, 0, oneTimeFunctionsButton.getHeight());
				}
			});
			oneTimeFunctionsButton.setToolTipText(StringBundle.get("one_time_functions_button_tooltip"));
			this.add(oneTimeFunctionsButton);
			
			dynamicFunctionsButton = new JButton(ImageIconBundle.get("dynamic_functions_icon"));
			dynamicFunctionsButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					dynamicFunctionsMenu.show(dynamicFunctionsButton, 0, dynamicFunctionsButton.getHeight());
				}
			});
			dynamicFunctionsButton.setToolTipText(StringBundle.get("dynamic_functions_button_tooltip"));
			this.add(dynamicFunctionsButton);
			
			oneTimeFunctionsMenu = new JPopupMenu();
			dynamicFunctionsMenu = new JPopupMenu();
			
			oneTimeFunctionMenuItems = new HashMap<JMenuItem, FunctionBase>();
			dynamicFunctionMenuItems = new HashMap<JCheckBoxMenuItem, FunctionBase>();
			
			refresh();
		}
		
		public void refresh()
		{
			if (oneTimeFunctionsMenu != null && dynamicFunctionsMenu != null)
			{
				oneTimeFunctionsMenu.removeAll();
				dynamicFunctionsMenu.removeAll();
				
				oneTimeFunctionMenuItems = new HashMap<JMenuItem, FunctionBase>();
				dynamicFunctionMenuItems = new HashMap<JCheckBoxMenuItem, FunctionBase>();
				ActionListener oneTimeFunctionMenuItemActionListener = new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0)
					{
						JMenuItem oneTimeFunctionMenuItem = (JMenuItem) arg0.getSource();
						functionsToBeRun.add(oneTimeFunctionMenuItems.get(oneTimeFunctionMenuItem));
						isViewportInvalidated = true;
					}
				};
				ActionListener dynamicFunctionMenuItemActionListener = new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0)
					{
						JCheckBoxMenuItem dynamicFunctionMenuItem = (JCheckBoxMenuItem) arg0.getSource();
						
						if (dynamicFunctionMenuItem.isSelected())
						{
							JLabel functionLabel = new JLabel();
							JToolBar functionToolBar = new JToolBar();
							functionToolBar.add(functionLabel);
							statusBar.add(functionToolBar);
							selectedFunctionLabels.put(dynamicFunctionMenuItems.get(dynamicFunctionMenuItem), functionLabel);
						}
						else
						{
							statusBar.remove(selectedFunctionLabels.get(dynamicFunctionMenuItems.get(dynamicFunctionMenuItem)).getParent());
							statusBar.validate();
							selectedFunctionLabels.remove(dynamicFunctionMenuItems.get(dynamicFunctionMenuItem));
						}
						
						isViewportInvalidated = true;
					}
				};
				
				for (FunctionBase function : FunctionService.instance.functions)
				{
					if(function.allowsDynamicEvaluation())
					{
						JCheckBoxMenuItem dynamicFunctionMenuItem = new JCheckBoxMenuItem(function.toString());
						dynamicFunctionMenuItem.addActionListener(dynamicFunctionMenuItemActionListener);
						dynamicFunctionsMenu.add(dynamicFunctionMenuItem);
						dynamicFunctionMenuItems.put(dynamicFunctionMenuItem, function);
					}
					
					if(function.allowsOneTimeEvaluation())
					{
						JMenuItem oneTimeFunctionMenuItem = new JMenuItem(function.toString());
						oneTimeFunctionMenuItem.addActionListener(oneTimeFunctionMenuItemActionListener);
						oneTimeFunctionsMenu.add(oneTimeFunctionMenuItem);
						oneTimeFunctionMenuItems.put(oneTimeFunctionMenuItem, function);
					}
				}
			}
		}
	}
	
	private class ViewportPopupMenu extends JPopupMenu
	{
		private JMenuItem  vertexItem;
		private JMenuItem   vertexLabelItem;
		private JMenuItem   vertexRadiusItem;
		private JMenuItem   vertexColorItem;
		private JMenuItem   vertexWeightItem;
		private JMenuItem  edgeItem;
		private JMenuItem   edgeLabelItem;
		private JMenuItem   edgeThicknessItem;
		private JMenuItem   edgeColorItem;
		private JMenuItem   edgeWeightItem;
		
		public ViewportPopupMenu()
		{
			vertexItem = new JMenu(StringBundle.get("properties_vertex_menu_text"));
			this.add(vertexItem);
			
			vertexLabelItem = new JMenuItem(StringBundle.get("properties_vertex_label_menu_text"));
			vertexLabelItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String value = JOptionPane.showInputDialog(viewport, StringBundle.get("new_vertex_label_dialog_text"), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE);
					if (value != null)
						for (Vertex vertex : graph.vertexes)
							if (vertex.isSelected.get())
								vertex.label.set(value);
				}
			});
			vertexItem.add(vertexLabelItem);
			
			vertexRadiusItem = new JMenuItem(StringBundle.get("properties_vertex_radius_menu_text"));
			vertexRadiusItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String value = JOptionPane.showInputDialog(viewport, StringBundle.get("new_vertex_radius_dialog_text"), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE);
					if (value != null)
					{
						double radius = Double.parseDouble(value);
						
						for (Vertex vertex : graph.vertexes)
							if (vertex.isSelected.get())
								vertex.radius.set(radius);
					}
				}
			});
			vertexItem.add(vertexRadiusItem);
			
			vertexColorItem = new JMenuItem(StringBundle.get("properties_vertex_color_menu_text"));
			vertexColorItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String value = JOptionPane.showInputDialog(viewport, StringBundle.get("new_vertex_color_dialog_text"), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE);
					if (value != null)
					{
						int color = Integer.parseInt(value);
						
						for (Vertex vertex : graph.vertexes)
							if (vertex.isSelected.get())
								vertex.color.set(color);
					}
				}
			});
			vertexItem.add(vertexColorItem);
			
			vertexWeightItem = new JMenuItem(StringBundle.get("properties_vertex_weight_menu_text"));
			vertexWeightItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String value = JOptionPane.showInputDialog(viewport, StringBundle.get("new_vertex_weight_dialog_text"), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE);
					if (value != null)
					{
						double weight = Double.parseDouble(value);
						
						for (Vertex vertex : graph.vertexes)
							if (vertex.isSelected.get())
								vertex.weight.set(weight);
					}
				}
			});
			vertexItem.add(vertexWeightItem);
			
			edgeItem = new JMenu(StringBundle.get("properties_edge_menu_text"));
			this.add(edgeItem);
			
			edgeLabelItem = new JMenuItem(StringBundle.get("properties_edge_label_menu_text"));
			edgeLabelItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String label = JOptionPane.showInputDialog(viewport, StringBundle.get("new_edge_label_dialog_text"), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE);
					if (label != null)
						for (Edge edge : graph.edges)
							if (edge.isSelected.get())
								edge.label.set(label);
				}
			});
			edgeItem.add(edgeLabelItem);
			
			edgeThicknessItem = new JMenuItem(StringBundle.get("properties_edge_thickness_menu_text"));
			edgeThicknessItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String value = JOptionPane.showInputDialog(viewport, StringBundle.get("new_edge_thickness_dialog_text"), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE);
					if (value != null)
					{
						double thickness = Double.parseDouble(value);
						
						for (Edge edge : graph.edges)
							if (edge.isSelected.get())
								edge.thickness.set(thickness);
					}
				}
			});
			edgeItem.add(edgeThicknessItem);
			
			edgeColorItem = new JMenuItem(StringBundle.get("properties_edge_color_menu_text"));
			edgeColorItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String value = JOptionPane.showInputDialog(viewport, StringBundle.get("new_edge_color_dialog_text"), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE);
					if (value != null)
					{
						int color = Integer.parseInt(value);
						
						for (Edge edge : graph.edges)
							if (edge.isSelected.get())
								edge.color.set(color);
					}
				}
			});
			edgeItem.add(edgeColorItem);
			
			edgeWeightItem = new JMenuItem(StringBundle.get("properties_edge_weight_menu_text"));
			edgeWeightItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String value = JOptionPane.showInputDialog(viewport, StringBundle.get("new_edge_weight_dialog_text"), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE);
					if (value != null)
					{
						double weight = Double.parseDouble(value);
						
						for (Edge edge : graph.edges)
							if (edge.isSelected.get())
								edge.weight.set(weight);
					}
				}
			});
			edgeItem.add(edgeWeightItem);
		}
	
		public void setVertexMenuEnabled(boolean enable)
		{
			vertexItem.setEnabled(enable);
		}
		
		public void setEdgeMenuEnabled(boolean enable)
		{
			edgeItem.setEnabled(enable);
		}
	}

	private class ViewportPrinter implements Printable
	{
		public void print() throws PrinterException
		{
			PrinterJob printJob = PrinterJob.getPrinterJob();
			printJob.setPrintable(this);
			
			if (printJob.printDialog())
				printJob.print();
		}
		
		public int print(Graphics g, PageFormat pageFormat, int pageIndex)
		{
			if (pageIndex > 0)
				return (NO_SUCH_PAGE);
			else
			{
				Graphics2D g2d = (Graphics2D) g;
				
				double widthRatio  = (viewport.getWidth()  + userSettings.zoomGraphPadding.get()) / pageFormat.getImageableWidth();
				double heightRatio = (viewport.getHeight() + userSettings.zoomGraphPadding.get()) / pageFormat.getImageableHeight();
				double maxRatio = 1.0 / Math.max(widthRatio, heightRatio);
				
				g2d.scale(maxRatio, maxRatio);
				g2d.translate(pageFormat.getImageableX() + userSettings.zoomGraphPadding.get() / 4.0, 
						      pageFormat.getImageableY() + userSettings.zoomGraphPadding.get() / 4.0);
				
				RepaintManager.currentManager(viewport).setDoubleBufferingEnabled(false);
				viewport.paint(g2d);
				RepaintManager.currentManager(viewport).setDoubleBufferingEnabled(true);
				
				return (PAGE_EXISTS);
			}
		}
	}
	
	private class SnapshotList
	{
		// SnapshotList is essentially a kind of doubly-linked circular list of strings with a maximum capacity
		private Snapshot current;
		private Snapshot newest;
		private Snapshot oldest;
		private int capacity;
		private int size;
		
		public SnapshotList(String snapshot)
		{
			newest = oldest = current = new Snapshot(snapshot);
			capacity = userSettings.undoLoggingMaximum.get();
			size = 0;
		}
		
		public void add(String snapshot)
		{
			if(!snapshot.equals(current.value))
			{
				if(size < capacity)
				{
					Snapshot newSnapshot = new Snapshot(snapshot, current, current.next);
					current.next.previous = newSnapshot;
					current = newest = current.next = newSnapshot;
					++size;
				}
				else
				{
					current.next.value = snapshot;
					current = newest = current.next;
					if(current == oldest)
						oldest = current.next;
				}
			}
		}
		
		public String previous()
		{
			if(current == oldest)
				return null;
			
			current = current.previous;
			
			return current.value;
		}
		
		public String current()
		{
			return current.value;
		}
		
		public String next()
		{
			if(current == newest)
				return null;
			
			current = current.next;
			
			return current.value;
		}
		
		private class Snapshot
		{
			public String value;
			public Snapshot previous;
			public Snapshot next;
			
			public Snapshot(String value)
			{
				this.value = value;
				this.previous = this;
				this.next = this;
			}
			
			public Snapshot(String value, Snapshot previous, Snapshot next)
			{
				this.value = value;
				this.previous = previous;
				this.next = next;
			}
		}
	}
	
	public class GraphChangeEvent extends EventObject
	{
		public GraphChangeEvent(Object source)
		{
			super(source);
		}
	}
	
	public interface GraphChangeEventListener extends EventListener
	{
		public void graphChangeEventOccurred(GraphChangeEvent evt);
	}
}







