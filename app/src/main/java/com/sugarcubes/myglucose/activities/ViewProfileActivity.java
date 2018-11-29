package com.sugarcubes.myglucose.activities;

import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.singletons.PatientSingleton;

import java.util.Locale;

public class ViewProfileActivity extends AppCompatActivity
{

	private final String LOG_TAG = getClass().getCanonicalName();

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_view_profile );

		populateFields();

		Button closeButton = findViewById( R.id.close_button );
		closeButton.setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick( View view )
			{
				finish();
			}
		} );

		Button editButton = findViewById( R.id.edit_button );
		editButton.setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick( View view )
			{
				Intent intent = new Intent( ViewProfileActivity.this,
						EditProfileActivity.class );
				startActivity( intent );
			}
		} );

	} // onCreate


	@Override
	protected void onResume()
	{
		super.onResume();
		populateFields();

	} // onResume


	private void populateFields()
	{
		PatientSingleton patientSingleton = PatientSingleton.getInstance();

		TextView firstName = findViewById( R.id.firstNameView );
		firstName.setText( patientSingleton.getFirstName() );
		TextView lastName = findViewById( R.id.lastNameView );
		lastName.setText( patientSingleton.getLastName() );
		TextView phoneNum = findViewById( R.id.phoneNumView );
		phoneNum.setText( patientSingleton.getPhoneNumber() );
		TextView addressView = findViewById( R.id.addressView );
		addressView.setText( patientSingleton.getAddress1() );
		TextView address1View = findViewById( R.id.address2 );
		address1View.setText( patientSingleton.getAddress2() );
		TextView cityView = findViewById( R.id.cityView );
		cityView.setText( patientSingleton.getCity() );
		TextView stateView = findViewById( R.id.stateView );
		stateView.setText( patientSingleton.getState() );
		TextView zip1View = findViewById( R.id.zip1 );
		zip1View.setText( String.valueOf( patientSingleton.getZip1() ) );
		TextView zip2View = findViewById( R.id.zip2 );
		zip2View.setText( String.format( Locale.US, "%04d", patientSingleton.getZip2() ) );

		TextView weightView = findViewById( R.id.weightView );
		weightView.setText( patientSingleton.getWeight() );
		TextView heightView = findViewById( R.id.heightView );
		heightView.setText( patientSingleton.getHeight() );
	} // populateFields

} // class
