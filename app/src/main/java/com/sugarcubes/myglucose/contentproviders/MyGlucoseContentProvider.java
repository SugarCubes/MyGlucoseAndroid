package com.sugarcubes.myglucose.contentproviders;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.sugarcubes.myglucose.db.DB;

import java.util.HashMap;

public class MyGlucoseContentProvider extends ContentProvider
{
	// fields for content provider
	public static final String
			AUTHORITY				= "com.sugarcubes.myglucose.provider",

			USERS_URL				= "content://" + AUTHORITY + "/" + DB.TABLE_USERS,
			GLUCOSE_ENTRIES_URL		= "content://" + AUTHORITY + "/" + DB.TABLE_GLUCOSE_ENTRIES,
			MEAL_ENTRIES_URL		= "content://" + AUTHORITY + "/" + DB.TABLE_MEAL_ENTRIES,
			MEAL_ITEMS_URL			= "content://" + AUTHORITY + "/" + DB.TABLE_MEAL_ITEMS,
			EXERCISE_ENTRIES_URL	= "content://" + AUTHORITY + "/" + DB.TABLE_EXERCISE_ENTRIES;

	// Uris to be used by the ContentProvider:
	public static final Uri
			USERS_URI				= Uri.parse( USERS_URL ),
			GLUCOSE_ENTRIES_URI		= Uri.parse( GLUCOSE_ENTRIES_URL ),
			MEAL_ENTRIES_URI		= Uri.parse( MEAL_ENTRIES_URL ),
			MEAL_ITEMS_URI			= Uri.parse( MEAL_ITEMS_URL ),
			EXERCISE_ENTRIES_URI	= Uri.parse( EXERCISE_ENTRIES_URL );

	// Fields for content URI
	public static final int
			USERS					= 1,
			USERS_ID				= 2,
			GLUCOSE_ENTRIES			= 3,
			MEAL_ENTRIES			= 4,
			MEAL_ITEMS				= 5,
			EXERCISE_ENTRIES		= 6;

	// Creates a UriMatcher object.
	private static final UriMatcher uriMatcher;
	static
	{
		uriMatcher = new UriMatcher( UriMatcher.NO_MATCH );
		// base uri, path followed by base uri, switch case to execute
		uriMatcher.addURI( AUTHORITY, DB.TABLE_USERS, USERS );
		uriMatcher.addURI( AUTHORITY, DB.TABLE_USERS + "/#", USERS_ID );
		uriMatcher.addURI( AUTHORITY, DB.TABLE_GLUCOSE_ENTRIES, GLUCOSE_ENTRIES );
		uriMatcher.addURI( AUTHORITY, DB.TABLE_MEAL_ENTRIES, MEAL_ENTRIES );
		uriMatcher.addURI( AUTHORITY, DB.TABLE_MEAL_ITEMS, MEAL_ITEMS );
		uriMatcher.addURI( AUTHORITY, DB.TABLE_EXERCISE_ENTRIES, EXERCISE_ENTRIES );

	} // uriMatcher


	private static HashMap<String, String> queryMap;	// For queryBuilder
	private static DB db;					// SQLiteOpenHelper
	private static SQLiteDatabase database;				// Get a connection using our custom handler

	@Override
	public boolean onCreate()
	{
		// SQLite handler object
		db = new DB( getContext() );
		database = db.getReadableDatabase();
		queryMap = new HashMap<>();

		return database != null;

	} // onCreate


	@Nullable
	@Override
	public Cursor query(
			@NonNull Uri uri,
			@Nullable String[] projection,
			@Nullable String selection,
			@Nullable String[] selectionArgs,
			@Nullable String sortOrder )
	{
		// Create query and set to correct table
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		Cursor cursor;

		sortOrder =	sortOrder == null
				? DB.KEY_ID + " ASC"
				: null;
		queryBuilder.setProjectionMap( queryMap );

		// In case the db needs to be instantiated:
		if( db == null  )
			db = new DB( getContext() );
		if( database == null )
			database = db.getReadableDatabase();

		switch( uriMatcher.match( uri ) )
		{
			// If the incoming URI was for all of the users:
			case USERS:
				queryBuilder.setTables( DB.TABLE_USERS );
				break;

			// If the URI matches a single user:
			case USERS_ID:
				queryBuilder.setTables( DB.TABLE_USERS );
				queryBuilder.appendWhere( DB.KEY_USER_ID + "=" + uri.getLastPathSegment() );
				break;

			case GLUCOSE_ENTRIES:
				queryBuilder.setTables( DB.TABLE_GLUCOSE_ENTRIES );
				break;

			case MEAL_ENTRIES:
				queryBuilder.setTables( DB.TABLE_MEAL_ENTRIES );
				break;

			case MEAL_ITEMS:
				queryBuilder.setTables( DB.TABLE_MEAL_ITEMS );
				break;

			case EXERCISE_ENTRIES:
				queryBuilder.setTables( DB.TABLE_EXERCISE_ENTRIES );
				break;

			default:
				throw new IllegalArgumentException( "Unknown URI " + uri );
		}

		return queryBuilder.query(					// Return a cursor
				database, projection, selection, selectionArgs,
				null, null, sortOrder );

	} // query


	@Nullable
	@Override
	public Uri insert( @NonNull Uri uri,
					   @Nullable ContentValues values )
	{
		long row	= -1;

		// In case the db needs to be instantiated:
		if( db == null  )
			db = new DB( getContext() );
		if( database == null )
			database = db.getReadableDatabase();


		switch( uriMatcher.match( uri ) )
		{
			// If the incoming URI was for all of the sent capsules table
			case USERS:
				row = database.insert( DB.TABLE_USERS, AUTHORITY, values );
				break;

			case GLUCOSE_ENTRIES:
				row = database.insert( DB.TABLE_GLUCOSE_ENTRIES, AUTHORITY, values );
				break;

			case MEAL_ENTRIES:
				row = database.insert( DB.TABLE_MEAL_ENTRIES, AUTHORITY, values );
				break;

			case MEAL_ITEMS:
				row = database.insert( DB.TABLE_MEAL_ITEMS, AUTHORITY, values );
				break;

			case EXERCISE_ENTRIES:
				row = database.insert( DB.TABLE_EXERCISE_ENTRIES, AUTHORITY, values );
				break;

			default:
				throw new IllegalArgumentException( "Unknown URI " + uri );

		} // switch


		// If record is added successfully
		if( row > 0 )
		{
			// Usually need to notify any content resolvers of the change:
			try
			{
				getContext().getContentResolver().notifyChange( uri, null );
			}
			catch ( NullPointerException e )
			{
				e.printStackTrace();
			}

			return ContentUris.withAppendedId( uri, row );			// RETURN STATEMENT

		} // if

		throw new SQLException( "Fail to add a new record into " + uri );

	} // insert


	/**
	 * delete()
	 * @param uri - Uri to match
	 * @param selection - Selection
	 * @param selectionArgs - Selection arguments
	 * @return row count
	 */
	@Override
	public int delete( @NonNull Uri uri,
					   @Nullable String selection,
					   @Nullable String[] selectionArgs )
	{
		int count	= 0;

		// In case the db needs to be instantiated:
		if( db == null  )
			db = new DB( getContext() );
		if( database == null )
			database = db.getReadableDatabase();

		switch( uriMatcher.match( uri ) )
		{
			case USERS:
				count	=	database.delete( DB.TABLE_USERS, selection, selectionArgs );
				break;

			case USERS_ID:
				count = database.delete(
						DB.TABLE_USERS, DB.KEY_USER_ID +  " = " + uri.getLastPathSegment() +
								( !TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "" ),
						selectionArgs);
				break;

			case GLUCOSE_ENTRIES:
				count	=	database.delete( DB.TABLE_GLUCOSE_ENTRIES, selection, selectionArgs );
				break;

			case MEAL_ENTRIES:
				count	=	database.delete( DB.TABLE_MEAL_ENTRIES, selection, selectionArgs );
				break;

			case MEAL_ITEMS:
				count	=	database.delete( DB.TABLE_MEAL_ITEMS, selection, selectionArgs );
				break;

			case EXERCISE_ENTRIES:
				count	=	database.delete( DB.TABLE_EXERCISE_ENTRIES, selection, selectionArgs );
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);

		} // switch

		// Usually need to notify any content resolvers of the change:
		try
		{
			getContext().getContentResolver().notifyChange( uri, null );
		}
		catch ( NullPointerException e )
		{
			e.printStackTrace();
		}

		return count;

	} // delete


	@Override
	public int update( @NonNull Uri uri,
					   @Nullable ContentValues values,
					   @Nullable String selection,
					   @Nullable String[] selectionArgs )
	{
		int count	= 0;

		// In case the db needs to be instantiated:
		if( db == null  )
			db = new DB( getContext() );
		if( database == null )
			database = db.getReadableDatabase();


		switch( uriMatcher.match( uri ) )
		{

			// If the incoming URI was for all of the sent capsules table
			case USERS:
				count	=	database.update( DB.TABLE_USERS, values, selection, selectionArgs );
				break;

			// If the incoming URI was for a single row
			case USERS_ID:
				count	= 	database.update( DB.TABLE_USERS,
							values,
							DB.KEY_USER_ID +  "=?"
									+ ( !TextUtils.isEmpty(selection)
									? " AND (" + selection + ')' : "" ),
							new String[]{ uri.getLastPathSegment() } );
				break;

			case GLUCOSE_ENTRIES:
				count	=	database.update( DB.TABLE_GLUCOSE_ENTRIES, values, selection, selectionArgs );
				break;

			case MEAL_ENTRIES:
				count	=	database.update( DB.TABLE_MEAL_ENTRIES, values, selection, selectionArgs );
				break;

			case MEAL_ITEMS:
				count	=	database.update( DB.TABLE_MEAL_ITEMS, values, selection, selectionArgs );
				break;

			case EXERCISE_ENTRIES:
				count	=	database.update( DB.TABLE_EXERCISE_ENTRIES, values, selection, selectionArgs );
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);

		} // switch

		// Usually need to notify any content resolvers of the change:
		try
		{
			getContext().getContentResolver().notifyChange( uri, null );
		}
		catch ( NullPointerException e )
		{
			e.printStackTrace();
		}

		return count;

	} // update


	@Nullable
	@Override
	public String getType( @NonNull Uri uri )
	{
		switch ( uriMatcher.match(uri) )
		{
			case USERS:
				//return "vnd.android.cursor.dir/vnd.example.friends";
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.users";
			case USERS_ID:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.usersid";
			case GLUCOSE_ENTRIES:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.glucoseentries";
			case MEAL_ENTRIES:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.mealentries";
			case MEAL_ITEMS:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.mealitems";
			case EXERCISE_ENTRIES:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.exerciseentries";
			default:
				throw new IllegalArgumentException( "Unsupported URI: " + uri );
		} // switch

	} // getType

} // class
