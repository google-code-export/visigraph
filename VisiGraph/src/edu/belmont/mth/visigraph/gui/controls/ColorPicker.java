/**
 * ColorPicker.java
 */
package edu.belmont.mth.visigraph.gui.controls;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * @author Cameron Behar
 *
 */
@SuppressWarnings("serial")
public class ColorPicker extends JComponent
{	
	private float hue;
	public  float getHue       (           ) { return hue;  }
	public  void  setHue       (float value) { hue = value; }
	
	private float saturation;
	public  float getSaturation(           ) { return saturation;  }
	public  void  setSaturation(float value) { saturation = value; }
	
	private float brightness;
	public  float getBrightness(           ) { return brightness;  }
	public  void  setBrightness(float value) { brightness = value; }
	
	private int   alpha;
	
	public  Color getColor(       ) { Color c = Color.getHSBColor(hue, saturation, brightness); return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha); }
	public  void  setColor(Color c) { float[] hsb = new float[] { 0.0f, 0.0f, 0.0f }; Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb); hue = hsb[0]; saturation = hsb[1]; brightness = hsb[2]; alpha = c.getAlpha(); repaint(); }
	
	private boolean isDraggingHue        = false;
	private boolean isDraggingSaturation = false;
	private boolean isDraggingBrightness = false;
	
	public ColorPicker()
	{
		this(Color.blue);
	}
	
	public ColorPicker(Color color)
	{
		this.setColor(color);
		
		super.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(e.getX() >= 23 && e.getX() <= 91)
				{
					hue = (e.getX() - 23) / 69.0f;
					repaint();
				}
				else if(e.getX() >= 98 && e.getX() <= 166)
				{
					saturation = (e.getX() - 98) / 69.0f;
					repaint();
				}
				else if(e.getX() >= 173 && e.getX() <= 241)
				{
					brightness = (e.getX() - 173) / 69.0f;
					repaint();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{ }

			@Override
			public void mouseExited(MouseEvent e)
			{ }

			@Override
			public void mousePressed(MouseEvent e)
			{
				if(e.getX() >= 23 && e.getX() <= 91)
					isDraggingHue = true;
				else if(e.getX() >= 98 && e.getX() <= 166)
					isDraggingSaturation = true;
				else if(e.getX() >= 173 && e.getX() <= 241)
					isDraggingBrightness = true;
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				isDraggingHue        = false;
				isDraggingSaturation = false;
				isDraggingBrightness = false;
			} 
		} );
		super.addMouseMotionListener(new MouseMotionListener()
		{
			@Override
			public void mouseDragged(MouseEvent e)
			{
				int effectiveX = e.getX();
				
				if(isDraggingHue)
				{
					if(effectiveX < 23) effectiveX = 23;
					else if(effectiveX > 91) effectiveX = 91;
					
					hue = (effectiveX - 23) / 69.0f;
					repaint();
				}
				else if(isDraggingSaturation)
				{
					if(effectiveX < 98) effectiveX = 98;
					else if(effectiveX > 166) effectiveX = 166;
					
					saturation = (effectiveX - 98) / 69.0f;
					repaint();
				}
				else if(isDraggingBrightness)
				{
					if(effectiveX < 173) effectiveX = 173;
					else if(effectiveX > 242) effectiveX = 241;
					
					brightness = (effectiveX - 173) / 69.0f;
					repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) { }
		} );
		
		super.setPreferredSize(new Dimension(246, 20));
		super.setMinimumSize(super.getPreferredSize());
		super.setMaximumSize(super.getPreferredSize());
		super.setSize       (super.getPreferredSize());
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2D = (Graphics2D) g;
		
		g2D.setColor(Color.gray);
		g2D.draw3DRect(  0, 0, 17, 17, false);
		g2D.draw3DRect( 22, 0, 70, 17, false);
		g2D.draw3DRect( 97, 0, 70, 17, false);
		g2D.draw3DRect(172, 0, 70, 17, false);
		
		g2D.setColor(Color.getHSBColor(hue, saturation, brightness));
		g2D.fillRect(1, 1, 16, 16);
		
		for(int i = 0; i < 69; ++i)
		{
			g2D.setColor(Color.getHSBColor(i / 69.0f, 1.0f, 1.0f));
			g2D.drawLine(i + 23, 1, i + 23, 16);
		}
		
		for(int i = 0; i < 69; ++i)
		{
			g2D.setColor(Color.getHSBColor(hue, i / 69.0f, brightness));
			g2D.drawLine(i + 98, 1, i + 98, 16);
		}
		
		for(int i = 0; i < 69; ++i)
		{
			g2D.setColor(Color.getHSBColor(0.0f, 0.0f, i / 69.0f));
			g2D.drawLine(i + 173, 1, i + 173, 16);
		}
		
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		
		int hueX = (int)Math.round(hue * 69.0) + 23;
		Path2D.Double hueHandle = new Path2D.Double();
		hueHandle.moveTo(hueX, 13);
		hueHandle.lineTo(hueX + 4, 20);
		hueHandle.lineTo(hueX - 4, 20);
		hueHandle.lineTo(hueX, 13);
		g2D.setColor(Color.black); g2D.fill(hueHandle);
		g2D.setColor(Color.white); g2D.draw(hueHandle);
		
		int saturationX = (int)Math.round(saturation * 69.0) + 98;
		Path2D.Double saturationHandle = new Path2D.Double();
		saturationHandle.moveTo(saturationX, 13);
		saturationHandle.lineTo(saturationX + 4, 20);
		saturationHandle.lineTo(saturationX - 4, 20);
		saturationHandle.lineTo(saturationX, 13);
		g2D.setColor(Color.black); g2D.fill(saturationHandle);
		g2D.setColor(Color.white); g2D.draw(saturationHandle);
		
		int brightnessX = (int)Math.round(brightness * 69.0) + 173;
		Path2D.Double brightnessHandle = new Path2D.Double();
		brightnessHandle.moveTo(brightnessX, 13);
		brightnessHandle.lineTo(brightnessX + 4, 20);
		brightnessHandle.lineTo(brightnessX - 4, 20);
		brightnessHandle.lineTo(brightnessX, 13);
		g2D.setColor(Color.black); g2D.fill(brightnessHandle);
		g2D.setColor(Color.white); g2D.draw(brightnessHandle);
	}
}
