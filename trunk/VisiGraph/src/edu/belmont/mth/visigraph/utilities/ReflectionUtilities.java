/**
 * ReflectionUtilities.java
 */
package edu.belmont.mth.visigraph.utilities;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

/**
 * @author Cameron Behar
 * 
 */
public class ReflectionUtilities
{
	@SuppressWarnings( "unchecked" )
	public static List<Class> getClasses( String packageName ) throws ClassNotFoundException, IOException, URISyntaxException
	{
		String path = packageName.replace( '.', '/' );
		
		ClassLoader loader = Thread.currentThread( ).getContextClassLoader( );
		if ( loader == null )
			throw new ClassNotFoundException( "An exception occurred while fetching the class loader." );
		
		URL resource = loader.getResource( path );
		if ( resource == null )
			throw new ClassNotFoundException( "No such resource '" + path + "'" );
		
		List<String> classNames = new ArrayList<String>( );
		
		if ( resource.getProtocol( ).equals( "file" ) )
		{
			File directory = new File( resource.toURI( ).getPath( ) );
			
			if ( directory.exists( ) )
				for ( String file : directory.list( ) )
					if ( file.endsWith( ".class" ) )
						classNames.add( packageName + '.' + file.substring( 0, file.length( ) - 6 ) );
		}
		else if ( resource.getProtocol( ).equals( "jar" ) )
		{
			String jarFilePath = new URL( resource.getPath( ) ).toURI( ).getPath( );
			Enumeration entries = new JarFile( jarFilePath.substring( 1, jarFilePath.lastIndexOf( '!' ) ) ).entries( );
			
			while ( entries.hasMoreElements( ) )
			{
				String entryName = ( (JarEntry) entries.nextElement( ) ).getName( );
				if ( entryName.matches( path + "/.*\\.class" ) )
					classNames.add( packageName + '.' + entryName.substring( entryName.lastIndexOf( "/" ) + 1, entryName.length( ) - 6 ) );
			}
		}
		else
			throw new ClassNotFoundException( packageName + " does not appear to be a valid package" );
		
		ArrayList<Class> classes = new ArrayList<Class>( );
		for ( String className : classNames )
			classes.add( Class.forName( className ) );
		
		return classes;
	}
}
