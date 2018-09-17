package com.sugarcubes.myglucose.repositories.interfaces;

import android.database.Cursor;

import com.sugarcubes.myglucose.singletons.PatientSingleton;

public interface IPatientRepository extends IApplicationUserRepository<PatientSingleton>
{
	PatientSingleton getLoggedInUser();

	Cursor getCursorForLoggedInUser();

} // interface
