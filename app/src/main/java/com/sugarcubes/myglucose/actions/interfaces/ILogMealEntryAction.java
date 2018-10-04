package com.sugarcubes.myglucose.actions.interfaces;

import com.sugarcubes.myglucose.entities.MealEntry;
import com.sugarcubes.myglucose.enums.ErrorCode;

public interface ILogMealEntryAction
{
	ErrorCode logMealEntry( MealEntry mealEntry ) throws InterruptedException;

} // interface
