//--------------------------------------------------------------------------------------//
//																						//
// File Name:	DbPatientRepository.java												//
// Programmer:	J.T. Blevins (jt.blevins@gmail.com)										//
// Date:		09/08/2018																//
// Purpose:		A repository to allow MealEntry and Patient data manipulation in a 		//
// 				SQLite database. 														//
// 				NOTE: Only the PatientSingleton should access most of the methods, for	//
//				security purposes.														//
//																						//
//--------------------------------------------------------------------------------------//

package com.sugarcubes.myglucose.repositories;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.entities.ApplicationUser;
import com.sugarcubes.myglucose.repositories.interfaces.IApplicationUserRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IPatientRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class DbPatientRepository implements IPatientRepository
{

	private ContentResolver contentResolver;
	private Uri uri = MyGlucoseContentProvider.PATIENTS_URI;
	private Uri usersUri = MyGlucoseContentProvider.USERS_URI;
	private ApplicationUser user;
	private Context context;

	private final String LOG_TAG = this.getClass().getName();


	public DbPatientRepository( Context context )
	{
		this.context = context;
		contentResolver = context.getContentResolver();

	} // constructor


	/**
	 * populate - Fetches information for the logged-in user in the database
	 * @param patientSingleton - Explicitly require the patient object
	 * @param cursor - The cursor to get the information from. This is typically retrieved and
	 *               returned by the CursorLoader.
	 */
	@Override
	public void populate( PatientSingleton patientSingleton, Cursor cursor )
	{
		if( cursor != null )
		{
			cursor.moveToFirst();

			patientSingleton.setEmail( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_EMAIL ) ) );
			patientSingleton.setAddress1( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_ADDRESS1 ) ) );
			patientSingleton.setAddress2( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_ADDRESS2 ) )  );
			patientSingleton.setCity( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_CITY ) ) );
			try {
				// TODO: Test
				SimpleDateFormat formatter = new SimpleDateFormat( "E yyyy.MM.dd 'at' hh:mm:ss a zzz", Locale.US );
				if( cursor.getString( cursor.getColumnIndex( DB.KEY_DATE ) ) != null )
				{
					Date date = formatter.parse( cursor.getString( cursor.getColumnIndex( DB.KEY_DATE ) ) );
					Log.d( LOG_TAG, "Date found: " + date.toString() );
					patientSingleton.setDate( date );
				}
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
			patientSingleton.setEmail( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_EMAIL ) ) );
			patientSingleton.setFirstName( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_FIRST_NAME ) ) );
			patientSingleton.setLastName( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_LAST_NAME ) ) );
			patientSingleton.setPhoneNumber( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_PHONE ) ) );
			patientSingleton.setState( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_STATE ) ) );
			patientSingleton.setZip1( cursor.getInt( cursor.getColumnIndex( DB.KEY_USER_ZIP1 ) ) );
			patientSingleton.setZip2( cursor.getInt( cursor.getColumnIndex( DB.KEY_USER_ZIP2 ) ) );
			patientSingleton.setTimestamp( cursor.getLong( cursor.getColumnIndex( DB.KEY_TIMESTAMP ) ) );
			patientSingleton.setUserName( cursor.getString( cursor.getColumnIndex( DB.KEY_USERNAME ) ) );
			patientSingleton.setLoggedIn( true );

			cursor.close();

		} // if

	} // populate


	@Override
	public boolean delete( PatientSingleton patientSingleton )
	{
		ContentResolver contentResolver = context.getContentResolver();
		int a = contentResolver.delete( MyGlucoseContentProvider.USERS_URI,
				DB.KEY_USER_EMAIL + "=?",
				new String[]{ patientSingleton.getEmail() } );
		int b = contentResolver.delete( MyGlucoseContentProvider.PATIENTS_URI,
				DB.KEY_USER_EMAIL + "=?",
				new String[]{ patientSingleton.getEmail() } );
		int c = contentResolver.delete( MyGlucoseContentProvider.DOCTORS_URI, // (For good measure)
				DB.KEY_USER_EMAIL + "=?",
				new String[]{ patientSingleton.getEmail() } );

		if( a > 0 && ( b > 0 || c > 0 ) )
			return true;

		return false;

	} // populate


	@Override
	public boolean create( PatientSingleton patientSingleton )
	{
		// There has to be two tables updated: "users" and "patients":
		long timestamp = new Date().getTime();
		ContentValues patientValues = new ContentValues();
		if( patientSingleton.getDoctor() != null )
			patientValues.put( DB.KEY_DR_ID, patientSingleton.getDoctor().getEmail() );
		patientValues.put( DB.KEY_USER_EMAIL, patientSingleton.getEmail() );
		Uri patientUri = contentResolver.insert(
				MyGlucoseContentProvider.PATIENTS_URI, patientValues );

		// Now insert the "users" information:
		ContentValues userValues = new ContentValues();
		userValues.put( DB.KEY_USER_EMAIL, patientSingleton.getEmail() );
		userValues.put( DB.KEY_USER_ADDRESS1, patientSingleton.getAddress1() );
		userValues.put( DB.KEY_USER_ADDRESS2, patientSingleton.getAddress2() );
		userValues.put( DB.KEY_USER_CITY, patientSingleton.getCity() );
		userValues.put( DB.KEY_USER_ZIP1, patientSingleton.getZip1() );
		userValues.put( DB.KEY_USER_ZIP2, patientSingleton.getZip2() );
		userValues.put( DB.KEY_USER_FIRST_NAME, patientSingleton.getFirstName() );
		userValues.put( DB.KEY_USER_LAST_NAME, patientSingleton.getLastName() );
		userValues.put( DB.KEY_USER_LOGGED_IN, 1 );
		userValues.put( DB.KEY_USER_PHONE, patientSingleton.getPhoneNumber() );
		userValues.put( DB.KEY_USERNAME, patientSingleton.getUserName() );
		userValues.put( DB.KEY_TIMESTAMP, timestamp );
		Uri userUri = contentResolver.insert(
				MyGlucoseContentProvider.USERS_URI, userValues );

		if( patientUri != null && userUri != null )
			return true;

		return false;

	} // create


	@Override
	public Cursor getCursorForLoggedInUser()
	{
		try
		{
			return new GetLoggedInUserTask().execute( contentResolver ).get();
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}
		catch( ExecutionException e )
		{
			e.printStackTrace();
		}

		return null;

	} // getCursorForLoggedInUser



	public static class GetLoggedInUserTask extends AsyncTask<ContentResolver, Void, Cursor>
	{
		@Override
		protected Cursor doInBackground( ContentResolver... contentResolvers )
		{
			return contentResolvers[0].query( MyGlucoseContentProvider.PATIENT_USERS_URI,
					null, DB.TABLE_USERS + "." + DB.KEY_USER_LOGGED_IN + "=?",
					new String[]{ String.valueOf( 1 ) }, null );

		} // doInBackground

	} // AsyncTask

} // repository
