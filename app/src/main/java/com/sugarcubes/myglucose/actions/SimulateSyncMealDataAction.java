package com.sugarcubes.myglucose.actions;

import android.util.Log;

import com.sugarcubes.myglucose.actions.interfaces.ISyncMealDataAction;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.enums.ErrorCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.sugarcubes.myglucose.activities.MainActivity.DEBUG;

public class SimulateSyncMealDataAction implements ISyncMealDataAction
{
	private String LOG_TAG = getClass().getSimpleName();

	@Override
	public String syncMealData()
	{
		Log.e( LOG_TAG, "Simulating syncing meal data..." );
		JSONObject mealEntryObject = new JSONObject();
		JSONArray mealItemArray = new JSONArray();

		try
		{
			mealEntryObject.put( "success", true );

			JSONObject mealItemObject1 = new JSONObject();	// Each mealItem is an object
			// Fill out the object's attributes:
			mealItemObject1.put( DB.KEY_REMOTE_ID, "some-uuid-in-the-database" );
			mealItemObject1.put( ErrorCode.ERROR_CODE, ErrorCode.NO_ERROR.getValue() );

			mealItemArray.put( mealItemObject1 );				// Put the object in the array

			// Add the array to the main object to be returned:
			mealEntryObject.put( "mealItems", mealItemArray );

		}
		catch( JSONException e )
		{
			e.printStackTrace();
		}

		if( DEBUG ) Log.e( LOG_TAG, "MealEntry json (SIMULATED): " + mealEntryObject.toString() );
		return mealEntryObject.toString();

	} // syncMealData

} // class
