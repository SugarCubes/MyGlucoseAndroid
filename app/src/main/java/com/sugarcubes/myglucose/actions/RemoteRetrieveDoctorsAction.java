package com.sugarcubes.myglucose.actions;

import android.content.ContentValues;
import android.content.Context;

import com.sugarcubes.myglucose.actions.interfaces.IRetrieveDoctorsAction;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.dependencies.Dependencies;
import com.sugarcubes.myglucose.entities.Doctor;
import com.sugarcubes.myglucose.repositories.interfaces.IApplicationUserRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IDoctorRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;
import com.sugarcubes.myglucose.singletons.WebClientConnectionSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RemoteRetrieveDoctorsAction implements IRetrieveDoctorsAction
{
	@Override
	public List<Doctor> retrieveDoctors( Context context ) throws JSONException
	{
		ArrayList<Doctor> doctors = new ArrayList<>();
		WebClientConnectionSingleton webConnection =        // Get the connection manager
				WebClientConnectionSingleton.getInstance( context );
		IDoctorRepository doctorRepository =                // Get the doctor repository
				Dependencies.get( IDoctorRepository.class );
		// TODO:
		String jsonString = webConnection.sendRetrieveDoctorsRequest( null );
		JSONObject jsonObject = new JSONObject( jsonString );

		return doctors;

	} // retrieveDoctors

} // class
