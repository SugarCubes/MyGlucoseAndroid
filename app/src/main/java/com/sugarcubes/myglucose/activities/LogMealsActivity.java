package com.sugarcubes.myglucose.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.actions.interfaces.ILogMealEntryAction;
import com.sugarcubes.myglucose.dependencies.Dependencies;
import com.sugarcubes.myglucose.entities.MealEntry;
import com.sugarcubes.myglucose.enums.ErrorCode;

public class LogMealsActivity extends AppCompatActivity implements View.OnTouchListener
{
	CoordinatorLayout coordinatorLayout;
	private View spinner;
	private View mealForm;
	private ILogMealEntryAction logMealEntryAction;

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private LogMealTask mAuthTask = null;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_log_meals );
		Toolbar toolbar = findViewById( R.id.toolbar );
		setSupportActionBar( toolbar );
		getSupportActionBar().setDisplayHomeAsUpEnabled( true );

		coordinatorLayout = findViewById( R.id.coordinator_layout );

		// Dependency Injection:
		logMealEntryAction = Dependencies.get( ILogMealEntryAction.class );

		Button viewLatestEntryButton = findViewById( R.id.button_view_latest );
		viewLatestEntryButton.setOnTouchListener( this );	// Add listener to latest entry btn

		Button submitButton = findViewById( R.id.button_save );
		submitButton.setOnTouchListener( this );			// Add listener to save button

		spinner = findViewById( R.id.save_spinner );
		mealForm = findViewById( R.id.meal_form );

	} // onCreate


	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB_MR2 )
	private void showProgress( final boolean show )
	{
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2 )
		{
			int shortAnimTime = getResources().getInteger( android.R.integer.config_shortAnimTime );

			mealForm.setVisibility( show
					? View.GONE
					: View.VISIBLE );
			mealForm.animate().setDuration( shortAnimTime ).alpha(
					show
							? 0
							: 1 ).setListener( new AnimatorListenerAdapter()
			{
				@Override
				public void onAnimationEnd( Animator animation )
				{
					mealForm.setVisibility( show
							? View.GONE
							: View.VISIBLE );
				}
			} );

			spinner.setVisibility( show
					? View.VISIBLE
					: View.GONE );
			spinner.animate().setDuration( shortAnimTime ).alpha(
					show
							? 1
							: 0 ).setListener( new AnimatorListenerAdapter()
			{
				@Override
				public void onAnimationEnd( Animator animation )
				{
					spinner.setVisibility( show
							? View.VISIBLE
							: View.GONE );
				}
			} );
		}
		else
		{
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			spinner.setVisibility( show
					? View.VISIBLE
					: View.GONE );
			mealForm.setVisibility( show
					? View.GONE
					: View.VISIBLE );

		} // if honeycomb...else...

	} // showProgress


	private void startViewLatestMealEntryActivity()
	{
		Intent intent = new Intent( this, ViewLatestMealEntry.class );
		startActivity( intent );

	} // startViewLatestMealEntryActivity


	@Override
	public boolean onTouch( View view, MotionEvent event )
	{
		view.performClick();                                // Perform default action
		//Log.i( LOG_TAG, "Touch detected: " + view.getId() );

		if( event.getAction() == MotionEvent.ACTION_UP )    // Only handle single event
		{
			switch( view.getId() )
			{
				case R.id.button_view_latest:
					startViewLatestMealEntryActivity();
					break;

				case R.id.button_save:
					//Snackbar.make( coordinatorLayout, "Save button pressed", Snackbar.LENGTH_LONG ).show();
					MealEntry mealEntry = new MealEntry();
					new LogMealTask( mealEntry ).execute();
					break;

			} // switch

		} // if

		return false;
	} // onTouch


	/**
	 * An AsyncTask used to log the meal on a separate thread
	 */
	public class LogMealTask extends AsyncTask<Void, Void, ErrorCode>
	{

//		private static final String LOG_TAG = "LogMealTask";
		MealEntry mealEntry;

		LogMealTask( MealEntry mealEntry )
		{
			this.mealEntry = mealEntry;

		} // constructor

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			showProgress( true );

		} // onPreExecute

		@Override
		protected ErrorCode doInBackground( Void... params )
		{
			try
			{
				// TODO Send to Db
				return logMealEntryAction.logMealEntry( mealEntry );

			}
			catch( Exception e )
			{
				e.printStackTrace();
				return ErrorCode.UNKNOWN;
			}

		} // doInBackground


		@Override
		protected void onPostExecute( final ErrorCode errorCode )
		{
			mAuthTask = null;
			showProgress( false );

			switch( errorCode )
			{
				case NO_ERROR:									// 0:	No error
					Intent returnData = new Intent();
					returnData.setData( Uri.parse( "logged in" ) );
					setResult( RESULT_OK, returnData );			// Return ok result for activity result
					finish();									// Close the activity
					break;

				case UNKNOWN:									// 1:	Unknown - something went wrong
					// TODO

					break;
			}

		} // onPostExecute


		@Override
		protected void onCancelled()
		{
			mAuthTask = null;
			showProgress( false );

		} // onCancelled

	} // UserLoginTask


} // class
