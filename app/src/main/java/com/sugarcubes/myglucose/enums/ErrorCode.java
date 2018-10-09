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
	INVALID_LOGIN_TOKEN( 4 );

	private int _value;
	private static final String SUCCESS    = "success";
	private static final String ERROR_CODE = "errorCode";

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
		return null;

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
				int error = ErrorCode.getErrorCode( jsonObject );
				ErrorCode errorCode = ErrorCode.fromInt( error );
				if( errorCode != null )
					switch( errorCode )
					{
						case NO_ERROR:
							return ErrorCode.NO_ERROR;
						case UNKNOWN:
							return ErrorCode.UNKNOWN;
						case INVALID_URL:
							return ErrorCode.INVALID_URL;
						case INVALID_EMAIL_PASSWORD:
							return ErrorCode.INVALID_EMAIL_PASSWORD;
						default:
							return ErrorCode.UNKNOWN;

					} // switch
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
