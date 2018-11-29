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
import android.app.ActivityManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ShareActionProvider;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.dependencies.Dependencies;
import com.sugarcubes.myglucose.repositories.DbPatientRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IPatientRepository;
import com.sugarcubes.myglucose.services.PedometerService;
import com.sugarcubes.myglucose.services.SyncService;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity
		extends AppCompatActivity
		implements LoaderCallbacks<Cursor>,
		View.OnTouchListener,
		ServiceConnection
{
	public static final boolean DEBUG           = true;              // Activate/deactivate logging
	public final static String  WEBSITE_ADDRESS = "www.legendscomic.com:22222";
	//"www.myglucosetracker.com";

	private final       String           LOG_TAG                    = getClass().getSimpleName();
	private             PatientSingleton patientUser                =
			PatientSingleton.getInstance();
	public static final int              USER_LOADER                = 100;// Loader ID
	public static final int              LOGIN_REQUEST              = 200;// Return code after login
	public static final int              REGISTER_REQUEST           = 300;
	// Return code after register
	public static final int              RESULT_REGISTER_SUCCESSFUL = 101;

	private static final String[] INITIAL_PERMS   = {
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.ACCESS_FINE_LOCATION
	};
	private static final String[] LOCATION_PERMS  = {
			Manifest.permission.ACCESS_FINE_LOCATION
	};
	private static final int      INITIAL_REQUEST = 1337;

	private final Messenger         mMessenger        = new Messenger(
			new IncomingMessageHandler() );         // To communicate with Service
	private       boolean           pIsBound          = false;
	private       ServiceConnection pConnection       = this;
	private       Messenger         pServiceMessenger = null;

	private Menu                menu;                   // Reference to change Login/logout text
	private ShareActionProvider mShareActionProvider;


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


		// NOTE: UNCOMMENT to use location services when tracking steps:
		//if( !canAccessFineLocation() && !canAccessCoarseLocation()
		//		&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
		//{
		//	requestPermissions( INITIAL_PERMS, INITIAL_REQUEST );
		//}

		startServices();

		if( serviceIsRunning( getApplicationContext(), PedometerService.class ) && pIsBound )
		{
			// If the service is running at this point, the user wants to track steps,
			//		so we check the notification status and handle it:
			sendMessageToPedometerService( PedometerService.MSG_NOTIFICATION_STATUS );

		} // if svc running

	} // onCreate


	/**
	 * Called *after* onCreate, and each time we resume the Activity
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		Log.e( LOG_TAG, "Track steps: " + trackSteps( getApplicationContext() ) );
		//		restartServices();

	} // onResume


	// region  -----------------Service Helpers-----------------

	private void startServices()
	{
		if( !serviceIsRunning( getApplicationContext(), SyncService.class ) )
			startService( new Intent( getApplicationContext(), SyncService.class ) );

		// Restart the pedometer service:
		Intent pedometerIntent = new Intent( getApplicationContext(), PedometerService.class );
		pedometerIntent.setAction( PedometerService.ACTION_START );

		// Start the service, only if user wants to track steps:
		if( trackSteps( getApplicationContext() )
				&& !serviceIsRunning( getApplicationContext(), PedometerService.class ) )
		{
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )
				startForegroundService( pedometerIntent );
			else
				startService( pedometerIntent );

			doBindPedometer();     // Bind so we can send messages

		} // if

	} // startServices

	/**
	 * Checks each service's status and restarts if necessary
	 */
	private void restartServices()
	{
		// Restart the sync service:
		if( serviceIsRunning( getApplicationContext(), SyncService.class ) )
			stopService( new Intent( getApplicationContext(), SyncService.class ) );
		startService( new Intent( getApplicationContext(), SyncService.class ) );

		// Restart the pedometer service:
		Intent pedometerIntent = new Intent( getApplicationContext(), PedometerService.class );
		pedometerIntent.setAction( PedometerService.ACTION_START );

		// Stop the service if it is running:
		if( serviceIsRunning( getApplicationContext(), PedometerService.class ) )
		{
			doUnbindPedometer();   // Service won't stop if still bound to UI
			pedometerIntent.setAction( PedometerService.ACTION_STOP );
			stopService( pedometerIntent );

		} // if

		// Start the service again, only if user wants to track steps:
		if( trackSteps( getApplicationContext() ) )
		{
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )
				startForegroundService( pedometerIntent );
			else
				startService( pedometerIntent );

			doBindPedometer();     // Bind so we can send messages

		} // if

	} // restartServices


	private void doBindPedometer()
	{
		try
		{
			Log.d( LOG_TAG, "C:doBindPedometer()" );
			bindService( new Intent( MainActivity.this, PedometerService.class ),
					pConnection, Context.BIND_AUTO_CREATE );
			pIsBound = true;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

	} // doBindPedometer


	private void doUnbindPedometer()
	{
		Log.d( LOG_TAG, "C:doUnbindPedometer()" );

		if( pIsBound )
		{
			sendMessageToPedometerService( PedometerService.MSG_UNREGISTER_CLIENT );
			//if( DEBUG ) Log.d( LOG_TAG, "C: TX MSG_UNREGISTER_CLIENT" );
			unbindService( pConnection );
			pIsBound = false;
		}

	} // doUnbindPedometer

	/**
	 * Handle incoming messages from MqttService
	 */
	private class IncomingMessageHandler extends Handler
	{
		@Override
		public void handleMessage( Message msg )
		{
			if( DEBUG )
				Log.d( LOG_TAG, "C:IncomingHandler:handleMessage: " + msg.what );

			switch( msg.what )
			{
				case PedometerService.MSG_REPORT_STEPS:
					Log.d( LOG_TAG, "C: RX MSG_REPORT_STEPS" );
					//textIntValue.setText("Int Message: " + msg.arg1);
					break;

				case PedometerService.MSG_NOTIFICATION_IS_VISIBLE:
					Log.d( LOG_TAG, "C: RX MSG_NOTIFICATION_IS_VISIBLE" );

					if( msg.getData().getInt( "VALUE" ) > 0 &&
							!showNotification( getApplicationContext() ) )
						sendMessageToPedometerService( PedometerService.MSG_HIDE_NOTIFICATION );

					else if( showNotification( getApplicationContext() ) )
						sendMessageToPedometerService( PedometerService.MSG_SHOW_NOTIFICATION );

					break;

				default:
					super.handleMessage( msg );

			} // switch

		} // handleMessage

	} // IncomingMessageHandler


	@Override
	public void onServiceConnected( ComponentName componentName, IBinder iBinder )
	{
		if( DEBUG ) Log.d( LOG_TAG, "C: Service: " + componentName.getClassName()
				+ "; TX MSG_REGISTER_CLIENT" );

		if( componentName.getClassName().equals( PedometerService.class.getName() ) )
		{
			pServiceMessenger = new Messenger( iBinder );

			// Register activity for updates, if we ever need to receive them:
			sendMessageToPedometerService( PedometerService.MSG_REGISTER_CLIENT );

			// Tell the service to display the notification if needed:
			if( showNotification( getApplicationContext() ) )
				sendMessageToPedometerService( PedometerService.MSG_SHOW_NOTIFICATION );
			else
				sendMessageToPedometerService( PedometerService.MSG_HIDE_NOTIFICATION );

		}

		if( DEBUG ) Log.d( LOG_TAG, "C: Service: " + componentName.toString()
				+ "; TX MSG_REGISTER_CLIENT" );

	} // onServiceConnected


	@Override
	public void onServiceDisconnected( ComponentName componentName )
	{
		Log.d( LOG_TAG, "C:onServiceDisconnected()" );
		if( componentName.getClassName().equals( "com.myglucose.services.PedometerService" ) )
			pServiceMessenger = null;

	} // onServiceDisconnected


	/**
	 * Sends data to the Mqtt service
	 *
	 * @param intValueToSend:
	 */
	protected void sendMessageToPedometerService( int intValueToSend )
	{
		// null, msg.what [int used in switch], arg1, 0
		Message msg = Message.obtain( null, intValueToSend, 0, -1 );
		msg.replyTo = mMessenger;

		if( pIsBound && pServiceMessenger != null )
			try
			{
				pServiceMessenger.send( msg );
			}
			catch( RemoteException e )
			{
				e.printStackTrace();
			}

	    /*
	    for( Messenger serviceMessenger : serviceMessengers )
	    {
		    try
		    {
			    serviceMessenger.send( msg );
		    }
		    catch ( RemoteException e )
		    {
			    e.printStackTrace();
		    }
	    } //*/

	} // sendMessageToPedometerService


	protected static boolean serviceIsRunning( Context context, Class<?> serviceClass )
	{
		ActivityManager manager =
				(ActivityManager) context.getSystemService( Context.ACTIVITY_SERVICE );

		for( ActivityManager.RunningServiceInfo service : manager.getRunningServices( Integer.MAX_VALUE ) )
		{
			if( serviceClass.getName().equals( service.service.getClassName() ) )
			{
				return true;
			} // if

		} // for

		return false;

	} // serviceIsRunning

	// endregion -----------------Service Helpers-----------------


	// region -----------------Preference Helpers-----------------

	/**
	 * Gets a boolean value from SharedPreferences using a key
	 *
	 * @param key: Key
	 */
	public static boolean getPreferenceBoolean( Context context, String key ) // default = 0
	{
		SharedPreferences sharedPreferences
				= PreferenceManager.getDefaultSharedPreferences( context );
		return sharedPreferences.getBoolean( key, false );

	} // getPreferenceBoolean


	/**
	 * Gets an int from SharedPreferences using a key
	 *
	 * @param key: Key
	 */
	public static int getPreferenceInt( Context context, String key ) // default = 0
	{
		SharedPreferences sharedPreferences
				= PreferenceManager.getDefaultSharedPreferences( context );
		return sharedPreferences.getInt( key, 0 );

	} // getPreferenceInt

	public static boolean showNotification( Context context )
	{
		return getPreferenceBoolean( context, SettingsActivity.PREF_SHOW_NOTIFICATION );

	} // showNotification


	public static boolean trackSteps( Context context )
	{
		return getPreferenceBoolean( context, SettingsActivity.PREF_TRACK_STEPS );

	} // showNotification

	// endregion -----------------Preference Helpers-----------------


	// region -----------------Overrides-----------------

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.main, menu );

		this.menu = menu;

		// A mixture of solutions at:
		// https://stackoverflow.com/questions/18374183/how-to-show-icons-in-overflow-menu-in-actionbar/32523930

		// Force the menu to show the icons:
		if( menu instanceof MenuBuilder )
		{
			Method m;
			try
			{
				m = menu.getClass().getDeclaredMethod(
						"setOptionalIconsVisible", Boolean.TYPE );
				m.setAccessible( true );
				m.invoke( menu, true );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}

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
				if( patientUser.isLoggedIn() )
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
				if( patientUser.isLoggedIn() )
				{
					startViewProfileActivity();
				}
				else
				{
					startLoginActivity();
				}
				break;

			case R.id.action_share:// Fetch and store ShareActionProvider
				//				mShareActionProvider = (ShareActionProvider) item.getActionProvider();
				Intent sharingIntent = new Intent( android.content.Intent.ACTION_SEND );
				sharingIntent.setType( "text/plain" );
				String shareBody = "Hi, check out MyGlucose, a good application for you and your " +
						"doctor to track your health!\nhttp://" + WEBSITE_ADDRESS;
				sharingIntent.putExtra( android.content.Intent.EXTRA_SUBJECT, "MyGlucose Health Tracker" );
				sharingIntent.putExtra( android.content.Intent.EXTRA_TEXT, shareBody );
				startActivity( Intent.createChooser( sharingIntent, "Share via" ) );
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
				if( patientUser.isLoggedIn() )
				{
					if( event.getAction() == MotionEvent.ACTION_UP )    // Only handle single event
					{
						Intent glucoseIntent = new Intent( this, LogGlucoseActivity.class );
						startActivity( glucoseIntent );
					}
				}
				else
				{
					startLoginActivity();
				}
				break;

			case R.id.meals_button:                                    // Meals button tap
				if( DEBUG ) Log.d( LOG_TAG, "Meals button tapped" );
				if( patientUser.isLoggedIn() )
				{
					if( event.getAction() == MotionEvent.ACTION_UP )    // Only handle single event
					{
						Intent mealsIntent = new Intent( this, LogMealActivity.class );
						startActivity( mealsIntent );
					}
				}
				else
				{
					startLoginActivity();
				}
				break;

			case R.id.exercise_button:                                // Exercise button tap
				if( DEBUG ) Log.d( LOG_TAG, "Exercise button tapped" );
				if( patientUser.isLoggedIn() )
				{
					if( event.getAction() == MotionEvent.ACTION_UP )    // Only handle single event
					{
						Intent exerciseIntent = new Intent( this, LogExerciseActivity.class );
						startActivity( exerciseIntent );
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

	// endregion -----------------Overrides-----------------


	// region -----------------Activity Helpers-----------------

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

	// endregion -----------------Activity Helpers-----------------


	// region -----------------UNUSED-----------------

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

	// endregion -----------------UNUSED-----------------

} // class
