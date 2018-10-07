package com.sugarcubes.myglucose.actions;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sugarcubes.myglucose.actions.interfaces.ILoginAction;
import com.sugarcubes.myglucose.activities.SettingsActivity;
import com.sugarcubes.myglucose.enums.ErrorCode;
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
	private final String LOG_TAG	= getClass().getSimpleName();
	private final String EMAIL		= "Email";
	private final String PASSWORD	= "Password";
	private final String SUCCESS	= "success";
	private final String ERROR_CODE	= "errorCode";

	@Override
	public ErrorCode attemptLogin( String username, String password, Context context ) throws
			MalformedURLException, JSONException
	{
		PatientSingleton patientSingleton = PatientSingleton.getInstance();

		HashMap<String, String> values = new HashMap<>();    // Create post values
		values.put( "Email", username );                   // Add to http request
		values.put( "Password", password );
//			values.put( "RememberMe", true );

		if( DEBUG ) Log.i( LOG_TAG,
				"Email: " + username + "; Password: " + password.substring( 0, 2 ) + "..." );

		final String jsonResponse = getHttpJsonResponse( context, values );

		if( DEBUG ) Log.e( LOG_TAG, "Json returned: " + jsonResponse );

		JSONObject jsonObject = new JSONObject( jsonResponse );

		if( !isSuccess( jsonObject ) || jsonResponse.isEmpty() )
		{
			int error = getErrorCode( jsonObject );
			ErrorCode errorCode = ErrorCode.fromInt( error );
			if( errorCode != null )
				switch( errorCode )
				{
					case NO_ERROR:
						return ErrorCode.NO_ERROR;
					case UNKNOWN:
						return ErrorCode.UNKNOWN;
					case INVALID_URL:
						return ErrorCode.INVALID_URL;
					case INVALID_EMAIL_PASSWORD:
						return ErrorCode.INVALID_EMAIL_PASSWORD;
					default:
						return ErrorCode.UNKNOWN;

				} // switch
			else
				return ErrorCode.UNKNOWN;

		} // if !success

		// Login was successful, so enter into the db:
		DbPatientRepository patientRepository = new DbPatientRepository( context );
		patientSingleton.setLoggedIn( true );
		PatientSingleton.copyFrom( jsonResponse );			// Set patient's values from server
		patientRepository.create( patientSingleton );		// ...and insert into db

		return ErrorCode.NO_ERROR;

	} // attemptLogin


	private String getHttpJsonResponse( Context context, HashMap<String, String> values ) throws MalformedURLException
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( context );
		String host = prefs.getString( SettingsActivity.PREF_HOSTNAME, "localhost" );
		int port = Integer.parseInt( prefs.getString( SettingsActivity.PREF_PORT, "8080" ) );
		String url = "http://" + host + ":" + port + "/Account/LoginRemote";
		if( DEBUG ) Log.e( LOG_TAG, "URL: " + url );

		UrlConnection urlConnection = new UrlConnection( new URL( url ) );
		return urlConnection.performRequest( values );

	} // getHttpJsonResponse


	private boolean isSuccess( JSONObject jsonObject ) throws JSONException
	{
		return jsonObject.getBoolean( SUCCESS );

	} // isSuccess


	private int getErrorCode( JSONObject jsonObject ) throws JSONException
	{
		return jsonObject.getInt( ERROR_CODE );

	} // isSuccess

} // class
