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
import android.widget.Button;
import android.app.LoaderManager.LoaderCallbacks;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.enums.UserType;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

public class MainActivity
		extends AppCompatActivity
		implements LoaderCallbacks<Cursor>,
		View.OnTouchListener
{
	private final String LOG_TAG = "MainActivity";
	private PatientSingleton appUser = PatientSingleton.getInstance();
	private Menu menu;
	private final int USER_LOADER = 100;
	private final int LOGIN_REQUEST = 200;

	@Override
	protected void onCreate( Bundle savedInstanceState )
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
		glucoseButton.setOnTouchListener( this );
		mealsButton.setOnTouchListener( this );
		exerciseButton.setOnTouchListener( this );

	} // onCreate


	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.main, menu );

		if( appUser == null )
			appUser = PatientSingleton.getInstance();

		this.menu = menu;
		menu.findItem( R.id.action_login )
				.setTitle( appUser.isLoggedIn() ? R.string.logout : R.string.login );
		return true;

	} // onCreateOptionsMenu


	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		// Handle presses on the action bar items
		switch( item.getItemId() )
		{
			case R.id.action_settings:
				Intent intent = new Intent( this, SettingsActivity.class );
				startActivity( intent );
				break;

			case R.id.action_login:
				if( appUser.isLoggedIn() )
				{
					item.setTitle( R.string.login );
					appUser.setLoggedIn( false );
					// TODO: set not logged in in DB
				}
				else
				{
					Intent loginIntent = new Intent( this, LoginActivity.class );
					startActivityForResult( loginIntent, LOGIN_REQUEST );	// Redirect to the Login Activity
				}
				break;

			case R.id.action_register:
				Intent registerIntent = new Intent( this, RegisterActivity.class );
				startActivity( registerIntent );
				break;

			default:
				return super.onOptionsItemSelected( item );
		}

		return true;

	} // onOptionsItemSelected

	public void loaderReset()
	{
//		if( mCapsuleAdapter != null && cursor != null )
//		{
//			mCapsuleAdapter.notifyDataSetChanged();
//			//mCapsuleAdapter.getCursor().requery();	// cursor is null
//			mCapsuleAdapter.changeCursor( cursor );
//		}
		// 1. Must be called in order to show the items in the list:
		// 2. Causes app to crash on orientation change:
		//if( getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT )

		//getLoaderManager().initLoader( USER_LOADER, null, this );
		getLoaderManager().restartLoader( USER_LOADER, null, this );
		//getLoaderManager().notify();	// Object not locked by thread before notify()
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

//		if( cursor != null )
//			synchronized( cursor )
//			{
//				cursor.notify();
//			}

	} // loaderReset


	@Override
	public Loader<Cursor> onCreateLoader( int id, Bundle args )
	{
		return new CursorLoader( getApplicationContext(), MyGlucoseContentProvider.USERS_URI,
				null, DB.KEY_USER_TYPE + "=?", new String[]{ UserType.PATIENT.toString() }, null );

	} // onCreateLoader


	@Override
	public void onLoadFinished( Loader<Cursor> loader, Cursor cursor )
	{
//		this.cursor = cursor;
//		if( mAdapter != null )
//			mAdapter.swapCursor( cursor );
		if( cursor != null && !cursor.isClosed() )			// This should return Users from db
		{
			if( cursor.getCount() > 0 )						// If there are no users logged in...
				appUser.loadFromCursor( cursor );

			if( !appUser.isLoggedIn() )
			{
				Intent intent = new Intent( this, LoginActivity.class );
				startActivityForResult( intent, LOGIN_REQUEST );	// Redirect to the Login Activity
			}

			synchronized( cursor )
			{
				cursor.notify();
			}

		} // if cursor valid

	} // onLoadFinished


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to
		if ( requestCode == LOGIN_REQUEST )
		{
			// Make sure the request was successful
			if (resultCode == RESULT_OK)
			{
				MenuItem loginMenuItem = menu.findItem( R.id.action_login );
				if( loginMenuItem != null )
					loginMenuItem.setTitle( R.string.logout );

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
		view.performClick();								// Perform default action
		//Log.i( LOG_TAG, "Touch detected: " + view.getId() );

		switch( view.getId() )
		{
			case R.id.glucose_button:								// Glucose button tap
//				Log.i( LOG_TAG, "Glucose button tapped" );
				if( event.getAction() == MotionEvent.ACTION_UP )	// Only handle single event
				{
					Intent glucoseIntent = new Intent( this, LogGlucoseActivity.class );
					startActivity( glucoseIntent );
				}
				break;

			case R.id.meals_button:									// Meals button tap
//				Log.i( LOG_TAG, "Meals button tapped" );
				if( event.getAction() == MotionEvent.ACTION_UP )	// Only handle single event
				{
					Intent mealsIntent = new Intent( this, LogMealsActivity.class );
					startActivity( mealsIntent );
				}
				break;

			case R.id.exercise_button:								// Exercise button tap
//				Log.i( LOG_TAG, "Exercise button tapped" );
				if( event.getAction() == MotionEvent.ACTION_UP )	// Only handle single event
				{
					Intent exerciseIntent = new Intent( this, LogExerciseActivity.class );
					startActivity( exerciseIntent );
				}
				break;

		} // switch

		return false;

	} // onTouch

} // class
