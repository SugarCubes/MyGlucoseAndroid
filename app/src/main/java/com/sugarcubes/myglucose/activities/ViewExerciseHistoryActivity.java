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
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.adapters.ExerciseCursorAdapter;
import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;

public class ViewExerciseHistoryActivity extends AppCompatActivity
		implements LoaderManager.LoaderCallbacks<Cursor>
{
	int loaderIndex = 33333;
	CursorAdapter viewCursorAdapter;
	Cursor        cursor;


	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_view_history );

		// Initialize loader to handle calls to ContentProvider
		getSupportLoaderManager().initLoader( loaderIndex, null, this );

	} // onCreate


	public void setEmpty( View view )
	{
//		view.setVisibility( View.INVISIBLE );
		String emptyText = getString( R.string.empty_listview );
		TextView emptyTextView = view.findViewById( R.id.empty_txt );
		emptyTextView.setText( emptyText );

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
			Log.i( "LOADER", "Loader not initialized. Not forcing load.\n"
					+ e.getMessage() );
		}

		getContentResolver().notifyChange(
				MyGlucoseContentProvider.EXERCISE_ENTRIES_URI, null );

	} // loaderReset


	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader( int id, @Nullable Bundle args )
	{
		return new CursorLoader( getApplicationContext(),
				MyGlucoseContentProvider.EXERCISE_ENTRIES_URI,
				null, DB.KEY_EXERCISE_STEPS + "<?",
				new String[]{ String.valueOf( 1 ) },
				DB.KEY_UPDATED_AT + " ASC" );

	} // onCreateLoader


	@Override
	public void onLoadFinished( @NonNull android.support.v4.content.Loader<Cursor> loader, Cursor data )
	{
		this.cursor = data;

		// When a list item is clicked, set the activity to go to:
		viewCursorAdapter = new ExerciseCursorAdapter( getApplicationContext(), cursor );
		ListView listView = findViewById( R.id.listView );
		if( viewCursorAdapter != null )
			listView.setAdapter( viewCursorAdapter );
		else
			setEmpty( listView );

		if( viewCursorAdapter != null )
			viewCursorAdapter.swapCursor( cursor );
		if( cursor != null && !cursor.isClosed() )
			synchronized( cursor )
			{
				cursor.notify();
				cursor.close();
			}

	} // onLoadFinished


	@Override
	public void onLoaderReset( @NonNull android.support.v4.content.Loader<Cursor> loader )
	{
		//		loaderReset();

	} // onLoaderReset

} // class
