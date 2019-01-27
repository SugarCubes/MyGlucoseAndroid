package com.sugarcubes.myglucose.singletons;

import android.util.Log;

import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.enums.SyncStatus;
import com.sugarcubes.myglucose.models.ApplicationUser;
import com.sugarcubes.myglucose.models.Doctor;
import com.sugarcubes.myglucose.models.ExerciseEntry;
import com.sugarcubes.myglucose.models.GlucoseEntry;
import com.sugarcubes.myglucose.models.MealEntry;
import com.sugarcubes.myglucose.models.interfaces.Syncable;
import com.sugarcubes.myglucose.utils.JsonUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.sugarcubes.myglucose.activities.MainActivity.DEBUG;

public class PatientSingleton extends ApplicationUser
{
	private static PatientSingleton singleton;
	private static String LOG_TAG = "PatientSingleton";

	private   String doctorId;
	private   String doctorUserName;
	protected Doctor doctor;

	public ArrayList<GlucoseEntry>  glucoseEntries;
	public ArrayList<MealEntry>     mealEntries;
	public ArrayList<ExerciseEntry> exerciseEntries;
	//	public String firstname, lastname, phonenumber, city, state, address;
	//	public String weight, height;


	/**
	 * This class uses a private constructor so that no other class can create an instance
	 * object except this one, called from the static method, getInstance()
	 */
	private PatientSingleton()
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
		doctorUserName = "";
		weight = "";
		height = "";
		// Instantiate the doctor:
		doctor = new Doctor();
		// Instantiate all ArrayLists:
		glucoseEntries = new ArrayList<>();
		mealEntries = new ArrayList<>();
		exerciseEntries = new ArrayList<>();

	} // private constructor


	// Since there should only be 1 user, we make in impossible to create more than 1 instance
	//		of the class.
	public static PatientSingleton getInstance()
	{
		if( singleton == null || singleton.getUserName() == null )
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
					new JSONObject( jsonString );           // Convert string to Json

			//			if( jsonObject.has( DB.TABLE_PATIENTS ) )       // If "Patient" is passed as an object
			//				jsonObject = jsonObject.getJSONObject( DB.TABLE_PATIENTS );    // Get it

			if( jsonObject.has( DB.KEY_USER_EMAIL ) )
			{
				String nullString = "null";
				// Use jsonData.getInt( String key ), etc to get data from the object
				//				if( !jsonObject.getString( DB.KEY_USERNAME ).equals( nullString )
				//						&& !jsonObject.getString( DB.KEY_USERNAME ).isEmpty() )
				patientSingleton.setUserName( jsonObject.getString( DB.KEY_USERNAME ) );
				if( jsonObject.has( DB.KEY_USER_ADDRESS1 )
						&& !jsonObject.getString( DB.KEY_USER_ADDRESS1 ).equals( nullString )
						&& !jsonObject.getString( DB.KEY_USER_ADDRESS1 ).isEmpty() )
					patientSingleton.setAddress1( jsonObject.getString( DB.KEY_USER_ADDRESS1 ) );
				if( jsonObject.has( DB.KEY_USER_ADDRESS2 )
						&& !jsonObject.getString( DB.KEY_USER_ADDRESS2 ).equals( nullString )
						&& !jsonObject.getString( DB.KEY_USER_ADDRESS2 ).isEmpty() )
					patientSingleton.setAddress2( jsonObject.getString( DB.KEY_USER_ADDRESS2 ) );
				if( jsonObject.has( DB.KEY_USER_CITY )
						&& !jsonObject.getString( DB.KEY_USER_CITY ).equals( nullString )
						&& !jsonObject.getString( DB.KEY_USER_CITY ).isEmpty() )
					patientSingleton.setCity( jsonObject.getString( DB.KEY_USER_CITY ) );
				if( jsonObject.has( DB.KEY_USER_EMAIL )
						&& !jsonObject.getString( DB.KEY_USER_EMAIL ).equals( nullString )
						&& !jsonObject.getString( DB.KEY_USER_EMAIL ).isEmpty() )
					patientSingleton.setEmail( jsonObject.getString( DB.KEY_USER_EMAIL ) );
				if( jsonObject.has( DB.KEY_USER_FIRST_NAME )
						&& !jsonObject.getString( DB.KEY_USER_FIRST_NAME ).equals( nullString )
						&& !jsonObject.getString( DB.KEY_USER_FIRST_NAME ).isEmpty() )
					patientSingleton.setFirstName( jsonObject.getString( DB.KEY_USER_FIRST_NAME ) );
				if( !jsonObject.getString( DB.KEY_REMOTE_ID ).equals( nullString )
						&& !jsonObject.getString( DB.KEY_REMOTE_ID ).isEmpty() )
					patientSingleton.setId( jsonObject.getString( DB.KEY_REMOTE_ID ) );
				if( jsonObject.has( DB.KEY_USER_LAST_NAME )
						&& !jsonObject.getString( DB.KEY_USER_LAST_NAME ).equals( nullString )
						&& !jsonObject.getString( DB.KEY_USER_LAST_NAME ).isEmpty() )
					patientSingleton.setLastName( jsonObject.getString( DB.KEY_USER_LAST_NAME ) );
				if( jsonObject.has( DB.KEY_USER_PHONE )
						&& !jsonObject.getString( DB.KEY_USER_PHONE ).equals( nullString )
						&& !jsonObject.getString( DB.KEY_USER_PHONE ).isEmpty() )
					patientSingleton.setPhoneNumber( jsonObject.getString( DB.KEY_USER_PHONE ) );
				if( jsonObject.has( DB.KEY_USER_STATE )
						&& !jsonObject.getString( DB.KEY_USER_STATE ).equals( nullString )
						&& !jsonObject.getString( DB.KEY_USER_STATE ).isEmpty() )
					patientSingleton.setState( jsonObject.getString( DB.KEY_USER_STATE ) );
				if( jsonObject.has( DB.KEY_USER_ZIP1 )
						&& jsonObject.getInt( DB.KEY_USER_ZIP1 ) > 0 )
					patientSingleton.setZip1( jsonObject.getInt( DB.KEY_USER_ZIP1 ) );
				if( jsonObject.has( DB.KEY_USER_ZIP2 )
						&& jsonObject.getInt( DB.KEY_USER_ZIP2 ) > 0 )
					patientSingleton.setZip2( jsonObject.getInt( DB.KEY_USER_ZIP2 ) );
				if( jsonObject.has( DB.KEY_USER_LOGIN_TOKEN )
						&& !jsonObject.getString( DB.KEY_USER_LOGIN_TOKEN ).equals( nullString )
						&& !jsonObject.getString( DB.KEY_USER_LOGIN_TOKEN ).isEmpty() )
					patientSingleton.setLoginToken( jsonObject.getString( DB.KEY_USER_LOGIN_TOKEN ) );
				if( jsonObject.has( DB.KEY_USER_LOGIN_EXPIRATION_TIMESTAMP )
						&& jsonObject.getLong( DB.KEY_USER_LOGIN_EXPIRATION_TIMESTAMP ) > 0 )
					patientSingleton.setLoginExpirationTimestamp(
							jsonObject.getLong( DB.KEY_USER_LOGIN_EXPIRATION_TIMESTAMP ) );
				if( jsonObject.has( DB.KEY_USER_HEIGHT )
						&& !jsonObject.getString( DB.KEY_USER_HEIGHT ).equals( nullString )
						&& !jsonObject.getString( DB.KEY_USER_HEIGHT ).isEmpty() )
					patientSingleton.setHeight( jsonObject.getString( DB.KEY_USER_HEIGHT ) );
				if( jsonObject.has( DB.KEY_USER_WEIGHT )
						&& !jsonObject.getString( DB.KEY_USER_WEIGHT ).equals( nullString )
						&& !jsonObject.getString( DB.KEY_USER_WEIGHT ).isEmpty() )
					patientSingleton.setWeight( jsonObject.getString( DB.KEY_USER_WEIGHT ) );
				if( jsonObject.has( DB.KEY_DOCTOR ) )
				{
					JSONObject doctorJsonObj = jsonObject.getJSONObject( DB.KEY_DOCTOR );
					Doctor doctor = Doctor.fromJSONObject( doctorJsonObj );
					patientSingleton.setDoctor( doctor );
					patientSingleton.setDoctorUserName( doctor.getUserName() );
					patientSingleton.setDoctorId( doctor.getId() );

				} // if

				if( patientSingleton.getDoctorId() != null &&
						patientSingleton.getDoctorId().isEmpty() )
					patientSingleton.setDoctorId( jsonObject.getString( DB.KEY_DR_ID ) );

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
		patientSingleton.setDoctorUserName( "" );
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

	public String getDoctorUserName()
	{
		return doctorUserName;
	}

	public void setDoctorUserName( String doctorUserName )
	{
		this.doctorUserName = doctorUserName;
	}

	public String getDoctorId()
	{
		return doctorId;
	}

	public void setDoctorId( String doctorId )
	{
		this.doctorId = doctorId;
	}

	public static JSONObject toJSONObject( SyncStatus... syncStatus ) throws JSONException
	{
		// APPLICATION USER ATTRIBUTES:
		// NOTE 1: Do NOT send the Id or it may fail to bind:
		//		if( !id.isEmpty() )
		//			put( DB.KEY_REMOTE_ID, id );
		// NOTE 2: Some string objects may be null because they were never filled in by the user.
		//		In this case, check if null and !.isEmpty()

		PatientSingleton patient = getInstance();
		JSONObject json = new JSONObject();

		json.put( DB.KEY_USERNAME, patient.getUserName() );
		if( patient.getEmail() != null && !patient.getEmail().isEmpty() )
			json.put( DB.KEY_USER_EMAIL, patient.getEmail() );
		if( patient.getFirstName() != null && !patient.getFirstName().isEmpty() )
			json.put( DB.KEY_USER_FIRST_NAME, patient.getFirstName() );
		if( patient.getLastName() != null && !patient.getLastName().isEmpty() )
			json.put( DB.KEY_USER_LAST_NAME, patient.getLastName() );
		if( patient.getAddress1() != null && !patient.getAddress1().isEmpty() )
			json.put( DB.KEY_USER_ADDRESS1, patient.getAddress1() );
		if( patient.getAddress2() != null && !patient.getAddress2().isEmpty() )
			json.put( DB.KEY_USER_ADDRESS2, patient.getAddress2() );
		if( patient.getCity() != null && !patient.getCity().isEmpty() )
			json.put( DB.KEY_USER_CITY, patient.getCity() );
		if( patient.getState() != null && !patient.getState().isEmpty() )
			json.put( DB.KEY_USER_STATE, patient.getState() );
		if( patient.getPhoneNumber() != null && !patient.getPhoneNumber().isEmpty() )
			json.put( DB.KEY_USER_PHONE, patient.getPhoneNumber() );
		if( patient.getHeight() != null && !patient.getHeight().isEmpty() )
			json.put( DB.KEY_USER_HEIGHT, patient.getHeight() );
		if( patient.getWeight() != null && !patient.getWeight().isEmpty() )
			json.put( DB.KEY_USER_WEIGHT, patient.getWeight() );
		if( patient.getLoginToken() != null && !patient.getLoginToken().isEmpty() )
			json.put( DB.KEY_USER_LOGIN_TOKEN, patient.getLoginToken() );
		if( patient.getZip1() > 0 )
			json.put( DB.KEY_USER_ZIP1, patient.getZip1() );
		if( patient.getZip2() > 0 )
			json.put( DB.KEY_USER_ZIP1, patient.getZip2() );
		//		if( timestamp > 0 )
		//			put( DB.KEY_TIMESTAMP, timestamp );
		if( patient.getLoginExpirationTimestamp() > 0 )
			json.put( DB.KEY_USER_LOGIN_EXPIRATION_TIMESTAMP,
					patient.getLoginExpirationTimestamp() );
		try
		{
			json.put( DB.KEY_CREATED_AT, JsonUtilities.dateToJson( patient.getCreatedAt() ) );
			json.put( DB.KEY_UPDATED_AT, JsonUtilities.dateToJson( patient.getUpdatedAt() ) );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		//		// PATIENT ATTRIBUTES:
		if( patient.getDoctorUserName() != null && !patient.getDoctorUserName().isEmpty() )
			json.put( DB.KEY_DR_USERNAME, patient.getDoctorUserName() );
		if( patient.getDoctorId() != null && !patient.getDoctorId().isEmpty() )
			json.put( DB.KEY_DR_ID, patient.getDoctorId() );

		// NOTE: Causes sync to fail:
		// if( patient.getDoctor() != null )
		// json.put( DB.KEY_DOCTOR, patient.getDoctor().toJSONObject() );

		if( patient.getGlucoseEntries() != null && patient.getGlucoseEntries().size() > 0 )
		{
			JSONArray gEntries = new JSONArray();
			for( GlucoseEntry glucoseEntry : patient.getGlucoseEntries() )
			{
				if( syncStatus.length == 0 || toBeAdded( syncStatus[ 0 ], glucoseEntry ) )
					gEntries.put( glucoseEntry.toJSONObject() );
			}

			json.put( DB.KEY_GLUCOSE_ENTRIES, gEntries );    // Add the array as JSON

		} // if

		if( patient.getMealEntries() != null && patient.getMealEntries().size() > 0 )
		{
			JSONArray mEntries = new JSONArray();
			for( MealEntry mealEntry : patient.getMealEntries() )
			{
				if( syncStatus.length == 0 || toBeAdded( syncStatus[ 0 ], mealEntry ) )
					mEntries.put( mealEntry.toJSONObject() );
			}

			json.put( DB.KEY_MEAL_ENTRIES, mEntries );    // Add the array as JSON

		} // if

		if( patient.getExerciseEntries() != null && patient.getExerciseEntries().size() > 0 )
		{
			JSONArray eEntries = new JSONArray();
			for( ExerciseEntry exerciseEntry : patient.getExerciseEntries() )
			{
				if( syncStatus.length == 0 || toBeAdded( syncStatus[ 0 ], exerciseEntry ) )
					eEntries.put( exerciseEntry.toJSONObject() );
			}

			json.put( DB.KEY_EXERCISE_ENTRIES, eEntries );
		}

		return json;

	} // toJSONObject


	private static boolean toBeAdded( SyncStatus syncStatus, Syncable syncable )
	{
		// Otherwise, if we want unsynced and it's not synced, include it
		return ( ( syncStatus == SyncStatus.UNSYNCED ||
						syncStatus == SyncStatus.ALL )
						&& !syncable.isSynced() )
				||
				// Or, if we want synced and it's synced, include it
				( ( syncStatus == SyncStatus.SYNCED ||
						syncStatus == SyncStatus.ALL )
						&& syncable.isSynced() );
	} // toBeAdded


	@Override
	public String toString()
	{
		try
		{
			return toJSONObject().toString();
		}
		catch( JSONException e )
		{
			e.printStackTrace();
		}

		return "";

	} // toString

} // class
