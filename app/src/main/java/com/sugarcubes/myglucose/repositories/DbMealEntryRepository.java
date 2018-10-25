//--------------------------------------------------------------------------------------//
//																						//
// File Name:	DbGlucoseEntryRepository.java											//
// Programmer:	J.T. Blevins (jt.blevins@gmail.com)										//
// Date:		09/08/2018																//
// Purpose:		A repository to allow MealEntry and MealItem data manipulation in a 	//
// 				SQLite database. 														//
//																						//
//--------------------------------------------------------------------------------------//

package com.sugarcubes.myglucose.repositories;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.entities.MealEntry;
import com.sugarcubes.myglucose.entities.MealItem;
import com.sugarcubes.myglucose.enums.WhichMeal;
import com.sugarcubes.myglucose.repositories.interfaces.IMealEntryRepository;
import com.sugarcubes.myglucose.utils.DateUtilities;

import java.util.ArrayList;
import java.util.UUID;

public class DbMealEntryRepository implements IMealEntryRepository
{
	private ContentResolver contentResolver;
	private Uri uriEntries   = MyGlucoseContentProvider.MEAL_ENTRIES_URI;
	private Uri uriMealItems = MyGlucoseContentProvider.MEAL_ITEMS_URI;

	public DbMealEntryRepository( Context context )
	{
		contentResolver = context.getContentResolver();

	} // constructor


	/**
	 * Creates a MealEntry in the database, as well as all of the meal items associated
	 * with it.
	 *
	 * @param mealEntry - the MealEntry object
	 */
	@Override
	public void create( MealEntry mealEntry )
	{
		if( mealEntry.getRemoteId().isEmpty() )                        // Create an ID
			mealEntry.setRemoteId( UUID.randomUUID().toString() );

		int totalCarbs = 0;
		for( MealItem mealItem : mealEntry.getMealItems() )            // Calculate total carbohydrates
		{
			// Add each mealEntry's carbs if not empty
			totalCarbs += mealItem.getCarbs() < 1
					? 0
					: mealItem.getCarbs();

		} // for

		mealEntry.setTotalCarbs( totalCarbs );                        // Set the calculated carbs

		// Insert into the database:
		contentResolver.insert( uriEntries, putContentValues( mealEntry ) );

		if( mealEntry.getMealItems() != null && mealEntry.getMealItems().size() > 0 )
		{
			// Create MealItems in the DB if they exist
			for( MealItem mealItem : mealEntry.getMealItems() )
			{
				mealItem.setRemoteId( mealEntry.getRemoteId() );    // Set the MealItem's MealEntry Id
				createMealItem( mealItem );                            // Create in the database

			} // for

		} // if

	} // create


	@Override
	public MealEntry read( int id )
	{
		Cursor cursor = contentResolver.query( uriEntries,
				null, DB.KEY_ID + "=?",
				new String[]{ String.valueOf( id ) },
				DB.KEY_TIMESTAMP + " ASC" );

		if( cursor != null )                            // First, if we have a MealEntry...
		{
			cursor.moveToFirst();
			MealEntry entry = readFromCursor( cursor );    // ...Load it from the db
			cursor.close();


			Cursor mealItemsCursor = contentResolver.query( uriMealItems,
					null, DB.KEY_MEAL_ID + "=?",
					new String[]{ String.valueOf( id ) },
					DB.KEY_TIMESTAMP + " ASC" );

			if( mealItemsCursor != null )                // Then we check for MealItems
			{
				mealItemsCursor.moveToFirst();

				ArrayList<MealItem> mealItems = new ArrayList<>();

				while( cursor.moveToNext() )
				{
					MealItem mealItem = new MealItem();
					// TODO: Populate the MealItems

				} // while
				//				entry.setMealItems( readMealItemsFromCursor( mealItemsCursor ) );
				mealItemsCursor.close();

			} // if

			return entry;                                // Return the entry we retrieved

		} // if

		return null;

	} // read


	@Override
	public ArrayList<MealEntry> readAll()
	{
		ArrayList<MealEntry> mealEntries = new ArrayList<>();
		Cursor cursor = contentResolver.query( uriEntries, null, null,
				null, DB.KEY_TIMESTAMP + " DESC" );

		if( cursor != null )
		{
			cursor.moveToFirst();
			do
			{
				MealEntry mealEntry = readFromCursor( cursor );
				mealEntries.add( mealEntry );                // Add the entry to the ArrayList

			} while( cursor.moveToNext() ); // do...while
			cursor.close();

		} // if

		return mealEntries;

	} // readAll


	@Override
	public MealEntry readFromCursor( Cursor cursor )
	{
		MealEntry entry = new MealEntry();
		entry.setId( cursor.getInt( cursor.getColumnIndex( DB.KEY_ID ) ) );
		entry.setRemoteId( cursor.getString( cursor.getColumnIndex( DB.KEY_REMOTE_ID ) ) );
		entry.setTotalCarbs( cursor.getInt( cursor.getColumnIndex( DB.KEY_MEAL_ENTRY_TOTAL_CARBS ) ) );
		entry.setMealItems( readAllMealItems( entry.getId() ) );    // Access MealItems by meal id
		// Convert the date string to a Date object:
		entry.setDate( DateUtilities.convertStringToDate(
				cursor.getString( cursor.getColumnIndex( DB.KEY_DATE ) ) ) );
		// Retrieve as a long:
		entry.setTimestamp( cursor.getLong( cursor.getColumnIndex( DB.KEY_TIMESTAMP ) ) );
		entry.setWhichMeal( WhichMeal.fromInt(
				cursor.getInt( cursor.getColumnIndex( DB.KEY_WHICH_MEAL ) ) ) );
		return entry;

	} // readFromCursor


	@Override
	public ContentValues putContentValues( MealEntry item )
	{
		ContentValues values = new ContentValues();
		values.put( DB.KEY_REMOTE_ID, item.getRemoteId() );
		values.put( DB.KEY_MEAL_ENTRY_TOTAL_CARBS, item.getTotalCarbs() );
		values.put( DB.KEY_DATE, item.getDate().toString() );
		values.put( DB.KEY_TIMESTAMP, item.getTimestamp() );
		int whichMeal = 0;
		try
		{
			whichMeal = item.getWhichMeal().getValue();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		values.put( DB.KEY_WHICH_MEAL, whichMeal );
		return values;

	} // putContentValues


	@Override
	public void update( int id, MealEntry item )
	{
		contentResolver.update( uriEntries, putContentValues( item ),        // Update the MealEntry
				DB.KEY_ID + "=?", new String[]{ String.valueOf( id ) } );

		if( item.getMealItems() != null && item.getMealItems().size() > 0 )    // If !null
			for( MealItem mealItem : item.getMealItems() )
				updateMealItem( mealItem.getId(), mealItem );                // Also update mealItems

	} // update


	@Override
	public void delete( MealEntry item )
	{
		delete( item.getId() );

	} // delete


	@Override
	public void delete( int id )
	{
		// Delete the MealItems first:
		contentResolver.delete( uriMealItems, DB.KEY_MEAL_ID + "=?",
				new String[]{ String.valueOf( id ) } );

		// Finally, delete the MealEntry row itself:
		contentResolver.delete( uriEntries, DB.KEY_ID + "=?",
				new String[]{ String.valueOf( id ) } );

	} // delete


	//----------------------------- MealItem implementation: -----------------------------//


	@Override
	public void createMealItem( MealItem mealItem )
	{
		if( mealItem.getRemoteId().isEmpty() )
			mealItem.setRemoteId( UUID.randomUUID().toString() );

		contentResolver.insert( uriMealItems, putMealItemContentValues( mealItem ) );

	} // createMealItem


	@Override
	public ArrayList<MealItem> readAllMealItems( int mealEntryId )
	{
		ArrayList<MealItem> mealItems = new ArrayList<>();
		Cursor cursor = contentResolver.query( uriMealItems,
				null, DB.KEY_MEAL_ID + "=?",
				new String[]{ String.valueOf( mealEntryId ) },
				DB.KEY_TIMESTAMP + " DESC" );

		if( cursor != null )
		{
			cursor.moveToFirst();
			while( cursor.moveToNext() )
			{
				MealItem mealItem = readMealItemFromCursor( cursor );
				mealItems.add( mealItem );                // Add the entry to the ArrayList

			} // while
			cursor.close();

		} // if

		return mealItems;

	} // readAllMealItems


	@Override
	public MealItem readMealItemFromCursor( Cursor cursor )
	{
		return new MealItem(
				cursor.getInt( cursor.getColumnIndex( DB.KEY_ID ) ),
				cursor.getString( cursor.getColumnIndex( DB.KEY_REMOTE_ID ) ),
				cursor.getString( cursor.getColumnIndex( DB.KEY_MEAL_ID ) ),
				cursor.getString( cursor.getColumnIndex( DB.KEY_MEAL_ITEM_NAME ) ),
				cursor.getInt( cursor.getColumnIndex( DB.KEY_MEAL_ITEM_CARBS ) ),
				cursor.getInt( cursor.getColumnIndex( DB.KEY_MEAL_ITEM_SERVINGS ) )
		);

	} // readMealItemFromCursor


	@Override
	public ContentValues putMealItemContentValues( MealItem item )
	{
		ContentValues values = new ContentValues();
		values.put( DB.KEY_REMOTE_ID, item.getRemoteId() );
		values.put( DB.KEY_MEAL_ID, item.getMealId() );
		values.put( DB.KEY_MEAL_ITEM_NAME, item.getName() );
		values.put( DB.KEY_MEAL_ITEM_CARBS, item.getCarbs() );
		values.put( DB.KEY_MEAL_ITEM_SERVINGS, item.getServings() );
		return values;

	} // putMealItemContentValues


	@Override
	public void updateMealItem( int mealItemId, MealItem mealItem )
	{
		contentResolver.update( uriMealItems, putMealItemContentValues( mealItem ),
				DB.KEY_ID + "=?", new String[]{ String.valueOf( mealItemId ) } );

	} // updateMealItem


	@Override
	public void deleteMealEntryMealItems( int mealEntryId )
	{
		contentResolver.delete( uriMealItems, DB.KEY_MEAL_ID + "=?",
				new String[]{ String.valueOf( mealEntryId ) } );

	} // deleteMealEntryMealItems


	@Override
	public void deleteMealItem( int mealItemId )
	{
		contentResolver.delete( uriMealItems, DB.KEY_ID + "=?",
				new String[]{ String.valueOf( mealItemId ) } );

	} // deleteMealItem


} // repository