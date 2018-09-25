//--------------------------------------------------------------------------------------//
//																						//
// File Name:	DbExerciseEntryRepository.java											//
// Programmer:	J.T. Blevins (jt.blevins@gmail.com)										//
// Date:		09/08/2018																//
// Purpose:		A repository to allow ExerciseEntry data manipulation in a SQLite 		//
// 				database. 																//
//																						//
//--------------------------------------------------------------------------------------//

package com.sugarcubes.myglucose.repositories;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.entities.ExerciseEntry;
import com.sugarcubes.myglucose.repositories.interfaces.IExerciseEntryRepository;
import com.sugarcubes.myglucose.utils.DateUtilities;

import java.util.ArrayList;

public class DbExerciseEntryRepository implements IExerciseEntryRepository
{
	private ContentResolver contentResolver;
	private Uri uri = MyGlucoseContentProvider.EXERCISE_ENTRIES_URI;

	// We need to inject a Context object so that we can get the content resolver
	public DbExerciseEntryRepository( Context context )
	{
		contentResolver = context.getContentResolver();

	} // constructor


	@Override
	public void create( ExerciseEntry item )
	{
		contentResolver.insert( uri, this.getContentValues( item ) );

	} // create


	@Override
	public ExerciseEntry read( int id )
	{
		Cursor cursor = contentResolver.query( uri,
				null, DB.KEY_ID + "=?", new String[]{ String.valueOf( id ) },
				null );

		if( cursor != null )
		{
			cursor.moveToFirst();

			ExerciseEntry entry = this.readFromCursor( cursor );

			cursor.close();

			return entry;

		} // if

		return null;

	} // read


	@Override
	public ArrayList<ExerciseEntry> readAll()
	{
		ArrayList<ExerciseEntry> entryArrayList = new ArrayList<>();

		Cursor cursor = contentResolver.query( uri, null, null,
				null, DB.KEY_TIMESTAMP + " ASC" );

		if( cursor != null )
		{
			cursor.moveToFirst();

			do
			{
				ExerciseEntry entry = readFromCursor( cursor );
				entryArrayList.add( entry );                // Add the entry to the ArrayList

			} while( cursor.moveToNext() ); // do...while

			cursor.close();

		} // if

		return entryArrayList;

	} // readAll


	@Override
	public ExerciseEntry readFromCursor( Cursor cursor )
	{
		ExerciseEntry entry = new ExerciseEntry();

		entry.setId( cursor.getInt( cursor.getColumnIndex( DB.KEY_ID ) ) );
		entry.setRemoteId( cursor.getString( cursor.getColumnIndex( DB.KEY_REMOTE_ID ) ) );
		entry.setExerciseName( cursor.getString( cursor.getColumnIndex( DB.KEY_EXERCISE_NAME ) ));
		entry.setMinutes( cursor.getInt( cursor.getColumnIndex( DB.KEY_EXERCISE_MINUTES_SPENT ) ));

		// Convert the date string to a Date object:
		entry.setDate( DateUtilities.convertStringToDate(
				cursor.getString( cursor.getColumnIndex( DB.KEY_DATE ) ) ) );

		// Retrieve as a long:
		entry.setTimestamp(
				cursor.getLong( cursor.getColumnIndex( DB.KEY_TIMESTAMP ) ) );

		return entry;

	} // readFromCursor


	@Override
	public ContentValues getContentValues( ExerciseEntry item )
	{
		ContentValues values = new ContentValues();
//		values.put( DB.KEY_ID, item.getId() );
		values.put( DB.KEY_REMOTE_ID, item.getRemoteId() );
		values.put( DB.KEY_USERNAME, item.getUserName() );
		values.put( DB.KEY_EXERCISE_NAME, item.getExerciseName() );
		values.put( DB.KEY_EXERCISE_MINUTES_SPENT, item.getMinutes() );
		values.put( DB.KEY_DATE, item.getDate().toString() );
		values.put( DB.KEY_TIMESTAMP, item.getTimestamp() );
		return values;

	}


	@Override
	public void update( int id, ExerciseEntry item )
	{
		contentResolver.update( uri, getContentValues( item ), DB.KEY_ID + "=?",
				new String[]{ String.valueOf( id ) } );

	} // update


	@Override
	public void delete( ExerciseEntry item )
	{
		contentResolver.delete( uri, DB.KEY_ID + "=?",
				new String[]{ String.valueOf( item.getId() ) } );

	} // delete


	@Override
	public void delete( int id )
	{
		contentResolver.delete( uri, DB.KEY_ID + "=?",
				new String[]{ String.valueOf( id ) } );

	} // delete

} // class
