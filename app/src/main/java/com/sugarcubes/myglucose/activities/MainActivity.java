package com.sugarcubes.myglucose.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.sugarcubes.myglucose.R;

public class MainActivity extends AppCompatActivity
{
	private ActionBar actionBar;


	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		Toolbar toolbar = findViewById( R.id.toolbar );
		setSupportActionBar( toolbar );

		FloatingActionButton fab = findViewById( R.id.fab );
		fab.setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick( View view )
			{
				Snackbar.make( view, "Replace with your own action", Snackbar.LENGTH_LONG )
						.setAction( "Action", null ).show();
			}
		} );
	}


	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// TODO - create refresh button to sync capsules from server.
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.main, menu );
		return true;

	} // onCreateOptionsMenu

}
