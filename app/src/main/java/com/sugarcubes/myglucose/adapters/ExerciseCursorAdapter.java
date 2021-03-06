package com.sugarcubes.myglucose.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.activities.ViewExerciseEntryActivity;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.utils.DateUtilities;

public class ExerciseCursorAdapter extends CursorAdapter
{
	private final String LOG_TAG = getClass().getSimpleName();
	private LayoutInflater cursorInflater;
	private Context        context;

	public ExerciseCursorAdapter( Context context, Cursor cursor )
	{
		super( context, cursor, 0 );
		this.context = context;
		this.cursorInflater = LayoutInflater.from( context );

	} // constructor


	@Override
	public View newView( Context context, Cursor cursor, ViewGroup viewGroup )
	{
		return cursorInflater.inflate( R.layout.list_item_history_3_columns, viewGroup, false );

	} // newView


	@Override
	public void bindView( View view, Context context, Cursor cursor )
	{
		// Take data from cursor and set R.layout.list_item_history_3_columns text to the row's data
		TextView titleView = view.findViewById( R.id.itemName );
		TextView valueView = view.findViewById( R.id.itemValue );
		TextView value2View = view.findViewById( R.id.itemValue2 );

		// Get references to the cursor's data:
		String historyId = cursor.getString( cursor.getColumnIndexOrThrow( DB.KEY_REMOTE_ID ) );

		String exerciseName =
				cursor.getString( cursor.getColumnIndexOrThrow( DB.KEY_EXERCISE_NAME ) );
		int exerciseMin = cursor.getInt( cursor.getColumnIndexOrThrow( DB.KEY_EXERCISE_MINUTES ) );

		String exerciseMinutes = DateUtilities.minutesToHoursMinutesString( exerciseMin );

		String updatedAt = cursor.getString( cursor.getColumnIndexOrThrow( DB.KEY_UPDATED_AT ) );
		String formattedDate =
				DateUtilities.getSimpleDateString( DateUtilities.convertStringToDate( updatedAt ) );
		//		// Set texts:
		titleView.setText( exerciseName );
		valueView.setText( exerciseMinutes );
		value2View.setText( String.valueOf( formattedDate ) );

		// Set the view data to handle clicks
		view.setTag( historyId );                        // To pass during a click
		view.setOnClickListener( historyClickHandler );        // Clicks are now handled

	} // bindView


	private View.OnClickListener historyClickHandler = new View.OnClickListener()
	{
		@Override
		public void onClick( View v )
		{
			// Use this to index the cartoon by its DB index:
			String viewIndex = (String) v.getTag();
			try
			{
				// Send the entry's info to be viewed:
				Intent intent = new Intent( context, ViewExerciseEntryActivity.class );
				intent.putExtra( "EntryId", String.valueOf( viewIndex ) );       // Used to look up entry info
				context.startActivity( intent );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}

		}
	}; // historyClickHandler

} // class
