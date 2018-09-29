package com.sugarcubes.myglucose.singletons;

import android.util.Log;

import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.entities.ApplicationUser;
import com.sugarcubes.myglucose.entities.Doctor;
import com.sugarcubes.myglucose.entities.ExerciseEntry;
import com.sugarcubes.myglucose.entities.GlucoseEntry;
import com.sugarcubes.myglucose.entities.MealEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.sugarcubes.myglucose.activities.MainActivity.DEBUG;

public class PatientSingleton extends ApplicationUser
{
	private static PatientSingleton singleton;
	private static String LOG_TAG = "PatientSingleton";

	private String id;
	private String applicationUserId;

	private String doctorEmail;
	protected Doctor doctor;

	public ArrayList<GlucoseEntry> glucoseEntries;
	public ArrayList<MealEntry> mealEntries;
	public ArrayList<ExerciseEntry> exerciseEntries;
	public String firstname, lastname, phonenumber, city, state, address;
	public double weight, height;


	public PatientSingleton()
	{
		id 					= "";
		applicationUserId 	= "";
		firstname 			= "";
		lastname 			= "";
		phonenumber 		= "";
		city 				= "";
		state 				= "";
		address 			= "";
		doctorEmail 		= "";
		weight 				= 0;
		height 				= 0;
		// Instantiate the doctor:
		doctor 				= new Doctor();
		// Instantiate all ArrayLists:
		glucoseEntries 		= new ArrayList<>();
		mealEntries 		= new ArrayList<>();
		exerciseEntries 	= new ArrayList<>();

	} // constructor


	// Since there should only be 1 user, we make in impossible to create more than 1 instance
	//		of the class.
	public static PatientSingleton getInstance()
	{
		if( singleton == null )
			singleton = new PatientSingleton();

		return singleton;

	} // getInstance


	public static void copyFrom( final String jsonString )
	{
		PatientSingleton patientSingleton = getInstance();
		//parse json data
		try
		{
			JSONObject jsonObject = new JSONObject( jsonString );			// Convert string to Json

			if( jsonObject.getString( "id" ) != null )
			{
				// Use jsonData.getInt( String key ), etc to get data from the object
				patientSingleton.setUserName( jsonObject.getString( DB.KEY_USERNAME ) );
				patientSingleton.setAddress1( jsonObject.getString( DB.KEY_USER_ADDRESS1 ) );
				patientSingleton.setAddress2( jsonObject.getString( DB.KEY_USER_ADDRESS2 ) );
				patientSingleton.setCity( jsonObject.getString( DB.KEY_USER_CITY ) );
				patientSingleton.setEmail( jsonObject.getString( DB.KEY_USER_EMAIL ) );
				patientSingleton.setFirstName( jsonObject.getString( DB.KEY_USER_FIRST_NAME ) );
				patientSingleton.setId( jsonObject.getString( DB.KEY_REMOTE_ID ) );
				patientSingleton.setLastName( jsonObject.getString( DB.KEY_USER_LAST_NAME ) );
				patientSingleton.setPhoneNumber( jsonObject.getString( DB.KEY_USER_PHONE ) );
				patientSingleton.setState( jsonObject.getString( DB.KEY_USER_STATE ) );
				patientSingleton.setZip1( jsonObject.getInt( DB.KEY_USER_ZIP1 ) );
				patientSingleton.setZip2( jsonObject.getInt( DB.KEY_USER_ZIP2 ) );

			} // if !null

		}
		catch( JSONException e )
		{
			if( DEBUG ) Log.e( LOG_TAG, "Error parsing data " + e.toString() );
		}

	} // copyFrom


	public static void copyFrom( final ApplicationUser applicationUser )
	{
		PatientSingleton patientSingleton = getInstance();
		//parse json data
		try
		{
			if( applicationUser != null )
			{
				// Use jsonData.getInt( String key ), etc to get data from the object
				patientSingleton.setUserName( applicationUser.getUserName() );
				patientSingleton.setAddress1( applicationUser.getAddress1() );
				patientSingleton.setAddress2( applicationUser.getAddress2() );
				patientSingleton.setCity( applicationUser.getCity() );
				patientSingleton.setEmail( applicationUser.getEmail() );
				patientSingleton.setFirstName( applicationUser.getFirstName() );
				patientSingleton.setId( applicationUser.getId() );
				patientSingleton.setLastName( applicationUser.getLastName() );
				patientSingleton.setPhoneNumber( applicationUser.getPhoneNumber() );
				patientSingleton.setState( applicationUser.getState() );
				patientSingleton.setZip1( applicationUser.getZip1() );
				patientSingleton.setZip2( applicationUser.getZip2() );

			} // if !null

		}
		catch( Exception e )
		{
			if( DEBUG ) Log.e( LOG_TAG, "Error parsing data " + e.toString() );
		}

	} // copyFrom


	public static void eraseData()
	{
		PatientSingleton patientSingleton = getInstance();

		patientSingleton.setUserName( "" );
		patientSingleton.setAddress1( "" );
		patientSingleton.setAddress2( "" );
		patientSingleton.setCity( "" );
		patientSingleton.setEmail( "" );
		patientSingleton.setFirstName( "" );
		patientSingleton.setId( "" );
		patientSingleton.setLastName( "" );
		patientSingleton.setPhoneNumber( "" );
		patientSingleton.setState( "" );
		patientSingleton.setZip1( 0 );
		patientSingleton.setZip2( 0 );
		patientSingleton.setGlucoseEntries( null );
		patientSingleton.setExerciseEntries( null );
		patientSingleton.setMealEntries( null );
		patientSingleton.setDoctor( null );
		patientSingleton.setLoggedIn( false );

	}


	public Doctor getDoctor()
	{
		return doctor;
	}

	public void setDoctor( Doctor doctor )
	{
		this.doctor = doctor;
	}

	public ArrayList<GlucoseEntry> getGlucoseEntries()
	{
		return glucoseEntries;
	}

	public ArrayList<MealEntry> getMealEntries()
	{
		return mealEntries;
	}

	public ArrayList<ExerciseEntry> getExerciseEntries()
	{
		return exerciseEntries;
	}

	public void setGlucoseEntries( ArrayList<GlucoseEntry> glucoseEntries )
	{
		this.glucoseEntries = glucoseEntries;
	}

	public void setMealEntries( ArrayList<MealEntry> mealEntries )
	{
		this.mealEntries = mealEntries;
	}

	public void setExerciseEntries( ArrayList<ExerciseEntry> exerciseEntries )
	{
		this.exerciseEntries = exerciseEntries;
	}

	@Override
	public String toString()
	{
		String doctorString = doctor != null ? doctor.toString() : "";
		return super.toString() +
				"\nPatientSingleton{" +
				"doctor=" + doctorString +
				", glucoseEntries=" + glucoseEntries +
				", mealEntries=" + mealEntries +
				", exerciseEntries=" + exerciseEntries +
				'}';
	}

} // class
