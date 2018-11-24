package com.sugarcubes.myglucose.adapters;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.sugarcubes.myglucose.actions.interfaces.ISyncPatientDataAction;
import com.sugarcubes.myglucose.dependencies.Dependencies;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

import static com.sugarcubes.myglucose.activities.MainActivity.DEBUG;

/**
 * Reference:
 * https://developer.android.com/training/sync-adapters/creating-sync-adapter#java
 * <p>
 * The sync adapter component does not automatically do data transfer. Instead, it encapsulates
 * your data transfer code, so that the sync adapter framework can run the data transfer in the
 * background, without involvement from your app. When the framework is ready to sync your
 * application's data, it invokes your implementation of the method onPerformSync().
 */
public class DataSyncAdapter extends AbstractThreadedSyncAdapter
{
	private final String LOG_TAG = getClass().getSimpleName();

	// Get a reference to all our sync actions:
	private ISyncPatientDataAction syncPatientDataAction =
			Dependencies.get( ISyncPatientDataAction.class );

	private Context context;

	/**
	 * Use the constructors to run setup tasks each time your sync adapter component is
	 * created from scratch, just as you use Activity.onCreate() to set up an activity.
	 */
	public DataSyncAdapter( Context context, boolean autoInitialize )
	{
		super( context, autoInitialize );
		this.context = context;

	} // override constructor


	/**
	 * Set up the sync adapter. This form of the
	 * constructor maintains compatibility with Android 3.0
	 * and later platform versions
	 */
	public DataSyncAdapter(
			Context context,
			boolean autoInitialize,
			boolean allowParallelSyncs )
	{
		super( context, autoInitialize, allowParallelSyncs );
		this.context = context;

	} // override constructor


	/**
	 * @param account               - If your server doesn't use accounts, you don't need to use the information
	 *                              in this object.
	 * @param bundle                - contains flags sent by the event that triggered the sync adapter
	 * @param s                     - Usually, the authority corresponds to a content provider in your own app.
	 * @param contentProviderClient - If you're using a content provider to store data for your app,
	 *                              you can connect to the provider with this object. Otherwise,
	 *                              you can ignore it.
	 * @param syncResult            - A SyncResult object that you use to send information to the sync
	 *                              adapter framework.
	 */
	@Override
	public void onPerformSync( Account account, Bundle bundle, String s,
							   ContentProviderClient contentProviderClient, SyncResult syncResult )
	{
		/*
		 * Data transfer code goes here
		 */
		if( DEBUG ) Log.w( LOG_TAG, "***onPerformSync called***" );

		// try/catch handled in the Action method:
		String returnStr = syncPatientDataAction.syncPatientData( context );


	} // onPerformSync


} // class
