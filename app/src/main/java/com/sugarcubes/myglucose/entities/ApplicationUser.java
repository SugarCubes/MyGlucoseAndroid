package com.sugarcubes.myglucose.entities;

import android.database.Cursor;

import com.sugarcubes.myglucose.db.DB;

public class ApplicationUser
{
	protected static boolean loggedIn = false;
	protected String id;
	protected String email;
	protected String firstName;
	protected String lastName;
	protected String userName;

	protected String address1;
	protected String address2;
	protected String city;
	protected String state;
	protected int zip1;
	protected int zip2;
	protected String phoneNumber;

	public ApplicationUser()
	{

	}


	public void loadFromCursor( Cursor cursor )
	{
		if( cursor != null && cursor.getCount() > 0 )	// Load the patient info
		{
			cursor.moveToFirst();
			this.id				= cursor.getString( cursor.getColumnIndex( DB.KEY_ID ) );
			this.firstName 		= cursor.getString( cursor.getColumnIndex( DB.KEY_USER_FIRST_NAME ) );
			this.lastName 		= cursor.getString( cursor.getColumnIndex( DB.KEY_USER_LAST_NAME ) );
			this.address1 		= cursor.getString( cursor.getColumnIndex( DB.KEY_USER_ADDRESS1 ) );
			this.address2 		= cursor.getString( cursor.getColumnIndex( DB.KEY_USER_ADDRESS2 ) );
			this.city	 		= cursor.getString( cursor.getColumnIndex( DB.KEY_USER_CITY ) );
			this.state	 		= cursor.getString( cursor.getColumnIndex( DB.KEY_USER_STATE ) );
			this.zip1	 		= cursor.getInt( cursor.getColumnIndex( DB.KEY_USER_ZIP1 ) );
			this.zip2	 		= cursor.getInt( cursor.getColumnIndex( DB.KEY_USER_ZIP2 ) );
			this.phoneNumber	= cursor.getString( cursor.getColumnIndex( DB.KEY_USER_PHONE ) );
			this.email			= cursor.getString( cursor.getColumnIndex( DB.KEY_USER_EMAIL ) );
			this.userName		= cursor.getString( cursor.getColumnIndex( DB.KEY_USERNAME ) );
			loggedIn 			= true;

		} // if

	} // loadFromCursor


	public String getId()
	{
		return id;
	}

	public void setId( String id )
	{
		this.id = id;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail( String email )
	{
		this.email = email;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName( String firstName )
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName( String lastName )
	{
		this.lastName = lastName;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName( String userName )
	{
		this.userName = userName;
	}

	public boolean isLoggedIn()
	{
		return loggedIn;
	}

	public void setLoggedIn( boolean loggedIn )
	{
		ApplicationUser.loggedIn = loggedIn;
	}

} // class
