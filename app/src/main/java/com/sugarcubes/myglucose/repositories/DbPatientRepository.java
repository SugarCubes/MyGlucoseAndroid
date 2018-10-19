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

import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.repositories.interfaces.IPatientRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class DbPatientRepository implements IPatientRepository
{

	private ContentResolver contentResolver;
	private Context context;
	private DbApplicationUserRepository dbApplicationUserRepository;

	private final String LOG_TAG = this.getClass().getName();


	public DbPatientRepository( Context context )
	{
		this.context = context;
		contentResolver = context.getContentResolver();
		dbApplicationUserRepository = new DbApplicationUserRepository( context );

	} // constructor


	@Override
	public boolean delete( PatientSingleton patientSingleton )
	{
		ContentResolver contentResolver = context.getContentResolver();
		int a = contentResolver.delete( MyGlucoseContentProvider.USERS_URI,
				DB.KEY_USERNAME + "=?",
				new String[]{ patientSingleton.getEmail() } );
		int b = contentResolver.delete( MyGlucoseContentProvider.PATIENTS_URI,
				DB.KEY_USERNAME + "=?",
				new String[]{ patientSingleton.getEmail() } );
		int c = contentResolver.delete( MyGlucoseContentProvider.DOCTORS_URI, // (For good measure)
				DB.KEY_USERNAME + "=?",
				new String[]{ patientSingleton.getEmail() } );

		if( a > 0 && ( b > 0 || c > 0 ) )
			return true;

		return false;

	} // populate

	@Override
	public void delete( String id )
	{
		dbApplicationUserRepository.delete( id );			// delete from application users
		contentResolver.delete( MyGlucoseContentProvider.PATIENTS_URI, DB.KEY_USERNAME + "=?",
				new String[]{ id } );					// delete patient entry

	} // delete


	public Cursor getApplicationUserCursor( String username )
	{
		return dbApplicationUserRepository.getApplicationUserCursor( username );

	}


	@Override
	public PatientSingleton getLoggedInUser()
	{
		int loggedIn = 1;		// SQLite stores boolean values as 0=false, 1=true
		Cursor cursor = contentResolver.query( MyGlucoseContentProvider.USERS_URI, null, DB.KEY_USER_LOGGED_IN + "=?",
				new String[]{ String.valueOf( loggedIn ) }, null );

		if( cursor != null )
		{
			cursor.moveToFirst();

			PatientSingleton user = readFromCursor( PatientSingleton.getInstance(), cursor );

			cursor.close();

			return user;

		} // if cursor != null

		return null;

	} // getLoggedInUser


	@Override
	public boolean create( PatientSingleton patientSingleton )
	{
		// There has to be two tables updated: "users" and "patients":
		long timestamp = new Date().getTime();
		ContentValues patientValues = new ContentValues();
		if( patientSingleton.getDoctor() != null )
			patientValues.put( DB.KEY_DR_ID, patientSingleton.getDoctor().getEmail() );
		patientValues.put( DB.KEY_USERNAME, patientSingleton.getUserName() );
		Uri patientUri = contentResolver.insert(
				MyGlucoseContentProvider.PATIENTS_URI, patientValues );

		// Now insert the "users" information:
		boolean userInserted = dbApplicationUserRepository.create( patientSingleton );

		if( patientUri != null && userInserted )
			return true;

		return false;

	} // create


	@Override
	public PatientSingleton read( String username )
	{
		PatientSingleton patientSingleton = PatientSingleton.getInstance();
		Cursor userCursor = contentResolver.query( MyGlucoseContentProvider.USERS_URI, null,
				DB.KEY_USERNAME + "=?", new String[]{ username }, null );
		dbApplicationUserRepository.readFromCursor( patientSingleton, userCursor );	// Populate appUser
		Cursor patientCursor = contentResolver.query( MyGlucoseContentProvider.PATIENTS_URI, null,
				DB.KEY_USERNAME + "=?", new String[]{ username }, null );
		readFromCursor( patientSingleton, patientCursor );								// Populate patient
		return patientSingleton;

	} // read


	@Override
	public ArrayList<PatientSingleton> readAll()
	{
		ArrayList<PatientSingleton> arrayList = new ArrayList<>(  );
		arrayList.add( read( "ArbitraryDefaultUserUsername" ) );

		return arrayList;

	} // readAll


	@Override
	public PatientSingleton readFromCursor( PatientSingleton patientSingleton, Cursor cursor )
	{
//		DbApplicationUserRepository applicationUserRepository
//				= new DbApplicationUserRepository( context );
		dbApplicationUserRepository.readFromCursor( patientSingleton, cursor );
	    try
        {
        	// TODO: Get doctor from repository
//            DbDoctorRepository doctorRepository = new DbDoctorRepository(context);
//            patientSingleton.setDoctor(doctorRepository.readAll().get(0));
            return patientSingleton;
        }
        catch( Exception e )
        {
        	e .printStackTrace();
            patientSingleton = null;
            return patientSingleton;
        }


	} // readFromCursor

	@Override
	public ContentValues putContentValues( PatientSingleton item )
	{
		ContentValues values = new ContentValues();
		if( item.getDoctor() != null )
			values.put( DB.KEY_DR_ID, item.getDoctor().getUserName() );
		return values;
	}

	@Override
	public void update( String username, PatientSingleton item )
	{
		contentResolver.update( MyGlucoseContentProvider.PATIENTS_URI, putContentValues( item ),
				DB.KEY_USERNAME + "=?", new String[]{ username } );
		contentResolver.update( MyGlucoseContentProvider.USERS_URI,
				dbApplicationUserRepository.putContentValues( item ),
				DB.KEY_USERNAME + "=?", new String[]{ username } );

	} // update


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


	/**
	 * Used in getCursorForLoggedInUser to return the logged in user
	 */
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
