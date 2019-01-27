package com.sugarcubes.myglucose.actions;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.sugarcubes.myglucose.actions.interfaces.ISyncPatientDataAction;
import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.dependencies.Dependencies;
import com.sugarcubes.myglucose.enums.ErrorCode;
import com.sugarcubes.myglucose.enums.SyncStatus;
import com.sugarcubes.myglucose.repositories.interfaces.IPatientRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;
import com.sugarcubes.myglucose.singletons.WebClientConnectionSingleton;
import com.sugarcubes.myglucose.utils.JsonUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import static com.sugarcubes.myglucose.activities.MainActivity.DEBUG;

public class RemoteSyncPatientDataAction implements ISyncPatientDataAction
{
	private final String LOG_TAG = getClass().getSimpleName();

	@Override
	public String syncPatientData( Context context )
	{
		WebClientConnectionSingleton conn = WebClientConnectionSingleton.getInstance( context );
		IPatientRepository patientRepository = Dependencies.get( IPatientRepository.class );
		//		ContentValues values = patientRepository.putContentValues( PatientSingleton.getInstance() );
		String returnString = "";

		try
		{
			// ALWAYS load the latest data:
			Cursor cursor = context.getContentResolver().query(
					MyGlucoseContentProvider.PATIENT_USERS_URI, null,
					DB.KEY_USER_LOGGED_IN + "=?",
					new String[]{ String.valueOf( 1 ) }, null
			);
			patientRepository.readFromCursor( PatientSingleton.getInstance(), cursor );
			//			if( cursor != null )
			//				cursor.close();

			JSONObject jsonPatient = PatientSingleton.toJSONObject( SyncStatus.UNSYNCED );
			//HashMap<String, String> hashMap = JsonUtilities.toMap( jsonPatient );	// OLD method
			if( DEBUG ) Log.e( LOG_TAG, "Json: " + jsonPatient.toString() );

			if( conn.networkIsAvailable() )
			{
				// Sync all of the unsynced data:
				returnString = conn.sendSyncPatientDataRequest( jsonPatient );
				if( DEBUG ) Log.e( LOG_TAG, "Server returned: " + returnString );

				ErrorCode returnCode = ErrorCode.interpretErrorCode( returnString );
				if( returnCode == ErrorCode.INVALID_LOGIN_TOKEN )
					PatientSingleton.eraseData();    	// Log out
				else if( returnCode == ErrorCode.NO_ERROR )
					patientRepository.setAllSynced();	// Set all unsynced to now synced

			} // if
			else
				Log.e( LOG_TAG, "NETWORK UNAVAILABLE..." );

		}
		catch( JSONException e )
		{
			Log.e( LOG_TAG, "Error interpreting json." );
			e.printStackTrace();
		}
		catch( Exception e )
		{
			Log.e( LOG_TAG, "Error executing request." );
			e.printStackTrace();
		}

		if( DEBUG ) Log.e( "RemoteSyncPatientData", "Response String: " + returnString );

		return returnString;

	} // syncPatientData

} // class
