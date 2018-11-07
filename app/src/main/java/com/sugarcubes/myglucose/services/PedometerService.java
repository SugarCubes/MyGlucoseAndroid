package com.sugarcubes.myglucose.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.activities.MainActivity;
import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.dependencies.Dependencies;
import com.sugarcubes.myglucose.entities.ExerciseEntry;
import com.sugarcubes.myglucose.repositories.interfaces.IExerciseEntryRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IPatientRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import static com.sugarcubes.myglucose.activities.MainActivity.DEBUG;

public class PedometerService extends Service implements SensorEventListener
{
	public static final String LOG_TAG               = "PedometerService";
	public static final String ACTION_START          = LOG_TAG + ".Start";
	public static final String ACTION_STOP           = LOG_TAG + ".Stop";
	public static final String ACTION_KEEPALIVE      = LOG_TAG + ".KeepAlive";
	public static final int    MSG_REGISTER_CLIENT   = 2000;
	public static final int    MSG_UNREGISTER_CLIENT = 2001;
	public static final int    MSG_REPORT_STEPS      = 2002;

	private final  Messenger       mMessenger = new Messenger(
			new IncomingMessageHandler() ); // Target we publish for clients to
	private static List<Messenger> mClients   = new ArrayList<>();

	// For showing notifications:
	private NotificationManager mNotificationManager;
	private Notification        notification;

	private SensorManager mSensorManager;
	private Sensor        mStepCounterSensor;
	private Sensor        mStepDetectorSensor;
	private static boolean running = false;

	LocationListener[] mLocationListeners = new LocationListener[]{
			new LocationListener( LocationManager.GPS_PROVIDER ),
			new LocationListener( LocationManager.NETWORK_PROVIDER )
	}; // Implemented as private inner class

	// LOCATION fields
	private              LocationManager mLocationManager  = null;
	private static final int             LOCATION_INTERVAL = 600000;     // Milliseconds
	private static final float           LOCATION_DISTANCE = 10f;        // Meters

	private AlarmManager mAlarmManager;         // Alarm manager to perform repeating tasks
	private Timer        mTimer;


	private static int currentHour;
	private static int currentDay;
	private static int currentLogHour;
	private static int currentLogDay;
	private static int hourlySteps    = 0;
	private static int dailySteps     = 0;
	private static int lastHourSteps  = 0;
	private static int lastDaySteps   = 0;
	private static int milestoneSteps = 0;


	private static final int    notificationId        = 123321;
	private static final String notificationChannelId = "com.sugarcubes.myglucose.steps";
	private              String status                = "Steps dayString: " + dailySteps;
	private Calendar calendar;


	@Override
	public void onCreate()
	{
		super.onCreate();

		/*
		 * Only start up the service if the user's device has Kitkat or above
		 */
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
		{
			Log.i( LOG_TAG, "Kitkat or newer detected. Starting sensor detection." );

			requestStepSensorOrStopService();


			requestLocationUpdates();


			// If the service is still running, assign the time variables:
			currentHour = getHour( 0 );
			currentDay = getDay( 0 );

			currentLogHour = currentHour;
			currentLogDay = currentDay;

			if( mTimer == null )
				mTimer = new Timer();
			else
				mTimer.cancel();

			running = true;

		} // SDK check

	} // onCreate


	@Nullable
	@Override
	public IBinder onBind( Intent intent )
	{
		return mMessenger.getBinder();

	} // onBind


	@Override
	public void onSensorChanged( SensorEvent sensorEvent )
	{
		running = true;
		Sensor sensor = sensorEvent.sensor;

		if( sensor.getType() == Sensor.TYPE_STEP_COUNTER )
		{
			calculateDailySteps( sensorEvent );

		}
		else if( sensor.getType() == Sensor.TYPE_STEP_DETECTOR )
		{
			calculateHourlySteps();

		} // if*/

		// Set the current steps in the notification:
		updateNotification();

	} // onSensorChanged


	@Override
	public void onAccuracyChanged( Sensor sensor, int i )
	{
		// No implementation
	}


	@Override
	public void onDestroy()
	{
		super.onDestroy();
		// Deactivate pedometer listeners
		// Android Developer site suggested not un-registering listeners:
		//		mSensorManager.unregisterListener( this, mStepCounterSensor );
		//		mSensorManager.unregisterListener( this, mStepDetectorSensor );

		if( mTimer != null )
			mTimer.cancel();

		// Stop the Mqtt receiver
		try
		{
			if( mNotificationManager != null )
				mNotificationManager.cancelAll(); // Cancel the persistent notification.
		}
		catch( Exception e )
		{
			if( DEBUG ) e.printStackTrace();
		}

	} // onDestroy


	/**
	 * This will be called when the alarmManager wakes the service (can be set up to
	 * be called from other activities/services if needed)
	 *
	 * @param intent  - intent
	 * @param flags   - flags
	 * @param startId - startId
	 * @return int
	 */
	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		super.onStartCommand( intent, flags, startId );

		if( DEBUG ) Log.i( LOG_TAG, "onStartCommand" );

		String action = null;
		if( intent != null ) action = intent.getAction();

		if( DEBUG && action != null ) Log.i( LOG_TAG, "Received action of " + action );

		if( action == null )
		{
			if( DEBUG )
				Log.e( LOG_TAG, "Starting service with no action." +
						"\nThis usually occurs after a crash or service restart" );
		}
		else
		{
			switch( action )
			{
				case ACTION_KEEPALIVE:
					if( DEBUG ) Log.d( LOG_TAG, "Received ACTION_KEEPALIVE" );

					IPatientRepository patientRepository
							= Dependencies.get( IPatientRepository.class );

					// ALWAYS load the latest data:
					Cursor cursor = getApplicationContext().getContentResolver().query(
							MyGlucoseContentProvider.PATIENT_USERS_URI, null,
							DB.KEY_USER_LOGGED_IN + "=?",
							new String[]{ String.valueOf( 1 ) }, null
					);
					patientRepository.readFromCursor( PatientSingleton.getInstance(), cursor );

					// Log steps every time the alarm manager calls the KEEPALIVE action:
					logAllSteps();
					showNotification();
					break;

				case ACTION_START:
					if( DEBUG ) Log.d( LOG_TAG, "Received ACTION_START" );
					if( mStepCounterSensor != null || mStepDetectorSensor != null )
						showNotification();
					break;

				default:
					if( DEBUG ) Log.d( LOG_TAG, "Unknown startCommand action..." );

			}   // switch

		}   // if...else

		return START_STICKY;

	} // onStartCommand


	/**
	 * If step sensor service is not available, the service won't continue.
	 */
	private void requestStepSensorOrStopService()
	{
		// Get a reference to the Android sensor service
		mSensorManager = (SensorManager)
				getSystemService( Context.SENSOR_SERVICE );

		// Now get a reference to the step counter (if it exists)
		mStepCounterSensor = mSensorManager != null
				? mSensorManager
				.getDefaultSensor( Sensor.TYPE_STEP_COUNTER )
				: null;

		// and a reference to the step detector (if it exists)
		mStepDetectorSensor = mSensorManager != null
				? mSensorManager
				.getDefaultSensor( Sensor.TYPE_STEP_DETECTOR )
				: null;

		// Register a listener to each of the step sensors
		if( mSensorManager != null )
		{
			mSensorManager.registerListener( this, mStepCounterSensor,
					SensorManager.SENSOR_DELAY_FASTEST );
			mSensorManager.registerListener( this, mStepDetectorSensor,
					SensorManager.SENSOR_DELAY_FASTEST );
		}
		else
			// If the device doesn't have a sensor, stop the service:
			this.stopSelf();


		// If the device has a step sensor of some kind, start a
		//      step counter to report back every so often.
		if( mStepCounterSensor != null || mStepDetectorSensor != null )
		{
			try
			{
				// Start the timers...
				mAlarmManager = (AlarmManager) getApplicationContext()
						.getSystemService( Context.ALARM_SERVICE );
				startLogTimer();
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}

		}   // if device has a step sensor

	} // requestStepSensorOrStopService


	/**
	 * If location is available on the device, request updates
	 */
	private void requestLocationUpdates()
	{
		// Initialize location
		if( mLocationManager == null )
		{
			mLocationManager = (LocationManager) getApplicationContext()
					.getSystemService( Context.LOCATION_SERVICE );
		}

		// Try to initialize the wireless location listener first (lower priority):
		try
		{
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
					mLocationListeners[ 1 ] );
		}
		catch( SecurityException ex )
		{
			Log.i( LOG_TAG, "fail to request location update, ignore", ex );
		}
		catch( IllegalArgumentException ex )
		{
			Log.d( LOG_TAG, "network provider does not exist, " + ex.getMessage() );
		}

		// Try to initialize the GPS listener after wireless location (higher priority):
		try
		{
			mLocationManager.requestLocationUpdates(
					//LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
					LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
					mLocationListeners[ 0 ] );
		}
		catch( SecurityException ex )
		{
			Log.i( LOG_TAG, "fail to request location update, ignore", ex );
		}
		catch( IllegalArgumentException ex )
		{
			Log.d( LOG_TAG, "gps provider does not exist " + ex.getMessage() );
		} //*/

	} // requestLocationUpdates


	/**
	 * Schedules keep-alive requests via a PendingIntent in the Alarm Manager
	 */
	private void startLogTimer()
	{
		// Get the next hour
		calendar = Calendar.getInstance();
		int nextHour = getHour( 1 );
		if( nextHour > 24 )
			nextHour = 0;
		calendar.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ),
				calendar.get( Calendar.DAY_OF_MONTH ), nextHour, 0 );
		long startTime = calendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();

		if( DEBUG ) Log.i( LOG_TAG, "Timer start time: "
				+ System.currentTimeMillis() + startTime );

		Intent intent = new Intent();                               // Start an intent
		intent.setClass( this, getClass() );            // Get a class reference
		intent.setAction( ACTION_KEEPALIVE );                       // Action to run
		PendingIntent pendingIntent =
				PendingIntent.getService(     // Pass the intent to pendingIntent
						this, 0, intent, 0 );
		// Set the service to wake up every interval to show notification
		mAlarmManager.setInexactRepeating( AlarmManager.RTC_WAKEUP, startTime,
				AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent );

	} // startLogTimer


	/**
	 * Gets the numeric representation [0-23] of the hour at the offset specified
	 *
	 * @param minuteOffset: Positive or negative offset from the current hour
	 */
	private int getMinute( int minuteOffset )
	{
		calendar = Calendar.getInstance();
		calendar.setTime( new Date() );
		if( minuteOffset != 0 )
			calendar.add( Calendar.MINUTE, minuteOffset );            // Subtract an hour
		return calendar.get( Calendar.MINUTE );

	} // getHour


	/**
	 * Gets the numeric representation [0-23] of the hour at the offset specified
	 *
	 * @param hourOffset: Positive or negative offset from the current hour
	 */
	private int getHour( int hourOffset )
	{
		calendar = Calendar.getInstance();
		calendar.setTime( new Date() );
		if( hourOffset != 0 )
			calendar.add( Calendar.HOUR_OF_DAY, hourOffset );            // Subtract an hour
		return calendar.get( Calendar.HOUR_OF_DAY );

	} // getHour


	/**
	 * Gets the numeric representation of the day of the month at the offset specified
	 *
	 * @param dayOffset: Positive or negative offset from the current hour
	 */
	private int getDay( int dayOffset )
	{
		calendar = Calendar.getInstance();
		calendar.setTime( new Date() );
		calendar.add( Calendar.DAY_OF_MONTH, dayOffset );            // Subtract an hour
		return calendar.get( Calendar.DAY_OF_MONTH );

	} // getYesterday


	/**
	 * Calculates daily steps each time it is called. Doesn't always get called on each step.
	 *
	 * @param sensorEvent: The SensorEvent object
	 */
	private void calculateDailySteps( SensorEvent sensorEvent )
	{
		float[] values = sensorEvent.values;
		int stepsSinceReboot = -1;

		if( values.length > 0 )
			stepsSinceReboot = (int) values[ 0 ];

		// TODO: Test
		// Adapted from: https://stackoverflow.com/questions/42661678/android-how-to-get-the-sensor-step-counter-data-only-one-day
		if( currentDay == getDay( 0 ) )
		{
			// Get the saved value from prefs
			dailySteps = getPreferenceInt( dayString( 0 ) ); // Get today's steps
			if( milestoneSteps < 1 )
				milestoneSteps = stepsSinceReboot;                   // Initialize milestoneSteps
			// Subtract any steps accounted for previously and save:
			int additionalSteps = stepsSinceReboot - milestoneSteps; // Subtract logged steps
			dailySteps += additionalSteps;

		}
		else
		{
			// If the day has changed, we need to do some housekeeping. The current day integer
			//		will change. Then, we reset the daily steps for today.
			currentDay = getDay( 0 );                           // Reset current day
			dailySteps = 1;                                     // Reset daily steps

		} // if...else

		// Save dayString's steps. The key is to always save the steps using *today's*
		//	    preference string. We will retrieve *yesterday's* preference string
		//      when we log the steps.
		savePreferenceInt( dayString( 0 ), dailySteps );

		// Use milestoneSteps to Subtract current total from the total on next iteration:
		milestoneSteps = stepsSinceReboot;

		if( DEBUG ) Log.d( LOG_TAG, "Step Counter Detected : " + dailySteps
				+ "; Steps since reboot: " + stepsSinceReboot );

	} // calculateDailySteps


	/**
	 * Calculates hourly steps each time it is called. Typically called when a step is detected.
	 */
	// TODO: Test
	private void calculateHourlySteps()
	{
		if( DEBUG ) Log.d( LOG_TAG, "Hourly Steps: " + hourlySteps );

		if( currentHour == getHour( 0 ) )
			hourlySteps++;
		else                                                    // Handle hour change
		{
			currentHour = getHour( 0 );                         // Reset current hour
			lastHourSteps = hourlySteps;                        // Save to be logged
			hourlySteps = 1;                                    // Reset hourly steps

		} // if

	} // calculateHourlySteps


	/**
	 * Saves an int in SharedPreferences using a key/value pair
	 *
	 * @param key:   Key
	 * @param value: Value
	 */
	private void savePreferenceInt( String key, int value )
	{
		SharedPreferences sharedPreferences
				= PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt( key, value );
		editor.apply();

	} // savePreferenceInt


	/**
	 * Gets an int from SharedPreferences using a key
	 *
	 * @param key: Key
	 */
	private int getPreferenceInt( String key ) // default = 0
	{
		SharedPreferences sharedPreferences
				= PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
		return sharedPreferences.getInt( key, 0 );

	} // getPreferenceInt


	/**
	 * Gets a string representation of dayString's date for indexing purposes in SharedPreferences
	 */
	public String dayString( int dayOffset )
	{
		calendar = Calendar.getInstance();
		calendar.setTime( new Date() );
		if( dayOffset != 0 )
			calendar.add( Calendar.DAY_OF_MONTH, dayOffset );            // Subtract an hour
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd", Locale.US );
		return sdf.format( calendar.getTime() );

	} // dayString


	/**
	 * Called in onStartCommand, which is called by the AlarmManager.
	 * This gets the location of the user, and logs the date, time,
	 * steps, and location.
	 */
	public void logAllSteps()
	{
		running = true;

		if( DEBUG )
			Log.d( LOG_TAG, "T:KeepAlive():Timer doing work. Hourly steps: " + hourlySteps );

		if( mStepCounterSensor != null || mStepDetectorSensor != null )
		{
			try
			{
				// Log the ExerciseEntry:
				logDailySteps();


				// Only log steps when hour change detected:
				logHourlySteps();

			}
			catch( Throwable t )
			{
				// you should always ultimately catch all
				// exceptions in timer tasks.
				if( DEBUG ) Log.e( "TimerTick", "Timer Tick Failed.", t );

			} // try/catch

		} // if sensors not null

	}   // logAllSteps


	/**
	 * Logs daily steps in the database
	 */
	private void logDailySteps()
	{
		// We first retrieve *yesterday's* steps:
		lastDaySteps = getPreferenceInt( dayString( -1 ) );

		// TODO: TEST:
		//if( lastHourSteps > 0 && currentLogHour != getHour( 0 ) ) // Test hourly
		if( lastDaySteps > 0 && currentLogDay != getDay( 0 ) )
		{
			new Runnable()
			{
				@Override
				public void run()
				{
					// Enter the daily steps into the database:
					IExerciseEntryRepository exerciseEntryRepository
							= Dependencies.get( IExerciseEntryRepository.class );
					ExerciseEntry entry = new ExerciseEntry();  // Make a new entry
					entry.setUserName( PatientSingleton.getInstance().getUserName() );
					entry.setMinutes( 0 );
					entry.setSteps( lastDaySteps );
					entry.setExerciseName( DB.KEY_EXERCISE_STEPS );
					Date created = new Date();
					entry.setCreatedAt( created );
					entry.setUpdatedAt( created );
					exerciseEntryRepository.create( entry );    // Create in the database

				} // run

			}.run(); // runnable

			// Only reset the current day after we've logged previous one:
			currentLogDay = getDay( 0 );
			//			currentLogHour = getHour( 0 );    // Remove after testing

		} // if day has passed

	} // logDailySteps


	/**
	 * Logs steps every hour
	 */
	private void logHourlySteps()
	{
		// TODO: TEST:
		double coordX = -1;
		double coordY = -1;

		// Update the user's location:
		for( LocationListener locationListener : mLocationListeners )
		{
			if( locationListener != null )
			{
				if( locationListener.mLastLocation.getLongitude() > 0 )
					coordX = locationListener.mLastLocation.getLongitude();
				if( locationListener.mLastLocation.getLatitude() > 0 )
					coordY = locationListener.mLastLocation.getLatitude();

			} // if

		} // for

		int thisHour = getHour( 0 );

		if( lastHourSteps > 0 && currentLogHour != thisHour )
		{
			// TODO: Create PedometerRepository for this:
			// Log the Pedometer steps:
			ContentValues stepValues = new ContentValues();
			String createdAt = new Date().toString();
			stepValues.put( DB.KEY_CREATED_AT, createdAt );
			stepValues.put( DB.KEY_UPDATED_AT, createdAt );
			stepValues.put( DB.KEY_PED_COORD_X, coordX );
			stepValues.put( DB.KEY_PED_COORD_Y, coordY );
			// Always log the *previous* hour's steps
			stepValues.put( DB.KEY_PED_STEP_COUNT, lastHourSteps );
			getContentResolver().insert( MyGlucoseContentProvider.PEDOMETER_URI, stepValues );

			currentLogHour = thisHour;

		} // if hour has passed

	} // logHourlySteps


	private Notification getNotification( String text )
	{
		if( DEBUG ) Log.d( LOG_TAG, "getNotification called..." );

		// The PendingIntent to launch our activity if the user selects
		// this notification
		PendingIntent contentIntent = PendingIntent.getActivity( this, 0,
				new Intent( this, MainActivity.class ), 0 );

		Notification.Builder builder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
				? new Notification.Builder( this, notificationChannelId )
				: new Notification.Builder( this );

		Notification notification =
				builder.setContentTitle( getString( R.string.service_label ) )
						.setContentText( text )
						.setSmallIcon( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
								? R.mipmap.icon_single_cube
								: R.mipmap.ic_launcher )
						.setOnlyAlertOnce( true )
						.setDefaults( Notification.DEFAULT_ALL )
						.setContentIntent( contentIntent )
						.setAutoCancel( false )
						.setPriority( Notification.PRIORITY_MAX )
						.setOngoing( true )
						.build();
		return notification;

	} // getNotification

	/**
	 * Display a notification in the notification bar.
	 * NOTE:
	 */
	private void showNotification()
	{
		if( DEBUG ) Log.d( LOG_TAG, "showNotification called..." );

		notification = getNotification( getPreferenceInt( dayString( 0 ) ) + " Steps Today" );

		mNotificationManager =
				(NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );

		//startForeground( notificationId, notification);
		//NotificationManagerCompat.from( this );
		//(NotificationManager) getSystemService( NOTIFICATION_SERVICE );
		//notification.flags |= Notification.FLAG_ONGOING_EVENT;
		//notification.flags |= Notification.FLAG_AUTO_CANCEL;
		if( mNotificationManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )
		{
			CharSequence name = getString( R.string.service_label );
			NotificationChannel notificationChannel = new NotificationChannel(
					notificationChannelId,
					name,
					NotificationManager.IMPORTANCE_HIGH );
			notificationChannel.setSound( null, null );
			//			notificationChannel.enableLights( true );
			//			notificationChannel.setLightColor( Color.RED );
			//			notificationChannel.setShowBadge( true );
			notificationChannel.setLockscreenVisibility( Notification.VISIBILITY_PUBLIC );

			mNotificationManager.createNotificationChannel( notificationChannel );
		}

		if( mNotificationManager != null )
			mNotificationManager.notify( notificationId, notification );
		else if( DEBUG )
			Log.e( LOG_TAG, "Error getting notification manager..." );

	} // showNotification


	/**
	 * This is the method that can be called to update the Notification
	 */
	private void updateNotification()
	{
		//		if( DEBUG ) Log.d( LOG_TAG, "updateNotification called..." );

		Notification notification =
				getNotification( getPreferenceInt( dayString( 0 ) ) + " Steps Today" );

		if( mNotificationManager == null )
			mNotificationManager =
					(NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
		if( mNotificationManager != null )
		{
			mNotificationManager.notify( notificationId, notification );
		}

	} // updateNotification


	/**
	 * Handle incoming messages from other activities (UI)
	 */
	private class IncomingMessageHandler extends Handler
	{
		private String LOG_TAG = "INCOMINGMSGHANDLER";

		@Override
		public void handleMessage( Message msg )
		{
			if( MainActivity.DEBUG ) Log.d( LOG_TAG, "S:handleMessage: " + msg.what );

			switch( msg.what )
			{
				// NOTE: msg.getData() returns a Bundle
				case MSG_REGISTER_CLIENT:
					Log.d( LOG_TAG, "S: RX MSG_REGISTER_CLIENT:mClients.add(msg.replyTo) " );
					mClients.add( msg.replyTo );
					break;

				case MSG_UNREGISTER_CLIENT:
					Log.d( LOG_TAG, "S: RX MSG_REGISTER_CLIENT:mClients.remove(msg.replyTo) " );
					mClients.remove( msg.replyTo );
					break;

				default:
					super.handleMessage( msg );
			}   //switch

		}   // handleMessage

	}   // IncomingMessageHandler


	// Adapted from:
	// http://stackoverflow.com/questions/8828639/android-get-gps-location-via-a-service
	private class LocationListener implements android.location.LocationListener
	{
		Location mLastLocation;

		public LocationListener( String provider )
		{
			Log.i( LOG_TAG, "LocationListener: " + provider );
			mLastLocation = new Location( provider );
		}

		@Override
		public void onLocationChanged( Location location )
		{
			Log.i( LOG_TAG, "onLocationChanged: " + location );
			mLastLocation.set( location );
		}

		@Override
		public void onProviderDisabled( String provider )
		{
			Log.i( LOG_TAG, "onProviderDisabled: " + provider );
		}

		@Override
		public void onProviderEnabled( String provider )
		{
			Log.i( LOG_TAG, "onProviderEnabled: " + provider );
		}

		@Override
		public void onStatusChanged( String provider, int status, Bundle extras )
		{
			Log.i( LOG_TAG, "onStatusChanged: " + provider );
		}

	} // LocationListener

} // class
