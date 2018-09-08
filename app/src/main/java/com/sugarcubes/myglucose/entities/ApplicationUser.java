package com.sugarcubes.myglucose.entities;

public class ApplicationUser
{
	protected static boolean loggedIn = false;
	protected String id;
	protected String email;
	protected String firstName;
	protected String lastName;
	protected String userName;

	public ApplicationUser()
	{

	}


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
		this.loggedIn = loggedIn;
	}
} // class
