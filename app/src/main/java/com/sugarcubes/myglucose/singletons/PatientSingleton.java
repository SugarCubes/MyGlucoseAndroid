package com.sugarcubes.myglucose.singletons;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.entities.ApplicationUser;
import com.sugarcubes.myglucose.entities.Doctor;
import com.sugarcubes.myglucose.entities.ExerciseEntry;
import com.sugarcubes.myglucose.entities.GlucoseEntry;
import com.sugarcubes.myglucose.entities.MealEntry;

import java.util.ArrayList;

public class PatientSingleton extends ApplicationUser
{
	private static PatientSingleton singleton;

	protected Doctor doctor;

	protected ArrayList<GlucoseEntry> glucoseEntries;
	protected ArrayList<MealEntry> mealEntries;
	protected ArrayList<ExerciseEntry> exerciseEntries;


	private PatientSingleton()
	{
		// TODO: Check if logged in
		loggedIn = false;
		// Instantiate the doctor:
		// TODO: Use ContentProvider to get doctor details
		doctor = new Doctor();
		// Instantiate all ArrayLists:
		glucoseEntries = new ArrayList<>();
		mealEntries = new ArrayList<>();
		exerciseEntries = new ArrayList<>();

	} // constructor


	// Since there should only be 1 user, we make in impossible to create more than 1 instance
	//		of the class.
	public static PatientSingleton getInstance()
	{
		if( singleton == null )
			singleton = new PatientSingleton();

		return singleton;

	} // getInstance


	public void loadFromCursor( Cursor cursor, Context context )
	{
		super.loadFromCursor( cursor );			// Be sure to load the _id before proceeding

//		DB dbHandler = new DB( context );
//		SQLiteDatabase db = dbHandler.getReadableDatabase();
//
//		// LOAD GLUCOSE ENTRIES
//		Cursor glucoseCursor = db.query( DB.TABLE_PATIENTS, null,
//				DB.KEY_USER_ID + "=?", null, null, null, null );
//		glucoseCursor.moveToFirst();

		try
		{
//			while( glucoseCursor.moveToNext() )
//			{
//				GlucoseEntry entry = new GlucoseEntry();
//				entry.loadFromCursor( glucoseCursor );
//				glucoseEntries.add( entry );
//			}
			glucoseEntries = GlucoseEntry.getAllEntries( context, id );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

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

	public Doctor getDoctor()
	{
		return doctor;
	}

	public void setDoctor( Doctor doctor )
	{
		this.doctor = doctor;
	}

	public ArrayList<GlucoseEntry> getGlucoseEntries()
	{
		return glucoseEntries;
	}

	public ArrayList<MealEntry> getMealEntries()
	{
		return mealEntries;
	}

	public ArrayList<ExerciseEntry> getExerciseEntries()
	{
		return exerciseEntries;
	}

} // class
