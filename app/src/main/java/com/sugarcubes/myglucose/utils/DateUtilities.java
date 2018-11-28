package com.sugarcubes.myglucose.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtilities
{
	public static Date convertStringToDate( String dateString )
	{
		//DateFormat df = DateFormat.getTimeInstance();
		SimpleDateFormat dateFormat
				= new SimpleDateFormat( "EEE MMM dd kk:mm:ss zzz yyyy", Locale.US );
		Date convertedDate = new Date();

		try
		{
			convertedDate = dateFormat.parse( dateString );            // Parse the date string
		}
		catch( ParseException e )
		{
			e.printStackTrace();
			return null;
		}

		return convertedDate;

	} // convertStringToDate


	public static String getSimpleDateString( Date date )
	{
		// Create an instance of SimpleDateFormat used for formatting
		// the string representation of date (month/day/year)
		DateFormat df = new SimpleDateFormat( "MM/dd/yyyy HH:mm:ss", Locale.US );
		return df.format(date);

	} // getSimpleDateString


	public static String minutesToHoursMinutesString( int minutes )
	{
		if( minutes > 59 )
			return String.valueOf( minutes / 60 ) + " hour"
					+ ( ( minutes / 60 ) > 1
					? "s, "
					: " " ) + String.valueOf( minutes % 60 )
					+ " minutes";

		return String.valueOf( minutes ) + " minutes";
	}

} // class