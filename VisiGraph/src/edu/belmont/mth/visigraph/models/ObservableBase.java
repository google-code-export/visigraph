/**
 * ObservableBase.java
 */
package edu.belmont.mth.visigraph.models;

import java.util.*;
import edu.belmont.mth.visigraph.views.ObserverBase;

public abstract class ObservableBase
{
	// Should really be protected, but inter-package access requires it be public (idk?)
	public class Property<T>
	{
		protected T			   value;
		protected T			   defaultValue;
		protected String	   name;
		protected boolean	   notificationsSuspended	= false;
		protected ObserverBase valueObserver;
		
		public Property(final T initialValue, final String name)
		{
			this.name = name;
			valueObserver = new ObserverBase()
			{
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
	
	private Vector<ObserverBase> observers = new Vector<ObserverBase>();
	
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
