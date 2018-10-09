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
import com.sugarcubes.myglucose.singletons.PatientSingleton;

public class RemoteRegisterPatientAction implements IRegisterPatientAction
{
	@Override
	public boolean registerPatient( Context context, PatientSingleton patientSingleton )
	{
		// TODO: Send the registration request to the server:

		// TODO: Add the patient info to the database:

		// TODO: Set the id/login info to the information returned:

		return false;

	} // registerPatient

} // class
