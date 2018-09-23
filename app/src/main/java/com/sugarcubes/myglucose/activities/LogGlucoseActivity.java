package com.sugarcubes.myglucose.activities;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.repositories.DbGlucoseEntryRepository;
import com.sugarcubes.myglucose.repositories.DbPatientRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;
import com.sugarcubes.myglucose.entities.GlucoseEntry;

public class LogGlucoseActivity extends AppCompatActivity
{
	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_log_glucose );

		Button button = findViewById(R.id.submitButton);

		button.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event){
				if(event.getAction() == MotionEvent.ACTION_UP) {

					DbGlucoseEntryRepository dbGlucoseEntryRepository = new DbGlucoseEntryRepository(getApplicationContext());
                    GlucoseEntry glucoseEntry = new GlucoseEntry();



					dbGlucoseEntryRepository.create( glucoseEntry );
					finish();
					return true;
				}
				return false;
			}
		});
	}
}
