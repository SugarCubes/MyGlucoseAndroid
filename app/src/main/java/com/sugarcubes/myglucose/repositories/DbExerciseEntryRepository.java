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

import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.models.ExerciseEntry;
import com.sugarcubes.myglucose.repositories.interfaces.IExerciseEntryRepository;
import com.sugarcubes.myglucose.utils.DateUtilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class DbExerciseEntryRepository implements IExerciseEntryRepository
{
	private ContentResolver contentResolver;

	// We need to inject a Context object so that we can get the content resolver
	public DbExerciseEntryRepository( Context context )
	{
		contentResolver = context.getContentResolver();

	} // constructor


	@Override
	public void create( ExerciseEntry item )
	{
		if( item.getRemoteId().isEmpty() )                        // Create an ID
			item.setRemoteId( UUID.randomUUID().toString() );

		contentResolver.insert( MyGlucoseContentProvider.EXERCISE_ENTRIES_URI, this.putContentValues( item ) );

	} // create


	@Override
	public ExerciseEntry read( String id )
	{
		Cursor cursor = contentResolver.query(
				MyGlucoseContentProvider.EXERCISE_ENTRIES_URI,
				null,
				DB.KEY_REMOTE_ID + "=?",
				new String[]{ id },
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
		return readAll( null );

	} // readAll


	@Override
	public ArrayList<ExerciseEntry> readAll( String userName )
	{
		ArrayList<ExerciseEntry> entryArrayList = new ArrayList<>();

		String selection =  userName != null ? DB.KEY_USERNAME + "=?" : null;
		String[] selectionArgs = userName != null ? new String[]{ userName } : null;

		Cursor cursor = contentResolver.query( MyGlucoseContentProvider.EXERCISE_ENTRIES_URI,
				null, selection, selectionArgs,
				DB.KEY_TIMESTAMP + " DESC" );

		if( cursor != null && cursor.getCount() > 0 )
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
		entry.setExerciseName( cursor.getString( cursor.getColumnIndex( DB.KEY_EXERCISE_NAME ) ) );
		entry.setMinutes( cursor.getInt( cursor.getColumnIndex( DB.KEY_EXERCISE_MINUTES ) ) );
		entry.setSteps( cursor.getInt( cursor.getColumnIndex( DB.KEY_EXERCISE_STEPS ) ) );
		entry.setUserName( cursor.getString( cursor.getColumnIndex( DB.KEY_USERNAME ) ) );
		entry.setSynced( cursor.getInt( cursor.getColumnIndex( DB.KEY_SYNCED ) ) > 0 );

		String updatedAt = cursor.getString( cursor.getColumnIndex( DB.KEY_UPDATED_AT ) );
		if( !updatedAt.isEmpty() )
			// Convert the updatedAt string to a Date object:
			entry.setUpdatedAt( DateUtilities.convertStringToDate( updatedAt ) );

		String createdAt = cursor.getString( cursor.getColumnIndex( DB.KEY_CREATED_AT ) );
		if( !createdAt.isEmpty() )
			// Convert the createdAt string to a Date object:
			entry.setCreatedAt( DateUtilities.convertStringToDate( createdAt ) );

		// Retrieve as a long:
		entry.setTimestamp(
				cursor.getLong( cursor.getColumnIndex( DB.KEY_TIMESTAMP ) ) );

		return entry;

	} // readFromCursor


	@Override
	public ContentValues putContentValues( ExerciseEntry item )
	{
		ContentValues values = new ContentValues();
		//		values.put( DB.KEY_ID, item.getId() );
		values.put( DB.KEY_REMOTE_ID, item.getRemoteId() );
		values.put( DB.KEY_USERNAME, item.getUserName() );
		values.put( DB.KEY_SYNCED, item.isSynced() );
		values.put( DB.KEY_EXERCISE_NAME, item.getExerciseName() );
		values.put( DB.KEY_EXERCISE_MINUTES, item.getMinutes() );
		values.put( DB.KEY_EXERCISE_STEPS, item.getSteps() );
		values.put( DB.KEY_CREATED_AT, item.getCreatedAt().toString() );
		values.put( DB.KEY_UPDATED_AT, item.getUpdatedAt().toString() );
		values.put( DB.KEY_TIMESTAMP, item.getTimestamp() );
		return values;

	}


	@Override
	public void update( int id, ExerciseEntry entry )
	{
		entry.setUpdatedAt( new Date() );
		contentResolver.update( MyGlucoseContentProvider.EXERCISE_ENTRIES_URI,
				putContentValues( entry ), DB.KEY_ID + "=?",
				new String[]{ String.valueOf( id ) } );

	} // update


	@Override
	public void delete( ExerciseEntry entry )
	{
		delete( entry.getId() );

	} // delete


	@Override
	public void delete( int id )
	{
		contentResolver.delete( MyGlucoseContentProvider.EXERCISE_ENTRIES_URI,
				DB.KEY_ID + "=?",
				new String[]{ String.valueOf( id ) } );

	} // delete


	@Override
	public void setAllSynced()
	{
		ContentValues values = new ContentValues();
		values.put( DB.KEY_SYNCED, true );
		contentResolver.update( MyGlucoseContentProvider.EXERCISE_ENTRIES_URI,
				values, null, null );

	} // setAllSynced

} // class
