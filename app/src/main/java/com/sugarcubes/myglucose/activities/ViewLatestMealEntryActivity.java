package com.sugarcubes.myglucose.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.dependencies.Dependencies;
import com.sugarcubes.myglucose.entities.MealEntry;
import com.sugarcubes.myglucose.entities.MealItem;
import com.sugarcubes.myglucose.repositories.DbMealEntryRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IMealEntryRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

import java.util.ArrayList;

public class ViewLatestMealEntryActivity extends AppCompatActivity
{

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_view_latest_meal_entry );
		Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
		setSupportActionBar( toolbar );
		getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        PatientSingleton patientSingleton = PatientSingleton.getInstance();
		IMealEntryRepository mealEntryRepository = Dependencies.get( IMealEntryRepository.class );
        DbMealEntryRepository dbMealEntryRepository = new DbMealEntryRepository(getApplicationContext());


		MealEntry newest;
		ArrayList<MealEntry> latestMealEntries = mealEntryRepository.readAll();
		if( latestMealEntries.size() > 0 )
		{
			newest = latestMealEntries.get(0);
			ArrayList<MealItem> mealItems = newest.getMealItems();

			// TODO: Foreach mealItems

            TextView totalCarbsView = findViewById(R.id.totalCarbs);
            totalCarbsView.setText( String.valueOf( newest.getTotalCarbs() ) );

            TextView whichMealView = findViewById(R.id.whichMeal);
            whichMealView.setText( newest.getWhichMeal().toString() );
		} // if

	} // onCreate

}
