/**
 * ObservableList.java
 */
package edu.belmont.mth.visigraph.models;

import java.util.*;

/**
 * An {@code ObservableList} is a collection which maintains an ordering for its elements. Every element in the {@code ObservableList} has an index.
 * Each element can thus be accessed by its index, with the first index being zero. Normally, {@code ObservableList}s allow duplicate elements, as
 * compared to Sets, where elements have to be unique.
 * <p/>
 * Where {@code ObservableList} differs from the standard Java {@code List} is is in its extension of Java's {@link Observable} so that subscribed
 * {@link Observer}s are notified of any changes to its structure, or to its {@code Observable} elements' properties.
 * 
 * @author Cameron Behar
 * @see List
 * @see Observable
 */
public class ObservableList<T> extends Observable implements List<T>
{
	/**
	 * The private data store backing this {@code ObservableList}
	 */
	private final List<T>	list;
	
	/**
	 * A {@code boolean} flag indicating whether notifications are to be sent on to any of this {@code ObservableList}'s subscribed {@link Observer}s,
	 * or merely caught and handled internally
	 */
	private boolean			notificationsSuspended;
	
	/**
	 * An {@code Observer} used to notify this {@code ObservableList}'s subscribed {@code Observer}s of changes to any of its {@code Observable}
	 * elements' properties
	 */
	private final Observer	elementObserver;
	
	/**
	 * Constructs an empty {@code ObservableList} with an initial capacity of ten.
	 */
	public ObservableList( )
	{
		this.list = new ArrayList<T>( );
		this.notificationsSuspended = false;
		this.elementObserver = new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				if( !ObservableList.this.notificationsSuspended )
				{
					ObservableList.this.setChanged( );
					ObservableList.this.notifyObservers( arg );
				}
			}
		};
	}
	
	@Override
	public void add( int location, T object )
	{
		this.suspendNotifications( true );
		
		this.list.add( location, object );
		if( object instanceof Observable )
			( (Observable) object ).addObserver( this.elementObserver );
		
		this.suspendNotifications( false );
		
		this.setChanged( );
		this.notifyObservers( this );
	}
	
	@Override
	public boolean add( T object )
	{
		this.suspendNotifications( true );
		
		boolean ret = this.list.add( object );
		if( object instanceof Observable )
			( (Observable) object ).addObserver( this.elementObserver );
		
		this.suspendNotifications( false );
		
		this.setChanged( );
		this.notifyObservers( this );
		
		return ret;
	}
	
	@Override
	public boolean addAll( Collection<? extends T> collection )
	{
		this.suspendNotifications( true );
		
		boolean ret = this.list.addAll( collection );
		for( T element : collection )
			if( element instanceof Observable )
				( (Observable) element ).addObserver( this.elementObserver );
		
		this.suspendNotifications( false );
		
		this.setChanged( );
		this.notifyObservers( this );
		
		return ret;
	}
	
	@Override
	public boolean addAll( int location, Collection<? extends T> collection )
	{
		this.suspendNotifications( true );
		
		boolean ret = this.list.addAll( location, collection );
		for( T element : collection )
			if( element instanceof Observable )
				( (Observable) element ).addObserver( this.elementObserver );
		
		this.suspendNotifications( false );
		
		this.setChanged( );
		this.notifyObservers( this );
		
		return ret;
	}
	
	@Override
	public void clear( )
	{
		this.suspendNotifications( true );
		
		for( T element : this.list )
			if( element instanceof Observable )
				( (Observable) element ).deleteObserver( this.elementObserver );
		this.list.clear( );
		
		this.suspendNotifications( false );
		
		this.setChanged( );
		this.notifyObservers( this );
	}
	
	@Override
	public boolean contains( Object object )
	{
		return this.list.contains( object );
	}
	
	@Override
	public boolean containsAll( Collection<?> collection )
	{
		return this.list.containsAll( collection );
	}
	
	@Override
	public T get( int location )
	{
		return this.list.get( location );
	}
	
	@Override
	public int indexOf( Object object )
	{
		return this.list.indexOf( object );
	}
	
	@Override
	public boolean isEmpty( )
	{
		return this.list.isEmpty( );
	}
	
	@Override
	public Iterator<T> iterator( )
	{
		return this.list.iterator( );
	}
	
	@Override
	public int lastIndexOf( Object object )
	{
		return this.list.lastIndexOf( object );
	}
	
	@Override
	public ListIterator<T> listIterator( )
	{
		return this.list.listIterator( );
	}
	
	@Override
	public ListIterator<T> listIterator( int location )
	{
		return this.list.listIterator( location );
	}
	
	@Override
	public T remove( int location )
	{
		this.suspendNotifications( true );
		
		T ret = this.list.remove( location );
		if( ret instanceof Observable )
			( (Observable) ret ).deleteObserver( this.elementObserver );
		
		this.suspendNotifications( false );
		
		this.setChanged( );
		this.notifyObservers( this );
		
		return ret;
	}
	
	@Override
	public boolean remove( Object object )
	{
		this.suspendNotifications( true );
		
		boolean ret = this.list.remove( object );
		if( object instanceof Observable )
			( (Observable) object ).deleteObserver( this.elementObserver );
		
		this.suspendNotifications( false );
		
		this.setChanged( );
		this.notifyObservers( this );
		
		return ret;
	}
	
	@Override
	public boolean removeAll( Collection<?> collection )
	{
		this.suspendNotifications( true );
		
		for( Object element : collection )
		{
			int index = this.list.indexOf( element );
			if( index > -1 && this.list.get( index ) instanceof Observable )
				( (Observable) this.list.get( index ) ).deleteObserver( this.elementObserver );
		}
		boolean ret = this.list.removeAll( collection );
		
		this.suspendNotifications( false );
		
		this.setChanged( );
		this.notifyObservers( this );
		
		return ret;
	}
	
	@Override
	public boolean retainAll( Collection<?> collection )
	{
		this.suspendNotifications( true );
		
		for( T element : this.list )
			if( !collection.contains( element ) )
				if( element instanceof Observable )
					( (Observable) element ).deleteObserver( this.elementObserver );
		boolean ret = this.list.retainAll( collection );
		
		this.suspendNotifications( false );
		
		this.setChanged( );
		this.notifyObservers( this );
		
		return ret;
	}
	
	@Override
	public T set( int location, T object )
	{
		this.suspendNotifications( true );
		
		if( this.list.get( location ) != object )
		{
			if( this.list.get( location ) instanceof Observable )
				( (Observable) this.list.get( location ) ).deleteObserver( this.elementObserver );
			
			if( object instanceof Observable )
				( (Observable) object ).addObserver( this.elementObserver );
		}
		T ret = this.list.set( location, object );
		
		this.suspendNotifications( false );
		
		this.setChanged( );
		this.notifyObservers( this );
		
		return ret;
	}
	
	@Override
	public int size( )
	{
		return this.list.size( );
	}
	
	@Override
	public List<T> subList( int start, int end )
	{
		return this.list.subList( start, end );
	}
	
	/**
	 * Temporarily suspends the notification all of property changes to subscribed {@link Observer}s. Most often this method is called when
	 * performing a large number of batch operations on an {@code ObservableList}, so that subscribers are not overloaded with a multitude of
	 * notifications.
	 * 
	 * @param suspend a {@code boolean} indicating whether to suspend or reenable notifications to subscribed {@code Observer}s
	 * @return {@code true} if notifications were previously suspended, {@code false} otherwise
	 * @see Observable
	 * @see Observer
	 * @see ObservableList
	 */
	public boolean suspendNotifications( boolean suspend )
	{
		boolean ret = this.notificationsSuspended;
		this.notificationsSuspended = suspend;
		return ret;
	}
	
	@Override
	public Object[ ] toArray( )
	{
		return this.list.toArray( );
	}
	
	@Override
	@SuppressWarnings( "hiding" )
	public <T> T[ ] toArray( T[ ] array )
	{
		return this.list.toArray( array );
	}
}
