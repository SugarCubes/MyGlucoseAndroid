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
import com.sugarcubes.myglucose.activities.ViewGlucoseEntryActivity;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.enums.WhichMeal;
import com.sugarcubes.myglucose.utils.DateUtilities;

import static com.sugarcubes.myglucose.activities.MainActivity.DEBUG;

public class GlucoseCursorAdapter extends CursorAdapter
{
	private final String LOG_TAG = getClass().getSimpleName();
	private LayoutInflater cursorInflater;
	private Context        context;
	private int            listLayout;

	public GlucoseCursorAdapter( Context context, Cursor c )
	{
		super( context, c, 0 );
		this.context = context;
		this.cursorInflater = LayoutInflater.from( context );
		this.listLayout = R.layout.list_item_history_4_columns;

	} // constructor


	@Override
	public View newView( Context context, Cursor cursor, ViewGroup viewGroup )
	{
		return cursorInflater.inflate( listLayout, viewGroup, false );

	} // newView


	@Override
	public void bindView( View view, Context context, Cursor cursor )
	{
		// Set Before/After
		TextView valueView = view.findViewById( R.id.itemValue );
		String title = cursor.getString( cursor.getColumnIndex( DB.KEY_GLUCOSE_BEFORE_AFTER ) );
		TextView titleView = view.findViewById( R.id.itemName );
		titleView.setText( title );

		// Set WhichMeal
		int whichMealInt = cursor.getInt( cursor.getColumnIndex( DB.KEY_WHICH_MEAL ) );
		// TODO: Cursor returning 0:
		if( DEBUG ) Log.e( LOG_TAG, "whichMealInt:" + whichMealInt );
		WhichMeal whichMeal = WhichMeal.fromInt( whichMealInt );
		String whichMealString = "";
		if( whichMeal != null )
			whichMealString = whichMeal.toString();
		valueView.setText( whichMealString );

		// Set Measurement
		TextView value2View = view.findViewById( R.id.itemValue2 );
		int measurement = cursor.getInt( cursor.getColumnIndex( DB.KEY_GLUCOSE_MEASUREMENT ) );
		value2View.setText( String.valueOf( measurement ) );

		// Set date
		String dateString =
				DateUtilities.getSimpleDateString( DateUtilities.convertStringToDate(
						cursor.getString( cursor.getColumnIndexOrThrow( DB.KEY_UPDATED_AT ) ) ) );
		TextView value3View = view.findViewById( R.id.itemValue3 );
		value3View.setText( dateString );

		// Set the view data to handle clicks
		String historyId = cursor.getString( cursor.getColumnIndexOrThrow( DB.KEY_REMOTE_ID ) );
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
				Intent intent = new Intent( context, ViewGlucoseEntryActivity.class );
				intent.putExtra( "EntryId", String.valueOf( viewIndex ) );       // Used to look up entry info
				context.startActivity( intent );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
	};

} // class
