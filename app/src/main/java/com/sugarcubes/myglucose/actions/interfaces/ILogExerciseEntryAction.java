package com.sugarcubes.myglucose.actions.interfaces;

import android.content.Context;

import com.sugarcubes.myglucose.models.ExerciseEntry;
import com.sugarcubes.myglucose.enums.ErrorCode;

public interface ILogExerciseEntryAction
{
    ErrorCode logExerciseEntry( Context context, ExerciseEntry exerciseEntry ) throws InterruptedException;

} // interface
