package com.sugarcubes.myglucose.enums;

import org.json.JSONException;
import org.json.JSONObject;

// ERRORS CODES:
// 0:	No error
// 1:	Unknown - something went wrong
// 2:	URL
// 3:	Invalid email
// 4: 	Invalid password
public enum ErrorCode
{
	NO_ERROR( 0 ),
	UNKNOWN( 1 ),
	INVALID_URL( 2 ),
	INVALID_EMAIL_PASSWORD( 3 ),
	INVALID_LOGIN_TOKEN( 4 ),
	USER_ALREADY_REGISTERED( 5 ),
	ITEM_ALREADY_EXISTS( 6 ),
	INVALID_MEAL_ENTRY( 7);

	private int _value;
	public static final String SUCCESS    = "success";
	public static final String ERROR_CODE = "errorCode";

	ErrorCode( int Value )
	{
		this._value = Value;

	} // constructor


	public int getValue()
	{
		return _value;

	} // getValue


	public static ErrorCode fromInt( int i )
	{
		for( ErrorCode errorCode : ErrorCode.values() )
		{
			if( errorCode.getValue() == i )
			{
				return errorCode;
			}
		}
		return ErrorCode.UNKNOWN;

	} // fromInt


	// - HELPERS - //
	public static ErrorCode interpretErrorCode( String jsonString )
	{
		try
		{
			JSONObject jsonObject = new JSONObject( jsonString );
			return interpretErrorCode( jsonObject );
		}
		catch( JSONException e )
		{
			e.printStackTrace();
			return ErrorCode.UNKNOWN;
		}

	} // interpretErrorCode


	public static ErrorCode interpretErrorCode( JSONObject jsonObject )
	{
		try
		{
			if( !ErrorCode.isSuccess( jsonObject ) )
			{
				int error = ErrorCode.getErrorCode( jsonObject );	// Scans json for "errorCode"
				ErrorCode errorCode = ErrorCode.fromInt( error );	// Convert it to an ErrorCode
				if( errorCode != null )
					return errorCode;
				else
					return ErrorCode.UNKNOWN;

			} // if !success
			else
				return ErrorCode.NO_ERROR;
		}
		catch( JSONException e )
		{
			e.printStackTrace();
			return ErrorCode.UNKNOWN;
		}

	} // interpretErrorCode


	private static boolean isSuccess( JSONObject jsonObject ) throws JSONException
	{
		return jsonObject.getBoolean( SUCCESS );

	} // isSuccess


	private static int getErrorCode( JSONObject jsonObject ) throws JSONException
	{
		return jsonObject.getInt( ERROR_CODE );

	} // isSuccess

}
