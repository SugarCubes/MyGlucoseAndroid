package com.sugarcubes.myglucose.enums;

public enum BeforeAfter
{
	BEFORE(0),
	AFTER(1);

	private int _value;

	BeforeAfter( int Value )
	{
		this._value = Value;

	} // constructor


	public int getValue()
	{
		return _value;

	} // getValue


	public static BeforeAfter fromInt( int i )
	{
		for( BeforeAfter beforeAfter : BeforeAfter.values() )
		{
			if( beforeAfter.getValue() == i )
			{
				return beforeAfter;
			}
		}
		return null;

	} // fromInt

} // enum
