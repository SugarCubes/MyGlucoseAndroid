package com.sugarcubes.myglucose.activities;

import android.os.Bundle;

import com.sugarcubes.myglucose.R;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sugarcubes.myglucose.repositories.DbExerciseEntryRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;
import com.sugarcubes.myglucose.entities.ExerciseEntry;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ViewLatestExerciseEntryActivity extends AppCompatActivity
{

	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		//change this
		setContentView( R.layout.activity_view_latest_exercise_entry );
		Toolbar toolbar = findViewById( R.id.toolbar );
		toolbar.setTitle( R.string.title_activity_view_latest_exercise_entry );
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

		PatientSingleton patientSingleton = PatientSingleton.getInstance();
		DbExerciseEntryRepository dbExerciseEntryRepository =
				new DbExerciseEntryRepository( getApplicationContext() );
		ArrayList<ExerciseEntry> exerciseEntries =
				dbExerciseEntryRepository.readAll();    // Get all from db
		//        ExerciseEntry exerciseEntry = patientSingleton.getExerciseEntries().get(
		//                exerciseEntries.size() - 1 );
		ExerciseEntry exerciseEntry;
		if( exerciseEntries.size() > 0 )
		{
			exerciseEntry = exerciseEntries.get( 0 );    // Get last entry

		}
		else
			exerciseEntry = new ExerciseEntry();

		//change this

		TextView exerciseName = findViewById( R.id.exercise_name_view );
		exerciseName.setText( exerciseEntry.getExerciseName() );
		TextView exerciseMinutes = findViewById( R.id.exercise_minutes_view );
		exerciseMinutes.setText( String.valueOf( exerciseEntry.getMinutes() ) );
		TextView exerciseSteps = findViewById( R.id.exercise_steps_view );
		exerciseSteps.setText( String.valueOf( exerciseEntry.getSteps() ) );
		TextView tvDate = findViewById( R.id.exercise_date_view );
		SimpleDateFormat formatter = new SimpleDateFormat( "MM/dd/yyyy hh:mm a", Locale.US );
		String formattedDate = formatter.format( exerciseEntry.getCreatedAt() );
		tvDate.setText( formattedDate );//String.valueOf( formattedDate ) );

	}
}
