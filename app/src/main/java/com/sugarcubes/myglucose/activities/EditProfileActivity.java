package com.sugarcubes.myglucose.activities;

import android.annotation.SuppressLint;
import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.repositories.DbPatientRepository;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

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
		setContentView( R.layout.activity_edit_profile );

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

		firstNameInput.setText( patientSingleton.getFirstName() );
		lastNameInput.setText( patientSingleton.getLastName() );
		phoneNumInput.setText( patientSingleton.getPhoneNumber() );
		addressInput.setText( patientSingleton.getAddress1() );
		address2Input.setText( patientSingleton.getAddress2() );
		cityInput.setText( patientSingleton.getCity() );
		stateInput.setText( patientSingleton.getState() );
		if( patientSingleton.getZip1() > 0 )
			zipInput.setText( String.valueOf( patientSingleton.getZip1() ) );
		if( patientSingleton.getZip2() > 0 )
			zip2Input.setText( String.valueOf( patientSingleton.getZip2() ) );
		weightInput.setText( patientSingleton.getWeight() );
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
