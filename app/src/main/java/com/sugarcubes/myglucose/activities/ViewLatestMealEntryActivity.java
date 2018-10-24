package com.sugarcubes.myglucose.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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


        PatientSingleton patientSingleton = PatientSingleton.getInstance();
		IMealEntryRepository mealEntryRepository = Dependencies.get( IMealEntryRepository.class );
        //DbMealEntryRepository dbMealEntryRepository = new DbMealEntryRepository(getApplicationContext());


		MealEntry newest;
		ArrayList<MealEntry> latestMealEntries = mealEntryRepository.readAll();
		if( latestMealEntries.size() > 0 )
		{
			newest = latestMealEntries.get(0);
			ArrayList<MealItem> mealItems = newest.getMealItems();

			// TODO: Foreach mealItems

            int totalCarbsTemp = 0;
			for(int i =0; i < mealItems.size(); i++)
            {
                totalCarbsTemp = mealItems.get(i).getCarbs();
            }

            TextView totalCarbsView = findViewById(R.id.totalCarbs);
            totalCarbsView.setText(Integer.toString(totalCarbsTemp));

            TextView whichMealView = findViewById(R.id.whichMealM);
            whichMealView.setText(newest.getWhichMeal().toString());
		} // if
        else{
		    newest = new MealEntry();
        }

	} // onCreate

}
