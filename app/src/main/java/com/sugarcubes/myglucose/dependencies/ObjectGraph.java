package com.sugarcubes.myglucose.dependencies;

import android.app.Application;
import android.content.Context;

import com.sugarcubes.myglucose.actions.DbLogMealEntryAction;
import com.sugarcubes.myglucose.actions.RemoteLoginAction;
import com.sugarcubes.myglucose.actions.SimulateLogMealEntryAction;
import com.sugarcubes.myglucose.actions.SimulateRegisterPatientAction;
import com.sugarcubes.myglucose.actions.SimulateRetrieveDoctorsAction;
import com.sugarcubes.myglucose.actions.interfaces.ILogMealEntryAction;
import com.sugarcubes.myglucose.actions.interfaces.ILoginAction;
import com.sugarcubes.myglucose.actions.interfaces.IRegisterPatientAction;
import com.sugarcubes.myglucose.actions.interfaces.IRetrieveDoctorsAction;

import java.util.HashMap;
import java.util.Map;

// Adapted from:
// https://softwareengineering.stackexchange.com/questions/354465/pure-dependency-injection-how-to-implement-it
class ObjectGraph
{
	private final Map<Class<?>, Object> dependencies = new HashMap<>();	// Holds all dependencies

	ObjectGraph( Context context )	// package-private
	{
		// Step 1.  create dependency graph
		ILogMealEntryAction logMealEntryAction = new DbLogMealEntryAction();//SimulateLogMealEntryAction();//
		ILoginAction remoteLoginAction = new RemoteLoginAction();
		IRegisterPatientAction registerPatientAction = new SimulateRegisterPatientAction();
		IRetrieveDoctorsAction retrieveDoctorsAction = new SimulateRetrieveDoctorsAction();

		// Step 2. add models to a dependencies map if you will need them later
		dependencies.put( ILogMealEntryAction.class, logMealEntryAction );
		dependencies.put( ILoginAction.class, remoteLoginAction );
		dependencies.put( IRegisterPatientAction.class, registerPatientAction );
		dependencies.put( IRetrieveDoctorsAction.class, retrieveDoctorsAction );

	} // constructor


	<T> T get( Class<T> model )
	{
//		Affirm.notNull(model);
//		T t = model.cast( dependencies.get( model ) );
//		Affirm.notNull(t);
		return model.cast( dependencies.get( model ) );

	} // get


	<T> void putMock( Class<T> clazz, T object )
	{
//		Affirm.notNull(clazz);
//		Affirm.notNull(object);
		dependencies.put( clazz, object );

	} // putMock

} // class
