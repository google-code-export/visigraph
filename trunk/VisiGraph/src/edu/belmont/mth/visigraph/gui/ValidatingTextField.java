/**
 * ValidatingTextField.java
 */
package edu.belmont.mth.visigraph.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * @author Cameron Behar
 *
 */
@SuppressWarnings("serial")
public class ValidatingTextField extends JTextField
{	
	public final String validatingExpression;
	
	private final Color successColor;
	private final Color failColor;
	
	public ValidatingTextField(int columns, String validatingExpression)
	{
		super(columns);
		this.validatingExpression = validatingExpression;
		super.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void changedUpdate(DocumentEvent arg0) { /* ignore ? */ }

			@Override
			public void insertUpdate(DocumentEvent arg0) { validate(); }

			@Override
			public void removeUpdate(DocumentEvent arg0) { validate(); }
		} );
		
		successColor = new Color(getBackground().getRGB());
		failColor = new Color(255, 209, 209);
		
		validate();
	}
	
	public void validate()
	{
		setBackground(isValid( ) ? successColor : failColor);
	}
	
	public boolean isValid()
	{
		return (validatingExpression == null ? true : getText().matches(validatingExpression));
	}
	
	public String getValidatingExpression()
	{
		return validatingExpression;
	}
}
