package com.sugarcubes.myglucose.activities;

import android.annotation.SuppressLint;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.app.Activity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.app.LoaderManager.LoaderCallbacks;
import android.widget.TextView;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.contentproviders.MyGlucoseContentProvider;
import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.enums.UserType;
import com.sugarcubes.myglucose.repositories.DbApplicationUserRepository;
import com.sugarcubes.myglucose.repositories.DbPatientRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;

public class EditProfileActivity extends AppCompatActivity
{
    /* Spinner spinner = findViewById(R.id.spinner1);
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
            this,R.array.states, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);*/

	@SuppressLint( "ClickableViewAccessibility" )
	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.user_profile_edit );

		Toolbar toolbar = findViewById( R.id.toolbar );
		toolbar.setTitle( R.string.title_activity_edit_profile );
		setSupportActionBar( toolbar );
		if( getSupportActionBar() != null )
			getSupportActionBar().setDisplayHomeAsUpEnabled( true );


		final PatientSingleton patientSingleton = PatientSingleton.getInstance();

		final EditText firstNameInput = findViewById( R.id.firstNameInput );
		final EditText lastNameInput = findViewById( R.id.lastNameInput );
		final EditText phoneNumInput = findViewById( R.id.phoneNumInput );
		final EditText cityInput = findViewById( R.id.cityInput );
		final EditText stateInput = findViewById( R.id.stateInput );
		final EditText addressInput = findViewById( R.id.addressInput );
		final EditText address2Input = findViewById( R.id.address2Input );
		final EditText weightInput = findViewById( R.id.weightInput );
		final EditText heightInput = findViewById( R.id.heightInput );
		final EditText zipInput = findViewById( R.id.zipInput );
		final EditText zip2Input = findViewById( R.id.zip2Input );

		if( !patientSingleton.getFirstName().isEmpty() )
			firstNameInput.setText( patientSingleton.getFirstName() );
		if( !patientSingleton.getLastName().isEmpty() )
			lastNameInput.setText( patientSingleton.getLastName() );
		if( !patientSingleton.getPhoneNumber().isEmpty() )
			phoneNumInput.setText( patientSingleton.getPhoneNumber() );
		if( !patientSingleton.getAddress1().isEmpty() )
			addressInput.setText( patientSingleton.getAddress1() );
		if( !patientSingleton.getAddress2().isEmpty() )
			address2Input.setText( patientSingleton.getAddress2() );
		if( !patientSingleton.getCity().isEmpty() )
			cityInput.setText( patientSingleton.getCity() );
		if( !patientSingleton.getState().isEmpty() )
			stateInput.setText( patientSingleton.getCity() );
		if( patientSingleton.getZip1() > 0 )
			zipInput.setText( String.valueOf( patientSingleton.getZip1() ) );
		if( patientSingleton.getZip2() > 0 )
			zip2Input.setText( String.valueOf( patientSingleton.getZip2() ) );
		if( !patientSingleton.getWeight().isEmpty() )
			weightInput.setText( patientSingleton.getWeight() );
		if( !patientSingleton.getHeight().isEmpty() )
			heightInput.setText( patientSingleton.getHeight() );


		Button button = findViewById( R.id.submitButton );

		button.setOnTouchListener( new View.OnTouchListener()
		{
			@Override
			public boolean onTouch( View v, MotionEvent event )
			{
				if( event.getAction() == MotionEvent.ACTION_UP )
				{
					DbPatientRepository dbPatientRepository =
							new DbPatientRepository( getApplicationContext() );

					patientSingleton.setFirstName( firstNameInput.getText().toString() );
					patientSingleton.setLastName( lastNameInput.getText().toString() );
					patientSingleton.setPhoneNumber( phoneNumInput.getText().toString() );
					patientSingleton.setAddress1( addressInput.getText().toString() );
					patientSingleton.setAddress2( address2Input.getText().toString() );
					patientSingleton.setCity( cityInput.getText().toString() );
					patientSingleton.setState( stateInput.getText().toString() );
					try
					{
						patientSingleton.setZip1( Integer.valueOf( zipInput.getText().toString() ) );
					}
					catch( Exception e )
					{
						e.printStackTrace();
					}

					try
					{
						patientSingleton.setZip2( Integer.valueOf( zip2Input.getText().toString() ) );
					}
					catch( Exception e )
					{
						e.printStackTrace();
					}
					patientSingleton.setWeight( weightInput.getText().toString() );
					patientSingleton.setHeight( heightInput.getText().toString() );

					dbPatientRepository.update( patientSingleton.getUserName(), patientSingleton );
					finish();
					return true;
				}
				return false;
			}
		} );
	}
}
