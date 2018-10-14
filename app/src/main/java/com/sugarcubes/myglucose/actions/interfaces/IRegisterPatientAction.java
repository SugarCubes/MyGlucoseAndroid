package com.sugarcubes.myglucose.actions.interfaces;

import android.content.Context;

import com.sugarcubes.myglucose.enums.ErrorCode;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

import org.json.JSONException;

public interface IRegisterPatientAction
{
	ErrorCode registerPatient( Context context, PatientSingleton patientSingleton, String password ) throws JSONException;
}
