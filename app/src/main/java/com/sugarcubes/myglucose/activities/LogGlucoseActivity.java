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
import com.sugarcubes.myglucose.actions.interfaces.ILogGlucoseEntryAction;
import com.sugarcubes.myglucose.enums.ErrorCode;
import com.sugarcubes.myglucose.repositories.DbGlucoseEntryRepository;
import com.sugarcubes.myglucose.repositories.DbPatientRepository;
import com.sugarcubes.myglucose.services.AsyncTaskImplement;
import com.sugarcubes.myglucose.singletons.PatientSingleton;
import com.sugarcubes.myglucose.entities.GlucoseEntry;
import com.sugarcubes.myglucose.enums.WhichMeal;
import com.sugarcubes.myglucose.enums.BeforeAfter;

import java.util.ArrayList;
import java.util.Date;
import java.sql.Timestamp;

public class LogGlucoseActivity extends AppCompatActivity
{
	private final String LOG_TAG = getClass().getSimpleName();
	CoordinatorLayout coordinatorLayout;                    // The base view (for using Snackbar)
	public View spinner;                                   // Shows when submitting
	public View glucoseForm;                                  // The view to hide when submitting
	public ILogGlucoseEntryAction logGlucoseEntryAction;         // The command to log the glucose
	/*public TableLayout glucoseItemTable;                      // Holds the GlucoseItems on the screen
	public ArrayList<TableRow> allTableRows;               // Holds all TableRows*/
	public LogGlucoseActivity.LogGlucoseTask mAuthTask = null;


	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_glucose);

		Button button = findViewById(R.id.submitGlucose);
		Button viewLatest = findViewById(R.id.viewLatest);

		button.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					AsyncTaskImplement aSync = new AsyncTaskImplement(getApplicationContext());
					aSync.execute();
					DbPatientRepository dbPatientRepository = new DbPatientRepository(getApplicationContext());
					DbGlucoseEntryRepository dbGlucoseEntryRepository = new DbGlucoseEntryRepository(getApplicationContext());
					PatientSingleton patientSingleton = PatientSingleton.getInstance();
					GlucoseEntry glucoseEntry = new GlucoseEntry();

					EditText glucoseLevel = findViewById(R.id.glucoseLevel);
					Spinner whichMeal = findViewById(R.id.whichMeal);
					Spinner beforeAfter = findViewById(R.id.beforeAfter);
					WhichMeal whichMealEnum = WhichMeal.OTHER;
					BeforeAfter beforeAfterEnum = BeforeAfter.BEFORE;

					glucoseEntry.setMeasurement(Float.parseFloat(glucoseLevel.getText().toString()));
					whichMealEnum = WhichMeal.valueOf(whichMeal.getSelectedItem().toString().toUpperCase());
					glucoseEntry.setWhichMeal(whichMealEnum);
					beforeAfterEnum = BeforeAfter.valueOf(beforeAfter.getSelectedItem().toString().toUpperCase());
					glucoseEntry.setBeforeAfter(beforeAfterEnum);

					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					Date date = new Date();

					glucoseEntry.setTimeStamp(timestamp.getTime());
					glucoseEntry.setDate(timestamp);
					patientSingleton.glucoseEntries.add(glucoseEntry);
					dbPatientRepository.update(patientSingleton.getUserName(), patientSingleton);
					dbGlucoseEntryRepository.create(glucoseEntry);

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

		glucoseForm.setVisibility(show
				? View.GONE
				: View.VISIBLE);
		glucoseForm.animate().setDuration(shortAnimTime).alpha(
				show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				glucoseForm.setVisibility(show
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
	 * An AsyncTask used to log the glucose on a separate thread
	 */
	public class LogGlucoseTask extends AsyncTask<Void, Void, ErrorCode>
	{
		//		private static final String LOG_TAG = "LogGlucoseTask";
		GlucoseEntry glucoseEntry;

		LogGlucoseTask( GlucoseEntry glucoseEntry )
		{
			this.glucoseEntry = glucoseEntry;

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
				// Save the GlucoseEntry and its GlucoseItems
				return logGlucoseEntryAction.logGlucoseEntry( getApplicationContext(), glucoseEntry );

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
			mAuthTask = null;
			showProgress( false );

		} // onCancelled

	} // UserLoginTask


} // class


