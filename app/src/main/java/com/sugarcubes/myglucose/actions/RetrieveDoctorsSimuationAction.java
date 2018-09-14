package com.sugarcubes.myglucose.actions;

import com.sugarcubes.myglucose.actions.interfaces.IRetrieveDoctorsAction;
import com.sugarcubes.myglucose.entities.Doctor;

import java.util.ArrayList;
import java.util.List;

public class RetrieveDoctorsSimuationAction implements IRetrieveDoctorsAction
{
	@Override
	public List<Doctor> retrieveDoctors()
	{
		List<Doctor> doctors = new ArrayList<>();

		Doctor drew = new Doctor();
		drew.setEmail( "dr.drew@gmail.com" );
		drew.setFirstName( "Drew" );
		drew.setLastName( "Manley" );
		drew.setDegreeAbbreviation( "MD" );
		doctors.add( drew );

		Doctor phil = new Doctor();
		phil.setEmail( "dr.phil@yahoo.com" );
		phil.setFirstName( "Phillip" );
		phil.setLastName( "Philson" );
		phil.setDegreeAbbreviation( "MD" );
		doctors.add( phil );

		Doctor john = new Doctor();
		john.setEmail( "dr.john@gmail.com" );
		john.setFirstName( "John" );
		john.setLastName( "Johnson" );
		john.setDegreeAbbreviation( "MD" );
		doctors.add( john );

		return doctors;

	} // retrieveDoctors

} // class
