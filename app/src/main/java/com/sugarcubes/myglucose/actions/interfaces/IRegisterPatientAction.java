package com.sugarcubes.myglucose.actions.interfaces;

import com.sugarcubes.myglucose.singletons.PatientSingleton;

public interface IRegisterPatientAction
{
	boolean registerPatient( PatientSingleton patient );
}
