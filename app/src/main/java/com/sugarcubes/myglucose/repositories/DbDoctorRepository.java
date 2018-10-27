package com.sugarcubes.myglucose.repositories;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.entities.ApplicationUser;
import com.sugarcubes.myglucose.entities.Doctor;
import com.sugarcubes.myglucose.repositories.interfaces.IDoctorRepository;

import java.util.ArrayList;

public class DbDoctorRepository implements IDoctorRepository
{
	private ContentResolver contentResolver;
	private Uri doctorsUri = MyGlucoseContentProvider.DOCTORS_URI;
	private DbApplicationUserRepository dbApplicationUserRepository;


	public DbDoctorRepository( Context context )
	{
		contentResolver = context.getContentResolver();
		this.dbApplicationUserRepository = new DbApplicationUserRepository( context );

	} // constructor


	@Override
	public boolean create( Doctor item )
	{
		boolean createUser = dbApplicationUserRepository.create( item );	// Create ApplicationUser
		Uri createUri = contentResolver.insert( doctorsUri, putContentValues( item ) );

		return createUser && createUri != null;

	} // create


	@Override
	public Doctor read( String userName )
	{
		Doctor doctor = new Doctor();					// Get an empty object
		Cursor cursor = getDoctorCursor( userName );	// Get a cursor object
		readFromCursor( doctor, cursor );				// Pass the doctor to the cursor
		// Load the info related to ApplicationUser:
		dbApplicationUserRepository.readFromCursor( doctor, getApplicationUserCursor( userName ) );

		return doctor;

	} // read


	@Override
	public ArrayList<Doctor> readAll()
	{
		ArrayList<Doctor> doctors = new ArrayList<>();

		Cursor cursor =  contentResolver.query( doctorsUri,
				null, null, null, null );

		if( cursor != null )
		{
			cursor.moveToFirst();

			while( cursor.moveToNext() )
			{
				Doctor doctor = new Doctor();
				readFromCursor( doctor, cursor );								// Set the Doctor values
				Cursor userCursor = getApplicationUserCursor( doctor.getUserName() );
				dbApplicationUserRepository.readFromCursor( doctor, userCursor );	// Set the User values
				doctors.add( doctor );

			} // while

			cursor.close();
		}

		return doctors;

	} // readAll


	@Override
	public Doctor readFromCursor( Doctor doctor, Cursor cursor )
	{
		Doctor dr = new Doctor();

		dr.setUserName( cursor.getString( cursor.getColumnIndex( DB.KEY_USERNAME ) ) );
		dr.setDegreeAbbreviation( cursor.getColumnName(
				cursor.getColumnIndex( DB.KEY_DR_DEGREE_ABBREVIATION ) ) );

		return dr;

	} // readFromCursor


	@Override
	public ContentValues putContentValues( Doctor doctor )
	{
		ContentValues values = new ContentValues();

		values.put( DB.KEY_DR_DEGREE_ABBREVIATION, doctor.getDegreeAbbreviation() );

		return values;

	} // putContentValues


	@Override
	public void update( String id, Doctor doctor )
	{
		ContentValues values = putContentValues( doctor );
		contentResolver.update( doctorsUri, values, DB.KEY_USERNAME + "=?", new String[]{ id } );
		dbApplicationUserRepository.update( id, doctor );		// Also update in the appUser repo

	} // update


	@Override
	public boolean delete( Doctor doctor )
	{
		dbApplicationUserRepository.delete( doctor.getUserName() );	// Delete appUser info
		contentResolver.delete( doctorsUri,
				DB.KEY_USERNAME + "=?", new String[]{ doctor.getUserName() } );
		return true;

	} // delete


	@Override
	public void delete( String username )
	{
		dbApplicationUserRepository.delete( username );				// Delete appUser info
		contentResolver.delete( doctorsUri, DB.KEY_USERNAME + "=?", new String[]{ username } );

	} // delete


	public Cursor getApplicationUserCursor( String username )
	{
		return dbApplicationUserRepository.getApplicationUserCursor( username );

	} // getApplicationUserCursor


	public Cursor getDoctorCursor( String username )
	{
		return contentResolver.query( doctorsUri, null,
				DB.KEY_USERNAME + "=?", new String[]{ username }, null );

	} // getDoctorCursor

} // repository
