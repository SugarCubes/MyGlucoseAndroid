//--------------------------------------------------------------------------------------//
//																						//
// File Name:	IMealEntryRepository.java												//
// Programmer:	J.T. Blevins (jt.blevins@gmail.com)										//
// Date:		09/08/2018																//
// Purpose:		An interface to enforce the methods required to access a				//
// 				MealEntry and its MealItem objects in the database.						//
//																						//
//--------------------------------------------------------------------------------------//

package com.sugarcubes.myglucose.repositories.interfaces;

import android.content.ContentValues;
import android.database.Cursor;

import com.sugarcubes.myglucose.entities.MealEntry;
import com.sugarcubes.myglucose.entities.MealItem;

import java.util.ArrayList;

public interface IMealEntryRepository extends IRepository<MealEntry>
{
	void createMealItem( MealItem mealItem );
	ArrayList<MealItem> readAllMealItems( String mealId );
	MealItem readMealItemFromCursor( Cursor cursor );
	ContentValues putMealItemContentValues( MealItem mealItem );
	void updateMealItem( String mealItemId, MealItem mealItem );
	void deleteMealEntryMealItems( String mealEntryId );
	void deleteMealItem( String mealItemId );

} // interface
