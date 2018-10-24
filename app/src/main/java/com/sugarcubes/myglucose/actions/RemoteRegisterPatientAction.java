//--------------------------------------------------------------------------------------//
//																						//
// File Name:	RemoteRegisterPatientAction.java										//
// Programmer:	J.T. Blevins (jt.blevins@gmail.com)										//
// Date:		10/08/2018																//
// Purpose:		Allows registering a user to the remote server.							//
//																						//
//--------------------------------------------------------------------------------------//

package com.sugarcubes.myglucose.actions;

import android.content.Context;

import com.sugarcubes.myglucose.actions.interfaces.IRegisterPatientAction;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.dependencies.Dependencies;
import com.sugarcubes.myglucose.enums.ErrorCode;
import com.sugarcubes.myglucose.repositories.DbPatientRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IPatientRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;
import com.sugarcubes.myglucose.singletons.WebClientConnectionSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RemoteRegisterPatientAction implements IRegisterPatientAction
{
	// TODO: Test
	@Override
	public ErrorCode registerPatient( Context context,
									final PatientSingleton patientSingleton,
									final String password ) throws JSONException
	{
		// Send the registration request to the server:
		WebClientConnectionSingleton webConnection =           	// Get the connection manager
				WebClientConnectionSingleton.getInstance( context );
		HashMap<String, String> values = new HashMap<>();
		values.put( "Email", patientSingleton.getEmail() );
		values.put( "Password", password );
		String jsonString = webConnection.sendRegisterRequest( values );
		if( jsonString.isEmpty() )
			return ErrorCode.UNKNOWN;

		JSONObject jsonObject = new JSONObject( jsonString );   // Can throw exception

		ErrorCode errorCode = ErrorCode.interpretErrorCode( jsonObject );
		if( errorCode != ErrorCode.NO_ERROR )
			return errorCode;

		// Set the id/login info to the information returned:
		patientSingleton.setLoginToken( jsonObject.getString( DB.KEY_USER_LOGIN_TOKEN ) );
		patientSingleton.setId( jsonObject.getString( DB.KEY_REMOTE_ID ) );
		patientSingleton.setLoggedIn( true );

		// The patient hasn't been created in db yet, so add to the database:
		IPatientRepository patientRepository = Dependencies.get( IPatientRepository.class );
		patientRepository.create( patientSingleton );

		return ErrorCode.NO_ERROR;

	} // registerPatient

} // class
