/**
 * ObservableBase.java
 */
package edu.belmont.mth.visigraph.models;

import java.util.*;
import edu.belmont.mth.visigraph.views.*;

public abstract class ObservableBase
{
	public class Property<T>
	{
		private T			 value;
		private T			 defaultValue;
		private String	     name;
		private boolean	     notificationsSuspended	= false;
		private ObserverBase valueObserver;
		
		public Property(final T initialValue, final String name)
		{
			this.name = name;
			valueObserver = new ObserverBase()
			{
				@Override
				public void hasChanged(Object source)
				{
					notifyObservers(source);
				}
			};
			defaultValue = initialValue;
			set(initialValue);
		}
		
		public T get()
		{
			return this.value;
		}
		
		public String getName()
		{
			return name;
		}
		
		public void hasChanged(String msg)
		{
			notifyObservers(msg);
		}
		
		public void reset()
		{
			this.set(defaultValue);
		}
		
		public void set(final T value)
		{
			if (value instanceof Number)
			{
				Number num = (Number) value;
				if (num.doubleValue() < -Integer.MIN_VALUE || num.doubleValue() > Integer.MAX_VALUE) return;
				if (Double.isInfinite(num.doubleValue()) || Double.isNaN(num.doubleValue()))         return;
			}
			
			if (this.value != value)
			{				
				suspendNotifications(true);
				if (this.value instanceof ObservableBase)
					((ObservableBase) this.value).deleteObserver(valueObserver);
				this.value = value;
				if (value instanceof ObservableBase)
					((ObservableBase) value).addObserver(valueObserver);
				suspendNotifications(false);
				
				notifyObservers(this);
			}
		}
		
		public boolean suspendNotifications(boolean s)
		{
			boolean ret = notificationsSuspended;
			notificationsSuspended = s;
			return ret;
		}
	}
	
	private Set<ObserverBase> observers = new HashSet<ObserverBase>();
	
	public void addObserver(ObserverBase o)
	{
		observers.add(o);
		notifyObserver(o, this);
	}
	
	public void deleteObserver(ObserverBase o)
	{
		observers.remove(o);
	}
	
	public void notifyObserver(ObserverBase o, Object source)
	{
		o.hasChanged(source);
	}
	
	public void notifyObservers(Object source)
	{
		for (ObserverBase observer : observers)
			notifyObserver(observer, source);
	}
}
