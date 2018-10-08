package com.sugarcubes.myglucose.actions.interfaces;

import android.content.Context;

import com.sugarcubes.myglucose.entities.GlucoseEntry;
import com.sugarcubes.myglucose.enums.ErrorCode;
import com.sugarcubes.myglucose.repositories.DbGlucoseEntryRepository;
import com.sugarcubes.myglucose.repositories.DbPatientRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

public class DbLogGlucoseEntryAction implements ILogGlucoseEntryAction
{
	@Override
	public ErrorCode logGlucoseEntry( Context context, GlucoseEntry glucoseEntry ) throws InterruptedException
	{
		DbPatientRepository dbPatientRepository = new DbPatientRepository( context );
		DbGlucoseEntryRepository dbGlucoseEntryRepository = new DbGlucoseEntryRepository( context );
		PatientSingleton patientSingleton = PatientSingleton.getInstance();
		patientSingleton.glucoseEntries.add( glucoseEntry );
		dbPatientRepository.update( patientSingleton.getUserName(), patientSingleton );
		dbGlucoseEntryRepository.create( glucoseEntry );

		return ErrorCode.NO_ERROR;

	} // logGlucoseEntry

} // class
