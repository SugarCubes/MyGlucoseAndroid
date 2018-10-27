package com.sugarcubes.myglucose.singletons;

import android.util.Log;

import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.entities.ApplicationUser;
import com.sugarcubes.myglucose.entities.Doctor;
import com.sugarcubes.myglucose.entities.ExerciseEntry;
import com.sugarcubes.myglucose.entities.GlucoseEntry;
import com.sugarcubes.myglucose.entities.MealEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.sugarcubes.myglucose.activities.MainActivity.DEBUG;

public class PatientSingleton extends ApplicationUser
{
	private static PatientSingleton singleton;
	private static String LOG_TAG = "PatientSingleton";

	private   String doctorEmail;
	protected Doctor doctor;

	public ArrayList<GlucoseEntry>  glucoseEntries;
	public ArrayList<MealEntry>     mealEntries;
	public ArrayList<ExerciseEntry> exerciseEntries;
	//	public String firstname, lastname, phonenumber, city, state, address;
	//	public String weight, height;


	public PatientSingleton()
	{
		id = "";
		loginToken = "";
		loginExpirationTimestamp = 0;
		firstName = "";
		lastName = "";
		phoneNumber = "";
		city = "";
		state = "";
		address1 = "";
		address2 = "";
		doctorEmail = "";
		weight = "";
		height = "";
		// Instantiate the doctor:
		doctor = new Doctor();
		// Instantiate all ArrayLists:
		glucoseEntries = new ArrayList<>();
		mealEntries = new ArrayList<>();
		exerciseEntries = new ArrayList<>();

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
			JSONObject jsonObject =
					new JSONObject( jsonString );            // Convert string to Json

			if( jsonObject.getString( "id" ) != null )
			{
				// Use jsonData.getInt( String key ), etc to get data from the object
				if( !jsonObject.getString( DB.KEY_USERNAME ).isEmpty() )
					patientSingleton.setUserName( jsonObject.getString( DB.KEY_USERNAME ) );
				if( !jsonObject.getString( DB.KEY_USER_ADDRESS1 ).isEmpty() )
					patientSingleton.setAddress1( jsonObject.getString( DB.KEY_USER_ADDRESS1 ) );
				if( !jsonObject.getString( DB.KEY_USER_ADDRESS2 ).isEmpty() )
					patientSingleton.setAddress2( jsonObject.getString( DB.KEY_USER_ADDRESS2 ) );
				if( !jsonObject.getString( DB.KEY_USER_CITY ).isEmpty() )
					patientSingleton.setCity( jsonObject.getString( DB.KEY_USER_CITY ) );
				if( !jsonObject.getString( DB.KEY_USER_EMAIL ).isEmpty() )
					patientSingleton.setEmail( jsonObject.getString( DB.KEY_USER_EMAIL ) );
				if( !jsonObject.getString( DB.KEY_USER_FIRST_NAME ).isEmpty() )
					patientSingleton.setFirstName( jsonObject.getString( DB.KEY_USER_FIRST_NAME ) );
				if( !jsonObject.getString( DB.KEY_REMOTE_ID ).isEmpty() )
					patientSingleton.setId( jsonObject.getString( DB.KEY_REMOTE_ID ) );
				if( !jsonObject.getString( DB.KEY_USER_LAST_NAME ).isEmpty() )
					patientSingleton.setLastName( jsonObject.getString( DB.KEY_USER_LAST_NAME ) );
				if( !jsonObject.getString( DB.KEY_USER_PHONE ).isEmpty() )
					patientSingleton.setPhoneNumber( jsonObject.getString( DB.KEY_USER_PHONE ) );
				if( !jsonObject.getString( DB.KEY_USER_STATE ).isEmpty() )
					patientSingleton.setState( jsonObject.getString( DB.KEY_USER_STATE ) );
				if( jsonObject.getInt( DB.KEY_USER_ZIP1 ) > 0 )
					patientSingleton.setZip1( jsonObject.getInt( DB.KEY_USER_ZIP1 ) );
				if( jsonObject.getInt( DB.KEY_USER_ZIP2 ) > 0 )
					patientSingleton.setZip2( jsonObject.getInt( DB.KEY_USER_ZIP2 ) );
				if( !jsonObject.getString( DB.KEY_USER_LOGIN_TOKEN ).isEmpty() )
					patientSingleton.setLoginToken( jsonObject.getString( DB.KEY_USER_LOGIN_TOKEN ) );
				if( jsonObject.getLong( DB.KEY_USER_LOGIN_EXPIRATION_TIMESTAMP ) > 0 )
					patientSingleton.setLoginExpirationTimestamp(
							jsonObject.getLong( DB.KEY_USER_LOGIN_EXPIRATION_TIMESTAMP ) );
				if( jsonObject.has( DB.KEY_USER_HEIGHT )
						&& !jsonObject.getString( DB.KEY_USER_HEIGHT ).isEmpty() )
					patientSingleton.setHeight( jsonObject.getString( DB.KEY_USER_HEIGHT ) );
				if( jsonObject.has( DB.KEY_USER_WEIGHT )
						&& !jsonObject.getString( DB.KEY_USER_WEIGHT ).isEmpty() )
					patientSingleton.setWeight( jsonObject.getString( DB.KEY_USER_WEIGHT ) );

			} // if !null

		}
		catch( JSONException e )
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


	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject patient = new JSONObject();
		// APPLICATION USER ATTRIBUTES:
		if( !id.isEmpty() )
			patient.put( DB.KEY_REMOTE_ID, id );
		if( !email.isEmpty() )
			patient.put( DB.KEY_USER_EMAIL, email );
		if( !firstName.isEmpty() )
			patient.put( DB.KEY_USER_FIRST_NAME, firstName );
		if( !lastName.isEmpty() )
			patient.put( DB.KEY_USER_LAST_NAME, lastName );
		if( !userName.isEmpty() )
			patient.put( DB.KEY_USERNAME, userName );
		if( !address1.isEmpty() )
			patient.put( DB.KEY_USER_ADDRESS1, address1 );
		if( !address2.isEmpty() )
			patient.put( DB.KEY_USER_ADDRESS2, address2 );
		if( !city.isEmpty() )
			patient.put( DB.KEY_USER_CITY, city );
		if( !state.isEmpty() )
			patient.put( DB.KEY_USER_STATE, state );
		if( !phoneNumber.isEmpty() )
			patient.put( DB.KEY_USER_PHONE, phoneNumber );
		if( !height.isEmpty() )
			patient.put( DB.KEY_USER_HEIGHT, height );
		if( !weight.isEmpty() )
			patient.put( DB.KEY_USER_WEIGHT, weight );
		if( !loginToken.isEmpty() )
			patient.put( DB.KEY_USER_LOGIN_TOKEN, loginToken );
		if( zip1 > 0 )
			patient.put( DB.KEY_USER_ZIP1, zip1 );
		if( zip2 > 0 )
			patient.put( DB.KEY_USER_ZIP1, zip2 );
		if( timestamp > 0 )
			patient.put( DB.KEY_TIMESTAMP, timestamp );
		if( loginExpirationTimestamp > 0 )
			patient.put( DB.KEY_USER_LOGIN_EXPIRATION_TIMESTAMP, loginExpirationTimestamp );
		if( createdAt != null )
			patient.put( DB.KEY_CREATED_AT, createdAt.toString() );
		if( updatedAt != null )
			patient.put( DB.KEY_UPDATED_AT, updatedAt.toString() );
		// PATIENT ATTRIBUTES:
		if( doctor != null && doctor.getUserName() != null )
			patient.put( DB.KEY_DR_ID, doctor.getUserName() );
		if( glucoseEntries.size() > 0 )
		{
			JSONArray gEntries = new JSONArray();
			for( GlucoseEntry glucoseEntry : glucoseEntries )
			{
				gEntries.put( glucoseEntry.toJSONObject() );
			}

			patient.put( DB.KEY_GLUCOSE_ENTRIES, gEntries );	// Add the array as JSON

		} // if

		if( mealEntries.size() > 0 )
		{
			JSONArray mEntries = new JSONArray();
			for( MealEntry mealEntry : mealEntries )
			{
				mEntries.put( mealEntry.toJSONObject() );
			}

			patient.put( DB.KEY_MEAL_ENTRIES, mEntries );	// Add the array as JSON

		} // if

		if( exerciseEntries.size() > 0 )
		{
			JSONArray eEntries = new JSONArray();
			for( ExerciseEntry exerciseEntry : exerciseEntries )
			{
				eEntries.put( exerciseEntry.toJSONObject() );
			}

			patient.put( DB.KEY_EXERCISE_ENTRIES, eEntries );
		}

		return patient;

	} // toJSONObject


	@Override
	public String toString()
	{
		String doctorString = doctor != null
				? doctor.toString()
				: "";
		return super.toString() +
				"\nPatientSingleton{" +
				"doctor=" + doctorString +
				", glucoseEntries=" + glucoseEntries +
				", mealEntries=" + mealEntries +
				", exerciseEntries=" + exerciseEntries +
				'}';
	}

} // class
