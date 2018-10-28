package com.sugarcubes.myglucose.entities;

import com.sugarcubes.myglucose.db.DB;

import org.json.JSONException;
import org.json.JSONObject;

public class Doctor extends ApplicationUser
{
	protected String degreeAbbreviation;

	public Doctor()
	{
		degreeAbbreviation = "MD";				// Default abbreviation

	} // constructor

	public String getDegreeAbbreviation()
	{
		return degreeAbbreviation;
	}

	public void setDegreeAbbreviation( String degreeAbbreviation )
	{
		this.degreeAbbreviation = degreeAbbreviation;
	}


	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject doctor = new JSONObject();
		// APPLICATION USER ATTRIBUTES:
		if( !id.isEmpty() )
			doctor.put( DB.KEY_REMOTE_ID, id );
		if( !email.isEmpty() )
			doctor.put( DB.KEY_USER_EMAIL, email );
		if( !firstName.isEmpty() )
			doctor.put( DB.KEY_USER_FIRST_NAME, firstName );
		if( !lastName.isEmpty() )
			doctor.put( DB.KEY_USER_LAST_NAME, lastName );
		if( !userName.isEmpty() )
			doctor.put( DB.KEY_USERNAME, userName );
		if( !address1.isEmpty() )
			doctor.put( DB.KEY_USER_ADDRESS1, address1 );
		if( !address2.isEmpty() )
			doctor.put( DB.KEY_USER_ADDRESS2, address2 );
		if( !city.isEmpty() )
			doctor.put( DB.KEY_USER_CITY, city );
		if( !state.isEmpty() )
			doctor.put( DB.KEY_USER_STATE, state );
		if( !phoneNumber.isEmpty() )
			doctor.put( DB.KEY_USER_PHONE, phoneNumber );
		if( !height.isEmpty() )
			doctor.put( DB.KEY_USER_HEIGHT, height );
		if( !weight.isEmpty() )
			doctor.put( DB.KEY_USER_WEIGHT, weight );
		if( !loginToken.isEmpty() )
			doctor.put( DB.KEY_USER_LOGIN_TOKEN, loginToken );
		if( zip1 > 0 )
			doctor.put( DB.KEY_USER_ZIP1, zip1 );
		if( zip2 > 0 )
			doctor.put( DB.KEY_USER_ZIP1, zip2 );
		if( timestamp > 0 )
			doctor.put( DB.KEY_TIMESTAMP, timestamp );
		if( loginExpirationTimestamp > 0 )
			doctor.put( DB.KEY_USER_LOGIN_EXPIRATION_TIMESTAMP, loginExpirationTimestamp );
		if( createdAt != null )
			doctor.put( DB.KEY_CREATED_AT, createdAt.toString() );
		if( updatedAt != null )
			doctor.put( DB.KEY_UPDATED_AT, updatedAt.toString() );
		// DOCTOR ATTRIBUTES:
		if( !degreeAbbreviation.isEmpty() )
			doctor.put( DB.KEY_DR_DEGREE_ABBREVIATION, degreeAbbreviation );

		return doctor;

	}

	@Override
	public String toString()
	{
		try
		{
			return toJSONObject().toString();
		}
		catch( JSONException e )
		{
			e.printStackTrace();
			return "";
		}

	} // toString

} // class
