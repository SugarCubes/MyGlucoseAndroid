package com.sugarcubes.myglucose.actions;

import android.util.Log;

import com.sugarcubes.myglucose.actions.interfaces.ISyncGlucoseDataAction;

import org.json.JSONException;
import org.json.JSONObject;

public class SimulateSyncGlucoseDataAction implements ISyncGlucoseDataAction
{
	private String LOG_TAG = getClass().getSimpleName();

	@Override
	public String syncGlucoseData()
	{
		Log.e( LOG_TAG, "Simulating syncing glucose data..." );
		JSONObject glucoseEntryObject = new JSONObject();

		try
		{
			glucoseEntryObject.put( "success", true );

		}
		catch( JSONException e )
		{
			e.printStackTrace();
		}

		return glucoseEntryObject.toString();

	} // syncGlucoseData

} // class
