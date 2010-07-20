/**
 * ObservableList.java
 */
package edu.belmont.mth.visigraph.models;

import java.util.*;
import edu.belmont.mth.visigraph.views.*;

/**
 * @author Cameron Behar
 *
 */
public class ObservableList<T> extends ObservableBase implements List<T>
{	
	private String name;
	private ArrayList<T> list;
	private boolean notificationsSuspended;
	private ObserverBase elementObserver;
	
 	public ObservableList(String name)
	{
 		this.name = name;
		list = new ArrayList<T>();
		notificationsSuspended = false;
		elementObserver = new ObserverBase()
		{
			@Override
			public void hasChanged(Object source)
			{
				if(!notificationsSuspended)
					notifyObservers(source);
			}	
		};
	}
	
	public boolean add(T e)
	{
		suspendNotifications(true);
			boolean ret = list.add(e);
			if(e instanceof ObservableBase)
				((ObservableBase)e).addObserver(elementObserver);
		suspendNotifications(false);
		
		notifyObservers(this);
		return ret;
	}

	public String getName()
	{
		return name;
	}
	
	public void add(int index, T element)
	{
		suspendNotifications(true);
			list.add(index, element);
			if(element instanceof ObservableBase)
				((ObservableBase)element).addObserver(elementObserver);
		suspendNotifications(false);
		
		notifyObservers(this);
	}

	public boolean addAll(Collection<? extends T> c)
	{
		suspendNotifications(true);
			boolean ret = list.addAll(c);
			for(T element : c)
				if(element instanceof ObservableBase)
					((ObservableBase)element).addObserver(elementObserver);
		suspendNotifications(false);
		
		notifyObservers(this);
		return ret;
	}

	public boolean addAll(int index, Collection<? extends T> c)
	{
		suspendNotifications(true);
			boolean ret = list.addAll(index, c);
			for(T element : c)
				if(element instanceof ObservableBase)
					((ObservableBase)element).addObserver(elementObserver);
		suspendNotifications(false);
		
		notifyObservers(this);
		return ret;
	}

	public void clear()
	{
		suspendNotifications(true);
			for(T element : list)
				if(element instanceof ObservableBase)
					((ObservableBase)element).deleteObserver(elementObserver);
			list.clear();
		suspendNotifications(false);
		
		notifyObservers(this);
	}

	public boolean contains(Object o)
	{
		return list.contains(o);
	}

	public boolean containsAll(Collection<?> c)
	{
		return list.containsAll(c);
	}

	public T get(int index)
	{
		return list.get(index);
	}

	public int indexOf(Object o)
	{
		return list.indexOf(o);
	}

	public boolean isEmpty()
	{
		return list.isEmpty();
	}

	public Iterator<T> iterator()
	{
		return list.iterator();
	}

	public int lastIndexOf(Object o)
	{
		return list.lastIndexOf(o);
	}

	public ListIterator<T> listIterator()
	{
		return list.listIterator();
	}

	public ListIterator<T> listIterator(int index)
	{
		return list.listIterator(index);
	}

	public boolean remove(Object o)
	{
		suspendNotifications(true);
			boolean ret = list.remove(o);
			if(o instanceof ObservableBase)
				((ObservableBase)o).deleteObserver(elementObserver);
		suspendNotifications(false);
		
		notifyObservers(this);
		return ret;
	}

	public T remove(int index)
	{
		suspendNotifications(true);
			T ret = list.remove(index);
			if(ret instanceof ObservableBase)
				((ObservableBase)ret).deleteObserver(elementObserver);
		suspendNotifications(false);
		
		notifyObservers(this);
		return ret;
	}

	public boolean removeAll(Collection<?> c)
	{
		suspendNotifications(true);
			for(Object element : c)
			{
				int index = list.indexOf(element);
				if(index > -1 && list.get(index) instanceof ObservableBase)
					((ObservableBase)list.get(index)).deleteObserver(elementObserver);
			}
			boolean ret = list.removeAll(c);
		suspendNotifications(false);
		
		notifyObservers(this);
		return ret;
	}

	public boolean retainAll(Collection<?> c)
	{
		suspendNotifications(true);
			for(T element : list)
				if(!c.contains(element))
					if(element instanceof ObservableBase)
						((ObservableBase)element).deleteObserver(elementObserver);
			boolean ret = list.retainAll(c);
		suspendNotifications(false);
		
		notifyObservers(this);
		return ret;
	}

	public T set(int index, T element)
	{
		suspendNotifications(true);
			if(list.get(index) != element)
			{
				if(list.get(index) instanceof ObservableBase)
					((ObservableBase)list.get(index)).deleteObserver(elementObserver);
				
				if(element instanceof ObservableBase)
					((ObservableBase)element).addObserver(elementObserver);
			}
			T ret = list.set(index, element);
		suspendNotifications(false);
		
		notifyObservers(this);
		return ret;
	}

	public int size()
	{
		return list.size();
	}
	
	public List<T> subList(int fromIndex, int toIndex)
	{
		return list.subList(fromIndex, toIndex);
	}

	public Object[] toArray()
	{
		return list.toArray();
	}

	@SuppressWarnings("hiding")
	public <T> T[] toArray(T[] a)
	{
		return (T[])list.toArray(a);
	}

	public boolean suspendNotifications(boolean s)
	{
		boolean ret = notificationsSuspended;
		notificationsSuspended = s;
		return ret;
	}
}




