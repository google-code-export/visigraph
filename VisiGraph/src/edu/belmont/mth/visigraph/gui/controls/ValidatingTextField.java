/**
 * ValidatingTextField.java
 */
package edu.belmont.mth.visigraph.gui.controls;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * @author Cameron Behar
 */
public class ValidatingTextField extends JTextField
{
	public String		validatingExpression;
	
	private final Color	successColor;
	private final Color	failColor;
	
	public ValidatingTextField( int columns, String validatingExpression )
	{
		super( columns );
		this.validatingExpression = validatingExpression;
		super.getDocument( ).addDocumentListener( new DocumentListener( )
		{
			@Override
			public void changedUpdate( DocumentEvent e )
			{ /* ignore ? */}
			
			@Override
			public void insertUpdate( DocumentEvent e )
			{
				ValidatingTextField.this.validate( );
			}
			
			@Override
			public void removeUpdate( DocumentEvent e )
			{
				ValidatingTextField.this.validate( );
			}
		} );
		
		this.successColor = new Color( this.getBackground( ).getRGB( ) );
		this.failColor = new Color( 255, 209, 209 );
		
		this.validate( );
	}
	
	public String getValidatingExpression( )
	{
		return this.validatingExpression;
	}
	
	@Override
	public boolean isValid( )
	{
		return ( this.validatingExpression == null ? true : this.getText( ).matches( this.validatingExpression ) );
	}
	
	public void setValidatingExpression( String validatingExpression )
	{
		this.validatingExpression = validatingExpression;
		this.validate( );
	}
	
	@Override
	public void validate( )
	{
		this.setBackground( this.isValid( ) ? this.successColor : this.failColor );
	}
}
