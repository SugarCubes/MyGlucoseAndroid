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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
<<<<<<< HEAD
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.entities.ExerciseEntry;
import com.sugarcubes.myglucose.enums.ErrorCode;
import com.sugarcubes.myglucose.repositories.DbExerciseEntryRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

import java.util.Date;

//import com.sugarcubes.myglucose.actions.interfaces.ILogExerciseEntryAction;
=======
import android.support.v7.widget.Toolbar;
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
>>>>>>> MYG-9-LogExerciseTime

public class LogExerciseActivity extends AppCompatActivity
{
	private final String LOG_TAG = getClass().getSimpleName();
	CoordinatorLayout coordinatorLayout;                    // The base view (for using Snackbar)
<<<<<<< HEAD
	public View spinner;                                   // Shows when submitting
	public View exerciseForm;                                  // The view to hide when submitting
	//Create ILogExerciseEntryAction class
	//public ILogExerciseEntryAction logExerciseEntryAction;         // The command to log the exercise
	/*public TableLayout exerciseItemTable;                      // Holds the exerciseItems on the screen
	public ArrayList<TableRow> allTableRows;               // Holds all TableRows*/
	//Create LogExerciseTask method
	private LogExerciseTask mLogExerciseTask = null;
=======
	//private View                   spinner;                 // Shows when submitting
	private View                   exerciseForm;             // The view to hide when submitting
	private ILogExerciseEntryAction logExerciseEntryAction;   // The command to log the exercise
	/*public TableLayout exerciseItemTable;    				// Holds the ExerciseItems on the screen
	public ArrayList<TableRow> allTableRows;				// Holds all TableRows*/
	private LogExerciseTask mlogExerciseTask = null;
>>>>>>> MYG-9-LogExerciseTime


	@SuppressLint( "ClickableViewAccessibility" )
	@Override
<<<<<<< HEAD
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_exercise);

		//Instantiate submitExercise
		Button button = findViewById(R.id.submitGlucose);
		Button viewLatest = findViewById(R.id.viewLatest);

		button.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_UP)
				{
					// TODO: vvv Move all of this into a new .actions.interfaces.ILogExerciseEntry interface and its implementation vvv
					PatientSingleton patientSingleton = PatientSingleton.getInstance();
					String userName = patientSingleton.getUserName();	// Use this to set to the ExerciseEntry
					DbExerciseEntryRepository dbExerciseEntryRepository
							= new DbExerciseEntryRepository(getApplicationContext());
//					dbExerciseEntryRepository.create(exerciseEntry);
					// TODO: ^^^ Move ^^^

					//Enter EditTexts and Spinners here
					// TODO: Create the exerciseEntry to be passed to the AsyncTask (declared at bottom of this Activity)
					Date date = new Date();
					ExerciseEntry exerciseEntry = new ExerciseEntry();
					exerciseEntry.setDate(date);
					exerciseEntry.setTimestamp( date.getTime() );
					// TODO: Get the rest of the data from the EditTexts


					// TODO: This is the reference to the AsyncTask.
					mLogExerciseTask = new LogExerciseTask( exerciseEntry );
					mLogExerciseTask.execute();

					finish();
=======
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
		Button viewLatest = findViewById( R.id.viewLatest );
		//spinner = findViewById( R.id.save_spinner );
		exerciseForm = findViewById( R.id.exercise_form );

		button.setOnTouchListener( new View.OnTouchListener()
		{
			@Override
			public boolean onTouch( View v, MotionEvent event )
			{
				if( event.getAction() == MotionEvent.ACTION_UP )
				{
					mlogExerciseTask = new LogExerciseTask();
					mlogExerciseTask.execute();

>>>>>>> MYG-9-LogExerciseTime
					return true;
				}
				return false;
			}
		} );

		/*viewLatest.setOnTouchListener( new View.OnTouchListener()
		{
			@Override
<<<<<<< HEAD
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
=======
			public boolean onTouch( View v, MotionEvent event )
			{
				if( event.getAction() == MotionEvent.ACTION_UP )
				{
>>>>>>> MYG-9-LogExerciseTime
					startViewLatestExerciseActivity();
					return true;
				}
				return false;
			}
		} );*/

	} // onCreate


<<<<<<< HEAD
	private void startViewLatestExerciseActivity() {
		// TODO: Change to ViewLatestExerciseEntry:
		Intent intent = new Intent(this, ViewLatestGlucoseEntry.class);
		startActivity(intent);
=======
	private void startViewLatestExerciseActivity()
	{
		Intent intent = new Intent( this, ViewLatestExerciseEntry.class );
		startActivity( intent );
>>>>>>> MYG-9-LogExerciseTime

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

/*		spinner.setVisibility( show
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
		} );*/

	}

	/**
	 * An AsyncTask used to log the exercise on a separate thread
	 */
	public class LogExerciseTask extends AsyncTask<Void, Void, ErrorCode>
	{
		//		private static final String LOG_TAG = "LogExerciseTask";
<<<<<<< HEAD
		ExerciseEntry exerciseEntry;

		LogExerciseTask( ExerciseEntry exerciseEntry )
		{
			this.exerciseEntry = exerciseEntry;

		} // constructor
=======
>>>>>>> MYG-9-LogExerciseTime

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
				EditText minutes = findViewById(R.id.minutes);

				exerciseEntry.setExerciseName(exerciseType.toString());
				exerciseEntry.setMinutes( Integer.parseInt( minutes.getText().toString() ) );

				Date date = new Date();
				//exerciseEntry.setTimeStamp( date.getTime() );
				exerciseEntry.setDate( date );
				// Save the ExerciseEntry and its ExerciseItems
<<<<<<< HEAD
				// TODO: Create a new interface in actions/interfaces/ called ILogExerciseEntryAction
				// TODO: Then, create a new class in actions/ called DbLogExerciseEntryAction to *implement* the interface
				//return logExerciseEntryAction.logExerciseEntry( getApplicationContext(), exerciseEntry );
				return null;
=======
				return logExerciseEntryAction.logExerciseEntry( getApplicationContext(), exerciseEntry );

>>>>>>> MYG-9-LogExerciseTime
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
				case NO_ERROR:                              // 0: No error
					Intent returnData = new Intent();
					returnData.setData( Uri.parse( "exercise logged" ) );
<<<<<<< HEAD
					setResult( RESULT_OK, returnData );     // Return ok result for activity result
					finish();                               // Close the activity
=======
					setResult( RESULT_OK, returnData );            // Return ok result for activity result
					finish();                                    // Close the activity
>>>>>>> MYG-9-LogExerciseTime
					break;

				case UNKNOWN:                               // 1: Unknown - something went wrong
					Snackbar.make( coordinatorLayout, "Unknown error", Snackbar.LENGTH_LONG ).show();
					break;

				default:
					Snackbar.make( coordinatorLayout, "Error", Snackbar.LENGTH_LONG ).show();
					break;
			}

		} // onPostExecute


		@Override
		protected void onCancelled()
		{
<<<<<<< HEAD
			mLogExerciseTask = null;
=======
			mlogExerciseTask = null;
>>>>>>> MYG-9-LogExerciseTime
			showProgress( false );

		} // onCancelled

	} // LogExerciseTask


} // class


