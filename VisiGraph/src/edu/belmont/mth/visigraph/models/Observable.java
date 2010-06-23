package edu.belmont.mth.visigraph.models;

import java.util.Vector;

import edu.belmont.mth.visigraph.views.Observer;

public class Observable
{
	// Should really be protected, but inter-package access requires it be public (idk?)
	public class Property<T>
	{
		protected T			value;
		protected T			defaultValue;
		protected String	name;
		protected boolean	notificationsSuspended	= false;
		protected Observer	valueObserver;
		
		public Property(final T initialValue, final String name)
		{
			this.name = name;
			valueObserver = new Observer()
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
				if (this.value instanceof Observable)
					((Observable) this.value).deleteObserver(valueObserver);
				this.value = value;
				if (value instanceof Observable)
					((Observable) value).addObserver(valueObserver);
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
	
	protected Vector<Observer>	observers	= new Vector<Observer>();
	
	public void addObserver(Observer o)
	{
		observers.add(o);
		notifyObserver(o, this);
	}
	
	public void deleteObserver(Observer o)
	{
		observers.remove(o);
	}
	
	public void notifyObserver(Observer o, Object source)
	{
		o.hasChanged(source);
	}
	
	public void notifyObservers(Object source)
	{
		for (Observer observer : observers)
			notifyObserver(observer, source);
	}
}
