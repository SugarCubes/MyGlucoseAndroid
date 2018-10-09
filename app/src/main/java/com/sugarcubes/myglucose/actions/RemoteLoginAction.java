package com.sugarcubes.myglucose.actions;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sugarcubes.myglucose.actions.interfaces.ILoginAction;
import com.sugarcubes.myglucose.activities.SettingsActivity;
import com.sugarcubes.myglucose.enums.ErrorCode;
import com.sugarcubes.myglucose.repositories.DbPatientRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;
import com.sugarcubes.myglucose.singletons.WebClientConnectionSingleton;
import com.sugarcubes.myglucose.urlconnections.UrlConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static com.sugarcubes.myglucose.activities.MainActivity.DEBUG;

public class RemoteLoginAction implements ILoginAction
{
	private final String LOG_TAG    = getClass().getSimpleName();

	@Override
	public ErrorCode attemptLogin( String username, String password, Context context ) throws
			JSONException
	{
		PatientSingleton patientSingleton = PatientSingleton.getInstance();

		HashMap<String, String> values = new HashMap<>();   // Create post values
		values.put( "Email", username );                    // Add to http request
		values.put( "Password", password );
		//			values.put( "RememberMe", true );

		if( DEBUG ) Log.i( LOG_TAG,
				"Email: " + username + "; Password: " + password.substring( 0, 2 ) + "..." );

		final WebClientConnectionSingleton connection =
				WebClientConnectionSingleton.getInstance( context );
		final String jsonResponse = connection.sendLoginRequest( values );

		if( DEBUG ) Log.e( LOG_TAG, "Login returned: " + jsonResponse );

		JSONObject jsonObject = new JSONObject( jsonResponse );

		ErrorCode errorCode = ErrorCode.interpretErrorCode( jsonObject );
		if( errorCode != ErrorCode.NO_ERROR ) return errorCode;

		// Login was successful, so enter into the db:
		DbPatientRepository patientRepository = new DbPatientRepository( context );
		patientSingleton.setLoggedIn( true );
		PatientSingleton.copyFrom( jsonResponse );          // Set patient's values from server
		patientRepository.create( patientSingleton );       // ...and insert into db

		return ErrorCode.NO_ERROR;

	} // attemptLogin

} // class
