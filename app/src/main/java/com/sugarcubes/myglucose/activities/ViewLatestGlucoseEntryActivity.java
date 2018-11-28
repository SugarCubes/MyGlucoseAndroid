package com.sugarcubes.myglucose.activities;

import android.content.Intent;
import android.os.Bundle;

import com.sugarcubes.myglucose.R;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sugarcubes.myglucose.repositories.DbGlucoseEntryRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;
import com.sugarcubes.myglucose.entities.GlucoseEntry;

import java.util.ArrayList;
import java.util.Locale;

import static com.sugarcubes.myglucose.activities.MainActivity.DEBUG;

public class ViewLatestGlucoseEntryActivity extends AppCompatActivity
{
	private final String LOG_TAG = getClass().getSimpleName();

	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_view_latest_glucose_entry );
		Toolbar toolbar = findViewById( R.id.toolbar );
		setSupportActionBar( toolbar );
		if( getSupportActionBar() != null )
			getSupportActionBar().setDisplayHomeAsUpEnabled( true );

		Button closeButton = findViewById( R.id.close_button );
		closeButton.setOnClickListener( new View.OnClickListener()
										{
											@Override
											public void onClick( View view )
											{
												finish();
											}
										}
		);

		DbGlucoseEntryRepository dbGlucoseEntryRepository
				= new DbGlucoseEntryRepository( getApplicationContext() );

		// GET ALL THE INFO PASSED FROM THE LAST ACTIVITY
		Intent intent = getIntent();
		String glucoseId = intent.getStringExtra( "EntryId" );
		GlucoseEntry glucoseEntry;

		if( DEBUG ) Log.e( LOG_TAG, "EntryId: " + glucoseId );

		if( glucoseId == null || glucoseId.isEmpty() )
		{
			//GlucoseEntry glucoseEntry = patientSingleton.getGlucoseEntries().get(dbGlucoseEntryRepository.readAll().size() - 1);
			ArrayList<GlucoseEntry> glucoseEntries =
					dbGlucoseEntryRepository.readAll();    // Get all from db
			glucoseEntry = glucoseEntries.get( 0 );    // Get last entry
		}
		else
		{
			glucoseEntry = dbGlucoseEntryRepository.read( glucoseId );
		}

		if( glucoseEntry != null )
		{
			if( DEBUG ) Log.e( LOG_TAG, glucoseEntry.toString() );

			TextView glucoseLevel = findViewById( R.id.glucoseLevelView );
			glucoseLevel.setText( Float.toString( glucoseEntry.getMeasurement() ) );
			TextView whichMeal = findViewById( R.id.whichMealView );
			whichMeal.setText( glucoseEntry.getWhichMeal().toString() );
			TextView beforeAfter = findViewById( R.id.beforeAfterView );
			beforeAfter.setText( glucoseEntry.getBeforeAfter().toString() );
		}

	}
}
