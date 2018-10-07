//--------------------------------------------------------------------------------------//
//																						//
// File Name:	UrlConnectionManager.java												//
// Programmer:	J.T. Blevins (jt.blevins@gmail.com)										//
// Date:		09/23/2018																//
// Purpose:		A class to allow connections to a specified url (using Android's		//
//				Volley class).
//																						//
//--------------------------------------------------------------------------------------//

package com.sugarcubes.myglucose.urlmanagers;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sugarcubes.myglucose.activities.SettingsActivity;
import com.sugarcubes.myglucose.utils.JsonUtilities;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UrlConnectionManager
{
	private ContentResolver contentResolver;
	private String LOG_TAG = getClass().getSimpleName();
	private RequestQueue queue;
	private Context context;
	private JSONObject jsonObject;
	private String baseUrl;
	private String host;
	private int port;


	public UrlConnectionManager( Context context )
	{
		this.context = context;

		queue = Volley.newRequestQueue( context );	// Convenience method for creating queue

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( context );
		host = prefs.getString( SettingsActivity.PREF_HOSTNAME, "" );
		port = Integer.parseInt( prefs.getString( SettingsActivity.PREF_PORT, "" ) );
		baseUrl = "http://" + host + ":" + port;

	} // constructor


	/**
	 * sendRequest
	 * @param urlPath - relative path on the server
	 * @param params - HashMap of values
	 * @param requestMethod - [optional] defaults to Request.Method.POST
	 * @return Json Object
	 */
	public JSONObject sendRequest( String urlPath, Map<?, ?> params, int... requestMethod )
	{
		try
		{
			JSONObject jsonObj = JsonUtilities.mapToJson( params );
			Log.e( LOG_TAG, "Params: " + jsonObj + "; urlPath: " + urlPath );
			// Request a string response from the provided URL.
			JsonObjectRequest jsonRequest = new JsonObjectRequest(
					requestMethod[0] > -1 ? requestMethod[0] : Request.Method.POST, // Request.Method.POST or GET
					baseUrl + urlPath,
					jsonObj,				// Convert the supplied params to Json
					new Response.Listener<JSONObject>()
					{
						@Override
						public void onResponse( JSONObject response )
						{
							jsonObject = response;			// Set the field to the response

							// Display the first 500 characters of the response string.
							Log.i( LOG_TAG, "Response is: " + response.toString() );
						}
					},
					new Response.ErrorListener()
					{
						@Override
						public void onErrorResponse( VolleyError error )
						{
							jsonObject = new JSONObject();

							Log.i( LOG_TAG, "Request failed: \n" + error.getMessage() );
						}
					} );

			// Add the request to the RequestQueue.
			queue.add( jsonRequest );

			return jsonObject;
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return null;
		}

	} // sendRequest


	public JSONObject addRequestToQueueAsync( String url, HashMap<String, String> params )
	{
		try
		{
			new JsonRequest( params ).execute( url ).wait();

			return jsonObject;
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return null;
		}

	} // sendRequest


	/**
	 * JsonRequest class
	 * An inner class to allow a request to be queued asynchronously, apart from
	 * the main thread.
	 */
	private class JsonRequest extends AsyncTask<String, Void, Void>
	{
		private ProgressDialog pDialog;
		private HashMap<String, String> params;

		public JsonRequest( HashMap<String, String> params )
		{
			this.params = params;
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
//			pDialog = new ProgressDialog( context );
//			pDialog.setMessage( "Loading..." );
//			pDialog.show();
		}

		@Override
		protected Void doInBackground( String... urls )
		{

			// Request a string response from the provided URL.
			JsonObjectRequest jsonRequest = new JsonObjectRequest(
					Request.Method.POST,
					urls[ 0 ],
					new JSONObject( params ),				// Convert the supplied params to Json
					new Response.Listener<JSONObject>()
					{
						@Override
						public void onResponse( JSONObject response )
						{
							jsonObject = response;			// Set the field to the response

							// Display the first 500 characters of the response string.
							Log.i( LOG_TAG, "Response is: " + response.toString() );
						}
					},
					new Response.ErrorListener()
					{
						@Override
						public void onErrorResponse( VolleyError error )
						{
							jsonObject = null;

							Log.i( LOG_TAG, "Request failed: \n" + error.getMessage() );
						}
					} );

			// Add the request to the RequestQueue.
			queue.add( jsonRequest );

			return null;

		} // doInBackground


		@Override
		protected void onPostExecute( Void aVoid )
		{
			super.onPostExecute( aVoid );
//			if( pDialog.isShowing() )
//				pDialog.hide();

		} // onPostExecute

	} // StringRequest inner class


} // class
