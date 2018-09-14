package com.sugarcubes.myglucose.entities;

public class Doctor extends ApplicationUser
{
	protected String DegreeAbbreviation;

	public Doctor()
	{
		DegreeAbbreviation = "MD";				// Default abbreviation

	} // constructor

	public String getDegreeAbbreviation()
	{
		return DegreeAbbreviation;
	}

	public void setDegreeAbbreviation( String degreeAbbreviation )
	{
		DegreeAbbreviation = degreeAbbreviation;
	}

	@Override
	public String toString()
	{
		return super.toString()
				+ " DoctorInfo{" +
				"DegreeAbbreviation='" + DegreeAbbreviation + '\'' +
				'}';
	} // toString

} // class
