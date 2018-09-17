package com.sugarcubes.myglucose.actions;

import com.sugarcubes.myglucose.actions.interfaces.IRegisterPatientAction;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

public class RegisterPatientSimulationAction implements IRegisterPatientAction
{
	@Override
	public boolean registerPatient( PatientSingleton patient )
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
