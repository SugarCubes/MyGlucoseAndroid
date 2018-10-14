package com.sugarcubes.myglucose.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.actions.interfaces.ILogExerciseEntryAction;
import com.sugarcubes.myglucose.dependencies.Dependencies;
import com.sugarcubes.myglucose.entities.ExerciseEntry;
import com.sugarcubes.myglucose.enums.ErrorCode;


import java.util.Date;

public class LogExerciseActivity extends AppCompatActivity
{
	private final String LOG_TAG = getClass().getSimpleName();
	ConstraintLayout constraintLayout;                    // The base view (for using Snackbar)
	private View                    spinner;                 // Shows when submitting
	private View                    exerciseForm;             // The view to hide when submitting
	private ILogExerciseEntryAction logExerciseEntryAction;   // The command to log the exercise
	/*public TableLayout exerciseItemTable;    				// Holds the ExerciseItems on the screen
	public ArrayList<TableRow> allTableRows;				// Holds all TableRows*/
	private LogExerciseTask mlogExerciseTask = null;


	@SuppressLint( "ClickableViewAccessibility" )
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_log_exercise );
		//Toolbar toolbar = findViewById( R.id.toolbar );
		//setSupportActionBar( toolbar );
		if( getSupportActionBar() != null )
			getSupportActionBar().setDisplayHomeAsUpEnabled( true );

		// Return the correct LogExerciseEntry action (set up in .dependencies.ObjectMap)
		logExerciseEntryAction = Dependencies.get( ILogExerciseEntryAction.class );

		Button button = findViewById( R.id.submitButton );
		Button viewLatest = findViewById( R.id.view_latest );
		spinner = findViewById( R.id.save_spinner );
		exerciseForm = findViewById( R.id.exercise_form );
		constraintLayout = findViewById( R.id.contraint_layout );

		button.setOnTouchListener( new View.OnTouchListener()
		{
			@Override
			public boolean onTouch( View v, MotionEvent event )
			{
				if( event.getAction() == MotionEvent.ACTION_UP )
				{
					mlogExerciseTask = new LogExerciseTask();
					mlogExerciseTask.execute();

					return true;
				}
				return false;
			}
		} );

		viewLatest.setOnTouchListener( new View.OnTouchListener()
		{
			@Override
			public boolean onTouch( View v, MotionEvent event )
			{
				if( event.getAction() == MotionEvent.ACTION_UP )
				{
					startViewLatestExerciseActivity();
					return true;
				}
				return false;
			}
		} );

	} // onCreate


	private void startViewLatestExerciseActivity()
	{
		Intent intent = new Intent( this, ViewLatestExerciseEntryActivity.class );
		startActivity( intent );

	} // startEditProfileActivity

	/**
	 * Shows the progress UI and hides the login form.
	 */

	@TargetApi( Build.VERSION_CODES.HONEYCOMB_MR2 )
	private void showProgress( final boolean show )
	{
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		int shortAnimTime = getResources().getInteger( android.R.integer.config_shortAnimTime );

		exerciseForm.setVisibility( show
				? View.GONE
				: View.VISIBLE );
		exerciseForm.animate().setDuration( shortAnimTime ).alpha(
				show
						? 0
						: 1 ).setListener( new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd( Animator animation )
			{
				exerciseForm.setVisibility( show
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

	/**
	 * An AsyncTask used to log the exercise on a separate thread
	 */
	public class LogExerciseTask extends AsyncTask<Void, Void, ErrorCode>
	{
		//		private static final String LOG_TAG = "LogExerciseTask";

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
				ExerciseEntry exerciseEntry = new ExerciseEntry();

				EditText exerciseType = findViewById( R.id.exerciseType );
				EditText minutes = findViewById( R.id.minutes );

				exerciseEntry.setExerciseName( exerciseType.getText().toString() );
				exerciseEntry.setMinutes( Integer.parseInt( minutes.getText().toString() ) );

				Date date = new Date();
				exerciseEntry.setTimestamp( date.getTime() );
				exerciseEntry.setDate( date );
				// Save the ExerciseEntry and its ExerciseItems
				return logExerciseEntryAction.logExerciseEntry( getApplicationContext(), exerciseEntry );

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
			mlogExerciseTask = null;
			showProgress( false );

			switch( errorCode )
			{
				case NO_ERROR:                                    // 0:	No error
					Intent returnData = new Intent();
					returnData.setData( Uri.parse( "exercise logged" ) );
					setResult( RESULT_OK, returnData );            // Return ok result for activity result
					finish();                                    // Close the activity
					break;

				case UNKNOWN:                                    // 1:	Unknown - something went wrong
					Snackbar.make( constraintLayout, "Unknown error", Snackbar.LENGTH_LONG ).show();
					break;

				default:
					Snackbar.make( constraintLayout, "Error", Snackbar.LENGTH_LONG ).show();
					break;
			}

		} // onPostExecute


		@Override
		protected void onCancelled()
		{
			mlogExerciseTask = null;
			showProgress( false );

		} // onCancelled

	} // UserLoginTask


} // class


