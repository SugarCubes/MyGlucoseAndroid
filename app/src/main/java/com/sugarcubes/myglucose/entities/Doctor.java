package com.sugarcubes.myglucose.entities;

import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.utils.JsonUtilities;

import org.json.JSONException;
import org.json.JSONObject;

public class Doctor extends ApplicationUser
{
	protected String degreeAbbreviation;

	public Doctor()
	{
		degreeAbbreviation = "MD";                // Default abbreviation

	} // constructor

	public String getDegreeAbbreviation()
	{
		return degreeAbbreviation;
	}

	public void setDegreeAbbreviation( String degreeAbbreviation )
	{
		this.degreeAbbreviation = degreeAbbreviation;
	}


	public static Doctor fromJSONObject( JSONObject drObj ) throws JSONException
	{
		Doctor doctor =
				new Doctor();                       // Create a new doctor object

		if( drObj != null )
		{
			// Set all of the doctors' attributes from the json objects returned:
			if( drObj.has( DB.KEY_DR_DEGREE_ABBREVIATION ) )
				doctor.setDegreeAbbreviation( drObj.getString( DB.KEY_DR_DEGREE_ABBREVIATION ) );
			if( drObj.has( DB.KEY_USERNAME ) )
				doctor.setUserName( drObj.getString( DB.KEY_USERNAME ) );
			if( drObj.has( DB.KEY_USER_FIRST_NAME ) )
				doctor.setFirstName( drObj.getString( DB.KEY_USER_FIRST_NAME ) );
			if( drObj.has( DB.KEY_USER_LAST_NAME ) )
				doctor.setLastName( drObj.getString( DB.KEY_USER_LAST_NAME ) );
			if( drObj.has( DB.KEY_USER_EMAIL ) )
				doctor.setEmail( drObj.getString( DB.KEY_USER_EMAIL ) );
			if( drObj.has( DB.KEY_USER_ADDRESS1 ) )
				doctor.setAddress1( drObj.getString( DB.KEY_USER_ADDRESS1 ) );
			if( drObj.has( DB.KEY_USER_ADDRESS2 ) )
				doctor.setAddress2( drObj.getString( DB.KEY_USER_ADDRESS2 ) );
			if( drObj.has( DB.KEY_USER_CITY ) )
				doctor.setCity( drObj.getString( DB.KEY_USER_CITY ) );
			if( drObj.has( DB.KEY_USER_STATE ) )
				doctor.setState( drObj.getString( DB.KEY_USER_STATE ) );
			if( drObj.has( DB.KEY_USER_ZIP1 ) )
				doctor.setZip1( drObj.getInt( DB.KEY_USER_ZIP1 ) );
			if( drObj.has( DB.KEY_USER_ZIP2 ) )
				doctor.setZip2( drObj.getInt( DB.KEY_USER_ZIP2 ) );
			if( drObj.has( DB.KEY_CREATED_AT ) )
				doctor.setCreatedAt( JsonUtilities.dateFromJsonString(
						drObj.getString( DB.KEY_CREATED_AT ) ) );
			if( drObj.has( DB.KEY_UPDATED_AT ) )
				doctor.setCreatedAt( JsonUtilities.dateFromJsonString(
						drObj.getString( DB.KEY_UPDATED_AT ) ) );
			if( drObj.has( DB.KEY_USER_HEIGHT ) )
				doctor.setHeight( drObj.getString( DB.KEY_USER_HEIGHT ) );
			if( drObj.has( DB.KEY_USER_WEIGHT ) )
				doctor.setWeight( drObj.getString( DB.KEY_USER_WEIGHT ) );
			if( drObj.has( DB.KEY_REMOTE_ID ) )
				doctor.setId( drObj.getString( DB.KEY_REMOTE_ID ) );
			if( drObj.has( DB.KEY_USER_PHONE ) )
				doctor.setPhoneNumber( drObj.getString( DB.KEY_USER_PHONE ) );
			if( drObj.has( DB.KEY_TIMESTAMP ) )
				doctor.setTimestamp( drObj.getLong( DB.KEY_TIMESTAMP ) );
			doctor.setLoggedIn( false );

		} // if

		return doctor;

	} // fromJSONObject


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
