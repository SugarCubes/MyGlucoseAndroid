package com.sugarcubes.myglucose.actions;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sugarcubes.myglucose.actions.interfaces.ILoginAction;
import com.sugarcubes.myglucose.activities.SettingsActivity;
import com.sugarcubes.myglucose.repositories.DbPatientRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;
import com.sugarcubes.myglucose.urlconnections.UrlConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static com.sugarcubes.myglucose.activities.MainActivity.DEBUG;

public class RemoteLoginAction implements ILoginAction
{
	private final String LOG_TAG = getClass().getSimpleName();

	@Override
	public PatientSingleton attemptLogin( String username, String password, Context context )
	{
		try
		{
			PatientSingleton patientSingleton = PatientSingleton.getInstance();

			HashMap<String, String> values = new HashMap<>();    // Create post values
			values.put( "Email", username );                   // Add to http request
			values.put( "Password", password );
//			values.put( "RememberMe", true );

			if( DEBUG ) Log.i( LOG_TAG,
					"Email: " + username + "; Password: " + password.substring( 0, 2 ) + "..." );

			final String jsonResponse = getHttpJsonResponse( context, values );

			if( !isSuccess( jsonResponse ) || jsonResponse.isEmpty() )
				return null;

			// Login was successful, so enter into the db:
			DbPatientRepository patientRepository = new DbPatientRepository( context );
			patientSingleton.setLoggedIn( true );
			PatientSingleton.copyFrom( jsonResponse );			// Set patient's values from server
			patientRepository.create( patientSingleton );		// ...and insert into db

			if( DEBUG ) Log.e( LOG_TAG, "Json returned: " + jsonResponse );

			return patientSingleton;

		}
		catch( Exception e )
		{
			e.printStackTrace();
			return null;

		} // try/catch

	} // attemptLogin


	private String getHttpJsonResponse( Context context, HashMap<String, String> values ) throws MalformedURLException
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( context );
		String host = prefs.getString( SettingsActivity.PREF_HOSTNAME, "" );
		int port = Integer.parseInt( prefs.getString( SettingsActivity.PREF_PORT, "" ) );
		String url = "http://" + host + ":" + port + "/Account/LoginRemote";

		UrlConnection urlConnection = new UrlConnection( new URL( url ) );
		return urlConnection.performRequest( values );

	} // getHttpJsonResponse


	private boolean isSuccess( String jsonString ) throws JSONException
	{
		JSONObject jsonObject = new JSONObject( jsonString );
		return jsonObject.getBoolean( "success" );

	} // isSuccess

} // class
