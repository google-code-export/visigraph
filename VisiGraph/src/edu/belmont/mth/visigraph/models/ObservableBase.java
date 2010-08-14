/**
 * ObservableBase.java
 */
package edu.belmont.mth.visigraph.models;

import java.util.*;

import edu.belmont.mth.visigraph.views.*;

/**
 * This class represents an observable object, or "data" in the model-view paradigm. It can be subclassed to represent an object that the application
 * wants to have observed.
 * <p/>
 * An observable object can have one or more observers. An observer may be any object that implements interface {@code ObserverBase}. After an
 * observable instance changes, an application calling the {@code ObservableBase}'s {@code notifyObservers} method causes all of its observers to be
 * notified of the change by a call to their {@code hasChanged} method.
 * <p/>
 * The order in which notifications will be delivered is unspecified.
 * <p/>
 * Note that this notification mechanism is has nothing to do with threads and is completely separate from the wait and notify mechanism of class
 * {@code Object}.
 * <p/>
 * When an observable object is newly created, its set of observers is empty. Two observers are considered the same if and only if the equals method
 * returns {@code true} for them.
 * 
 * @author Cameron Behar
 * 
 * @see {@link ObserverBase}
 */
public abstract class ObservableBase
{
	/**
	 * According to Wikipedia's page on properties:
	 * <blockquote>
	 * A <i>property</i>, in some object-oriented programming languages, is a special sort of class member, intermediate between a field (or data
	 * member) and a method. Properties are read and written like fields, but property reads and writes are (usually) translated to get and set method
	 * calls. The field-like syntax is said to be easier to read and write than lots of method calls, yet the interposition of method calls allows for
	 * data validation, active updating (as of GUI visuals), and/or read-only 'fields'.
	 * </blockquote>
	 * Unfortunately, Java supports neither native properties, nor operator overloading. Therefore the {@code Property} class has been provided to
	 * implement minimal accessor/mutator functionality with the least boilerplate code possible, and to allow for property-change notifications
	 * consistent with a simplified Observer-Observable pattern. When used inside a class, {@code Property} members may be declared
	 * publicly, so long as they are also declared as final. This, however, does not make a {@code Property} read-only, as just its reference—which
	 * need only be set once anyway—is read-only, not its value.
	 * <p/>
	 * When used inside an {@link ObservableBase}, {@code Property} members will also notify their parent of any changes to their values or—if the
	 * values also extend {@code ObservableBase}—their values' {@code Property} members. These notifications may then be subscribed to- and listened
	 * to- by any and all {@link ObserverBase}s.
	 * 
	 * @author Cameron Behar
	 * 
	 * @see {@link ObservableBase}, {@link ObserverBase}
	 */
	public class Property<T>
	{
		/**
		 * The value set by {@link #set(Object)}, and gotten by {@link #get()}
		 */
		private T value;
		
		/**
		 * The default value set in this {@code Property}'s constructor and made accessible through {@link #getDefault()}
		 * 
		 * @see {@link #reset()}
		 */
		private T defaultValue;
		
		/**
		 * A {@code boolean} flag indicating whether notifications are to be sent on to any of this {@code Property}'s subscribed {@link ObserverBase}
		 * s, or merely caught and handled internally
		 */
		private boolean notificationsSuspended;
		
		/**
		 * An {@code ObserverBase} used to notify this {@code Property}'s subscribed {@code ObserverBase}s of changes to any of its value's properties
		 * if the value extends {@code ObservableBase}
		 */
		private ObserverBase valueObserver;
		
		/**
		 * Constructs a {@code Property} with the specified default value
		 * 
		 * @param defaultValue the initial value of this property later used in {@link #reset()}
		 */
		public Property ( final T defaultValue )
		{
			this.notificationsSuspended = false;
			this.valueObserver = new ObserverBase( )
			{
				@Override
				public void hasChanged( Object source )
				{
					notifyObservers( source );
				}
			};
			this.defaultValue = defaultValue;
			this.set( defaultValue );
		}
		
		/**
		 * Gets this {@code Property}'s value
		 */
		public T get( )
		{
			return this.value;
		}
		
		/**
		 * Gets this {@code Property}'s default value, as specified in {@link #Property(Object)}
		 */
		public T getDefault( )
		{
			return defaultValue;
		}
		
		/**
		 * Resets this {@code Property}'s value back to the default value specified in the constructor and notifies any subscribed {@code
		 * ObserverBase}s of the change
		 */
		public void reset( )
		{
			this.set( defaultValue );
		}
		
		/**
		 * Sets this {@code Property}'s value to the specified object and notifies any subscribed {@code ObserverBase}s of the change
		 * 
		 * @param value the object to assign to this {@code Property}'s value
		 */
		public void set( final T value )
		{
			if ( value instanceof Number )
			{
				Number num = (Number) value;
				if ( num.doubleValue( ) < -Integer.MIN_VALUE || num.doubleValue( ) > Integer.MAX_VALUE )
					return;
				if ( Double.isInfinite( num.doubleValue( ) ) || Double.isNaN( num.doubleValue( ) ) )
					return;
			}
			
			if ( this.value != value )
			{
				suspendNotifications( true );
				
				if ( this.value instanceof ObservableBase )
					( (ObservableBase) this.value ).deleteObserver( valueObserver );
				
				this.value = value;
				
				if ( value instanceof ObservableBase )
					( (ObservableBase) value ).addObserver( valueObserver );
				
				suspendNotifications( false );
				
				notifyObservers( this );
			}
		}
		
		/**
		 * Temporarily suspends the notification all of property changes to subscribed {@link ObserverBase}s. Most often this method is called when
		 * performing a large number of batch operations on a {@code Property}, so that subscribers are not overloaded with a multitude of
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
	}
	
	/**
	 * A set of this {@code ObservableBase}'s subscribed {@code ObserverBase}s
	 * 
	 * @see {@link ObserverBase}
	 */
	private Set<ObserverBase> observers;
	
	/**
	 * Constructs a standard {@code ObservableBase} and initializes an empty set of subscribed {@code ObserverBase}s
	 * 
	 * @see {@link ObserverBase}
	 */
	public ObservableBase ( )
	{
		observers = new HashSet<ObserverBase>( );
	}
	
	/**
	 * Adds the specified observer to this {@code ObservableBase}'s set of subscribed observers, then notifies the observer of the new subscription
	 * 
	 * @param observer the new {@code ObserverBase} to subscribe to this {@code ObservableBase}'s property-change notifications
	 * 
	 * @see {@link #deleteObserver(ObserverBase)}, {@link ObserverBase}
	 */
	public void addObserver( ObserverBase observer )
	{
		observers.add( observer );
		notifyObserver( observer, this );
	}
	
	/**
	 * Removes the specified observer from this {@code ObservableBase}'s set of subscribed observers
	 * 
	 * @param observer the {@code ObserverBase} to un-subscribe from this {@code ObservableBase}'s property-change notifications
	 * 
	 * @see {@link #addObserver(ObserverBase)}, {@link ObserverBase}
	 */
	public void deleteObserver( ObserverBase observer )
	{
		observers.remove( observer );
	}
	
	/**
	 * Notifies the specified observer of changes made to the specified object
	 * 
	 * @param observer the {@code ObserverBase} to receive notification of the changes
	 * @param source the {@code Object} whose change prompted this notification
	 * 
	 * @see {@link #notifyObservers(Object)}, {@link ObserverBase}
	 */
	public void notifyObserver( ObserverBase observer, Object source )
	{
		observer.hasChanged( source );
	}
	
	/**
	 * Notifies all subscribed {@code ObserverBase}s of changes made to the specified object
	 * 
	 * @param source the {@code Object} whose change prompted this notification
	 * 
	 * @see {@link ObserverBase}
	 */
	public void notifyObservers( Object source )
	{
		for ( ObserverBase observer : observers )
			notifyObserver( observer, source );
	}
}
