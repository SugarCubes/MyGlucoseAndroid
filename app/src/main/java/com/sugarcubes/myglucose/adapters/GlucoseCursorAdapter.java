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
import com.sugarcubes.myglucose.activities.ViewLatestGlucoseEntryActivity;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.enums.WhichMeal;

public class GlucoseCursorAdapter extends CursorAdapter
{
	private LayoutInflater cursorInflater;
	private Context context;
	private int listLayout;

	public GlucoseCursorAdapter( Context context, Cursor c )
	{
		super( context, c, 0 );
		this.context = context;
		this.cursorInflater = LayoutInflater.from( context );
		this.listLayout = R.layout.list_item_history;

	} // constructor


	@Override
	public View newView( Context context, Cursor cursor, ViewGroup viewGroup )
	{
		return cursorInflater.inflate( listLayout, viewGroup, false );

	} // newView


	@Override
	public void bindView( View view, Context context, Cursor cursor )
	{
		// Get references to the cursor's data:
		int historyId = cursor.getInt(
				cursor.getColumnIndexOrThrow( DB.KEY_REMOTE_ID )
		);

		// TODO: Take data from cursor and set R.layout.list_item_history text to the row's data
		String title = cursor.getString( cursor.getColumnIndex( DB.KEY_GLUCOSE_BEFORE_AFTER ) );
		TextView titleView = view.findViewById( R.id.itemName );
		titleView.setText( title );

		WhichMeal whichMeal = WhichMeal.fromInt(
				cursor.getInt( cursor.getColumnIndex( DB.KEY_WHICH_MEAL ) ) );
		String whichMealString = "";
		if( whichMeal != null )
			whichMealString = whichMeal.toString();
		TextView valueView = view.findViewById( R.id.itemValue );
		valueView.setText( whichMealString );

		int measurement = cursor.getInt( cursor.getColumnIndex( DB.KEY_GLUCOSE_MEASUREMENT ) );
		TextView value2View = view.findViewById( R.id.itemValue2 );
		value2View.setText( String.valueOf( measurement ) );

		// Set the view data to handle clicks
		view.setId( historyId );							// To pass during a click
		view.setOnClickListener( historyClickHandler );		// Clicks are now handled

	} // bindView


	private View.OnClickListener historyClickHandler = new View.OnClickListener()
	{
		@Override
		public void onClick( View v )
		{
			// Use this to index the cartoon by its DB index:
			int viewIndex = v.getId();
			try
			{
				// TODO: Set up LatestGlucoseActivity class to handle this intent:
				Intent intent = new Intent( context, ViewLatestGlucoseEntryActivity.class );
				intent.putExtra( "EntryId", viewIndex );       // Used to look up entry info
				context.startActivity( intent );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
	};

} // class
