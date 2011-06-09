/**
 * MainWindow.java
 */
package edu.belmont.mth.visigraph.gui.windows;

import java.io.*;
import java.awt.*;
import java.util.*;
import java.beans.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.print.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.resources.*;
import edu.belmont.mth.visigraph.utilities.*;
import edu.belmont.mth.visigraph.gui.dialogs.*;

/**
 * @author Cameron Behar
 */
public class MainWindow extends JFrame
{
	private final JMenuBar		menuBar;
	private final JMenu			fileMenu;
	private final JMenuItem		newGraphMenuItem;
	private final JMenuItem		duplicateGraphMenuItem;
	private final JMenuItem		openGraphMenuItem;
	private final JMenuItem		openGraphFromTheWebMenuItem;
	private final JMenuItem		saveGraphMenuItem;
	private final JMenuItem		saveAsGraphMenuItem;
	private final JMenuItem		printGraphMenuItem;
	private final JMenuItem		exitGraphMenuItem;
	private final JMenu			editMenu;
	private final JMenuItem		undoMenuItem;
	private final JMenuItem		redoMenuItem;
	private final JMenuItem		cutMenuItem;
	private final JMenuItem		copyMenuItem;
	private final JMenuItem		pasteMenuItem;
	private final JMenuItem		selectAllMenuItem;
	private final JMenuItem		selectAllVerticesMenuItem;
	private final JMenuItem		selectAllEdgesMenuItem;
	private final JMenu			windowsMenu;
	private final JMenuItem		cascadeMenuItem;
	private final JMenuItem		showSideBySideMenuItem;
	private final JMenuItem		showStackedMenuItem;
	private final JMenuItem		tileWindowsMenuItem;
	private final JMenuItem		showPreviousMenuItem;
	private final JMenuItem		showNextMenuItem;
	private final JMenu			helpMenu;
	private final JMenuItem		helpContentsMenuItem;
	private final JMenuItem		scriptLibraryMenuItem;
	private final JMenuItem		preferencesMenuItem;
	private final JMenuItem		downloadsMenuItem;
	private final JMenuItem		aboutVisiGraphMenuItem;
	private final JDesktopPane	desktopPane;
	private final JFileChooser	fileChooser;
	
	public MainWindow( )
	{
		super( GlobalSettings.applicationName );
		this.setIconImage( ImageBundle.get( "app_icon_16x16" ) );
		this.setSize( new Dimension( UserSettings.instance.mainWindowWidth.get( ), UserSettings.instance.mainWindowHeight.get( ) ) );
		this.setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		this.addWindowListener( new WindowListener( )
		{
			@Override
			public void windowActivated( WindowEvent e )
			{}
			
			@Override
			public void windowClosed( WindowEvent e )
			{}
			
			@Override
			public void windowClosing( WindowEvent e )
			{
				MainWindow.this.closingWindow( e );
			}
			
			@Override
			public void windowDeactivated( WindowEvent e )
			{}
			
			@Override
			public void windowDeiconified( WindowEvent e )
			{}
			
			@Override
			public void windowIconified( WindowEvent e )
			{}
			
			@Override
			public void windowOpened( WindowEvent e )
			{}
		} );
		
		this.desktopPane = new JDesktopPane( );
		this.getContentPane( ).add( this.desktopPane, BorderLayout.CENTER );
		
		this.menuBar = new JMenuBar( );
		
		this.fileChooser = new JFileChooser( );
		
		this.fileMenu = new JMenu( StringBundle.get( "file_menu_text" ) );
		this.menuBar.add( this.fileMenu );
		
		this.newGraphMenuItem = new JMenuItem( StringBundle.get( "file_new_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						Graph newGraph = NewGraphDialog.showDialog( MainWindow.this );
						if( newGraph != null )
							MainWindow.this.addGraphWindow( newGraph );
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_N, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.fileMenu.add( this.newGraphMenuItem );
		
		this.duplicateGraphMenuItem = new JMenuItem( StringBundle.get( "file_duplicate_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame instanceof GraphWindow )
						{
							Graph graph = ( (GraphWindow) selectedFrame ).getGdc( ).getGraph( );
							MainWindow.this.addGraphWindow( new Graph( graph.toString( ) ) );
						}
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_D, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.fileMenu.add( this.duplicateGraphMenuItem );
		
		this.fileMenu.addSeparator( );
		
		this.openGraphMenuItem = new JMenuItem( StringBundle.get( "file_open_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						MainWindow.this.fileChooser.resetChoosableFileFilters( );
						MainWindow.this.fileChooser.setAcceptAllFileFilterUsed( false );
						MainWindow.this.fileChooser.setFileFilter( new FileNameExtensionFilter( StringBundle.get( "visigraph_file_description" ), "vsg" ) );
						MainWindow.this.fileChooser.setMultiSelectionEnabled( true );
						
						boolean success = false;
						
						while( !success )
						{
							success = false;
							
							if( MainWindow.this.fileChooser.showOpenDialog( MainWindow.this ) == JFileChooser.APPROVE_OPTION )
								try
								{
									for( File selectedFile : MainWindow.this.fileChooser.getSelectedFiles( ) )
									{
										MainWindow.this.openFile( selectedFile );
										success = true;
									}
								}
								catch( IOException ex )
								{
									DebugUtilities.logException( "An exception occurred while loading a graph from file.", ex );
								}
							else
								success = true;
						}
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_O, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.fileMenu.add( this.openGraphMenuItem );
		
		this.openGraphFromTheWebMenuItem = new JMenuItem( StringBundle.get( "file_open_from_the_web_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						boolean success = false;
						
						while( !success )
						{
							success = false;
							
							String filename = OpenFromTheWebDialog.showDialog( MainWindow.this );
							if( filename != null )
								try
								{
									MainWindow.this.openFile( new File( filename ) );
									success = true;
								}
								catch( IOException ex )
								{
									DebugUtilities.logException( "An exception occurred while loading a graph from file.", ex );
								}
							else
								success = true;
						}
					}
				} );
			}
		};
		this.fileMenu.add( this.openGraphFromTheWebMenuItem );
		
		this.saveGraphMenuItem = new JMenuItem( StringBundle.get( "file_save_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						GraphWindow graphWindow = ( (GraphWindow) selectedFrame );
						
						if( graphWindow != null )
							try
							{
								graphWindow.save( );
							}
							catch( IOException ex )
							{
								DebugUtilities.logException( "An exception occurred while saving the selected graph.", ex );
							}
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_S, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.fileMenu.add( this.saveGraphMenuItem );
		
		this.saveAsGraphMenuItem = new JMenuItem( StringBundle.get( "file_save_as_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						GraphWindow graphWindow = ( (GraphWindow) selectedFrame );
						
						if( graphWindow != null )
							graphWindow.saveAs( );
					}
				} );
			}
		};
		this.fileMenu.add( this.saveAsGraphMenuItem );
		
		this.fileMenu.addSeparator( );
		
		this.printGraphMenuItem = new JMenuItem( StringBundle.get( "file_print_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						if( selectedFrame != null )
						{
							GraphWindow graphWindow = ( (GraphWindow) selectedFrame );
							try
							{
								graphWindow.getGdc( ).printGraph( );
							}
							catch( PrinterException ex )
							{
								DebugUtilities.logException( "An exception occurred while printing the selected graph.", ex );
							}
						}
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_P, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.fileMenu.add( this.printGraphMenuItem );
		
		if( !System.getProperty( "os.name" ).startsWith( "Mac" ) )
		{
			this.fileMenu.addSeparator( );
			
			this.exitGraphMenuItem = new JMenuItem( StringBundle.get( "file_exit_menu_text" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							System.exit( 0 );
						}
					} );
				}
			};
			this.fileMenu.add( this.exitGraphMenuItem );
		}
		else
			this.exitGraphMenuItem = null;
		
		this.editMenu = new JMenu( StringBundle.get( "edit_menu_text" ) );
		this.menuBar.add( this.editMenu );
		
		this.undoMenuItem = new JMenuItem( StringBundle.get( "edit_undo_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame instanceof GraphWindow )
							( (GraphWindow) selectedFrame ).getGdc( ).undo( );
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Z, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.editMenu.add( this.undoMenuItem );
		
		this.redoMenuItem = new JMenuItem( StringBundle.get( "edit_redo_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame instanceof GraphWindow )
							( (GraphWindow) selectedFrame ).getGdc( ).redo( );
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Y, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.editMenu.add( this.redoMenuItem );
		
		this.editMenu.addSeparator( );
		
		this.cutMenuItem = new JMenuItem( StringBundle.get( "edit_cut_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame instanceof GraphWindow )
							( (GraphWindow) selectedFrame ).getGdc( ).cut( );
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_X, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.editMenu.add( this.cutMenuItem );
		
		this.copyMenuItem = new JMenuItem( StringBundle.get( "edit_copy_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame instanceof GraphWindow )
							( (GraphWindow) selectedFrame ).getGdc( ).copy( );
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_C, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.editMenu.add( this.copyMenuItem );
		
		this.pasteMenuItem = new JMenuItem( StringBundle.get( "edit_paste_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame instanceof GraphWindow )
							( (GraphWindow) selectedFrame ).getGdc( ).paste( );
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_V, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.editMenu.add( this.pasteMenuItem );
		
		this.editMenu.addSeparator( );
		
		this.selectAllMenuItem = new JMenuItem( StringBundle.get( "edit_select_all_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame instanceof GraphWindow )
							( (GraphWindow) selectedFrame ).getGdc( ).selectAll( );
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_A, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.editMenu.add( this.selectAllMenuItem );
		
		this.selectAllVerticesMenuItem = new JMenuItem( StringBundle.get( "edit_select_all_vertices_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame instanceof GraphWindow )
							( (GraphWindow) selectedFrame ).getGdc( ).selectAllVertices( );
					}
				} );
			}
		};
		this.editMenu.add( this.selectAllVerticesMenuItem );
		
		this.selectAllEdgesMenuItem = new JMenuItem( StringBundle.get( "edit_select_all_edges_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame instanceof GraphWindow )
							( (GraphWindow) selectedFrame ).getGdc( ).selectAllEdges( );
					}
				} );
			}
		};
		this.editMenu.add( this.selectAllEdgesMenuItem );
		
		this.windowsMenu = new JMenu( StringBundle.get( "windows_menu_text" ) );
		this.menuBar.add( this.windowsMenu );
		
		this.cascadeMenuItem = new JMenuItem( StringBundle.get( "windows_cascade_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame[ ] frames = MainWindow.this.getInternalFrames( );
						
						for( int i = 0; i < frames.length; ++i )
							try
							{
								frames[frames.length - i - 1].setIcon( false );
								frames[frames.length - i - 1].setMaximum( false );
								frames[frames.length - i - 1].setLocation( i * UserSettings.instance.cascadeWindowOffset.get( ), i * UserSettings.instance.cascadeWindowOffset.get( ) );
								frames[frames.length - i - 1].setSize( new Dimension( UserSettings.instance.graphWindowWidth.get( ), UserSettings.instance.graphWindowHeight.get( ) ) );
							}
							catch( PropertyVetoException ex )
							{
								DebugUtilities.logException( "An exception occurred while repositioning an internal window.", ex );
							}
					}
				} );
			}
		};
		this.windowsMenu.add( this.cascadeMenuItem );
		
		this.showSideBySideMenuItem = new JMenuItem( StringBundle.get( "windows_show_side_by_side_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame[ ] frames = MainWindow.this.getInternalFrames( );
						
						if( frames.length > 0 )
						{
							double frameWidth = MainWindow.this.desktopPane.getWidth( ) / frames.length;
							
							for( int i = 0; i < frames.length; ++i )
								try
								{
									frames[i].setIcon( false );
									frames[i].setMaximum( false );
									frames[i].setLocation( (int) ( i * frameWidth ), 0 );
									frames[i].setSize( (int) frameWidth, MainWindow.this.desktopPane.getHeight( ) );
								}
								catch( PropertyVetoException ex )
								{
									DebugUtilities.logException( "An exception occurred while repositioning an internal window.", ex );
								}
						}
					}
				} );
			}
		};
		this.windowsMenu.add( this.showSideBySideMenuItem );
		
		this.showStackedMenuItem = new JMenuItem( StringBundle.get( "windows_show_stacked_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame[ ] frames = MainWindow.this.getInternalFrames( );
						
						if( frames.length > 0 )
						{
							double frameHeight = MainWindow.this.desktopPane.getHeight( ) / frames.length;
							
							for( int i = 0; i < frames.length; ++i )
								try
								{
									frames[i].setIcon( false );
									frames[i].setMaximum( false );
									frames[i].setLocation( 0, (int) ( i * frameHeight ) );
									frames[i].setSize( MainWindow.this.desktopPane.getWidth( ), (int) frameHeight );
								}
								catch( PropertyVetoException ex )
								{
									DebugUtilities.logException( "An exception occurred while repositioning an internal window.", ex );
								}
						}
					}
				} );
			}
		};
		this.windowsMenu.add( this.showStackedMenuItem );
		
		this.tileWindowsMenuItem = new JMenuItem( StringBundle.get( "windows_tile_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame[ ] frames = MainWindow.this.getInternalFrames( );
						
						if( frames.length > 0 )
						{
							int rows = (int) Math.round( Math.sqrt( frames.length ) );
							int columns = (int) Math.ceil( frames.length / (double) rows );
							double rowSpace = MainWindow.this.desktopPane.getHeight( ) / rows;
							double colSpace = MainWindow.this.desktopPane.getWidth( ) / columns;
							
							for( int i = 0; i < frames.length; ++i )
								try
								{
									frames[i].setIcon( false );
									frames[i].setMaximum( false );
									frames[i].setLocation( (int) ( ( i % columns ) * colSpace ), (int) ( ( i / columns ) * rowSpace ) );
									frames[i].setSize( (int) colSpace, (int) rowSpace );
								}
								catch( PropertyVetoException ex )
								{
									DebugUtilities.logException( "An exception occurred while repositioning an internal window.", ex );
								}
						}
					}
				} );
			}
		};
		this.windowsMenu.add( this.tileWindowsMenuItem );
		
		this.windowsMenu.addSeparator( );
		
		this.showPreviousMenuItem = new JMenuItem( StringBundle.get( "windows_show_previous_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame[ ] frames = MainWindow.this.getInternalFrames( );
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame != null )
						{
							int selectedFrameIndex = 0;
							for( selectedFrameIndex = 0; selectedFrameIndex < frames.length; ++selectedFrameIndex )
								if( frames[selectedFrameIndex] == selectedFrame )
									break;
							
							if( selectedFrameIndex < frames.length )
								try
								{
									frames[( selectedFrameIndex + frames.length - 1 ) % frames.length].setSelected( true );
								}
								catch( PropertyVetoException ex )
								{
									DebugUtilities.logException( "An exception occurred while selecting the previous graph window.", ex );
								}
						}
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_PAGE_UP, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.windowsMenu.add( this.showPreviousMenuItem );
		
		this.showNextMenuItem = new JMenuItem( StringBundle.get( "windows_show_next_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame[ ] frames = MainWindow.this.getInternalFrames( );
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame != null )
						{
							int selectedFrameIndex = 0;
							for( selectedFrameIndex = 0; selectedFrameIndex < frames.length; ++selectedFrameIndex )
								if( frames[selectedFrameIndex] == selectedFrame )
									break;
							
							if( selectedFrameIndex < frames.length )
								try
								{
									frames[( selectedFrameIndex + 1 ) % frames.length].setSelected( true );
								}
								catch( PropertyVetoException ex )
								{
									DebugUtilities.logException( "An exception occurred while selecting the next graph window.", ex );
								}
						}
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_PAGE_DOWN, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.windowsMenu.add( this.showNextMenuItem );
		
		this.helpMenu = new JMenu( StringBundle.get( "help_menu_text" ) );
		this.menuBar.add( this.helpMenu );
		
		this.helpContentsMenuItem = new JMenuItem( StringBundle.get( "help_contents_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						WebUtilities.launchBrowser( GlobalSettings.applicationWebsite );
					}
				} );
			}
		};
		this.helpMenu.add( this.helpContentsMenuItem );
		
		this.scriptLibraryMenuItem = new JMenuItem( StringBundle.get( "help_script_library_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						new ScriptLibraryWindow( );
					}
				} );
			}
		};
		this.helpMenu.add( this.scriptLibraryMenuItem );
		
		this.helpMenu.addSeparator( );
		
		this.downloadsMenuItem = new JMenuItem( StringBundle.get( "help_downloads_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						DownloadsDialog.showDialog( MainWindow.this );
					}
				} );
			}
		};
		this.helpMenu.add( this.downloadsMenuItem );
		
		this.preferencesMenuItem = new JMenuItem( StringBundle.get( "help_preferences_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						PreferencesDialog.showDialog( MainWindow.this );
					}
				} );
			}
		};
		this.helpMenu.add( this.preferencesMenuItem );
		
		this.helpMenu.addSeparator( );
		
		this.aboutVisiGraphMenuItem = new JMenuItem( StringBundle.get( "help_about_menu_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						AboutDialog.showDialog( MainWindow.this );
					}
				} );
			}
		};
		this.helpMenu.add( this.aboutVisiGraphMenuItem );
		
		this.setJMenuBar( this.menuBar );
		
		this.setVisible( true );
	}
	
	public GraphWindow addGraphWindow( Graph graph )
	{
		GraphWindow graphWindow = new GraphWindow( graph );
		MainWindow.this.desktopPane.add( graphWindow );
		try
		{
			graphWindow.setMaximum( true );
			graphWindow.setSelected( true );
		}
		catch( PropertyVetoException ex )
		{
			DebugUtilities.logException( "An exception occurred while loading the graph window.", ex );
		}
		
		return graphWindow;
	}
	
	public void closingWindow( WindowEvent e )
	{
		JInternalFrame[ ] frames = this.desktopPane.getAllFrames( );
		
		for( JInternalFrame frame : frames )
		{
			GraphWindow window = (GraphWindow) frame;
			
			window.closingWindow( new InternalFrameEvent( frame, 0 ) );
			
			if( !window.isClosed( ) )
				break;
		}
		
		if( this.desktopPane.getAllFrames( ).length == 0 )
		{
			this.dispose( );
			System.exit( 0 );
		}
	}
	
	public JInternalFrame[ ] getInternalFrames( )
	{
		JInternalFrame[ ] frames = MainWindow.this.desktopPane.getAllFrames( );
		Arrays.sort( frames, new Comparator<JInternalFrame>( )
		{
			@Override
			public int compare( JInternalFrame frame0, JInternalFrame frame1 )
			{
				// Why sort by the seemingly arbitrary value returned by toString()? Well, we actually don't really care about any
				// particular order, just so long as it stays the same (getAllFrames() does not always return them in a consistent order for some
				// reason). This is the only property that is guaranteed to be unique to each frame and unchanging throughout the object's lifetime.
				return frame0.toString( ).compareTo( frame1.toString( ) );
			}
		} );
		return frames;
	}
	
	public void openFile( File file ) throws IOException
	{
		Scanner scanner = new Scanner( file );
		StringBuilder sb = new StringBuilder( );
		while( scanner.hasNextLine( ) )
			sb.append( scanner.nextLine( ) );
		
		scanner.close( );
		
		Graph newGraph = new Graph( sb.toString( ) );
		if( newGraph != null )
			this.addGraphWindow( newGraph ).setFile( file );
	}
}
