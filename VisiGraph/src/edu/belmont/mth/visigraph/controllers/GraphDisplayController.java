/**
 * GraphDisplayController.java
 */
package edu.belmont.mth.visigraph.controllers;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.Map.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import edu.belmont.mth.visigraph.gui.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.models.functions.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.views.*;

/**
 * @author Cameron Behar
 * 
 */
@SuppressWarnings("serial")
public class GraphDisplayController extends JPanel
{
	protected Graph				   graph;
	protected GraphDisplaySettings 	settings;
	protected Palette			   	palette;
	
	protected JPanel			   toolToolBarPanel;
	protected ToolToolBar		    toolToolBar;
	protected JPanel			   nonToolToolbarPanel;
	protected ArrangeToolBar	    arrangeToolBar;
	protected ViewToolBar		    viewToolBar;
	protected ZoomToolBar		    zoomToolBar;
	protected FunctionToolBar	    functionToolBar;								 
	protected JPanel			   viewportPanel;
	protected JComponent		    viewport;
	protected ViewportPopupMenu	    viewportPopupMenu;
	protected JPanel			   statusBar;
	protected Map<FunctionBase, JLabel>	selectedFunctionLabels;
	
	protected Tool					tool;
	protected int					paintColor;
	protected boolean				isMouseDownOnCanvas;
	protected boolean				isMouseDownOnPaintToolButton;
	protected boolean				pointerToolClickedObject;
	protected boolean				cutToolClickedObject;
	protected boolean				paintToolClickedObject;
	protected Point					currentMousePoint;
	protected Point					pastMousePoint;
	protected Vertex				fromVertex;
	protected AffineTransform		transform;
	protected Set<FunctionBase>	    functionsToBeRun;
	protected ResourceBundle		imageIcons;
	
	public GraphDisplayController(Graph graph)
	{
		// Add/bind graph
		this.graph = graph;
		graph.addObserver(new ObserverBase()
		{
			@Override
			public void hasChanged(Object source)
			{
				hasGraphChanged(source);
			}
		});
		
		// Add/bind palette
		palette = new Palette();
		palette.addObserver(new ObserverBase()
		{
			@Override
			public void hasChanged(Object source)
			{
				hasSettingChanged(source);
			}
		});
		
		// Add/bind display settings
		settings = new GraphDisplaySettings();
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
	}
	
	public Graph getGraph()
	{
		return graph;
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
		repaint();
	}
	
	public void hasSettingChanged(Object source)
	{
		repaint();

		if(toolToolBar != null)
			toolToolBar.refreshPaintMenu();
		
		if(viewToolBar != null)
			viewToolBar.refresh();
	}
	
	public void initializeComponents()
	{
		setLayout(new BorderLayout());
		setBackground(GlobalSettings.defaultGraphBackgroundDisplayColor);
		setOpaque(true);
		
		imageIcons = ResourceBundle.getBundle("edu.belmont.mth.visigraph.resources.ImageIconBundle");
		
		toolToolBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		toolToolBarPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
		add(toolToolBarPanel, BorderLayout.WEST);
		
		toolToolBar = new ToolToolBar();
		toolToolBarPanel.add(toolToolBar);
		
		nonToolToolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
				catch (NoninvertibleTransformException e) { }
			}
			
			@Override
			public void mouseReleased(MouseEvent event)
			{
				try { viewportMouseReleased(event); }
				catch (NoninvertibleTransformException e) { }
			}
		});
		viewport.addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseDragged(MouseEvent event)
			{
				try { viewportMouseDragged(event); }
				catch (NoninvertibleTransformException e) { }
			}
			
			@Override
			public void mouseMoved(MouseEvent event)
			{
				try { transform.inverseTransform(event.getPoint(), currentMousePoint); }
				catch (NoninvertibleTransformException e) { }
			}
		});
		viewport.addMouseWheelListener(new MouseWheelListener()
		{
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				zoomCenter(new Point2D.Double(currentMousePoint.x, currentMousePoint.y), 1 - e.getWheelRotation() * GlobalSettings.scrollIncrementZoom);
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
		isMouseDownOnCanvas = false;
		currentMousePoint = new Point(0, 0);
		pastMousePoint = new Point(0, 0);
		viewportPanel.add(viewport, BorderLayout.CENTER);
		viewportPopupMenu = new ViewportPopupMenu();
		
		statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		statusBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));
		add(statusBar, BorderLayout.SOUTH);
	}
	
	public void paintSelectionRectangle(Graphics2D g2D)
	{
		Rectangle selection = getSelectionRectangle();
		
		g2D.setColor(palette.graphSelectionBoxFill.get());
		g2D.fillRect(selection.x, selection.y, selection.width, selection.height);
		
		g2D.setColor(palette.graphSelectionBoxLine.get());
		g2D.drawRect(selection.x, selection.y, selection.width, selection.height);
	}
	
	public void paintViewport(Graphics2D g2D)
	{
		if (GlobalSettings.drawAntiAliased)
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if (GlobalSettings.drawStrokePure)
			g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		
		if (GlobalSettings.drawBicubicInterpolation)
			g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		// Apply any one-time functions
		Set<FunctionBase> run = new TreeSet<FunctionBase>();
		run.addAll(functionsToBeRun);
		functionsToBeRun.clear();
		
		for (FunctionBase function : run)
			JOptionPane.showMessageDialog(viewport, function.getDescription() +": " + function.evaluate(g2D, palette, graph), GlobalSettings.applicationName, JOptionPane.OK_OPTION + JOptionPane.INFORMATION_MESSAGE);
		
		// Clear everything
		super.paintComponent(g2D);
		
		// Apply the transformation
		AffineTransform original = g2D.getTransform();
		original.concatenate(transform);
		g2D.setTransform(original);
		
		// Paint the graph
		GraphDisplayView.paint(g2D, graph, palette, settings);
		
		// Apply any selected functions
		for (Entry<FunctionBase, JLabel> entry : selectedFunctionLabels.entrySet())
			entry.getValue().setText(entry.getKey().getDescription() + ": " + entry.getKey().evaluate(g2D, palette, graph));
		
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
							g2D.setColor(palette.draggingEdgeLine.get());
							g2D.drawLine(fromVertex.x.get().intValue(), fromVertex.y.get().intValue(), currentMousePoint.x, currentMousePoint.y);
						}
						
						break;
					}
				case PAINT_TOOL: if (!paintToolClickedObject) paintSelectionRectangle(g2D); break;
			}
	}	
	
	public void setTool(Tool tool)
	{
		this.tool = tool;
		if(toolToolBar != null)
			toolToolBar.refresh();
		
		Cursor cursor;
		ResourceBundle cursors = ResourceBundle.getBundle("edu.belmont.mth.visigraph.resources.CursorBundle");
		
		switch (this.tool)
		{
			case POINTER_TOOL: cursor = (Cursor) cursors.getObject("pointer_tool_cursor"); break;
			case VERTEX_TOOL:  cursor = (Cursor) cursors.getObject("vertex_tool_cursor");  break;
			case EDGE_TOOL:    cursor = (Cursor) cursors.getObject("edge_tool_cursor");    break;
			case CUT_TOOL:	   cursor = (Cursor) cursors.getObject("cut_tool_cursor");	   break;
			case CAPTION_TOOL: cursor = (Cursor) cursors.getObject("caption_tool_cursor"); break;
			case PAINT_TOOL:   cursor = (Cursor) cursors.getObject("paint_tool_cursor");   break;
			default:           cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR); break;
		}
		
		setCursor(cursor);
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
					graph.moveSelected(0, -GlobalSettings.arrowKeyIncrement);
					for (Edge edge : graph.edges)
						if (edge.isSelected.get())
							edge.fixHandle();
					
					break;
				}
			case KeyEvent.VK_RIGHT:
				{
					graph.moveSelected(GlobalSettings.arrowKeyIncrement, 0);
					for (Edge edge : graph.edges)
						if (edge.isSelected.get())
							edge.fixHandle();
					
					break;
				}
			case KeyEvent.VK_DOWN:
				{
					graph.moveSelected(0, GlobalSettings.arrowKeyIncrement);
					for (Edge edge : graph.edges)
						if (edge.isSelected.get())
							edge.fixHandle();
					
					break;
				}
			case KeyEvent.VK_LEFT:
				{
					graph.moveSelected(-GlobalSettings.arrowKeyIncrement, 0);
					for (Edge edge : graph.edges)
						if (edge.isSelected.get())
							edge.fixHandle();
					
					break;
				}
			case KeyEvent.VK_A:
				{
					if(event.isControlDown())
						graph.selectAll();
					
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
		
		repaint();
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
								
								String newText = EditCaptionDialog.showDialog(this, this, caption.text.get());
								if (newText != null)
									caption.text.set(newText);
								
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
												graph.edges.add(new Edge(graph.nextEdgeId(), graph.areDirectedEdgesAllowed, fromVertex, vertex));
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
						
						String newText = EditCaptionDialog.showDialog(this, this, GlobalSettings.defaultCaptionText);
						
						if (newText == null)
							graph.captions.remove(caption);
						else
							caption.text.set(newText);
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
		repaint();
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
						viewportPopupMenu.show(viewport, event.getPoint().x, event.getPoint().y);
					
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
										graph.edges.add(new Edge(graph.nextEdgeId(), graph.areDirectedEdgesAllowed, fromVertex, vertex));
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
		repaint();
	}
	
	public void zoomCenter(Point2D.Double center, double factor)
	{
		transform.translate(center.x, center.y);
		
		transform.scale(factor, factor);
		if (transform.getScaleX() > GlobalSettings.maximumZoomFactor)
			zoomMax();
		
		transform.translate(-center.x, -center.y);
		
		viewport.repaint();
	}
	
	public void zoomFit(Rectangle2D rectangle)
	{
		// First we need to reset and translate the graph to the viewport's center
		transform.setToIdentity();
		transform.translate(viewport.getWidth() / 2.0, viewport.getHeight() / 2.0);
		
		// We need to fit it to the viewport. So we want to scale according to the lowest viewport-to-graph dimension ratio.
		double widthRatio = (viewport.getWidth() - GlobalSettings.zoomGraphPadding) / rectangle.getWidth();
		double heightRatio = (viewport.getHeight() - GlobalSettings.zoomGraphPadding) / rectangle.getHeight();
		double minRatio = Math.min(widthRatio, heightRatio);
		
		if (minRatio < 1)
			transform.scale(minRatio, minRatio);
		
		// Only now that we've properly scaled can we translate to the graph's midpoint
		Point2D.Double graphCenter = new Point2D.Double(rectangle.getCenterX(), rectangle.getCenterY());
		transform.translate(-graphCenter.x, -graphCenter.y);
		
		// And of course, we want to refresh the viewport
		viewport.repaint();
	}
	
	public void zoomFit()
	{
		if (graph.vertexes.size() > 0)
			zoomFit(GraphDisplayView.getBounds(graph));
	}
	
	public void zoomOneToOne()
	{
		transform.setTransform(1, transform.getShearY(), transform.getShearX(), 1, (int)transform.getTranslateX(), (int)transform.getTranslateY());
		viewport.repaint();
	}
	
	public void zoomMax()
	{
		transform.setTransform(GlobalSettings.maximumZoomFactor, transform.getShearY(), transform.getShearX(), GlobalSettings.maximumZoomFactor, transform.getTranslateX(), transform.getTranslateY());
		viewport.repaint();
	}
	
	public enum Tool
	{
		POINTER_TOOL, VERTEX_TOOL, EDGE_TOOL, CAPTION_TOOL, CUT_TOOL, PAINT_TOOL
	}
	
	protected class ToolToolBar extends JToolBar
	{
		protected JButton pointerToolButton;
		protected JButton vertexToolButton;
		protected JButton edgeToolButton;
		protected JButton captionToolButton;
		protected JButton cutToolButton;
		protected JButton paintToolButton;
		protected JPopupMenu paintMenu;
		protected Timer	paintMenuTimer;
		
		public ToolToolBar()
		{
			this.setOrientation(SwingConstants.VERTICAL);
			this.setFloatable(false);
			
			pointerToolButton = new JButton((ImageIcon) imageIcons.getObject("pointer_tool_icon"));
			pointerToolButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					setTool(Tool.POINTER_TOOL);
					graph.deselectAll();
				}
			});
			pointerToolButton.setSelected(true);
			this.add(pointerToolButton);
			
			vertexToolButton = new JButton((ImageIcon) imageIcons.getObject("vertex_tool_icon"));
			vertexToolButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					setTool(Tool.VERTEX_TOOL);
					graph.deselectAll();
				}
			});
			this.add(vertexToolButton);
			
			edgeToolButton = new JButton((ImageIcon) imageIcons.getObject("edge_tool_icon"));
			edgeToolButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					setTool(Tool.EDGE_TOOL);
					graph.deselectAll();
				}
			});
			this.add(edgeToolButton);
			
			captionToolButton = new JButton((ImageIcon) imageIcons.getObject("caption_tool_icon"));
			captionToolButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					setTool(Tool.CAPTION_TOOL);
					graph.deselectAll();
				}
			});
			this.add(captionToolButton);
			
			cutToolButton = new JButton((ImageIcon) imageIcons.getObject("cut_tool_icon"));
			cutToolButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					setTool(Tool.CUT_TOOL);
					graph.deselectAll();
				}
			});
			this.add(cutToolButton);
			
			paintToolButton = new JButton((ImageIcon) imageIcons.getObject("paint_tool_icon"));
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
			paintMenuTimer = new Timer(GlobalSettings.paintToolButtonDelay, new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					paintMenuTimer.stop();
					
					if (isMouseDownOnPaintToolButton)
						paintMenu.show(paintToolButton, 0, paintToolButton.getHeight());
				}
			});
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
				public void actionPerformed(ActionEvent arg0)
				{
					try
					{
						paintColor = Integer.parseInt(((JMenuItem) arg0.getSource()).getText());				
					}
					catch (NumberFormatException e)
					{
						paintColor = -1;
					}
					
					for (Component paintMenuItem : paintMenu.getComponents())
						if (paintMenuItem instanceof JMenuItem)
							((JCheckBoxMenuItem) paintMenuItem).setState(false);
					
					if (paintColor + 1 < paintMenu.getComponentCount() && paintMenu.getComponent(paintColor + 1) instanceof JMenuItem)
						((JCheckBoxMenuItem) paintMenu.getComponent(paintColor + 1)).setState(true);
				}
			};
			
			JCheckBoxMenuItem emptyBrushMenuItem = new JCheckBoxMenuItem("<none>");
			emptyBrushMenuItem.addActionListener(paintMenuItemActionListener);
			emptyBrushMenuItem.setSelected(true);
			paintMenu.add(emptyBrushMenuItem);
			
			for (int i = 0; i < palette.getElementColorCount(); ++i)
			{
				JCheckBoxMenuItem brushMenuItem = new JCheckBoxMenuItem(i + "");
				brushMenuItem.addActionListener(paintMenuItemActionListener);
				paintMenu.add(brushMenuItem);
			}
		}
	}
	
	protected class ArrangeToolBar extends JToolBar
	{
		protected JButton arrangeCircleButton;
		protected JButton arrangeGridButton;
		protected JButton arrangeTreeButton;
		protected JButton arrangeWebButton;
		protected JButton alignVerticallyButton;
		protected JButton alignHorizontallyButton;
		protected JButton distributeHorizontallyButton;
		protected JButton distributeVerticallyButton;
		protected JButton rotateLeft90Button;
		protected JButton rotateRight90Button;
		protected JButton flipHorizontallyButton;
		protected JButton flipVerticallyButton;	
		
		public ArrangeToolBar()
		{
			arrangeCircleButton = new JButton((ImageIcon) imageIcons.getObject("arrange_circle_icon"));
			this.add(arrangeCircleButton);
			arrangeCircleButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					double radius = GlobalSettings.arrangeCircleRadiusMultiplier * graph.vertexes.size();
					double degreesPerVertex = 2 * Math.PI / graph.vertexes.size();
					
					for (int i = 0; i < graph.vertexes.size(); ++i)
					{
						graph.vertexes.get(i).x.set(radius * Math.cos(degreesPerVertex * i - Math.PI / 2.0));
						graph.vertexes.get(i).y.set(radius * Math.sin(degreesPerVertex * i - Math.PI / 2.0));
					}
					
					zoomFit();
				}
			});
			
			arrangeGridButton = new JButton((ImageIcon) imageIcons.getObject("arrange_grid_icon"));
			this.add(arrangeGridButton);
			arrangeGridButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int n = graph.vertexes.size();
					int rows = (int) Math.round(Math.sqrt(n));
					int columns = (int) Math.ceil(n / (double) rows);
					Point2D.Double location = new Point2D.Double((columns / 2.0) * -GlobalSettings.arrangeGridSpacing, (rows / 2.0) * -GlobalSettings.arrangeGridSpacing);
					
					for (int row = 0; row < rows; ++row)
						for(int col = 0; (row < rows - 1 && col < columns) || (row == rows - 1 && col < (n % columns == 0 ? columns : n % columns)); ++col)
						{
							graph.vertexes.get(row * columns + col).x.set(location.x + GlobalSettings.arrangeGridSpacing * col);
							graph.vertexes.get(row * columns + col).y.set(location.y + GlobalSettings.arrangeGridSpacing * row);
						}
					
					zoomFit();
				}
			});
			
			arrangeTreeButton = new JButton((ImageIcon) imageIcons.getObject("arrange_tree_icon"));
			this.add(arrangeTreeButton);
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
			
			arrangeWebButton = new JButton((ImageIcon) imageIcons.getObject("arrange_web_icon"));
			this.add(arrangeWebButton);
			arrangeWebButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					new Timer(50, new ActionListener() {
						public void actionPerformed(ActionEvent arg0)
						{
							Timer timer = (Timer)arg0.getSource(); 
							if(timer.getDelay() >= 500)
							{
								timer.stop(); return; 
							}
							
							timer.setDelay((int)(timer.getDelay() * GlobalSettings.applyForcesDecelerationFactor));
							
							HashMap<Vertex, Point2D.Double> forces = new HashMap<Vertex, Point2D.Double>();
						
							// Initialize the hashmap of forces
							for (int i = 0; i < graph.vertexes.size(); ++i)
								forces.put(graph.vertexes.get(i), new Point2D.Double(0, 0));
							
							// Calculate all repulsive forces
							for (int i = 0; i < graph.vertexes.size(); ++i)
								for (int j = i + 1; j < graph.vertexes.size(); ++j)
								{
									Vertex v0 = graph.vertexes.get(i);
									Vertex v1 = graph.vertexes.get(j);
									
									double xDiff = v1.x.get() - v0.x.get();
									double yDiff = v1.y.get() - v0.y.get();
									double distanceSquared = (xDiff * xDiff + yDiff * yDiff);
									double xForce = GlobalSettings.repulsiveForce * (xDiff / distanceSquared);
									double yForce = GlobalSettings.repulsiveForce * (yDiff / distanceSquared);
									
									forces.get(v0).x += xForce;
									forces.get(v0).y += yForce;
									
									// And because every action has an opposite and equal reaction
									forces.get(v1).x -= xForce;
									forces.get(v1).y -= yForce;
								}
							
							// Calculate all attractive forces
							for (Edge edge : graph.edges)
								if (edge.from != edge.to)
								{
									double xDiff = edge.to.x.get() - edge.from.x.get();
									double yDiff = edge.to.y.get() - edge.from.y.get();
									double distanceSquared = (xDiff * xDiff + yDiff * yDiff);
									double xForce = GlobalSettings.attractiveForce * xDiff * distanceSquared;
									double yForce = GlobalSettings.attractiveForce * yDiff * distanceSquared;
									
									forces.get(edge.from).x += xForce;
									forces.get(edge.from).y += yForce;
									
									// And because every action has an opposite and equal reaction
									forces.get(edge.to).x -= xForce;
									forces.get(edge.to).y -= yForce;
								}
							
							// Apply all net forces
							for (Vertex v : graph.vertexes)
							{
								v.x.set(v.x.get() + forces.get(v).x);
								v.y.set(v.y.get() + forces.get(v).y);
							}
						} 
					} ).start();	
				}
			});
			
			this.add(new JToolBar.Separator());
			
			alignHorizontallyButton = new JButton((ImageIcon) imageIcons.getObject("align_horizontally_icon"));
			this.add(alignHorizontallyButton);
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
			
			alignVerticallyButton = new JButton((ImageIcon) imageIcons.getObject("align_vertically_icon"));
			this.add(alignVerticallyButton);
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
			
			distributeHorizontallyButton = new JButton((ImageIcon) imageIcons.getObject("distribute_horizontally_icon"));
			this.add(distributeHorizontallyButton);
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
			
			distributeVerticallyButton = new JButton((ImageIcon) imageIcons.getObject("distribute_vertically_icon"));
			this.add(distributeVerticallyButton);
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
			
			this.add(new JToolBar.Separator());
			
			rotateLeft90Button = new JButton((ImageIcon) imageIcons.getObject("rotate_left_90_icon"));
			this.add(rotateLeft90Button);
			rotateLeft90Button.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int selectedVertexCount = 0;
					Point2D.Double centroid = new Point2D.Double();
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
						{
							centroid.x += vertex.x.get();
							centroid.y += vertex.y.get();
							++selectedVertexCount;
						}
					
					centroid.x /= (double)selectedVertexCount;
					centroid.y /= (double)selectedVertexCount;
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
						{
							double oldVertexX = vertex.x.get();
							vertex.x.set(centroid.x - (centroid.y - vertex.y.get()));
							vertex.y.set(centroid.y + (centroid.x - oldVertexX));
						}
				}
			});
			
			rotateRight90Button = new JButton((ImageIcon) imageIcons.getObject("rotate_right_90_icon"));
			this.add(rotateRight90Button);
			rotateRight90Button.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int selectedVertexCount = 0;
					Point2D.Double centroid = new Point2D.Double();
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
						{
							centroid.x += vertex.x.get();
							centroid.y += vertex.y.get();
							++selectedVertexCount;
						}
					
					centroid.x /= (double)selectedVertexCount;
					centroid.y /= (double)selectedVertexCount;
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
						{
							double oldVertexX = vertex.x.get();
							vertex.x.set(centroid.x + (centroid.y - vertex.y.get()));
							vertex.y.set(centroid.y - (centroid.x - oldVertexX));
						}
				}
			});
			
			flipHorizontallyButton = new JButton((ImageIcon) imageIcons.getObject("flip_horizontally_icon"));
			this.add(flipHorizontallyButton);
			flipHorizontallyButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int selectedVertexCount = 0;
					double centerX = 0.0;
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
						{
							centerX += vertex.x.get();
							++selectedVertexCount;
						}
					
					centerX /= (double)selectedVertexCount;
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
							vertex.x.set(2.0 * centerX - vertex.x.get());
				}
			});
			
			flipVerticallyButton = new JButton((ImageIcon) imageIcons.getObject("flip_vertically_icon"));
			this.add(flipVerticallyButton);
			flipVerticallyButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int selectedVertexCount = 0;
					double centerY = 0.0;
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
						{
							centerY += vertex.y.get();
							++selectedVertexCount;
						}
					
					centerY /= (double)selectedVertexCount;
					
					for(Vertex vertex : graph.vertexes)
						if(vertex.isSelected.get())
							vertex.y.set(2.0 * centerY - vertex.y.get());
				}
			});
		}
	}
	
	protected class ViewToolBar extends JToolBar
	{
		protected JButton showVertexLabelsButton;
		protected JButton showVertexWeightsButton;
		protected JButton showEdgeHandlesButton;
		protected JButton showEdgeLabelsButton;
		protected JButton showEdgeWeightsButton;
		protected JButton showCrossingsButton;
		
		public ViewToolBar()
		{
			showVertexLabelsButton = new JButton((ImageIcon) imageIcons.getObject("show_vertex_labels_icon"));
			showVertexLabelsButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					settings.showVertexLabels.set(!settings.showVertexLabels.get());
				}
			});
			this.add(showVertexLabelsButton);
			
			showVertexWeightsButton = new JButton((ImageIcon) imageIcons.getObject("show_vertex_weights_icon"));
			showVertexWeightsButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					settings.showVertexWeights.set(!settings.showVertexWeights.get());
				}
			});
			this.add(showVertexWeightsButton);
			
			showEdgeHandlesButton = new JButton((ImageIcon) imageIcons.getObject("show_edge_handles_icon"));
			showEdgeHandlesButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					settings.showEdgeHandles.set(!settings.showEdgeHandles.get());
				}
			});
			this.add(showEdgeHandlesButton);
			
			showEdgeLabelsButton = new JButton((ImageIcon) imageIcons.getObject("show_edge_labels_icon"));
			showEdgeLabelsButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					settings.showEdgeLabels.set(!settings.showEdgeLabels.get());
				}
			});
			this.add(showEdgeLabelsButton);
			
			showEdgeWeightsButton = new JButton((ImageIcon) imageIcons.getObject("show_edge_weights_icon"));
			showEdgeWeightsButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					settings.showEdgeWeights.set(!settings.showEdgeWeights.get());
				}
			});
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
	
	protected class ZoomToolBar extends JToolBar
	{
		protected JButton zoomGraphButton;
		protected JButton zoomOneToOneButton;
		protected JButton zoomInButton;
		protected JButton zoomOutButton;
		
		public ZoomToolBar()
		{
			zoomGraphButton = new JButton((ImageIcon) imageIcons.getObject("zoom_graph_icon"));
			zoomGraphButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					zoomFit();
				}
			});
			this.add(zoomGraphButton);
			
			zoomOneToOneButton = new JButton((ImageIcon) imageIcons.getObject("zoom_one_to_one_icon"));
			zoomOneToOneButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					zoomOneToOne();
				}
			});
			this.add(zoomOneToOneButton);
			
			zoomInButton = new JButton((ImageIcon) imageIcons.getObject("zoom_in_icon"));
			zoomInButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Point2D.Double viewportCenter = new Point2D.Double(viewport.getWidth() / 2.0, viewport.getHeight() / 2.0);
					Point2D.Double zoomCenter = new Point2D.Double();
					try
					{
						transform.inverseTransform(viewportCenter, zoomCenter);
					}
					catch (NoninvertibleTransformException e1)
					{}
					
					zoomCenter(zoomCenter, GlobalSettings.zoomInFactor);
				}
			});
			this.add(zoomInButton);
			
			zoomOutButton = new JButton((ImageIcon) imageIcons.getObject("zoom_out_icon"));
			zoomOutButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Point2D.Double viewportCenter = new Point2D.Double(viewport.getWidth() / 2.0, viewport.getHeight() / 2.0);
					Point2D.Double zoomCenter = new Point2D.Double();
					try
					{
						transform.inverseTransform(viewportCenter, zoomCenter);
					}
					catch (NoninvertibleTransformException e1)
					{}
					
					zoomCenter(zoomCenter, GlobalSettings.zoomOutFactor);
				}
			});
			this.add(zoomOutButton);
		}
	}
	
	protected class FunctionToolBar extends JToolBar
	{
		protected JButton								  oneTimeFunctionsButton;
		protected JPopupMenu							  oneTimeFunctionsMenu;
		protected Map<JMenuItem, FunctionBase>		   oneTimeFunctionMenuItems;
		
		protected JButton								  dynamicFunctionsButton;
		protected JPopupMenu							  dynamicFunctionsMenu;
		protected Map<JCheckBoxMenuItem, FunctionBase> dynamicFunctionMenuItems;
		
		public FunctionToolBar()
		{
			oneTimeFunctionsButton = new JButton((ImageIcon) imageIcons.getObject("one_time_functions_icon"));
			oneTimeFunctionsButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					oneTimeFunctionsMenu.show(oneTimeFunctionsButton, 0, oneTimeFunctionsButton.getHeight());
				}
			});
			this.add(oneTimeFunctionsButton);
			
			dynamicFunctionsButton = new JButton((ImageIcon) imageIcons.getObject("dynamic_functions_icon"));
			dynamicFunctionsButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					dynamicFunctionsMenu.show(dynamicFunctionsButton, 0, dynamicFunctionsButton.getHeight());
				}
			});
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
						viewport.repaint();
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
						
						viewport.repaint();
					}
				};
				
				for (FunctionBase function : GlobalSettings.allFunctions)
				{
					JCheckBoxMenuItem dynamicFunctionMenuItem = new JCheckBoxMenuItem(function.toString());
					dynamicFunctionMenuItem.addActionListener(dynamicFunctionMenuItemActionListener);
					dynamicFunctionsMenu.add(dynamicFunctionMenuItem);
					dynamicFunctionMenuItems.put(dynamicFunctionMenuItem, function);
					
					JMenuItem oneTimeFunctionMenuItem = new JMenuItem(function.toString());
					oneTimeFunctionMenuItem.addActionListener(oneTimeFunctionMenuItemActionListener);
					oneTimeFunctionsMenu.add(oneTimeFunctionMenuItem);
					oneTimeFunctionMenuItems.put(oneTimeFunctionMenuItem, function);
				}
			}
		}
	}
	
	protected class ViewportPopupMenu extends JPopupMenu
	{
		protected JMenuItem selectAll;
		protected JMenuItem selectAllVerticesItem;
		protected JMenuItem selectAllEdgesItem;
		protected JMenuItem propertiesItem;
		protected JMenuItem vertexItem;
		protected JMenuItem vertexLabelItem;
		protected JMenuItem vertexRadiusItem;
		protected JMenuItem vertexColorItem;
		protected JMenuItem vertexWeightItem;
		protected JMenuItem edgeItem;
		protected JMenuItem edgeWeightItem;
		protected JMenuItem edgeColorItem;
		protected JMenuItem edgeLabelItem;
		protected JMenuItem edgeThicknessItem;
		protected JMenuItem edgeHandleRadiusItem;
		
		public ViewportPopupMenu()
		{
			selectAll = new JMenuItem("Select all");
			selectAll.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					graph.selectAll();
				}
			});
			this.add(selectAll);
			
			selectAllVerticesItem = new JMenuItem("Select all vertices");
			selectAllVerticesItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					for (Vertex v : graph.vertexes)
						v.isSelected.set(true);
				}
			});
			this.add(selectAllVerticesItem);
			
			selectAllEdgesItem = new JMenuItem("Select all edges");
			selectAllEdgesItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					for (Edge e : graph.edges)
						e.isSelected.set(true);
				}
			});
			this.add(selectAllEdgesItem);
			
			this.addSeparator();
			
			propertiesItem = new JMenu("Properties");
			this.add(propertiesItem);
			
			vertexItem = new JMenu("Vertex");
			propertiesItem.add(vertexItem);
			
			vertexLabelItem = new JMenuItem("Label");
			vertexLabelItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String value = JOptionPane.showInputDialog(viewport, "New label:", GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE);
					if (value != null)
						for (Vertex vertex : graph.vertexes)
							if (vertex.isSelected.get())
								vertex.label.set(value);
				}
			});
			vertexItem.add(vertexLabelItem);
			
			vertexRadiusItem = new JMenuItem("Radius");
			vertexRadiusItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String value = JOptionPane.showInputDialog(viewport, "New radius:", GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE);
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
			
			vertexColorItem = new JMenuItem("Color");
			vertexColorItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String value = JOptionPane.showInputDialog(viewport, "New color:", GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE);
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
			
			vertexWeightItem = new JMenuItem("Weight");
			vertexWeightItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String value = JOptionPane.showInputDialog(viewport, "New weight:", GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE);
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
			
			edgeItem = new JMenu("Edge");
			propertiesItem.add(edgeItem);
			
			edgeWeightItem = new JMenuItem("Weight");
			edgeWeightItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String value = JOptionPane.showInputDialog(viewport, "New weight:", GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE);
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
			
			edgeColorItem = new JMenuItem("Color");
			edgeColorItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String value = JOptionPane.showInputDialog(viewport, "New color:", GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE);
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
			
			edgeLabelItem = new JMenuItem("Label");
			edgeLabelItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String label = JOptionPane.showInputDialog(viewport, "New label:", GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE);
					if (label != null)
						for (Edge edge : graph.edges)
							if (edge.isSelected.get())
								edge.label.set(label);
				}
			});
			edgeItem.add(edgeLabelItem);
			
			edgeThicknessItem = new JMenuItem("Thickness");
			edgeThicknessItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String value = JOptionPane.showInputDialog(viewport, "New thickness:", GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE);
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
		}
	}
}







