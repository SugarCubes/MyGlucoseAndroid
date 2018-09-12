package com.sugarcubes.myglucose.singletons;

import android.content.ContentValues;
import android.content.Context;

import com.sugarcubes.myglucose.entities.ApplicationUser;
import com.sugarcubes.myglucose.entities.Doctor;
import com.sugarcubes.myglucose.entities.ExerciseEntry;
import com.sugarcubes.myglucose.entities.GlucoseEntry;
import com.sugarcubes.myglucose.entities.MealEntry;
import com.sugarcubes.myglucose.repositories.DbGlucoseEntryRepository;
import com.sugarcubes.myglucose.repositories.DbPatientRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IGlucoseEntryRepository;
import com.sugarcubes.myglucose.repositories.interfaces.IPatientRepository;

import java.util.ArrayList;

public class PatientSingleton extends ApplicationUser
{
	private static PatientSingleton singleton;

	protected Doctor doctor;

	protected ArrayList<GlucoseEntry> glucoseEntries;
	protected ArrayList<MealEntry> mealEntries;
	protected ArrayList<ExerciseEntry> exerciseEntries;


	private PatientSingleton()
	{
		// Instantiate the doctor:
		doctor = new Doctor();
		// Instantiate all ArrayLists:
		glucoseEntries = new ArrayList<>();
		mealEntries = new ArrayList<>();
		exerciseEntries = new ArrayList<>();

	} // constructor


	// Since there should only be 1 user, we make in impossible to create more than 1 instance
	//		of the class.
	public static PatientSingleton getInstance()
	{
		if( singleton == null )
			singleton = new PatientSingleton();

		return singleton;

	} // getInstance

	public Doctor getDoctor()
	{
		return doctor;
	}

	public void setDoctor( Doctor doctor )
	{
		this.doctor = doctor;
	}

	public ArrayList<GlucoseEntry> getGlucoseEntries()
	{
		return glucoseEntries;
	}

	public ArrayList<MealEntry> getMealEntries()
	{
		return mealEntries;
	}

	public ArrayList<ExerciseEntry> getExerciseEntries()
	{
		return exerciseEntries;
	}

	@Override
	public String toString()
	{
		String doctorString = doctor != null ? doctor.toString() : "";
		return super.toString() +
				"\nPatientSingleton{" +
				"doctor=" + doctorString +
				", glucoseEntries=" + glucoseEntries +
				", mealEntries=" + mealEntries +
				", exerciseEntries=" + exerciseEntries +
				'}';
	}
} // class
