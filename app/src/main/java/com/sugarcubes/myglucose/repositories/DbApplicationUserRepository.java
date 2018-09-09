package com.sugarcubes.myglucose.repositories;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.entities.ApplicationUser;
import com.sugarcubes.myglucose.repositories.interfaces.IApplicationUserRepository;
import com.sugarcubes.myglucose.utils.DateUtilities;

import java.util.ArrayList;

public class DbApplicationUserRepository implements IApplicationUserRepository
{
	private ContentResolver contentResolver;
	private Uri uri = MyGlucoseContentProvider.USERS_URI;


	// We need to inject a Context object so that we can get the content resolver
	public DbApplicationUserRepository( Context context )
	{
		contentResolver = context.getContentResolver();

	} // constructor


	@Override
	public void create( ApplicationUser user )
	{
		ContentValues values = getContentValues( user );
		contentResolver.insert( uri, values );
		user.setLoggedIn( true );

	} // create


	@Override
	public ApplicationUser read( String id )
	{
		Cursor cursor = contentResolver.query( uri, null, DB.KEY_ID + "=?",
				new String[]{ id }, null );

		if( cursor != null )
		{
			cursor.moveToFirst();

			ApplicationUser user = readFromCursor( cursor );

			cursor.close();

			return user;

		} // if cursor != null

		return null;

	} // read


	@Override
	public ArrayList<ApplicationUser> readAll()
	{
		ArrayList<ApplicationUser> allUsers = new ArrayList<>();
		Cursor cursor = contentResolver.query( uri, null, null,
				null, null );

		if( cursor != null )
		{
			cursor.moveToFirst();

			while( cursor.moveToNext() )
			{
				allUsers.add( readFromCursor( cursor ) );
			}

			cursor.close();

		} // if cursor != null

		return allUsers;

	} // readAll


	@Override
	public ApplicationUser readFromCursor( Cursor cursor )
	{
		// NOTE: Do not check if not null, moveToFirst, or moveToNext. This is left
		// 	to the calling class/method.

		ApplicationUser user = new ApplicationUser();

		user.setId( cursor.getString( cursor.getColumnIndex( DB.KEY_ID ) ) );
		user.setFirstName( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_FIRST_NAME ) ) );
		user.setLastName( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_LAST_NAME ) ) );
		user.setAddress1( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_ADDRESS1 ) ) );
		user.setAddress2( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_ADDRESS2 ) ) );
		user.setCity( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_CITY ) ) );
		user.setState( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_STATE ) ) );
		user.setZip1( cursor.getInt( cursor.getColumnIndex( DB.KEY_USER_ZIP1 ) ) );
		user.setZip2( cursor.getInt( cursor.getColumnIndex( DB.KEY_USER_ZIP2 ) ) );
		user.setPhoneNumber( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_PHONE ) ) );
		user.setEmail( cursor.getString( cursor.getColumnIndex( DB.KEY_USER_EMAIL ) ) );
		user.setUserName( cursor.getString( cursor.getColumnIndex( DB.KEY_USERNAME ) ) );
		user.setDate( DateUtilities.convertStringToDate(
				cursor.getString( cursor.getColumnIndex( DB.KEY_DATE ) )
		) );
		user.setTimestamp( cursor.getLong( cursor.getColumnIndex( DB.KEY_TIMESTAMP ) ) );
		user.setLoggedIn( true );

		return user;

	} // readFromCursor


	@NonNull
	@Override
	public ContentValues getContentValues( ApplicationUser user )
	{
		ContentValues values = new ContentValues();
		values.put( DB.KEY_ID, user.getId() );
		values.put( DB.KEY_USER_FIRST_NAME, user.getFirstName() );
		values.put( DB.KEY_USER_LAST_NAME, user.getLastName() );
		values.put( DB.KEY_USER_ADDRESS1, user.getAddress1() );
		values.put( DB.KEY_USER_ADDRESS2, user.getAddress2() );
		values.put( DB.KEY_USER_CITY, user.getCity() );
		values.put( DB.KEY_USER_STATE, user.getState() );
		values.put( DB.KEY_USER_ZIP1, user.getZip1() );
		values.put( DB.KEY_USER_ZIP2, user.getZip2() );
		values.put( DB.KEY_USER_PHONE, user.getPhoneNumber() );
		values.put( DB.KEY_USER_EMAIL, user.getEmail() );
		values.put( DB.KEY_USERNAME, user.getUserName() );
		values.put( DB.KEY_DATE, user.getDate().toString() );
		values.put( DB.KEY_TIMESTAMP, user.getTimestamp() );
		return values;

	} // getContentValues


	@Override
	public void update( String id, ApplicationUser item )
	{
		ContentValues values = getContentValues( item );
		contentResolver.update( uri, values, DB.KEY_ID + "=?", new String[]{ id } );

	} // update


	@Override
	public void delete( ApplicationUser item )
	{
		contentResolver.delete( uri, DB.KEY_ID + "=?", new String[]{ item.getId() } );

	} // delete


	@Override
	public void delete( String id )
	{
		contentResolver.delete( uri, DB.KEY_ID + "=?", new String[]{ id } );

	} // delete

} // repository
