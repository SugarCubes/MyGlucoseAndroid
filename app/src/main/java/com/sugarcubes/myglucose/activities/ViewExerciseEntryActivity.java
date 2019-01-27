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

import com.sugarcubes.myglucose.repositories.DbExerciseEntryRepository;
import com.sugarcubes.myglucose.models.ExerciseEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.sugarcubes.myglucose.activities.MainActivity.DEBUG;

public class ViewExerciseEntryActivity extends AppCompatActivity
{
	private final String LOG_TAG = getClass().getSimpleName();

	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		//change this
		setContentView( R.layout.activity_view_latest_exercise_entry );
		Toolbar toolbar = findViewById( R.id.toolbar );
		toolbar.setTitle( "View " + getString( R.string.title_activity_exercise_entry ) );
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

		DbExerciseEntryRepository dbExerciseEntryRepository =
				new DbExerciseEntryRepository( getApplicationContext() );


		// GET ALL THE INFO PASSED FROM THE LAST ACTIVITY (if any)
		Intent intent = getIntent();
		String exerciseId = intent.getStringExtra( "EntryId" );
		ExerciseEntry exerciseEntry;

		if( DEBUG ) Log.e( LOG_TAG, "EntryId: " + exerciseId );

		if( exerciseId == null || exerciseId.isEmpty() )
		{
			ArrayList<ExerciseEntry> exerciseEntries =
					dbExerciseEntryRepository.readAll();    // Get all from db
			//        ExerciseEntry exerciseEntry = patientSingleton.getExerciseEntries().get(
			//                exerciseEntries.size() - 1 );
			if( exerciseEntries.size() > 0 )
			{
				exerciseEntry = exerciseEntries.get( 0 );    // Get last entry

			}
			else
				exerciseEntry = new ExerciseEntry();
		}
		else
			exerciseEntry = dbExerciseEntryRepository.read( exerciseId );

		if( exerciseEntry != null )
		{
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

		} // if

	} // onCreate

} // class
