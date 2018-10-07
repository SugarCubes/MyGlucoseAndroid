package com.sugarcubes.myglucose.entities;

import java.util.Date;

public class ApplicationUser
{
	protected static boolean loggedIn = false;
	protected String loginToken;
	protected long loginExpirationTimestamp;
	protected String id;
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
	protected String height;
	protected String weight;

	protected Date date;
	protected long timestamp;

	public ApplicationUser()
	{
		Date newDate = new Date();
		id 			= "";
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
		height = "";
		weight = "";
		date		= newDate;
		timestamp	= newDate.getTime();
		loginToken 	= "";
		loginExpirationTimestamp = newDate.getTime();

	} // constructor

	public String getId()
	{
		return id;
	}

	public void setId( String id )
	{
		id = id;
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

	public String getWeight()
	{
		return weight;
	}

	public void setWeight( String weight )
	{
		this.weight = weight;
	}

	public String getHeight()
	{
		return height;
	}

	public void setHeight( String height )
	{
		this.height = height;
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

	public String getLoginToken()
	{
		return loginToken;
	}

	public void setLoginToken( String loginToken )
	{
		this.loginToken = loginToken;
	}

	public long getLoginExpirationTimestamp()
	{
		return loginExpirationTimestamp;
	}

	public void setLoginExpirationTimestamp( long loginExpirationTimestamp )
	{
		this.loginExpirationTimestamp = loginExpirationTimestamp;
	}

	@Override
	public String toString()
	{
		return "ApplicationUser{" +
				"loggedIn: '" + loggedIn + '\'' +
				", email='" + email + '\'' +
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
				", loginToken=" + loginToken +
				", loginExpiration=" + loginExpirationTimestamp +
				'}';
	} // toString

} // class
