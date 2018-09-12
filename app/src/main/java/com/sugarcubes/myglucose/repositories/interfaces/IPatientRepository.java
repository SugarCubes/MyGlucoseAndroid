//--------------------------------------------------------------------------------------//
//																						//
// File Name:	IApplicationUserRepository.java											//
// Programmer:	J.T. Blevins (jt.blevins@gmail.com)										//
// Date:		09/08/2018																//
// Purpose:		An interface to enforce the methods required to access the 				//
// 				PatientSingleton data in the database. This does not extend the			//
// 				IRepository interface so that there is no way for unauthorized users 	//
// 				to modify data.															//
//																						//
//--------------------------------------------------------------------------------------//

package com.sugarcubes.myglucose.repositories.interfaces;

import android.content.Context;
import android.database.Cursor;

import com.sugarcubes.myglucose.singletons.PatientSingleton;

public interface IPatientRepository
{
	// Add new Patient-specific methods here
	void logIn( PatientSingleton patientSingleton, Cursor cursor );
	boolean logOut( PatientSingleton patientSingleton );
	boolean createLogin( PatientSingleton patientSingleton );
} // interface
