package com.sugarcubes.myglucose.actions;

import com.sugarcubes.myglucose.actions.interfaces.ILoginAction;
import com.sugarcubes.myglucose.entities.ApplicationUser;

public class LoginSimulationAction implements ILoginAction
{
	ApplicationUser user;

	public LoginSimulationAction( ApplicationUser user )
	{
		this.user = user;

	} // constructor


	@Override
	public boolean attemptLogin( String username, String password )
	{
		try {
			Thread.sleep( 2000 );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		return true;

	} // attemptLogin

} // class
