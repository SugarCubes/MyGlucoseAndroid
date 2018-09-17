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
	private ApplicationUser user;


	public DbDoctorRepository( Context context )
	{
		contentResolver = context.getContentResolver();
		this.dbApplicationUserRepository = new DbApplicationUserRepository( context );

	} // constructor


	@Override
	public boolean create( Doctor item )
	{
		boolean createUser = dbApplicationUserRepository.create( item );
		Uri createUri = contentResolver.insert( doctorsUri, getContentValues( item ) );

		return createUser && createUri != null;

	} // create


	@Override
	public Doctor read( String userName )
	{
		Doctor doctor = (Doctor) new Doctor();	// Get an appUser and cast
		Cursor cursor = getDoctorCursor( userName );
		readFromCursor( doctor, cursor );
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
	public ContentValues getContentValues( Doctor doctor )
	{
		ContentValues values = new ContentValues();

		values.put( DB.KEY_DR_DEGREE_ABBREVIATION, doctor.getDegreeAbbreviation() );

		return values;

	} // getContentValues


	@Override
	public void update( String id, Doctor doctor )
	{
		ContentValues values = getContentValues( doctor );
		contentResolver.update( doctorsUri, values, DB.KEY_USERNAME + "=?", new String[]{ id } );

	} // update


	@Override
	public boolean delete( Doctor doctor )
	{
		return true;

	} // delete


	@Override
	public void delete( String username )
	{
		dbApplicationUserRepository.delete( username );
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
