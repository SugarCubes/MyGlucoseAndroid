package com.sugarcubes.myglucose.entities;

import android.database.Cursor;

import com.sugarcubes.myglucose.db.DB;

import java.util.Date;

public class ApplicationUser
{
	protected static boolean loggedIn = false;
	protected String email;					// Primary key
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

	protected Date date;
	protected long timestamp;

	public ApplicationUser()
	{
		email 		= "";
		firstName 	= "";
		lastName 	= "";
		userName	= "";
		address1	= "";
		address2	= "";
		city		= "";
		state		= "";
		zip1		= -1;
		zip2		= -1;
		phoneNumber = "";
		date		= new Date();
		timestamp	= -1;

	} // constructor


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

	public String getAddress1()
	{
		return address1;
	}

	public void setAddress1( String address1 )
	{
		this.address1 = address1;
	}

	public String getAddress2()
	{
		return address2;
	}

	public void setAddress2( String address2 )
	{
		this.address2 = address2;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity( String city )
	{
		this.city = city;
	}

	public String getState()
	{
		return state;
	}

	public void setState( String state )
	{
		this.state = state;
	}

	public int getZip1()
	{
		return zip1;
	}

	public void setZip1( int zip1 )
	{
		this.zip1 = zip1;
	}

	public int getZip2()
	{
		return zip2;
	}

	public void setZip2( int zip2 )
	{
		this.zip2 = zip2;
	}

	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	public void setPhoneNumber( String phoneNumber )
	{
		this.phoneNumber = phoneNumber;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate( Date date )
	{
		this.date = date;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp( long timestamp )
	{
		this.timestamp = timestamp;
	}

	@Override
	public String toString()
	{
		return "ApplicationUser{" +
				"email='" + email + '\'' +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", userName='" + userName + '\'' +
				", address1='" + address1 + '\'' +
				", address2='" + address2 + '\'' +
				", city='" + city + '\'' +
				", state='" + state + '\'' +
				", zip1=" + zip1 +
				", zip2=" + zip2 +
				", phoneNumber='" + phoneNumber + '\'' +
				", date=" + date +
				", timestamp=" + timestamp +
				'}';
	} // toString

} // class
