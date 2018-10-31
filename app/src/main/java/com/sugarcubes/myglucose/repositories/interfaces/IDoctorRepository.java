package com.sugarcubes.myglucose.repositories.interfaces;

import android.database.Cursor;

import com.sugarcubes.myglucose.entities.Doctor;

public interface IDoctorRepository extends IApplicationUserRepository<Doctor>
{
	boolean doctorExists( String userName );

} // interface
