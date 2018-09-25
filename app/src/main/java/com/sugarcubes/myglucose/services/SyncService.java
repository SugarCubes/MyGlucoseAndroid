package com.sugarcubes.myglucose.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;


// TODO: This is experimental, and would be useful for displaying a notification
// TODO: for the app when needed. However, for now there is no need...

public class SyncService extends Service
{
	private final static String LOG_TAG		= "SyncService";

	public static final int
			MSG_LOG_GLUCOSE       	= 1000,
			MSG_LOG_EXERCISE    	= 1001,
			MSG_LOG_MEAL     		= 1002,
			MSG_LOGIN				= 1003,
			MSG_REGISTER			= 1004;

	public static final String
			ACTION_START 	= LOG_TAG + ".START", 		// Action to start
			ACTION_STOP		= LOG_TAG + ".STOP", 		// Action to stop
			ACTION_RECONNECT= LOG_TAG + ".RECONNECT";	// Action to reconnect


	// Reference to a Handler, which others can use to send messages to it. This
	// allows for the implementation of message-based communication across
	// processes, by creating a Messenger pointing to a Handler in one process,
	// and handing that Messenger to another process.
	private Handler mConnHandler;	            // Seperate Handler thread for networking

	private AlarmManager mAlarmManager;			// Alarm manager to perform repeating tasks
	private ConnectivityManager mConnectivityManager; // To check for connectivity changes
	// For showing notifications:
	private NotificationManager mNotificationManager;
	private Notification notification;
	private final Messenger mMessenger = new Messenger(
			new IncomingMessageHandler() ); // Target we publish for clients to
	private Timer mTimer = new Timer();

	private static SharedPreferences prefs;

	// send messages to IncomingHandler.
	private static List<Messenger> mClients = new ArrayList<Messenger>();  	// Keeps track of all
																			// current registered clients.



	/**
	 * Service onStartCommand
	 * Handles the action passed via the Intent
	 *
	 * @return START_REDELIVER_INTENT
	 */
	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		super.onStartCommand( intent, flags, startId );

		String action = null;
		if( intent != null )
			action = intent.getAction();

			Log.i(LOG_TAG,"Received action of " + action );

		if( action == null )
		{
			Log.w(LOG_TAG,"Starting service with no action\n Probably from a crash, "
					+ "or service had to restart");
		}
		else
		{
			switch (action)
			{
				case ACTION_START:
					Log.i(LOG_TAG, "Received ACTION_START");
					start();
					break;
				case ACTION_STOP:
					Log.i(LOG_TAG, "Received ACTION_STOP");
					stop();
					break;
			}   // switch

		}   // if...else

		//return START_REDELIVER_INTENT;    // Mqtt version
		return START_STICKY; // Notification version - Run until explicitly stopped.

	} // onStartCommand



	@Nullable
	@Override
	public IBinder onBind( Intent intent )
	{
		return mMessenger.getBinder();

	} // onBind


	private void sendMessageToUI( int intValueToSend )
	{
		for (int i=mClients.size()-1; i>=0; i--)
		{
			try
			{
				// Send data as an Integer
				mClients.get(i).send( Message.obtain( null, intValueToSend, 0, -1 ) );

				//Send data as a String
				// This will be useful when we are able to retrieve messages from the server and
				// display them to the UI.
				//Bundle b = new Bundle();
				//b.putString( "str1", "ab" + intValueToSend + "cd" );
				//b.putInt( intValueToSend );
				//Message msg = Message.obtain( null, MSG_SET_STRING_VALUE );
				//msg.setData(b);
				//mClients.get(i).send(msg);

			}
			catch ( Exception e ) {
				// The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
				mClients.remove(i);
			}

		} // for

	} // sendMessageToUI


	private void sendIntMessageToUI( int switchMessage, int intValueToSend )
	{
		for ( int i=mClients.size()-1; i>=0; i-- )
		{
			try
			{
				// Send data as an Integer
				mClients.get(i).send( Message.obtain( null, switchMessage, intValueToSend, 0 ) );

				//Send data as a String
				//Bundle b = new Bundle();
				//b.putString( "str1", "ab" + intValueToSend + "cd" );
				//b.putInt( intValueToSend );
				//Message msg = Message.obtain( null, MSG_SET_STRING_VALUE );
				//msg.setData(b);
				//mClients.get(i).send(msg);

			}
			catch ( Exception e ) {
				// The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
				mClients.remove(i);
			}

		} // for

	} // sendMessageToUI


	/**
	 * Attempts to listen for Connectivity changes
	 * via ConnectivityManager.CONNECTVITIY_ACTION BroadcastReceiver
	 */
	private synchronized void start()
	{
		registerReceiver(
				mConnectivityReceiver,
				new IntentFilter( ConnectivityManager.CONNECTIVITY_ACTION )
		);

		try
		{
			// Show notification in status bar
			showNotification();
		} catch ( Exception e ) {
			e.printStackTrace();
		}

	} // syncronized start()


	/**
	 * Display a notification in the notification bar.
	 */
	private void showNotification() throws NullPointerException
	{
		int status;
//		status = mqttClient != null ? R.string.connected_to_server
//				: R.string.cannot_connect_to_server;
//		PendingIntent contentIntent = PendingIntent.getActivity( this, 0,
//				new Intent( this, MainActivity.class ), 0 );
//		notification = new Notification.Builder( this )
//				.setContentTitle( this.getString( R.string.service_label ) )
//				.setContentText( getResources().getString( status ) )
//				.setSmallIcon( Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
//						? R.mipmap.pristine_zen_icon_silhouette : R.mipmap.pristine_zen_icon )
//				.setContentIntent( contentIntent )
//				.setAutoCancel( false )
//				.setOngoing( true )
//				.build();
		mNotificationManager =
				(NotificationManager) getApplicationContext().getSystemService( NOTIFICATION_SERVICE );
		//notification.flags |= Notification.FLAG_ONGOING_EVENT;
		//notification.flags |= Notification.FLAG_AUTO_CANCEL;

		mNotificationManager.notify( 123321, notification );
	}


	/**
	 * Attempts to stop the Mqtt client
	 * as well as halting all keep alive messages queued
	 * in the alarm manager
	 */
	private synchronized void stop()
	{
		try
		{
			unregisterReceiver( mConnectivityReceiver );
		}
		catch ( IllegalArgumentException e )
		{
			e.printStackTrace();

		} // try/catch

	} // stop


	/**
	 * Query's the NetworkInfo via ConnectivityManager
	 * to return the current connected state
	 * @return boolean true if we are connected false otherwise
	 */
	private boolean isNetworkAvailable()
	{
		NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();

		return info != null && info.isConnected();
	}


	/**
	 * Receiver that listens for connectivity changes
	 * via ConnectivityManager
	 */
	private final BroadcastReceiver mConnectivityReceiver
			= new BroadcastReceiver()
	{
		@Override
		public void onReceive( Context context, Intent intent )
		{
			Log.i( LOG_TAG, "DEBUG: Data: " + mConnectivityReceiver.getResultData()
					+ "; Received message: " + intent.getExtras().toString() );

		} // onReceive
	};




	/**
	 * Handle incoming messages from MainActivity (UI)
	 */
	private class IncomingMessageHandler extends Handler
	{
		// Handler of
		// incoming messages
		// from other activities.

		private final String LOG_TAG = "INCOMINGMSGHANDLER";

		@Override
		public void handleMessage( Message msg )
		{
			Log.d( LOG_TAG, "S:handleMessage: " + msg.what );

			switch( msg.what )
			{
				case MSG_LOG_GLUCOSE:
					break;

				case MSG_LOG_MEAL:
//					msg.getData().getParcelable(  );
					break;

				case MSG_LOG_EXERCISE:
					break;

				default:
					super.handleMessage( msg );

			}   //switch

		}   // handleMessage

	}   // IncomingMessageHandler

} // class
