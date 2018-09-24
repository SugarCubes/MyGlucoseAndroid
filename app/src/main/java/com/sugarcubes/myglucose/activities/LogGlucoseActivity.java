package com.sugarcubes.myglucose.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.repositories.DbGlucoseEntryRepository;
import com.sugarcubes.myglucose.repositories.DbPatientRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;
import com.sugarcubes.myglucose.entities.GlucoseEntry;
import com.sugarcubes.myglucose.enums.WhichMeal;
import com.sugarcubes.myglucose.enums.BeforeAfter;
import java.util.Date;
import java.sql.Timestamp;

public class LogGlucoseActivity extends AppCompatActivity
{
	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_log_glucose );

		Button button = findViewById(R.id.submitGlucose);
        Button viewLatest = findViewById(R.id.viewLatest);

		button.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event){
				if(event.getAction() == MotionEvent.ACTION_UP) {

                    DbPatientRepository dbPatientRepository = new DbPatientRepository(getApplicationContext());
                    DbGlucoseEntryRepository dbGlucoseEntryRepository = new DbGlucoseEntryRepository(getApplicationContext());
                    PatientSingleton patientSingleton = PatientSingleton.getInstance();
					GlucoseEntry glucoseEntry = new GlucoseEntry();

                    EditText glucoseLevel = findViewById(R.id.glucoseLevel);
                    Spinner whichMeal = findViewById(R.id.whichMeal);
                    Spinner beforeAfter = findViewById(R.id.beforeAfter);
                    WhichMeal whichMealEnum = WhichMeal.OTHER;
                    BeforeAfter beforeAfterEnum = BeforeAfter.BEFORE;

                    glucoseEntry.setMeasurement(Float.parseFloat(glucoseLevel.getText().toString()));
                    whichMealEnum = WhichMeal.valueOf(whichMeal.getSelectedItem().toString().toUpperCase());
                    glucoseEntry.setWhichMeal(whichMealEnum);
                    beforeAfterEnum = BeforeAfter.valueOf(beforeAfter.getSelectedItem().toString().toUpperCase());
                    glucoseEntry.setBeforeAfter(beforeAfterEnum);

                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    Date date = new Date();

                    glucoseEntry.setTimeStamp(timestamp.getTime());
                    glucoseEntry.setDate(timestamp);
                    patientSingleton.glucoseEntries.add(glucoseEntry);
                    dbPatientRepository.update(patientSingleton.getUserName(), patientSingleton);
                    dbGlucoseEntryRepository.create(glucoseEntry);

					finish();
					return true;
				}
				return false;
			}
		});

//        viewLatest.setOnTouchListener(new View.OnTouchListener(){
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//
//                    setContentView(R.layout.view_latest_glucose_entry);
//                    return true;
//                }
//                return false;
//            }
//        });

		viewLatest.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					startViewLatestGlucoseActivity();
					return true;
				}
				return false;
			}
		});

	} // onCreate


	private void startViewLatestGlucoseActivity()
	{
		Intent intent = new Intent( this, ViewLatestGlucoseEntry.class );
		startActivity( intent );

	} // startEditProfileActivity

} // class
