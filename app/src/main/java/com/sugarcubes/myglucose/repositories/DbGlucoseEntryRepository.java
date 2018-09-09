package com.sugarcubes.myglucose.repositories;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.entities.GlucoseEntry;
import com.sugarcubes.myglucose.enums.BeforeAfter;
import com.sugarcubes.myglucose.enums.WhichMeal;
import com.sugarcubes.myglucose.repositories.interfaces.IGlucoseEntryRepository;
import com.sugarcubes.myglucose.utils.DateUtilities;

import java.util.ArrayList;

public class DbGlucoseEntryRepository implements IGlucoseEntryRepository
{
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
		contentResolver.insert(  uri, getContentValues( item ) );

	} // create


	@Override
	public GlucoseEntry read( String id )
	{
		Cursor cursor = contentResolver.query( uri,
				null, DB.KEY_ID + "=?", new String[]{ id },
				DB.KEY_TIMESTAMP + " ASC" );

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
		ArrayList<GlucoseEntry> entryArrayList = new ArrayList<>();

		Cursor cursor = contentResolver.query( uri,
				//null, DB.KEY_USER_ID + "=?", new String[]{ userId },	// Just return ALL entries
				null, null, null,
				DB.KEY_TIMESTAMP + " ASC" );

		if( cursor != null )
		{
			cursor.moveToFirst();

			while( cursor.moveToNext() )
			{
				GlucoseEntry entry = readFromCursor( cursor );
				entryArrayList.add( entry );				// Add the entry to the ArrayList

			} // while

			cursor.close();

		} // if

		return entryArrayList;

	} // readAll


	@Override
	public ContentValues getContentValues( GlucoseEntry item )
	{
		ContentValues values = new ContentValues();
		values.put( DB.KEY_ID, item.getId() );
		values.put( DB.KEY_USER_ID, item.getUserId() );
		values.put( DB.KEY_GLUCOSE_MEASUREMENT, item.getMeasurement() );
		values.put( DB.KEY_GLUCOSE_BEFORE_AFTER, item.getBeforeAfter().toString() );
		values.put( DB.KEY_GLUCOSE_WHICH_MEAL, item.getWhichMeal().toString() );
		values.put( DB.KEY_DATE, item.getDate().toString() );
		values.put( DB.KEY_TIMESTAMP, item.getTimeStamp() );
		return values;

	} // getContentValues


	@Override
	public void update( String id, GlucoseEntry item )
	{
		contentResolver.update( uri, getContentValues( item ),
				DB.KEY_ID + "=?", new String[]{ id } );

	} // update


	@Override
	public void delete( GlucoseEntry item )
	{
		contentResolver.delete( uri, DB.KEY_ID + "=?", new String[]{ item.getId() } );

	} // delete


	@Override
	public void delete( String id )
	{
		contentResolver.delete( uri, DB.KEY_ID + "=?", new String[]{ id } );

	} // delete


	@Override
	public GlucoseEntry readFromCursor( Cursor cursor )
	{
		GlucoseEntry entry = new GlucoseEntry();

		entry.setId( cursor.getString( cursor.getColumnIndex( DB.KEY_ID ) ) );
		entry.setMeasurement( cursor.getFloat(
				cursor.getColumnIndex( DB.KEY_GLUCOSE_MEASUREMENT ) ) );
		entry.setBeforeAfter( BeforeAfter.valueOf( cursor.getString(
				cursor.getColumnIndex( DB.KEY_GLUCOSE_BEFORE_AFTER ) ) ) );
		entry.setWhichMeal( WhichMeal.valueOf( cursor.getString(
				cursor.getColumnIndex( DB.KEY_GLUCOSE_WHICH_MEAL ) ) ) );

		// Convert the date string to a Date object:
		entry.setDate( DateUtilities.convertStringToDate(
				cursor.getString( cursor.getColumnIndex( DB.KEY_DATE ) ) ) );

		// Retrieve as a long:
		entry.setTimeStamp(
				cursor.getLong( cursor.getColumnIndex( DB.KEY_TIMESTAMP ) ) );

		return entry;

	} // readFromCursor

} // repository
