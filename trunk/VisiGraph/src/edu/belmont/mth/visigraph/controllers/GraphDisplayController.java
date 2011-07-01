/**
 * GraphDisplayController.java
 */
package edu.belmont.mth.visigraph.controllers;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.util.List;
import java.awt.geom.*;
import java.util.Map.*;
import java.awt.image.*;
import java.awt.print.*;
import java.awt.event.*;
import javax.swing.Timer;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.datatransfer.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.resources.*;
import edu.belmont.mth.visigraph.gui.dialogs.*;
import edu.belmont.mth.visigraph.gui.layouts.*;
import edu.belmont.mth.visigraph.views.display.*;
import edu.belmont.mth.visigraph.models.functions.*;

/**
 * @author Cameron Behar
 */
public class GraphDisplayController extends JPanel
{
	private class ArrangeToolBar extends JToolBar
	{
		private final JButton	arrangeCircleButton;
		private final JButton	arrangeGridButton;
		private final JButton	arrangeTreeButton;
		private final JButton	arrangeWebButton;
		private final JButton	alignVerticallyButton;
		private final JButton	alignHorizontallyButton;
		private final JButton	distributeHorizontallyButton;
		private final JButton	distributeVerticallyButton;
		private final JButton	rotateLeft90Button;
		private final JButton	rotateRight90Button;
		private final JButton	flipHorizontallyButton;
		private final JButton	flipVerticallyButton;
		private final JButton	contractButton;
		private final JButton	expandButton;
		
		public ArrangeToolBar( )
		{
			this.arrangeCircleButton = new JButton( ImageIconBundle.get( "arrange_circle_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
							
							if( selectedVertices.isEmpty( ) )
								if( GraphDisplayController.this.graph.hasSelectedCaptions( ) || GraphDisplayController.this.graph.hasSelectedEdges( ) )
									return;
								else
									selectedVertices = GraphDisplayController.this.graph.vertices;
							
							LayoutUtilities.arrangeCircle( selectedVertices );
							
							GraphDisplayController.this.zoomFit( );
						}
					} );
					this.setToolTipText( StringBundle.get( "arrange_circle_button_tooltip" ) );
				}
			};
			this.add( this.arrangeCircleButton );
			
			this.arrangeGridButton = new JButton( ImageIconBundle.get( "arrange_grid_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
							
							if( selectedVertices.isEmpty( ) )
								if( GraphDisplayController.this.graph.hasSelectedCaptions( ) || GraphDisplayController.this.graph.hasSelectedEdges( ) )
									return;
								else
									selectedVertices = GraphDisplayController.this.graph.vertices;
							
							LayoutUtilities.arrangeGrid( selectedVertices );
							
							GraphDisplayController.this.zoomFit( );
						}
					} );
					this.setToolTipText( StringBundle.get( "arrange_grid_button_tooltip" ) );
				}
			};
			this.add( this.arrangeGridButton );
			
			this.arrangeTreeButton = new JButton( ImageIconBundle.get( "arrange_tree_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
							if( !selectedVertices.isEmpty( ) )
							{
								LayoutUtilities.arrangeTree( selectedVertices, GraphDisplayController.this.graph );
								GraphDisplayController.this.zoomFit( );
							}
						}
					} );
					this.setToolTipText( StringBundle.get( "arrange_tree_button_tooltip" ) );
				}
			};
			this.add( this.arrangeTreeButton );
			
			this.arrangeWebButton = new JButton( ImageIconBundle.get( "arrange_web_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							ArrangeToolBar.this.arrangeWebButton.setEnabled( false );
							GraphDisplayController.this.arrangeWebSpeed = 5.0;
							
							new Timer( 50, new ActionListener( )
							{
								Map<Vertex, Point2D>	velocities	= new HashMap<Vertex, Point2D>( );
								
								@Override
								public void actionPerformed( ActionEvent e )
								{
									List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
									if( selectedVertices.isEmpty( ) )
										if( GraphDisplayController.this.graph.hasSelectedCaptions( ) || GraphDisplayController.this.graph.hasSelectedEdges( ) )
											return;
										else
											selectedVertices = GraphDisplayController.this.graph.vertices;
									
									final double kineticEnergy = LayoutUtilities.arrangeTensors( selectedVertices, GraphDisplayController.this.graph.edges, UserSettings.instance.autoArrangeAttractiveForce.get( ), UserSettings.instance.autoArrangeRepulsiveForce.get( ), GraphDisplayController.this.arrangeWebSpeed, this.velocities );
									
									if( kineticEnergy < 0.01 || ( GraphDisplayController.this.arrangeWebSpeed *= UserSettings.instance.autoArrangeDecelerationFactor.get( ) ) < 0.15 )
									{
										ArrangeToolBar.this.arrangeWebButton.setEnabled( true );
										( (Timer) e.getSource( ) ).stop( );
									}
								}
							} ).start( );
						}
					} );
					this.setToolTipText( StringBundle.get( "arrange_web_button_tooltip" ) );
				}
			};
			this.add( this.arrangeWebButton );
			
			this.add( new JToolBar.Separator( ) );
			
			this.alignHorizontallyButton = new JButton( ImageIconBundle.get( "align_horizontally_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
							
							if( selectedVertices.isEmpty( ) )
								if( GraphDisplayController.this.graph.hasSelectedCaptions( ) || GraphDisplayController.this.graph.hasSelectedEdges( ) )
									return;
								else
									selectedVertices = GraphDisplayController.this.graph.vertices;
							
							LayoutUtilities.alignHorizontally( selectedVertices );
						}
					} );
					this.setToolTipText( StringBundle.get( "align_horizontally_button_tooltip" ) );
				}
			};
			this.add( this.alignHorizontallyButton );
			
			this.alignVerticallyButton = new JButton( ImageIconBundle.get( "align_vertically_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
							
							if( selectedVertices.isEmpty( ) )
								if( GraphDisplayController.this.graph.hasSelectedCaptions( ) || GraphDisplayController.this.graph.hasSelectedEdges( ) )
									return;
								else
									selectedVertices = GraphDisplayController.this.graph.vertices;
							
							LayoutUtilities.alignVertically( selectedVertices );
						}
					} );
					this.setToolTipText( StringBundle.get( "align_vertically_button_tooltip" ) );
				}
			};
			this.add( this.alignVerticallyButton );
			
			this.distributeHorizontallyButton = new JButton( ImageIconBundle.get( "distribute_horizontally_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
							
							if( selectedVertices.isEmpty( ) )
								if( GraphDisplayController.this.graph.hasSelectedCaptions( ) || GraphDisplayController.this.graph.hasSelectedEdges( ) )
									return;
								else
									selectedVertices = GraphDisplayController.this.graph.vertices;
							
							LayoutUtilities.distributeHorizontally( selectedVertices );
						}
					} );
					this.setToolTipText( StringBundle.get( "distribute_horizontally_button_tooltip" ) );
				}
			};
			this.add( this.distributeHorizontallyButton );
			
			this.distributeVerticallyButton = new JButton( ImageIconBundle.get( "distribute_vertically_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
							
							if( selectedVertices.isEmpty( ) )
								if( GraphDisplayController.this.graph.hasSelectedCaptions( ) || GraphDisplayController.this.graph.hasSelectedEdges( ) )
									return;
								else
									selectedVertices = GraphDisplayController.this.graph.vertices;
							
							LayoutUtilities.distributeVertically( selectedVertices );
						}
					} );
					this.setToolTipText( StringBundle.get( "distribute_vertically_button_tooltip" ) );
				}
			};
			this.add( this.distributeVerticallyButton );
			
			this.add( new JToolBar.Separator( ) );
			
			this.rotateLeft90Button = new JButton( ImageIconBundle.get( "rotate_left_90_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
							List<Edge> selectedEdges = GraphDisplayController.this.graph.getSelectedEdges( );
							
							if( selectedVertices.isEmpty( ) )
							{
								if( !selectedEdges.isEmpty( ) || GraphDisplayController.this.graph.hasSelectedCaptions( ) )
									return;
								
								selectedVertices = GraphDisplayController.this.graph.vertices;
							}
							
							LayoutUtilities.rotateLeft90( selectedVertices, selectedEdges );
						}
					} );
					this.setToolTipText( StringBundle.get( "rotate_left_90_button_tooltip" ) );
				}
			};
			this.add( this.rotateLeft90Button );
			
			this.rotateRight90Button = new JButton( ImageIconBundle.get( "rotate_right_90_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
							List<Edge> selectedEdges = GraphDisplayController.this.graph.getSelectedEdges( );
							
							if( selectedVertices.isEmpty( ) )
							{
								if( !selectedEdges.isEmpty( ) || GraphDisplayController.this.graph.hasSelectedCaptions( ) )
									return;
								
								selectedVertices = GraphDisplayController.this.graph.vertices;
							}
							
							LayoutUtilities.rotateRight90( selectedVertices, selectedEdges );
						}
					} );
					this.setToolTipText( StringBundle.get( "rotate_right_90_button_tooltip" ) );
				}
			};
			this.add( this.rotateRight90Button );
			
			this.flipHorizontallyButton = new JButton( ImageIconBundle.get( "flip_horizontally_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
							List<Edge> selectedEdges = GraphDisplayController.this.graph.getSelectedEdges( );
							
							if( selectedVertices.isEmpty( ) )
							{
								if( !selectedEdges.isEmpty( ) || GraphDisplayController.this.graph.hasSelectedCaptions( ) )
									return;
								
								selectedVertices = GraphDisplayController.this.graph.vertices;
								selectedEdges = GraphDisplayController.this.graph.edges;
							}
							
							if( selectedEdges.isEmpty( ) )
								for( Edge edge : GraphDisplayController.this.graph.edges )
									if( edge.from.isSelected.get( ) && edge.to.isSelected.get( ) )
										selectedEdges.add( edge );
							
							LayoutUtilities.flipHorizontally( selectedVertices, selectedEdges );
						}
					} );
					this.setToolTipText( StringBundle.get( "flip_horizontally_button_tooltip" ) );
				}
			};
			this.add( this.flipHorizontallyButton );
			
			this.flipVerticallyButton = new JButton( ImageIconBundle.get( "flip_vertically_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
							List<Edge> selectedEdges = GraphDisplayController.this.graph.getSelectedEdges( );
							
							if( selectedVertices.isEmpty( ) )
							{
								if( !selectedEdges.isEmpty( ) || GraphDisplayController.this.graph.hasSelectedCaptions( ) )
									return;
								
								selectedVertices = GraphDisplayController.this.graph.vertices;
								selectedEdges = GraphDisplayController.this.graph.edges;
							}
							
							if( selectedEdges.isEmpty( ) )
								for( Edge edge : GraphDisplayController.this.graph.edges )
									if( edge.from.isSelected.get( ) && edge.to.isSelected.get( ) )
										selectedEdges.add( edge );
							
							LayoutUtilities.flipVertically( selectedVertices, selectedEdges );
						}
					} );
					this.setToolTipText( StringBundle.get( "flip_vertically_button_tooltip" ) );
				}
			};
			this.add( this.flipVerticallyButton );
			
			this.add( new JToolBar.Separator( ) );
			
			this.contractButton = new JButton( ImageIconBundle.get( "contract_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
							
							if( selectedVertices.isEmpty( ) )
								if( GraphDisplayController.this.graph.hasSelectedCaptions( ) || GraphDisplayController.this.graph.hasSelectedEdges( ) )
									return;
								else
									selectedVertices = GraphDisplayController.this.graph.vertices;
							
							LayoutUtilities.scale( selectedVertices, UserSettings.instance.arrangeContractFactor.get( ) );
						}
					} );
					this.setToolTipText( StringBundle.get( "contract_button_tooltip" ) );
				}
			};
			this.add( this.contractButton );
			
			this.expandButton = new JButton( ImageIconBundle.get( "expand_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
							
							if( selectedVertices.isEmpty( ) )
								if( GraphDisplayController.this.graph.hasSelectedCaptions( ) || GraphDisplayController.this.graph.hasSelectedEdges( ) )
									return;
								else
									selectedVertices = GraphDisplayController.this.graph.vertices;
							
							LayoutUtilities.scale( selectedVertices, UserSettings.instance.arrangeExpandFactor.get( ) );
						}
					} );
					this.setToolTipText( StringBundle.get( "expand_button_tooltip" ) );
				}
			};
			this.add( this.expandButton );
		}
	}
	
	private class FunctionToolBar extends JToolBar implements Observer
	{
		private final JButton						oneTimeFunctionsButton;
		private final JPopupMenu					oneTimeFunctionsMenu;
		private Map<JMenuItem, Function>			oneTimeFunctionMenuItems;
		
		private final JButton						dynamicFunctionsButton;
		private final JPopupMenu					dynamicFunctionsMenu;
		private Map<JCheckBoxMenuItem, Function>	dynamicFunctionMenuItems;
		
		public FunctionToolBar( )
		{
			this.oneTimeFunctionsButton = new JButton( ImageIconBundle.get( "one_time_functions_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							FunctionToolBar.this.oneTimeFunctionsMenu.show( FunctionToolBar.this.oneTimeFunctionsButton, 0, FunctionToolBar.this.oneTimeFunctionsButton.getHeight( ) );
						}
					} );
					this.setToolTipText( StringBundle.get( "one_time_functions_button_tooltip" ) );
				}
			};
			this.add( this.oneTimeFunctionsButton );
			
			this.dynamicFunctionsButton = new JButton( ImageIconBundle.get( "dynamic_functions_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							FunctionToolBar.this.dynamicFunctionsMenu.show( FunctionToolBar.this.dynamicFunctionsButton, 0, FunctionToolBar.this.dynamicFunctionsButton.getHeight( ) );
						}
					} );
					this.setToolTipText( StringBundle.get( "dynamic_functions_button_tooltip" ) );
				}
			};
			this.add( this.dynamicFunctionsButton );
			
			this.oneTimeFunctionsMenu = new JPopupMenu( );
			this.dynamicFunctionsMenu = new JPopupMenu( );
			
			this.oneTimeFunctionMenuItems = new HashMap<JMenuItem, Function>( );
			this.dynamicFunctionMenuItems = new HashMap<JCheckBoxMenuItem, Function>( );
			
			this.refresh( );
			
			FunctionService.instance.functions.addObserver( this );
		}
		
		@Override
		public void finalize( )
		{
			FunctionService.instance.functions.deleteObserver( this );
		}
		
		public void refresh( )
		{
			if( this.oneTimeFunctionsMenu != null && this.dynamicFunctionsMenu != null )
			{
				this.oneTimeFunctionsMenu.removeAll( );
				this.dynamicFunctionsMenu.removeAll( );
				
				this.oneTimeFunctionMenuItems = new HashMap<JMenuItem, Function>( );
				this.dynamicFunctionMenuItems = new HashMap<JCheckBoxMenuItem, Function>( );
				ActionListener oneTimeFunctionMenuItemActionListener = new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JMenuItem oneTimeFunctionMenuItem = (JMenuItem) e.getSource( );
						GraphDisplayController.this.functionsToBeRun.add( FunctionToolBar.this.oneTimeFunctionMenuItems.get( oneTimeFunctionMenuItem ) );
						GraphDisplayController.this.isViewportInvalidated = true;
					}
				};
				ActionListener dynamicFunctionMenuItemActionListener = new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JCheckBoxMenuItem dynamicFunctionMenuItem = (JCheckBoxMenuItem) e.getSource( );
						
						if( dynamicFunctionMenuItem.isSelected( ) )
						{
							JLabel functionLabel = new JLabel( );
							JToolBar functionToolBar = new JToolBar( );
							functionToolBar.add( functionLabel );
							GraphDisplayController.this.statusBar.add( functionToolBar );
							GraphDisplayController.this.selectedFunctionLabels.put( FunctionToolBar.this.dynamicFunctionMenuItems.get( dynamicFunctionMenuItem ), functionLabel );
						}
						else
						{
							GraphDisplayController.this.statusBar.remove( GraphDisplayController.this.selectedFunctionLabels.get( FunctionToolBar.this.dynamicFunctionMenuItems.get( dynamicFunctionMenuItem ) ).getParent( ) );
							GraphDisplayController.this.statusBar.validate( );
							GraphDisplayController.this.selectedFunctionLabels.remove( FunctionToolBar.this.dynamicFunctionMenuItems.get( dynamicFunctionMenuItem ) );
						}
						
						GraphDisplayController.this.isViewportInvalidated = true;
					}
				};
				
				for( Function function : FunctionService.instance.functions )
					if( function.isApplicable( GraphDisplayController.this.graph.areLoopsAllowed, GraphDisplayController.this.graph.areDirectedEdgesAllowed, GraphDisplayController.this.graph.areMultipleEdgesAllowed, GraphDisplayController.this.graph.areCyclesAllowed ) )
					{
						if( (Boolean) function.getAttribute( Function.Attribute.ALLOWS_ONE_TIME_EVALUATION ) )
						{
							JMenuItem oneTimeFunctionMenuItem = new JMenuItem( function.toString( ) );
							oneTimeFunctionMenuItem.addActionListener( oneTimeFunctionMenuItemActionListener );
							this.oneTimeFunctionsMenu.add( oneTimeFunctionMenuItem );
							this.oneTimeFunctionMenuItems.put( oneTimeFunctionMenuItem, function );
						}
						
						if( (Boolean) function.getAttribute( Function.Attribute.ALLOWS_DYNAMIC_EVALUATION ) )
						{
							JCheckBoxMenuItem dynamicFunctionMenuItem = new JCheckBoxMenuItem( function.toString( ) );
							dynamicFunctionMenuItem.addActionListener( dynamicFunctionMenuItemActionListener );
							this.dynamicFunctionsMenu.add( dynamicFunctionMenuItem );
							this.dynamicFunctionMenuItems.put( dynamicFunctionMenuItem, function );
						}
					}
			}
		}
		
		@Override
		public void update( Observable observable, Object object )
		{
			this.refresh( );
		}
	}
	
	public class GraphChangeEvent extends EventObject
	{
		public GraphChangeEvent( Object source )
		{
			super( source );
		}
	}
	
	public interface GraphChangeEventListener extends EventListener
	{
		public void graphChangeEventOccurred( GraphChangeEvent evt );
	}
	
	private class SnapshotList
	{
		private class Snapshot
		{
			public String	value;
			public Snapshot	previous;
			public Snapshot	next;
			
			public Snapshot( String value )
			{
				this.value = value;
				this.previous = this;
				this.next = this;
			}
			
			public Snapshot( String value, Snapshot previous, Snapshot next )
			{
				this.value = value;
				this.previous = previous;
				this.next = next;
			}
		}
		
		// SnapshotList is essentially a kind of doubly-linked circular list of strings with a maximum capacity
		private Snapshot	current;
		private Snapshot	newest;
		private Snapshot	oldest;
		private int			capacity;
		private int			size;
		
		public SnapshotList( String snapshot )
		{
			this.newest = this.oldest = this.current = new Snapshot( snapshot );
			this.capacity = UserSettings.instance.undoLoggingMaximum.get( );
			this.size = 0;
		}
		
		public void add( String snapshot )
		{
			if( !snapshot.equals( this.current.value ) )
				if( this.size < this.capacity )
				{
					Snapshot newSnapshot = new Snapshot( snapshot, this.current, this.current.next );
					this.current.next.previous = newSnapshot;
					this.current = this.newest = this.current.next = newSnapshot;
					++this.size;
				}
				else
				{
					this.current.next.value = snapshot;
					this.current = this.newest = this.current.next;
					if( this.current == this.oldest )
						this.oldest = this.current.next;
				}
		}
		
		public void clear( )
		{
			this.newest = this.oldest = this.current;
			this.current.previous = this.current;
			this.current.next = this.current;
		}
		
		public String current( )
		{
			return this.current.value;
		}
		
		public int getCapacity( )
		{
			return this.capacity;
		}
		
		public String next( )
		{
			if( this.current == this.newest )
				return null;
			
			this.current = this.current.next;
			
			return this.current.value;
		}
		
		public String previous( )
		{
			if( this.current == this.oldest )
				return null;
			
			this.current = this.current.previous;
			
			return this.current.value;
		}
		
		public void setCapacity( int capacity )
		{
			this.capacity = capacity;
		}
	}
	
	public enum Tool
	{
		POINTER_TOOL, GRAPH_TOOL, CAPTION_TOOL, CUT_TOOL, PAINT_TOOL
	}
	
	private class ToolToolBar extends JToolBar
	{
		private final JButton		pointerToolButton;
		private final JButton		graphToolButton;
		private final JButton		captionToolButton;
		private final JButton		cutToolButton;
		private final JButton		paintToolButton;
		private final JPopupMenu	paintMenu;
		private final Timer			paintMenuTimer;
		
		public ToolToolBar( )
		{
			this.setOrientation( SwingConstants.VERTICAL );
			this.setFloatable( false );
			
			this.pointerToolButton = new JButton( ImageIconBundle.get( "pointer_tool_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.setTool( Tool.POINTER_TOOL );
						}
					} );
					this.setToolTipText( StringBundle.get( "pointer_tool_tooltip" ) );
					this.setSelected( true );
				}
			};
			this.add( this.pointerToolButton );
			
			this.graphToolButton = new JButton( ImageIconBundle.get( "graph_tool_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.setTool( Tool.GRAPH_TOOL );
						}
					} );
					this.setToolTipText( StringBundle.get( "graph_tool_tooltip" ) );
				}
			};
			this.add( this.graphToolButton );
			
			this.captionToolButton = new JButton( ImageIconBundle.get( "caption_tool_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.setTool( Tool.CAPTION_TOOL );
						}
					} );
					this.setToolTipText( StringBundle.get( "caption_tool_tooltip" ) );
				}
			};
			this.add( this.captionToolButton );
			
			this.cutToolButton = new JButton( ImageIconBundle.get( "cut_tool_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.setTool( Tool.CUT_TOOL );
						}
					} );
					this.setToolTipText( StringBundle.get( "cut_tool_tooltip" ) );
				}
			};
			this.add( this.cutToolButton );
			
			this.paintToolButton = new JButton( ImageIconBundle.get( "paint_tool_icon" ) )
			{
				{
					this.addMouseListener( new MouseAdapter( )
					{
						@Override
						public void mouseExited( MouseEvent e )
						{
							GraphDisplayController.this.isMouseDownOnPaintToolButton = false;
						}
						
						@Override
						public void mousePressed( MouseEvent event )
						{
							GraphDisplayController.this.setTool( Tool.PAINT_TOOL );
							GraphDisplayController.this.isMouseDownOnPaintToolButton = true;
							ToolToolBar.this.paintMenuTimer.start( );
						}
						
						@Override
						public void mouseReleased( MouseEvent event )
						{
							GraphDisplayController.this.isMouseDownOnPaintToolButton = false;
						}
					} );
					this.setToolTipText( StringBundle.get( "paint_tool_tooltip" ) );
				}
			};
			this.paintMenuTimer = new Timer( UserSettings.instance.paintToolMenuDelay.get( ), new ActionListener( )
			{
				@Override
				public void actionPerformed( ActionEvent e )
				{
					ToolToolBar.this.paintMenuTimer.stop( );
					
					if( GraphDisplayController.this.isMouseDownOnPaintToolButton )
						ToolToolBar.this.paintMenu.show( ToolToolBar.this.paintToolButton, 0, ToolToolBar.this.paintToolButton.getHeight( ) );
				}
			} );
			this.add( this.paintToolButton );
			
			GraphDisplayController.this.setTool( Tool.POINTER_TOOL );
			
			this.paintMenu = new JPopupMenu( );
			this.refreshPaintMenu( );
			GraphDisplayController.this.paintColor = -1;
			
			this.refresh( );
		}
		
		public void refresh( )
		{
			for( Component toolButton : this.getComponents( ) )
				if( toolButton instanceof JButton )
					( (JButton) toolButton ).setSelected( false );
			
			switch( GraphDisplayController.this.tool )
			{
				case POINTER_TOOL:
					this.pointerToolButton.setSelected( true );
					break;
				case GRAPH_TOOL:
					this.graphToolButton.setSelected( true );
					break;
				case CUT_TOOL:
					this.cutToolButton.setSelected( true );
					break;
				case CAPTION_TOOL:
					this.captionToolButton.setSelected( true );
					break;
				case PAINT_TOOL:
					this.paintToolButton.setSelected( true );
					break;
			}
		}
		
		public void refreshPaintMenu( )
		{
			this.paintMenu.removeAll( );
			
			final ActionListener paintMenuItemActionListener = new ActionListener( )
			{
				@Override
				public void actionPerformed( ActionEvent e )
				{
					GraphDisplayController.this.paintColor = ToolToolBar.this.paintMenu.getComponentIndex( (Component) e.getSource( ) ) - 1;
					
					for( Component paintMenuItem : ToolToolBar.this.paintMenu.getComponents( ) )
						if( paintMenuItem instanceof JMenuItem )
							( (JCheckBoxMenuItem) paintMenuItem ).setState( false );
					
					if( GraphDisplayController.this.paintColor + 1 < ToolToolBar.this.paintMenu.getComponentCount( ) && ToolToolBar.this.paintMenu.getComponent( GraphDisplayController.this.paintColor + 1 ) instanceof JMenuItem )
						( (JCheckBoxMenuItem) ToolToolBar.this.paintMenu.getComponent( GraphDisplayController.this.paintColor + 1 ) ).setState( true );
				}
			};
			
			new JCheckBoxMenuItem( "(-)" )
			{
				{
					this.addActionListener( paintMenuItemActionListener );
					ToolToolBar.this.paintMenu.add( this );
					this.setIcon( this.getSwatch( ) );
					this.setSelected( true );
				}
				
				private ImageIcon getSwatch( )
				{
					BufferedImage swatch = new BufferedImage( 56, 14, BufferedImage.TYPE_INT_ARGB );
					
					Graphics2D g2D = (Graphics2D) swatch.getGraphics( );
					g2D.setColor( UserSettings.instance.getVertexColor( -1 ) );
					g2D.fillRect( 0, 0, 56, 14 );
					g2D.setColor( UserSettings.instance.getEdgeColor( -1 ) );
					g2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
					g2D.fillPolygon( new int[ ] { 0, 0, 56 }, new int[ ] { 0, 14, 14 }, 3 );
					g2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
					g2D.setColor( new Color( 0, 0, 0, 127 ) );
					g2D.drawRect( 0, 0, 55, 13 );
					g2D.dispose( );
					
					return new ImageIcon( swatch );
				}
			};
			
			for( int i = 0; i < UserSettings.instance.elementColors.size( ); ++i )
				new JCheckBoxMenuItem( Integer.toString( i ) )
				{
					{
						int color = Integer.parseInt( this.getText( ) );
						
						this.setText( "(" + color + ")" );
						this.setForeground( UserSettings.instance.getVertexColor( color ) );
						this.addActionListener( paintMenuItemActionListener );
						ToolToolBar.this.paintMenu.add( this );
						this.setIcon( this.getSwatch( UserSettings.instance.getVertexColor( color ), UserSettings.instance.getVertexColor( color ).darker( ) ) );
					}
					
					private ImageIcon getSwatch( Color fill, Color border )
					{
						BufferedImage swatch = new BufferedImage( 56, 14, BufferedImage.TYPE_INT_ARGB );
						
						Graphics2D g2D = (Graphics2D) swatch.getGraphics( );
						g2D.setColor( fill );
						g2D.fillRect( 0, 0, 56, 14 );
						g2D.setColor( new Color( 0, 0, 0, 127 ) );
						g2D.drawRect( 0, 0, 55, 13 );
						g2D.dispose( );
						
						return new ImageIcon( swatch );
					}
				};
		}
	}
	
	private class ViewportPopupMenu extends JPopupMenu
	{
		private final JMenuItem	vertexItem;
		private final JMenuItem	vertexLabelItem;
		private final JMenuItem	vertexRadiusItem;
		private final JMenuItem	vertexColorItem;
		private final JMenuItem	vertexWeightItem;
		private final JMenuItem	edgeItem;
		private final JMenuItem	edgeLabelItem;
		private final JMenuItem	edgeThicknessItem;
		private final JMenuItem	edgeColorItem;
		private final JMenuItem	edgeWeightItem;
		
		public ViewportPopupMenu( )
		{
			this.vertexItem = new JMenu( StringBundle.get( "properties_vertex_menu_text" ) );
			this.add( this.vertexItem );
			
			this.vertexLabelItem = new JMenuItem( StringBundle.get( "properties_vertex_label_menu_text" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
							
							String oldLabel = null;
							for( Vertex vertex : selectedVertices )
								if( oldLabel == null )
									oldLabel = vertex.label.get( );
								else if( !oldLabel.equals( vertex.label.get( ) ) )
								{
									oldLabel = null;
									break;
								}
							
							Object value = JOptionPane.showInputDialog( GraphDisplayController.this.viewport, StringBundle.get( "new_vertex_label_dialog_text" ), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE, null, null, ( oldLabel != null ? oldLabel : "" ) );
							if( value != null )
								for( Vertex vertex : selectedVertices )
									vertex.label.set( value.toString( ) );
						}
					} );
				}
			};
			this.vertexItem.add( this.vertexLabelItem );
			
			this.vertexRadiusItem = new JMenuItem( StringBundle.get( "properties_vertex_radius_menu_text" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
							
							Double oldRadius = null;
							for( Vertex vertex : selectedVertices )
								if( oldRadius == null )
									oldRadius = vertex.radius.get( );
								else if( !oldRadius.equals( vertex.radius.get( ) ) )
								{
									oldRadius = null;
									break;
								}
							
							Object value = JOptionPane.showInputDialog( GraphDisplayController.this.viewport, StringBundle.get( "new_vertex_radius_dialog_text" ), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE, null, null, ( oldRadius != null ? oldRadius : "" ) );
							if( value != null )
							{
								double newRadius = Double.parseDouble( value.toString( ) );
								for( Vertex vertex : selectedVertices )
									vertex.radius.set( newRadius );
							}
						}
					} );
				}
			};
			this.vertexItem.add( this.vertexRadiusItem );
			
			this.vertexColorItem = new JMenuItem( StringBundle.get( "properties_vertex_color_menu_text" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
							
							Integer oldColor = null;
							for( Vertex vertex : selectedVertices )
								if( oldColor == null )
									oldColor = vertex.color.get( );
								else if( !oldColor.equals( vertex.color.get( ) ) )
								{
									oldColor = null;
									break;
								}
							
							Integer newColor = EditColorDialog.showDialog( GraphDisplayController.this.viewport, oldColor );
							if( newColor != null )
								for( Vertex vertex : selectedVertices )
									vertex.color.set( newColor );
						}
					} );
				}
			};
			this.vertexItem.add( this.vertexColorItem );
			
			this.vertexWeightItem = new JMenuItem( StringBundle.get( "properties_vertex_weight_menu_text" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
							
							Double oldWeight = null;
							for( Vertex vertex : selectedVertices )
								if( oldWeight == null )
									oldWeight = vertex.weight.get( );
								else if( !oldWeight.equals( vertex.weight.get( ) ) )
								{
									oldWeight = null;
									break;
								}
							
							Object value = JOptionPane.showInputDialog( GraphDisplayController.this.viewport, StringBundle.get( "new_vertex_weight_dialog_text" ), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE, null, null, ( oldWeight != null ? oldWeight : "" ) );
							if( value != null )
							{
								double newWeight = Double.parseDouble( value.toString( ) );
								for( Vertex vertex : selectedVertices )
									vertex.weight.set( newWeight );
							}
						}
					} );
				}
			};
			this.vertexItem.add( this.vertexWeightItem );
			
			this.edgeItem = new JMenu( StringBundle.get( "properties_edge_menu_text" ) );
			this.add( this.edgeItem );
			
			this.edgeLabelItem = new JMenuItem( StringBundle.get( "properties_edge_label_menu_text" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Edge> selectedEdges = GraphDisplayController.this.graph.getSelectedEdges( );
							
							String oldLabel = null;
							for( Edge edge : selectedEdges )
								if( oldLabel == null )
									oldLabel = edge.label.get( );
								else if( !oldLabel.equals( edge.label.get( ) ) )
								{
									oldLabel = null;
									break;
								}
							
							Object value = JOptionPane.showInputDialog( GraphDisplayController.this.viewport, StringBundle.get( "new_edge_label_dialog_text" ), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE, null, null, ( oldLabel != null ? oldLabel : "" ) );
							if( value != null )
								for( Edge edge : selectedEdges )
									edge.label.set( value.toString( ) );
						}
					} );
				}
			};
			this.edgeItem.add( this.edgeLabelItem );
			
			this.edgeThicknessItem = new JMenuItem( StringBundle.get( "properties_edge_thickness_menu_text" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Edge> selectedEdges = GraphDisplayController.this.graph.getSelectedEdges( );
							
							Double oldThickness = null;
							for( Edge edge : selectedEdges )
								if( oldThickness == null )
									oldThickness = edge.thickness.get( );
								else if( !oldThickness.equals( edge.thickness.get( ) ) )
								{
									oldThickness = null;
									break;
								}
							
							Object value = JOptionPane.showInputDialog( GraphDisplayController.this.viewport, StringBundle.get( "new_edge_thickness_dialog_text" ), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE, null, null, ( oldThickness != null ? oldThickness : "" ) );
							if( value != null )
							{
								double newThickness = Double.parseDouble( value.toString( ) );
								for( Edge edge : selectedEdges )
									edge.thickness.set( newThickness );
							}
						}
					} );
				}
			};
			this.edgeItem.add( this.edgeThicknessItem );
			
			this.edgeColorItem = new JMenuItem( StringBundle.get( "properties_edge_color_menu_text" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Edge> selectedEdges = GraphDisplayController.this.graph.getSelectedEdges( );
							
							Integer oldColor = null;
							for( Edge edge : selectedEdges )
								if( oldColor == null )
									oldColor = edge.color.get( );
								else if( !oldColor.equals( edge.color.get( ) ) )
								{
									oldColor = null;
									break;
								}
							
							Integer newColor = EditColorDialog.showDialog( GraphDisplayController.this.viewport, oldColor );
							if( newColor != null )
								for( Edge edge : selectedEdges )
									edge.color.set( newColor );
						}
					} );
				}
			};
			this.edgeItem.add( this.edgeColorItem );
			
			this.edgeWeightItem = new JMenuItem( StringBundle.get( "properties_edge_weight_menu_text" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							List<Edge> selectedEdges = GraphDisplayController.this.graph.getSelectedEdges( );
							
							Double oldWeight = null;
							for( Edge edge : selectedEdges )
								if( oldWeight == null )
									oldWeight = edge.weight.get( );
								else if( !oldWeight.equals( edge.weight.get( ) ) )
								{
									oldWeight = null;
									break;
								}
							
							Object value = JOptionPane.showInputDialog( GraphDisplayController.this.viewport, StringBundle.get( "new_edge_weight_dialog_text" ), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE, null, null, ( oldWeight != null ? oldWeight : "" ) );
							if( value != null )
							{
								double newWeight = Double.parseDouble( value.toString( ) );
								for( Edge edge : selectedEdges )
									edge.weight.set( newWeight );
							}
						}
					} );
				}
			};
			this.edgeItem.add( this.edgeWeightItem );
		}
		
		public void setEdgeMenuEnabled( boolean enable )
		{
			this.edgeItem.setEnabled( enable );
		}
		
		public void setVertexMenuEnabled( boolean enable )
		{
			this.vertexItem.setEnabled( enable );
		}
	}
	
	private class ViewportPrinter implements Printable
	{
		public void print( ) throws PrinterException
		{
			PrinterJob printJob = PrinterJob.getPrinterJob( );
			printJob.setPrintable( this );
			
			if( printJob.printDialog( ) )
				printJob.print( );
		}
		
		@Override
		public int print( Graphics g, PageFormat pageFormat, int pageIndex )
		{
			if( pageIndex > 0 )
				return ( NO_SUCH_PAGE );
			else
			{
				Graphics2D g2d = (Graphics2D) g;
				
				double widthRatio = ( GraphDisplayController.this.viewport.getWidth( ) + UserSettings.instance.zoomGraphPadding.get( ) ) / pageFormat.getImageableWidth( );
				double heightRatio = ( GraphDisplayController.this.viewport.getHeight( ) + UserSettings.instance.zoomGraphPadding.get( ) ) / pageFormat.getImageableHeight( );
				double maxRatio = 1.0 / Math.max( widthRatio, heightRatio );
				
				g2d.scale( maxRatio, maxRatio );
				g2d.translate( pageFormat.getImageableX( ) + UserSettings.instance.zoomGraphPadding.get( ) / 4.0, pageFormat.getImageableY( ) + UserSettings.instance.zoomGraphPadding.get( ) / 4.0 );
				
				RepaintManager.currentManager( GraphDisplayController.this.viewport ).setDoubleBufferingEnabled( false );
				GraphDisplayController.this.viewport.paint( g2d );
				RepaintManager.currentManager( GraphDisplayController.this.viewport ).setDoubleBufferingEnabled( true );
				
				return ( PAGE_EXISTS );
			}
		}
	}
	
	private class ViewToolBar extends JToolBar
	{
		private final JButton	showVertexLabelsButton;
		private final JButton	showVertexWeightsButton;
		private final JButton	showEdgeHandlesButton;
		private final JButton	showEdgeLabelsButton;
		private final JButton	showEdgeWeightsButton;
		private final JButton	showCaptionHandlesButton;
		private final JButton	showCaptionEditorsButton;
		
		public ViewToolBar( )
		{
			this.showVertexLabelsButton = new JButton( ImageIconBundle.get( "show_vertex_labels_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.settings.showVertexLabels.set( !GraphDisplayController.this.settings.showVertexLabels.get( ) );
						}
					} );
					this.setToolTipText( StringBundle.get( "show_vertex_labels_button_tooltip" ) );
				}
			};
			this.add( this.showVertexLabelsButton );
			
			this.showVertexWeightsButton = new JButton( ImageIconBundle.get( "show_vertex_weights_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.settings.showVertexWeights.set( !GraphDisplayController.this.settings.showVertexWeights.get( ) );
						}
					} );
					this.setToolTipText( StringBundle.get( "show_vertex_weights_button_tooltip" ) );
				}
			};
			this.add( this.showVertexWeightsButton );
			
			this.showEdgeHandlesButton = new JButton( ImageIconBundle.get( "show_edge_handles_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.settings.showEdgeHandles.set( !GraphDisplayController.this.settings.showEdgeHandles.get( ) );
						}
					} );
					this.setToolTipText( StringBundle.get( "show_edge_handles_button_tooltip" ) );
				}
			};
			this.add( this.showEdgeHandlesButton );
			
			this.showEdgeLabelsButton = new JButton( ImageIconBundle.get( "show_edge_labels_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.settings.showEdgeLabels.set( !GraphDisplayController.this.settings.showEdgeLabels.get( ) );
						}
					} );
					this.setToolTipText( StringBundle.get( "show_edge_labels_button_tooltip" ) );
				}
			};
			this.add( this.showEdgeLabelsButton );
			
			this.showEdgeWeightsButton = new JButton( ImageIconBundle.get( "show_edge_weights_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.settings.showEdgeWeights.set( !GraphDisplayController.this.settings.showEdgeWeights.get( ) );
						}
					} );
					this.setToolTipText( StringBundle.get( "show_edge_weights_button_tooltip" ) );
				}
			};
			this.add( this.showEdgeWeightsButton );
			
			this.showCaptionHandlesButton = new JButton( ImageIconBundle.get( "show_caption_handles_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.settings.showCaptionHandles.set( !GraphDisplayController.this.settings.showCaptionHandles.get( ) );
						}
					} );
					this.setToolTipText( StringBundle.get( "show_caption_handles_button_tooltip" ) );
				}
			};
			this.add( this.showCaptionHandlesButton );
			
			this.showCaptionEditorsButton = new JButton( ImageIconBundle.get( "show_caption_editors_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.settings.showCaptionEditors.set( !GraphDisplayController.this.settings.showCaptionEditors.get( ) );
						}
					} );
					this.setToolTipText( StringBundle.get( "show_caption_editors_button_tooltip" ) );
				}
			};
			this.add( this.showCaptionEditorsButton );
			
			this.refresh( );
		}
		
		public void refresh( )
		{
			if( this.showVertexLabelsButton != null && this.showVertexLabelsButton.isSelected( ) != GraphDisplayController.this.settings.showVertexLabels.get( ) )
				this.showVertexLabelsButton.setSelected( GraphDisplayController.this.settings.showVertexLabels.get( ) );
			
			if( this.showVertexWeightsButton != null && this.showVertexWeightsButton.isSelected( ) != GraphDisplayController.this.settings.showVertexWeights.get( ) )
				this.showVertexWeightsButton.setSelected( GraphDisplayController.this.settings.showVertexWeights.get( ) );
			
			if( this.showEdgeHandlesButton != null && this.showEdgeHandlesButton.isSelected( ) != GraphDisplayController.this.settings.showEdgeHandles.get( ) )
				this.showEdgeHandlesButton.setSelected( GraphDisplayController.this.settings.showEdgeHandles.get( ) );
			
			if( this.showEdgeLabelsButton != null && this.showEdgeLabelsButton.isSelected( ) != GraphDisplayController.this.settings.showEdgeLabels.get( ) )
				this.showEdgeLabelsButton.setSelected( GraphDisplayController.this.settings.showEdgeLabels.get( ) );
			
			if( this.showEdgeWeightsButton != null && this.showEdgeWeightsButton.isSelected( ) != GraphDisplayController.this.settings.showEdgeWeights.get( ) )
				this.showEdgeWeightsButton.setSelected( GraphDisplayController.this.settings.showEdgeWeights.get( ) );
			
			if( this.showCaptionHandlesButton != null && this.showCaptionHandlesButton.isSelected( ) != GraphDisplayController.this.settings.showCaptionHandles.get( ) )
				this.showCaptionHandlesButton.setSelected( GraphDisplayController.this.settings.showCaptionHandles.get( ) );
			
			if( this.showCaptionEditorsButton != null && this.showCaptionEditorsButton.isSelected( ) != GraphDisplayController.this.settings.showCaptionEditors.get( ) )
				this.showCaptionEditorsButton.setSelected( GraphDisplayController.this.settings.showCaptionEditors.get( ) );
		}
	}
	
	private class ZoomToolBar extends JToolBar
	{
		private final JButton	zoomGraphButton;
		private final JButton	zoomOneToOneButton;
		private final JButton	zoomInButton;
		private final JButton	zoomOutButton;
		
		public ZoomToolBar( )
		{
			this.zoomGraphButton = new JButton( ImageIconBundle.get( "zoom_graph_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.zoomFit( );
						}
					} );
					this.setToolTipText( StringBundle.get( "zoom_graph_button_tooltip" ) );
				}
			};
			this.add( this.zoomGraphButton );
			
			this.zoomOneToOneButton = new JButton( ImageIconBundle.get( "zoom_one_to_one_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.zoomOneToOne( );
						}
					} );
					this.setToolTipText( StringBundle.get( "zoom_one_to_one_button_tooltip" ) );
				}
			};
			this.add( this.zoomOneToOneButton );
			
			this.zoomInButton = new JButton( ImageIconBundle.get( "zoom_in_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							Point2D.Double viewportCenter = new Point2D.Double( GraphDisplayController.this.viewport.getWidth( ) / 2.0, GraphDisplayController.this.viewport.getHeight( ) / 2.0 );
							Point2D.Double zoomCenter = new Point2D.Double( );
							try
							{
								GraphDisplayController.this.transform.inverseTransform( viewportCenter, zoomCenter );
							}
							catch( NoninvertibleTransformException ex )
							{
								DebugUtilities.logException( "An exception occurred while inverting transformation.", ex );
							}
							
							GraphDisplayController.this.zoomCenter( zoomCenter, UserSettings.instance.zoomInFactor.get( ) );
						}
					} );
					this.setToolTipText( StringBundle.get( "zoom_in_button_tooltip" ) );
				}
			};
			this.add( this.zoomInButton );
			
			this.zoomOutButton = new JButton( ImageIconBundle.get( "zoom_out_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							Point2D.Double viewportCenter = new Point2D.Double( GraphDisplayController.this.viewport.getWidth( ) / 2.0, GraphDisplayController.this.viewport.getHeight( ) / 2.0 );
							Point2D.Double zoomCenter = new Point2D.Double( );
							try
							{
								GraphDisplayController.this.transform.inverseTransform( viewportCenter, zoomCenter );
							}
							catch( NoninvertibleTransformException ex )
							{
								DebugUtilities.logException( "An exception occurred while inverting transformation.", ex );
							}
							
							GraphDisplayController.this.zoomCenter( zoomCenter, UserSettings.instance.zoomOutFactor.get( ) );
						}
					} );
					this.setToolTipText( StringBundle.get( "zoom_out_button_tooltip" ) );
				}
			};
			this.add( this.zoomOutButton );
		}
	}
	
	private Graph					graph;
	public final GraphSettings		settings;
	private JPanel					toolToolBarPanel;
	
	private ToolToolBar				toolToolBar;
	private JPanel					nonToolToolbarPanel;
	private ArrangeToolBar			arrangeToolBar;
	private ViewToolBar				viewToolBar;
	private ZoomToolBar				zoomToolBar;
	private FunctionToolBar			functionToolBar;
	private JPanel					viewportPanel;
	private JComponent				viewport;
	private ViewportPopupMenu		viewportPopupMenu;
	private JPanel					statusBar;
	private Map<Function, JLabel>	selectedFunctionLabels;
	private Tool					tool;
	private int						paintColor;
	private boolean					isMouseDownOnCanvas;
	private boolean					isMouseDownOnPaintToolButton;
	private boolean					pointerToolClickedObject;
	private boolean					cutToolClickedObject;
	private boolean					paintToolClickedObject;
	private boolean					isMouseOverViewport;
	private Point					currentMousePoint;
	private Point					pastMousePoint;
	private Point					pastPanPoint;
	
	private Vertex					fromVertex;
	private final AffineTransform	transform;
	private Set<Function>			functionsToBeRun;
	private final SnapshotList		undoHistory;
	private Timer					undoTimer;
	private Timer					panTimer;
	private final EventListenerList	graphChangeListenerList;
	private boolean					isViewportInvalidated;
	private final Timer				viewportValidationTimer;
	private double					arrangeWebSpeed;
	
	public GraphDisplayController( Graph graph )
	{
		// Initialize the list of GraphChangeEvent listeners
		this.graphChangeListenerList = new EventListenerList( );
		
		// Add/bind graph
		this.setGraph( graph );
		this.undoHistory = new SnapshotList( graph.toString( ) );
		
		// Add/bind palette
		UserSettings.instance.addObserver( new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				GraphDisplayController.this.onSettingChanged( arg );
			}
		} );
		
		// Add/bind display settings
		this.settings = new GraphSettings( )
		{
			{
				this.addObserver( new Observer( )
				{
					@Override
					public void update( Observable o, Object arg )
					{
						GraphDisplayController.this.onSettingChanged( arg );
					}
				} );
			}
		};
		
		// Initialize the toolbar, buttons, and viewport
		this.initializeComponents( );
		
		// Initialize the viewport's affine transform
		this.transform = new AffineTransform( );
		
		// Initialize the viewport's frame delimiter
		this.isViewportInvalidated = true;
		this.viewportValidationTimer = new Timer( 30, new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( GraphDisplayController.this.isViewportInvalidated )
					GraphDisplayController.this.repaint( );
				
				GraphDisplayController.this.isViewportInvalidated = false;
			}
		} );
		this.viewportValidationTimer.start( );
	}
	
	public void addGraphChangeListener( GraphChangeEventListener listener )
	{
		this.graphChangeListenerList.add( GraphChangeEventListener.class, listener );
	}
	
	public void copy( )
	{
		// Make a copy of graph containing only selected elements
		Graph copy = new Graph( "copy", this.graph.areLoopsAllowed, this.graph.areDirectedEdgesAllowed, this.graph.areMultipleEdgesAllowed, this.graph.areCyclesAllowed );
		
		for( Vertex vertex : this.graph.getSelectedVertices( ) )
			copy.vertices.add( vertex );
		
		for( Edge edge : this.graph.getSelectedEdges( ) )
			if( edge.from.isSelected.get( ) && edge.to.isSelected.get( ) )
				copy.edges.add( edge );
		
		for( Caption caption : this.graph.getSelectedCaptions( ) )
			copy.captions.add( caption );
		
		// Send the JSON to the clipboard
		StringSelection stringSelection = new StringSelection( copy.toString( ) );
		Clipboard clipboard = Toolkit.getDefaultToolkit( ).getSystemClipboard( );
		clipboard.setContents( stringSelection, new ClipboardOwner( )
		{
			@Override
			public void lostOwnership( Clipboard c, Transferable t )
			{
				// Ignore?
			}
		} );
	}
	
	public void cut( )
	{
		this.copy( );
		
		this.graph.vertices.removeAll( this.graph.getSelectedVertices( ) );
		this.graph.edges.removeAll( this.graph.getSelectedEdges( ) );
		this.graph.captions.removeAll( this.graph.getSelectedCaptions( ) );
	}
	
	public void dispose( )
	{
		this.undoTimer.stop( );
	}
	
	private void fireGraphChangeEvent( GraphChangeEvent event )
	{
		Object[ ] listeners = this.graphChangeListenerList.getListenerList( );
		// Each listener occupies two elements - the first is the listener class and the second is the listener instance
		for( int i = 0; i < listeners.length; i += 2 )
			if( listeners[i] == GraphChangeEventListener.class )
				( (GraphChangeEventListener) listeners[i + 1] ).graphChangeEventOccurred( event );
	}
	
	public Graph getGraph( )
	{
		return this.graph;
	}
	
	public RenderedImage getImage( )
	{
		int width = this.viewport.getWidth( ), height = this.viewport.getHeight( );
		
		// Create a buffered image on which to draw
		BufferedImage bufferedImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
		
		// Create a graphics contents on the buffered image
		Graphics g = bufferedImage.createGraphics( );
		
		// Draw graphics
		this.viewport.paint( g );
		
		// Graphics context no longer needed so dispose it
		g.dispose( );
		
		return bufferedImage;
	}
	
	public Rectangle getSelectionRectangle( )
	{
		return new Rectangle( )
		{
			{
				this.x = Math.min( GraphDisplayController.this.pastMousePoint.x, GraphDisplayController.this.currentMousePoint.x );
				this.y = Math.min( GraphDisplayController.this.pastMousePoint.y, GraphDisplayController.this.currentMousePoint.y );
				this.width = Math.abs( GraphDisplayController.this.pastMousePoint.x - GraphDisplayController.this.currentMousePoint.x );
				this.height = Math.abs( GraphDisplayController.this.pastMousePoint.y - GraphDisplayController.this.currentMousePoint.y );
			}
		};
	}
	
	public void initializeComponents( )
	{
		this.setLayout( new BorderLayout( ) );
		this.setBackground( UserSettings.instance.graphBackground.get( ) );
		this.setOpaque( true );
		
		this.toolToolBarPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) )
		{
			{
				this.setMaximumSize( new Dimension( Integer.MAX_VALUE, 24 ) );
			}
		};
		this.add( this.toolToolBarPanel, BorderLayout.WEST );
		
		this.toolToolBar = new ToolToolBar( );
		this.toolToolBarPanel.add( this.toolToolBar );
		
		this.nonToolToolbarPanel = new JPanel( new WrapLayout( FlowLayout.LEFT ) )
		{
			{
				this.setMaximumSize( new Dimension( Integer.MAX_VALUE, 32 ) );
			}
		};
		this.add( this.nonToolToolbarPanel, BorderLayout.NORTH );
		
		this.arrangeToolBar = new ArrangeToolBar( );
		this.nonToolToolbarPanel.add( this.arrangeToolBar );
		
		this.viewToolBar = new ViewToolBar( );
		this.nonToolToolbarPanel.add( this.viewToolBar );
		
		this.zoomToolBar = new ZoomToolBar( );
		this.nonToolToolbarPanel.add( this.zoomToolBar );
		
		this.functionToolBar = new FunctionToolBar( );
		this.nonToolToolbarPanel.add( this.functionToolBar );
		
		this.selectedFunctionLabels = new HashMap<Function, JLabel>( );
		this.functionsToBeRun = new TreeSet<Function>( );
		
		this.viewportPanel = new JPanel( new BorderLayout( ) )
		{
			{
				this.setBorder( new BevelBorder( BevelBorder.LOWERED ) );
			}
		};
		this.add( this.viewportPanel, BorderLayout.CENTER );
		
		this.viewport = new JComponent( )
		{
			@Override
			public void paintComponent( Graphics g )
			{
				GraphDisplayController.this.paintViewport( (Graphics2D) g );
			}
		};
		this.viewport.addMouseListener( new MouseAdapter( )
		{
			@Override
			public void mouseEntered( MouseEvent event )
			{
				GraphDisplayController.this.isMouseOverViewport = true;
			}
			
			@Override
			public void mouseExited( MouseEvent event )
			{
				GraphDisplayController.this.isMouseOverViewport = false;
			}
			
			@Override
			public void mousePressed( MouseEvent event )
			{
				try
				{
					GraphDisplayController.this.viewportMousePressed( event );
				}
				catch( NoninvertibleTransformException e )
				{
					DebugUtilities.logException( "An exception occurred while inverting transformation.", e );
				}
				
				if( event.getClickCount( ) > 1 )
					try
					{
						GraphDisplayController.this.viewportMouseDoubleClicked( event );
					}
					catch( NoninvertibleTransformException e )
					{
						DebugUtilities.logException( "An exception occurred while inverting transformation.", e );
					}
			}
			
			@Override
			public void mouseReleased( MouseEvent event )
			{
				try
				{
					GraphDisplayController.this.viewportMouseReleased( event );
				}
				catch( NoninvertibleTransformException e )
				{
					DebugUtilities.logException( "An exception occurred while inverting transformation.", e );
				}
			}
		} );
		this.viewport.addMouseMotionListener( new MouseMotionAdapter( )
		{
			@Override
			public void mouseDragged( MouseEvent event )
			{
				try
				{
					GraphDisplayController.this.viewportMouseDragged( event );
				}
				catch( NoninvertibleTransformException e )
				{
					DebugUtilities.logException( "An exception occurred while inverting transformation.", e );
				}
			}
			
			@Override
			public void mouseMoved( MouseEvent event )
			{
				try
				{
					GraphDisplayController.this.transform.inverseTransform( event.getPoint( ), GraphDisplayController.this.currentMousePoint );
				}
				catch( NoninvertibleTransformException e )
				{
					DebugUtilities.logException( "An exception occurred while inverting transformation.", e );
				}
			}
		} );
		this.viewport.addMouseWheelListener( new MouseWheelListener( )
		{
			@Override
			public void mouseWheelMoved( MouseWheelEvent e )
			{
				GraphDisplayController.this.zoomCenter( new Point2D.Double( GraphDisplayController.this.currentMousePoint.x, GraphDisplayController.this.currentMousePoint.y ), 1 - e.getWheelRotation( ) * UserSettings.instance.scrollIncrementZoom.get( ) );
			}
		} );
		this.viewport.addKeyListener( new KeyListener( )
		{
			@Override
			public void keyPressed( KeyEvent e )
			{
				GraphDisplayController.this.viewportKeyPressed( e );
			}
			
			@Override
			public void keyReleased( KeyEvent e )
			{
				// Do nothing
			}
			
			@Override
			public void keyTyped( KeyEvent e )
			{
				// Do nothing
			}
		} );
		this.viewportPanel.add( this.viewport, BorderLayout.CENTER );
		this.viewportPopupMenu = new ViewportPopupMenu( );
		
		this.statusBar = new JPanel( new FlowLayout( FlowLayout.LEFT ) )
		{
			{
				this.setMaximumSize( new Dimension( Integer.MAX_VALUE, 12 ) );
			}
		};
		this.add( this.statusBar, BorderLayout.SOUTH );
		
		this.undoTimer = new Timer( UserSettings.instance.undoLoggingInterval.get( ), new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( GraphDisplayController.this.undoHistory.getCapacity( ) > 0 )
					GraphDisplayController.this.undoHistory.add( GraphDisplayController.this.graph.toString( ) );
			}
		} );
		this.undoTimer.start( );
	}
	
	public void onGraphChanged( Object source )
	{
		this.isViewportInvalidated = true;
		this.fireGraphChangeEvent( new GraphChangeEvent( this.graph ) );
	}
	
	public void onSettingChanged( Object source )
	{
		this.isViewportInvalidated = true;
		
		if( this.toolToolBar != null )
			this.toolToolBar.refreshPaintMenu( );
		
		if( this.viewToolBar != null )
			this.viewToolBar.refresh( );
		
		if( this.undoTimer != null )
			this.undoTimer.setDelay( UserSettings.instance.undoLoggingInterval.get( ) );
		
		if( this.undoHistory != null && this.undoHistory.getCapacity( ) != UserSettings.instance.undoLoggingMaximum.get( ) )
		{
			this.undoHistory.setCapacity( UserSettings.instance.undoLoggingMaximum.get( ) );
			this.undoHistory.clear( );
		}
	}
	
	public void paintSelectionRectangle( Graphics2D g2D )
	{
		Rectangle selection = this.getSelectionRectangle( );
		
		g2D.setColor( UserSettings.instance.selectionBoxFill.get( ) );
		g2D.fillRect( selection.x, selection.y, selection.width, selection.height );
		
		g2D.setColor( UserSettings.instance.selectionBoxLine.get( ) );
		g2D.drawRect( selection.x, selection.y, selection.width, selection.height );
	}
	
	public void paintViewport( Graphics2D g2D )
	{
		// Apply rendering settings
		if( UserSettings.instance.useAntiAliasing.get( ) )
			g2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		
		if( UserSettings.instance.usePureStroke.get( ) )
			g2D.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
		
		if( UserSettings.instance.useBicubicInterpolation.get( ) )
			g2D.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
		
		if( UserSettings.instance.useFractionalMetrics.get( ) )
			g2D.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON );
		
		// Apply any one-time functions
		if( !this.functionsToBeRun.isEmpty( ) )
		{
			// For some reason we have to operate on a copy of the set so the message box doesn't lose focus...
			Set<Function> functionsToBeRunCopy = new TreeSet<Function>( );
			functionsToBeRunCopy.addAll( this.functionsToBeRun );
			this.functionsToBeRun.clear( );
			
			// See Issue #90 (Run-once functions lock up on XP and macs)
			String os = System.getProperty( "os.name" );
			boolean showExternally = os.startsWith( "Mac" ) || os.equals( "Windows NT" ) || os.equals( "Windows XP" );
			
			for( Function function : functionsToBeRunCopy )
			{
				String result = function.evaluate( g2D, this.graph, this );
				if( result != null && !result.isEmpty( ) )
					if( showExternally )
						JOptionPane.showMessageDialog( this.viewport, function + ": " + result, GlobalSettings.applicationName, JOptionPane.OK_OPTION + JOptionPane.INFORMATION_MESSAGE );
					else
						JOptionPane.showInternalMessageDialog( this.viewport, function + ": " + result, GlobalSettings.applicationName, JOptionPane.OK_OPTION + JOptionPane.INFORMATION_MESSAGE );
			}
		}
		
		// Clear everything
		super.paintComponent( g2D );
		
		// Apply the transformation
		AffineTransform original = g2D.getTransform( );
		original.concatenate( this.transform );
		g2D.setTransform( original );
		
		// Paint the graph
		GraphDisplayView.paint( g2D, this.graph, this.settings );
		
		// Apply any selected functions
		for( Entry<Function, JLabel> entry : this.selectedFunctionLabels.entrySet( ) )
			entry.getValue( ).setText( entry.getKey( ) + ": " + entry.getKey( ).evaluate( g2D, this.graph, this ) );
		
		// Paint controller-specific stuff
		if( this.isMouseDownOnCanvas )
			switch( this.tool )
			{
				case POINTER_TOOL:
					if( !this.pointerToolClickedObject )
						this.paintSelectionRectangle( g2D );
					break;
				case CUT_TOOL:
					if( !this.cutToolClickedObject )
						this.paintSelectionRectangle( g2D );
					break;
				case GRAPH_TOOL:
					// For the edge tool we might need to paint the temporary drag-edge
					if( this.fromVertex != null )
					{
						g2D.setColor( UserSettings.instance.draggingEdge.get( ) );
						g2D.drawLine( this.fromVertex.x.get( ).intValue( ), this.fromVertex.y.get( ).intValue( ), this.currentMousePoint.x, this.currentMousePoint.y );
					}
					
					break;
				case PAINT_TOOL:
					if( !this.paintToolClickedObject )
						this.paintSelectionRectangle( g2D );
					break;
			}
	}
	
	public void paste( )
	{
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit( ).getSystemClipboard( );
		
		Transferable contents = clipboard.getContents( null );
		boolean hasTransferableText = ( contents != null ) && contents.isDataFlavorSupported( DataFlavor.stringFlavor );
		
		if( hasTransferableText )
			try
			{
				result = (String) contents.getTransferData( DataFlavor.stringFlavor );
				
				Graph pasted = new Graph( result );
				
				// Find the centroid
				double elementCount = 0.0;
				Point2D.Double centroid = new Point2D.Double( 0.0, 0.0 );
				
				for( Vertex vertex : pasted.vertices )
				{
					centroid.x += vertex.x.get( );
					centroid.y += vertex.y.get( );
					++elementCount;
				}
				
				for( Edge edge : pasted.edges )
				{
					centroid.x += edge.handleX.get( );
					centroid.y += edge.handleY.get( );
					++elementCount;
				}
				
				for( Caption caption : pasted.captions )
				{
					centroid.x += caption.x.get( );
					centroid.y += caption.y.get( );
					++elementCount;
				}
				
				centroid.x /= elementCount;
				centroid.y /= elementCount;
				
				// Center everything around the mouse or in the center of the viewport
				Point2D.Double pastePoint = new Point2D.Double( );
				
				if( this.isMouseOverViewport )
					pastePoint = new Point2D.Double( this.currentMousePoint.x, this.currentMousePoint.y );
				else
					this.transform.inverseTransform( new Point2D.Double( this.viewport.getWidth( ) / 2.0, this.viewport.getHeight( ) / 2.0 ), pastePoint );
				
				pasted.suspendNotifications( true );
				
				for( Edge edge : pasted.edges )
					edge.suspendNotifications( true );
				
				for( Vertex vertex : pasted.vertices )
				{
					vertex.x.set( vertex.x.get( ) - centroid.x + pastePoint.x );
					vertex.y.set( vertex.y.get( ) - centroid.y + pastePoint.y );
				}
				
				for( Edge edge : pasted.edges )
				{
					edge.handleX.set( edge.handleX.get( ) - centroid.x + pastePoint.x );
					edge.handleY.set( edge.handleY.get( ) - centroid.y + pastePoint.y );
					edge.suspendNotifications( false );
				}
				
				for( Caption caption : pasted.captions )
				{
					caption.x.set( caption.x.get( ) - centroid.x + pastePoint.x );
					caption.y.set( caption.y.get( ) - centroid.y + pastePoint.y );
				}
				
				pasted.suspendNotifications( false );
				
				this.graph.selectAll( false );
				this.graph.union( pasted );
			}
			catch( Exception ex )
			{
				DebugUtilities.logException( "An exception occurred while painting the viewport.", ex );
			}
	}
	
	public void printGraph( ) throws PrinterException
	{
		new ViewportPrinter( ).print( );
	}
	
	public void redo( )
	{
		if( this.undoHistory.next( ) != null )
			this.setGraph( new Graph( this.undoHistory.current( ) ) );
	}
	
	public void removeGraphChangeListener( GraphChangeEventListener listener )
	{
		this.graphChangeListenerList.remove( GraphChangeEventListener.class, listener );
	}
	
	public void selectAll( )
	{
		this.graph.selectAll( true );
	}
	
	public void selectAllEdges( )
	{
		for( Edge edge : this.graph.edges )
			edge.isSelected.set( true );
	}
	
	public void selectAllVertices( )
	{
		for( Vertex vertex : this.graph.vertices )
			vertex.isSelected.set( true );
	}
	
	public void setGraph( Graph graph )
	{
		this.graph = graph;
		graph.addObserver( new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				GraphDisplayController.this.onGraphChanged( arg );
			}
		} );
		
		this.isMouseDownOnCanvas = false;
		this.currentMousePoint = new Point( 0, 0 );
		this.pastMousePoint = new Point( 0, 0 );
		this.pastPanPoint = new Point( 0, 0 );
		this.pointerToolClickedObject = false;
		this.cutToolClickedObject = false;
		this.paintToolClickedObject = false;
		this.fromVertex = null;
		
		this.isViewportInvalidated = true;
	}
	
	public void setTool( Tool tool )
	{
		this.tool = tool;
		if( this.toolToolBar != null )
			this.toolToolBar.refresh( );
		
		Cursor cursor;
		
		switch( this.tool )
		{
			case POINTER_TOOL:
				cursor = CursorBundle.get( "pointer_tool_cursor" );
				break;
			case GRAPH_TOOL:
				cursor = CursorBundle.get( "graph_tool_cursor" );
				break;
			case CUT_TOOL:
				cursor = CursorBundle.get( "cut_tool_cursor" );
				break;
			case CAPTION_TOOL:
				cursor = CursorBundle.get( "caption_tool_cursor" );
				break;
			case PAINT_TOOL:
				cursor = CursorBundle.get( "paint_tool_cursor" );
				break;
			default:
				cursor = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
				break;
		}
		
		this.setCursor( cursor );
		
		this.fromVertex = null;
		this.graph.selectAll( false );
	}
	
	public void undo( )
	{
		if( this.undoHistory.previous( ) != null )
			this.setGraph( new Graph( this.undoHistory.current( ) ) );
	}
	
	private void viewportKeyPressed( KeyEvent event )
	{
		switch( event.getKeyCode( ) )
		{
			case KeyEvent.VK_BACK_SPACE: // Fall through...
			case KeyEvent.VK_DELETE:
				if( this.tool == Tool.POINTER_TOOL )
				{
					this.graph.vertices.removeAll( this.graph.getSelectedVertices( ) );
					this.graph.edges.removeAll( this.graph.getSelectedEdges( ) );
					this.graph.captions.removeAll( this.graph.getSelectedCaptions( ) );
				}
				
				break;
			case KeyEvent.VK_ESCAPE:
				this.graph.selectAll( false );
				this.fromVertex = null;
				if( this.panTimer != null )
					this.panTimer.stop( );
				
				break;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_LEFT:
				double increment = UserSettings.instance.arrowKeyIncrement.get( );
				
				if( event.isAltDown( ) )
					increment /= 10.0;
				if( event.isShiftDown( ) )
					increment *= 10.0;
				
				if( ( event.getModifiers( ) & Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) != 0 )
				{
					switch( event.getKeyCode( ) )
					{
						case KeyEvent.VK_UP:
							this.transform.translate( 0.0, increment );
							break;
						case KeyEvent.VK_RIGHT:
							this.transform.translate( -increment, 0.0 );
							break;
						case KeyEvent.VK_DOWN:
							this.transform.translate( 0.0, -increment );
							break;
						case KeyEvent.VK_LEFT:
							this.transform.translate( increment, 0.0 );
							break;
					}
					
					this.isViewportInvalidated = true;
				}
				else
					switch( event.getKeyCode( ) )
					{
						case KeyEvent.VK_UP:
							this.graph.translateSelected( 0.0, -increment );
							break;
						case KeyEvent.VK_RIGHT:
							this.graph.translateSelected( increment, 0.0 );
							break;
						case KeyEvent.VK_DOWN:
							this.graph.translateSelected( 0.0, increment );
							break;
						case KeyEvent.VK_LEFT:
							this.graph.translateSelected( -increment, 0.0 );
							break;
					}
				
				for( Edge edge : this.graph.getSelectedEdges( ) )
					if( edge.isLinear( ) && !edge.isLoop )
						edge.reset( );
				
				break;
		}
	}
	
	private void viewportMouseDoubleClicked( MouseEvent event ) throws NoninvertibleTransformException
	{
		if( !this.viewport.hasFocus( ) )
			this.viewport.requestFocus( );
		
		this.transform.inverseTransform( event.getPoint( ), this.pastPanPoint );
		
		if( UserSettings.instance.panOnDoubleClick.get( ) )
		{
			if( this.panTimer != null )
				this.panTimer.stop( );
			
			this.panTimer = new Timer( 50, new ActionListener( )
			{
				@Override
				public void actionPerformed( ActionEvent e )
				{
					Timer timer = (Timer) e.getSource( );
					
					try
					{
						Point2D focusPoint = new Point2D.Double( );
						GraphDisplayController.this.transform.inverseTransform( new Point( GraphDisplayController.this.viewport.getWidth( ) / 2, GraphDisplayController.this.viewport.getHeight( ) / 2 ), focusPoint );
						
						double xDelta = Math.round( ( GraphDisplayController.this.pastMousePoint.x - focusPoint.getX( ) ) / UserSettings.instance.panDecelerationFactor.get( ) );
						double yDelta = Math.round( ( GraphDisplayController.this.pastMousePoint.y - focusPoint.getY( ) ) / UserSettings.instance.panDecelerationFactor.get( ) );
						
						GraphDisplayController.this.transform.translate( xDelta, yDelta );
						GraphDisplayController.this.isViewportInvalidated = true;
						
						if( xDelta == 0 && yDelta == 0 )
							timer.stop( );
					}
					catch( Exception ex )
					{
						DebugUtilities.logException( "An exception occurred while panning viewport.", ex );
						timer.stop( );
					}
				}
			} );
			this.panTimer.start( );
		}
	}
	
	private void viewportMouseDragged( MouseEvent event ) throws NoninvertibleTransformException
	{
		Point oldPoint = new Point( this.currentMousePoint );
		this.transform.inverseTransform( event.getPoint( ), this.currentMousePoint );
		
		if( this.tool == Tool.POINTER_TOOL && this.pointerToolClickedObject )
			this.graph.translateSelected( this.currentMousePoint.getX( ) - oldPoint.x, this.currentMousePoint.getY( ) - oldPoint.y );
		
		this.isViewportInvalidated = true;
	}
	
	private void viewportMousePressed( MouseEvent event ) throws NoninvertibleTransformException
	{
		if( !this.viewport.hasFocus( ) )
			this.viewport.requestFocus( );
		
		int modifiers = event.getModifiersEx( );
		this.transform.inverseTransform( event.getPoint( ), this.pastMousePoint );
		this.transform.inverseTransform( event.getPoint( ), this.currentMousePoint );
		
		switch( this.tool )
		{
			case POINTER_TOOL:
				boolean isShiftDown = ( ( modifiers & InputEvent.SHIFT_DOWN_MASK ) == InputEvent.SHIFT_DOWN_MASK );
				this.pointerToolClickedObject = false;
				
				for( int i = this.graph.vertices.size( ) - 1; i >= 0; --i )
				{
					Vertex vertex = this.graph.vertices.get( i );
					if( VertexDisplayView.wasClicked( vertex, this.currentMousePoint, this.transform.getScaleX( ) ) )
					{
						if( !vertex.isSelected.get( ) && !isShiftDown )
							this.graph.selectAll( false );
						
						vertex.isSelected.set( true );
						this.pointerToolClickedObject = true;
						break;
					}
				}
				
				if( !this.pointerToolClickedObject )
					for( int i = this.graph.edges.size( ) - 1; i >= 0; --i )
					{
						Edge edge = this.graph.edges.get( i );
						if( EdgeDisplayView.wasClicked( edge, this.currentMousePoint, this.transform.getScaleX( ) ) )
						{
							if( !edge.isSelected.get( ) && !isShiftDown )
								this.graph.selectAll( false );
							
							edge.isSelected.set( true );
							this.pointerToolClickedObject = true;
							break;
						}
					}
				
				if( !this.pointerToolClickedObject )
					for( int i = this.graph.captions.size( ) - 1; i >= 0; --i )
					{
						Caption caption = this.graph.captions.get( i );
						if( CaptionDisplayView.wasHandleClicked( caption, this.currentMousePoint, this.transform.getScaleX( ) ) )
						{
							if( !caption.isSelected.get( ) && !isShiftDown )
								this.graph.selectAll( false );
							
							caption.isSelected.set( true );
							this.pointerToolClickedObject = true;
							break;
						}
					}
				
				if( !this.pointerToolClickedObject )
					for( int i = this.graph.captions.size( ) - 1; i >= 0; --i )
					{
						Caption caption = this.graph.captions.get( i );
						if( CaptionDisplayView.wasEditorClicked( caption, this.currentMousePoint, this.transform.getScaleX( ) ) )
						{
							if( !caption.isSelected.get( ) && !isShiftDown )
								this.graph.selectAll( false );
							
							EditCaptionDialog.Value newValue = EditCaptionDialog.showDialog( this, caption.text.get( ), caption.size.get( ) );
							if( newValue != null )
							{
								caption.text.set( newValue.getText( ) );
								caption.size.set( newValue.getSize( ) );
							}
							
							this.pointerToolClickedObject = true;
							break;
						}
					}
				
				if( !this.pointerToolClickedObject && !isShiftDown )
					this.graph.selectAll( false );
				
				break;
			case GRAPH_TOOL:
				if( event.getButton( ) == MouseEvent.BUTTON1 )
				{
					// The procedure for adding an edge using the edge tool is to click a vertex the edge will come from and subsequently a vertex
					// the edge will go to
					boolean fromVertexClicked = false;
					boolean toVertexClicked = false;
					
					for( Vertex vertex : this.graph.vertices )
						if( VertexDisplayView.wasClicked( vertex, this.currentMousePoint, this.transform.getScaleX( ) ) )
							if( this.fromVertex == null )
							{
								// If the user has not yet defined a from Vertex, make this one so
								vertex.isSelected.set( true );
								this.fromVertex = vertex;
								fromVertexClicked = true;
								break;
							}
							else
							{
								// If the user has already defined a from Vertex, try to add an edge between it and this one
								
								if( this.graph.edges.add( new Edge( this.graph.areDirectedEdgesAllowed, this.fromVertex, vertex ) ) )
								{
									this.fromVertex.isSelected.set( false );
									this.fromVertex = !UserSettings.instance.deselectVertexWithNewEdge.get( ) ? vertex : null;
									if( this.fromVertex != null )
										this.fromVertex.isSelected.set( true );
									toVertexClicked = true;
								}
								else
									Toolkit.getDefaultToolkit( ).beep( );
								
								if( !toVertexClicked )
								{
									this.fromVertex.isSelected.set( false );
									this.fromVertex = null;
									fromVertexClicked = true;
								}
							}
					
					if( !fromVertexClicked && !toVertexClicked )
						this.graph.vertices.add( new Vertex( this.currentMousePoint.x, this.currentMousePoint.y ) );
				}
				
				break;
			case CAPTION_TOOL:
				if( event.getButton( ) == MouseEvent.BUTTON1 )
				{
					Caption caption = new Caption( this.currentMousePoint.x, this.currentMousePoint.y, "" );
					this.graph.captions.add( caption );
					
					EditCaptionDialog.Value newValue = EditCaptionDialog.showDialog( this, UserSettings.instance.defaultCaptionText.get( ), UserSettings.instance.defaultCaptionFontSize.get( ) );
					if( newValue != null )
					{
						caption.text.set( newValue.getText( ) );
						caption.size.set( newValue.getSize( ) );
					}
					else
						this.graph.captions.remove( caption );
				}
				
				break;
			case CUT_TOOL:
				this.cutToolClickedObject = false;
				
				if( event.getButton( ) == MouseEvent.BUTTON1 )
				{
					for( Vertex vertex : this.graph.vertices )
						if( VertexDisplayView.wasClicked( vertex, this.currentMousePoint, this.transform.getScaleX( ) ) )
						{
							this.graph.vertices.remove( vertex );
							this.cutToolClickedObject = true;
							break;
						}
					
					if( !this.cutToolClickedObject )
						for( Edge edge : this.graph.edges )
							if( EdgeDisplayView.wasClicked( edge, this.currentMousePoint, this.transform.getScaleX( ) ) )
							{
								this.graph.edges.remove( edge );
								this.cutToolClickedObject = true;
								break;
							}
					
					if( !this.cutToolClickedObject )
						for( Caption caption : this.graph.captions )
							if( CaptionDisplayView.wasHandleClicked( caption, this.currentMousePoint, this.transform.getScaleX( ) ) )
							{
								this.graph.captions.remove( caption );
								this.cutToolClickedObject = true;
								break;
							}
				}
				
				break;
			case PAINT_TOOL:
				this.paintToolClickedObject = false;
				
				if( event.getButton( ) == MouseEvent.BUTTON1 )
				{
					for( Vertex vertex : this.graph.vertices )
						if( VertexDisplayView.wasClicked( vertex, this.currentMousePoint, this.transform.getScaleX( ) ) )
						{
							vertex.color.set( this.paintColor );
							this.paintToolClickedObject = true;
							break;
						}
					
					if( !this.paintToolClickedObject )
						for( Edge edge : this.graph.edges )
							if( EdgeDisplayView.wasClicked( edge, this.currentMousePoint, this.transform.getScaleX( ) ) )
							{
								edge.color.set( this.paintColor );
								this.paintToolClickedObject = true;
								break;
							}
				}
		}
		
		this.isMouseDownOnCanvas = true;
		this.isViewportInvalidated = true;
	}
	
	private void viewportMouseReleased( MouseEvent event ) throws NoninvertibleTransformException
	{
		switch( this.tool )
		{
			case POINTER_TOOL:
				if( !this.pastMousePoint.equals( this.currentMousePoint ) )
					if( !this.pointerToolClickedObject )
					{
						Rectangle selection = this.getSelectionRectangle( );
						
						for( Vertex vertex : this.graph.vertices )
							if( VertexDisplayView.wasSelected( vertex, selection ) )
								vertex.isSelected.set( true );
						
						for( Edge edge : this.graph.edges )
							if( EdgeDisplayView.wasSelected( edge, selection ) )
								edge.isSelected.set( true );
						
						for( Caption caption : this.graph.captions )
							if( CaptionDisplayView.wasHandleSelected( caption, selection ) )
								caption.isSelected.set( true );
					}
					else
						for( Edge edge : this.graph.getSelectedEdges( ) )
							if( edge.isLinear( ) && !edge.isLoop )
								edge.reset( );
				
				if( event.getButton( ) == MouseEvent.BUTTON3 )
				{
					this.viewportPopupMenu.setVertexMenuEnabled( this.graph.hasSelectedVertices( ) );
					this.viewportPopupMenu.setEdgeMenuEnabled( this.graph.hasSelectedEdges( ) );
					this.viewportPopupMenu.show( this.viewport, event.getPoint( ).x, event.getPoint( ).y );
				}
				
				break;
			case GRAPH_TOOL:
				if( this.fromVertex != null )
					for( Vertex vertex : this.graph.vertices )
						if( this.fromVertex != vertex && VertexDisplayView.wasClicked( vertex, this.currentMousePoint, this.transform.getScaleX( ) ) )
							if( this.graph.edges.add( new Edge( this.graph.areDirectedEdgesAllowed, this.fromVertex, vertex ) ) )
							{
								this.fromVertex.isSelected.set( false );
								this.fromVertex = !UserSettings.instance.deselectVertexWithNewEdge.get( ) ? vertex : null;
								if( this.fromVertex != null )
									this.fromVertex.isSelected.set( true );
							}
							else
								Toolkit.getDefaultToolkit( ).beep( );
				
				break;
			case CUT_TOOL:
				if( !this.cutToolClickedObject && !this.pastMousePoint.equals( this.currentMousePoint ) )
				{
					Rectangle selection = this.getSelectionRectangle( );
					this.graph.selectAll( false );
					
					for( Vertex vertex : this.graph.vertices )
						if( VertexDisplayView.wasSelected( vertex, selection ) )
							vertex.isSelected.set( true );
					
					for( Edge edge : this.graph.edges )
						if( EdgeDisplayView.wasSelected( edge, selection ) )
							edge.isSelected.set( true );
					
					for( Caption caption : this.graph.captions )
						if( CaptionDisplayView.wasHandleSelected( caption, selection ) )
							caption.isSelected.set( true );
					
					this.graph.vertices.removeAll( this.graph.getSelectedVertices( ) );
					this.graph.edges.removeAll( this.graph.getSelectedEdges( ) );
					this.graph.captions.removeAll( this.graph.getSelectedCaptions( ) );
				}
				
				break;
			case PAINT_TOOL:
				if( !this.paintToolClickedObject && !this.pastMousePoint.equals( this.currentMousePoint ) )
				{
					Rectangle selection = this.getSelectionRectangle( );
					
					for( Vertex vertex : this.graph.vertices )
						if( VertexDisplayView.wasSelected( vertex, selection ) )
							vertex.color.set( this.paintColor );
					
					for( Edge edge : this.graph.edges )
						if( EdgeDisplayView.wasSelected( edge, selection ) )
							edge.color.set( this.paintColor );
				}
				
				break;
		}
		
		this.isMouseDownOnCanvas = false;
		this.isViewportInvalidated = true;
	}
	
	public void zoomCenter( Point2D.Double center, double factor )
	{
		this.transform.translate( Math.round( center.x ), Math.round( center.y ) );
		
		this.transform.scale( factor, factor );
		if( this.transform.getScaleX( ) > UserSettings.instance.maximumZoomFactor.get( ) )
			this.zoomMax( );
		
		this.transform.translate( Math.round( -center.x ), Math.round( -center.y ) );
		
		this.isViewportInvalidated = true;
	}
	
	public void zoomFit( )
	{
		if( this.graph.vertices.size( ) > 0 )
			this.zoomFit( GraphDisplayView.getBounds( this.graph ) );
	}
	
	public void zoomFit( Rectangle2D rectangle )
	{
		// First we need to reset and translate the graph to the viewport's center
		this.transform.setToIdentity( );
		this.transform.translate( Math.round( this.viewport.getWidth( ) / 2.0 ), Math.round( this.viewport.getHeight( ) / 2.0 ) );
		
		// We need to fit it to the viewport. So we want to scale according to the lowest viewport-to-graph dimension ratio.
		double widthRatio = ( this.viewport.getWidth( ) - UserSettings.instance.zoomGraphPadding.get( ) ) / rectangle.getWidth( );
		double heightRatio = ( this.viewport.getHeight( ) - UserSettings.instance.zoomGraphPadding.get( ) ) / rectangle.getHeight( );
		double minRatio = Math.min( widthRatio, heightRatio );
		
		if( minRatio < 1 )
			this.transform.scale( minRatio, minRatio );
		
		// Only now that we've properly scaled can we translate to the graph's center
		Point2D.Double graphCenter = new Point2D.Double( rectangle.getCenterX( ), rectangle.getCenterY( ) );
		this.transform.translate( Math.round( -graphCenter.x ), Math.round( -graphCenter.y ) );
		
		// And of course, we want to refresh the viewport
		this.isViewportInvalidated = true;
	}
	
	public void zoomMax( )
	{
		this.transform.setTransform( UserSettings.instance.maximumZoomFactor.get( ), this.transform.getShearY( ), this.transform.getShearX( ), UserSettings.instance.maximumZoomFactor.get( ), Math.round( this.transform.getTranslateX( ) ), Math.round( this.transform.getTranslateY( ) ) );
		this.isViewportInvalidated = true;
	}
	
	public void zoomOneToOne( )
	{
		this.transform.setTransform( 1, this.transform.getShearY( ), this.transform.getShearX( ), 1, (int) this.transform.getTranslateX( ), (int) this.transform.getTranslateY( ) );
		this.isViewportInvalidated = true;
	}
}
