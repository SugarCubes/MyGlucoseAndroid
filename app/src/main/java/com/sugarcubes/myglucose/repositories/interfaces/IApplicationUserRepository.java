//--------------------------------------------------------------------------------------//
//																						//
// File Name:	IApplicationUserRepository.java											//
// Programmer:	J.T. Blevins (jt.blevins@gmail.com)										//
// Date:		09/08/2018																//
// Purpose:		An interface to enforce the methods required to access an 				//
// 				ApplicationUser in the database.										//
//																						//
//--------------------------------------------------------------------------------------//

package com.sugarcubes.myglucose.repositories.interfaces;

import android.content.ContentValues;
import android.database.Cursor;

import com.sugarcubes.myglucose.entities.ApplicationUser;

import java.util.ArrayList;

public interface IApplicationUserRepository<T>
{
	void create( T item );

	T read( String id );

	ArrayList<T> readAll();

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
	ContentValues getContentValues( T item );

	void update( String id, T item );

	void delete( T item );

	void delete( String id );

	T getLoggedInUser();

} // interface
