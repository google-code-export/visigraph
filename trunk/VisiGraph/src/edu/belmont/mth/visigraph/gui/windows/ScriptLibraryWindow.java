/**
 * ScriptLibraryWindow.java
 */
package edu.belmont.mth.visigraph.gui.windows;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import edu.belmont.mth.visigraph.settings.*;
import edu.belmont.mth.visigraph.resources.*;
import edu.belmont.mth.visigraph.models.functions.*;
import edu.belmont.mth.visigraph.models.generators.*;

/**
 * @author Cameron Behar
 */
public class ScriptLibraryWindow extends JFrame
{
	public ScriptLibraryWindow( )
	{
		super( StringBundle.get( "script_library_window_text" ) );
		this.setIconImage( ImageBundle.get( "app_icon_16x16" ) );
		this.setSize( new Dimension( UserSettings.instance.scriptLibraryWindowWidth.get( ), UserSettings.instance.scriptLibraryWindowHeight.get( ) ) );
		this.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		
		final JEditorPane editor = new JEditorPane( "text/html", "<p>Loading...</p>" )
		{
			{
				this.setEditable( false );
				this.setAutoscrolls( true );
				this.setMargin( new Insets( 5, 15, 15, 15 ) );
			}
		};
		final JScrollPane detailsScrollPane = new JScrollPane( editor );
		
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode( StringBundle.get( "script_library_window_root_node_text" ) );
		
		DefaultMutableTreeNode generatorsNode = new DefaultMutableTreeNode( StringBundle.get( "script_library_window_generators_node_text" ) );
		for( Generator generator : GeneratorService.instance.generators )
			generatorsNode.add( new DefaultMutableTreeNode( generator ) );
		rootNode.add( generatorsNode );
		
		DefaultMutableTreeNode functionsNode = new DefaultMutableTreeNode( StringBundle.get( "script_library_window_functions_node_text" ) );
		for( Function function : FunctionService.instance.functions )
			functionsNode.add( new DefaultMutableTreeNode( function ) );
		rootNode.add( functionsNode );
		
		JTree navigationTree = new JTree( rootNode )
		{
			{
				this.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
				this.getSelectionModel( ).setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
				this.addTreeSelectionListener( new TreeSelectionListener( )
				{
					@Override
					public void valueChanged( TreeSelectionEvent e )
					{
						if( e.getNewLeadSelectionPath( ) != null )
						{
							Object selectedObject = ( (DefaultMutableTreeNode) e.getNewLeadSelectionPath( ).getLastPathComponent( ) ).getUserObject( );
							
							if( selectedObject instanceof Generator )
								editor.setText( ScriptLibraryWindow.this.buildGeneratorHtml( (Generator) selectedObject ) );
							else if( selectedObject instanceof Function )
								editor.setText( ScriptLibraryWindow.this.buildFunctionHtml( (Function) selectedObject ) );
							else
								editor.setText( String.format( "<p><font color=gray><i>%s</i></font></p>", StringBundle.get( "script_library_window_default_text" ) ) );
							
							editor.setCaretPosition( 0 );
						}
					}
				} );
			}
		};
		navigationTree.expandRow( 1 );
		navigationTree.setSelectionRow( 2 );
		JScrollPane treeScrollPane = new JScrollPane( navigationTree );
		
		JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, detailsScrollPane )
		{
			{
				this.setDividerLocation( 250 );
			}
		};
		this.add( splitPane );
		
		this.setVisible( true );
	}
	
	private String buildFunctionHtml( Function function )
	{
		StringBuilder sb = new StringBuilder( );
		
		// Name
		sb.append( String.format( "<h1>%s</h1>", function.toString( ) ) );
		
		// Evaluation capabilities
		sb.append( String.format( "<dl><dt><b>%s</b></dt><dd>%s</dd></dl>", StringBundle.get( "script_library_window_function_evaluation_label" ), (Boolean) function.getAttribute( Function.Attribute.ALLOWS_ONE_TIME_EVALUATION ) ? ( (Boolean) function.getAttribute( Function.Attribute.ALLOWS_DYNAMIC_EVALUATION ) ? StringBundle.get( "script_library_window_function_one_time_and_dynamic_label" ) : StringBundle.get( "script_library_window_function_one_time_label" ) ) : StringBundle.get( "script_library_window_function_dynamic_label" ) ) );
		
		// Header divider
		sb.append( "<hr/>" );
		
		// Description
		if( function.getAttribute( Function.Attribute.DESCRIPTION ) != null )
			sb.append( String.format( "<p>%s</p>", function.getAttribute( Function.Attribute.DESCRIPTION ) ) );
		
		// New line
		if( function.getAttribute( Function.Attribute.INPUT ) != null || function.getAttribute( Function.Attribute.SIDE_EFFECTS ) != null || function.getAttribute( Function.Attribute.OUTPUT ) != null || function.getAttribute( Function.Attribute.CONSTRAINTS ) != null )
			sb.append( "<br/>" );
		
		// Input
		if( function.getAttribute( Function.Attribute.INPUT ) != null )
			sb.append( String.format( "<dl><dt><b>%s</b></dt><dd>%s</dd></dl>", StringBundle.get( "script_library_window_function_input_label" ), function.getAttribute( Function.Attribute.INPUT ) ) );
		
		// Side effects
		if( function.getAttribute( Function.Attribute.SIDE_EFFECTS ) != null )
			sb.append( String.format( "<dl><dt><b>%s</b></dt><dd>%s</dd></dl>", StringBundle.get( "script_library_window_function_side_effects_label" ), function.getAttribute( Function.Attribute.SIDE_EFFECTS ) ) );
		
		// Output
		if( function.getAttribute( Function.Attribute.OUTPUT ) != null )
			sb.append( String.format( "<dl><dt><b>%s</b></dt><dd>%s</dd></dl>", StringBundle.get( "script_library_window_function_output_label" ), function.getAttribute( Function.Attribute.OUTPUT ) ) );
		
		// Constraints
		if( function.getAttribute( Function.Attribute.CONSTRAINTS ) != null )
			sb.append( String.format( "<dl><dt><b>%s</b></dt><dd>%s</dd></dl>", StringBundle.get( "script_library_window_function_constraints_label" ), this.join( (String[ ]) function.getAttribute( Function.Attribute.CONSTRAINTS ), "</dd><dd>" ) ) );
		
		// Applicability table
		sb.append( String.format( "<hr/><br/><table border=1 cellpadding=3 cellspacing=0><tr bgcolor=#EEEEFF><td><b>%s</b></td><td><b>%s</b></td></tr><tr><td>%s</td><td align=center><i>%s</i></td></tr><tr><td>%s</td><td align=center><i>%s</i></td></tr><tr><td>%s</td><td align=center><i>%s</i></td></tr><tr><td>%s</td><td align=center><i>%s</i></td></tr><tr><td>%s</td><td align=center><i>%s</i></td></tr><tr><td>%s</td><td align=center><i>%s</i></td></tr><tr><td>%s</td><td align=center><i>%s</i></td></tr><tr><td>%s</td><td align=center><i>%s</i></td></tr><tr><td>%s</td><td align=center><i>%s</i></td></tr><tr><td>%s</td><td align=center><i>%s</i></td></tr></table>", StringBundle.get( "script_library_window_function_graph_type_label" ), StringBundle.get( "script_library_window_function_applicable_label" ), StringBundle.get( "script_library_window_function_simple_tree_label" ), StringBundle.get( function.isApplicable( false, false, false, false ) ? "yes" : "no" ), StringBundle.get( "script_library_window_function_simple_graph_label" ), StringBundle.get( function.isApplicable( false, false, false, true ) ? "yes" : "no" ), StringBundle.get( "script_library_window_function_multigraph_label" ), StringBundle.get( function.isApplicable( false, false, true, true ) ? "yes" : "no" ), StringBundle.get( "script_library_window_function_pseudograph_label" ), StringBundle.get( function.isApplicable( true, false, false, true ) ? "yes" : "no" ), StringBundle.get( "script_library_window_function_pseudomultigraph_label" ), StringBundle.get( function.isApplicable( true, false, true, true ) ? "yes" : "no" ), StringBundle.get( "script_library_window_function_directed_tree_label" ), StringBundle.get( function.isApplicable( false, true, false, false ) ? "yes" : "no" ), StringBundle.get( "script_library_window_function_directed_graph_label" ), StringBundle.get( function.isApplicable( false, true, false, true ) ? "yes" : "no" ), StringBundle.get( "script_library_window_function_multidigraph_label" ), StringBundle.get( function.isApplicable( false, true, true, true ) ? "yes" : "no" ), StringBundle.get( "script_library_window_function_pseudodigraph_label" ), StringBundle.get( function.isApplicable( true, true, false, true ) ? "yes" : "no" ), StringBundle.get( "script_library_window_function_pseudomultidigraph_label" ), StringBundle.get( function.isApplicable( true, true, true, true ) ? "yes" : "no" ) ) );
		
		// Related generators
		if( function.getAttribute( Function.Attribute.RELATED_GENERATORS ) != null )
			sb.append( String.format( "<br/><table border=1 width=100%% cellpadding=3 cellspacing=0><tr bgcolor=#EEEEFF><td><b>%s</b></td></tr><tr bgcolor=#FFFFFF><td><code>%s</code></td></tr></table>", StringBundle.get( "script_library_window_function_related_generators_label" ), this.sortedJoin( (String[ ]) function.getAttribute( Function.Attribute.RELATED_GENERATORS ), ", " ) ) );
		
		// Related functions
		if( function.getAttribute( Function.Attribute.RELATED_FUNCTIONS ) != null )
			sb.append( String.format( "<br/><table border=1 width=100%% cellpadding=3 cellspacing=0><tr bgcolor=#EEEEFF><td><b>%s</b></td></tr><tr bgcolor=#FFFFFF><td><code>%s</code></td></tr></table>", StringBundle.get( "script_library_window_function_related_functions_label" ), this.sortedJoin( (String[ ]) function.getAttribute( Function.Attribute.RELATED_FUNCTIONS ), ", " ) ) );
		
		// Tags
		if( function.getAttribute( Function.Attribute.TAGS ) != null )
			sb.append( String.format( "<br/><table border=1 width=100%% cellpadding=3 cellspacing=0><tr bgcolor=#EEEEFF><td><b>%s</b></td></tr><tr bgcolor=#FFFFFF><td><code>%s</code></td></tr></table>", StringBundle.get( "script_library_window_function_tags_label" ), this.sortedJoin( (String[ ]) function.getAttribute( Function.Attribute.TAGS ), ", " ) ) );
		
		// Version / author
		sb.append( String.format( "<br/><hr/><table width=100%%><tr><td>%s</td><td align=right>%s</td></tr></table>", function.getAttribute( Function.Attribute.VERSION ) == null ? "" : StringBundle.get( "script_library_window_function_version_label" ) + function.getAttribute( Function.Attribute.VERSION ), function.getAttribute( Function.Attribute.AUTHOR ) == null ? "<i>" + StringBundle.get( "script_library_window_function_unsigned_label" ) + "</i>" : StringBundle.get( "script_library_window_function_by_label" ) + function.getAttribute( Function.Attribute.AUTHOR ) ) );
		
		return sb.toString( );
	}
	
	private String buildGeneratorHtml( Generator generator )
	{
		StringBuilder sb = new StringBuilder( );
		
		// Name
		sb.append( String.format( "<h1>%s</h1>", generator.toString( ) ) );
		
		// Isomorphisms
		if( generator.getAttribute( Generator.Attribute.ISOMORPHISMS ) != null )
			sb.append( String.format( "<dl><dt><b>%s</b></dt><dd>%s</dd></dl>", StringBundle.get( "script_library_window_generator_isomorphisms_label" ), this.join( (String[ ]) generator.getAttribute( Generator.Attribute.ISOMORPHISMS ), ",&nbsp;&nbsp;" ) ) );
		
		// Header divider
		sb.append( "<hr/>" );
		
		// Description
		if( generator.getAttribute( Generator.Attribute.DESCRIPTION ) != null )
			sb.append( String.format( "<p>%s</p>", generator.getAttribute( Generator.Attribute.DESCRIPTION ) ) );
		
		if( (Boolean) generator.getAttribute( Generator.Attribute.ARE_PARAMETERS_ALLOWED ) )
		{
			// Parameters
			sb.append( String.format( "<br/><dl><dt><b>%s</b></dt><dd><code>%s</code></dd></dl>", StringBundle.get( "script_library_window_generator_parameters_label" ), generator.getAttribute( Generator.Attribute.PARAMETERS_DESCRIPTION ) ) );
			
			// Constraints
			if( generator.getAttribute( Generator.Attribute.CONSTRAINTS ) != null )
				sb.append( String.format( "<dl><dt><b>%s</b></dt><dd>%s</dd></dl>", StringBundle.get( "script_library_window_generator_constraints_label" ), this.join( (String[ ]) generator.getAttribute( Generator.Attribute.CONSTRAINTS ), "</dd><dd>" ) ) );
		}
		
		// Rules table
		sb.append( String.format( "<hr/><br/><table border=1 cellpadding=3 cellspacing=0><tr bgcolor=#EEEEFF><td><b>%s</b></td><td><b>%s</b></td><td><b>%s</b></td></tr><tr><td>%s</td><td align=center><i>%s</i></td><td align=center><i>%s</i></td></tr><tr><td>%s</td><td align=center><i>%s</i></td><td align=center><i>%s</i></td></tr><tr><td>%s</td><td align=center><i>%s</i></td><td align=center><i>%s</i></td></tr><tr><td>%s</td><td align=center><i>%s</i></td><td align=center><i>%s</i></td></tr></table>", StringBundle.get( "script_library_window_generator_rule_label" ), StringBundle.get( "script_library_window_generator_allows_label" ), StringBundle.get( "script_library_window_generator_forces_label" ), StringBundle.get( "script_library_window_generator_loops_label" ), StringBundle.get( ( (Generator.BooleanRule) generator.getAttribute( Generator.Attribute.ARE_LOOPS_ALLOWED ) ).isTrue( ) ? "yes" : "no" ), StringBundle.get( ( (Generator.BooleanRule) generator.getAttribute( Generator.Attribute.ARE_LOOPS_ALLOWED ) ).isForced( ) ? "yes" : "no" ), StringBundle.get( "script_library_window_generator_directed_edges_label" ), StringBundle.get( ( (Generator.BooleanRule) generator.getAttribute( Generator.Attribute.ARE_DIRECTED_EDGES_ALLOWED ) ).isTrue( ) ? "yes" : "no" ), StringBundle.get( ( (Generator.BooleanRule) generator.getAttribute( Generator.Attribute.ARE_DIRECTED_EDGES_ALLOWED ) ).isForced( ) ? "yes" : "no" ), StringBundle.get( "script_library_window_generator_multiple_edges_label" ), StringBundle.get( ( (Generator.BooleanRule) generator.getAttribute( Generator.Attribute.ARE_MULTIPLE_EDGES_ALLOWED ) ).isTrue( ) ? "yes" : "no" ), StringBundle.get( ( (Generator.BooleanRule) generator.getAttribute( Generator.Attribute.ARE_MULTIPLE_EDGES_ALLOWED ) ).isForced( ) ? "yes" : "no" ), StringBundle.get( "script_library_window_generator_cycles_label" ), StringBundle.get( ( (Generator.BooleanRule) generator.getAttribute( Generator.Attribute.ARE_CYCLES_ALLOWED ) ).isTrue( ) ? "yes" : "no" ), StringBundle.get( ( (Generator.BooleanRule) generator.getAttribute( Generator.Attribute.ARE_CYCLES_ALLOWED ) ).isForced( ) ? "yes" : "no" ) ) );
		
		// Related generators
		if( generator.getAttribute( Generator.Attribute.RELATED_GENERATORS ) != null )
			sb.append( String.format( "<br/><table border=1 width=100%% cellpadding=3 cellspacing=0><tr bgcolor=#EEEEFF><td><b>%s</b></td></tr><tr bgcolor=#FFFFFF><td><code>%s</code></td></tr></table>", StringBundle.get( "script_library_window_generator_related_generators_label" ), this.sortedJoin( (String[ ]) generator.getAttribute( Generator.Attribute.RELATED_GENERATORS ), ", " ) ) );
		
		// Related functions
		if( generator.getAttribute( Generator.Attribute.RELATED_FUNCTIONS ) != null )
			sb.append( String.format( "<br/><table border=1 width=100%% cellpadding=3 cellspacing=0><tr bgcolor=#EEEEFF><td><b>%s</b></td></tr><tr bgcolor=#FFFFFF><td><code>%s</code></td></tr></table>", StringBundle.get( "script_library_window_generator_related_functions_label" ), this.sortedJoin( (String[ ]) generator.getAttribute( Generator.Attribute.RELATED_FUNCTIONS ), ", " ) ) );
		
		// Tags
		if( generator.getAttribute( Generator.Attribute.TAGS ) != null )
			sb.append( String.format( "<br/><table border=1 width=100%% cellpadding=3 cellspacing=0><tr bgcolor=#EEEEFF><td><b>%s</b></td></tr><tr bgcolor=#FFFFFF><td><code>%s</code></td></tr></table>", StringBundle.get( "script_library_window_generator_tags_label" ), this.sortedJoin( (String[ ]) generator.getAttribute( Generator.Attribute.TAGS ), ", " ) ) );
		
		// Version / author
		sb.append( String.format( "<br/><hr/><table width=100%%><tr><td>%s</td><td align=right>%s</td></tr></table>", ( generator.getAttribute( Generator.Attribute.VERSION ) == null ? "" : StringBundle.get( "script_library_window_generator_version_label" ) + generator.getAttribute( Generator.Attribute.VERSION ) ), ( generator.getAttribute( Generator.Attribute.AUTHOR ) == null ? "<i>" + StringBundle.get( "script_library_window_generator_unsigned_label" ) + "</i>" : StringBundle.get( "script_library_window_generator_by_label" ) + generator.getAttribute( Generator.Attribute.AUTHOR ) ) ) );
		
		return sb.toString( );
	}
	
	private String join( String[ ] strings, String separator )
	{
		if( strings.length == 0 )
			return "";
		
		StringBuilder sb = new StringBuilder( );
		for( String string : strings )
			sb.append( string + separator );
		
		return sb.substring( 0, sb.length( ) - separator.length( ) );
	}
	
	private String sortedJoin( String[ ] strings, String separator )
	{
		Arrays.sort( strings );
		return this.join( strings, separator );
	}
}
