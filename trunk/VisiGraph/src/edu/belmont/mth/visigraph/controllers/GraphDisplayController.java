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
import edu.belmont.mth.visigraph.utilities.*;
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
public class GraphDisplayController extends JPanel
{
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
	
	public GraphDisplayController ( Graph graph )
	{
		// Initialize the list of GraphChangeEvent listeners
		graphChangeListenerList = new EventListenerList( );
		
		// Add/bind graph
		loadGraph( graph );
		undoHistory = new SnapshotList( graph.toString( ) );
		
		// Add/bind palette
		userSettings.addObserver( new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				onSettingChanged( arg );
			}
		} );
		
		// Add/bind display settings
		settings = new GraphSettings( )
		{
			{
				addObserver( new Observer( )
				{
					@Override
					public void update( Observable o, Object arg )
					{
						onSettingChanged( arg );
					}
				} );
			}
		};
		
		// Initialize the toolbar, buttons, and viewport
		initializeComponents( );
		
		// Initialize the viewport's affine transform
		transform = new AffineTransform( );
		
		// Initialize the viewport's frame delimiter
		isViewportInvalidated = true;
		viewportValidationTimer = new Timer( 30, new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if ( isViewportInvalidated )
					repaint( );
				
				isViewportInvalidated = false;
			}
		} );
		viewportValidationTimer.start( );
	}
	
	public void addGraphChangeListener( GraphChangeEventListener listener )
	{
		graphChangeListenerList.add( GraphChangeEventListener.class, listener );
	}
	
	public void cut( )
	{
		copy( );
		
		// Delete all selected elements from the original graph
		int i = 0;
		while ( i < graph.edges.size( ) )
		{
			Edge edge = graph.edges.get( i );
			if ( edge.isSelected.get( ) )
				graph.edges.remove( i );
			else
				++i;
		}
		
		i = 0;
		while ( i < graph.vertexes.size( ) )
		{
			if ( graph.vertexes.get( i ).isSelected.get( ) )
				graph.vertexes.remove( i );
			else
				++i;
		}
		
		i = 0;
		while ( i < graph.captions.size( ) )
		{
			if ( graph.captions.get( i ).isSelected.get( ) )
				graph.captions.remove( i );
			else
				++i;
		}
	}
	
	public void copy( )
	{
		// Make a copy of graph containing only selected elements
		Graph copy = new Graph( "copy", graph.areLoopsAllowed, graph.areDirectedEdgesAllowed, graph.areMultipleEdgesAllowed, graph.areCyclesAllowed );
		
		for ( Vertex vertex : graph.vertexes )
			if ( vertex.isSelected.get( ) )
				copy.vertexes.add( vertex );
		
		for ( Edge edge : graph.edges )
			if ( edge.isSelected.get( ) && edge.from.isSelected.get( ) && edge.to.isSelected.get( ) )
				copy.edges.add( edge );
		
		for ( Caption caption : graph.captions )
			if ( caption.isSelected.get( ) )
				copy.captions.add( caption );
		
		// Send the JSON to the clipboard
		StringSelection stringSelection = new StringSelection( copy.toString( ) );
		Clipboard clipboard = Toolkit.getDefaultToolkit( ).getSystemClipboard( );
		clipboard.setContents( stringSelection, new ClipboardOwner( )
		{
			@Override
			public void lostOwnership( Clipboard c, Transferable t )
			{
				// Who cares?
			}
		} );
	}
	
	public void dispose( )
	{
		undoTimer.stop( );
	}
	
	private void fireGraphChangeEvent( GraphChangeEvent evt )
	{
		Object[ ] listeners = graphChangeListenerList.getListenerList( );
		// Each listener occupies two elements - the first is the listener class
		// and the second is the listener instance
		for ( int i = 0; i < listeners.length; i += 2 )
			if ( listeners[i] == GraphChangeEventListener.class )
				( (GraphChangeEventListener) listeners[i + 1] ).graphChangeEventOccurred( evt );
	}
	
	public RenderedImage getImage( )
	{
		int width = viewport.getWidth( );
		int height = viewport.getHeight( );
		
		// Create a buffered image in which to draw
		BufferedImage bufferedImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
		
		// Create a graphics contents on the buffered image
		Graphics g = bufferedImage.createGraphics( );
		
		// Draw graphics
		viewport.paint( g );
		
		// Graphics context no longer needed so dispose it
		g.dispose( );
		
		return bufferedImage;
	}
	
	public Graph getGraph( )
	{
		return graph;
	}
	
	public GraphSettings getSettings( )
	{
		return settings;
	}
	
	public Rectangle getSelectionRectangle( )
	{
		return new Rectangle( )
		{
			{
				x = Math.min( pastMousePoint.x, currentMousePoint.x );
				y = Math.min( pastMousePoint.y, currentMousePoint.y );
				width = Math.abs( pastMousePoint.x - currentMousePoint.x );
				height = Math.abs( pastMousePoint.y - currentMousePoint.y );
			}
		};
	}
	
	public void onGraphChanged( Object source )
	{
		isViewportInvalidated = true;
		fireGraphChangeEvent( new GraphChangeEvent( graph ) );
	}
	
	public void onSettingChanged( Object source )
	{
		isViewportInvalidated = true;
		
		if ( toolToolBar != null )
			toolToolBar.refreshPaintMenu( );
		
		if ( viewToolBar != null )
			viewToolBar.refresh( );
		
		if ( undoTimer != null )
			undoTimer.setDelay( userSettings.undoLoggingInterval.get( ) );
		
		if ( undoHistory != null && undoHistory.getCapacity( ) != userSettings.undoLoggingMaximum.get( ) )
		{
			undoHistory.setCapacity( userSettings.undoLoggingMaximum.get( ) );
			undoHistory.clear( );
		}
	}
	
	public void initializeComponents( )
	{
		setLayout( new BorderLayout( ) );
		setBackground( userSettings.graphBackground.get( ) );
		setOpaque( true );
		
		toolToolBarPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) )
		{
			{
				setMaximumSize( new Dimension( Integer.MAX_VALUE, 24 ) );
			}
		};
		add( toolToolBarPanel, BorderLayout.WEST );
		
		toolToolBar = new ToolToolBar( );
		toolToolBarPanel.add( toolToolBar );
		
		nonToolToolbarPanel = new JPanel( new WrapLayout( FlowLayout.LEFT ) )
		{
			{
				setMaximumSize( new Dimension( Integer.MAX_VALUE, 32 ) );
			}
		};
		add( nonToolToolbarPanel, BorderLayout.NORTH );
		
		arrangeToolBar = new ArrangeToolBar( );
		nonToolToolbarPanel.add( arrangeToolBar );
		
		viewToolBar = new ViewToolBar( );
		nonToolToolbarPanel.add( viewToolBar );
		
		zoomToolBar = new ZoomToolBar( );
		nonToolToolbarPanel.add( zoomToolBar );
		
		functionToolBar = new FunctionToolBar( );
		nonToolToolbarPanel.add( functionToolBar );
		
		selectedFunctionLabels = new HashMap<FunctionBase, JLabel>( );
		functionsToBeRun = new TreeSet<FunctionBase>( );
		
		viewportPanel = new JPanel( new BorderLayout( ) )
		{
			{
				setBorder( new BevelBorder( BevelBorder.LOWERED ) );
			}
		};
		add( viewportPanel, BorderLayout.CENTER );
		
		viewport = new JComponent( )
		{
			@Override
			public void paintComponent( Graphics g )
			{
				paintViewport( (Graphics2D) g );
			}
		};
		viewport.addMouseListener( new MouseAdapter( )
		{
			@Override
			public void mousePressed( MouseEvent event )
			{
				try
				{
					viewportMousePressed( event );
				}
				catch ( NoninvertibleTransformException e )
				{
					DebugUtilities.logException( "An exception occurred while inverting transformation.", e );
				}
				
				if ( event.getClickCount( ) > 1 )
					try
					{
						viewportMouseDoubleClicked( event );
					}
					catch ( NoninvertibleTransformException e )
					{
						DebugUtilities.logException( "An exception occurred while inverting transformation.", e );
					}
			}
			
			@Override
			public void mouseReleased( MouseEvent event )
			{
				try
				{
					viewportMouseReleased( event );
				}
				catch ( NoninvertibleTransformException e )
				{
					DebugUtilities.logException( "An exception occurred while inverting transformation.", e );
				}
			}
			
			@Override
			public void mouseEntered( MouseEvent event )
			{
				isMouseOverViewport = true;
			}
			
			@Override
			public void mouseExited( MouseEvent event )
			{
				isMouseOverViewport = false;
			}
		} );
		viewport.addMouseMotionListener( new MouseMotionAdapter( )
		{
			@Override
			public void mouseDragged( MouseEvent event )
			{
				try
				{
					viewportMouseDragged( event );
				}
				catch ( NoninvertibleTransformException e )
				{
					DebugUtilities.logException( "An exception occurred while inverting transformation.", e );
				}
			}
			
			@Override
			public void mouseMoved( MouseEvent event )
			{
				try
				{
					transform.inverseTransform( event.getPoint( ), currentMousePoint );
				}
				catch ( NoninvertibleTransformException e )
				{
					DebugUtilities.logException( "An exception occurred while inverting transformation.", e );
				}
			}
		} );
		viewport.addMouseWheelListener( new MouseWheelListener( )
		{
			public void mouseWheelMoved( MouseWheelEvent e )
			{
				zoomCenter( new Point2D.Double( currentMousePoint.x, currentMousePoint.y ), 1 - e.getWheelRotation( ) * userSettings.scrollIncrementZoom.get( ) );
			}
		} );
		viewport.addKeyListener( new KeyListener( )
		{
			public void keyPressed( KeyEvent e )
			{
				viewportKeyPressed( e );
			}
			
			public void keyReleased( KeyEvent e )
			{
				// Do nothing
			}
			
			public void keyTyped( KeyEvent e )
			{
				// Do nothing
			}
		} );
		viewportPanel.add( viewport, BorderLayout.CENTER );
		viewportPopupMenu = new ViewportPopupMenu( );
		
		statusBar = new JPanel( new FlowLayout( FlowLayout.LEFT ) )
		{
			{
				setMaximumSize( new Dimension( Integer.MAX_VALUE, 12 ) );
			}
		};
		add( statusBar, BorderLayout.SOUTH );
		
		undoTimer = new Timer( userSettings.undoLoggingInterval.get( ), new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if ( undoHistory.getCapacity( ) > 0 )
					undoHistory.add( graph.toString( ) );
			}
		} );
		undoTimer.start( );
	}
	
	public void loadGraph( Graph graph )
	{
		this.graph = graph;
		graph.addObserver( new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				onGraphChanged( arg );
			}
		} );
		
		isMouseDownOnCanvas = false;
		currentMousePoint = new Point( 0, 0 );
		pastMousePoint = new Point( 0, 0 );
		pastPanPoint = new Point( 0, 0 );
		pointerToolClickedObject = false;
		cutToolClickedObject = false;
		paintToolClickedObject = false;
		fromVertex = null;
		
		isViewportInvalidated = true;
	}
	
	public void paintSelectionRectangle( Graphics2D g2D )
	{
		Rectangle selection = getSelectionRectangle( );
		
		g2D.setColor( userSettings.selectionBoxFill.get( ) );
		g2D.fillRect( selection.x, selection.y, selection.width, selection.height );
		
		g2D.setColor( userSettings.selectionBoxLine.get( ) );
		g2D.drawRect( selection.x, selection.y, selection.width, selection.height );
	}
	
	public void paintViewport( Graphics2D g2D )
	{
		// Apply rendering settings
		if ( userSettings.useAntiAliasing.get( ) )
			g2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		
		if ( userSettings.usePureStroke.get( ) )
			g2D.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
		
		if ( userSettings.useBicubicInterpolation.get( ) )
			g2D.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
		
		if ( userSettings.useFractionalMetrics.get( ) )
			g2D.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON );
		
		// Apply any one-time functions
		if ( !functionsToBeRun.isEmpty( ) )
		{
			// For some reason we have to operate on a copy of the set so the message box doesn't lose focus...
			Set<FunctionBase> functionsToBeRunCopy = new TreeSet<FunctionBase>( );
			functionsToBeRunCopy.addAll( functionsToBeRun );
			functionsToBeRun.clear( );
			
			// See Issue #90 (Run-once functions lock up on XP and macs)
			String os = System.getProperty( "os.name" );
			boolean showExternally = os.startsWith( "Mac" ) || os.equals( "Windows NT" ) || os.equals( "Windows XP" );
			
			for ( FunctionBase function : functionsToBeRunCopy )
			{
				String result = function.evaluate( g2D, graph );
				if ( result != null && !result.isEmpty( ) )
				{
					if ( showExternally )
						JOptionPane.showMessageDialog( viewport, function + ": " + result, GlobalSettings.applicationName, JOptionPane.OK_OPTION + JOptionPane.INFORMATION_MESSAGE );
					else
						JOptionPane.showInternalMessageDialog( viewport, function + ": " + result, GlobalSettings.applicationName, JOptionPane.OK_OPTION + JOptionPane.INFORMATION_MESSAGE );
				}
			}
		}
		
		// Clear everything
		super.paintComponent( g2D );
		
		// Apply the transformation
		AffineTransform original = g2D.getTransform( );
		original.concatenate( transform );
		g2D.setTransform( original );
		
		// Paint the graph
		GraphDisplayView.paint( g2D, graph, settings );
		
		// Apply any selected functions
		for ( Entry<FunctionBase, JLabel> entry : selectedFunctionLabels.entrySet( ) )
			entry.getValue( ).setText( entry.getKey( ) + ": " + entry.getKey( ).evaluate( g2D, graph ) );
		
		// Paint controller-specific stuff
		if ( isMouseDownOnCanvas )
			switch ( tool )
			{
				case POINTER_TOOL: if ( !pointerToolClickedObject ) paintSelectionRectangle( g2D ); break;
				case CUT_TOOL:     if ( !cutToolClickedObject     ) paintSelectionRectangle( g2D ); break;
				case GRAPH_TOOL:
					{
						// For the edge tool we might need to paint the temporary drag-edge
						if ( fromVertex != null )
						{
							g2D.setColor( userSettings.draggingEdge.get( ) );
							g2D.drawLine( fromVertex.x.get( ).intValue( ), fromVertex.y.get( ).intValue( ), currentMousePoint.x, currentMousePoint.y );
						}
						
						break;
					}
				case PAINT_TOOL: if ( !paintToolClickedObject ) paintSelectionRectangle( g2D ); break;
			}
	}
	
	public void paste( )
	{
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit( ).getSystemClipboard( );
		
		Transferable contents = clipboard.getContents( null );
		boolean hasTransferableText = ( contents != null ) && contents.isDataFlavorSupported( DataFlavor.stringFlavor );
		
		if ( hasTransferableText )
		{
			try
			{
				result = (String) contents.getTransferData( DataFlavor.stringFlavor );
				
				Graph pasted = new Graph( result );
				
				// Find the centroid
				double elementCount = 0.0;
				Point2D.Double centroid = new Point2D.Double( 0.0, 0.0 );
				
				for ( Vertex vertex : pasted.vertexes )
				{
					centroid.x += vertex.x.get( );
					centroid.y += vertex.y.get( );
					++elementCount;
				}
				
				for ( Edge edge : pasted.edges )
				{
					centroid.x += edge.handleX.get( );
					centroid.y += edge.handleY.get( );
					++elementCount;
				}
				
				for ( Caption caption : pasted.captions )
				{
					centroid.x += caption.x.get( );
					centroid.y += caption.y.get( );
					++elementCount;
				}
				
				centroid.x /= elementCount;
				centroid.y /= elementCount;
				
				// Center everything around the mouse or in the center of the viewport
				Point2D.Double pastePoint = new Point2D.Double( );
				
				if ( isMouseOverViewport )
					pastePoint = new Point2D.Double( currentMousePoint.x, currentMousePoint.y );
				else
					transform.inverseTransform( new Point2D.Double( viewport.getWidth( ) / 2.0, viewport.getHeight( ) / 2.0 ), pastePoint );

				pasted.suspendNotifications( true );
				
				for ( Edge edge : pasted.edges )
					edge.suspendNotifications( true );
				
				for ( Vertex vertex : pasted.vertexes )
				{
					vertex.x.set( vertex.x.get( ) - centroid.x + pastePoint.x );
					vertex.y.set( vertex.y.get( ) - centroid.y + pastePoint.y );
				}
				
				for ( Edge edge : pasted.edges )
				{
					edge.handleX.set( edge.handleX.get( ) - centroid.x + pastePoint.x );
					edge.handleY.set( edge.handleY.get( ) - centroid.y + pastePoint.y );
					edge.suspendNotifications( false );
				}
				
				for ( Caption caption : pasted.captions )
				{
					caption.x.set( caption.x.get( ) - centroid.x + pastePoint.x );
					caption.y.set( caption.y.get( ) - centroid.y + pastePoint.y );
				}
				
				pasted.suspendNotifications( false );
				
				graph.selectAll( false );
				graph.union( pasted );
			}
			catch ( Exception ex ) { DebugUtilities.logException( "An exception occurred while painting the viewport.", ex ); }
		}
	}
	
	public void printGraph( ) throws PrinterException
	{
		new ViewportPrinter( ).print( );
	}
	
	public void redo( )
	{
		if ( undoHistory.next( ) != null )
			loadGraph( new Graph( undoHistory.current( ) ) );
	}
	
	public void removeGraphChangeListener( GraphChangeEventListener listener )
	{
		graphChangeListenerList.remove( GraphChangeEventListener.class, listener );
	}
	
	public void selectAll( )
	{
		graph.selectAll( true );
	}
	
	public void selectAllEdges( )
	{
		for ( Edge e : graph.edges )
			e.isSelected.set( true );
	}

	public void selectAllVertexes( )
	{
		for ( Vertex v : graph.vertexes )
			v.isSelected.set( true );
	}
	
	public void setTool( Tool tool )
	{
		this.tool = tool;
		if ( toolToolBar != null )
			toolToolBar.refresh( );
		
		Cursor cursor;
		
		switch ( this.tool )
		{
			case POINTER_TOOL: cursor = CursorBundle.get( "pointer_tool_cursor" ); break;
			case GRAPH_TOOL:   cursor = CursorBundle.get( "graph_tool_cursor"   ); break;
			case CUT_TOOL:     cursor = CursorBundle.get( "cut_tool_cursor"     ); break;
			case CAPTION_TOOL: cursor = CursorBundle.get( "caption_tool_cursor" ); break;
			case PAINT_TOOL:   cursor = CursorBundle.get( "paint_tool_cursor"   ); break;
			default:           cursor = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ); break;
		}
		
		setCursor( cursor );
		
		fromVertex = null;
		graph.selectAll( false );
	}
	
	public void undo( )
	{
		if ( undoHistory.previous( ) != null )
			loadGraph( new Graph( undoHistory.current( ) ) );
	}
	
	private void viewportKeyPressed( KeyEvent event )
	{
		switch ( event.getKeyCode( ) )
		{
			case KeyEvent.VK_BACK_SPACE: // Fall through...
			case KeyEvent.VK_DELETE:
				{
					if ( tool == Tool.POINTER_TOOL )
						graph.removeSelected( );
					
					break;
				}
			case KeyEvent.VK_ESCAPE:
				{
					graph.selectAll( false );
					break;
				}
			case KeyEvent.VK_UP:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_LEFT:
				{
					double increment = userSettings.arrowKeyIncrement.get( );
					
					if ( event.isAltDown( ) )
						increment /= 10.0;
					if ( event.isShiftDown( ) )
						increment *= 10.0;
					
					if ( ( event.getModifiers( ) & Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) != 0 )
					{
						switch ( event.getKeyCode( ) )
						{
							case KeyEvent.VK_UP:    transform.translate( 0.0, increment  ); break;
							case KeyEvent.VK_RIGHT: transform.translate( -increment, 0.0 ); break;
							case KeyEvent.VK_DOWN:  transform.translate( 0.0, -increment ); break;
							case KeyEvent.VK_LEFT:  transform.translate( increment, 0.0  ); break;
						}
						
						isViewportInvalidated = true;
					}
					else
					{
						switch ( event.getKeyCode( ) )
						{
							case KeyEvent.VK_UP:    graph.translateSelected( 0.0, -increment ); break;
							case KeyEvent.VK_RIGHT: graph.translateSelected( increment, 0.0  ); break;
							case KeyEvent.VK_DOWN:  graph.translateSelected( 0.0, increment  ); break;
							case KeyEvent.VK_LEFT:  graph.translateSelected( -increment, 0.0 ); break;
						}
						
						for ( Edge edge : graph.edges )
							if ( edge.isSelected.get( ) )
								edge.fixHandle( );
					}
					
					break;
				}
		}
	}
	
	private void viewportMouseDragged( MouseEvent event ) throws NoninvertibleTransformException
	{
		Point oldPoint = new Point( currentMousePoint );
		transform.inverseTransform( event.getPoint( ), currentMousePoint );
		
		if ( tool == Tool.POINTER_TOOL && pointerToolClickedObject )
			graph.translateSelected( currentMousePoint.getX( ) - oldPoint.x, currentMousePoint.getY( ) - oldPoint.y );
		
		isViewportInvalidated = true;
	}
	
	private void viewportMousePressed( MouseEvent event ) throws NoninvertibleTransformException
	{
		if ( !viewport.hasFocus( ) )
			viewport.requestFocus( );
		
		int modifiers = event.getModifiersEx( );
		transform.inverseTransform( event.getPoint( ), pastMousePoint );
		transform.inverseTransform( event.getPoint( ), currentMousePoint );
		
		switch ( tool )
		{
			case POINTER_TOOL:
				{
					boolean isShiftDown = ( ( modifiers & InputEvent.SHIFT_DOWN_MASK ) == InputEvent.SHIFT_DOWN_MASK );
					pointerToolClickedObject = false;
					
					for ( int i = graph.vertexes.size( ) - 1; i >= 0; --i )
					{
						Vertex vertex = graph.vertexes.get( i );
						if ( VertexDisplayView.wasClicked( vertex, currentMousePoint, transform.getScaleX( ) ) )
						{
							if ( !vertex.isSelected.get( ) && !isShiftDown )
								graph.selectAll( false );
							
							vertex.isSelected.set( true );
							pointerToolClickedObject = true;
							break;
						}
					}
					
					if ( !pointerToolClickedObject )
						for ( int i = graph.edges.size( ) - 1; i >= 0; --i )
						{
							Edge edge = graph.edges.get( i );
							if ( EdgeDisplayView.wasClicked( edge, currentMousePoint, transform.getScaleX( ) ) )
							{
								if ( !edge.isSelected.get( ) && !isShiftDown )
									graph.selectAll( false );
								
								edge.isSelected.set( true );
								pointerToolClickedObject = true;
								break;
							}
						}
					
					if ( !pointerToolClickedObject )
						for ( int i = graph.captions.size( ) - 1; i >= 0; --i )
						{
							Caption caption = graph.captions.get( i );
							if ( CaptionDisplayView.wasHandleClicked( caption, currentMousePoint, transform.getScaleX( ) ) )
							{
								if ( !caption.isSelected.get( ) && !isShiftDown )
									graph.selectAll( false );
								
								caption.isSelected.set( true );
								pointerToolClickedObject = true;
								break;
							}
						}
					
					if ( !pointerToolClickedObject )
						for ( int i = graph.captions.size( ) - 1; i >= 0; --i )
						{
							Caption caption = graph.captions.get( i );
							if ( CaptionDisplayView.wasEditorClicked( caption, currentMousePoint, transform.getScaleX( ) ) )
							{
								if ( !caption.isSelected.get( ) && !isShiftDown )
									graph.selectAll( false );
								
								EditCaptionDialog.Value newValue = EditCaptionDialog.showDialog( this, this, caption.text.get( ), caption.size.get( ) );
								if ( newValue != null )
								{
									caption.text.set( newValue.getText( ) );
									caption.size.set( newValue.getSize( ) );
								}
								
								pointerToolClickedObject = true;
								break;
							}
						}
					
					if ( !pointerToolClickedObject && !isShiftDown )
						graph.selectAll( false );
					
					break;
				}
			case GRAPH_TOOL:
				{
					if ( event.getButton( ) == MouseEvent.BUTTON1 )
					{
						// The procedure for adding an edge using the edge tool is to click a vertex the edge will come from and subsequently a vertex
						// the edge will go to
						boolean fromVertexClicked = false;
						boolean toVertexClicked = false;
						
						for ( Vertex vertex : graph.vertexes )
							if ( VertexDisplayView.wasClicked( vertex, currentMousePoint, transform.getScaleX( ) ) )
								if ( fromVertex == null )
								{
									// If the user has not yet defined a from Vertex, make this one so
									vertex.isSelected.set( true );
									fromVertex = vertex;
									fromVertexClicked = true;
									break;
								}
								else
								{
									// If the user has already defined a from Vertex, try to add an edge between it and this one
									
									// Only allow loops if the graph specifies to
									if ( graph.areLoopsAllowed || vertex != fromVertex )
									{
										// Only allow multiple edges if the graph specifies to
										if ( graph.areMultipleEdgesAllowed || graph.getEdges( fromVertex, vertex ).size( ) == 0 )
										{
											// Only allow a cycle if the graph specifies to
											if ( graph.areCyclesAllowed || !graph.areConnected( fromVertex, vertex ) )
											{
												graph.edges.add( new Edge( graph.areDirectedEdgesAllowed, fromVertex, vertex ) );
												fromVertex.isSelected.set( false );
												fromVertex = !userSettings.deselectVertexWithNewEdge.get( ) ? vertex : null;
												if ( fromVertex != null )
													fromVertex.isSelected.set( true );
												toVertexClicked = true;
											}
										}
									}
									
									if ( !toVertexClicked )
									{
										fromVertex.isSelected.set( false );
										fromVertex = null;
										fromVertexClicked = true;
									}
								}
						
						if ( !fromVertexClicked && !toVertexClicked )
							graph.vertexes.add( new Vertex( currentMousePoint.x, currentMousePoint.y ) );
					}
					
					break;
				}
			case CAPTION_TOOL:
				{
					if ( event.getButton( ) == MouseEvent.BUTTON1 )
					{
						Caption caption = new Caption( currentMousePoint.x, currentMousePoint.y, "" );
						graph.captions.add( caption );
						
						EditCaptionDialog.Value newValue = EditCaptionDialog.showDialog( this, this, userSettings.defaultCaptionText.get( ), userSettings.defaultCaptionFontSize.get( ) );
						if ( newValue != null )
						{
							caption.text.set( newValue.getText( ) );
							caption.size.set( newValue.getSize( ) );
						}
						else
							graph.captions.remove( caption );
					}
					
					break;
				}
			case CUT_TOOL:
				{
					cutToolClickedObject = false;
					
					if ( event.getButton( ) == MouseEvent.BUTTON1 )
					{
						for ( Vertex vertex : graph.vertexes )
							if ( VertexDisplayView.wasClicked( vertex, currentMousePoint, transform.getScaleX( ) ) )
							{
								graph.vertexes.remove( vertex );
								cutToolClickedObject = true;
								break;
							}
						
						if ( !cutToolClickedObject )
							for ( Edge edge : graph.edges )
								if ( EdgeDisplayView.wasClicked( edge, currentMousePoint, transform.getScaleX( ) ) )
								{
									graph.edges.remove( edge );
									cutToolClickedObject = true;
									break;
								}
						
						if ( !cutToolClickedObject )
							for ( Caption caption : graph.captions )
								if ( CaptionDisplayView.wasHandleClicked( caption, currentMousePoint, transform.getScaleX( ) ) )
								{
									graph.captions.remove( caption );
									cutToolClickedObject = true;
									break;
								}
					}
					
					break;
				}
			case PAINT_TOOL:
				{
					paintToolClickedObject = false;
					
					if ( event.getButton( ) == MouseEvent.BUTTON1 )
					{
						for ( Vertex vertex : graph.vertexes )
							if ( VertexDisplayView.wasClicked( vertex, currentMousePoint, transform.getScaleX( ) ) )
							{
								vertex.color.set( paintColor );
								paintToolClickedObject = true;
								break;
							}
						
						if ( !paintToolClickedObject )
							for ( Edge edge : graph.edges )
								if ( EdgeDisplayView.wasClicked( edge, currentMousePoint, transform.getScaleX( ) ) )
								{
									edge.color.set( paintColor );
									paintToolClickedObject = true;
									break;
								}
					}
				}
		}
		
		isMouseDownOnCanvas = true;
		isViewportInvalidated = true;
	}
	
	private void viewportMouseDoubleClicked( MouseEvent event ) throws NoninvertibleTransformException
	{
		if ( !viewport.hasFocus( ) )
			viewport.requestFocus( );
		
		transform.inverseTransform( event.getPoint( ), pastPanPoint );
		
		if ( userSettings.panOnDoubleClick.get( ) )
		{
			if ( panTimer != null )
				panTimer.stop( );
			
			panTimer = new Timer( 50, new ActionListener( )
			{
				public void actionPerformed( ActionEvent e )
				{
					Timer timer = (Timer) e.getSource( );
					
					try
					{
						Point2D focusPoint = new Point2D.Double( );
						transform.inverseTransform( new Point( viewport.getWidth( ) / 2, viewport.getHeight( ) / 2 ), focusPoint );
						
						double xDelta = Math.round( ( pastMousePoint.x - focusPoint.getX( ) ) / userSettings.panDecelerationFactor.get( ) );
						double yDelta = Math.round( ( pastMousePoint.y - focusPoint.getY( ) ) / userSettings.panDecelerationFactor.get( ) );
						
						transform.translate( xDelta, yDelta );
						isViewportInvalidated = true;
						
						if ( xDelta == 0 && yDelta == 0 )
							timer.stop( );
					}
					catch ( Exception ex )
					{
						DebugUtilities.logException( "An exception occurred while panning viewport.", ex );
						timer.stop( );
					}
				}
			} );
			panTimer.start( );
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
			case GRAPH_TOOL:
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
										fromVertex.isSelected.set( false );
										fromVertex = !userSettings.deselectVertexWithNewEdge.get( ) ? vertex : null;
										if(fromVertex != null) fromVertex.isSelected.set(true);
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
						graph.selectAll(false);
						
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
	
	public void zoomCenter( Point2D.Double center, double factor )
	{
		transform.translate( Math.round( center.x ), Math.round( center.y ) );
		
		transform.scale( factor, factor );
		if ( transform.getScaleX( ) > userSettings.maximumZoomFactor.get( ) )
			zoomMax( );
		
		transform.translate( Math.round( -center.x ), Math.round( -center.y ) );
		
		isViewportInvalidated = true;
	}
	
	public void zoomFit( Rectangle2D rectangle )
	{
		// First we need to reset and translate the graph to the viewport's center
		transform.setToIdentity( );
		transform.translate( Math.round( viewport.getWidth( ) / 2.0 ), Math.round( viewport.getHeight( ) / 2.0 ) );
		
		// We need to fit it to the viewport. So we want to scale according to the lowest viewport-to-graph dimension ratio.
		double widthRatio  = ( viewport.getWidth ( ) - userSettings.zoomGraphPadding.get( ) ) / rectangle.getWidth ( );
		double heightRatio = ( viewport.getHeight( ) - userSettings.zoomGraphPadding.get( ) ) / rectangle.getHeight( );
		double minRatio = Math.min( widthRatio, heightRatio );
		
		if ( minRatio < 1 )
			transform.scale( minRatio, minRatio );
		
		// Only now that we've properly scaled can we translate to the graph's midpoint
		Point2D.Double graphCenter = new Point2D.Double( rectangle.getCenterX( ), rectangle.getCenterY( ) );
		transform.translate( Math.round( -graphCenter.x ), Math.round( -graphCenter.y ) );
		
		// And of course, we want to refresh the viewport
		isViewportInvalidated = true;
	}
	
	public void zoomFit( )
	{
		if ( graph.vertexes.size( ) > 0 )
			zoomFit( GraphDisplayView.getBounds( graph ) );
	}
	
	public void zoomOneToOne( )
	{
		transform.setTransform( 1, transform.getShearY( ), transform.getShearX( ), 1, (int) transform.getTranslateX( ), (int) transform.getTranslateY( ) );
		isViewportInvalidated = true;
	}
	
	public void zoomMax( )
	{
		transform.setTransform( userSettings.maximumZoomFactor.get( ), transform.getShearY( ), transform.getShearX( ), userSettings.maximumZoomFactor.get( ), Math.round( transform.getTranslateX( ) ), Math.round( transform.getTranslateY( ) ) );
		isViewportInvalidated = true;
	}

	public enum Tool
	{
		POINTER_TOOL, GRAPH_TOOL, CAPTION_TOOL, CUT_TOOL, PAINT_TOOL
	}
	
	private class ToolToolBar extends JToolBar
	{
		private JButton pointerToolButton;
		private JButton graphToolButton;
		private JButton captionToolButton;
		private JButton cutToolButton;
		private JButton paintToolButton;
		private JPopupMenu paintMenu;
		private Timer paintMenuTimer;
		
		public ToolToolBar ( )
		{
			this.setOrientation( SwingConstants.VERTICAL );
			this.setFloatable( false );
			
			pointerToolButton = new JButton( ImageIconBundle.get( "pointer_tool_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							setTool( Tool.POINTER_TOOL );
						}
					} );
					setToolTipText( StringBundle.get( "pointer_tool_tooltip" ) );
					setSelected( true );
				}
			};
			this.add( pointerToolButton );
			
			graphToolButton = new JButton( ImageIconBundle.get( "graph_tool_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							setTool( Tool.GRAPH_TOOL );
						}
					} );
					setToolTipText( StringBundle.get( "graph_tool_tooltip" ) );
				}
			};
			this.add( graphToolButton );
			
			captionToolButton = new JButton( ImageIconBundle.get( "caption_tool_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							setTool( Tool.CAPTION_TOOL );
						}
					} );
					setToolTipText( StringBundle.get( "caption_tool_tooltip" ) );
				}
			};
			this.add( captionToolButton );
			
			cutToolButton = new JButton( ImageIconBundle.get( "cut_tool_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							setTool( Tool.CUT_TOOL );
						}
					} );
					setToolTipText( StringBundle.get( "cut_tool_tooltip" ) );
				}
			};
			this.add( cutToolButton );
			
			paintToolButton = new JButton( ImageIconBundle.get( "paint_tool_icon" ) )
			{
				{
					addMouseListener( new MouseAdapter( )
					{
						@Override
						public void mouseExited( MouseEvent e )
						{
							isMouseDownOnPaintToolButton = false;
						}
						
						@Override
						public void mousePressed( MouseEvent event )
						{
							setTool( Tool.PAINT_TOOL );
							isMouseDownOnPaintToolButton = true;
							paintMenuTimer.start( );
						}
						
						@Override
						public void mouseReleased( MouseEvent event )
						{
							isMouseDownOnPaintToolButton = false;
						}
					} );
					setToolTipText( StringBundle.get( "paint_tool_tooltip" ) );
				}
			};
			paintMenuTimer = new Timer( userSettings.paintToolMenuDelay.get( ), new ActionListener( )
			{
				@Override
				public void actionPerformed( ActionEvent e )
				{
					paintMenuTimer.stop( );
					
					if ( isMouseDownOnPaintToolButton )
						paintMenu.show( paintToolButton, 0, paintToolButton.getHeight( ) );
				}
			} );
			this.add( paintToolButton );
			
			setTool( Tool.POINTER_TOOL );
			
			paintMenu = new JPopupMenu( );
			refreshPaintMenu( );
			paintColor = -1;
			
			refresh( );
		}
		
		public void refresh( )
		{
			for ( Component toolButton : this.getComponents( ) )
				if ( toolButton instanceof JButton )
					( (JButton) toolButton ).setSelected( false );
			
			switch ( tool )
			{
				case POINTER_TOOL: pointerToolButton.setSelected( true ); break;
				case GRAPH_TOOL:   graphToolButton  .setSelected( true ); break;
				case CUT_TOOL:     cutToolButton    .setSelected( true ); break;
				case CAPTION_TOOL: captionToolButton.setSelected( true ); break;
				case PAINT_TOOL:   paintToolButton  .setSelected( true ); break;
			}
		}
		
		public void refreshPaintMenu( )
		{
			paintMenu.removeAll( );
			
			final ActionListener paintMenuItemActionListener = new ActionListener( )
			{
				public void actionPerformed( ActionEvent e )
				{
					paintColor = paintMenu.getComponentIndex( (Component) e.getSource( ) ) - 1;
					
					for ( Component paintMenuItem : paintMenu.getComponents( ) )
						if ( paintMenuItem instanceof JMenuItem )
							( (JCheckBoxMenuItem) paintMenuItem ).setState( false );
					
					if ( paintColor + 1 < paintMenu.getComponentCount( ) && paintMenu.getComponent( paintColor + 1 ) instanceof JMenuItem )
						( (JCheckBoxMenuItem) paintMenu.getComponent( paintColor + 1 ) ).setState( true );
				}
			};
			
			JCheckBoxMenuItem defaultPaintToolMenuItem = new JCheckBoxMenuItem( StringBundle.get( "default_paint_tool_menu_item" ) )
			{
				{
					addActionListener( paintMenuItemActionListener );
					setSelected( true );
				}
			};
			paintMenu.add( defaultPaintToolMenuItem );
			
			for ( int i = 0; i < userSettings.elementColors.size( ); ++i )
			{
				JCheckBoxMenuItem paintToolMenuItem = new JCheckBoxMenuItem( "\u2588\u2588\u2588\u2588\u2588\u2588  (" + i + ")" );
				paintToolMenuItem.setForeground( userSettings.getVertexColor( i ) );
				paintToolMenuItem.addActionListener( paintMenuItemActionListener );
				paintMenu.add( paintToolMenuItem );
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
		
		public ArrangeToolBar ( )
		{
			arrangeCircleButton = new JButton( ImageIconBundle.get( "arrange_circle_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphUtilities.arrangeCircle( graph );
							zoomFit( );
						}
					} );
					setToolTipText( StringBundle.get( "arrange_circle_button_tooltip" ) );
				}
			};
			this.add( arrangeCircleButton );
			
			arrangeGridButton = new JButton( ImageIconBundle.get( "arrange_grid_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphUtilities.arrangeGrid( graph );
							zoomFit( );
						}
					} );
					setToolTipText( StringBundle.get( "arrange_grid_button_tooltip" ) );
				}
			};
			this.add( arrangeGridButton );
			
			arrangeTreeButton = new JButton( ImageIconBundle.get( "arrange_tree_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphUtilities.arrangeTree( graph );
							zoomFit( );
						}
					} );
					setToolTipText( StringBundle.get( "arrange_tree_button_tooltip" ) );
				}
			};
			this.add( arrangeTreeButton );
			
			arrangeWebButton = new JButton( ImageIconBundle.get( "arrange_web_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							new Timer( 50, new ActionListener( )
							{
								public void actionPerformed( ActionEvent e )
								{
									Timer timer = (Timer) e.getSource( );
									timer.setDelay( (int) ( timer.getDelay( ) * userSettings.autoArrangeDecelerationFactor.get( ) ) );
									
									if ( GraphUtilities.arrangeTensors( graph ) || timer.getDelay( ) >= 500 )
										timer.stop( );
								}
							} ).start( );
						}
					} );
					setToolTipText( StringBundle.get( "arrange_web_button_tooltip" ) );
				}
			};
			this.add( arrangeWebButton );
			
			this.add( new JToolBar.Separator( ) );
			
			alignHorizontallyButton = new JButton( ImageIconBundle.get( "align_horizontally_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphUtilities.alignHorizontally( graph );
						}
					} );
					setToolTipText( StringBundle.get( "align_horizontally_button_tooltip" ) );
				}
			};
			this.add( alignHorizontallyButton );
			
			alignVerticallyButton = new JButton( ImageIconBundle.get( "align_vertically_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphUtilities.alignVertically( graph );
						}
					} );
					setToolTipText( StringBundle.get( "align_vertically_button_tooltip" ) );
				}
			};
			this.add( alignVerticallyButton );
			
			distributeHorizontallyButton = new JButton( ImageIconBundle.get( "distribute_horizontally_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphUtilities.distributeHorizontally( graph );
						}
					} );
					setToolTipText( StringBundle.get( "distribute_horizontally_button_tooltip" ) );
				}
			};
			this.add( distributeHorizontallyButton );
			
			distributeVerticallyButton = new JButton( ImageIconBundle.get( "distribute_vertically_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphUtilities.distributeVertically( graph );
						}
					} );
					setToolTipText( StringBundle.get( "distribute_vertically_button_tooltip" ) );
				}
			};
			this.add( distributeVerticallyButton );
			
			this.add( new JToolBar.Separator( ) );
			
			rotateLeft90Button = new JButton( ImageIconBundle.get( "rotate_left_90_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphUtilities.rotateLeft90( graph );
						}
					} );
					setToolTipText( StringBundle.get( "rotate_left_90_button_tooltip" ) );
				}
			};
			this.add( rotateLeft90Button );
			
			rotateRight90Button = new JButton( ImageIconBundle.get( "rotate_right_90_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphUtilities.rotateRight90( graph );
						}
					} );
					setToolTipText( StringBundle.get( "rotate_right_90_button_tooltip" ) );
				}
			};
			this.add( rotateRight90Button );
			
			flipHorizontallyButton = new JButton( ImageIconBundle.get( "flip_horizontally_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphUtilities.flipHorizontally( graph );
						}
					} );
					setToolTipText( StringBundle.get( "flip_horizontally_button_tooltip" ) );
				}
			};
			this.add( flipHorizontallyButton );
			
			flipVerticallyButton = new JButton( ImageIconBundle.get( "flip_vertically_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphUtilities.flipVertically( graph );
						}
					} );
					setToolTipText( StringBundle.get( "flip_vertically_button_tooltip" ) );
				}
			};
			this.add( flipVerticallyButton );
			
			this.add( new JToolBar.Separator( ) );
			
			contractButton = new JButton( ImageIconBundle.get( "contract_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphUtilities.scale( graph, UserSettings.instance.arrangeContractFactor.get( ) );
						}
					} );
					setToolTipText( StringBundle.get( "contract_button_tooltip" ) );
				}
			};
			this.add( contractButton );
			
			expandButton = new JButton( ImageIconBundle.get( "expand_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphUtilities.scale( graph, UserSettings.instance.arrangeExpandFactor.get( ) );
						}
					} );
					setToolTipText( StringBundle.get( "expand_button_tooltip" ) );
				}
			};
			this.add( expandButton );
		}
	}
	
	private class ViewToolBar extends JToolBar
	{
		private JButton showVertexLabelsButton;
		private JButton showVertexWeightsButton;
		private JButton showEdgeHandlesButton;
		private JButton showEdgeLabelsButton;
		private JButton showEdgeWeightsButton;
		private JButton showCaptionHandlesButton;
		private JButton showCaptionEditorsButton;
		
		public ViewToolBar ( )
		{
			showVertexLabelsButton = new JButton( ImageIconBundle.get( "show_vertex_labels_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							settings.showVertexLabels.set( !settings.showVertexLabels.get( ) );
						}
					} );
					setToolTipText( StringBundle.get( "show_vertex_labels_button_tooltip" ) );
				}
			};
			this.add( showVertexLabelsButton );
			
			showVertexWeightsButton = new JButton( ImageIconBundle.get( "show_vertex_weights_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							settings.showVertexWeights.set( !settings.showVertexWeights.get( ) );
						}
					} );
					setToolTipText( StringBundle.get( "show_vertex_weights_button_tooltip" ) );
				}
			};
			this.add( showVertexWeightsButton );
			
			showEdgeHandlesButton = new JButton( ImageIconBundle.get( "show_edge_handles_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							settings.showEdgeHandles.set( !settings.showEdgeHandles.get( ) );
						}
					} );
					setToolTipText( StringBundle.get( "show_edge_handles_button_tooltip" ) );
				}
			};
			this.add( showEdgeHandlesButton );
			
			showEdgeLabelsButton = new JButton( ImageIconBundle.get( "show_edge_labels_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							settings.showEdgeLabels.set( !settings.showEdgeLabels.get( ) );
						}
					} );
					setToolTipText( StringBundle.get( "show_edge_labels_button_tooltip" ) );
				}
			};
			this.add( showEdgeLabelsButton );
			
			showEdgeWeightsButton = new JButton( ImageIconBundle.get( "show_edge_weights_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							settings.showEdgeWeights.set( !settings.showEdgeWeights.get( ) );
						}
					} );
					setToolTipText( StringBundle.get( "show_edge_weights_button_tooltip" ) );
				}
			};
			this.add( showEdgeWeightsButton );
			
			showCaptionHandlesButton = new JButton( ImageIconBundle.get( "show_caption_handles_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							settings.showCaptionHandles.set( !settings.showCaptionHandles.get( ) );
						}
					} );
					setToolTipText( StringBundle.get( "show_caption_handles_button_tooltip" ) );
				}
			};
			this.add( showCaptionHandlesButton );
			
			showCaptionEditorsButton = new JButton( ImageIconBundle.get( "show_caption_editors_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							settings.showCaptionEditors.set( !settings.showCaptionEditors.get( ) );
						}
					} );
					setToolTipText( StringBundle.get( "show_caption_editors_button_tooltip" ) );
				}
			};
			this.add( showCaptionEditorsButton );
			
			this.refresh( );
		}
		
		public void refresh( )
		{
			if ( showVertexLabelsButton != null && showVertexLabelsButton.isSelected( ) != settings.showVertexLabels.get( ) )
				showVertexLabelsButton.setSelected( settings.showVertexLabels.get( ) );
			
			if ( showVertexWeightsButton != null && showVertexWeightsButton.isSelected( ) != settings.showVertexWeights.get( ) )
				showVertexWeightsButton.setSelected( settings.showVertexWeights.get( ) );
			
			if ( showEdgeHandlesButton != null && showEdgeHandlesButton.isSelected( ) != settings.showEdgeHandles.get( ) )
				showEdgeHandlesButton.setSelected( settings.showEdgeHandles.get( ) );
			
			if ( showEdgeLabelsButton != null && showEdgeLabelsButton.isSelected( ) != settings.showEdgeLabels.get( ) )
				showEdgeLabelsButton.setSelected( settings.showEdgeLabels.get( ) );
			
			if ( showEdgeWeightsButton != null && showEdgeWeightsButton.isSelected( ) != settings.showEdgeWeights.get( ) )
				showEdgeWeightsButton.setSelected( settings.showEdgeWeights.get( ) );
			
			if ( showCaptionHandlesButton != null && showCaptionHandlesButton.isSelected( ) != settings.showCaptionHandles.get( ) )
				showCaptionHandlesButton.setSelected( settings.showCaptionHandles.get( ) );
			
			if ( showCaptionEditorsButton != null && showCaptionEditorsButton.isSelected( ) != settings.showCaptionEditors.get( ) )
				showCaptionEditorsButton.setSelected( settings.showCaptionEditors.get( ) );
		}
	}
	
	private class ZoomToolBar extends JToolBar
	{
		private JButton zoomGraphButton;
		private JButton zoomOneToOneButton;
		private JButton zoomInButton;
		private JButton zoomOutButton;
		
		public ZoomToolBar ( )
		{
			zoomGraphButton = new JButton( ImageIconBundle.get( "zoom_graph_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							zoomFit( );
						}
					} );
					setToolTipText( StringBundle.get( "zoom_graph_button_tooltip" ) );
				}
			};
			this.add( zoomGraphButton );
			
			zoomOneToOneButton = new JButton( ImageIconBundle.get( "zoom_one_to_one_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							zoomOneToOne( );
						}
					} );
					setToolTipText( StringBundle.get( "zoom_one_to_one_button_tooltip" ) );
				}
			};
			this.add( zoomOneToOneButton );
			
			zoomInButton = new JButton( ImageIconBundle.get( "zoom_in_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							Point2D.Double viewportCenter = new Point2D.Double( viewport.getWidth( ) / 2.0, viewport.getHeight( ) / 2.0 );
							Point2D.Double zoomCenter = new Point2D.Double( );
							try
							{
								transform.inverseTransform( viewportCenter, zoomCenter );
							}
							catch ( NoninvertibleTransformException ex )
							{
								DebugUtilities.logException( "An exception occurred while inverting transformation.", ex );
							}
							
							zoomCenter( zoomCenter, userSettings.zoomInFactor.get( ) );
						}
					} );
					setToolTipText( StringBundle.get( "zoom_in_button_tooltip" ) );
				}
			};
			this.add( zoomInButton );
			
			zoomOutButton = new JButton( ImageIconBundle.get( "zoom_out_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							Point2D.Double viewportCenter = new Point2D.Double( viewport.getWidth( ) / 2.0, viewport.getHeight( ) / 2.0 );
							Point2D.Double zoomCenter = new Point2D.Double( );
							try
							{
								transform.inverseTransform( viewportCenter, zoomCenter );
							}
							catch ( NoninvertibleTransformException ex )
							{
								DebugUtilities.logException( "An exception occurred while inverting transformation.", ex );
							}
							
							zoomCenter( zoomCenter, userSettings.zoomOutFactor.get( ) );
						}
					} );
					setToolTipText( StringBundle.get( "zoom_out_button_tooltip" ) );
				}
			};
			this.add( zoomOutButton );
		}
	}
	
	private class FunctionToolBar extends JToolBar
	{
		private JButton    oneTimeFunctionsButton;
		private JPopupMenu oneTimeFunctionsMenu;
		private Map<JMenuItem, FunctionBase> oneTimeFunctionMenuItems;
		
		private JButton    dynamicFunctionsButton;
		private JPopupMenu dynamicFunctionsMenu;
		private Map<JCheckBoxMenuItem, FunctionBase> dynamicFunctionMenuItems;
		
		public FunctionToolBar ( )
		{
			oneTimeFunctionsButton = new JButton( ImageIconBundle.get( "one_time_functions_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							oneTimeFunctionsMenu.show( oneTimeFunctionsButton, 0, oneTimeFunctionsButton.getHeight( ) );
						}
					} );
					setToolTipText( StringBundle.get( "one_time_functions_button_tooltip" ) );
				}
			};
			this.add( oneTimeFunctionsButton );
			
			dynamicFunctionsButton = new JButton( ImageIconBundle.get( "dynamic_functions_icon" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							dynamicFunctionsMenu.show( dynamicFunctionsButton, 0, dynamicFunctionsButton.getHeight( ) );
						}
					} );
					setToolTipText( StringBundle.get( "dynamic_functions_button_tooltip" ) );
				}
			};
			this.add( dynamicFunctionsButton );
			
			oneTimeFunctionsMenu = new JPopupMenu( );
			dynamicFunctionsMenu = new JPopupMenu( );
			
			oneTimeFunctionMenuItems = new HashMap<JMenuItem, FunctionBase>( );
			dynamicFunctionMenuItems = new HashMap<JCheckBoxMenuItem, FunctionBase>( );
			
			refresh( );
		}
		
		public void refresh( )
		{
			if ( oneTimeFunctionsMenu != null && dynamicFunctionsMenu != null )
			{
				oneTimeFunctionsMenu.removeAll( );
				dynamicFunctionsMenu.removeAll( );
				
				oneTimeFunctionMenuItems = new HashMap<JMenuItem, FunctionBase>( );
				dynamicFunctionMenuItems = new HashMap<JCheckBoxMenuItem, FunctionBase>( );
				ActionListener oneTimeFunctionMenuItemActionListener = new ActionListener( )
				{
					public void actionPerformed( ActionEvent e )
					{
						JMenuItem oneTimeFunctionMenuItem = (JMenuItem) e.getSource( );
						functionsToBeRun.add( oneTimeFunctionMenuItems.get( oneTimeFunctionMenuItem ) );
						isViewportInvalidated = true;
					}
				};
				ActionListener dynamicFunctionMenuItemActionListener = new ActionListener( )
				{
					public void actionPerformed( ActionEvent e )
					{
						JCheckBoxMenuItem dynamicFunctionMenuItem = (JCheckBoxMenuItem) e.getSource( );
						
						if ( dynamicFunctionMenuItem.isSelected( ) )
						{
							JLabel functionLabel = new JLabel( );
							JToolBar functionToolBar = new JToolBar( );
							functionToolBar.add( functionLabel );
							statusBar.add( functionToolBar );
							selectedFunctionLabels.put( dynamicFunctionMenuItems.get( dynamicFunctionMenuItem ), functionLabel );
						}
						else
						{
							statusBar.remove( selectedFunctionLabels.get( dynamicFunctionMenuItems.get( dynamicFunctionMenuItem ) ).getParent( ) );
							statusBar.validate( );
							selectedFunctionLabels.remove( dynamicFunctionMenuItems.get( dynamicFunctionMenuItem ) );
						}
						
						isViewportInvalidated = true;
					}
				};
				
				for ( FunctionBase function : FunctionService.instance.functions )
				{
					if ( function.allowsDynamicEvaluation( ) )
					{
						JCheckBoxMenuItem dynamicFunctionMenuItem = new JCheckBoxMenuItem( function.toString( ) );
						dynamicFunctionMenuItem.addActionListener( dynamicFunctionMenuItemActionListener );
						dynamicFunctionsMenu.add( dynamicFunctionMenuItem );
						dynamicFunctionMenuItems.put( dynamicFunctionMenuItem, function );
					}
					
					if ( function.allowsOneTimeEvaluation( ) )
					{
						JMenuItem oneTimeFunctionMenuItem = new JMenuItem( function.toString( ) );
						oneTimeFunctionMenuItem.addActionListener( oneTimeFunctionMenuItemActionListener );
						oneTimeFunctionsMenu.add( oneTimeFunctionMenuItem );
						oneTimeFunctionMenuItems.put( oneTimeFunctionMenuItem, function );
					}
				}
			}
		}
	}
	
	private class ViewportPopupMenu extends JPopupMenu
	{
		private JMenuItem vertexItem;
		private JMenuItem vertexLabelItem;
		private JMenuItem vertexRadiusItem;
		private JMenuItem vertexColorItem;
		private JMenuItem vertexWeightItem;
		private JMenuItem edgeItem;
		private JMenuItem edgeLabelItem;
		private JMenuItem edgeThicknessItem;
		private JMenuItem edgeColorItem;
		private JMenuItem edgeWeightItem;
		
		public ViewportPopupMenu ( )
		{
			vertexItem = new JMenu( StringBundle.get( "properties_vertex_menu_text" ) );
			this.add( vertexItem );
			
			vertexLabelItem = new JMenuItem( StringBundle.get( "properties_vertex_label_menu_text" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							String value = JOptionPane.showInputDialog( viewport, StringBundle.get( "new_vertex_label_dialog_text" ), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE );
							if ( value != null )
								for ( Vertex vertex : graph.vertexes )
									if ( vertex.isSelected.get( ) )
										vertex.label.set( value );
						}
					} );
				}
			};
			vertexItem.add( vertexLabelItem );
			
			vertexRadiusItem = new JMenuItem( StringBundle.get( "properties_vertex_radius_menu_text" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							String value = JOptionPane.showInputDialog( viewport, StringBundle.get( "new_vertex_radius_dialog_text" ), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE );
							if ( value != null )
							{
								double radius = Double.parseDouble( value );
								
								for ( Vertex vertex : graph.vertexes )
									if ( vertex.isSelected.get( ) )
										vertex.radius.set( radius );
							}
						}
					} );
				}
			};
			vertexItem.add( vertexRadiusItem );
			
			vertexColorItem = new JMenuItem( StringBundle.get( "properties_vertex_color_menu_text" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							String value = JOptionPane.showInputDialog( viewport, StringBundle.get( "new_vertex_color_dialog_text" ), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE );
							if ( value != null )
							{
								int color = Integer.parseInt( value );
								
								for ( Vertex vertex : graph.vertexes )
									if ( vertex.isSelected.get( ) )
										vertex.color.set( color );
							}
						}
					} );
				}
			};
			vertexItem.add( vertexColorItem );
			
			vertexWeightItem = new JMenuItem( StringBundle.get( "properties_vertex_weight_menu_text" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							String value = JOptionPane.showInputDialog( viewport, StringBundle.get( "new_vertex_weight_dialog_text" ), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE );
							if ( value != null )
							{
								double weight = Double.parseDouble( value );
								
								for ( Vertex vertex : graph.vertexes )
									if ( vertex.isSelected.get( ) )
										vertex.weight.set( weight );
							}
						}
					} );
				}
			};
			vertexItem.add( vertexWeightItem );
			
			edgeItem = new JMenu( StringBundle.get( "properties_edge_menu_text" ) );
			this.add( edgeItem );
			
			edgeLabelItem = new JMenuItem( StringBundle.get( "properties_edge_label_menu_text" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							String label = JOptionPane.showInputDialog( viewport, StringBundle.get( "new_edge_label_dialog_text" ), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE );
							if ( label != null )
								for ( Edge edge : graph.edges )
									if ( edge.isSelected.get( ) )
										edge.label.set( label );
						}
					} );
				}
			};
			edgeItem.add( edgeLabelItem );
			
			edgeThicknessItem = new JMenuItem( StringBundle.get( "properties_edge_thickness_menu_text" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							String value = JOptionPane.showInputDialog( viewport, StringBundle.get( "new_edge_thickness_dialog_text" ), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE );
							if ( value != null )
							{
								double thickness = Double.parseDouble( value );
								
								for ( Edge edge : graph.edges )
									if ( edge.isSelected.get( ) )
										edge.thickness.set( thickness );
							}
						}
					} );
				}
			};
			edgeItem.add( edgeThicknessItem );
			
			edgeColorItem = new JMenuItem( StringBundle.get( "properties_edge_color_menu_text" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							String value = JOptionPane.showInputDialog( viewport, StringBundle.get( "new_edge_color_dialog_text" ), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE );
							if ( value != null )
							{
								int color = Integer.parseInt( value );
								
								for ( Edge edge : graph.edges )
									if ( edge.isSelected.get( ) )
										edge.color.set( color );
							}
						}
					} );
				}
			};
			edgeItem.add( edgeColorItem );
			
			edgeWeightItem = new JMenuItem( StringBundle.get( "properties_edge_weight_menu_text" ) )
			{
				{
					addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							String value = JOptionPane.showInputDialog( viewport, StringBundle.get( "new_edge_weight_dialog_text" ), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE );
							if ( value != null )
							{
								double weight = Double.parseDouble( value );
								
								for ( Edge edge : graph.edges )
									if ( edge.isSelected.get( ) )
										edge.weight.set( weight );
							}
						}
					} );
				}
			};
			edgeItem.add( edgeWeightItem );
		}
		
		public void setVertexMenuEnabled( boolean enable )
		{
			vertexItem.setEnabled( enable );
		}
		
		public void setEdgeMenuEnabled( boolean enable )
		{
			edgeItem.setEnabled( enable );
		}
	}

	private class ViewportPrinter implements Printable
	{
		public void print( ) throws PrinterException
		{
			PrinterJob printJob = PrinterJob.getPrinterJob( );
			printJob.setPrintable( this );
			
			if ( printJob.printDialog( ) )
				printJob.print( );
		}
		
		public int print( Graphics g, PageFormat pageFormat, int pageIndex )
		{
			if ( pageIndex > 0 )
				return ( NO_SUCH_PAGE );
			else
			{
				Graphics2D g2d = (Graphics2D) g;
				
				double widthRatio = ( viewport.getWidth( ) + userSettings.zoomGraphPadding.get( ) ) / pageFormat.getImageableWidth( );
				double heightRatio = ( viewport.getHeight( ) + userSettings.zoomGraphPadding.get( ) ) / pageFormat.getImageableHeight( );
				double maxRatio = 1.0 / Math.max( widthRatio, heightRatio );
				
				g2d.scale( maxRatio, maxRatio );
				g2d.translate( pageFormat.getImageableX( ) + userSettings.zoomGraphPadding.get( ) / 4.0, pageFormat.getImageableY( ) + userSettings.zoomGraphPadding.get( ) / 4.0 );
				
				RepaintManager.currentManager( viewport ).setDoubleBufferingEnabled( false );
				viewport.paint( g2d );
				RepaintManager.currentManager( viewport ).setDoubleBufferingEnabled( true );
				
				return ( PAGE_EXISTS );
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
		
		public SnapshotList ( String snapshot )
		{
			newest = oldest = current = new Snapshot( snapshot );
			capacity = userSettings.undoLoggingMaximum.get( );
			size = 0;
		}
		
		public void add( String snapshot )
		{
			if ( !snapshot.equals( current.value ) )
			{
				if ( size < capacity )
				{
					Snapshot newSnapshot = new Snapshot( snapshot, current, current.next );
					current.next.previous = newSnapshot;
					current = newest = current.next = newSnapshot;
					++size;
				}
				else
				{
					current.next.value = snapshot;
					current = newest = current.next;
					if ( current == oldest )
						oldest = current.next;
				}
			}
		}
		
		public void clear( )
		{
			newest = oldest = current;
			current.previous = current;
			current.next = current;
		}
		
		public String previous( )
		{
			if ( current == oldest )
				return null;
			
			current = current.previous;
			
			return current.value;
		}
		
		public String current( )
		{
			return current.value;
		}
		
		public String next( )
		{
			if ( current == newest )
				return null;
			
			current = current.next;
			
			return current.value;
		}
		
		public int getCapacity( )
		{
			return capacity;
		}
		
		public void setCapacity( int capacity )
		{
			this.capacity = capacity;
		}
		
		private class Snapshot
		{
			public String value;
			public Snapshot previous;
			public Snapshot next;
			
			public Snapshot ( String value )
			{
				this.value = value;
				this.previous = this;
				this.next = this;
			}
			
			public Snapshot ( String value, Snapshot previous, Snapshot next )
			{
				this.value = value;
				this.previous = previous;
				this.next = next;
			}
		}
	}
	
	public class GraphChangeEvent extends EventObject
	{
		public GraphChangeEvent ( Object source )
		{
			super( source );
		}
	}
	
	public interface GraphChangeEventListener extends EventListener
	{
		public void graphChangeEventOccurred( GraphChangeEvent evt );
	}
}






