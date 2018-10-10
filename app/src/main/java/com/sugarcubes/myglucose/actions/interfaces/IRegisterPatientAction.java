package com.sugarcubes.myglucose.actions.interfaces;

import android.content.Context;

import com.sugarcubes.myglucose.singletons.PatientSingleton;

import org.json.JSONException;

public interface IRegisterPatientAction
{
	boolean registerPatient( Context context, PatientSingleton patientSingleton, String password ) throws JSONException;
}
