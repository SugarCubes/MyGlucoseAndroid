package com.sugarcubes.myglucose.actions;

import android.content.Context;

import com.sugarcubes.myglucose.actions.interfaces.IRegisterPatientAction;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

public class SimulateRegisterPatientAction implements IRegisterPatientAction
{
	@Override
	public boolean registerPatient( Context context, PatientSingleton patientSingleton  )
	{
		try
		{
			Thread.sleep(2000);
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}
		return false;

	} // registerPatient

} // class
