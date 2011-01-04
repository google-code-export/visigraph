/**
 * ObservableModel.java
 */
package edu.belmont.mth.visigraph.models;

import java.util.*;

/**
 * The {@code ObservableModel} class is an extension of {@code Observable}, which provides a minimal implementation of the Observer pattern in Java.
 * In addition, the {@code ObservableModel} class also provides an internal {@code Property} class, which allows for the notification of property
 * changes to subscribed {@code Observers}.
 * 
 * @author Cameron Behar
 * @see Observable
 * @see Observer
 * @see Property
 */
public class ObservableModel extends Observable
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
	 * When used inside an {@link ObservableModel}, {@code Property} members will also notify their parent of any changes to their values or—if the
	 * values also extend {@code ObservableModel}—their values' {@code Property} members. These notifications may then be subscribed to- and listened
	 * to- by any and all {@link Observer}s.
	 * 
	 * @author Cameron Behar
	 * @see ObservableModel
	 * @see Observer
	 */
	public class Property<T>
	{
		/**
		 * The value set by {@link #set(Object)}, and gotten by {@link #get()}
		 */
		private T				value;
		
		/**
		 * The default value set in this {@code Property}'s constructor and made accessible through {@link #getDefault()}
		 * 
		 * @see {@link #reset()}
		 */
		private final T			defaultValue;
		
		/**
		 * A {@code boolean} flag indicating whether notifications are to be sent on to any of this {@code Property}'s subscribed {@link Observer}s,
		 * or merely caught and handled internally
		 */
		private boolean			notificationsSuspended;
		
		/**
		 * An {@code Observer} used to notify this {@code Property}'s subscribed {@code Observer}s of changes to any of its value's properties
		 * if the value extends {@code Observable}
		 */
		private final Observer	valueObserver;
		
		/**
		 * Constructs a {@code Property} with the specified default value
		 * 
		 * @param defaultValue the initial value of this property later used in {@link #reset()}
		 */
		public Property( final T defaultValue )
		{
			this.notificationsSuspended = false;
			this.valueObserver = new Observer( )
			{
				@Override
				public void update( Observable o, Object arg )
				{
					ObservableModel.this.setChanged( );
					ObservableModel.this.notifyObservers( arg );
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
		 * Gets this {@code Property}'s default value, as specified in the constructor
		 */
		public T getDefault( )
		{
			return this.defaultValue;
		}
		
		/**
		 * Resets this {@code Property}'s value back to the default value specified in the constructor and notifies any subscribed {@code Observer}s
		 * of the change
		 */
		public void reset( )
		{
			this.set( this.defaultValue );
		}
		
		/**
		 * Sets this {@code Property}'s value to the specified object and notifies any subscribed {@code Observer}s of the change
		 * 
		 * @param value the object to assign to this {@code Property}'s value
		 */
		public void set( final T value )
		{
			if( value instanceof Number )
			{
				Number num = (Number) value;
				if( Double.isInfinite( num.doubleValue( ) ) || Double.isNaN( num.doubleValue( ) ) )
					return;
			}
			
			if( this.value != value )
			{
				this.suspendNotifications( true );
				
				if( this.value instanceof Observable )
					( (Observable) this.value ).deleteObserver( this.valueObserver );
				
				this.value = value;
				
				if( value instanceof Observable )
					( (Observable) value ).addObserver( this.valueObserver );
				
				this.suspendNotifications( false );
				
				ObservableModel.this.setChanged( );
				ObservableModel.this.notifyObservers( this );
			}
		}
		
		/**
		 * Temporarily suspends the notification all of property changes to subscribed {@link Observer}s. Most often this method is called when
		 * performing a large number of batch operations on a {@code Property}, so that subscribers are not overloaded with a multitude of
		 * notifications.
		 * 
		 * @param suspend a {@code boolean} indicating whether to suspend or reenable notifications to subscribed Observers
		 * @return {@code true} if notifications were previously suspended, {@code false} otherwise
		 * @see ObservableModel
		 * @see Observer
		 * @see ObservableList
		 */
		public boolean suspendNotifications( boolean suspend )
		{
			boolean ret = this.notificationsSuspended;
			this.notificationsSuspended = suspend;
			return ret;
		}
	}
}
