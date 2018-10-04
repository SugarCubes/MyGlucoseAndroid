package com.sugarcubes.myglucose.actions;

import android.content.Context;

import com.sugarcubes.myglucose.actions.interfaces.ILogMealEntryAction;
import com.sugarcubes.myglucose.entities.MealEntry;
import com.sugarcubes.myglucose.enums.ErrorCode;
import com.sugarcubes.myglucose.repositories.DbMealEntryRepository;

public class DbLogMealEntryAction implements ILogMealEntryAction
{
	@Override
	public ErrorCode logMealEntry( Context context, MealEntry mealEntry ) throws InterruptedException
	{
		DbMealEntryRepository mealEntryRepository = new DbMealEntryRepository( context );
		mealEntryRepository.create( mealEntry );

		return null;

	} // logMealEntry

} // class
