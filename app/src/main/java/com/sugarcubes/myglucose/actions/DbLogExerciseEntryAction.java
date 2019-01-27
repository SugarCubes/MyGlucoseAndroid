package com.sugarcubes.myglucose.actions;

import android.content.Context;

import com.sugarcubes.myglucose.actions.interfaces.ILogExerciseEntryAction;
import com.sugarcubes.myglucose.models.ExerciseEntry;
import com.sugarcubes.myglucose.enums.ErrorCode;
import com.sugarcubes.myglucose.repositories.DbExerciseEntryRepository;
import com.sugarcubes.myglucose.repositories.DbPatientRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

public class DbLogExerciseEntryAction implements ILogExerciseEntryAction
{
    @Override
    public ErrorCode logExerciseEntry( Context context, ExerciseEntry exerciseEntry ) throws InterruptedException
    {
        DbPatientRepository dbPatientRepository = new DbPatientRepository( context );
        DbExerciseEntryRepository dbExerciseEntryRepository = new DbExerciseEntryRepository( context );
        PatientSingleton patientSingleton = PatientSingleton.getInstance();
        patientSingleton.exerciseEntries.add( exerciseEntry );
        dbPatientRepository.update( patientSingleton.getUserName(), patientSingleton );
        dbExerciseEntryRepository.create( exerciseEntry );

        return ErrorCode.NO_ERROR;

    } // logExerciseEntry

} // class
