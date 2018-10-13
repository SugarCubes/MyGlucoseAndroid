package com.sugarcubes.myglucose.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.actions.RemoteLoginAction;
import com.sugarcubes.myglucose.actions.interfaces.ILoginAction;
import com.sugarcubes.myglucose.dependencies.Dependencies;
import com.sugarcubes.myglucose.entities.ApplicationUser;
import com.sugarcubes.myglucose.enums.ErrorCode;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>
{

	/**
	 * Id to identity READ_CONTACTS permission request.
	 */
	private static final int REQUEST_READ_CONTACTS = 0;

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	private ILoginAction loginAction;
	private ApplicationUser appUser;

	// UI references.
	private AutoCompleteTextView mEmailView;
	private EditText mPasswordView;
	private View mProgressView;
	private View mLoginFormView;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_login );
		// Set up the login form.
		mEmailView = findViewById( R.id.email );
		populateAutoComplete();

		// Dependency Injection:
		loginAction = Dependencies.get( ILoginAction.class );

		mPasswordView = findViewById( R.id.password );
		mPasswordView.setOnEditorActionListener( new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction( TextView textView, int id, KeyEvent keyEvent )
			{
				if( id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL )
				{
					attemptLogin();
					return true;
				}
				return false;
			}
		} );

		Button mEmailSignInButton = findViewById( R.id.email_sign_in_button );
		mEmailSignInButton.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick( View view )
			{
				attemptLogin();
			}
		} );

		Button mRegisterButton = findViewById( R.id.login_register_button );
		mRegisterButton.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick( View v )
			{
				Intent registerIntent = new Intent( getApplicationContext(), RegisterActivity.class );
				startActivityForResult( registerIntent, MainActivity.REGISTER_REQUEST );
			}
		} );

		mLoginFormView = findViewById( R.id.login_form );
		mProgressView = findViewById( R.id.login_progress );

		appUser = PatientSingleton.getInstance();

	} // onCreate


	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		// Check which request we're responding to
		switch( resultCode )
		{
			// Use this so that if the user starts an activity
			case MainActivity.RESULT_REGISTER_SUCCESSFUL:
				Intent returnData = new Intent();
				returnData.setData( Uri.parse( "registered" ) );
				setResult( MainActivity.RESULT_REGISTER_SUCCESSFUL, returnData );			// Return ok result for activity result
				finish();
				break;

		} // switch

	} // onActivityResult


	private void populateAutoComplete()
	{
		if( !mayRequestContacts() )
		{
			return;
		}

		getLoaderManager().initLoader( 0, null, this );
	}


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
	}


	/**
	 * Callback received when a permissions request has been completed.
	 */
	@Override
	public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions,
											@NonNull int[] grantResults )
	{
		if( requestCode == REQUEST_READ_CONTACTS )
		{
			if( grantResults.length == 1 && grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED )
			{
				populateAutoComplete();
			}
		}
	}


	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	private void attemptLogin()
	{
		if( mAuthTask != null )
		{
			return;
		}

		// Reset errors.
		mEmailView.setError( null );
		mPasswordView.setError( null );

		// Store values at the time of the login attempt.
		String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password, if the user entered one.
		if( !TextUtils.isEmpty( password ) && !isPasswordValid( password ) )
		{
			mPasswordView.setError( getString( R.string.error_invalid_password ) );
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if( TextUtils.isEmpty( email ) )
		{
			mEmailView.setError( getString( R.string.error_field_required ) );
			focusView = mEmailView;
			cancel = true;
		}
		else if( !isEmailValid( email ) )
		{
			mEmailView.setError( getString( R.string.error_invalid_email_password ) );
			focusView = mEmailView;
			cancel = true;
		}

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
			showProgress( true );
			mAuthTask = new UserLoginTask( email, password );
			mAuthTask.execute( (Void) null );
		}

	} // attemptLogin


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
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
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
	}


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
	}


	@Override
	public void onLoaderReset( Loader<Cursor> cursorLoader )
	{

	}


	private void addEmailsToAutoComplete( List<String> emailAddressCollection )
	{
		//Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
		ArrayAdapter<String> adapter =
				new ArrayAdapter<>( LoginActivity.this,
						android.R.layout.simple_dropdown_item_1line, emailAddressCollection );

		mEmailView.setAdapter( adapter );
	}


	private interface ProfileQuery
	{
		String[] PROJECTION = {
				ContactsContract.CommonDataKinds.Email.ADDRESS,
				ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
		};

		int ADDRESS = 0;
		int IS_PRIMARY = 1;
	}


	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, ErrorCode>
	{

		private static final String LOG_TAG = "UserLoginTask";
		private final String mEmail;
		private final String mPassword;

		UserLoginTask( String email, String password )
		{
			mEmail = email;
			mPassword = password;

		} // constructor


		@Override
		protected ErrorCode doInBackground( Void... params )
		{
			try
			{
				// Send the http request and either setup the patient or return null
				return loginAction.attemptLogin( mEmail, mPassword, getApplicationContext() );

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
					appUser.setLoggedIn( true );
					Intent returnData = new Intent();
					returnData.setData( Uri.parse( "logged in" ) );
					setResult( RESULT_OK, returnData );			// Return ok result for activity result
					finish();									// Close the activity
					break;

				case UNKNOWN:									// 1:	Unknown - something went wrong
					mPasswordView.setError( getString( R.string.error_something_went_wrong ) );
					mPasswordView.requestFocus();
					break;

				case INVALID_URL:								// 2:	URL
					mPasswordView.setError( getString( R.string.error_invalid_hostname_or_port ) );
					mPasswordView.requestFocus();
					break;

				case INVALID_EMAIL_PASSWORD:					// 3:	Invalid email/password
					mPasswordView.setError( getString( R.string.error_invalid_email_password ) );
					mPasswordView.requestFocus();
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

