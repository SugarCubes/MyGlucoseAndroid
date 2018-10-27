package com.sugarcubes.myglucose.actions;

import android.content.Context;

import com.sugarcubes.myglucose.actions.interfaces.ILoginAction;
import com.sugarcubes.myglucose.entities.Doctor;
import com.sugarcubes.myglucose.enums.ErrorCode;
import com.sugarcubes.myglucose.repositories.DbPatientRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

import java.util.Date;

public class SimulateLoginAction implements ILoginAction
{
	@Override
	public ErrorCode attemptLogin( String username, String password, Context context )
	{
		try {
			PatientSingleton patientSingleton = PatientSingleton.getInstance();
			DbPatientRepository patientRepository = new DbPatientRepository( context );
			patientSingleton.setEmail( username );
			patientSingleton.setUserName( username );
			patientSingleton.setFirstName( "John" );
			patientSingleton.setLastName( "Doe" );
			patientSingleton.setAddress1( "123 Example Lane" );
			patientSingleton.setAddress2( "Apt. 2" );
			patientSingleton.setCity( "Exampleview" );
			patientSingleton.setState( "TN" );
			patientSingleton.setZip1( 12345 );
			patientSingleton.setZip2( 1234 );
			patientSingleton.setPhoneNumber( "(555) 999-1234" );
			Date date = new Date();
			patientSingleton.setCreatedAt( date );
			patientSingleton.setTimestamp( date.getTime() );

			Doctor dr = new Doctor();
			dr.setEmail( "dr.jones@example.com" );
			patientSingleton.setDoctor( dr );

			patientRepository.create( patientSingleton );

			return ErrorCode.NO_ERROR;
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return ErrorCode.UNKNOWN;
		}

	} // attemptLogin

} // class
