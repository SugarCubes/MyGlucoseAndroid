package com.sugarcubes.myglucose.actions.interfaces;

import android.content.Context;

import com.sugarcubes.myglucose.singletons.PatientSingleton;

public interface IRegisterPatientAction
{
	boolean registerPatient( Context context, PatientSingleton patientSingleton  );
}
