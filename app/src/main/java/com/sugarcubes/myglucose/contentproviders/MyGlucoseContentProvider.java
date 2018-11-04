package com.sugarcubes.myglucose.contentproviders;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.sugarcubes.myglucose.db.DB;

import java.util.HashMap;

import static com.sugarcubes.myglucose.activities.MainActivity.DEBUG;

public class MyGlucoseContentProvider extends ContentProvider
{
	// fields for content provider
	public static final String
			AUTHORITY = "com.sugarcubes.myglucose.provider",

	USERS_URL                    = "content://" + AUTHORITY + "/" + DB.TABLE_USERS,
			PATIENTS_URL         = "content://" + AUTHORITY + "/" + DB.TABLE_PATIENTS,
			PATIENT_USERS_URL    = "content://" + AUTHORITY + "/" + DB.PATIENT_USERS,
			DOCTORS_URL          = "content://" + AUTHORITY + "/" + DB.TABLE_DOCTORS,
			DOCTOR_USERS_URL     = "content://" + AUTHORITY + "/" + DB.DOCTOR_USERS,
			GLUCOSE_ENTRIES_URL  = "content://" + AUTHORITY + "/" + DB.TABLE_GLUCOSE_ENTRIES,
			MEAL_ENTRIES_URL     = "content://" + AUTHORITY + "/" + DB.TABLE_MEAL_ENTRIES,
			MEAL_ITEMS_URL       = "content://" + AUTHORITY + "/" + DB.TABLE_MEAL_ITEMS,
			EXERCISE_ENTRIES_URL = "content://" + AUTHORITY + "/" + DB.TABLE_EXERCISE_ENTRIES,
			PEDOMETER_URL        = "content://" + AUTHORITY + "/" + DB.TABLE_PEDOMETER;

	// Uris to be used by the ContentProvider:
	public static final Uri
			USERS_URI            = Uri.parse( USERS_URL ),
			PATIENT_USERS_URI    = Uri.parse( PATIENT_USERS_URL ),
			PATIENTS_URI         = Uri.parse( PATIENTS_URL ),
			DOCTORS_URI          = Uri.parse( DOCTORS_URL ),
			DOCTOR_USERS_URI     = Uri.parse( DOCTOR_USERS_URL ),
			GLUCOSE_ENTRIES_URI  = Uri.parse( GLUCOSE_ENTRIES_URL ),
			MEAL_ENTRIES_URI     = Uri.parse( MEAL_ENTRIES_URL ),
			MEAL_ITEMS_URI       = Uri.parse( MEAL_ITEMS_URL ),
			EXERCISE_ENTRIES_URI = Uri.parse( EXERCISE_ENTRIES_URL ),
			PEDOMETER_URI        = Uri.parse( PEDOMETER_URL );

	// Fields for content URI
	public static final int
			USERS               = 1,
			USERS_ID            = 2,
			PATIENTS            = 3,
			PATIENTS_ID         = 4,
			PATIENT_USERS       = 5,
			DOCTORS             = 6,
			DOCTORS_ID          = 7,
			DOCTOR_USERS        = 8,
			GLUCOSE_ENTRIES     = 9,
			GLUCOSE_ENTRIES_ID  = 10,
			MEAL_ENTRIES        = 11,
			MEAL_ENTRIES_ID     = 12,
			MEAL_ENTRY_ITEMS    = 13,
			MEAL_ITEMS          = 14,
			MEAL_ITEMS_ID       = 15,
			EXERCISE_ENTRIES    = 16,
			EXERCISE_ENTRIES_ID = 17,
			PEDOMETER           = 18;

	// Creates a UriMatcher object.
	private static final UriMatcher uriMatcher;

	static
	{
		uriMatcher = new UriMatcher( UriMatcher.NO_MATCH );
		// base uri, path followed by base uri, switch case to execute
		uriMatcher.addURI( AUTHORITY, DB.TABLE_USERS, USERS );
		uriMatcher.addURI( AUTHORITY, DB.TABLE_USERS + "/#", USERS_ID );
		uriMatcher.addURI( AUTHORITY, DB.PATIENT_USERS, PATIENT_USERS );
		uriMatcher.addURI( AUTHORITY, DB.TABLE_PATIENTS, PATIENTS );
		uriMatcher.addURI( AUTHORITY, DB.TABLE_PATIENTS + "/#", PATIENTS_ID );
		uriMatcher.addURI( AUTHORITY, DB.TABLE_DOCTORS, DOCTORS );
		uriMatcher.addURI( AUTHORITY, DB.TABLE_DOCTORS + "/#", DOCTORS_ID );
		uriMatcher.addURI( AUTHORITY, DB.DOCTOR_USERS, DOCTOR_USERS );
		uriMatcher.addURI( AUTHORITY, DB.TABLE_GLUCOSE_ENTRIES, GLUCOSE_ENTRIES );
		uriMatcher.addURI( AUTHORITY, DB.TABLE_GLUCOSE_ENTRIES + "/#", GLUCOSE_ENTRIES_ID );
		uriMatcher.addURI( AUTHORITY, DB.TABLE_MEAL_ENTRIES, MEAL_ENTRIES );
		uriMatcher.addURI( AUTHORITY, DB.TABLE_MEAL_ENTRIES + "/#", MEAL_ENTRIES_ID );
		uriMatcher.addURI( AUTHORITY, DB.TABLE_MEAL_ITEMS, MEAL_ENTRY_ITEMS );
		uriMatcher.addURI( AUTHORITY, DB.TABLE_MEAL_ITEMS, MEAL_ITEMS );
		uriMatcher.addURI( AUTHORITY, DB.TABLE_MEAL_ITEMS + "/#", MEAL_ITEMS_ID );
		uriMatcher.addURI( AUTHORITY, DB.TABLE_EXERCISE_ENTRIES, EXERCISE_ENTRIES );
		uriMatcher.addURI( AUTHORITY, DB.TABLE_EXERCISE_ENTRIES + "/#", EXERCISE_ENTRIES_ID );
		uriMatcher.addURI( AUTHORITY, DB.TABLE_PEDOMETER, PEDOMETER );

	} // uriMatcher


	private static HashMap<String, String> queryMap;    // For queryBuilder
	private static DB                      db;                    // SQLiteOpenHelper
	private static SQLiteDatabase          database;
	// Get a connection using our custom handler
	private String LOG_TAG = getClass().getSimpleName();

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

		//		sortOrder =	sortOrder == null
		//				? DB.KEY_ID + " ASC"
		//				: null;
		queryBuilder.setProjectionMap( queryMap );

		// In case the db needs to be instantiated:
		if( db == null )
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
				queryBuilder.appendWhere( DB.KEY_USER_EMAIL + "=" + uri.getLastPathSegment() );
				break;

			case PATIENTS:
				queryBuilder.setTables( DB.TABLE_PATIENTS );
				break;

			case PATIENTS_ID:
				queryBuilder.setTables( DB.TABLE_PATIENTS );
				queryBuilder.appendWhere( DB.KEY_USER_EMAIL + "=" + uri.getLastPathSegment() );
				break;

			case PATIENT_USERS:
				queryBuilder.setTables( DB.TABLE_PATIENTS + ", " + DB.TABLE_USERS );//+ " ON "
				//						+ DB.TABLE_PATIENTS + "." + DB.KEY_USERNAME + "=" + DB.TABLE_USERS + "."
				//						+ DB.KEY_USERNAME );

				if( DEBUG && selectionArgs != null ) Log.e( LOG_TAG, "Selection: " + selection
						+ "; args: " + selectionArgs[ 0 ] );
				break;

			case DOCTORS:
				queryBuilder.setTables( DB.TABLE_DOCTORS );
				break;

			case DOCTORS_ID:
				queryBuilder.setTables( DB.TABLE_DOCTORS );
				queryBuilder.appendWhere( DB.KEY_USER_EMAIL + "=" + uri.getLastPathSegment() );
				break;

			case DOCTOR_USERS:
				queryBuilder.setTables( DB.TABLE_DOCTORS + ", " + DB.TABLE_USERS );

				if( DEBUG && selectionArgs != null ) Log.e( LOG_TAG, "Selection: " + selection
						+ "; args: " + selectionArgs[ 0 ] );
				break;

			case GLUCOSE_ENTRIES:
				queryBuilder.setTables( DB.TABLE_GLUCOSE_ENTRIES );
				break;

			case GLUCOSE_ENTRIES_ID:
				queryBuilder.setTables( DB.TABLE_GLUCOSE_ENTRIES );
				queryBuilder.appendWhere( DB.KEY_ID + "=" + uri.getLastPathSegment() );
				break;

			case MEAL_ENTRIES:
				queryBuilder.setTables( DB.TABLE_MEAL_ENTRIES );
				break;

			case MEAL_ENTRIES_ID:
				queryBuilder.setTables( DB.TABLE_MEAL_ENTRIES );
				queryBuilder.appendWhere( DB.KEY_ID + "=" + uri.getLastPathSegment() );
				break;

			// Return all items included in one meal
			case MEAL_ENTRY_ITEMS:
				queryBuilder.setTables( DB.TABLE_MEAL_ITEMS );
				queryBuilder.appendWhere( DB.KEY_MEAL_ID + "=" + uri.getLastPathSegment() );
				break;

			case MEAL_ITEMS:
				queryBuilder.setTables( DB.TABLE_MEAL_ITEMS );
				break;

			case MEAL_ITEMS_ID:
				queryBuilder.setTables( DB.TABLE_MEAL_ITEMS );
				queryBuilder.appendWhere( DB.KEY_ID + "=" + uri.getLastPathSegment() );
				break;

			case EXERCISE_ENTRIES:
				queryBuilder.setTables( DB.TABLE_EXERCISE_ENTRIES );
				break;

			case EXERCISE_ENTRIES_ID:
				queryBuilder.setTables( DB.TABLE_EXERCISE_ENTRIES );
				queryBuilder.appendWhere( DB.KEY_ID + "=" + uri.getLastPathSegment() );
				break;

			case PEDOMETER:
				queryBuilder.setTables( DB.TABLE_PEDOMETER );
				break;

			default:
				throw new IllegalArgumentException( "Unknown URI " + uri );
		}

		return queryBuilder.query(                    // Return a cursor
				database, projection, selection, selectionArgs,
				null, null, sortOrder );

	} // query


	@Nullable
	@Override
	public Uri insert( @NonNull Uri uri,
					   @Nullable ContentValues values )
	{
		long count = -1;

		// In case the db needs to be instantiated:
		if( db == null )
			db = new DB( getContext() );
		if( database == null )
			database = db.getReadableDatabase();


		switch( uriMatcher.match( uri ) )
		{
			// If the incoming URI was for all of the sent capsules table
			case USERS:
				count = database.insert( DB.TABLE_USERS, AUTHORITY, values );
				break;

			case PATIENTS:
				count = database.insert( DB.TABLE_PATIENTS, AUTHORITY, values );
				break;

			case PATIENT_USERS:
				// Invalid action
				count = 0;
				break;

			case DOCTORS:
				count = database.insert( DB.TABLE_DOCTORS, AUTHORITY, values );
				break;

			case DOCTOR_USERS:
				// Invalid action
				count = 0;
				break;

			case GLUCOSE_ENTRIES:
				count = database.insert( DB.TABLE_GLUCOSE_ENTRIES, AUTHORITY, values );
				break;

			case MEAL_ENTRIES:
				count = database.insert( DB.TABLE_MEAL_ENTRIES, AUTHORITY, values );
				break;

			case MEAL_ITEMS:
				count = database.insert( DB.TABLE_MEAL_ITEMS, AUTHORITY, values );
				break;

			case EXERCISE_ENTRIES:
				count = database.insert( DB.TABLE_EXERCISE_ENTRIES, AUTHORITY, values );
				break;

			case PEDOMETER:
				count = database.insert( DB.TABLE_PEDOMETER, AUTHORITY, values );
				break;

			default:
				throw new IllegalArgumentException( "Unknown URI " + uri );

		} // switch


		// If record is added successfully
		if( count > 0 )
		{
			// Usually need to notify any content resolvers of the change:
			Context context = getContext();
			if( context != null && context.getContentResolver() != null )
				context.getContentResolver().notifyChange( uri, null );

			return ContentUris.withAppendedId( uri, count );            // RETURN STATEMENT

		} // if

		throw new SQLException( "Fail to add a new record into " + uri );

	} // insert


	/**
	 * delete()
	 *
	 * @param uri           - Uri to match
	 * @param selection     - Selection
	 * @param selectionArgs - Selection arguments
	 * @return row count
	 */
	@Override
	public int delete( @NonNull Uri uri,
					   @Nullable String selection,
					   @Nullable String[] selectionArgs )
	{
		int count = 0;

		// In case the db needs to be instantiated:
		if( db == null )
			db = new DB( getContext() );
		if( database == null )
			database = db.getReadableDatabase();

		switch( uriMatcher.match( uri ) )
		{
			case USERS:
				count = database.delete( DB.TABLE_USERS, selection, selectionArgs );
				break;

			case USERS_ID:
				count = database.delete(
						DB.TABLE_USERS, DB.KEY_ID + " = " + uri.getLastPathSegment() +
								( !TextUtils.isEmpty( selection )
										? " AND (" + selection + ')'
										: "" ),
						selectionArgs );
				break;

			case PATIENTS:
				count = database.delete( DB.TABLE_PATIENTS, selection, selectionArgs );
				break;

			case PATIENTS_ID:
				count = database.delete(
						DB.TABLE_PATIENTS, DB.KEY_USER_EMAIL + " = " + uri.getLastPathSegment() +
								( !TextUtils.isEmpty( selection )
										? " AND (" + selection + ')'
										: "" ),
						selectionArgs );
				break;

			case PATIENT_USERS:
				// Invalid action
				count = 0;
				break;

			case DOCTORS:
				count = database.delete( DB.TABLE_DOCTORS, selection, selectionArgs );
				break;

			case DOCTORS_ID:
				count = database.delete(
						DB.TABLE_DOCTORS, DB.KEY_USER_EMAIL + " = " + uri.getLastPathSegment() +
								( !TextUtils.isEmpty( selection )
										? " AND (" + selection + ')'
										: "" ),
						selectionArgs );
				break;

			case DOCTOR_USERS:
				// Invalid action
				count = 0;
				break;

			case GLUCOSE_ENTRIES:
				count = database.delete( DB.TABLE_GLUCOSE_ENTRIES, selection, selectionArgs );
				break;

			case GLUCOSE_ENTRIES_ID:
				count = database.delete(
						DB.TABLE_GLUCOSE_ENTRIES, DB.KEY_ID + " = " + uri.getLastPathSegment() +
								( !TextUtils.isEmpty( selection )
										? " AND (" + selection + ')'
										: "" ),
						selectionArgs );
				break;

			case MEAL_ENTRIES:
				count = database.delete( DB.TABLE_MEAL_ENTRIES, selection, selectionArgs );
				break;

			case MEAL_ENTRIES_ID:
				count = database.delete(
						DB.TABLE_MEAL_ENTRIES, DB.KEY_ID + " = " + uri.getLastPathSegment() +
								( !TextUtils.isEmpty( selection )
										? " AND (" + selection + ')'
										: "" ),
						selectionArgs );
				break;

			case MEAL_ENTRY_ITEMS:
				count = database.delete(
						DB.TABLE_MEAL_ITEMS, DB.KEY_MEAL_ID + " = " + uri.getLastPathSegment() +
								( !TextUtils.isEmpty( selection )
										? " AND (" + selection + ')'
										: "" ),
						selectionArgs );
				break;

			case MEAL_ITEMS:
				count = database.delete( DB.TABLE_MEAL_ITEMS, selection, selectionArgs );
				break;

			case MEAL_ITEMS_ID:
				count = database.delete(
						DB.TABLE_MEAL_ITEMS, DB.KEY_ID + " = " + uri.getLastPathSegment() +
								( !TextUtils.isEmpty( selection )
										? " AND (" + selection + ')'
										: "" ),
						selectionArgs );
				break;

			case EXERCISE_ENTRIES:
				count = database.delete( DB.TABLE_EXERCISE_ENTRIES, selection, selectionArgs );
				break;

			case EXERCISE_ENTRIES_ID:
				count = database.delete(
						DB.TABLE_EXERCISE_ENTRIES, DB.KEY_ID + " = " + uri.getLastPathSegment() +
								( !TextUtils.isEmpty( selection )
										? " AND (" + selection + ')'
										: "" ),
						selectionArgs );
				break;

			case PEDOMETER:
				count = database.delete( DB.TABLE_PEDOMETER, selection, selectionArgs );
				break;

			default:
				throw new IllegalArgumentException( "Unknown URI " + uri );

		} // switch

		// Usually need to notify any content resolvers of the change:
		Context context = getContext();
		if( context != null && context.getContentResolver() != null )
			context.getContentResolver().notifyChange( uri, null );

		return count;

	} // delete


	@Override
	public int update( @NonNull Uri uri,
					   @Nullable ContentValues values,
					   @Nullable String selection,
					   @Nullable String[] selectionArgs )
	{
		int count = 0;

		// In case the db needs to be instantiated:
		if( db == null )
			db = new DB( getContext() );
		if( database == null )
			database = db.getReadableDatabase();


		switch( uriMatcher.match( uri ) )
		{

			// If the incoming URI was for all of the sent capsules table
			case USERS:
				count = database.update( DB.TABLE_USERS, values, selection, selectionArgs );
				break;

			// If the incoming URI was for a single row
			case USERS_ID:
				count = database.update( DB.TABLE_USERS,
						values,
						DB.KEY_ID + "=?"
								+ ( !TextUtils.isEmpty( selection )
								? " AND (" + selection + ')'
								: "" ),
						new String[]{ uri.getLastPathSegment() } );
				break;

			case PATIENTS:
				count = database.update( DB.TABLE_PATIENTS, values, selection, selectionArgs );
				break;

			case PATIENT_USERS:
				// Invalid action
				count = 0;
				break;

			case PATIENTS_ID:
				count = database.update( DB.TABLE_PATIENTS,
						values,
						DB.KEY_USER_EMAIL + "=?"
								+ ( !TextUtils.isEmpty( selection )
								? " AND (" + selection + ')'
								: "" ),
						new String[]{ uri.getLastPathSegment() } );
				break;

			case DOCTORS:
				count = database.update( DB.TABLE_DOCTORS, values, selection, selectionArgs );
				break;

			case DOCTORS_ID:
				count = database.update( DB.TABLE_DOCTORS,
						values,
						DB.KEY_USER_EMAIL + "=?"
								+ ( !TextUtils.isEmpty( selection )
								? " AND (" + selection + ')'
								: "" ),
						new String[]{ uri.getLastPathSegment() } );
				break;

			case DOCTOR_USERS:
				// Invalid action
				count = 0;
				break;

			case GLUCOSE_ENTRIES:
				count =
						database.update( DB.TABLE_GLUCOSE_ENTRIES, values, selection, selectionArgs );
				break;

			// If the incoming URI was for a single row
			case GLUCOSE_ENTRIES_ID:
				count = database.update( DB.TABLE_GLUCOSE_ENTRIES,
						values,
						DB.KEY_ID + "=?"
								+ ( !TextUtils.isEmpty( selection )
								? " AND (" + selection + ')'
								: "" ),
						new String[]{ uri.getLastPathSegment() } );
				break;

			case MEAL_ENTRIES:
				count = database.update( DB.TABLE_MEAL_ENTRIES, values, selection, selectionArgs );
				break;

			// If the incoming URI was for a single row
			case MEAL_ENTRIES_ID:
				count = database.update( DB.TABLE_MEAL_ENTRIES,
						values,
						DB.KEY_ID + "=?"
								+ ( !TextUtils.isEmpty( selection )
								? " AND (" + selection + ')'
								: "" ),
						new String[]{ uri.getLastPathSegment() } );
				break;

			case MEAL_ITEMS:
				count = database.update( DB.TABLE_MEAL_ITEMS, values, selection, selectionArgs );
				break;

			// If the incoming URI was for a single row
			case MEAL_ITEMS_ID:
				count = database.update( DB.TABLE_MEAL_ITEMS,
						values,
						DB.KEY_ID + "=?"
								+ ( !TextUtils.isEmpty( selection )
								? " AND (" + selection + ')'
								: "" ),
						new String[]{ uri.getLastPathSegment() } );
				break;

			case EXERCISE_ENTRIES:
				count =
						database.update( DB.TABLE_EXERCISE_ENTRIES, values, selection, selectionArgs );
				break;

			// If the incoming URI was for a single row
			case EXERCISE_ENTRIES_ID:
				count = database.update( DB.TABLE_EXERCISE_ENTRIES,
						values,
						DB.KEY_ID + "=?"
								+ ( !TextUtils.isEmpty( selection )
								? " AND (" + selection + ')'
								: "" ),
						new String[]{ uri.getLastPathSegment() } );
				break;

			case PEDOMETER:
				count =
						database.update( DB.TABLE_PEDOMETER, values, selection, selectionArgs );
				break;

			default:
				throw new IllegalArgumentException( "Unknown URI " + uri );

		} // switch

		// Usually need to notify any content resolvers of the change:
		Context context = getContext();
		if( context != null && context.getContentResolver() != null )
			context.getContentResolver().notifyChange( uri, null );

		return count;

	} // update


	@Nullable
	@Override
	public String getType( @NonNull Uri uri )
	{
		switch( uriMatcher.match( uri ) )
		{
			case USERS:
				//return "vnd.android.cursor.dir/vnd.example.friends";
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.users";
			case USERS_ID:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.usersid";
			case PATIENTS:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.patients";
			case PATIENT_USERS:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.patient_users";
			case PATIENTS_ID:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.patientsid";
			case DOCTORS:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.doctors";
			case DOCTORS_ID:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.doctorsid";
			case DOCTOR_USERS:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.doctor_users";
			case GLUCOSE_ENTRIES:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.glucoseentries";
			case GLUCOSE_ENTRIES_ID:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.glucoseentriesid";
			case MEAL_ENTRIES:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.mealentries";
			case MEAL_ENTRIES_ID:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.mealentriesid";
			case MEAL_ENTRY_ITEMS:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.mealentryitems";
			case MEAL_ITEMS:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.mealitems";
			case MEAL_ITEMS_ID:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.mealitemsid";
			case EXERCISE_ENTRIES:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.exerciseentries";
			case EXERCISE_ENTRIES_ID:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.exerciseentriesid";
			case PEDOMETER:
				return "com.sugarcubes.myglucose/com.sugarcubes.myglucose.pedometer";
			default:
				throw new IllegalArgumentException( "Unsupported URI: " + uri );
		} // switch

	} // getType

} // class
