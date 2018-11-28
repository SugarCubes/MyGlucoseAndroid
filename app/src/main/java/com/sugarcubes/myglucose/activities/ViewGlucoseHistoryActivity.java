package com.sugarcubes.myglucose.activities;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.adapters.GlucoseCursorAdapter;
import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;

public class ViewGlucoseHistoryActivity extends AppCompatActivity
		implements LoaderManager.LoaderCallbacks<Cursor>
{
	int loaderIndex = 53535;
	CursorAdapter viewCursorAdapter;
	Cursor        cursor;


	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_view_history_4_columns );

		// Initialize loader to handle calls to ContentProvider
		getSupportLoaderManager().initLoader( loaderIndex, null, this );

		// Set all the header texts:
		TextView header1 = findViewById( R.id.header1 );
		header1.setText( R.string.before_after );

		TextView header2 = findViewById( R.id.header2 );
		header2.setText( R.string.which_meal );

		TextView header3 = findViewById( R.id.header3 );
		header3.setText( R.string.glucose_level );

		TextView header4 = findViewById( R.id.header4 );
		header4.setText( R.string.date );

	} // onCreate


	public void setEmpty( View view )
	{
		view.setVisibility( View.GONE );
		//		String emptyText = getString( R.string.empty_listview );
		//		TextView emptyTextView = findViewById( R.id.empty_txt );
		//		emptyTextView.setText( emptyText );

	} // setEmpty


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
			Log.i( "LOADER", "Loader not initialized. Not forcing load.\n" + e.getMessage() );
		}

		getContentResolver().notifyChange( MyGlucoseContentProvider.GLUCOSE_ENTRIES_URI, null );

	} // loaderReset


	private void showListview()
	{
		ListView listView = findViewById( R.id.listView );
		listView.setVisibility( View.VISIBLE );

		// Set the adapter
		if( viewCursorAdapter != null )
			listView.setAdapter( viewCursorAdapter );

		View emptyView = findViewById( R.id.empty_view );
		emptyView.setVisibility( View.GONE );
		View headerView = findViewById( R.id.header );
		headerView.setVisibility( View.VISIBLE );

	} // showListview


	private void showEmpty()
	{
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
				MyGlucoseContentProvider.GLUCOSE_ENTRIES_URI,
				null, null, null, DB.KEY_UPDATED_AT + " ASC" );

	} // onCreateLoader

	@Override
	public void onLoadFinished( @NonNull android.support.v4.content.Loader<Cursor> loader, Cursor data )
	{
		this.cursor = data;

		viewCursorAdapter = new GlucoseCursorAdapter( getApplicationContext(), cursor );

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
