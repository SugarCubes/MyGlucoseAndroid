package com.sugarcubes.myglucose.actions.interfaces;

import android.content.Context;

import com.sugarcubes.myglucose.models.GlucoseEntry;
import com.sugarcubes.myglucose.enums.ErrorCode;

public interface ILogGlucoseEntryAction
{
    ErrorCode logGlucoseEntry( Context context, GlucoseEntry glucoseEntry ) throws InterruptedException;

} // interface
