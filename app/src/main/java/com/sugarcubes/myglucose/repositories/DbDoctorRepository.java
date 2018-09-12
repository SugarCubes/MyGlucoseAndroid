package com.sugarcubes.myglucose.repositories;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.entities.ApplicationUser;
import com.sugarcubes.myglucose.entities.Doctor;
import com.sugarcubes.myglucose.repositories.interfaces.IApplicationUserRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IDoctorRepository;

import java.util.ArrayList;

public class DbDoctorRepository implements IDoctorRepository
{
	private ContentResolver contentResolver;
	private Uri uri = MyGlucoseContentProvider.DOCTORS_URI;
	private IApplicationUserRepository userRepository;
	private ApplicationUser user;


	public DbDoctorRepository( Context context )
	{
		contentResolver = context.getContentResolver();
		this.userRepository = new DbApplicationUserRepository( context );

	} // constructor


	@Override
	public void create( Doctor item )
	{

	}

	@Override
	public Doctor read( String id )
	{
		return null;
	}

	@Override
	public ArrayList<Doctor> readAll()
	{
		return null;
	}

	@Override
	public Doctor readFromCursor( Cursor cursor )
	{
		return null;
	}

	@Override
	public ContentValues getContentValues( Doctor item )
	{
		return null;
	}

	@Override
	public void update( String id, Doctor item )
	{

	}

	@Override
	public void delete( Doctor item )
	{

	}

	@Override
	public void delete( String id )
	{

	}

	@Override
	public Doctor getLoggedInUser()
	{
		// TODO: Update doctor info
		return (Doctor) userRepository.getLoggedInUser();
	}

}
