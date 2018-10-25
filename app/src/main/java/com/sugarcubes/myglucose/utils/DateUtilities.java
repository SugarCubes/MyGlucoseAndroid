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

		try {
			convertedDate = dateFormat.parse( dateString );			// Parse the date string
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			return null;
		}

		return convertedDate;

	} // convertStringToDate

} // class
