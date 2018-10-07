package com.sugarcubes.myglucose.actions;

import android.content.Context;

import com.sugarcubes.myglucose.actions.interfaces.ILogMealEntryAction;
import com.sugarcubes.myglucose.entities.MealEntry;
import com.sugarcubes.myglucose.enums.ErrorCode;

public class SimulateLogMealEntryAction implements ILogMealEntryAction
{
	@Override
	public ErrorCode logMealEntry( Context context, MealEntry mealEntry ) throws InterruptedException
	{
		Thread.sleep( 2000 );		// Simulate working for 2 seconds
		return ErrorCode.NO_ERROR;

	} // logMealEntry

} // class
