//--------------------------------------------------------------------------------------//
//																						//
// File Name:	WebClientConnectionSingleton.java										//
// Programmer:	J.T. Blevins (jt.blevins@gmail.com)										//
// Date:		10/08/2018																//
// Purpose:		An aggregate class to hold and use UrlConnection objects.				//
//																						//
//--------------------------------------------------------------------------------------//

package com.sugarcubes.myglucose.singletons;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sugarcubes.myglucose.activities.SettingsActivity;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.urlconnections.UrlConnection;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static com.sugarcubes.myglucose.activities.MainActivity.DEBUG;

/**
 * WebClientConnectionSingleton
 * An aggregate class to hold and use UrlConnection objects and handle all requests to the
 * remote server.
 */
public class WebClientConnectionSingleton
{
	private static WebClientConnectionSingleton webClientConnection;    // The singleton to provide

	private static SharedPreferences sharedPreferences;
	private static String            host;
	private static int               port;

	private final static String LOGIN_STRING             = "/API/AccountApi/Login";
	private final static String REGISTER_STRING          = "/API/AccountApi/Register";
	private final static String SYNC_GLUCOSE_STRING      = "/API/Glucose/Sync";
	private final static String SYNC_MEAL_ENTRY_STRING   = "/API/Meal/CreateEntry";
	private final static String SYNC_MEAL_ITEM_STRING    = "/API/Meal/CreateItem";
	private final static String SYNC_EXERCISE_STRING     = "/API/Exercise/Sync";
	private final static String SYNC_PATIENT_DATA_STRING = "/API/Patient/Sync";
	private final static String RETRIEVE_DOCTORS_STRING  = "/API/Doctor/List";

	private UrlConnection loginConnection;             // The UrlConnections used to
	private UrlConnection registerConnection;          // 		connect to each URL that
	private UrlConnection syncGlucoseConnection;       //		may be used throughout the
	private UrlConnection syncMealEntryConnection;     //		application
	private UrlConnection syncMealItemConnection;
	private UrlConnection syncExerciseConnection;
	private UrlConnection retrieveDoctorsConnection;
	private UrlConnection syncPatientDataConnection;
	private String LOG_TAG = getClass().getSimpleName();

	private WebClientConnectionSingleton( Context context ) throws MalformedURLException
	{
		// We first get our user's preferences
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );
		host = sharedPreferences.getString( SettingsActivity.PREF_HOSTNAME, "localhost" );
		port = Integer.parseInt(
				sharedPreferences.getString( SettingsActivity.PREF_PORT, "8080" ) );

		// Instantiate all of the connections to the server that the app will use:
		String urlString = "http://" + host + ":" + port + LOGIN_STRING;
		loginConnection = new UrlConnection( new URL( urlString ) );

		urlString = "http://" + host + ":" + port + REGISTER_STRING;
		registerConnection = new UrlConnection( new URL( urlString ) );

		urlString = "http://" + host + ":" + port + SYNC_GLUCOSE_STRING;
		syncGlucoseConnection = new UrlConnection( new URL( urlString ) );

		urlString = "http://" + host + ":" + port + SYNC_MEAL_ENTRY_STRING;
		syncMealEntryConnection = new UrlConnection( new URL( urlString ) );

		urlString = "http://" + host + ":" + port + SYNC_MEAL_ITEM_STRING;
		syncMealItemConnection = new UrlConnection( new URL( urlString ) );

		urlString = "http://" + host + ":" + port + SYNC_EXERCISE_STRING;
		syncExerciseConnection = new UrlConnection( new URL( urlString ) );

		urlString = "http://" + host + ":" + port + RETRIEVE_DOCTORS_STRING;
		retrieveDoctorsConnection = new UrlConnection( new URL( urlString ) );

		urlString = "http://" + host + ":" + port + SYNC_PATIENT_DATA_STRING;
		syncPatientDataConnection = new UrlConnection( new URL( urlString ) );

	} // constructor


	public static WebClientConnectionSingleton getInstance( Context context )
	{
		if( sharedPreferences == null )
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );

		// Create the client *only* if it hasn't been created, the host or port has changed
		if( webClientConnection == null
				|| !host.equals( sharedPreferences.getString(
				SettingsActivity.PREF_HOSTNAME, "localhost" ) )
				|| port != Integer.parseInt(
				sharedPreferences.getString( SettingsActivity.PREF_PORT, "8080" ) ) )
		{
			try
			{
				webClientConnection = new WebClientConnectionSingleton( context );
			}
			catch( MalformedURLException e )
			{
				e.printStackTrace();
			}

		} // if

		return webClientConnection;             // Otherwise, just return the client

	} // getInstance


	public String sendLoginRequest( HashMap<String, String> values )
	{
		return loginConnection.performRequest( values );

	} // getHttpJsonResponse


	public String sendRegisterRequest( HashMap<String, String> values )
	{
		return registerConnection.performRequest( values );

	} // getHttpJsonResponse


	public String sendSyncGlucoseRequest( HashMap<String, String> values )
	{
		return syncGlucoseConnection.performRequest( values );

	} // sendSyncGlucoseRequest


	public String sendSyncMealEntryRequest( HashMap<String, String> values )
	{
		return syncMealEntryConnection.performRequest( values );

	} // sendSyncMealRequest


	public String sendSyncMealItemRequest( HashMap<String, String> values )
	{
		return syncMealItemConnection.performRequest( values );

	} // sendSyncMealRequest


	public String sendSyncExerciseRequest( HashMap<String, String> values )
	{
		return syncExerciseConnection.performRequest( values );

	} // sendSyncExerciseRequest


	public String sendSyncPatientDataRequest( HashMap<String, String> values )
	{

//		if( DEBUG ) Log.e( LOG_TAG, "Values: " + values.toString() );
		return syncPatientDataConnection.performRequest( values );

	} // sendSyncExerciseRequest


	public String sendSyncPatientDataRequest( JSONObject values )
	{

		//		if( DEBUG ) Log.e( LOG_TAG, "Values: " + values.toString() );
		return syncPatientDataConnection.performRequest( values );

	} // sendSyncExerciseRequest


	public String sendRetrieveDoctorsRequest( HashMap<String, String> values )
	{
		return retrieveDoctorsConnection.performRequest( values );

	} // sendSyncExerciseRequest

} // class
