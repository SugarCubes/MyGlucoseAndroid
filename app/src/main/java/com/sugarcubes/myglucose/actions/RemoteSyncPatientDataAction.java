package com.sugarcubes.myglucose.actions;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.sugarcubes.myglucose.actions.interfaces.ISyncPatientDataAction;
import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.dependencies.Dependencies;
import com.sugarcubes.myglucose.enums.ErrorCode;
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

			JSONObject jsonPatient = PatientSingleton.toJSONObject();
			//			HashMap<String, String> hashMap = JsonUtilities.toMap( jsonPatient );
			//			if( DEBUG ) Log.e( "RemoteSyncPatientData", "Patient: " + patient.toString() );
			//						if( DEBUG ) Log.e( "RemoteSyncPatientData", "Json: " + jsonPatient.toString() );
			//			if( DEBUG ) Log.e( "RemoteSyncPatientData", "HashMap: " + hashMap.toString() );

			if( conn.networkIsAvailable() )
			{
				returnString = conn.sendSyncPatientDataRequest( jsonPatient );

				if( ErrorCode.interpretErrorCode( returnString ) == ErrorCode.INVALID_LOGIN_TOKEN )
					PatientSingleton.eraseData();    // Log out

			} // if

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
