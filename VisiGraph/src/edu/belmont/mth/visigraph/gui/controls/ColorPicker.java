/**
 * ColorPicker.java
 */
package edu.belmont.mth.visigraph.gui.controls;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.event.*;

/**
 * @author Cameron Behar
 */
public class ColorPicker extends JComponent
{
	private float	hue;
	private float	saturation;
	private float	brightness;
	
	private int		alpha;
	private boolean	isDraggingHue			= false;
	private boolean	isDraggingSaturation	= false;
	
	private boolean	isDraggingBrightness	= false;
	
	public ColorPicker( )
	{
		this( Color.blue );
	}
	
	public ColorPicker( Color color )
	{
		this.setColor( color );
		
		super.addMouseListener( new MouseListener( )
		{
			@Override
			public void mouseClicked( MouseEvent e )
			{
				if( e.getX( ) >= 23 && e.getX( ) <= 91 )
				{
					ColorPicker.this.hue = ( e.getX( ) - 23 ) / 69.0f;
					ColorPicker.this.repaint( );
				}
				else if( e.getX( ) >= 98 && e.getX( ) <= 166 )
				{
					ColorPicker.this.saturation = ( e.getX( ) - 98 ) / 69.0f;
					ColorPicker.this.repaint( );
				}
				else if( e.getX( ) >= 173 && e.getX( ) <= 241 )
				{
					ColorPicker.this.brightness = ( e.getX( ) - 173 ) / 69.0f;
					ColorPicker.this.repaint( );
				}
			}
			
			@Override
			public void mouseEntered( MouseEvent e )
			{}
			
			@Override
			public void mouseExited( MouseEvent e )
			{}
			
			@Override
			public void mousePressed( MouseEvent e )
			{
				if( e.getX( ) >= 23 && e.getX( ) <= 91 )
					ColorPicker.this.isDraggingHue = true;
				else if( e.getX( ) >= 98 && e.getX( ) <= 166 )
					ColorPicker.this.isDraggingSaturation = true;
				else if( e.getX( ) >= 173 && e.getX( ) <= 241 )
					ColorPicker.this.isDraggingBrightness = true;
			}
			
			@Override
			public void mouseReleased( MouseEvent e )
			{
				ColorPicker.this.isDraggingHue = false;
				ColorPicker.this.isDraggingSaturation = false;
				ColorPicker.this.isDraggingBrightness = false;
			}
		} );
		super.addMouseMotionListener( new MouseMotionListener( )
		{
			@Override
			public void mouseDragged( MouseEvent e )
			{
				int effectiveX = e.getX( );
				
				if( ColorPicker.this.isDraggingHue )
				{
					if( effectiveX < 23 )
						effectiveX = 23;
					else if( effectiveX > 91 )
						effectiveX = 91;
					
					ColorPicker.this.hue = ( effectiveX - 23 ) / 69.0f;
					ColorPicker.this.repaint( );
				}
				else if( ColorPicker.this.isDraggingSaturation )
				{
					if( effectiveX < 98 )
						effectiveX = 98;
					else if( effectiveX > 166 )
						effectiveX = 166;
					
					ColorPicker.this.saturation = ( effectiveX - 98 ) / 69.0f;
					ColorPicker.this.repaint( );
				}
				else if( ColorPicker.this.isDraggingBrightness )
				{
					if( effectiveX < 173 )
						effectiveX = 173;
					else if( effectiveX > 242 )
						effectiveX = 241;
					
					ColorPicker.this.brightness = ( effectiveX - 173 ) / 69.0f;
					ColorPicker.this.repaint( );
				}
			}
			
			@Override
			public void mouseMoved( MouseEvent e )
			{}
		} );
		
		super.setPreferredSize( new Dimension( 246, 20 ) );
		super.setMinimumSize( super.getPreferredSize( ) );
		super.setMaximumSize( super.getPreferredSize( ) );
		super.setSize( super.getPreferredSize( ) );
	}
	
	public float getBrightness( )
	{
		return this.brightness;
	}
	
	public Color getColor( )
	{
		Color color = Color.getHSBColor( this.hue, this.saturation, this.brightness );
		return new Color( color.getRed( ), color.getGreen( ), color.getBlue( ), this.alpha );
	}
	
	public float getHue( )
	{
		return this.hue;
	}
	
	public float getSaturation( )
	{
		return this.saturation;
	}
	
	@Override
	public void paintComponent( Graphics g )
	{
		Graphics2D g2D = (Graphics2D) g;
		
		g2D.setColor( Color.gray );
		g2D.draw3DRect( 0, 0, 17, 17, false );
		g2D.draw3DRect( 22, 0, 70, 17, false );
		g2D.draw3DRect( 97, 0, 70, 17, false );
		g2D.draw3DRect( 172, 0, 70, 17, false );
		
		g2D.setColor( Color.getHSBColor( this.hue, this.saturation, this.brightness ) );
		g2D.fillRect( 1, 1, 16, 16 );
		
		for( int i = 0; i < 69; ++i )
		{
			g2D.setColor( Color.getHSBColor( i / 69.0f, 1.0f, 1.0f ) );
			g2D.drawLine( i + 23, 1, i + 23, 16 );
		}
		
		for( int i = 0; i < 69; ++i )
		{
			g2D.setColor( Color.getHSBColor( this.hue, i / 69.0f, this.brightness ) );
			g2D.drawLine( i + 98, 1, i + 98, 16 );
		}
		
		for( int i = 0; i < 69; ++i )
		{
			g2D.setColor( Color.getHSBColor( 0.0f, 0.0f, i / 69.0f ) );
			g2D.drawLine( i + 173, 1, i + 173, 16 );
		}
		
		g2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g2D.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
		
		final int hueX = (int) Math.round( this.hue * 69.0 ) + 23;
		Path2D.Double hueHandle = new Path2D.Double( )
		{
			{
				this.moveTo( hueX, 13 );
				this.lineTo( hueX + 4, 20 );
				this.lineTo( hueX - 4, 20 );
				this.lineTo( hueX, 13 );
			}
		};
		g2D.setColor( Color.black );
		g2D.fill( hueHandle );
		g2D.setColor( Color.white );
		g2D.draw( hueHandle );
		
		final int saturationX = (int) Math.round( this.saturation * 69.0 ) + 98;
		Path2D.Double saturationHandle = new Path2D.Double( )
		{
			{
				this.moveTo( saturationX, 13 );
				this.lineTo( saturationX + 4, 20 );
				this.lineTo( saturationX - 4, 20 );
				this.lineTo( saturationX, 13 );
			}
		};
		g2D.setColor( Color.black );
		g2D.fill( saturationHandle );
		g2D.setColor( Color.white );
		g2D.draw( saturationHandle );
		
		final int brightnessX = (int) Math.round( this.brightness * 69.0 ) + 173;
		Path2D.Double brightnessHandle = new Path2D.Double( )
		{
			{
				this.moveTo( brightnessX, 13 );
				this.lineTo( brightnessX + 4, 20 );
				this.lineTo( brightnessX - 4, 20 );
				this.lineTo( brightnessX, 13 );
			}
		};
		g2D.setColor( Color.black );
		g2D.fill( brightnessHandle );
		g2D.setColor( Color.white );
		g2D.draw( brightnessHandle );
	}
	
	public void setBrightness( float value )
	{
		this.brightness = value;
	}
	
	public void setColor( Color c )
	{
		float[ ] hsb = new float[ ] { 0.0f, 0.0f, 0.0f };
		Color.RGBtoHSB( c.getRed( ), c.getGreen( ), c.getBlue( ), hsb );
		this.hue = hsb[0];
		this.saturation = hsb[1];
		this.brightness = hsb[2];
		this.alpha = c.getAlpha( );
		this.repaint( );
	}
	
	public void setHue( float value )
	{
		this.hue = value;
	}
	
	public void setSaturation( float value )
	{
		this.saturation = value;
	}
}
