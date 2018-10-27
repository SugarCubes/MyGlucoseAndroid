package com.sugarcubes.myglucose.services;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.activities.MainActivity;
import com.sugarcubes.myglucose.adapters.DataSyncAdapter;
import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;


// TODO: This is experimental, and would be useful for displaying a notification
// TODO: for the app when needed. However, for now there is no need...

public class SyncService extends Service
{
	private final static String LOG_TAG      = "SyncService";
	// An account type, in the form of a domain name
	public static final  String ACCOUNT_TYPE = "com.sugarcubes";
	// The account name
	public static final  String ACCOUNT      = "sugarcubes";
	// Instance fields
	Account mAccount;

	/**
	 * Define a Service that returns an <code><a href="/reference/android/os/IBinder.html">IBinder</a></code> for the
	 * sync adapter class, allowing the sync adapter framework to call
	 * onPerformSync().
	 */
	// Storage for an instance of the sync adapter
	private static       DataSyncAdapter sSyncAdapter     = null;
	// Object to use as a thread-safe lock
	private static final Object          sSyncAdapterLock = new Object();
	ContentResolver mResolver; // A content resolver for accessing the provider

	// A content URI for the content provider's data table
//	Uri doctorsUri, patientsUri;


	@Override
	public void onCreate()
	{
		/*
		 * Create the sync adapter as a singleton.
		 * Set the sync adapter as syncable
		 * Disallow parallel syncs
		 */
		synchronized( sSyncAdapterLock )
		{
			if( sSyncAdapter == null )
			{
				// Returns SyncAdapterBinder in onBind:
				sSyncAdapter = new DataSyncAdapter( getApplicationContext(), true );
			}
		}

		// Create a dummy account for syncing if needed:
		mAccount = CreateSyncAccount();


		// Get the content resolver object for your app
		mResolver = getContentResolver();
		// Construct a URI that points to the content provider data table
//		doctorsUri = new Uri.Builder()
//				.scheme( SCHEME )
//				.authority( AUTHORITY )
//				.path( DB.TABLE_DOCTORS )
//				.build();
//		patientsUri = new Uri.Builder()
//				.scheme( SCHEME )
//				.authority( AUTHORITY )
//				.path( DB.TABLE_PATIENTS )
//				.build();
//		userssUri = new Uri.Builder()
//				.scheme( SCHEME )
//				.authority( AUTHORITY )
//				.path( DB.TABLE_PATIENTS )
//				.build();

		/*
		 * Create a content observer object.
		 * Its code does not mutate the provider, so set
		 * selfChange to "false"
		 */
		TableObserver observer = new TableObserver( new Handler() );

		/*
		 * Register the observer for the data table. The table's path
		 * and any of its subpaths trigger the observer.
		 */
		mResolver.registerContentObserver( MyGlucoseContentProvider.USERS_URI,
				true, new TableObserver( new Handler() ) );
		// Note: shouldn't have to sync doctors *from* Android
//		mResolver.registerContentObserver( MyGlucoseContentProvider.DOCTORS_URI,
//				true, new TableObserver( new Handler() ) );
		mResolver.registerContentObserver( MyGlucoseContentProvider.PATIENTS_URI,
				true, new TableObserver( new Handler() ) );
		mResolver.registerContentObserver( MyGlucoseContentProvider.EXERCISE_ENTRIES_URI,
				true, new TableObserver( new Handler() ) );
		mResolver.registerContentObserver( MyGlucoseContentProvider.GLUCOSE_ENTRIES_URI,
				true, new TableObserver( new Handler() ) );
		mResolver.registerContentObserver( MyGlucoseContentProvider.MEAL_ENTRIES_URI,
				true, new TableObserver( new Handler() ) );

		// https://stackoverflow.com/questions/5253858/why-does-contentresolver-requestsync-not-trigger-a-sync
		ContentResolver.setSyncAutomatically( mAccount, MyGlucoseContentProvider.AUTHORITY, true );

	} // onCreate


	/**
	 * Return an object that allows the system to invoke
	 * the sync adapter.
	 */
	@Override
	public IBinder onBind( Intent intent )
	{
		/*
		 * Get the object that allows external processes
		 * to call onPerformSync(). The object is created
		 * in the base class code when the SyncAdapter
		 * constructors call super()
		 */
		return sSyncAdapter.getSyncAdapterBinder();

	} // onBind


	/**
	 * TableObserver
	 */
	public class TableObserver extends ContentObserver
	{
		private boolean selfChange;

		private TableObserver( Handler handler )
		{
			super( handler );
			selfChange = false;

		} // constructor

		/*
		 * Define a method that's called when data in the
		 * observed content provider changes.
		 * This method signature is provided for compatibility with
		 * older platforms.
		 */
		@Override
		public void onChange( boolean selfChange )
		{
			/*
			 * Invoke the method signature available as of
			 * Android platform version 4.1, with a null URI.
			 */
			onChange( this.selfChange, null );

		} // onChange

		/*
		 * Define a method that's called when data in the
		 * observed content provider changes.
		 */
		@Override
		public void onChange( boolean selfChange, Uri changeUri )
		{
			/*
			 * Ask the framework to run your sync adapter.
			 * To maintain backward compatibility, assume that
			 * changeUri is null.
			 */
			// Note: this automatically finds our DataSyncAdapter in the Manifest and runs it:
			ContentResolver.requestSync( mAccount, MyGlucoseContentProvider.AUTHORITY, new Bundle() );

		} // onChange

	} // TableObserver class


	/**
	 * Create a new dummy account for the sync adapter
	 */
	public Account CreateSyncAccount()
	{
		// Create the account type and default account
		Account newAccount = new Account( ACCOUNT, ACCOUNT_TYPE );
		// Get an instance of the Android account manager
		AccountManager accountManager =
				(AccountManager) getSystemService( ACCOUNT_SERVICE );
		/*
		 * Add the account and account type, no password or user data
		 * If successful, return the Account object, otherwise report an error.
		 */
		if( accountManager.addAccountExplicitly( newAccount, null, null ) )
		{
			/*
			 * If you don't set android:syncable="true" in
			 * in your <provider> element in the manifest,
			 * then call context.setIsSyncable(account, AUTHORITY, 1)
			 * here.
			 */
			Log.i( LOG_TAG, "Account \"" + newAccount.toString()
					+ "\" created SUCCESSFULLY." );
		}
		else
		{
			/*
			 * The account exists or some other error occurred. Log this, report it,
			 * or handle it internally.
			 */
			Log.i( LOG_TAG, "Account \"" + newAccount.toString()
					+ "\" either already exists, or failed..." );
		}

		return newAccount;

	} // CreateSyncAccount

} // class
