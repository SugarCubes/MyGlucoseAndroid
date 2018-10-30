package com.sugarcubes.myglucose.dependencies;

import android.content.Context;

import com.sugarcubes.myglucose.actions.DbLogExerciseEntryAction;
import com.sugarcubes.myglucose.actions.DbLogMealEntryAction;
import com.sugarcubes.myglucose.actions.RemoteLoginAction;
import com.sugarcubes.myglucose.actions.RemoteSyncPatientDataAction;
import com.sugarcubes.myglucose.actions.SimulateRegisterPatientAction;
import com.sugarcubes.myglucose.actions.SimulateRetrieveDoctorsAction;
import com.sugarcubes.myglucose.actions.DbLogGlucoseEntryAction;
import com.sugarcubes.myglucose.actions.SimulateSyncExerciseDataAction;
import com.sugarcubes.myglucose.actions.SimulateSyncGlucoseDataAction;
import com.sugarcubes.myglucose.actions.SimulateSyncMealDataAction;
import com.sugarcubes.myglucose.actions.SimulateSyncPatientDataAction;
import com.sugarcubes.myglucose.actions.interfaces.ILogExerciseEntryAction;
import com.sugarcubes.myglucose.actions.interfaces.ILogGlucoseEntryAction;
import com.sugarcubes.myglucose.actions.interfaces.ILogMealEntryAction;
import com.sugarcubes.myglucose.actions.interfaces.ILoginAction;
import com.sugarcubes.myglucose.actions.interfaces.IRegisterPatientAction;
import com.sugarcubes.myglucose.actions.interfaces.IRetrieveDoctorsAction;
import com.sugarcubes.myglucose.actions.interfaces.ISyncExerciseDataAction;
import com.sugarcubes.myglucose.actions.interfaces.ISyncGlucoseDataAction;
import com.sugarcubes.myglucose.actions.interfaces.ISyncMealDataAction;
import com.sugarcubes.myglucose.actions.interfaces.ISyncPatientDataAction;
import com.sugarcubes.myglucose.repositories.DbApplicationUserRepository;
import com.sugarcubes.myglucose.repositories.DbDoctorRepository;
import com.sugarcubes.myglucose.repositories.DbExerciseEntryRepository;
import com.sugarcubes.myglucose.repositories.DbGlucoseEntryRepository;
import com.sugarcubes.myglucose.repositories.DbMealEntryRepository;
import com.sugarcubes.myglucose.repositories.DbPatientRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IApplicationUserRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IDoctorRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IExerciseEntryRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IGlucoseEntryRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IMealEntryRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IPatientRepository;

import java.util.HashMap;
import java.util.Map;

// Adapted from:
// https://softwareengineering.stackexchange.com/questions/354465/pure-dependency-injection-how-to-implement-it
class ObjectGraph
{
	private final Map<Class<?>, Object> dependencies = new HashMap<>();    // Holds all dependencies

	ObjectGraph( Context context )    // package-private
	{
		/*
			Step 1.  create dependency graph:
		 */

		// Log Actions:
		ILogMealEntryAction logMealEntryAction = new DbLogMealEntryAction();
		ILogExerciseEntryAction logExerciseEntryAction = new DbLogExerciseEntryAction();
		ILogGlucoseEntryAction logGlucoseEntryAction = new DbLogGlucoseEntryAction();

		// Sync Actions:
		ISyncMealDataAction syncMealDataAction = new SimulateSyncMealDataAction();
		ISyncExerciseDataAction syncExerciseDataAction = new SimulateSyncExerciseDataAction();
		ISyncGlucoseDataAction syncGlucoseDataAction = new SimulateSyncGlucoseDataAction();
		ISyncPatientDataAction syncPatientDataAction = new RemoteSyncPatientDataAction();

		// Remote Actions:
		ILoginAction remoteLoginAction = new RemoteLoginAction();
		IRetrieveDoctorsAction retrieveDoctorsAction = new SimulateRetrieveDoctorsAction();
		IRegisterPatientAction registerPatientAction = new SimulateRegisterPatientAction();

		// Repositories:
		IPatientRepository patientRepository = new DbPatientRepository( context );
		IApplicationUserRepository userRepository = new DbApplicationUserRepository( context );
		IDoctorRepository doctorRepository = new DbDoctorRepository( context );
		IExerciseEntryRepository exerciseEntryRepository = new DbExerciseEntryRepository( context );
		IGlucoseEntryRepository glucoseEntryRepository = new DbGlucoseEntryRepository( context );
		IMealEntryRepository mealEntryRepository = new DbMealEntryRepository( context );


		/*
			Step 2. add models which you will need later to a dependencies map
		 */

		// Log actions:
		dependencies.put( ILogMealEntryAction.class, logMealEntryAction );
		dependencies.put( ILogExerciseEntryAction.class, logExerciseEntryAction );
		dependencies.put( ILogGlucoseEntryAction.class, logGlucoseEntryAction );

		// Sync actions:
		dependencies.put( ISyncMealDataAction.class, syncMealDataAction );
		dependencies.put( ISyncExerciseDataAction.class, syncExerciseDataAction );
		dependencies.put( ISyncGlucoseDataAction.class, syncGlucoseDataAction );
		dependencies.put( ISyncPatientDataAction.class, syncPatientDataAction );

		// Remote Actions:
		dependencies.put( ILoginAction.class, remoteLoginAction );
		dependencies.put( IRegisterPatientAction.class, registerPatientAction );
		dependencies.put( IRetrieveDoctorsAction.class, retrieveDoctorsAction );

		// Repositories:
		dependencies.put( IPatientRepository.class, patientRepository );
		dependencies.put( IApplicationUserRepository.class, userRepository );
		dependencies.put( IDoctorRepository.class, doctorRepository );
		dependencies.put( IExerciseEntryRepository.class, exerciseEntryRepository );
		dependencies.put( IGlucoseEntryRepository.class, glucoseEntryRepository );
		dependencies.put( IMealEntryRepository.class, mealEntryRepository );

	} // constructor


	<T> T get( Class<T> model )
	{
		return model.cast( dependencies.get( model ) );

	} // get


	<T> void putMock( Class<T> clazz, T object )
	{
		dependencies.put( clazz, object );

	} // putMock

} // class
