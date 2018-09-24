package com.sugarcubes.myglucose.actions.interfaces;

import android.content.Context;

import com.sugarcubes.myglucose.entities.ApplicationUser;

public interface ILoginAction
{
	ApplicationUser attemptLogin( String username, String password, Context context );

} // interface
