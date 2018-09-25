package com.sugarcubes.myglucose.actions;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sugarcubes.myglucose.actions.interfaces.ILoginAction;
import com.sugarcubes.myglucose.activities.SettingsActivity;
import com.sugarcubes.myglucose.entities.ApplicationUser;
import com.sugarcubes.myglucose.singletons.PatientSingleton;
import com.sugarcubes.myglucose.urlconnections.UrlConnection;

import java.net.URL;
import java.util.HashMap;

import static com.sugarcubes.myglucose.activities.MainActivity.DEBUG;

public class RemoteLoginAction implements ILoginAction
{
	private final String LOG_TAG = getClass().getSimpleName();

	@Override
	public ApplicationUser attemptLogin( String username, String password, Context context )
	{
		try
		{
			PatientSingleton patientSingleton = PatientSingleton.getInstance();

			HashMap<String, String> values = new HashMap<>();    // Create post values
			values.put( "Email", username );                        // Add to http request
			values.put( "Password", password );
//				values.put( "RememberMe", true );

			if( DEBUG ) Log.i( LOG_TAG, "Email: " + username + "; Password: " + password.substring( 0, 2 ) + "..." );

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( context );
			String host = prefs.getString( SettingsActivity.PREF_HOSTNAME, "" );
			int port = Integer.parseInt( prefs.getString( SettingsActivity.PREF_PORT, "" ) );
			String url = "http://" + host + ":" + port + "/Account/LoginRemote";

			UrlConnection urlConnection = new UrlConnection( new URL( url ) );
			String response = urlConnection.performRequest( values );
			if( DEBUG ) Log.e( LOG_TAG, "Json returned: " + response );

			return patientSingleton;

		} catch( Exception e )
		{
			e.printStackTrace();
			return null;

		} // try/catch

	} // attemptLogin

} // class
