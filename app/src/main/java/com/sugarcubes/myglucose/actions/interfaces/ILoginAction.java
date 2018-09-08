package com.sugarcubes.myglucose.actions.interfaces;

import com.sugarcubes.myglucose.entities.ApplicationUser;

public interface ILoginAction
{
	boolean attemptLogin( String username, String password );

} // interface
