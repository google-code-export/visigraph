/**
 * ThreadUtilities.java
 */
package edu.belmont.mth.visigraph.utilities;

/**
 * @author Cameron Behar
 * 
 */
public class ThreadUtilities
{
	public static abstract class ParameterizedThread extends Thread
	{
		private Object parameter;
		
		public ParameterizedThread ( Object parameter )
		{
			super( );
			this.parameter = parameter;
		}
		
		@Override
		public void run( )
		{
			run( parameter );
		}
		
		public abstract void run( Object parameter );
	}
}
