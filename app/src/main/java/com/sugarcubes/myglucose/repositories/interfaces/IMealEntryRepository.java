package com.sugarcubes.myglucose.repositories.interfaces;

import android.content.ContentValues;
import android.database.Cursor;

import com.sugarcubes.myglucose.entities.MealEntry;
import com.sugarcubes.myglucose.entities.MealItem;

import java.util.ArrayList;

public interface IMealEntryRepository extends IRepository<MealEntry>
{
	ArrayList<MealItem> readAllMealItems( String mealId );
	void createMealItem( MealItem item );
	ContentValues getMealItemContentValues( MealItem item );
	MealItem readMealItemFromCursor( Cursor cursor );
	void deleteMealItem( String id );
	void updateMealItem( String mealItemId, MealItem mealItem );

}
