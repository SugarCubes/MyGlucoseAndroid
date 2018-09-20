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

public class ViewProfileActivity extends AppCompatActivity {

    private final String LOG_TAG = getClass().getCanonicalName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);

        Log.d( LOG_TAG, "View opened!" );

        PatientSingleton patientSingleton = PatientSingleton.getInstance();

        TextView firstName = findViewById(R.id.firstNameView);
        firstName.setText(patientSingleton.getFirstName());

//        finish();                     // finish() closes the activity
    }
}
