package com.sugarcubes.myglucose.actions.interfaces;

import android.content.Context;

import com.sugarcubes.myglucose.entities.ApplicationUser;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

public interface ILoginAction
{
	PatientSingleton attemptLogin( String username, String password, Context context );

} // interface
