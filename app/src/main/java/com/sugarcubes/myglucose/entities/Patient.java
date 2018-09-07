package com.sugarcubes.myglucose.entities;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Patient extends ApplicationUser
{
	protected String address1;
	protected String address2;
	protected String city;
	protected String state;
	protected int zip1;
	protected int zip2;
	protected String phoneNumber;

	protected Doctor doctor;

	protected ArrayList<GlucoseEntry> glucoseEntries;
	protected ArrayList<MealEntry> mealEntries;
	protected ArrayList<ExerciseEntry> exerciseEntries;


	public Patient()
	{
		// Instantiate the doctor:
		// TODO: Use repository to get doctor details
		doctor = new Doctor();
		// Instantiate all ArrayLists:
		glucoseEntries = new ArrayList<>();
		mealEntries = new ArrayList<>();
		exerciseEntries = new ArrayList<>();

	} // constructor

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

} // class
