package com.sugarcubes.myglucose.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper
{
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// DB Name:
	public static final String DB_NAME = "myglucose";

	// Table names:
	public static final String TABLE_USERS = "users";
	public static final String TABLE_PATIENTS = "patients";
	public static final String TABLE_DOCTORS = "doctors";
	public static final String TABLE_GLUCOSE_ENTRIES = "glucose_entries";
	public static final String TABLE_MEAL_ENTRIES = "meal_entries";
	public static final String TABLE_MEAL_ITEMS = "meal_items";
	public static final String TABLE_EXERCISE_ENTRIES = "exercise_entries";

	public static final String PATIENT_USERS = "patient_users";		// Use in ContentProvider to do joins

	// Also add tables here to use in a for loop:
	private String[] tables = {
			TABLE_USERS,
			TABLE_PATIENTS,
			TABLE_DOCTORS,
			TABLE_GLUCOSE_ENTRIES,
			TABLE_MEAL_ENTRIES,
			TABLE_MEAL_ITEMS,
			TABLE_EXERCISE_ENTRIES
	};

	// Misc db table keys:
	public static final String KEY_ID = "_id";
	public static final String KEY_DATE = "Date";
	public static final String KEY_TIMESTAMP = "Timestamp";
	public static final String KEY_REMOTE_KEY = "RemoteKey";
	// ApplicationUser table keys:
	public static final String KEY_USER_LOGGED_IN = "LoggedIn";
	public static final String KEY_USERNAME = "Username";
	public static final String KEY_USER_FIRST_NAME = "FistName";
	public static final String KEY_USER_LAST_NAME = "LastName";
	public static final String KEY_USER_TYPE = "UserType";
	public static final String KEY_USER_EMAIL = "Email";
	public static final String KEY_USER_ADDRESS1 = "Address1";
	public static final String KEY_USER_ADDRESS2 = "Address2";
	public static final String KEY_USER_CITY = "City";
	public static final String KEY_USER_STATE = "State";
	public static final String KEY_USER_ZIP1 = "Zip1";
	public static final String KEY_USER_ZIP2 = "Zip2";
	public static final String KEY_USER_PHONE = "PhoneNumber";
	// Patient table keys:
	public static final String KEY_DR_EMAIL = "DoctorEmail";
	// Doctor table keys:
	public static final String KEY_DR_ID = "DoctorId";
	public static final String KEY_DR_DEGREE_ABBREVIATION = "DegreeAbbreviation";
	// GlucoseEntry table keys:
	public static final String KEY_GLUCOSE_MEASUREMENT = "Measurement";
	public static final String KEY_GLUCOSE_BEFORE_AFTER = "BeforeAfter";
	public static final String KEY_GLUCOSE_WHICH_MEAL = "WhichMeal";
	// MealEntry table keys:
	public static final String KEY_MEAL_ENTRY_TOTAL_CARBS = "TotalCarbohydrates";
	// MealItem table keys:
	public static final String KEY_MEAL_ITEM_NAME = "Name";
	public static final String KEY_MEAL_ID = "MealId";
	public static final String KEY_MEAL_ITEM_CARBS = "Carbohydrates";
	public static final String KEY_MEAL_ITEM_SERVINGS = "Servings";
	// ExerciseEntry table keys:
	public static final String KEY_EXERCISE_MINUTES_SPENT = "MinutesSpent";
	public static final String KEY_EXERCISE_NAME = "Name";


	public DB( Context context )
	{
		super( context, DB_NAME, null, DATABASE_VERSION );
	}

	public DB( Context context, SQLiteDatabase.CursorFactory factory )
	{
		super( context, DB_NAME, factory, DATABASE_VERSION );
	}

	public DB( Context context, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler )
	{
		super( context, DB_NAME, factory, DATABASE_VERSION, errorHandler );
	}

	@Override
	public void onCreate( SQLiteDatabase db )
	{
		// ADD NEW TABLES HERE

		// CREATE LOGIN TABLE
		String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
				+ KEY_USER_EMAIL + " TEXT PRIMARY KEY,"
				+ KEY_USER_LOGGED_IN + " INTEGER,"
				+ KEY_USERNAME + " TEXT,"
				+ KEY_USER_TYPE + " TEXT,"
				+ KEY_USER_FIRST_NAME + " TEXT,"
				+ KEY_USER_LAST_NAME + " TEXT,"
				+ KEY_USER_ADDRESS1 + " TEXT,"
				+ KEY_USER_ADDRESS2 + " TEXT,"
				+ KEY_USER_CITY + " TEXT,"
				+ KEY_USER_STATE + " TEXT,"
				+ KEY_USER_ZIP1 + " INTEGER,"
				+ KEY_USER_ZIP2 + " INTEGER,"
				+ KEY_USER_PHONE + " TEXT,"
				+ KEY_DATE + " TEXT, "
				+ KEY_TIMESTAMP + " INTEGER );";	// Retrieve as a *long* value

		// CREATE PATIENTS TABLE
		String CREATE_PATIENTS_TABLE = "CREATE TABLE " + TABLE_PATIENTS + "("
				+ KEY_USER_EMAIL + " TEXT PRIMARY KEY,"
				+ KEY_DR_ID + " TEXT );";

		// CREATE DOCTORS TABLE
		String CREATE_DOCTORS_TABLE = "CREATE TABLE " + TABLE_DOCTORS + "("
				+ KEY_USER_EMAIL + " TEXT PRIMARY KEY,"
				+ KEY_DR_DEGREE_ABBREVIATION + " TEXT );";

		// CREATE GLUCOSE TABLE
		String CREATE_GLUCOSE_ENTRIES_TABLE = "CREATE TABLE " + TABLE_GLUCOSE_ENTRIES + "("
				+ KEY_ID + " INTEGER PRIMARY KEY, "
				+ KEY_REMOTE_KEY + " TEXT, "
				+ KEY_USER_EMAIL + " TEXT, "
				+ KEY_GLUCOSE_MEASUREMENT + " REAL, "	// DEFAULT: mmol/L. May need conversion
				+ KEY_GLUCOSE_BEFORE_AFTER + " INTEGER, "
				+ KEY_GLUCOSE_WHICH_MEAL + " INTEGER, "
				+ KEY_DATE + " TEXT, "					// Parse and restrict readings to 3 per day
				+ KEY_TIMESTAMP + " INTEGER);";			// Retrieve as a *long* value

		// CREATE MEALS TABLE
		String CREATE_MEAL_ENTRIES_TABLE = "CREATE TABLE " + TABLE_MEAL_ENTRIES + "("
				+ KEY_ID + " INTEGER PRIMARY KEY, "
				+ KEY_REMOTE_KEY + " TEXT, "
				+ KEY_USER_EMAIL + " TEXT, "
				+ KEY_MEAL_ENTRY_TOTAL_CARBS + " INTEGER, "
				+ KEY_DATE + " TEXT, "
				+ KEY_TIMESTAMP + " INTEGER);";	// Retrieve as a *long* value

		// CREATE MEAL ITEMS TABLE
		String CREATE_MEAL_ITEMS_TABLE = "CREATE TABLE " + TABLE_MEAL_ITEMS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY, "
				+ KEY_REMOTE_KEY + " TEXT, "
				+ KEY_MEAL_ID + " TEXT, "
				+ KEY_MEAL_ITEM_NAME + " TEXT, "
				+ KEY_MEAL_ITEM_CARBS + " INTEGER, "
				+ KEY_MEAL_ITEM_SERVINGS + " INTEGER, "
				+ KEY_DATE + " TEXT, "
				+ KEY_TIMESTAMP + " INTEGER);";	// Retrieve as a *long* value

		// CREATE EXERCISE TABLE
		String CREATE_EXERCISE_ENTRIES_TABLE = "CREATE TABLE " + TABLE_EXERCISE_ENTRIES + "("
				+ KEY_ID + " INTEGER PRIMARY KEY, "
				+ KEY_REMOTE_KEY + " TEXT, "
				+ KEY_USER_EMAIL + " TEXT, "
				+ KEY_EXERCISE_NAME + " TEXT, "
				+ KEY_EXERCISE_MINUTES_SPENT + " INTEGER, "
				+ KEY_DATE + " TEXT, "
				+ KEY_TIMESTAMP + " INTEGER);";	// Retrieve as a *long* value


		db.execSQL( CREATE_USERS_TABLE );
		db.execSQL( CREATE_PATIENTS_TABLE );
		db.execSQL( CREATE_DOCTORS_TABLE );
		db.execSQL( CREATE_GLUCOSE_ENTRIES_TABLE );
		db.execSQL( CREATE_MEAL_ENTRIES_TABLE );
		db.execSQL( CREATE_MEAL_ITEMS_TABLE );
		db.execSQL( CREATE_EXERCISE_ENTRIES_TABLE );

		// NOTE: When a DB has to hit an index, and then another table, this adds
		// 	another query it has to perform. Since we will only be storing one patient
		// 	and one doctor, this extra query would be more overhead than is required.

		// Create Indexes:
		// CREATE PATIENTS ENTRIES INDEX ON user_id
		String CREATE_PATIENTS_INDEX = "CREATE INDEX `patients_index` ON " +
				TABLE_PATIENTS + "(" + DB.KEY_USER_ID + ");";
//
//		// CREATE DOCTORS ENTRIES INDEX ON user_id
//		String CREATE_DOCTORS_INDEX = "CREATE INDEX `patients_index` ON " +
//				TABLE_DOCTORS + "(" + DB.KEY_USER_ID + ");";
//
//		// CREATE MEAL ENTRIES INDEX ON user_id
//		String CREATE_MEAL_ENTRIES_INDEX = "CREATE INDEX `meal_entries_index` ON " +
//				TABLE_MEAL_ENTRIES + "(" + DB.KEY_USER_ID + ");";
//
//		// CREATE MEAL ITEMS INDEX ON meal_id
//		String CREATE_MEAL_ITEMS_INDEX = "CREATE INDEX `meal_items_index` ON " +
//				TABLE_MEAL_ITEMS + "(" + DB.KEY_MEAL_ID + ");";
//
//		// CREATE GLUCOSE ITEMS INDEX ON user_id
//		String CREATE_GLUCOSE_ENTRIES_INDEX = "CREATE INDEX `glucose_entries_index` ON " +
//				TABLE_GLUCOSE_ENTRIES + "(" + DB.KEY_USER_ID + ");";
//
//		// CREATE EXERCISE ITEMS INDEX ON user_id
//		String CREATE_EXERCISE_ENTRIES_INDEX = "CREATE INDEX `exercise_entries_index` ON " +
//				TABLE_EXERCISE_ENTRIES + "(" + DB.KEY_USER_ID + ");";
//
		db.execSQL( CREATE_PATIENTS_INDEX );
//		db.execSQL( CREATE_DOCTORS_INDEX );
//		db.execSQL( CREATE_MEAL_ENTRIES_INDEX );
//		db.execSQL( CREATE_MEAL_ITEMS_INDEX );
//		db.execSQL( CREATE_GLUCOSE_ENTRIES_INDEX );
//		db.execSQL( CREATE_EXERCISE_ENTRIES_INDEX );
	}

	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
	{
		// Drop all tables if exist
		for( String table : tables )
		{
			db.execSQL( "DROP TABLE IF EXISTS " + table );
		}

		// Create tables again
		onCreate( db );

	} // onUpgrade

} // class
