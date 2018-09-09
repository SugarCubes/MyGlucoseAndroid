package com.sugarcubes.myglucose.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class DateUtilities
{
	public static Date convertStringToDate( String dateString )
	{
		DateFormat df = DateFormat.getTimeInstance();
		//SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
		Date convertedDate = new Date();

		try {
			convertedDate = df.parse( dateString );			// Parse the date string
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			return null;
		}

		return convertedDate;

	} // convertStringToDate

}
