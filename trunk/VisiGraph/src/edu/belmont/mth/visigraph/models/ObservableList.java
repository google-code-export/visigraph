/**
 * ObservableList.java
 */
package edu.belmont.mth.visigraph.models;

import java.util.*;
import edu.belmont.mth.visigraph.views.*;

/**
 * An {@code ObservableList} is a collection which maintains an ordering for its elements. Every element in the {@code ObservableList} has an index.
 * Each element can thus be accessed by its index, with the first index being zero. Normally, {@code ObservableList}s allow duplicate elements, as
 * compared to Sets, where elements have to be unique.
 * <p/>
 * Where {@code ObservableList} differs from the standard Java {@code List} is is in its extension of {@link ObservableBase} so that subscribed
 * {@link ObserverBase}s are notified of any changes to its structure, or to its {@code ObservableBase} elements' properties.
 * 
 * @author Cameron Behar
 * 
 * @see {@link List}, {@link ObservableBase}
 */
public class ObservableList<T> extends ObservableBase implements List<T>
{
	/**
	 * The private data store backing this {@code ObservableList}
	 */
	private ArrayList<T> list;
	
	/**
	 * A {@code boolean} flag indicating whether notifications are to be sent on to any of this {@code ObservableList}'s subscribed
	 * {@link ObserverBase}s, or merely caught and handled internally
	 */
	private boolean notificationsSuspended;
	
	/**
	 * An {@code ObserverBase} used to notify this {@code ObservableList}'s subscribed {@code ObserverBase}s of changes to any of its {@code
	 * ObservableBase} elements' properties
	 */
	private ObserverBase elementObserver;
	
	/**
	 * Constructs an empty {@code ObservableList} with an initial capacity of ten. 
	 */
	public ObservableList ( )
	{
		list = new ArrayList<T>( );
		notificationsSuspended = false;
		elementObserver = new ObserverBase( )
		{
			@Override
			public void hasChanged( Object source )
			{
				if ( !notificationsSuspended )
					notifyObservers( source );
			}
		};
	}
	
	/**
	 * Inserts the specified object into this {@code ObservableList} at the specified location. The object is inserted before the current element at
	 * the specified location. If the location is equal to the size of this {@code ObservableList}, the object is added at the end. If the location is
	 * smaller than the size of this {@code ObservableList}, then all elements beyond the specified location are moved by one position towards the end
	 * of the {@code ObservableList}.
	 * 
	 * @param location the index at which to insert
	 * @param object the object to add
	 * 
	 * @throws UnsupportedOperationException if adding to this {@code ObservableList} is not supported
	 * @throws ClassCastException if the class of the object is inappropriate for this {@code ObservableList}
	 * @throws IllegalArgumentException if the object cannot be added to this {@code ObservableList}
	 * @throws IndexOutOfBoundsException if {@code location < 0 || location > size()}
	 */
	public void add( int location, T object )
	{
		suspendNotifications( true );
		
		list.add( location, object );
		if ( object instanceof ObservableBase )
			( (ObservableBase) object ).addObserver( elementObserver );
		
		suspendNotifications( false );
		
		notifyObservers( this );
	}
	
	/**
	 * Adds the specified object at the end of this {@code ObservableList}.
	 * 
	 * @param object the object to add
	 * 
	 * @return {@code true} (as specified by {@link Collection#add(E)})
	 * 
	 * @throws UnsupportedOperationException if adding to this {@code ObservableList} is not supported
	 * @throws ClassCastException if the class of the object is inappropriate for this {@code ObservableList}
	 * @throws IllegalArgumentException if the object cannot be added to this {@code ObservableList}
	 */
	public boolean add( T object )
	{
		suspendNotifications( true );
		
		boolean ret = list.add( object );
		if ( object instanceof ObservableBase )
			( (ObservableBase) object ).addObserver( elementObserver );
		
		suspendNotifications( false );
		
		notifyObservers( this );
		
		return ret;
	}
	
	/**
	 * Adds the objects in the specified collection to the end of this {@code ObservableList}. The objects are added in the order in which they are
	 * returned from the collection's iterator.
	 * 
	 * @param collection the collection of objects
	 * 
	 * @return {@code true} if this {@code ObservableList} is modified, {@code false} otherwise (i.e. if the passed collection was empty)
	 * 
	 * @throws UnsupportedOperationException if adding to this {@code ObservableList} is not supported
	 * @throws ClassCastException if the class of an object is inappropriate for this {@code ObservableList}
	 * @throws IllegalArgumentException if an object cannot be added to this {@code ObservableList}
	 */
	public boolean addAll( Collection<? extends T> collection )
	{
		suspendNotifications( true );
		
		boolean ret = list.addAll( collection );
		for ( T element : collection )
			if ( element instanceof ObservableBase )
				( (ObservableBase) element ).addObserver( elementObserver );
		
		suspendNotifications( false );
		
		notifyObservers( this );
		
		return ret;
	}
	
	/**
	 * Inserts the objects in the specified collection at the specified location in this {@code ObservableList}. The objects are added in the order
	 * they are returned from the collection's iterator.
	 * 
	 * @param location the index at which to insert
	 * @param collection the collection of objects to be inserted
	 * 
	 * @return {@code true} if this {@code ObservableList} has been modified through the insertion, {@code false} otherwise (i.e. if the passed collection was empty)
	 * 
	 * @throws UnsupportedOperationException if adding to this {@code ObservableList} is not supported
	 * @throws ClassCastException if the class of an object is inappropriate for this {@code ObservableList}
	 * @throws IllegalArgumentException if an object cannot be added to this {@code ObservableList}
	 * @throws IndexOutOfBoundsException if {@code location < 0 || > size()}
	 */
	public boolean addAll( int location, Collection<? extends T> collection )
	{
		suspendNotifications( true );
		
		boolean ret = list.addAll( location, collection );
		for ( T element : collection )
			if ( element instanceof ObservableBase )
				( (ObservableBase) element ).addObserver( elementObserver );
		
		suspendNotifications( false );
		
		notifyObservers( this );
		
		return ret;
	}
	
	/**
	 * Removes all elements from this {@code ObservableList}, leaving it empty.
	 * 
	 * @throws UnsupportedOperationException if removing from this {@code ObservableList} is not supported
	 * 
	 * @see #isEmpty
	 * @see #size
	 */
	public void clear( )
	{
		suspendNotifications( true );
		
		for ( T element : list )
			if ( element instanceof ObservableBase )
				( (ObservableBase) element ).deleteObserver( elementObserver );
		list.clear( );
		
		suspendNotifications( false );
		
		notifyObservers( this );
	}
	
	/**
	 * Tests whether this {@code ObservableList} contains the specified object.
	 * 
	 * @param object the object to search for
	 * 
	 * @return {@code true} if object is an element of this {@code ObservableList}, {@code false} otherwise
	 */
	public boolean contains( Object object )
	{
		return list.contains( object );
	}
	
	/**
	 * Tests whether this {@code ObservableList} contains all objects contained in the specified collection.
	 * 
	 * @param collection the collection of objects
	 * 
	 * @return {@code true} if all objects in the specified collection are elements of this {@code ObservableList}, {@code false} otherwise
	 */
	public boolean containsAll( Collection<?> collection )
	{
		return list.containsAll( collection );
	}
	
	/**
	 * Returns the element at the specified location in this {@code ObservableList}.
	 * 
	 * @param location the index of the element to return
	 * 
	 * @return the element at the specified location
	 * 
	 * @throws IndexOutOfBoundsException if {@code location < 0 || >= size()}
	 */
	public T get( int location )
	{
		return list.get( location );
	}
	
	/**
	 * Searches this {@code ObservableList} for the specified object and returns the index of the first occurrence.
	 * 
	 * @param object the object to search for
	 * 
	 * @return the index of the first occurrence of the object or -1 if the object was not found
	 */
	public int indexOf( Object object )
	{
		return list.indexOf( object );
	}
	
	/**
	 * Returns whether this {@code ObservableList} contains no elements.
	 * 
	 * @return {@code true} if this {@code ObservableList} has no elements, {@code false} otherwise
	 * 
	 * @see #size
	 */
	public boolean isEmpty( )
	{
		return list.isEmpty( );
	}
	
	/**
	 * Returns an iterator on the elements of this {@code ObservableList}. The elements are iterated in the same order as they occur in the {@code
	 * ObservableList}.
	 * 
	 * @return an iterator on the elements of this {@code ObservableList}
	 * 
	 * @see Iterator
	 */
	public Iterator<T> iterator( )
	{
		return list.iterator( );
	}
	
	/**
	 * Searches this {@code ObservableList} for the specified object and returns the index of the last occurrence.
	 * 
	 * @param object the object to search for
	 * 
	 * @return the index of the last occurrence of the object, or -1 if the object was not found
	 */
	public int lastIndexOf( Object object )
	{
		return list.lastIndexOf( object );
	}
	
	/**
	 * Returns an {@code ObservableList} iterator on the elements of this {@code ObservableList}. The elements are iterated in the same order that
	 * they occur in the {@code ObservableList}.
	 * 
	 * @return an {@code ObservableList} iterator on the elements of this {@code ObservableList}
	 * 
	 * @see ListIterator
	 */
	public ListIterator<T> listIterator( )
	{
		return list.listIterator( );
	}
	
	/**
	 * Returns a list iterator on the elements of this {@code ObservableList}. The elements are iterated in the same order as they occur in the
	 * {@code ObservableList}. The iteration starts at the specified location.
	 * 
	 * @param location the index at which to start the iteration
	 * 
	 * @return a list iterator on the elements of this {@code ObservableList}
	 * 
	 * @throws IndexOutOfBoundsException if {@code location < 0 || location > size()}
	 * 
	 * @see ListIterator
	 */
	public ListIterator<T> listIterator( int location )
	{
		return list.listIterator( location );
	}
	
	/**
	 * Removes the object at the specified location from this {@code ObservableList}.
	 * 
	 * @param location the index of the object to remove
	 * 
	 * @return the removed object
	 * 
	 * @throws UnsupportedOperationException if removing from this {@code ObservableList} is not supported
	 * @throws IndexOutOfBoundsException if {@code location < 0 || >= size()}
	 */
	public T remove( int location )
	{
		suspendNotifications( true );
		
		T ret = list.remove( location );
		if ( ret instanceof ObservableBase )
			( (ObservableBase) ret ).deleteObserver( elementObserver );
		
		suspendNotifications( false );
		
		notifyObservers( this );
		
		return ret;
	}
	
	/**
	 * Removes the first occurrence of the specified object from this {@code ObservableList}.
	 * 
	 * @param object the object to remove
	 * 
	 * @return true if this {@code ObservableList} was modified by this operation, false otherwise
	 * 
	 * @throws UnsupportedOperationException if removing from this {@code ObservableList} is not supported
	 */
	public boolean remove( Object object )
	{
		suspendNotifications( true );
		
		boolean ret = list.remove( object );
		if ( object instanceof ObservableBase )
			( (ObservableBase) object ).deleteObserver( elementObserver );
		
		suspendNotifications( false );
		
		notifyObservers( this );
		
		return ret;
	}
	
	/**
	 * Removes all occurrences in this {@code ObservableList} of each object in the specified collection.
	 * 
	 * @param collection the collection of objects to remove
	 * 
	 * @return {@code true} if this {@code ObservableList} is modified, {@code false} otherwise
	 * 
	 * @throws UnsupportedOperationException if removing from this {@code ObservableList} is not supported
	 */
	public boolean removeAll( Collection<?> collection )
	{
		suspendNotifications( true );
		
		for ( Object element : collection )
		{
			int index = list.indexOf( element );
			if ( index > -1 && list.get( index ) instanceof ObservableBase )
				( (ObservableBase) list.get( index ) ).deleteObserver( elementObserver );
		}
		boolean ret = list.removeAll( collection );
		
		suspendNotifications( false );
		
		notifyObservers( this );
		
		return ret;
	}
	
	/**
	 * Removes all objects from this {@code ObservableList} that are not contained in the specified collection.
	 * 
	 * @param collection the collection of objects to retain
	 * 
	 * @return {@code true} if this {@code ObservableList} is modified, {@code false} otherwise
	 * 
	 * @throws UnsupportedOperationException if removing from this {@code ObservableList} is not supported
	 */
	public boolean retainAll( Collection<?> collection )
	{
		suspendNotifications( true );
		
		for ( T element : list )
			if ( !collection.contains( element ) )
				if ( element instanceof ObservableBase )
					( (ObservableBase) element ).deleteObserver( elementObserver );
		boolean ret = list.retainAll( collection );
		
		suspendNotifications( false );
		
		notifyObservers( this );
		
		return ret;
	}
	
	/**
	 * Replaces the element at the specified location in this {@code ObservableList} with the specified object. This operation does not change the
	 * size of the {@code ObservableList}.
	 * 
	 * @param location the index at which to put the specified object
	 * @param object the object to insert
	 * 
	 * @return the previous element at the index
	 * 
	 * @throws UnsupportedOperationException if replacing elements in this {@code ObservableList} is not supported
	 * @throws ClassCastException if the class of an object is inappropriate for this {@code ObservableList}
	 * @throws IllegalArgumentException if an object cannot be added to this {@code ObservableList}
	 * @throws IndexOutOfBoundsException if {@code location < 0 || >= size()}
	 */
	public T set( int location, T object )
	{
		suspendNotifications( true );
		
		if ( list.get( location ) != object )
		{
			if ( list.get( location ) instanceof ObservableBase )
				( (ObservableBase) list.get( location ) ).deleteObserver( elementObserver );
			
			if ( object instanceof ObservableBase )
				( (ObservableBase) object ).addObserver( elementObserver );
		}
		T ret = list.set( location, object );
		
		suspendNotifications( false );
		
		notifyObservers( this );
		
		return ret;
	}
	
	/**
	 * Returns the number of elements in this {@code ObservableList}.
	 * 
	 * @return the number of elements in this {@code ObservableList}
	 */
	public int size( )
	{
		return list.size( );
	}
	
	/**
	 * Returns an {@code ObservableList} of the specified portion of this {@code ObservableList} from the given start index to the end index minus
	 * one. The returned {@code ObservableList} is backed by this {@code ObservableList} so changes to it are reflected by the other.
	 * 
	 * @param start the index at which to start the sublist
	 * @param end the index one past the end of the sublist
	 * 
	 * @return a list of a portion of this {@code ObservableList}
	 * 
	 * @throws IndexOutOfBoundsException if {@code start < 0, start > end} or {@code end > size()}
	 */
	public List<T> subList( int start, int end )
	{
		return list.subList( start, end );
	}
	
	/**
	 * Temporarily suspends the notification all of property changes to subscribed {@link ObserverBase}s. Most often this method is called when
	 * performing a large number of batch operations on an {@code ObservableList}, so that subscribers are not overloaded with a multitude of
	 * notifications.
	 * 
	 * @param suspend a {@code boolean} indicating whether to suspend or reenable notifications to subscribed ObserverBases
	 * 
	 * @return {@code true} if notifications were previously suspended, {@code false} otherwise
	 * 
	 * @see {@link ObservableBase}, {@link ObserverBase}, {@link ObservableList}
	 */
	public boolean suspendNotifications( boolean suspend )
	{
		boolean ret = notificationsSuspended;
		notificationsSuspended = suspend;
		return ret;
	}
	
	/**
	 * Returns an array containing all elements contained in this {@code ObservableList}.
	 * 
	 * @return an array of the elements from this {@code ObservableList}
	 */
	public Object[ ] toArray( )
	{
		return list.toArray( );
	}
	
	/**
	 * Returns an array containing all elements contained in this {@code ObservableList}. If the specified array is large enough to hold the elements,
	 * the specified array is used, otherwise an array of the same type is created. If the specified array is used and is larger than this {@code
	 * ObservableList}, the array element following the collection elements is set to null.
	 * 
	 * @param array the array
	 * 
	 * @return an array of the elements from this {@code ObservableList}
	 * 
	 * @throws ArrayStoreException if the type of an element in this {@code ObservableList} cannot be stored in the type of the specified array
	 */
	@SuppressWarnings( "hiding" )
	public <T> T[ ] toArray( T[ ] array )
	{
		return list.toArray( array );
	}
}
