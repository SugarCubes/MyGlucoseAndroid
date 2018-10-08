package com.sugarcubes.myglucose.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.sugarcubes.myglucose.R;
//import com.sugarcubes.myglucose.actions.interfaces.ILogExerciseEntryAction;
import com.sugarcubes.myglucose.enums.ErrorCode;
import com.sugarcubes.myglucose.repositories.DbExerciseEntryRepository;
import com.sugarcubes.myglucose.repositories.DbPatientRepository;
import com.sugarcubes.myglucose.services.AsyncTaskImplement;
import com.sugarcubes.myglucose.singletons.PatientSingleton;
import com.sugarcubes.myglucose.entities.ExerciseEntry;
import com.sugarcubes.myglucose.enums.WhichMeal;
import com.sugarcubes.myglucose.enums.BeforeAfter;

import java.util.ArrayList;
import java.util.Date;
import java.sql.Timestamp;

public class LogExerciseActivity extends AppCompatActivity
{
	private final String LOG_TAG = getClass().getSimpleName();
	CoordinatorLayout coordinatorLayout;                    // The base view (for using Snackbar)
	public View spinner;                                   // Shows when submitting
	public View exerciseForm;                                  // The view to hide when submitting
	//Create ILogExerciseEntryAction class
	//public ILogExerciseEntryAction logExerciseEntryAction;         // The command to log the exercise
	/*public TableLayout exerciseItemTable;                      // Holds the exerciseItems on the screen
	public ArrayList<TableRow> allTableRows;               // Holds all TableRows*/
	//Create LogExerciseTask method
	//public LogExerciseActivity.LogExerciseTask mAuthTask = null;


	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_exercise);

		//Instantiate submitExercise
		Button button = findViewById(R.id.submitGlucose);
		Button viewLatest = findViewById(R.id.viewLatest);

		button.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					AsyncTaskImplement aSync = new AsyncTaskImplement(getApplicationContext());
					aSync.execute();
					DbPatientRepository dbPatientRepository = new DbPatientRepository(getApplicationContext());
					DbExerciseEntryRepository dbExerciseEntryRepository = new DbExerciseEntryRepository(getApplicationContext());
					PatientSingleton patientSingleton = PatientSingleton.getInstance();
					ExerciseEntry exerciseEntry = new ExerciseEntry();

					//Enter EditTexts and Spinners here
					/* EditText glucoseLevel = findViewById(R.id.glucoseLevel);
					Spinner whichMeal = findViewById(R.id.whichMeal);
					Spinner beforeAfter = findViewById(R.id.beforeAfter);
					WhichMeal whichMealEnum = WhichMeal.OTHER;
					BeforeAfter beforeAfterEnum = BeforeAfter.BEFORE;

					glucoseEntry.setMeasurement(Float.parseFloat(glucoseLevel.getText().toString()));
					whichMealEnum = WhichMeal.valueOf(whichMeal.getSelectedItem().toString().toUpperCase());
					glucoseEntry.setWhichMeal(whichMealEnum);
					beforeAfterEnum = BeforeAfter.valueOf(beforeAfter.getSelectedItem().toString().toUpperCase());
					glucoseEntry.setBeforeAfter(beforeAfterEnum);*/

					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					Date date = new Date();

					//setTimestamp error here
					//exerciseEntry.setTimeStamp(timestamp.getTime());
					exerciseEntry.setDate(timestamp);
					patientSingleton.exerciseEntries.add(exerciseEntry);
					dbPatientRepository.update(patientSingleton.getUserName(), patientSingleton);
					dbExerciseEntryRepository.create(exerciseEntry);

					finish();
					return true;
				}
				return false;
			}
		});

//        viewLatest.setOnTouchListener(new View.OnTouchListener(){
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//
//                    setContentView(R.layout.view_latest_glucose_entry);
//                    return true;
//                }
//                return false;
//            }
//        });

		viewLatest.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					startViewLatestGlucoseActivity();
					return true;
				}
				return false;
			}
		});

	} // onCreate


	private void startViewLatestGlucoseActivity() {
		Intent intent = new Intent(this, ViewLatestGlucoseEntry.class);
		startActivity(intent);

	} // startEditProfileActivity

	/**
	 * Shows the progress UI and hides the login form.
	 */

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

		exerciseForm.setVisibility(show
				? View.GONE
				: View.VISIBLE);
		exerciseForm.animate().setDuration(shortAnimTime).alpha(
				show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				exerciseForm.setVisibility(show
						? View.GONE
						: View.VISIBLE);
			}
		});

		spinner.setVisibility(show
				? View.VISIBLE
				: View.GONE);
		spinner.animate().setDuration(shortAnimTime).alpha(
				show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				spinner.setVisibility(show
						? View.VISIBLE
						: View.GONE);
			}
		});

	}

	/**
	 * An AsyncTask used to log the exercise on a separate thread
	 */
	public class LogExerciseTask extends AsyncTask<Void, Void, ErrorCode>
	{
		//		private static final String LOG_TAG = "LogGlucoseTask";
		ExerciseEntry exerciseEntry;

		LogExerciseTask( ExerciseEntry exerciseEntry )
		{
			this.exerciseEntry = exerciseEntry;

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
				// Save the ExerciseEntry and its ExerciseItems
				//return logExerciseEntryAction.logExerciseEntry( getApplicationContext(), exerciseEntry );
				return null;
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
			//mAuthTask = null;
			showProgress( false );

			switch( errorCode )
			{
				case NO_ERROR:                                    // 0:	No error
					Intent returnData = new Intent();
					returnData.setData( Uri.parse( "logged in" ) );
					setResult( RESULT_OK, returnData );            // Return ok result for activity result
					finish();                                    // Close the activity
					break;

				case UNKNOWN:                                    // 1:	Unknown - something went wrong
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
			//mAuthTask = null;
			showProgress( false );

		} // onCancelled

	} // UserLoginTask


} // class

