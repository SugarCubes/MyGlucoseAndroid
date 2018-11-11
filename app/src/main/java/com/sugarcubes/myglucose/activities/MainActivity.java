//--------------------------------------------------------------------------------------//
//																						//
// File Name:	MainActivity.java														//
// Programmer:	J.T. Blevins (jt.blevins@gmail.com)										//
// Date:		09/03/2018																//
// Purpose:		The main activity, shown when the user first opens the app and logs in. //
//																						//
//--------------------------------------------------------------------------------------//

package com.sugarcubes.myglucose.activities;

import android.Manifest;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.dependencies.Dependencies;
import com.sugarcubes.myglucose.repositories.DbPatientRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IPatientRepository;
import com.sugarcubes.myglucose.services.AuthenticatorService;
import com.sugarcubes.myglucose.services.PedometerService;
import com.sugarcubes.myglucose.services.SyncService;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

public class MainActivity
		extends AppCompatActivity
		implements LoaderCallbacks<Cursor>,
		View.OnTouchListener
{
	public static final boolean DEBUG = true;                        // Activate/deactivate logging

	private final       String           LOG_TAG                    = getClass().getSimpleName();
	private             PatientSingleton patientUser                =
			PatientSingleton.getInstance();
	public static final int              USER_LOADER                = 100;// Loader ID
	public static final int              LOGIN_REQUEST              = 200;// Return code after login
	public static final int              REGISTER_REQUEST           = 300;
	// Return code after register
	public static final int              RESULT_REGISTER_SUCCESSFUL = 101;
	private Menu menu;                   // Reference to change Login/logout text

	private static final String[] INITIAL_PERMS   = {
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.ACCESS_FINE_LOCATION
	};
	private static final String[] LOCATION_PERMS  = {
			Manifest.permission.ACCESS_FINE_LOCATION
	};
	private static final int      INITIAL_REQUEST = 1337;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		Toolbar toolbar = findViewById( R.id.toolbar );
		setSupportActionBar( toolbar );
		setTitle( R.string.app_name );

		// Initialize loader to get user information:
		getLoaderManager().initLoader( USER_LOADER, null, this );

		Button glucoseButton = findViewById( R.id.glucose_button );
		Button mealsButton = findViewById( R.id.meals_button );
		Button exerciseButton = findViewById( R.id.exercise_button );
		glucoseButton.setOnTouchListener( this );
		mealsButton.setOnTouchListener( this );
		exerciseButton.setOnTouchListener( this );

		// Start all of the services to run in the background:
		startService( new Intent( this, SyncService.class ) );
		startService( new Intent( this, AuthenticatorService.class ) );

		if( !canAccessFineLocation() && !canAccessCoarseLocation()
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
		{
			requestPermissions( INITIAL_PERMS, INITIAL_REQUEST );
		}

		// TODO: Remove for sprint presentation:
		Intent pedometerIntent = new Intent( this, PedometerService.class );
		pedometerIntent.setAction( PedometerService.ACTION_START );
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )
			startForegroundService( pedometerIntent );
		else
			startService( pedometerIntent );

	} // onCreate

	private boolean canAccessFineLocation()
	{
		return ( hasPermission( Manifest.permission.ACCESS_FINE_LOCATION ) );

	} // canAccessFineLocation

	private boolean canAccessCoarseLocation()
	{
		return ( hasPermission( Manifest.permission.ACCESS_COARSE_LOCATION ) );

	} // canAccessCoarseLocation

	private boolean hasPermission( String perm )
	{
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
		{
			return ( PackageManager.PERMISSION_GRANTED == checkSelfPermission( perm ) );
		}
		return false;

	} // hasPermission


	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.main, menu );

		this.menu = menu;

		setMenuTexts();                    // Show Log in/out, Register

		return true;

	} // onCreateOptionsMenu


	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		// Handle presses on the action bar items
		switch( item.getItemId() )
		{
			case R.id.action_settings:
				startSettingsActivity();
				break;

			case R.id.action_login:
				if( patientUser.isLoggedIn() )
				{
					DbPatientRepository patientRepository = // Get reference to repo
							new DbPatientRepository( getApplicationContext() );
					patientRepository.delete( patientUser );// Delete from db
					PatientSingleton.eraseData();           // deletes data and sets logged in to false
					setMenuTexts();                         // Show Log in/out, Register
				}
				else
				{
					startLoginActivity();
				}
				break;

			case R.id.action_register:
				startRegisterActivity();
				break;

			case R.id.action_edit_profile:
				if(patientUser.isLoggedIn())
				{
					startEditProfileActivity();
				}
				else
				{
					startLoginActivity();
				}
				break;

			case R.id.action_view_profile:
				if( DEBUG ) Log.d( LOG_TAG, "View Profile clicked!" );
				if(patientUser.isLoggedIn())
				{
					startViewProfileActivity();
				}
				else
				{
					startLoginActivity();
				}
				break;

			case R.id.action_exit:
				finish();
				break;

			default:
				return super.onOptionsItemSelected( item );
		}

		return true;

	} // onOptionsItemSelected


	/**
	 * loaderReset - Refreshes content resolver when the db changes
	 */
	public void loaderReset()
	{
		getLoaderManager().restartLoader( USER_LOADER, null, this );
		try
		{
			getLoaderManager().getLoader( USER_LOADER ).forceLoad();
			getLoaderManager().notify();
		}
		catch( Exception e )
		{
			Log.i( "LOADER", "Loader not initialized. Not forcing load." + e.getMessage() );
		}

		getApplicationContext().getContentResolver().notifyChange(
				MyGlucoseContentProvider.PATIENT_USERS_URI, null );

	} // loaderReset


	/**
	 * onCreateLoader - This is where the actual database query will be performed.
	 *
	 * @param id   - Loader id
	 * @param args - Args
	 * @return CursorLoader
	 */
	@Override
	public Loader<Cursor> onCreateLoader( int id, Bundle args )
	{
		// This is where the main joined query takes place. We want to check if the user is
		//	logged in. To do this, we join the "patients" and "users" table, and check the
		//	"logged_in" column
		return new CursorLoader( getApplicationContext(), MyGlucoseContentProvider.PATIENT_USERS_URI,
				null, DB.KEY_USER_LOGGED_IN + "=?",
				new String[]{ String.valueOf( 1 ) }, null );

	} // onCreateLoader


	@Override
	public void onLoadFinished( Loader<Cursor> loader, Cursor cursor )
	{
		//		this.cursor = cursor;
		//		if( mAdapter != null )
		//			mAdapter.swapCursor( cursor );
		if( cursor != null && !cursor.isClosed() &&
				cursor.getCount() > 0 ) // This should return Users from db
		{

			if( DEBUG ) Log.d( LOG_TAG, patientUser.toString() );

			if( cursor.getCount() > 0 )                        // If there are no users logged in...
			{
				// Patient is already initialized. Now we need to log him/her in:
				IPatientRepository patientRepository = Dependencies.get( IPatientRepository.class );
				patientRepository.readFromCursor( PatientSingleton.getInstance(), cursor );

			} // if
			else if( DEBUG )
				Log.e( LOG_TAG, "No user is returned from the database" );

			if( DEBUG ) Log.d( LOG_TAG, patientUser.toString() );

			setMenuTexts();                                        // Show Log in/out, Register

			//			synchronized( cursor )
			//			{
			//				cursor.notify();
			//			}

		} // if cursor valid

		else
		{
			startLoginActivity();
		}

	} // onLoadFinished


	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		// Check which request we're responding to
		switch( requestCode )
		{
			case LOGIN_REQUEST:
				// Make sure the request was successful
				if( resultCode == RESULT_OK )
				{
					setMenuTexts();

					startSettingsActivity();

				} // if RESULT_OK
				break;

			case REGISTER_REQUEST:
				setMenuTexts();
				break;

		} // switch

		switch( resultCode )    // TODO: Not working after registering...
		{
			case RESULT_REGISTER_SUCCESSFUL:
				setMenuTexts();
				break;

		} // switch

	} // onActivityResult


	@Override
	public void onLoaderReset( Loader<Cursor> loader )
	{
		//		loaderReset();

	} // onLoaderReset


	@Override
	public boolean onTouch( View view, MotionEvent event )
	{
		view.performClick();                                // Perform default action
		//Log.i( LOG_TAG, "Touch detected: " + view.getId() );

		switch( view.getId() )
		{
			case R.id.glucose_button:                                // Glucose button tap
				if( DEBUG ) Log.d( LOG_TAG, "Glucose button tapped" );
				if(patientUser.isLoggedIn()) {
					if (event.getAction() == MotionEvent.ACTION_UP)    // Only handle single event
					{
						Intent glucoseIntent = new Intent(this, LogGlucoseActivity.class);
						startActivity(glucoseIntent);
					}
				}
				else
				{
					startLoginActivity();
				}
				break;

			case R.id.meals_button:                                    // Meals button tap
				if( DEBUG ) Log.d( LOG_TAG, "Meals button tapped" );
				if(patientUser.isLoggedIn()) {
					if (event.getAction() == MotionEvent.ACTION_UP)    // Only handle single event
					{
						Intent mealsIntent = new Intent(this, LogMealActivity.class);
						startActivity(mealsIntent);
					}
				}
				else
				{
					startLoginActivity();
				}
				break;

			case R.id.exercise_button:                                // Exercise button tap
				if( DEBUG ) Log.d( LOG_TAG, "Exercise button tapped" );
				if(patientUser.isLoggedIn()) {
					if (event.getAction() == MotionEvent.ACTION_UP)    // Only handle single event
					{
						Intent exerciseIntent = new Intent(this, LogExerciseActivity.class);
						startActivity(exerciseIntent);
					}
				}
				else
				{
					startLoginActivity();
				}
				break;

		} // switch

		return false;

	} // onTouch


	private void startLoginActivity()
	{
		Intent intent = new Intent( this, LoginActivity.class );
		startActivityForResult( intent, LOGIN_REQUEST );    // Redirect to the Login Activity

	} // startLoginActivity


	private void startRegisterActivity()
	{
		Intent registerIntent = new Intent( this, RegisterActivity.class );
		startActivityForResult( registerIntent, REGISTER_REQUEST );

	} // startLoginActivity


	private void startSettingsActivity()
	{
		Intent intent = new Intent( this, SettingsActivity.class );
		startActivity( intent );

	} // startSettingsActivity


	private void startEditProfileActivity()
	{
		Intent intent = new Intent( this, EditProfileActivity.class );
		startActivity( intent );

	} // startEditProfileActivity

	private void startViewProfileActivity()
	{
		Intent intent = new Intent( this, ViewProfileActivity.class );
		startActivity( intent );

	} // startEditProfileActivity

	private void setMenuTexts()
	{
		if( menu != null )
		{
			menu.findItem( R.id.action_login )
					.setTitle( patientUser.isLoggedIn()
							? R.string.logout
							: R.string.login );

			menu.findItem( R.id.action_register )
					.setVisible( !patientUser.isLoggedIn() );

		} // if

	} // setMenuTexts

} // class
