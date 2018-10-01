package com.sugarcubes.myglucose.enums;

// ERRORS CODES:
// 0:	No error
// 1:	Unknown - something went wrong
// 2:	URL
// 3:	Invalid email
// 4: 	Invalid password
public enum ErrorCode
{
	NO_ERROR(0),
	UNKNOWN(1),
	INVALID_URL(2),
	INVALID_EMAIL_PASSWORD(3);

	private int _value;

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

}
