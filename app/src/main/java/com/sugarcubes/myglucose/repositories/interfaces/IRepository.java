//--------------------------------------------------------------------------------------//
//																						//
// File Name:	IRepository.java														//
// Programmer:	J.T. Blevins (jt.blevins@gmail.com)										//
// Date:		09/08/2018																//
// Purpose:		An interface to model all repositories which need to access data from a //
// 				source other than from memory (although a memory implementation is 		//
//				possible). All other repository interfaces in the application should 	//
// 				extend this interface with the correct type (replacing T). This keeps	//
//				the classes consistent, and can easily be swapped out for other 		//
// 				implementations if needed.												//
//																						//
//--------------------------------------------------------------------------------------//

package com.sugarcubes.myglucose.repositories.interfaces;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

public interface IRepository<T>
{
	void create( T item );

	T read( String id );

	ArrayList<T> readAll();

	ArrayList<T> readAll( String userName );

	/**
	 * Reads an item from the database and converts it to an entity object.
	 * @param cursor - The cursor to read the item from
	 * @return an item of the specified type
	 */
	T readFromCursor( Cursor cursor );

	/**
	 * Extracts ContentValues from a model class, for use with the create() and update() methods.
	 * @param item - The item to get the content values for
	 * @return ContentValues
	 */
	ContentValues putContentValues( T item );

	void update( int id, T item );

	void delete( T item );

	void delete( int id );

	void setAllSynced();

} // interface
