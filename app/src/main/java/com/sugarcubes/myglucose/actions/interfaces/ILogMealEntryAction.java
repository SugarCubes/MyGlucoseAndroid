package com.sugarcubes.myglucose.actions.interfaces;

import android.content.Context;

import com.sugarcubes.myglucose.entities.MealEntry;
import com.sugarcubes.myglucose.enums.ErrorCode;

public interface ILogMealEntryAction
{
	ErrorCode logMealEntry( Context context, MealEntry mealEntry ) throws InterruptedException;

} // interface
