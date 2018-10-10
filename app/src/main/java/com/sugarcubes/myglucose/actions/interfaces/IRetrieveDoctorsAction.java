package com.sugarcubes.myglucose.actions.interfaces;

import android.content.Context;

import com.sugarcubes.myglucose.entities.Doctor;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public interface IRetrieveDoctorsAction
{
	List<Doctor> retrieveDoctors( Context context ) throws JSONException;

} // interface
