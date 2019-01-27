package com.sugarcubes.myglucose.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sugarcubes.myglucose.R;
import com.sugarcubes.myglucose.models.Doctor;

import java.util.List;

public class DoctorDropDownAdapter extends ArrayAdapter<Doctor>
{

	LayoutInflater inflater;

	public DoctorDropDownAdapter( Activity context, int resourceId, int textViewId, List<Doctor> list )
	{
		super( context, resourceId, textViewId, list );
		inflater = context.getLayoutInflater();
	}

	@NonNull
	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		Doctor doctor = getItem( position );

		if( convertView == null )
		convertView = inflater.inflate( R.layout.doctor_dropdown_item_layout, null, true );

		TextView txtTitle = convertView.findViewById( R.id.doctor_name );
		String title = doctor.getFirstName() + " " + doctor.getLastName()
				+ ", " + doctor.getDegreeAbbreviation();
		txtTitle.setText( title );

		convertView.setTag( doctor.getEmail() );			// Set the tag to get the dr email later

		return convertView;

	} // getView

	@Override
	public View getDropDownView( int position, View convertView, ViewGroup parent )
	{
		if( convertView == null )
			convertView = inflater.inflate( R.layout.doctor_dropdown_item_layout, parent, false );

		Doctor doctor = getItem( position );
		TextView txtTitle = convertView.findViewById( R.id.doctor_name );
		String title = doctor.getFirstName() + " " + doctor.getLastName()
				+ ", " + doctor.getDegreeAbbreviation();
		txtTitle.setText( title );

		return convertView;

	} // getDropDownView

} // class
