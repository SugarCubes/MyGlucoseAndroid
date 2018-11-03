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
import com.sugarcubes.myglucose.repositories.interfaces.IApplicationUserRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IDoctorRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IExerciseEntryRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IGlucoseEntryRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IMealEntryRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IPatientRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class DbPatientRepository implements IPatientRepository
{
	private final String LOG_TAG = this.getClass().getName();

	private ContentResolver            contentResolver;
	private Context                    context;
	private IDoctorRepository          doctorRepository;
	private IApplicationUserRepository applicationUserRepository;
	private IGlucoseEntryRepository    glucoseEntryRepository;
	private IMealEntryRepository       mealEntryRepository;
	private IExerciseEntryRepository   exerciseEntryRepository;


	public DbPatientRepository( Context context )
	{
		this.context = context;
		contentResolver = context.getContentResolver();
		doctorRepository = new DbDoctorRepository( context );
		applicationUserRepository = new DbApplicationUserRepository( context );
		glucoseEntryRepository = new DbGlucoseEntryRepository( context );
		mealEntryRepository = new DbMealEntryRepository( context );
		exerciseEntryRepository = new DbExerciseEntryRepository( context );

	} // constructor


	@Override
	public boolean exists( String userName )
	{
		Cursor cursor = getPatientCursor( userName );
		boolean exists = cursor != null && cursor.getCount() > 0;
		if( exists )
		{
			cursor.close();
		}
		return exists;

	} // exists


	private Cursor getPatientCursor( String userName )
	{
		return contentResolver.query( MyGlucoseContentProvider.PATIENTS_URI, null,
				DB.KEY_USERNAME + "=?", new String[]{ userName }, null );

	} // getPatientCursor


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
		applicationUserRepository.delete( id );            // delete from application users
		contentResolver.delete( MyGlucoseContentProvider.PATIENTS_URI,
				DB.KEY_USERNAME +
						"=?", new String[]{ id } );                    // delete patient entry

	} // delete


	public Cursor getApplicationUserCursor( String username )
	{
		return applicationUserRepository.getApplicationUserCursor( username );

	}


	@Override
	public PatientSingleton getLoggedInUser()
	{
		int loggedIn = 1;        // SQLite stores boolean values as 0=false, 1=true
		Cursor cursor = contentResolver.query( MyGlucoseContentProvider.USERS_URI, null,
				DB.KEY_USER_LOGGED_IN + "=?",
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
		// There be two tables to be updated: "users" and "patients":
		Uri patientUri = null;
		if( !exists( patientSingleton.getUserName() ) )
		{
			long timestamp = new Date().getTime();
			ContentValues patientValues = new ContentValues();
			if( patientSingleton.getDoctorUserName() != null
					&& !patientSingleton.getDoctorUserName().isEmpty() )
				patientValues.put( DB.KEY_DR_USERNAME, patientSingleton.getDoctorUserName() );
			patientValues.put( DB.KEY_USERNAME, patientSingleton.getUserName() );
			patientUri = contentResolver.insert(
					MyGlucoseContentProvider.PATIENTS_URI, patientValues );

		} // if

		// Now insert the "users" information:
		boolean userInserted = applicationUserRepository.create( patientSingleton );

		return patientUri != null && userInserted;

	} // create


	@Override
	public PatientSingleton read( String username )
	{
		PatientSingleton patientSingleton = PatientSingleton.getInstance();
		Cursor patientCursor = contentResolver.query( MyGlucoseContentProvider.PATIENT_USERS_URI,
				null, DB.KEY_USERNAME + "=?",
				new String[]{ username }, null );
		readFromCursor( patientSingleton, patientCursor );
		applicationUserRepository.readFromCursor( patientSingleton, patientCursor );    // Populate appUser
		//		Cursor patientCursor = contentResolver.query( MyGlucoseContentProvider.PATIENTS_URI, null,
		//				DB.KEY_USERNAME + "=?", new String[]{ username }, null );
		//		readFromCursor( patientSingleton, patientCursor );                                // Populate patient
		return patientSingleton;

	} // read


	@Override
	public ArrayList<PatientSingleton> readAll()
	{
		ArrayList<PatientSingleton> arrayList = new ArrayList<>();
		//		arrayList.add( read( "ArbitraryDefaultUserUsername" ) );

		return arrayList;

	} // readAll


	@Override
	public PatientSingleton readFromCursor( PatientSingleton patientSingleton, Cursor cursor )
	{
		if( cursor != null && cursor.getCount() > 0 )
		{
			cursor.moveToFirst();

			// Set values:
			patientSingleton.setDoctorUserName(
					cursor.getString( cursor.getColumnIndex( DB.KEY_DR_USERNAME ) ) );
			patientSingleton.setDoctorId(
					cursor.getString( cursor.getColumnIndex( DB.KEY_DR_ID ) ) );
			// Get doctor from repository:
			if( patientSingleton.getDoctorUserName() != null
					&& !patientSingleton.getDoctorUserName().isEmpty() )
				patientSingleton.setDoctor(
						doctorRepository.read( patientSingleton.getDoctorUserName() ) );
			applicationUserRepository.readFromCursor( patientSingleton, cursor );
			patientSingleton.setGlucoseEntries( glucoseEntryRepository.readAll(
					patientSingleton.getUserName() ) );
			patientSingleton.setExerciseEntries( exerciseEntryRepository.readAll(
					patientSingleton.getUserName() ) );
			patientSingleton.setMealEntries( mealEntryRepository.readAll(
					patientSingleton.getUserName() ) );

			cursor.close();

			return patientSingleton;

		} // if

		return null;


	} // readFromCursor


	@Override
	public ContentValues putContentValues( PatientSingleton patient )
	{
		ContentValues values = new ContentValues();
		values.put( DB.KEY_DR_USERNAME, patient.getDoctorUserName() );
		return values;

	} // putContentValues


	@Override
	public void update( String userName, PatientSingleton patient )
	{
		ContentValues values = putContentValues( patient );
		if( values.size() > 0 )
			contentResolver.update(
					MyGlucoseContentProvider.PATIENTS_URI,
					putContentValues( patient ),
					DB.KEY_USERNAME + "=?",
					new String[]{ userName } );
		applicationUserRepository.update( userName, patient );
		//		contentResolver.update( MyGlucoseContentProvider.USERS_URI,
		//				applicationUserRepository.putContentValues( item ),
		//				DB.KEY_USERNAME + "=?", new String[]{ username } );

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
			return contentResolvers[ 0 ].query( MyGlucoseContentProvider.PATIENT_USERS_URI,
					null, DB.TABLE_USERS + "." + DB.KEY_USER_LOGGED_IN + "=?",
					new String[]{ String.valueOf( 1 ) }, null );

		} // doInBackground

	} // AsyncTask

} // repository
