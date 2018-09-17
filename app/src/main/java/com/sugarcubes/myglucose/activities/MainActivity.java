//--------------------------------------------------------------------------------------//
//																						//
// File Name:	MainActivity.java														//
// Programmer:	J.T. Blevins (jt.blevins@gmail.com)										//
// Date:		09/03/2018																//
// Purpose:		The main activity, shown when the user first opens the app and logs in. //
//																						//
//--------------------------------------------------------------------------------------//

package com.sugarcubes.myglucose.activities;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.app.LoaderManager.LoaderCallbacks;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.repositories.DbPatientRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;
import android.widget.Spinner;

public class MainActivity
		extends AppCompatActivity
		implements LoaderCallbacks<Cursor>,
		View.OnTouchListener
{
	private final String LOG_TAG = "MainActivity";
	private PatientSingleton patientUser = PatientSingleton.getInstance();
	private Menu menu;
	private final int USER_LOADER = 100;
	private final int LOGIN_REQUEST = 200;

	@Override
	protected void onCreate( Bundle savedInstanceState)
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		Toolbar toolbar = findViewById( R.id.toolbar );
		setSupportActionBar( toolbar );
		setTitle( R.string.app_name );

		// Initialize loader to handle calls to ContentProvider
		getLoaderManager().initLoader( USER_LOADER, null, this );

		Button glucoseButton = findViewById( R.id.glucose_button );
		Button mealsButton = findViewById( R.id.meals_button );
		Button exerciseButton = findViewById( R.id.exercise_button );
		Button editButton = findViewById (R.id.edit);
		glucoseButton.setOnTouchListener( this );
		mealsButton.setOnTouchListener( this );
		exerciseButton.setOnTouchListener( this );
		editButton.setOnTouchListener( this );


	} // onCreate


	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.main, menu );

		this.menu = menu;

		setMenuTexts();					// Show Log in/out, Register

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
					patientUser.setLoggedIn( false );
					DbPatientRepository patientRepository =
							new DbPatientRepository( getApplicationContext() );
					patientRepository.delete( patientUser );
					setMenuTexts();		// Show Log in/out, Register
				}
				else
				{
					startLoginActivity();
				}
				break;

			case R.id.action_register:
				startRegisterActivity();
				break;

			default:
				return super.onOptionsItemSelected( item );
		}

		return true;

	} // onOptionsItemSelected


	/**
	 * 	loaderReset - Refreshes content resolver when the db changes
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
				MyGlucoseContentProvider.USERS_URI, null );

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
				null, DB.TABLE_USERS + "." + DB.KEY_USER_LOGGED_IN + "=?",
				new String[]{ String.valueOf( 1 ) }, null );

	} // onCreateLoader


	@Override
	public void onLoadFinished( Loader<Cursor> loader, Cursor cursor )
	{
//		this.cursor = cursor;
//		if( mAdapter != null )
//			mAdapter.swapCursor( cursor );
		if( cursor != null && !cursor.isClosed() )            // This should return Users from db
		{

			Log.d( LOG_TAG, patientUser.toString() );

			if( cursor.getCount() > 0 )                        // If there are no users logged in...
			{
				// Patient is already initialized. Now we need to log him/her in:
				DbPatientRepository patientRepository
						= new DbPatientRepository( getApplicationContext() );
				patientRepository.populate( patientUser, cursor );

			} // if

			Log.d( LOG_TAG, patientUser.toString() );

			if( !patientUser.isLoggedIn() )
			{
				startLoginActivity();
			}

			synchronized( cursor )
			{
				cursor.notify();
			}

		} // if cursor valid

	} // onLoadFinished


	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		// Check which request we're responding to
		if( requestCode == LOGIN_REQUEST )
		{
			// Make sure the request was successful
			if( resultCode == RESULT_OK )
			{
				setMenuTexts();

				// TODO: Do this only if first time logging in:
				startSettingsActivity();

			} // if RESULT_OK

		} // if LOGIN_REQUEST

	} // onActivityResult


	@Override
	public void onLoaderReset( Loader<Cursor> loader )
	{
		loaderReset();

	} // onLoaderReset


	@Override
	public boolean onTouch( View view, MotionEvent event )
	{
		view.performClick();                                // Perform default action
		//Log.i( LOG_TAG, "Touch detected: " + view.getId() );

		switch( view.getId() )
		{
			case R.id.glucose_button:                                // Glucose button tap
//				Log.i( LOG_TAG, "Glucose button tapped" );
				if( event.getAction() == MotionEvent.ACTION_UP )    // Only handle single event
				{
					Intent glucoseIntent = new Intent( this, LogGlucoseActivity.class );
					startActivity( glucoseIntent );
				}
				break;

			case R.id.meals_button:                                    // Meals button tap
//				Log.i( LOG_TAG, "Meals button tapped" );
				if( event.getAction() == MotionEvent.ACTION_UP )    // Only handle single event
				{
					Intent mealsIntent = new Intent( this, LogMealsActivity.class );
					startActivity( mealsIntent );
				}
				break;

			case R.id.exercise_button:                                // Exercise button tap
//				Log.i( LOG_TAG, "Exercise button tapped" );
				if( event.getAction() == MotionEvent.ACTION_UP )    // Only handle single event
				{
					Intent exerciseIntent = new Intent( this,LogExerciseActivity.class );
                    startActivity( exerciseIntent );
				}
				break;

            case R.id.edit:
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    Intent editIntent = new Intent(this, EditProfileActivity.class);
                    startActivity(editIntent);
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
		startActivity( registerIntent );

	} // startLoginActivity


	private void startSettingsActivity()
	{
		Intent intent = new Intent( this, SettingsActivity.class );
		startActivity( intent );

	} // startSettingsActivity


	private void setMenuTexts()
	{
//		MenuItem loginMenuItem = menu.findItem( R.id.action_login );
//		if( loginMenuItem != null )
//			loginMenuItem.setTitle( R.string.logout );

		menu.findItem( R.id.action_login )
				.setTitle( patientUser.isLoggedIn()
						? R.string.logout
						: R.string.login );

		menu.findItem( R.id.action_register )
				.setVisible( !patientUser.isLoggedIn() );

	} // setMenuTexts

} // class
