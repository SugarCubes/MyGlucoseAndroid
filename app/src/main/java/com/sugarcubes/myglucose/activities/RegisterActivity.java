package com.sugarcubes.myglucose.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.actions.interfaces.IRegisterPatientAction;
import com.sugarcubes.myglucose.actions.interfaces.IRetrieveDoctorsAction;
import com.sugarcubes.myglucose.adapters.DoctorDropDownAdapter;
import com.sugarcubes.myglucose.dependencies.Dependencies;
import com.sugarcubes.myglucose.entities.Doctor;
import com.sugarcubes.myglucose.enums.ErrorCode;
import com.sugarcubes.myglucose.repositories.DbPatientRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IApplicationUserRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

import org.json.JSONException;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>
{
	/**
	 * Id to identity READ_CONTACTS permission request.
	 */
	private static final int    REQUEST_READ_CONTACTS = 0;
	private final        String LOG_TAG               = getClass().getSimpleName();

	private IRetrieveDoctorsAction retrieveDoctorsAction;    // Use to retrieve list of doctors
	private IRegisterPatientAction registerPatientAction;    // Use to register the patient


	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserRegisterTask    mRegisterTask        = null;
	private RetrieveDoctorsTask mRetrieveDoctorsTask = null;

	// UI references.
	private AutoCompleteTextView mEmailView;
	private EditText             mPasswordView;
	private View                 mProgressView;
	private View                 mLoginFormView;
	private Spinner              mDoctorDropdownSpinner;


	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_register );
		setupActionBar();

		// Dependency Injection:
		retrieveDoctorsAction = Dependencies.get( IRetrieveDoctorsAction.class );
		registerPatientAction = Dependencies.get( IRegisterPatientAction.class );

		// Create a background task to retrieve all doctors:
		mRetrieveDoctorsTask = new RetrieveDoctorsTask();
		mRetrieveDoctorsTask.execute( this );

		// Set up the login form.
		mEmailView = (AutoCompleteTextView) findViewById( R.id.email );
		populateAutoComplete();

		mPasswordView = (EditText) findViewById( R.id.password );
		mPasswordView.setOnEditorActionListener( new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction( TextView textView, int id, KeyEvent keyEvent )
			{
				if( id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL )
				{
					attemptRegistration();
					return true;
				}
				return false;
			}
		} );

		Button mEmailSignInButton = (Button) findViewById( R.id.register_button );
		mEmailSignInButton.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick( View view )
			{
				attemptRegistration();
			}
		} );

		mLoginFormView = findViewById( R.id.registration_form );
		mProgressView = findViewById( R.id.register_progress );

	} // onCreate


	private void populateAutoComplete()
	{
		if( !mayRequestContacts() )
		{
			return;
		}

		getLoaderManager().initLoader( 0, null, this );

	} // populateAutoComplete


	private boolean mayRequestContacts()
	{
		if( Build.VERSION.SDK_INT < Build.VERSION_CODES.M )
		{
			return true;
		}
		if( checkSelfPermission( READ_CONTACTS ) == PackageManager.PERMISSION_GRANTED )
		{
			return true;
		}
		if( shouldShowRequestPermissionRationale( READ_CONTACTS ) )
		{
			Snackbar.make( mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE )
					.setAction( android.R.string.ok, new View.OnClickListener()
					{
						@Override
						@TargetApi( Build.VERSION_CODES.M )
						public void onClick( View v )
						{
							requestPermissions( new String[]{ READ_CONTACTS }, REQUEST_READ_CONTACTS );
						}
					} );
		}
		else
		{
			requestPermissions( new String[]{ READ_CONTACTS }, REQUEST_READ_CONTACTS );
		}
		return false;

	} // mayRequestContacts


	/**
	 * Callback received when a permissions request has been completed.
	 */
	@Override
	public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions,
											@NonNull int[] grantResults )
	{
		if( requestCode == REQUEST_READ_CONTACTS &&
				( grantResults.length == 1 &&
						grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED ) )
		{
			populateAutoComplete();
		}

	} // onRequestPermissionsResult


	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	private void setupActionBar()
	{
		if( getSupportActionBar() != null )
			// Show the Up button in the action bar.
			getSupportActionBar().setDisplayHomeAsUpEnabled( true );

	} // setupActionBar

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	private void attemptRegistration()
	{
		if( mRegisterTask != null )
		{
			return;
		}

		// Reset errors.
		mEmailView.setError( null );
		mPasswordView.setError( null );

		PatientSingleton patient = PatientSingleton.getInstance();

		// Store values at the time of the login attempt.
		String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();
		Doctor doctor = (Doctor) mDoctorDropdownSpinner.getSelectedItem();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password, if the user entered one.
		if( !TextUtils.isEmpty( password ) && !isPasswordValid( password ) )
		{
			mPasswordView.setError( getString( R.string.error_invalid_password ) );
			focusView = mPasswordView;
			cancel = true;

		} // if

		// Check for a valid email address.
		if( TextUtils.isEmpty( email ) )
		{
			mEmailView.setError( getString( R.string.error_field_required ) );
			focusView = mEmailView;
			cancel = true;
		}
		else if( !isEmailValid( email ) )
		{
			mEmailView.setError( getString( R.string.error_invalid_email ) );
			focusView = mEmailView;
			cancel = true;

		} // if email empty...else

		if( cancel )
		{
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		}
		else
		{
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			hideKeypad( this );
			showProgress( true );

			patient.setEmail( email );
			// TODO: Test
			patient.setDoctor( doctor );

			mRegisterTask = new UserRegisterTask( patient, password );
			mRegisterTask.execute( (Void) null );

		} // If canceled...else

	} // attemptRegistration


	private boolean isEmailValid( String email )
	{
		return email.contains( "@" );
	}


	private boolean isPasswordValid( String password )
	{
		return password.length() > 7;
	}


	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB_MR2 )
	private void showProgress( final boolean show )
	{
		int shortAnimTime = getResources().getInteger( android.R.integer.config_shortAnimTime );

		mLoginFormView.setVisibility( show
				? View.GONE
				: View.VISIBLE );
		mLoginFormView.animate().setDuration( shortAnimTime ).alpha(
				show
						? 0
						: 1 ).setListener( new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd( Animator animation )
			{
				mLoginFormView.setVisibility( show
						? View.GONE
						: View.VISIBLE );
			}
		} );

		mProgressView.setVisibility( show
				? View.VISIBLE
				: View.GONE );
		mProgressView.animate().setDuration( shortAnimTime ).alpha(
				show
						? 1
						: 0 ).setListener( new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd( Animator animation )
			{
				mProgressView.setVisibility( show
						? View.VISIBLE
						: View.GONE );
			}
		} );

	} // showProgress


	@Override
	public Loader<Cursor> onCreateLoader( int i, Bundle bundle )
	{
		return new CursorLoader( this,
				// Retrieve data rows for the device user's 'profile' contact.
				Uri.withAppendedPath( ContactsContract.Profile.CONTENT_URI,
						ContactsContract.Contacts.Data.CONTENT_DIRECTORY ), ProfileQuery.PROJECTION,

				// Select only email addresses.
				ContactsContract.Contacts.Data.MIMETYPE +
						" = ?", new String[]{ ContactsContract.CommonDataKinds.Email
				.CONTENT_ITEM_TYPE },

				// Show primary email addresses first. Note that there won't be
				// a primary email address if the user hasn't specified one.
				ContactsContract.Contacts.Data.IS_PRIMARY + " DESC" );

	} // onCreateLoader

	@Override
	public void onLoadFinished( Loader<Cursor> cursorLoader, Cursor cursor )
	{
		List<String> emails = new ArrayList<>();
		cursor.moveToFirst();
		while( !cursor.isAfterLast() )
		{
			emails.add( cursor.getString( ProfileQuery.ADDRESS ) );
			cursor.moveToNext();
		}

		addEmailsToAutoComplete( emails );

	} // onLoadFinished


	@Override
	public void onLoaderReset( Loader<Cursor> cursorLoader )
	{

	} // onLoaderReset


	private void addEmailsToAutoComplete( List<String> emailAddressCollection )
	{
		//Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
		ArrayAdapter<String> adapter =
				new ArrayAdapter<>( RegisterActivity.this,
						android.R.layout.simple_dropdown_item_1line, emailAddressCollection );

		mEmailView.setAdapter( adapter );

	} // addEmailsToAutoComplete


	private interface ProfileQuery
	{
		String[] PROJECTION = {
				ContactsContract.CommonDataKinds.Email.ADDRESS,
				ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
		};

		int ADDRESS    = 0;
		int IS_PRIMARY = 1;

	} // interface


	public static void hideKeypad( Activity activity )
	{
		if( activity != null )
		{
			try
			{
				InputMethodManager inputMethodManager = (InputMethodManager) activity
						.getSystemService( Context.INPUT_METHOD_SERVICE );
				inputMethodManager.hideSoftInputFromWindow( activity
						.getCurrentFocus().getWindowToken(), 0 );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}

		} // if

	} // hideKeypad

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserRegisterTask extends AsyncTask<Void, Void, ErrorCode>
	{
		private final PatientSingleton mPatient;
		private final String           mPassword;

		UserRegisterTask( PatientSingleton patient, String password )
		{
			mPatient = patient;
			mPassword = password;

		} // constructor

		@Override
		protected ErrorCode doInBackground( Void... params )
		{
			ErrorCode errorCode = ErrorCode.NO_ERROR;
			try
			{
				// We have populated our PatientSingleton, so now we save that information
				//		to the appropriate databases:
				errorCode = registerPatientAction.registerPatient(
						getApplicationContext(), mPatient, mPassword );
			}
			catch( Exception e )
			{
				return ErrorCode.UNKNOWN;
			}

			return errorCode;

		} // doInBackground

		@Override
		protected void onPostExecute( final ErrorCode error )
		{
			mRegisterTask = null;
			showProgress( false );

			switch( error )
			{
				case NO_ERROR:                                    // 0:	No error
					PatientSingleton.getInstance().setLoggedIn( true );
					Intent returnData = new Intent();
					returnData.setData( Uri.parse( "registered" ) );
					setResult( MainActivity.RESULT_REGISTER_SUCCESSFUL, returnData ); // Return ok result for activity result
					Log.i( LOG_TAG, mPatient.toString() );
					finish();                                    // Close the activity
					break;

				case UNKNOWN:                                    // 1:	Unknown - something went wrong
					mPasswordView.setError( getString( R.string.error_something_went_wrong ) );
					mPasswordView.requestFocus();
					break;

				case INVALID_URL:                                // 2:	URL
					mPasswordView.setError( getString( R.string.error_invalid_hostname_or_port ) );
					mPasswordView.requestFocus();
					break;

				case INVALID_EMAIL_PASSWORD:                    // 3:	Invalid email/password
					mPasswordView.setError( getString( R.string.error_invalid_email_password ) );
					mPasswordView.requestFocus();
					break;

				case USER_ALREADY_REGISTERED:
					mEmailView.setError( getString( R.string.error_already_registered ) );
					break;

			} // switch

		} // onPostExecute

		@Override
		protected void onCancelled()
		{
			mRegisterTask = null;
			showProgress( false );
		}

	} // UserRegisterTask


	private class RetrieveDoctorsTask extends AsyncTask<Activity, Void, List<Doctor>>
	{
		private Activity context;

		@Override
		protected List<Doctor> doInBackground( Activity... contexts )
		{
			mDoctorDropdownSpinner = findViewById( R.id.doctor_dropdown_spinner );
			context = contexts[ 0 ];

			try
			{
				return retrieveDoctorsAction.retrieveDoctors( getApplicationContext() );
			}
			catch( JSONException e )
			{
				e.printStackTrace();
			}

			return null;

		} // doInBackground

		@Override
		protected void onCancelled()
		{
			super.onCancelled();
			mRetrieveDoctorsTask = null;                // Avoids errors

		} // onCancelled

		@Override
		protected void onPostExecute( List<Doctor> doctors )
		{
			super.onPostExecute( doctors );
			mRetrieveDoctorsTask = null;                // Avoids errors

			if( doctors != null )
			mDoctorDropdownSpinner.setAdapter(
					new DoctorDropDownAdapter( context,
							R.layout.doctor_dropdown_item_layout,
							R.id.doctor_name,
							doctors )
			); // setAdapter

		} // onPostExecute

	} // class RetrieveDoctorsTask

} // class

