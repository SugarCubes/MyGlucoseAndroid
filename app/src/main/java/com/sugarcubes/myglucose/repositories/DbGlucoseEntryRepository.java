//--------------------------------------------------------------------------------------//
//																						//
// File Name:	DbGlucoseEntryRepository.java											//
// Programmer:	J.T. Blevins (jt.blevins@gmail.com)										//
// Date:		09/08/2018																//
// Purpose:		A repository to allow glucose entry data manipulation in a SQLite 		//
// 				database. 																//
//																						//
//--------------------------------------------------------------------------------------//

package com.sugarcubes.myglucose.repositories;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.entities.GlucoseEntry;
import com.sugarcubes.myglucose.enums.BeforeAfter;
import com.sugarcubes.myglucose.enums.WhichMeal;
import com.sugarcubes.myglucose.repositories.interfaces.IGlucoseEntryRepository;
import com.sugarcubes.myglucose.utils.DateUtilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class DbGlucoseEntryRepository implements IGlucoseEntryRepository
{
	private final String LOG_TAG = getClass().getSimpleName();
	private ContentResolver contentResolver;
	private Uri uri = MyGlucoseContentProvider.GLUCOSE_ENTRIES_URI;

	// We need to inject a Context object so that we can get the content resolver
	public DbGlucoseEntryRepository( Context context )
	{
		contentResolver = context.getContentResolver();

	} // constructor


	@Override
	public void create( GlucoseEntry item )
	{
		if( item.getRemoteId().isEmpty() )                        // Create an ID
			item.setRemoteId( UUID.randomUUID().toString() );
		ContentValues values = putContentValues( item );
		Log.w( LOG_TAG, "URI: " + uri + "; Values: " + values.toString() );
		contentResolver.insert(  uri, values );

	} // create


	@Override
	public GlucoseEntry read( int id )
	{
		Cursor cursor = contentResolver.query( uri,
				null, DB.KEY_ID + "=?", new String[]{ String.valueOf( id ) },
				null );

		if( cursor != null )
		{
			cursor.moveToFirst();

			GlucoseEntry entry = readFromCursor( cursor );

			cursor.close();

			return entry;

		} // if

		return null;

	} // read


	@Override
	public ArrayList<GlucoseEntry> readAll()
	{
		return readAll( null );

	} // readAll


	@Override
	public ArrayList<GlucoseEntry> readAll( String userName )
	{
		ArrayList<GlucoseEntry> entryArrayList = new ArrayList<>();

		String selection = userName != null ? DB.KEY_USERNAME + "=?" : null;
		String[] selectionArgs = userName != null ? new String[]{ userName } : null;

		Cursor cursor = contentResolver.query( uri,
				null, selection, selectionArgs,
				DB.KEY_TIMESTAMP + " DESC" );

		if( cursor != null && cursor.getCount() > 0 )
		{
			cursor.moveToFirst();

			do
			{
				GlucoseEntry entry = readFromCursor( cursor );
				entryArrayList.add( entry );				// Add the entry to the ArrayList

			} while( cursor.moveToNext() ); // do...while

			cursor.close();

		} // if

		return entryArrayList;

	} // readAll


	@Override
	public ContentValues putContentValues( GlucoseEntry item )
	{
		ContentValues values = new ContentValues();
//		values.put( DB.KEY_ID, item.getId() );
		values.put( DB.KEY_REMOTE_ID, item.getRemoteId() );
		values.put( DB.KEY_USERNAME, item.getUserName() );
		values.put( DB.KEY_GLUCOSE_MEASUREMENT, item.getMeasurement() );
		values.put( DB.KEY_GLUCOSE_BEFORE_AFTER, item.getBeforeAfter().toString() );
		values.put( DB.KEY_WHICH_MEAL, item.getWhichMeal().toString() );
		values.put( DB.KEY_CREATED_AT, item.getCreatedAt().toString() );
		values.put( DB.KEY_UPDATED_AT, item.getUpdatedAt().toString() );
		values.put( DB.KEY_TIMESTAMP, item.getTimestamp() );
		return values;

	} // putContentValues


	@Override
	public void update( int id, GlucoseEntry item )
	{
		item.setUpdatedAt( new Date() );
		contentResolver.update( uri, putContentValues( item ),
				DB.KEY_ID + "=?", new String[]{ String.valueOf( id ) } );

	} // update


	@Override
	public void delete( GlucoseEntry item )
	{
		contentResolver.delete( uri, DB.KEY_ID + "=?",
				new String[]{ String.valueOf( item.getId() ) } );

	} // delete


	@Override
	public void delete( int id )
	{
		contentResolver.delete( uri, DB.KEY_ID + "=?", new String[]{ String.valueOf( id ) } );

	} // delete


	@Override
	public GlucoseEntry readFromCursor( Cursor cursor )
	{
		GlucoseEntry entry = new GlucoseEntry();

//		entry.setId( cursor.getInt( cursor.getColumnIndex( DB.KEY_ID ) ) );
		entry.setRemoteId( cursor.getString(
				cursor.getColumnIndex( DB.KEY_REMOTE_ID ) ) );
		entry.setMeasurement( cursor.getFloat(
				cursor.getColumnIndex( DB.KEY_GLUCOSE_MEASUREMENT ) ) );
		entry.setBeforeAfter( BeforeAfter.valueOf( cursor.getString(
				cursor.getColumnIndex( DB.KEY_GLUCOSE_BEFORE_AFTER ) ) ) );
		entry.setWhichMeal( WhichMeal.valueOf( cursor.getString(
				cursor.getColumnIndex( DB.KEY_WHICH_MEAL ) ) ) );
		entry.setUserName( cursor.getString( cursor.getColumnIndex( DB.KEY_USERNAME ) ) );

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

} // repository
