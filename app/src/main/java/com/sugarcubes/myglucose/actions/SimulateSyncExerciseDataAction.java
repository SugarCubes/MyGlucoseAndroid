package com.sugarcubes.myglucose.actions;

import android.util.Log;

import com.sugarcubes.myglucose.actions.interfaces.ISyncExerciseDataAction;

import org.json.JSONException;
import org.json.JSONObject;

public class SimulateSyncExerciseDataAction implements ISyncExerciseDataAction
{
	private String LOG_TAG = getClass().getSimpleName();

	@Override
	public String syncExerciseData()
	{
		Log.e( LOG_TAG, "Simulating syncing exercise data..." );
		JSONObject exerciseEntryObject = new JSONObject();

		try
		{
			exerciseEntryObject.put( "success", true );

		}
		catch( JSONException e )
		{
			e.printStackTrace();
		}

		return exerciseEntryObject.toString();

	} // syncExerciseData

} // class
