package com.sugarcubes.myglucose.actions;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

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
import java.util.HashMap;
import java.util.List;

import static com.sugarcubes.myglucose.activities.MainActivity.DEBUG;

public class RemoteRetrieveDoctorsAction implements IRetrieveDoctorsAction
{
	private final String LOG_TAG = getClass().getSimpleName();

	@Override
	public List<Doctor> retrieveDoctors( Context context ) throws JSONException
	{
		ArrayList<Doctor> doctors = new ArrayList<>();
		WebClientConnectionSingleton webConnection =        // Get the connection manager
				WebClientConnectionSingleton.getInstance( context );
		IDoctorRepository doctorRepository =                // Get the doctor repository
				Dependencies.get( IDoctorRepository.class );

		String jsonString = webConnection.sendRetrieveDoctorsRequest( null );

		if( DEBUG ) Log.e( LOG_TAG, "Response: " + jsonString );

		if( !jsonString.isEmpty() )
		{
			JSONObject jsonObject = new JSONObject( jsonString );
			JSONArray jsonArray = jsonObject.getJSONArray( "doctors" );
			int arrayLength = jsonArray.length();

			if( arrayLength > 0 )
			{
				for( int i = 0; i < arrayLength; i++ )
				{
					JSONObject drObj = (JSONObject) jsonArray.get( i ); // Get the Json object

					Doctor newDoctor = Doctor.fromJSONObject( drObj );
					doctors.add( newDoctor );      // Add the doctor to the ArrayList


					if( !doctorRepository.doctorExists( newDoctor.getUserName() ) )
						doctorRepository.create( newDoctor );

				} // for

			} // if

		} // if

		return doctors;                                         // Return the doctors ArrayList

	} // retrieveDoctors

} // class
