package com.sugarcubes.myglucose.actions.interfaces;

import android.content.Context;

import com.sugarcubes.myglucose.entities.ApplicationUser;

public interface ILoginAction
{
	boolean attemptLogin( String username, String password, Context context );

} // interface
