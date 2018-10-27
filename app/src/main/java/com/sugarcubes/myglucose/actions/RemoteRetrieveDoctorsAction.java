package com.sugarcubes.myglucose.actions;

import android.content.Context;

import com.sugarcubes.myglucose.actions.interfaces.IRetrieveDoctorsAction;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.dependencies.Dependencies;
import com.sugarcubes.myglucose.entities.Doctor;
import com.sugarcubes.myglucose.repositories.interfaces.IDoctorRepository;
import com.sugarcubes.myglucose.singletons.WebClientConnectionSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RemoteRetrieveDoctorsAction implements IRetrieveDoctorsAction
{
	@Override
	public List<Doctor> retrieveDoctors( Context context ) throws JSONException
	{
		ArrayList<Doctor> doctors = new ArrayList<>();
		WebClientConnectionSingleton webConnection =        // Get the connection manager
				WebClientConnectionSingleton.getInstance( context );
		IDoctorRepository doctorRepository =                // Get the doctor repository
				Dependencies.get( IDoctorRepository.class );

		String jsonString = webConnection.sendRetrieveDoctorsRequest( null );
		JSONObject jsonObject = new JSONObject( jsonString );
		JSONArray jsonArray = jsonObject.getJSONArray( "doctors" );
		int arrayLength = jsonArray.length();

		if( arrayLength > 0 )
		{
			for( int i = 0; i < arrayLength; i++ )
			{
				Doctor doctor = new Doctor();                       // Create a new doctor object
				JSONObject drObj = (JSONObject) jsonArray.get( i ); // Get the Json object

				// Set all of the doctors' attributes from the json objects returned:
				doctor.setDegreeAbbreviation( drObj.getString( DB.KEY_DR_DEGREE_ABBREVIATION ) );
				doctor.setUserName( drObj.getString( DB.KEY_USERNAME ) );
				doctor.setFirstName( drObj.getString( DB.KEY_USER_FIRST_NAME ) );
				doctor.setLastName( drObj.getString( DB.KEY_USER_LAST_NAME ) );
				doctor.setEmail( drObj.getString( DB.KEY_USER_EMAIL ) );
				doctor.setAddress1( drObj.getString( DB.KEY_USER_ADDRESS1 ) );
				doctor.setAddress2( drObj.getString( DB.KEY_USER_ADDRESS2 ) );
				doctor.setCity( drObj.getString( DB.KEY_USER_CITY ) );
				doctor.setState( drObj.getString( DB.KEY_USER_STATE ) );
				doctor.setZip1( drObj.getInt( DB.KEY_USER_ZIP1 ) );
				doctor.setZip2( drObj.getInt( DB.KEY_USER_ZIP2 ) );
				doctor.setCreatedAt( new Date( drObj.getString( DB.KEY_CREATED_AT ) ) );
				doctor.setHeight( drObj.getString( DB.KEY_USER_HEIGHT ) );
				doctor.setWeight( drObj.getString( DB.KEY_USER_WEIGHT ) );
				doctor.setId( drObj.getString( DB.KEY_REMOTE_ID ) );
				doctor.setLoggedIn( false );
				doctor.setPhoneNumber( drObj.getString( DB.KEY_USER_PHONE ) );
				doctor.setTimestamp( drObj.getLong( DB.KEY_TIMESTAMP ) );

				doctors.add( doctor );                          // Add the doctor to the ArrayList

			} // for

		} // if

		return doctors;                                         // Return the doctors ArrayList

	} // retrieveDoctors

} // class
