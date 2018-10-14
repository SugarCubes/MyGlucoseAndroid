package com.sugarcubes.myglucose.actions;

import android.content.Context;

import com.sugarcubes.myglucose.actions.interfaces.IRegisterPatientAction;
import com.sugarcubes.myglucose.enums.ErrorCode;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

public class SimulateRegisterPatientAction implements IRegisterPatientAction
{
	@Override
	public ErrorCode registerPatient( Context context, PatientSingleton patientSingleton, String password )
	{
		try
		{
			Thread.sleep(2000);
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}
		return ErrorCode.NO_ERROR;

	} // registerPatient

} // class
