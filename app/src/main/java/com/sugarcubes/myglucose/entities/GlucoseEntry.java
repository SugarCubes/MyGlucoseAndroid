package com.sugarcubes.myglucose.entities;

import android.database.Cursor;

import com.sugarcubes.myglucose.enums.BeforeAfter;
import com.sugarcubes.myglucose.enums.WhichMeal;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

import java.util.Date;

public class GlucoseEntry
{
	private int id;
	private String remoteId;
	private String userEmail;
	private float measurement;			// DEFAULT: mmol/L. May need conversion to display correctly
//	private GlucoseUnits units;			// enum	mmol/L or mg/dL
	private BeforeAfter beforeAfter;	// enum representing before or after a meal
	private WhichMeal whichMeal;		// enum representing which meal entry taken before/after
	private Date date;
	private long timeStamp;
	private PatientSingleton patient;


	public GlucoseEntry()
	{

	}

	public void loadFromCursor( Cursor glucoseCursor )
	{

	}

//	public void load( Context context, String glucoseEntryId )
//	{
//		ContentResolver contentResolver = context.getContentResolver();
//		Cursor cursor = contentResolver.query( MyGlucoseContentProvider.GLUCOSE_ENTRIES_URI, null,
//				DB.KEY_ID + "=?", new String[]{ glucoseEntryId }, null );
//
//	} // load


	public int getId()
	{
		return id;
	}

	public void setId( int id )
	{
		this.id = id;
	}

	public String getRemoteId()
	{
		return remoteId;
	}

	public void setRemoteId( String remoteId )
	{
		this.remoteId = remoteId;
	}

	public long getTimeStamp()
	{
		return timeStamp;
	}

	public void setTimeStamp( long timeStamp )
	{
		this.timeStamp = timeStamp;
	}

	public String getUserEmail()
	{
		return userEmail;
	}

	public void setUserEmail( String userEmail )
	{
		this.userEmail = userEmail;
	}

	public float getMeasurement()
	{
		return measurement;
	}

	public void setMeasurement( float measurement )
	{
		this.measurement = measurement;
	}

	public BeforeAfter getBeforeAfter()
	{
		return beforeAfter;
	}

	public void setBeforeAfter( BeforeAfter beforeAfter )
	{
		this.beforeAfter = beforeAfter;
	}

	public WhichMeal getWhichMeal()
	{
		return whichMeal;
	}

	public void setWhichMeal( WhichMeal whichMeal )
	{
		this.whichMeal = whichMeal;
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
		return timeStamp;
	}

	public void setTimestamp( long timeStamp )
	{
		this.timeStamp = timeStamp;
	}

	public PatientSingleton getPatient()
	{
		return patient;
	}

	public void setPatient( PatientSingleton patient )
	{
		this.patient = patient;
	}
} // class
