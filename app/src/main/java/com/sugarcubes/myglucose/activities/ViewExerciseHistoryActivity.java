package com.sugarcubes.myglucose.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.adapters.ExerciseCursorAdapter;
import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;

import static com.sugarcubes.myglucose.activities.MainActivity.DEBUG;

public class ViewExerciseHistoryActivity extends AppCompatActivity
		implements LoaderManager.LoaderCallbacks<Cursor>
{
	private final String LOG_TAG = getClass().getSimpleName();
	int loaderIndex = 33333;
	CursorAdapter viewCursorAdapter;
	Cursor        cursor;


	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_view_history_3_columns );

		setTitle( getString( R.string.title_activity_exercise_entry ) + " History" );

		// Initialize loader to handle calls to ContentProvider
		getSupportLoaderManager().initLoader( loaderIndex, null, this );

		// Set all the header texts:
		TextView header1 = findViewById( R.id.header1 );
		header1.setText( R.string.exercise_name );

		TextView header2 = findViewById( R.id.header2 );
		header2.setText( R.string.minutes );

		TextView header3 = findViewById( R.id.header3 );
		header3.setText( R.string.date );

	} // onCreate


	/**
	 * To be used if data changes (item is deleted from, added to database, etc)
	 */
	public void loaderReset()
	{
		if( viewCursorAdapter != null && cursor != null )
		{
			viewCursorAdapter.notifyDataSetChanged();
			//viewCursorAdapter.getCursor().requery();	// cursor is null
			viewCursorAdapter.changeCursor( cursor );
		}

		try
		{
			getLoaderManager().getLoader( loaderIndex ).forceLoad();
			getLoaderManager().notify();
		}
		catch( Exception e )
		{
			Log.i( "LOADER", "Loader not initialized. Not forcing load.\n"
					+ e.getMessage() );
		}

		getContentResolver().notifyChange(
				MyGlucoseContentProvider.EXERCISE_ENTRIES_URI, null );

	} // loaderReset


	/**
	 * Shows the ListView and sets the adapter
	 */
	private void showListview()
	{
		if( DEBUG ) Log.d( LOG_TAG, "Showing ListView" );
		ListView listView = findViewById( R.id.listView );
		listView.setVisibility( View.VISIBLE );

		// Set the adapter
		if( viewCursorAdapter != null )
			listView.setAdapter( viewCursorAdapter );
		else
			Log.e( LOG_TAG, "viewCursorAdapter is null!!" );

		View emptyView = findViewById( R.id.empty_view );
		emptyView.setVisibility( View.GONE );
		View headerView = findViewById( R.id.header );
		headerView.setVisibility( View.VISIBLE );

	} // showListview


	/**
	 * Hides ListView and shows the empty view
	 */
	private void showEmpty()
	{
		if( DEBUG ) Log.d( LOG_TAG, "Showing empty" );
		ListView listView = findViewById( R.id.listView );
		listView.setVisibility( View.GONE );
		View emptyView = findViewById( R.id.empty_view );
		emptyView.setVisibility( View.VISIBLE );
		View headerView = findViewById( R.id.header );
		headerView.setVisibility( View.GONE );

	} // showEmpty


	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader( int id, @Nullable Bundle args )
	{
		return new CursorLoader( getApplicationContext(),
				MyGlucoseContentProvider.EXERCISE_ENTRIES_URI,
				null, DB.KEY_EXERCISE_STEPS + "< ?",
				new String[]{ String.valueOf( 1 ) },
				DB.KEY_UPDATED_AT + " ASC" );

	} // onCreateLoader


	@Override
	public void onLoadFinished( @NonNull android.support.v4.content.Loader<Cursor> loader, Cursor data )
	{
		this.cursor = data;

		if( DEBUG ) Log.d( LOG_TAG, "Cursor count: " + data.getCount() );

		viewCursorAdapter = new ExerciseCursorAdapter( getApplicationContext(), cursor );

		if( data.getCount() > 0 )
			showListview();
		else
			showEmpty();

		viewCursorAdapter.swapCursor( cursor );

		if( cursor != null && !cursor.isClosed() )
			synchronized( cursor )
			{
				cursor.notify();
			}

	} // onLoadFinished


	@Override
	public void onLoaderReset( @NonNull android.support.v4.content.Loader<Cursor> loader )
	{
		//		loaderReset();

	} // onLoaderReset

} // class
