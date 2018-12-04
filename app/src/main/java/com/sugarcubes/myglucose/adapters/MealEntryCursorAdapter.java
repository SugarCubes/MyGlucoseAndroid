package com.sugarcubes.myglucose.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.activities.ViewMealEntryActivity;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.enums.WhichMeal;
import com.sugarcubes.myglucose.utils.DateUtilities;

import static com.sugarcubes.myglucose.activities.MainActivity.DEBUG;

public class MealEntryCursorAdapter extends CursorAdapter
{
	private final String LOG_TAG = getClass().getSimpleName();
	private LayoutInflater cursorInflater;
	private Context context;
	private int listLayout;

	public MealEntryCursorAdapter( Context context, Cursor cursor )
	{
		super( context, cursor, 0 );
		this.context = context;
		this.cursorInflater = LayoutInflater.from( context );
		this.listLayout = R.layout.list_item_history_3_columns;

	}


	@Override
	public View newView( Context context, Cursor cursor, ViewGroup viewGroup )
	{
		return cursorInflater.inflate( listLayout, viewGroup, false );

	} // newView


	@Override
	public void bindView( View view, Context context, Cursor cursor )
	{
		// Take data from cursor and set R.layout.list_item_history_3_columns text to the row's data
		//TextView titleView = view.findViewById( R.id.itemName );
		//TextView value2View = view.findViewById( R.id.itemValue2 );

		// Get references to the cursor's data:
		// TODO: Started the wrong user story. Please continue...   :D
//
//		// Set texts:
//		titleView.setText( title );
//		valueView.setText( "" );
//		value2View.setText( String.valueOf( measurement ) );

		int whichMealInt = cursor.getInt( cursor.getColumnIndex( DB.KEY_WHICH_MEAL ) );
		// TODO: Cursor returning 0:
		if( DEBUG ) Log.e( LOG_TAG, "whichMealInt:" + whichMealInt );
		TextView valueView = view.findViewById( R.id.itemName );
		WhichMeal whichMeal = WhichMeal.fromInt( whichMealInt );
		String whichMealString = "";
		if( whichMeal != null )
			whichMealString = whichMeal.toString();

		valueView.setText( whichMealString );

		TextView value2View = view.findViewById( R.id.itemValue);
		int totalCarbs = cursor.getInt( cursor.getColumnIndex( DB.KEY_MEAL_ENTRY_TOTAL_CARBS ) );
		value2View.setText( String.valueOf( totalCarbs ) );

		String dateString =
				DateUtilities.getSimpleDateString( DateUtilities.convertStringToDate(
						cursor.getString( cursor.getColumnIndexOrThrow( DB.KEY_UPDATED_AT ) ) ) );
		TextView value3View = view.findViewById( R.id.itemValue2 );
		value3View.setText( dateString );


		// Set the view data to handle clicks
		String historyId = cursor.getString( cursor.getColumnIndexOrThrow( DB.KEY_REMOTE_ID ) );
		view.setTag( historyId );						// To pass during a click
		view.setOnClickListener( historyClickHandler );		// Clicks are now handled

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
				Intent intent = new Intent( context, ViewMealEntryActivity.class );
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
