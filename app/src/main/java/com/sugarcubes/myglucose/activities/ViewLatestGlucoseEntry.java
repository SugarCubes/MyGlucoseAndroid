package com.sugarcubes.myglucose.activities;

import android.os.Bundle;

import com.sugarcubes.myglucose.R;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.repositories.DbGlucoseEntryRepository;
import com.sugarcubes.myglucose.repositories.DbPatientRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;
import com.sugarcubes.myglucose.entities.GlucoseEntry;
import com.sugarcubes.myglucose.enums.WhichMeal;
import com.sugarcubes.myglucose.enums.BeforeAfter;
import java.util.Date;
import java.sql.Timestamp;

public class ViewLatestGlucoseEntry extends AppCompatActivity {

    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_latest_glucose_entry);

        PatientSingleton patientSingleton = PatientSingleton.getInstance();
        DbGlucoseEntryRepository dbGlucoseEntryRepository = new DbGlucoseEntryRepository(getApplicationContext());
        GlucoseEntry glucoseEntry = patientSingleton.getGlucoseEntries().get(dbGlucoseEntryRepository.readAll().size() - 1);

        TextView glucoseLevel = findViewById(R.id.glucoseLevelView);
        glucoseLevel.setText(Float.toString(glucoseEntry.getMeasurement()));
        TextView whichMeal = findViewById(R.id.whichMealView);
        whichMeal.setText(glucoseEntry.getWhichMeal().toString());
        TextView beforeAfter = findViewById(R.id.beforeAfterView);
        beforeAfter.setText(glucoseEntry.getBeforeAfter().toString());
    }
}
